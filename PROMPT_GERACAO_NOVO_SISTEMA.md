# Prompt para Geração do Novo Sistema de Apontamento de Horas — Triscal

> Use este prompt em um assistente de geração de código (Claude, Copilot, etc.). Ele é
> autocontido: descreve stack, arquitetura, modelo de dados, regras de negócio, telas e
> entregáveis. Gere o código de forma incremental, módulo por módulo, com testes,
> documentação técnica e manual do usuário.

---

## 1. Contexto e objetivo

Você é um(a) engenheiro(a) de software sênior. Construa do zero um **novo sistema web de
apontamento de horas (timesheet)** para a empresa **Triscal**, substituindo um sistema
legado em JSP/Java + Oracle. O novo sistema deve ser **moderno, responsivo (mobile-first),
seguro** e hospedado no **Microsoft Azure**, com login via **SSO Microsoft Entra ID**.
O histórico de dados do legado será migrado (ver seção 10), portanto o modelo de dados deve
preservar a semântica das tabelas existentes.

**Escala e simplicidade:** ~100 colaboradores internos (uso corporativo, não público). Otimize
para **simplicidade operacional e baixo custo**, não para escala de milhares. Monólito modular
(um backend + um frontend), instância única, PostgreSQL único com backup agendado. Evite
componentes de infraestrutura que só se justificam em alta escala (Kubernetes, brokers, cache
distribuído) salvo necessidade comprovada.

## 2. Princípio diretor: tecnologias abertas, sem dependência de fornecedor (no vendor lock-in)

Toda a stack deve ser **open-source e portável**. O Azure é apenas o ambiente de
**hospedagem** e o Entra ID o **provedor de identidade corporativo** — mas a aplicação NÃO
deve depender de bibliotecas, SDKs ou serviços proprietários da Microsoft (ou de qualquer
outro fornecedor) para funcionar. Regras:

- Comunicação com identidade via **padrões abertos** (OpenID Connect / OAuth2 / SAML), nunca
  via SDK proprietário acoplado. Use um **broker de identidade open-source (Keycloak)** que
  federa o Entra ID; assim trocar de IdP (ou de nuvem) não exige reescrita.
- Empacotamento em **contêineres (Docker)** — roda em Azure (App Service/Container Apps), em
  qualquer outra nuvem ou on-premises. Orquestração pesada (Kubernetes) é dispensável nesta escala.
- Persistência, mensageria, cache, observabilidade e e-mail por **componentes open-source**
  (ver seção 3), evitando PaaS proprietário sempre que houver equivalente aberto.
- Sem serviços serverless proprietários no núcleo de negócio; jobs como processos/contêineres.

## 3. Stack tecnológica (open-source)

- **Backend:** **Java 21 + Spring Boot 3** (Spring Web, Spring Security, Spring Data JPA,
  Spring Validation). **Arquitetura em camadas pragmáticas** (controller → service → repository);
  hexagonal completo é dispensável nesta escala. Build com **Maven**. (Alternativa aberta: Node.js + NestJS.)
- **Banco de dados:** **PostgreSQL 16**. Migrations com **Flyway** ou **Liquibase**; ORM
  JPA/Hibernate.
- **Frontend:** **React 18 + TypeScript + Vite**, como **PWA** (instalável, offline-friendly).
  UI com biblioteca open-source (MUI ou Mantine). Mobile-first e acessível (WCAG AA).
- **Identidade/SSO:** **Keycloak** (broker) federando o Entra ID via OIDC. Backend valida JWT
  por OIDC padrão (Spring Security Resource Server); frontend usa biblioteca OIDC genérica
  (oidc-client-ts / react-oidc-context). Sem MSAL, sem senhas locais.
- **Autorização:** RBAC **100% interno à aplicação**. O Entra ID/Keycloak fornece **apenas a
  identidade (autenticação)** — o token NÃO traz papéis, grupos nem hierarquia. Papéis
  (Colaborador, Gestor, Gestor de Projeto, Admin/RH, Diretoria), a relação de **aprovador** e a
  alocação são geridos em **tabelas internas** (`perfil`, `funcionario_perfil`,
  `func_cd_func_aprovador`). No login, casa-se o identificador do token (sub/e-mail) com
  `funcionario.cdg_usur` para resolver papéis e permissões.
- **Jobs/agendamento:** **Spring Scheduler in-process** (sem broker). RabbitMQ/Redis **não são
  necessários** nesta escala (~100 usuários internos); cache em memória (Caffeine) só se medir necessidade.
- **E-mail/notificações:** **SMTP** com templates (Thymeleaf); sem API proprietária.
- **Gráficos/relatórios:** **Recharts/Chart.js** no front; **OpenPDF/JasperReports** (PDF) e
  **Apache POI** (Excel) no back.
