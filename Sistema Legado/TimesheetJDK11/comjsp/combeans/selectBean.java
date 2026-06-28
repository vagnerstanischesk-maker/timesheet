package combeans;

//import java.io.*;
//import java.lang.*;
import java.util.Vector;
//import java.text.*;
import java.sql.Connection;
import oracle.jdbc.*; 
import java.sql.*;
import java.sql.*;
import javax.servlet.http.*;
import java.sql.Types.*;

public class selectBean {

   private PreparedStatement pstm = null;
   private ResultSet rs = null;
   private Vector vet_tipoparametros    = new Vector();
   private Vector vet_valorparametros   = new Vector();
   boolean SCROLLABLE = false;

  public String getVersao()
  {  return("12/12/2001");
  }

   public void addParametro(String tipo, Object valor) {
      vet_tipoparametros.addElement(tipo);
      vet_valorparametros.addElement(valor);
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
      pstm = (PreparedStatement) conn.getConnection().prepareStatement(sql,
                                                ResultSet.TYPE_FORWARD_ONLY,
                                                ResultSet.CONCUR_READ_ONLY);
      SCROLLABLE = false;
   }
   public void setsql(conexaoBean conn, String sql, boolean bScrollable)
     throws SQLException, Exception  {
      vet_tipoparametros.removeAllElements();
      vet_valorparametros.removeAllElements();
      if(! bScrollable)
      {  pstm = (PreparedStatement) conn.getConnection().prepareStatement(sql,
                                                ResultSet.TYPE_FORWARD_ONLY,
                                                ResultSet.CONCUR_READ_ONLY);
         SCROLLABLE = false;
      }
      else
      {  pstm = (PreparedStatement) conn.getConnection().prepareStatement(sql,
                                                ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                ResultSet.CONCUR_READ_ONLY);
         SCROLLABLE = true;
      }
   }

   public void execute() throws SQLException, Exception {
      if(vet_tipoparametros.size() > 0)
        setaParametros();

      rs = (ResultSet) pstm.executeQuery();
   }

   public boolean next()
          throws SQLException, Exception {
      return rs.next();
   }

   protected boolean PreenchePagina()
          throws SQLException, Exception {
      return rs.next();
   }

   public boolean previous()
          throws SQLException, Exception {
      return rs.previous();
   }
   public void beforeFirst()
          throws SQLException, Exception {
      rs.beforeFirst();
   }

