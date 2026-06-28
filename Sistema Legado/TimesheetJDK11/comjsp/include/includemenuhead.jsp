<!-- Inicio includemenuhead.jsp -->
<% menubean.verificaMenuCarregado(iconexaobean, session, request, response, out, comlogado); %>

<!--script language="JavaScript" src="/portalcom/webapp<%=session.getAttribute("COM_DOMINIO")%>/cabo/jslib/menuesquerda.js"></script-->
<script src="/portalcom/webapp<%=session.getAttribute("COM_DOMINIO")%>/cabo/jslib/over.js"></script>

<% menubean.preparaInicioMenuLocal(session, request, response, out); %>

<!--script language="JavaScript1.2" src="/portalcom/webapp<%=session.getAttribute("COM_DOMINIO")%>/cabo/jslib/menu.js"></script-->
<SCRIPT LANGUAGE="JavaScript1.2">
<!--
var isMinNS4=(navigator.appName.indexOf("Netscape")>=0&&parseFloat(navigator.appVersion)>=4)?1:0; //c
var isMinIE4=(document.all)?1:0; //c
var isMinIE5=(isMinIE4&&navigator.appVersion.indexOf("5.")>=0)?1:0; //c

function onLoad() {
        loadMenus();
}
function loadMenus() {
    //Dynamic Menus
    //addMenuItem(label, action, color, mouseover, mouseout)
    window.topMenu = new Menu();
    topMenu.addMenuItem("my menu item A");

//---Menu para telas de pesquisa:-------------------------------------------------------------
    window.myMenu = new Menu("White");
    //myMenu.addMenuItem("Para opções: o texto no campo deve ser separado por vírgulas! Para maiores informações clique aqui.","top.window.location='http://www...html'");
    myMenu.addMenuItem("Para opções: o texto no campo deve ser separado por vírgulas! Para maiores informações clique aqui.","");
    myMenu.fontColor = "blue";
    myMenu.fontColorHilite = "blue";
    myMenu.bgColor = "#dddddd";
    myMenu.menuItemBgColor = "white";
    myMenu.menuHiliteBgColor = "white";
    myMenu.menuItemHeight = 50;
    myMenu.fontSize = 12;
    myMenu.fontWeight = "Bold";
    myMenu.fontFamily = "corrier";


    <%=menubean.montaMenuHead() %>

    topMenu0.disableDrag = true;
    topMenu.writeMenus();
}
//-->
</SCRIPT>
<!-- Fim includemenuhead.jsp -->
