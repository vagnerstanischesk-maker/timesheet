# Build do SPA e serve via nginx (não exige Node na máquina — só Docker).
FROM node:20 AS build
WORKDIR /app
COPY package.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:1.27-alpine
# Conteúdo estático (inclui /config.js de runtime, reescrito no start pelo entrypoint).
COPY --from=build /app/dist /usr/share/nginx/html
# Template do nginx: a imagem faz envsubst de ${BACKEND_ORIGIN} -> /etc/nginx/conf.d/default.conf.
COPY default.conf.template /etc/nginx/templates/default.conf.template
# Gera o config.js a partir das variáveis de ambiente no start do container.
COPY docker-entrypoint.d/30-window-env.sh /docker-entrypoint.d/30-window-env.sh
RUN chmod +x /docker-entrypoint.d/30-window-env.sh
# Default de dev (docker-compose). Em produção, sobrescreva com a FQDN da API.
ENV BACKEND_ORIGIN=http://backend:8080
EXPOSE 80