- **Observabilidade:** **Spring Boot Actuator** + logs estruturados. Prometheus/Grafana **opcionais**.
- **Segredos:** variáveis de ambiente via cofre do provedor (ex.: Azure Key Vault) ou
  secrets do orquestrador; acesso abstraído.
- **Hospedagem:** contêiner Docker em **Azure App Service** ou **Container Apps** (instância única,
  escala vertical se preciso). **Sem Kubernetes** nesta fase. **CI/CD:** GitHub Actions. Terraform opcional.

## 4. Padrões de segurança (obrigatórios)

Siga OWASP ASVS / Top 10. Em especial:
- Toda API exige token JWT válido (OIDC); valide audience, issuer e expiração.
- Autorização por papel **e por dado**, com papéis/hierarquia resolvidos **na aplicação**
  (não a partir de claims do token): colaborador só vê/edita os próprios lançamentos; gestor só
  aprova quem é seu subordinado (`func_cd_func_aprovador`).
- Validação server-side (Bean Validation) além da validação no front.
- Acesso a dados via JPA com queries parametrizadas — nunca concatenar SQL.
- Proteção CSRF onde aplicável, CORS restrito, headers de segurança (HSTS, CSP, X-Content-Type).
- Segredos somente no cofre; nunca em código ou repositório.
- Auditoria: autor (`usuario`) e timestamp (`dt_ult_alt`) em toda alteração; log de auditoria
  para aprovações/rejeições e ajustes de saldo de BHE.
- Rate limiting e logging estruturado (sem dados sensíveis em log).
- LGPD: dados pessoais (CPF, RG, banco) com acesso restrito a RH/Admin.

## 5. Conceitos de domínio (glossário)

- **Colaborador/Funcionário:** lança horas. `TP_FUNCIONARIO` = `E` (PJ) ou `F` (CLT) — afeta
  hora extra/BHE.
- **Projeto:** interno (`IN_INTERNO='S'`, cliente = Triscal) ou de cliente (CLIFOR). Pertence a
  uma filial de origem (EMPRESA).
- **Fase -> Tarefa:** a **tarefa** é o menor nível de lançamento.
- **Tarefa externa (`IN_TAREFA_EXTERNA='S'`):** horas de integração externa (legado: OTRS);
  exibidas no log mas **somente leitura**.
- **Alocação:** define quem pode lançar (PROJETO_TAREFA_FUNCIONARIO / ALOCACAOPROJ).
- **Quinzena (LOG_PERIODO):** dois períodos/mês (legado: 11–25 e 26–10), parametrizável.
- **CO (com cobrança)** vs **NC (sem cobrança)**, com variantes **EXTRA**.
- **BHE:** banco de horas para compensação; saldos `BHE50` e `BHE100`.
- **Ausência:** `AUSBHE` (abatida do banco) e `AUSABN` (abonada).
- **Gestor de Projeto:** informa o **% de andamento** e participa do fechamento quinzenal.

## 6. Regras de negócio (extraídas do legado — implemente fielmente)

### 6.1 Jornada e limite diário
- Jornada padrão = **8 horas/dia** (parametrizável via JORNADA_TRABALHO).
- Total do dia (CO + NC + EXTRA + ausências) deve **bater com as horas normais**. Acima de 8h,
  o excedente é **hora extra** (paga ou **BHE**). Limite de **24h/dia**.
- **Mínimo por lançamento de tarefa: 0,5h (30 minutos)**, em **múltiplos de 0,5h**. Validar no front e no backend; rejeitar valores < 0,5h ou fora do passo de 0,5h. Regra de negócio Triscal (não existia no legado).

### 6.2 Status do dia (`ST_TIMESHEET_DIA`)
`A` Aberto, `F` Fechado pelo funcionário, `P` aProvado, `R` Rejeitado. Fluxo `A -> F -> (P|R)`.
`LOG_PERIODO.LOPE_STT_LOG` reflete o status agregado da quinzena.

### 6.3 Aprovação
Cada funcionário tem aprovador (`FUNC_CD_FUNC_APROVADOR`); gestor não aprova o próprio log.
A aprovação pode ser feita na visão quinzenal, porém **cada dia é aprovado individualmente**
(cada dia `F` -> `P` com seu próprio cálculo de BHE).

### 6.4 Banco de Horas (BHE) — crítica (trigger TRG_TIMESHEET_DIA_BANCO_HE)
Ao aprovar um dia (status `P`):
1. Some HORAS_NR_NCEXTRA + HORAS_NR_COEXTRA do dia e subtraia `LOG_QN_AUSBHE`.
2. PJ (`E`): só extra **50%** (BHE50).
3. CLT (`F`): **100%** (BHE100) se domingo/feriado; senão **50%** (BHE50).
4. Gere movimento em MOVIMENTACAO_SALDO_BHE e atualize FUNC_NR_SALDO_BHE50/100.
5. Movimentos manuais (RH): `MOSA_IN_LANCAMENTO_MANUAL='S'`.
6. Só participa quem tem `IN_POSSUI_BANCO_HORAS='S'`. Implemente na **camada de aplicação**,
   com testes.

