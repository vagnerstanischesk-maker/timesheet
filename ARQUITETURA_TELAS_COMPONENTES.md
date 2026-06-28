# Arquitetura de Telas e Componentes — Frontend (React/PWA)

Documento de design da camada de apresentação do novo sistema de apontamento de horas da
Triscal. Complementa `PROMPT_GERACAO_NOVO_SISTEMA.md` (seções 7-A e 8) e segue os padrões de
UI pesquisados nos SaaS de referência (Toggl, Harvest, Clockify).

## 1. Princípios

- **Esforço mínimo de lançamento** (autocomplete, replicação da quinzena anterior, auto-save).
- **Mobile-first**; mesma base para desktop e celular. (PWA instalável removido por ora — ver PENDENCIAS.)
- **Sem perda de dados no lançamento:** **autosave** com debounce + **aviso ao sair** se houver alterações não salvas (ver seção 5.1). Rascunho local resiliente a queda de conexão.
- **Acessível** (WCAG AA) e **internacionalizável** (pt-BR padrão).
- **Componentes desacoplados** das regras de negócio (estas no backend; o front valida e exibe).

## 2. Stack de frontend

| Aspecto | Escolha (open-source) |
|---------|------------------------|
| Framework | React 18 + TypeScript + Vite |
| UI / Design System | MUI **ou** Mantine + tokens próprios (cor, tipografia, spacing) |
| Estado servidor | TanStack Query (cache, revalidação, optimistic updates) |
| Estado UI local | Zustand (ou Context) para sessão, filtros, rascunhos |
| Formulários | React Hook Form + Zod (validação espelhando o backend) |
| Roteamento | React Router (rotas protegidas por papel) |
| Autenticação | oidc-client-ts / react-oidc-context (Keycloak/Entra) — só identidade; papéis vêm de `/api/v1/me` |
| Gráficos | Recharts ou Chart.js |
| Datas | date-fns (locale pt-BR) |
| Testes | Vitest + React Testing Library + Playwright (E2E) |

## 3. Camadas do frontend

```
src/
  app/            # bootstrap, providers (Query, Auth, Theme, Router), layout raiz
  routes/         # definição de rotas + guards por papel
  features/       # um módulo por domínio (ver seção 5) — UI + hooks + api do módulo
    timesheet-quinzena/
    lancamento-dia/
    aprovacao/
    acompanhamento-projeto/
    cadastros/
    dashboards/
    notificacoes/
  shared/
    ui/           # design system (Button, Input, HoursField, StatusBadge, DataGrid...)
    api/          # cliente HTTP, interceptors (token, erros), tipos gerados do OpenAPI
    auth/         # contexto de sessão, papéis, hooks useAuth/useHasRole
    hooks/        # hooks utilitários (useDebounce, useOnlineStatus, useAutosave)
    lib/          # date, money, format, validação Zod compartilhada
  styles/         # tokens e tema (claro/escuro)
```

**Regra de dependência:** `features/*` podem usar `shared/*`, nunca o contrário; features não
importam umas das outras diretamente (comunicam-se via rotas/estado de servidor).

## 4. Navegação e papéis (RBAC no front)

Rotas protegidas; o menu se adapta ao papel do usuário. **Importante:** os papéis NÃO vêm do token — o Entra/Keycloak só autentica. O front obtém os papéis da própria aplicação (endpoint `/api/v1/me`, resolvido a partir das tabelas internas).

| Rota | Tela | Papéis |
|------|------|--------|
| `/login` | Login (redirect OIDC) | público |
| `/dia` | Lançamento do dia | Colaborador |
| `/quinzena` | Log da quinzena | Colaborador |
| `/aprovacao` | Aprovação de logs | Gestor/Aprovador |
| `/projetos/acompanhamento` | % de andamento + fechamento quinzenal | Gestor de Projeto |
| `/dashboards` | Dashboards e relatórios | Gestor, Gestor de Projeto, Diretoria |
| `/cadastros/*` | Projetos, fases, tarefas, alocação, funcionários, clientes, feriados, BHE | Admin/RH |
| `/config` | Parâmetros, perfis | Admin |
| `/notificacoes` | Central de alertas | todos |

> Autorização real é sempre no backend (por papel **e por dado**), com papéis/hierarquia
> mantidos em tabelas internas. O Entra ID fornece só a identidade. O front apenas
> oculta/mostra e antecipa validações.

## 5. Telas e árvore de componentes

### 5.1 Lançamento do dia (mobile, prioridade)
Objetivo: lançar as horas do dia corrente em segundos.

