import { Fragment } from "react";

export type Tipo = "CO" | "NC" | "COx" | "NCx";
export const ORDEM: Tipo[] = ["CO", "NC", "COx", "NCx"];
export const LABEL: Record<Tipo, string> = { CO: "CO", NC: "NC", COx: "CO ex", NCx: "NC ex" };

export interface DiaMeta { ano: number; mes: number; dia: number; diaSemana: string; feriado: boolean; status: string;
  horasNormaisEsperadas: number; ausBhe: number; ausAbn: number; }
export interface Linha { cdProjeto: number; seqFase: number; seqTarefa: number; descricao: string; externa: boolean;
  tipos: Record<Tipo, boolean>; horas: Record<Tipo, number[]>; }

interface CelApi { nc: number; co: number; ncExtra: number; coExtra: number; }
interface TarefaApi { cdProjeto: number; projetoDescricao: string; seqFase: number; faseDescricao: string; seqTarefa: number; descricaoTarefa: string; externa: boolean; celulas: CelApi[]; }

const soma = (a: number[]) => a.reduce((s, v) => s + (v || 0), 0);

export function linhasFromApi(tarefas: TarefaApi[]): Linha[] {
  return tarefas.map((t) => {
    const horas: Record<Tipo, number[]> = {
      CO: t.celulas.map((c) => c.co || 0), NC: t.celulas.map((c) => c.nc || 0),
      COx: t.celulas.map((c) => c.coExtra || 0), NCx: t.celulas.map((c) => c.ncExtra || 0),
    };
    const tipos: Record<Tipo, boolean> = { CO: soma(horas.CO) > 0, NC: soma(horas.NC) > 0, COx: soma(horas.COx) > 0, NCx: soma(horas.NCx) > 0 };
    if (!tipos.CO && !tipos.NC && !tipos.COx && !tipos.NCx) tipos.NC = true;
    return { cdProjeto: t.cdProjeto, seqFase: t.seqFase, seqTarefa: t.seqTarefa,
      descricao: `${t.projetoDescricao} · ${t.faseDescricao} · ${t.descricaoTarefa}`, externa: t.externa, tipos, horas };
  });
}

const fdsDia = (s: string) => s === "sáb" || s === "dom";
const editavelDia = (s: string) => s === "A" || s === "R";
const STATUS_COR: Record<string, [string, string]> = {
  A: ["var(--warning,#8A6D00)", "A"], F: ["var(--brand-orange)", "F"], P: ["#0B6E3C", "P"], R: ["var(--danger)", "R"],
};

const STATUS_LEGENDA: [string, string][] = [
  ["A", "Aberto"], ["F", "Fechado"], ["P", "Aprovado"], ["R", "Rejeitado"],
];

/** Legenda dos status diários do log (A/F/P/R). */
export function LegendaStatus() {
  return (
    <div style={{ display: "flex", flexWrap: "wrap", gap: 12, alignItems: "center", fontSize: 12, color: "var(--muted)", margin: "4px 0 8px" }}>
      <span>Status do dia:</span>
      {STATUS_LEGENDA.map(([sig, nome]) => {
        const c = STATUS_COR[sig];
        return (
          <span key={sig} style={{ display: "inline-flex", gap: 4, alignItems: "center" }}>
            <strong style={{ color: c[0] }}>{sig}</strong> {nome}
          </span>
        );
      })}
    </div>
  );
}

export interface GradeLogProps {
  dias: DiaMeta[]; linhas: Linha[]; modo: "edicao" | "aprovacao";
  onHora?: (li: number, t: Tipo, di: number, v: number) => void;
  onToggleTipo?: (li: number, t: Tipo) => void;
  onAus?: (di: number, campo: "ausBhe" | "ausAbn", v: number) => void;
  onRemover?: (li: number) => void;
  onAprovarDia?: (di: number) => void;
  onRejeitarDia?: (di: number, obs: string) => void;
  justif?: Record<number, string>;
  setJustif?: (di: number, v: string) => void;
}

