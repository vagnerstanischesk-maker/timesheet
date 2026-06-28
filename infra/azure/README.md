# Infraestrutura como código (Bicep) — Azure

Provisiona toda a topologia de produção do sistema (ver `docs/DEPLOY_AZURE.md`):
Log Analytics, **ACR**, **PostgreSQL Flexible**, **Key Vault**, **Container Apps Environment** e
dois **Container Apps** (API, Web). Autenticação **direta no Microsoft Entra ID** (sem Keycloak).

## Arquivos
- `main.bicep` — definição de toda a infra (parametrizada).
- `main.parameters.example.json` — modelo de parâmetros. **Copie** para `main.parameters.json`
  e preencha os segredos (esse arquivo real é gitignored).
- `deploy.sh` — orquestra: cria RG → (Bicep) → build das imagens no ACR → deploy final → outputs.

## Pré-requisitos
- Azure CLI logado: `az login` (e `az account set --subscription <id>` se necessário).
- Permissão para criar recursos na assinatura.

## Validar antes de aplicar (recomendado)
O `deploy.sh` sempre faz **lint do Bicep** (`az bicep build`) e um **what-if** (pré-visualização
das mudanças) antes de provisionar. Para apenas validar, sem alterar nada:
```bash
az login
PREFIX=triscal RG=rg-triscal-timesheet LOC=brazilsouth bash infra/azure/deploy.sh --validate
```
Isso compila o template e mostra o que seria criado/alterado (não cria recursos). Útil em revisão
de PR ou antes do primeiro provisionamento.

## Uso
```bash
cp infra/azure/main.parameters.example.json infra/azure/main.parameters.json
# edite main.parameters.json: pgPassword, entraTenantId, entraClientId, SMTP...

# a partir da RAIZ do repositório (os builds usam ./backend e ./frontend):
PREFIX=triscal RG=rg-triscal-timesheet LOC=brazilsouth bash infra/azure/deploy.sh
```

### Observação sobre o "ovo e a galinha" (imagens x apps)
Os Container Apps referenciam imagens no ACR que só existem **após** o build. O `deploy.sh`
contorna isso rodando o Bicep, buildando as imagens no ACR e rodando o Bicep de novo. Em
ambientes mais rígidos, separe em dois deploys (infra base e apps) ou use uma imagem placeholder
no primeiro `create`.

## Pós-deploy (resumo — detalhes em docs/DEPLOY_AZURE.md e docs/ENTRA_SSO.md)
1. **App Registration** do Entra: plataforma SPA + Expose API (`access_as_user`) + `accessTokenAcceptedVersion: 2`; redirect = FQDN do `triscal-web`.
2. Conferir `OIDC_*` (API) e `VITE_OIDC_*` (web) — o Bicep já injeta a partir de `entraTenantId`/`entraClientId`.
3. Domínios próprios + TLS (CNAME). 4. Migração de dados (`migration/`). 5. Perfis e smoke test.

## Segurança
- `main.parameters.json` contém segredos → **gitignored** (não versione).
- Em produção, conceda à API uma **managed identity** com acesso ao **Key Vault** e
  referencie os segredos de lá (o Bicep já cria o Key Vault com os segredos).
- PostgreSQL com `sslmode=require`; avalie **Private Endpoint/VNet**.

## Destruir o ambiente (cuidado)
```bash
az group delete -n rg-triscal-timesheet --yes --no-wait
```
