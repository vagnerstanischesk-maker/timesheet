import { useEffect, useMemo, useState } from "react";
import { useAuth } from "react-oidc-context";
import { useQuery } from "@tanstack/react-query";
import { useApi } from "../auth/useApi";
import { useAutosave } from "../hooks/useAutosave";
import { useUnsavedChangesPrompt } from "../hooks/useUnsavedChangesPrompt";
import { ehMultiploDeMeia } from "../lib/horas";

interface TarefaAlocada { cdProjeto: number; projetoDescricao: string; seqFase: number; faseDescricao: string; seqTarefa: number; descricaoTarefa: string; externa: boolean; }
interface TarefaDTO {
  cdProjeto: number; seqFase: number; seqTarefa: number; descricaoTarefa: string | null;
  horasNc: number; horasCo: number; horasNcExtra: number; horasCoExtra: number;
  somenteLeitura: boolean; obs: string | null;
}
interface DiaDTO { ano: number; mes: number; dia: number; status: string;
  ausenciaBhe: number; ausenciaAbono: number; feriado: boolean; horasNormaisEsperadas: number; tarefas: TarefaDTO[]; }

const chave = (p: number, f: number, t: number) => `${p}-${f}-${t}`;
const rotuloAlocada = (a: TarefaAlocada) => `${a.projetoDescricao} · ${a.faseDescricao} · ${a.descricaoTarefa}`;
const totLinha = (t: TarefaDTO) => t.horasNc + t.horasCo + t.horasNcExtra + t.horasCoExtra;

