import { describe, it, expect } from "vitest";
import { referenciaQuinzena } from "./quinzena";

describe("quinzena", () => {
  it("dia 15 -> período 1 do mês", () => {
    expect(referenciaQuinzena(2026, 6, 15)).toEqual([2026, 6, 1]);
  });
  it("dia 26 -> período 2 do mês", () => {
    expect(referenciaQuinzena(2026, 6, 26)).toEqual([2026, 6, 2]);
  });
  it("dia 5 -> período 2 do mês anterior", () => {
    expect(referenciaQuinzena(2026, 7, 5)).toEqual([2026, 6, 2]);
  });
  it("dia 5 de janeiro -> dezembro do ano anterior", () => {
    expect(referenciaQuinzena(2026, 1, 5)).toEqual([2025, 12, 2]);
  });
});
