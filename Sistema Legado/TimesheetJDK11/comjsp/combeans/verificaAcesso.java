/**************************************************************************
Empresa: Triscal
Autor: Christiano Chamma
Data: Janeiro de 2001

Descrição:
      Classe de verificação de acesso do usuário à página. Verifica também se
    o usuário está logado e executa o rastreamento da página que está sendo
    aberta.

-----------------------------------------------------------------
Alterações:

   Empresa: Triscal
   Autor: Júnior
   Data: Abril de 2001
   Descrição: Inclusão do método getJsp para pegar o nome da página corrente.
-----------------------------------------------------------------
**************************************************************************/

package combeans;

import java.io.*;
import java.text.*;
import java.lang.*;
import java.util.*;
import java.io.PrintWriter;
//2106 import oracle.jdeveloper.html.*;
import java.sql.*;
//import oracle.jsp.dbutil.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import oracle.jdbc.*; 

public class verificaAcesso {

  HttpSession sessao;
  String TELA_NM_TELA  = "";
  String USUR_NM_LOGIN = "";
  String USUR_IN_ADMIN = "";
  String TIPO_ACESSO = "";
  String sReferer = "";
  boolean verificaAcessoOK = false;

  //Set methods
  public String getVersao()
  {   return("02/05/2002");
  }
  public void setTelaParms(String p_tela){
     TELA_NM_TELA  = p_tela;
     TIPO_ACESSO = "";
  }
  //Get methods
  public boolean getverificaAcessoOK() {
     return verificaAcessoOK;
  }
  public String getReferer() {
     /**********
     String HTTP_REFERER = "";
     if (request.getHeader("Referer") != null)
     {  HTTP_REFERER = request.getHeader("Referer");
     }

     if(HTTP_REFERER.indexOf("?") >= 0)
        HTTP_REFERER = HTTP_REFERER.substring(0, HTTP_REFERER.indexOf("?"));

     String str="";
     String buf=HTTP_REFERER;
     for (int x = (buf.length()-1) ; x>0; x--){
       if (buf.charAt(x) != '/'){
         str = buf.charAt(x)+str;
       }else{
         break;
       }
     }
     HTTP_REFERER = str;

     return HTTP_REFERER;
     ************/
     return sReferer;
  }

