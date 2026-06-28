import { useAuth } from "react-oidc-context";
import { useCallback } from "react";
import { PREVIEW, mockApi } from "../lib/preview";
import { renv } from "../lib/runtimeEnv";

const API = renv("VITE_API_BASE") ?? "/api";

/** fetch autenticado (Bearer OIDC). Em modo pré-visualização, devolve dados fictícios. */
export function useApi() {
  const auth = useAuth();
  return useCallback(async function api<T>(path: string, init: RequestInit = {}): Promise<T> {
    if (PREVIEW) return mockApi<T>(path, init);
    const res = await fetch(`${API}${path}`, {
      ...init,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${auth.user?.access_token}`,
        ...(init.headers ?? {}),
      },
    });
    if (!res.ok) throw new Error((await res.text()) || `Erro ${res.status}`);
    if (res.status === 204) return undefined as T;
    return res.json() as Promise<T>;
  }, [auth.user?.access_token]);
}
