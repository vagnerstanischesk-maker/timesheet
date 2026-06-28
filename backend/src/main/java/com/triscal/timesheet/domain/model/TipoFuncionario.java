package com.triscal.timesheet.domain.model;

/** Tipo do funcionário: E=Pessoa Jurídica (PJ), F=CLT. Afeta a regra de hora extra/BHE. */
public enum TipoFuncionario {
    PJ('E'),
    CLT('F');

    private final char codigo;
    TipoFuncionario(char codigo) { this.codigo = codigo; }
    public char codigo() { return codigo; }

    public static TipoFuncionario of(char c) {
        for (TipoFuncionario t : values()) if (t.codigo == c) return t;
        throw new IllegalArgumentException("Tipo de funcionário inválido: " + c);
    }
}
