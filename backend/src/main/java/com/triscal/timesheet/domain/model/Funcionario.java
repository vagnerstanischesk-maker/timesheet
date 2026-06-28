package com.triscal.timesheet.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "funcionario")
public class Funcionario {
    @Id @Column(name = "cd_funcionario")
    private Integer id;

    @Column(name = "nm_funcionario", nullable = false) private String nome;
    @Column(name = "tp_funcionario", nullable = false) private char tipo;     // E=PJ, F=CLT
    @Column(name = "ds_email") private String email;
    @Column(name = "cdg_usur") private String cdgUsur;
    @Column(name = "cd_empresa") private Integer cdEmpresa;                   // filial
    @Column(name = "func_cd_func_aprovador") private Integer aprovadorId;
    @Column(name = "in_possui_banco_horas", nullable = false) private char possuiBancoHoras = 'N';
    @Column(name = "func_nr_saldo_bhe50", nullable = false) private BigDecimal saldoBhe50 = BigDecimal.ZERO;
    @Column(name = "func_nr_saldo_bhe100", nullable = false) private BigDecimal saldoBhe100 = BigDecimal.ZERO;
    @Column(name = "st_funcionario") private String status = "AT";

    @Column(name="nr_cpf") private String cpf;
    @Column(name="nr_rg") private String rg;
    @Column(name="ds_orgao_emissor") private String orgaoEmissor;
    @Column(name="nr_titulo_eleitor") private String tituloEleitor;
    @Column(name="nr_zona_eleitoral") private String zonaEleitoral;
    @Column(name="nr_secao_eleitoral") private String secaoEleitoral;
    @Column(name="ds_nacionalidade") private String nacionalidade;
    @Column(name="nm_pai") private String nomePai;
    @Column(name="nm_mae") private String nomeMae;
    @Column(name="tp_estado_civil") private String estadoCivil;
    @Column(name="dt_nascimento") private LocalDate dataNascimento;
    @Column(name="dt_admissao") private LocalDate dataAdmissao;
    @Column(name="dt_rescisao") private LocalDate dataRescisao;

    @Column(name="ds_logradouro") private String logradouro;
    @Column(name="nr_endereco") private String numero;
    @Column(name="ds_complemento") private String complemento;
    @Column(name="ds_bairro") private String bairro;
    @Column(name="nm_cidade") private String cidade;
    @Column(name="sg_estado") private String estado;
    @Column(name="nr_cep") private String cep;
    @Column(name="nr_tel_residencial") private String telResidencial;
    @Column(name="nr_tel_celular") private String telCelular;

    @Column(name="ds_banco") private String banco;
    @Column(name="nr_agencia") private String agencia;
    @Column(name="nr_conta") private String conta;
    @Column(name="vl_ultimo_pagamento") private BigDecimal ultimoPagamento;
    @Column(name="nr_pis") private String pis;

    @Column(name="nr_carteira_trabalho") private String carteiraTrabalho;
    @Column(name="nr_serie_carteira") private String serieCarteira;
    @Column(name="nr_contrato") private String contrato;
    @Column(name="ds_contrato") private String descricaoContrato;

