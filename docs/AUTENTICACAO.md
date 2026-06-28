# Autenticação e autorização — Triscal Apontamento de Horas

> ⚠️ **Arquitetura atualizada:** o **Keycloak foi removido**. A autenticação agora é **direta no
> Microsoft Entra ID** (OIDC + PKCE). O guia atual e definitivo é **`docs/ENTRA_SSO.md`**. O conteúdo
> abaixo descreve o modelo antigo (broker Keycloak) e é mantido apenas como histórico.

**Autenticação (atual):** Microsoft Entra ID **direto** (SPA público + PKCE; API valida o JWT).
**Autorização:** 100% interna à aplicação (o token NÃO carrega papéis nem hierarquia).

## 1. Dados necessários do Entra ID (App Registration)

Solicite ao administrador do Entra ID da Triscal:

| Item | Onde usar | Exemplo |
|------|-----------|---------|
| Directory (tenant) ID | Keycloak IdP + discovery | `00000000-0000-0000-0000-000000000000` |
| Application (client) ID | Keycloak IdP (Client ID) | `11111111-1111-1111-1111-111111111111` |
| Client secret | Keycloak IdP (Client secret) — no cofre | `***` |
| Discovery endpoint | Keycloak IdP (import) | `https://login.microsoftonline.com/{tenant}/v2.0/.well-known/openid-configuration` |
| Redirect URI (registrar no Entra) | App Registration → Authentication | `https://<keycloak>/realms/triscal/broker/oidc/endpoint` (e `http://localhost:8081/...` em dev) |
| Scopes / permissões | App Registration → API permissions | `openid`, `profile`, `email` + **admin consent** |
| Claim de vínculo | mapeamento → `funcionario.cdg_usur` | **e-mail/UPN** (recomendado) + `oid` como chave estável |
| Front-channel logout (opcional) | logout federado | `https://<app>/logout` |

> Decisão pendente: confirmar o **claim oficial de vínculo** com `funcionario.cdg_usur`
> (recomendado: e-mail corporativo; armazenar também o `oid`).

## 2. Configuração do Keycloak (realm `triscal`)

1. Criar realm `triscal`.
2. Identity Providers → **OpenID Connect v1.0** → importar pelo discovery endpoint do Entra;
   preencher Client ID e Client secret. Mapear claims (email, name, oid → atributos do usuário).
3. Clients:
   - `timesheet-web` (público, PKCE) — para o SPA React. Redirect: `https://<app>/*`, `http://localhost:5173/*`.
   - `timesheet-api` (bearer-only) — audience validada pelo backend.
4. Login flow: redirecionar direto ao Entra (idp hint), sem tela de login local.

## 3. Variáveis de ambiente

Backend (`backend`):
```
OIDC_ISSUER_URI=https://<keycloak>/realms/triscal
DB_URL=jdbc:postgresql://<host>:5432/timesheet
DB_USER=...
DB_PASSWORD=...     # via cofre (Vault/K8s Secret), nunca no código
```

Frontend (`frontend/.env`):
```
VITE_OIDC_AUTHORITY=https://<keycloak>/realms/triscal
VITE_OIDC_CLIENT_ID=timesheet-web
VITE_OIDC_REDIRECT_URI=https://<app>/  (dev: http://localhost:5173/)
VITE_API_BASE=/api
```

## 4. Fluxo (resumo)

1. SPA redireciona ao Keycloak (OIDC, PKCE) → Keycloak redireciona ao Entra → usuário autentica.
2. SPA recebe o token (identidade: sub/email/oid/name). **Sem papéis.**
3. SPA chama `GET /api/v1/me`; o backend valida o JWT, casa o e-mail/oid com
   `funcionario.cdg_usur` e retorna `{ id, nome, email, papeis[] }` a partir das tabelas
   internas (`perfil`, `funcionario_perfil`).
4. Front e back aplicam RBAC por papel **e por dado** com base nesse resultado.

Papéis internos disponíveis: **COLABORADOR, GESTOR, GESTOR_PROJETO, ADMIN_RH, DIRETORIA, COMERCIAL**
(tabela `perfil`). A matriz "menu × perfil" está na Documentação Técnica (seção 5.1). **Importante
em produção:** garanta que exista **ao menos um funcionário com ADMIN_RH** já na carga inicial
(via migração ou cadastro manual), senão ninguém conseguirá administrar cadastros/papéis.

## 5. Configuração aplicada (Tenant Triscal)

Valores fornecidos pela infraestrutura e já refletidos em `infra/keycloak/realm-triscal.json`,
`infra/docker-compose.yml` e `frontend/.env.development`:

| Item | Valor |
|------|-------|
| Claim de vínculo | **e-mail corporativo** (casa com `funcionario.cdg_usur`) |
| Directory (tenant) ID | `2b42ae36-0393-4fc6-b055-5d2a93ceb4b4` |
| Application (client) ID | `61296ae1-89c3-459b-a9e9-22487fb35f5d` |
| Discovery | `https://login.microsoftonline.com/2b42ae36-0393-4fc6-b055-5d2a93ceb4b4/v2.0/.well-known/openid-configuration` |
| Redirect URI (dev) | `http://localhost:8081/realms/triscal/broker/oidc/endpoint` |
| Redirect URI (prod, provisório) | `https://keycloak.triscal.com.br/realms/triscal/broker/oidc/endpoint` (ajustar à URL final do Keycloak) |
| Scopes | `openid profile email` · Admin consent concedido |
| Client secret | **NÃO** versionado — fica em `infra/.env` (gitignored), lido como `ENTRA_CLIENT_SECRET` |

> **Segurança:** o client secret foi compartilhado em texto e está só em `infra/.env` (fora do
> git). **Recomenda-se rotacioná-lo** no App Registration do Entra e atualizar `infra/.env`.
> Quando a URL final do Keycloak existir, ajustar a Redirect URI de produção no Entra e em
> `realm-triscal.json` (lista `redirectUris` do IdP é gerada pelo broker).

### Subir o SSO em dev
```bash
cd infra && docker compose up -d        # importa o realm 'triscal' e injeta ENTRA_CLIENT_SECRET
# Keycloak admin: http://localhost:8081 (admin/admin)
# Realm: triscal | IdP: Microsoft Entra ID | Clients: timesheet-web (SPA), timesheet-api (API)
```
Se o placeholder do secret não for substituído na importação, defina-o manualmente em
Keycloak → Identity Providers → Microsoft Entra ID → Client Secret.
