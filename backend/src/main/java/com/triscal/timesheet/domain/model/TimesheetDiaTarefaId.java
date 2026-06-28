package com.triscal.timesheet.domain.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TimesheetDiaTarefaId implements Serializable {
    private Integer cdFuncionario;
    private Integer cdProjeto;
    private Short seqProjetoFase;
    private Short seqProjetoTarefa;
    private Short lopeNrAno;
    private Short lopeNrMes;
    private Short logNrDia;

    protected TimesheetDiaTarefaId() {}
    public TimesheetDiaTarefaId(Integer f, Integer p, Short fase, Short tarefa, Short ano, Short mes, Short dia) {
        this.cdFuncionario=f; this.cdProjeto=p; this.seqProjetoFase=fase; this.seqProjetoTarefa=tarefa;
        this.lopeNrAno=ano; this.lopeNrMes=mes; this.logNrDia=dia;
    }
    public Integer getCdFuncionario(){return cdFuncionario;}
    public Integer getCdProjeto(){return cdProjeto;}
    public Short getSeqProjetoFase(){return seqProjetoFase;}
    public Short getSeqProjetoTarefa(){return seqProjetoTarefa;}
    public Short getLopeNrAno(){return lopeNrAno;}
    public Short getLopeNrMes(){return lopeNrMes;}
    public Short getLogNrDia(){return logNrDia;}

    @Override public boolean equals(Object o){ if(this==o) return true; if(!(o instanceof TimesheetDiaTarefaId t)) return false;
        return Objects.equals(cdFuncionario,t.cdFuncionario)&&Objects.equals(cdProjeto,t.cdProjeto)
            &&Objects.equals(seqProjetoFase,t.seqProjetoFase)&&Objects.equals(seqProjetoTarefa,t.seqProjetoTarefa)
            &&Objects.equals(lopeNrAno,t.lopeNrAno)&&Objects.equals(lopeNrMes,t.lopeNrMes)&&Objects.equals(logNrDia,t.logNrDia); }
    @Override public int hashCode(){ return Objects.hash(cdFuncionario,cdProjeto,seqProjetoFase,seqProjetoTarefa,lopeNrAno,lopeNrMes,logNrDia); }
}
