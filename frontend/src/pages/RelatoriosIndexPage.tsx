import { Link } from "react-router-dom";

interface Item { titulo: string; descricao: string; para: string; }
const RELATORIOS: Item[] = [
  { titulo: "Logs pendentes (períodos passados)", descricao: "Colaboradores com quinzenas passadas não fechadas (status Aberto ou Rejeitado), agrupado por colaborador.", para: "/relatorios/logs-pendentes" },
  { titulo: "Acompanhamento de projetos", descricao: "Visão consolidada de todos os projetos: cliente, horas vendidas × executadas, % por horas, % informado pelo gestor e diferença previsto × realizado.", para: "/relatorios/acompanhamento" },
  { titulo: "Movimentação de BHE", descricao: "Créditos por aprovação de horas extras e lançamentos manuais de banco de horas, por período, agrupados por colaborador.", para: "/relatorios/bhe" },
];

export function RelatoriosIndexPage() {
  return (
    <section>
      <h1 style={{ color: "var(--heading)" }}>Relatórios</h1>
      <p style={{ color: "var(--muted)" }}>Selecione um relatório.</p>
      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(260px, 1fr))", gap: 12, marginTop: 8 }}>
        {RELATORIOS.map((r) => (
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