  public boolean verificaReferer( String pReferer, HttpServletRequest request,
                                  HttpServletResponse response,
                                  HttpSession session)
         throws java.io.IOException, Exception
  {
     if(pReferer == null)
     {  sReferer = "";
        return true;
     }
     if(pReferer.equals(""))
     {  sReferer = "";
        return true;
     }
     String HTTP_REFERER = "";
     String HTTP_SERVERNAME = "";
     if (request.getHeader("Referer") != null)
     {  HTTP_REFERER = request.getHeader("Referer");
        HTTP_SERVERNAME = request.getHeader("Referer");
     }

     if(HTTP_REFERER.indexOf("?") >= 0)
        HTTP_REFERER = HTTP_REFERER.substring(0, HTTP_REFERER.indexOf("?"));

     String str="";
     String buf=HTTP_REFERER;
     for (int x = (buf.length()-1) ; x>0; x--){
       if (buf.charAt(x) != '/'){
         str = buf.charAt(x)+str;
       }else{
         break;
       }
     }
     HTTP_REFERER = str;

     //pegando o servername daonde veio:
     String str2="";
     String buf2=HTTP_SERVERNAME;
     int achouBarra = 0;
     for (int x = 0; x<buf2.length(); x++){
       if (buf2.charAt(x) == '/')
       { achouBarra++;
       }
       else
       { if (buf2.charAt(x) == ':')
         {  if(achouBarra==2)
               break;
         }
         else
         {  if(achouBarra==2)
               str2 += buf2.charAt(x);
            if(achouBarra==3)
               break;
         }
       }
     }
     HTTP_SERVERNAME = str2;

     //inicio 21/12/2001
     String COM_SESSAOJSP = "";
     String COM_DT_HR_ULT_ACESSO = "";
     String COMLOGGED = "";
     String COM_ULTIMA_PAGINA_REFRESH = "";
     String COM_SN = "";
     String COM_RN = "";
     if(session.getAttribute("com_ultima_pagina_refresh") == null)
     {
        Cookie mycookie[] = request.getCookies(); // = new Cookie("COMLOGGED", "FALSE");
        if(request.getCookies() != null)
        {  for(int i = 0; i < mycookie.length ; i++)
           {  if (mycookie[i].getName().equals("COMLOGGED"))
              {  if(COMLOGGED.equals(""))
                   COMLOGGED = mycookie[i].getValue();
              }
              if (mycookie[i].getName().equals("COM_SESSAOJSP"))
              {  if(COM_SESSAOJSP.equals(""))
                   COM_SESSAOJSP = mycookie[i].getValue();
              }
              if (mycookie[i].getName().equals("COM_DT_HR_ULT_ACESSO"))
              {  if(COM_DT_HR_ULT_ACESSO.equals(""))
                   COM_DT_HR_ULT_ACESSO = mycookie[i].getValue();
              }
              if (mycookie[i].getName().equals("COM_ULTIMA_PAGINA_REFRESH"))
              {  if(COM_ULTIMA_PAGINA_REFRESH.equals(""))
                   COM_ULTIMA_PAGINA_REFRESH = mycookie[i].getValue();
              }
              if (mycookie[i].getName().equals("COM_SN"))
              {  if(COM_SN.equals(""))
                   COM_SN = mycookie[i].getValue();
              }
              if (mycookie[i].getName().equals("COM_RN"))
              {  if(COM_RN.equals(""))
                   COM_RN = mycookie[i].getValue();
              }
           }
        }
        if(COM_SESSAOJSP.equals("NULO")) COM_SESSAOJSP = "";
        if(COM_DT_HR_ULT_ACESSO.equals("NULO")) COM_DT_HR_ULT_ACESSO = "";
        if(COMLOGGED.equals("NULO")) COMLOGGED = "";
        if(COM_ULTIMA_PAGINA_REFRESH.equals("NULO")) COM_ULTIMA_PAGINA_REFRESH = "";
        if(COM_SN.equals("NULO")) COM_SN = "";
        if(COM_RN.equals("NULO")) COM_RN = "";

        session.setAttribute("com_ultima_pagina_refresh", COM_ULTIMA_PAGINA_REFRESH);
        session.setAttribute("COM_MENU_PAGINA_ATUAL", getJSP(request));
        if( (session.getAttribute("COM_SN") == null) || (session.getAttribute("COM_RN") == null))
        {  session.setAttribute("COM_SN", COM_SN);
           session.setAttribute("COM_RN", COM_RN);
        }
     }
     //fim 21/12/2001

     //pegando o servername daonde estou:
     String HTTP_SERVERNAME_ATUAL = "";
     //IAS if(request.getServerName() != null)
     //IAS   HTTP_SERVERNAME_ATUAL = request.getServerName();
     if(session.getAttribute("COM_SN") != null)
        HTTP_SERVERNAME_ATUAL = ""+session.getAttribute("COM_SN"); //IAS

     if( (! HTTP_SERVERNAME_ATUAL.equals("")) &&
         (! HTTP_SERVERNAME.equals("")))
     {  if(! HTTP_SERVERNAME.equalsIgnoreCase(HTTP_SERVERNAME_ATUAL))
        {  UtilBean.session_putValue(session, "COM_MENU_PAGINA_ATUAL", getJSP(request));
           response.sendRedirect(session.getAttribute("COM_RN") + "jsp/com_referererror.jsp");
           return false;
        }
     }
//String nada1 = session.getAttribute("com_ultima_pagina_refresh").toString();
//String nada2 = getJSP(request);

     boolean verifica_na_sessao = true;
     if( (pReferer.startsWith("lov")) || (pReferer.startsWith("com_Popup")) ||
         (pReferer.equals(""))        || (pReferer.toLowerCase().indexOf("_submit") >= 0)
         || (getJSP(request).toLowerCase().indexOf("_submit") < 0) )
     {  verifica_na_sessao = false;
     }
     else
     {  if(getJSP(request).toLowerCase().indexOf("_submitpesquisa") >= 0)
           verifica_na_sessao = false;
     }

     if (! HTTP_REFERER.equalsIgnoreCase(pReferer))
     {  if(  (session.getAttribute("com_ultima_pagina_refresh") != null) &&
             (session.getAttribute("com_ultima_pagina_refresh").equals(getJSP(request))))
           sReferer = pReferer;
        else
        {  UtilBean.session_putValue(session, "COM_MENU_PAGINA_ATUAL", getJSP(request));
           response.sendRedirect(session.getAttribute("COM_RN") + "jsp/com_referererror.jsp");
           return false;
        }
     }
     else
     {
        if(verifica_na_sessao)
           if(! session.getAttribute("com_ultima_pagina_refresh").toString().equals(pReferer) )
           {  if( ! session.getAttribute("com_ultima_pagina_refresh").toString().equals(getJSP(request))  )
              {  UtilBean.session_putValue(session, "COM_MENU_PAGINA_ATUAL", getJSP(request));
                 response.sendRedirect(session.getAttribute("COM_RN") + "jsp/com_referererror.jsp");
                 return false;
              }
              else
                 sReferer = pReferer;
           }
           else
              sReferer = pReferer;
        else
           sReferer = pReferer;
     }
     return true;
  }
  public boolean verificaReferer( String pReferer1, String pReferer2,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  HttpSession session )
         throws java.io.IOException, Exception
  {
//System.out.println("Entrei, " + pReferer1 + ", " + pReferer2);
     if((pReferer1 == null) || (pReferer2 == null))
     {  sReferer = "";
        return true;
     }
     if((pReferer1.equals("")) || (pReferer2.equals("")))
     {  sReferer = "";
        return true;
     }
     String HTTP_REFERER = "";
     String HTTP_SERVERNAME = "";
     if (request.getHeader("Referer") != null)
     {  HTTP_REFERER = request.getHeader("Referer");
        HTTP_SERVERNAME = request.getHeader("Referer");
     }

     if(HTTP_REFERER.indexOf("?") >= 0)
        HTTP_REFERER = HTTP_REFERER.substring(0, HTTP_REFERER.indexOf("?"));

     String str="";
     String buf=HTTP_REFERER;
     for (int x = (buf.length()-1) ; x>0; x--){
       if (buf.charAt(x) != '/'){
         str = buf.charAt(x)+str;
       }else{
         break;
       }
     }
     HTTP_REFERER = str;
//System.out.println("HTTP_REFERER, " + HTTP_REFERER);

     //pegando o servername daonde veio:
     String str2="";
     String buf2=HTTP_SERVERNAME;
     int achouBarra = 0;
     for (int x = 0; x<buf2.length(); x++){
       if (buf2.charAt(x) == '/')
       { achouBarra++;
       }
       else
       { if (buf2.charAt(x) == ':')
         {  if(achouBarra==2)
               break;
         }
         else
         {  if(achouBarra==2)
               str2 += buf2.charAt(x);
            if(achouBarra==3)
               break;
         }
       }
     }
     HTTP_SERVERNAME = str2;

     //inicio 21/12/2001
     String COM_SESSAOJSP = "";
     String COM_DT_HR_ULT_ACESSO = "";
     String COMLOGGED = "";
     String COM_ULTIMA_PAGINA_REFRESH = "";
     String COM_SN = "";
     String COM_RN = "";
     if(session.getAttribute("com_ultima_pagina_refresh") == null)
     {
        Cookie mycookie[] = request.getCookies(); // = new Cookie("COMLOGGED", "FALSE");
        if(request.getCookies() != null)
        {  for(int i = 0; i < mycookie.length ; i++)
           {  if (mycookie[i].getName().equals("COMLOGGED"))
              {  if(COMLOGGED.equals(""))
                   COMLOGGED = mycookie[i].getValue();
              }
              if (mycookie[i].getName().equals("COM_SESSAOJSP"))
              {  if(COM_SESSAOJSP.equals(""))
                   COM_SESSAOJSP = mycookie[i].getValue();
              }
              if (mycookie[i].getName().equals("COM_DT_HR_ULT_ACESSO"))
              {  if(COM_DT_HR_ULT_ACESSO.equals(""))
                   COM_DT_HR_ULT_ACESSO = mycookie[i].getValue();
              }
              if (mycookie[i].getName().equals("COM_ULTIMA_PAGINA_REFRESH"))
              {  if(COM_ULTIMA_PAGINA_REFRESH.equals(""))
                   COM_ULTIMA_PAGINA_REFRESH = mycookie[i].getValue();
              }
              if (mycookie[i].getName().equals("COM_SN"))
              {  if(COM_SN.equals(""))
                   COM_SN = mycookie[i].getValue();
              }
              if (mycookie[i].getName().equals("COM_RN"))
              {  if(COM_RN.equals(""))
                   COM_RN = mycookie[i].getValue();
              }
           }
        }
        if(COM_SESSAOJSP.equals("NULO")) COM_SESSAOJSP = "";
        if(COM_DT_HR_ULT_ACESSO.equals("NULO")) COM_DT_HR_ULT_ACESSO = "";
        if(COMLOGGED.equals("NULO")) COMLOGGED = "";
        if(COM_ULTIMA_PAGINA_REFRESH.equals("NULO")) COM_ULTIMA_PAGINA_REFRESH = "";
        if(COM_SN.equals("NULO")) COM_SN = "";
        if(COM_RN.equals("NULO")) COM_RN = "";

        session.setAttribute("com_ultima_pagina_refresh", COM_ULTIMA_PAGINA_REFRESH);
        session.setAttribute("COM_MENU_PAGINA_ATUAL", getJSP(request));
        if( (session.getAttribute("COM_SN") == null) || (session.getAttribute("COM_RN") == null))
        {  session.setAttribute("COM_SN", COM_SN);
           session.setAttribute("COM_RN", COM_RN);
        }
     }
     //fim 21/12/2001

     //pegando o servername daonde estou:
     String HTTP_SERVERNAME_ATUAL = "";
     //IAS if(request.getServerName() != null)
     //IAS   HTTP_SERVERNAME_ATUAL = request.getServerName();
     if(session.getAttribute("COM_SN") != null)
        HTTP_SERVERNAME_ATUAL = ""+session.getAttribute("COM_SN"); //IAS

     if( (! HTTP_SERVERNAME_ATUAL.equals("")) &&
         (! HTTP_SERVERNAME.equals("")))
     {  if(! HTTP_SERVERNAME.equalsIgnoreCase(HTTP_SERVERNAME_ATUAL))
        {  UtilBean.session_putValue(session, "COM_MENU_PAGINA_ATUAL", getJSP(request));
           response.sendRedirect(session.getAttribute("COM_RN") + "jsp/com_referererror.jsp");
           return false;
        }
     }

//String nada1 = session.getAttribute("com_ultima_pagina_refresh").toString();
//String nada2 = getJSP(request);

     boolean verifica_na_sessao = true;
     if( (pReferer1.startsWith("lov")) || (pReferer1.startsWith("com_Popup")) ||
         (pReferer1.equals(""))        || (pReferer1.toLowerCase().indexOf("_submit") >= 0)
         || (getJSP(request).toLowerCase().indexOf("_submit") < 0) )
     {  verifica_na_sessao = false;
     }
     else
     {  if(getJSP(request).toLowerCase().indexOf("_submitpesquisa") >= 0)
           verifica_na_sessao = false;
     }
     if(verifica_na_sessao)
     {
       if( (pReferer2.startsWith("lov")) || (pReferer2.startsWith("com_Popup")) ||
              (pReferer2.equals(""))        || (pReferer2.toLowerCase().indexOf("_submit") >= 0) )
       {  verifica_na_sessao = false;
       }
     }

     if ((! HTTP_REFERER.equalsIgnoreCase(pReferer1)) &&
         (! HTTP_REFERER.equalsIgnoreCase(pReferer2)))
     {
//System.out.println("HTTP_REFERER diferente");
        if(session.getAttribute("com_ultima_pagina_refresh").equals(getJSP(request)))
           sReferer = HTTP_REFERER;
        else
        {  UtilBean.session_putValue(session, "COM_MENU_PAGINA_ATUAL", getJSP(request));
           response.sendRedirect(session.getAttribute("COM_RN") + "jsp/com_referererror.jsp");
           return false;
        }
     }
     else
     {  if(verifica_na_sessao)
           if( (! session.getAttribute("com_ultima_pagina_refresh").toString().equals(pReferer1)) &&
               (! session.getAttribute("com_ultima_pagina_refresh").toString().equals(pReferer2)) )
           {  if( ! session.getAttribute("com_ultima_pagina_refresh").toString().equals(getJSP(request))  )
              {  UtilBean.session_putValue(session, "COM_MENU_PAGINA_ATUAL", getJSP(request));
                 response.sendRedirect(session.getAttribute("COM_RN") + "jsp/com_referererror.jsp");
                 return false;
              }
              else
                 sReferer = HTTP_REFERER;
           }
           else
              sReferer = HTTP_REFERER;
        else
           sReferer = HTTP_REFERER;
     }
     return true;
  }
  public boolean verificaReferer( String pReferer1, String pReferer2,
                                  String pReferer3,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  HttpSession session )
         throws java.io.IOException, Exception
  {
//System.out.println("Entrei, " + pReferer1 + ", " + pReferer2);
     if((pReferer1 == null) || (pReferer2 == null) || (pReferer3 == null))
     {  sReferer = "";
        return true;
     }
     if((pReferer1.equals("")) || (pReferer2.equals("")) || (pReferer3.equals("")))
     {  sReferer = "";
        return true;
     }
     String HTTP_REFERER = "";
     String HTTP_SERVERNAME = "";
     if (request.getHeader("Referer") != null)
     {  HTTP_REFERER = request.getHeader("Referer");
        HTTP_SERVERNAME = request.getHeader("Referer");
     }

     if(HTTP_REFERER.indexOf("?") >= 0)
        HTTP_REFERER = HTTP_REFERER.substring(0, HTTP_REFERER.indexOf("?"));

     String str="";
     String buf=HTTP_REFERER;
     for (int x = (buf.length()-1) ; x>0; x--){
       if (buf.charAt(x) != '/'){
         str = buf.charAt(x)+str;
       }else{
         break;
       }
     }
     HTTP_REFERER = str;
//System.out.println("HTTP_REFERER, " + HTTP_REFERER);

     //pegando o servername daonde veio:
     String str2="";
     String buf2=HTTP_SERVERNAME;
     int achouBarra = 0;
     for (int x = 0; x<buf2.length(); x++){
       if (buf2.charAt(x) == '/')
       { achouBarra++;
       }
       else
       { if (buf2.charAt(x) == ':')
         {  if(achouBarra==2)
               break;
         }
         else
         {  if(achouBarra==2)
               str2 += buf2.charAt(x);
            if(achouBarra==3)
               break;
         }
       }
     }
     HTTP_SERVERNAME = str2;

     //inicio 21/12/2001
     String COM_SESSAOJSP = "";
     String COM_DT_HR_ULT_ACESSO = "";
     String COMLOGGED = "";
     String COM_ULTIMA_PAGINA_REFRESH = "";
     String COM_SN = "";
     String COM_RN = "";
     if(session.getAttribute("com_ultima_pagina_refresh") == null)
     {
        Cookie mycookie[] = request.getCookies(); // = new Cookie("COMLOGGED", "FALSE");
        if(request.getCookies() != null)
        {  for(int i = 0; i < mycookie.length ; i++)
           {  if (mycookie[i].getName().equals("COMLOGGED"))
              {  if(COMLOGGED.equals(""))
                   COMLOGGED = mycookie[i].getValue();
              }
              if (mycookie[i].getName().equals("COM_SESSAOJSP"))
              {  if(COM_SESSAOJSP.equals(""))
                   COM_SESSAOJSP = mycookie[i].getValue();
              }
              if (mycookie[i].getName().equals("COM_DT_HR_ULT_ACESSO"))
              {  if(COM_DT_HR_ULT_ACESSO.equals(""))
                   COM_DT_HR_ULT_ACESSO = mycookie[i].getValue();
              }
              if (mycookie[i].getName().equals("COM_ULTIMA_PAGINA_REFRESH"))
              {  if(COM_ULTIMA_PAGINA_REFRESH.equals(""))
                   COM_ULTIMA_PAGINA_REFRESH = mycookie[i].getValue();
              }
              if (mycookie[i].getName().equals("COM_SN"))
              {  if(COM_SN.equals(""))
                   COM_SN = mycookie[i].getValue();
              }
              if (mycookie[i].getName().equals("COM_RN"))
              {  if(COM_RN.equals(""))
                   COM_RN = mycookie[i].getValue();
              }
           }
        }
        if(COM_SESSAOJSP.equals("NULO")) COM_SESSAOJSP = "";
        if(COM_DT_HR_ULT_ACESSO.equals("NULO")) COM_DT_HR_ULT_ACESSO = "";
        if(COMLOGGED.equals("NULO")) COMLOGGED = "";
        if(COM_ULTIMA_PAGINA_REFRESH.equals("NULO")) COM_ULTIMA_PAGINA_REFRESH = "";
        if(COM_SN.equals("NULO")) COM_SN = "";
        if(COM_RN.equals("NULO")) COM_RN = "";

        session.setAttribute("com_ultima_pagina_refresh", COM_ULTIMA_PAGINA_REFRESH);
        session.setAttribute("COM_MENU_PAGINA_ATUAL", getJSP(request));
        if( (session.getAttribute("COM_SN") == null) || (session.getAttribute("COM_RN") == null))
        {  session.setAttribute("COM_SN", COM_SN);
           session.setAttribute("COM_RN", COM_RN);
        }
     }
     //fim 21/12/2001

     //pegando o servername daonde estou:
     String HTTP_SERVERNAME_ATUAL = "";
     //IAS if(request.getServerName() != null)
     //IAS    HTTP_SERVERNAME_ATUAL = request.getServerName();
     if(session.getAttribute("COM_SN") != null)
        HTTP_SERVERNAME_ATUAL = ""+session.getAttribute("COM_SN"); //IAS

     if( (! HTTP_SERVERNAME_ATUAL.equals("")) &&
         (! HTTP_SERVERNAME.equals("")))
     {  if(! HTTP_SERVERNAME.equalsIgnoreCase(HTTP_SERVERNAME_ATUAL))
        {  UtilBean.session_putValue(session, "COM_MENU_PAGINA_ATUAL", getJSP(request));
           response.sendRedirect(session.getAttribute("COM_RN") + "jsp/com_referererror.jsp");
           return false;
        }
     }

//String nada1 = session.getAttribute("com_ultima_pagina_refresh").toString();
//String nada2 = getJSP(request);

     boolean verifica_na_sessao = true;
     if( (pReferer1.startsWith("lov")) || (pReferer1.startsWith("com_Popup")) ||
         (pReferer1.equals(""))        || (pReferer1.toLowerCase().indexOf("_submit") >= 0)
         || (getJSP(request).toLowerCase().indexOf("_submit") < 0) )
     {  verifica_na_sessao = false;
     }
     else
     {  if(getJSP(request).toLowerCase().indexOf("_submitpesquisa") >= 0)
           verifica_na_sessao = false;
     }
     if(verifica_na_sessao)
     {
       if( (pReferer2.startsWith("lov")) || (pReferer2.startsWith("com_Popup")) ||
              (pReferer2.equals(""))        || (pReferer2.toLowerCase().indexOf("_submit") >= 0) )
       {  verifica_na_sessao = false;
       }
     }
     if(verifica_na_sessao)
     {
       if( (pReferer3.startsWith("lov")) || (pReferer3.startsWith("com_Popup")) ||
              (pReferer3.equals(""))        || (pReferer3.toLowerCase().indexOf("_submit") >= 0) )
       {  verifica_na_sessao = false;
       }
     }
     if ((! HTTP_REFERER.equalsIgnoreCase(pReferer1)) &&
         (! HTTP_REFERER.equalsIgnoreCase(pReferer2)) &&
         (! HTTP_REFERER.equalsIgnoreCase(pReferer3)) )
     {
//System.out.println("HTTP_REFERER diferente");
        if(session.getAttribute("com_ultima_pagina_refresh").equals(getJSP(request)))
           sReferer = HTTP_REFERER;
        else
        {  UtilBean.session_putValue(session, "COM_MENU_PAGINA_ATUAL", getJSP(request));
           response.sendRedirect(session.getAttribute("COM_RN") + "jsp/com_referererror.jsp");
           return false;
        }
     }
     else
     {  if(verifica_na_sessao)
           if( (! session.getAttribute("com_ultima_pagina_refresh").toString().equals(pReferer1)) &&
               (! session.getAttribute("com_ultima_pagina_refresh").toString().equals(pReferer2)) &&
               (! session.getAttribute("com_ultima_pagina_refresh").toString().equals(pReferer3)) )
           {  if( ! session.getAttribute("com_ultima_pagina_refresh").toString().equals(getJSP(request))  )
              {  UtilBean.session_putValue(session, "COM_MENU_PAGINA_ATUAL", getJSP(request));
                 response.sendRedirect(session.getAttribute("COM_RN") + "jsp/com_referererror.jsp");
                 return false;
              }
              else
                 sReferer = HTTP_REFERER;
           }
           else
              sReferer = HTTP_REFERER;
        else
           sReferer = HTTP_REFERER;
     }
     return true;
  }

