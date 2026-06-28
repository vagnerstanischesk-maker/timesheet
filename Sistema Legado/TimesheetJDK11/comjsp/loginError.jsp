<%@ page buffer="5kb" autoFlush="true" %>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma", "no-cache");
response.setHeader("Expires", "Thu, 29 Oct 2000 17:04:19 GMT");
%>

<html>
<head>
<title>Login Sistema de Timesheet - Identifica&ccedil;&atilde;o do usu&aacute;rio</title>
</head>
<body vlink="#BD1010" alink="#BD1010">

<table border="0" cellspacing="0" cellpadding="0" height="80" background="" align="center" width="100%">
  <tr> 
    <td bgcolor="#FFFFFF" width="30%"><img src="/portalcom/images/logo.png" alt="logo" id="logo"></td>
    <td width="45%" align="center" ><h2>Login do Sistema de Timesheet</h2></td>
    <td width="25%" >&nbsp;&nbsp;</td>
  </tr>
</table>

<%

try
{
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0" bgcolor="#EEEEEE">
  <tr> 
    <td width="241"> </td>
    <td width="322"> <font color="#BD1010" face="Helvetica,Verdana,Arial" size="1"> 
      <b><% out.print((new java.util.Date()).toString()); %>      
      </b></font> 
     </td>
    <td width="440" align="right"> 
         <font color="#BD1010" face="Helvetica,Verdana,Arial" size="1">
             <a href="http://www.triscal.com.br/" >
              www.triscal.com.br 
             </a>
          &nbsp;&nbsp;&nbsp;&nbsp;
          &nbsp;&nbsp;&nbsp;&nbsp;
          &nbsp;&nbsp;&nbsp;&nbsp;
         </font>
    </td>
  </tr>
</table>

<table border="0" cellspacing="0" cellpadding="0" height="70%" background="" align="center" width="100%" >
  <tr>
  <td width="25%" >&nbsp;
  </td>
  <td width="50%" align="center" >
   
<% String str_submit = "";
   out.println("<br><br>");
   out.println("<form method=\"POST\" action=\"j_security_check\" name=\"loginForm\">");
   
   out.println("<table border=0 align='center' >");
   String str_err = "";
   if((str_err != null) && (str_err.length() > 1))
   {
       out.println("<tr>");
       out.println("<td>");
       out.println("<font color='red'>Erro :</font>");
       out.println("</td>");
       out.println("<td>");
       out.println(str_err);
       out.println("</td>");
       out.println("</tr>");
   }
   out.println("<tr><td colspan=\"3\"><font color=\"red\" face=\"Helvetica,Verdana,Arial\" size=\"2\">Ocorreu um erro, por favor tente novamente</font></td></tr>");

   out.println("<tr>");
   out.println("<td>");
   out.println("<b>Usu&aacute;rio:</b>");
   out.println("</td>");
   out.println("<td>");
   out.println("<INPUT TYPE=\"text\" NAME=\"j_username\">");
   out.println("</td>");
   out.println("</tr>");
   out.println("<tr>");
   out.println("<td>");
   out.println("<b>Senha:</b>");
   out.println("</td>");
   out.println("<td>");
   out.println("<INPUT type=\"password\" name=\"j_password\">");
   out.println("</td>");
   out.println("</tr>");

   out.println("<tr><td colspan=\"3\">&nbsp;</td></tr>");

   out.println("<tr>");
   out.println("<td>&nbsp;");
   out.println("</td><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("<nobr><input type=\"submit\" value=\"Login\">"); 
   //out.println("<input type='reset' value='Limpar'></nobr>");
   out.println("</nobr>");
   out.println("<td>");
   out.println("</tr>");

   out.println("<tr><td colspan=\"3\">&nbsp;</td></tr>");

   out.println("<tr><td colspan=\"3\"><font color=\"#BD1010\" face=\"Helvetica,Verdana,Arial\" size=\"2\"><i>Digite seu usu&aacute;rio e senha. O sistema diferencia letras mai&uacute;sculas e min&uacute;sculas</i></font></td></tr>");

   out.println("</table>");

   out.println("</form>");
   

}
catch(Exception e)
{
    out.println("<h2><center><font color='red'>ERROR:</font>");
    out.println("This page can not be accessed directly!</center></h2>");
}

%>
<br>
</td>


<td width="45%"  valign="top">
</td>
</tr>
</table>

</body>
</html>
