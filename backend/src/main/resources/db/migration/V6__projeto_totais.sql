-- V6: totais do projeto passam a ser somatórios das fases (somente leitura no cadastro).
-- Novas colunas de valor/custo realizado e valor previsto/realizado no projeto;
-- backfill inicial a partir das fases existentes.
ALTER TABLE projeto ADD COLUMN vl_custo_realizado NUMERIC(14,2);
ALTER TABLE projeto ADD COLUMN vl_previsto        NUMERIC(14,2);
ALTER TABLE projeto ADD COLUMN vl_realizado       NUMERIC(14,2);

UPDATE projeto p SET
  nr_horasprevistas  = s.hp,
  nr_horasreal       = s.hr,
  vl_previsto        = s.vp,
  vl_realizado       = s.vr,
  vl_custo_previsto  = s.cp,
  vl_custo_realizado = s.cr
FROM (
  SELECT cd_projeto,
         SUM(nr_horasprevistas) hp, SUM(nr_horasreal) hr,
         SUM(vl_previsto) vp, SUM(vl_realizado) vr,
         SUM(vl_custo_previsto) cp, SUM(vl_custo_realizado) cr
  FROM projeto_fase GROUP BY cd_projeto
) s
WHERE p.cd_projeto = s.cd_projeto;
