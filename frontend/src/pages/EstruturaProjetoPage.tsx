import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useApi } from "../auth/useApi";

const STATUS: [string, string][] = [["NIN", "Não iniciada"], ["INI", "Aberta/Iniciada"], ["ENC", "Fechada/Encerrada"]];

interface Fase { seqFase: number | null; descricao: string; status: string; dataInicio: string; dataFim: string;
  horasPrevistas: number | null; horasRealizadas: number | null; percConclusao: number | null;
  valorPrevisto: number | null; valorRealizado: number | null; custoPrevisto: number | null; custoRealizado: number | null; }
interface Tarefa extends Fase { seqTarefa: number | null; externa: boolean; }
interface Aloc { seqFase: number; seqTarefa: number; cdFuncionario: number; nomeFuncionario: string | null; status: string; taxaNormal: number | null; taxaExtra: number | null; }
interface FuncOpt { cdFuncionario: number; nome: string; }
interface ProjOpt { cdProjeto: number; descricao: string; nomeCliente: string | null; interno: boolean; }
interface CliOpt { cdClifor: number; razaoSocial: string; }

const faseVazia: Fase = { seqFase: null, descricao: "", status: "NIN", dataInicio: "", dataFim: "",
  horasPrevistas: null, horasRealizadas: null, percConclusao: null, valorPrevisto: null, valorRealizado: null, custoPrevisto: null, custoRealizado: null };
const tarefaVazia: Tarefa = { ...faseVazia, seqTarefa: null, externa: false };

const ist = { width: "100%", padding: "5px 7px", boxSizing: "border-box" as const, fontSize: 13 };
const col = { border: "1px solid var(--border)", borderRadius: 12, padding: 12 } as const;
const lg = { fontSize: 12, color: "var(--muted)" };
const g2 = { display: "grid", gridTemplateColumns: "1fr 1fr", gap: 6 } as const;
const statusLabel = (s: string) => (STATUS.find(([v]) => v === s)?.[1] ?? s);

