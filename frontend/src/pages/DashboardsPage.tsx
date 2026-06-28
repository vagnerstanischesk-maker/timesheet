import { useQuery } from "@tanstack/react-query";
import { useApi } from "../auth/useApi";

interface ProjetoExec { cdProjeto: number; descricao: string; realizado: number; previsto: number; percExecucao: number; }
interface Resumo { ano: number; mes: number; horasMes: number; percFaturavel: number; saldoBheEquipe: number; projetos: ProjetoExec[]; }

function Kpi({ rotulo, valor }: { rotulo: string; valor: string }) {
  return (
    <div style={{ background: "var(--surface-2)", borderRadius: 8, padding: "12px 14px" }}>
      <div style={{ fontSize: 12, color: "var(--muted)" }}>{rotulo}</div>
      <div style={{ fontSize: 22, fontWeight: 500 }}>{valor}</div>
    </div>
  );
}

export function DashboardsPage() {
  const api = useApi();
  const hoje = new Date();
  const ano = hoje.getFullYear(), mes = hoje.getMonth() + 1;
  const { data, isLoading } = useQuery<Resumo>({
    queryKey: ["dash", ano, mes],
    queryFn: () => api<Resumo>(`/v1/dashboards/resumo/${ano}/${mes}`),
  });
  if (isLoading) return <p>Carregando…</p>;
  if (!data) return <p>Sem dados.</p>;

  return (
    <section>
      <h1 style={{ color: "var(--heading)" }}>Dashboards</h1>
      <p style={{ color: "var(--muted)" }}>Mês {String(mes).padStart(2,"0")}/{ano}</p>
      <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 12, marginBottom: 16 }}>
        <Kpi rotulo="Horas no mês" valor={`${data.horasMes}h`} />
        <Kpi rotulo="% faturável" valor={`${data.percFaturavel}%`} />
        <Kpi rotulo="Saldo BHE da equipe" valor={`${data.saldoBheEquipe}h`} />
      </div>
      <strong>Previsto × realizado por projeto</strong>
      <div style={{ display: "flex", flexDirection: "column", gap: 10, marginTop: 8 }}>
        {data.projetos.map((p) => {
          const pct = Math.min(100, p.percExecucao || 0);
          const cor = pct > 100 ? "#E24B4A" : pct >= 85 ? "#1D9E75" : "#378ADD";
          return (
            <div key={p.cdProjeto}>
              <div style={{ display: "flex", justifyContent: "space-between", fontSize: 13, marginBottom: 4 }}>
                <span>{p.descricao}</span>
                <span style={{ color: "var(--muted)" }}>{p.realizado} / {p.previsto ?? "—"}h · {p.percExecucao}%</span>
              </div>
              <div style={{ height: 10, background: "var(--surface-2)", borderRadius: 999, overflow: "hidden" }}>
                <div style={{ width: `${pct}%`, height: "100%", background: cor }} />
              </div>
            </div>
          );
        })}
      </div>
    </section>
  );
}
