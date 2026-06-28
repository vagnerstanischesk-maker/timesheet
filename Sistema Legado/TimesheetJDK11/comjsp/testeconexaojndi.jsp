<%@ page language  = "java"
    import = "java.util.*, javax.naming.*"
    contentType="text/html;charset=ISO-8859-1"%>
<%@ page import = "java.sql.* , javax.sql.DataSource, oracle.jdbc.*, oracle.jdbc.driver.OracleSQLException" %>

<% boolean conectado = false; 

%>

<html>
<BODY>

   <% 
   out.print("01<BR>");
         String TXT_RETORNO="";
         //ds = (DataSource)ctx.lookup("jdbc/OracleConnASDB");  
      try
      { 
   out.print("02<BR>");
Context ctx = new InitialContext(); 
   out.print("03<BR>");
         DataSource ds = (DataSource)ctx.lookup("java:/OracleConnASDB");  
          
   out.print("04<BR>");
         Connection conn = ds.getConnection();
   out.print("05<BR>");

        Statement stm = (Statement)conn.createStatement();
   out.print("06<BR>");
        ResultSet rs = (ResultSet)stm.executeQuery("SELECT TO_CHAR(SYSDATE,'YYYYMMDD HH24:MI:SS') TESTE FROM DUAL");
   out.print("07<BR>");
        rs.next();
   out.print("08<BR>");
        out.print("query=" + rs.getString("TESTE") + "<BR>");
   out.print("09<BR>");

      }
      catch (Exception exignored) { 
          conectado = false; out.print("erro=" + exignored + "<BR>");
      }

%>

</body>
</html>