  public  void execute(appControlBean icontroleapp, conexaoBean iconexaobean, JspWriter out,
                       HttpServletRequest request,
                       HttpServletResponse response,
                       HttpSession session,
                       boolean verificalogado)
          throws java.io.IOException, SQLException, Exception
  {
     internalexecute(icontroleapp, iconexaobean, out, request, response, session, verificalogado);
  }
  public  void execute(appControlBean icontroleapp, conexaoBean iconexaobean, JspWriter out,
                       HttpServletRequest request,
                       HttpServletResponse response,
                       HttpSession session)
          throws java.io.IOException, SQLException, Exception
  {
     internalexecute(icontroleapp, iconexaobean, out, request, response, session, true);
  }

  public  void internalexecute(appControlBean icontroleapp, conexaoBean iconexaobean, JspWriter out,
                       HttpServletRequest request,
                       HttpServletResponse response,
                       HttpSession session,
                       boolean verificalogado)
          throws java.io.IOException, SQLException, Exception
  {
     sessao = session;

     // Tratamento time-out -------------------------
     boolean nulo = false;
     boolean timeout = false;
     String COM_SESSAOJSP = "";
     String COM_DT_HR_ULT_ACESSO = "";
     String COMLOGGED = "";
     String COM_ULTIMA_PAGINA_REFRESH = "";
     String COM_SN = "";
     String COM_RN = "";
     if (UtilBean.session_getValue(session, "COM_ISLOGGEDIN") == null)
     {  nulo = true;
UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute 01 COM_ISLOGGEDIN está NULL.", session, request, true);

        //pode ter dado time-out ou pode ter caido a session
        Cookie mycookie[] = request.getCookies(); // = new Cookie("COMLOGGED", "FALSE");
        if(request.getCookies() != null)
        {  for(int i = 0; i < mycookie.length ; i++)
           {  if (mycookie[i].getName().equals("COMLOGGED"))
              {  if(COMLOGGED.equals(""))
                   COMLOGGED = mycookie[i].getValue();
              }
              if (mycookie[i].getName().equals("COM_SESSAOJSP"))
              {  if(COM_SESSAOJSP.equals(""))
                   COM_SESSAOJSP = mycookie[i].getValue();
              }
              if (mycookie[i].getName().equals("COM_DT_HR_ULT_ACESSO"))
              {  if(COM_DT_HR_ULT_ACESSO.equals(""))
                   COM_DT_HR_ULT_ACESSO = mycookie[i].getValue();
              }
              if (mycookie[i].getName().equals("COM_ULTIMA_PAGINA_REFRESH"))
              {  if(COM_ULTIMA_PAGINA_REFRESH.equals(""))
                   COM_ULTIMA_PAGINA_REFRESH = mycookie[i].getValue();
              }
              if (mycookie[i].getName().equals("COM_SN"))
              {  if(COM_SN.equals(""))
                   COM_SN = mycookie[i].getValue();
              }
              if (mycookie[i].getName().equals("COM_RN"))
              {  if(COM_RN.equals(""))
                   COM_RN = mycookie[i].getValue();
              }
           }
        }
        if(COM_SESSAOJSP.equals("NULO")) COM_SESSAOJSP = "";
        if(COM_DT_HR_ULT_ACESSO.equals("NULO")) COM_DT_HR_ULT_ACESSO = "";
        if(COMLOGGED.equals("NULO")) COMLOGGED = "";
        if(COM_ULTIMA_PAGINA_REFRESH.equals("NULO")) COM_ULTIMA_PAGINA_REFRESH = "";
        if(COM_SN.equals("NULO")) COM_SN = "";
        if(COM_RN.equals("NULO")) COM_RN = "";

        if(  (COMLOGGED.toUpperCase().equals("TRUE")) && (! COM_SESSAOJSP.equals("")) &&
             (! COM_DT_HR_ULT_ACESSO.equals("")) )
        {
           //verificar se deu time-out ou caiu o servidor
           //COM_DT_HR_ULT_ACESSO = Thu Dec 20 13:08:50 GMT-02:00 2001
           //                       Thu Dec 20 13:09:50 GMT-02:00 2001
           java.util.Date dataCookie = new java.util.Date();
           java.util.Date dataAtual  = new java.util.Date();
           SimpleDateFormat dtForm = new SimpleDateFormat("",new Locale("pt","BR"));
           dtForm.applyPattern("yyyy-MM-dd HH:mm:ss.S");
           dataCookie = dtForm.parse(COM_DT_HR_ULT_ACESSO);

           //System.out.println("DataAtual =" + dataAtual + ", DataCookie=" + dataCookie);
           //System.out.println("YearAtual =" + dataAtual.getYear() + ", Mes=" + dataAtual.getMonth() + ", Dia=" + dataAtual.getDay() + ", Hora=" + dataAtual.getHours() + ", Min=" + dataAtual.getMinutes() + ", Seg=" + dataAtual.getSeconds() + ", Date=" + dataAtual.getDate() + ", Time=" + dataAtual.getTime());
           //System.out.println("YearCookie=" + dataCookie.getYear() + ", Mes=" + dataCookie.getMonth() + ", Dia=" + dataCookie.getDay() + ", Hora=" + dataCookie.getHours() + ", Min=" + dataCookie.getMinutes() + ", Seg=" + dataCookie.getSeconds() + ", Date=" + dataCookie.getDate() + ", Time=" + dataCookie.getTime());

           double diferenca = 0;
           int numtimeout = 20;

           diferenca = ((dataAtual.getTime() - dataCookie.getTime()) / 1000) / 60;
           /*******************************************************
           diferenca = (dataAtual.getYear() - dataCookie.getYear()) * 365 * 24 * 60;
           if(diferenca < numtimeout)
           {
             if(dataAtual.getMonth() >= dataCookie.getMonth())
                diferenca += (dataAtual.getMonth() - dataCookie.getMonth()) * 30 * 24 * 60;
             else
                diferenca += (12 - (dataCookie.getMonth() - dataAtual.getMonth())) * 30 * 24 * 60;
             if(diferenca < numtimeout)
             {
               if(dataAtual.getDate() >= dataCookie.getDate())
                  diferenca += (dataAtual.getDate() - dataCookie.getDate()) * 24 * 60;
               else
                  diferenca += (30 - (dataCookie.getDate() - dataAtual.getDate())) * 24 * 60;
               if(diferenca < numtimeout)
               {
                 if(dataAtual.getHours() >= dataCookie.getHours())
                    diferenca += (dataAtual.getHours() - dataCookie.getHours()) * 60;
                 else
                    diferenca += (24 - (dataCookie.getHours() - dataAtual.getHours())) * 60;
                 if(diferenca < numtimeout)
                 {
                   if(dataAtual.getMinutes() >= dataCookie.getMinutes())
                      diferenca += (dataAtual.getMinutes() - dataCookie.getMinutes());
                   else
                      diferenca += (60 - (dataCookie.getMinutes() - dataAtual.getMinutes()));
                 }
               }
             }
           }
           *******************************************************/
           //System.out.println("diferenca=" + diferenca);
           if(diferenca > numtimeout)
           {  //deu timeout
              timeout = true;
           }
           else
           {  //caiu servidor, devo restaurar a session...
              PreparedStatement pstm  = null;
              ResultSet rs  = null;
              try {
                //iconexaobean.rollback();
                pstm = (PreparedStatement)iconexaobean.getConnection().prepareStatement(
                   "SELECT CDG_USUR "
                  +"FROM COM_ACES_HISTORICO_LOGIN "
                  +"WHERE COM_SESSAOJSP = :1 AND STT_LOGIN_LOGOUT = 'I' AND DTA_LOGOUT IS NULL ",
                   ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                pstm.setString(1, COM_SESSAOJSP);
                rs = (ResultSet) pstm.executeQuery();
                if(rs.next())
                {
                   session.setAttribute("COM_SESSAOJSP", COM_SESSAOJSP);
                   session.setAttribute("COM_ISLOGGEDIN", "true");
                   session.setAttribute("com_ultima_pagina_refresh", COM_ULTIMA_PAGINA_REFRESH);
                   session.setAttribute("COM_MENU_PAGINA_ATUAL", getJSP(request));
                   session.setAttribute("COM_SN", COM_SN);
                   session.setAttribute("COM_RN", COM_RN);

                   session.setAttribute("COM_CDG_USUR", rs.getString("CDG_USUR"));
                }
                else
                {  timeout = true; }
                rs.close();
                pstm.close();
                System.out.println("COM warning: sessao restaurada: " + COM_SESSAOJSP);
              } //fim try
              catch (SQLException sqlex) {
                 timeout = true;
                 System.out.println("COM erro (2) ao restaurar sessao em verificaAcessoBean: " + sqlex + "-" + new java.util.Date() + ".");
                 if(rs != null) rs.close();
                 if(pstm != null) pstm.close();
              }
              catch (Exception ex) {
                 timeout = true;
                 System.out.println("COM erro (3) ao restaurar sessao em verificaAcessoBean: " + ex + "-" + new java.util.Date() + ".");
                 if(rs != null) rs.close();
                 if(pstm != null) pstm.close();
              }
           } // fim do if(diferenca > timeout)
        }
        else //else do if(  (COMLOGGED.equals("true"))...
        {
          if(TELA_NM_TELA.equals("VERIFICATIMEOUT") && COMLOGGED.toUpperCase().equals("FALSE"))
          {
             //session.setAttribute("COM_SESSAOJSP", "");
             session.setAttribute("COM_ISLOGGEDIN", "false");
             session.setAttribute("com_ultima_pagina_refresh", COM_ULTIMA_PAGINA_REFRESH);
             session.setAttribute("COM_MENU_PAGINA_ATUAL", getJSP(request));
             session.setAttribute("COM_SN", COM_SN);
             session.setAttribute("COM_RN", COM_RN);

             //session.setAttribute("COM_CDG_USUR", "");
          }
          else if(TELA_NM_TELA.equals("VERIFICATIMEOUT") && COMLOGGED.toUpperCase().equals(""))
          {
             //session.setAttribute("COM_SESSAOJSP", "");
             session.setAttribute("COM_ISLOGGEDIN", "false");
             session.setAttribute("com_ultima_pagina_refresh", COM_ULTIMA_PAGINA_REFRESH);
             session.setAttribute("COM_MENU_PAGINA_ATUAL", getJSP(request));
             //session.setAttribute("COM_SN", COM_SN);
             //session.setAttribute("COM_RN", COM_RN);

             //session.setAttribute("COM_CDG_USUR", "");
          }
          else
             timeout = true;
        } //fim do if(  (COMLOGGED.equals("true"))...

        if(! timeout)
        {
          if(session.getAttribute("COM_RN") == null)
          {  //boolean comlogado =  UtilBean.verificaLogado(session, request, response);
             //UtilBean.setDominio(iconexaobean, session, request, response);

             //21-11/2001---Inicio-------------------------------------------
  out.println("<HTML><HEAD>");
  out.println("  <meta http-equiv=\"Expires\" CONTENT=\"0\">");
  out.println("  <meta http-equiv=\"Cache-Control\" CONTENT=\"no-cache\">");
  out.println("  <meta http-equiv=\"Pragma\" CONTENT=\"no-cache\">");
  out.println("<SCRIPT LANGUAGE=\"JavaScript1.2\">");
  out.println("function errorcarrega() {");
  out.println("   /* http://servidor/empresa/pagina.jsp */");
  out.println("   var local = \"\"+document.location;");
  out.println("   var ind = 0;");
  out.println("   var achou = false;");
  out.println("");
  out.println("   if(local.indexOf(\"?\") >= 0)");
  out.println("      local = local.substring(0, local.indexOf(\"?\"));");
  out.println("   ind = local.length - 1;");
  out.println("");
  out.println("   while ((ind >= 0) && (! achou))");
  out.println("   { ");
  out.println("      if(local.substring(ind, ind+1) == \"/\")");
  out.println("         achou = true;");
  out.println("      else");
  out.println("         ind--;");
  out.println("   } ");
  if(UtilBean.getJSP(request).equals("com_msgtimeout.jsp"))
  {  out.println("      document.location.href = local.substring(0, ind+1) + \"redir.jsp?\" + \"timeout=true\" + \"&origem=\" + escape(document.location) + \"&hostname=\" + escape(document.location.hostname);");
  } else {
     out.println("      document.location.href = local.substring(0, ind+1) + \"redir.jsp?\" + \"origem=\" + escape(document.location) + \"&hostname=\" + escape(document.location.hostname);");
  }
  out.println("   return true;");
  out.println("}");
  out.println("function navega() {");
  out.println("   /* http://servidor/empresa/pagina.jsp */");
  out.println("   var local = \"\"+document.location;");
  out.println("   var ind = 0;");
  out.println("   var achou = false;");
  out.println("");
  out.println("   if(local.indexOf(\"?\") >= 0)");
  out.println("      local = local.substring(0, local.indexOf(\"?\"));");
  out.println("   ind = local.length - 1;");
  out.println("");
  out.println("   while ((ind >= 0) && (! achou))");
  out.println("   {");
  out.println("      if(local.substring(ind, ind+1) == \"/\")");
  out.println("         achou = true;");
  out.println("      else");
  out.println("         ind--;");
  out.println("   }");
  out.println("   document.location.href = local.substring(0, ind+1) + \"redir.jsp?\" + \"origem=\" + escape(document.location) + \"&hostname=\" + escape(document.location.hostname);");
  out.println("   return true;");
  out.println("}");
  out.println("</script>");
  out.println("</HEAD>");
  out.println("<BODY onLoad=\"errorcarrega()\">");
  out.println("<font color=\"#FFFFFF\"><a href=\"javascript:void navega()\"> </a> Clique para continuar.</font>");
  out.println("</BODY>");
  out.println("</HTML>");
             return;
             //21-11/2001---Fim----------------------------------------------
          }
          //processar redirecionamentos.
          String paginaredir = "TelaInicial.jsp";
          String pagina = getJSP(request);

          String paginaredirbaixo = "";
          String paginabaixo = "";

          PreparedStatement pstm  = null;
          ResultSet rs  = null;

          if(pagina == null) pagina = "";
          if( pagina.startsWith("erro") )
          {  paginaredir = ""; }
          else
          {
             try {
               //iconexaobean.rollback();
               pstm = (PreparedStatement)iconexaobean.getConnection().prepareStatement(
                  "SELECT EXE_REDIR_QUEDA_SESSAO "
                 +"FROM COM_ACES_MENU "
                 +"WHERE EXECUTAVEL = :1 ",
                  ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
               pstm.setString(1, pagina);
               rs = (ResultSet) pstm.executeQuery();
               if(rs.next())
               {
                  if(rs.getString("EXE_REDIR_QUEDA_SESSAO") == null)
                  {  paginaredir = "TelaInicial.jsp"; }
                  else
                  {  if(! rs.getString("EXE_REDIR_QUEDA_SESSAO").equals(pagina) )
                     {  paginaredir = rs.getString("EXE_REDIR_QUEDA_SESSAO"); }
                     else
                     {  paginaredir = ""; }
                  }
               }
               else
               {  paginaredir = "TelaInicial.jsp"; }
               rs.close();
               pstm.close();
               rs = null;
               pstm = null;
             } //fim try
             catch (SQLException sqlex) {
                System.out.println("COM erro (2) ao redirecionar pagina em verificaAcessoBean: " + sqlex + "-" + new java.util.Date() + ".");
                if(rs != null) { rs.close(); rs = null; }
                if(pstm != null) { pstm.close(); pstm = null; }
             }
             catch (Exception ex) {
                System.out.println("COM erro (3) ao redirecionar pagina em verificaAcessoBean: " + ex + "-" + new java.util.Date() + ".");
                if(rs != null) { rs.close(); rs = null; }
                if(pstm != null) { pstm.close(); pstm = null; }
             }
             if(pagina.equals("TelaInicial.jsp"))
                paginaredir = "index.jsp";
             //testar se é Popup ou LOV
             //Se a Popup precisa redirecionar, então
             //   Se a página de baixo precisa redirecionar, fecha a Popup e redireciona a pagina de baixo de acordo com o EXE_QUEDA da página de baixo
             //   Senão fecha a Popup e redireciona a página de baixo de acordo com o EXE_QUEDA da Popup
             //Senão
             //   Se a página de baixo precisa redirecionar, fecha a Popup e redireciona a pagina de baixo de acordo com o EXE_QUEDA da página de baixo
             //   Senão continua
             if( ((pagina.startsWith("lov")) ||
                  (pagina.startsWith("com_Popup"))
                 ) &&
                 (! pagina.equals("")) )
             {
                paginaredirbaixo = "TelaInicial.jsp";
                paginabaixo = COM_ULTIMA_PAGINA_REFRESH;

                try {
                  //iconexaobean.rollback();
                  if(rs != null) rs.close();
                  if(pstm != null) pstm.close();
                  pstm = (PreparedStatement)iconexaobean.getConnection().prepareStatement(
                     "SELECT EXE_REDIR_QUEDA_SESSAO "
                    +"FROM COM_ACES_MENU "
                    +"WHERE EXECUTAVEL = :1 ",
                     ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                  pstm.setString(1, paginabaixo);
                  rs = (ResultSet) pstm.executeQuery();
                  if(rs.next())
                  {
                     if(rs.getString("EXE_REDIR_QUEDA_SESSAO") == null)
                     {  paginaredirbaixo = "TelaInicial.jsp"; }
                     else
                     {  if(! rs.getString("EXE_REDIR_QUEDA_SESSAO").equals(paginabaixo) )
                        {  paginaredirbaixo = rs.getString("EXE_REDIR_QUEDA_SESSAO"); }
                        else
                        {  paginaredirbaixo = ""; }
                     }
                  }
                  else
                  {  paginaredirbaixo = "TelaInicial.jsp"; }
                  rs.close();
                  pstm.close();
                  rs = null;
                  pstm = null;
                } //fim try
                catch (SQLException sqlex) {
                   System.out.println("COM erro (5) ao redirecionar pagina em verificaAcessoBean: " + sqlex + "-" + new java.util.Date() + ".");
                   if(rs != null) { rs.close(); rs = null; }
                   if(pstm != null) { pstm.close(); pstm = null; }
                }
                catch (Exception ex) {
                   System.out.println("COM erro (6) ao redirecionar pagina em verificaAcessoBean: " + ex + "-" + new java.util.Date() + ".");
                   if(rs != null) { rs.close(); rs = null; }
                   if(pstm != null) { pstm.close(); pstm = null; }
                } 
             } //fim do if (lov ou popup)
          } //fim do else do if( pagina.startsWith("erro") )

          if(! UtilBean.setDominio(icontroleapp, iconexaobean, session, request, response, out))
             return;
          if(COMLOGGED.toUpperCase().equals("TRUE")) {
            menuBean2 menubean = new menuBean2();
            menubean.verificaMenuCarregado(iconexaobean, session, request, response, out, COMLOGGED.toUpperCase().equals("TRUE"));
            session.setAttribute("menubean", menubean);
          }

          //Se a Popup precisa redirecionar, então
          //   Se a página de baixo precisa redirecionar, fecha a Popup e redireciona a pagina de baixo de acordo com o EXE_QUEDA da página de baixo
          //   Senão fecha a Popup e redireciona a página de baixo de acordo com o EXE_QUEDA da Popup
          //Senão
          //   Se a página de baixo precisa redirecionar, fecha a Popup e redireciona a pagina de baixo de acordo com o EXE_QUEDA da página de baixo
          //   Senão continua

          if((! paginaredir.equals("")) || (! paginaredirbaixo.equals(""))) //novo tratamento de controle de sessao (20/12/2001)
          {
             if(! paginaredirbaixo.equals(""))
             {  //é Popup ou lov...
                //26/12/2001---Inicio-------------------------------------------
                out.println("<HTML><HEAD>");
                out.println("  <meta http-equiv=\"Expires\" CONTENT=\"0\">");
                out.println("  <meta http-equiv=\"Cache-Control\" CONTENT=\"no-cache\">");
                out.println("  <meta http-equiv=\"Pragma\" CONTENT=\"no-cache\">");
                out.println("<SCRIPT LANGUAGE=\"JavaScript1.2\">");
                out.println("function redircarrega() {");
                out.println("   /* http://servidor/empresa/pagina.jsp */");
                out.println("   var local = \"\"+document.location;");
                out.println("   var ind = 0;");
                out.println("   var achou = false;");
                out.println("");
                out.println("   if(local.indexOf(\"?\") >= 0)");
                out.println("      local = local.substring(0, local.indexOf(\"?\"));");
                out.println("   ind = local.length - 1;");
                out.println("");
                out.println("   while ((ind >= 0) && (! achou))");
                out.println("   { ");
                out.println("      if(local.substring(ind, ind+1) == \"/\")");
                out.println("         achou = true;");
                out.println("      else");
                out.println("         ind--;");
                out.println("   } ");
                out.println("   opener.location.href = local.substring(0, ind+1) + \"" + paginaredirbaixo +  "\"; ");
                out.println("   window.close(); ");
                out.println("   //return true;");
                out.println("}");
                out.println("</script>");
                out.println("</HEAD>");
                out.println("<BODY onLoad=\"redircarrega()\">");
                out.println("<font color=\"#FFFFFF\"><a href=\"javascript:void redircarrega()\"> </a> Clique para continuar.</font>");
                out.println("</BODY>");
                out.println("</HTML>");
                System.out.println("COM warning(1): sessao restaurada, redirecionando para : " + paginaredirbaixo);
                return;
                //26/12/2001---Fim----------------------------------------------
             }
             else
             {
                if(! paginabaixo.equals(""))
                {  //é Popup ou lov...
                   //26/12/2001---Inicio-------------------------------------------
                   out.println("<HTML><HEAD>");
                   out.println("  <meta http-equiv=\"Expires\" CONTENT=\"0\">");
                   out.println("  <meta http-equiv=\"Cache-Control\" CONTENT=\"no-cache\">");
                   out.println("  <meta http-equiv=\"Pragma\" CONTENT=\"no-cache\">");
                   out.println("<SCRIPT LANGUAGE=\"JavaScript1.2\">");
                   out.println("function redircarrega() {");
                   out.println("   /* http://servidor/empresa/pagina.jsp */");
                   out.println("   var local = \"\"+document.location;");
                   out.println("   var ind = 0;");
                   out.println("   var achou = false;");
                   out.println("");
                   out.println("   if(local.indexOf(\"?\") >= 0)");
                   out.println("      local = local.substring(0, local.indexOf(\"?\"));");
                   out.println("   ind = local.length - 1;");
                   out.println("");
                   out.println("   while ((ind >= 0) && (! achou))");
                   out.println("   { ");
                   out.println("      if(local.substring(ind, ind+1) == \"/\")");
                   out.println("         achou = true;");
                   out.println("      else");
                   out.println("         ind--;");
                   out.println("   } ");
                   out.println("   opener.location.href = local.substring(0, ind+1) + \"" + paginaredir +  "\"; ");
                   out.println("   window.close(); ");
                   out.println("   //return true;");
                   out.println("}");
                   out.println("</script>");
                   out.println("</HEAD>");
                   out.println("<BODY onLoad=\"redircarrega()\">");
                   out.println("<font color=\"#FFFFFF\"><a href=\"javascript:void redircarrega()\"> </a> Clique para continuar.</font>");
                   out.println("</BODY>");
                   out.println("</HTML>");
                   System.out.println("COM warning(2): sessao restaurada, redirecionando para : " + paginaredir);
                   return;
                   //26/12/2001---Fim----------------------------------------------
                }
                else
                {  System.out.println("COM warning: sessao restaurada, redirecionando para : " + session.getAttribute("COM_RN") + "jsp/" + paginaredir);
                   response.sendRedirect(session.getAttribute("COM_RN") + "jsp/" + paginaredir);
                   return;
                }
             }
          }
          else
          {  System.out.println("COM warning: sessao restaurada, não é necessário redirecionar.");
          }
        }
        else
        {
          UtilBean.session_putValue(session, "COM_ISLOGGEDIN", "false");
          UtilBean.session_putValue(session, "COM_MENU_PAGINA_ATUAL", getJSP(request));
          if(session.getAttribute("COM_RN") == null)
          {  //boolean comlogado =  UtilBean.verificaLogado(session, request, response);
             //UtilBean.setDominio(iconexaobean, session, request, response);

             //21-11/2001---Inicio-------------------------------------------

  out.println("<HTML><HEAD>");
  out.println("  <meta http-equiv=\"Expires\" CONTENT=\"0\">");
  out.println("  <meta http-equiv=\"Cache-Control\" CONTENT=\"no-cache\">");
  out.println("  <meta http-equiv=\"Pragma\" CONTENT=\"no-cache\">");
  out.println("<SCRIPT LANGUAGE=\"JavaScript1.2\">");
  out.println("function errorcarrega() {");
  out.println("   /* http://servidor/empresa/pagina.jsp */");
  out.println("   var local = \"\"+document.location;");
  out.println("   var ind = 0;");
  out.println("   var achou = false;");
  out.println("");
  out.println("   if(local.indexOf(\"?\") >= 0)");
  out.println("      local = local.substring(0, local.indexOf(\"?\"));");
  out.println("   ind = local.length - 1;");
  out.println("");
  out.println("   while ((ind >= 0) && (! achou))");
  out.println("   {");
  out.println("      if(local.substring(ind, ind+1) == \"/\")");
  out.println("         achou = true;");
  out.println("      else");
  out.println("         ind--;");
  out.println("   }");
  out.println("   document.location.href = local.substring(0, ind+1) + \"com_msgtimeout.jsp\";");
  out.println("   return true;");
  out.println("}");
  out.println("");
  out.println("function navega() {");
  out.println("   /* http://servidor/empresa/pagina.jsp */");
  out.println("   var local = \"\"+document.location;");
  out.println("   var ind = 0;");
  out.println("   var achou = false;");
  out.println("");
  out.println("   if(local.indexOf(\"?\") >= 0)");
  out.println("      local = local.substring(0, local.indexOf(\"?\"));");
  out.println("   ind = local.length - 1;");
  out.println("");
  out.println("   while ((ind >= 0) && (! achou))");
  out.println("   {");
  out.println("      if(local.substring(ind, ind+1) == \"/\")");
  out.println("         achou = true;");
  out.println("      else");
  out.println("         ind--;");
  out.println("   }");
  out.println("   document.location.href = local.substring(0, ind+1) + \"com_msgtimeout.jsp\";");
  out.println("   return true;");
  out.println("}");
  out.println("");
  out.println("</script>");
  out.println("</HEAD>");
  out.println("<BODY onLoad=\"errorcarrega()\">");
  out.println("<font color=\"#FFFFFF\"><a href=\"javascript:void navega()\"> </a>Clique para continuar.</font>");
  out.println("</BODY>");
  out.println("</HTML>");
             return;
             //21-11/2001---Fim----------------------------------------------
          }
          response.sendRedirect(session.getAttribute("COM_RN") + "jsp/com_msgtimeout.jsp");
          return;
       }//fim do if(timeout)

     }
     // ---------------------------------------------

     String loggedin = "";
     if(UtilBean.session_getValue(session, "COM_ISLOGGEDIN") != null)
     {  loggedin = (String) UtilBean.session_getValue(session, "COM_ISLOGGEDIN");
         UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.java 01 COM_ISLOGGEDIN está "+loggedin+".", session, request, true);
     } else
         UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.java 02 COM_ISLOGGEDIN está null.", session, request, true);



     if(verificalogado)
       if((loggedin == null) || (! loggedin.equals("true")))
       {  //out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"1; URL=" + session.getAttribute("COM_RN") + "jsp/" + UtilBean.montaURL(session, "index.jsp", "msg_erro=Para%20acessar%20a%20tela%20desejada,%20e%20necessario%20efetuar%20o%20Login%20no%20sistema") + "\"> ");
UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute 02.", session, request, true);
          out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"1; URL=" + session.getAttribute("COM_RN") + "jsp/" + UtilBean.montaURL(session, "index.jsp", "msg_erro=") + "\"> ");
          out.println("</head><body class=\"comBodyAces\">");
          out.println("<br><center>Redirecionando para autenticação...</center>");
          return;
       }

      boolean existeCookie = false;
      String valorCookie = "";
      Cookie mycookie[] = request.getCookies(); // = new Cookie("COMLOGGED", "FALSE");
      if(request.getCookies() != null)
      {  for(int i = 0; i < mycookie.length ; i++)
         {  if (mycookie[i].getName().equals("COMLOGGED"))
            {  if(! existeCookie) {
                 existeCookie = true;
                 valorCookie = mycookie[i].getValue();
UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute XX valorCookie="+valorCookie+".", session, request, true);
                 //System.out.println("getMaxAge="+mycookie[i].getMaxAge());
                 //System.out.println("getVersion="+mycookie[i].getVersion());
                 //System.out.println("getComment="+mycookie[i].getComment());
                 //System.out.println("getSecure="+mycookie[i].getSecure());
               }
            }
         }
      }
      if((! nulo) && (! existeCookie))
      {   
UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute 03.", session, request, true);
        UtilBean.limpaSession(request, session, response, true, false);
      }
      else
      {  
UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute 04.", session, request, true);
          
         if(! existeCookie)
         {  
UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute 05 setando cookie false.", session, request, true);
             
            //Cookie mycookienew = new Cookie("COMLOGGED", "FALSE");
            //response.addCookie(mycookienew);

            //Cookie mycookie2 = new Cookie("COM_SESSAOJSP", "NULO");
            //response.addCookie(mycookie2);
             UtilBean.CriaOuModificaCookie(request, response, "COMLOGGED", "FALSE");
             UtilBean.CriaOuModificaCookie(request, response, "COM_SESSAOJSP", "NULO");
         }
      }

      if(loggedin.equalsIgnoreCase("true"))
      {
UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute 06.", session, request, true);
         //Tratamento de novo IE: no Internet Explorer, quando o usuário clica
         // novamente no ícone do programa, é aberta uma nova janela do Browser e
         // também um novo executável fica rodando no micro. Este novo executável
         // NÃO "ENXERGA" OS COOKIES temporários setados no executável anterior,
         // porém ele vai compartilhar a MESMA SESSION do executável anterior, já
         // que a session fica no servidor Web e não no Browser.
         //Devemos impedir que isso aconteça, pois poderá ocasionar uma
         // inconsistência (a session é compartilhada pelos 2 executáveis,
         // mas o cookie não). Iria aparecer em um Browser como se o usuário
         // estivesse logado, mas em outro como se ele não estivesse logado.
         if (! existeCookie)
         {  
UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute 07.", session, request, true);
            
            UtilBean.session_putValue(session, "COM_MENU_PAGINA_ATUAL", getJSP(request));
            response.sendRedirect(session.getAttribute("COM_RN") + "jsp/com_referererror.jsp?e=nav");
            return;
         }
      }

      if(verificalogado)
      { if ((! existeCookie) || (! valorCookie.equals("TRUE")))
        {  
UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute 08, valorCookie="+valorCookie+".", session, request, true);
            
           UtilBean.session_putValue(session, "COM_ISLOGGEDIN", "false");
           iconexaobean.rollback();
           iconexaobean.desconecta(iconexaobean, out, request, response, session);

           out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"1; URL=" + session.getAttribute("COM_RN") + "jsp/" + UtilBean.montaURL(session, "index.jsp", "") + "\"> ");
           out.println("</head><body class=\"comBodyAces\">");
           out.println("<br><center> ... </center>");
           return;
        }

         //if( (request.getServerPort() != 7070) &&
         //    (request.getServerPort() != 7072) &&
         //    (request.getServerPort() != 8988) &&
         //    (request.getServerPort() != 7101) )
		 if(true)
         {  //weblogic String redirlogin = UtilBean.SSOInclude (icontroleapp, iconexaobean, iconexaobean.getConnection(), out, request, response, session);
UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute 09.", session, request, true);
            String redirlogin = ""; //weblogic  
            if(! redirlogin.equals(""))
            {  
UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute 10.", session, request, true);
                
               UtilBean.session_putValue(session, "COM_ISLOGGEDIN", "false");
               iconexaobean.rollback();
               iconexaobean.desconecta(iconexaobean, out, request, response, session);

               out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"1; URL=" + session.getAttribute("COM_RN") + "jsp/" + UtilBean.montaURL(session, "index.jsp", "") + "\"> ");
               out.println("</head><body class=\"comBodyAces\">");
               out.println("<br><center> ... </center>");
               return;
            }
            else
            {   
UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute 11.", session, request, true);
                
                if((session.getAttribute("COM_CDG_USUR") == null) ||
                   (session.getAttribute("COM_CDG_USUR").equals("")))
                {  
UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute 12.", session, request, true);
                    
                   UtilBean.session_putValue(session, "COM_ISLOGGEDIN", "false");
                   iconexaobean.rollback();
                   iconexaobean.desconecta(iconexaobean, out, request, response, session);

                   out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"1; URL=" + session.getAttribute("COM_RN") + "jsp/" + UtilBean.montaURL(session, "index.jsp", "") + "\"> ");
                   out.println("</head><body class=\"comBodyAces\">");
                   out.println("<br><center> ... </center>");
                   return;
                }
            }
         }
      }

