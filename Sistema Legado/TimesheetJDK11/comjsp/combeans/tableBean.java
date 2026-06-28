/**************************************************************************
Empresa: Triscal
Autor: Júnior
Data: Abril de 2001

Descrição:
      Classe que imprime uma tabela HTML com base em um acesso ao banco de
    dados realizado pelo cmDbBean.

-----------------------------------------------------------------
Pendências:
   1) Terminar tratamento de URLs nas colunas;

   2) Adaptar a classe para trabalhar com selectBean;

**************************************************************************/

package combeans;

import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import java.text.*;

public class tableBean extends Object {
  private int borderSize = 1;
  private int height = 300;
  private int width = 300;
  public boolean showBar = true;
  private char barAlign = 'C';
  private String barcssClasse = "#CDCDCD";
  public boolean showHeader = true;
  private String alignHeader;
  private boolean headSpan = true;
  private label titulo;
  public Columns columns;
  private cmDbBeanRO dbSource;
  public boolean showFooter = true;
  private String footerText;
  public String getVersao()
  {   return("19/11/2001");
  }
  public void enableFooter(String message){
    showFooter = true;
    footerText = message;
  }
  public void enableFooter(){
    showFooter = true;
    footerText = null;
  }
  public void disableFooter(){
    showFooter = false;
    footerText = null;
  }

  public void setbarcssClasse( String value ) {
     barcssClasse = value;
  }
  public String getbarcssClasse() {
     return barcssClasse;
  }

  private String getAlign(char alignment){
    String buffer = "";
    switch (alignment){
      case 'C':
        buffer = " align=\"center\"";
        break;
      case 'L':
        buffer = " align=\"left\"";
        break;
      case 'R':
        buffer = " align=\"right\"";
        break;
    }
    return buffer;
  }
  private String getStyleClass(String style){
    if (style!=null) return " class=\""+style+"\"";
    else return "";
  }
  private String formataTexto(Object texto,String pattern){
    if (pattern!=null) {
      String buffer = pattern.toUpperCase();
      if (buffer.equals("CPF")||buffer.equals("CGC")){
        return UtilBean.getFormataCGC_CPF(texto.toString());
      }
      else if (buffer.equals("VALOR")) {
        return UtilBean.formataNumero(texto.toString());
      }
      else if (buffer.equals("REAL")) {
        return UtilBean.numberToCurrency(texto);
      }
      else {
        return UtilBean.formataData(texto,pattern);
      }
    }else return "#Erro#";
  }
  public void initialize (String newTitulo, cmDbBeanRO newDbSource){
    label buffer = new label(newTitulo,null,"#000080","#FFFFFF",null,null,null);
    initialize(buffer,newDbSource);
  }

  public void initialize (label newTitulo, cmDbBeanRO newDbSource){
    titulo = newTitulo;
    dbSource = newDbSource;
    columns = new Columns();
  }

  public void setWidth(int newWidth){
    width = newWidth;
  }

