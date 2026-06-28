// Modo de pré-visualização: desliga o login (SSO) e devolve dados fictícios,
// para navegar por todas as telas sem backend nem login. Ative com `npm run demo`
// (mode=preview -> .env.preview com VITE_PREVIEW=true). NUNCA habilite em produção.
export const PREVIEW = import.meta.env.VITE_PREVIEW === "true";

export const FAKE_ME = {
  id: 1, nome: "Usuária Demo (pré-visualização)", email: "demo@triscal.com.br",
  papeis: ["COLABORADOR", "GESTOR", "GESTOR_PROJETO", "ADMIN_RH", "DIRETORIA"],
  cadastrado: true,
};

const hoje = new Date();
const Y = hoje.getFullYear(), M = hoje.getMonth() + 1, D = hoje.getDate();

const TAREFAS_ALOCADAS = [
  { cdProjeto: 100, projetoDescricao: "Projeto Alfa", seqFase: 1, faseDescricao: "Concepção", seqTarefa: 1, descricaoTarefa: "Backend", externa: false },
  { cdProjeto: 100, projetoDescricao: "Projeto Alfa", seqFase: 1, faseDescricao: "Concepção", seqTarefa: 2, descricaoTarefa: "Reunião", externa: false },
  { cdProjeto: 200, projetoDescricao: "Interno · Gestão", seqFase: 1, faseDescricao: "Gestão", seqTarefa: 1, descricaoTarefa: "Treinamento", externa: false },
  { cdProjeto: 0,   projetoDescricao: "Suporte", seqFase: 1, faseDescricao: "OTRS", seqTarefa: 9, descricaoTarefa: "Suporte OTRS", externa: true },
];

const DIA = {
  ano: Y, mes: M, dia: D, status: "A", ausenciaBhe: 0, ausenciaAbono: 0, feriado: false, horasNormaisEsperadas: 8,
  tarefas: [
    { cdProjeto: 100, seqFase: 1, seqTarefa: 1, descricaoTarefa: "Projeto Alfa · Concepção · Backend", horasNc: 0, horasCo: 6, horasNcExtra: 0, horasCoExtra: 1, somenteLeitura: false, obs: null },
    { cdProjeto: 100, seqFase: 1, seqTarefa: 2, descricaoTarefa: "Projeto Alfa · Concepção · Reunião", horasNc: 2, horasCo: 0, horasNcExtra: 0, horasCoExtra: 0, somenteLeitura: false, obs: null },
  ],
};

function qzDiasPeriodo() {
  const out: any[] = [];
  const push = (yy: number, mm: number, dd: number) => {
    const wd = new Date(yy, mm - 1, dd).getDay(); const fds = wd === 0 || wd === 6;
    out.push({ ano: yy, mes: mm, dia: dd, diaSemana: ["dom","seg","ter","qua","qui","sex","sáb"][wd],
      feriado: false, status: fds ? "A" : (out.length % 3 === 0 ? "F" : "P"), totalHoras: fds ? 0 : 8 });
  };
  if (D < 16) { for (let d = 1; d <= 15; d++) push(Y, M, d); }
  else { const last = new Date(Y, M, 0).getDate(); for (let d = 16; d <= last; d++) push(Y, M, d); }
  return out;
}
const QZ_DIAS = qzDiasPeriodo();
const QUINZENA = {
  nome: "Usuária Demo (pré-visualização)",
  anoRef: Y, mesRef: M, periodo: D < 16 ? 1 : 2, saldoBhe50: 12.5, saldoBhe100: 3,
  dias: QZ_DIAS,
  grade: [
    { cdProjeto: 100, projetoDescricao: "Projeto Alfa", horasPorDia: QZ_DIAS.map((d) => d.totalHoras ? 6 : 0), total: QZ_DIAS.reduce((s, d) => s + (d.totalHoras ? 6 : 0), 0) },
    { cdProjeto: 200, projetoDescricao: "Interno · Gestão", horasPorDia: QZ_DIAS.map((d) => d.totalHoras ? 2 : 0), total: QZ_DIAS.reduce((s, d) => s + (d.totalHoras ? 2 : 0), 0) },
  ],
  porTarefa: [
    { cdProjeto: 100, projetoDescricao: "Projeto Alfa", seqFase: 1, faseDescricao: "Concepção", seqTarefa: 1, tarefaDescricao: "Backend", horasCo: 96, horasNc: 0, horasCoExtra: 8, horasNcExtra: 0, total: 104 },
    { cdProjeto: 100, projetoDescricao: "Projeto Alfa", seqFase: 1, faseDescricao: "Concepção", seqTarefa: 2, tarefaDescricao: "Reunião", horasCo: 0, horasNc: 14, horasCoExtra: 0, horasNcExtra: 0, total: 14 },
  ],
};

