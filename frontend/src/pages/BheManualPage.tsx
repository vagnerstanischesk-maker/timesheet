import { useState } from "react";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useApi } from "../auth/useApi";

interface FuncOpt { cdFuncionario: number; nome: string; }

export function BheManualPage() {
  const api = useApi();
  const [func, setFunc] = useState("");
  const [filtro, setFiltro] = useState("");
  const [v50, setV50] = useState(""); const [v100, setV100] = useState("");
  const [msg, setMsg] = useState<string | null>(null); const [erro, setErro] = useState<string | null>(null);

  const { data: funcs = [] } = useQuery<FuncOpt[]>({ queryKey: ["func-nome"], queryFn: () => api<FuncOpt[]>("/v1/cadastros/funcionarios") });
  const reg = useMutation({
    mutationFn: () => api("/v1/cadastros/bhe/manual", { method: "POST", body: JSON.stringify({ cdFuncionario: Number(func), valor50: v50 ? Number(v50) : 0, valor100: v100 ? Number(v100) : 0 }) }),
    onSuccess: () => { setMsg("Movimento registrado."); setErro(null); setV50(""); setV100(""); }, onError: (e: Error) => { setErro(e.message); setMsg(null); } });
  const filtrados = funcs.filter((x) => x.nome.toLowerCase().includes(filtro.toLowerCase()));

  return (
    <section style={{ maxWidth: 480 }}>
      <h1 style={{ color: "var(--heading)" }}>Movimentação manual de BHE</h1>
      <p style={{ color: "var(--muted)" }}>Valores positivos creditam; negativos debitam o saldo do funcionário.</p>
      {erro && <p style={{ color: "var(--danger)" }}>{erro}</p>}{msg && <p style={{ color: "var(--success,#0B6E3C)" }}>{msg}</p>}
      <div style={{ display: "grid", gap: 8 }}>
        <label>Funcionário (pesquise por nome)
          <input placeholder="filtrar por nome…" value={filtro} onChange={(e) => setFiltro(e.target.value)} style={{ width: "100%", padding: "6px 8px", marginBottom: 4 }} />
          <select value={func} onChange={(e) => setFunc(e.target.value)} style={{ width: "100%", padding: "6px 8px" }}>
            <option value="">Selecione…</option>
            {filtrados.map((x) => <option key={x.cdFuncionario} value={x.cdFuncionario}>{x.nome}</option>)}
          </select>
        </label>
        <label>Valor BHE 50% <input type="number" step={0.5} value={v50} onChange={(e) => setV50(e.target.value)} /></label>
        <label>Valor BHE 100% <input type="number" step={0.5} value={v100} onChange={(e) => setV100(e.target.value)} /></label>
        <button onClick={() => reg.mutate()} disabled={!func} style={{ background: "var(--brand-orange)", color: "#fff", border: "none", borderRadius: 8, padding: "8px 14px" }}>Registrar movimento</button>
      </div>
    </section>
  );
}
