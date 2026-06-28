# Manual do usuário — Triscal Apontamento de Horas

Guia de uso do sistema de apontamento de horas da Triscal. Organizado por perfil. Linguagem
simples; se algo não aparecer no seu menu, é porque depende de um perfil que você não possui.

## 1. Acesso (todos)

1. Abra o sistema no navegador (ou instale como **app**: o navegador oferece "Instalar"
   — funciona no celular e no computador).
2. Clique em **Entrar**. Você será levado ao login da **Microsoft** (conta corporativa Triscal).
3. Após autenticar, o sistema identifica você pelo **e-mail** e carrega seus menus conforme seu
   perfil. Se aparecer "usuário sem cadastro de funcionário", procure o RH.

Seu nome aparece no canto superior direito. O menu muda conforme seus papéis. O login é por SSO
(Microsoft Entra ID), mas **os papéis são internos do sistema** (definidos no cadastro de
Funcionários) — o que você vê no menu depende deles.

### Quem acessa o quê

| Opção de menu | Perfis com acesso |
|---|---|
| Meu dia, Quinzena (lançamento) | Todos |
| Aprovações | Gestor, Admin/RH, Diretoria |
| Acompanhamento de projetos | Gestor de Projeto, Admin/RH, Diretoria |
| Dashboards | Gestor, Gestor de Projeto, Admin/RH, Diretoria |
| Relatórios | Gestor, Gestor de Projeto, Admin/RH, Diretoria |
| Cadastros (Projetos, Estrutura, Funcionários, Clientes, Verticais, Feriados, BHE) | Admin/RH, Diretoria |

Em resumo: o **Colaborador** só lança horas; o **Gestor (aprovador)** lança, aprova e vê
relatórios/dashboards; o **Gestor de Projeto** lança, acompanha projetos e vê relatórios/dashboards;
**Admin/RH** e **Diretoria** acessam tudo.

## 2. Colaborador — lançar horas

Há duas formas de lançar: **Meu dia** (rápido, um dia por vez) e **Quinzena** (a grade do período
inteiro). As duas usam as mesmas regras e se complementam — use a que for mais confortável.

### 2.1 Meu dia
Menu **Meu dia**. Em vez de listar tudo o que está alocado a você, **você escolhe** o que vai
apontar: clique em **adicionar tarefa**, selecione **projeto → fase → tarefa** e lance as horas.
Isso evita poluir a tela com tarefas que normalmente não recebem horas (férias, treinamento etc.),
que só aparecem quando você as escolhe. A lista de escolha mostra **apenas as tarefas em que você
está alocado e que estão dentro da validade** (entre a data de início e a data de fim da tarefa)
para o dia que está lançando.

- Você pode lançar em **vários projetos/fases/tarefas no mesmo dia** — adicione quantas linhas
  precisar.
- Para cada tarefa, informe as horas por tipo: **CO** (com cobrança/faturável), **NC** (sem
  cobrança) e **CO extra / NC extra** (horas acima da jornada, que podem virar banco de horas — BHE).
- **CO e NC na mesma tarefa, no mesmo dia, são permitidos** (parte faturável + parte não faturável).
- Cada linha mostra o **total da tarefa**; ao final há o **total do dia**.

**Ausências** (no mesmo dia, em um bloco próprio — **não** ligadas a projeto):
- **Ausência BHE** — ausência **debitada do banco de horas**;
- **Ausência ABN** — ausência **abonada**.

**Totais e horas esperadas:** o **total do dia** = horas de projeto **+** ausências. Ao lado, o
sistema mostra as **horas normais esperadas** para aquele dia: **8h** em dia útil, as **horas a
trabalhar cadastradas no feriado** quando for feriado, e **0** no fim de semana.

Regras importantes:
- Lance em **múltiplos de 0,5h** (meia em meia hora). O mínimo é **0,5h**.
- O sistema **salva sozinho** enquanto você digita (indicador "salvando… / salvo"). Se você
  tentar sair com algo não salvo, ele **pergunta antes**.
- Tarefas marcadas com 🔒 são **externas** (vêm de outro sistema) e ficam **somente leitura**.

### 2.2 Quinzena
Menu **Quinzena**. É a **grade do período inteiro**, com o **seu nome** e o período no topo. Os
períodos são **1 a 15** e **16 ao último dia do mês**. Funciona como o Meu dia, mas para **todos os
dias de uma vez** — ideal para preencher ou revisar a quinzena fechada.

