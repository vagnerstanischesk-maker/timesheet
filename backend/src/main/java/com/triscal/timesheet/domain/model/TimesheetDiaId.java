package com.triscal.timesheet.domain.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TimesheetDiaId implements Serializable {
    private Integer cdFuncionario;
    private Short lopeNrAno;
    private Short lopeNrMes;
    private Short logNrDia;

    protected TimesheetDiaId() {}
    public TimesheetDiaId(Integer f, Short ano, Short mes, Short dia) {
        this.cdFuncionario=f; this.lopeNrAno=ano; this.lopeNrMes=mes; this.logNrDia=dia;
    }
    public Integer getCdFuncionario(){return cdFuncionario;}
    public Short getLopeNrAno(){return lopeNrAno;}
    public Short getLopeNrMes(){return lopeNrMes;}
    public Short getLogNrDia(){return logNrDia;}

    @Override public boolean equals(Object o){ if(this==o) return true; if(!(o instanceof TimesheetDiaId t)) return false;
        return Objects.equals(cdFuncionario,t.cdFuncionario)&&Objects.equals(lopeNrAno,t.lopeNrAno)
            &&Objects.equals(lopeNrMes,t.lopeNrMes)&&Objects.equals(logNrDia,t.logNrDia); }
    @Override public int hashCode(){ return Objects.hash(cdFuncionario,lopeNrAno,lopeNrMes,logNrDia); }
}
