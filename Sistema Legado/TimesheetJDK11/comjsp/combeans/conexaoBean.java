/**************************************************************************
Empresa: Triscal
Autor: Christiano Chamma
Data: Janeiro de 2001

Descrição:
      Classe de conexão com o banco de dados. Esta classe utiliza um
   JDBC Thin Driver.

**************************************************************************/
package combeans;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.text.*;
import java.sql.*;
import javax.sql.DataSource;

import oracle.jdbc.*; 

//import oracle.jsp.dbutil.*;
////import oracle.jsp.event.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.io.PrintWriter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.*;
//import oracle.jbo.*;
//import oracle.jdeveloper.html.*;

public class conexaoBean extends Object implements HttpSessionBindingListener {
// implements JspScopeListener {

Connection conn;
String sDatabase = "OLTP"; //pode ser OLTP ou DBM ou ROLAP
String sUSER = "";
String sPW = "";
String connStr = "";
appControlBean iappControlBean = null;
Hashtable ht_requests = new Hashtable(); //12/12/2001
String jspAnterior = "";
private static BundleFilesManager bundle = new BundleFilesManager();

  public String getVersao()
  {   return("17/01/2002");
  }

   public void addRequest(HttpServletRequest request, String p_jsp) {
      String parametro = "";

      try {
         if(p_jsp == null)
            parametro = UtilBean.getJSP(request);
         else
            parametro = p_jsp;

         //Todo este tratamento do ht_requests foi feito para o caso que acontece quando o mesmo
         //   JSP é executado em paralelo (ao clicar 2 vezes em seu link). O erro acontecia pois
         //   a primeira execução pegava uma conexão da session e começava a fazer os selects.
         //   Enquanto isso a segunda requisição usava a mesma conexão já na session e começava
         //   a fazer os selects também. A requisição que terminasse primeiro iria retirar a
         //   conexão da session, liberando-a. Isso ocasionava um erro na outra requisição que
         //   ainda estivesse rodando, pois ela iria utilizar a conexão que já tinha sido liberada.
         //   Agora eu só libero a conexão quando a última requisição para a mesma página terminar.

         //Caso o jsp atual não seja igual ao anterior, limpo o Hashtable de requests na fila.
         if(! jspAnterior.equals(parametro))
         {  ht_requests.clear();
         }
         jspAnterior = parametro;

         if(! ht_requests.containsKey(request))
         {  ht_requests.put(request, "1"); }
      }
      catch (Exception ex) {
         System.out.println("conexaobean: ERRO no addRequest:" + new java.util.Date() + "=" + ex + "." );
      }
   }
   public void removeRequest(HttpServletRequest request) {
      int i = 0;
      try {
         if(ht_requests.containsKey(request))
         {  ht_requests.remove(request); }
      }
      catch (Exception ex) {
         System.out.println("conexaobean: ERRO no removeRequest:" + new java.util.Date() + "=" + ex + "." );
      }
   }
   public int getNumRequests() {
      return( ht_requests.size() );
   }

   public void setDatabase( String value ) {
      sDatabase = value;
   }
   public Connection getConnection() {
      return conn;
   }

   //public synchronized void execute( javax.servlet.ServletContext application )
   //       throws SQLException, Exception
   //{
   //   execute();
   //}

