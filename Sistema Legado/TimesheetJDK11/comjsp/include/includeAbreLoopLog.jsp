<%
   /*================ Alexandre 03/07/2002 ===================
      O include foi alterado pelo tratamento de campos novos.
      Os campos novos são inseridos junto com os existentes
      seguindo a ordem do dia. O tratamento segue abaixo.
   =========================================================*/

 // ============= Início do bloco do includeAbreLoopLog ===============

boolean saiLoop = false;
boolean fimBanco = true;
UtilBean.geraDebugLog("<BR>C19---11", icontroleapp, session, request, response, out); 

int diaAtual = Integer.parseInt(request.getParameter("iniDia"));
int diaFinal = Integer.parseInt(request.getParameter("finDia"));
int diaBanco = 0;
//Christiano---18_12_2002---Alteracao Periodo do Log para 11a25 e 26a10---inicio---
cal.set(cal.DAY_OF_MONTH, diaAtual);
UtilBean.geraDebugLog("<BR>C19---12", icontroleapp, session, request, response, out); 
//Christiano---18_12_2002---Alteracao Periodo do Log para 11a25 e 26a10---fim------
while (! saiLoop) {
    if (fimBanco) {
      if ( diaBanco == diaAtual) {
        oper_detalhe = "";
      }
      else if (UtilBean.getLOG_ORDERBY(diaBanco) > UtilBean.getLOG_ORDERBY(diaAtual)) {
        oper_detalhe = "I";
      }
      else {
UtilBean.geraDebugLog("<BR>C19---13", icontroleapp, session, request, response, out); 
        if (cmDbBeanInclude.PreenchePagina()) {
          diaBanco = Integer.parseInt(cmDbBeanInclude.getColuna("LOG_NR_DIA")+"");
          if (UtilBean.getLOG_ORDERBY(diaBanco) > UtilBean.getLOG_ORDERBY(diaAtual)) {
            oper_detalhe = "I";
          }
          else {
            oper_detalhe = "";
          }
        }
        else{
          oper_detalhe =  "I";
          fimBanco = false;
        }
      }
    }
    else{
      oper_detalhe = "I";
    }
UtilBean.geraDebugLog("<BR>C19---14", icontroleapp, session, request, response, out); 

    if(diaAtual == diaFinal){
      saiLoop = true;
    }

        if (primeiro)
        {  primeiro = false;
%>
         <input type="hidden" name="reginicial<%=numeroTabeladetalhe%>"
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

 // ============= Fim do bloco do includeAbreLoopLog ===============
%>

<!-- -----------INICIO LINHAS DENTRO DO LOOP DETALHE----------- -->