const EDICAO = {
  nome: "Usuária Demo (pré-visualização)", anoRef: Y, mesRef: M, periodo: D < 16 ? 1 : 2, saldoBhe50: 12.5, saldoBhe100: 3,
  dias: QZ_DIAS.map((d) => ({ ano: d.ano, mes: d.mes, dia: d.dia, diaSemana: d.diaSemana, feriado: d.feriado, status: d.status, horasNormaisEsperadas: d.totalHoras ? 8 : 0, ausenciaBhe: 0, ausenciaAbono: 0 })),
  tarefas: [
    { cdProjeto: 100, projetoDescricao: "Projeto Alfa", seqFase: 1, faseDescricao: "Concepção", seqTarefa: 1, descricaoTarefa: "Backend", externa: false,
      celulas: QZ_DIAS.map((d) => ({ nc: 0, co: d.totalHoras ? 6 : 0, ncExtra: 0, coExtra: 0 })) },
    { cdProjeto: 100, projetoDescricao: "Projeto Alfa", seqFase: 1, faseDescricao: "Concepção", seqTarefa: 2, descricaoTarefa: "Reunião", externa: false,
      celulas: QZ_DIAS.map((d) => ({ nc: d.totalHoras ? 2 : 0, co: 0, ncExtra: 0, coExtra: 0 })) },
  ],
};

const RELATORIO_LOGS = [
  { cdFuncionario: 7, nome: "Carla Souza", anoRef: Y, mesRef: M, periodo: 1, status: "Aberto" },
  { cdFuncionario: 7, nome: "Carla Souza", anoRef: Y, mesRef: M, periodo: 2, status: "Rejeitado" },
  { cdFuncionario: 9, nome: "Rafael Lima", anoRef: Y, mesRef: M, periodo: 2, status: "Aberto" },
];

const RELATORIO_ACOMP = [
  { cdProjeto: 100, descricao: "Projeto Alfa", cdClifor: 1, nomeCliente: "ACME S.A.", horasVendidas: 1200, horasExecutadas: 840, percExecHoras: 70, percInformado: 62, percDiferenca: 8, diferencaHoras: 96 },
  { cdProjeto: 101, descricao: "Projeto Beta", cdClifor: 1, nomeCliente: "ACME S.A.", horasVendidas: 400, horasExecutadas: 430, percExecHoras: 107.5, percInformado: 95, percDiferenca: 12.5, diferencaHoras: 50 },
  { cdProjeto: 200, descricao: "Projeto Gama", cdClifor: 2, nomeCliente: "Globex Ltda.", horasVendidas: 800, horasExecutadas: 300, percExecHoras: 37.5, percInformado: 40, percDiferenca: -2.5, diferencaHoras: -20 },
  { cdProjeto: 900, descricao: "Melhorias internas", cdClifor: null, nomeCliente: "Projetos internos", horasVendidas: 0, horasExecutadas: 120, percExecHoras: 0, percInformado: 0, percDiferenca: 0, diferencaHoras: 0 },
];

