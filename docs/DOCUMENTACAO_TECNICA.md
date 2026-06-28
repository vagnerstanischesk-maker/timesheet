# Documentação técnica — Triscal Apontamento de Horas

Versão 1.0 · sistema de apontamento de horas (timesheet) que substitui o legado JSP/Java + Oracle.

## 1. Visão geral

Aplicação web corporativa (~100 colaboradores) para registro, aprovação e acompanhamento de
horas. Monólito modular: um backend (API REST) e um frontend (SPA/PWA), com PostgreSQL.
Autenticação por SSO **direto no Microsoft Entra ID** (OIDC + PKCE; sem Keycloak — ver
`docs/ENTRA_SSO.md`); autorização (papéis e hierarquia) é **100% interna à aplicação**.

## 2. Arquitetura

### 2.1 Contexto (C4 — nível 1)
- **Colaboradores, gestores, gestores de projeto, Admin/RH** acessam o **SPA React** no navegador.
- O SPA autentica **direto no Microsoft Entra ID** (OIDC + PKCE) e recebe um access_token cuja
  audience é a própria API. O token traz só a identidade (e-mail/UPN/oid).
- O SPA consome a **API REST (Spring Boot)**, que valida o JWT e resolve papéis/permissões nas
  tabelas internas do **PostgreSQL**.
- Jobs agendados enviam **alertas por SMTP**.

### 2.2 Contêineres (C4 — nível 2)
| Contêiner | Tecnologia | Responsabilidade |
|-----------|-----------|------------------|
| Web (SPA) | React 18 + TypeScript + Vite (PWA), servido por Nginx | UI, guarda de rotas por papel, autosave |
| API | Java 21 + Spring Boot 3 | Regras de negócio, REST, segurança, jobs |
| Banco | PostgreSQL 16 (Flyway) | Persistência e histórico |
| Identidade | Microsoft Entra ID (direto) | Login OIDC/PKCE do SPA; JWT validado pela API |

### 2.3 Camadas do backend (pragmáticas)
`api` (controllers REST) → `application` (services, regras) → `domain` (model + service puro:
BHE, quinzena, execução, validação) → `infrastructure/persistence` (Spring Data JPA).
Estatística atual: **112 classes Java**, **17 controllers**.

### 2.4 Decisões
- **ADR-001**: arquitetura simplificada para ~100 usuários (sem Kubernetes, sem broker, sem
  cache distribuído). Ver `docs/ADR-001-arquitetura-simplificada.md`.
- **RBAC interno**: o token não carrega papéis; ver `docs/AUTENTICACAO.md`.
- **No vendor lock-in**: contêiner + OIDC padrão + PostgreSQL → portável entre nuvens.

## 3. Modelo de dados

Schema versionado por Flyway (`backend/src/main/resources/db/migration/V1__core_schema.sql`).
Tabelas-núcleo (semântica preservada do legado):

- **funcionario** (tp_funcionario E/F, cd_empresa, func_cd_func_aprovador, in_possui_banco_horas,
  saldos BHE50/100, cdg_usur — vínculo com a identidade) e **perfil** / **funcionario_perfil** (RBAC).
- **empresa** (filial), **clifor** (cliente).
- **projeto** → **projeto_fase** → **projeto_tarefa** (in_tarefa_externa) →
  **projeto_tarefa_funcionario** (alocação).
- **log_periodo** (quinzena), **timesheet_dia** (status A/F/P/R, ausências) e
  **timesheet_dia_tarefa** (horas CO/NC/extra).
- **feriado** (NN/NF/LN/LF), **movimentacao_saldo_bhe**, **jornada_trabalho**, **param_geral**.
- **andamento_projeto_hist** e **fechamento_quinzenal** (acompanhamento de projeto), **auditoria**.

Chaves naturais do legado mantidas (ex.: `cd_funcionario`, `cd_projeto`, `seq_projeto_fase/tarefa`).

## 4. Regras de negócio (núcleo, testadas)

- **Jornada**: padrão 8h/dia (`JORNADA_PADRAO_HORAS`); **mínimo 0,5h** e **múltiplos de 0,5h** por
  lançamento; **máximo 24h/dia** (`LancamentoValidator`, testado).
- **Lançamento por tarefa escolhida**: o colaborador **seleciona** projeto/fase/tarefa a lançar
  (não há listagem automática de todas as alocações). Vários projetos/fases/tarefas por dia;
  **CO e NC na mesma tarefa/dia são permitidos**. A lista de escolha (`tarefas-alocadas?data=`)
  traz só tarefas **alocadas (INI)**, não encerradas e **dentro da validade** da tarefa
  (`dataInicio ≤ data ≤ dataFim`) na data de referência.
