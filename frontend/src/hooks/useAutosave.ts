import { useEffect, useRef, useState } from "react";

export type SaveState = "idle" | "dirty" | "saving" | "saved" | "error";

/**
 * Autosave com debounce. Observa `value`; quando muda, agenda o save.
 * Expõe o estado para a UI (salvando… / salvo / falha) e um `flush()` para salvar já.
 */
export function useAutosave<T>(
  value: T,
  save: (v: T) => Promise<void>,
  opts: { delay?: number; enabled?: boolean } = {}
) {
  const { delay = 800, enabled = true } = opts;
  const [state, setState] = useState<SaveState>("idle");
  const timer = useRef<ReturnType<typeof setTimeout> | null>(null);
  const latest = useRef(value);
  const first = useRef(true);
  latest.current = value;

  async function doSave() {
    setState("saving");
    try { await save(latest.current); setState("saved"); }
    catch { setState("error"); }
  }

  useEffect(() => {
    if (!enabled) return;
    if (first.current) { first.current = false; return; } // não salva no mount
    setState("dirty");
    if (timer.current) clearTimeout(timer.current);
    timer.current = setTimeout(doSave, delay);
    return () => { if (timer.current) clearTimeout(timer.current); };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [value, enabled, delay]);

  const dirty = state === "dirty" || state === "saving" || state === "error";
  return { state, dirty, flush: doSave };
}