     if(UtilBean.session_getValue(session, "COM_CDG_USUR") != null)
        USUR_NM_LOGIN = (String) UtilBean.session_getValue(session, "COM_CDG_USUR");
     else
        USUR_NM_LOGIN = "";


     if(TELA_NM_TELA.equals("VERIFICALOGADO"))
     {  
UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute 13 T True.", session, request, true);
         
        TIPO_ACESSO = "T";
        verificaAcessoOK = true;
     }
     else if(TELA_NM_TELA.equals("VERIFICATIMEOUT"))
     {  
UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute 14 T True.", session, request, true);
        TIPO_ACESSO = "T";
        verificaAcessoOK = true;
     }
     else
     {
UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute 15.", session, request, true);
       menuBean sessao_menubean = (menuBean)session.getAttribute("menubean");
       TIPO_ACESSO = sessao_menubean.getTipoAcesso( TELA_NM_TELA );
       if(TIPO_ACESSO.equals(""))
       {  
UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute 16.", session, request, true);
           
          UtilBean.session_putValue(session, "COM_MENU_PAGINA_ATUAL", getJSP(request));
          verificaAcessoOK = false;
          response.sendRedirect(session.getAttribute("COM_RN") + "jsp/com_semacesso.jsp");
       }
       else if(TIPO_ACESSO.equals("BLOQUEADO"))
       {  UtilBean.session_putValue(session, "COM_MENU_PAGINA_ATUAL", getJSP(request));
          verificaAcessoOK = false;
          response.sendRedirect(session.getAttribute("COM_RN") + "jsp/com_telabloqueada.jsp");
       }
       else
       {
UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute 17 ?.", session, request, true);
          if(false) //retirado trecho antigo...
          {  
          }
          else
          {
UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute 18.", session, request, true);
             verificaAcessoOK = true;
             //Bloco de gravação de histórico de Telas:
             conexaoBean rastrConnBean = null;
             try {
                if( sessao_menubean.IN_RASTREAVEL.equals("S") )
                {
                   rastrConnBean = new conexaoBean();
                   rastrConnBean.execute(icontroleapp, session, request, response, out);
                   CallableStatement pstmIns  = null;
                   //iconexaobean.rollback();
                   pstmIns = (CallableStatement)rastrConnBean.getConnection().prepareCall("BEGIN COM_ACES_GRAVA_HIST_TELA(:1, :2, :3, :4); END;");
                   pstmIns.setString(1, ""+UtilBean.session_getValue(session, "COM_CDG_USUR"));
                   pstmIns.setString(2, getJSP(request));
                   pstmIns.setString(3, request.getRemoteAddr());
                   pstmIns.setString(4, ""+UtilBean.session_getValue(session, "COM_SESSAOJSP"));
                   pstmIns.execute();
                   rastrConnBean.desconecta(iconexaobean, out, request, response, session);
                }
             }
             catch(SQLException sqlexh) { System.out.println("verificaAcessobean: Erro ao gravar rastreamento de tela(2)=" + new java.util.Date() + " " + sqlexh); }
             catch(Exception exh) { System.out.println("verificaAcessobean: Erro ao gravar rastreamento de tela(3)=" + new java.util.Date() + " " + exh); }
             finally {
                if( rastrConnBean != null )
                   rastrConnBean.desconecta(iconexaobean, out, request, response, session);
             }
          }
       }
     } //fim do else do if(TELA_NM_TELA.equals("VERIFICALOGADO"))
if(verificaAcessoOK)     
  UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute FIM verificaAcessoOK True.", session, request, true);
else
  UtilBean.GeraDebugLogSimples("20221122 verificaAcesso.execute FIM verificaAcessoOK False.", session, request, true);

  } // execute

  public String verificaAcessoMenu(String nomeAplicacao)
         throws java.io.IOException, Exception
  {  //pode retornar T (total), L (leitura) ou R (restrito) ou BLOQUEADO
     if(sessao.getAttribute("menubean") == null)
     {  return "";
     }
     else
     {  menuBean sessao_menubean = (menuBean)sessao.getAttribute("menubean");
        return sessao_menubean.getTipoAcesso( nomeAplicacao );
     }
  }
  public String verificaAcessoCampo(String nomeAplicacao, String nomeAtributo)
         throws java.io.IOException, Exception
  {  //pode retornar T (total), L (leitura) ou I (invisível)
     if(sessao.getAttribute("menubean") == null)
     {  return "";
     }
     else
     {  menuBean sessao_menubean = (menuBean)sessao.getAttribute("menubean");
        return sessao_menubean.getTipoAcessoCampo( nomeAplicacao, nomeAtributo );
     }
  }

  public String getJSP(HttpServletRequest request)
         throws java.io.IOException, Exception
  {
    String str="";
    //???? apache jserv String buf=request.getServletPath();
    String buf=request.getRequestURI();
    
    if(buf.indexOf("?") >= 0)                    //2708-chris
       buf = buf.substring(0, buf.indexOf("?")); //2708-chris
    for (int x = (buf.length()-1) ; x>0; x--){
      if (buf.charAt(x) != '/'){
        str = buf.charAt(x)+str;
      }else{
        break;
      }
    }
    return str;
  }

}

