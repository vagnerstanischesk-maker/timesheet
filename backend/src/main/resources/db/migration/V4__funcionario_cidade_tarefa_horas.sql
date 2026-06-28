-- V4: cidade/UF no endereço do funcionário (a tarefa já possui horas/percentual no V1).
ALTER TABLE funcionario ADD COLUMN nm_cidade VARCHAR(60);
ALTER TABLE funcionario ADD COLUMN sg_estado CHAR(2);
