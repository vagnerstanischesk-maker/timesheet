<!-- Inicio includeabreLoopAlteracaoDetalheSemInsercao.jsp -->
<%
     boolean saiLoop = false;
     int linhasParaInserir = 0;
     while (! saiLoop)
     {  
        if(cmDbBeanInclude.PreenchePagina())
        {  oper_detalhe = "";
        }
        else
        {  saiLoop = true;
           break;
        }

        if (primeiro)
        {  primeiro = false;
%>         <input type="hidden" name="reginicial<%=numeroTabeladetalhe%>" 
                  value="<%=cmDbBeanInclude.getregAtual()%>">
<%
        }
%>      <input type="hidden" name="reg<%=numeroTabeladetalhe%><%=nIndex%>" 
               value="<%=cmDbBeanInclude.getregAtual()%>">

        <input type="hidden" name="STATUS<%=numeroTabeladetalhe%><%=nIndex%>"
               value="<%=(oper_detalhe.equals("I")? "I": "-") %>">

        <input type="hidden" name="_ISNEWROW<%=numeroTabeladetalhe%><%=nIndex%>"
               value="<%=(oper_detalhe.equals("I")? "true": "false") %>">

<%      if(cmDbBeanInclude.getregAtual() != 0)
        {
            out.print(cmDbBeanInclude.showCamposPrevious(oper_detalhe, acesso, request, nIndex));
%>
            <%=cmDbBeanInclude.showCampoInputHidden( "DTA_ULT_ALT", DTA_ULT_ALT_DETALHE, nIndex, oper_detalhe, acesso, request, false ) %>
            <%=cmDbBeanInclude.showCampoInputHidden( "CDG_USUR_COM_ADM", CDG_USUR_COM_ADM_DETALHE , nIndex, oper_detalhe, acesso, request, false ) %>
<%      } //fim if regatual != 0
%>
<!-- Fim includeabreLoopAlteracaoDetalheSemInsercao.jsp -->
