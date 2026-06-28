import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { useApi } from "../auth/useApi";
import { rotuloPeriodo } from "../lib/quinzena";

interface LinhaLog { cdFuncionario: number; nome: string; anoRef: number; mesRef: number; periodo: number; status: string; }

const corStatus = (s: string) => s === "Aberto" ? "var(--warning,#8A6D00)" : s === "Rejeitado" ? "var(--danger)" : "#0B6E3C";

export function RelatoriosPage() {
  const api = useApi();
  const { data: linhas = [], isLoading } = useQuery<LinhaLog[]>({
    queryKey: ["rel-logs-nao-fechados"], queryFn: () => api<LinhaLog[]>("/v1/relatorios/logs-nao-fechados"),
  });

  // agrupa por colaborador (já vem ordenado por nome e período)
  const grupos = useMemo(() => {
    const map = new Map<number, { nome: string; itens: LinhaLog[] }>();
    for (const l of linhas) {
      if (!map.has(l.cdFuncionario)) map.set(l.cdFuncionario, { nome: l.nome, itens: [] });
      map.get(l.cdFuncionario)!.itens.push(l);
    }
    return [...map.values()];
  }, [linhas]);

  function exportarCsv() {
    const linhasCsv = [["Colaborador", "Período de log", "Status"],
      ...linhas.map((l) => [l.nome, rotuloPeriodo(l.anoRef, l.mesRef, l.periodo), l.status])];
    const csv = linhasCsv.map((r) => r.map((c) => `"${String(c).replace(/"/g, '""')}"`).join(";")).join("\n");
    const blob = new Blob(["﻿" + csv], { type: "text/csv;charset=utf-8" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a"); a.href = url; a.download = "logs-nao-fechados.csv"; a.click();
    URL.revokeObjectURL(url);
  }

  return (
    <section>
      <header style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: 8 }}>
        <h1 style={{ color: "var(--heading)", margin: 0 }}>Logs pendentes (períodos passados)</h1>
        <button onClick={exportarCsv} disabled={!linhas.length}>Exportar CSV</button>
      </header>
      <p style={{ color: "var(--muted)" }}>Períodos de log <strong>pendentes</strong> — status Aberto ou Rejeitado (exclui Fechado e Aprovado) — agrupados por colaborador e ordenados por colaborador e período.</p>

      {isLoading && <p>Carregando…</p>}
      {!isLoading && linhas.length === 0 && <p style={{ color: "var(--muted)" }}>Nenhum período de log pendente. Tudo fechado ou aprovado.</p>}

      {grupos.map((g) => (
        <article key={g.nome} style={{ border: "1px solid var(--border)", borderRadius: 12, padding: 14, marginBottom: 12 }}>
          <strong>{g.nome}</strong>
          <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14, marginTop: 8 }}>
            <thead><tr style={{ textAlign: "left", background: "var(--surface-2)" }}>
              <th style={{ padding: "6px 10px" }}>Período de log</th><th style={{ padding: "6px 10px" }}>Status</th>
            </tr></thead>
            <tbody>
              {g.itens.map((l, i) => (
                <tr key={i} style={{ borderTop: "1px solid var(--border)" }}>
                  <td style={{ padding: "6px 10px" }}>{rotuloPeriodo(l.anoRef, l.mesRef, l.periodo)}</td>
                  <td style={{ padding: "6px 10px", color: corStatus(l.status), fontWeight: 500 }}>{l.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </article>
      ))}
    </section>
  );
}
