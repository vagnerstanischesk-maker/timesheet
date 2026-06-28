import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useApi } from "../auth/useApi";

interface Feriado { id: number|null; nome: string; ano: number|null; mes: number|null; dia: number|null;
  tipo: string; filial: number|null; horasATrabalhar: number|null; inativo: boolean; }
interface Lookup { id: number|string; descricao: string; }

// Tipo do feriado (código no banco -> rótulo). Local (LF/LN) exige filial.
const TIPOS: [string, string][] = [
  ["NF", "Nacional Fixo"], ["LF", "Local Fixo"], ["NN", "Nacional Não Fixo"], ["LN", "Local Não Fixo"],
];
const tipoLabel = (t: string) => TIPOS.find(([v]) => v === t)?.[1] ?? t;
const ehLocal = (t: string) => t === "LF" || t === "LN";
const vazio: Feriado = { id:null, nome:"", ano:null, mes:null, dia:null, tipo:"NF", filial:null, horasATrabalhar:0, inativo:false };

export function CadastroFeriadosPage() {
  const api = useApi(); const qc = useQueryClient();
  const [f, setF] = useState<Feriado>(vazio); const [erro, setErro] = useState<string|null>(null);
  const { data: lista = [] } = useQuery<Feriado[]>({ queryKey:["fer"], queryFn:()=>api<Feriado[]>("/v1/cadastros/feriados") });
  const { data: filiais = [] } = useQuery<Lookup[]>({ queryKey:["filiais"], queryFn:()=>api<Lookup[]>("/v1/cadastros/listas/filiais") });
  const salvar = useMutation({ mutationFn:(x:Feriado)=>api("/v1/cadastros/feriados",{method:"PUT",body:JSON.stringify(x)}),
    onSuccess:()=>{qc.invalidateQueries({queryKey:["fer"]});setF(vazio);setErro(null);}, onError:(e:Error)=>setErro(e.message) });
  const excluir = useMutation({ mutationFn:(id:number)=>api(`/v1/cadastros/feriados/${id}`,{method:"DELETE"}),
    onSuccess:()=>qc.invalidateQueries({queryKey:["fer"]}), onError:(e:Error)=>setErro(e.message) });
  const set=(k:keyof Feriado,v:unknown)=>setF(s=>({...s,[k]:v}));
  const numOrNull = (s:string)=> (s ? Number(s) : null);

  return (
    <section style={{display:"grid",gridTemplateColumns:"1.3fr 1fr",gap:16}}>
      <div>
        <h1 style={{color:"var(--heading)"}}>Feriados</h1>
        <table style={{width:"100%",borderCollapse:"collapse",fontSize:14}}>
          <thead><tr style={{textAlign:"left",background:"var(--surface-2)"}}><th style={{padding:"6px 8px"}}>Data</th><th>Nome</th><th>Tipo</th><th>Horas</th><th></th></tr></thead>
          <tbody>{lista.map(x=>(
            <tr key={x.id} style={{borderTop:"1px solid var(--border)"}}>
              <td style={{padding:"6px 8px"}}>{String(x.dia).padStart(2,"0")}/{String(x.mes).padStart(2,"0")}{x.ano?`/${x.ano}`:""}</td>
              <td>{x.nome}{x.inativo?" (inativo)":""}</td><td>{tipoLabel(x.tipo)}</td>
              <td>{x.horasATrabalhar ?? 0}h</td>
              <td style={{textAlign:"right"}}><button onClick={()=>setF({...vazio,...x})}>Editar</button>{" "}<button onClick={()=>x.id&&excluir.mutate(x.id)} style={{borderColor:"var(--danger)",color:"var(--danger)"}}>Excluir</button></td>
            </tr>))}</tbody>
        </table>
      </div>
      <div style={{border:"1px solid var(--border)",borderRadius:12,padding:14}}>
        <strong>{f.id?"Editar feriado":"Novo feriado"}</strong>
        {erro&&<p style={{color:"var(--danger)"}}>{erro}</p>}
        <div style={{display:"grid",gap:8,marginTop:8}}>
          <label>Nome <input value={f.nome} onChange={e=>set("nome",e.target.value)} style={{width:"100%"}}/></label>
          <div style={{display:"flex",gap:8}}>
            <label>Dia <input type="number" min={1} max={31} value={f.dia??""} onChange={e=>set("dia",numOrNull(e.target.value))} style={{width:60}}/></label>
            <label>Mês <input type="number" min={1} max={12} value={f.mes??""} onChange={e=>set("mes",numOrNull(e.target.value))} style={{width:60}}/></label>
            <label>Ano <input type="number" value={f.ano??""} onChange={e=>set("ano",numOrNull(e.target.value))} style={{width:80}}/></label>
          </div>
          <label>Tipo
            <select value={f.tipo} onChange={e=>set("tipo",e.target.value)} style={{width:"100%"}}>
              {TIPOS.map(([v,l])=> <option key={v} value={v}>{l}</option>)}
            </select>
          </label>
          {ehLocal(f.tipo) && (
            <label>Filial (obrigatória para feriado local)
              <select value={f.filial??""} onChange={e=>set("filial",numOrNull(e.target.value))} style={{width:"100%"}}>
                <option value="">Selecione…</option>
                {filiais.map(fl=> <option key={fl.id} value={fl.id as number}>{fl.descricao}</option>)}
              </select>
            </label>
          )}
          <label>Horas a trabalhar (0 = dia inteiro de folga)
            <input type="number" step="0.5" min={0} max={24} value={f.horasATrabalhar??0} onChange={e=>set("horasATrabalhar",numOrNull(e.target.value))} style={{width:"100%"}}/>
          </label>
          <small style={{color:"var(--muted)"}}>Use as horas a trabalhar quando o feriado for parcial (ex.: meio expediente = 4h).</small>
          <label><input type="checkbox" checked={f.inativo} onChange={e=>set("inativo",e.target.checked)}/> Inativo</label>
          <div style={{display:"flex",gap:8}}><button onClick={()=>{setF(vazio);setErro(null);}}>Limpar</button>
            <button onClick={()=>salvar.mutate(f)} style={{background:"var(--brand-orange)",color:"#fff",border:"none",borderRadius:8,padding:"8px 14px"}}>Salvar</button></div>
        </div>
      </div>
    </section>
  );
}
