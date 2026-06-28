<!-- Inicio includesetDataeUsuarioMaster.jsp -->
<%String DT_INCLUSAO    = "";
  String DT_ALTERACAO   = "";
  String USUR_INCLUSAO  = "";
  String USUR_ALTERACAO = "";
  if(oper.equals("I"))
  {  DT_INCLUSAO    = "" + sysdate.selectSysdate(iconexaobean.getConnection());
     DT_ALTERACAO   = "";
     USUR_INCLUSAO  = "" + session.getAttribute("COM_CDG_USUR");
     USUR_ALTERACAO = "";
  }
  else
  {  
     DT_INCLUSAO    = "" + UtilBean.getVerificaNull(cmDbBeanInclude.getColuna("DT_INCLUSAO"));
     DT_ALTERACAO   = "" + sysdate.selectSysdate(iconexaobean.getConnection());
     USUR_INCLUSAO  = "" + UtilBean.getVerificaNull(cmDbBeanInclude.getColuna("USUR_INCLUSAO"));
     USUR_ALTERACAO = "" + session.getAttribute("COM_CDG_USUR");
  }
%>
<!-- Fim includesetDataeUsuarioMaster.jsp -->
