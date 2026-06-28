<!-- Inicio includesetaoper.jsp -->
<%
  String oper = "";
  if(request.getParameter("oper") == null)
  { if(session.getAttribute("INSERINDO").toString().equals("true"))
      oper="I";
    else
      oper="E";
  }
  else
  { if(request.getParameter("oper").equals("I"))
      oper="I";
    else
      oper="E";
  }
%>
<!-- Fim includesetaoper.jsp -->
