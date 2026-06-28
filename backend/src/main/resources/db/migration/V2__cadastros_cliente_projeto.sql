-- V2: ampliação dos cadastros de Cliente e Projeto.
-- Novos campos de cliente (contato financeiro, site, endereço) e de projeto
-- (custo previsto, faturamento, gerentes, vertical, tipo de contrato, comissão,
-- observação). Tabelas de domínio: vertical e tipo_contrato. Perfil COMERCIAL.
-- Colunas nullable no banco (não quebra linhas existentes/migração); a
-- obrigatoriedade do cliente é validada na camada de aplicação.

-- ---------- Cliente (clifor) ----------
ALTER TABLE clifor ADD COLUMN nm_contato_financeiro  VARCHAR(80);
ALTER TABLE clifor ADD COLUMN ds_email_financeiro    VARCHAR(120);
ALTER TABLE clifor ADD COLUMN ds_site                VARCHAR(120);
ALTER TABLE clifor ADD COLUMN ds_logradouro          VARCHAR(120);
ALTER TABLE clifor ADD COLUMN nr_endereco            VARCHAR(20);
ALTER TABLE clifor ADD COLUMN ds_complemento         VARCHAR(60);
ALTER TABLE clifor ADD COLUMN nm_cidade              VARCHAR(60);
ALTER TABLE clifor ADD COLUMN sg_estado              CHAR(2);
ALTER TABLE clifor ADD COLUMN nr_cep                 VARCHAR(9);

-- ---------- Domínios para projeto ----------
CREATE TABLE vertical (
    cd_vertical  SMALLINT PRIMARY KEY,
    ds_vertical  VARCHAR(60) NOT NULL,
    st_vertical  VARCHAR(3) NOT NULL DEFAULT 'AT'
);

CREATE TABLE tipo_contrato (
    cd_tipo_contrato  SMALLINT PRIMARY KEY,
    ds_tipo_contrato  VARCHAR(60) NOT NULL,
    st_tipo_contrato  VARCHAR(3) NOT NULL DEFAULT 'AT'
);

INSERT INTO vertical (cd_vertical, ds_vertical) VALUES
 (1,'Tecnologia'),(2,'Serviços Financeiros'),(3,'Saúde'),(4,'Varejo'),
 (5,'Indústria'),(6,'Setor Público'),(7,'Telecom'),(8,'Outros');

INSERT INTO tipo_contrato (cd_tipo_contrato, ds_tipo_contrato) VALUES
 (1,'Tempo e Material'),(2,'Escopo Fechado'),(3,'Bodyshop/Alocação'),
 (4,'Sustentação'),(5,'Recorrente/SaaS'),(6,'Outros');

-- ---------- Projeto ----------
ALTER TABLE projeto ADD COLUMN vl_custo_previsto    NUMERIC(14,2);
ALTER TABLE projeto ADD COLUMN cd_tipo_faturamento  VARCHAR(20);   -- FECHADO_FASE, FECHADO_MENSAL, HH_MENSAL, CUSTOMIZADO, NENHUM
ALTER TABLE projeto ADD COLUMN cd_gerente_tecnico   INTEGER REFERENCES funcionario(cd_funcionario);
ALTER TABLE projeto ADD COLUMN cd_gerente_comercial INTEGER REFERENCES funcionario(cd_funcionario);
ALTER TABLE projeto ADD COLUMN cd_vertical          SMALLINT REFERENCES vertical(cd_vertical);
ALTER TABLE projeto ADD COLUMN cd_tipo_contrato     SMALLINT REFERENCES tipo_contrato(cd_tipo_contrato);
ALTER TABLE projeto ADD COLUMN in_comissao          CHAR(1) NOT NULL DEFAULT 'N' CHECK (in_comissao IN ('S','N'));
ALTER TABLE projeto ADD COLUMN ds_observacao        VARCHAR(1000);

-- ---------- Perfil comercial (para a lista de gerente comercial) ----------
INSERT INTO perfil (cd_perfil, ds_perfil) VALUES (6,'COMERCIAL')
 ON CONFLICT (cd_perfil) DO NOTHING;
