package com.triscal.timesheet.domain.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "clifor")
public class Clifor {
    @Id @Column(name="cd_clifor") private Integer id;
    @Column(name="nm_razaosocial", nullable=false) private String razaoSocial;
    @Column(name="nm_fantasia") private String fantasia;
    @Column(name="nr_cnpj") private String cnpj;
    @Column(name="st_clifor", nullable=false) private String status = "AT";
    @Column(name="cd_filial_origem") private Integer filialOrigem;
    // Contato financeiro / site
    @Column(name="nm_contato_financeiro") private String contatoFinanceiro;
    @Column(name="ds_email_financeiro") private String emailFinanceiro;
    @Column(name="ds_site") private String site;
    // Endereço
    @Column(name="ds_logradouro") private String logradouro;
    @Column(name="nr_endereco") private String numero;
    @Column(name="ds_complemento") private String complemento;
    @Column(name="nm_cidade") private String cidade;
    @Column(name="sg_estado") private String estado;
    @Column(name="nr_cep") private String cep;
    @Column(name="dt_ult_alt") private OffsetDateTime dtUltAlt;

    protected Clifor(){}
    public Clifor(Integer id){ this.id=id; }
    public Integer getId(){return id;} public void setId(Integer v){this.id=v;}
    public String getRazaoSocial(){return razaoSocial;} public void setRazaoSocial(String v){this.razaoSocial=v;}
    public String getFantasia(){return fantasia;} public void setFantasia(String v){this.fantasia=v;}
    public String getCnpj(){return cnpj;} public void setCnpj(String v){this.cnpj=v;}
    public String getStatus(){return status;} public void setStatus(String v){this.status=v;}
    public Integer getFilialOrigem(){return filialOrigem;} public void setFilialOrigem(Integer v){this.filialOrigem=v;}
    public String getContatoFinanceiro(){return contatoFinanceiro;} public void setContatoFinanceiro(String v){this.contatoFinanceiro=v;}
    public String getEmailFinanceiro(){return emailFinanceiro;} public void setEmailFinanceiro(String v){this.emailFinanceiro=v;}
    public String getSite(){return site;} public void setSite(String v){this.site=v;}
    public String getLogradouro(){return logradouro;} public void setLogradouro(String v){this.logradouro=v;}
    public String getNumero(){return numero;} public void setNumero(String v){this.numero=v;}
    public String getComplemento(){return complemento;} public void setComplemento(String v){this.complemento=v;}
    public String getCidade(){return cidade;} public void setCidade(String v){this.cidade=v;}
    public String getEstado(){return estado;} public void setEstado(String v){this.estado=v;}
    public String getCep(){return cep;} public void setCep(String v){this.cep=v;}
    public void setDtUltAlt(OffsetDateTime v){this.dtUltAlt=v;}
}
