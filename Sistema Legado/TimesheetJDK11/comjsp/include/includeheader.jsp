<!-- Inicio includeheader.jsp -->
<%
   String sUSUR_NM_LOGIN = "";
   if(comlogado) 
   { sUSUR_NM_LOGIN = "" + UtilBean.getVerificaNull(session.getAttribute("COM_CDG_USUR"));
   }
%>
<!-- | box info | -->
<% if(false)
   { 
%>
<div id="info">
<table border="0" width="121" cellspacing="0" cellpadding="0">
<tr>
<td width="120" bgcolor="#E6E6E6">
	<table border="0" width="120" cellspacing="0" cellpadding="0">
	<tr>
	<td colspan="3"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="120" height="2"></td>
	<tr>
	<td width="2"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="2" height="1"></td>
	<td width="116">
		<!-- | nome loja | -->
		<table width="116" cellspacing="0" cellpadding="0" border="0">
		<tr> 
		<td bgcolor="#979797" rowspan="3"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="1" height="14"></td>
		<td bgcolor="#979797"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="116" height="1"></td>
		<td bgcolor="#E7E7E7" rowspan="3"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="1" height="14"></td>
		</tr>
		<tr> 
		<td bgcolor="#B1B1B1" align="center" class="txtPreto"><%=sUSUR_NM_LOGIN%></td>
		</tr>
		<tr> 
		<td bgcolor="#E7E7E7"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="116" height="1"></td>
		</tr>
		</table>
		<!-- | end : nome loja | -->

		<table width="112" cellspacing="0" cellpadding="0" border="0" align="center">
		<tr>
		<td width="3"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="3" height="1"></td>

      <td width="106" class="txtCinza" align="center">Grupo: <%=session.getAttribute("DSC_GRUPO_ACESSO")%> </td>

		<td width="3"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="3" height="1"></td>
		</tr>
		</table>
	</td>
	<td width="2"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="2" height="1"></td>
	</tr>
	<tr>
	<td colspan="3"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="120" height="2"></td>
	<tr>
	</table>
</td>
<td width="1" bgcolor="#999999" valign="top"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixelbranco.gif" width="1" height="1"></td>
</tr>
<tr>
<td colspan="2" bgcolor="#999999"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixelbranco.gif" width="1" height="1"></td>
</tr>
</table>
<img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="1" height="10"><br>
</div>
<%
   } // if logado
%>
<!-- | end : box info | -->

<!-- | box banner | --> 
<div id=bannerOp>
</div>
<!-- | end : box banner | -->

<table border="0" cellspacing="0" cellpadding="0" width="780" height="100%"><tr height="79"><td colspan=2><!--| cabeçalho |-->
<table border=0 width="100%" height="80px"><tr><td colspan="2"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" height="5px"></td></tr>
   <tr>
    <td width="100%"><img src="/portalcom/images/logo.png" alt="logo" id="logo"></td>
    <td cellpadding="0" align="right" valign="baseline" height="100%" class="headerOp">
     <img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="550px" height="1px">
     <span style="font-family:Arial; color:#BD1010; font-size:8pt; font-weight:bold;">
