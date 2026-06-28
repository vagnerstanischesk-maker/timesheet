package com.triscal.timesheet.api.dto;

import java.math.BigDecimal;
import java.util.List;

/** Funcionário (cadastro completo). Datas em ISO (yyyy-MM-dd). nomeAprovador é só leitura. */
public record FuncionarioDTO(
        Integer cdFuncionario, String nome, String tipo, String email, String cdgUsur,
        Integer cdEmpresa, Integer aprovadorId, String nomeAprovador,
        boolean possuiBancoHoras, String status, List<Short> perfis,
        String cpf, String rg, String orgaoEmissor, String tituloEleitor, String zonaEleitoral,
        String secaoEleitoral, String nacionalidade, String nomePai, String nomeMae, String estadoCivil,
        String dataNascimento, String dataAdmissao, String dataRescisao,
        String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String cep,
        String telResidencial, String telCelular,
        String banco, String agencia, String conta, BigDecimal ultimoPagamento, String pis,
        String carteiraTrabalho, String serieCarteira, String contrato, String descricaoContrato,
        BigDecimal saldoBhe50, BigDecimal saldoBhe100,
        boolean valeTransporte, boolean planoSaude, boolean requerAprovAdm) {}
