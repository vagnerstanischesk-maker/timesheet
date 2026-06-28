package com.triscal.timesheet.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "projeto")
public class Projeto {
    @Id @Column(name="cd_projeto") private Integer id;
    @Column(name="ds_projeto") private String descricao;
    @Column(name="in_interno") private char interno = 'N';
    @Column(name="cd_clifor") private Integer cdClifor;
    @Column(name="cd_empresa") private Integer cdEmpresa;
    @Column(name="st_projeto") private String status = "AT";
    @Column(name="dt_inicio_prev") private LocalDate dataInicio;
    @Column(name="dt_fim_previsto") private LocalDate dataFim;
    @Column(name="aceita_horas_co") private char aceitaCo = 'S';
    @Column(name="aceita_horas_nc") private char aceitaNc = 'S';
    @Column(name="cd_tipo_faturamento") private String tipoFaturamento;
    @Column(name="cd_gerente_tecnico") private Integer gerenteTecnico;
    @Column(name="cd_gerente_comercial") private Integer gerenteComercial;
    @Column(name="cd_vertical") private Short vertical;
    @Column(name="cd_tipo_contrato") private Short tipoContrato;
    @Column(name="in_comissao") private char comissao = 'N';
    @Column(name="ds_observacao") private String observacao;
    // Totais — somatórios das fases (somente leitura no cadastro; mantidos ao salvar fase)
    @Column(name="nr_horasprevistas") private BigDecimal horasPrevistas;
    @Column(name="nr_horasreal") private BigDecimal horasRealizadas;
    @Column(name="vl_previsto") private BigDecimal valorPrevisto;
    @Column(name="vl_realizado") private BigDecimal valorRealizado;
    @Column(name="vl_custo_previsto") private BigDecimal custoPrevisto;
    @Column(name="vl_custo_realizado") private BigDecimal custoRealizado;
    @Column(name="dt_ult_alt") private OffsetDateTime dtUltAlt;

    protected Projeto(){}
    public Projeto(Integer id){ this.id = id; }

    public Integer getId(){return id;}
    public void setId(Integer id){this.id=id;}
    public String getDescricao(){return descricao;} public void setDescricao(String v){this.descricao=v;}
    public char getInterno(){return interno;} public void setInterno(char v){this.interno=v;}
    public Integer getCdClifor(){return cdClifor;} public void setCdClifor(Integer v){this.cdClifor=v;}
    public Integer getCdEmpresa(){return cdEmpresa;} public void setCdEmpresa(Integer v){this.cdEmpresa=v;}
    public String getStatus(){return status;} public void setStatus(String v){this.status=v;}
    public LocalDate getDataInicio(){return dataInicio;} public void setDataInicio(LocalDate v){this.dataInicio=v;}
    public LocalDate getDataFim(){return dataFim;} public void setDataFim(LocalDate v){this.dataFim=v;}
    public char getAceitaCo(){return aceitaCo;} public void setAceitaCo(char v){this.aceitaCo=v;}
    public char getAceitaNc(){return aceitaNc;} public void setAceitaNc(char v){this.aceitaNc=v;}
    public String getTipoFaturamento(){return tipoFaturamento;} public void setTipoFaturamento(String v){this.tipoFaturamento=v;}
    public Integer getGerenteTecnico(){return gerenteTecnico;} public void setGerenteTecnico(Integer v){this.gerenteTecnico=v;}
    public Integer getGerenteComercial(){return gerenteComercial;} public void setGerenteComercial(Integer v){this.gerenteComercial=v;}
    public Short getVertical(){return vertical;} public void setVertical(Short v){this.vertical=v;}
    public Short getTipoContrato(){return tipoContrato;} public void setTipoContrato(Short v){this.tipoContrato=v;}
    public char getComissao(){return comissao;} public void setComissao(char v){this.comissao=v;}
    public String getObservacao(){return observacao;} public void setObservacao(String v){this.observacao=v;}
    public BigDecimal getHorasPrevistas(){return horasPrevistas;} public void setHorasPrevistas(BigDecimal v){this.horasPrevistas=v;}
    public BigDecimal getHorasRealizadas(){return horasRealizadas;} public void setHorasRealizadas(BigDecimal v){this.horasRealizadas=v;}
    public BigDecimal getValorPrevisto(){return valorPrevisto;} public void setValorPrevisto(BigDecimal v){this.valorPrevisto=v;}
    public BigDecimal getValorRealizado(){return valorRealizado;} public void setValorRealizado(BigDecimal v){this.valorRealizado=v;}
    public BigDecimal getCustoPrevisto(){return custoPrevisto;} public void setCustoPrevisto(BigDecimal v){this.custoPrevisto=v;}
    public BigDecimal getCustoRealizado(){return custoRealizado;} public void setCustoRealizado(BigDecimal v){this.custoRealizado=v;}
    public void setDtUltAlt(OffsetDateTime v){this.dtUltAlt=v;}
}
