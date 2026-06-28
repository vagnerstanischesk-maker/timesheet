<%@ page language  = "java" errorPage="errorpage.jsp" buffer="300kb"
    import = "java.util.*, javax.naming.*"
    contentType="text/html;charset=ISO-8859-1"%>
<%@ page import = "java.sql.* , oracle.jdbc.*, oracle.jdbc.driver.OracleSQLException" %>

<HTML>
<HEAD> <TITLE> The JDBCQuery JSP  </TITLE> </HEAD>
<BODY BGCOLOR=white>

<% //String connStr = "jdbc:oracle:thin:@192.168.3.3:1521:ASDB";
   String connStr = "jdbc:oracle:thin:@10.111.111.201:1521:ORCL";
   String searchCondition = request.getParameter("cond"); 
   if (searchCondition != null) { %>
      <H3> Search results for : <I> <%= searchCondition %> </I> </H3>
      <%= runQuery(connStr,searchCondition) %>
      <HR><BR>
<% }  %>

<B>Enter a search condition:</B>
<FORM METHOD=get> 
<INPUT TYPE="text" NAME="cond" SIZE=30>
<INPUT TYPE="submit" VALUE="Ask Oracle");
</FORM>
</BODY>
</HTML>
<%! 
  private String runQuery(String connStr, String cond) throws SQLException {
     Connection conn = null; 
     Statement stmt = null; 
     ResultSet rset = null; 
     try {
	DriverManager.registerDriver(new OracleDriver());
        conn = DriverManager.getConnection(connStr,
                                           "timesheet", "timesheet#131022");
        stmt = conn.createStatement();
        rset = stmt.executeQuery ("SELECT '1' FROM dual ");
	return (formatResult(rset));
     } catch (SQLException e) { 
         return ("<P> SQL error: <PRE> " + e + " </PRE> </P>\n");
     } finally {
         if (rset!= null) rset.close(); 
         if (stmt!= null) stmt.close();
         if (conn!= null) conn.close();
     }
  }

  private String formatResult(ResultSet rset) throws SQLException {
    StringBuffer sb = new StringBuffer();
    if (!rset.next()) 	
      sb.append("<P> No matching rows.<P>\n");
    else {  sb.append("<UL><B>"); 
	    do {  sb.append("<LI>" + rset.getString(1) + 
                            " earns $ " + rset.getString(1) + ".</LI>\n");
            } while (rset.next());
 	    sb.append("</B></UL>"); 
    }
    return sb.toString();
  }
%>
