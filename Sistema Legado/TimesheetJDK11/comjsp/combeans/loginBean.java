/**************************************************************************
Empresa: Triscal
Autor: Christiano Chamma
Data: Janeiro de 2001

Descrição:
      Classe de login. Esta classe verifica se o usuário/senha existem
   em uma tabela do banco de dados e seta as variáveis necessárias para
   indicar que foi efetuado o Login.

**************************************************************************/

package combeans;

import java.io.*;
import java.io.PrintWriter;
import java.sql.*;
import oracle.jdbc.*; 
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import java.util.*;
import java.net.*;

//weblogic import oracle.security.sso.enabler.SSOEnabler;
//weblogic import oracle.security.sso.enabler.SSOUserInfo;
//weblogic import oracle.security.sso.enabler.SSOEnablerUtil;
//weblogic import oracle.security.sso.enabler.SSOEnablerException;

public class loginBean
{
  public boolean logado = false;
  String userid = "";
  String password = "";
  //String[] labels = { "Yes", "No" };

  //Set methods
  public String getVersao()
  {   return("02/05/2002");
  }
  //public void setLoginParms(String p_userid, String p_password){
  //   userid = p_userid;
  //   password = p_password;
  //}

  public synchronized void execute(appControlBean icontroleapp,
                      conexaoBean iconexaobean,
                      Connection pconn,
                      JspWriter out,
                      HttpServletRequest request,
                      HttpServletResponse response,
                      HttpSession session)
          throws java.io.IOException, SQLException, Exception
  {
try { UtilBean.geraDebugLog("<BR>Loginbean, execute 01",
             null, session, request, response, null); } catch (Exception ex) { }

    //Statement stm   = null;
    //ResultSet rs    = null;
    //stm = pconn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    //rs =  stm.executeQuery("SELECT to_char(sysdate, 'dd/mm/yyyy hh24:mi:ss') from dual");
    //if(rs.next())
    //  System.out.println(""+rs.getString(1));
    //rs.close();
    //stm.close();

    logado = false;
    String Key = "";
    Statement stmLOGIN   = null;
    ResultSet rsLOGIN    = null;
    session.removeAttribute("com_ultima_pagina_refresh");
    session.removeAttribute("com_ultima_pagina_qs");
    boolean enviouexception = false;
    //weblogic String l_bakedAppCookie = "";
    //weblogic SSOEnablerBean ssoObj = new SSOEnablerBean(session);

    String servidorporta = request.getServerName() + ":" + request.getServerPort();
    //weblogic ssoObj.m_listenerToken    = "commit9ias:" +  servidorporta;
    //weblogic if( (request.getServerPort() == 7070) ||
    //weblogic      (request.getServerPort() == 7072) ||
    //weblogic      (request.getServerPort() == 8988) ||
    //weblogic      (request.getServerPort() == 7101) )
    //weblogic {  ssoObj.m_requestedUrl     = "http://" + servidorporta + "/index.jsp";
    //weblogic    ssoObj.m_onCancelUrl      = "http://" + UtilBean.session_getValue(session,"COM_SN") + "/";
    //weblogic    ssoObj.m_pappCookieDomain = UtilBean.session_getValue(session,"COM_SN") + "";
    //weblogic }
    //weblogic else
    //weblogic {  
	   //weblogic ssoObj.m_requestedUrl     = "http://" + UtilBean.session_getValue(session,"COM_SN") + "/portalcom/index.jsp";//weblogic "/commit9ias/index.jsp";
      //weblogic ssoObj.m_onCancelUrl      = "http://" + UtilBean.session_getValue(session,"COM_SN") + "/";
      //weblogic ssoObj.m_pappCookieDomain = UtilBean.session_getValue(session,"COM_SN") + "";
    //weblogic }

    try
    {  
       userid = request.getRemoteUser();
System.out.println("loginBean.java, userid=" + userid + ", getScheme=" + request.getScheme()); 
	     //if (userid == null) userid = "CCHAMMA";
UtilBean.geraDebugLog("<BR>LoginBean --- userid=" + userid, icontroleapp, session, request, response, out);
UtilBean.geraDebugLog("<BR>LoginBean --- getScheme=" + request.getScheme(), icontroleapp, session, request, response, out);

       stmLOGIN = pconn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
       rsLOGIN = stmLOGIN.executeQuery("SELECT U.SENHA SENHA, /*COM_ACES_DESCRIPT(U.SENHA)*/ U.SENHA SENHA_DES, "
                  + " U.CDG_USUR CDG_USUR, U.STATUS STATUS, "
                  + " U.IN_ALTEROU_SENHA IN_ALTEROU_SENHA "
                  + " FROM COM_ACES_USUARIO U "
                  + " WHERE  UPPER(U.CDG_USUR) = UPPER('" + userid + "') "
                  ); //+ " AND U.SENHA = COM_ACES_CRIPT(UPPER('" + password + "'))");
UtilBean.geraDebugLog("<BR>LoginBean2 --- userid=" + userid, icontroleapp, session, request, response, out);
       if(rsLOGIN.next())
       {
           UtilBean.geraDebugLog("<BR>LoginBean3 --- userid=" + userid, icontroleapp, session, request, response, out);
         if(rsLOGIN.getString("STATUS" ).equals("B"))
         {  logado = false;
            enviouexception = true;
            throw new Exception("<CM>Usuario encontra-se Bloqueado, por favor entre em contato com o administrador do sistema.");
         }
         else if(rsLOGIN.getString("STATUS" ).equals("C"))
         {  logado = false;
            enviouexception = true;
            throw new Exception("<CM>Usuario encontra-se Cancelado, por favor entre em contato com o administrador do sistema.");
         }
         else
         {
           /***ini************** Procedimento de Login generico **************/
           //Bloco de gravação de histórico de Logins:
           int tentativas = 0;
           try {
             String excessao = "";
             while (tentativas < 3)
             {
               try {
                  tentativas++;
                  Random rand = new Random();
                  Key = rand.nextInt()+"";
                  if(Key.length() > 40) Key = Key.substring(0, 40);
                  UtilBean.session_putValue(session, "COM_SESSAOJSP", Key);
                  ///////pconn.rollback(); //Christiano added 16/11/2001 devido a bug PRAGMA AUTONOMOUS_TRANSACTION
                  CallableStatement pstmIns  = null;
                  pstmIns = (CallableStatement)pconn.prepareCall("BEGIN COM_AC_GRAVA_HIST_LOGIN(:1, :2, :3, :4, :5, :6, :7); END;");

                  pstmIns.setString(1, rsLOGIN.getString("CDG_USUR"));
                  pstmIns.setString(2, "I");
                  pstmIns.setString(3, request.getRemoteHost());
                  pstmIns.setString(4, request.getRemoteAddr());
                  pstmIns.setString(5, request.getRemoteUser());
                  pstmIns.setString(6, Key);
                  pstmIns.setString(7, " ");
                  pstmIns.execute();
                  pstmIns.close();
                  tentativas = 999; //para sair do loop caso sucesso
               }
               catch(SQLException sqlexh) {
                  excessao = "loginBean: WARNING ao gravar histórico de Login(2):" + new java.util.Date() + " " + sqlexh + ".";
                  System.out.println("loginBean: WARNING ao gravar histórico de Login(2):" + new java.util.Date() + " " + sqlexh + "."); }
               catch(Exception exh) {
                  excessao = "loginBean: WARNING ao gravar histórico de Login(3):" + new java.util.Date() + " " + exh + ".";
                  System.out.println("loginBean: WARNING ao gravar histórico de Login(3):" + new java.util.Date() + " " + exh + "."); }
             } //fim while
             if(tentativas != 999)
             {   //throw new Exception(excessao);
                 System.out.println(excessao+"");
             }
           }
           //catch(SQLException osqlexh) {
           //   logado = false;
           //   UtilBean.session_putValue(session, "COM_ISLOGGEDIN", "false");
           //   System.out.println("loginBean: Erro ao gravar histórico de Login(1):" + new java.util.Date() + " " + osqlexh + ".");
           //   enviouexception = true;
           //   throw new Exception("<CM>Ocorreu um erro ao realizar o Login, por favor contacte o suporte.");
           //}
           //catch(SQLException sqlexh) {
           //   logado = false;
           //   UtilBean.session_putValue(session, "COM_ISLOGGEDIN", "false");
           //   System.out.println("loginBean: Erro ao gravar histórico de Login(2):" + new java.util.Date() + " " + sqlexh + ".");
           //   enviouexception = true;
           //   throw new Exception("<CM>Ocorreu um erro ao realizar o Login, por favor contacte o suporte.");
           //}
           catch(Exception exh) {
              logado = false;
              UtilBean.session_putValue(session, "COM_ISLOGGEDIN", "false");
              System.out.println("loginBean: Erro ao gravar histórico de Login(3):" + new java.util.Date() + " " + exh + ".");
              enviouexception = true;
              throw new Exception("<CM>Ocorreu um erro ao realizar o Login, por favor contacte o suporte.");
           }

             UtilBean.geraDebugLog("<BR>LoginBean4 --- userid=" + userid, icontroleapp, session, request, response, out);
           if( (request.getServerPort() != 7070) &&
               (request.getServerPort() != 7072) &&
               (request.getServerPort() != 8988) &&
               (request.getServerPort() != 7101) )
           { // Create JSP Partner application cookie and set it
             // ** IMPORTANT ** 
             // Time stamp **must** be added in this cookie and should implement 
             // application cookie time out based on user inactivity etc.
             //weblogic Cookie l_jspAppCookie = new Cookie(ssoObj.m_pappCookieName, l_bakedAppCookie);
             //c l_jspAppCookie.setDomain(ssoObj.m_pappCookieDomain);
             // In-memory cookie for better security 
             //c l_jspAppCookie.setMaxAge(-1); 
             //c l_jspAppCookie.setPath(ssoObj.m_pappCookieScope);
             //c l_jspAppCookie.setComment(ssoObj.m_pappCookieDesc);
             //weblogic response.addCookie(l_jspAppCookie);
           }
             
             boolean existeCookie = false;
             String valorCookie = "";
             Cookie mycookiea[] = request.getCookies(); // = new Cookie("COMLOGGED", "FALSE");
             if(request.getCookies() != null)
             {  for(int i = 0; i < mycookiea.length ; i++)
                {  if (mycookiea[i].getName().equals("COMLOGGED"))
                   {  if(! existeCookie) {
                        existeCookie = true;
                        valorCookie = mycookiea[i].getValue();
             UtilBean.GeraDebugLogSimples("20221122 loginBean XX valorCookie="+valorCookie+".", session, request, true);
                        //System.out.println("getMaxAge="+mycookie[i].getMaxAge());
                        //System.out.println("getVersion="+mycookie[i].getVersion());
                        //System.out.println("getComment="+mycookie[i].getComment());
                        //System.out.println("getSecure="+mycookie[i].getSecure());
                      }
                   }
                }
             }
             UtilBean.CriaOuModificaCookie(request, response, "COMLOGGED", "TRUE");
             UtilBean.CriaOuModificaCookie(request, response, "COM_ULTIMA_PAGINA_REFRESH", "NULO");
             UtilBean.CriaOuModificaCookie(request, response, "COM_DT_HR_ULT_ACESSO", UtilBean.formataData( new java.util.Date(),"yyyy-MM-dd HH:mm:ss.S"));
             UtilBean.CriaOuModificaCookie(request, response, "COM_SESSAOJSP", Key);

UtilBean.GeraDebugLogSimples("20221122 loginbean COM_ISLOGGEDIN está sendo setado para true.", session, request, true);

           UtilBean.session_putValue(session, "COM_ISLOGGEDIN", "true");
             
             
           UtilBean.session_putValue(session, "COM_ULTIMA_PAGINA", "");
           UtilBean.session_putValue(session, "com_ultima_pagina_refresh", "");
           UtilBean.session_putValue(session, "com_ultima_pagina_qs", "");

           UtilBean.session_putValue(session, "COM_CDG_USUR", rsLOGIN.getString("CDG_USUR"));

           // para recarregar o menu:
           UtilBean.session_putValue(session, "COM_MENUBUILT", "");

           logado = true;

             UtilBean.geraDebugLog("<BR>LoginBean5 --- userid=" + userid, icontroleapp, session, request, response, out);
           if(! UtilBean.setDominio(icontroleapp, iconexaobean, session, request, response, out))
           { 
               UtilBean.geraDebugLog("<BR>LoginBean6 --- userid=" + userid, icontroleapp, session, request, response, out);
               logado = false;
             // print header with meta refresh tag to login.jsp
             out.println("<html><head>");
             out.println("<link REL=\"STYLESHEET\" TYPE=\"text/css\" HREF=\"" + UtilBean.session_getValue(session, "COM_CSS") + "\">");
             out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"0; URL=" + session.getAttribute("COM_RN") + "jsp/index.jsp"
             + "?msg_erro=" + /*URLEncoder.encode(*/"Ocorreu um erro ao efetuar o login, por favor tente de novo"/*)*/ + "\"> ");
             out.println("<title></title></head><body class=\"comBodyLogin\" topmargin=\"0\" leftmargin=\"0\">");
             out.println("<br><center><h2>Ocorreu um erro ao efetuar o login, por favor tente de novo.");
             out.println("<br>");
             out.println("</h2></center>");
             out.println("</body></html>");
           }
             UtilBean.geraDebugLog("<BR>LoginBean7 --- userid=" + userid, icontroleapp, session, request, response, out);
           menuBean2 menubean = new menuBean2();
           menubean.verificaMenuCarregado(iconexaobean, session, request, response, out, true);
           session.setAttribute("menubean", menubean);

           //if(rsLOGIN.getString("IN_ALTEROU_SENHA" ).equals("N"))
           //{  session.setAttribute("com_ultima_pagina_refresh", "com_AltSenha.jsp?login=true");
           //   // print header with meta refresh tag to main.jsp
           //   out.println("<html><head>");
           //   out.println("<link REL=\"STYLESHEET\" TYPE=\"text/css\" HREF=\"" + UtilBean.session_getValue(session, "COM_CSS") + "\">");
           //   out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"1; URL=" + session.getAttribute("COM_RN") + "" + UtilBean.montaURL(session, "com_AltSenha.jsp", "login=true") + "\"> ");
           //   out.println("<title></title></head><body class=\"comBodyLogin\" topmargin=\"0\" leftmargin=\"0\">");
           //   out.println("<br><center><h2>Efetuando Login......</h2></center>");
           //   out.println("</body></html>");
           //}
           //else
           //{  // print header with meta refresh tag to index.jsp
              out.println("<html><head>");
              out.println("<link REL=\"STYLESHEET\" TYPE=\"text/css\" HREF=\"" + UtilBean.session_getValue(session, "COM_CSS") + "\">");
              //out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"0; URL=" + session.getAttribute("COM_RN") + "" + UtilBean.montaURL(session, "index.jsp", "") + "\"> ");
              out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"0; URL=" + session.getAttribute("COM_RN") + "jsp/" + UtilBean.montaURL(session, "TelaInicial.jsp", "") + "\"> ");
              out.println("<title></title></head><body class=\"comBodyLogin\" topmargin=\"0\" leftmargin=\"0\">");
              //out.println("<br><center><h2>Efetuando Login......</h2></center>");
              out.println("</body></html>");
           //}
           /***fim************** Procedimento de Login ***********************/

         } // fim do else do if(rsLOGIN.getString("STATUS")
       }
       else //else do if(rsLOGIN.next())
       {
           UtilBean.geraDebugLog("<BR>LoginBean8e --- userid=" + userid, icontroleapp, session, request, response, out);
         logado = false;
         // print header with meta refresh tag to login.jsp

         out.println("<html><head>");
         out.println("<link REL=\"STYLESHEET\" TYPE=\"text/css\" HREF=\"" + UtilBean.session_getValue(session, "COM_CSS") + "\">");
         //out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"0; URL=" + session.getAttribute("COM_RN") + "index.jsp"
         //+ "?msg_erro=" + URLEncoder.encode("Identificação inválida, por favor tente de novo") + "\"> ");
         out.println("<title></title></head><body class=\"comBodyLogin\" topmargin=\"0\" leftmargin=\"0\">");
         out.println("<br><center><h2>Identificação inválida, por favor tente de novo.");
         out.println("<br>");
         out.println("</h2></center>");
         out.println("</body></html>");
       }
     }
	   catch(Exception ex)
     {   logado = false;
         if(enviouexception)
            throw ex;

         // print header with meta refresh tag to login.jsp
         out.println("<html><head>");
         out.println("<link REL=\"STYLESHEET\" TYPE=\"text/css\" HREF=\"" + UtilBean.session_getValue(session, "COM_CSS") + "\">");
         //out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"0; URL=" + session.getAttribute("COM_RN") + "index.jsp?msg_erro=Login%20invalido,%20por%20favor%20tente%20de%20novo\"> ");
         out.println("<title></title></head><body class=\"comBodyLogin\" topmargin=\"0\" leftmargin=\"0\">");
         out.println("<br><center><h2>Login invalido, por favor tente de novo.");
         out.println("<br>");
         if(ex.getMessage() != null)
             out.println(ex.getMessage());

         out.println("</h2></center>");
         out.println("</body></html>");
      }// catch
      finally
      {  if (rsLOGIN != null)    rsLOGIN.close();
         if (stmLOGIN != null)   stmLOGIN.close();
      }
  } // execute

  public synchronized void logout(Connection pconn, JspWriter out,
                      HttpServletRequest request,
                      HttpServletResponse response,
                      HttpSession session)
          throws java.io.IOException, SQLException, Exception
  {
      //weblogic SSOEnablerBean ssoObj = new SSOEnablerBean(session);
      //weblogic Cookie l_JspAppCookie = new Cookie(ssoObj.m_pappCookieName, "End application session");
      //weblogic l_JspAppCookie.setMaxAge(0); 
      //weblogic response.addCookie(l_JspAppCookie);
UtilBean.GeraDebugLogSimples("20221122 LoginBean setando cookie false.", session, request, true);

      UtilBean.RemoveCookie(request, response, "COMLOGGED");
      UtilBean.RemoveCookie(request, response, "COM_ULTIMA_PAGINA_REFRESH");
      UtilBean.RemoveCookie(request, response, "COM_DT_HR_ULT_ACESSO");
      UtilBean.RemoveCookie(request, response, "COM_SESSAOJSP");

      //Cookie mycookie = new Cookie("COMLOGGED", "FALSE");
      //mycookie.setMaxAge(0); 
      //response.addCookie(mycookie);

      //Cookie mycookie2 = new Cookie("COM_ULTIMA_PAGINA_REFRESH", "NULO");
      //mycookie2.setMaxAge(0); 
      //response.addCookie(mycookie2);

      //Cookie mycookie3 = new Cookie("COM_DT_HR_ULT_ACESSO", "NULO");
      //mycookie3.setMaxAge(0); 
      //response.addCookie(mycookie3);

      //Cookie mycookie4 = new Cookie("COM_SESSAOJSP", "NULO");
      //mycookie4.setMaxAge(0); 
      //response.addCookie(mycookie4);

      String usuario = "";
      try {
         if(pconn != null)
           pconn.rollback(); //Christiano added 16/11/2001 devido a bug PRAGMA AUTONOMOUS_TRANSACTION
         if(UtilBean.session_getValue(session, "COM_CDG_USUR") != null)
         {
            usuario = ""+UtilBean.session_getValue(session, "COM_CDG_USUR");
            if(pconn != null) {
              CallableStatement pstmIns  = null;
              pstmIns = (CallableStatement)pconn.prepareCall("BEGIN COM_AC_GRAVA_HIST_LOGIN(:1, :2, :3, :4, :5, :6, :7); END;");
              pstmIns.setString(1, usuario);
              pstmIns.setString(2, "O");
              pstmIns.setString(3, request.getRemoteHost());
              pstmIns.setString(4, request.getRemoteAddr());
              pstmIns.setString(5, request.getRemoteUser());
              pstmIns.setString(6, ""+UtilBean.session_getValue(session, "COM_SESSAOJSP"));
              pstmIns.setString(7, " ");

              pstmIns.execute();
              pstmIns.close();
            }
         }
      }
      catch(SQLException osqlexh) { System.out.println("loginBean: Erro ao gravar histórico de Logout(1):" + new java.util.Date() + " " + osqlexh + "."); }
      catch(Exception exh) { System.out.println("loginBean: Erro ao gravar histórico de Logout(3):" + new java.util.Date() + " " + exh + "."); }
  }
  public synchronized void logout(HttpServletRequest request, HttpServletResponse response, HttpSession session)
          throws java.io.IOException, SQLException, Exception
  {
      //weblogic SSOEnablerBean ssoObj = new SSOEnablerBean(session);
      //weblogic Cookie l_JspAppCookie = new Cookie(ssoObj.m_pappCookieName, "End application session");
      //weblogic l_JspAppCookie.setMaxAge(0); 
      //weblogic response.addCookie(l_JspAppCookie);

UtilBean.GeraDebugLogSimples("20221122 LoginBean setando cookie false 02.", session, request, true);
      UtilBean.RemoveCookie(request, response, "COMLOGGED");
      UtilBean.RemoveCookie(request, response, "COM_ULTIMA_PAGINA_REFRESH");
      UtilBean.RemoveCookie(request, response, "COM_DT_HR_ULT_ACESSO");
      UtilBean.RemoveCookie(request, response, "COM_SESSAOJSP");

      //Cookie mycookie = new Cookie("COMLOGGED", "FALSE");
      //mycookie.setMaxAge(0); 
      //response.addCookie(mycookie);

      //Cookie mycookie2 = new Cookie("COM_ULTIMA_PAGINA_REFRESH", "NULO");
      //mycookie2.setMaxAge(0); 
      //response.addCookie(mycookie2);

      //Cookie mycookie3 = new Cookie("COM_DT_HR_ULT_ACESSO", "NULO");
      //mycookie3.setMaxAge(0); 
      //response.addCookie(mycookie3);

      //Cookie mycookie4 = new Cookie("COM_SESSAOJSP", "NULO");
      //mycookie4.setMaxAge(0); 
      //response.addCookie(mycookie4);

      String usuario = "";
      try {
         if(UtilBean.session_getValue(session, "COM_CDG_USUR") != null)
         {
            usuario = ""+UtilBean.session_getValue(session, "COM_CDG_USUR");
         }
      }
      catch(Exception exh) { System.out.println("loginBean: Erro ao gravar histórico de Logout(3):" + new java.util.Date() + " " + exh + "."); }
  }
 
}
