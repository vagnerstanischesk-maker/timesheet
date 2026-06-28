# Backlog / pendências — Triscal Apontamento de Horas

Status: núcleo funcional com **CI verde** (build + testes do backend e frontend). Abaixo, o que
falta para completude de produção, em ordem de prioridade.

## P1 — Destravam o uso do dia a dia
- [x] **Alocação**: cadastro + endpoint de "tarefas alocadas" + validação no lançamento. ✅
- [x] **Cadastro de fases e tarefas** (inclui flag de tarefa externa). ✅
- [ ] **Replicação das tarefas do período anterior** ao abrir nova quinzena (com_CriaLog).
- [ ] **Validações de jornada**: total do dia coerente com as horas normais; classificação do
      excedente (>8h) como hora extra/BHE; bloqueio efetivo de edição de tarefa externa no back.

## P2 — Cadastros administrativos
- [x] **Funcionários** — cadastro completo (documentos com **CPF validado**, dados eleitorais, filiação, endereço, bancários, contrato, BHE 50/100, benefícios; filial RJ/SP; **aprovador por nome**; perfil Comercial). ✅
- [x] **Clientes (CLIFOR)** — CRUD completo (contato financeiro, site, endereço; filial RJ/SP; pesquisa; campos obrigatórios). ✅
- [x] **Feriados** — CRUD (NN/NF/LN/LF, por filial). ✅
- [x] **Movimentação manual de BHE** (RH) — lançamento. ✅ (consulta de histórico: pendente)
- [x] **Filiais** — carga inicial RJ/SP (lista de valores nos cadastros). ✅ (CRUD próprio: backlog)
- [x] **Verticais** (linha de negócio) — cadastro gerenciável (CRUD). ✅
- [x] **Projetos** — datas início/fim, custo previsto, horas/custo realizados, faturamento, gerentes, vertical, tipo de contrato, comissão, filial, observação; **pesquisa por cliente**; código automático. ✅

## P3 — Funcionalidades de produtividade/gestão
- [ ] **PWA instalável** (readicionar `vite-plugin-pwa` com setup atualizado — removido para sanar deprecações de dependências transitivas).
- [ ] **Cronômetro (timer)** em tempo real no lançamento.
- [ ] **Relatórios de utilização/capacidade** e **billing** (horas a faturar por cliente).
- [ ] **Exportação** de relatórios (PDF/Excel).
- [ ] **Central de notificações** e tela de **configurações/parâmetros**.

## P4 — Integração e dados
- [ ] **Integração que substitui o OTRS** (horas externas read-only) — interface genérica.
- [x] **Migração Oracle→PostgreSQL**: kit pronto em `migration/` (ETL + validação + plano de cutover). Falta executar com o dump real. ✅

## P5 — Qualidade e entrega
- [ ] **Aviso ao trocar de rota** no lançamento (migrar para data router do React Router; hoje só `beforeunload` ao fechar/recarregar a aba).
- [ ] **Testes de integração** (controllers/serviços com banco — Testcontainers).
- [x] **Documentação técnica** e **manual do usuário** (Markdown + .docx). ✅ (OpenAPI/Swagger ainda pendente)
- [ ] **Login com página de marca** (hoje é redirect OIDC direto).

## Concluído (referência)
SSO (Entra via Keycloak) + RBAC interno; lançamento do dia (autosave + aviso ao sair);
grade da quinzena (fechar); aprovação por dia/quinzena com **BHE** (testado); acompanhamento
de projeto (% + fechamento); dashboards (KPIs + previsto×realizado); cadastro de projetos;
alertas (diário, quinzenal, inadimplente); CI (GitHub Actions) verde; Docker + guia de setup; cadastros ampliados (cliente, projeto, funcionário completo, verticais) com código sequencial automático.
