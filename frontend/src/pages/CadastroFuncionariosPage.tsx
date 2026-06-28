import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useApi } from "../auth/useApi";

interface Func {
  cdFuncionario: number | null; nome: string; tipo: string; email: string; cdgUsur: string;
  cdEmpresa: number | null; aprovadorId: number | null; nomeAprovador?: string | null;
  possuiBancoHoras: boolean; status: string; perfis: number[];
  cpf: string; rg: string; orgaoEmissor: string; tituloEleitor: string; zonaEleitoral: string; secaoEleitoral: string;
  nacionalidade: string; nomePai: string; nomeMae: string; estadoCivil: string;
  dataNascimento: string; dataAdmissao: string; dataRescisao: string;
  logradouro: string; numero: string; complemento: string; bairro: string; cidade: string; estado: string; cep: string;
  telResidencial: string; telCelular: string;
  banco: string; agencia: string; conta: string; ultimoPagamento: number | null; pis: string;
  carteiraTrabalho: string; serieCarteira: string; contrato: string; descricaoContrato: string;
  saldoBhe50: number | null; saldoBhe100: number | null;
  valeTransporte: boolean; planoSaude: boolean; requerAprovAdm: boolean;
}
interface PerfilOpt { id: number; descricao: string; }
interface Lookup { id: number | string; descricao: string; }

const vazio: Func = {
  cdFuncionario: null, nome: "", tipo: "F", email: "", cdgUsur: "", cdEmpresa: null, aprovadorId: null,
  possuiBancoHoras: false, status: "AT", perfis: [],
  cpf: "", rg: "", orgaoEmissor: "", tituloEleitor: "", zonaEleitoral: "", secaoEleitoral: "",
  nacionalidade: "", nomePai: "", nomeMae: "", estadoCivil: "",
  dataNascimento: "", dataAdmissao: "", dataRescisao: "",
  logradouro: "", numero: "", complemento: "", bairro: "", cidade: "", estado: "", cep: "", telResidencial: "", telCelular: "",
  banco: "", agencia: "", conta: "", ultimoPagamento: null, pis: "",
  carteiraTrabalho: "", serieCarteira: "", contrato: "", descricaoContrato: "",
  saldoBhe50: 0, saldoBhe100: 0,
  valeTransporte: false, planoSaude: false, requerAprovAdm: false,
};
const ist = { width: "100%", padding: "6px 8px", boxSizing: "border-box" as const };
const fsStyle = { border: "1px solid var(--border)", borderRadius: 8, padding: 10 };
const lg = { fontSize: 12, color: "var(--muted)" };

