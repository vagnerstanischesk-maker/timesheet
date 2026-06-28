import { useEffect, useRef, type ReactNode } from "react";
import { useAuth, hasAuthParams } from "react-oidc-context";
import { useCurrentUser } from "../auth/useCurrentUser";
import { PREVIEW } from "../lib/preview";

export function ProtectedRoute({ children, roles }: { children: ReactNode; roles?: string[] }) {
  const auth = useAuth();
  const { data: me, isLoading } = useCurrentUser();
  const tentouLogin = useRef(false);

  // Dispara o login UMA vez, em efeito colateral (nunca durante o render) e NUNCA enquanto o
  // callback do Entra (?code=...) está na URL ou em processamento. Sem isso, o redirect repetido
  // sobrescreve o `state` do OIDC e a troca do code por token nunca completa (loop de login).
  useEffect(() => {
    if (PREVIEW) return;
    if (!hasAuthParams() && !auth.isAuthenticated && !auth.activeNavigator && !auth.isLoading && !tentouLogin.current) {
      tentouLogin.current = true;
      void auth.signinRedirect();
    }
  }, [auth.isAuthenticated, auth.activeNavigator, auth.isLoading, auth]);

  if (PREVIEW) return <>{children}</>;        // pré-visualização: sem login

  // Enquanto processa o login/callback (troca do code por token), aguarda — não redireciona.
  if (auth.isLoading || auth.activeNavigator || hasAuthParams()) return <p>Carregando…</p>;
  if (auth.error) return <p>Falha no login: {auth.error.message}. Recarregue a página para tentar de novo.</p>;
  if (!auth.isAuthenticated) return <p>Redirecionando para o login…</p>;

  if (isLoading) return <p>Carregando usuário…</p>;
  if (me && !me.cadastrado) return <p>Usuário autenticado, mas sem cadastro de funcionário. Contate o RH.</p>;
  if (roles && roles.length > 0 && !(me && roles.some((r) => me.papeis.includes(r)))) {
    return <p>Você não tem permissão para acessar esta tela.</p>;
  }
  return <>{children}</>;
}
