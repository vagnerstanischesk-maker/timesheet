// Badge de status do dia (A/F/P/R) com as cores da marca.
const MAP: Record<string, { rotulo: string; bg: string; cor: string }> = {
  A: { rotulo: "Aberto",    bg: "#efefef", cor: "#686868" },
  F: { rotulo: "Fechado",   bg: "#FCF3D6", cor: "#8A6D00" },
  P: { rotulo: "Aprovado",  bg: "#E4F3EB", cor: "#0B6E3C" },
  R: { rotulo: "Rejeitado", bg: "#FBE7E5", cor: "#B5362C" },
};

export function StatusBadge({ status }: { status: string }) {
  const s = MAP[status] ?? MAP.A;
  return <span style={{ background: s.bg, color: s.cor, fontSize: 12, padding: "2px 8px", borderRadius: 999 }}>{s.rotulo}</span>;
}
