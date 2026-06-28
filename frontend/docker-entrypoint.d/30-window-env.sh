#!/bin/sh
# Gera /usr/share/nginx/html/config.js a partir das variáveis de ambiente do container,
# para que o SPA (já buildado) leia a config em RUNTIME via window.__ENV__.
# Roda automaticamente: o entrypoint do nginx executa os scripts de /docker-entrypoint.d/.
set -eu

cat > /usr/share/nginx/html/config.js <<EOF
window.__ENV__ = {
  VITE_OIDC_AUTHORITY: "${VITE_OIDC_AUTHORITY:-}",
  VITE_OIDC_CLIENT_ID: "${VITE_OIDC_CLIENT_ID:-}",
  VITE_OIDC_REDIRECT_URI: "${VITE_OIDC_REDIRECT_URI:-}",
  VITE_OIDC_SCOPE: "${VITE_OIDC_SCOPE:-openid profile email}",
  VITE_API_BASE: "${VITE_API_BASE:-/api}"
};
EOF

echo "30-window-env.sh: config.js gerado (authority=${VITE_OIDC_AUTHORITY:-<vazio>})."
