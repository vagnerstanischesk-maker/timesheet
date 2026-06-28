import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useApi } from "../auth/useApi";

interface Cliente {
  cdClifor: number | null; razaoSocial: string; fantasia: string; cnpj: string; status: string; filialOrigem: number | null;
  contatoFinanceiro: string; emailFinanceiro: string; site: string;
  logradouro: string; numero: string; complemento: string; cidade: string; estado: string; cep: string;
}
interface Lookup { id: number | string; descricao: string; }
const vazio: Cliente = {
  cdClifor: null, razaoSocial: "", fantasia: "", cnpj: "", status: "AT", filialOrigem: null,
  contatoFinanceiro: "", emailFinanceiro: "", site: "",
  logradouro: "", numero: "", complemento: "", cidade: "", estado: "", cep: "",
};
const inputStyle = { width: "100%", padding: "6px 8px", boxSizing: "border-box" as const };

export function CadastroClientesPage() {
  const api = useApi();
  const qc = useQueryClient();
  const [c, setC] = useState<Cliente>(vazio);
  const [erro, setErro] = useState<string | null>(null);
  const [busca, setBusca] = useState("");
  const [q, setQ] = useState("");

  const { data: lista = [] } = useQuery<Cliente[]>({
    queryKey: ["cli", q],
    queryFn: () => api<Cliente[]>(`/v1/cadastros/clientes${q ? `?q=${encodeURIComponent(q)}` : ""}`),
  });
  const { data: filiais = [] } = useQuery<Lookup[]>({ queryKey: ["filiais"], queryFn: () => api<Lookup[]>("/v1/cadastros/listas/filiais") });
  const salvar = useMutation({
    mutationFn: (x: Cliente) => api("/v1/cadastros/clientes", { method: "PUT", body: JSON.stringify(x) }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ["cli"] }); setC(vazio); setErro(null); },
    onError: (e: Error) => setErro(e.message),
  });
  const set = (k: keyof Cliente, v: unknown) => setC((s) => ({ ...s, [k]: v }));
  const numOrNull = (s: string) => (s ? Number(s) : null);

  return (
    <section style={{ display: "grid", gridTemplateColumns: "1.1fr 1fr", gap: 16 }}>
      <div>
        <h1 style={{ color: "var(--heading)" }}>Clientes</h1>
        <form onSubmit={(e) => { e.preventDefault(); setQ(busca.trim()); }} style={{ display: "flex", gap: 8, marginBottom: 10 }}>
          <input placeholder="Pesquisar por nome, CNPJ ou cidade…" value={busca} onChange={(e) => setBusca(e.target.value)} style={{ flex: 1, padding: "6px 8px" }} />
          <button type="submit">Pesquisar</button>
          {q && <button type="button" onClick={() => { setBusca(""); setQ(""); }}>Limpar</button>}
        </form>
        <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14 }}>
          <thead><tr style={{ textAlign: "left", background: "var(--surface-2)" }}>
            <th style={{ padding: "6px 8px" }}>Cód.</th><th>Razão social</th><th>Cidade/UF</th><th>Status</th><th></th>
          </tr></thead>
          <tbody>{lista.map((x) => (
            <tr key={x.cdClifor} style={{ borderTop: "1px solid var(--border)" }}>
              <td style={{ padding: "6px 8px" }}>{x.cdClifor}</td><td>{x.razaoSocial}</td>
              <td>{x.cidade}{x.estado ? `/${x.estado}` : ""}</td><td>{x.status}</td>
              <td style={{ textAlign: "right" }}><button onClick={() => { setC({ ...vazio, ...x }); setErro(null); }}>Editar</button></td>
            </tr>))}
            {lista.length === 0 && <tr><td colSpan={5} style={{ padding: 10, color: "var(--muted)" }}>Nenhum cliente encontrado.</td></tr>}
          </tbody>
        </table>
      </div>

      <div style={{ border: "1px solid var(--border)", borderRadius: 12, padding: 14 }}>
        <strong>{c.cdClifor ? `Editar cliente ${c.cdClifor}` : "Novo cliente"}</strong>
        <p style={{ fontSize: 12, color: "var(--muted)", margin: "4px 0 8px" }}>Código gerado automaticamente. Todos os campos são obrigatórios.</p>
        {erro && <p style={{ color: "var(--danger)" }}>{erro}</p>}
        <div style={{ display: "grid", gap: 8 }}>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 2fr", gap: 8 }}>
            <label>Código <input value={c.cdClifor ?? "(automático)"} readOnly disabled style={{ ...inputStyle, background: "var(--surface-2)" }} /></label>
            <label>Razão social <input value={c.razaoSocial} onChange={(e) => set("razaoSocial", e.target.value)} style={inputStyle} /></label>
          </div>
          <div style={{ display: "grid", gridTemplateColumns: "2fr 1.5fr 1fr", gap: 8 }}>
            <label>Nome fantasia <input value={c.fantasia} onChange={(e) => set("fantasia", e.target.value)} style={inputStyle} /></label>
            <label>CNPJ <input value={c.cnpj} onChange={(e) => set("cnpj", e.target.value)} style={inputStyle} /></label>
            <label>Filial
              <select value={c.filialOrigem ?? ""} onChange={(e) => set("filialOrigem", numOrNull(e.target.value))} style={inputStyle}>
                <option value="">Selecione…</option>
                {filiais.map((fl) => <option key={fl.id} value={fl.id as number}>{fl.descricao}</option>)}
              </select>
            </label>
          </div>
          <fieldset style={{ border: "1px solid var(--border)", borderRadius: 8, padding: 10 }}>
            <legend style={{ fontSize: 12, color: "var(--muted)" }}>Contato financeiro</legend>
            <div style={{ display: "grid", gap: 8 }}>
              <label>Nome do contato <input value={c.contatoFinanceiro} onChange={(e) => set("contatoFinanceiro", e.target.value)} style={inputStyle} /></label>
              <label>E-mail do contato <input type="email" value={c.emailFinanceiro} onChange={(e) => set("emailFinanceiro", e.target.value)} style={inputStyle} /></label>
              <label>Site <input value={c.site} onChange={(e) => set("site", e.target.value)} placeholder="https://" style={inputStyle} /></label>
            </div>
          </fieldset>
          <fieldset style={{ border: "1px solid var(--border)", borderRadius: 8, padding: 10 }}>
            <legend style={{ fontSize: 12, color: "var(--muted)" }}>Endereço</legend>
            <div style={{ display: "grid", gap: 8 }}>
              <div style={{ display: "grid", gridTemplateColumns: "2fr 1fr", gap: 8 }}>
                <label>Rua/Logradouro <input value={c.logradouro} onChange={(e) => set("logradouro", e.target.value)} style={inputStyle} /></label>
                <label>Número <input value={c.numero} onChange={(e) => set("numero", e.target.value)} style={inputStyle} /></label>
              </div>
              <label>Complemento <input value={c.complemento} onChange={(e) => set("complemento", e.target.value)} style={inputStyle} /></label>
              <div style={{ display: "grid", gridTemplateColumns: "2fr 0.7fr 1fr", gap: 8 }}>
                <label>Cidade <input value={c.cidade} onChange={(e) => set("cidade", e.target.value)} style={inputStyle} /></label>
                <label>UF <input maxLength={2} value={c.estado} onChange={(e) => set("estado", e.target.value.toUpperCase())} style={inputStyle} /></label>
                <label>CEP <input value={c.cep} onChange={(e) => set("cep", e.target.value)} placeholder="00000-000" style={inputStyle} /></label>
              </div>
            </div>
          </fieldset>
          <label>Status <select value={c.status} onChange={(e) => set("status", e.target.value)}><option value="AT">Ativo</option><option value="IN">Inativo</option></select></label>
          <div style={{ display: "flex", gap: 8 }}>
            <button onClick={() => { setC(vazio); setErro(null); }}>Limpar</button>
            <button onClick={() => salvar.mutate(c)} style={{ background: "var(--brand-orange)", color: "#fff", border: "none", borderRadius: 8, padding: "8px 14px" }}>Salvar</button>
          </div>
        </div>
      </div>
    </section>
  );
}
