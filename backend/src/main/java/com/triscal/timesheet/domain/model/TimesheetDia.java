package com.triscal.timesheet.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "timesheet_dia")
public class TimesheetDia {
    @EmbeddedId
    private TimesheetDiaId id;

    @Column(name = "log_nr_hnormal", nullable = false)
    private BigDecimal horasNormais = BigDecimal.ZERO;

    @Column(name = "log_qn_ausbhe", nullable = false)
    private BigDecimal ausenciaBhe = BigDecimal.ZERO;

    @Column(name = "log_qn_ausabn", nullable = false)
    private BigDecimal ausenciaAbono = BigDecimal.ZERO;

    @Column(name = "st_timesheet_dia", nullable = false)
    private char status = 'A';   // A,F,P,R

    @Column(name = "obs")
    private String obs;

    @Column(name = "dt_ult_alt")
    private OffsetDateTime dtUltAlt;

    @Column(name = "usur_cd_usur")
    private String usuario;

    protected TimesheetDia() {}
    public TimesheetDia(TimesheetDiaId id) { this.id = id; }

    public TimesheetDiaId getId(){return id;}
    public BigDecimal getHorasNormais(){return horasNormais;}
    public void setHorasNormais(BigDecimal v){this.horasNormais=v;}
    public BigDecimal getAusenciaBhe(){return ausenciaBhe;}
    public void setAusenciaBhe(BigDecimal v){this.ausenciaBhe=v;}
    public BigDecimal getAusenciaAbono(){return ausenciaAbono;}
    public void setAusenciaAbono(BigDecimal v){this.ausenciaAbono=v;}
    public char getStatus(){return status;}
    public void setStatus(char s){this.status=s;}
    public String getObs(){return obs;}
    public void setObs(String o){this.obs=o;}
    public void setUsuario(String u){this.usuario=u;}
    public void setDtUltAlt(OffsetDateTime d){this.dtUltAlt=d;}
}