  private String linkIt(column col, String value, HttpSession session){
    if (col.getURL()!=null){
      if (col.getURL().toLowerCase().indexOf("javascript")<0){
        if (col.parameters.isEmpty()){
          try{
            if((col.getVerificaRO()) && (dbSource.getColuna("CMREADONLY").equals("S")))
              if(col.getVerificaROSubstitui()==null) return value; else return col.getVerificaROSubstitui();
            else
              if(col.getVerificaURLColuna().length() > 0)
                 if(UtilBean.getVerificaNull(dbSource.getColuna(col.getVerificaURLColuna())).toString().equals( UtilBean.getVerificaNull(col.getVerificaURLValor()).toString() ))
                    return "<a href=\""+UtilBean.montaURL(session,col.getURL(),"")+"\">"+value+"</a>";
                 else
                    if(col.getVerificaURLSubstitui()==null) return value; else return col.getVerificaURLSubstitui();
              else
                 return "<a href=\""+UtilBean.montaURL(session,col.getURL(),"")+"\">"+value+"</a>";
          }catch(Exception ex){ return ex.toString(); }
        }else{
          String buffer = col.getURL();
          String e;
          parameter par;
          if (buffer.indexOf("?") < 0) e = "?";
          else e = "&";
          ListIterator li = col.parameters.listIterator();
          while (li.hasNext()){
            par =(parameter) li.next();
            try{
              String parametro = "";
              if(par.parameterField!=null) {
                 parametro = par.parameterField;
              }
              buffer += e + par.parameterName + "="
                     + (parametro.equals("") ? ""+dbSource.getregAtual() : dbSource.getColuna(parametro));
            }catch(Exception ex){
              return ex.toString();
            }
            e = "&";
          }
          String url = buffer.substring(0,buffer.indexOf('?'));
          String parametros = buffer.substring(buffer.indexOf('?')+1);
          try{
            if((col.getVerificaRO()) && (dbSource.getColuna("CMREADONLY").equals("S")))
              if(col.getVerificaROSubstitui()==null) return value; else return col.getVerificaROSubstitui();
            else
              if(col.getVerificaURLColuna().length() > 0)
                 if(UtilBean.getVerificaNull(dbSource.getColuna(col.getVerificaURLColuna())).toString().equals( UtilBean.getVerificaNull(col.getVerificaURLValor()).toString() ))
                    return "<a href=\""+UtilBean.montaURL(session,url,parametros)+"\">"+value+"</a>";
                 else
                    if(col.getVerificaURLSubstitui()==null) return value; else return col.getVerificaURLSubstitui();
              else
                 return "<a href=\""+UtilBean.montaURL(session,url,parametros)+"\">"+value+"</a>";
          }catch(Exception ex){ return ex.toString(); }
        }
      }else{
        if (col.parameters.isEmpty()){
          try{
            if((col.getVerificaRO()) && (dbSource.getColuna("CMREADONLY").equals("S")))
              if(col.getVerificaROSubstitui()==null) return value; else return col.getVerificaROSubstitui();
            else
              if(col.getVerificaURLColuna().length() > 0)
                 if(UtilBean.getVerificaNull(dbSource.getColuna(col.getVerificaURLColuna())).toString().equals( UtilBean.getVerificaNull(col.getVerificaURLValor()).toString() ))
                    return "<a href=\""+col.getURL()+"\">"+value+"</a>";
                 else
                    if(col.getVerificaURLSubstitui()==null) return value; else return col.getVerificaURLSubstitui();
              else
                 return "<a href=\""+col.getURL()+"\">"+value+"</a>";
          }catch(Exception ex){ return ex.toString(); }
        }else{
          String buffer = col.getURL();
          int index;
          parameter par;
          ListIterator li = col.parameters.listIterator();
          while (li.hasNext()){
            par =(parameter) li.next();
            try{
              index = buffer.indexOf(par.parameterName);
              if (index>-1){
                String parametro = "";
                if(par.parameterField!=null) {
                   parametro = par.parameterField;
                }
                buffer = buffer.substring(0,index) + "'" + (parametro.equals("") ? ""+dbSource.getregAtual() : dbSource.getColuna(parametro))
                         + "'" + buffer.substring(index+par.parameterName.length());
              }
            }catch(Exception ex){
              return ex.toString();
            }
          }
          try{
            if((col.getVerificaRO()) && (dbSource.getColuna("CMREADONLY").equals("S")))
              if(col.getVerificaROSubstitui()==null) return value; else return col.getVerificaROSubstitui();
            else
              if(col.getVerificaURLColuna().length() > 0)
                 if(UtilBean.getVerificaNull(dbSource.getColuna(col.getVerificaURLColuna())).toString().equals( UtilBean.getVerificaNull(col.getVerificaURLValor()).toString() ))
                    return "<a href=\""+buffer+"\">"+value+"</a>";
                 else
                    if(col.getVerificaURLSubstitui()==null) return value; else return col.getVerificaURLSubstitui();
              else
                 return "<a href=\""+buffer+"\">"+value+"</a>";
          }catch(Exception ex){ return ex.toString(); }
        }
      }
    }else return ""+value;
  }

  private String checkIt(column col, String value, HttpSession session){
     String retorno = "";
     retorno += "<input class=\"cmpCheck\" type=\"CHECKBOX\" name=\"" + col.getField() + dbSource.getregAtual() + "\" "
              + " value=\"S\" ";
     if( "S".equals(value) ) retorno += " CHECKED ";
     retorno += "> "; // + label;
     return retorno;
  }

