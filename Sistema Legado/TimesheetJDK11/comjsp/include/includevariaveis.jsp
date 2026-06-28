<!-- includevariaveis.jsp -->
<%
    //controle de acesso
    String inc_referer1  = "";
    String inc_referer2  = "";
    String inc_referer3  = "";
    String inc_telaParms = "";
    String inc_titulo    = "";
    int    inc_clear     = 0;
    
    //telas
    String  inc_browse     = "";
    String  inc_edit       = "";
    String  inc_submitedit = "";
    String  inc_pesquisa   = "";
    int     inc_rangesize  = 0;
    
    //botões
    boolean inc_bot_primeiro   = false;
    boolean inc_bot_anterior   = false;
    boolean inc_bot_proximo    = false;
    boolean inc_bot_ultimo     = false;
    boolean inc_bot_atulizar   = false;
    boolean inc_bot_pesquisa   = false;
    boolean inc_bot_novo       = false;
    
    //comandos sql
    String inc_select      = "";
    String inc_where       = "";
    String inc_colalt      = "";
    String inc_sellock     = "";
    String inc_update      = "";
    String inc_delete      = "";
    String inc_insert      = "";
%>
<!-- Fim includevariaveis.jsp -->