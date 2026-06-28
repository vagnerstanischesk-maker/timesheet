// Lê configuração priorizando o RUNTIME (window.__ENV__, injetado pelo container em produção)
// e caindo para o build-time (import.meta.env, usado em dev). Assim o mesmo build/imagem
// serve qualquer ambiente — basta definir as variáveis no Container App.
type Env = Record<string, string | undefined>;

const runtime: Env = (typeof window !== "undefined" && (window as Window & { __ENV__?: Env }).__ENV__) || {};

export function renv(key: string): string | undefined {
  const v = runtime[key];
  if (v !== undefined && v !== "") return v;
  return (import.meta.env as unknown as Env)[key];
}
