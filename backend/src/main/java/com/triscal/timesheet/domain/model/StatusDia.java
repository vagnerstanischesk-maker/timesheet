package com.triscal.timesheet.domain.model;

/** Status do dia de apontamento (ST_TIMESHEET_DIA). Fluxo: A -> F -> (P | R). */
public enum StatusDia {
    ABERTO('A'),
    FECHADO('F'),
    APROVADO('P'),
    REJEITADO('R');

    private final char codigo;
    StatusDia(char codigo) { this.codigo = codigo; }
    public char codigo() { return codigo; }

    public static StatusDia of(char c) {
        for (StatusDia s : values()) if (s.codigo == c) return s;
        throw new IllegalArgumentException("Status de dia inválido: " + c);
    }
}
