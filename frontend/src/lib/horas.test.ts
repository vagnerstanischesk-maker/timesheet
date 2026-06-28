import { describe, it, expect } from "vitest";
import { ehMultiploDeMeia, parseHoras } from "./horas";

describe("horas", () => {
  it("aceita múltiplos de 0,5", () => {
    expect(ehMultiploDeMeia(0.5)).toBe(true);
    expect(ehMultiploDeMeia(6.5)).toBe(true);
  });
  it("rejeita fora do passo de 0,5", () => {
    expect(ehMultiploDeMeia(0.25)).toBe(false);
    expect(ehMultiploDeMeia(6.3)).toBe(false);
  });
  it("parseHoras converte vírgula e HH:MM", () => {
    expect(parseHoras("8")).toBe(8);
    expect(parseHoras("8,5")).toBe(8.5);
    expect(parseHoras("8:30")).toBe(8.5);
    expect(parseHoras("")).toBe(0);
  });
  it("parseHoras devolve null para inválido", () => {
    expect(parseHoras("abc")).toBeNull();
  });
});
