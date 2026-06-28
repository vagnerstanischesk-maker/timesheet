---
name: triscal-apontamento-horas
description: >
  Regras de negócio do sistema de apontamento de horas (timesheet) da Triscal. Use sempre que
  a tarefa envolver apontamento/lançamento de horas, banco de horas (BHE), horas extras,
  jornada de 8h, status do dia/quinzena, aprovação de log, gestor/aprovador, projetos em fases
  e tarefas, alocação de funcionário, tarefas externas (OTRS), feriados, fechamento quinzenal,
  % de andamento de projeto, ou ao gerar/revisar código, modelo de dados, testes ou
  documentação do novo sistema (Java Spring Boot + React + PostgreSQL + Keycloak/Entra ID).
  Gatilhos: timesheet, apontamento, BHE, banco de horas, hora extra, quinzena, aprovação de
  log, TIMESHEET_DIA, Triscal.
license: Proprietary - uso interno Triscal
---

# Regras de negócio — Apontamento de Horas (Triscal)

Esta skill encapsula as regras de negócio extraídas do sistema legado (JSP/Java + Oracle,
schema `TIMESHEET`) e as decisões do novo sistema. Aplique-a ao implementar, revisar ou
documentar qualquer funcionalidade do timesheet. Em caso de dúvida entre o legado e esta
skill, **esta skill prevalece** (incorpora as decisões mais recentes).

## 1. Conceitos e atores

- **Funcionário/Colaborador**: lança horas. `TP_FUNCIONARIO` = `E` (Pessoa Jurídica/PJ) ou
  `F` (CLT). O tipo afeta as regras de hora extra/BHE.
- **Gestor/Aprovador**: aprova/rejeita os logs dos seus subordinados. Cada funcionário tem um
  aprovador em `FUNC_CD_FUNC_APROVADOR`. **Ninguém aprova o próprio log.**
- **Gestor de Projeto**: informa/confirma o **% de andamento** de projeto/fase/tarefa e
  participa do fechamento quinzenal.
- **Admin/RH**: cadastros, feriados, movimentação manual de BHE, dados sensíveis.
- **Projeto** → **Fase** → **Tarefa**: a **tarefa** é o menor nível de lançamento de horas.
  Projeto é interno (`IN_INTERNO='S'`, cliente = Triscal) ou de cliente (associado a `CLIFOR`).

## 2. Jornada, horas e limites

- Jornada padrão = **8 horas/dia** (parametrizável por funcionário em `JORNADA_TRABALHO`).
- O total lançado no dia (CO + NC + EXTRA + ausências) deve **bater com as horas normais** do
  dia. Valide essa consistência.
- Acima de 8h, o excedente é **hora extra** — o colaborador escolhe **hora extra paga** ou
  **BHE** (banco de horas para compensação futura).
- **Limite máximo de 24h por dia** (validação de sanidade).
- **Mínimo por lançamento: 0,5h (30 minutos)**; lançamentos sempre em **múltiplos de 0,5h** (0,5; 1,0; 1,5...). Não aceitar valores menores que 0,5h nem frações fora do passo de 0,5h. (Regra de negócio Triscal; não existia no legado.)
- Classificação das horas por tarefa:
  - `HORAS_NR_CO` — normais **com cobrança** (billable)
  - `HORAS_NR_NC` — normais **sem cobrança** (não-billable)
  - `HORAS_NR_COEXTRA` — extras com cobrança
  - `HORAS_NR_NCEXTRA` — extras sem cobrança
- Ausências no dia: `LOG_QN_AUSBHE` (abatida do banco de horas) e `LOG_QN_AUSABN` (abonada).

## 3. Status do dia e fluxo de aprovação

`ST_TIMESHEET_DIA` (1 caractere):

| Código | Status | Significado |
|--------|--------|-------------|
| `A` | Aberto | Em edição pelo colaborador |
| `F` | Fechado pelo funcionário | Enviado, pendente de aprovação |
| `P` | aProvado | Aprovado pelo gestor |
| `R` | Rejeitado/Reprovado | Volta ao colaborador para correção |

- Fluxo: **`A -> F -> (P | R)`**. Se `R`, retorna para correção e novo envio.
- A aprovação é **por quinzena**: ao aprovar, todos os dias `F` do período passam a `P`.
- `LOG_PERIODO.LOPE_STT_LOG` reflete o status agregado da quinzena.

## 4. Banco de Horas (BHE) — REGRA CRÍTICA

Dispara ao **aprovar** um dia (status muda para `P`). No legado estava no trigger
`TRG_TIMESHEET_DIA_BANCO_HE`; no novo sistema **deve ser implementada na camada de aplicação,
com testes**. Algoritmo:

1. Some as horas extras do dia: `HORAS_NR_NCEXTRA + HORAS_NR_COEXTRA` (todas as tarefas do dia)
   e subtraia a ausência de BHE do dia (`LOG_QN_AUSBHE`).
2. **Funcionário PJ (`TP_FUNCIONARIO = 'E'`)**: existe apenas **extra 50%** -> todo o excedente
   vai para `BHE50`. Não há 100%.
3. **Funcionário CLT (`TP_FUNCIONARIO = 'F'`)**:
   - Se o dia for **domingo OU feriado** -> excedente é **100%** (`BHE100`).
   - Caso contrário -> **50%** (`BHE50`).
4. Gere um movimento em `MOVIMENTACAO_SALDO_BHE` e atualize `FUNC_NR_SALDO_BHE50` /
   `FUNC_NR_SALDO_BHE100` do funcionário.
