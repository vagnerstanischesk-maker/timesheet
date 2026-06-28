<!-- Inicio includeincluiBotoesSalvaTabela.jsp -->
<a href="javascript: document.location='<%=cmDbBeanInclude.getPaginaBrowse() %>'" name="Cancelar"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/commit/botoes/bot_cancelar_01.gif" BORDER="0" alt="Cancelar"></a>
<%if (! acesso.verificaAcessoMenu(acesso.getJSP(request)).equals("L"))
  {%><a href="javascript:document.iForm.reset()" name="Limpar" TABINDEX="2" Tela"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/commit/botoes/bot_limpartela_01.gif" BORDER="0" alt="Limpar"></a>
      <%=cmDbBeanInclude.showButtonSalvarEdit(session) %>
<%} %>
<!-- Fim includeincluiBotoesSalvaTabela.jsp -->
