// Configuração de RUNTIME do SPA. Em produção este arquivo é REESCRITO pelo container
// (ver docker-entrypoint.d/30-window-env.sh) a partir das variáveis de ambiente.
// Em dev (vite) fica vazio e o app usa import.meta.env (.env.development).
window.__ENV__ = {};
