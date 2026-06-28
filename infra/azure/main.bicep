// =====================================================================
// Triscal Apontamento de Horas — Infraestrutura no Azure (Bicep)
// Provisiona: Log Analytics, ACR, PostgreSQL Flexible, Key Vault,
// Container Apps Environment e 2 Container Apps (API, Web).
// Autenticação direta no Microsoft Entra ID (sem Keycloak).
// Arquitetura enxuta (~100 usuários, sem Kubernetes).
// =====================================================================
@description('Prefixo curto para nomear recursos (apenas minúsculas/números).')
param prefix string = 'triscal'

@description('Região do Azure.')
param location string = resourceGroup().location

@description('Tags corporativas aplicadas a todos os recursos (governança/billing). Ex.: { app: "triscal-timesheet", env: "prod", centro_custo: "..." }.')
param tags object = {}

@description('Usuário admin do PostgreSQL.')
param pgAdmin string = 'tsadmin'

@secure()
@description('Senha do admin do PostgreSQL.')
param pgPassword string

@description('Tenant (Directory) ID do Microsoft Entra ID.')
param entraTenantId string = '2b42ae36-0393-4fc6-b055-5d2a93ceb4b4'

@description('Client ID da App Registration (SPA + API). É também a audience validada pela API.')
param entraClientId string

@description('Escopo OIDC pedido pelo SPA — precisa incluir o escopo da API para o access_token ter a audience correta.')
param oidcScope string = 'openid profile email api://${entraClientId}/access_as_user'

@description('Tags das imagens nos Container Apps (ex.: latest ou o SHA do commit).')
param imageTag string = 'latest'

@description('Configuração SMTP para alertas (host/porta/usuario).')
param smtpHost string = ''
param smtpPort string = '587'
param smtpUser string = ''
@secure()
param smtpPassword string = ''

var suffix = uniqueString(resourceGroup().id)
var acrName = toLower('${prefix}acr${suffix}')
var pgName = toLower('${prefix}-pg-${suffix}')
var kvName = toLower('${prefix}kv${substring(suffix,0,8)}')
var lawName = '${prefix}-law'
var caeName = '${prefix}-cae'

// ---------- Log Analytics ----------
resource law 'Microsoft.OperationalInsights/workspaces@2023-09-01' = {
  name: lawName
  location: location
  tags: tags
  properties: { sku: { name: 'PerGB2018' }, retentionInDays: 30 }
}

// ---------- Container Registry ----------
resource acr 'Microsoft.ContainerRegistry/registries@2023-07-01' = {
  name: acrName
  location: location
  tags: tags
  sku: { name: 'Basic' }
  properties: { adminUserEnabled: true }
}

// ---------- Key Vault ----------
resource kv 'Microsoft.KeyVault/vaults@2023-07-01' = {
  name: kvName
  location: location
  tags: tags
  properties: {
    tenantId: subscription().tenantId
    sku: { family: 'A', name: 'standard' }
    enableRbacAuthorization: true
    accessPolicies: []
  }
}
resource secretDb 'Microsoft.KeyVault/vaults/secrets@2023-07-01' = {
  parent: kv
  name: 'db-password'
  properties: { value: pgPassword }
}

// ---------- PostgreSQL Flexible Server ----------
resource pg 'Microsoft.DBforPostgreSQL/flexibleServers@2024-08-01' = {
  name: pgName
  location: location
  tags: tags
  sku: { name: 'Standard_B1ms', tier: 'Burstable' }
  properties: {
    version: '16'
    administratorLogin: pgAdmin
    administratorLoginPassword: pgPassword
    storage: { storageSizeGB: 32 }
    backup: { backupRetentionDays: 7, geoRedundantBackup: 'Disabled' }
    highAvailability: { mode: 'Disabled' }
  }
}
resource pgDb 'Microsoft.DBforPostgreSQL/flexibleServers/databases@2024-08-01' = {
  parent: pg
  name: 'timesheet'
}
// Permite serviços do Azure (0.0.0.0). Para produção, prefira VNet/Private Endpoint.
resource pgFw 'Microsoft.DBforPostgreSQL/flexibleServers/firewallRules@2024-08-01' = {
  parent: pg
  name: 'allow-azure'
  properties: { startIpAddress: '0.0.0.0', endIpAddress: '0.0.0.0' }
}

