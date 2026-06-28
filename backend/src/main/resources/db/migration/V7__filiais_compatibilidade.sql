-- V7: filiais com dados de compatibilidade. A base mantém RJ, SP e as filiais
-- legadas (COMMIT GO, COMMIT POA SP, COMMIT PR) para integridade dos dados
-- históricos; na tela, porém, apenas RJ e SP são selecionáveis (in_selecionavel='S').
ALTER TABLE empresa ADD COLUMN IF NOT EXISTS in_selecionavel CHAR(1) NOT NULL DEFAULT 'N';

UPDATE empresa SET in_selecionavel = 'S' WHERE sg_filial IN ('RJ','SP');

INSERT INTO empresa (cd_empresa, nm_empresa, sg_filial, in_selecionavel) VALUES
 (3,'COMMIT GO',     'GO', 'N'),
 (4,'COMMIT POA SP', 'PA', 'N'),
 (5,'COMMIT PR',     'PR', 'N')
 ON CONFLICT (cd_empresa) DO NOTHING;
