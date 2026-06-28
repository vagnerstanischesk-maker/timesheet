# Deploy em produção no Microsoft Azure

Guia para provisionar e publicar o sistema (~100 usuários). Arquitetura enxuta, portável e de
baixo custo, alinhada ao ADR-001. Tudo em **contêineres**, sem Kubernetes.

## 1. Topologia recomendada (Azure)

| Componente | Serviço Azure | Observação |
|-----------|---------------|------------|
| Backend (API) | **Azure Container Apps** | contêiner `triscal-api`; 1–2 réplicas; escala por CPU |
| Frontend (SPA) | **Azure Container Apps** ou **Static Web Apps** | contêiner `triscal-web` (Nginx) servindo o build |
| Banco | **Azure Database for PostgreSQL Flexible Server** | 1 instância, backup automático |
| Identidade | **Microsoft Entra ID** (direto, sem Keycloak) | App Registration: SPA + Expose API; ver `docs/ENTRA_SSO.md` |
| Imagens | **Azure Container Registry (ACR)** | repositório das imagens |
| Segredos | **Azure Key Vault** | client secret do Entra, senha do banco |
| Observabilidade | **Log Analytics / Application Insights** (opcional) | logs do Container Apps |

> Alternativa ainda mais simples: **App Service for Containers** (3 Web Apps) em vez de Container
> Apps. Os passos são análogos; Container Apps tende a sair mais barato em baixa escala.

## 2. Pré-requisitos
- Assinatura Azure com permissão de criar recursos.
- **Azure CLI** instalado (`az version`) e logado (`az login`).
- Imagens já construídas (Dockerfiles do repositório) — o passo 5 builda no ACR.
- App Registration do Entra já criado (ver `docs/AUTENTICACAO.md`).

> **Encaixe numa assinatura Azure já existente (caso da Triscal).** Como já há outros sistemas
> na assinatura, mas **nada definido para esta aplicação**, recomenda-se: criar um **resource group
> próprio** (`rg-triscal-timesheet`) seguindo a convenção de nomes/tags da empresa; **reaproveitar**
> recursos compartilhados quando fizer sentido (Log Analytics workspace, Key Vault corporativo,
> DNS/zona, VNet/Private Endpoint); e usar um **service principal dedicado** para o CI/CD com escopo
> só desse resource group. Selecione a assinatura antes de começar: `az account set --subscription <ID>`.

## 3. Variáveis (ajuste antes de começar)
```bash
export RG=rg-triscal-timesheet
export LOC=brazilsouth
export ACR=triscalacr$RANDOM           # nome global único, só minúsculas/números
export ENVNAME=cae-triscal             # Container Apps Environment
export PG=triscal-pg$RANDOM            # nome global único do PostgreSQL
export PG_ADMIN=tsadmin
export PG_PASS='<senha-forte>'         # NÃO comitar; idealmente gerar e guardar no Key Vault
export KV=kv-triscal$RANDOM
```

## 4. Grupo de recursos, ACR e ambiente
```bash
az group create -n $RG -l $LOC

az acr create -g $RG -n $ACR --sku Basic --admin-enabled true

az extension add --name containerapp --upgrade
az provider register -n Microsoft.App --wait
az provider register -n Microsoft.OperationalInsights --wait
az containerapp env create -g $RG -n $ENVNAME -l $LOC
```

## 5. Banco de dados (PostgreSQL Flexible)
```bash
az postgres flexible-server create -g $RG -n $PG -l $LOC \
  --tier Burstable --sku-name Standard_B1ms --version 16 --storage-size 32 \
  --admin-user $PG_ADMIN --admin-password "$PG_PASS" --public-access 0.0.0.0

az postgres flexible-server db create -g $RG -s $PG -d timesheet

# Permitir serviços do Azure acessarem (ou configure VNet/Private Endpoint para mais segurança)
az postgres flexible-server firewall-rule create -g $RG -n $PG \
  --rule-name allow-azure --start-ip-address 0.0.0.0 --end-ip-address 0.0.0.0
```
A aplicação aplica o schema automaticamente no startup (**Flyway**, migrations **V1…V8** —
inclui os cortes de quinzena 1/16 na V8).

