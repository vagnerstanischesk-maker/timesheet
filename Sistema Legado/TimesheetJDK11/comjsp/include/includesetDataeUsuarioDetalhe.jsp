<!-- Inicio includesetDataeUsuarioDetalhe.jsp -->
<%   String DT_INCLUSAO_DETALHE = "";
     String USUR_INCLUSAO_DETALHE = "";
     String DT_ALTERACAO_DETALHE = "";
     String USUR_ALTERACAO_DETALHE = "";
     if(oper.equals("I"))
     {  DT_INCLUSAO_DETALHE = DT_INCLUSAO;
        USUR_INCLUSAO_DETALHE = USUR_INCLUSAO;
     }
     else
     {  DT_INCLUSAO_DETALHE = DT_ALTERACAO;
        DT_ALTERACAO_DETALHE = DT_ALTERACAO;
        USUR_INCLUSAO_DETALHE = USUR_ALTERACAO;
        USUR_ALTERACAO_DETALHE = USUR_ALTERACAO;
     }
%>
<!-- Fim includesetDataeUsuarioDetalhe.jsp -->
