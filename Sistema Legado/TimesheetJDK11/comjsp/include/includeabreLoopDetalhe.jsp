<!-- Inicio includeabreLoopdetalhe.jsp -->
<%
     boolean saiLoop = false;
     int linhasParaInserir = 0;
     while (! saiLoop)
     {  if(linhasParaInserir == 0)
        {  if(cmDbBeanInclude.PreenchePagina())
           {  oper_detalhe = "";
           }
           else
           {  linhasParaInserir = 1;
              oper_detalhe = "I";
           }
           if(linhasParaInserir >= linhasEmBranco)
              saiLoop = true;
        }
        else
        {  linhasParaInserir++;
           if(linhasParaInserir >= linhasEmBranco)
              saiLoop = true;
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
            <%=cmDbBeanInclude.showCampoInputHidden( "DT_INCLUSAO", (oper_detalhe.equals("I")? DT_INCLUSAO_DETALHE : "" + UtilBean.getVerificaNull(cmDbBeanInclude.getColuna("DT_INCLUSAO"))),
                                               nIndex, oper_detalhe, acesso, request, false ) %>
            <%=cmDbBeanInclude.showCampoInputHidden( "DT_ALTERACAO", (oper_detalhe.equals("I")? "" : DT_ALTERACAO_DETALHE),
                                               nIndex, oper_detalhe, acesso, request, false ) %>
            <%=cmDbBeanInclude.showCampoInputHidden( "USUR_INCLUSAO", (oper_detalhe.equals("I")? USUR_INCLUSAO : "" + UtilBean.getVerificaNull(cmDbBeanInclude.getColuna("USUR_INCLUSAO"))),
                                               nIndex, oper_detalhe, acesso, request, false ) %>
            <%=cmDbBeanInclude.showCampoInputHidden( "USUR_ALTERACAO", (oper_detalhe.equals("I")? "" : USUR_ALTERACAO_DETALHE),
                                               nIndex, oper_detalhe, acesso, request, false ) %>
<%      } //fim if regatual != 0
%>
<!-- Fim includeabreLoopdetalhe.jsp -->