## 6. Key Vault (segredos)
```bash
az keyvault create -g $RG -n $KV -l $LOC
az keyvault secret set --vault-name $KV -n db-password        --value "$PG_PASS"
az keyvault secret set --vault-name $KV -n entra-client-secret --value "<CLIENT_SECRET_DO_ENTRA>"
```
Os Container Apps podem referenciar segredos do Key Vault via identidade gerenciada (managed
identity). Abaixo, por simplicidade, passamos como `--secrets`; em produção prefira Key Vault.

## 7. Build das imagens no ACR
```bash
# builda direto no ACR (não precisa de Docker local)
az acr build -r $ACR -t triscal-api:latest ./backend
az acr build -r $ACR -t triscal-web:latest ./frontend
ACR_SERVER=$(az acr show -n $ACR --query loginServer -o tsv)
```

## 8. Identidade — Microsoft Entra ID (direto)
Não há mais Keycloak. A autenticação é **direta no Entra ID**. Configure o App Registration
(plataforma **SPA** + **Expose an API** com o escopo `access_as_user` + `accessTokenAcceptedVersion: 2`)
conforme **`docs/ENTRA_SSO.md`**. Guarde o **tenant id** e o **client id** para os passos 9 e 10:
```bash
ENTRA_TENANT=2b42ae36-0393-4fc6-b055-5d2a93ceb4b4
ENTRA_CLIENT=<client-id-da-App-Registration>
ENTRA_ISSUER="https://login.microsoftonline.com/$ENTRA_TENANT/v2.0"
```

## 9. Backend (API)
```bash
PG_HOST=$(az postgres flexible-server show -g $RG -n $PG --query fullyQualifiedDomainName -o tsv)
az containerapp create -g $RG -n triscal-api --environment $ENVNAME \
  --image $ACR_SERVER/triscal-api:latest --registry-server $ACR_SERVER \
  --target-port 8080 --ingress external --min-replicas 1 --max-replicas 2 \
  --secrets db-pass="$PG_PASS" \
  --env-vars \
    DB_URL="jdbc:postgresql://$PG_HOST:5432/timesheet?sslmode=require" \
    DB_USER="$PG_ADMIN" DB_PASSWORD=secretref:db-pass \
    OIDC_ISSUER_URI="$ENTRA_ISSUER" \
    OIDC_AUDIENCE="$ENTRA_CLIENT" \
    SMTP_HOST="<smtp-host>" SMTP_PORT="587" SMTP_USER="<smtp-user>" SMTP_PASSWORD="<smtp-pass>"
API_URL=https://$(az containerapp show -g $RG -n triscal-api --query properties.configuration.ingress.fqdn -o tsv)
echo "API: $API_URL"
```

## 10. Frontend (SPA)
O SPA lê a config em **runtime** (`window.__ENV__`, gerado pelo container). Builde a imagem **uma vez**
e configure por variáveis de ambiente no Container App (autoridade = Entra, escopo com o da API):
```bash
az acr build -r $ACR -t triscal-web:latest ./frontend
# variáveis no Container App triscal-web:
#   VITE_OIDC_AUTHORITY = $ENTRA_ISSUER
#   VITE_OIDC_CLIENT_ID = $ENTRA_CLIENT
#   VITE_OIDC_SCOPE     = openid profile email api://$ENTRA_CLIENT/access_as_user
#   VITE_API_BASE       = /api
#   BACKEND_ORIGIN      = $API_URL
# (VITE_OIDC_REDIRECT_URI é opcional — o SPA usa a própria origem se omitido)

az containerapp create -g $RG -n triscal-web --environment $ENVNAME \
  --image $ACR_SERVER/triscal-web:latest --registry-server $ACR_SERVER \
  --target-port 80 --ingress external --min-replicas 1 --max-replicas 2
WEB_URL=https://$(az containerapp show -g $RG -n triscal-web --query properties.configuration.ingress.fqdn -o tsv)
echo "Web: $WEB_URL"
```
No `frontend/nginx.conf`, ajuste o `proxy_pass` de `/api/` para o `API_URL` (ou rode API e Web
no mesmo domínio via path routing). Em produção, prefira **domínios próprios** (passo 11).

