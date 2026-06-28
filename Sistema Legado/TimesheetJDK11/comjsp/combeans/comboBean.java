
package combeans;

import java.io.*;
import java.io.PrintWriter;
//2106 import oracle.jbo.*;
//2106 import oracle.jdeveloper.html.*;
import java.util.*;
import java.sql.*;
import oracle.jdbc.*; 
import javax.servlet.jsp.JspWriter;
import java.sql.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.sql.Types.*;

public class comboBean {

// variaveis de configuracao da combo
  private String nome = "";
  private int size = 0;
  private boolean multiple = false;
  private String selected = "";
  private int qtde_colunas = 0;
  private String separador = "";

// variaveis de configuracao da combo em javascript
  private String script;

// variaveis estaticas na combo
  private String descricao = "";
  private Vector vet_descricao = new Vector();
  private Vector vet_valor     = new Vector();

// variaveis para query da combo
  private String sql = "";
  private String sqlAnterior = "";
  private boolean parametro = false;
  private Vector vet_tipoparametros    = new Vector();
  private Vector vet_valorparametros   = new Vector();
  private ResultSet rs = null;
  private PreparedStatement pstm = null;
  private conexaoBean iconexaobean = null;


  public void setSql(String newSql, conexaoBean conn, boolean param, int pQtde_colunas, String pSeparador) {
    sql = newSql;
    iconexaobean = conn;
    parametro = param;
    qtde_colunas = pQtde_colunas;
    separador = pSeparador;
  }

  public void setSql(String newSql, conexaoBean conn, boolean param) {
    sql = newSql;
    iconexaobean = conn;
    parametro = param;
  }

  public void inicializa(String pNome, String pDesc, String pSelected, int pSize, boolean pMultiple, String pScript) {
     nome = pNome;
     size = pSize;
     multiple = pMultiple;
     descricao = pDesc;
     selected = pSelected;
     script = pScript;
  }

  public void removeCampos() {
     vet_valor.removeAllElements();
     vet_descricao.removeAllElements();
  }

  public void addCampos(Object valor, Object desc) {
     vet_valor.addElement(valor);
     vet_descricao.addElement(desc);
  }

  public void inicializa(String pNome, String pSelected, int pSize, boolean pMultiple, String pScript) {
     nome = pNome;
     size = pSize;
     multiple = pMultiple;
     selected = pSelected;
     script = pScript;
  }

  public void addParametros(int tipo, Object valor) {
     vet_tipoparametros.addElement(tipo+"");
     vet_valorparametros.addElement(valor);
  }

  public void removeParametros() {
     vet_tipoparametros.removeAllElements();
     vet_valorparametros.removeAllElements();
  }