    @Column(name="in_vale_transporte", nullable=false) private char valeTransporte = 'N';
    @Column(name="in_plano_saude", nullable=false) private char planoSaude = 'N';
    @Column(name="in_requer_aprov_adm", nullable=false) private char requerAprovAdm = 'N';

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "funcionario_perfil",
        joinColumns = @JoinColumn(name = "cd_funcionario"),
        inverseJoinColumns = @JoinColumn(name = "cd_perfil"))
    private Set<Perfil> perfis;

    protected Funcionario() {}
    public Funcionario(Integer id){ this.id = id; }

    public Integer getId(){return id;}
    public void setId(Integer v){this.id=v;}
    public String getNome(){return nome;} public void setNome(String v){this.nome=v;}
    public char getTipo(){return tipo;} public void setTipo(char v){this.tipo=v;}
    public String getEmail(){return email;} public void setEmail(String v){this.email=v;}
    public String getCdgUsur(){return cdgUsur;} public void setCdgUsur(String v){this.cdgUsur=v;}
    public Integer getCdEmpresa(){return cdEmpresa;} public void setCdEmpresa(Integer v){this.cdEmpresa=v;}
    public Integer getAprovadorId(){return aprovadorId;} public void setAprovadorId(Integer v){this.aprovadorId=v;}
    public boolean possuiBancoHoras(){return possuiBancoHoras == 'S';}
    public void setPossuiBancoHoras(char v){this.possuiBancoHoras=v;}
    public BigDecimal getSaldoBhe50(){return saldoBhe50;}
    public void setSaldoBhe50(BigDecimal v){this.saldoBhe50 = v == null ? BigDecimal.ZERO : v;}
    public BigDecimal getSaldoBhe100(){return saldoBhe100;}
    public void setSaldoBhe100(BigDecimal v){this.saldoBhe100 = v == null ? BigDecimal.ZERO : v;}
    public void creditarBhe50(BigDecimal v){ this.saldoBhe50 = this.saldoBhe50.add(v); }
    public void creditarBhe100(BigDecimal v){ this.saldoBhe100 = this.saldoBhe100.add(v); }
    public String getStatus(){return status;} public void setStatus(String v){this.status=v;}
    public Set<Perfil> getPerfis(){return perfis;} public void setPerfis(Set<Perfil> p){this.perfis=p;}

    public String getCpf(){return cpf;} public void setCpf(String v){this.cpf=v;}
    public String getRg(){return rg;} public void setRg(String v){this.rg=v;}
    public String getOrgaoEmissor(){return orgaoEmissor;} public void setOrgaoEmissor(String v){this.orgaoEmissor=v;}
    public String getTituloEleitor(){return tituloEleitor;} public void setTituloEleitor(String v){this.tituloEleitor=v;}
    public String getZonaEleitoral(){return zonaEleitoral;} public void setZonaEleitoral(String v){this.zonaEleitoral=v;}
    public String getSecaoEleitoral(){return secaoEleitoral;} public void setSecaoEleitoral(String v){this.secaoEleitoral=v;}
    public String getNacionalidade(){return nacionalidade;} public void setNacionalidade(String v){this.nacionalidade=v;}
    public String getNomePai(){return nomePai;} public void setNomePai(String v){this.nomePai=v;}
    public String getNomeMae(){return nomeMae;} public void setNomeMae(String v){this.nomeMae=v;}
    public String getEstadoCivil(){return estadoCivil;} public void setEstadoCivil(String v){this.estadoCivil=v;}
    public LocalDate getDataNascimento(){return dataNascimento;} public void setDataNascimento(LocalDate v){this.dataNascimento=v;}
    public LocalDate getDataAdmissao(){return dataAdmissao;} public void setDataAdmissao(LocalDate v){this.dataAdmissao=v;}
    public LocalDate getDataRescisao(){return dataRescisao;} public void setDataRescisao(LocalDate v){this.dataRescisao=v;}
    public String getLogradouro(){return logradouro;} public void setLogradouro(String v){this.logradouro=v;}
    public String getNumero(){return numero;} public void setNumero(String v){this.numero=v;}
    public String getComplemento(){return complemento;} public void setComplemento(String v){this.complemento=v;}
    public String getBairro(){return bairro;} public void setBairro(String v){this.bairro=v;}
    public String getCidade(){return cidade;} public void setCidade(String v){this.cidade=v;}
    public String getEstado(){return estado;} public void setEstado(String v){this.estado=v;}
    public String getCep(){return cep;} public void setCep(String v){this.cep=v;}
    public String getTelResidencial(){return telResidencial;} public void setTelResidencial(String v){this.telResidencial=v;}
    public String getTelCelular(){return telCelular;} public void setTelCelular(String v){this.telCelular=v;}
    public String getBanco(){return banco;} public void setBanco(String v){this.banco=v;}
    public String getAgencia(){return agencia;} public void setAgencia(String v){this.agencia=v;}
    public String getConta(){return conta;} public void setConta(String v){this.conta=v;}
    public BigDecimal getUltimoPagamento(){return ultimoPagamento;} public void setUltimoPagamento(BigDecimal v){this.ultimoPagamento=v;}
    public String getPis(){return pis;} public void setPis(String v){this.pis=v;}
    public String getCarteiraTrabalho(){return carteiraTrabalho;} public void setCarteiraTrabalho(String v){this.carteiraTrabalho=v;}
    public String getSerieCarteira(){return serieCarteira;} public void setSerieCarteira(String v){this.serieCarteira=v;}
    public String getContrato(){return contrato;} public void setContrato(String v){this.contrato=v;}
    public String getDescricaoContrato(){return descricaoContrato;} public void setDescricaoContrato(String v){this.descricaoContrato=v;}
    public boolean recebeValeTransporte(){return valeTransporte=='S';} public void setValeTransporte(char v){this.valeTransporte=v;}
    public boolean possuiPlanoSaude(){return planoSaude=='S';} public void setPlanoSaude(char v){this.planoSaude=v;}
    public boolean requerAprovAdm(){return requerAprovAdm=='S';} public void setRequerAprovAdm(char v){this.requerAprovAdm=v;}
}
