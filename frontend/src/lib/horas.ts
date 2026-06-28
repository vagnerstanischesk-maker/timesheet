// Utilitários de horas (regra Triscal: mínimo 0,5h, múltiplos de 0,5h).
export const PASSO = 0.5;

export function ehMultiploDeMeia(v: number): boolean {
  return Number.isFinite(v) && Math.round(v * 2) === v * 2;
}

/** Converte "8", "8,5", "8:30" em número de horas. Retorna null se inválido. */
export function parseHoras(input: string): number | null {
  const s = input.trim().replace(",", ".");
  if (!s) return 0;
  if (s.includes(":")) {
    const [h, m] = s.split(":");
    const hh = Number(h), mm = Number(m);
    if (!Number.isFinite(hh) || !Number.isFinite(mm)) return null;
    return hh + mm / 60;
  }
  const n = Number(s);
  return Number.isFinite(n) ? n : null;
}
