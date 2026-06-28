# ADR-001 — Arquitetura simplificada para ~100 usuários

**Status:** Aceito · **Data:** 2026-06 · **Contexto:** Triscal Apontamento de Horas

## Contexto
O sistema é de uso interno por ~100 colaboradores (não é público nem precisa escalar para
milhares). O objetivo é simplicidade operacional e baixo custo, mantendo segurança,
portabilidade (no vendor lock-in) e qualidade.

## Decisão
Adotar um **monólito modular** simples:

- **Backend** Spring Boot único, em **camadas pragmáticas** (controller → service → repository),
  sem hexagonal completo.
- **Frontend** React/Vite servido como estático (PWA).
- **PostgreSQL** único, com **backup agendado** (sem réplicas).
- **Jobs** com **Spring Scheduler in-process** (alertas diário/quinzenal) — **sem broker**
  (RabbitMQ) e **sem cache distribuído** (Redis). Cache em memória (Caffeine) só se medir necessidade.
- **Hospedagem** em **Azure App Service** ou **Container Apps**, **instância única** (escala
  vertical se necessário). **Sem Kubernetes**.
- **Observabilidade** via **Actuator + logs estruturados**; Prometheus/Grafana opcionais.
- **Identidade**: Keycloak federando o Entra ID (mantido pela portabilidade do OIDC).
- **CI/CD** GitHub Actions; Terraform opcional (poucos recursos).

## Consequências
**Prós:** menos peças para operar, custo menor, deploy e troubleshooting mais simples, onboarding
rápido. Continua portável (contêiner + OIDC + PostgreSQL).
**Contras / quando revisitar:** se o uso crescer muito além de algumas centenas de usuários
concorrentes, ou surgirem integrações pesadas, reavaliar broker, cache distribuído e orquestração.
A escolha por contêiner + OIDC + Postgres mantém esse caminho aberto sem reescrita.
