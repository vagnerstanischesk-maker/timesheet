import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { useApi } from "../auth/useApi";

interface Linha {
  cdProjeto: number; descricao: string; cdClifor: number | null; nomeCliente: string | null;
  horasVendidas: number; horasExecutadas: number;
  percExecHoras: number; percInformado: number; percDiferenca: number; diferencaHoras: number;
}

const h = (v: number | null | undefined) => (v == null ? "—" : Number(v).toLocaleString("pt-BR", { maximumFractionDigits: 1 }));
const pct = (v: number | null | undefined) => (v == null ? "—" : `${Number(v).toLocaleString("pt-BR", { maximumFractionDigits: 1 })}%`);

export function RelatorioAcompanhamentoPage() {
  const api = useApi();
  const { data: linhas = [], isLoading } = useQuery<Linha[]>({
    queryKey: ["rel-acompanhamento"], queryFn: () => api<Linha[]>("/v1/relatorios/acompanhamento-projetos"),
  });

  const totais = useMemo(() => linhas.reduce(
    (a, l) => ({ vend: a.vend + (l.horasVendidas || 0), exec: a.exec + (l.horasExecutadas || 0), dif: a.dif + (l.diferencaHoras || 0) }),
    { vend: 0, exec: 0, dif: 0 }), [linhas]);

  function exportarCsv() {
    const cab = ["Cliente", "Projeto", "Horas vendidas", "Horas executadas", "% executado (horas)", "% informado (gestor)", "Diferença %", "Diferença (h)"];
    const corpo = linhas.map((l) => [
      l.nomeCliente ?? "—", `${l.cdProjeto} · ${l.descricao}`,
      h(l.horasVendidas), h(l.horasExecutadas), pct(l.percExecHoras), pct(l.percInformado), pct(l.percDiferenca), h(l.diferencaHoras),
    ]);
    const csv = [cab, ...corpo].map((r) => r.map((c) => `"${String(c).replace(/"/g, '""')}"`).join(";")).join("\n");
    const blob = new Blob(["﻿" + csv], { type: "text/csv;charset=utf-8" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a"); a.href = url; a.download = "acompanhamento-projetos.csv"; a.click();
    URL.revokeObjectURL(url);
  }

  const th = { padding: "6px 10px", textAlign: "left" as const };
  const thNum = { padding: "6px 10px", textAlign: "right" as const };
  const tdNum = { padding: "6px 10px", textAlign: "right" as const };

  return (
    <section>
      <header style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: 8 }}>
        <h1 style={{ color: "var(--heading)", margin: 0 }}>Acompanhamento de projetos</h1>
        <button onClick={exportarCsv} disabled={!linhas.length}>Exportar CSV</button>
      </header>
      <p style={{ color: "var(--muted)" }}>
        Visão consolidada de todos os projetos, somando todos os períodos de log. Ordenado por cliente e projeto.
      </p>

      {isLoading && <p>Carregando…</p>}
      {!isLoading && linhas.length === 0 && <p style={{ color: "var(--muted)" }}>Nenhum projeto encontrado.</p>}

      {linhas.length > 0 && (
        <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14, marginTop: 8 }}>
          <thead>
            <tr style={{ background: "var(--surface-2)" }}>
              <th style={th}>Cliente</th>
              <th style={th}>Projeto</th>
              <th style={thNum}>Horas vendidas</th>
              <th style={thNum}>Horas executadas</th>
              <th style={thNum}>% exec. (horas)</th>
              <th style={thNum}>% informado (gestor)</th>
              <th style={thNum}>Diferença %</th>
              <th style={thNum}>Diferença (h)</th>
            </tr>
          </thead>
          <tbody>
            {linhas.map((l) => (
              <tr key={l.cdProjeto} style={{ borderTop: "1px solid var(--border)" }}>
                <td style={{ padding: "6px 10px" }}>{l.nomeCliente ?? "—"}</td>
                <td style={{ padding: "6px 10px" }}>{l.cdProjeto} · {l.descricao}</td>
                <td style={tdNum}>{h(l.horasVendidas)}</td>
                <td style={tdNum}>{h(l.horasExecutadas)}</td>
                <td style={tdNum}>{pct(l.percExecHoras)}</td>
                <td style={tdNum}>{pct(l.percInformado)}</td>
                <td style={{ ...tdNum, color: l.percDiferenca > 0 ? "var(--danger)" : "var(--ink)" }}>{pct(l.percDiferenca)}</td>
                <td style={{ ...tdNum, color: l.diferencaHoras > 0 ? "var(--danger)" : "var(--ink)" }}>{h(l.diferencaHoras)}</td>
              </tr>
            ))}
          </tbody>
          <tfoot>
            <tr style={{ borderTop: "2px solid var(--border)", fontWeight: 600, background: "var(--surface-2)" }}>
              <td style={{ padding: "6px 10px" }} colSpan={2}>Total</td>
              <td style={tdNum}>{h(totais.vend)}</td>
              <td style={tdNum}>{h(totais.exec)}</td>
              <td style={tdNum}>—</td>
              <td style={tdNum}>—</td>
              <td style={tdNum}>—</td>
              <td style={{ ...tdNum, color: totais.dif > 0 ? "var(--danger)" : "var(--ink)" }}>{h(totais.dif)}</td>
            </tr>
          </tfoot>
        </table>
      )}
    </section>
  );
}
