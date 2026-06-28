<!-- Inicio includemeta.jsp -->
<% //---21/11/2001---Inicio---------------------------------------------------
   //UtilBean.geraDebugLog("<BR>INCLUDEMETA>JSP --- INICIO",
   //          icontroleapp, session, request, response, out);

   //Christiano Chamma-06/01/2003/---RN e SN fixos---INICIO---
   if( (session.getAttribute("COM_SN") == null) || (session.getAttribute("COM_RN") == null) )
   {  //weblogic if( (request.getServerPort() != 7070) &&
      //weblogic     (request.getServerPort() != 7072) &&
      //weblogic     (request.getServerPort() != 8988) &&
      //weblogic     (request.getServerPort() != 7101) )
      //weblogic {  
	     if(! UtilBean.getJSP(request).equals("com_msgtimeout.jsp"))
         {  
		    //session.setAttribute("COM_SN", "127.0.0.1");
            //session.setAttribute("COM_RN", "http://127.0.0.1:7101/portalcom/");
            //Cookie mycookie1 = new Cookie("COM_SN", "127.0.0.1");
            //response.addCookie(mycookie1);
            //Cookie mycookie2 = new Cookie("COM_RN", "http://127.0.0.1/portalcom/");
            //response.addCookie(mycookie2);

		    session.setAttribute("COM_SN", request.getServerName());
            session.setAttribute("COM_RN", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/");
            Cookie mycookie1 = new Cookie("COM_SN", request.getServerName());
            response.addCookie(mycookie1);
            Cookie mycookie2 = new Cookie("COM_RN", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/");
            response.addCookie(mycookie2);
         }
      //weblogic }
   }
   //Christiano Chamma-06/01/2003/---RN e SN fixos---FIM------

   if( (session.getAttribute("COM_SN") == null) || (session.getAttribute("COM_RN") == null) )
   {
%>
  <meta http-equiv="Expires" CONTENT="0">
  <meta http-equiv="Cache-Control" CONTENT="no-cache">
  <meta http-equiv="Pragma" CONTENT="no-cache">
<SCRIPT LANGUAGE="JavaScript1.2">

function errorcarrega() {
   /* http://servidor/empresa/pagina.jsp */
   var local = ""+document.location;
   var ind = 0;
   var achou = false;

   if(local.indexOf("?") >= 0)
      local = local.substring(0, local.indexOf("?"));
   ind = local.length - 1;

   while ((ind >= 0) && (! achou))
   {
      if(local.substring(ind, ind+1) == "/")
         achou = true;
      else
         ind--;
   }
<% if(UtilBean.getJSP(request).equals("com_msgtimeout.jsp"))
   { %>
      document.location.href = local.substring(0, ind+1) + "redir.jsp?" + "timeout=true" + "&origem=" + escape(document.location) + "&hostname=" + escape(document.location.hostname);
<% } else { %>
      document.location.href = local.substring(0, ind+1) + "redir.jsp?" + "origem=" + escape(document.location) + "&hostname=" + escape(document.location.hostname);
<% } %>
   return true;
}

function navega() {
   /* http://servidor/empresa/pagina.jsp */
   var local = ""+document.location;
   var ind = 0;
   var achou = false;

   if(local.indexOf("?") >= 0)
      local = local.substring(0, local.indexOf("?"));
   ind = local.length - 1;

   while ((ind >= 0) && (! achou))
   {
      if(local.substring(ind, ind+1) == "/")
         achou = true;
      else
         ind--;
   }
   document.location.href = local.substring(0, ind+1) + "redir.jsp?" + "origem=" + escape(document.location) + "&hostname=" + escape(document.location.hostname);
   return true;
}

</script>
</HEAD>
<BODY onLoad="errorcarrega()">
<font color="#FFFFFF"><a href="javascript:void navega()"> </a> Clique para continuar.</font>
</BODY>
</HTML>
<%    return;
   }
   //---21/11/2001---Fim------------------------------------------------------
%>
<%  String SN = ""+session.getAttribute("COM_SN");
    String RN = ""+session.getAttribute("COM_RN");
    //UtilBean.geraDebugLog("<BR>INCLUDEMETA>JSP --- ANTES DE VERIFICAR LOGADO, SN=" + SN + ", RN=" + RN,
    //         icontroleapp, session, request, response, out);
%>

<% boolean comlogado =  UtilBean.verificaLogado(session, request, response); %>

<%  SN = ""+session.getAttribute("COM_SN");
    RN = ""+session.getAttribute("COM_RN");
    //UtilBean.geraDebugLog("<BR>INCLUDEMETA>JSP --- APOS VERIFICAR LOGADO=" + comlogado + ", SN=" + SN + ", RN=" + RN,
    //         icontroleapp, session, request, response, out);
%>

<% if(! UtilBean.setDominio(icontroleapp, iconexaobean, session, request, response, out)) return; %>

<%  SN = ""+session.getAttribute("COM_SN");
    RN = ""+session.getAttribute("COM_RN");
    //UtilBean.geraDebugLog("<BR>INCLUDEMETA>JSP --- APOS SET DOMINIO, SN=" + SN + ", RN=" + RN,
    //         icontroleapp, session, request, response, out);
%>


<META NAME="GENERATOR" CONTENT="Oracle JDeveloper">
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=ISO-8859-1">
<META name="description" content="Triscal - O portal Triscal Consultoria...">
<META name="keywords" content="Triscal, IBM, Oracle, banco de dados, database, BI, CRM, application, aplicação, ...">
<META NAME="author" CONTENT="Triscal">

<SCRIPT LANGUAGE="JavaScript">
function mostra(item){
	if (item.style.display=='none'){
  		item.style.display='';
  }else{
  		item.style.display='none';
   	}
}
</script>
<jsp:useBean id="menubean" class="combeans.menuBean2" scope="session">
</jsp:useBean>
<!-- Fim includemeta.jsp -->