  private void setaParametros(PreparedStatement pstm)
   throws SQLException, Exception
   {
      for(int i=0; i < vet_tipoparametros.size(); i++)
      {
         switch (Integer.parseInt((String)vet_tipoparametros.elementAt(i)))
         {
            case Types.ROWID: {
              if((vet_valorparametros.elementAt(i) == null) ||
                 (vet_valorparametros.elementAt(i).toString().equals("")))
                 pstm.setNull ((i + 1), Types.ROWID);
              else
                 pstm.setRowId((i + 1), (RowId)vet_valorparametros.elementAt(i));
              break;
            }
            case Types.INTEGER: {
              if((vet_valorparametros.elementAt(i) == null) ||
                 (vet_valorparametros.elementAt(i).toString().trim().equals("")))
                 pstm.setNull ((i + 1), Types.INTEGER);
              else
                 pstm.setInt((i + 1),Integer.parseInt(vet_valorparametros.elementAt(i).toString().trim()));
              break;
            }
            case Types.BIGINT: {
              if((vet_valorparametros.elementAt(i) == null) ||
                 (vet_valorparametros.elementAt(i).toString().trim().equals("")))
                 pstm.setNull ((i + 1), Types.BIGINT);
              else
                 pstm.setLong((i + 1), Long.parseLong(vet_valorparametros.elementAt(i).toString().trim()));
              break;
            }
            case Types.DECIMAL:
            case Types.NUMERIC:
            case Types.DOUBLE:
            case Types.REAL: {
              if((vet_valorparametros.elementAt(i) == null) ||
                 (vet_valorparametros.elementAt(i).toString().trim().equals("")))
                 pstm.setNull ((i + 1), Types.DOUBLE);
              else
                 pstm.setDouble((i + 1),Double.parseDouble(UtilBean.desformataNumero(vet_valorparametros.elementAt(i).toString().trim())));
              break;
            }
            case Types.FLOAT: {
              if((vet_valorparametros.elementAt(i) == null) ||
                 (vet_valorparametros.elementAt(i).toString().trim().equals("")))
                 pstm.setNull ((i + 1), Types.FLOAT);
              else
                 pstm.setFloat((i + 1),Float.parseFloat(UtilBean.desformataNumero(vet_valorparametros.elementAt(i).toString().trim())));
              break;
            }
            case Types.LONGVARCHAR: {
              if((vet_valorparametros.elementAt(i) == null) ||
                 (vet_valorparametros.elementAt(i).toString().equals("")))
                 pstm.setNull ((i + 1), Types.LONGVARCHAR);
              else
                 pstm.setLong((i + 1),Long.parseLong((String) vet_valorparametros.elementAt(i)));
              break;
            }
            case Types.VARCHAR: {
              if((vet_valorparametros.elementAt(i) == null) ||
                 (vet_valorparametros.elementAt(i).toString().equals("")))
                 pstm.setNull ((i + 1), Types.VARCHAR);
              else
                 pstm.setString((i + 1),(String) vet_valorparametros.elementAt(i));
              break;
            }
            case Types.DATE: {
              if((vet_valorparametros.elementAt(i) == null) ||
                 (vet_valorparametros.elementAt(i).toString().trim().equals("")) ||
                 (vet_valorparametros.elementAt(i).toString().equals(" ")))
                 pstm.setNull ((i + 1), Types.DATE);
              else
              {
                 String sdata = "" + vet_valorparametros.elementAt(i);
                 //yyyy-mm-dd hh:mm:ss.fffffffff
                 if(sdata.length() == 16)
                    //sdata += ":00.000000000";
                    sdata += ":00";
                 if(sdata.length() == 10)
                    //sdata += " 00:00:00.000000000";
                    sdata += " 00:00:00";
                 if(sdata.length() == 19)
                    //sdata += ".000000000";
                    sdata += "";
                 if(sdata.length() >= 20)
                 {   while(sdata.length() < 29)
                        sdata += "0";
                 }
                // DATE data = new DATE( sdata );
                  //java.sql.Date data = java.sql.Date.valueOf(sdata);
                 //pstm.setDate((i + 1), data);
                  java.sql.Timestamp data = java.sql.Timestamp.valueOf(sdata);
                  pstm.setTimestamp((i + 1), data);
              }
              break;
            }

            default: {
              // mensagem de erro
              Exception e = new Exception("Não foi definido um parâmetro!");
              throw e;
            }
         } //fim switch
      } //fim for
   }

  public void execute(JspWriter saida) throws SQLException, Exception{

    try{
     pstm = (PreparedStatement)iconexaobean.getConnection().prepareStatement(sql);
     sqlAnterior = sql;
     if( parametro )
       setaParametros(pstm);

     rs = (ResultSet)pstm.executeQuery();

     saida.print("<SELECT class=\"cmpSelect\" NAME=\"" +nome+ "\" SIZE=\"" +size+ "\" " );
     if(multiple) saida.print(" MULTIPLE ");
     saida.println( script + ">");

     if( vet_descricao.size() > 0 ) {
       for(int pos=0; pos < vet_descricao.size(); pos++) {
         if( selected.equals(vet_valor.elementAt(pos)) )
           saida.println("<OPTION class=\"cmpOption\" SELECTED VALUE=\"" + vet_valor.elementAt(pos) + "\">" + vet_descricao.elementAt(pos) + "</OPTION>" );
         else
           saida.println("<OPTION class=\"cmpOption\" VALUE=\"" + vet_valor.elementAt(pos) + "\">" + vet_descricao.elementAt(pos) + "</OPTION>");
       }
     }
     else if(! descricao.equals("") )
     {
       if( selected.equals("") )
         saida.println("<OPTION class=\"cmpOption\" SELECTED VALUE=\"\">" + descricao + "</OPTION>" );
       else
         saida.println("<OPTION class=\"cmpOption\" VALUE=\"\">" + descricao + "</OPTION>");
     }

     if( qtde_colunas > 1 ) {
       while( rs.next() )
       {
         //if( selected.equals(rs.getObject(1).toString()) )
         if( selected.equals(rs.getString(1)) )
         {
              //saida.print("<OPTION class=\"cmpOption\" SELECTED VALUE=\"" + rs.getObject(1) + "\">");
              saida.print("<OPTION class=\"cmpOption\" SELECTED VALUE=\"" + rs.getString(1) + "\">");
              for(int i=2; i <= qtde_colunas; i++)
                 saida.println(rs.getString(i) + separador);
                 //saida.println(rs.getObject(i) + separador);
              //saida.println(rs.getObject(qtde_colunas+1));
              saida.println(rs.getString(qtde_colunas+1));
              saida.println("</OPTION>");
         }
         else {
              //saida.print("<OPTION class=\"cmpOption\" VALUE=\"" + rs.getObject(1) + "\">");
              saida.print("<OPTION class=\"cmpOption\" VALUE=\"" + rs.getString(1) + "\">");
              for(int i=2; i <= qtde_colunas; i++)
                 saida.println(rs.getString(i) + separador);
                 //saida.println(rs.getObject(i) + separador);
              //saida.println(rs.getObject(qtde_colunas+1));
              saida.println(rs.getString(qtde_colunas+1));
              saida.println("</OPTION>");
         }
       }
     }
     else {
       while( rs.next() )
       {
         //if( selected.equals(rs.getObject(1).toString()) )
         if( selected.equals(rs.getString(1)) )
         {
              //saida.print("<OPTION class=\"cmpOption\" SELECTED VALUE=\"" + rs.getObject(1) + "\">");
              saida.print("<OPTION class=\"cmpOption\" SELECTED VALUE=\"" + rs.getString(1) + "\">");
              //saida.println(rs.getObject(2));
              saida.println(rs.getString(2));
              saida.println("</OPTION>");
         }
         else {
              //saida.print("<OPTION class=\"cmpOption\" VALUE=\"" + rs.getObject(1) + "\">");
              saida.print("<OPTION class=\"cmpOption\" VALUE=\"" + rs.getString(1) + "\">");
              //saida.println(rs.getObject(2));
              saida.println(rs.getString(2));
              saida.println("</OPTION>");
         }
       }
     }
     saida.println(" </SELECT>");
     }
     finally {
       if(rs!=null) rs.close();
       if(pstm!=null) pstm.close();
     }

    } // execute

