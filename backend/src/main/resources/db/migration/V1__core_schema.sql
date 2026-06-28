-- =====================================================================
-- Triscal · Apontamento de Horas — schema inicial (PostgreSQL 16)
-- Migration Flyway V1. Preserva a semântica do legado Oracle (schema TIMESHEET)
-- e adiciona as tabelas novas (andamento e fechamento) e auditoria.
-- Convenções: snake_case, chaves naturais do legado mantidas para a migração.
-- =====================================================================

-- ---------- Organização ----------
CREATE TABLE empresa (
    cd_empresa      INTEGER PRIMARY KEY,            -- filial/escritório (origem de projeto e de funcionário)
    nm_empresa      VARCHAR(60) NOT NULL,
    pr_iss          NUMERIC(5,2),
    dt_ult_alt      TIMESTAMP DEFAULT now()
);

CREATE TABLE clifor (
    cd_clifor       INTEGER PRIMARY KEY,            -- cliente
    nm_razaosocial  VARCHAR(120) NOT NULL,
    nm_fantasia     VARCHAR(60),
    nr_cnpj         VARCHAR(14),
    st_clifor       VARCHAR(3) NOT NULL DEFAULT 'AT',
    cd_filial_origem INTEGER REFERENCES empresa(cd_empresa),
    dt_ult_alt      TIMESTAMP DEFAULT now()
);

-- ---------- Pessoas ----------
CREATE TABLE funcionario (
    cd_funcionario          INTEGER PRIMARY KEY,
    nm_funcionario          VARCHAR(80) NOT NULL,
    tp_funcionario          CHAR(1) NOT NULL CHECK (tp_funcionario IN ('E','F')), -- E=PJ, F=CLT
    cd_empresa              INTEGER REFERENCES empresa(cd_empresa),               -- filial do funcionário
    func_cd_func_aprovador  INTEGER REFERENCES funcionario(cd_funcionario),       -- aprovador
    in_possui_banco_horas   CHAR(1) NOT NULL DEFAULT 'N' CHECK (in_possui_banco_horas IN ('S','N')),
    func_nr_saldo_bhe50     NUMERIC(7,2) NOT NULL DEFAULT 0,
    func_nr_saldo_bhe100    NUMERIC(7,2) NOT NULL DEFAULT 0,
    ds_email                VARCHAR(120),
    cdg_usur                VARCHAR(60),            -- vínculo com a identidade (Entra/Keycloak: sub/upn)
    dt_admissao             DATE,
    dt_rescisao             DATE,
    st_funcionario          VARCHAR(3) NOT NULL DEFAULT 'AT',
    dt_ult_alt              TIMESTAMP DEFAULT now()
);
CREATE INDEX ix_func_aprovador ON funcionario(func_cd_func_aprovador);
CREATE INDEX ix_func_cdg_usur  ON funcionario(cdg_usur);

CREATE TABLE jornada_trabalho (
    cd_funcionario   INTEGER NOT NULL REFERENCES funcionario(cd_funcionario),
    dt_inicio_vig    DATE NOT NULL,
    dt_fim_vig       DATE,
    horas_dia        NUMERIC(4,2) NOT NULL DEFAULT 8.0,
    dt_ult_alt       TIMESTAMP DEFAULT now(),
    PRIMARY KEY (cd_funcionario, dt_inicio_vig)
);

-- ---------- RBAC híbrido (Entra base + perfis internos) ----------
CREATE TABLE perfil (
    cd_perfil   SMALLINT PRIMARY KEY,
    ds_perfil   VARCHAR(40) NOT NULL  -- COLABORADOR, GESTOR, GESTOR_PROJETO, ADMIN_RH, DIRETORIA
);
CREATE TABLE funcionario_perfil (
    cd_funcionario INTEGER NOT NULL REFERENCES funcionario(cd_funcionario),
    cd_perfil      SMALLINT NOT NULL REFERENCES perfil(cd_perfil),
    PRIMARY KEY (cd_funcionario, cd_perfil)
);

