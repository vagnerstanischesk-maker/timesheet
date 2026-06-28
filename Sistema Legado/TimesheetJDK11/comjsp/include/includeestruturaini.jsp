	<!--| coluna direita [conte�do] |-->
<% try { 
      if ((false) && (UtilBean.getJSP(request).equals("TelaInicial.jsp"))) { %>
	<table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%" background="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/commit/fundoclaro.jpg">
<%    } else { %>
	<table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
<%    } 
      } catch (Exception exignored) { %>
	<table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
<%    } %>
	<tr>
	<td width="4" valign="top">
  </td>
	<td width="600" valign="top">
		<!-- |  topo | -->
<% if(! inc_titulo.equals("")) {
%>
		<table border="0" width="100%" cellspacing="0" cellpadding="0">
		<tr>
		<td width="100%">
			<table border="0" width="100%" cellspacing="0" cellpadding="0">
			<tr>
			<td width="6"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="6" height="11"></td>
			<td width="100%" class="txtHeaderLink"><!-- | txt | --><%=inc_titulo%><!-- | end : txt | --></td>
			</tr>
			<tr>
			<td colspan="2"><!--img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/layout/pontilhado.gif"--></td>
			</tr>
			</table>
		</td>
		</tr>
		<tr>
		<td><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="1" height="4"></td>
		</tr>
		</table>
<% }
%>
		<!-- | end : topo | -->
		<!--| estrutura do conte�do |-->
		<table border="0" width="470" cellspacing="0" cellpadding="0">
		<tr>
		<td colspan="2"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="1" height="18"></td>
		</tr>
		<tr>
		<td width="16"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="16" height="1"></td>
		<td width="100%">
			<!-- | conte�do | -->
			<table border="0" width="460" cellspacing="0" cellpadding="0">
			<tr>
			<td width="13" valign="top"></td>
			<td width="7"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="7" height="1"></td>
			<td class="txtChumbo">
	