- Botões **‹ Anterior** e **Próxima ›** permitem navegar entre quinzenas (corrigir/fechar um período
  passado, conferir o próximo).
- Ao **iniciar um novo período ainda sem lançamentos**, a grade já vem **pré-preenchida com as
  fases/tarefas do período anterior** (linhas zeradas), para você só ajustar as horas — aparecem só
  as que continuam alocadas a você e dentro da validade.
- Uma **linha por projeto/fase/tarefa** (coluna larga para leitura), com **uma sub-linha por tipo
  de hora** lançado (NC, CO, NC extra, CO extra) — você liga só os tipos que usa.
- Colunas = dias do período; a edição é **direta na célula** (mesmo autosave do Meu dia).
- Linhas de **Ausência BHE** e **Ausência ABN** por dia, e a linha de **Horas esperadas** do dia —
  todas com **total** no fim.
- A **consolidação/status** de cada dia também aparece como linha da grade, com o status abreviado
  embaixo de cada coluna: **A** (Aberto), **F** (Fechado), **P** (Aprovado), **R** (Rejeitado) — há
  uma **legenda** logo acima da grade.
- Mostra também o seu **saldo de BHE**.

Status do dia:
- **Aberto** — em edição;
- **Fechado** — enviado para aprovação;
- **Aprovado** — aprovado pelo gestor;
- **Rejeitado** — devolvido para correção (corrija e feche de novo).

Quando terminar o período, clique em **Fechar quinzena** (envia todos os dias em aberto para
aprovação). Você também pode fechar um único dia pela tela Meu dia.

## 3. Gestor — aprovar logs

Menu **Aprovações** (aparece se você é aprovador de alguém). Os logs vêm **agrupados por
colaborador e quinzena**, e cada um é exibido na **mesma grade da Quinzena** (em modo leitura),
para você avaliar **o apontamento dia a dia, por projeto/fase/tarefa** — não só o total do dia.

A aprovação é **dia a dia**:
- **Aprovar** / **Rejeitar** por dia — ao rejeitar, é **obrigatório informar a justificativa**.
  Você pode aprovar um dia e rejeitar outro no mesmo log.
- **Aprovar quinzena** — botão que aprova de uma vez todos os dias pendentes daquele período (cada
  dia continua sendo aprovado individualmente, e o banco de horas é calculado por dia).

Cada card mostra um resumo: **Total da quinzena**, **Total do mês** (na 1ª quinzena é igual à
quinzena; na 2ª soma as duas quinzenas do mês) e o **saldo de BHE 50% e 100%** do colaborador. Há
também os botões **‹ / ›** para navegar entre as quinzenas daquele colaborador (e voltar à pendência).

Você só aprova **colaboradores cujo aprovador cadastrado é você** e **não vê o seu próprio log**
para aprovar — isso é feito pelo seu aprovador.

## 4. Gestor de Projeto — acompanhamento

Menu **Acompanhamento**. Selecione o **cliente** e, em seguida, o **projeto** daquele cliente
(dois menus suspensos — não é preciso decorar códigos de projeto). Veja:
- **horas na quinzena** e **acumuladas vs previstas**;
- **% por horas** (realizado ÷ previsto) e o **% informado por você**;
- o **desvio** entre os dois.

Ações:
- **Registrar %** — informe o % de andamento do projeto (mantém histórico datado).
- **Confirmar % e fechar quinzena** — gera o fechamento do período (snapshot de horas e %).

## 5. Gestão — dashboards e relatórios

Disponíveis para gestores, gestor de projeto, diretoria e Admin/RH. Os menus **Dashboards** e
**Relatórios** abrem **índices** que listam tudo o que está disponível (novos itens são acrescentados
à medida que são criados) — clique no item para abrir.

### 5.1 Dashboards
**Visão geral** — KPIs do mês (horas, % faturável, saldo de BHE da equipe) e **previsto × realizado
por projeto**.

### 5.2 Relatórios
- **Logs pendentes (períodos passados)** — colaboradores com quinzenas **passadas** ainda não
  fechadas (status **Aberto** ou **Rejeitado**). Exclui o que está **Fechado** e **Aprovado** e
  **ignora a quinzena atual** (que ainda está sendo preenchida). Agrupado por colaborador e ordenado
  por colaborador e período. O objetivo é mostrar **só o que está pendente no passado**.