export function EstruturaProjetoPage() {
  const api = useApi();
  const qc = useQueryClient();
  const [cd, setCd] = useState<number | null>(null);
  const [cliFiltro, setCliFiltro] = useState<string>("");
  const [fase, setFase] = useState<number | null>(null);
  const [tarefa, setTarefa] = useState<number | null>(null);
  const [erro, setErro] = useState<string | null>(null);
  const [ff, setFf] = useState<Fase>(faseVazia);
  const [tf, setTf] = useState<Tarefa>(tarefaVazia);
  const [funcFiltro, setFuncFiltro] = useState("");
  const [funcSel, setFuncSel] = useState<string>("");
  const [taxa, setTaxa] = useState("");
  const [taxaEx, setTaxaEx] = useState("");
  const [alocStatus, setAlocStatus] = useState("INI");
  const numOrNull = (s: string) => (s ? Number(s) : null);

  const fases = useQuery<Fase[]>({ queryKey: ["fases", cd], enabled: cd != null, queryFn: () => api<Fase[]>(`/v1/cadastros/projetos/${cd}/fases`) });
  const tarefas = useQuery<Tarefa[]>({ queryKey: ["tarefas", cd, fase], enabled: fase != null, queryFn: () => api<Tarefa[]>(`/v1/cadastros/projetos/${cd}/fases/${fase}/tarefas`) });
  const alocs = useQuery<Aloc[]>({ queryKey: ["alocs", cd, fase, tarefa], enabled: fase != null && tarefa != null, queryFn: () => api<Aloc[]>(`/v1/cadastros/projetos/${cd}/fases/${fase}/tarefas/${tarefa}/alocacoes`) });
  const funcs = useQuery<FuncOpt[]>({ queryKey: ["func-nome"], queryFn: () => api<FuncOpt[]>("/v1/cadastros/funcionarios") });
  const clientes = useQuery<CliOpt[]>({ queryKey: ["cli-lista"], queryFn: () => api<CliOpt[]>("/v1/cadastros/clientes") });
  const projetos = useQuery<ProjOpt[]>({ queryKey: ["proj-estrutura", cliFiltro], queryFn: () => api<ProjOpt[]>(`/v1/cadastros/projetos${cliFiltro ? `?cliente=${cliFiltro}` : ""}`) });

  const salvarFase = useMutation({ mutationFn: () => api(`/v1/cadastros/projetos/${cd}/fases`, { method: "PUT", body: JSON.stringify(ff) }),
    onSuccess: () => { setFf(faseVazia); qc.invalidateQueries({ queryKey: ["fases", cd] }); setErro(null); }, onError: (e: Error) => setErro(e.message) });
  const salvarTarefa = useMutation({ mutationFn: () => api(`/v1/cadastros/projetos/${cd}/tarefas`, { method: "PUT", body: JSON.stringify({ ...tf, seqFase: fase }) }),
    onSuccess: () => { setTf(tarefaVazia); qc.invalidateQueries({ queryKey: ["tarefas", cd, fase] }); setErro(null); }, onError: (e: Error) => setErro(e.message) });
  const alocar = useMutation({ mutationFn: () => api(`/v1/cadastros/projetos/${cd}/alocacoes`, { method: "PUT",
      body: JSON.stringify({ seqFase: fase, seqTarefa: tarefa, cdFuncionario: Number(funcSel), status: alocStatus, taxaNormal: numOrNull(taxa), taxaExtra: numOrNull(taxaEx) }) }),
    onSuccess: () => { setFuncSel(""); setFuncFiltro(""); setTaxa(""); setTaxaEx(""); setAlocStatus("NIN"); qc.invalidateQueries({ queryKey: ["alocs", cd, fase, tarefa] }); }, onError: (e: Error) => setErro(e.message) });
  const desalocar = useMutation({ mutationFn: (fn: number) => api(`/v1/cadastros/projetos/${cd}/fases/${fase}/tarefas/${tarefa}/alocacoes/${fn}`, { method: "DELETE" }),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["alocs", cd, fase, tarefa] }), onError: (e: Error) => setErro(e.message) });

  const setF = (k: keyof Fase, v: unknown) => setFf((s) => ({ ...s, [k]: v }));
  const setT = (k: keyof Tarefa, v: unknown) => setTf((s) => ({ ...s, [k]: v }));
  const funcsFiltrados = (funcs.data ?? []).filter((x) => x.nome.toLowerCase().includes(funcFiltro.toLowerCase()));

  const camposValor = (o: Fase, set: (k: keyof Fase, v: unknown) => void) => (<>
    <div style={g2}>
      <label style={lg}>Data início<input type="date" value={o.dataInicio} onChange={(e) => set("dataInicio", e.target.value)} style={ist} /></label>
      <label style={lg}>Data fim<input type="date" value={o.dataFim} onChange={(e) => set("dataFim", e.target.value)} style={ist} /></label>
    </div>
    <div style={g2}>
      <label style={lg}>Horas previstas<input type="number" value={o.horasPrevistas ?? ""} onChange={(e) => set("horasPrevistas", numOrNull(e.target.value))} style={ist} /></label>
      <label style={lg}>Horas realizado<input type="number" value={o.horasRealizadas ?? ""} onChange={(e) => set("horasRealizadas", numOrNull(e.target.value))} style={ist} /></label>
    </div>
    <div style={g2}>
      <label style={lg}>Valor previsto<input type="number" step="0.01" value={o.valorPrevisto ?? ""} onChange={(e) => set("valorPrevisto", numOrNull(e.target.value))} style={ist} /></label>
      <label style={lg}>Valor realizado<input type="number" step="0.01" value={o.valorRealizado ?? ""} onChange={(e) => set("valorRealizado", numOrNull(e.target.value))} style={ist} /></label>
    </div>
    <div style={g2}>
      <label style={lg}>Custo previsto<input type="number" step="0.01" value={o.custoPrevisto ?? ""} onChange={(e) => set("custoPrevisto", numOrNull(e.target.value))} style={ist} /></label>
      <label style={lg}>Custo realizado<input type="number" step="0.01" value={o.custoRealizado ?? ""} onChange={(e) => set("custoRealizado", numOrNull(e.target.value))} style={ist} /></label>
    </div>
    <label style={lg}>% de conclusão<input type="number" value={o.percConclusao ?? ""} onChange={(e) => set("percConclusao", numOrNull(e.target.value))} style={ist} /></label>
  </>);

  return (
    <section>
      <h1 style={{ color: "var(--heading)" }}>Estrutura do projeto</h1>
      <p style={{ ...lg, marginTop: -6 }}>Cadastro de <strong>fases</strong> e <strong>tarefas</strong> (datas, status, horas, valores e custos previstos/realizados) e <strong>alocação</strong> de funcionários (por nome, com taxa e status).</p>
      <div style={{ display: "flex", gap: 10, alignItems: "flex-end", flexWrap: "wrap", marginTop: 4 }}>
        <label style={lg}>Cliente
          <select value={cliFiltro} onChange={(e) => { setCliFiltro(e.target.value); setCd(null); setFase(null); setTarefa(null); }} style={{ ...ist, minWidth: 200 }}>
            <option value="">Todos</option>
            {(clientes.data ?? []).map((c) => <option key={c.cdClifor} value={c.cdClifor}>{c.razaoSocial}</option>)}
          </select>
        </label>
        <label style={lg}>Projeto
          <select value={cd ?? ""} onChange={(e) => { setCd(e.target.value ? Number(e.target.value) : null); setFase(null); setTarefa(null); setFf(faseVazia); setTf(tarefaVazia); }} style={{ ...ist, minWidth: 280 }}>
            <option value="">Selecione um projeto…</option>
            {(projetos.data ?? []).map((p) => <option key={p.cdProjeto} value={p.cdProjeto}>{p.cdProjeto} · {p.descricao}{p.nomeCliente ? ` — ${p.nomeCliente}` : (p.interno ? " — Interno" : "")}</option>)}
          </select>
        </label>
      </div>
      {erro && <p style={{ color: "var(--danger)" }}>{erro}</p>}

      {cd == null && <p style={{ ...lg, marginTop: 12 }}>Selecione um projeto para montar fases, tarefas e alocação.</p>}
      {cd != null && <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 12, marginTop: 12, alignItems: "start" }}>
        {/* ---- Fases ---- */}
        <div style={col}>
          <strong>Fases</strong>
          <ul style={{ listStyle: "none", padding: 0 }}>{(fases.data ?? []).map((f) => (
            <li key={f.seqFase} style={{ display: "flex", alignItems: "center", gap: 4 }}>
              <button onClick={() => { setFase(f.seqFase!); setTarefa(null); }} style={{ flex: 1, background: fase === f.seqFase ? "var(--surface-2)" : "transparent", border: "none", textAlign: "left", padding: "4px 6px" }}>
                {f.seqFase}. {f.descricao} <span style={lg}>· {statusLabel(f.status)}</span>
              </button>
              <button title="Editar" onClick={() => setFf({ ...faseVazia, ...f })}>✎</button>
            </li>))}</ul>
          <div style={{ borderTop: "1px solid var(--border)", paddingTop: 8, display: "grid", gap: 6 }}>
            <span style={lg}>{ff.seqFase ? `Editar fase ${ff.seqFase}` : "Nova fase"}</span>
            <input placeholder="Descrição" value={ff.descricao} onChange={(e) => setF("descricao", e.target.value)} style={ist} />
            <select value={ff.status} onChange={(e) => setF("status", e.target.value)} style={ist}>{STATUS.map(([v, l]) => <option key={v} value={v}>{l}</option>)}</select>
            {camposValor(ff, setF)}
            <div style={{ display: "flex", gap: 6 }}>
              {ff.seqFase && <button onClick={() => setFf(faseVazia)}>novo</button>}
              <button onClick={() => salvarFase.mutate()} disabled={!ff.descricao} style={{ flex: 1, background: "var(--brand-orange)", color: "#fff", border: "none", borderRadius: 6 }}>Salvar fase</button>
            </div>
          </div>
        </div>

        {/* ---- Tarefas ---- */}
        <div style={col}>
          <strong>Tarefas {fase != null ? `(fase ${fase})` : ""}</strong>
          {fase == null ? <p style={lg}>Selecione uma fase.</p> : <>
            <ul style={{ listStyle: "none", padding: 0 }}>{(tarefas.data ?? []).map((t) => (
              <li key={t.seqTarefa} style={{ display: "flex", alignItems: "center", gap: 4 }}>
                <button onClick={() => setTarefa(t.seqTarefa!)} style={{ flex: 1, background: tarefa === t.seqTarefa ? "var(--surface-2)" : "transparent", border: "none", textAlign: "left", padding: "4px 6px" }}>
                  {t.seqTarefa}. {t.descricao}{t.externa ? " 🔒" : ""} <span style={lg}>· {statusLabel(t.status)}</span>
                </button>
                <button title="Editar" onClick={() => setTf({ ...tarefaVazia, ...t })}>✎</button>
              </li>))}</ul>
            <div style={{ borderTop: "1px solid var(--border)", paddingTop: 8, display: "grid", gap: 6 }}>
              <span style={lg}>{tf.seqTarefa ? `Editar tarefa ${tf.seqTarefa}` : "Nova tarefa"}</span>
              <input placeholder="Descrição" value={tf.descricao} onChange={(e) => setT("descricao", e.target.value)} style={ist} />
              <select value={tf.status} onChange={(e) => setT("status", e.target.value)} style={ist}>{STATUS.map(([v, l]) => <option key={v} value={v}>{l}</option>)}</select>
              {camposValor(tf, setT as (k: keyof Fase, v: unknown) => void)}
              <label style={{ fontSize: 12 }}><input type="checkbox" checked={tf.externa} onChange={(e) => setT("externa", e.target.checked)} /> externa (read-only no log)</label>
              <div style={{ display: "flex", gap: 6 }}>
                {tf.seqTarefa && <button onClick={() => setTf(tarefaVazia)}>novo</button>}
                <button onClick={() => salvarTarefa.mutate()} disabled={!tf.descricao} style={{ flex: 1, background: "var(--brand-orange)", color: "#fff", border: "none", borderRadius: 6 }}>Salvar tarefa</button>
              </div>
            </div>
          </>}
        </div>

        {/* ---- Alocação ---- */}
        <div style={col}>
          <strong>Alocação {tarefa != null ? `(tarefa ${tarefa})` : ""}</strong>
          {tarefa == null ? <p style={lg}>Selecione uma tarefa.</p> : <>
            <ul style={{ listStyle: "none", padding: 0 }}>{(alocs.data ?? []).map((a) => (
              <li key={a.cdFuncionario} style={{ display: "flex", justifyContent: "space-between", alignItems: "center", padding: "4px 6px", gap: 6 }}>
                <span>{a.nomeFuncionario ?? `func ${a.cdFuncionario}`} <span style={lg}>· {statusLabel(a.status)}{a.taxaNormal != null ? ` · R$${a.taxaNormal}/h` : ""}</span></span>
                <button onClick={() => desalocar.mutate(a.cdFuncionario)} style={{ borderColor: "var(--danger)", color: "var(--danger)" }}>remover</button>
              </li>))}</ul>
            <div style={{ borderTop: "1px solid var(--border)", paddingTop: 8, display: "grid", gap: 6 }}>
              <span style={lg}>Alocar funcionário (pesquise por nome)</span>
              <input placeholder="filtrar por nome…" value={funcFiltro} onChange={(e) => setFuncFiltro(e.target.value)} style={ist} />
              <select value={funcSel} onChange={(e) => setFuncSel(e.target.value)} style={ist}>
                <option value="">Selecione…</option>
                {funcsFiltrados.map((x) => <option key={x.cdFuncionario} value={x.cdFuncionario}>{x.nome}</option>)}
              </select>
              <div style={g2}>
                <label style={lg}>Taxa horária<input type="number" step="0.01" value={taxa} onChange={(e) => setTaxa(e.target.value)} style={ist} /></label>
                <label style={lg}>Taxa extra<input type="number" step="0.01" value={taxaEx} onChange={(e) => setTaxaEx(e.target.value)} style={ist} /></label>
              </div>
              <select value={alocStatus} onChange={(e) => setAlocStatus(e.target.value)} style={ist}>{STATUS.map(([v, l]) => <option key={v} value={v}>{l}</option>)}</select>
              <button onClick={() => alocar.mutate()} disabled={!funcSel} style={{ background: "var(--brand-orange)", color: "#fff", border: "none", borderRadius: 6, padding: "6px" }}>Alocar</button>
            </div>
          </>}
        </div>
      </div>}
    </section>
  );
}