   public Object getColuna(String coluna)
          throws SQLException, Exception {
      int tipo = 0;
      boolean achou = false;
      Object col = null;
//1806
       for(int i=1; i <= rs.getMetaData().getColumnCount(); i++)
       {  if(rs.getMetaData().getColumnName(i).equalsIgnoreCase(coluna))
          {  achou = true;
             tipo = rs.getMetaData().getColumnType(i);
             //classname = rs.getMetaData().getColumnClassName(i);
             //tam = rs.getMetaData().getColumnDisplaySize(i);
             //typename = rs.getMetaData().getColumnTypeName(i);
             //prec = rs.getMetaData().getPrecision(i);
             //scale = rs.getMetaData().getScale(i);
          }
       }
       switch (tipo)
       {
          case Types.NULL: {
             break;
          }
          case Types.ROWID: {
             col = rs.getRowId(coluna);
             break;
          }
          case Types.TINYINT:
          case Types.SMALLINT:
          case Types.INTEGER: {
             //col = ""+rs.getInt(coluna);
             col = rs.getString(coluna);
             break;
          }
          case Types.BIGINT: {
             //col = ""+rs.getLong(coluna);
             col = rs.getString(coluna);
             break;
          }
          case Types.DECIMAL:
          case Types.NUMERIC:
          case Types.DOUBLE:
          case Types.REAL: {
             //col = ""+rs.getDouble(coluna);
             col = rs.getString(coluna);
             break;
          }
          case Types.FLOAT: {
             //col = ""+rs.getFloat(coluna);
             col = rs.getString(coluna);
             break;
          }
          case Types.CHAR:
          case Types.VARCHAR:
          case Types.LONGVARCHAR: {
             col = rs.getString(coluna);
             break;
          }
          case Types.TIME:
          case Types.TIMESTAMP:
          case Types.DATE: {
             //col = rs.getDate(coluna);
             col = rs.getDate(coluna);
             break;
          }
          default: {
            // mensagem de erro
            Exception e = new Exception("Tipo de Dados não encontrado!");
            throw e;
          }
       }
       //Object col = rs.getObject(coluna);
       if (rs.wasNull())
          return null;
       else
       {
         switch (tipo)
         {
            case Types.NULL: {
               break;
            }
            case Types.ROWID: {
               col = rs.getRowId(coluna);
               break;
            }
              case Types.TINYINT:
              case Types.SMALLINT:
              case Types.INTEGER: {
               //col = ""+rs.getInt(coluna);
               col = rs.getString(coluna);
               break;
            }
            case Types.BIGINT: {
               //col = ""+rs.getLong(coluna);
               col = rs.getString(coluna);
               break;
            }
            case Types.DECIMAL:
            case Types.NUMERIC:
            case Types.DOUBLE:
            case Types.REAL: {
               //col = ""+rs.getDouble(coluna);
               col = rs.getString(coluna);
               break;
            }
            case Types.FLOAT: {
               //col = ""+rs.getFloat(coluna);
               col = rs.getString(coluna);
               break;
            }
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR: {
               col = rs.getString(coluna);
               break;
            }
            case Types.TIME:
            case Types.TIMESTAMP:
            case Types.DATE: {
               //col = rs.getDate(coluna);
               //col = rs.getDate(coluna).timestampValue();
                col = rs.getDate(coluna);
               break;
            }
            default: {
              // mensagem de erro
              Exception e = new Exception("Tipo de Dados não encontrado!");
              throw e;
            }
         }
         return col;
       }
       //return rs.getObject(coluna);
   }

/****public Object getColuna(int index)
          throws SQLException, Exception {
       int tipo = 0;
       boolean achou = false;
       Object col = null;
//1806
       tipo = rs.getMetaData().getColumnType(index);
       switch (tipo)
       {
          case Types.NULL: {
             break;
          }
          case Types.ROWID: {
             col = rs.getRowId(index);
             break;
          }
          case Types.TINYINT:
          case Types.SMALLINT:
          case Types.INTEGER: {
             //col = ""+rs.getInt(index);
             col = rs.getString(index);
             break;
          }
          case Types.BIGINT: {
             //col = ""+rs.getLong(index);
             col = rs.getString(index);
             break;
          }
          case Types.DECIMAL:
          case Types.NUMERIC:
          case Types.DOUBLE:
          case Types.REAL: {
             //col = ""+rs.getDouble(index);
             col = rs.getString(index);
             break;
          }
          case Types.FLOAT: {
             //col = ""+rs.getFloat(index);
             col = rs.getString(index);
             break;
          }
          case Types.CHAR:
          case Types.VARCHAR:
          case Types.LONGVARCHAR: {
             col = rs.getString(index);
             break;
          }
          case Types.TIME:
          case Types.TIMESTAMP:
          case Types.DATE: {
             //col = rs.getDate(index);
             col = rs.getDate(index);
             break;
          }
          default: {
            // mensagem de erro
            Exception e = new Exception("Tipo de Dados não encontrado!");
            throw e;
          }
       }
       //Object col = rs.getObject(index);
       if (rs.wasNull())
          return null;
       else
       {
         switch (tipo)
         {
            case Types.NULL: {
               break;
            }
            case Types.ROWID: {
               col = rs.getRowId(index);
               break;
            }
              case Types.TINYINT:
              case Types.SMALLINT:
              case Types.INTEGER: {
               //col = ""+rs.getInt(index);
               col = rs.getString(index);
               break;
            }
            case Types.BIGINT: {
               //col = ""+rs.getLong(index);
               col = rs.getString(index);
               break;
            }
            case Types.DECIMAL:
            case Types.NUMERIC:
            case Types.DOUBLE:
            case Types.REAL: {
               //col = ""+rs.getDouble(index);
               col = rs.getString(index);
               break;
            }
            case Types.FLOAT: {
               //col = ""+rs.getFloat(index);
               col = rs.getString(index);
               break;
            }
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR: {
               col = rs.getString(index);
               break;
            }
            case Types.TIME:
            case Types.TIMESTAMP:
            case Types.DATE: {
               //col = rs.getDate(index);
               col = rs.getDate(index).timestampValue();
               break;
            }
            default: {
              // mensagem de erro
              Exception e = new Exception("Tipo de Dados não encontrado!");
              throw e;
            }
         }
         return col;
       }
       //return rs.getObject(index);
   }
***/
   public void fechar()
          throws SQLException, Exception {
      vet_tipoparametros.removeAllElements();
      vet_valorparametros.removeAllElements();
      if(rs != null)
         rs.close();
      if(pstm != null)
         pstm.close();
   }

   public String showCampo(String nomeCampo,
                           verificaAcesso acesso,
                           HttpServletRequest request)
          throws java.io.IOException, Exception
   {
      String retorno = "";
      if (! acesso.verificaAcessoCampo(acesso.getJSP(request), nomeCampo).equals("I"))
      {  retorno += "" + UtilBean.getVerificaNull((getColuna(nomeCampo)));
      }
      return retorno;
   }

   public void removeParametros() {
      vet_tipoparametros.removeAllElements();
      vet_valorparametros.removeAllElements();
   }

}