export function GradeLog(p: GradeLogProps) {
  const { dias, linhas, modo } = p;
  const ro = modo === "aprovacao";
  const inp = { width: 34, height: 22, textAlign: "center" as const, padding: 1, fontSize: 11 };
  const dcol = (d: DiaMeta) => ({ padding: 1, textAlign: "center" as const, minWidth: 38, background: fdsDia(d.diaSemana) || d.feriado ? "var(--surface-2)" : undefined });
  const nameCell = { padding: "3px 8px", position: "sticky" as const, left: 0, background: "var(--surface)", minWidth: 240, maxWidth: 280 };
  const totalDia = (di: number) => linhas.reduce((s, l) => s + ORDEM.reduce((a, t) => a + (l.tipos[t] ? l.horas[t][di] || 0 : 0), 0), 0) + (dias[di]?.ausBhe || 0) + (dias[di]?.ausAbn || 0);
  const totalLinha = (l: Linha) => ORDEM.reduce((a, t) => a + (l.tipos[t] ? soma(l.horas[t]) : 0), 0);
  const somaDias = (sel: (d: DiaMeta) => number) => dias.reduce((s, d) => s + (sel(d) || 0), 0);
  const chip = (li: number, t: Tipo) => (
    <button key={t} onClick={() => p.onToggleTipo?.(li, t)} title={LABEL[t]}
      style={{ fontSize: 10, padding: "0 4px", marginLeft: 3, borderRadius: 4, lineHeight: "16px",
        borderColor: linhas[li].tipos[t] ? "var(--brand-orange)" : "var(--border)", color: linhas[li].tipos[t] ? "var(--brand-orange)" : "var(--muted)" }}>{LABEL[t]}</button>
  );

  return (
    <div style={{ overflowX: "auto" }}>
      <table style={{ borderCollapse: "collapse", fontSize: 11, minWidth: 640 }}>
        <thead>
          <tr style={{ background: "var(--surface-2)" }}>
            <th style={{ ...nameCell, background: "var(--surface-2)", textAlign: "left" }}>Projeto / Fase / Tarefa</th>
            <th style={{ padding: "3px 4px" }}>Tipo</th>
            {dias.map((d) => <th key={`${d.ano}-${d.mes}-${d.dia}`} style={dcol(d)}><div style={{ color: "var(--muted)", fontWeight: 400 }}>{d.diaSemana}</div>{String(d.dia).padStart(2, "0")}</th>)}
            <th style={{ padding: "3px 5px" }}>Tot</th>
          </tr>
        </thead>
        <tbody>
          {linhas.map((l, li) => {
            const ativos = ORDEM.filter((t) => l.tipos[t]);
            return (
              <Fragment key={`${l.cdProjeto}-${l.seqFase}-${l.seqTarefa}`}>
                {ativos.map((t, ti) => (
                  <tr key={t} style={{ borderTop: ti === 0 ? "1px solid var(--border)" : undefined }}>
                    {ti === 0 && (
                      <td rowSpan={ativos.length} style={nameCell}>
                        <div>{l.descricao}{l.externa ? " 🔒" : ""}</div>
                        {!ro && !l.externa && <div style={{ marginTop: 2 }}>
                          {ORDEM.map((tt) => chip(li, tt))}
                          <button onClick={() => p.onRemover?.(li)} title="remover tarefa" style={{ fontSize: 10, padding: "0 4px", marginLeft: 6, borderColor: "var(--danger)", color: "var(--danger)", borderRadius: 4, lineHeight: "16px" }}>× remover</button>
                        </div>}
                      </td>
                    )}
                    <td style={{ padding: "2px 4px", textAlign: "center", color: "var(--muted)" }}>{LABEL[t]}</td>
                    {dias.map((d, di) => {
                      const ed = !ro && editavelDia(d.status) && !l.externa;
                      const v = l.horas[t][di] || 0;
                      return <td key={di} style={dcol(d)}>
                        {ro ? <span>{v || ""}</span> : <input type="number" step={0.5} min={0} value={v || ""} disabled={!ed} onChange={(e) => p.onHora?.(li, t, di, Number(e.target.value))} style={inp} />}
                      </td>;
                    })}
                    {ti === 0 && <td rowSpan={ativos.length} style={{ padding: "2px 5px", textAlign: "center" }}><strong>{totalLinha(l)}</strong></td>}
                  </tr>
                ))}
              </Fragment>
            );
          })}
          {linhas.length === 0 && <tr><td colSpan={dias.length + 3} style={{ padding: 10, color: "var(--muted)" }}>Nenhuma tarefa lançada no período.</td></tr>}

          <tr style={{ borderTop: "2px solid var(--border)", fontWeight: 600 }}>
            <td style={nameCell}>Total do dia</td><td></td>
            {dias.map((d, di) => <td key={di} style={dcol(d)}>{totalDia(di) || "—"}</td>)}
            <td style={{ textAlign: "center" }}>{dias.reduce((s, _, di) => s + totalDia(di), 0)}</td>
          </tr>
          <tr><td style={{ ...nameCell, color: "var(--muted)" }}>Ausência BHE</td><td></td>
            {dias.map((d, di) => <td key={di} style={dcol(d)}>{ro ? (d.ausBhe || "") : <input type="number" step={0.5} min={0} value={d.ausBhe || ""} disabled={!editavelDia(d.status)} onChange={(e) => p.onAus?.(di, "ausBhe", Number(e.target.value))} style={inp} />}</td>)}<td style={{ textAlign: "center" }}>{somaDias((d) => d.ausBhe) || ""}</td></tr>
          <tr><td style={{ ...nameCell, color: "var(--muted)" }}>Ausência ABN</td><td></td>
            {dias.map((d, di) => <td key={di} style={dcol(d)}>{ro ? (d.ausAbn || "") : <input type="number" step={0.5} min={0} value={d.ausAbn || ""} disabled={!editavelDia(d.status)} onChange={(e) => p.onAus?.(di, "ausAbn", Number(e.target.value))} style={inp} />}</td>)}<td style={{ textAlign: "center" }}>{somaDias((d) => d.ausAbn) || ""}</td></tr>
          <tr style={{ color: "var(--muted)" }}><td style={nameCell}>Horas esperadas</td><td></td>
            {dias.map((d, di) => <td key={di} style={dcol(d)}>{d.horasNormaisEsperadas}</td>)}<td style={{ textAlign: "center" }}>{somaDias((d) => d.horasNormaisEsperadas)}</td></tr>
          <tr><td style={nameCell}>Status</td><td></td>
            {dias.map((d, di) => { const c = STATUS_COR[d.status] ?? ["var(--muted)", d.status]; return <td key={di} style={dcol(d)}><span title={d.status} style={{ fontWeight: 600, color: c[0] }}>{c[1]}</span></td>; })}<td></td></tr>

          {modo === "aprovacao" && (<>
            <tr style={{ borderTop: "2px solid var(--border)" }}><td style={nameCell}>Aprovar / Rejeitar dia</td><td></td>
              {dias.map((d, di) => <td key={di} style={dcol(d)}>
                {d.status === "F" ? <span style={{ display: "inline-flex", gap: 2 }}>
                  <button title="aprovar" onClick={() => p.onAprovarDia?.(di)} style={{ padding: "0 5px", color: "#0B6E3C", borderColor: "#0B6E3C", borderRadius: 4 }}>✓</button>
                  <button title="rejeitar" onClick={() => p.onRejeitarDia?.(di, p.justif?.[di] ?? "")} style={{ padding: "0 5px", color: "var(--danger)", borderColor: "var(--danger)", borderRadius: 4 }}>✗</button>
                </span> : <span style={{ color: "var(--muted)" }}>—</span>}
              </td>)}<td></td></tr>
            <tr><td style={{ ...nameCell, color: "var(--muted)" }}>Justificativa (rejeição)</td><td></td>
              {dias.map((d, di) => <td key={di} style={dcol(d)}>{d.status === "F" ? <input value={p.justif?.[di] ?? ""} onChange={(e) => p.setJustif?.(di, e.target.value)} title="motivo da rejeição" style={{ ...inp, width: 60 }} /> : ""}</td>)}<td></td></tr>
          </>)}
        </tbody>
      </table>
    </div>
  );
}