export function CadastroFuncionariosPage() {
  const api = useApi();
  const qc = useQueryClient();
  const [f, setF] = useState<Func>(vazio);
  const [erro, setErro] = useState<string | null>(null);
  const [busca, setBusca] = useState("");

  const { data: lista = [] } = useQuery<Func[]>({ queryKey: ["func"], queryFn: () => api<Func[]>("/v1/cadastros/funcionarios") });
  const { data: perfis = [] } = useQuery<PerfilOpt[]>({ queryKey: ["perfis"], queryFn: () => api<PerfilOpt[]>("/v1/cadastros/funcionarios/perfis") });
  const { data: filiais = [] } = useQuery<Lookup[]>({ queryKey: ["filiais"], queryFn: () => api<Lookup[]>("/v1/cadastros/listas/filiais") });

  const salvar = useMutation({
    mutationFn: (x: Func) => api("/v1/cadastros/funcionarios", { method: "PUT", body: JSON.stringify(x) }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ["func"] }); setF(vazio); setErro(null); },
    onError: (e: Error) => setErro(e.message),
  });
  const set = (k: keyof Func, v: unknown) => setF((s) => ({ ...s, [k]: v }));
  const numOrNull = (s: string) => (s ? Number(s) : null);
  const togglePerfil = (id: number) => setF((s) => ({ ...s, perfis: s.perfis.includes(id) ? s.perfis.filter((p) => p !== id) : [...s.perfis, id] }));
  const aprovadores = lista.filter((x) => x.cdFuncionario !== f.cdFuncionario);
  const termo = busca.trim().toLowerCase();
  const filtrados = termo
    ? lista.filter((x) => `${x.cdFuncionario ?? ""} ${x.nome ?? ""} ${x.cpf ?? ""} ${x.email ?? ""}`.toLowerCase().includes(termo))
    : lista;

  return (
    <section>
      <h1 style={{ color: "var(--heading)" }}>Funcionários</h1>
      <div style={{ display: "flex", gap: 8, marginBottom: 10 }}>
        <input placeholder="Pesquisar por nome, CPF, código ou e-mail…" value={busca} onChange={(e) => setBusca(e.target.value)} style={{ flex: 1, padding: "6px 8px", maxWidth: 460 }} />
        {busca && <button type="button" onClick={() => setBusca("")}>Limpar</button>}
        <span style={{ marginLeft: "auto", fontSize: 13, color: "var(--muted)", alignSelf: "center" }}>{filtrados.length} de {lista.length}</span>
      </div>
      <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14, marginBottom: 16 }}>
        <thead><tr style={{ textAlign: "left", background: "var(--surface-2)" }}>
          <th style={{ padding: "6px 8px" }}>Cód.</th><th>Nome</th><th>Tipo</th><th>CPF</th><th>Aprovador</th><th>BHE</th><th></th></tr></thead>
        <tbody>{filtrados.map((x) => (
          <tr key={x.cdFuncionario} style={{ borderTop: "1px solid var(--border)" }}>
            <td style={{ padding: "6px 8px" }}>{x.cdFuncionario}</td><td>{x.nome}</td>
            <td>{x.tipo === "E" ? "PJ" : "CLT"}</td><td>{x.cpf ?? ""}</td><td>{x.nomeAprovador ?? ""}</td>
            <td>{x.possuiBancoHoras ? "sim" : "não"}</td>
            <td style={{ textAlign: "right" }}><button onClick={() => { setF({ ...vazio, ...x }); setErro(null); }}>Editar</button></td>
          </tr>))}
          {filtrados.length === 0 && <tr><td colSpan={7} style={{ padding: 10, color: "var(--muted)" }}>Nenhum funcionário encontrado.</td></tr>}</tbody>
      </table>

      <div style={fsStyle}>
        <strong>{f.cdFuncionario ? `Editar funcionário ${f.cdFuncionario}` : "Novo funcionário"}</strong>
        <p style={{ ...lg, margin: "4px 0 8px" }}>Código gerado automaticamente. CPF validado pela Receita Federal (obrigatório para CLT).</p>
        {erro && <p style={{ color: "var(--danger)" }}>{erro}</p>}

        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
          <fieldset style={fsStyle}>
            <legend style={lg}>Identificação</legend>
            <div style={{ display: "grid", gap: 8 }}>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 2fr", gap: 8 }}>
                <label>Código <input value={f.cdFuncionario ?? "(automático)"} readOnly disabled style={{ ...ist, background: "var(--surface-2)" }} /></label>
                <label>Nome <input value={f.nome} onChange={(e) => set("nome", e.target.value)} style={ist} /></label>
              </div>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
                <label>Tipo <select value={f.tipo} onChange={(e) => set("tipo", e.target.value)} style={ist}><option value="F">CLT</option><option value="E">PJ</option></select></label>
                <label>Filial <select value={f.cdEmpresa ?? ""} onChange={(e) => set("cdEmpresa", numOrNull(e.target.value))} style={ist}>
                  <option value="">Selecione…</option>{filiais.map((fl) => <option key={fl.id} value={fl.id as number}>{fl.descricao}</option>)}</select></label>
              </div>
              <label>E-mail corporativo (vínculo do login) <input value={f.email} onChange={(e) => set("email", e.target.value)} style={ist} /></label>
              <label>cdg_usur (opcional) <input value={f.cdgUsur} onChange={(e) => set("cdgUsur", e.target.value)} style={ist} /></label>
              <label>Aprovador (por nome)
                <select value={f.aprovadorId ?? ""} onChange={(e) => set("aprovadorId", numOrNull(e.target.value))} style={ist}>
                  <option value="">— sem aprovador —</option>
                  {aprovadores.map((a) => <option key={a.cdFuncionario} value={a.cdFuncionario as number}>{a.nome}</option>)}
                </select>
              </label>
              <label>Status <select value={f.status} onChange={(e) => set("status", e.target.value)} style={ist}><option value="AT">Ativo</option><option value="IN">Inativo</option></select></label>
              <div>
                <span style={lg}>Perfis</span>
                <div style={{ display: "flex", gap: 12, flexWrap: "wrap", marginTop: 4 }}>
                  {perfis.map((p) => (
                    <label key={p.id} style={{ fontSize: 13 }}>
                      <input type="checkbox" checked={f.perfis.includes(p.id)} onChange={() => togglePerfil(p.id)} /> {p.descricao}
                    </label>
                  ))}
                </div>
              </div>
            </div>
          </fieldset>

          <fieldset style={fsStyle}>
            <legend style={lg}>Documentos</legend>
            <div style={{ display: "grid", gap: 8 }}>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
                <label>CPF <input value={f.cpf} onChange={(e) => set("cpf", e.target.value)} placeholder="000.000.000-00" style={ist} /></label>
                <label>PIS <input value={f.pis} onChange={(e) => set("pis", e.target.value)} style={ist} /></label>
              </div>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
                <label>RG <input value={f.rg} onChange={(e) => set("rg", e.target.value)} style={ist} /></label>
                <label>Órgão emissor <input value={f.orgaoEmissor} onChange={(e) => set("orgaoEmissor", e.target.value)} style={ist} /></label>
              </div>
              <div style={{ display: "grid", gridTemplateColumns: "2fr 1fr 1fr", gap: 8 }}>
                <label>Título de eleitor <input value={f.tituloEleitor} onChange={(e) => set("tituloEleitor", e.target.value)} style={ist} /></label>
                <label>Zona <input value={f.zonaEleitoral} onChange={(e) => set("zonaEleitoral", e.target.value)} style={ist} /></label>
                <label>Seção <input value={f.secaoEleitoral} onChange={(e) => set("secaoEleitoral", e.target.value)} style={ist} /></label>
              </div>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
                <label>Carteira de trabalho <input value={f.carteiraTrabalho} onChange={(e) => set("carteiraTrabalho", e.target.value)} style={ist} /></label>
                <label>Série da carteira <input value={f.serieCarteira} onChange={(e) => set("serieCarteira", e.target.value)} style={ist} /></label>
              </div>
              <label>Nacionalidade <input value={f.nacionalidade} onChange={(e) => set("nacionalidade", e.target.value)} style={ist} /></label>
            </div>
          </fieldset>

          <fieldset style={fsStyle}>
            <legend style={lg}>Filiação e pessoais</legend>
            <div style={{ display: "grid", gap: 8 }}>
              <label>Nome do pai <input value={f.nomePai} onChange={(e) => set("nomePai", e.target.value)} style={ist} /></label>
              <label>Nome da mãe <input value={f.nomeMae} onChange={(e) => set("nomeMae", e.target.value)} style={ist} /></label>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
                <label>Estado civil
                  <select value={f.estadoCivil} onChange={(e) => set("estadoCivil", e.target.value)} style={ist}>
                    <option value="">—</option><option value="SOLTEIRO">Solteiro(a)</option><option value="CASADO">Casado(a)</option>
                    <option value="DIVORCIADO">Divorciado(a)</option><option value="VIUVO">Viúvo(a)</option>
                  </select>
                </label>
                <label>Data de nascimento <input type="date" value={f.dataNascimento} onChange={(e) => set("dataNascimento", e.target.value)} style={ist} /></label>
              </div>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
                <label>Data de admissão <input type="date" value={f.dataAdmissao} onChange={(e) => set("dataAdmissao", e.target.value)} style={ist} /></label>
                <label>Data de rescisão <input type="date" value={f.dataRescisao} onChange={(e) => set("dataRescisao", e.target.value)} style={ist} /></label>
              </div>
            </div>
          </fieldset>

          <fieldset style={fsStyle}>
            <legend style={lg}>Endereço e contato</legend>
            <div style={{ display: "grid", gap: 8 }}>
              <div style={{ display: "grid", gridTemplateColumns: "2fr 1fr", gap: 8 }}>
                <label>Rua/Logradouro <input value={f.logradouro} onChange={(e) => set("logradouro", e.target.value)} style={ist} /></label>
                <label>Número <input value={f.numero} onChange={(e) => set("numero", e.target.value)} style={ist} /></label>
              </div>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
                <label>Complemento <input value={f.complemento} onChange={(e) => set("complemento", e.target.value)} style={ist} /></label>
                <label>Bairro <input value={f.bairro} onChange={(e) => set("bairro", e.target.value)} style={ist} /></label>
              </div>
              <div style={{ display: "grid", gridTemplateColumns: "2fr 0.6fr 1fr", gap: 8 }}>
                <label>Cidade <input value={f.cidade} onChange={(e) => set("cidade", e.target.value)} style={ist} /></label>
                <label>UF <input maxLength={2} value={f.estado} onChange={(e) => set("estado", e.target.value.toUpperCase())} style={ist} /></label>
                <label>CEP <input value={f.cep} onChange={(e) => set("cep", e.target.value)} style={ist} /></label>
              </div>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
                <label>Tel. residencial <input value={f.telResidencial} onChange={(e) => set("telResidencial", e.target.value)} style={ist} /></label>
                <label>Celular <input value={f.telCelular} onChange={(e) => set("telCelular", e.target.value)} style={ist} /></label>
              </div>
            </div>
          </fieldset>

          <fieldset style={fsStyle}>
            <legend style={lg}>Dados bancários e pagamento</legend>
            <div style={{ display: "grid", gap: 8 }}>
              <label>Banco <input value={f.banco} onChange={(e) => set("banco", e.target.value)} style={ist} /></label>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
                <label>Agência e dígito <input value={f.agencia} onChange={(e) => set("agencia", e.target.value)} style={ist} /></label>
                <label>Conta e dígito <input value={f.conta} onChange={(e) => set("conta", e.target.value)} style={ist} /></label>
              </div>
              <label>Valor do último pagamento (R$) <input type="number" step="0.01" value={f.ultimoPagamento ?? ""} onChange={(e) => set("ultimoPagamento", numOrNull(e.target.value))} style={ist} /></label>
            </div>
          </fieldset>

          <fieldset style={fsStyle}>
            <legend style={lg}>Contrato, banco de horas e benefícios</legend>
            <div style={{ display: "grid", gap: 8 }}>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 2fr", gap: 8 }}>
                <label>Nº do contrato <input value={f.contrato} onChange={(e) => set("contrato", e.target.value)} style={ist} /></label>
                <label>Descrição do contrato <input value={f.descricaoContrato} onChange={(e) => set("descricaoContrato", e.target.value)} style={ist} /></label>
              </div>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
                <label>Saldo BHE 50% <input type="number" step="0.5" value={f.saldoBhe50 ?? ""} onChange={(e) => set("saldoBhe50", numOrNull(e.target.value))} style={ist} /></label>
                <label>Saldo BHE 100% <input type="number" step="0.5" value={f.saldoBhe100 ?? ""} onChange={(e) => set("saldoBhe100", numOrNull(e.target.value))} style={ist} /></label>
              </div>
              <div style={{ display: "flex", gap: 16, flexWrap: "wrap" }}>
                <label><input type="checkbox" checked={f.possuiBancoHoras} onChange={(e) => set("possuiBancoHoras", e.target.checked)} /> Possui banco de horas</label>
                <label><input type="checkbox" checked={f.valeTransporte} onChange={(e) => set("valeTransporte", e.target.checked)} /> Recebe vale-transporte</label>
                <label><input type="checkbox" checked={f.planoSaude} onChange={(e) => set("planoSaude", e.target.checked)} /> Possui plano de saúde</label>
                <label><input type="checkbox" checked={f.requerAprovAdm} onChange={(e) => set("requerAprovAdm", e.target.checked)} /> Requer aprovação administrativa</label>
              </div>
            </div>
          </fieldset>
        </div>

        <div style={{ display: "flex", gap: 8, marginTop: 12 }}>
          <button onClick={() => { setF(vazio); setErro(null); }}>Limpar</button>
          <button onClick={() => salvar.mutate(f)} style={{ background: "var(--brand-orange)", color: "#fff", border: "none", borderRadius: 8, padding: "8px 14px" }}>Salvar</button>
        </div>
      </div>
    </section>
  );
}
