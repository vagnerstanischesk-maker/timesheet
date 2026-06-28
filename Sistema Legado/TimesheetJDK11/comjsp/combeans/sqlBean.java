package combeans;

//import java.io.*;
//import java.lang.*;
import java.util.Vector;
//import java.text.*;
import java.sql.Connection;
//import oracle.jdbc.*; 
import oracle.jdbc.*; 
import java.sql.*;
import java.sql.*;
import javax.servlet.http.*;

import java.sql.Types.*;


public class sqlBean {

   private conexaoBean iconexaobean = null;
   private PreparedStatement pstm = null;
   private Vector vet_tipoparametros    = new Vector();
   private Vector vet_valorparametros   = new Vector();
   private int rowsAltered = 0;

  public String getVersao()
  {   return("25/02/2002");
  }
   public void addParametro(String tipo, Object valor) {
      vet_tipoparametros.addElement(tipo);
      vet_valorparametros.addElement(valor);
   }
   public int getrowsAltered() {
      return rowsAltered;
   }

   private void setaParametros()
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
                 //  2001-02-03
                 //  03/02/2001
                 //  3/2/2001
                 //  3/2/2001 11:12:13
                 if(sdata.indexOf("/") > 0)
                 {  //a data está no formato brasileiro, devo converter para o formato americano:
                    String sdia   = "";
                    String smes   = "";
                    String sano   = "";
                    String sresto = "";
                    int passo = 1;
                    for(int i2=0; i2 < sdata.length(); i2++)
                    {
                       if(passo == 1)
                       {  if(sdata.substring(i2, i2+1).equals("/"))
                             passo = 2;
                          else
                             sdia += sdata.substring(i2, i2+1);
                       }
                       else if(passo == 2)
                       {  if(sdata.substring(i2, i2+1).equals("/"))
                             passo = 3;
                          else
                             smes += sdata.substring(i2, i2+1);
                       }
                       else if(passo == 3)
                       {  if(sdata.substring(i2, i2+1).equals(" "))
                             passo = 4;
                          else
                             sano += sdata.substring(i2, i2+1);
                       }
                       else if(passo == 4)
                       {  sresto += sdata.substring(i2, i2+1);
                       }
                    }
                    if(Integer.parseInt(sano) < 100)
                       if(Integer.parseInt(sano) < 50)
                          sano = "20" + sano;
                       else
                          sano = "19" + sano;
                    while (smes.length() < 2)
                       smes = "0" + smes;
                    while (sdia.length() < 2)
                       sdia = "0" + sdia;
                    sdata = sano + "-" + smes + "-" + sdia + " " + sresto;
                    sdata = sdata.trim();
                 }

                 //yyyy-mm-dd hh:mm:ss.fffffffff
                 if(sdata.length() == 16)
                    sdata += ":00";
                 if(sdata.length() == 10)
                    sdata += " 00:00:00";
                 if(sdata.length() == 19)
                    sdata += "";
                 if(sdata.length() >= 20)
                 {   while(sdata.length() < 29)
                        sdata += "0";
                 }
                 //DATE data = new DATE( sdata );
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

   public void setsql(conexaoBean conn, String sql)
          throws SQLException, Exception  {
      vet_tipoparametros.removeAllElements();
      vet_valorparametros.removeAllElements();
      //pstm = (PreparedStatement) conn.getConnection().prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      iconexaobean = conn;
      pstm = (PreparedStatement) conn.getConnection().prepareStatement(sql);
   }

   public void execute() throws SQLException, Exception {
      if(vet_tipoparametros.size() > 0)
        setaParametros();

      try {
         rowsAltered = pstm.executeUpdate();
      }
      catch (SQLException e) {
         try { iconexaobean.rollback(); }
         catch (Exception ignored) { System.out.println("sqlBean: Erro ao efetuar rollback(1):" + new java.util.Date() + " " + ignored + "." ); }
         throw e;
       }
      catch (Exception ex) {
         try { iconexaobean.rollback(); }
         catch (Exception ignored) { System.out.println("sqlBean: Erro ao efetuar rollback(3):" + new java.util.Date() + " " + ignored + "." ); }
         throw ex;
       }
   }

   public void fechar()
          throws SQLException, Exception {
      vet_tipoparametros.removeAllElements();
      vet_valorparametros.removeAllElements();
      if(pstm != null)
         pstm.close();
   }
}

