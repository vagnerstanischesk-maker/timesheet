import { useMemo, useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useApi } from "../auth/useApi";
import { referenciaQuinzena, rotuloPeriodo, periodoAnterior, periodoSeguinte } from "../lib/quinzena";
import { GradeLog, DiaMeta, linhasFromApi, LegendaStatus } from "../lib/gradeLog";

interface Pendente { funcionarioId: number; nome: string; ano: number; mes: number; dia: number; }
interface Grupo { funcionarioId: number; nome: string; anoRef: number; mesRef: number; periodo: number; qtd: number; }
interface EdicaoResp { nome: string; saldoBhe50: number; saldoBhe100: number;
  dias: { ano: number; mes: number; dia: number; diaSemana: string; feriado: boolean; status: string; horasNormaisEsperadas: number; ausenciaBhe: number; ausenciaAbono: number; }[];
  tarefas: any[]; }

// Total de horas do período: horas lançadas (NC/CO/extras) + ausências (BHE/ABN), igual ao "Total do dia" da grade.
function totalHoras(d?: EdicaoResp): number {
  if (!d) return 0;
  let t = 0;
  for (const tar of d.tarefas) for (const c of (tar.celulas ?? [])) t += (c.nc || 0) + (c.co || 0) + (c.ncExtra || 0) + (c.coExtra || 0);
  for (const dia of d.dias) t += (dia.ausenciaBhe || 0) + (dia.ausenciaAbono || 0);
  return t;
}
const fmtH = (n: number) => Number(n.toFixed(1)).toLocaleString("pt-BR");

export function AprovacaoPage() {
  const api = useApi();
  const [erro, setErro] = useState<string | null>(null);

  const { data: pendentes = [], isLoading } = useQuery<Pendente[]>({ queryKey: ["pendentes"], queryFn: () => api<Pendente[]>("/v1/aprovacao/pendentes") });

  const grupos = useMemo<Grupo[]>(() => {
    const map = new Map<string, Grupo>();
    for (const p of pendentes) {
      const [anoRef, mesRef, periodo] = referenciaQuinzena(p.ano, p.mes, p.dia);
      const k = `${p.funcionarioId}-${anoRef}-${mesRef}-${periodo}`;
      if (!map.has(k)) map.set(k, { funcionarioId: p.funcionarioId, nome: p.nome, anoRef, mesRef, periodo, qtd: 0 });
      map.get(k)!.qtd++;
    }
    return [...map.values()].sort((a, b) => a.nome.localeCompare(b.nome));
  }, [pendentes]);

  if (isLoading) return <p>Carregando pendências…</p>;
  return (
    <section>
      <h1 style={{ color: "var(--heading)" }}>Aprovação de logs</h1>
      {erro && <p style={{ color: "var(--danger)" }} role="alert">{erro}</p>}
      {grupos.length === 0 && <p style={{ color: "var(--muted)" }}>Nenhuma pendência de aprovação.</p>}
      {grupos.map((g) => <GrupoCard key={`${g.funcionarioId}-${g.anoRef}-${g.mesRef}-${g.periodo}`} g={g} onErro={setErro} />)}
    </section>
  );
}