-- ---------- Projetos / fases / tarefas ----------
CREATE TABLE projeto (
    cd_projeto          INTEGER PRIMARY KEY,
    ds_projeto          VARCHAR(200) NOT NULL,
    in_interno          CHAR(1) NOT NULL DEFAULT 'N' CHECK (in_interno IN ('S','N')),
    cd_clifor           INTEGER REFERENCES clifor(cd_clifor),
    cd_empresa          INTEGER REFERENCES empresa(cd_empresa),     -- filial de origem do projeto
    st_projeto          VARCHAR(3) NOT NULL DEFAULT 'AT',
    dt_inicio_prev      DATE, dt_fim_previsto DATE, dt_inicio_real DATE, dt_fim_real DATE,
    nr_horasprevistas   NUMERIC(9,2), nr_horasreal NUMERIC(9,2),
    aceita_horas_co     CHAR(1) NOT NULL DEFAULT 'S' CHECK (aceita_horas_co IN ('S','N')),
    aceita_horas_nc     CHAR(1) NOT NULL DEFAULT 'S' CHECK (aceita_horas_nc IN ('S','N')),
    dt_ult_alt          TIMESTAMP DEFAULT now()
);

CREATE TABLE projeto_fase (
    cd_projeto          INTEGER NOT NULL REFERENCES projeto(cd_projeto),
    seq_projeto_fase    SMALLINT NOT NULL,
    ds_fase             VARCHAR(80) NOT NULL,
    st_faseproj         VARCHAR(3) NOT NULL DEFAULT 'AT',
    dt_inicio_prev DATE, dt_fim_previsto DATE, dt_inicio_real DATE, dt_fim_real DATE,
    nr_horasprevistas   NUMERIC(9,2), nr_horasreal NUMERIC(9,2),
    perc_conclusao_fase NUMERIC(5,2) DEFAULT 0,
    dt_ult_alt          TIMESTAMP DEFAULT now(),
    PRIMARY KEY (cd_projeto, seq_projeto_fase)
);

CREATE TABLE projeto_tarefa (
    cd_projeto          INTEGER NOT NULL,
    seq_projeto_fase    SMALLINT NOT NULL,
    seq_projeto_tarefa  SMALLINT NOT NULL,
    ds_tarefa           VARCHAR(80) NOT NULL,
    st_tarefa           VARCHAR(3) NOT NULL DEFAULT 'AT',
    in_tarefa_externa   CHAR(1) NOT NULL DEFAULT 'N' CHECK (in_tarefa_externa IN ('S','N')), -- horas via integração (OTRS): read-only no log
    dt_inicio_prev DATE, dt_fim_previsto DATE, dt_inicio_real DATE, dt_fim_real DATE,
    nr_horasprevistas   NUMERIC(9,2), nr_horasreal NUMERIC(9,2),
    perc_conclusao_tarefa NUMERIC(5,2) DEFAULT 0,
    dt_ult_alt          TIMESTAMP DEFAULT now(),
    PRIMARY KEY (cd_projeto, seq_projeto_fase, seq_projeto_tarefa),
    FOREIGN KEY (cd_projeto, seq_projeto_fase) REFERENCES projeto_fase(cd_projeto, seq_projeto_fase)
);

-- Alocação fina: quem pode lançar em cada tarefa
CREATE TABLE projeto_tarefa_funcionario (
    cd_projeto          INTEGER NOT NULL,
    seq_projeto_fase    SMALLINT NOT NULL,
    seq_projeto_tarefa  SMALLINT NOT NULL,
    cd_funcionario      INTEGER NOT NULL REFERENCES funcionario(cd_funcionario),
    st_alocacao         VARCHAR(3) NOT NULL DEFAULT 'AT',
    taxa_horaria_normal NUMERIC(12,2),
    taxa_horaria_extra  NUMERIC(12,2),
    perc_conclusao_tarefa_func NUMERIC(5,2) DEFAULT 0,
    dt_ult_alt          TIMESTAMP DEFAULT now(),
    PRIMARY KEY (cd_projeto, seq_projeto_fase, seq_projeto_tarefa, cd_funcionario),
    FOREIGN KEY (cd_projeto, seq_projeto_fase, seq_projeto_tarefa)
        REFERENCES projeto_tarefa(cd_projeto, seq_projeto_fase, seq_projeto_tarefa)
);
CREATE INDEX ix_ptf_func ON projeto_tarefa_funcionario(cd_funcionario);

-- ---------- Calendário ----------
CREATE TABLE feriado (
    feri_cd_feriado     BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    feri_nm_feriado     VARCHAR(250) NOT NULL,
    feri_nr_ano         SMALLINT,        -- nulo para tipos fixos
    feri_nr_mes         SMALLINT NOT NULL,
    feri_nr_dia         SMALLINT NOT NULL,
    feri_tp_feriado     VARCHAR(2) NOT NULL CHECK (feri_tp_feriado IN ('NN','NF','LN','LF')),
    filial_cd_filial    INTEGER REFERENCES empresa(cd_empresa),   -- obrigatório para LN/LF
    feri_nr_horas_a_trabalhar NUMERIC(5,2) DEFAULT 0,
    feri_stt_inativo    CHAR(1) NOT NULL DEFAULT 'N' CHECK (feri_stt_inativo IN ('S','N')),
    dt_ult_alt          TIMESTAMP DEFAULT now()
);
CREATE INDEX ix_feriado_data ON feriado(feri_nr_mes, feri_nr_dia);

