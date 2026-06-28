<!-- Inicio includemenuesqini.jsp -->
<tr>
<form name="iNavega" method="post" action="">
<input type="hidden" name="INAVEGACLICOU" VALUE="N">
</form>
<td width="166" class="menuOp" id="MenuEsq" valign="top" style="display: none;">
   <!--| coluna esquerda |-->
<img src="/portalcom/images<%=session.getAttribute("COM_DOMINIO")%>/images/pixel.gif" width="1" height="1"><br>
<table width="166" border="0" cellspacing="0" cellpadding="0">
	<tr>
	 <td width="6"><img src="../images/pixel.gif" width="6" height="1"></td>
	 <td width="160"><img src="../images/pixel.gif" width="160" height="1"><br>
	 	
<%        menubean.setImageDir("/portalcom/images"+session.getAttribute("COM_DOMINIO")); 

          if(UtilBean.getJSP(request).startsWith("errorpage"))
	     menubean.setPaginaAtual(""+UtilBean.session_getValue(session, "COM_ULTIMA_PAGINA")); //2708-chris
          else if(UtilBean.getJSP(request).startsWith("TelaInicial"))
	     menubean.setPaginaAtual("redirmain.jsp"); //2708-chris
          else if(UtilBean.getJSP(request).startsWith("com_semacesso"))
	     menubean.setPaginaAtual(""+UtilBean.session_getValue(session, "COM_MENU_PAGINA_ATUAL")); //2708-chris
          else if(UtilBean.getJSP(request).startsWith("com_referererror"))
	     menubean.setPaginaAtual(""+UtilBean.session_getValue(session, "COM_MENU_PAGINA_ATUAL")); //2708-chris
          else if(UtilBean.getJSP(request).startsWith("com_msgtimeout"))
	     menubean.setPaginaAtual(""+UtilBean.session_getValue(session, "COM_MENU_PAGINA_ATUAL")); //2708-chris
          else if(UtilBean.getJSP(request).startsWith("com_telabloqueada"))
	     menubean.setPaginaAtual(""+UtilBean.session_getValue(session, "COM_MENU_PAGINA_ATUAL")); //2708-chris
          else if(UtilBean.getJSP(request).startsWith("com_CampanhaFacil_Olap"))
	     menubean.setPaginaAtual("com_CampanhaFacil_Inst.jsp"); //2708-chris
          else if(UtilBean.getJSP(request).startsWith("com_CampanhaFacil_Cross"))
	     menubean.setPaginaAtual("com_CampanhaFacil_Inst.jsp"); //2708-chris
          else if(UtilBean.getJSP(request).startsWith("com_CampanhaFacil_Mapas"))
	     menubean.setPaginaAtual("com_CampanhaFacil_Inst.jsp"); //2708-chris
          else if(UtilBean.getJSP(request).startsWith("com_Campanha_Resultados")) {
             if(UtilBean.getReferer(request).equals("com_CampanhaFacil_Mapas.jsp"))
	       menubean.setPaginaAtual("com_CampanhaFacil_Inst.jsp"); //2708-chris
	     else if(UtilBean.getReferer(request).equals("com_CampanhaRapida.jsp"))
	       menubean.setPaginaAtual("com_CampanhaRapida.jsp"); //2708-chris
          }
          else if(UtilBean.getJSP(request).startsWith("com_RetornoCamp_Detalhes"))
	     menubean.setPaginaAtual("com_RetornoCamp.jsp"); //2708-chris
          else
             if(request.getAttribute("COM_MENU_SETPAGINAATUAL") != null)
      	        menubean.setPaginaAtual(""+request.getAttribute("COM_MENU_SETPAGINAATUAL")); //29012002-chris
             else
      	        menubean.setPaginaAtual(UtilBean.getJSP(request)); //2708-chris
%>        
         <%=menubean.montaMenuDinamico(session, request, response) %>
         <!-- | end : menu | -->
	 </td>
	</tr>
      </table>      
   <!--| end : coluna esquerda |-->
</td>
<td width="604" valign="top">
 
<!-- Fim includemenuesqini.jsp -->