   public synchronized void execute()
          throws SQLException, Exception
   {
System.out.print("conexaobean: execute sem parametros APP WARNING" );

      if (conn != null)
        if(isConnected())
          return;

      //cbean.setUser("scott");
      //cbean.setPassword("tiger");
      if(sDatabase.equals("OLTP")) {

         //Properties prop = new Properties();
         //InputStream input = null;
         
         //sUSER = "commit9ias";
         //sPW = "commit9ias10g";
         ////connStr = "jdbc:oracle:thin:@1.1.1.197:1521:IASDB1";
         ////connStr = "jdbc:oracle:thin:@192.168.1.11:1521:ASDB";
         ////connStr = "jdbc:oracle:thin:@192.168.3.3:1521:ASDB";
         //connStr = "jdbc:oracle:thin:@localhost:1521:star";
         ////connStr = "jdbc:oracle:thin:@"
         ////         + bundle.getBundle("conexao").getObject("HOST") + ":"
         ////         + bundle.getBundle("conexao").getObject("PORT") + ":"
         ////         + bundle.getBundle("conexao").getObject("SID");
         
         //input = new FileInputStream("connections.properties");
          
         // load a properties file
         //prop.load(input);
      
         // get the property value and print it out
         //System.out.println(prop.getProperty("database"));
         //sUSER = prop.getProperty("User");
         //sPW = prop.getProperty("Pass");
         //connStr = prop.getProperty("ConnectionString");
         
         //input.close();         
      }
      if(sDatabase.equals("DBM")) {
         throw new Exception("Banco de dados desativado. Contacte o administrador do sistema.");
      }
      if(sDatabase.equals("OLAP")) {
         throw new Exception("Banco de dados desativado. Contacte o administrador do sistema.");
      }
      try {
         //DriverManager.registerDriver(new OracleDriver());
//System.out.println("conexaobean: VOU OBTER CONEXAO:" + new java.util.Date() + "." );
         //conn = DriverManager.getConnection(connStr, sUSER, sPW);
//System.out.println("conexaobean: OBTIVE    CONEXAO:" + new java.util.Date() + "." );

         DataSource ds = null;
         Context ctx = null;
         ctx = new InitialContext();
         //ds = (DataSource)ctx.lookup("jdbc/OracleConnASDB");  
          ds = (DataSource)ctx.lookup("java:/OracleConnASDB");  
          
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
//System.out.println("conectado2=" + cbean.getConnection().isClosed());
      if(conn.getAutoCommit())
         conn.setAutoCommit(false);
      //cbean.getConnection().setReadOnly(false);
   }

   public void commit()
          throws SQLException, Exception
   {
      if (conn != null)
         if(isConnected())
            conn.commit();
   }
   public void rollback()
          throws SQLException, Exception
   {
      if (conn != null)
         if(isConnected())
            conn.rollback();
   }

  public boolean isConnected()
         throws SQLException, Exception
  {
        if (conn == null)
           return false;

        boolean Conectado = true;
        try { verifica(); } catch (Exception exverif) { Conectado = false; }
        return Conectado;
        //if (conn != null)
        //{
        //   if (conn.isClosed())
        //      return false;
        //   else
        //      return true;
        //}
        //else
        //   return false;
  }

  public void desconecta()
         throws SQLException, Exception
  {
System.out.print("conexaobean: desconecta sem parametros APP WARNING" );

     try {
           if(conn != null)
           {
             conn.rollback();
//System.out.println("conexaobean: VOU FECHAR CONEXAO:" + new java.util.Date() + "." );
             conn.close();
//System.out.println("conexaobean: FECHEI     CONEXAO:" + new java.util.Date() + "." );
             //if(sDatabase.equals("OLTP"))
             //   ConnectionPool.releaseConnection("bspd07", conn);
             //else
             //   ConnectionPool.releaseConnection("bspd10", conn);
             conn = null;
           }
     }
     catch (Exception ignored) {
        System.out.println("conexaobean: ERRO AO FECHAR CONEXAO:" + new java.util.Date() + "=" + ignored + "." );
     }
  }

  public void valueBound (HttpSessionBindingEvent event) {
//System.out.print("conexaobean: VALUEBOUND!!!");
//System.err.print("conexaobean: VALUEBOUND!!!");
  }

  public synchronized void valueUnbound (HttpSessionBindingEvent event) {
java.util.Date data = new java.util.Date();

//event.getSession()
//System.out.print("conexaobean: VALUEUNBOUND:" + data + " " + data.getTime() + "." );
//System.err.print("conexaobean: VALUEUNBOUND!!!");
     try {
//System.out.print("VALUEUNBOUND!!!");
           if(conn != null)
           {
             conn.rollback();
//System.out.println("conexaobean: VOU FECHAR CONEXAO:" + data + "." );
             conn.close();
//System.out.println("conexaobean: FECHEI     CONEXAO:" + data + "." );

             //if(sDatabase.equals("OLTP"))
             //   ConnectionPool.releaseConnection("bspd07", conn);
             //else
             //   ConnectionPool.releaseConnection("bspd10", conn);
             conn = null;

             try //03122001
             {
                if(iappControlBean != null)
                {
                   //if(event.getSession() == null)
                      iappControlBean.remove(null, this, "");
                   //else
                   //   iappControlBean.remove(event.getSession(), this, ""+event.getSession().getValue("COM_CDG_USUR"));
                }
             }
             catch (Exception appex) { System.out.print("conexaobean: VALUEUNBOUND APP ERROR:" + appex + " " + new java.util.Date() + "." ); }

           }
     }
     catch (Exception ignored) {
        System.out.print("conexaobean: VALUEUNBOUND ERROR:" + ignored + " " + new java.util.Date() + "." );
     }
  }

  public void verifica()
         throws Exception
  {
     Statement stm = null;
     ResultSet rs = null;
     try {
        stm = (Statement)conn.createStatement();
        rs = (ResultSet)stm.executeQuery("SELECT '1' TESTE FROM DUAL");
        rs.next();
        if(! rs.getString("TESTE").equals("1"))
           throw new Exception("Disconnected");
     }
     catch (Exception exv) {
           throw new Exception("Disconnected");
     }
     finally {
        if(rs != null) rs.close();
        if(stm != null) stm.close();
     }
  }

   public synchronized void execute( appControlBean p_appControlBean,
                                     HttpSession session,
                                     HttpServletRequest request,
                                     HttpServletResponse response,
                                     JspWriter out )
          throws SQLException, Exception
   {
      internalexecute( p_appControlBean, session, request, response, out, null );
   }
   public synchronized void execute( appControlBean p_appControlBean,
                                     HttpSession session,
                                     HttpServletRequest request,
                                     HttpServletResponse response,
                                     JspWriter out,
                                     String p_jsp )
          throws SQLException, Exception
   {
      internalexecute( p_appControlBean, session, request, response, out, p_jsp );
   }
   public synchronized void internalexecute( appControlBean p_appControlBean,
                                     HttpSession session,
                                     HttpServletRequest request,
                                     HttpServletResponse response,
                                     JspWriter out,
                                     String p_jsp )
          throws SQLException, Exception
   {
      if(response != null)
      {   //Cookie mycookie = new Cookie("", );
          //response.addCookie(mycookie);
          UtilBean.CriaOuModificaCookie(request, response, "COM_DT_HR_ULT_ACESSO", UtilBean.formataData( new java.util.Date(),"yyyy-MM-dd HH:mm:ss.S"));

      }

      if(request != null)
         addRequest(request, p_jsp);

      if (conn != null)
        if(isConnected())
          return;

      //cbean.setUser("scott");
      //cbean.setPassword("tiger");
      if(sDatabase.equals("OLTP")) {

         //Properties prop = new Properties();
         //InputStream input = null;
          
         //sUSER = "commit9ias";
         //sPW = "commit9ias10g";
         ////connStr = "jdbc:oracle:thin:@1.1.1.197:1521:IASDB1";
         ////connStr = "jdbc:oracle:thin:@192.168.1.11:1521:ASDB";
         ////connStr = "jdbc:oracle:thin:@192.168.3.3:1521:ASDB";
         //connStr = "jdbc:oracle:thin:@localhost:1521:star";
         ////connStr = "jdbc:oracle:thin:@"
         ////         + bundle.getBundle("conexao").getObject("HOST") + ":"
         ////         + bundle.getBundle("conexao").getObject("PORT") + ":"
         ////         + bundle.getBundle("conexao").getObject("SID");
         
         //input = new FileInputStream("connections.properties");
          
         // load a properties file
         //prop.load(input);
         
         // get the property value and print it out
         //System.out.println(prop.getProperty("database"));
         //sUSER = prop.getProperty("User");
         //sPW = prop.getProperty("Pass");
         //connStr = prop.getProperty("ConnectionString");
         
         //input.close();         
        
      }
      if(sDatabase.equals("DBM")) {
         throw new Exception("Banco de dados desativado. Contacte o administrador do sistema.");
      }
      if(sDatabase.equals("OLAP")) {
         throw new Exception("Banco de dados desativado. Contacte o administrador do sistema.");
      }
      try {
         //DriverManager.registerDriver(new OracleDriver());
//System.out.println("conexaobean: VOU OBTER CONEXAO:" + new java.util.Date() + "." );
         //conn = DriverManager.getConnection(connStr, sUSER, sPW);
//System.out.println("conexaobean: internal OBTIVE CONEXAO:" + new java.util.Date() + "." );
          
         DataSource ds = null;
         Context ctx = null;
         ctx = new InitialContext();
          
          if((request.getServerPort() == 8443) || (request.getServerPort() == 443)) 
             //desenvolvimento pelo jdeveloper 
             ds = (DataSource)ctx.lookup("java:/OracleConnASDB");      
          else
             //produção ou dssenvolvimento no wildfly 
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

         try //03122001
         {
            if(session == null)
            {
               iappControlBean = p_appControlBean;
               if(p_jsp != null)
                  iappControlBean.add( null, this, "", p_jsp );
               else
                  iappControlBean.add( null, this, "", UtilBean.getJSP(request) );
            }
            else
            {  iappControlBean = p_appControlBean;
               if(p_jsp != null)
                  iappControlBean.add( session, this, ""+session.getAttribute("COM_CDG_USUR"), p_jsp );
               else
                  iappControlBean.add( session, this, ""+session.getAttribute("COM_CDG_USUR"), UtilBean.getJSP(request) );
            }
         }
         catch (Exception appex) { System.out.print("conexaobean: execute APP ERROR:" + appex + " " + new java.util.Date() + "." ); }

//System.out.println("conexaobean: OBTIVE    CONEXAO:" + new java.util.Date() + "." );
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
//System.out.println("conectado2=" + cbean.getConnection().isClosed());
      if(conn.getAutoCommit())
         conn.setAutoCommit(false);
      //cbean.getConnection().setReadOnly(false);
   }
    public synchronized void executedebug( HttpSession session,
                                      HttpServletRequest request)
           throws SQLException, Exception
    {
       if (conn != null)
         if(isConnected())
           return;

       //cbean.setUser("scott");
       //cbean.setPassword("tiger");
           
       try {
          //DriverManager.registerDriver(new OracleDriver());
    //System.out.println("conexaobean: VOU OBTER CONEXAO:" + new java.util.Date() + "." );
          //conn = DriverManager.getConnection(connStr, sUSER, sPW);
    //System.out.println("conexaobean: internal OBTIVE CONEXAO:" + new java.util.Date() + "." );
           
          DataSource ds = null;
          Context ctx = null;
          ctx = new InitialContext();
           if((request.getServerPort() == 8443) || (request.getServerPort() == 443)) 
              //desenvolvimento pelo jdeveloper 
              ds = (DataSource)ctx.lookup("java:/OracleConnASDB");      
           else
              //produção ou dssenvolvimento no wildfly 
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

    //System.out.println("conexaobean: OBTIVE    CONEXAO:" + new java.util.Date() + "." );
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
          throw new Exception("erro ao conectar executedebug");
       }
    //System.out.println("conectado2=" + cbean.getConnection().isClosed());
       if(conn.getAutoCommit())
          conn.setAutoCommit(false);
       //cbean.getConnection().setReadOnly(false);
    }

  public void desconecta(conexaoBean iconexaobean,
                        JspWriter out, //pode  ser null
                        HttpServletRequest request, //pode  ser null
                        HttpServletResponse response,
                        HttpSession session)
         throws SQLException, Exception
  {
     try {
           if(conn != null)
           {
             conn.rollback();
//System.out.println("conexaobean: VOU FECHAR CONEXAO:" + new java.util.Date() + "." );
             conn.close();
//System.out.println("conexaobean: FECHEI     CONEXAO:" + new java.util.Date() + "." );

             //if(sDatabase.equals("OLTP"))
             //   ConnectionPool.releaseConnection("bspd07", conn);
             //else
             //   ConnectionPool.releaseConnection("bspd10", conn);
             conn = null;

             try //03122001
             {
                if(iappControlBean != null)
                {
                   if(session == null)
                      iappControlBean.remove(null, this, "");
                   else
                      iappControlBean.remove(session, this, ""+session.getAttribute("COM_CDG_USUR"));
                }
             }
             catch (Exception appex) { System.out.print("conexaobean: desconecta APP ERROR:" + appex + " " + new java.util.Date() + "." ); }
           }
     }
     catch (Exception ignored) {
        System.out.println("conexaobean: ERRO AO FECHAR CONEXAO:" + new java.util.Date() + "=" + ignored + "." );
     }
  }

}


