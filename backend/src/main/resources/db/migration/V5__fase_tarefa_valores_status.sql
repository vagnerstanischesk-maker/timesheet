-- V5: valores (previsto/realizado) e custos (previsto/realizado) em fase e tarefa;
-- padroniza status de fase/tarefa/alocação em INI (Aberta/Iniciada), ENC (Fechada/
-- Encerrada) e NIN (Não iniciada). Datas, horas realizadas e taxas já existem (V1).

ALTER TABLE projeto_fase ADD COLUMN vl_previsto         NUMERIC(14,2);
ALTER TABLE projeto_fase ADD COLUMN vl_realizado        NUMERIC(14,2);
ALTER TABLE projeto_fase ADD COLUMN vl_custo_previsto   NUMERIC(14,2);
ALTER TABLE projeto_fase ADD COLUMN vl_custo_realizado  NUMERIC(14,2);

ALTER TABLE projeto_tarefa ADD COLUMN vl_previsto        NUMERIC(14,2);
ALTER TABLE projeto_tarefa ADD COLUMN vl_realizado       NUMERIC(14,2);
ALTER TABLE projeto_tarefa ADD COLUMN vl_custo_previsto  NUMERIC(14,2);
ALTER TABLE projeto_tarefa ADD COLUMN vl_custo_realizado NUMERIC(14,2);

-- Converte status legados ('AT' etc.) para o novo conjunto
UPDATE projeto_fase                SET st_faseproj = 'INI' WHERE st_faseproj NOT IN ('INI','ENC','NIN');
UPDATE projeto_tarefa              SET st_tarefa   = 'INI' WHERE st_tarefa   NOT IN ('INI','ENC','NIN');
UPDATE projeto_tarefa_funcionario  SET st_alocacao = 'INI' WHERE st_alocacao NOT IN ('INI','ENC','NIN');
