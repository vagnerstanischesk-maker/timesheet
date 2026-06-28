# SSO direto no Microsoft Entra ID (sem Keycloak)

Nesta versão o **Keycloak foi removido**: o SPA autentica **direto no Entra ID** (OIDC + PKCE) e o
backend valida o **JWT do Entra**. Os papéis continuam **100% internos** (resolvidos em
`GET /api/v1/me` a partir de `perfil`/`funcionario_perfil`).

```
SPA (Entra App, público + PKCE) ──OIDC──> Microsoft Entra ID (login Microsoft)
        ▲                                        │
        └──────── access_token (aud = API) ──────┘   depois: SPA ──Bearer──> API /v1/me ──> papéis (DB)
```

Tenant Triscal: `2b42ae36-0393-4fc6-b055-5d2a93ceb4b4`. App Registration atual:
`61296ae1-89c3-459b-a9e9-22487fb35f5d` (reaproveitável como SPA + API).

## 1. App Registration no Entra (uma vez)
No portal do Entra → App registrations → (a app existente ou uma nova):

1. **Authentication → Add a platform → Single-page application (SPA)**. Redirect URIs:
   - `https://triscal-web.agreeableflower-031480c0.brazilsouth.azurecontainerapps.io/`
   - (dev) `http://localhost:5173/`
   > É **SPA**, não "Web". Em SPA o token endpoint aceita CORS e usa **PKCE** (sem client secret).
2. **Expose an API**:
   - Application ID URI: `api://61296ae1-89c3-459b-a9e9-22487fb35f5d` (default).
   - Add a scope: **`access_as_user`** (admins e usuários podem consentir).
3. **API permissions**: adicione a permissão delegada do próprio app (`access_as_user`) e, se a
   política exigir, **Grant admin consent**. `openid`, `profile`, `email` já são padrão.
4. **Manifest**: garanta `"accessTokenAcceptedVersion": 2` (emite tokens **v2**, com
   `iss = https://login.microsoftonline.com/<tenant>/v2.0`, que é o que a API valida).
5. O **client secret antigo** (usado pelo Keycloak) **não é mais necessário** para o SPA — pode
   rotacioná-lo/removê-lo.

## 2. Variáveis — Frontend (`triscal-web`)
Como o SPA lê config em runtime (`window.__ENV__`), basta setar no Container App (e rebuildar a
imagem **uma vez** com o código novo):
```
VITE_OIDC_AUTHORITY = https://login.microsoftonline.com/2b42ae36-0393-4fc6-b055-5d2a93ceb4b4/v2.0
VITE_OIDC_CLIENT_ID = 61296ae1-89c3-459b-a9e9-22487fb35f5d
VITE_OIDC_SCOPE     = openid profile email api://61296ae1-89c3-459b-a9e9-22487fb35f5d/access_as_user
VITE_API_BASE       = /api
BACKEND_ORIGIN      = https://triscal-api.agreeableflower-031480c0.brazilsouth.azurecontainerapps.io
# VITE_OIDC_REDIRECT_URI é opcional: se omitido, o SPA usa a própria origem (window.location.origin).
```
> O `VITE_OIDC_SCOPE` **precisa** incluir `api://…/access_as_user`, senão o Entra emite um
> access_token para o Graph (aud errada) e a API rejeita.

## 3. Variáveis — Backend (`triscal-api`)
```
OIDC_ISSUER_URI = https://login.microsoftonline.com/2b42ae36-0393-4fc6-b055-5d2a93ceb4b4/v2.0
OIDC_AUDIENCE   = 61296ae1-89c3-459b-a9e9-22487fb35f5d
```
O backend (`SecurityConfig`) valida assinatura (JWKS do Entra), **issuer** e **audience**.

## 4. Vínculo identidade → funcionário
O backend (`UsuarioService.resolver`) casa o token com o funcionário por **e-mail/UPN**
(`email` → `preferred_username`) com `funcionario.cdg_usur` (fallback `oid`). Garanta que o
`cdg_usur`/e-mail do funcionário no banco seja o **e-mail corporativo** do usuário no Entra.

## 5. Fluxo de login
1. `ProtectedRoute`/botão "Entrar" → `signinRedirect()` → vai **direto ao Entra** (login Microsoft).
2. Entra retorna o `code` ao SPA (PKCE) → o SPA troca por **access_token** (aud = API) + id_token.
3. SPA chama `GET /api/v1/me` com `Authorization: Bearer <access_token>`; a API valida e devolve
   `{ id, nome, email, papeis[] }`.
4. Front e back aplicam o RBAC por papel e por dado.

## 6. Verificação
- `https://triscal-web…/config.js` mostra os `VITE_OIDC_*` preenchidos (prova do rebuild).
- Clicar **Entrar** abre a tela da **Microsoft** (não mais a do Keycloak).
- Após logar, `GET /api/v1/me` retorna os papéis (ou `cadastrado:false` se o e-mail não casar).

## 7. O que foi removido
Container App `keycloak`, `infra/keycloak/*`, serviço keycloak do `docker-compose`, e o recurso
Keycloak do Bicep. Não há mais realm para importar nem secret de broker para manter.
