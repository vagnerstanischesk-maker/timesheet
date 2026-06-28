import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useApi } from "../auth/useApi";

interface Vertical { cdVertical: number | null; descricao: string; status: string; }
const vazio: Vertical = { cdVertical: null, descricao: "", status: "AT" };

export function CadastroVerticaisPage() {
  const api = useApi();
  const qc = useQueryClient();
  const [v, setV] = useState<Vertical>(vazio);
  const [erro, setErro] = useState<string | null>(null);
  const { data: lista = [] } = useQuery<Vertical[]>({ queryKey: ["verticais"], queryFn: () => api<Vertical[]>("/v1/cadastros/verticais") });
  const salvar = useMutation({
    mutationFn: (x: Vertical) => api("/v1/cadastros/verticais", { method: "PUT", body: JSON.stringify(x) }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ["verticais"] }); setV(vazio); setErro(null); },
    onError: (e: Error) => setErro(e.message),
  });
  return (
    <section style={{ display: "grid", gridTemplateColumns: "1.3fr 1fr", gap: 16 }}>
      <div>
        <h1 style={{ color: "var(--heading)" }}>Verticais (linha de negócio)</h1>
        <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14 }}>
          <thead><tr style={{ textAlign: "left", background: "var(--surface-2)" }}>
            <th style={{ padding: "6px 8px" }}>Cód.</th><th>Descrição</th><th>Status</th><th></th></tr></thead>
          <tbody>{lista.map((x) => (
            <tr key={x.cdVertical} style={{ borderTop: "1px solid var(--border)" }}>
              <td style={{ padding: "6px 8px" }}>{x.cdVertical}</td><td>{x.descricao}</td><td>{x.status}</td>
              <td style={{ textAlign: "right" }}><button onClick={() => { setV(x); setErro(null); }}>Editar</button></td>
            </tr>))}</tbody>
        </table>
      </div>
      <div style={{ border: "1px solid var(--border)", borderRadius: 12, padding: 14 }}>
        <strong>{v.cdVertical ? `Editar vertical ${v.cdVertical}` : "Nova vertical"}</strong>
        {erro && <p style={{ color: "var(--danger)" }}>{erro}</p>}
        <div style={{ display: "grid", gap: 8, marginTop: 8 }}>
          <label>Código <input value={v.cdVertical ?? "(automático)"} readOnly disabled style={{ width: "100%", background: "var(--surface-2)" }} /></label>
          <label>Descrição <input value={v.descricao} onChange={(e) => setV((s) => ({ ...s, descricao: e.target.value }))} style={{ width: "100%" }} /></label>
          <label>Status <select value={v.status} onChange={(e) => setV((s) => ({ ...s, status: e.target.value }))}><option value="AT">Ativo</option><option value="IN">Inativo</option></select></label>
          <div style={{ display: "flex", gap: 8 }}>
            <button onClick={() => { setV(vazio); setErro(null); }}>Limpar</button>
            <button onClick={() => salvar.mutate(v)} style={{ background: "var(--brand-orange)", color: "#fff", border: "none", borderRadius: 8, padding: "8px 14px" }}>Salvar</button>
          </div>
        </div>
      </div>
    </section>
  );
}
