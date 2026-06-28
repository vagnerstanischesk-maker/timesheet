# Pacote de deploy em produção — Triscal Apontamento de Horas

Capa para o **administrador do Azure**. Resume **o que entregar**, **a ordem de execução** e **o que
precisa de segredos**. O roteiro detalhado é o `docs/PRODUCAO_CHECKLIST.md`.

> Forma recomendada de entrega: **acesso ao repositório Git** (clonar). O provisionamento builda as
> imagens a partir do código-fonte (`az acr build ./backend` e `./frontend`), então arquivos soltos
> não bastam — é preciso o projeto completo.

## 1. Arquivos / pastas do pacote

**Infraestrutura (provisionar)**
- `infra/azure/main.bicep` — toda a infra: ACR, PostgreSQL Flexible 16, Key Vault, Container Apps (API, Web), Log Analytics. (Sem Keycloak — auth direto no Entra.)
- `infra/azure/deploy.sh` — valida (lint + what-if), provisiona e builda as imagens; suporta `SUBSCRIPTION` e `tags`.
- `infra/azure/main.parameters.example.json` — modelo de parâmetros. Crie o `main.parameters.json` a partir dele (NÃO versionar com segredos).
- `docs/ENTRA_SSO.md` — configuração do SSO **direto no Entra ID** (App Registration: SPA + Expose API).

**Código-fonte (necessário para buildar as imagens)**
- `backend/` (+ `Dockerfile`) — API Java/Spring Boot. O **Flyway aplica o schema V1…V8** no startup (inclui cortes de quinzena 1/16).
- `frontend/` (+ `Dockerfile`, `nginx.conf`) — SPA React. As `VITE_*` são injetadas **no build** (gere `frontend/.env.production` antes).

**Migração de dados (Oracle → PostgreSQL)**
- `migration/etl.py`, `migration/requirements.txt` — ETL (traduz status `AT → INI`).
- `migration/validacao.sql` — conferências pós-carga.
- `migration/seed_admin_rh.sql` — concede **ADMIN_RH ao André Vieira de Mello** (rodar após o ETL).
- `migration/README.md` — instruções da migração.

**Documentação (roteiro)**
- `docs/PRODUCAO_CHECKLIST.md` — **runbook ponta a ponta (siga este)**.
- `docs/DEPLOY_AZURE.md` — detalhe dos comandos Azure.
- `docs/ENTRA_SSO.md` / `docs/AUTENTICACAO.md` — SSO direto no Entra ID.

## 2. Ordem de execução (resumo)
1. **Provisionar**: `az login` → `az account set --subscription <ID>` → `bash infra/azure/deploy.sh --validate` (pré-visualiza) → `bash infra/azure/deploy.sh` (cria infra + builda imagens).
2. **Publicar**: Container Apps de API e Web (o `deploy.sh` cobre).
3. **Schema**: aplicado automaticamente pelo Flyway no startup da API (conferir no log).
4. **Migrar dados**: preencher `migration/.env` → ensaiar em homologação → `python etl.py --truncate` em produção → `psql -f migration/validacao.sql` → `psql -f migration/seed_admin_rh.sql`.
5. **SSO**: App Registration do Entra (SPA + Expose API `access_as_user` + `accessTokenAcceptedVersion: 2`); variáveis `OIDC_*`/`VITE_OIDC_*`. Ver `docs/ENTRA_SSO.md`.
6. **Domínios/TLS**: CNAMEs + bind de hostnames; ajustar Redirect URIs aos domínios finais.
7. **Smoke test** e **pós-go-live** (backup, alertas de custo, CI/CD) — ver runbook.

## 3. NÃO incluir no pacote (segredos)
`infra/.env`, qualquer `main.parameters.json` preenchido, senhas e o client secret do Entra.
Esses valores vão por **canal seguro** e ficam no **Key Vault** — nunca em arquivo versionado.

## 4. Itens que o administrador precisa receber de você / da infra
- **Assinatura** Azure (ID) e convenção de **resource group**/**tags**.
- **Client secret do Entra rotacionado** (o atual foi compartilhado em texto — rotacionar antes).
- Senha do **PostgreSQL**.
- **Credenciais de leitura do Oracle** legado (usuário/DSN) para o ETL.
- **Domínio** final do app (ex.: `app.triscal.com.br`) e o **client id** da App Registration do Entra.
- Confirmação dos **códigos de status do Oracle** (o ETL assume `AT` = ativo → `INI`).