const RELATORIO_BHE = [
  { cdFuncionario: 7, nome: "Carla Souza", data: `${Y}-${String(M).padStart(2,"0")}-05`, valor50: 2, valor100: 0, origem: "Aprovação", usuario: "gestor@triscal" },
  { cdFuncionario: 7, nome: "Carla Souza", data: `${Y}-${String(M).padStart(2,"0")}-12`, valor50: 1.5, valor100: 0, origem: "Aprovação", usuario: "gestor@triscal" },
  { cdFuncionario: 7, nome: "Carla Souza", data: `${Y}-${String(M).padStart(2,"0")}-20`, valor50: 0, valor100: 4, origem: "Manual", usuario: "rh@triscal" },
  { cdFuncionario: 9, nome: "Rafael Lima", data: `${Y}-${String(M).padStart(2,"0")}-08`, valor50: 3, valor100: 0, origem: "Aprovação", usuario: "gestor@triscal" },
];

const PENDENTES = [
  { funcionarioId: 7, nome: "Carla Souza", ano: Y, mes: M, dia: 2 },
  { funcionarioId: 7, nome: "Carla Souza", ano: Y, mes: M, dia: 3 },
  { funcionarioId: 9, nome: "Rafael Lima", ano: Y, mes: M, dia: 2 },
];

const ACOMPANHAMENTO = {
  cdProjeto: 100, descricao: "Projeto Alfa", horasPeriodo: 126, horasAcumuladas: 840, horasPrevistas: 1200,
  percExecucaoHoras: 70, ultimoPercInformado: 62,
  historico: [
    { dataReferencia: `${Y}-${String(M).padStart(2,"0")}-10`, percInformado: 48, percDerivadoHoras: 55 },
    { dataReferencia: `${Y}-${String(M).padStart(2,"0")}-25`, percInformado: 62, percDerivadoHoras: 70 },
  ],
};

const DASH = {
  ano: Y, mes: M, horasMes: 3240, percFaturavel: 68, saldoBheEquipe: 214,
  projetos: [
    { cdProjeto: 100, descricao: "Alfa", realizado: 840, previsto: 1200, percExecucao: 70 },
    { cdProjeto: 101, descricao: "Beta", realizado: 430, previsto: 500, percExecucao: 86 },
    { cdProjeto: 102, descricao: "Gama", realizado: 610, previsto: 560, percExecucao: 109 },
  ],
};

const FILIAIS = [{ id: 1, descricao: "RJ" }, { id: 2, descricao: "SP" }];
const CLIENTES = [
  { cdClifor: 1, razaoSocial: "Acme S.A.", fantasia: "Acme", cnpj: "68555473000199", status: "AT", filialOrigem: 2,
    contatoFinanceiro: "Joana Prado", emailFinanceiro: "financeiro@acme.com.br", site: "https://acme.com.br",
    logradouro: "Av. Paulista", numero: "1000", complemento: "Conj. 51", cidade: "São Paulo", estado: "SP", cep: "01310-100" },
  { cdClifor: 2, razaoSocial: "Globex Ltda", fantasia: "Globex", cnpj: "11222333000181", status: "AT", filialOrigem: 1,
    contatoFinanceiro: "Marcos Dias", emailFinanceiro: "contas@globex.com", site: "https://globex.com",
    logradouro: "Rua das Flores", numero: "245", complemento: "Sala 3", cidade: "Rio de Janeiro", estado: "RJ", cep: "20040-002" },
];
const VERTICAIS = [
  { id: 1, descricao: "Tecnologia" }, { id: 2, descricao: "Serviços Financeiros" }, { id: 3, descricao: "Saúde" },
  { id: 4, descricao: "Varejo" }, { id: 5, descricao: "Indústria" }, { id: 6, descricao: "Setor Público" },
  { id: 7, descricao: "Telecom" }, { id: 8, descricao: "Outros" },
];
const VERTICAIS_CAD = VERTICAIS.map((v) => ({ cdVertical: v.id, descricao: v.descricao, status: "AT" }));
const TIPOS_CONTRATO = [
  { id: 1, descricao: "Tempo e Material" }, { id: 2, descricao: "Escopo Fechado" }, { id: 3, descricao: "Bodyshop/Alocação" },
  { id: 4, descricao: "Sustentação" }, { id: 5, descricao: "Recorrente/SaaS" }, { id: 6, descricao: "Outros" },
];
const TIPOS_FATURAMENTO = [
  { id: "FECHADO_FASE", descricao: "Fechado por fase" }, { id: "FECHADO_MENSAL", descricao: "Fechado com faturamento mensal" },
  { id: "HH_MENSAL", descricao: "HH com faturamento mensal" }, { id: "CUSTOMIZADO", descricao: "Customizado" },
  { id: "NENHUM", descricao: "Nenhum" },
];
const GERENTES_TEC = [{ id: 1, descricao: "Usuária Demo" }, { id: 5, descricao: "Paulo Gestor" }];
const GERENTES_COM = [{ id: 8, descricao: "Renata Comercial" }];

