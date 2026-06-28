import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useApi } from "../auth/useApi";

interface Projeto {
  cdProjeto: number | null; descricao: string; interno: boolean; cdClifor: number | null; nomeCliente?: string | null;
  cdEmpresa: number | null; status: string; dataInicio: string; dataFim: string;
  aceitaCo: boolean; aceitaNc: boolean;
  tipoFaturamento: string; gerenteTecnico: number | null; gerenteComercial: number | null;
  vertical: number | null; tipoContrato: number | null; comissao: boolean; observacao: string;
  // totais (somente leitura — somados das fases)
  horasPrevistas?: number | null; horasRealizadas?: number | null;
  valorPrevisto?: number | null; valorRealizado?: number | null;
  custoPrevisto?: number | null; custoRealizado?: number | null;
}
interface Lookup { id: number | string; descricao: string; }
interface Cliente { cdClifor: number; razaoSocial: string; }

const vazio: Projeto = {
  cdProjeto: null, descricao: "", interno: false, cdClifor: null, cdEmpresa: null, status: "AT", dataInicio: "", dataFim: "",
  aceitaCo: true, aceitaNc: true, tipoFaturamento: "NENHUM", gerenteTecnico: null,
  gerenteComercial: null, vertical: null, tipoContrato: null, comissao: false, observacao: "",
};
const inputStyle = { width: "100%", padding: "6px 8px", boxSizing: "border-box" as const };
const brl = (v: number | null | undefined) => v == null ? "—" : v.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
const num = (v: number | null | undefined) => v == null ? "—" : v.toLocaleString("pt-BR");

