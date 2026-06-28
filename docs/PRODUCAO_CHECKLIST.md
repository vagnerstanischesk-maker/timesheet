# Runbook de produção — Triscal Apontamento de Horas

Sequência ponta a ponta para colocar o sistema em produção no Azure: **provisionar → publicar →
migrar dados → configurar SSO → validar → cutover**. Detalhes de cada comando em
`docs/DEPLOY_AZURE.md` (Azure), `docs/AUTENTICACAO.md` (SSO/Entra) e `migration/README.md` (dados).

Pré-condição: a assinatura Azure já existe (com outros sistemas). Esta aplicação ganha um
**resource group próprio** e service principal dedicado, reaproveitando recursos compartilhados
(Key Vault corporativo, Log Analytics, DNS, VNet) quando fizer sentido.

## 0. Antes de começar (decisões e segredos)
- [ ] Definir a **assinatura** e o **resource group** (ex.: `rg-triscal-timesheet`) e a região (`brazilsouth`).
- [ ] Confirmar o **domínio** final do app (ex.: `app.triscal.com.br`). (Sem Keycloak — auth direto no Entra.)
- [ ] **Rotacionar o client secret** do Entra (o atual foi compartilhado em texto) e guardar no Key Vault.
- [ ] Primeiro **ADMIN_RH**: **André Vieira de Mello** (concedido via `migration/seed_admin_rh.sql` após o ETL).
- [ ] Garantir acesso de **leitura ao Oracle legado** (usuário/DSN) para o ETL.
- [ ] **Nunca** comitar segredos — usar Key Vault e `infra/.env` (gitignored).

## 1. Build das imagens
- [ ] `az acr build -r $ACR -t triscal-api:<tag> ./backend`
- [ ] `az acr build -r $ACR -t triscal-web:<tag> ./frontend` (o SPA lê a config em runtime via
      `window.__ENV__`; as `VITE_OIDC_*` são definidas nas variáveis do Container App, não no build).
- [ ] CI verde (`mvn clean verify` + `npm run build`/test) antes de promover a tag.

## 2. Provisionar a infraestrutura
- [ ] `az account set --subscription <ID>` e `az group create` (ou usar o RG corporativo).
- [ ] ACR, Container Apps Environment, **PostgreSQL Flexible Server 16**, Key Vault (passos 4–6 do guia).
- [ ] Opção IaC: `infra/azure/main.bicep` + `deploy.sh` (parametrizado por `main.parameters.example.json`).
- [ ] Segredos no Key Vault: `db-password`, `entra-client-secret` (e SMTP, se aplicável).

## 3. Publicar a aplicação
- [ ] (Sem Keycloak.) Configurar o **App Registration** do Entra: plataforma SPA + Expose API (`access_as_user`) + `accessTokenAcceptedVersion: 2` — ver `docs/ENTRA_SSO.md`.
- [ ] Subir **API** (`triscal-api`) com `DB_URL` (sslmode=require), `OIDC_ISSUER_URI`, `OIDC_AUDIENCE`, SMTP.
- [ ] Subir **Web** (`triscal-web`).
- [ ] No **startup da API**, o **Flyway aplica V1…V8** (schema + cortes de quinzena 1/16). Conferir no log.

## 4. Migração de dados do legado (Oracle → PostgreSQL)
Ver `migration/README.md`. Roda **uma vez**, na janela de cutover, com a aplicação no ar (schema já criado).
- [ ] `pip install -r migration/requirements.txt`; preencher `migration/.env` (Oracle + PostgreSQL de produção).
- [ ] **Ensaio**: rodar `python etl.py` num banco de homologação e validar.
- [ ] Produção: `python etl.py --truncate` (recarga total) na janela de corte.
- [ ] **Validação**: `psql ... -f migration/validacao.sql` — conferir:
      contagens × Oracle; reconciliação de BHE; tarefas órfãs = 0; acentuação;
      **status traduzido** (não pode sobrar `AT`; alocações `INI` são as lançáveis);
      **perfis** (existe ao menos um `ADMIN_RH`); **cortes** = 1 e 16.
- [ ] Observação: status de tarefa/alocação é traduzido `AT → INI`, demais → `ENC` (premissa do ETL;
      ajustar o CASE se o legado usar outros códigos).
- [ ] **Primeiro administrador**: `psql ... -f migration/seed_admin_rh.sql` (concede ADMIN_RH ao
      André Vieira de Mello). Conferir que o SELECT final retorna o registro.

## 5. SSO (Entra ID direto)
- [ ] App Registration: plataforma **SPA** com o redirect do app (`https://<app>/`); **Expose an API**
      com escopo `access_as_user`; manifest `accessTokenAcceptedVersion: 2`.
- [ ] `triscal-api`: `OIDC_ISSUER_URI` (issuer v2 do tenant) e `OIDC_AUDIENCE` (client id).
- [ ] `triscal-web`: `VITE_OIDC_AUTHORITY`, `VITE_OIDC_CLIENT_ID`, `VITE_OIDC_SCOPE` (com `api://…/access_as_user`), `VITE_API_BASE`, `BACKEND_ORIGIN`.
- [ ] Claim de vínculo = **e-mail corporativo** casando com `funcionario.cdg_usur`. Detalhes: `docs/ENTRA_SSO.md`.

## 6. Domínios, TLS e rede
- [ ] CNAMEs no DNS da Triscal → FQDNs dos Container Apps; bind dos hostnames (TLS gerenciado).
- [ ] Ajustar Redirect URIs/realm para os domínios finais.
- [ ] PostgreSQL com `sslmode=require`; restringir por **Private Endpoint/VNet** se a política exigir.

## 7. Smoke test (produção)
- [ ] Login SSO de um usuário real → `GET /api/v1/me` retorna os **papéis** corretos.
- [ ] Colaborador: **lançar um dia**, fechar quinzena.
- [ ] Gestor: ver pendência, **aprovar e rejeitar** um dia (BHE creditado).
- [ ] Gestão: abrir **Dashboards** e os **Relatórios** (logs pendentes, acompanhamento, BHE).
- [ ] Admin/RH: abrir **Cadastros** e a **pesquisa de funcionários**.

## 8. Pós-go-live
- [ ] Ativar **backup** do PostgreSQL (retenção ≥ 7 dias) e **alertas de custo**.
- [ ] Conferir os **crons de alerta** (diário/quinzenal/semanal) e o SMTP.
- [ ] CI/CD de produção (job de deploy no push à `main`) com `AZURE_CREDENTIALS` nos Secrets do GitHub.
- [ ] Agendar **rotação** periódica do client secret do Entra e da senha do banco.

## 9. Rollback (se o cutover falhar)
- [ ] Manter o **sistema legado disponível** até o smoke test passar.
- [ ] Recarga limpa: `python etl.py --truncate` reexecuta do zero (idempotente).
- [ ] Banco de produção com backup/restore point antes da carga.
- [ ] Reverter imagens: `az containerapp update ... --image <tag-anterior>`.
