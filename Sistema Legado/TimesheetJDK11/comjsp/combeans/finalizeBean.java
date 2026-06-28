/**************************************************************************
Empresa: Triscal
Autor: Christiano Chamma/Alexandre
Data: Janeiro de 2001

Descrição:
      Classe que deve ser colocada no fim das paginas JSP.

**************************************************************************/
package combeans;

import javax.servlet.jsp.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.sql.*;
import java.util.*;

public class finalizeBean {

  public String getVersao()
  {   return("18/12/2001");
  }
    public void execute(conexaoBean iconexaobean,
                        JspWriter out,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        HttpSession session)
    {
//System.out.println("finalizebean ! ");
       if(iconexaobean != null)
          iconexaobean.removeRequest(request);

       boolean achou = false;
       //String array1[] = session.getAttributeNames();
	     Enumeration enum1 = session.getAttributeNames();
       //if(array1 != null)
       if(enum1 != null)
       {  //for (int i=0; i < array1.length; i++)
	        while (enum1.hasMoreElements()) 
          {  //if(array1[i].toLowerCase().startsWith("cmdbbean"))
		         String array1 = (String) enum1.nextElement();
		         if(/*array1[i]*/array1.toLowerCase().startsWith("cmdbbean"))
             {  //05/12/2001
                cmDbBeanRO cmdbbeanlocal = (cmDbBeanRO)session.getAttribute(array1/*[i]*/);
                if(cmdbbeanlocal.geticonexaobean() == iconexaobean)
                {  achou = true;
                   break;
                }
             }
             else if(/*array1[i]*/array1.toLowerCase().startsWith("select"))
             {  achou = true;
                break;
             }
          }
       }
       if(! achou)
       {
          //libera a conexao para o pool...
          if(iconexaobean != null)
          {
             try {
                if(iconexaobean.getNumRequests() == 0)
                {  iconexaobean.desconecta(iconexaobean, out, request, response, session); }
             }
             catch (SQLException ex) {
                System.out.println("finalizebean: ERRO AO FECHAR CONEXAO (1):" + new java.util.Date() + "=" + ex + "." );
             }
             catch (Exception ex2) {
                System.out.println("finalizebean: ERRO AO FECHAR CONEXAO (2):" + new java.util.Date() + "=" + ex2 + "." );
             }
          }
       }
       try {
         String pagina = UtilBean.getJSP(request);
         if(pagina == null) pagina = "";
         if( (! pagina.startsWith("lov")) &&
             (! pagina.startsWith("com_Popup")) &&
             (! pagina.startsWith("help_")) &&
             (! pagina.startsWith("erro")) &&
             (! pagina.equals(""))        &&
             (pagina.toLowerCase().indexOf("_submit") < 0) )
         {
            if(pagina.startsWith("consultasolap")) pagina = "consultasolap.jsp";
            session.setAttribute("com_ultima_pagina_refresh", pagina);

            if(response != null)
            {
               if(! pagina.equals(""))
               {  //Cookie mycookie = new Cookie("COM_ULTIMA_PAGINA_REFRESH", pagina);
                  //response.addCookie(mycookie);
                   UtilBean.CriaOuModificaCookie(request, response, "COM_ULTIMA_PAGINA_REFRESH", pagina);

               }
               else
               {  //Cookie mycookie = new Cookie("COM_ULTIMA_PAGINA_REFRESH", "NULO");
                  //response.addCookie(mycookie);
                   UtilBean.CriaOuModificaCookie(request, response, "COM_ULTIMA_PAGINA_REFRESH", "NULO");
               }
            }
//String nada1 = request.getQueryString();
//HttpUtils HTTPUTIL = new HttpUtils();
//String nada2 = HTTPUTIL.getRequestURL(request).toString();
//Hashtable ht = HTTPUTIL.parsePostData(request.getContentLength(), request.getInputStream());
//String nada3 = "";
//for(Enumeration el = ht.keys(); el.hasMoreElements();)
//   nada3 = ""+el.nextElement();

           Enumeration e;
           e = request.getParameterNames();
           String URL = "";
           if (e.hasMoreElements())
           {  while (e.hasMoreElements())
              {  String key = (String)e.nextElement();
                 //A name can have multiple values (e.g. checkboxes)
                 String[] values = request.getParameterValues(key);
                 String b = "";
                 for(int i = 0; i < values.length; i++) { b += values[i];}
                 if(URL.equals(""))
                    URL = key + "=" + b;
                 else
                    URL += "&" + key + "=" + b;
              }
           }
           session.setAttribute("com_ultima_pagina_qs", URL);
         }
       }
       catch (Exception exignored) {
          System.out.println("finalizebean: ERRO:" + new java.util.Date() + "=" + exignored + "." );
       }
    }
}

