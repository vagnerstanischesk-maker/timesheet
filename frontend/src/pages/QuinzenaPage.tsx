import { useEffect, useRef, useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useApi } from "../auth/useApi";
import { referenciaQuinzena, rotuloPeriodo, periodoAnterior, periodoSeguinte } from "../lib/quinzena";
import { GradeLog, DiaMeta, Linha, Tipo, linhasFromApi, LegendaStatus } from "../lib/gradeLog";

interface Alocada { cdProjeto: number; projetoDescricao: string; seqFase: number; faseDescricao: string; seqTarefa: number; descricaoTarefa: string; externa: boolean; }
interface EdicaoResp { nome: string; saldoBhe50: number; saldoBhe100: number;
  dias: { ano: number; mes: number; dia: number; diaSemana: string; feriado: boolean; status: string; horasNormaisEsperadas: number; ausenciaBhe: number; ausenciaAbono: number; }[];
  tarefas: { cdProjeto: number; projetoDescricao: string; seqFase: number; faseDescricao: string; seqTarefa: number; descricaoTarefa: string; externa: boolean; celulas: { nc: number; co: number; ncExtra: number; coExtra: number }[]; }[];
}
const chave = (p: number, f: number, t: number) => `${p}-${f}-${t}`;
const editavelDia = (s: string) => s === "A" || s === "R";

