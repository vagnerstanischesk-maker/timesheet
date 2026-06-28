/**************************************************************************
Empresa: Triscal
Autor: Christiano Chamma
Data: Janeiro de 2001

Descrição:
      Esta classe retira da session os cmDbBeans que não são mais necessários.

**************************************************************************/
package combeans;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.text.*;
import java.sql.*;
import javax.servlet.http.*;

public class sessionCleanBean {

  public String getVersao()
  {   return("19/11/2001");
  }
   public void execute( HttpSession session, int numCmDbBeans ) {
      internalexecute( session, numCmDbBeans, true );
   }
   public void execute( HttpSession session, int numCmDbBeans, boolean retiraconn ) {
      internalexecute( session, numCmDbBeans, retiraconn );
   }
   void internalexecute( HttpSession session, int numCmDbBeans, boolean retiraconn ) {
      //String array[] = session.getAttributeNames();
	  Enumeration enum1 = session.getAttributeNames();

      if( numCmDbBeans == -1 ) return;

      //if(array != null)
      if(enum1 != null)
      {
        //for (int i=0; i < array.length; i++)
	    while (enum1.hasMoreElements()) 
      {
		   String array1 = (String) enum1.nextElement();
           if((retiraconn) && (/*array[i]*/array1.toLowerCase().startsWith("dbmiconexaobean")))
           {  session.removeAttribute(array1/*[i]*/);
//System.out.print("RETIREI UM BEAN DE DBMCONEXAO DA SESSION=" + array[i]);
           }
           if((/*array[i]*/array1.toLowerCase().startsWith("cmdbbean")) &&
              (/*array[i]*/array1.toLowerCase().endsWith("lov")))
           {  session.removeAttribute(array1/*[i]*/);
//System.out.print("RETIREI UM BEAN DE LOV DA SESSION=" + array[i]);
           }
           else if(/*array[i]*/array1.toLowerCase().startsWith("cmdbbeanro"))
           {  int seq = Integer.parseInt(/*array[i]*/array1.substring(10));
              if(seq >= numCmDbBeans)
              {  session.removeAttribute(array1/*[i]*/);
//System.out.print("RETIREI UM BEAN READ ONLY DA SESSION=" + array[i]);
              }
           }
           else if(/*array[i]*/array1.toLowerCase().startsWith("cmdbbean"))
           {  int seq = Integer.parseInt(/*array[i]*/array1.substring(8));
              if(seq >= numCmDbBeans)
              {  session.removeAttribute(array1/*[i]*/);
//System.out.print("RETIREI UM BEAN DA SESSION=" + array[i]);
              }
           }
           else if(/*array[i]*/array1.toLowerCase().startsWith("selectbean"))
           {  String sufixo = /*array[i]*/array1.substring(10);
              if ( sufixo.length() >= 2 )
              {  try {
                    int seq = Integer.parseInt(sufixo.substring(0, 2));
                    if(seq >= numCmDbBeans)
                    {  session.removeAttribute(array1/*[i]*/);
//System.out.print("RETIREI UM BEAN DA SESSION=" + array[i]);
                    }
                 }
                 catch (Exception ex) { System.out.println("sessionClean: Erro ao remover select:" + new java.util.Date() + " " + ex + "." ); }
              }
           }
        }
      }
   }
}