- **Carregar período anterior**: ao abrir um período **sem lançamentos**, `gradeEdicao` (modo
  colaborador, `seedAnterior=true`) semeia linhas vazias com as tarefas do **período anterior** —
  só as ainda alocadas, não externas e dentro da validade. Não se aplica à visão de aprovação.
- **Ausências por dia** (não vinculadas a projeto): **Ausência BHE** (debitada do banco de horas) e
  **Ausência ABN** (abonada). Compõem o total do dia junto com as horas de projeto.
- **Total e horas esperadas do dia**: total do dia = horas de projeto **+** ausências. Horas normais
  esperadas: **8h** em dia útil, **horas a trabalhar do feriado** quando feriado, **0** no fim de
  semana (`QuinzenaService.gradeEdicao`).
- **Status do dia**: `A → F → (P | R)`; gestor nunca aprova o próprio log.
- **BHE** (`CalculadoraBhe`, testado), ao aprovar o dia: PJ (`E`) → só 50%; CLT (`F`) → 100% em
  domingo/feriado, senão 50%; participa quem tem banco de horas; ausência de BHE reduz o excedente.
- **Quinzena** (`Quinzena`, testado): cortes parametrizáveis (`QUINZENA_CORTE_1/2` = **1/16**;
  períodos **1–15** e **16–fim do mês**, definidos na migração **V4**). O domínio também suporta o
  modelo legado que cruza o mês quando `corte1 > 1`. A aprovação é **dia a dia** (cada dia
  aprovado/rejeitado individualmente, com justificativa obrigatória na rejeição; cada dia gera seu
  BHE) e há atalho "aprovar quinzena toda". `Quinzena.referenciaDe/datasDoPeriodo/periodoAnterior/
  periodoSeguinte` dão suporte à navegação entre quinzenas (telas de Quinzena e Aprovação).
- **% de execução** (`CalculadoraExecucao`, testado): % por horas = realizado/previsto; desvio =
  % informado − % por horas.
- **Relatório de acompanhamento de projetos** (consolidado, todos os períodos): **horas vendidas** =
  previstas do projeto; **horas executadas** = soma de todos os lançamentos; **diferença %** =
  % por horas − % informado pelo gestor; **diferença em horas** = diferença % × horas vendidas
  (atraso/estouro). Ordenado por cliente e projeto.
- **Relatório de logs pendentes**: consolida o status por (colaborador × quinzena), considera só
  status ≠ Fechado e ≠ Aprovado e **exclui o período atual e futuros** (`chave(ano,mes,período) ≥
  período atual` é descartado).
- **Alocação**: só lança em tarefa onde está alocado/ativo (status `INI`). Tarefa externa = leitura.

## 5. API REST (resumo)

Base: `/api/v1`. Toda chamada exige JWT válido (exceto `/actuator/health`). Autorização por
papel e por dado é feita no backend.

| Método | Rota | Papel |
|--------|------|-------|
| GET | `/me` | autenticado |
| GET | `/timesheet/tarefas-alocadas?data=` (filtra por alocação + validade da tarefa na data) | Colaborador |
| GET/PUT | `/timesheet/dia/...` · `/timesheet/dia` | Colaborador |
| POST | `/timesheet/dia/{ano}/{mes}/{dia}/fechar` | Colaborador |
| GET | `/timesheet/quinzena/{ano}/{mes}/{periodo}` | Colaborador |
| GET | `/timesheet/quinzena/{ano}/{mes}/{periodo}/edicao` (grade editável do período) | Colaborador |
| POST | `/timesheet/quinzena/{ano}/{mes}/{periodo}/fechar` | Colaborador |
| GET | `/aprovacao/pendentes` | Gestor |
| GET | `/aprovacao/edicao/{func}/{ano}/{mes}/{periodo}` (grade do log, leitura) | Gestor |
| POST | `/aprovacao/dia/{func}/{ano}/{mes}/{dia}/aprovar` · `/rejeitar` | Gestor |
| POST | `/aprovacao/quinzena/{func}/{ano}/{mes}/{periodo}` | Gestor |
| GET | `/projetos/lista` (cliente→projeto, com nome do cliente) | Gestor de Projeto |
| GET | `/projetos/{cd}/acompanhamento/{ano}/{mes}/{periodo}` | Gestor de Projeto |
| POST | `/projetos/{cd}/acompanhamento/andamento` · `/.../fechar` | Gestor de Projeto |
| GET | `/dashboards/resumo/{ano}/{mes}` | Gestão |
| GET | `/relatorios/logs-nao-fechados` | Gestão |
| GET | `/relatorios/acompanhamento-projetos` | Gestão |
| GET | `/relatorios/bhe?inicio=&fim=` (movimentação de BHE no período) | Gestão |
| GET/PUT | `/cadastros/projetos` · `/funcionarios` · `/clientes` · `/feriados` · `/verticais` | Admin/RH ou Diretoria |
| GET/PUT/DELETE | `/cadastros/projetos/{cd}/fases` · `/tarefas` · `/alocacoes` | Admin/RH ou Diretoria |
| POST | `/cadastros/bhe/manual` | Admin/RH ou Diretoria |