  public void execute(JspWriter saida, HttpSession session)
         throws Exception{
    ListIterator li;
    column col;
    boolean isEven = false;
    saida.println("<!---------------------------------tabela conteudo--------------------------------->");
    //imprimi os botoes de navegação
    saida.println(dbSource.showButtons(session));
    saida.println("<table border=\"0\" bordercolor=\"#AEAEAE\" cellpadding=\"0\" cellspacing=\"0\" width=\""+ width +"\">");
    saida.println("<tr>");
    saida.println("<td bgcolor=\"#AEAEAE\" align=\"center\">");
    saida.println("<table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" width=\""+ (width-2) +"\">");

    // Inicio do bloco dos cabeçalhos
    if (showHeader){
      saida.println("<tr>");
      li = columns.items.listIterator();
      int span = 0;
      while (li.hasNext()){
        col = (column) li.next();
        if (headSpan && col.getField().indexOf("Btn%~")>-1){
          while (li.hasNext()&& col.getField().indexOf("Btn%~")>-1){
            col = (column) li.next();
            span++;
          }
          if (col.getField().indexOf("Btn%~")<0) li.previous();
          saida.print("<td bgcolor=\"#FEAC01\" ");
          saida.println(" colspan=\""+span+"\" Class=\"txtBlack\""+getAlign(col.align)+">");
          saida.println("<img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"1\" height=\"1\"></td>");
        }else{
          saida.print("<td bgcolor=\"#FED681\" ");
          if (col.getWidth()!=null) saida.print(" width=\""+ col.getWidth() +"\"");
          saida.println(" Class=\"txtBlack\""+getAlign(col.align)+">");
          saida.println(col.getTitle()+"</td>");
        }
      }
      saida.println("</tr>");
    }
//--------------------------------------------------------------
     boolean primeiro = true;
     int ultimo = 0;
     while (dbSource.PreenchePagina())
     {
//System.out.println("regatual=" + dbSource.getregAtual());
       if (dbSource.getregAtual()!=0){
         ultimo = dbSource.getregAtual();
         if(primeiro)
         {   primeiro = false;
             saida.println("<input type=\"HIDDEN\" name=\"TABREGINICIO\" value=\"" + dbSource.getregAtual() + "\">");
         }
         saida.println("<tr>");
         li = columns.items.listIterator();
         while (li.hasNext()){
           col = (column) li.next();
//System.out.println("field=" + col.getField());
           if (col.getField().indexOf("Btn%~")>-1) {
             saida.print("<td bgcolor=\"#E6E6E6\"");
           }
           else {
             saida.print("<td bgcolor=\"#FED681\"");

             saida.print(getAlign(col.align)+" "+getStyleClass(col.getStyle()));
             if (col.getWidth()!=null) saida.print(" width=\""+ col.getWidth() +"\"");
             if (col.getField().equals("~%RowId%~")&&(columns.rowIdDefaultColor)){
               saida.print(" Class=\"txtChumbo\"");
             }else{
               if (columns.alternateColors()){
                 if (isEven) saida.print(" Class=\""+columns.getEvenColor()+"\"");
                 else saida.print(" Class=\""+columns.getOddColor()+"\"");
               }
             }
           }
           switch (col.kind){
             case 0: // coluna default
               if (col.getPattern()!=null){
                 if(dbSource.getColuna(col.getField()) == null)
                    saida.println(">"+linkIt(col,"&nbsp;",session)+"</td>");
                 else
                    saida.println(">"+linkIt(col,formataTexto(dbSource.getColuna(col.getField()),col.getPattern()),session)+"</td>");
               }else{
                 String bf;
                 if (dbSource.getColuna(col.getField()) == null){
                   bf = "&nbsp;";
                 }else{
                   bf = dbSource.getColuna(col.getField()).toString();
                 }
                 saida.println(">"+linkIt(col,bf,session)+"</td>");
               }
               break;
             case 1: //Coluna é RowId
               saida.println(">"+ (dbSource.getregAtual()>0 ? dbSource.getregAtual()+"" : "INS") +"</td>");
               break;
             case 2: //Coluna é Edit Button
               saida.println(">"+dbSource.showButtonUpdate(session)+"</td>");
               break;
             case 3: //Coluna é Delete Button
               saida.println(">"+dbSource.showButtonDelete(session)+"</td>");
               break;
             case 4:  //Coluna é Static Text
               String bf;
               if (col.getField() == null){
                 bf = "&nbsp;";
               }else{
                 bf = col.getField();
               }
               saida.println(">"+linkIt(col,bf,session)+"</td>");
               break;
             case 5:  //Coluna é Input Check
               String cf;
               if (dbSource.getColuna(col.getField()) == null){
                 cf = "N";
               }else{
                 cf = dbSource.getColuna(col.getField()).toString();
               }
               saida.println(">"+checkIt(col,cf,session)+"</td>");
               break;
           }
           /*
           if (col.getField().equals("~%RowId%~")) saida.println(">"+ (dbSource.getregAtual()>0 ? dbSource.getregAtual()+"" : "INS") +"</td>");
           else if (col.getField().equals("~%DelBtn%~")) saida.println(">"+dbSource.showButtonDelete(session)+"</td>");
           else if (col.getField().equals("~%EditBtn%~")) saida.println(">"+dbSource.showButtonUpdate(session)+"</td>");
           else if (col.getPattern()!=null){ //kind 0
             saida.println(">"+linkIt(col,formataTexto(dbSource.getColuna(col.getField()),col.getPattern()))+"</td>");
           }else{
             String bf;
             if (dbSource.getColuna(col.getField()) == null){
               bf = "&nbsp;";
             }else{
               bf = dbSource.getColuna(col.getField()).toString();
             }
             saida.println(">"+linkIt(col,bf)+"</td>");
           }
         */
         }
         saida.println("</tr>");
         isEven=!isEven;
       }
     }
     saida.println("<input type=\"HIDDEN\" name=\"TABREGFIM\" value=\"" + ultimo + "\">");
     saida.println("</table></td></tr></table>");
//--------------------------------------------------------------
     //imprimi os botoes de navegação
     saida.println(dbSource.showToolbar(session));

     if (showFooter){
       saida.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td class=\"txtChumbo\" align=\"right\">");
       if (footerText == null){
         saida.println("Registro Atual=" + dbSource.getregAtual()+"</td>");
       }else{
         saida.println(footerText);
       }
       saida.println("</tr></table>");
     }
  }
}


