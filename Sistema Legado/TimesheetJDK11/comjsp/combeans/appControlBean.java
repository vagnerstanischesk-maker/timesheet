package combeans;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.text.*;
import javax.servlet.http.*;

public class appControlBean
       //extends Object implements oracle.jsp.event.JspScopeListener
{

   Vector vet_session    = new Vector();
   Vector vet_conexao    = new Vector();
   Vector vet_usuario    = new Vector();
   Vector vet_sessionid  = new Vector();
   Vector vet_jsp        = new Vector();

  public String getVersao()
  {   return("04/12/2001");
  }

   public void add( HttpSession p_session,
               conexaoBean p_iconexaobean,
               String p_usuario,
               String p_jsp )
   {
      if(p_session == null)
      {  vet_session.addElement("");
         vet_sessionid.addElement("");
      }
      else
      {  vet_session.addElement( p_session );
         vet_sessionid.addElement( p_session.getId() );
      }
      vet_conexao.addElement(p_iconexaobean);
      vet_usuario.addElement(p_usuario);
      vet_jsp.addElement( p_jsp );
//System.out.println("Adicionei na aplicacao");
   }

   public boolean remove( HttpSession p_session,
                          conexaoBean p_iconexaobean,
                          String p_usuario )
   {
      int ind = 0;
      int tam = vet_session.size();
      while(ind < tam)
      {
         if(p_session == null)
         {
           if(vet_conexao.elementAt(ind) == p_iconexaobean)
           {
              vet_session.removeElementAt(ind);
              vet_conexao.removeElementAt(ind);
              vet_usuario.removeElementAt(ind);
              vet_sessionid.removeElementAt(ind);
              vet_jsp.removeElementAt(ind);
//System.out.println("Removi da aplicacao1");
              ind = tam + 1;
              return true;
           }

         }
         else
         {
           if((vet_session.elementAt(ind) == p_session) &&
              (vet_conexao.elementAt(ind) == p_iconexaobean) &&
              (vet_sessionid.elementAt(ind).toString().equals(p_session.getId() )))
           {
              vet_session.removeElementAt(ind);
              vet_conexao.removeElementAt(ind);
              vet_usuario.removeElementAt(ind);
              vet_sessionid.removeElementAt(ind);
              vet_jsp.removeElementAt(ind);
//System.out.println("Removi da aplicacao2");
              ind = tam + 1;
              return true;
           }
         }
         ind++;
      }
      return false;
   }

   public String dumpAplicacao ()
   {
      String retorno = "<BR>";
      String temp = "";
      int ind = 0;
      conexaoBean iconexaobean;
      HttpSession session;

      while(ind < vet_session.size())
      {
         retorno += "<BR>";
         temp = "";
         try {
            if(vet_session.elementAt(ind).toString().equals(""))
            {
               temp = "nula";
            }
            else
            {
               session = (HttpSession)vet_session.elementAt(ind);
               temp = session.getId(); }
            }
         catch (Exception ex) { System.out.println("Erro (1) appControlBean="+ex); }
         retorno += "SessionID Nova=" + temp + "---" + "SessionID Antiga=" + vet_sessionid.elementAt(ind);

         temp = "OK";
         try {
            iconexaobean = (conexaoBean)vet_conexao.elementAt(ind);
            iconexaobean.verifica();
         }
         catch (Exception ex) { temp = ""+ex; }
         retorno += "---ConexaoAtiva=" + temp;

         retorno += "---Usuario=" + vet_usuario.elementAt(ind);

         retorno += "---JSP=" + vet_jsp.elementAt(ind) + ".";

         ind++;
      }

      return retorno;
   }

   //public void outOfScope(oracle.jsp.event.JspScopeEvent ae) {
   //     int scope = ae.getScope();

        //if ((scope == javax.servlet.jsp.PageContext.REQUEST_SCOPE  ||
        //     scope == javax.servlet.jsp.PageContext.PAGE_SCOPE)) {
        //Object args[] = {ae.getApplication(), ae.getContainer()};
        //}

   //     if (scope == javax.servlet.jsp.PageContext.APPLICATION_SCOPE) {
   //        System.out.println("FIM DE ESCOPO APP");
   //     }
   //}

}

