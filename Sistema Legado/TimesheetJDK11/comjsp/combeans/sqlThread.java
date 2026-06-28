/**************************************************************************
Empresa: Triscal
Autor: Christiano Chamma/Alexandre
Data: Janeiro de 2001'

Descrição:
      Classe com funções diversas.

**************************************************************************/
package combeans;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.text.*;
import java.sql.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.sql.Connection;
import oracle.jdbc.*; 
import java.sql.*;


public class sqlThread extends Thread implements Runnable {
   private String sql;
   private appControlBean icontroleapp;
   private HttpServletRequest request;
   private String jsp;

  public sqlThread(String Sql, appControlBean icontroleapp,
             HttpServletRequest request, String jsp ) {
       this.sql = Sql;
       this.icontroleapp = icontroleapp;
       this.request = request;
       this.jsp = jsp;
  }

  public void run() {
     executa();
  }

  private void executa() {
    conexaoBean iconexaobean = null;
    try {
      //while (!Thread.interrupted()) {
      if (!Thread.interrupted()) {
         iconexaobean = new combeans.conexaoBean();
         iconexaobean.execute(icontroleapp, null, request, null, null, jsp);

//System.out.println("ANTES Oracle PS------------"+new java.util.Date());
         PreparedStatement stmt = ( PreparedStatement ) iconexaobean.getConnection().prepareStatement( sql );
//System.out.println("DEPOIS Oracle PS------------"+new java.util.Date());

//System.out.println("ANTES EXECUTE---------"+new java.util.Date());
          stmt.execute();
//System.out.println("DEPOIS EXECUTE--------"+new java.util.Date());
          stmt.close();
      }
    }
    catch (SQLException exsql)
    {  System.out.print("Erro na Thread com_Campanha_Compra.jsp - "+exsql);
    }
    catch (Exception ex)
    {  System.out.print("Erro na Thread com_Campanha_Compra.jsp-"+ex);
    }
    finally {
        if(iconexaobean!= null) {
          try {
//liberar a conexao aqui...
            iconexaobean.desconecta(iconexaobean,null,request,null,null);
//System.out.println("DEPOIS RELEASE--------"+new java.util.Date());
          }
          catch (SQLException exsql)
          {  System.out.print("Erro na Thread com_Campanha_Compra.jsp = "+exsql);
          }
          catch (Exception ex)
          {  System.out.print("Erro na Thread com_Campanha_Compra.jsp="+ex);
          }
        }
    }
  }
}