// ---------- Container Apps Environment ----------
resource cae 'Microsoft.App/managedEnvironments@2024-03-01' = {
  name: caeName
  location: location
  tags: tags
  properties: {
    appLogsConfiguration: {
      destination: 'log-analytics'
      logAnalyticsConfiguration: {
        customerId: law.properties.customerId
        sharedKey: law.listKeys().primarySharedKey
      }
    }
  }
}

var acrServer = acr.properties.loginServer
var acrPassword = acr.listCredentials().passwords[0].value

// Autenticação direta no Microsoft Entra ID (sem Keycloak). issuer v2 do tenant:
var entraIssuer = 'https://login.microsoftonline.com/${entraTenantId}/v2.0'

// ---------- API ----------
resource api 'Microsoft.App/containerApps@2024-03-01' = {
  name: '${prefix}-api'
  location: location
  tags: tags
  properties: {
    managedEnvironmentId: cae.id
    configuration: {
      ingress: { external: true, targetPort: 8080, transport: 'auto' }
      registries: [ { server: acrServer, username: acr.name, passwordSecretRef: 'acr-pass' } ]
      secrets: [
        { name: 'acr-pass', value: acrPassword }
        { name: 'db-pass', value: pgPassword }
        { name: 'smtp-pass', value: smtpPassword }
      ]
    }
    template: {
      containers: [
        {
          name: 'api'
          image: '${acrServer}/triscal-api:${imageTag}'
          resources: { cpu: json('0.5'), memory: '1Gi' }
          env: [
            { name: 'DB_URL', value: 'jdbc:postgresql://${pg.properties.fullyQualifiedDomainName}:5432/timesheet?sslmode=require' }
            { name: 'DB_USER', value: pgAdmin }
            { name: 'DB_PASSWORD', secretRef: 'db-pass' }
            { name: 'OIDC_ISSUER_URI', value: entraIssuer }
            { name: 'OIDC_AUDIENCE', value: entraClientId }
            { name: 'SMTP_HOST', value: smtpHost }
            { name: 'SMTP_PORT', value: smtpPort }
            { name: 'SMTP_USER', value: smtpUser }
            { name: 'SMTP_PASSWORD', secretRef: 'smtp-pass' }
          ]
        }
      ]
      scale: { minReplicas: 1, maxReplicas: 2 }
    }
  }
}
var apiUrl = 'https://${api.properties.configuration.ingress.fqdn}'

// ---------- Web (SPA) ----------
resource web 'Microsoft.App/containerApps@2024-03-01' = {
  name: '${prefix}-web'
  location: location
  tags: tags
  properties: {
    managedEnvironmentId: cae.id
    configuration: {
      ingress: { external: true, targetPort: 80, transport: 'auto' }
      registries: [ { server: acrServer, username: acr.name, passwordSecretRef: 'acr-pass' } ]
      secrets: [ { name: 'acr-pass', value: acrPassword } ]
    }
    template: {
      containers: [
        {
          name: 'web'
          image: '${acrServer}/triscal-web:${imageTag}'
          resources: { cpu: json('0.25'), memory: '0.5Gi' }
          env: [
            { name: 'VITE_OIDC_AUTHORITY', value: entraIssuer }
            { name: 'VITE_OIDC_CLIENT_ID', value: entraClientId }
            { name: 'VITE_OIDC_SCOPE', value: oidcScope }
            { name: 'VITE_API_BASE', value: '/api' }
            // VITE_OIDC_REDIRECT_URI omitido → o SPA usa a própria origem (window.location.origin).
            { name: 'BACKEND_ORIGIN', value: apiUrl }
          ]
        }
      ]
      scale: { minReplicas: 1, maxReplicas: 2 }
    }
  }
}

output acrLoginServer string = acrServer
output apiUrl string = apiUrl
output webUrl string = 'https://${web.properties.configuration.ingress.fqdn}'
output postgresFqdn string = pg.properties.fullyQualifiedDomainName
output keyVaultName string = kv.name
