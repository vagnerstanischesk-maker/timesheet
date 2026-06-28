/**************************************************************************
Empresa: Triscal
Autor: Christiano Chamma/Alexandre
Data: Junho de 2001

Descrição:
      Classe com funções diversas para tratamento de menus do site.

**************************************************************************/
package combeans;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.text.*;
import java.sql.*;
import javax.servlet.http.*;

import java.sql.Connection;
import oracle.jdbc.*; 
import java.sql.*;
import javax.servlet.jsp.*;

public class menuBean extends Object implements HttpSessionBindingListener {

    String menuTop = "";
    public String menuLocal = "";
    public String menuLocalHeader = "";
    public String IN_RASTREAVEL = "";
    String menuAtual = "";
    int regContador = 0;
    int regNumMenus = -1;
    Vector vet_ORDEM                  = new Vector();

    Vector vet_MENU_CDG_MENU          = new Vector();
    Vector vet_PERFIL_CDG_PERFIL      = new Vector();
    Vector vet_MENU_DESCR_RESUMIDA    = new Vector();
    Vector vet_MENU_IN_BLOQUEADO      = new Vector();
    Vector vet_MENU_IN_RASTREAVEL     = new Vector();
    Vector vet_MENU_CDG_MENU_PAI      = new Vector();
    Vector vet_PM_TIPO_ACESSO         = new Vector();
    Vector vet_MENU_IN_VOLTAR         = new Vector();
    Vector vet_MENU_IN_VISIVEL        = new Vector();
    Vector vet_MENU_IN_POPUP          = new Vector();
    Vector vet_PM_ORDEM               = new Vector();
    Vector vet_MENU_NIVEL             = new Vector();
    Vector vet_MENU_IMPRIMIU          = new Vector();

    Vector vet_APP_EXECUTAVEL         = new Vector();
    Vector vet_APP_URL_HELP           = new Vector();

    Vector vet_ATUAL_MENU_CDG_MENU          = new Vector();
    Vector vet_ATUAL_PERFIL_CDG_PERFIL      = new Vector();
    Vector vet_ATUAL_MENU_DESCR_RESUMIDA    = new Vector();
    Vector vet_ATUAL_MENU_IN_BLOQUEADO      = new Vector();
    Vector vet_ATUAL_MENU_IN_RASTREAVEL     = new Vector();
    Vector vet_ATUAL_MENU_CDG_MENU_PAI      = new Vector();
    Vector vet_ATUAL_PM_TIPO_ACESSO         = new Vector();
    Vector vet_ATUAL_MENU_IN_VOLTAR         = new Vector();
    Vector vet_ATUAL_MENU_IN_VISIVEL        = new Vector();
    Vector vet_ATUAL_MENU_IN_POPUP          = new Vector();
    Vector vet_ATUAL_PM_ORDEM               = new Vector();
    Vector vet_ATUAL_MENU_NIVEL             = new Vector();

    Vector vet_ATUAL_APP_EXECUTAVEL         = new Vector();
    Vector vet_ATUAL_APP_URL_HELP           = new Vector();

    Vector vet_ATT_NOME_ATRIBUTO            = new Vector();
    Vector vet_ATT_TIPO_ACESSO              = new Vector();
    Vector vet_ATT_CDG_MENU                 = new Vector();
    Vector vet_ATT_EXECUTAVEL               = new Vector();

  public String getVersao()
  {   return("13/05/2002");
  }
   /************************ Metodos do LISTENER da session **********************/
   public void valueBound (HttpSessionBindingEvent event) {
//System.out.print("menubean: VALUEBOUND!!!");
   }
   public synchronized void valueUnbound (HttpSessionBindingEvent event) {
//System.out.print("menubean: VALUEUNBOUND!!!");
java.util.Date data = new java.util.Date();
//System.out.print("menubean: VALUEUNBOUND:" + data + " " + data.getTime() + "." );
     try { destroiMenu(); }
     catch (Exception ignored) {
//System.out.print("menubean: VALUEUNBOUND ERROR:" + ignored + "." );
     }
   }
   /************************ Metodos do LISTENER da session **********************/

    public void verificaMenuCarregado(conexaoBean iconexaobean,
                                      HttpSession session,
                                      HttpServletRequest request,
                                      HttpServletResponse response,
                                      JspWriter out,
                                      boolean menulogado)
           throws SQLException, java.io.IOException, SQLException, Exception
    {
       if( (UtilBean.session_getValue(session, "COM_MENUBUILT") == null) ||
           (! UtilBean.session_getValue(session, "COM_MENUBUILT").equals("S")) )
           carregaMenu(iconexaobean, session, request, out, menulogado);
    }

