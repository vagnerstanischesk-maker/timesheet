<%@ page language  = "java"   
    import = "java.util.*, javax.naming.*"
    contentType="text/html;charset=ISO-8859-1"%>
<%@ page import = "java.sql.* , oracle.jdbc.*, oracle.jdbc.driver.OracleSQLException, combeans.*" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse" %>
<%@ page import="javax.servlet.http.Cookie,javax.servlet.jsp.*,java.net.URL,java.net.InetAddress,java.sql.Connection" %>

<HTML><HEAD>
  <meta http-equiv="Expires" CONTENT="0">
  <meta http-equiv="Cache-Control" CONTENT="no-cache">
  <meta http-equiv="Pragma" CONTENT="no-cache">
<%
HttpSession mysession = request.getSession(true);
session.setAttribute("TESTE","TESTE");
    try {
       if((request.getRemoteUser() == null) ||
          (request.getRemoteUser().equals("")))
       {   //out.println("<center>Verificando Login, por favor aguarde...</center>"); //não incluir tag HTML nem BODY aqui
           //UtilBean.geraDebugLog("<BR>INDEX.JSP --- REDIR PARA LOGIN",
           //                     icontroleapp, session, request, response, out);
           //out.println("<a href=\"login.jsp\">Login</a>");
           //out.println("request.getRemoteUser()=" + request.getRemoteUser());
           //return;
           //response.sendRedirect(request.getContextPath() + "/login.jsp");
           response.sendRedirect(request.getContextPath() + "/jsp/TelaInicial.jsp");
       }
       else
       {  
          //UtilBean.geraDebugLog("<BR>INDEX.JSP --- REDIR PARA APP",
          //                    icontroleapp, session, request, response, out);
          //out.println("request.getRemoteUser()=" + request.getRemoteUser());
          //response.sendRedirect(request.getContextPath() + "/jsp/TelaInicial.jsp");
          //response.sendRedirect(request.getContextPath() + "/jsp/index.jsp");
          response.sendRedirect(request.getContextPath() + "/jsp/TelaInicial.jsp");
       }
//======== ATENÇÃO =============
// Comentar os dois comandos quando o site estiver em manutenção
// Verificar no TelaInicial.jsp também.
//System.out.println(">"+session.getAttribute("COM_RN")+"<");

      //response.sendRedirect(session.getAttribute("COM_RN") + "jsp/TelaInicial.jsp");
      
      //out.println("<a href=\"jsp/TelaInicial.jsp\">TelaInicial</a>");
      if(true) return;
      session.setAttribute("COM_DTA", new java.util.Date());
    } catch (Exception ex) {
         out.print("<BR>Erro ao verificar login: " + ex);
         //out.print("<BR>Passo: " + passo);
         //UtilBean.geraDebugLog("<BR>INDEX.JSP---erro: " + ex + ", passo=" + passo,
         //    icontroleapp, session, request, response, out);

    }
//==============================
%>
<TITLE>Triscal</TITLE></HEAD>
<body>

<!-- <table border="0" width="100%" cellspacing="0" cellpadding="0" height="100%">
<tr>
<td colspan="3" class="txtChumbo" height="55">
Esse site está temporariamente em manutenção. Acesse mais tarde.<br>
Obrigado.
</td>
</tr>
</table> -->

</BODY>
</HTML>
