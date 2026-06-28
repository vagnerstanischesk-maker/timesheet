<!-- Inicio includelink.jsp -->
<% if(UtilBean.session_getValue(session, "COM_CSS") == null)
      UtilBean.session_putValue(session, "COM_CSS", "/portalcom/webapp"+session.getAttribute("COM_DOMINIO")+"/css/oracle.css");
%>
<link REL="STYLESHEET" TYPE="text/css" HREF="<%=UtilBean.session_getValue(session, "COM_CSS")%>">
<style type="text/css">
<!--
.semsub1 {  font-family: Verdana, Arial, Helvetica, sans-serif, Tahoma; font-size: 7pt; color: #FFFFFF; text-decoration: none}
-->
</style>

<SCRIPT LANGUAGE="JavaScript1.2">
<!--
function chamahelp( urlhelp ) {
  window.open(".\\help\\"+urlhelp,"help","toolbar=no,location=no,directories=no,menubar=no,scrollbars=yes,status=no,width=560,height=440,resizable=no");
  return true;
}
//-->
</SCRIPT>
<!-- Fim includelink.jsp -->
