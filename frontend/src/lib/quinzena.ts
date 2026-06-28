// Espelha o cálculo de quinzena do backend (cortes 1/16 — períodos 1–15 e 16–fim do mês).
const CORTE1 = 1, CORTE2 = 16;
const MESES = ["jan","fev","mar","abr","mai","jun","jul","ago","set","out","nov","dez"];

/** Dado um dia, retorna [anoRef, mesRef, periodo]. */
export function referenciaQuinzena(ano: number, mes: number, dia: number): [number, number, number] {
  if (CORTE1 <= 1) return [ano, mes, dia < CORTE2 ? 1 : 2];
  // modelo legado (cortes cruzando o mês)
  if (dia >= CORTE1 && dia < CORTE2) return [ano, mes, 1];
  if (dia >= CORTE2) return [ano, mes, 2];
  const ant = new Date(ano, mes - 1, 1); ant.setMonth(ant.getMonth() - 1);
  return [ant.getFullYear(), ant.getMonth() + 1, 2];
}

export function rotuloPeriodo(anoRef: number, mesRef: number, periodo: number): string {
  if (periodo === 1) return `${CORTE1}–${CORTE2 - 1} ${MESES[mesRef - 1]} ${anoRef}`;
  if (CORTE1 <= 1) {
    const ultimoDia = new Date(anoRef, mesRef, 0).getDate(); // dia 0 do mês seguinte = último do mês
    return `${CORTE2}–${ultimoDia} ${MESES[mesRef - 1]} ${anoRef}`;
  }
  const prox = new Date(anoRef, mesRef, 1);
  return `${CORTE2} ${MESES[mesRef - 1]} – ${CORTE1 - 1} ${MESES[prox.getMonth()]} ${prox.getFullYear()}`;
}

/** Referência do período imediatamente anterior. */
export function periodoAnterior(anoRef: number, mesRef: number, periodo: number): [number, number, number] {
  if (periodo === 2) return [anoRef, mesRef, 1];
  const ant = new Date(anoRef, mesRef - 1, 1); ant.setMonth(ant.getMonth() - 1);
  return [ant.getFullYear(), ant.getMonth() + 1, 2];
}

/** Referência do período imediatamente seguinte. */
export function periodoSeguinte(anoRef: number, mesRef: number, periodo: number): [number, number, number] {
  if (periodo === 1) return [anoRef, mesRef, 2];
  const prox = new Date(anoRef, mesRef - 1, 1); prox.setMonth(prox.getMonth() + 1);
  return [prox.getFullYear(), prox.getMonth() + 1, 1];
}
