// Tokens de design da marca Triscal (extraídos do site oficial).
// Ver ARQUITETURA_TELAS_COMPONENTES.md (seção 10).
export const triscalColors = {
  brand: { orange: "#CC4700", orangeDark: "#9A3600", orangeBright: "#E2640F", blue: "#004ECC", indigo: "#365899" },
  neutral: { heading: "#111111", text: "#222222", grey: "#686868", lightGrey: "#EFEFEF", lighterGrey: "#F5F5F5", border: "#CCCCCC" },
  semantic: { success: "#0F9D58", warning: "#F4B400", danger: "#DB4437", info: "#004ECC" },
  // Estados do timesheet (ST_TIMESHEET_DIA)
  status: { A: "#686868", F: "#F4B400", P: "#0F9D58", R: "#DB4437" },
} as const;

export type TriscalColors = typeof triscalColors;
