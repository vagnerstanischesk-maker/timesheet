import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// PWA (vite-plugin-pwa) removido para eliminar dependências transitivas
// depreciadas (workbox → source-map/glob antigos). Pode ser readicionado no
// futuro com um setup atualizado, caso o "instalável" seja desejado.
export default defineConfig({
  plugins: [react()],
  server: { port: 5173, proxy: { "/api": "http://localhost:8080" } },
});
