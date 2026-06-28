# Migração de dados — Oracle (TIMESHEET) → PostgreSQL

Kit para migrar o histórico do sistema legado para o novo schema (Flyway V1).

## Estratégia
1. **Schema novo primeiro**: o destino PostgreSQL já deve estar com a migration **V1 aplicada**
   (suba o backend uma vez, ou rode `flyway migrate`). A migração só **insere dados**.
2. **Fonte Oracle**: o `etl.py` lê direto do Oracle via `python-oracledb` (modo *thin*, sem
   Instant Client). Se você tem um **dump (.dmp)**, primeiro faça `impdp` num Oracle temporário
   e aponte o ETL para ele.
3. **Charset**: o driver entrega `str` Unicode; o destino é UTF-8 — a conversão de latin-1
   acontece automaticamente. A validação inclui uma amostra de acentuação.
4. **FKs/triggers**: durante a carga o ETL usa `session_replication_role=replica` no Postgres
   para não depender da ordem e tratar a auto-FK de `funcionario(aprovador)`.
5. **Idempotência**: use `--truncate` para recarga total (limpa as tabelas-alvo antes).

## Tabelas migradas e mapeamento (legado → novo)
| Oracle (TIMESHEET) | PostgreSQL | Observações |
|--------------------|------------|-------------|
| EMPRESA | empresa | subset (cd, nome, iss) |
| CLIFOR | clifor | CNPJ via TO_CHAR |
| FUNCIONARIO | funcionario | `st_funcionario` derivado de DT_RESCISAO; `FUNC_CD_FUNC_*` → `cd_*` |
| PROJETO | projeto | ACEITA_HORAS_*_S_N → aceita_horas_* |
| PROJETO_FASE / PROJETO_TAREFA | idem | inclui in_tarefa_externa e % conclusão |
| PROJETO_TAREFA_FUNCIONARIO | projeto_tarefa_funcionario | alocação (FUNC_CD_FUNC → cd_funcionario) |
| JORNADA_TRABALHO | jornada_trabalho | `horas_dia` = HORA_FIM − HORA_INICIO |
| FERIADO | feriado | IDENTITY no destino (sequência ressincronizada) |
| LOG_PERIODO | log_periodo | FUNC_CD_FUNC → cd_funcionario |
| TIMESHEET_DIA / TIMESHEET_DIA_TAREFA | idem | núcleo do histórico de horas |
| MOVIMENTACAO_SALDO_BHE | movimentacao_saldo_bhe | IDENTITY ressincronizada |

**Não migrados (intencional):** `param_geral` (mantém os seeds da V1: cortes de quinzena, etc.),
`perfil`/`funcionario_perfil` (perfis são semeados na V1; a atribuição por funcionário deve ser
feita após a migração — manualmente ou derivada das tabelas COM_ACES_* do legado, se desejado).

## Como executar
```bash
cd migration
pip install -r requirements.txt
cp .env.example .env     # preencha ORA_* e PG_*  (NÃO comitar o .env)
python etl.py --truncate # carga total (sem --truncate, apenas insere)
psql "$PG_DSN" -f validacao.sql   # confere contagens, BHE, integridade e acentuação
```

## Cutover (sugerido)
1. Congelar o legado (sem novos lançamentos).
2. Gerar o dump final do Oracle.
3. Rodar `etl.py --truncate` no PostgreSQL de produção.
4. Rodar `validacao.sql` e conferir contagens e saldos de BHE com o legado.
5. Atribuir perfis aos funcionários (RBAC) e validar logins (SSO).
6. Liberar o novo sistema; manter o legado em leitura por uma ou duas quinzenas (definir).

## Segurança
- O `.env` (credenciais Oracle/PG) é **gitignored**. Nunca versione segredos.
- Rode a migração a partir de uma rede confiável; use usuário Oracle **somente leitura** na origem.
