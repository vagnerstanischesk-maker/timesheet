import { useAuth } from "react-oidc-context";
import { useQuery } from "@tanstack/react-query";
import { PREVIEW, FAKE_ME } from "../lib/preview";
import { renv } from "../lib/runtimeEnv";

export interface CurrentUser {
  id: number | null; nome: string | null; email: string | null;
  papeis: string[]; cadastrado: boolean;
}
const API = renv("VITE_API_BASE") ?? "/api";

export function useCurrentUser() {
  const auth = useAuth();
  return useQuery<CurrentUser>({
    queryKey: ["me"],
    enabled: PREVIEW || auth.isAuthenticated,
    queryFn: async () => {
      if (PREVIEW) return FAKE_ME as CurrentUser;
      const res = await fetch(`${API}/v1/me`, {
        headers: { Authorization: `Bearer ${auth.user?.access_token}` },
      });
      if (!res.ok) throw new Error("Falha ao carregar usuário");
      return res.json();
    },
  });
}

export function useHasRole(...roles: string[]) {
  const { data } = useCurrentUser();
  return !!data && roles.some((r) => data.papeis.includes(r));
}
