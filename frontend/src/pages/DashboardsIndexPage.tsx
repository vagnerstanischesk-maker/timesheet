import { Link } from "react-router-dom";

interface Item { titulo: string; descricao: string; para: string; }
const DASHBOARDS: Item[] = [
  { titulo: "Visão geral", descricao: "KPIs do mês: horas, % faturável, saldo de BHE da equipe e previsto × realizado por projeto.", para: "/dashboards/geral" },
];

export function DashboardsIndexPage() {
  return (
    <section>
      <h1 style={{ color: "var(--heading)" }}>Dashboards</h1>
      <p style={{ color: "var(--muted)" }}>Selecione um dashboard.</p>
      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(260px, 1fr))", gap: 12, marginTop: 8 }}>
        {DASHBOARDS.map((r) => (
          <Link key={r.para} to={r.para} style={{ textDecoration: "none", color: "inherit" }}>
            <div style={{ border: "1px solid var(--border)", borderRadius: 12, padding: 14, height: "100%" }}>
              <strong style={{ color: "var(--brand-orange)" }}>{r.titulo}</strong>
              <p style={{ color: "var(--muted)", fontSize: 13, margin: "6px 0 0" }}>{r.descricao}</p>
            </div>
          </Link>
        ))}
      </div>
    </section>
  );
}