```
<LancamentoDiaPage>
  <DateSwitcher/>                     // dia anterior/próximo, hoje
  <BHEBalanceBar/>                    // saldo BHE50/100 (somente leitura)
  <TimerWidget/>                      // start/stop por tarefa -> vira lançamento (novo, MVP)
  <TaskQuickAdd/>                     // autocomplete de projeto/tarefa alocada
  <DayEntryList>
    <DayEntryRow x N>                 // tarefa + HoursField (CO/NC/EXTRA) + obs
      <ProjectColorDot/> <HoursField/> <ExtraToggle/> <NotesPopover/>
    </DayEntryRow>
    <ExternalTaskRow/>               // IN_TAREFA_EXTERNA: read-only
  </DayEntryList>
  <DayTotalsFooter/>                  // total do dia, validação 8h/24h, indicador auto-save
  <SubmitDayBar/>                     // fechar dia (A -> F)
</LancamentoDiaPage>
```
Padrões: **autosave** (debounce ~800ms a cada edição → `PUT` do rascunho do dia, status A),
time picker híbrido, cor por projeto. **Aviso ao sair com alterações não salvas:** se houver
edições pendentes (autosave em andamento/falho), bloquear navegação e perguntar *“Salvar antes
de sair?”* (Salvar / Sair sem salvar / Cancelar) — cobre tanto trocar de rota (React Router
`useBlocker`) quanto fechar a aba (`beforeunload`). Indicador de estado: *salvando… / salvo /
falha ao salvar (tentar de novo)*.

### 5.2 Log da quinzena (grade dias × tarefas)
```
<QuinzenaPage>
  <PeriodSelector/>                   // escolhe a quinzena (cortes parametrizáveis)
  <BHEBalanceBar/>
  <TimesheetGrid>                     // linhas = tarefas, colunas = dias
    <GridHeader/> (dias, feriados/finais de semana destacados)
    <GridRow x tarefas>
      <TaskCell/> <DayHoursCell editável x dias/> ...
    </GridRow>
    <AusenciaRows/>                    // AUSBHE / AUSABN
    <ExternalTaskRows read-only/>
    <GridTotalsRow/>                   // total por dia (valida 8h) e por tarefa
  </TimesheetGrid>
  <CopyFromPreviousButton/>           // replica tarefas do período anterior
  <SubmitPeriodBar/>                  // fechar quinzena
</QuinzenaPage>
```

### 5.3 Aprovação (gestor)
```
<AprovacaoPage>
  <PendingPeriodsList/>               // subordinados com dias 'F'
  <ApprovalDetailDrawer>
    <EmployeePeriodSummary/> <DayBreakdown/>
    <ApproveRejectActions/>           // aprovar (F->P) / rejeitar (F->R) + observação
  </ApprovalDetailDrawer>
</AprovacaoPage>
```
Regra de UI: gestor nunca vê o próprio log para aprovar.

### 5.4 Acompanhamento de projeto / fechamento quinzenal (gestor de projeto)
```
<AcompanhamentoProjetoPage>
  <ProjectPhaseTaskTree/>             // navega projeto -> fase -> tarefa
  <ProgressEditor/>                   // informa/confirma % de andamento (PERC_CONCLUSAO_*)
  <ExecutionPanel>
    <HoursExecutedVsPlanned/>         // horas executadas (quinzena/acum.) x previstas
    <PercentComparison/>              // % informado x % derivado das horas + desvio
    <ProgressHistoryChart/>           // evolução entre quinzenas
  </ExecutionPanel>
  <ConfirmClosureBar/>               // confirma % como parte do fechamento (snapshot)
</AcompanhamentoProjetoPage>
```

### 5.5 Dashboards e relatórios
```
<DashboardsPage>
  <FilterBar/>                        // período, projeto, cliente, equipe, status
  <KpiCards/>                         // horas, % execução, aderência, saldo BHE
  <Charts/>                           // previsto x realizado, evolução %, utilização
  <UtilizationReport/>               // alocado vs disponível (novo)
  <BillingReport/>                    // horas a faturar por cliente (novo)
  <ExportMenu/>                       // PDF / CSV / Excel / link compartilhável
</DashboardsPage>
```

### 5.6 Cadastros (Admin/RH)
Padrão CRUD master-detail reutilizável (`<CrudListPage>` + `<CrudFormDrawer>`) para projetos,
fases, tarefas, alocação, funcionários, clientes, feriados e movimentação manual de BHE.