### 6.5 Tarefas externas
`IN_TAREFA_EXTERNA='S'`: horas externas, exibidas mas **read-only**. Interface de integração
genérica substitui o OTRS.

### 6.6 Feriados
`FERI_TP_FERIADO`: `NN` nacional anual, `NF` nacional fixo, `LN` local anual (por filial),
`LF` local fixo. Local depende de `FILIAL_CD_FILIAL` x `FUNCIONARIO.CD_EMPRESA`.
`FERI_NR_HORAS_A_TRABALHAR` e `FERI_STT_INATIVO`. Alimenta extras 100% e alertas.

### 6.7 Quinzena e abertura de log
Só lança em projeto/tarefa **alocado** e **ativo** dentro do período. Ao abrir nova quinzena,
**replica as tarefas do período anterior** (com_CriaLog).

### 6.8 % de andamento do projeto (novo)
Gestor de Projeto informa o % por projeto/fase/tarefa (PERC_CONCLUSAO_*). Histórico datado
(`andamento_projeto_hist`). Comparar % informado com % derivado das horas (realizado/previsto).

## 7. Funcionalidades novas (além de replicar o legado)

1. **Lançamento por dia único:** tela mobile com rascunho e auto-save.
2. **Alertas diários de não preenchimento** ao colaborador (respeita feriados/folgas).
3. **Alerta quinzenal ao gestor** sobre logs pendentes de aprovação.
4. **Alerta ao gestor sobre colaborador inadimplente** (dias em aberto/atrasados).
5. **Fechamento quinzenal de acompanhamento de projeto:** por projeto, horas executadas
   (quinzena e acumuladas), **% informado pelo Gestor de Projeto** vs **% derivado das horas**,
   **desvio**; o Gestor de Projeto confirma o % como parte do fechamento; snapshot histórico.
6. **Visões gerenciais (dashboards):** previsto vs realizado, evolução do %, horas por
   funcionário, a faturar, alocação, saldo de BHE, aderência de preenchimento.
7. **Exportação** de relatórios em Excel e PDF.
8. **Cronômetro (timer) em tempo real (novo, prioridade MVP):** start/stop por tarefa, além do
   lançamento manual; ao parar, o tempo vira um lançamento (editável). Reduz esquecimento e é o
   recurso mais usado nos SaaS de referência (Toggl/Clockify).
9. **Relatórios de utilização/capacidade (novo):** horas alocadas vs disponíveis por
   colaborador/equipe, com pivot e tendências, export PDF/CSV/Excel e link compartilhável.
10. **Faturamento/billing (novo, evolução de "horas a faturar"):** consolidar horas CO por
    cliente/projeto em relatório/fatura exportável. (Geração fiscal fora do escopo inicial.)

> **Fora do escopo inicial (fases futuras, não críticas para a Triscal):** gestão de despesas
> (expenses) e integração com calendário (Outlook/Google). Projete o modelo para acomodá-las
> depois, mas não as implemente agora.

## 7-A. Padrões de UI/UX (obrigatórios)

Baseados nas melhores práticas de SaaS de time tracking; objetivo: **minimizar o esforço de
lançamento** e aumentar a adesão.

- **Esforço/memória mínimos:** autocomplete de projeto/tarefa e **pré-preenchimento da quinzena
  com os lançamentos do período anterior**.
- **Dois modos de entrada:** grade quinzenal (dias x tarefas) e tela do dia único (mobile).
- **Time picker híbrido:** input numérico com autocomplete + seletor visual; aceitar 8, 8,5, 8:30.
- **Edição inline na grade** com **auto-save otimista** e indicador de "rascunho salvo".
- **Cor por projeto**, ruído visual mínimo, CTAs de alto contraste, **dark mode**.
- **Feedback de estado:** saldo de BHE no topo; status (A/F/P/R) com cores; validação inline
  8h/24h; erros próximos ao campo.
- **Padrões estruturais:** master-detail; skeleton loaders; empty states; busca rápida/command
  palette; navegação por teclado.
- **Acessibilidade WCAG AA** e mobile-first em todas as telas.
- **Design system** próprio (tokens) sobre MUI/Mantine. Detalhe em ARQUITETURA_TELAS_COMPONENTES.md.
- **Paleta da marca Triscal (obrigatória):** acento primário laranja `#CC4700` (hover `#9A3600`, vivo `#E2640F`); secundária azul `#004ECC`; neutros `#111111`/`#222222`/`#686868`/`#EFEFEF`/`#F5F5F5`; semânticas sucesso `#0F9D58`, alerta `#F4B400`, erro `#DB4437`. Botões primários e o cronômetro em laranja; status A/F/P/R = neutro/alerta/sucesso/erro. Ver tokens em ARQUITETURA_TELAS_COMPONENTES.md.

