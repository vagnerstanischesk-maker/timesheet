import { useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { useApi } from "../auth/useApi";

interface Mov {
  cdFuncionario: number; nome: string; data: string;
  valor50: number; valor100: number; origem: string; usuario: string;
}

const hoje = new Date();
const iso = (d: Date) => d.toISOString().slice(0, 10);
const primeiroDia = iso(new Date(hoje.getFullYear(), hoje.getMonth(), 1));
const ultimoDia = iso(new Date(hoje.getFullYear(), hoje.getMonth() + 1, 0));
const h = (v: number | null | undefined) => (v == null ? "0" : Number(v).toLocaleString("pt-BR", { maximumFractionDigits: 1 }));
const dataBr = (s: string) => s.split("-").reverse().join("/");

export function RelatorioBhePage() {
  const api = useApi();
  const [inicio, setInicio] = useState(primeiroDia);
  const [fim, setFim] = useState(ultimoDia);

  const { data: movs = [], isLoading } = useQuery<Mov[]>({
    queryKey: ["rel-bhe", inicio, fim],
    queryFn: () => api<Mov[]>(`/v1/relatorios/bhe?inicio=${inicio}&fim=${fim}`),
  });

  // agrupa por colaborador (já vem ordenado por nome e data)
  const grupos = useMemo(() => {
    const map = new Map<number, { nome: string; itens: Mov[]; t50: number; t100: number }>();
    for (const m of movs) {
      if (!map.has(m.cdFuncionario)) map.set(m.cdFuncionario, { nome: m.nome, itens: [], t50: 0, t100: 0 });
      const g = map.get(m.cdFuncionario)!;
      g.itens.push(m); g.t50 += m.valor50 || 0; g.t100 += m.valor100 || 0;
    }
    return [...map.values()];
  }, [movs]);

  const total = useMemo(() => movs.reduce((a, m) => ({ v50: a.v50 + (m.valor50 || 0), v100: a.v100 + (m.valor100 || 0) }), { v50: 0, v100: 0 }), [movs]);

  function exportarCsv() {
    const cab = ["Colaborador", "Data", "BHE 50%", "BHE 100%", "Origem", "Usuário"];
    const corpo = movs.map((m) => [m.nome, dataBr(m.data), h(m.valor50), h(m.valor100), m.origem, m.usuario ?? ""]);
    const csv = [cab, ...corpo].map((r) => r.map((c) => `"${String(c).replace(/"/g, '""')}"`).join(";")).join("\n");
    const blob = new Blob(["﻿" + csv], { type: "text/csv;charset=utf-8" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a"); a.href = url; a.download = `bhe-${inicio}-a-${fim}.csv`; a.click();
    URL.revokeObjectURL(url);
  }

  const thNum = { padding: "6px 10px", textAlign: "right" as const };
  const tdNum = { padding: "6px 10px", textAlign: "right" as const };

  return (
    <section>
      <header style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: 8 }}>
        <h1 style={{ color: "var(--heading)", margin: 0 }}>Movimentação de BHE</h1>
        <button onClick={exportarCsv} disabled={!movs.length}>Exportar CSV</button>
      </header>
      <p style={{ color: "var(--muted)" }}>Créditos por aprovação de horas extras e lançamentos manuais, por período, agrupados por colaborador.</p>

      <div style={{ display: "flex", gap: 10, alignItems: "center", flexWrap: "wrap", margin: "8px 0 14px" }}>
        <label style={{ fontSize: 13, color: "var(--muted)" }}>Início <input type="date" value={inicio} onChange={(e) => setInicio(e.target.value)} style={{ marginLeft: 6, padding: "5px 7px" }} /></label>
        <label style={{ fontSize: 13, color: "var(--muted)" }}>Fim <input type="date" value={fim} onChange={(e) => setFim(e.target.value)} style={{ marginLeft: 6, padding: "5px 7px" }} /></label>
      </div>

      {isLoading && <p>Carregando…</p>}
      {!isLoading && movs.length === 0 && <p style={{ color: "var(--muted)" }}>Nenhuma movimentação de BHE no período.</p>}

      {grupos.map((g) => (
        <article key={g.nome} style={{ border: "1px solid var(--border)", borderRadius: 12, padding: 14, marginBottom: 12 }}>
          <strong>{g.nome}</strong>
          <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14, marginTop: 8 }}>
            <thead><tr style={{ textAlign: "left", background: "var(--surface-2)" }}>
              <th style={{ padding: "6px 10px" }}>Data</th>
              <th style={thNum}>BHE 50%</th>
              <th style={thNum}>BHE 100%</th>
              <th style={{ padding: "6px 10px" }}>Origem</th>
              <th style={{ padding: "6px 10px" }}>Usuário</th>
            </tr></thead>
            <tbody>
              {g.itens.map((m, i) => (
                <tr key={i} style={{ borderTop: "1px solid var(--border)" }}>
                  <td style={{ padding: "6px 10px" }}>{dataBr(m.data)}</td>
                  <td style={tdNum}>{h(m.valor50)}</td>
                  <td style={tdNum}>{h(m.valor100)}</td>
                  <td style={{ padding: "6px 10px" }}>{m.origem}</td>
                  <td style={{ padding: "6px 10px", color: "var(--muted)" }}>{m.usuario ?? ""}</td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr style={{ borderTop: "2px solid var(--border)", fontWeight: 600 }}>
                <td style={{ padding: "6px 10px" }}>Subtotal</td>
                <td style={tdNum}>{h(g.t50)}</td>
                <td style={tdNum}>{h(g.t100)}</td>
                <td colSpan={2}></td>
              </tr>
            </tfoot>
          </table>
        </article>
      ))}

      {movs.length > 0 && (
        <div style={{ borderTop: "2px solid var(--border)", paddingTop: 8, fontWeight: 600, display: "flex", gap: 18 }}>
          <span>Total no período:</span>
          <span>BHE 50% {h(total.v50)}h</span>
          <span>BHE 100% {h(total.v100)}h</span>
        </div>
      )}
    </section>
  );
}