> "Gestão" = qualquer um de **GESTOR, GESTOR_PROJETO, DIRETORIA, ADMIN_RH**. Os menus **Relatórios**
> e **Dashboards** no frontend são **índices** (`RelatoriosIndexPage`, `DashboardsIndexPage`) que
> listam os itens disponíveis; novos relatórios/dashboards entram só acrescentando uma linha à lista.

### 5.1 Controle de acesso por papel (menu × perfil)

Login por SSO (Entra ID), mas **papéis 100% internos**. Front (link + `ProtectedRoute`) e back
(checagem por papel em cada controller) aplicam a mesma matriz:

| Recurso | Perfis |
|---|---|
| Lançamento (Meu dia, Quinzena) | todos os papéis |
| Aprovações | GESTOR, ADMIN_RH, DIRETORIA — e, por dado, **só o aprovador cadastrado** do colaborador |
| Acompanhamento de projetos | GESTOR_PROJETO, ADMIN_RH, DIRETORIA |
| Dashboards e Relatórios | GESTOR, GESTOR_PROJETO, ADMIN_RH, DIRETORIA |
| Cadastros | ADMIN_RH, DIRETORIA |

> Recomenda-se expor a especificação completa via **OpenAPI/Swagger** (springdoc) — pendência P5.

## 6. Segurança

- JWT validado por issuer/audience (Spring Security Resource Server). CSRF desabilitado para a
  API stateless; CORS restrito; headers de segurança no Nginx/back.
- Autorização **por papel e por dado**: colaborador só acessa os próprios lançamentos; gestor só
  aprova subordinados (`func_cd_func_aprovador`); cadastros restritos a Admin/RH.
- Segredos em variáveis de ambiente / cofre (Key Vault); nunca no repositório (ex.: client secret
  do Entra fica em `infra/.env`, gitignored).
- Auditoria de autor e timestamp; campos pessoais sensíveis com acesso restrito (LGPD).

## 7. Build, execução e deploy

- **Local (Docker)**: `cd infra && docker compose up -d` (apenas PostgreSQL; auth direto no Entra); backend
  `mvn spring-boot:run`; frontend `npm run dev`. Ver `docs/DESENVOLVIMENTO.md` e `AMBIENTE_SETUP.md`.
- **Imagens**: `backend/Dockerfile` (multi-stage, JDK 21) e `frontend/Dockerfile` (build + Nginx).
- **CI** (`.github/workflows/ci.yml`): `mvn clean verify` (JDK 21) + `npm run build`/test (Node 20).
- **Hospedagem**: contêiner único em Azure App Service / Container Apps; PostgreSQL gerenciado com
  backup agendado. **Sem Kubernetes** nesta fase.

## 8. Configuração (variáveis de ambiente)

Backend: `OIDC_ISSUER_URI`, `OIDC_AUDIENCE`, `DB_URL/DB_USER/DB_PASSWORD`, `SMTP_*`,
`app.alertas.*-cron`. Frontend: `VITE_OIDC_AUTHORITY`, `VITE_OIDC_CLIENT_ID`,
`VITE_OIDC_REDIRECT_URI`, `VITE_API_BASE`. Detalhes em `docs/AUTENTICACAO.md`.

## 9. Migração de dados

Kit em `migration/`: ETL Oracle→PostgreSQL (`etl.py`, oracledb→psycopg2 com mapeamento de
colunas), `validacao.sql` (contagens, reconciliação de BHE, integridade, acentuação) e plano de
cutover (`migration/README.md`).

## 10. Observabilidade e operação

- Spring Boot Actuator (`/actuator/health`, `/actuator/info`); logs estruturados.
- Jobs agendados (Spring Scheduler) para alertas; sem broker.
- Prometheus/Grafana opcionais nesta escala.

## 11. Qualidade e pendências

- Testes unitários cobrindo as regras críticas (BHE, quinzena, execução, jornada, status); CI verde.
- Relatórios já entregues: **logs pendentes**, **acompanhamento de projetos** e **movimentação de
  BHE** (todos com export CSV), acessíveis pelo índice de Relatórios.
- Pendências (ver `docs/PENDENCIAS.md`): testes de integração (Testcontainers), OpenAPI/Swagger,
  relatórios de billing/faturamento e exportação em PDF/Excel, cronômetro, integração que substitui
  o OTRS, execução da migração com o dump real.
