<%@ page buffer="5kb" autoFlush="true" %>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma", "no-cache");
response.setHeader("Expires", "Thu, 29 Oct 2000 17:04:19 GMT");
HttpSession mysession = request.getSession(true);

mysession.setAttribute("DEBUG", "S");

%>

<html>
<head>
<title>Habilitacao de Debug (table com_debug)</title>
</head>
<body vlink="#BD1010" alink="#BD1010">

<table border="0" cellspacing="0" cellpadding="0" height="80" background="" align="center" width="100%">
  <tr> 
    <td bgcolor="#FFFFFF" width="30%"><img src="/portalcom/images/logo.png" alt="logo" id="logo"></td>
    <td width="45%" align="center" ><h2>Debug Habilitado com Sucesso !</h2></td>
    <td width="25%" >&nbsp;&nbsp;</td>
  </tr>
</table>

</body>
</html>
