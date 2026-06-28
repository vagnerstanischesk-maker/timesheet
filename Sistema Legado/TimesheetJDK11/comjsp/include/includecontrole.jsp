<jsp:useBean id="icontroleapp" class="combeans.appControlBean" scope="application">
</jsp:useBean>
<jsp:useBean id="iconexaobean"  class="combeans.conexaoBean" scope="session">
</jsp:useBean>
   <% iconexaobean.execute(icontroleapp, session, request, response, out); %>

<jsp:useBean class="combeans.verificaAcesso" id="acesso" scope="request">
</jsp:useBean>
<%
  if(! inc_referer3.equals("")) {
    if(! acesso.verificaReferer(inc_referer1,inc_referer2,inc_referer3, request, response,session))
       return;
  }
  else if(! inc_referer2.equals("")) {
    if(! acesso.verificaReferer(inc_referer1,inc_referer2, request, response,session))
       return;
  }
  else if(! inc_referer1.equals("")) {
    if(! acesso.verificaReferer(inc_referer1, request, response,session))
       return;
  }

  if(inc_telaParms.equals("jsp"))
    acesso.setTelaParms(acesso.getJSP(request));
  else if(inc_telaParms.equals("referer"))
    acesso.setTelaParms(acesso.getReferer());
  else
    acesso.setTelaParms(inc_telaParms);


  acesso.execute(icontroleapp, iconexaobean, out, request, response, session);
  if(! acesso.getverificaAcessoOK()) return;
%>


<jsp:useBean class="combeans.sessionCleanBean" id="clean" scope="page">
</jsp:useBean>
<% clean.execute(session, inc_clear);
   String sPaginaAnterior = (String)UtilBean.session_getValue(session, "COM_ULTIMA_PAGINA");
   if(acesso.getJSP(request).indexOf("Submit") == -1)
     UtilBean.session_putValue(session, "COM_ULTIMA_PAGINA", acesso.getJSP(request));
%>
