<!-- Inicio includemetaerror.jsp -->
<% boolean comlogado =  UtilBean.verificaLogado(session, request, response); %>
<META NAME="GENERATOR" CONTENT="Oracle JDeveloper">
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=ISO-8859-1">
<META name="description" content="Triscal - O portal Triscal Consultoria">
<META name="keywords" content="Triscal, IBM, Oracle, banco de dados, database, BI, CRM, application, aplicação, ...">
<META NAME="author" CONTENT="Triscal">

<SCRIPT LANGUAGE="JavaScript">
function mostra(item){
	if (item.style.display=='none'){
  		item.style.display='';
  }else{
  		item.style.display='none';
   	}
}

//function setGeraFarmaNavMenuCookie(cdgNivel, cdgMenuPai, cdgMenu){
//	document.cookie = "COM_NavMenuCookie_Nivel="+cdgNivel;
//	document.cookie = "COM_NavMenuCookie_CdgPai="+cdgMenuPai;
//  document.cookie = "COM_NavMenuLinkCookie_Cdg="+cdgMenu;
//}

function setMenuLinkStyle(subMenuId){
        eval( subMenuId+'.className = '+unescape("%27menuAmarelo%27"));
}

</script>

<jsp:useBean id="menubean" class="combeans.menuBean2" scope="session">
</jsp:useBean>
<!-- Fim includemetaerror.jsp -->
