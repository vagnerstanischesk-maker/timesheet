<%@ page language  = "java" buffer="5kb" autoFlush="true" 
    import = "java.util.* "
    contentType="text/html;charset=ISO-8859-1"%>
<%@ page import = "java.sql.* , oracle.jdbc.*, oracle.jdbc.driver.OracleSQLException,combeans.*" %>
<%@ page import = "java.text.SimpleDateFormat" %>
<%@ page import = "java.util.Date" %>

<jsp:useBean id="icontroleapp" class="combeans.appControlBean" scope="application">
</jsp:useBean>
<jsp:useBean id="iconexaobean"  class="combeans.conexaoBean" scope="session">
</jsp:useBean>
   <% iconexaobean.execute(icontroleapp, session, request, response, out); %>
<jsp:useBean id="iloginBean" class="combeans.loginBean" scope="session">
</jsp:useBean>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma", "no-cache");
response.setHeader("Expires", "Thu, 29 Oct 1970 17:04:19 GMT");

//UtilBean.GeraDebugLogSimples("20221122 logout.jsp 01 COM_ISLOGGEDIN está "+session.getAttribute("COM_ISLOGGEDIN")+".", session, request, true);

 iloginBean.logout(iconexaobean.getConnection(), out, request, response, session);
 UtilBean.limpaSession(request, session, response, true, true);
 //subject.getPrincipals().remove(userPrincipal);
 //subject.getPrincipals().remove(rolePrincipal);
 //request.getSession().invalidate();
 
  if(request.getSession(false)!=null){
    request.getSession(false).invalidate();//remove session.
  }

  if(request.getSession()!=null){
    request.getSession().invalidate();//remove session.
  }
  request.logout(); 

//UtilBean.GeraDebugLogSimples("20221122 logout.jsp 02 COM_ISLOGGEDIN está "+session.getAttribute("COM_ISLOGGEDIN")+".", session, request, true);
 
 //HttpSession mysession = request.getSession(true);

%>

<html>
<head>
<title>Logout Sistema de Timesheet</title>
</head>
<body vlink="#BD1010" alink="#BD1010">

<table border="0" cellspacing="0" cellpadding="0" height="80" background="" align="center" width="100%">
  <tr> 
    <td bgcolor="#FFFFFF" width="30%" ><img src="/portalcom/images/logo.png" alt="logo" id="logo"></td>
    <td width="45%" align="center" ><h2>Logout do Sistema de Timesheet</h2></td>
    <td width="25%" >&nbsp;&nbsp;</td>
  </tr>
</table>

<%
String done_url = "http://www.triscal.com.br";
int i = 0;
try
{
%>
   <table width="100%" border="0" cellspacing="0" cellpadding="0" bgcolor="#EEEEEE">
    <tr> 
       <td width="241"> </td>
       <td width="322"> <font color="#BD1010" face="Helvetica,Verdana,Arial" size="1"> 
       <b><% out.print((new java.util.Date()).toString()); %> </b></font></td>
       <td width="440" align="center">
         <font color="#BD1010" face="Helvetica,Verdana,Arial" size="1">
             <a href="http://www.triscal.com.br/" >www.triscal.com.br</a>
              &nbsp;&nbsp;|&nbsp;&nbsp;
             <a href="login.jsp" >Efetuar Login Novamente</a>
         </font>
       </td>
    </tr>
   </table>

<%
   out.println("<br><br>");
   out.println("<center><h3>Obrigado por utilizar o sistema. Para sua maior seguran&ccedil;a, feche o browser.</h3><p>");
   out.println("<br><br>");
   out.println("<table border=0>");

}
catch(Exception e)
{
    if(i>1)
    {
      out.println("</table>");
      out.println("<br>");
      out.println("</center>");
    }
    else
    {
       out.println("<h2><center><font color='red'>ERROR:</font>");
       out.println("This page can not be accessed directly!</center></h2>");
    }
}

%>
</body>
</html>