5. Movimentos manuais (RH) são permitidos: `MOSA_IN_LANCAMENTO_MANUAL = 'S'`.
6. **Apenas funcionários com `IN_POSSUI_BANCO_HORAS = 'S'` participam do banco de horas.**

Casos de teste obrigatórios: PJ em dia útil; CLT em dia útil (50%); CLT em domingo (100%);
CLT em feriado (100%); dia com ausência BHE reduzindo o excedente; funcionário sem banco de
horas (não gera movimento).

## 5. Tarefas externas (integração)

- Tarefa com `IN_TAREFA_EXTERNA = 'S'` tem horas vindas de sistema externo (legado: OTRS).
- No log, essas horas são **exibidas, mas somente leitura** — o colaborador não pode editá-las.
- No novo sistema, use uma **interface de integração genérica** (substitui o OTRS) que recebe
  as horas externas e as marca como read-only.

## 6. Feriados

`FERIADO.FERI_TP_FERIADO`:

| Tipo | Significado |
|------|-------------|
| `NN` | Nacional anual (por ano) |
| `NF` | Nacional fixo (recorrente) |
| `LN` | Local anual (depende da filial do funcionário) |
| `LF` | Local fixo (por filial) |

- Feriado local depende da filial do funcionário (`FILIAL_CD_FILIAL` x `FUNCIONARIO.CD_EMPRESA`).
- `FERI_NR_HORAS_A_TRABALHAR` define horas a trabalhar; `FERI_STT_INATIVO` marca inativo.
- O calendário alimenta o cálculo de extras a 100% (regra 4.3) e os alertas de não preenchimento
  (não cobrar log em feriado integral).

## 7. Quinzena, alocação e abertura de log

- O mês tem **dois períodos de fechamento (quinzenas)**. No legado: **11-25** e **26-10**.
  Torne os cortes **parametrizáveis**.
- Só é possível lançar em projeto/tarefa onde o colaborador está **alocado**
  (`PROJETO_TAREFA_FUNCIONARIO` / `ALOCACAOPROJ`), que esteja **ativo** e dentro do período
  (data fim real do projeto >= data do lançamento).
- Ao abrir o log de uma nova quinzena, **replique as tarefas/projetos do período anterior** do
  funcionário (comportamento de `com_CriaLog`).

## 8. % de andamento e fechamento quinzenal de projeto (novo)

- O **Gestor de Projeto** informa o **% de andamento** por projeto/fase/tarefa
  (`PERC_CONCLUSAO_FASE`, `PERC_CONCLUSAO_TAREFA`, `PERC_CONCLUSAO_TAREFA_FUNC`).
- Mantenha **histórico datado** das atualizações (`andamento_projeto_hist`: data, autor,
  perc_informado, perc_derivado_horas).
- **Fechamento quinzenal**: a cada quinzena, gere um relatório por projeto com:
  - horas executadas na quinzena (e acumuladas) por projeto/fase/tarefa e por colaborador;
  - **% informado pelo Gestor de Projeto** vs **% derivado das horas** (realizado / previsto);
  - **desvio** entre previsto e realizado (horas e %);
  - exigência de o Gestor de Projeto **confirmar o %** como parte do fechamento;
  - snapshot histórico (`fechamento_quinzenal`) para comparar a evolução entre quinzenas.

## 9. Alertas

- **Diário** ao colaborador que não preencheu o log do dia (respeitando feriados/folgas).
- **Quinzenal** ao gestor sobre logs pendentes de aprovação.
- Ao gestor sobre **colaborador inadimplente** (dias em aberto/atrasados).

## 10. Segurança e dados (sempre aplicar)

- Autenticação via **OIDC** (Keycloak federando Entra ID) **apenas para identidade** — o token
  não traz papéis nem hierarquia. **Permissionamento e hierarquia são 100% internos à aplicação**
  (tabelas `perfil`, `funcionario_perfil`, `func_cd_func_aprovador`); casa-se o sub/e-mail do
  token com `funcionario.cdg_usur`. Autorização **por papel e por dado**
  (colaborador só vê/edita os próprios lançamentos; gestor só aprova subordinados).
- Auditoria: registre autor (`USUR_CD_USUR`) e timestamp (`DT_ULT_ALT`) em toda alteração;
  log de auditoria para aprovações/rejeições e ajustes de BHE.
- **LGPD**: dados pessoais do funcionário (CPF, RG, dados bancários) com acesso restrito a
  RH/Admin.
- Queries parametrizadas; validação server-side; nunca lógica de negócio só no cliente.

## 11. Tabelas-chave (referência)

`TIMESHEET_DIA`, `TIMESHEET_DIA_TAREFA`, `FUNCIONARIO`, `PROJETO`, `PROJETO_FASE`,
`PROJETO_TAREFA`, `PROJETO_TAREFA_FUNCIONARIO`, `ALOCACAOPROJ`, `CLIFOR`, `EMPRESA`,
`LOG_PERIODO`, `FERIADO`, `MOVIMENTACAO_SALDO_BHE`, `JORNADA_TRABALHO`, `PARAM_GERAL`, e as
novas `andamento_projeto_hist` e `fechamento_quinzenal`.

> `EMPRESA` tem dois relacionamentos: `PROJETO.CD_EMPRESA` = filial de origem do projeto;
> `FUNCIONARIO.CD_EMPRESA` = filial do funcionário (define feriados locais aplicáveis).

---

Consulte também, na raiz do projeto, `PROMPT_GERACAO_NOVO_SISTEMA.md` (especificação de
geração) e `Analise_Sistema_Legado_e_Recomendacoes.docx` (análise e arquitetura).
