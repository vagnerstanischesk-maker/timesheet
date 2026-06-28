<!-- Inicio includefechaLoopDetalhe.jsp -->
<%      nIndex++;
     } //fim while
     totalLinhas = nIndex;
%>   <input type="hidden" name="regfinal<%=numeroTabeladetalhe%>" value="<%=cmDbBeanInclude.getregAtual()%>">
     <input type="hidden" name="TOTALLINHAS<%=numeroTabeladetalhe%>" value="<%=totalLinhas%>">
<!-- Fim includefechaLoopDetalhe.jsp -->
