# Relatório de verificação — Triscal Apontamento de Horas

Data: 2026-06-18 · Escopo: verificação possível no ambiente atual (sandbox).

## Ambiente
O sandbox tem **Java 11**, **sem Maven** e **sem acesso ao registro npm**. Por isso **não foi
possível executar o build real** (Maven exige JDK 21) nem `npm install/build`/testes aqui.
Foi feita uma **verificação estática** abrangente (abaixo). O build/testes devem ser executados
em ambiente com **JDK 21 + Maven** e rede liberada (ver "Como validar localmente").

## Verificações executadas (resultado: OK)
- **Java (66 arquivos):** pacote de cada arquivo == caminho; nome do tipo público == nome do
  arquivo; imports internos `com.triscal.*` resolvidos (0 problemas reais — o único alerta era
  o `record` aninhado `CalculadoraBhe.ResultadoBhe`, import válido).
- **Referências entre camadas:** todas as chamadas a métodos de repositórios nos serviços
  correspondem a métodos declarados (Spring Data) — 0 divergências.
- **Frontend (18 arquivos TS/TSX):** todos os imports relativos resolvem para arquivos
  existentes; todas as páginas roteadas em `App.tsx` existem.
- **application.yml:** YAML válido (chaves: spring, server, management, app).
- **Schema Flyway V1:** 19 `CREATE TABLE`, 19 PKs, 20 `REFERENCES`, parênteses balanceados;
  todas as tabelas-núcleo presentes (funcionario, timesheet_dia[_tarefa], log_periodo, feriado,
  movimentacao_saldo_bhe, andamento_projeto_hist, fechamento_quinzenal, param_geral, perfil).

## Testes automatizados (a executar no build)
Cobrem as regras críticas: `CalculadoraBheTest` (PJ/CLT, domingo/feriado, ausência, sem banco),
`QuinzenaTest` (períodos 11–25 e 26–10, referência de dia), `CalculadoraExecucaoTest`
(% e desvio), `LancamentoValidatorTest` (0,5h/24h), `StatusDiaTest`.

## Como validar localmente (ambiente adequado)
```bash
# pré-requisitos: JDK 21, Maven 3.9+, Node 20+, Docker
cd infra && docker compose up -d           # PostgreSQL + Keycloak (importa o realm)
cd ../backend && mvn clean verify           # compila + roda testes + Flyway valida o schema
cd ../frontend && npm install && npm run build   # type-check + build do SPA
```

## Limitações conhecidas / próximos passos
- Build e testes ainda **não executados** (ambiente sem JDK 21/Maven/npm) — rodar conforme acima.
- Cadastros completos (fases, tarefas, alocação, funcionários, clientes, feriados, BHE manual)
  além de projetos: pendentes (projetos já tem CRUD).
- Integração que substitui o OTRS (horas externas read-only): interface a implementar.
- Migração de dados Oracle→PostgreSQL: a partir do dump completo.
- Entregáveis a gerar: documentação técnica e manual do usuário.
