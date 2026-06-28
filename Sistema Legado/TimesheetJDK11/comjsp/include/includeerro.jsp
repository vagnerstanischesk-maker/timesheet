<!-- Inicio includeerro.jsp -->
		<!-- |  topo | -->
		<table border="0" width="100%" cellspacing="0" cellpadding="0">
		<tr>
		<td width="466" background="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/layout/grad_laranja.gif">
			<table border="0" width="466" cellspacing="0" cellpadding="0">
			<tr>
			<td width="6"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="6" height="11"></td>
			<td width="100%" class="txtCaramelo"><!-- | txt | --><%=mensagemtitulo%><!-- | end : txt | --></td>
			</tr>
			<tr>
			<td colspan="2"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/layout/pontilhado.gif"></td>
			</tr>
			</table>
		</td>
		<td width="100%" background="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/layout/layout_grad_centro1.gif"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="1" height="22"></td>
		</tr>
		<tr>
		<td colspan="2"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="1" height="4"></td>
		</tr>
		</table>
		<!-- | end : topo | -->


		<table border="0" width="470" cellspacing="0" cellpadding="0">
		<tr>
		<td colspan="2"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="1" height="18"></td>
		</tr>
		<tr>
		<td width="16"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="16" height="1"></td>
		<td width="100%">
			<!-- | conteúdo | -->
			<table border="0" width="460" cellspacing="0" cellpadding="0">
			<tr>
			<td width="13" valign="top"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/misc/bullet_amarelo.gif"></td>
			<td width="7"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="7" height="1"></td>
			<td width="100%" class="txtChumbo">
                         <%=mensagem%>
                         <BR><BR><center><a HREF="/portalcom/jsp/TelaInicial.jsp">Tela Inicial</a></center>
		  <br><br>
			</td>
			</tr>

			</table>
			<!-- | end : conteúdo | -->
		</td>
		</tr>
		</table>
		<!--| end : estrutura do conteúdo |-->
<!-- Fim includeerro.jsp -->
