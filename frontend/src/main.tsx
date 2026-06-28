import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { AuthProvider } from "react-oidc-context";
import { oidcConfig } from "./auth/authConfig";
import { App } from "./app/App";
import "./styles/global.css";

// Remove qualquer Service Worker antigo (versões anteriores tinham PWA/vite-plugin-pwa).
// Um SW remanescente pode interceptar rotas como /cadastros/estrutura e devolver o
// HTML como download (ex.: "estrutura.htm"). Esta limpeza é idempotente e segura.
if ("serviceWorker" in navigator) {
  navigator.serviceWorker.getRegistrations().then((regs) => regs.forEach((r) => r.unregister()));
  if (window.caches) caches.keys().then((keys) => keys.forEach((k) => caches.delete(k)));
}

const queryClient = new QueryClient();

// O AuthProvider é mantido sempre (necessário para o hook useAuth). Em modo
// pré-visualização (VITE_PREVIEW=true), o login nunca é acionado — ver ProtectedRoute.
ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <AuthProvider {...oidcConfig}>
      <QueryClientProvider client={queryClient}>
        <BrowserRouter>
          <App />
        </BrowserRouter>
      </QueryClientProvider>
    </AuthProvider>
  </React.StrictMode>
);
