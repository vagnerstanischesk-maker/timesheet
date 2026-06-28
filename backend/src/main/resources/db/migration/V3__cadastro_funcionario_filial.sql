-- V3: ampliação do cadastro de funcionário (dados pessoais, documentos, endereço,
-- dados bancários, contrato e flags), filial como lista (RJ/SP) e datas de projeto.
-- Colunas nullable (não quebram dados existentes); regras na camada de aplicação.

-- ---------- Filial (empresa) como lista RJ/SP ----------
ALTER TABLE empresa ADD COLUMN IF NOT EXISTS sg_filial CHAR(2);
INSERT INTO empresa (cd_empresa, nm_empresa, sg_filial) VALUES
 (1,'Rio de Janeiro','RJ'),
 (2,'São Paulo','SP')
 ON CONFLICT (cd_empresa) DO UPDATE SET nm_empresa = EXCLUDED.nm_empresa, sg_filial = EXCLUDED.sg_filial;

-- ---------- Funcionário: dados pessoais e documentos ----------
ALTER TABLE funcionario ADD COLUMN nr_cpf               VARCHAR(11);
ALTER TABLE funcionario ADD COLUMN nr_rg                VARCHAR(20);
ALTER TABLE funcionario ADD COLUMN ds_orgao_emissor     VARCHAR(20);
ALTER TABLE funcionario ADD COLUMN nr_titulo_eleitor    VARCHAR(20);
ALTER TABLE funcionario ADD COLUMN nr_zona_eleitoral    VARCHAR(10);
ALTER TABLE funcionario ADD COLUMN nr_secao_eleitoral   VARCHAR(10);
ALTER TABLE funcionario ADD COLUMN ds_nacionalidade     VARCHAR(40);
ALTER TABLE funcionario ADD COLUMN nm_pai               VARCHAR(80);
ALTER TABLE funcionario ADD COLUMN nm_mae               VARCHAR(80);
ALTER TABLE funcionario ADD COLUMN tp_estado_civil      VARCHAR(12) CHECK (tp_estado_civil IS NULL OR tp_estado_civil IN ('SOLTEIRO','CASADO','DIVORCIADO','VIUVO'));
ALTER TABLE funcionario ADD COLUMN dt_nascimento        DATE;

-- ---------- Funcionário: endereço ----------
ALTER TABLE funcionario ADD COLUMN ds_logradouro        VARCHAR(120);
ALTER TABLE funcionario ADD COLUMN nr_endereco          VARCHAR(20);
ALTER TABLE funcionario ADD COLUMN ds_complemento       VARCHAR(60);
ALTER TABLE funcionario ADD COLUMN ds_bairro            VARCHAR(60);
ALTER TABLE funcionario ADD COLUMN nr_cep               VARCHAR(9);
ALTER TABLE funcionario ADD COLUMN nr_tel_residencial   VARCHAR(20);
ALTER TABLE funcionario ADD COLUMN nr_tel_celular       VARCHAR(20);

-- ---------- Funcionário: dados bancários e pagamento ----------
ALTER TABLE funcionario ADD COLUMN ds_banco             VARCHAR(60);
ALTER TABLE funcionario ADD COLUMN nr_agencia           VARCHAR(20);   -- agência e dígito
ALTER TABLE funcionario ADD COLUMN nr_conta             VARCHAR(20);   -- conta e dígito
ALTER TABLE funcionario ADD COLUMN vl_ultimo_pagamento  NUMERIC(12,2);
ALTER TABLE funcionario ADD COLUMN nr_pis               VARCHAR(20);

-- ---------- Funcionário: contrato / carteira ----------
ALTER TABLE funcionario ADD COLUMN nr_carteira_trabalho VARCHAR(20);
ALTER TABLE funcionario ADD COLUMN nr_serie_carteira    VARCHAR(20);
ALTER TABLE funcionario ADD COLUMN nr_contrato          VARCHAR(30);
ALTER TABLE funcionario ADD COLUMN ds_contrato          VARCHAR(200);

-- ---------- Funcionário: flags ----------
ALTER TABLE funcionario ADD COLUMN in_vale_transporte   CHAR(1) NOT NULL DEFAULT 'N' CHECK (in_vale_transporte IN ('S','N'));
ALTER TABLE funcionario ADD COLUMN in_plano_saude       CHAR(1) NOT NULL DEFAULT 'N' CHECK (in_plano_saude IN ('S','N'));
ALTER TABLE funcionario ADD COLUMN in_requer_aprov_adm  CHAR(1) NOT NULL DEFAULT 'N' CHECK (in_requer_aprov_adm IN ('S','N'));

CREATE UNIQUE INDEX IF NOT EXISTS ux_funcionario_cpf ON funcionario(nr_cpf) WHERE nr_cpf IS NOT NULL;