const PROJETOS = [
  { cdProjeto: 100, descricao: "Projeto Alfa", interno: false, cdClifor: 1, nomeCliente: "Acme S.A.", cdEmpresa: 2, status: "AT",
    dataInicio: `${Y}-01-15`, dataFim: `${Y}-12-20`,
    horasPrevistas: 1200, aceitaCo: true, aceitaNc: true, custoPrevisto: 240000, tipoFaturamento: "FECHADO_FASE",
    gerenteTecnico: 1, gerenteComercial: 8, vertical: 1, tipoContrato: 2, comissao: true, observacao: "Projeto piloto.",
    horasRealizadas: 840, custoRealizado: 168000, valorPrevisto: 360000, valorRealizado: 252000 },
  { cdProjeto: 200, descricao: "Interno · Gestão", interno: true, cdClifor: null, nomeCliente: null, cdEmpresa: 1, status: "AT",
    dataInicio: "", dataFim: "",
    horasPrevistas: 300, aceitaCo: false, aceitaNc: true, custoPrevisto: 0, tipoFaturamento: "NENHUM",
    gerenteTecnico: 1, gerenteComercial: null, vertical: 8, tipoContrato: 6, comissao: false, observacao: null,
    horasRealizadas: 120, custoRealizado: 0, valorPrevisto: 0, valorRealizado: 0 },
];
const FUNCIONARIOS = [
  { cdFuncionario: 1, nome: "Usuária Demo", tipo: "F", email: "demo@triscal.com.br", cdgUsur: null, cdEmpresa: 2,
    aprovadorId: null, nomeAprovador: null, possuiBancoHoras: true, status: "AT", perfis: [1,2,3,4,5],
    cpf: "39053344705", rg: "12.345.678-9", orgaoEmissor: "SSP/SP", estadoCivil: "SOLTEIRO", cidade: "São Paulo", estado: "SP",
    saldoBhe50: 12.5, saldoBhe100: 3, valeTransporte: true, planoSaude: true, requerAprovAdm: false },
  { cdFuncionario: 7, nome: "Carla Souza", tipo: "F", email: "carla@triscal.com.br", cdgUsur: null, cdEmpresa: 1,
    aprovadorId: 1, nomeAprovador: "Usuária Demo", possuiBancoHoras: true, status: "AT", perfis: [1],
    cpf: "11144477735", rg: "98.765.432-1", orgaoEmissor: "DETRAN/RJ", estadoCivil: "CASADO", cidade: "Rio de Janeiro", estado: "RJ",
    saldoBhe50: 4, saldoBhe100: 0, valeTransporte: true, planoSaude: false, requerAprovAdm: true },
];
const PERFIS = [
  { id: 1, descricao: "COLABORADOR" }, { id: 2, descricao: "GESTOR" }, { id: 3, descricao: "GESTOR_PROJETO" },
  { id: 4, descricao: "ADMIN_RH" }, { id: 5, descricao: "DIRETORIA" }, { id: 6, descricao: "COMERCIAL" },
];
const FERIADOS = [{ id: 1, nome: "Confraternização", ano: null, mes: 1, dia: 1, tipo: "NF", filial: null, horasATrabalhar: 0, inativo: false }];
const FASES = [{ seqFase: 1, descricao: "Concepção", status: "ENC", dataInicio: `${Y}-01-15`, dataFim: `${Y}-03-30`, horasPrevistas: 200, horasRealizadas: 190, percConclusao: 100, valorPrevisto: 60000, valorRealizado: 58000, custoPrevisto: 30000, custoRealizado: 28500 }];
const TAREFAS = [{ seqFase: 1, seqTarefa: 1, descricao: "Backend", status: "INI", externa: false, dataInicio: `${Y}-02-01`, dataFim: `${Y}-03-30`, horasPrevistas: 120, horasRealizadas: 96, percConclusao: 80, valorPrevisto: 36000, valorRealizado: 28800, custoPrevisto: 18000, custoRealizado: 14400 }];
const ALOCS = [{ seqFase: 1, seqTarefa: 1, cdFuncionario: 1, nomeFuncionario: "Usuária Demo", status: "INI", taxaNormal: 150, taxaExtra: 225 }];

