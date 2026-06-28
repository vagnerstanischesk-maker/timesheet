
package combeans;

import java.io.*;
import java.io.PrintWriter;
//2106 import oracle.jbo.*;
//2106 import oracle.jdeveloper.html.*;
import java.util.*;
import java.sql.*;
import oracle.jdbc.*; 
import javax.servlet.jsp.*;
import java.sql.*;
import javax.servlet.http.*;
import java.sql.Types.*;

//import oracle.sql.ROWID;

public class radioBean {

// variaveis de configuracao da combo
  private String nome = "";
  private String checked = "";

// variavel de configuracao em javascript
  private String script;

// variavel de complemento no final de cada linha HTML
  private String complemento;

// variaveis para query da combo
  private String sql = "";
  private String sqlAnterior = "";
  private boolean parametro = false;
  private Vector vet_tipoparametros    = new Vector();
  private Vector vet_valorparametros   = new Vector();
  private ResultSet rs = null;
  private PreparedStatement pstm = null;
  private conexaoBean iconexaobean = null;


  public void setSql(String newSql, conexaoBean conn, boolean param) {
    sql = newSql;
    iconexaobean = conn;
    parametro = param;
  }

  public void inicializa(String pNome, String pChecked, String pScript, String pComplemento) {
     nome = pNome;
     checked = pChecked;
     script = pScript;
     complemento = pComplemento;
  }

  public void addParametros(int tipo, Object valor) {
     vet_tipoparametros.addElement(tipo+"");
     vet_valorparametros.addElement(valor);
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
               //  DATE data = new DATE( sdata );
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

     while( rs.next() )
     {
       //if( checked.equals(rs.getObject(1).toString()) )
       if( checked.equals(rs.getString(1)) )
       {
            //saida.print("<INPUT class=\"cmpRadio\" TYPE=\"RADIO\" NAME=\"" + nome + "\" CHECKED VALUE=\"" + rs.getObject(1) + "\"" + script + ">");
            saida.print("<INPUT class=\"cmpRadio\" TYPE=\"RADIO\" NAME=\"" + nome + "\" CHECKED VALUE=\"" + rs.getString(1) + "\"" + script + ">");
            //saida.println(rs.getObject(2) + " " + complemento);
            saida.println(rs.getString(2) + " " + complemento);
       }
       else {
            //saida.print("<INPUT class=\"cmpRadio\" TYPE=\"RADIO\" NAME=\"" + nome + "\" VALUE=\"" + rs.getObject(1) + "\"" + script + ">");
            saida.print("<INPUT class=\"cmpRadio\" TYPE=\"RADIO\" NAME=\"" + nome + "\" VALUE=\"" + rs.getString(1) + "\"" + script + ">");
            //saida.println(rs.getObject(2) + " " + complemento);
            saida.println(rs.getString(2) + " " + complemento);
       }
     }
    }
    finally {
      if(rs!=null) rs.close();
      if(pstm!=null) pstm.close();
    }

    } // execute

  public void executePesquisa(JspWriter saida) throws SQLException, Exception{

    try{
     pstm = (PreparedStatement)iconexaobean.getConnection().prepareStatement(sql);
     sqlAnterior = sql;
     if( parametro )
       setaParametros(pstm);

     rs = (ResultSet)pstm.executeQuery();

     saida.print("<tr><td colspan=\"4\"><table><tr>");
     while( rs.next() )
     {
       //if( checked.equals(rs.getObject(1).toString()) )
       if( checked.equals(rs.getString(1)) )
       {
            //saida.print("<INPUT class=\"cmpRadio\" TYPE=\"RADIO\" NAME=\"" + nome + "\" CHECKED VALUE=\"" + rs.getObject(1) + "\"" + script + ">");
            saida.print("<td><INPUT class=\"cmpRadio\" TYPE=\"RADIO\" NAME=\"" + nome + "\" CHECKED VALUE=\"" + rs.getString(1) + "\"" + script + ">");
            //saida.println(rs.getObject(2) + " " + complemento);
            saida.println("</td><td width=\"3\"></td> "
               + " <td class=\"txtBlack\" valign=\"middle\"> "
               + rs.getString(2) + " " + complemento + "</td> ");
       }
       else {
            //saida.print("<INPUT class=\"cmpRadio\" TYPE=\"RADIO\" NAME=\"" + nome + "\" VALUE=\"" + rs.getObject(1) + "\"" + script + ">");
            saida.print("<td><INPUT class=\"cmpRadio\" TYPE=\"RADIO\" NAME=\"" + nome + "\" VALUE=\"" + rs.getString(1) + "\"" + script + ">");
            //saida.println(rs.getObject(2) + " " + complemento);
            saida.println("</td><td width=\"3\"></td> "
               + " <td class=\"txtBlack\" valign=\"middle\"> "
               + rs.getString(2) + " " + complemento + "</td> ");
       }
     }
     saida.println("</tr></table></td>");
    }
    finally {
      if(rs!=null) rs.close();
      if(pstm!=null) pstm.close();
    }

    } // execute
}


