 /**************************************************************************
 Empresa: Triscal
 Autor: Christiano Chamma/Alexandre
 Data: Janeiro de 2001'

 Descri??o:
       Classe com fun??es diversas.
 abc ghi
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
 import oracle.jdbc.*; //import oracle.jdbc.driver.OracleSQLException;
import java.sql.Types.*;

//import oracle.sql.DATE;
//weblogic import oracle.security.sso.enabler.SSOEnabler;
 //weblogic import oracle.security.sso.enabler.SSOUserInfo;
 //weblogic import oracle.security.sso.enabler.SSOEnablerUtil;
 //weblogic import oracle.security.sso.enabler.SSOEnablerException;

 public class UtilBean {

   public String getVersao()
   {   return("05/05/2002");
   }
     public static String formataNumero( Object pvalor)
     {
       if( pvalor == null ) return "";
       String valor = pvalor.toString();
       if( valor.equals("") ) return "";

       String strdecimais = "";
       String strinteiros = "";
       String sinal = "";

       if( valor.length() > 0 )
       {
          if( valor.substring(0,1).equals("-"))
          {
             sinal = "-";
             if( valor.length() > 1 )
                valor = valor.substring(1);
             else
             {
                valor = "";
                return valor;
             }
          }

          valor = valor.replace( '.', ',' );
          if( valor.indexOf(",") >= 0 )
          {
             strinteiros = valor.substring( 0, valor.indexOf(",") );
             strdecimais = valor.substring( valor.indexOf(",") );
          }
          else
          {
             strinteiros = valor;
          }
          int cont = 0;
          String fmtinteiros = "";
          for( int i = strinteiros.length(); i > 0; i-- )
          {
             cont++;
             if( cont == 4)
             {   cont = 1;
                 fmtinteiros = "." + fmtinteiros;
             }
             fmtinteiros = strinteiros.substring( i - 1, i ) + fmtinteiros;
          }
          return sinal + fmtinteiros + strdecimais;
       }
       return valor;
     }

   public static String numberToCurrency(double numero){
     NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
     nf.setMinimumFractionDigits(2);
     return nf.format(numero);
 //     return ""+numero;
   }

   public static String numberToCurrency(Object numero){
     double buffer;
     try{
       buffer = Double.parseDouble(numero+"");
     }catch (NumberFormatException e){
       buffer = 0;
     }
     return numberToCurrency(buffer);
   }

   public static String formatNumber(double numero, boolean return_zero ){
     NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt","BR"));
     if(return_zero)
       return nf.format(numero);
     else 
       return nf.format(numero).equals("0")?"":nf.format(numero);
 //     return ""+numero;
   }

   public static String formatNumber(Object numero, boolean return_zero){
     double buffer;
     try{
       buffer = Double.parseDouble(numero+"");
     }catch (NumberFormatException e){
       buffer = 0;
     }
     return formatNumber(buffer, return_zero);
   }

   public static String formatNumber(double numero, int maxDecimal, int minDecimal, boolean return_zero){
     NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt","BR"));
     nf.setMaximumFractionDigits(maxDecimal);
     nf.setMinimumFractionDigits(minDecimal);
     if(return_zero)
       return nf.format(numero);
     else 
       return nf.format(numero).equals("0")?"":nf.format(numero);
 //     return ""+numero;
   }

   public static String formatNumber(Object numero, int maxDecimal, int minDecimal, boolean return_zero){
     double buffer;
     try{
       buffer = Double.parseDouble(numero+"");
     }catch (NumberFormatException e){
       buffer = 0;
     }
     return formatNumber(buffer, maxDecimal, minDecimal, return_zero);
   }

   public static String desformataNumero( String value ) {
       String strnumero = "";
       String strnumeroaux = "";
       if( value.length() <= 0 )
          return "";
       else
       {
           if((value.indexOf(",") >= 0))
           {
             //strnumero = valor.replace( '.', '');
             for ( int aux=0; aux < value.length(); aux++ )
             {  //tira os pontos
                if(! value.substring( aux, aux+1 ).equals(".") )
                   strnumero = strnumero + value.substring( aux, aux+1 );
             }
             // strnumero = strnumero.replace( ',', '.' );
             for ( int k=0; k < strnumero.length(); k++)
             {  //substitui virgula por ponto
                if (! strnumero.substring(k, k+1).equals(","))
                   strnumeroaux += strnumero.substring(k, k+1);
                else
                   strnumeroaux += ".";
             }
             strnumero=strnumeroaux;
           }
           else if((value.indexOf(".") >= 0))
           {
             //strnumero = valor.replace( '.', '');
             for ( int aux=0; aux < value.length(); aux++ )
             {  //tira os pontos
                if(! value.substring( aux, aux+1 ).equals(".") )
                   strnumero = strnumero + value.substring( aux, aux+1 );
             }
           }
           else
             strnumero = value;
           if( isNumber( strnumero ) )
              return strnumero;
           else
              return null;
       }
   }
   public static boolean isNumber(String pvalor) {
      double testanumero = 0;
      try {
         testanumero = Double.parseDouble(pvalor);
      } catch (Exception ex) {
         return false;
      }
      return true;
   }

   public static String formataData ( Object s ,String pattern) {
     if( (s == null) || (s.equals("")) ) return "";
     String classe = s.getClass().getName();

     java.util.Date dt = new java.util.Date();
     SimpleDateFormat dtForm = new SimpleDateFormat("",new Locale("pt","BR"));
     if (classe.equals("java.lang.String")){
       try{
         if((s.toString().indexOf("/") == 2) &&
            (s.toString().lastIndexOf("/") == 5))
         {   //System.out.println("formataData, 001, s=" + s.toString());
             return s.toString();
         }    
           //System.out.println("formataData, 002, s=" + s.toString());
         //dtForm.applyPattern("yyyy-MM-dd HH:mm:ss.S");
           if (s.toString().length() > 10)
             dtForm.applyPattern("yyyy-MM-dd HH:mm:ss");
           else
               dtForm.applyPattern("yyyy-MM-dd");
           //System.out.println("formataData, 003, s=" + s.toString());
         dt = dtForm.parse(s.toString());
           //System.out.println("formataData, 003,5, s=" + s.toString());
       }catch (java.text.ParseException ex){
           System.out.println("formataData, Formato Inv?lido, s=" + s.toString());
         return "Formato Inv?lido";
       }
     }else if (dt.getClass().isAssignableFrom(s.getClass())){//(classe.equals("java.util.Date")||classe.equals("java.sql.Timestamp")) {
       //System.out.println("formataData, 004, s=" + s.toString());
       dt = (java.util.Date) s;
     }
     else return "Tipo Inv?lido";

       //System.out.println("formataData, 005, s=" + s.toString());
     if (pattern == null) pattern="dd/MM/yyyy HH:mm";
       //System.out.println("formataData, 006, s=" + s.toString());
     dtForm.applyPattern(pattern);
       //System.out.println("formataData, 007, s=" + s.toString());
     return dtForm.format(dt);
   }

   public static Object getVerificaNull(Object pValor) {
     if( pValor == null ) {
       return "";
     }
     return pValor;
   }

   public static String getImprimeSimNao(String pValor) {
     if( pValor.equals("S"))
       pValor = "Sim";
     else if( pValor.equals("N") )
       pValor = "N?o";
     return pValor;
   }

   public static String getImprimeChecked(String pValor, String pCompara) {
     if( pValor.equals(pCompara))
       pValor = "CHECKED";
     else
       pValor = "";
     return pValor;
   }

   public static String getFormataCGC_CPF(String pValor) {
     String pRetorno = "";

     if( (pValor == null) || (pValor.equals("")) ) {
       return "";
     }
     if( pValor.length() == 14)
     {
        //CGC
        pRetorno = pValor.substring(0,2) + "." +
                   pValor.substring(2,5) + "." +
                   pValor.substring(5,8) + "/" +
                   pValor.substring(8,12) + "-" +
                   pValor.substring(12);
        return pRetorno;
     }
     if( pValor.length() == 11)
     {
        //CPF
        pRetorno = pValor.substring(0,3) + "." +
                   pValor.substring(3,6) + "." +
                   pValor.substring(6,9) + "-" +
                   pValor.substring(9);
        return pRetorno;
     }
     return pValor;
   }

   public static boolean validaCGC (String pCGC) {
     String W_CGC;
     int W_DV;
     int W_DV_AUX=0;
     int W_VAL_MULT;

     try
     {
         if(pCGC.equals(""))
         {  return true;
         }

         for(int y=1; y <= 2; y++) {
           W_DV=0;
           W_VAL_MULT=2;

           while(pCGC.length() < 14) {
               pCGC = "0" + pCGC;
           }
           W_CGC=pCGC.substring(0, y+11);

           for(int x=y+10; x >= 0; x--) {
             if(W_VAL_MULT == 10)
             {  W_VAL_MULT = 2;
             }
             W_DV = W_DV + (Integer.parseInt(W_CGC.substring(x,x+1))
             * W_VAL_MULT);
             W_VAL_MULT++;
           }
           W_DV = W_DV % 11;

           if(W_DV==0 || W_DV==1) {
             W_DV = 11;
           }

           if(y==1) {
             W_DV_AUX=(11-W_DV) * 10;
           } else {
           W_DV_AUX = W_DV_AUX + (11 - W_DV);
           }
         }
         while(pCGC.length() < 14) {
             pCGC = "0" + pCGC;
         }
         if (W_DV_AUX==Integer.parseInt(pCGC.substring(12))) {
             return true;
         } else {
             return false;
         }
     }
     catch (Exception ex)
     {
        return false;
     }
   }

   public static boolean validaCPF(String str)
   {
     String W_CPF;
     int W_DV;
     int W_DV_AUX=0;
     int W_VAL_MULT;

     for(int y=1; y <= 2; y++)
     {
       W_DV=0;
       W_VAL_MULT=2;

       while(str.length() < 11)
       { str = "0" + str;
       }

       W_CPF=str.substring(0, y+8);

       for(int x=y+7; x >= 0; x--)
       {
         W_DV = W_DV + (Integer.parseInt(W_CPF.substring(x,x+1)) * W_VAL_MULT);
         W_VAL_MULT++;
       }

       W_DV = W_DV % 11;
       if(W_DV==0 || W_DV==1)
       { W_DV = 11;
       }

       if(y==1)
       {  W_DV_AUX=(11-W_DV) * 10;
       }
       else
       { W_DV_AUX = W_DV_AUX + (11 - W_DV);
       }
     }

     while(str.length() < 11)
     {  str = "0" + str;
     }

     if (W_DV_AUX==Integer.parseInt(str.substring(9)))
     { return true;
     }
     else
     { return false;
     }
   }

 //----------------------------------------------------------------------
 //   ROTINA QUE EFETUA A RETIRADA DE CARACTERES ESPECIAIS DE UMA STRING
 //   Devolve uma string formatada - sem os caracteres especiais
 //   Recebe por par?metro a string original
 //----------------------------------------------------------------------
   public static String tiraEspeciais (String str)
   {
     String retorno = str;

     String charEsp[] = {"á","â","ã","Á","Â","Ã","é","ê","É","Ê","í","Í","ó","ô","õ","Ó","Ô","Õ","ú","ü","Ú","Ü","ç","Ç"};
     String charNor[] = {"a","a","a","A","A","A","e","e","E","E","i","I","o","o","o","O","O","O","u","u","U","U","c","C"};

         for (int esp = 0; esp <= 23; esp++)
                 retorno = retorno.replace(charEsp[esp], charNor[esp]);

         return retorno;
   }

 //----------------------------------------------------------------------
 //   ROTINA QUE TROCA A PRIMEIRA LETRA DAS PALAVRAS PARA MAI?SCULA
 //   Chama --
 //   Recebe por par?metro o nome que sofrer? o acerto de caixa
 //----------------------------------------------------------------------
   public static String acertaPrimeira (String nome, int min)
   {
      if (nome.length() > min)
         return nome.substring(0,1).toUpperCase() + nome.substring(1);
      else
         return nome;
   }
 //----------------------------------------------------------------------
 //   ROTINA QUE EFETUA O ACERTO DE CAIXA DAS PALAVRAS
 //   Chama acertaPrimeira(nome)
 //   Recebe por par?metro o nome que sofrer? o acerto de caixa
 //----------------------------------------------------------------------
   public static String acertaCaixa (String nome, int min)
   {
      String vacertaCaixa;
      String nomeAux;

      if (nome.indexOf(" ") > 0)
      {
         nomeAux = acertaPrimeira(nome.substring(0, nome.indexOf(" ")).toLowerCase(), min);
         nome    = nome.substring(nome.indexOf(" ") + 1);

         while (nome.indexOf(" ") > 0)
         {
            nomeAux = nomeAux + " " + acertaPrimeira(nome.substring(0, nome.indexOf(" ")).toLowerCase(),min);
            nome    = nome.substring(nome.indexOf(" ") + 1);
         }
         vacertaCaixa = nomeAux + " " + acertaPrimeira(nome.toLowerCase(),min);
      }
      else
         vacertaCaixa = acertaPrimeira(nome.toLowerCase(), min);
      return vacertaCaixa;
   }

   public static boolean validaDataMaior(Object pData1, Object pData2) {
      Calendar vData1 = Calendar.getInstance();
      Calendar vData2 = Calendar.getInstance();
      vData1.setTime((java.util.Date)pData1);
      vData2.setTime((java.util.Date)pData2);
      return vData1.after(vData2);
   }

   public static boolean validaData(Object pValor) {
     try
     {
        if( pValor == null ) {
          return true;
        }
        if(pValor.toString().length() ==0) {
          return true;
        }
        //DATE odata = new DATE(pValor.toString());
         java.sql.Date odata = java.sql.Date.valueOf(pValor.toString());
        return true;
     }
     catch (Exception ex)
     {
        return false;
     }
   }
   public static String encodeURL(String pStr) {
     String retorno;
     if( pStr == null )
        return null;
     if( pStr.equals("") )
        return "";
     retorno = pStr;
     retorno = retorno.replace("?", "§");
     retorno = retorno.replace("=", "¢");
     retorno = retorno.replace("&", "Æ");
     //iplanet nao aceita esses caracteres
     //retorno = retorno.replace(' ', '?');
     retorno = retorno.replace(' ', '*');
     return retorno;
   }
   public static String decodeURL(String pStr) {
     String retorno;
     if( pStr == null )
        return null;
     if( pStr.equals("") )
        return "";
     retorno = pStr;
     retorno = retorno.replace("§", "?");
     retorno = retorno.replace("¢", "=");
     retorno = retorno.replace("Æ", "&");
     //iplanet nao aceita esses caracteres
     //retorno = retorno.replace('?', ' ');
     retorno = retorno.replace('*', ' ');
     return retorno;
   }

   public static String chamouAtravesDe (HttpServletRequest request, String pChamador) {
      boolean bOK = true;
      String retorno = "";
      if(request.getHeader("Referer") == null)
         bOK = false;
      else
         if(request.getHeader("Referer").indexOf(pChamador) <= 0)
            bOK = false;

      if( !bOK )
      {
         retorno = "<HTML><head><title></title> ";
         retorno += "<script LANGUAGE=\"javascript\"> \n";
         retorno += "function carrega() { \n";
         retorno += "  if(parent.frames[1] != null) \n";
         retorno += "  { ";
         retorno += "     //parent.frames[1].location.href = \"contents.jsp\";";
         retorno += "  } ";
         retorno += "  else ";
         retorno += "  { ";
         retorno += "     alert(\"Ocorreu uma tentativa de acesso direto a uma p?gina do sistema. O sistema ir? redirecionar para a p?gina principal, tente navegar atrav?s dos links.\");";
         retorno += "     document.location.href = \"redirmain.jsp\";";
         retorno += "  } ";
         retorno += "} \n";
         retorno += "</script> </head> <body class=\"comBodyAces\" onLoad=\"carrega()\"> \n";
         retorno += "Ocorreu uma tentativa de acesso direto a uma p?gina do sistema. O sistema ir? redirecionar para a p?gina principal, tente navegar atrav?s dos links. \n";
         retorno += "</body></html>";
         return retorno;
      }
      return "";
   }

   public static String substituiPlic(Object pValor) {
     String retorno = "";
     String retorno2 = "";
     if( pValor == null ) {
       return "";
     }
     retorno = "" + pValor;
     for (int pos=0; pos < retorno.length(); pos++)
     {
        if (retorno.substring(pos, pos+1).equals("'"))
           retorno2 += "''";
        else
           retorno2 += retorno.substring(pos, pos+1);
     }
     return retorno2;
   }

   public static void limpaSession(HttpServletRequest request, HttpSession session,
                                   HttpServletResponse response,
                                   boolean setaCookie, boolean signout)
          throws SQLException, java.io.IOException, Exception
   {
     UtilBean.session_putValue(session, "COM_ISLOGGEDIN", "false");
     UtilBean.session_putValue(session, "COM_MENUBUILT", "");

     if( setaCookie )
     {  
        //SSOEnablerBean ssoObj = new SSOEnablerBean(session);
        //ssoObj.removeJspAppCookie(response);
        //Cookie l_JspAppCookie = new Cookie(ssoObj.m_pappCookieName, "End application sesion");
        //l_JspAppCookie.setDomain(ssoObj.m_pappCookieDomain);
        //l_JspAppCookie.setMaxAge(0); 
        //l_JspAppCookie.setPath(m_pappCookieScope);
        //l_JspAppCookie.setComment(ssoObj.m_pappCookieDesc);
        //response.addCookie(l_JspAppCookie);
UtilBean.GeraDebugLogSimples("20221122 UtilBean setando cookie false.", session, null, true);

         UtilBean.RemoveCookie(request, response, "COMLOGGED");
         UtilBean.RemoveCookie(request, response, "COM_SESSAOJSP");

        //Cookie mycookie = new Cookie("COMLOGGED", "FALSE");
        //response.addCookie(mycookie);

        //Cookie mycookie2 = new Cookie("COM_SESSAOJSP", "NULO");
        //response.addCookie(mycookie2);
     }
     conexaoBean iconexaobean = (conexaoBean)session.getAttribute("iconexaobean");
     if(iconexaobean != null)
        iconexaobean.rollback();

     sessionCleanBean clean = new sessionCleanBean();
     clean.execute(session, 0);

     if(signout)
        if(iconexaobean != null)
           iconexaobean.desconecta(iconexaobean, null, null, response, session);

      //String array1[] = session.getAttributeNames();
      Enumeration enum1 = session.getAttributeNames();
      //if(array1 != null)
      if(enum1 != null)
      {  //for (int i=0; i < array1.length; i++)
               while (enum1.hasMoreElements()) 
         { String array1 = (String) enum1.nextElement();
                       if(/*array1[i]*/array1.toLowerCase().startsWith("cmdbbean"))
               session.removeAttribute(array1/*[i]*/);
         }
      }
      //String array2[] = session.getAttributeNames();
      Enumeration enum2 = session.getAttributeNames();
      //if(array2 != null)
      if(enum2 != null)
      {  //for (int i=0; i < array2.length; i++)
               while (enum2.hasMoreElements()) 
         {  String array2 = (String) enum2.nextElement();
                        if( (/*array2[i]*/array2.toLowerCase().startsWith("com_")) ||
                (/*array2[i]*/array2.toLowerCase().startsWith("lov"))  ||
                (/*array2[i]*/array2.toLowerCase().indexOf("menubean") >= 0))
               session.removeAttribute(array2/*[i]*/);
         }
      }
      if(signout)
      { //String array3[] = session.getAttributeNames();
        Enumeration enum3 = session.getAttributeNames();
        //if(array3 != null)
        if(enum3 != null)
        {  //for (int i=0; i < array3.length; i++)
                 while (enum3.hasMoreElements()) 
           {  String array3 = (String) enum3.nextElement();
                          if(/*array3[i]*/array3.toLowerCase().indexOf("conexaobean") >= 0)
                 session.removeAttribute(array3/*[i]*/);
           }
        }
      }
   }

   public static String getJSP(HttpServletRequest request)
          throws java.io.IOException, Exception
   {
     String str="";
     //????? apache jserv String buf=request.getServletPath();
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

   public static String montaURL(HttpSession session,
                                 String sURL, String pParametros) {
      String sParametros = (pParametros == null? "" : pParametros);
      String retorno = (sURL == null? "" : sURL);

      String sNetAnalysis = "";
      //if(session_getValue(session, "COM_CDG_EMP") != null)
      //{  //if(sNetAnalysis.equals(""))
      //   //   sNetAnalysis += "NA1=" + session_getValue(session, "COM_CDG_EMP");
      //   //else
      //   //   sNetAnalysis += "&NA1=" + session_getValue(session, "COM_CDG_EMP");
      //}

      //
      if(retorno.indexOf("?") > 0)
      {  if(sParametros.equals(""))
            sParametros = retorno.substring((retorno.indexOf("?") + 1));
         else
            sParametros += "&" + retorno.substring((retorno.indexOf("?") + 1));
         retorno = retorno.substring(0, retorno.indexOf("?"));
      }

      if(sNetAnalysis.equals(""))
      {  if(! sParametros.equals(""))
            retorno += "?" + sParametros;
      }
      else
      {  if(! sParametros.equals(""))
            retorno += "?" + sNetAnalysis + "&" + sParametros;
         else
            retorno += "?" + sNetAnalysis;
      }
      return retorno;
   }

   public static String getReferer( HttpServletRequest request )
          throws java.io.IOException, Exception
   {
      String HTTP_REFERER = "";
      if (request.getHeader("Referer") != null)
         HTTP_REFERER = request.getHeader("Referer");

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
   }
   public static boolean geraDebugLog(String pmsg,
                           appControlBean icontroleapp, 
                           HttpSession session,
                           HttpServletRequest request,
                           HttpServletResponse response, JspWriter out)
   {
      return InternalGeraDebugLog(pmsg, icontroleapp, session, request, response, out, false);
   }
   public static boolean InternalGeraDebugLog(String pmsg,
                           appControlBean icontroleapp, 
                           HttpSession session,
                           HttpServletRequest request,
                           HttpServletResponse response, JspWriter out, boolean bGera)
   {
       //if(! bGera) return true; //desabilitado os logs em 10/01/2003
       boolean bcontinua = false;

       if(request == null) {
         return true;
       }

       if(request.getSession().getAttribute("DEBUG") != null)
         if(request.getSession().getAttribute("DEBUG").equals("S"))
           bcontinua = true;

       if(! bcontinua)
         if((request.getParameter("DEBUG") == null) || 
            (! request.getParameter("DEBUG").equals("S")))
           return true;
      
        boolean result = true;
        conexaoBean rastrConnBean = null;
        CallableStatement pstmIns  = null;
        try {
           rastrConnBean = new conexaoBean();
           rastrConnBean.execute(icontroleapp, session, request, response, out);
           //iconexaobean.rollback();
           pstmIns = (CallableStatement)rastrConnBean.getConnection().prepareCall(
           "BEGIN INSERT INTO COM_DEBUG (TEXTO, DATA) VALUES (:1, SYSDATE); END;");
           pstmIns.setString(1, ""+pmsg);
           pstmIns.execute();
           rastrConnBean.getConnection().commit();
           rastrConnBean.desconecta(rastrConnBean, out, request, response, session);
        }
        catch(SQLException sqlexh) { 
           result = false;
           System.out.println("UtilBean.geraDebugLog: Erro ao gravar log(2)=" + new java.util.Date() + " " + sqlexh); }
        catch(Exception exh) { 
           result = false;
           System.out.println("UtilBean.geraDebugLog: Erro ao gravar log(3)=" + new java.util.Date() + " " + exh); }
        finally {
           try {
              if(pstmIns != null) pstmIns.close();
              if( rastrConnBean != null )
                 rastrConnBean.desconecta(rastrConnBean, out, request, response, session);
           } catch (Exception exlog) { System.out.println("UtilBean.geraDebugLog: Erro ao desconectar (4)=" + new java.util.Date() + " " + exlog); }
        }
        return result; 
   }
     public static boolean GeraDebugLogSimples(String pmsg,
                             HttpSession session,
                             HttpServletRequest request,
                                                     boolean bGera)
     {
         if(! bGera) return true; //desabilitado os logs em 10/01/2003
         boolean bcontinua = false;

         if(request == null) {
           return true;
         }

         if(request.getSession().getAttribute("DEBUG") != null)
           if(request.getSession().getAttribute("DEBUG").equals("S"))
             bcontinua = true;

         if(! bcontinua)
           if((request.getParameter("DEBUG") == null) || 
              (! request.getParameter("DEBUG").equals("S")))
             return true;
System.out.println(pmsg);        
          boolean result = true;
          conexaoBean rastrConnBean = null;
          CallableStatement pstmIns  = null;
          try {
             rastrConnBean = new conexaoBean();
             rastrConnBean.executedebug(session, request);
             //iconexaobean.rollback();
             pstmIns = (CallableStatement)rastrConnBean.getConnection().prepareCall(
             "BEGIN INSERT INTO COM_DEBUG (TEXTO, DATA) VALUES (:1, SYSDATE); END;");
             pstmIns.setString(1, ""+pmsg);
             pstmIns.execute();
             rastrConnBean.getConnection().commit();
             rastrConnBean.desconecta(rastrConnBean, null, request, null, session);
          }
          catch(SQLException sqlexh) { 
             result = false;
             System.out.println("UtilBean.GeraDebugLogSimples: Erro ao gravar log(2)=" + new java.util.Date() + " " + sqlexh); }
          catch(Exception exh) { 
             result = false;
             System.out.println("UtilBean.GeraDebugLogSimples: Erro ao gravar log(3)=" + new java.util.Date() + " " + exh); }
          finally {
             try {
                if(pstmIns != null) pstmIns.close();
                if( rastrConnBean != null )
                   rastrConnBean.desconecta(rastrConnBean, null, request, null, session);
             } catch (Exception exlog) { System.out.println("UtilBean.GeraDebugLogSimples: Erro ao desconectar (4)=" + new java.util.Date() + " " + exlog); }
          }
          return result; 
     }
 
   
   public static boolean setDominio (appControlBean icontroleapp, conexaoBean iconexaobean,
                                    HttpSession session,
                                    HttpServletRequest request,
                                    HttpServletResponse response, JspWriter out)
          throws java.io.IOException, SQLException, Exception
   {
 //geraDebugLog("UtilBean.setDominio --- INICIO",
 //             icontroleapp, session, request, response, out);
      if (session.getAttribute("COM_DOMINIO") == null)
      {
 //geraDebugLog("UtilBean.setDominio --- 001",
 //             icontroleapp, session, request, response, out);
      
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String URL_REPORTS = "";
        String URL_SMTP = "";
        String URL = "";
        String PORT_OC4J  = "";
        String URL_COM_SN = "";
        String URL_COM_RN = "";
        boolean bDesenv = false;
        try
        {
          //???? apache jserv String aux = request.getServletPath(); // /portalcom/images/pag.jsp
          String aux = request.getRequestURI(); // /portalcom/images/pag.jsp
          String dominio = "";

          if(aux == null) aux = "";
          int contBarra = 0;
          for(int i=0; i<aux.length(); i++)
          {  if(aux.substring(i, i+1).equals("/"))
                contBarra++;
             else
                if(contBarra == 1)
                   dominio += aux.substring(i, i+1);
          }
 //geraDebugLog("UtilBean.setDominio --- 002, dominio="+dominio,
 //             icontroleapp, session, request, response, out);
          if(contBarra < 2)
             dominio = "";
             
 //===== Alteracao para o 9i  =====
          if( ((request.getServerPort() >= 7070) && (request.getServerPort() <= 7100)) ||
              (request.getServerPort() > 8980) )
             dominio = "";
 //===== fim Alteracao para o 9i  =====

          //request.getServerName()

          iconexaobean.execute(icontroleapp, session, request, response, out);

          if( ((request.getServerPort() >= 7070) && (request.getServerPort() <= 7100)) ||
              (request.getServerPort() > 8980) )
          {  //desenvolvimento pelo JDeveloper
             session_putValue(session, "COM_DOMINIO", "portalcom");
             session_putValue(session, "COM_DOMINIO2", "commit9ias");
 //session_putValue(session, "COM_RN", "http://commitjava/portalcom/");
 //session_putValue(session, "COM_SN", "commitjava");
             //21/11/2001: adicionado o if abaixo...
             if(session_getValue(session, "COM_RN") == null)
             {  session_putValue(session, "COM_RN", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/");
                session_putValue(session, "COM_SN", request.getServerName());
             }
             bDesenv = true;
          }
          else
          {
 //geraDebugLog("UtilBean.setDominio --- 003",
 //             icontroleapp, session, request, response, out);
             session_putValue(session, "COM_DOMINIO", "portalcom");
             session_putValue(session, "COM_DOMINIO2", "commit9ias");
             if(session_getValue(session, "COM_RN") == null)
             {  session_putValue(session, "COM_RN", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/portalcom/");
 //geraDebugLog("UtilBean.setDominio --- 004",
 //             icontroleapp, session, request, response, out);
                session_putValue(session, "COM_SN", request.getServerName());
             }
          }

 //========= Alexandre - Parametros Administrativos em geral =========
            pstm = (PreparedStatement)iconexaobean.getConnection().prepareStatement(
              "SELECT CDG_PARAM, VLR_INI_PARAM "
            + "FROM COM_PARAM_ADMIN_GERAL "
            + "WHERE CDG_PARAM IN (1, 2) "
            + "ORDER BY CDG_PARAM",
                   ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

            rs = (ResultSet) pstm.executeQuery();
            while(rs.next())
            {  if(rs.getInt("CDG_PARAM") == 1)
                  URL_SMTP = rs.getString("VLR_INI_PARAM");
               else if(rs.getInt("CDG_PARAM") == 2)
                  URL_REPORTS = rs.getString("VLR_INI_PARAM");
            }
            session_putValue(session, "COM_URL_SMTP", URL_SMTP);
            session_putValue(session, "COM_URL_REPORTS", URL_REPORTS);
 //====================== fim - Alexandre =============================

        }
        catch (Exception ex)
        {  throw ex;
        }
        finally
        { if(rs != null)
             rs.close();
          if(pstm != null)
             pstm.close();
        }
        return true;
      }
      else
      {
 //geraDebugLog("UtilBean.setDominio --- 010",
 //             icontroleapp, session, request, response, out);
          //?????? apache jserv String aux = request.getServletPath(); // /portalcom/images/pag.jsp
          String aux = request.getRequestURI(); // /portalcom/images/pag.jsp
        
          String dominio = "";
          if(aux == null) aux = "";
          int contBarra = 0;
          for(int i=0; i<aux.length(); i++)
          {  if(aux.substring(i, i+1).equals("/"))
                contBarra++;
             else
                if(contBarra == 1)
                   dominio += aux.substring(i, i+1);
          }
          if(contBarra < 2)
             dominio = "";
          //request.getServerName()
          if( ((request.getServerPort() >= 7070) && (request.getServerPort() <= 7100)) ||
              (request.getServerPort() > 8980) )
          {  //desenvolvimento pelo JDeveloper
             dominio = "portalcom";
             //dominio = "commit9ias";
          }

          if( (! session.getAttribute("COM_DOMINIO").toString().toUpperCase().equals(dominio.toUpperCase())) &&
              (! session.getAttribute("COM_DOMINIO2").toString().toUpperCase().equals(dominio.toUpperCase()))
            )
          {  //alterou a URL na m?o, indo para outra empresa...
UtilBean.GeraDebugLogSimples("20221122 UtilBean setando cookie false 10.", session, request, true);
              
             limpaSession(request, session, response, true, false);
             //response.sendRedirect(session.getAttribute("COM_RN") + "index.jsp");

            //21-11/2001---Inicio-------------------------------------------
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
 out.println("   document.location.href = local.substring(0, ind+1) + \"index.jsp\";");
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
 out.println("   document.location.href = local.substring(0, ind+1) + \"index.jsp\";");
 out.println("   return true;");
 out.println("}");
 out.println("");
 out.println("</script>");
 out.println("</HEAD>");
 out.println("<BODY onLoad=\"errorcarrega()\">");
 out.println("<font color=\"#FFFFFF\"><a href=\"javascript:void navega()\"> </a>Clique para continuar.</font>");
 out.println("</BODY>");
 out.println("</HTML>");
            //21-11/2001---Fim----------------------------------------------
             return false;
          }
          return true;
      }
   }

   public static String getHTTPDominio(HttpSession session,
                                       HttpServletRequest request)
          throws java.io.IOException, Exception
   {
      if(session.getAttribute("COM_SN") == null)
         return request.getServerName();
      else
         return ""+session.getAttribute("COM_SN");
   }

   public static void session_putValue(HttpSession session,
                                       String nome, Object valor) {
      session.setAttribute( nome, valor );
   }
   public static Object session_getValue(HttpSession session,
                                         String nome) {
      return session.getAttribute( nome );
   }
   public static void session_removeValue(HttpSession session,
                                          String nome) {
      session.removeAttribute( nome );
   }

   //05/02/2002---
   public static String substitui( String strorigem, String strprocura, String strsubstitui ) {
      String retorno = "";
      int tamprocura = strprocura.length();
      int tamtotal = strorigem.length();

      for ( int k=0; k < tamtotal; k++)
      {
         if((k+tamprocura) > tamtotal)
            retorno += strorigem.substring(k, k+1);
         else
            if (strorigem.substring(k, k+tamprocura).equals(strprocura))
            {  retorno += strsubstitui;
               k = k + tamprocura - 1;
            }
            else
               retorno += strorigem.substring(k, k+1);
      }
      return (retorno);
   }
   public static void verificaCastCmDbBean(HttpSession session,
                                           HttpServletRequest request,
                                           HttpServletResponse response,
                                           String nome)
   {    //quando tem dmDbBeanRW na p?gina, devo verificar se existia anteriormente
        //algum cmDbBeanRO na session...
        //String array1[] = session.getAttributeNames();
        Enumeration enum1 = session.getAttributeNames();
        //if(array1 != null)
        if(enum1 != null)
        {  //for (int i=0; i < array1.length; i++)
                 while (enum1.hasMoreElements()) 
           {  String array1 = (String) enum1.nextElement();
                          //if(array1[i].toLowerCase().startsWith("cmdbbean"))
              if(/*array1[i]*/array1.toLowerCase().equals(nome.toLowerCase()))
              {  //combeans.cmDbBeanRO
                 //combeans.cmDbBeanRW
                 String classe = ((Object)session.getAttribute(array1/*[i]*/)).getClass().getName();
                 if( (classe != null) && (classe.equals("combeans.cmDbBeanRO")))
                    session.removeAttribute(array1/*[i]*/);
              }
           }
        }
   }
   //05/02/2002---
 //javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession, javax.servlet.jsp.JspWriter
   public static String SSOInclude (appControlBean icontroleapp,
                       conexaoBean iconexaobean,
                       Connection pconn,
                       JspWriter out,
                       HttpServletRequest request,
                       HttpServletResponse response,
                       HttpSession session)
          throws java.io.IOException, SQLException, Exception
   {
      String retorno =  "";
      //weblogic SSOEnablerBean ssoObj = new SSOEnablerBean(session);
 geraDebugLog("UtilBean.SSOInclude --- INICIO",
              icontroleapp, session, request, response, out);

      String servidorporta = request.getServerName() + ":" + request.getServerPort();
      //weblogic ssoObj.m_listenerToken    = "commit9ias:" +  servidorporta;
      //if( (request.getServerPort() == 7070) ||
      //    (request.getServerPort() == 7072) ||
      //    (request.getServerPort() == 8988) ||
      //    (request.getServerPort() == 7101) )
      if(true)
      {   //weblogic ssoObj.m_requestedUrl     = "http://" + servidorporta + "/index.jsp";
          //weblogic ssoObj.m_onCancelUrl      = "http://" + UtilBean.session_getValue(session,"COM_SN") + "/";
          //weblogic ssoObj.m_pappCookieDomain = UtilBean.session_getValue(session,"COM_SN") + "";
          if(! verificaLogado (session, request, response, false))
          {  loginBean login = new loginBean();
             login.execute(icontroleapp, iconexaobean, iconexaobean.getConnection(), out, request, response, session);
          }
      }
      else
      {   //weblogic ssoObj.m_requestedUrl     = "http://" + UtilBean.session_getValue(session,"COM_SN") + "/commit9ias/index.jsp";
          //weblogic ssoObj.m_onCancelUrl      = "http://" + UtilBean.session_getValue(session,"COM_SN") + "/";
          //weblogic ssoObj.m_pappCookieDomain = UtilBean.session_getValue(session,"COM_SN") + "";

 geraDebugLog("UtilBean.SSOInclude --- 001",
              icontroleapp, session, request, response, out);
          String usrInfo = null;
          String l_userName = null;
          //boolean PORTAL_l_gotPappCookie = false;
          //String  PORTAL_l_userInfo      = null;
          try
          {  // Get database connection
             //weblogic Connection l_db_con = ssoObj.getDbConnection();
             boolean l_gotPappCookie = false;
             String  l_userInfo      = null;
 geraDebugLog("UtilBean.SSOInclude --- 002",
              icontroleapp, session, request, response, out);
             try
             {   Cookie[] l_cookies = request.getCookies();
                 for(int i=0; i < l_cookies.length; i++)
                 {   Cookie l_pappCookie = l_cookies[i];
                     //weblogic if (l_pappCookie.getName().equals(ssoObj.m_pappCookieName))
                     //weblogic {   l_gotPappCookie = true;
                     //weblogic     l_userInfo      = l_pappCookie.getValue();
                     //weblogic     break;
                     //weblogic }
                 }
 geraDebugLog("UtilBean.SSOInclude --- 003",
              icontroleapp, session, request, response, out);

                 //for(int i=0; i < l_cookies.length; i++)
                 //{   Cookie l_pappCookie = l_cookies[i];
                 //    if (l_pappCookie.getName().toLowerCase().equals("portal30"))
                 //    {   PORTAL_l_gotPappCookie = true;
                 //        PORTAL_l_userInfo      = l_pappCookie.getValue();
                 //        break;
                 //    }
                 //}
             }
             catch(Exception e)
             {   l_userName = null;
             }
             if( (l_userInfo != null) && (l_userInfo.length() > 0) )
             {   l_userName = l_userInfo;
             }
             else
             {   l_userName = null;
             }
 geraDebugLog("UtilBean.SSOInclude --- 004",
              icontroleapp, session, request, response, out);

             if(l_userName == null)
             {
 geraDebugLog("UtilBean.SSOInclude --- 005",
              icontroleapp, session, request, response, out);

                 // Create SSOEnabler object
                 //weblogic SSOEnabler l_ssoEnabler = new SSOEnabler(l_db_con);
 geraDebugLog("UtilBean.SSOInclude --- 005, 01", icontroleapp, session, request, response, out);
                 // Create redirect url to the SSO server for user authentication
                 //9iASR1 String l_redirectUrl = l_ssoEnabler.generateRedirect(ssoObj.m_listenerToken, ssoObj.m_requestedUrl, ssoObj.m_onCancelUrl);
                 //weblogic String l_redirectUrl = l_ssoEnabler.generateRedirect(ssoObj.m_listenerToken, ssoObj.m_requestedUrl, ssoObj.m_onCancelUrl, false);
 geraDebugLog("UtilBean.SSOInclude --- 005, 02", icontroleapp, session, request, response, out);
                 // close database connection
                 //weblogic ssoObj.closeDbConnection(l_db_con);
 geraDebugLog("UtilBean.SSOInclude --- 005, 03", icontroleapp, session, request, response, out);
                 // p_response.sendRedirect(l_redirectUrl);
                 // Since the redirect url is usually large so send the redirect url input
                 // parameters using HTTP post method instead of usual GET method of
                 // HttpServletResponse.sendRedirect
                 //weblogic String htmlPostForm = SSOEnablerUtil.genHtmlPostForm(l_redirectUrl);
 geraDebugLog("UtilBean.SSOInclude --- 005, 04", icontroleapp, session, request, response, out);
                 //p_response.getWriter().println(htmlPostForm);
                 //out.println(htmlPostForm);
                 //weblogic retorno += htmlPostForm;
 geraDebugLog("UtilBean.SSOInclude --- 005 e meio",
              icontroleapp, session, request, response, out);
             }
             else
             {
 geraDebugLog("UtilBean.SSOInclude --- 006",
              icontroleapp, session, request, response, out);

                 // We got this user information from JSP application cookie
                 //9iASR1 SSOEnablerUtil l_ssoAppUtil = new SSOEnablerUtil(l_db_con);
                 //9iASR1 usrInfo = l_ssoAppUtil.unbakeAppCookie(ssoObj.m_listenerToken, l_userName);
                 //weblogic SSOEnabler l_ssoEnabler = new SSOEnabler(l_db_con);
 geraDebugLog("UtilBean.SSOInclude --- 006 e meio",
              icontroleapp, session, request, response, out);
                 //weblogic usrInfo = l_ssoEnabler.decryptCookie(ssoObj.m_listenerToken, l_userName);

 geraDebugLog("UtilBean.SSOInclude --- 007, usrInfo=" + usrInfo,
              icontroleapp, session, request, response, out);
                 if (usrInfo.indexOf("/") > 0) //9iASR2
                    usrInfo = usrInfo.substring(0, usrInfo.indexOf("/")); //9iASR2
                 session.setAttribute("COM_CDG_USUR", usrInfo);
             }
          }
          catch(Exception e)
          {  //throw new SSOEnablerException(e.toString());
             throw new Exception("Erro ao verificar login: " + e.toString());
          }
          //if(usrInfo == null) out.print("<center>Please wait while redirecting to the SSO Server...</center>");
      }
      return retorno;
   }

   public static boolean verificaLogado(HttpSession session,
                                        HttpServletRequest request,
                                        HttpServletResponse response,
                                        boolean limpasessao)
          throws SQLException, java.io.IOException, Exception
   {
      return internalverificaLogado(session, request, response, limpasessao);
   }
   public static boolean verificaLogado(HttpSession session,
                                        HttpServletRequest request,
                                        HttpServletResponse response)
          throws SQLException, java.io.IOException, Exception
   {
      return internalverificaLogado(session, request, response, true);
   }
   private static boolean internalverificaLogado(HttpSession session,
                                        HttpServletRequest request,
                                        HttpServletResponse response,
                                        boolean limpasessao)
          throws  SQLException, java.io.IOException, Exception
   { 
       String ultimapagina = "";
       if(session.getAttribute("COM_ULTIMA_PAGINA") != null)
          ultimapagina = "" + session.getAttribute("COM_ULTIMA_PAGINA");
       String loggedin = getVerificaNull(session_getValue(session, "COM_ISLOGGEDIN")).toString();

       boolean existeCookie = false;
       String valorCookie = "";
       Cookie mycookie[] = request.getCookies(); // = new Cookie("COMLOGGED", "FALSE");
       if(request.getCookies() != null)
       {  for(int i = 0; i < mycookie.length ; i++)
          {  if (mycookie[i].getName().equals("COMLOGGED"))
             {  if(! existeCookie) {
                  existeCookie = true;
                  valorCookie = mycookie[i].getValue();
                }
             }
          }
       }
       if( (session_getValue(session, "COM_ISLOGGEDIN") != null) && (! existeCookie))
       {  //usu?rio fechou e abriu o Browser
           if(limpasessao) {
               UtilBean.GeraDebugLogSimples("20221122 UtilBean setando cookie false 11.", session, request, true);
               
             limpaSession(request, session, response, true, false);
           }
       }
       else
       {  if(! existeCookie)
          {  
UtilBean.GeraDebugLogSimples("20221122 UtilBean setando cookie false 02.", session, request, true);
              
             //Cookie mycookienew = new Cookie("COMLOGGED", "FALSE");
             //response.addCookie(mycookienew);
             //Cookie mycookie2 = new Cookie("COM_SESSAOJSP", "NULO");
             //response.addCookie(mycookie2);
             UtilBean.CriaOuModificaCookie(request, response, "COMLOGGED", "FALSE");
             UtilBean.CriaOuModificaCookie(request, response, "COM_SESSAOJSP", "NULO");
             
          }
       }

       if ((! existeCookie) || (! valorCookie.equals("TRUE")))
       {  loggedin = "false";
          session_putValue(session, "COM_ISLOGGEDIN", "false");
          session_putValue(session, "COM_ULTIMA_PAGINA", "");
       }
       boolean logado = false;
       //String sUSUR_NM_LOGIN = "";
       if((loggedin == null) || (! loggedin.equals("true")))
       {
          if( (session.getAttribute("COM_ULTIMA_PAGINA") == null) ||
              (session.getAttribute("COM_ULTIMA_PAGINA").toString().equals("")) )
             session_putValue(session, "COM_ULTIMA_PAGINA", ultimapagina);
          return false;
       }
       else
       {
          //sUSUR_NM_LOGIN = (String)session_getValue("COM_CDG_USUR");
          //if( (request.getServerPort() == 7070) ||
          //    (request.getServerPort() == 7072) ||
          //    (request.getServerPort() == 8988) ||
          //    (request.getServerPort() == 7101) )
                  if(true)
             return true;
          else
          {  //weblogic String redirlogin = SSOInclude (null, null, null, null, request, response, session);
             String redirlogin = ""; //weblogic  
             if(! redirlogin.equals(""))
                 return false;
             else
             {   if((session.getAttribute("COM_CDG_USUR") == null) ||
                    (session.getAttribute("COM_CDG_USUR").equals("")))
                    return false;
                 else
                    return true;
             }
          }
       }
   }
   
   public static int getIndexBgColor(String nm_feriado[], int dia, int semana) 
   {    int index = semana;
        if(nm_feriado[dia-1] != null)
          index = 0;
        return index;
   }  

   public static int getLOG_ORDERBY (int P_DIA) 
   {
     if((P_DIA >= 11) && (P_DIA <= 25)) return( P_DIA );
     if((P_DIA >= 26) && (P_DIA <= 31)) return( P_DIA - 26 );
     if((P_DIA >= 1) && (P_DIA <= 10))  return( P_DIA + 5 );
     return( P_DIA );
   }
     public static Cookie getCookie(HttpServletRequest request, String name) {
             if (request.getCookies() != null) {
                 for (Cookie cookie : request.getCookies()) {
                     if (cookie.getName().equals(name)) {
                         return cookie;
                     }
                 }
             }

             return null;
         }
     public static void CriaOuModificaCookie(HttpServletRequest request, HttpServletResponse response, String name, String valor) {
         Cookie cookie = UtilBean.getCookie(request, name);
         if (cookie != null) {
             cookie.setValue(valor);
             response.addCookie(cookie);
         } else {
             Cookie mycookie = new Cookie(name, valor);
             response.addCookie(mycookie);
         }
     }
     public static void RemoveCookie(HttpServletRequest request, HttpServletResponse response, String name) {
         Cookie cookie = UtilBean.getCookie(request, name);
         if (cookie != null) {
             cookie.setMaxAge(0);
             cookie.setValue(null);
             response.addCookie(cookie);
         } 
     }
 }