## 8. Telas / módulos a gerar

1. **Login** (redirect OIDC via Keycloak/Entra) + guarda de rotas por papel.
2. **Log da quinzena** (grade dias x tarefas, CO/NC/EXTRA, ausências, saldo BHE, externas
   read-only, validação 8h/24h, fechar dia/quinzena).
3. **Lançamento do dia** (mobile, auto-save, timer).
4. **Aprovação** (gestor: quinzenas pendentes, aprovar/rejeitar com observação).
5. **Acompanhamento de projeto / fechamento quinzenal** (% de andamento; horas executadas x
   previstas; desvios; histórico).
6. **Cadastros:** projetos, fases, tarefas, alocação, funcionários, clientes, feriados,
   movimentação manual de BHE.
7. **Dashboards e relatórios** (execução, evolução de %, horas por funcionário, a faturar,
   utilização, alocação, aderência, saldo de BHE).
8. **Configurações/parâmetros**, perfis e **central de notificações/alertas**.

Detalhamento da arquitetura de telas e componentes em **ARQUITETURA_TELAS_COMPONENTES.md**.

## 9. Modelo de dados (entidades principais — preserve a semântica do legado)

PostgreSQL (snake_case), preservando os campos e o mapeamento para as tabelas legadas:

- **funcionario** (TP_FUNCIONARIO E/F, CD_EMPRESA, FUNC_CD_FUNC_APROVADOR,
  IN_POSSUI_BANCO_HORAS, FUNC_NR_SALDO_BHE50/100, dados pessoais restritos).
- **empresa** (filial: origem do projeto e do funcionário). **clifor** (cliente).
- **projeto** (IN_INTERNO, CD_CLIFOR, CD_EMPRESA, ST_PROJETO, horas prev/real, ACEITA_HORAS_CO/NC).
- **projeto_fase** (PERC_CONCLUSAO_FASE). **projeto_tarefa** (IN_TAREFA_EXTERNA,
  PERC_CONCLUSAO_TAREFA). **projeto_tarefa_funcionario** (alocação, taxas,
  PERC_CONCLUSAO_TAREFA_FUNC).
- **timesheet_dia** (LOG_NR_HNORMAL, LOG_QN_AUSBHE, LOG_QN_AUSABN, ST_TIMESHEET_DIA).
- **timesheet_dia_tarefa** (HORAS_NR_NC/CO/NCEXTRA/COEXTRA, horários).
- **log_periodo** (PERI_CD_PERIODO, LOPE_STT_LOG). **feriado**, **movimentacao_saldo_bhe**,
  **jornada_trabalho**, **param_geral**.
- **andamento_projeto_hist** (novo): projeto/fase/tarefa, data, autor, perc_informado,
  perc_derivado_horas.
- **fechamento_quinzenal** (novo): período, projeto, horas_executadas/previstas, perc_execucao,
  desvio, data.
- **perfil/acesso** (RBAC) e **auditoria**.

## 10. Migração de dados

- Migrar todas as tabelas do schema Oracle `TIMESHEET` para PostgreSQL preservando histórico.
  Mapear tipos Oracle->Postgres; converter charset (ISO-8859-1 -> UTF-8); validar saldos de BHE.
- Reimplementar a lógica do trigger de BHE na **camada de aplicação**, com testes.

## 11. Entregáveis obrigatórios

1. **Classes de teste** — unitários (BHE, jornada, status, % de execução, fechamento quinzenal)
   e de integração de API (cobertura mínima recomendada de 80% no núcleo de negócio).
2. **Documentação técnica** — arquitetura (C4), ERD, módulos/camadas, OpenAPI/Swagger, guia de
   build local (Docker Compose) e deploy (Terraform/Kubernetes), configuração, segurança e plano
   de migração.
3. **Manual de utilização (usuário final)** em pt-BR, ilustrado: login (SSO), lançamento
   (quinzena e dia), BHE/horas extras, fechamento e aprovação, % de andamento e fechamento
   quinzenal de projeto, relatórios/dashboards e alertas. Em Markdown e PDF.

## 12. Qualidade e entrega

- CI/CD (GitHub Actions/GitLab CI) com build, testes, migrations e deploy em contêiner.
- Infra como código (Terraform); execução local via Docker Compose. Idioma padrão **pt-BR**.

Gere incrementalmente: domínio + migrations -> autenticação OIDC -> log da quinzena e dia ->
aprovação + BHE -> acompanhamento de projeto (% e fechamento) -> cadastros -> dashboards ->
alertas. Inclua testes, documentação técnica e manual do usuário a cada módulo concluído.
