<!-- Inicio includealerta.jsp -->
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
			<td width="100%" class="txtChumbo" valign="bottom"><span class="txtBlackBold"><%=mensagemtitulo%></span>
			<br><br>
                         <%=mensagem%>
		  <br><br>
			</td>
			</tr>

			</table>
			<!-- | end : conteúdo | -->
		</td>
		</tr>
		</table>
		<!--| end : estrutura do conteúdo |-->
<!-- Fim includealerta.jsp -->