export function CadastroProjetosPage() {
  const api = useApi();
  const qc = useQueryClient();
  const [erro, setErro] = useState<string | null>(null);
  const [form, setForm] = useState<Projeto>(vazio);
  const [busca, setBusca] = useState("");
  const [q, setQ] = useState("");
  const [cliFiltro, setCliFiltro] = useState<string>("");

  const { data: lista = [] } = useQuery<Projeto[]>({
    queryKey: ["cad-projetos", q, cliFiltro],
    queryFn: () => {
      const p = new URLSearchParams();
      if (q) p.set("q", q);
      if (cliFiltro) p.set("cliente", cliFiltro);
      const qs = p.toString();
      return api<Projeto[]>(`/v1/cadastros/projetos${qs ? `?${qs}` : ""}`);
    },
  });
  const { data: clientes = [] } = useQuery<Cliente[]>({ queryKey: ["cli-lista"], queryFn: () => api<Cliente[]>("/v1/cadastros/clientes") });
  const { data: filiais = [] } = useQuery<Lookup[]>({ queryKey: ["filiais"], queryFn: () => api<Lookup[]>("/v1/cadastros/listas/filiais") });
  const { data: verticais = [] } = useQuery<Lookup[]>({ queryKey: ["lk-vert"], queryFn: () => api<Lookup[]>("/v1/cadastros/projetos/listas/verticais") });
  const { data: tiposContrato = [] } = useQuery<Lookup[]>({ queryKey: ["lk-tc"], queryFn: () => api<Lookup[]>("/v1/cadastros/projetos/listas/tipos-contrato") });
  const { data: tiposFat = [] } = useQuery<Lookup[]>({ queryKey: ["lk-tf"], queryFn: () => api<Lookup[]>("/v1/cadastros/projetos/listas/tipos-faturamento") });
  const { data: gerTec = [] } = useQuery<Lookup[]>({ queryKey: ["lk-gt"], queryFn: () => api<Lookup[]>("/v1/cadastros/projetos/listas/gerentes-tecnicos") });
  const { data: gerCom = [] } = useQuery<Lookup[]>({ queryKey: ["lk-gc"], queryFn: () => api<Lookup[]>("/v1/cadastros/projetos/listas/gerentes-comerciais") });

  const salvar = useMutation({
    mutationFn: (p: Projeto) => api("/v1/cadastros/projetos", { method: "PUT", body: JSON.stringify(p) }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ["cad-projetos"] }); setForm(vazio); setErro(null); },
    onError: (e: Error) => setErro(e.message),
  });
  const set = (k: keyof Projeto, v: unknown) => setForm((f) => ({ ...f, [k]: v }));
  const numOrNull = (s: string) => (s ? Number(s) : null);

  const metric = (label: string, value: string) => (
    <div><span style={{ color: "var(--muted)", fontSize: 12 }}>{label}</span><br /><strong>{value}</strong></div>
  );

  return (
    <section style={{ display: "grid", gridTemplateColumns: "1fr 1.1fr", gap: 16 }}>
      <div>
        <h1 style={{ color: "var(--heading)" }}>Cadastro de projetos</h1>
        <form onSubmit={(e) => { e.preventDefault(); setQ(busca.trim()); }} style={{ display: "flex", gap: 8, marginBottom: 6 }}>
          <input placeholder="Pesquisar por descrição ou código…" value={busca} onChange={(e) => setBusca(e.target.value)} style={{ flex: 1, padding: "6px 8px" }} />
          <button type="submit">Pesquisar</button>
        </form>
        <div style={{ display: "flex", gap: 8, alignItems: "center", marginBottom: 10 }}>
          <label style={{ fontSize: 13, color: "var(--muted)" }}>Cliente:</label>
          <select value={cliFiltro} onChange={(e) => setCliFiltro(e.target.value)} style={{ flex: 1, padding: "6px 8px" }}>
            <option value="">Todos</option>
            {clientes.map((c) => <option key={c.cdClifor} value={c.cdClifor}>{c.razaoSocial}</option>)}
          </select>
          {(q || cliFiltro) && <button type="button" onClick={() => { setBusca(""); setQ(""); setCliFiltro(""); }}>Limpar</button>}
        </div>
        <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14 }}>
          <thead><tr style={{ textAlign: "left", background: "var(--surface-2)" }}>
            <th style={{ padding: "6px 8px" }}>Cód.</th><th>Descrição</th><th>Cliente</th><th>Status</th><th></th>
          </tr></thead>
          <tbody>
            {lista.map((p) => (
              <tr key={p.cdProjeto} style={{ borderTop: "1px solid var(--border)" }}>
                <td style={{ padding: "6px 8px" }}>{p.cdProjeto}</td>
                <td>{p.descricao}</td>
                <td>{p.nomeCliente ?? (p.interno ? "Interno" : "—")}</td>
                <td>{p.status}</td>
                <td style={{ textAlign: "right" }}><button onClick={() => { setForm({ ...vazio, ...p, observacao: p.observacao ?? "", dataInicio: p.dataInicio ?? "", dataFim: p.dataFim ?? "", tipoFaturamento: p.tipoFaturamento ?? "NENHUM" }); setErro(null); }}>Editar</button></td>
              </tr>
            ))}
            {lista.length === 0 && <tr><td colSpan={5} style={{ padding: 10, color: "var(--muted)" }}>Nenhum projeto encontrado.</td></tr>}
          </tbody>
        </table>
      </div>

      <div style={{ border: "1px solid var(--border)", borderRadius: 12, padding: 14 }}>
        <strong>{form.cdProjeto ? `Editar projeto ${form.cdProjeto}` : "Novo projeto"}</strong>
        <p style={{ fontSize: 12, color: "var(--muted)", margin: "4px 0 8px" }}>Código gerado automaticamente. Os totais (horas, valor e custo) são somados das fases.</p>
        {erro && <p style={{ color: "var(--danger)" }}>{erro}</p>}
        <div style={{ display: "grid", gap: 8 }}>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 2fr", gap: 8 }}>
            <label>Código <input value={form.cdProjeto ?? "(automático)"} readOnly disabled style={{ ...inputStyle, background: "var(--surface-2)" }} /></label>
            <label>Descrição <input value={form.descricao} onChange={(e) => set("descricao", e.target.value)} style={inputStyle} /></label>
          </div>
          <label>Cliente
            <select value={form.cdClifor ?? ""} onChange={(e) => set("cdClifor", numOrNull(e.target.value))} disabled={form.interno} style={inputStyle}>
              <option value="">{form.interno ? "(interno)" : "Selecione…"}</option>
              {clientes.map((c) => <option key={c.cdClifor} value={c.cdClifor}>{c.razaoSocial}</option>)}
            </select>
          </label>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
            <label>Vertical
              <select value={form.vertical ?? ""} onChange={(e) => set("vertical", numOrNull(e.target.value))} style={inputStyle}>
                <option value="">Selecione…</option>
                {verticais.map((v) => <option key={v.id} value={v.id as number}>{v.descricao}</option>)}
              </select>
            </label>
            <label>Tipo de contrato
              <select value={form.tipoContrato ?? ""} onChange={(e) => set("tipoContrato", numOrNull(e.target.value))} style={inputStyle}>
                <option value="">Selecione…</option>
                {tiposContrato.map((t) => <option key={t.id} value={t.id as number}>{t.descricao}</option>)}
              </select>
            </label>
          </div>
          <label>Tipo de cobrança/faturamento
            <select value={form.tipoFaturamento} onChange={(e) => set("tipoFaturamento", e.target.value)} style={inputStyle}>
              {tiposFat.map((t) => <option key={t.id} value={t.id as string}>{t.descricao}</option>)}
            </select>
          </label>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
            <label>Gerente técnico
              <select value={form.gerenteTecnico ?? ""} onChange={(e) => set("gerenteTecnico", numOrNull(e.target.value))} style={inputStyle}>
                <option value="">Selecione…</option>
                {gerTec.map((g) => <option key={g.id} value={g.id as number}>{g.descricao}</option>)}
              </select>
            </label>
            <label>Gerente comercial
              <select value={form.gerenteComercial ?? ""} onChange={(e) => set("gerenteComercial", numOrNull(e.target.value))} style={inputStyle}>
                <option value="">Selecione…</option>
                {gerCom.map((g) => <option key={g.id} value={g.id as number}>{g.descricao}</option>)}
              </select>
            </label>
          </div>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 8 }}>
            <label>Data de início <input type="date" value={form.dataInicio} onChange={(e) => set("dataInicio", e.target.value)} style={inputStyle} /></label>
            <label>Data de fim <input type="date" value={form.dataFim} onChange={(e) => set("dataFim", e.target.value)} style={inputStyle} /></label>
            <label>Filial
              <select value={form.cdEmpresa ?? ""} onChange={(e) => set("cdEmpresa", numOrNull(e.target.value))} style={inputStyle}>
                <option value="">Selecione…</option>
                {filiais.map((fl) => <option key={fl.id} value={fl.id as number}>{fl.descricao}</option>)}
              </select>
            </label>
          </div>
          <fieldset style={{ border: "1px solid var(--border)", borderRadius: 8, padding: 10 }}>
            <legend style={{ fontSize: 12, color: "var(--muted)" }}>Opções</legend>
            <div style={{ display: "flex", gap: 16, flexWrap: "wrap" }}>
              <label><input type="checkbox" checked={form.interno} onChange={(e) => set("interno", e.target.checked)} /> Projeto interno</label>
              <label><input type="checkbox" checked={form.comissao} onChange={(e) => set("comissao", e.target.checked)} /> Projeto com comissão</label>
              <label><input type="checkbox" checked={form.aceitaCo} onChange={(e) => set("aceitaCo", e.target.checked)} /> Aceita horas CO</label>
              <label><input type="checkbox" checked={form.aceitaNc} onChange={(e) => set("aceitaNc", e.target.checked)} /> Aceita horas NC</label>
            </div>
          </fieldset>
          <label>Status <select value={form.status} onChange={(e) => set("status", e.target.value)}><option value="AT">Ativo</option><option value="EN">Encerrado</option></select></label>
          <label>Observação <textarea value={form.observacao} onChange={(e) => set("observacao", e.target.value)} rows={2} style={inputStyle} /></label>

          <fieldset style={{ border: "1px solid var(--border)", borderRadius: 8, padding: 10, background: "var(--surface-2)" }}>
            <legend style={{ fontSize: 12, color: "var(--muted)" }}>Totais (somados das fases — somente leitura)</legend>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 10, fontSize: 14 }}>
              {metric("Horas previstas", num(form.horasPrevistas))}
              {metric("Valor previsto", brl(form.valorPrevisto))}
              {metric("Custo previsto", brl(form.custoPrevisto))}
              {metric("Horas realizado", num(form.horasRealizadas))}
              {metric("Valor realizado", brl(form.valorRealizado))}
              {metric("Custo realizado", brl(form.custoRealizado))}
            </div>
          </fieldset>

          <div style={{ display: "flex", gap: 8 }}>
            <button onClick={() => { setForm(vazio); setErro(null); }}>Limpar</button>
            <button onClick={() => salvar.mutate(form)} style={{ background: "var(--brand-orange)", color: "#fff", border: "none", borderRadius: 8, padding: "8px 14px" }}>Salvar</button>
          </div>
        </div>
      </div>
    </section>
  );
}