export function LancamentoDiaPage() {
  const auth = useAuth();
  const api = useApi();
  const hoje = new Date();
  const [ano] = useState(hoje.getFullYear());
  const [mes] = useState(hoje.getMonth() + 1);
  const [dia] = useState(hoje.getDate());
  const [aAdicionar, setAAdicionar] = useState("");

  const { data: alocadas = [] } = useQuery<TarefaAlocada[]>({
    queryKey: ["tarefas-alocadas", ano, mes, dia], enabled: auth.isAuthenticated,
    queryFn: () => api<TarefaAlocada[]>(`/v1/timesheet/tarefas-alocadas?data=${ano}-${String(mes).padStart(2, "0")}-${String(dia).padStart(2, "0")}`),
  });
  const { data: carregado, isLoading } = useQuery<DiaDTO>({
    queryKey: ["dia", ano, mes, dia], enabled: auth.isAuthenticated,
    queryFn: () => api<DiaDTO>(`/v1/timesheet/dia/${ano}/${mes}/${dia}`),
  });

  const [linhas, setLinhas] = useState<TarefaDTO[]>([]);
  const [ausBhe, setAusBhe] = useState(0);
  const [ausAbn, setAusAbn] = useState(0);
  useEffect(() => {
    setLinhas((carregado?.tarefas ?? []).map((t) => ({ ...t })));
    setAusBhe(carregado?.ausenciaBhe ?? 0);
    setAusAbn(carregado?.ausenciaAbono ?? 0);
  }, [carregado]);

  const esperado = carregado?.horasNormaisEsperadas ?? 8;
  const editavel = (carregado?.status ?? "A") === "A" || (carregado?.status ?? "A") === "R";
  const presentes = new Set(linhas.map((t) => chave(t.cdProjeto, t.seqFase, t.seqTarefa)));
  const disponiveis = alocadas.filter((a) => !presentes.has(chave(a.cdProjeto, a.seqFase, a.seqTarefa)));

  function adicionar() {
    const a = alocadas.find((x) => chave(x.cdProjeto, x.seqFase, x.seqTarefa) === aAdicionar);
    if (!a) return;
    setLinhas((arr) => [...arr, {
      cdProjeto: a.cdProjeto, seqFase: a.seqFase, seqTarefa: a.seqTarefa, descricaoTarefa: rotuloAlocada(a),
      horasNc: 0, horasCo: 0, horasNcExtra: 0, horasCoExtra: 0, somenteLeitura: a.externa, obs: null,
    }]);
    setAAdicionar("");
  }
  function remover(i: number) { setLinhas((arr) => arr.filter((_, j) => j !== i)); }
  function set(i: number, campo: keyof TarefaDTO, v: number) {
    setLinhas((arr) => arr.map((x, j) => (j === i ? { ...x, [campo]: v } : x)));
  }

  const payload = useMemo<DiaDTO>(() => ({
    ano, mes, dia, status: "A", ausenciaBhe: ausBhe, ausenciaAbono: ausAbn,
    feriado: carregado?.feriado ?? false, horasNormaisEsperadas: esperado,
    tarefas: linhas.filter((t) => totLinha(t) > 0),
  }), [linhas, ausBhe, ausAbn, carregado, esperado, ano, mes, dia]);

  async function salvar(p: DiaDTO) { await api("/v1/timesheet/dia", { method: "PUT", body: JSON.stringify(p) }); }

  const { state, dirty, flush } = useAutosave(payload, salvar, { enabled: auth.isAuthenticated && editavel });
  useUnsavedChangesPrompt(dirty, async () => { try { await flush(); return true; } catch { return false; } });

  const totalProjetos = linhas.reduce((s, t) => s + totLinha(t), 0);
  const totalDia = totalProjetos + ausBhe + ausAbn;
  const invalido = linhas.some((t) => [t.horasNc, t.horasCo, t.horasNcExtra, t.horasCoExtra].some((v) => v > 0 && !ehMultiploDeMeia(v)));
  const rotulo = { idle: "", dirty: "alterações não salvas", saving: "salvando…", saved: "salvo", error: "falha ao salvar — tentar de novo" }[state];

  if (isLoading) return <p>Carregando o dia…</p>;
  const numInput = (v: number, on: (n: number) => void, disabled?: boolean) =>
    <input type="number" step={0.5} min={0} value={v} disabled={disabled} onChange={(e) => on(Number(e.target.value))} style={{ width: 64 }} />;

  return (
    <section style={{ maxWidth: 860 }}>
      <h1 style={{ color: "var(--heading)" }}>Lançamento do dia — {String(dia).padStart(2,"0")}/{String(mes).padStart(2,"0")}/{ano}{carregado?.feriado ? " · feriado" : ""}</h1>
      {!editavel && <p style={{ color: "var(--muted)" }}>Dia com status {carregado?.status} — somente leitura.</p>}

      {editavel && (
        <div style={{ display: "flex", gap: 8, alignItems: "center", margin: "8px 0 12px" }}>
          <label style={{ fontSize: 13, color: "var(--muted)" }}>Adicionar tarefa:</label>
          <select value={aAdicionar} onChange={(e) => setAAdicionar(e.target.value)} style={{ flex: 1, padding: "6px 8px", maxWidth: 520 }}>
            <option value="">Escolha um projeto / fase / tarefa…</option>
            {disponiveis.map((a) => <option key={chave(a.cdProjeto, a.seqFase, a.seqTarefa)} value={chave(a.cdProjeto, a.seqFase, a.seqTarefa)}>{rotuloAlocada(a)}{a.externa ? " 🔒" : ""}</option>)}
          </select>
          <button onClick={adicionar} disabled={!aAdicionar}>Adicionar</button>
        </div>
      )}
      {alocadas.length === 0 && <p style={{ color: "var(--muted)" }}>Você não tem tarefas alocadas. Procure o Admin/RH para alocação.</p>}

      <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14 }}>
        <thead><tr style={{ textAlign: "left", background: "var(--surface-2)" }}>
          <th style={{ padding: "6px 8px" }}>Projeto / Fase / Tarefa</th>
          <th style={{ padding: "6px 8px" }}>CO</th><th style={{ padding: "6px 8px" }}>NC</th>
          <th style={{ padding: "6px 8px" }}>CO extra</th><th style={{ padding: "6px 8px" }}>NC extra</th>
          <th style={{ padding: "6px 8px" }}>Total</th><th></th>
        </tr></thead>
        <tbody>
          {linhas.map((t, i) => (
            <tr key={chave(t.cdProjeto, t.seqFase, t.seqTarefa)} style={{ borderTop: "1px solid var(--border)" }}>
              <td style={{ padding: "6px 8px" }}>{t.descricaoTarefa ?? `Projeto ${t.cdProjeto} · ${t.seqFase} · ${t.seqTarefa}`}{t.somenteLeitura ? " 🔒" : ""}</td>
              {(["horasCo","horasNc","horasCoExtra","horasNcExtra"] as (keyof TarefaDTO)[]).map((campo) => (
                <td key={campo} style={{ padding: "6px 8px" }}>{numInput(t[campo] as number, (v) => set(i, campo, v), t.somenteLeitura || !editavel)}</td>
              ))}
              <td style={{ padding: "6px 8px" }}><strong>{totLinha(t)}h</strong></td>
              <td style={{ padding: "6px 8px", textAlign: "right" }}>
                {editavel && !t.somenteLeitura && <button onClick={() => remover(i)} style={{ borderColor: "var(--danger)", color: "var(--danger)" }}>remover</button>}
              </td>
            </tr>
          ))}
          {linhas.length === 0 && <tr><td colSpan={7} style={{ padding: 10, color: "var(--muted)" }}>Nenhuma tarefa no dia. Use "Adicionar tarefa" para lançar horas.</td></tr>}
        </tbody>
      </table>

      <fieldset style={{ border: "1px solid var(--border)", borderRadius: 8, padding: 10, marginTop: 12 }}>
        <legend style={{ fontSize: 12, color: "var(--muted)" }}>Ausências</legend>
        <div style={{ display: "flex", gap: 20, flexWrap: "wrap" }}>
          <label>Ausência BHE (debitada do banco de horas) {numInput(ausBhe, setAusBhe, !editavel)}</label>
          <label>Ausência ABN (abonada) {numInput(ausAbn, setAusAbn, !editavel)}</label>
        </div>
      </fieldset>

      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginTop: 12, flexWrap: "wrap", gap: 8 }}>
        <span style={{ fontSize: 15 }}>
          Projetos: <strong>{totalProjetos}h</strong> + Ausências: <strong>{ausBhe + ausAbn}h</strong> ={" "}
          Total do dia: <strong>{totalDia}h</strong>
          <span style={{ color: totalDia === esperado ? "var(--muted)" : "var(--warning, #8A6D00)", marginLeft: 8 }}>
            / esperado: {esperado}h{carregado?.feriado ? " (feriado)" : ""}
          </span>
          {invalido && <span style={{ color: "var(--danger)" }}> · use múltiplos de 0,5h</span>}
        </span>
        <span style={{ color: state === "error" ? "var(--danger)" : "var(--muted)", fontSize: 13 }}>{rotulo}</span>
      </div>
    </section>
  );
}