// Resolve uma resposta fictícia para uma rota GET. Mutações retornam vazio (sucesso).
export function mockApi<T>(path: string, init: RequestInit = {}): Promise<T> {
  const method = (init.method ?? "GET").toUpperCase();
  if (method !== "GET") return Promise.resolve(undefined as T);
  const p = path.split("?")[0];
  const r = (v: unknown) => Promise.resolve(v as T);
  if (p === "/v1/me") return r(FAKE_ME);
  if (p === "/v1/timesheet/tarefas-alocadas") return r(TAREFAS_ALOCADAS);
  if (/^\/v1\/timesheet\/dia\//.test(p)) return r(DIA);
  if (/^\/v1\/timesheet\/quinzena\/.*\/edicao$/.test(p)) return r(EDICAO);
  if (/^\/v1\/timesheet\/quinzena\//.test(p)) return r(QUINZENA);
  if (p === "/v1/relatorios/logs-nao-fechados") return r(RELATORIO_LOGS);
  if (p === "/v1/relatorios/acompanhamento-projetos") return r(RELATORIO_ACOMP);
  if (p === "/v1/relatorios/bhe") return r(RELATORIO_BHE);
  if (p === "/v1/aprovacao/pendentes") return r(PENDENTES);
  if (/^\/v1\/aprovacao\/grade\//.test(p)) return r(QUINZENA);
  if (/^\/v1\/aprovacao\/edicao\//.test(p)) return r(EDICAO);
  if (p === "/v1/projetos/lista") return r(PROJETOS.map((x) => ({ cdProjeto: x.cdProjeto, descricao: x.descricao, cdClifor: x.cdClifor, nomeCliente: x.nomeCliente, interno: x.interno })));
  if (/^\/v1\/projetos\/\d+\/acompanhamento\//.test(p)) return r(ACOMPANHAMENTO);
  if (/^\/v1\/dashboards\/resumo\//.test(p)) return r(DASH);
  if (p === "/v1/cadastros/listas/filiais") return r(FILIAIS);
  if (p === "/v1/cadastros/verticais") return r(VERTICAIS_CAD);
  if (p === "/v1/cadastros/projetos/listas/verticais") return r(VERTICAIS);
  if (p === "/v1/cadastros/projetos/listas/tipos-contrato") return r(TIPOS_CONTRATO);
  if (p === "/v1/cadastros/projetos/listas/tipos-faturamento") return r(TIPOS_FATURAMENTO);
  if (p === "/v1/cadastros/projetos/listas/gerentes-tecnicos") return r(GERENTES_TEC);
  if (p === "/v1/cadastros/projetos/listas/gerentes-comerciais") return r(GERENTES_COM);
  if (p === "/v1/cadastros/projetos") return r(PROJETOS);
  if (p === "/v1/cadastros/funcionarios/perfis") return r(PERFIS);
  if (p === "/v1/cadastros/funcionarios") return r(FUNCIONARIOS);
  if (p === "/v1/cadastros/clientes") return r(CLIENTES);
  if (p === "/v1/cadastros/feriados") return r(FERIADOS);
  if (/\/fases\/\d+\/tarefas\/\d+\/alocacoes$/.test(p)) return r(ALOCS);
  if (/\/fases\/\d+\/tarefas$/.test(p)) return r(TAREFAS);
  if (/\/fases$/.test(p)) return r(FASES);
  return r([]);
}
