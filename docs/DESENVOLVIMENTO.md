# Guia de desenvolvimento — Triscal Apontamento de Horas

Fundação do novo sistema (monorepo). Estrutura:

```
backend/    Spring Boot 3 (Java 21, Maven) — API REST, JPA, Flyway, OIDC Resource Server
frontend/   React 18 + TypeScript + Vite (PWA) — tema/cores da marca Triscal
infra/      docker-compose (PostgreSQL + Keycloak) para desenvolvimento local
docs/       documentação técnica
```

## Pré-requisitos
- Java 21, Maven 3.9+
- Node 20+
- Docker + Docker Compose

## Subir o ambiente

```bash
# 1) infra (PostgreSQL + Keycloak)
cd infra && docker compose up -d

# 2) backend (porta 8080) — aplica as migrations Flyway no PostgreSQL
cd ../backend && mvn spring-boot:run

# 3) frontend (porta 5173, proxy /api -> 8080)
cd ../frontend && npm install && npm run dev
```

Healthcheck: `GET http://localhost:8080/actuator/health` e `GET http://localhost:8080/api/v1/ping`
(este último exige token; configure o realm `triscal` no Keycloak).

## Banco de dados
- O schema está em `backend/src/main/resources/db/migration/V1__core_schema.sql` (Flyway).
- `ddl-auto: validate` — o Hibernate apenas valida; o schema é versionado por migrations.
- Próximas migrations: `V2__...`, `V3__...` (nunca editar uma migration já aplicada).

## Autenticação (OIDC)
- Keycloak em `http://localhost:8081` (admin/admin). Crie o realm `triscal` e configure a
  federação com o Microsoft Entra ID (Identity Provider OIDC).
- Papéis e hierarquia: **100% internos à aplicação**. O Entra/Keycloak fornece apenas a
  identidade (autenticação); o token NÃO carrega papéis. No login, casa-se o sub/e-mail do
  token com `funcionario.cdg_usur` e resolvem-se papéis/permissões via `perfil`,
  `funcionario_perfil` e `func_cd_func_aprovador`.

## Próximos passos (ver PROMPT_GERACAO_NOVO_SISTEMA.md)
1. Entidades JPA + repositórios sobre o schema V1.
2. Autenticação OIDC ponta a ponta + RBAC.
3. Módulo do log da quinzena e lançamento do dia.
4. Aprovação + regra de **BHE** (com testes — ver skill triscal-apontamento-horas).
5. Acompanhamento de projeto (% e fechamento quinzenal).
6. Cadastros, dashboards, alertas.
7. Migração de dados Oracle -> PostgreSQL (a partir do dump completo).

## Pré-visualização da interface (sem backend)
Para navegar por todas as telas com **dados fictícios e sem login** (útil para demonstrar a UI):
```bash
cd frontend && npm install && npm run demo
```
Abre em http://localhost:5173 com um banner "Modo pré-visualização". Ativado pela flag
`VITE_PREVIEW=true` (arquivo `frontend/.env.preview`). **Não use em produção** — o login e a
API reais continuam valendo no `npm run dev`/build normais.
