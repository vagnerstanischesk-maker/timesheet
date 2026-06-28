package com.triscal.timesheet.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "movimentacao_saldo_bhe")
public class MovimentacaoSaldoBhe {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="mosa_nr_seq") private Long id;
    @Column(name="cd_funcionario", nullable=false) private Integer cdFuncionario;
    @Column(name="mosa_dt_data", nullable=false) private LocalDate data;
    @Column(name="mosa_vl_abatimento50", nullable=false) private BigDecimal valor50 = BigDecimal.ZERO;
    @Column(name="mosa_vl_abatimento100", nullable=false) private BigDecimal valor100 = BigDecimal.ZERO;
    @Column(name="mosa_in_lancamento_manual", nullable=false) private char manual = 'N';
    @Column(name="lope_nr_ano") private Short ano;
    @Column(name="lope_nr_mes") private Short mes;
    @Column(name="log_nr_dia") private Short dia;
    @Column(name="usur_cdg_usur") private String usuario;

    protected MovimentacaoSaldoBhe(){}
    public MovimentacaoSaldoBhe(Integer func, LocalDate data, BigDecimal v50, BigDecimal v100,
                                char manual, Short ano, Short mes, Short dia, String usuario){
        this.cdFuncionario=func; this.data=data; this.valor50=v50; this.valor100=v100;
        this.manual=manual; this.ano=ano; this.mes=mes; this.dia=dia; this.usuario=usuario;
    }
}