function GrupoCard({ g, onErro }: { g: Grupo; onErro: (s: string | null) => void }) {
  const api = useApi();
  const qc = useQueryClient();
  const [justif, setJustif] = useState<Record<number, string>>({});
  const [ref, setRef] = useState<[number, number, number]>([g.anoRef, g.mesRef, g.periodo]);
  const [anoRef, mesRef, periodo] = ref;
  const ehPendencia = anoRef === g.anoRef && mesRef === g.mesRef && periodo === g.periodo;
  const chaveQ = ["aprov-edicao", g.funcionarioId, anoRef, mesRef, periodo];
  const { data } = useQuery<EdicaoResp>({ queryKey: chaveQ, queryFn: () => api<EdicaoResp>(`/v1/aprovacao/edicao/${g.funcionarioId}/${anoRef}/${mesRef}/${periodo}`) });
  // Na 2ª quinzena, busca também a 1ª do mesmo mês para o total do mês (1ª + 2ª).
  const { data: dataP1 } = useQuery<EdicaoResp>({
    queryKey: ["aprov-edicao", g.funcionarioId, anoRef, mesRef, 1], enabled: periodo === 2,
    queryFn: () => api<EdicaoResp>(`/v1/aprovacao/edicao/${g.funcionarioId}/${anoRef}/${mesRef}/1`),
  });
  const totalQuinzena = totalHoras(data);
  const totalMes = periodo === 1 ? totalQuinzena : totalHoras(dataP1) + totalQuinzena;
  const invalidar = () => { qc.invalidateQueries({ queryKey: ["pendentes"] }); qc.invalidateQueries({ queryKey: chaveQ }); };

  const dias: DiaMeta[] = (data?.dias ?? []).map((d) => ({ ano: d.ano, mes: d.mes, dia: d.dia, diaSemana: d.diaSemana, feriado: d.feriado, status: d.status, horasNormaisEsperadas: d.horasNormaisEsperadas, ausBhe: d.ausenciaBhe ?? 0, ausAbn: d.ausenciaAbono ?? 0 }));
  const linhas = data ? linhasFromApi(data.tarefas) : [];

  const aprovarDia = useMutation({ mutationFn: (d: DiaMeta) => api(`/v1/aprovacao/dia/${g.funcionarioId}/${d.ano}/${d.mes}/${d.dia}/aprovar`, { method: "POST" }), onSuccess: invalidar, onError: (e: Error) => onErro(e.message) });
  const rejeitarDia = useMutation({
    mutationFn: ({ d, obs }: { d: DiaMeta; obs: string }) => {
      if (!obs.trim()) throw new Error("Justificativa é obrigatória ao rejeitar o dia.");
      return api(`/v1/aprovacao/dia/${g.funcionarioId}/${d.ano}/${d.mes}/${d.dia}/rejeitar`, { method: "POST", body: JSON.stringify({ observacao: obs }) });
    }, onSuccess: invalidar, onError: (e: Error) => onErro(e.message),
  });
  const aprovarQuinzena = useMutation({ mutationFn: () => api(`/v1/aprovacao/quinzena/${g.funcionarioId}/${anoRef}/${mesRef}/${periodo}`, { method: "POST" }), onSuccess: invalidar, onError: (e: Error) => onErro(e.message) });

  return (
    <article style={{ border: "1px solid var(--border)", borderRadius: 12, padding: 14, marginBottom: 12 }}>
      <header style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: 8 }}>
        <div style={{ display: "flex", gap: 8, alignItems: "center", flexWrap: "wrap" }}>
          <strong>{g.nome}</strong>
          <button onClick={() => setRef(periodoAnterior(anoRef, mesRef, periodo))} title="Quinzena anterior">‹</button>
          <span style={{ color: "var(--muted)", minWidth: 150, textAlign: "center" }}>{rotuloPeriodo(anoRef, mesRef, periodo)}</span>
          <button onClick={() => setRef(periodoSeguinte(anoRef, mesRef, periodo))} title="Próxima quinzena">›</button>
          {ehPendencia ? <span style={{ color: "var(--muted)", fontSize: 13 }}>· {g.qtd} dia(s) pendente(s)</span>
            : <button onClick={() => setRef([g.anoRef, g.mesRef, g.periodo])} style={{ fontSize: 12 }}>voltar à pendência</button>}
        </div>
        <button onClick={() => aprovarQuinzena.mutate()} style={{ background: "var(--brand-orange)", color: "#fff", border: "none", borderRadius: 8, padding: "8px 14px" }}>Aprovar quinzena toda</button>
      </header>
      <LegendaStatus />
      <p style={{ fontSize: 13, margin: "2px 0 6px" }}>
        Total da quinzena: <strong>{fmtH(totalQuinzena)}h</strong>
        <span style={{ color: "var(--muted)" }}> · </span>
        Total do mês: <strong>{fmtH(totalMes)}h</strong>
        {periodo === 1 && <span style={{ color: "var(--muted)", fontSize: 12 }}> (1ª quinzena — igual à quinzena)</span>}
        {periodo === 2 && <span style={{ color: "var(--muted)", fontSize: 12 }}> (1ª + 2ª quinzena)</span>}
        <span style={{ color: "var(--muted)" }}> · </span>
        Saldo BHE 50%: <strong>{fmtH(data?.saldoBhe50 ?? 0)}h</strong>
        <span style={{ color: "var(--muted)" }}> · </span>
        100%: <strong>{fmtH(data?.saldoBhe100 ?? 0)}h</strong>
      </p>
      <p style={{ fontSize: 12, color: "var(--muted)" }}>Você pode aprovar/rejeitar cada dia individualmente nas linhas abaixo (✓ aprova, ✗ rejeita com justificativa).</p>
      {data && <GradeLog dias={dias} linhas={linhas} modo="aprovacao"
        onAprovarDia={(di) => aprovarDia.mutate(dias[di])}
        onRejeitarDia={(di, obs) => rejeitarDia.mutate({ d: dias[di], obs })}
        justif={justif} setJustif={(di, v) => setJustif((s) => ({ ...s, [di]: v }))} />}
    </article>
  );
}
