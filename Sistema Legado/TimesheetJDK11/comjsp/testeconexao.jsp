<%@ page language  = "java"
    import = "java.util.*, javax.naming.*"
    contentType="text/html;charset=ISO-8859-1"%>
<%@ page import = "java.sql.* , oracle.jdbc.*, oracle.jdbc.driver.OracleSQLException, combeans.*" %>

<% boolean conectado = false; %>

<html>
<BODY>
<jsp:useBean id="icontroleapp" class="combeans.appControlBean" scope="application">
</jsp:useBean>

<jsp:useBean id="iconexaobean" class="combeans.conexaoBean" scope="session">
</jsp:useBean>

<% try { // principal
%>
   <% conectado = true;
      try
      { iconexaobean.execute(icontroleapp, session, request, response, out);
        iconexaobean.verifica();
      }
      catch (Exception exignored) { 
          conectado = false; out.print("erro=" + exignored + "<BR>");
      }
   %>

<jsp:useBean id="selectERRO" class="combeans.selectBean" scope="request">
  </jsp:useBean>
<%
  String TXT_RETORNO = "";

     if(conectado)
     {  
        selectERRO.setsql(iconexaobean, "SELECT TO_CHAR(SYSDATE, 'DD-MON-YYYY HH24:MI:SS') DATA FROM DUAL ");
        selectERRO.execute();

        if(selectERRO.next())
        {
           TXT_RETORNO = selectERRO.getColuna("DATA").toString();
        }
        selectERRO.fechar();
     }
%>

<BR>        
RETORNO DO SELECT=<%=TXT_RETORNO %>.
<BR>        

<jsp:useBean id="finaliza" class="combeans.finalizeBean" scope="page">
</jsp:useBean>
<%
   try {
      finaliza.execute(iconexaobean, out, request, response, session);
   }
   catch (Exception ex1) {
      out.print("<BR>Erro ao finalizar="+ex1);
   }
%>

</body>
</html>

   <% } // fim try
      catch (Exception exprincipal) {
         out.print("<BR>Erro exception principal="+ exprincipal);
      }
      finally {
         if(conectado)
         {   iconexaobean.desconecta(iconexaobean, out, request, response, session);
             conectado = false;
         }
      }
   %>