  public void execute_sem_select(JspWriter saida) throws Exception {

    try{
     saida.print("<SELECT class=\"cmpSelect\" NAME=\"" +nome+ "\" SIZE=\"" +size+ "\" " );
     if(multiple) saida.print(" MULTIPLE ");
     saida.println( script + ">");

     if( vet_descricao.size() > 0 ) {
       for(int pos=0; pos < vet_descricao.size(); pos++) {
         if( selected.equals(vet_valor.elementAt(pos)) )
           saida.println("<OPTION class=\"cmpOption\" SELECTED VALUE=\"" + vet_valor.elementAt(pos) + "\">" + vet_descricao.elementAt(pos) + "</OPTION>" );
         else
           saida.println("<OPTION class=\"cmpOption\" VALUE=\"" + vet_valor.elementAt(pos) + "\">" + vet_descricao.elementAt(pos) + "</OPTION>");
       }
     }
     else if(! descricao.equals("") )
     {
       if( selected.equals("") )
         saida.println("<OPTION class=\"cmpOption\" SELECTED VALUE=\"\">" + descricao + "</OPTION>" );
       else
         saida.println("<OPTION class=\"cmpOption\" VALUE=\"\">" + descricao + "</OPTION>");
     }
     saida.println(" </SELECT>");
     }
     finally {
     }
  } // execute

  public String execute_sem_select() throws Exception {
    String saida = "";
    try{
     saida = "<SELECT class=\"cmpSelect\" NAME=\"" +nome+ "\" SIZE=\"" +size+ "\" " ;
     if(multiple) saida += " MULTIPLE ";
     saida += script + ">";

     if( vet_descricao.size() > 0 ) {
       for(int pos=0; pos < vet_descricao.size(); pos++) {
         if( selected.equals(vet_valor.elementAt(pos)) )
           saida += "<OPTION class=\"cmpOption\" SELECTED VALUE=\"" + vet_valor.elementAt(pos) + "\">" + vet_descricao.elementAt(pos) + "</OPTION>" ;
         else
           saida += "<OPTION class=\"cmpOption\" VALUE=\"" + vet_valor.elementAt(pos) + "\">" + vet_descricao.elementAt(pos) + "</OPTION>";
       }
     }
     else if(! descricao.equals("") )
     {
       if( selected.equals("") )
         saida += "<OPTION class=\"cmpOption\" SELECTED VALUE=\"\">" + descricao + "</OPTION>" ;
       else
         saida += "<OPTION class=\"cmpOption\" VALUE=\"\">" + descricao + "</OPTION>";
     }
     saida += " </SELECT>";
     }
    finally {
    }
    return (saida);
  } // execute
}