-- ---------- Quinzena (período de log) ----------
CREATE TABLE log_periodo (
    cd_funcionario   INTEGER NOT NULL REFERENCES funcionario(cd_funcionario),
    lope_nr_ano      SMALLINT NOT NULL,
    lope_nr_mes      SMALLINT NOT NULL,
    peri_cd_periodo  SMALLINT NOT NULL,   -- 1 ou 2 (1ª/2ª quinzena)
    lope_stt_log     CHAR(1) NOT NULL DEFAULT 'A' CHECK (lope_stt_log IN ('A','F','P','R')),
    dt_ult_alt       TIMESTAMP DEFAULT now(),
    usur_cd_usur     VARCHAR(60),
    PRIMARY KEY (cd_funcionario, lope_nr_ano, lope_nr_mes, peri_cd_periodo)
);

-- ---------- Lançamentos ----------
-- Dia (sem projeto): horas normais, ausências e status do dia
CREATE TABLE timesheet_dia (
    cd_funcionario   INTEGER NOT NULL REFERENCES funcionario(cd_funcionario),
    lope_nr_ano      SMALLINT NOT NULL,
    lope_nr_mes      SMALLINT NOT NULL,
    log_nr_dia       SMALLINT NOT NULL,
    log_nr_hnormal   NUMERIC(5,2) NOT NULL DEFAULT 0,
    log_qn_ausbhe    NUMERIC(5,2) NOT NULL DEFAULT 0,   -- ausência abatida do banco de horas
    log_qn_ausabn    NUMERIC(5,2) NOT NULL DEFAULT 0,   -- ausência abonada
    st_timesheet_dia CHAR(1) NOT NULL DEFAULT 'A' CHECK (st_timesheet_dia IN ('A','F','P','R')),
    obs              VARCHAR(1000),
    dt_ult_alt       TIMESTAMP DEFAULT now(),
    usur_cd_usur     VARCHAR(60),
    PRIMARY KEY (cd_funcionario, lope_nr_ano, lope_nr_mes, log_nr_dia)
);

-- Horas do dia por tarefa de projeto
CREATE TABLE timesheet_dia_tarefa (
    cd_funcionario     INTEGER NOT NULL,
    cd_projeto         INTEGER NOT NULL,
    seq_projeto_fase   SMALLINT NOT NULL,
    seq_projeto_tarefa SMALLINT NOT NULL,
    lope_nr_ano        SMALLINT NOT NULL,
    lope_nr_mes        SMALLINT NOT NULL,
    log_nr_dia         SMALLINT NOT NULL,
    horas_nr_nc        NUMERIC(5,2) NOT NULL DEFAULT 0,  -- normais sem cobrança
    horas_nr_co        NUMERIC(5,2) NOT NULL DEFAULT 0,  -- normais com cobrança
    horas_nr_ncextra   NUMERIC(5,2) NOT NULL DEFAULT 0,  -- extra sem cobrança
    horas_nr_coextra   NUMERIC(5,2) NOT NULL DEFAULT 0,  -- extra com cobrança
    horas_tx_obs       VARCHAR(1000),
    dt_ult_alt         TIMESTAMP DEFAULT now(),
    usur_cd_usur       VARCHAR(60),
    PRIMARY KEY (cd_funcionario, cd_projeto, seq_projeto_fase, seq_projeto_tarefa, lope_nr_ano, lope_nr_mes, log_nr_dia),
    FOREIGN KEY (cd_funcionario, lope_nr_ano, lope_nr_mes, log_nr_dia)
        REFERENCES timesheet_dia(cd_funcionario, lope_nr_ano, lope_nr_mes, log_nr_dia),
    FOREIGN KEY (cd_projeto, seq_projeto_fase, seq_projeto_tarefa)
        REFERENCES projeto_tarefa(cd_projeto, seq_projeto_fase, seq_projeto_tarefa),
    -- regra Triscal: mínimo 0,5h e múltiplos de 0,5h por campo de horas
    CONSTRAINT ck_passo_meia_hora CHECK (
        MOD((horas_nr_nc*2)::numeric,1)=0 AND MOD((horas_nr_co*2)::numeric,1)=0 AND
        MOD((horas_nr_ncextra*2)::numeric,1)=0 AND MOD((horas_nr_coextra*2)::numeric,1)=0
    )
);

