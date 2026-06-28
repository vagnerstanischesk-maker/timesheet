<%@ page language="java" import="java.util.*, javax.naming.*" contentType="text/html;charset=ISO-8859-1"%>
<%@ page import="java.sql.* , oracle.jdbc.*, oracle.jdbc.driver.OracleSQLException, combeans.*"%>
<%@ page import="java.sql.DriverManager, javax.naming.Context, javax.naming.InitialContext, javax.sql.*"%>
<%@ page import="javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse"%>
<%@ page import="javax.servlet.http.Cookie,javax.servlet.jsp.*,java.net.URL,java.net.InetAddress,java.sql.Connection"%>
<html>
    <head>
        <meta http-equiv="Expires" content="0"/>
        <meta http-equiv="Cache-Control" content="no-cache"/>
        <meta http-equiv="Pragma" content="no-cache"/>
        <%
Connection conn;
String sDatabase = "OLTP"; //pode ser OLTP ou DBM ou ROLAP
String sUSER = "";
String sPW = "";
String connStr = "";

      try {
         //DriverManager.registerDriver(new OracleDriver());
//System.out.println("conexaobean: VOU OBTER CONEXAO:" + new java.util.Date() + "." );
         //conn = DriverManager.getConnection(connStr, sUSER, sPW);
//System.out.println("conexaobean: OBTIVE    CONEXAO:" + new java.util.Date() + "." );

         DataSource ds = null;
         Context ctx = null;
         ctx = new InitialContext();
         ds = (DataSource)ctx.lookup("jdbc/OracleConnASDB");      
         conn = ds.getConnection();

        //Statement stm   = null;
        //ResultSet rs    = null;
        //stm = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        //rs =  stm.executeQuery("SELECT to_char(sysdate, 'dd/mm/yyyy hh24:mi:ss') from dual");
        //if(rs.next())
        //  System.out.println(""+rs.getString(1));
        //rs.close();
        //stm.close();

         //if(sDatabase.equals("OLTP"))
         //   conn = ConnectionPool.getConnection("bspd07");
         //else
         //   conn = ConnectionPool.getConnection("bspd10");
//System.out.println("Conectou !!!!!!!!!!!!!!!!!");
      }
      //o catch (SQLException exsql)
      catch (Exception ex) {
         //o conn.close();
         //o conn = DriverManager.getConnection(connStr, sUSER, sPW);
         //if(sDatabase.equals("OLTP"))
         //   ConnectionPool.releaseConnection("bspd07", conn);
         //else
         //   ConnectionPool.releaseConnection("bspd10", conn);
         throw new Exception("erro ao conectar");
      }
%>
        <title>Triscal</title>
    </head>
    <body>
        <!-- <table border="0" width="100%" cellspacing="0" cellpadding="0" height="100%">
<tr>
<td colspan="3" class="txtChumbo" height="55">
Esse site está temporariamente em manutenção. Acesse mais tarde.<br>
Obrigado.
</td>
</tr>
</table> -->
    </body>
</html>