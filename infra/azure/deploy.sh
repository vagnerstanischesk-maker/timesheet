#!/usr/bin/env bash
# Provisiona a infra no Azure e publica as imagens. Requer: az CLI logado (az login).
#
# Uso:
#   bash infra/azure/deploy.sh            # valida + provisiona + builda imagens + deploy final
#   bash infra/azure/deploy.sh --validate # SÓ valida (lint do Bicep + what-if), não altera nada
#
# Variáveis: SUBSCRIPTION, PREFIX, RG, LOC, PARAMS, TAG (com padrões abaixo).
set -euo pipefail

SUBSCRIPTION="${SUBSCRIPTION:-}"                       # ID/nome da assinatura corporativa (recomendado em tenant com vários sistemas)
PREFIX="${PREFIX:-triscal}"
RG="${RG:-rg-${PREFIX}-timesheet}"
LOC="${LOC:-brazilsouth}"
PARAMS="${PARAMS:-infra/azure/main.parameters.json}"   # crie a partir do .example.json
TAG="${TAG:-latest}"
TEMPLATE="infra/azure/main.bicep"
MODE="${1:-deploy}"

command -v az >/dev/null || { echo "Azure CLI (az) não encontrado. Instale e rode 'az login'."; exit 1; }
[ -f "$PARAMS" ] || { echo "Arquivo de parâmetros não encontrado: $PARAMS (copie do .example.json)"; exit 1; }
if [ -n "$SUBSCRIPTION" ]; then
  echo ">> Selecionando a assinatura: $SUBSCRIPTION"
  az account set --subscription "$SUBSCRIPTION"
fi
echo ">> Assinatura ativa: $(az account show --query name -o tsv) ($(az account show --query id -o tsv))"

echo ">> Lint do Bicep (compila e checa sintaxe/avisos)"
az bicep build --file "$TEMPLATE" --stdout >/dev/null
echo "   OK: Bicep válido."

echo ">> Garantindo o grupo de recursos ($RG em $LOC)"
az group create -n "$RG" -l "$LOC" 1>/dev/null

echo ">> what-if (pré-visualização das mudanças, sem aplicar)"
az deployment group what-if -g "$RG" --template-file "$TEMPLATE" --parameters @"$PARAMS" imageTag="$TAG" || true

if [ "$MODE" = "--validate" ]; then
  echo ">> Modo --validate: nada foi alterado. Encerrando."
  exit 0
fi

read -r -p ">> Confirmar provisionamento em '$RG'? (digite 'sim'): " OK
[ "$OK" = "sim" ] || { echo "Cancelado."; exit 1; }

echo ">> 1/3 Deploy base (ACR/Postgres/KV/env/apps) via Bicep"
az deployment group create -g "$RG" --name infra-base \
  --template-file "$TEMPLATE" --parameters @"$PARAMS" imageTag="$TAG" 1>/dev/null || true

echo ">> 2/3 Build das imagens no ACR"
ACR=$(az acr list -g "$RG" --query "[0].name" -o tsv)
[ -n "$ACR" ] || { echo "ACR não encontrado após o deploy base."; exit 1; }
az acr build -r "$ACR" -t triscal-api:"$TAG" ./backend
az acr build -r "$ACR" -t triscal-web:"$TAG" ./frontend

echo ">> 3/3 Deploy final (apps com as imagens publicadas)"
az deployment group create -g "$RG" --name main \
  --template-file "$TEMPLATE" --parameters @"$PARAMS" imageTag="$TAG"

echo ">> Saídas do deployment:"
az deployment group show -g "$RG" -n main --query "properties.outputs" -o jsonc

echo "Concluído. Configure DNS e o App Registration do Entra conforme docs/ENTRA_SSO.md e docs/DEPLOY_AZURE.md."
