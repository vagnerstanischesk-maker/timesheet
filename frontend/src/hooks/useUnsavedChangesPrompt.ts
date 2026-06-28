import { useEffect } from "react";

/**
 * Aviso de alterações não salvas ao FECHAR/RECARREGAR a aba (beforeunload).
 *
 * O bloqueio de navegação interna (trocar de rota) exigiria o "data router" do
 * React Router (createBrowserRouter + useBlocker). Como o app usa <BrowserRouter>,
 * mantemos aqui apenas o guard de beforeunload (cobre fechar/recarregar a aba e
 * voltar/avançar do navegador). O prompt ao trocar de rota interna pode ser
 * readicionado com a migração para data router (ver docs/PENDENCIAS.md).
 *
 * O 2º parâmetro é mantido para compatibilidade da assinatura.
 */
export function useUnsavedChangesPrompt(dirty: boolean, _onSave?: () => Promise<boolean>) {
  useEffect(() => {
    if (!dirty) return;
    const handler = (e: BeforeUnloadEvent) => { e.preventDefault(); e.returnValue = ""; };
    window.addEventListener("beforeunload", handler);
    return () => window.removeEventListener("beforeunload", handler);
  }, [dirty]);
}
