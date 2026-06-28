import { useEffect, useMemo, useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useApi } from "../auth/useApi";
import { referenciaQuinzena, rotuloPeriodo } from "../lib/quinzena";

interface Ponto { dataReferencia: string; percInformado: number; percDerivadoHoras: number; }
interface Acompanhamento { cdProjeto: number; descricao: string;
  horasPeriodo: number; horasAcumuladas: number; horasPrevistas: number;
  percExecucaoHoras: number; ultimoPercInformado: number; historico: Ponto[]; }
interface ProjLista { cdProjeto: number; descricao: string; cdClifor: number | null; nomeCliente: string | null; interno: boolean; }

function Kpi({ rotulo, valor, destaque }: { rotulo: string; valor: string; destaque?: boolean }) {
  return (
    <div style={{ background: destaque ? "#FBEAE0" : "var(--surface-2)", borderRadius: 8, padding: "12px 14px" }}>
      <div style={{ fontSize: 12, color: destaque ? "var(--brand-orange-dark, #9A3600)" : "var(--muted)" }}>{rotulo}</div>
      <div style={{ fontSize: 22, fontWeight: 500, color: destaque ? "var(--brand-orange-dark, #9A3600)" : "var(--ink)" }}>{valor}</div>
    </div>
  );
}

export function AcompanhamentoProjetoPage() {
  const api = useApi();
  const qc = useQueryClient();
  const [cliSel, setCliSel] = useState<string>("");
  const [cdProjeto, setCdProjeto] = useState<number | null>(null);
  const [erro, setErro] = useState<string | null>(null);
  const [percInformado, setPercInformado] = useState<string>("");

  const hoje = new Date();
  const [anoRef, mesRef, periodo] = referenciaQuinzena(hoje.getFullYear(), hoje.getMonth() + 1, hoje.getDate());

  const { data: projetos = [] } = useQuery<ProjLista[]>({ queryKey: ["proj-lista"], queryFn: () => api<ProjLista[]>("/v1/projetos/lista") });
  const clientes = useMemo(() => {
    const map = new Map<string, string>();
    for (const p of projetos) {
      if (p.cdClifor != null) map.set(String(p.cdClifor), p.nomeCliente ?? `Cliente ${p.cdClifor}`);
      else map.set("INT", "Projetos internos");
    }
    return [...map.entries()].sort((a, b) => a[1].localeCompare(b[1]));
  }, [projetos]);
  const projetosFiltrados = useMemo(() => projetos.filter((p) => !cliSel || (cliSel === "INT" ? p.interno : String(p.cdClifor) === cliSel)), [projetos, cliSel]);
  useEffect(() => {
    if (cdProjeto == null && projetosFiltrados.length) setCdProjeto(projetosFiltrados[0].cdProjeto);
    if (cdProjeto != null && projetosFiltrados.length && !projetosFiltrados.some((p) => p.cdProjeto === cdProjeto)) setCdProjeto(projetosFiltrados[0]?.cdProjeto ?? null);
  }, [projetosFiltrados, cdProjeto]);

  const key = ["acompanhamento", cdProjeto, anoRef, mesRef, periodo];
  const { data, isLoading } = useQuery<Acompanhamento>({
    queryKey: key, enabled: cdProjeto != null,
    queryFn: () => api<Acompanhamento>(`/v1/projetos/${cdProjeto}/acompanhamento/${anoRef}/${mesRef}/${periodo}`),
  });
  const refresh = () => qc.invalidateQueries({ queryKey: key });

  const informar = useMutation({
    mutationFn: () => api(`/v1/projetos/${cdProjeto}/acompanhamento/andamento`, { method: "POST", body: JSON.stringify({ percInformado: Number(percInformado) }) }),
    onSuccess: () => { setPercInformado(""); refresh(); }, onError: (e: Error) => setErro(e.message),
  });
  const fechar = useMutation({
    mutationFn: () => api(`/v1/projetos/${cdProjeto}/acompanhamento/${anoRef}/${mesRef}/${periodo}/fechar?percInformado=${encodeURIComponent(percInformado || data?.ultimoPercInformado || 0)}`, { method: "POST" }),
    onSuccess: refresh, onError: (e: Error) => setErro(e.message),
  });

  const desvio = useMemo(() => {
    if (!data) return 0;
    const inf = Number(percInformado) || data.ultimoPercInformado || 0;
    return Math.round((inf - data.percExecucaoHoras) * 100) / 100;
  }, [data, percInformado]);

  return (
    <section>
      <h1 style={{ color: "var(--heading)", margin: 0 }}>Acompanhamento de projeto</h1>
      <div style={{ display: "flex", gap: 10, alignItems: "center", flexWrap: "wrap", margin: "8px 0" }}>
        <label style={{ fontSize: 13, color: "var(--muted)" }}>Cliente
          <select value={cliSel} onChange={(e) => { setCliSel(e.target.value); setCdProjeto(null); }} style={{ marginLeft: 6, padding: "6px 8px", minWidth: 180 }}>
            <option value="">Todos</option>
            {clientes.map(([id, nome]) => <option key={id} value={id}>{nome}</option>)}
          </select>
        </label>
        <label style={{ fontSize: 13, color: "var(--muted)" }}>Projeto
          <select value={cdProjeto ?? ""} onChange={(e) => setCdProjeto(e.target.value ? Number(e.target.value) : null)} style={{ marginLeft: 6, padding: "6px 8px", minWidth: 260 }}>
            <option value="">Selecione…</option>
            {projetosFiltrados.map((p) => <option key={p.cdProjeto} value={p.cdProjeto}>{p.cdProjeto} · {p.descricao}</option>)}
          </select>
        </label>
      </div>
      <p style={{ color: "var(--muted)" }}>{rotuloPeriodo(anoRef, mesRef, periodo)}</p>
      {erro && <p style={{ color: "var(--danger)" }} role="alert">{erro}</p>}
      {cdProjeto == null && <p style={{ color: "var(--muted)" }}>Selecione um cliente e um projeto.</p>}
      {isLoading && <p>Carregando…</p>}

      {data && (
        <>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(4, 1fr)", gap: 12, marginBottom: 16 }}>
            <Kpi rotulo="Horas na quinzena" valor={`${data.horasPeriodo}h`} />
            <Kpi rotulo="Acumulado / previsto" valor={`${data.horasAcumuladas} / ${data.horasPrevistas ?? "—"}h`} />
            <Kpi rotulo="% por horas" valor={`${data.percExecucaoHoras}%`} />
            <Kpi rotulo="% informado (gestor)" valor={`${data.ultimoPercInformado}%`} destaque />
          </div>

          <div style={{ border: "1px solid var(--border)", borderRadius: 12, padding: 14, marginBottom: 16 }}>
            <strong>Informar % de andamento</strong>
            <div style={{ display: "flex", gap: 8, alignItems: "center", marginTop: 8 }}>
              <input type="number" min={0} max={100} step={1} placeholder="% (0–100)" value={percInformado} onChange={(e) => setPercInformado(e.target.value)} style={{ width: 120 }} />
              <button onClick={() => informar.mutate()} disabled={!percInformado}>Registrar %</button>
              <span style={{ color: desvio < 0 ? "var(--danger)" : "var(--muted)", fontSize: 13 }}>desvio vs % por horas: {desvio}%</span>
              <button onClick={() => fechar.mutate()} disabled={!data} style={{ marginLeft: "auto", background: "var(--brand-orange)", color: "#fff", border: "none", borderRadius: 8, padding: "8px 14px" }}>Confirmar % e fechar quinzena</button>
            </div>
          </div>

          <strong>Histórico de % de andamento</strong>
          <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14, marginTop: 8 }}>
            <thead><tr style={{ textAlign: "left", background: "var(--surface-2)" }}>
              <th style={{ padding: "6px 10px" }}>Data</th><th style={{ padding: "6px 10px" }}>% informado</th><th style={{ padding: "6px 10px" }}>% por horas</th>
            </tr></thead>
            <tbody>
              {data.historico.length === 0 && <tr><td colSpan={3} style={{ padding: 10, color: "var(--muted)" }}>Sem registros ainda.</td></tr>}
              {data.historico.map((p, i) => (
                <tr key={i} style={{ borderTop: "1px solid var(--border)" }}>
                  <td style={{ padding: "6px 10px" }}>{p.dataReferencia}</td><td style={{ padding: "6px 10px" }}>{p.percInformado}%</td><td style={{ padding: "6px 10px" }}>{p.percDerivadoHoras}%</td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      )}
    </section>
  );
}
