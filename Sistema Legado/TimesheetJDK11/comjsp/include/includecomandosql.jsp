<!-- includecomandosql.jsp -->
<%
     cmDbBeanInclude.setsaida(out);
     cmDbBeanInclude.setPaginaBrowse(session, inc_browse); //este método deve vir obrigatoriamente na frente dos outros
     cmDbBeanInclude.setPaginaAnterior(sPaginaAnterior);
     cmDbBeanInclude.setPaginaEdit(inc_edit);
     cmDbBeanInclude.setPaginaSubmit(inc_submitedit);
     if (! acesso.verificaAcessoMenu(cmDbBeanInclude.getPaginaEdit()).equals("L"))
        cmDbBeanInclude.setPaginaInsert("javascript:void goinserir('" + cmDbBeanInclude.getPaginaEdit() + "')");
     else
        cmDbBeanInclude.setPaginaInsert("");
     cmDbBeanInclude.setPaginaConsulta(inc_pesquisa);
     cmDbBeanInclude.setRangeSize(inc_rangesize); //-1 ou 1 ou n
     if (! acesso.verificaAcessoMenu(cmDbBeanInclude.getPaginaEdit()).equals("L"))
        cmDbBeanInclude.setBotoes(inc_bot_primeiro, inc_bot_anterior, inc_bot_proximo, inc_bot_ultimo, inc_bot_atulizar, inc_bot_pesquisa, inc_bot_novo);
     else
        cmDbBeanInclude.setBotoes(inc_bot_primeiro, inc_bot_anterior, inc_bot_proximo, inc_bot_ultimo, inc_bot_atulizar, inc_bot_pesquisa, false);

     cmDbBeanInclude.seticonexaobean( iconexaobean );

     if(! inc_select.equals(""))
     {
        if(inc_where.equalsIgnoreCase("montawhere"))
          cmDbBeanInclude.setsql(inc_select, cmDbBeanInclude.montaWhere(session, request, acesso.getJSP(request)));
        else 
          cmDbBeanInclude.setsql(inc_select,inc_where);
      }

     cmDbBeanInclude.setColunaAlteracao(inc_colalt);
     cmDbBeanInclude.setsqlLock(inc_sellock);
     cmDbBeanInclude.setsqlUp(inc_update);
     cmDbBeanInclude.setsqlDel(inc_delete);
     cmDbBeanInclude.setsqlIns(inc_insert);

     cmDbBeanInclude.seleciona(session, request);
     
     if(! inc_where.equalsIgnoreCase("montawhere"))
       cmDbBeanInclude.PreparaInicioPagina(request);
%>
<!-- Fim includecomandosql.jsp -->