- **Acompanhamento de projetos** — visão consolidada de **todos os projetos**, **somando todos os
  períodos de log**. Por projeto: **cliente**, **horas vendidas** (previstas), **horas executadas**,
  **% executado por horas** (executadas ÷ vendidas), **% informado pelo gestor** (último registrado),
  **diferença %** e **diferença em horas**. A diferença % = **% por horas − % informado**; a
  diferença em horas = **diferença % × horas vendidas** — representa o **atraso/estouro** do projeto
  (ex.: 70% executado vs 62% informado → 8% → 8% × 1.200h = **96h**). Valores de atraso/estouro
  aparecem em **vermelho**; há **total** no rodapé.
- **Movimentação de BHE** — créditos por aprovação de horas extras e lançamentos manuais de banco
  de horas, em um **período** escolhido (filtro de início/fim, padrão = mês corrente), **agrupados
  por colaborador**, com data, **BHE 50%**, **BHE 100%**, origem (Aprovação/Manual) e usuário;
  subtotal por colaborador e **total geral**.

Todos os relatórios têm botão **Exportar CSV**.

## 6. Admin/RH e Diretoria — cadastros

Em todos os cadastros o **código é gerado automaticamente** pelo sistema (sequencial); ao editar,
ele aparece somente para leitura. Menus de cadastro (perfis **Admin/RH** e **Diretoria**):

- **Projetos** — criar/editar projetos: cliente, vertical (linha de negócio), tipo de contrato,
  tipo de cobrança/faturamento, gerente técnico e comercial, filial (RJ/SP), **datas de início e
  fim**, horas e custo previstos, comissão e observação. Mostra **horas realizadas** e **custo
  realizado** (somatórios, somente leitura). Há **pesquisa** por descrição/código e **por cliente**.
- **Estrutura** — montar **fases** e **tarefas** de cada projeto e fazer a **alocação** de
  funcionários (quem pode lançar em cada tarefa). Marque a tarefa como **externa** quando as horas
  vierem de integração.
- **Funcionários** — cadastro completo: identificação (nome, tipo PJ/CLT, **filial RJ/SP**,
  e-mail de vínculo, **aprovador escolhido pelo nome**, perfis incl. **Comercial**), documentos
  (**CPF com validação da Receita Federal**, RG e órgão emissor, título/zona/seção eleitoral,
  PIS, carteira e série, nacionalidade), filiação e dados pessoais (nome do pai e da mãe, estado
  civil, datas de nascimento/admissão/rescisão), endereço e contatos, dados bancários e último
  pagamento, contrato, saldos de **BHE 50%/100%** e benefícios (banco de horas, vale-transporte,
  plano de saúde, requer aprovação administrativa). A listagem tem **pesquisa** por nome, CPF,
  código ou e-mail.
- **Clientes** — razão social, fantasia, CNPJ, **filial (RJ/SP)**, contato financeiro (nome e
  e-mail), site e endereço completo. Todos os campos são obrigatórios; há **pesquisa** de clientes.
- **Verticais** — cadastro das **linhas de negócio** da empresa (usadas no projeto).
- **Feriados** — calendário (nacional/local, fixo/anual; locais por filial). Usados no cálculo de
  horas extras a 100% e nos alertas.
- **BHE** — **movimentação manual** do banco de horas (creditar/debitar saldo de um funcionário).

## 7. Alertas automáticos

O sistema envia e-mails automáticos:
- **Diário** (dias úteis): lembra o colaborador que não preencheu o log do dia.
- **Quinzenal**: lembra o gestor de logs pendentes de aprovação.
- **Semanal**: avisa o gestor sobre subordinados com dias em atraso.

## 8. Dúvidas frequentes

- **Não consigo lançar em um projeto** → você precisa estar **alocado** na tarefa (peça ao
  Admin/RH).
- **O sistema não deixa salvar um valor** → use múltiplos de 0,5h (ex.: 0,5; 1; 1,5; 2).
- **Meu dia está "Aprovado" e não edito mais** → dia aprovado é somente leitura; fale com o gestor
  se precisar corrigir.
- **Esqueci de fechar a quinzena** → você ainda pode fechar pela tela Quinzena enquanto houver
  dias em aberto.
