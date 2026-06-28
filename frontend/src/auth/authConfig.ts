import type { AuthProviderProps } from "react-oidc-context";
import { WebStorageStateStore } from "oidc-client-ts";
import { renv } from "../lib/runtimeEnv";

// Configuração OIDC — autentica DIRETO no Microsoft Entra ID (sem Keycloak).
// authority = https://login.microsoftonline.com/<tenant-id>/v2.0
// O scope DEVE incluir o escopo da API (ex.: api://<client-id>/access_as_user) para que o
// access_token tenha a audience que o backend valida. Valores vêm do RUNTIME (window.__ENV__)
// em produção, ou do build (.env) em dev.
export const oidcConfig: AuthProviderProps = {
  authority: renv("VITE_OIDC_AUTHORITY"),
  client_id: renv("VITE_OIDC_CLIENT_ID"),
  redirect_uri: renv("VITE_OIDC_REDIRECT_URI") ?? (typeof window !== "undefined" ? `${window.location.origin}/` : ""),
  scope: renv("VITE_OIDC_SCOPE") ?? "openid profile email",
  userStore: new WebStorageStateStore({ store: window.localStorage }),
  // IMPORTANTE (Entra direto): NÃO chamar o endpoint userinfo (Graph). O access_token tem audience
  // da NOSSA API (api://.../access_as_user), não do Graph — chamar userinfo daria 401 e derrubaria a
  // sessão. A identidade (nome/e-mail/oid) vem do id_token, que é suficiente.
  loadUserInfo: false,
  // Renovação silenciosa por iframe pode falhar (cookies de terceiros). Como os tokens do Entra
  // duram ~60-90min, desligamos para evitar loops de logout; o usuário re-loga quando expirar.
  automaticSilentRenew: false,
  monitorSession: false,
  onSigninCallback: () => {
    window.history.replaceState({}, document.title, window.location.pathname);
  },
};
