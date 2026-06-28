import { Routes, Route, Navigate, Link } from "react-router-dom";
import { useAuth, hasAuthParams } from "react-oidc-context";
import { ProtectedRoute } from "./ProtectedRoute";
import { useCurrentUser } from "../auth/useCurrentUser";
import { PREVIEW } from "../lib/preview";
import { LancamentoDiaPage } from "../pages/LancamentoDiaPage";
import { QuinzenaPage } from "../pages/QuinzenaPage";
import { AprovacaoPage } from "../pages/AprovacaoPage";
import { AcompanhamentoProjetoPage } from "../pages/AcompanhamentoProjetoPage";
import { DashboardsPage } from "../pages/DashboardsPage";
import { RelatoriosPage } from "../pages/RelatoriosPage";
import { RelatorioAcompanhamentoPage } from "../pages/RelatorioAcompanhamentoPage";
import { RelatorioBhePage } from "../pages/RelatorioBhePage";
import { RelatoriosIndexPage } from "../pages/RelatoriosIndexPage";
import { DashboardsIndexPage } from "../pages/DashboardsIndexPage";
import { CadastroProjetosPage } from "../pages/CadastroProjetosPage";
import { CadastroFeriadosPage } from "../pages/CadastroFeriadosPage";
import { CadastroClientesPage } from "../pages/CadastroClientesPage";
import { CadastroFuncionariosPage } from "../pages/CadastroFuncionariosPage";
import { CadastroVerticaisPage } from "../pages/CadastroVerticaisPage";
import { EstruturaProjetoPage } from "../pages/EstruturaProjetoPage";
import { BheManualPage } from "../pages/BheManualPage";

// Rota raiz: NÃO redireciona enquanto o callback do Entra (?code=...) está na URL ou em
// processamento — senão o redirect apaga o `code` antes da troca por token (login nunca completa).
function InicioRedirect() {
  const auth = useAuth();
  if (!PREVIEW && (hasAuthParams() || auth.isLoading || auth.activeNavigator)) return <p style={{ padding: 16 }}>Carregando…</p>;
  return <Navigate to="/dia" replace />;
}