<%   String UrlHelp = menubean.getUrlHelp(UtilBean.getJSP(request));
     if(comlogado) { 
%>     <a href="http://www.triscal.com.br/" target="_blank" name="sitetriscal" style="color: #BD1010;">TRISCAL</a>&nbsp;|&nbsp;
       <a href="javascript: void FNCNAVEGA( 'TelaInicial.jsp' );" style="color: #BD1010;" onClick="return TESTAFNCNAVEGA('TelaInicial.jsp');">TELA INICIAL</a>&nbsp;|&nbsp;
       <a href="javascript: void FNCNAVEGA( 'Tela_Log.jsp' );" style="color: #BD1010;" onClick="return TESTAFNCNAVEGA('Tela_Log.jsp');">TIMESHEET</a>&nbsp;|&nbsp;
       <!--<a href="javascript: void window.open('http://www.triscal.com.br/commit9ias/forms.jsp?s=<%=session.getAttribute("COM_SESSAOJSP") %>','SISADM','width=1, height=1, left=20000, top=20000,menubar=no, top=yes' );" style="color: #BD1010;" >FORMS (SISADM)</a>&nbsp;|&nbsp;-->
       <!--a href="http://infra.triscal.com.br:7779/pls/orasso/orasso.wwsso_app_user_mgr.change_password?p_done_url=http%3A%2F%2Fwww.triscal.com.br%2Fcommit9ias%2F" style="color: #BD1010;" >ALT.SENHA</a>&nbsp;|&nbsp;-->
       <a href="<% out.print(request.getContextPath() + "/logout.jsp"); %>" style="color: #BD1010;" >LOGOUT</a>
       <!--a href="javascript: void FNCNAVEGA( 'com_logout.jsp' );"  style="color:#BD1010;" onClick="return TESTAFNCNAVEGA( 'com_logout.jsp' )">SAIR</a-->
<%   }
     if(false && ! UrlHelp.equals("")) {
%>    <%=comlogado?" | ":""%><a class="txtCinza" href="javascript:void chamahelp('<%=UrlHelp%>');" style="color:#BD1010;" onclick="" onmouseover="" target="_self">AJUDA</a>
<%   } 
%>    &nbsp;&nbsp;      
     </span>
    </td>
   </tr>
   <tr>
    <td colspan="2"><img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/commit/footer.gif" width="100%" height="3px"></td>
   </tr>
   <tr>
<%
 // SimpleDateFormat df = new SimpleDateFormat("hh:mm' | 'EEEEE, dd' de 'MMMMM' de 'yyyyy");
%>
    <td align="left" style="color:#000000; background-color:#EEEEEE; font-family:Helvetica,Verdana,Arial; font-size:7pt;">
<!--<img id="CONTRAIRMENUESQ0" src="/portalcom/images/blue_r_arrow.gif" alt="Contrair o Menu" onClick="javascript: void contrair_menu_esq(this);" />
&nbsp;<a id="CONTRAIRMENUESQLINK0" href="javascript: void contrair_menu_esq(this);">Esconder o Menu</a>-->
<img id="CONTRAIRMENUESQ" src="/portalcom/images/blue_d_arrow.gif" alt="Expandir/Contrair o Menu" onClick="javascript: void contrair_menu_esq(this);" />
&nbsp;<a id="CONTRAIRMENUESQLINK" href="javascript: void contrair_menu_esq(this);">Mostrar o Menu</a>

<script LANGUAGE="javascript">
function endsWith2(str, suffix) {
  return str.indexOf(suffix, str.length - suffix.length) !== -1;
}
function contrair_menu_esq(itemcontrairexpandir) {
  if(! document.getElementById("CONTRAIRMENUESQ"))
    return;
  if(! document.getElementById("MenuEsq"))
    return;

  var imagem = document.getElementById("CONTRAIRMENUESQ").src + "";

  if(endsWith2(imagem, "blue_r_arrow.gif")) {
    //estah contraindo
    if (document.getElementById("MenuEsq").style.display == "") 
      document.getElementById("MenuEsq").style.display = "none";

    imagem = imagem.substring(0, imagem.length - "blue_r_arrow.gif".length);
    imagem = imagem + "blue_d_arrow.gif";
    document.getElementById("CONTRAIRMENUESQ").src = imagem;
    document.getElementById("CONTRAIRMENUESQLINK").innerHTML = "Mostrar o Menu";
  } else {
    //estah expandindo
    if (document.getElementById("MenuEsq").style.display == "none") 
      document.getElementById("MenuEsq").style.display = "";
    
    imagem = imagem.substring(0, imagem.length - "blue_d_arrow.gif".length);
    imagem = imagem + "blue_r_arrow.gif";
    document.getElementById("CONTRAIRMENUESQ").src = imagem;
    document.getElementById("CONTRAIRMENUESQLINK").innerHTML = "Esconder o Menu";
  }
}
</script>
    </td>
    <td align="center" style="color:#000000; background-color:#EEEEEE; font-family:Helvetica,Verdana,Arial; font-size:7pt;"><%=UtilBean.formataData(new java.util.Date(),"hh:mm' | 'EEEEE, dd' de 'MMMMM' de 'yyyy")%> | Login: <%=UtilBean.session_getValue(session,"COM_CDG_USUR")%>
	</td>
   </tr>
  </table>
  </td>
  </tr>
  <!-- Fim includeheader.jsp -->