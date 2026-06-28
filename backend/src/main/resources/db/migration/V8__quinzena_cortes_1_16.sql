-- Períodos de log passam a ser 1–15 e 16–fim do mês (cortes 1 e 16),
-- substituindo o modelo legado 11–25 / 26–10 que cruzava o mês.
UPDATE param_geral SET vl_parametro = '1',  ds_parametro = 'Dia inicial da 1a quinzena (1–15)'        WHERE nm_parametro = 'QUINZENA_CORTE_1';
UPDATE param_geral SET vl_parametro = '16', ds_parametro = 'Dia inicial da 2a quinzena (16–fim do mês)' WHERE nm_parametro = 'QUINZENA_CORTE_2';