## 11. Domínios e TLS
```bash
# Exemplo para o frontend (repita para a API se usar subdomínio próprio)
az containerapp hostname add -g $RG -n triscal-web --hostname app.triscal.com.br
az containerapp hostname bind -g $RG -n triscal-web --hostname app.triscal.com.br \
  --environment $ENVNAME --validation-method CNAME
```
Crie os registros CNAME no DNS da Triscal apontando para os FQDNs dos Container Apps. O Azure
provê certificado TLS gerenciado. Atualize a Redirect URI (SPA) no App Registration do Entra com os
domínios finais.

## 12. Configuração pós-deploy (checklist)
1. **Entra ID (App Registration)**: plataforma **SPA** com o redirect do app (domínio final e/ou o
   FQDN do Container App); **Expose an API** com escopo `access_as_user`; `accessTokenAcceptedVersion: 2`.
2. **Variáveis**: `triscal-api` com `OIDC_ISSUER_URI`/`OIDC_AUDIENCE`; `triscal-web` com `VITE_OIDC_*`
   (authority do Entra + escopo da API). Detalhes em `docs/ENTRA_SSO.md`.
3. **Banco**: migrations aplicadas (log do startup); rodar a **migração de dados** (`migration/`)
   se for trazer o histórico do legado.
4. **Perfis**: cadastrar os funcionários e atribuir papéis/aprovadores (Admin/RH) ou via migração.
5. **Alertas**: SMTP configurado; conferir os crons.
6. **Smoke test**: login SSO → `/api/v1/me` retorna papéis → lançar um dia → aprovar.

## 13. CI/CD para produção (opcional, recomendado)
Estenda o `.github/workflows/ci.yml` com um job de deploy no push à `main` (após os testes):
```yaml
  deploy:
    needs: [backend, frontend]
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v5
      - uses: azure/login@v2
        with: { creds: ${{ secrets.AZURE_CREDENTIALS }} }
      - run: az acr build -r $ACR -t triscal-api:${{ github.sha }} ./backend
      - run: az acr build -r $ACR -t triscal-web:${{ github.sha }} ./frontend
      - run: az containerapp update -g $RG -n triscal-api --image $ACR_SERVER/triscal-api:${{ github.sha }}
      - run: az containerapp update -g $RG -n triscal-web --image $ACR_SERVER/triscal-web:${{ github.sha }}
```
Guarde `AZURE_CREDENTIALS` (service principal) e variáveis nos **Secrets** do GitHub. Nunca no repo.

## 14. Custos e operação (estimativa em baixa escala)
- PostgreSQL Burstable B1ms, Container Apps com 1–2 réplicas pequenas e ACR Basic mantêm o custo
  baixo para ~100 usuários. Ative **backup** do PostgreSQL (padrão 7 dias) e **alertas de custo**.
- Escalonar é vertical/horizontal simples nos Container Apps quando necessário.

## 15. Segurança em produção (resumo)
- Segredos no **Key Vault** + managed identity (evite passar secret em texto nos comandos).
- PostgreSQL com `sslmode=require`; restringir acesso por **Private Endpoint/VNet** se possível.
- HTTPS obrigatório (TLS gerenciado); headers de segurança no Nginx.
- Rotacionar o **client secret** do Entra periodicamente.
- Logs sem dados sensíveis; LGPD para dados pessoais (RH/Admin).
