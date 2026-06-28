-- Validação pós-migração (rode no PostgreSQL após o etl.py).
-- 1) Contagens por tabela (compare com o Oracle de origem).
SELECT 'empresa' t, count(*) n FROM empresa
UNION ALL SELECT 'clifor', count(*) FROM clifor
UNION ALL SELECT 'funcionario', count(*) FROM funcionario
UNION ALL SELECT 'projeto', count(*) FROM projeto
UNION ALL SELECT 'projeto_fase', count(*) FROM projeto_fase
UNION ALL SELECT 'projeto_tarefa', count(*) FROM projeto_tarefa
UNION ALL SELECT 'projeto_tarefa_funcionario', count(*) FROM projeto_tarefa_funcionario
UNION ALL SELECT 'jornada_trabalho', count(*) FROM jornada_trabalho
UNION ALL SELECT 'feriado', count(*) FROM feriado
UNION ALL SELECT 'log_periodo', count(*) FROM log_periodo
UNION ALL SELECT 'timesheet_dia', count(*) FROM timesheet_dia
UNION ALL SELECT 'timesheet_dia_tarefa', count(*) FROM timesheet_dia_tarefa
UNION ALL SELECT 'movimentacao_saldo_bhe', count(*) FROM movimentacao_saldo_bhe
ORDER BY t;

-- 2) Reconciliação do saldo de BHE: saldo do funcionário x soma dos movimentos.
--    (Os abatimentos no legado são negativos; somá-los deve aproximar o saldo atual.)
SELECT f.cd_funcionario, f.nm_funcionario,
       f.func_nr_saldo_bhe50  AS saldo50,
       COALESCE(SUM(m.mosa_vl_abatimento50),0)  AS mov50,
       f.func_nr_saldo_bhe100 AS saldo100,
       COALESCE(SUM(m.mosa_vl_abatimento100),0) AS mov100
FROM funcionario f
LEFT JOIN movimentacao_saldo_bhe m ON m.cd_funcionario = f.cd_funcionario
GROUP BY f.cd_funcionario, f.nm_funcionario, f.func_nr_saldo_bhe50, f.func_nr_saldo_bhe100
HAVING f.func_nr_saldo_bhe50 <> 0 OR f.func_nr_saldo_bhe100 <> 0
ORDER BY f.cd_funcionario;

-- 3) Integridade referencial: lançamentos sem tarefa/projeto correspondente (deve retornar 0).
SELECT count(*) AS tarefas_orfas
FROM timesheet_dia_tarefa td
LEFT JOIN projeto_tarefa pt
  ON pt.cd_projeto = td.cd_projeto AND pt.seq_projeto_fase = td.seq_projeto_fase
 AND pt.seq_projeto_tarefa = td.seq_projeto_tarefa
WHERE pt.cd_projeto IS NULL;

-- 4) Caracteres (acentuação) — amostra para conferir conversão latin-1 -> UTF-8.
SELECT cd_funcionario, nm_funcionario FROM funcionario
WHERE nm_funcionario ~ '[ãâáàçéêíõôóú]' FETCH FIRST 20 ROWS ONLY;

-- 5) Status de tarefa/alocação traduzido para o domínio novo (INI/ENC/NIN).
--    Não deve sobrar nenhum 'AT' legado; alocações 'INI' são as lançáveis no novo sistema.
SELECT 'tarefa' obj, st_tarefa status, count(*) n FROM projeto_tarefa GROUP BY st_tarefa
UNION ALL
SELECT 'alocacao', st_alocacao, count(*) FROM projeto_tarefa_funcionario GROUP BY st_alocacao
ORDER BY obj, status;

-- 6) Perfis (papéis internos) e quantos funcionários têm cada um (RBAC pós-migração).
--    Os papéis NÃO vêm do Entra; confira se há ao menos um ADMIN_RH para administrar o sistema.
SELECT p.ds_perfil, count(fp.cd_funcionario) AS funcionarios
FROM perfil p LEFT JOIN funcionario_perfil fp ON fp.cd_perfil = p.cd_perfil
GROUP BY p.ds_perfil ORDER BY p.ds_perfil;

-- 7) Cortes de quinzena vigentes (devem ser 1 e 16 após a migração V8).
SELECT nm_parametro, vl_parametro FROM param_geral
WHERE nm_parametro IN ('QUINZENA_CORTE_1','QUINZENA_CORTE_2');