export function App() {
  const auth = useAuth();
  const { data: me } = useCurrentUser();
  const tem = (...r: string[]) => !!me?.papeis.some((p) => r.includes(p));
  const admin = tem("ADMIN_RH", "DIRETORIA"); // acesso total aos cadastros
  const authed = PREVIEW || auth.isAuthenticated;
  return (
    <div style={{ maxWidth: 1100, margin: "0 auto", padding: 16 }}>
      {PREVIEW && (
        <div style={{ background: "var(--brand-orange)", color: "#fff", textAlign: "center", padding: "6px 10px", borderRadius: 8, marginBottom: 10, fontSize: 13 }}>
          Modo pré-visualização — dados fictícios, sem login. Não use em produção.
        </div>
      )}
      <header style={{ display: "flex", gap: 14, alignItems: "center", paddingBottom: 12, borderBottom: "1px solid var(--border)", flexWrap: "wrap" }}>
        <strong style={{ color: "var(--brand-orange)", fontSize: 18 }}>Triscal · Apontamento</strong>
        <nav style={{ display: "flex", gap: 12, flexWrap: "wrap" }}>
          <Link to="/dia">Meu dia</Link>
          <Link to="/quinzena">Quinzena</Link>
          {tem("GESTOR","ADMIN_RH","DIRETORIA") && <Link to="/aprovacao">Aprovações</Link>}
          {tem("GESTOR_PROJETO","ADMIN_RH","DIRETORIA") && <Link to="/projetos/acompanhamento">Acompanhamento</Link>}
          {tem("GESTOR","GESTOR_PROJETO","ADMIN_RH","DIRETORIA") && <Link to="/dashboards">Dashboards</Link>}
          {tem("GESTOR","GESTOR_PROJETO","ADMIN_RH","DIRETORIA") && <Link to="/relatorios">Relatórios</Link>}
          {admin && <Link to="/cadastros/projetos">Projetos</Link>}
          {admin && <Link to="/cadastros/estrutura">Estrutura</Link>}
          {admin && <Link to="/cadastros/funcionarios">Funcionários</Link>}
          {admin && <Link to="/cadastros/clientes">Clientes</Link>}
          {admin && <Link to="/cadastros/verticais">Verticais</Link>}
          {admin && <Link to="/cadastros/feriados">Feriados</Link>}
          {admin && <Link to="/cadastros/bhe">BHE</Link>}
        </nav>
        <span style={{ marginLeft: "auto", color: "var(--muted)", fontSize: 13 }}>
          {authed ? (me?.nome ?? "…") : <button onClick={() => auth.signinRedirect()}>Entrar</button>}
        </span>
      </header>
      <main style={{ paddingTop: 16 }}>
        <Routes>
          <Route path="/" element={<InicioRedirect />} />
          <Route path="/dia" element={<ProtectedRoute roles={["COLABORADOR","GESTOR","GESTOR_PROJETO","ADMIN_RH","DIRETORIA"]}><LancamentoDiaPage /></ProtectedRoute>} />
          <Route path="/quinzena" element={<ProtectedRoute roles={["COLABORADOR","GESTOR","GESTOR_PROJETO","ADMIN_RH","DIRETORIA"]}><QuinzenaPage /></ProtectedRoute>} />
          <Route path="/aprovacao" element={<ProtectedRoute roles={["GESTOR","ADMIN_RH","DIRETORIA"]}><AprovacaoPage /></ProtectedRoute>} />
          <Route path="/projetos/acompanhamento" element={<ProtectedRoute roles={["GESTOR_PROJETO","ADMIN_RH","DIRETORIA"]}><AcompanhamentoProjetoPage /></ProtectedRoute>} />
          <Route path="/dashboards" element={<ProtectedRoute roles={["GESTOR","GESTOR_PROJETO","ADMIN_RH","DIRETORIA"]}><DashboardsIndexPage /></ProtectedRoute>} />
          <Route path="/dashboards/geral" element={<ProtectedRoute roles={["GESTOR","GESTOR_PROJETO","ADMIN_RH","DIRETORIA"]}><DashboardsPage /></ProtectedRoute>} />
          <Route path="/relatorios" element={<ProtectedRoute roles={["GESTOR","GESTOR_PROJETO","ADMIN_RH","DIRETORIA"]}><RelatoriosIndexPage /></ProtectedRoute>} />
          <Route path="/relatorios/logs-pendentes" element={<ProtectedRoute roles={["GESTOR","GESTOR_PROJETO","ADMIN_RH","DIRETORIA"]}><RelatoriosPage /></ProtectedRoute>} />
          <Route path="/relatorios/acompanhamento" element={<ProtectedRoute roles={["GESTOR","GESTOR_PROJETO","ADMIN_RH","DIRETORIA"]}><RelatorioAcompanhamentoPage /></ProtectedRoute>} />
          <Route path="/relatorios/bhe" element={<ProtectedRoute roles={["GESTOR","GESTOR_PROJETO","ADMIN_RH","DIRETORIA"]}><RelatorioBhePage /></ProtectedRoute>} />
          <Route path="/cadastros/projetos" element={<ProtectedRoute roles={["ADMIN_RH","DIRETORIA"]}><CadastroProjetosPage /></ProtectedRoute>} />
          <Route path="/cadastros/estrutura" element={<ProtectedRoute roles={["ADMIN_RH","DIRETORIA"]}><EstruturaProjetoPage /></ProtectedRoute>} />
          <Route path="/cadastros/funcionarios" element={<ProtectedRoute roles={["ADMIN_RH","DIRETORIA"]}><CadastroFuncionariosPage /></ProtectedRoute>} />
          <Route path="/cadastros/clientes" element={<ProtectedRoute roles={["ADMIN_RH","DIRETORIA"]}><CadastroClientesPage /></ProtectedRoute>} />
          <Route path="/cadastros/verticais" element={<ProtectedRoute roles={["ADMIN_RH","DIRETORIA"]}><CadastroVerticaisPage /></ProtectedRoute>} />
          <Route path="/cadastros/feriados" element={<ProtectedRoute roles={["ADMIN_RH","DIRETORIA"]}><CadastroFeriadosPage /></ProtectedRoute>} />
          <Route path="/cadastros/bhe" element={<ProtectedRoute roles={["ADMIN_RH","DIRETORIA"]}><BheManualPage /></ProtectedRoute>} />
        </Routes>
      </main>
    </div>
  );
}