export function QuinzenaPage() {
  const api = useApi();
  const qc = useQueryClient();
  const hoje = new Date();
  const [ref, setRef] = useState<[number, number, number]>(() => referenciaQuinzena(hoje.getFullYear(), hoje.getMonth() + 1, hoje.getDate()));
  const [anoRef, mesRef, periodo] = ref;
  const dataRef = `${anoRef}-${String(mesRef).padStart(2, "0")}-${periodo === 1 ? "01" : "16"}`;
  const [erro, setErro] = useState<string | null>(null);
  const [saveState, setSaveState] = useState<"idle" | "saving" | "saved" | "error">("idle");
  const [aAdicionar, setAAdicionar] = useState("");

  const { data, isLoading } = useQuery<EdicaoResp>({
    queryKey: ["quinzena-edicao", anoRef, mesRef, periodo],
    queryFn: () => api<EdicaoResp>(`/v1/timesheet/quinzena/${anoRef}/${mesRef}/${periodo}/edicao`),
  });
  const { data: alocadas = [] } = useQuery<Alocada[]>({ queryKey: ["tarefas-alocadas", dataRef], queryFn: () => api<Alocada[]>(`/v1/timesheet/tarefas-alocadas?data=${dataRef}`) });

  const [dias, setDias] = useState<DiaMeta[]>([]);
  const [linhas, setLinhas] = useState<Linha[]>([]);
  const sujos = useRef<Set<number>>(new Set());
  const timer = useRef<ReturnType<typeof setTimeout> | null>(null);
  const diasRef = useRef<DiaMeta[]>([]); const linhasRef = useRef<Linha[]>([]);
  useEffect(() => { diasRef.current = dias; }, [dias]);
  useEffect(() => { linhasRef.current = linhas; }, [linhas]);

  useEffect(() => {
    if (!data) return;
    setDias(data.dias.map((d) => ({ ano: d.ano, mes: d.mes, dia: d.dia, diaSemana: d.diaSemana, feriado: d.feriado, status: d.status,
      horasNormaisEsperadas: d.horasNormaisEsperadas, ausBhe: d.ausenciaBhe ?? 0, ausAbn: d.ausenciaAbono ?? 0 })));
    setLinhas(linhasFromApi(data.tarefas));
  }, [data]);

  function agendaSalvar() { setSaveState("saving"); if (timer.current) clearTimeout(timer.current); timer.current = setTimeout(salvarSujos, 800); }
  async function salvarSujos() {
    const idxs = [...sujos.current]; sujos.current = new Set();
    try {
      for (const i of idxs) {
        const dm = diasRef.current[i]; if (!dm || !editavelDia(dm.status)) continue;
        const tarefas = linhasRef.current.filter((l) => !l.externa).flatMap((l) => {
          const co = l.tipos.CO ? l.horas.CO[i] || 0 : 0, nc = l.tipos.NC ? l.horas.NC[i] || 0 : 0;
          const coEx = l.tipos.COx ? l.horas.COx[i] || 0 : 0, ncEx = l.tipos.NCx ? l.horas.NCx[i] || 0 : 0;
          if (!(co || nc || coEx || ncEx)) return [];
          return [{ cdProjeto: l.cdProjeto, seqFase: l.seqFase, seqTarefa: l.seqTarefa, descricaoTarefa: l.descricao, horasCo: co, horasNc: nc, horasCoExtra: coEx, horasNcExtra: ncEx, somenteLeitura: false, obs: null }];
        });
        await api("/v1/timesheet/dia", { method: "PUT", body: JSON.stringify({ ano: dm.ano, mes: dm.mes, dia: dm.dia, status: "A",
          ausenciaBhe: dm.ausBhe, ausenciaAbono: dm.ausAbn, feriado: dm.feriado, horasNormaisEsperadas: dm.horasNormaisEsperadas, tarefas }) });
      }
      setSaveState("saved"); setErro(null);
    } catch (e) { setSaveState("error"); setErro((e as Error).message); }
  }
  const sujo = (i: number) => { sujos.current.add(i); agendaSalvar(); };
  const todosSujos = () => { dias.forEach((_, i) => sujos.current.add(i)); agendaSalvar(); };

  const onHora = (li: number, t: Tipo, di: number, v: number) => { setLinhas((a) => a.map((l, j) => j === li ? { ...l, horas: { ...l.horas, [t]: l.horas[t].map((x, k) => k === di ? v : x) } } : l)); sujo(di); };
  const onToggleTipo = (li: number, t: Tipo) => { setLinhas((a) => a.map((l, j) => j === li ? { ...l, tipos: { ...l.tipos, [t]: !l.tipos[t] } } : l)); todosSujos(); };
  const onAus = (di: number, campo: "ausBhe" | "ausAbn", v: number) => { setDias((a) => a.map((d, j) => j === di ? { ...d, [campo]: v } : d)); sujo(di); };
  const onRemover = (li: number) => { setLinhas((a) => a.filter((_, j) => j !== li)); todosSujos(); };

  function adicionar() {
    const a = alocadas.find((x) => chave(x.cdProjeto, x.seqFase, x.seqTarefa) === aAdicionar); if (!a) return;
    if (linhas.some((l) => chave(l.cdProjeto, l.seqFase, l.seqTarefa) === aAdicionar)) { setAAdicionar(""); return; }
    const zeros = () => dias.map(() => 0);
    setLinhas((arr) => [...arr, { cdProjeto: a.cdProjeto, seqFase: a.seqFase, seqTarefa: a.seqTarefa,
      descricao: `${a.projetoDescricao} · ${a.faseDescricao} · ${a.descricaoTarefa}`, externa: a.externa,
      tipos: { CO: false, NC: true, COx: false, NCx: false }, horas: { CO: zeros(), NC: zeros(), COx: zeros(), NCx: zeros() } }]);
    setAAdicionar("");
  }

  const fechar = useMutation({
    mutationFn: () => api(`/v1/timesheet/quinzena/${anoRef}/${mesRef}/${periodo}/fechar`, { method: "POST" }),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["quinzena-edicao", anoRef, mesRef, periodo] }), onError: (e: Error) => setErro(e.message),
  });

  if (isLoading || !data) return <p>Carregando quinzena…</p>;
  const temAberto = dias.some((d) => d.status === "A");
  const disp = alocadas.filter((a) => !linhas.some((l) => chave(l.cdProjeto, l.seqFase, l.seqTarefa) === chave(a.cdProjeto, a.seqFase, a.seqTarefa)));
  const rotuloSave = { idle: "", saving: "salvando…", saved: "salvo", error: "falha ao salvar" }[saveState];

  return (
    <section>
      <header style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: 8 }}>
        <h1 style={{ color: "var(--heading)", margin: 0 }}>Log da quinzena — {data.nome}</h1>
        <span style={{ color: "var(--muted)", fontSize: 13 }}>BHE 50% <strong>{data.saldoBhe50}h</strong> · 100% <strong>{data.saldoBhe100}h</strong></span>
      </header>
      <div style={{ display: "flex", gap: 8, alignItems: "center", margin: "6px 0" }}>
        <button onClick={() => setRef(periodoAnterior(anoRef, mesRef, periodo))} title="Quinzena anterior">‹ Anterior</button>
        <strong style={{ minWidth: 200, textAlign: "center" }}>{rotuloPeriodo(anoRef, mesRef, periodo)}</strong>
        <button onClick={() => setRef(periodoSeguinte(anoRef, mesRef, periodo))} title="Próxima quinzena">Próxima ›</button>
      </div>
      <LegendaStatus />
      {erro && <p style={{ color: "var(--danger)" }} role="alert">{erro}</p>}

      <div style={{ display: "flex", gap: 8, alignItems: "center", margin: "8px 0 12px" }}>
        <label style={{ fontSize: 13, color: "var(--muted)" }}>Adicionar tarefa:</label>
        <select value={aAdicionar} onChange={(e) => setAAdicionar(e.target.value)} style={{ flex: 1, padding: "6px 8px", maxWidth: 460 }}>
          <option value="">Escolha um projeto / fase / tarefa…</option>
          {disp.map((a) => <option key={chave(a.cdProjeto, a.seqFase, a.seqTarefa)} value={chave(a.cdProjeto, a.seqFase, a.seqTarefa)}>{a.projetoDescricao} · {a.faseDescricao} · {a.descricaoTarefa}{a.externa ? " 🔒" : ""}</option>)}
        </select>
        <button onClick={adicionar} disabled={!aAdicionar}>Adicionar</button>
        <span style={{ marginLeft: "auto", fontSize: 13, color: saveState === "error" ? "var(--danger)" : "var(--muted)" }}>{rotuloSave}</span>
      </div>
      <p style={{ fontSize: 12, color: "var(--muted)", marginTop: -4 }}>Use os botões CO/NC/CO ex/NC ex em cada tarefa para lançar mais de um tipo no mesmo dia.</p>

      <GradeLog dias={dias} linhas={linhas} modo="edicao" onHora={onHora} onToggleTipo={onToggleTipo} onAus={onAus} onRemover={onRemover} />

      <div style={{ marginTop: 12 }}>
        <button onClick={() => fechar.mutate()} disabled={!temAberto} style={{ background: "var(--brand-orange)", color: "#fff", border: "none", borderRadius: 8, padding: "9px 16px" }}>Fechar quinzena</button>
        {!temAberto && <span style={{ color: "var(--muted)", marginLeft: 8, fontSize: 13 }}>nenhum dia em aberto</span>}
      </div>
    </section>
  );
}