-- ---------- Banco de horas ----------
CREATE TABLE movimentacao_saldo_bhe (
    mosa_nr_seq            BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    cd_funcionario         INTEGER NOT NULL REFERENCES funcionario(cd_funcionario),
    mosa_dt_data           DATE NOT NULL,
    mosa_vl_abatimento50   NUMERIC(7,2) NOT NULL DEFAULT 0,
    mosa_vl_abatimento100  NUMERIC(7,2) NOT NULL DEFAULT 0,
    mosa_in_lancamento_manual CHAR(1) NOT NULL DEFAULT 'N' CHECK (mosa_in_lancamento_manual IN ('S','N')),
    lope_nr_ano  SMALLINT, lope_nr_mes SMALLINT, log_nr_dia SMALLINT,
    usur_cdg_usur VARCHAR(60),
    dt_ult_alt    TIMESTAMP DEFAULT now()
);
CREATE INDEX ix_bhe_func_data ON movimentacao_saldo_bhe(cd_funcionario, mosa_dt_data);

-- ---------- Acompanhamento de projeto (novo) ----------
CREATE TABLE andamento_projeto_hist (
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    cd_projeto          INTEGER NOT NULL REFERENCES projeto(cd_projeto),
    seq_projeto_fase    SMALLINT,
    seq_projeto_tarefa  SMALLINT,
    dt_referencia       DATE NOT NULL,
    perc_informado      NUMERIC(5,2) NOT NULL,
    perc_derivado_horas NUMERIC(5,2),
    usur_cd_usur        VARCHAR(60) NOT NULL,
    dt_registro         TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX ix_andamento_proj ON andamento_projeto_hist(cd_projeto, dt_referencia);

CREATE TABLE fechamento_quinzenal (
    id                BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    cd_projeto        INTEGER NOT NULL REFERENCES projeto(cd_projeto),
    lope_nr_ano       SMALLINT NOT NULL,
    lope_nr_mes       SMALLINT NOT NULL,
    peri_cd_periodo   SMALLINT NOT NULL,
    horas_executadas  NUMERIC(11,2) NOT NULL DEFAULT 0,
    horas_previstas   NUMERIC(11,2),
    perc_execucao_horas NUMERIC(5,2),
    perc_informado    NUMERIC(5,2),
    desvio_perc       NUMERIC(6,2),
    usur_cd_usur      VARCHAR(60) NOT NULL,
    dt_fechamento     TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (cd_projeto, lope_nr_ano, lope_nr_mes, peri_cd_periodo)
);

-- ---------- Parâmetros e auditoria ----------
CREATE TABLE param_geral (
    nm_parametro  VARCHAR(40) PRIMARY KEY,
    ds_parametro  VARCHAR(120),
    tp_parametro  CHAR(1),
    vl_parametro  VARCHAR(120)
);

CREATE TABLE auditoria (
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    entidade     VARCHAR(60) NOT NULL,
    chave        VARCHAR(200) NOT NULL,
    acao         VARCHAR(20) NOT NULL,   -- INSERT/UPDATE/DELETE/APROVAR/REJEITAR/AJUSTE_BHE
    usuario      VARCHAR(60) NOT NULL,
    detalhe      JSONB,
    dt_evento    TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX ix_auditoria_ent ON auditoria(entidade, dt_evento);

-- ---------- Seeds mínimos ----------
INSERT INTO perfil (cd_perfil, ds_perfil) VALUES
 (1,'COLABORADOR'),(2,'GESTOR'),(3,'GESTOR_PROJETO'),(4,'ADMIN_RH'),(5,'DIRETORIA');

INSERT INTO param_geral (nm_parametro, ds_parametro, tp_parametro, vl_parametro) VALUES
 ('QUINZENA_CORTE_1','Dia inicial da 1a quinzena','N','11'),
 ('QUINZENA_CORTE_2','Dia inicial da 2a quinzena','N','26'),
 ('JORNADA_PADRAO_HORAS','Jornada padrão diária (horas)','N','8'),
 ('MIN_LANCAMENTO_HORAS','Mínimo por lançamento (horas)','N','0.5'),
 ('PASSO_LANCAMENTO_HORAS','Passo/incremento de lançamento (horas)','N','0.5'),
 ('MAX_HORAS_DIA','Máximo de horas por dia','N','24');
