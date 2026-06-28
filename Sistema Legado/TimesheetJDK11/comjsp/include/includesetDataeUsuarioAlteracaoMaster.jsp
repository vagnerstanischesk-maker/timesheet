<!-- Inicio includesetDataeUsuarioAlteracaoMaster.jsp -->
<%String DTA_ULT_ALT    = "";
   String DTA_ATUAL    ="" + sysdate.selectSysdate(iconexaobean.getConnection());
  String CDG_USUR_COM_ADM  = "";
  if(oper.equals("I"))
     DTA_ULT_ALT    = "" + sysdate.selectSysdate(iconexaobean.getConnection());
  else
     DTA_ULT_ALT    = "" + UtilBean.getVerificaNull(cmDbBeanInclude.getColuna("DTA_ULT_ALT"));

  CDG_USUR_COM_ADM = "" + session.getAttribute("COM_CDG_USUR");

%>
<!-- Fim includesetDataeUsuarioAlteracaoMaster.jsp -->
