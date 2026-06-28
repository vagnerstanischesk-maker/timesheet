package com.triscal.timesheet.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "timesheet_dia_tarefa")
public class TimesheetDiaTarefa {
    @EmbeddedId
    private TimesheetDiaTarefaId id;

    @Column(name = "horas_nr_nc", nullable = false) private BigDecimal horasNc = BigDecimal.ZERO;
    @Column(name = "horas_nr_co", nullable = false) private BigDecimal horasCo = BigDecimal.ZERO;
    @Column(name = "horas_nr_ncextra", nullable = false) private BigDecimal horasNcExtra = BigDecimal.ZERO;
    @Column(name = "horas_nr_coextra", nullable = false) private BigDecimal horasCoExtra = BigDecimal.ZERO;
    @Column(name = "horas_tx_obs") private String obs;
    @Column(name = "dt_ult_alt") private OffsetDateTime dtUltAlt;
    @Column(name = "usur_cd_usur") private String usuario;

    protected TimesheetDiaTarefa() {}
    public TimesheetDiaTarefa(TimesheetDiaTarefaId id) { this.id = id; }

    public TimesheetDiaTarefaId getId(){return id;}
    public BigDecimal getHorasNc(){return horasNc;}  public void setHorasNc(BigDecimal v){this.horasNc=v;}
    public BigDecimal getHorasCo(){return horasCo;}  public void setHorasCo(BigDecimal v){this.horasCo=v;}
    public BigDecimal getHorasNcExtra(){return horasNcExtra;} public void setHorasNcExtra(BigDecimal v){this.horasNcExtra=v;}
    public BigDecimal getHorasCoExtra(){return horasCoExtra;} public void setHorasCoExtra(BigDecimal v){this.horasCoExtra=v;}
    public String getObs(){return obs;} public void setObs(String o){this.obs=o;}
    public void setUsuario(String u){this.usuario=u;}
    public void setDtUltAlt(OffsetDateTime d){this.dtUltAlt=d;}
}
