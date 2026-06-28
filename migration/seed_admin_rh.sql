-- Concede o papel ADMIN_RH ao primeiro administrador do sistema: André Vieira de Mello.
-- RODAR APÓS o ETL (migration/etl.py), pois depende do funcionário já migrado do legado.
-- Idempotente (não duplica se rodar de novo). Sem um ADMIN_RH ninguém administra cadastros/papéis.

INSERT INTO funcionario_perfil (cd_funcionario, cd_perfil)
SELECT f.cd_funcionario, p.cd_perfil
FROM funcionario f
CROSS JOIN perfil p
WHERE p.ds_perfil = 'ADMIN_RH'
  AND upper(btrim(f.nm_funcionario)) = upper('André Vieira de Mello')
ON CONFLICT (cd_funcionario, cd_perfil) DO NOTHING;

-- Alternativa (mais robusta): casar pelo e-mail corporativo, que é o claim de vínculo do SSO.
-- Descomente e ajuste o e-mail; comente o bloco acima se preferir esta forma:
-- INSERT INTO funcionario_perfil (cd_funcionario, cd_perfil)
-- SELECT f.cd_funcionario, p.cd_perfil
-- FROM funcionario f CROSS JOIN perfil p
-- WHERE p.ds_perfil = 'ADMIN_RH'
--   AND lower(btrim(f.ds_email)) = lower('andre.mello@triscal.com.br')
-- ON CONFLICT (cd_funcionario, cd_perfil) DO NOTHING;

-- Conferência: deve listar o André com ADMIN_RH.
SELECT f.cd_funcionario, f.nm_funcionario, f.ds_email
FROM funcionario f
JOIN funcionario_perfil fp ON fp.cd_funcionario = f.cd_funcionario
JOIN perfil p ON p.cd_perfil = fp.cd_perfil
WHERE p.ds_perfil = 'ADMIN_RH'
ORDER BY f.nm_funcionario;
