export interface GradeDia { ano: number; mes: number; dia: number; diaSemana: string; feriado: boolean; status: string; totalHoras: number; }
export interface GradeProjeto { cdProjeto: number; projetoDescricao: string; horasPorDia: number[]; total: number; }

const fds = (s: string) => s === "sáb" || s === "dom";

/** Grade de horas por projeto × dia (linhas = projetos, colunas = dias). */
export function GradeProjetoDia({ dias, grade }: { dias: GradeDia[]; grade: GradeProjeto[] }) {
  if (!dias?.length) return null;
  const totalDia = (i: number) => grade.reduce((s, p) => s + (p.horasPorDia[i] ?? 0), 0);
  const totalGeral = grade.reduce((s, p) => s + p.total, 0);
  const cell = { padding: "5px 7px", textAlign: "center" as const, borderLeft: "1px solid var(--border)" };
  return (
    <div style={{ overflowX: "auto" }}>
      <table style={{ borderCollapse: "collapse", fontSize: 13, minWidth: 520 }}>
        <thead>
          <tr style={{ background: "var(--surface-2)" }}>
            <th style={{ padding: "5px 8px", textAlign: "left", position: "sticky", left: 0, background: "var(--surface-2)" }}>Projeto</th>
            {dias.map((d) => (
              <th key={`${d.ano}-${d.mes}-${d.dia}`} style={{ ...cell, background: fds(d.diaSemana) || d.feriado ? "var(--surface-2)" : undefined }}>
                <div style={{ color: "var(--muted)", fontWeight: 400 }}>{d.diaSemana}</div>{String(d.dia).padStart(2, "0")}
              </th>
            ))}
            <th style={{ ...cell }}>Total</th>
          </tr>
        </thead>
        <tbody>
          {grade.map((p) => (
            <tr key={p.cdProjeto} style={{ borderTop: "1px solid var(--border)" }}>
              <td style={{ padding: "5px 8px", position: "sticky", left: 0, background: "var(--surface)" }}>{p.projetoDescricao}</td>
              {dias.map((d, i) => (
                <td key={i} style={{ ...cell, background: fds(d.diaSemana) || d.feriado ? "var(--surface-2)" : undefined }}>
                  {p.horasPorDia[i] ? p.horasPorDia[i] : <span style={{ color: "var(--muted)" }}>·</span>}
                </td>
              ))}
              <td style={{ ...cell }}><strong>{p.total}</strong></td>
            </tr>
          ))}
          {grade.length === 0 && <tr><td colSpan={dias.length + 2} style={{ padding: 10, color: "var(--muted)" }}>Nenhum lançamento no período.</td></tr>}
          <tr style={{ borderTop: "2px solid var(--border)", fontWeight: 600 }}>
            <td style={{ padding: "5px 8px", position: "sticky", left: 0, background: "var(--surface)" }}>Total do dia</td>
            {dias.map((_, i) => <td key={i} style={{ ...cell }}>{totalDia(i) || "—"}</td>)}
            <td style={{ ...cell }}>{totalGeral}</td>
          </tr>
        </tbody>
      </table>
    </div>
  );
}
