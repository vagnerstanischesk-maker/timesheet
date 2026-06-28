<!-- Inicio includeincluiBotoesSalvaLimpa.jsp -->
<%if (! acesso.verificaAcessoMenu(acesso.getJSP(request)).equals("L"))
  {%><a href="javascript:document.iForm.reset()" TABINDEX="2"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/commit/botoes/bot_limpartela_01.gif" border="0"></a>
     <%=cmDbBeanInclude.showButtonSalvarEdit(session) %>
<%} %>
<!-- Fim includeincluiBotoesSalvaLimpa.jsp -->