    void carregaMenu(conexaoBean iconexaobean,
                     HttpSession session,
                     HttpServletRequest request,
                     JspWriter out,
                     boolean menulogado)
           throws SQLException, java.io.IOException, SQLException, Exception
    {
       PreparedStatement pstm = null;
       ResultSet rs = null;
       PreparedStatement pstm2 = null;
       ResultSet rs2 = null;

       destroiMenu();
       if(menulogado)
       {
          pstm = (PreparedStatement)iconexaobean.getConnection().prepareStatement(
" SELECT P.CDG_PERFIL CDG_PERFIL, M.CDG_MENU, M.CDG_MENU_PAI, M.DESCR_RESUMIDA, PM.TIPO_ACESSO, M.IN_BLOQUEADO MENU_IN_BLOQUEADO," +
"        M.IN_RASTREAVEL MENU_IN_RASTREAVEL, M.EXECUTAVEL, M.URL_HELP," +
"        PM.ORDEM, M.IN_VOLTAR, M.IN_VISIVEL, RTRIM(M.SQLCMD_HABILITA) SQLCMD_HABILITA, M.IN_POPUP IN_POPUP " +
"   FROM COM_ACES_USUARIO_PERFIL UP, COM_ACES_PERFIL P, COM_ACES_PERFIL_MENU PM," +
"        COM_ACES_MENU M " +
"  WHERE UP.CDG_USUR = :1" +
"    AND UP.CDG_PERFIL = P.CDG_PERFIL" +
"    AND P.CDG_PERFIL = PM.CDG_PERFIL" +
"    AND SYSDATE BETWEEN PM.DT_INICIO AND PM.DT_FIM" +
"    AND PM.CDG_MENU = M.CDG_MENU" +
"    AND SYSDATE BETWEEN M.DT_INICIO AND M.DT_FIM" +
"    AND M.TP_MODULO IN ('M', 'J')" +
" ORDER BY nvl(m.cdg_menu_pai, 0), PM.ORDEM"
                  ,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
          pstm.setString( 1, ""+UtilBean.session_getValue(session, "COM_CDG_USUR") );
       }
       else
       {  pstm = (PreparedStatement)iconexaobean.getConnection().prepareStatement(
"select * from dual where dummy = 'CHRIS' "  ,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
       }
       rs = (ResultSet) pstm.executeQuery();

       boolean mostra = true;
       while(rs.next())
       {
        //05/02/2002---
        mostra = verificaSQLCMD_HABILITA( rs, iconexaobean, session, request, out, menulogado );

        if(mostra)
        { int Nivel = calculaNivel(""+rs.getInt("CDG_MENU"), ""+UtilBean.getVerificaNull(rs.getString("CDG_MENU_PAI")));

          vet_MENU_CDG_MENU.addElement( ""+rs.getInt("CDG_MENU") );
          vet_PERFIL_CDG_PERFIL.addElement( ""+rs.getInt("CDG_PERFIL") );
          vet_MENU_DESCR_RESUMIDA.addElement( rs.getString("DESCR_RESUMIDA") );
          vet_MENU_IN_BLOQUEADO.addElement( rs.getString("MENU_IN_BLOQUEADO") );
          vet_MENU_IN_RASTREAVEL.addElement( rs.getString("MENU_IN_RASTREAVEL") );
          vet_MENU_CDG_MENU_PAI.addElement( ""+UtilBean.getVerificaNull(rs.getString("CDG_MENU_PAI")) );
          vet_PM_TIPO_ACESSO.addElement( rs.getString("TIPO_ACESSO") );
          vet_MENU_IN_VOLTAR.addElement( rs.getString("IN_VOLTAR") );
          vet_MENU_IN_VISIVEL.addElement( rs.getString("IN_VISIVEL") );
          vet_MENU_IN_POPUP.addElement( rs.getString("IN_POPUP") );
          vet_PM_ORDEM.addElement( ""+UtilBean.getVerificaNull(rs.getString("ORDEM")) );
          vet_MENU_NIVEL.addElement( ""+Nivel );
          vet_MENU_IMPRIMIU.addElement( "N" );

          vet_APP_EXECUTAVEL.addElement( ""+UtilBean.getVerificaNull(rs.getString("EXECUTAVEL")) );
          vet_APP_URL_HELP.addElement( ""+UtilBean.getVerificaNull(rs.getString("URL_HELP")) );
        }
       } //fim while next

       if(rs != null)
          rs.close();
       if(pstm != null)
          pstm.close();

       //recalcula os níveis:
       for (int i=0; i < vet_MENU_CDG_MENU.size(); i++)
       {  int Nivel = calculaNivel(""+vet_MENU_CDG_MENU.elementAt(i), ""+vet_MENU_CDG_MENU_PAI.elementAt(i));
          vet_MENU_NIVEL.setElementAt(""+Nivel, i);
       }
       //ordena os vetores por nível:
       menuordenanivel();
       //gera vetor contendo os índices ordenados:
       menuordena();

       /*---ini------- CARREGANDO OS ATRIBUTOS ACESSIVEIS ----------------*/
       if(menulogado)
       {  pstm2 = (PreparedStatement)iconexaobean.getConnection().prepareStatement(
" SELECT PA.NOME_ATRIBUTO, PA.TIPO_ACESSO, " +
"        M.CDG_MENU, M.EXECUTAVEL" +
"   FROM COM_ACES_USUARIO_PERFIL UP, COM_ACES_PERFIL P, COM_ACES_PERFIL_MENU PM, COM_ACES_PERFIL_ATRIBUTOS PA," +
"        COM_ACES_MENU M, COM_ACES_ATRIBUTOS ATT" +
"  WHERE UP.CDG_USUR = :1" +
"    AND UP.CDG_PERFIL = P.CDG_PERFIL" +
"    AND P.CDG_PERFIL = PM.CDG_PERFIL" +
"    AND P.CDG_PERFIL = PA.CDG_PERFIL" +
"    AND SYSDATE BETWEEN PM.DT_INICIO AND PM.DT_FIM" +
"    AND PM.CDG_MENU = M.CDG_MENU" +
"    AND SYSDATE BETWEEN M.DT_INICIO AND M.DT_FIM" +
"    AND PA.CDG_MENU = M.CDG_MENU" +
"    AND M.CDG_MENU = ATT.CDG_MENU" +
"    AND PA.NOME_ATRIBUTO = ATT.NOME_ATRIBUTO"
                  ,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
          pstm2.setString( 1, ""+UtilBean.session_getValue(session, "COM_CDG_USUR") );
       }
       else
       {  pstm = (PreparedStatement)iconexaobean.getConnection().prepareStatement(
"select * from dual where dummy = 'CHRIS' "  ,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
       }
       rs2 = (ResultSet) pstm2.executeQuery();

       while(rs2.next())
       {
          vet_ATT_NOME_ATRIBUTO.addElement( rs2.getString("NOME_ATRIBUTO") );
          vet_ATT_TIPO_ACESSO.addElement( rs2.getString("TIPO_ACESSO") );
          vet_ATT_CDG_MENU.addElement( ""+UtilBean.getVerificaNull(rs2.getString("CDG_MENU")) );
          vet_ATT_EXECUTAVEL.addElement( ""+UtilBean.getVerificaNull(rs2.getString("EXECUTAVEL")) );
       } //fim while next

       if(rs2 != null)
          rs2.close();
       if(pstm2 != null)
          pstm2.close();
       /*---fim------- CARREGANDO OS ATRIBUTOS ACESSIVEIS ----------------*/
       UtilBean.session_putValue(session, "COM_MENUBUILT", "S");
    }

    public void preparaInicioMenuLocal(HttpSession session,
                                       HttpServletRequest request,
                                       HttpServletResponse response,
                                       JspWriter out)
           throws java.io.IOException, Exception
    {
       destroiMenuAtual();
       //regContador = 0;
       //regNumMenus = -1;
       menuLocalHeader = "";

       if(request.getParameter("mn") != null)
          menuAtual = request.getParameter("mn");
       else
          menuAtual = "";

       if(menuAtual.equals(""))
       {  for (int i=0; i < vet_MENU_CDG_MENU.size(); i++)
          {  // carrega as subopcoes da opcao atual
             if(vet_APP_EXECUTAVEL.elementAt(i).toString().equals( UtilBean.getJSP(request) ))
             {
//String nada1 = UtilBean.getJSP(request);
//String nada2 = vet_APP_EXECUTAVEL.elementAt(i).toString();
//String nada3 = ""+vet_MENU_CDG_MENU_PAI.elementAt(i);
                //menuAtual = getMENU_CDG_MENU_PAI( vet_MENU_CDG_MENU_PAI.elementAt(i));
                menuAtual = ""+vet_MENU_CDG_MENU_PAI.elementAt(i);
             }
          }
       }
       menuLocalHeader = getMENU_DESCR_RESUMIDA(menuAtual);

       for (int i=0; i < vet_MENU_CDG_MENU.size(); i++)
       {  // carrega as subopcoes da opcao atual
          if(vet_MENU_CDG_MENU_PAI.elementAt(i).toString().equals( menuAtual ))
          {
              vet_ATUAL_MENU_CDG_MENU.addElement( vet_MENU_CDG_MENU.elementAt(i));
              vet_ATUAL_PERFIL_CDG_PERFIL.addElement( vet_PERFIL_CDG_PERFIL.elementAt(i));
              vet_ATUAL_MENU_DESCR_RESUMIDA.addElement( vet_MENU_DESCR_RESUMIDA.elementAt(i));
              vet_ATUAL_MENU_IN_BLOQUEADO.addElement( vet_MENU_IN_BLOQUEADO.elementAt(i));
              vet_ATUAL_MENU_IN_RASTREAVEL.addElement( vet_MENU_IN_RASTREAVEL.elementAt(i));
              vet_ATUAL_MENU_CDG_MENU_PAI.addElement( vet_MENU_CDG_MENU_PAI.elementAt(i));
              vet_ATUAL_PM_TIPO_ACESSO.addElement( vet_PM_TIPO_ACESSO.elementAt(i));
              vet_ATUAL_MENU_IN_VOLTAR.addElement( vet_MENU_IN_VOLTAR.elementAt(i));
              vet_ATUAL_MENU_IN_VISIVEL.addElement( vet_MENU_IN_VISIVEL.elementAt(i));
              vet_ATUAL_MENU_IN_POPUP.addElement( vet_MENU_IN_POPUP.elementAt(i));
              vet_ATUAL_PM_ORDEM.addElement( vet_PM_ORDEM.elementAt(i));
              vet_ATUAL_MENU_NIVEL.addElement( vet_MENU_NIVEL.elementAt(i));

              vet_ATUAL_APP_EXECUTAVEL.addElement( vet_APP_EXECUTAVEL.elementAt(i));
              vet_ATUAL_APP_URL_HELP.addElement( vet_APP_URL_HELP.elementAt(i));
              regNumMenus++;
          }
       }
    }
    public boolean preencheMenuLocal(HttpSession session)
           throws java.io.IOException, Exception
    {
       if(regContador > regNumMenus)
          return false;

       if(vet_ATUAL_MENU_IN_VISIVEL.elementAt(regContador).toString().equals("N"))
       {
          regContador++;
          return preencheMenuLocal(session);
       }

       if(vet_ATUAL_MENU_IN_VOLTAR.elementAt(regContador).toString().equals("S"))
       { // é para voltar...
         if(vet_ATUAL_APP_EXECUTAVEL.elementAt(regContador).toString().equals(""))
         {  // nao é aplicacao, é menu...
            if(vet_ATUAL_MENU_IN_BLOQUEADO.elementAt(regContador).toString().equals("N"))
               menuLocal = "<a class=\"semsub1\" HREF=\""
                         + UtilBean.montaURL(session, "menu.jsp", "mn=" + getMENU_CDG_MENU_PAI( vet_ATUAL_MENU_CDG_MENU_PAI.elementAt(regContador)) )
                         + "\"><b>"
                         + vet_ATUAL_MENU_DESCR_RESUMIDA.elementAt(regContador)
                         + "</b></a>";
            else
               menuLocal = "<b>"
                         + vet_ATUAL_MENU_DESCR_RESUMIDA.elementAt(regContador)
                         + "</b>";
         }
         else
         {  // é aplicacao
            if(vet_ATUAL_MENU_IN_BLOQUEADO.elementAt(regContador).toString().equals("N"))
               menuLocal = "<a class=\"semsub1\" HREF=\""
                         + UtilBean.montaURL(session, vet_ATUAL_APP_EXECUTAVEL.elementAt(regContador).toString(), "")
                         + "\"><b>"
                         + vet_ATUAL_MENU_DESCR_RESUMIDA.elementAt(regContador)
                         + "</b></a>";
            else
               menuLocal = "<b>"
                         + vet_ATUAL_MENU_DESCR_RESUMIDA.elementAt(regContador)
                         + "</b>";
         }
       }
       else
       { // não é para voltar...
         if(vet_ATUAL_APP_EXECUTAVEL.elementAt(regContador).toString().equals(""))
         {  // nao é aplicacao, é menu...
            if(vet_ATUAL_MENU_IN_BLOQUEADO.elementAt(regContador).toString().equals("N"))
               menuLocal = "<a class=\"semsub1\" HREF=\""
                         + UtilBean.montaURL(session, "menu.jsp", "mn=" + vet_ATUAL_MENU_CDG_MENU.elementAt(regContador) )
                         + "\"><b>"
                         + vet_ATUAL_MENU_DESCR_RESUMIDA.elementAt(regContador)
                         + "</b></a>";
            else
               menuLocal = "<b>"
                         + vet_ATUAL_MENU_DESCR_RESUMIDA.elementAt(regContador)
                         + "</b>";
         }
         else
         {  // é aplicacao
            if(vet_ATUAL_MENU_IN_BLOQUEADO.elementAt(regContador).toString().equals("N"))
               menuLocal = "<a class=\"semsub1\" HREF=\""
                         + UtilBean.montaURL(session, vet_ATUAL_APP_EXECUTAVEL.elementAt(regContador).toString(), "" )
                         + "\"><b>"
                         + vet_ATUAL_MENU_DESCR_RESUMIDA.elementAt(regContador)
                         + "</b></a>";
            else
               menuLocal = "<b>"
                         + vet_ATUAL_MENU_DESCR_RESUMIDA.elementAt(regContador)
                         + "</b>";
         }
       }
       regContador++;
       return true;
    }

    String getMENU_DESCR_RESUMIDA(Object pCDG_MENU)
    {
       if(pCDG_MENU == null)
          return "";
       if(pCDG_MENU.toString().equals(""))
          return "";

       for (int i=0; i < vet_MENU_CDG_MENU.size(); i++)
       {  if(vet_MENU_CDG_MENU.elementAt(i).toString().equals( pCDG_MENU.toString() ))
          {  return ""+vet_MENU_DESCR_RESUMIDA.elementAt(i);
          }
       }
       return "";
    }
    String getMENU_CDG_MENU_PAI(Object pCDG_MENU)
    {
       if(pCDG_MENU == null)
          return "";
       if(pCDG_MENU.toString().equals(""))
          return "";

       for (int i=0; i < vet_MENU_CDG_MENU.size(); i++)
       {  if(vet_MENU_CDG_MENU.elementAt(i).toString().equals( pCDG_MENU.toString() ))
          {  return ""+vet_MENU_CDG_MENU_PAI.elementAt(i);
          }
       }
       return "";
    }
    int calculaNivel( String pCDG_MENU, String pCDG_MENU_PAI )
    {  //devo pegar todos os "pais" para saber o nivel...
       if(pCDG_MENU_PAI.equals(""))
          return 0;

       String auxCDG_MENU_PAI = pCDG_MENU_PAI;
       int contador = 0;
       while( (! auxCDG_MENU_PAI.equals("")) && (contador < 9999) )
       {  contador++;
          auxCDG_MENU_PAI = getMENU_CDG_MENU_PAI(auxCDG_MENU_PAI);
       }
       return contador;
    }
    void menuordenanivel()
    {
       int maiornivel = 0;
       for (int i=0; i < vet_MENU_NIVEL.size(); i++)
       {  if( Integer.parseInt(""+vet_MENU_NIVEL.elementAt(i)) > maiornivel )
             maiornivel = Integer.parseInt(""+vet_MENU_NIVEL.elementAt(i));
       }
       Vector aux_vet_MENU_CDG_MENU          = new Vector();
       Vector aux_vet_PERFIL_CDG_PERFIL      = new Vector();
       Vector aux_vet_MENU_DESCR_RESUMIDA    = new Vector();
       Vector aux_vet_MENU_IN_BLOQUEADO      = new Vector();
       Vector aux_vet_MENU_IN_RASTREAVEL     = new Vector();
       Vector aux_vet_MENU_CDG_MENU_PAI      = new Vector();
       Vector aux_vet_PM_TIPO_ACESSO         = new Vector();
       Vector aux_vet_MENU_IN_VOLTAR         = new Vector();
       Vector aux_vet_MENU_IN_VISIVEL        = new Vector();
       Vector aux_vet_MENU_IN_POPUP          = new Vector();
       Vector aux_vet_PM_ORDEM               = new Vector();
       Vector aux_vet_MENU_NIVEL             = new Vector();
       Vector aux_vet_MENU_IMPRIMIU          = new Vector();

       Vector aux_vet_APP_EXECUTAVEL         = new Vector();
       Vector aux_vet_APP_URL_HELP           = new Vector();
       //tira um backup dos arrays:
//System.out.print("tam=" + vet_MENU_CDG_MENU.size());
       for (int i=0; i < vet_MENU_CDG_MENU.size(); i++)
       {  aux_vet_MENU_CDG_MENU.addElement(vet_MENU_CDG_MENU.elementAt(i));
          aux_vet_PERFIL_CDG_PERFIL.addElement(vet_PERFIL_CDG_PERFIL.elementAt(i));
          aux_vet_MENU_DESCR_RESUMIDA.addElement(vet_MENU_DESCR_RESUMIDA.elementAt(i));
          aux_vet_MENU_IN_BLOQUEADO.addElement(vet_MENU_IN_BLOQUEADO.elementAt(i));
          aux_vet_MENU_IN_RASTREAVEL.addElement(vet_MENU_IN_RASTREAVEL.elementAt(i));
          aux_vet_MENU_CDG_MENU_PAI.addElement(vet_MENU_CDG_MENU_PAI.elementAt(i));
          aux_vet_PM_TIPO_ACESSO.addElement(vet_PM_TIPO_ACESSO.elementAt(i));
          aux_vet_MENU_IN_VOLTAR.addElement(vet_MENU_IN_VOLTAR.elementAt(i));
          aux_vet_MENU_IN_VISIVEL.addElement(vet_MENU_IN_VISIVEL.elementAt(i));
          aux_vet_MENU_IN_POPUP.addElement(vet_MENU_IN_POPUP.elementAt(i));
          aux_vet_PM_ORDEM.addElement(vet_PM_ORDEM.elementAt(i));
          aux_vet_MENU_NIVEL.addElement(vet_MENU_NIVEL.elementAt(i));
          aux_vet_MENU_IMPRIMIU.addElement(vet_MENU_IMPRIMIU.elementAt(i));

          aux_vet_APP_EXECUTAVEL.addElement(vet_APP_EXECUTAVEL.elementAt(i));
          aux_vet_APP_URL_HELP.addElement(vet_APP_URL_HELP.elementAt(i));
       }
       vet_MENU_CDG_MENU.removeAllElements();
       vet_PERFIL_CDG_PERFIL.removeAllElements();
       vet_MENU_DESCR_RESUMIDA.removeAllElements();
       vet_MENU_IN_BLOQUEADO.removeAllElements();
       vet_MENU_IN_RASTREAVEL.removeAllElements();
       vet_MENU_CDG_MENU_PAI.removeAllElements();
       vet_PM_TIPO_ACESSO.removeAllElements();
       vet_MENU_IN_VOLTAR.removeAllElements();
       vet_MENU_IN_VISIVEL.removeAllElements();
       vet_MENU_IN_POPUP.removeAllElements();
       vet_PM_ORDEM.removeAllElements();
       vet_MENU_NIVEL.removeAllElements();
       vet_MENU_IMPRIMIU.removeAllElements();

       vet_APP_EXECUTAVEL.removeAllElements();
       vet_APP_URL_HELP.removeAllElements();

       //ordena pelos níveis:
       for(int nivel=0; nivel <= maiornivel; nivel++)
       {  for (int i=0; i < aux_vet_MENU_CDG_MENU.size(); i++)
          {  if(Integer.parseInt(""+aux_vet_MENU_NIVEL.elementAt(i)) == nivel)
             {
                vet_MENU_CDG_MENU.addElement(aux_vet_MENU_CDG_MENU.elementAt(i));
                vet_PERFIL_CDG_PERFIL.addElement(aux_vet_PERFIL_CDG_PERFIL.elementAt(i));
                vet_MENU_DESCR_RESUMIDA.addElement(aux_vet_MENU_DESCR_RESUMIDA.elementAt(i));
                vet_MENU_IN_BLOQUEADO.addElement(aux_vet_MENU_IN_BLOQUEADO.elementAt(i));
                vet_MENU_IN_RASTREAVEL.addElement(aux_vet_MENU_IN_RASTREAVEL.elementAt(i));
                vet_MENU_CDG_MENU_PAI.addElement(aux_vet_MENU_CDG_MENU_PAI.elementAt(i));
                vet_PM_TIPO_ACESSO.addElement(aux_vet_PM_TIPO_ACESSO.elementAt(i));
                vet_MENU_IN_VOLTAR.addElement(aux_vet_MENU_IN_VOLTAR.elementAt(i));
                vet_MENU_IN_VISIVEL.addElement(aux_vet_MENU_IN_VISIVEL.elementAt(i));
                vet_MENU_IN_POPUP.addElement(aux_vet_MENU_IN_POPUP.elementAt(i));
                vet_PM_ORDEM.addElement(aux_vet_PM_ORDEM.elementAt(i));
                vet_MENU_NIVEL.addElement(aux_vet_MENU_NIVEL.elementAt(i));
                vet_MENU_IMPRIMIU.addElement(aux_vet_MENU_IMPRIMIU.elementAt(i));

                vet_APP_EXECUTAVEL.addElement(aux_vet_APP_EXECUTAVEL.elementAt(i));
                vet_APP_URL_HELP.addElement(aux_vet_APP_URL_HELP.elementAt(i));
             }
          }
       }
//System.out.print("tam=" + vet_MENU_CDG_MENU.size());

       aux_vet_MENU_CDG_MENU.removeAllElements();
       aux_vet_PERFIL_CDG_PERFIL.removeAllElements();
       aux_vet_MENU_DESCR_RESUMIDA.removeAllElements();
       aux_vet_MENU_IN_BLOQUEADO.removeAllElements();
       aux_vet_MENU_IN_RASTREAVEL.removeAllElements();
       aux_vet_MENU_CDG_MENU_PAI.removeAllElements();
       aux_vet_PM_TIPO_ACESSO.removeAllElements();
       aux_vet_MENU_IN_VOLTAR.removeAllElements();
       aux_vet_MENU_IN_VISIVEL.removeAllElements();
       aux_vet_MENU_IN_POPUP.removeAllElements();
       aux_vet_PM_ORDEM.removeAllElements();
       aux_vet_MENU_NIVEL.removeAllElements();
       aux_vet_MENU_IMPRIMIU.removeAllElements();

       aux_vet_APP_EXECUTAVEL.removeAllElements();
       aux_vet_APP_URL_HELP.removeAllElements();
    }

    void menuordena()
    {  int pos;
       vet_ORDEM.removeAllElements();

       for (int i=0; i < vet_MENU_CDG_MENU.size(); i++)
       {
          pos = getPosicao(i);
          insere(pos, i);
       }
    }
    int getPosicao(int atual)
    {
       if(vet_ORDEM.size() == 0)
          return 0;

       int pos = -1;
       int elemento = 0;
       String sCDG_MENU_PAI = vet_MENU_CDG_MENU_PAI.elementAt(atual).toString();
       String auxCDG_MENU_PAI = "";

       //procura pelo menu pai...
//System.out.print("menu=" + vet_MENU_DESCR_RESUMIDA.elementAt(atual));
       for (int j=0; j < vet_ORDEM.size(); j++)
       {
          elemento = Integer.parseInt(""+vet_ORDEM.elementAt(j));
          auxCDG_MENU_PAI = ""+vet_MENU_CDG_MENU.elementAt(elemento);
          if(auxCDG_MENU_PAI.equals(sCDG_MENU_PAI))
             pos = j;
       }
       if(pos == -1)
          return vet_ORDEM.size();

       elemento = Integer.parseInt(""+vet_ORDEM.elementAt(pos));
       String sMENU = ""+vet_MENU_CDG_MENU.elementAt(elemento);
       pos++;
       int aux = 0;
       boolean quebrou = false;
       while( (pos < vet_ORDEM.size()) && (! quebrou))
       {  aux = Integer.parseInt(""+vet_ORDEM.elementAt(pos));
          if(vet_MENU_CDG_MENU_PAI.elementAt(aux).toString().equals(sMENU))
             pos++;
          else
             quebrou = true;
       }
       return pos;
    }
    void insere(int pos, int atual)
    {
       if(pos >= vet_ORDEM.size())
          vet_ORDEM.addElement( ""+atual );
       else
       {
          int varre = vet_ORDEM.size() - 1;
          vet_ORDEM.addElement( vet_ORDEM.elementAt(varre) );
          while( varre > pos )
          {
             vet_ORDEM.setElementAt( vet_ORDEM.elementAt(varre-1), varre );
             varre--;
          }
          vet_ORDEM.setElementAt( ""+atual, varre );
       }
    }

    public String montaMenuTop(HttpSession session,
                               HttpServletRequest request,
                               HttpServletResponse response)
           throws Exception
    {  String retorno = "";
       String espacos = "";
       /* ---
       for (int i=0; i < vet_MENU_CDG_MENU.size(); i++)
       {  if(vet_MENU_IN_VISIVEL.elementAt(i).toString().equals("S"))
          {  espacos = "";
             for(int j=0; j < Integer.parseInt(""+vet_MENU_NIVEL.elementAt(i)); j++)
                espacos += ".";
             retorno += "<BR>" + espacos + vet_MENU_DESCR_RESUMIDA.elementAt(i) + "-"
                               + vet_APP_EXECUTAVEL.elementAt(i);
          }
       }
       retorno += "<BR><HR>";
       --- */
       try
       {
         int elemento = 0;
         for (int i=0; i < vet_ORDEM.size(); i++)
         {  elemento = Integer.parseInt(""+vet_ORDEM.elementAt(i));
            if(vet_MENU_IN_VISIVEL.elementAt(elemento).toString().equals("S"))
            {  espacos = "";
               for(int j=0; j < Integer.parseInt(""+vet_MENU_NIVEL.elementAt(elemento)); j++)
                  espacos += ">";
               if(UtilBean.session_getValue(session, "COM_TPO_USUARIO") != null)
               {  if(UtilBean.session_getValue(session, "COM_TPO_USUARIO").toString().equals("I"))
                     espacos = espacos; //espacos += "#";
                  else
                     espacos += "^";
               }
               else
                  espacos += "^";
               retorno += "<BR>" + espacos + vet_MENU_DESCR_RESUMIDA.elementAt(elemento);
            }
         }
       }
       //catch (SQLException oex) { throw new Exception(""+oex); }
       //catch (SQLException exs) { throw new Exception(""+exs); }
       //catch (java.io.IOException exi) { throw new Exception(""+exi); }
       catch (Exception ex) { throw ex; }
       return retorno;
    }

    public String montaMenuDinamico(HttpSession session,
                                    HttpServletRequest request,
                                    HttpServletResponse response)
           throws Exception
    {  String retorno = "";
       String espacos = "";
       /* ---
       for (int i=0; i < vet_MENU_CDG_MENU.size(); i++)
       {  if(vet_MENU_IN_VISIVEL.elementAt(i).toString().equals("S"))
          {  espacos = "";
             for(int j=0; j < Integer.parseInt(""+vet_MENU_NIVEL.elementAt(i)); j++)
                espacos += ".";
             retorno += "<BR>" + espacos + vet_MENU_DESCR_RESUMIDA.elementAt(i) + "-"
                               + vet_APP_EXECUTAVEL.elementAt(i);
          }
       }
       retorno += "<BR><HR>";
       --- */
       try
       {
         int elemento = 0;
         for (int i=0; i < vet_ORDEM.size(); i++)
         {  elemento = Integer.parseInt(""+vet_ORDEM.elementAt(i));
            if(vet_MENU_IN_VISIVEL.elementAt(elemento).toString().equals("S"))
            {  espacos = "";
               for(int j=0; j < Integer.parseInt(""+vet_MENU_NIVEL.elementAt(elemento)); j++)
                  espacos += ">";
               if(UtilBean.session_getValue(session, "COM_TPO_USUARIO") != null)
               {  if(UtilBean.session_getValue(session, "COM_TPO_USUARIO").toString().equals("I"))
                     espacos = espacos; //espacos += "#";
                  else
                     espacos += "^";
               }
               else
                  espacos += "^";

               if(! vet_MENU_IN_VOLTAR.elementAt(elemento).toString().equals("S"))
               { if(vet_APP_EXECUTAVEL.elementAt(elemento).toString().equals(""))
                 {  // nao é aplicacao, é menu...
                    //if(vet_MENU_IN_BLOQUEADO.elementAt(elemento).toString().equals("N"))
                    //  if(retorno.length() > 0)
                    //     retorno += ", '" + espacos + vet_MENU_DESCR_RESUMIDA.elementAt(elemento)
                    //                      + "', '" + UtilBean.montaURL(session, "menu.jsp", "mn=" + vet_MENU_CDG_MENU.elementAt(elemento) ) + "'";
                    //  else
                    //     retorno += " '" + espacos + vet_MENU_DESCR_RESUMIDA.elementAt(elemento)
                    //                     + "', '" + UtilBean.montaURL(session, "menu.jsp", "mn=" + vet_MENU_CDG_MENU.elementAt(elemento) ) + "'";
                    //else
                      if(retorno.length() > 0)
                         retorno += ", '" + espacos + vet_MENU_DESCR_RESUMIDA.elementAt(elemento)
                                          + "', ''";
                      else
                         retorno += " '" + espacos + vet_MENU_DESCR_RESUMIDA.elementAt(elemento)
                                         + "', ''";
                 }
                 else
                 {  // é aplicacao
                    if(vet_MENU_IN_BLOQUEADO.elementAt(elemento).toString().equals("N"))
                      if(retorno.length() > 0)
                         retorno += ", '" + espacos + vet_MENU_DESCR_RESUMIDA.elementAt(elemento)
                                          + "', '" + UtilBean.montaURL(session, vet_APP_EXECUTAVEL.elementAt(elemento).toString(), "" ) + "'";
                      else
                         retorno += " '" + espacos + vet_MENU_DESCR_RESUMIDA.elementAt(elemento)
                                         + "', '" + UtilBean.montaURL(session, vet_APP_EXECUTAVEL.elementAt(elemento).toString(), "" ) + "'";
                    else
                      if(retorno.length() > 0)
                         retorno += ", '" + espacos + vet_MENU_DESCR_RESUMIDA.elementAt(elemento)
                                          + "', ''";
                      else
                         retorno += " '" + espacos + vet_MENU_DESCR_RESUMIDA.elementAt(elemento)
                                         + "', ''";
                 }
               }
            }
         }
       }
       //catch (SQLException oex) { throw new Exception(""+oex); }
       //catch (SQLException exs) { throw new Exception(""+exs); }
       //catch (java.io.IOException exi) { throw new Exception(""+exi); }
       catch (Exception ex) { throw ex; }
       if(retorno.equals(""))
          retorno = " menuinit(' ', ''); ";
       else
          retorno = " menuinit( " + retorno + " ); ";
       return retorno;
    }

    public String montaMenuHead()
    {
       int maiornivel = 0;
       for (int i=0; i < vet_MENU_NIVEL.size(); i++)
       {  if( Integer.parseInt(""+vet_MENU_NIVEL.elementAt(i)) > maiornivel )
             maiornivel = Integer.parseInt(""+vet_MENU_NIVEL.elementAt(i));
       }
       zeraImprimiuPai();
       String retorno = "";
       int elemento = 0;
       for (int nivel=maiornivel; nivel >= 0; nivel--)
       {  for (int i=0; i < vet_ORDEM.size(); i++)
          {  elemento = Integer.parseInt(""+vet_ORDEM.elementAt(i));
             if(Integer.parseInt(""+vet_MENU_NIVEL.elementAt(elemento)) == nivel)
             {  if(vet_MENU_IN_VISIVEL.elementAt(elemento).toString().equals("S"))
                {
                   if(! vet_MENU_CDG_MENU_PAI.elementAt(elemento).toString().equals(""))
                   {  //possui Pai
                      if(! jaImprimiPai(elemento))
                      {  //se ainda nao imprimiu o pai
                         retorno += " window.topMenu" + vet_MENU_CDG_MENU_PAI.elementAt(elemento)
                                                      + " = new Menu(\"" + getMENU_DESCR_RESUMIDA(vet_MENU_CDG_MENU_PAI.elementAt(elemento)) + "\"); ";
                         setaImprimiuPai(elemento);
                      }
                      if(! possuiFilho(elemento))
                      {
                         retorno += " topMenu" + vet_MENU_CDG_MENU_PAI.elementAt(elemento)
                                         + ".addMenuItem(\"" + vet_MENU_DESCR_RESUMIDA.elementAt(elemento)
                                         + "\", \"document.location='" + vet_APP_EXECUTAVEL.elementAt(elemento) + "'\"); ";
                         vet_MENU_IMPRIMIU.setElementAt("S", elemento);
                      }
                      else
                      {
                         if(vet_MENU_IMPRIMIU.elementAt(elemento).toString().equals("N"))
                         {
                            retorno += " window.topMenu" + vet_MENU_CDG_MENU.elementAt(elemento)
                                                      + " = new Menu(\"" + getMENU_DESCR_RESUMIDA(vet_MENU_CDG_MENU.elementAt(elemento)) + "\"); ";
                            vet_MENU_IMPRIMIU.setElementAt("S", elemento);
                         }
                         retorno += " topMenu" + vet_MENU_CDG_MENU_PAI.elementAt(elemento)
                                               + ".addMenuItem(topMenu" + vet_MENU_CDG_MENU.elementAt(elemento) + "); ";
                      }
                   }
                }
             }
          }
       }
       retorno += " window.topMenu0 = new Menu(\"ROOT\"); ";
       for (int i=0; i < vet_ORDEM.size(); i++)
       {  elemento = Integer.parseInt(""+vet_ORDEM.elementAt(i));
          if(vet_MENU_IN_VISIVEL.elementAt(elemento).toString().equals("S"))
          {
             if(vet_MENU_CDG_MENU_PAI.elementAt(elemento).toString().equals(""))
             {  //nao possui pai
                if(possuiFilho(elemento))
                {
                   retorno += " topMenu0.addMenuItem(topMenu" + vet_MENU_CDG_MENU.elementAt(elemento) + "); ";
                }
                else
                {
                   retorno += " topMenu0.addMenuItem(\"" + vet_MENU_DESCR_RESUMIDA.elementAt(elemento)
                                         + "\", \"" + vet_APP_EXECUTAVEL.elementAt(elemento) + "\"); ";
                }
             }
          }
       }
       retorno = " window.topMenu0 = new Menu(\"ROOT\"); "; //??? nao faz nada
       return retorno;
    }
    boolean jaImprimiPai(int elemento)
    {  String sMENUPAI = vet_MENU_CDG_MENU_PAI.elementAt(elemento).toString();
       for (int i=0; i < vet_MENU_CDG_MENU.size(); i++)
       {  if(vet_MENU_CDG_MENU.elementAt(i).toString().equals(sMENUPAI))
             if(vet_MENU_IMPRIMIU.elementAt(i).toString().equals("S"))
                return true;
       }
       return false;
    }
    void setaImprimiuPai(int elemento)
    {  String sMENUPAI = vet_MENU_CDG_MENU_PAI.elementAt(elemento).toString();
       for (int i=0; i < vet_MENU_CDG_MENU.size(); i++)
       {  if(vet_MENU_CDG_MENU.elementAt(i).toString().equals(sMENUPAI))
             vet_MENU_IMPRIMIU.setElementAt("S", i);
       }
    }
    void zeraImprimiuPai()
    {  for (int i=0; i < vet_MENU_CDG_MENU.size(); i++)
       {  vet_MENU_IMPRIMIU.setElementAt("N", i);
       }
    }

    boolean possuiFilho(int elemento)
    {  String sMENU = vet_MENU_CDG_MENU.elementAt(elemento).toString();
       for (int i=0; i < vet_MENU_CDG_MENU.size(); i++)
       {  if(vet_MENU_CDG_MENU_PAI.elementAt(i).toString().equals(sMENU))
             return true;
       }
       return false;
    }

    void destroiMenuAtual()
    {   menuTop = "";
        menuLocal = "";
        menuLocalHeader = "";
        menuAtual = "";
        IN_RASTREAVEL = "";
        regContador = 0;
        regNumMenus = -1;
        vet_ATUAL_MENU_CDG_MENU.removeAllElements();
        vet_ATUAL_PERFIL_CDG_PERFIL.removeAllElements();
        vet_ATUAL_MENU_DESCR_RESUMIDA.removeAllElements();
        vet_ATUAL_MENU_IN_BLOQUEADO.removeAllElements();
        vet_ATUAL_MENU_IN_RASTREAVEL.removeAllElements();
        vet_ATUAL_MENU_CDG_MENU_PAI.removeAllElements();
        vet_ATUAL_PM_TIPO_ACESSO.removeAllElements();
        vet_ATUAL_MENU_IN_VOLTAR.removeAllElements();
        vet_ATUAL_MENU_IN_VISIVEL.removeAllElements();
        vet_ATUAL_MENU_IN_POPUP.removeAllElements();
        vet_ATUAL_PM_ORDEM.removeAllElements();
        vet_ATUAL_MENU_NIVEL.removeAllElements();

        vet_ATUAL_APP_EXECUTAVEL.removeAllElements();
        vet_ATUAL_APP_URL_HELP.removeAllElements();
    }
    public void destroiMenu()
    {   menuTop = "";
        menuLocal = "";
        menuLocalHeader = "";
        menuAtual = "";
        IN_RASTREAVEL = "";
        regContador = 0;
        regNumMenus = -1;
        vet_ORDEM.removeAllElements();

        vet_MENU_CDG_MENU.removeAllElements();
        vet_PERFIL_CDG_PERFIL.removeAllElements();
        vet_MENU_DESCR_RESUMIDA.removeAllElements();
        vet_MENU_IN_BLOQUEADO.removeAllElements();
        vet_MENU_IN_RASTREAVEL.removeAllElements();
        vet_MENU_CDG_MENU_PAI.removeAllElements();
        vet_PM_TIPO_ACESSO.removeAllElements();
        vet_MENU_IN_VOLTAR.removeAllElements();
        vet_MENU_IN_VISIVEL.removeAllElements();
        vet_MENU_IN_POPUP.removeAllElements();
        vet_PM_ORDEM.removeAllElements();
        vet_MENU_NIVEL.removeAllElements();
        vet_MENU_IMPRIMIU.removeAllElements();

        vet_APP_EXECUTAVEL.removeAllElements();
        vet_APP_URL_HELP.removeAllElements();

        vet_ATT_NOME_ATRIBUTO.removeAllElements();
        vet_ATT_TIPO_ACESSO.removeAllElements();
        vet_ATT_CDG_MENU.removeAllElements();
        vet_ATT_EXECUTAVEL.removeAllElements();

        vet_ATUAL_MENU_CDG_MENU.removeAllElements();
        vet_ATUAL_PERFIL_CDG_PERFIL.removeAllElements();
        vet_ATUAL_MENU_DESCR_RESUMIDA.removeAllElements();
        vet_ATUAL_MENU_IN_BLOQUEADO.removeAllElements();
        vet_ATUAL_MENU_IN_RASTREAVEL.removeAllElements();
        vet_ATUAL_MENU_CDG_MENU_PAI.removeAllElements();
        vet_ATUAL_PM_TIPO_ACESSO.removeAllElements();
        vet_ATUAL_MENU_IN_VOLTAR.removeAllElements();
        vet_ATUAL_MENU_IN_VISIVEL.removeAllElements();
        vet_ATUAL_MENU_IN_POPUP.removeAllElements();
        vet_ATUAL_PM_ORDEM.removeAllElements();
        vet_ATUAL_MENU_NIVEL.removeAllElements();

        vet_ATUAL_APP_EXECUTAVEL.removeAllElements();
        vet_ATUAL_APP_URL_HELP.removeAllElements();
    }
    public String getTipoAcesso( String pTELA_NM_TELA )
    {  String retorno = "";
       IN_RASTREAVEL = "";
       if(pTELA_NM_TELA == null)
          return "";
       if(pTELA_NM_TELA.equals(""))
          return "";

       for (int i=0; i < vet_MENU_CDG_MENU.size(); i++)
       {  // carrega as subopcoes da opcao atual
          if(vet_APP_EXECUTAVEL.elementAt(i).toString().equals( pTELA_NM_TELA ))
          {
             IN_RASTREAVEL = vet_MENU_IN_RASTREAVEL.elementAt(i).toString();
             if(vet_MENU_IN_BLOQUEADO.elementAt(i).toString().equals("S"))
                retorno = "BLOQUEADO";
             else
                retorno = "" + vet_PM_TIPO_ACESSO.elementAt(i);
          }
       }
       return retorno;
    }
    public String getTipoAcessoCampo( String pTELA_NM_TELA, String pNOME_ATRIBUTO )
    {  String retorno = "T";
       for (int i=0; i < vet_ATT_NOME_ATRIBUTO.size(); i++)
       {  // carrega as subopcoes da opcao atual
          if((vet_ATT_EXECUTAVEL.elementAt(i).toString().equals( pTELA_NM_TELA )) &&
             (vet_ATT_NOME_ATRIBUTO.elementAt(i).toString().equals( pNOME_ATRIBUTO )))
          {
             retorno = "" + vet_ATT_TIPO_ACESSO.elementAt(i);
          }
       }
       return retorno;
    }
    public String getUrlHelp( String pTELA_NM_TELA )
    {  String retorno = "";
       if(pTELA_NM_TELA == null)
          return "";
       if(pTELA_NM_TELA.equals(""))
          return "";

       for (int i=0; i < vet_MENU_CDG_MENU.size(); i++)
       {  // carrega as subopcoes da opcao atual
          if(vet_APP_EXECUTAVEL.elementAt(i).toString().equals( pTELA_NM_TELA ))
          {
             retorno = vet_APP_URL_HELP.elementAt(i).toString();
          }
       }
       return retorno;
    }
    
    //05/02/2002---
    private boolean verificaSQLCMD_HABILITA( ResultSet rs, 
                                             conexaoBean iconexaobean, HttpSession session,
                                             HttpServletRequest request, JspWriter out,
                                             boolean menulogado )
           throws SQLException, java.io.IOException, SQLException, Exception
    {
       if(true) return true; //?????????????????????????????
       boolean retorno = true;
       PreparedStatement pstmSH = null;
       ResultSet rsSH = null;
       try {
         if( (rs.getString("SQLCMD_HABILITA") != null) &&
             (! rs.getString("SQLCMD_HABILITA").equals("")) )
         {
           //SELECT COM_FNC_VERIFICA_CLIENTE_ATIVO( <CDG_SEG>, <CDG_EMP>, <CDG_PES_FJ_CLIE> ) FROM DUAL
           String sSQLH = rs.getString("SQLCMD_HABILITA");
           if(menulogado)
           {  sSQLH = UtilBean.substitui( sSQLH, "<CDG_SEG>", ""+UtilBean.session_getValue(session, "COM_CDG_SEG") );
              sSQLH = UtilBean.substitui( sSQLH, "<CDG_EMP>", ""+UtilBean.session_getValue(session, "COM_CDG_EMP") );
              sSQLH = UtilBean.substitui( sSQLH, "<CDG_PES_FJ_CLIE>", ""+UtilBean.session_getValue(session, "COM_CDG_PES_FJ_CLIE") );
           }
           pstmSH = (PreparedStatement)iconexaobean.getConnection().prepareStatement(
                     sSQLH, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
           rsSH = (ResultSet) pstmSH.executeQuery();
           if(rsSH.next())
              if( (rsSH.getString(1) != null) &&
                  (rsSH.getString(1).equals("S")) )
                 retorno = true;
              else
                 retorno = false;
           else
              retorno = false;

           if(rsSH != null)   { rsSH.close();   rsSH = null; }
           if(pstmSH != null) { pstmSH.close(); pstmSH = null; }
         }
       }
       catch (Exception ex) {
         if(rsSH != null)   { rsSH.close();   rsSH = null; }
         if(pstmSH != null) { pstmSH.close(); pstmSH = null; }
       	 retorno = false;
       }
       return (retorno);
    }
    //05/02/2002---

} //fim class