## 6. Design System (componentes compartilhados)

Componentes-chave em `shared/ui`, tematizados (claro/escuro) por tokens:

- `HoursField` — input numérico + picker, aceita `8`, `8,5`, `8:30`; valida limites.
- `StatusBadge` — A/F/P/R com cor e rótulo.
- `BHEBalanceBar` — saldos 50/100.
- `ProjectAutocomplete` — busca por nome, mostra só tarefas alocadas e ativas.
- `TimerWidget` — cronômetro start/stop.
- `DataGrid` — grade editável com totais e validação.
- `ConfirmDialog`, `NotesPopover`, `EmptyState`, `SkeletonLoader`, `ExportMenu`.
- `useAutosave` (debounce + estado salvando/salvo/erro) e `useUnsavedChangesPrompt`
  (`useBlocker` + `beforeunload`) — base do “salvar ao sair”.

## 7. Estado, dados e sincronização

- **Servidor:** TanStack Query por recurso (quinzena, dia, projetos, aprovações...), com
  `optimistic update` no auto-save e revalidação ao focar a janela.
- **Rascunho/offline:** `useAutosave` grava no IndexedDB e numa fila; `useOnlineStatus`
  dispara o flush ao reconectar; conflitos resolvidos por timestamp (último a fechar vence,
  com aviso).
- **Tipos:** gerar tipos TypeScript a partir do **OpenAPI** do backend (fonte única da verdade).
- **Validação:** esquemas Zod no front espelham as regras do backend (jornada, 8h/24h), mas a
  decisão final (BHE, status) é sempre do servidor.

## 8. Trade-offs e decisões a revisitar

| Decisão | Trade-off | Revisitar quando |
|---------|-----------|------------------|
| MUI/Mantine vs design system do zero | Velocidade vs identidade visual | Se a marca exigir UI muito customizada |
| Grade editável própria vs lib de datagrid | Controle vs esforço | Se a performance da grade quinzenal degradar |
| Offline via IndexedDB | Complexidade de sincronização | Se conflitos forem frequentes na prática |
| Zustand vs Redux | Simplicidade vs ferramentas/escala | Se o estado global crescer muito |

## 9. Acessibilidade e performance

- Navegação por teclado na grade; foco visível; labels ARIA; contraste AA.
- Code splitting por rota; lazy loading de dashboards; memoização da grade.
- Lighthouse/PWA: instalável, offline básico. Observabilidade simples (Actuator + logs); métricas avançadas são opcionais nesta escala (~100 usuários).

## 10. Design tokens — paleta da marca Triscal

Cores oficiais extraídas do site da Triscal (tema 2023). O **laranja é o acento primário**;
neutros em cinza/quase-preto; azul como secundário. Use estes tokens no design system
(MUI/Mantine theme) — não hardcode hexadecimais nos componentes.

| Token | Hex | Uso |
|-------|-----|-----|
| brand.orange (primária) | `#CC4700` | CTAs primários, estado ativo/seleção, marca |
| brand.orangeDark (hover) | `#9A3600` | hover/pressed do primário, texto sobre tint claro |
| brand.orangeBright | `#E2640F` | realces, bordas de destaque |
| brand.blue (secundária) | `#004ECC` | links/ações secundárias, informações |
| brand.indigo | `#365899` | apoio à secundária |
| neutral.heading | `#111111` | títulos |
| neutral.text | `#222222` | texto padrão |
| neutral.grey | `#686868` | texto secundário |
| neutral.lightGrey | `#EFEFEF` | superfícies secundárias |
| neutral.lighterGrey | `#F5F5F5` | fundo da página |
| neutral.border | `#CCCCCC` | bordas/sombras |
| bg.dark / darker | `#111111` / `#0A0A0A` | rodapé, modo escuro |
| semantic.success | `#0F9D58` | sucesso / status Aprovado (P) |
| semantic.warning | `#F4B400` | atenção / status Fechado (F) |
| semantic.danger | `#DB4437` | erro / status Rejeitado (R) |

Mapeamento para estados do timesheet: **A** (aberto) = neutro/cinza; **F** (fechado) =
warning; **P** (aprovado) = success; **R** (rejeitado) = danger. Botões primários e o
cronômetro usam o laranja da marca. As "cores por projeto" são uma escala categórica à parte
(azul/teal/cinza/âmbar), independente da paleta da marca.

> Implementação aplicada nos mockups em `mockups/` (variáveis CSS `--brand-*` e
> `--color-*`), com suporte automático a modo claro e escuro.
