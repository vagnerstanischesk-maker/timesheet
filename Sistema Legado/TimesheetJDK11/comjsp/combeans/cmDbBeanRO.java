/**************************************************************************
Empresa: Triscal
Autor: Christiano Chamma
Data: Janeiro de 2001

Descrição:
      Classe de acesso ao banco de dados somente para Leitura.
      Esta classe cria um ResultSet READ-ONLY e implementa paginação
    nesses registros (Scrollable). Esta classe também mostra
    a Toolbar que permite a navegação.

-----------------------------------------------------------------
Alterações:
   Data: Janeiro de 2001
   Autor: Alexandre
   Empresa: Triscal
   Alteração: Métodos e páginas de Pesquisa
-----------------------------------------------------------------


************ PENDÊNCIAS: *************
   1) Apagar (comentar) os System.out.print e verificar se existe alguma interrogação (?).
   2) Resolver problema da paginação por números quando se tem um RangeSize igual a 1 e
      pode-se inserir e/ou excluir registros (números de página negativos ?)
      
**************************************************************************/

package combeans;

import java.io.*;
import java.lang.*;
import java.math.*;
import java.util.*;
import java.text.*;
import java.sql.*;
import oracle.jdbc.*; 


//import oracle.jsp.dbutil.*;
//import oracle.jsp.event.*;
import java.io.PrintWriter;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
//import oracle.sql.DATE;
import java.sql.*;

//import oracle.sql.DATE;
import java.sql.Types.*;

public class cmDbBeanRO
extends Object implements HttpSessionBindingListener {
// implements JspScopeListener {

   //CursorBean curbean = null;
   PreparedStatement curbean = null;
   conexaoBean iconexaobean = null;

   String sql             = "";
   String sqlAnterior     = "";
   boolean bMontaWhere    = false;

   int regAtual = 0;
   int regInicioPagina = 0;
   int regContador = 0;
   int regAnterior = 0;
   int numPages = -1; //1708
   int pagina = 0;

   HttpSession session;
   
   boolean is_rsClosed = true;
   ResultSet rs               = null;

   JspWriter saida = null;

   //select
//   Vector vet_posparametros   = new Vector();
   Vector vet_tipoparametros    = new Vector();
   Vector vet_valorparametros   = new Vector();

   String PaginaAnterior = "";
   String PaginaSubmit = "";
   String PaginaEdit = "";

   String colunaPaginacao = "";
   int paginaA = 0;
   int paginaB = 0;
   int paginaC = 0;
   int paginaD = 0;
   int paginaE = 0;
   int paginaF = 0;
   int paginaG = 0;
   int paginaH = 0;
   int paginaI = 0;
   int paginaJ = 0;
   int paginaK = 0;
   int paginaL = 0;
   int paginaM = 0;
   int paginaN = 0;
   int paginaO = 0;
   int paginaP = 0;
   int paginaQ = 0;
   int paginaR = 0;
   int paginaS = 0;
   int paginaT = 0;
   int paginaU = 0;
   int paginaV = 0;
   int paginaW = 0;
   int paginaX = 0;
   int paginaY = 0;
   int paginaZ = 0;
   boolean achouProcuraValorPagina = true;
   String ParametroPaginacao = "";

  public String getVersao()
  {   return("14/02/2002");
  }
   public PreparedStatement getcurbean() {
      return curbean;
   }
   public void seticonexaobean(conexaoBean value) {
      iconexaobean = value;
      //select
//    //27 vet_posparametros.removeAllElements();
      //27 vet_tipoparametros.removeAllElements();
      //27 vet_valorparametros.removeAllElements();
   }
   public conexaoBean geticonexaobean() {
      return( iconexaobean );
   }

   public String getsql() {
      return sql;
   }

   public int getregAtual() {
      return regAtual;
   }

   public void setPaginaAnterior(String value) {
      PaginaAnterior = value;
   }

   public void setPaginaSubmit(String value) {
      PaginaSubmit = value;
   }

   public String getPaginaSubmit() {
      return PaginaSubmit;
   }

   public void setPaginaEdit(String value) {
      PaginaEdit = value;
   }
   
   public String getPaginaEdit() {
      return PaginaEdit;
   }
   


   /*********************** NAVEGACAO NO RESULT SET ************************/
   //????
   public ResultSet getrs() {
      return rs;
   }
   public void beforeFirst() {
      regAtual = 0;
   }
   public void absolute(int pnumreg) {
      regAtual = pnumreg;
   }
   public boolean next()
          throws SQLException, java.io.IOException, Exception
   {
      if(regAtual < 0) regAtual = 0;

      regAtual++;

      try {
         if(rs == null)
            return false;
         if(is_rsClosed)
            return false;
         rs.absolute(regAtual);
         if(rs.isAfterLast() || rs.isBeforeFirst())
            return false;
         if(rs.getRow() == 0)
            return false;
         return true;
      }
      catch (SQLException exsqlnext) {
         // o absolute falhou
         regAtual--;
         return false;
      }
      catch (Exception exnext) {
         // o absolute falhou
         regAtual--;
         return false;
      }
   }
/***public Object getColuna(int coluna)
          throws SQLException, java.io.IOException, Exception
   {
      if( regAtual <= 0)
         return null;

      String nomecoluna = "";

      nomecoluna = rs.getMetaData().getColumnName(coluna);
      if(rs == null)
         return null;
      if(is_rsClosed)
         return null;
      rs.absolute(regAtual);
      if(rs.isAfterLast() || rs.isBeforeFirst())
         return null;
      else
      {
         if(rs.getRow() == 0)
            return null;
         else
         {
            Object col = rs.getObject(coluna);
            if (rs.wasNull())
               return null;
            else
               return col;
         }
      }
   }
***/
   public Object getColuna(String nomecoluna)
          throws SQLException, java.io.IOException, Exception
   {  int tipo = 0;
      boolean achou = false;
      Object col = null;
      if(nomecoluna.equals("CMREADONLY"))
         return "S";

      if( regAtual <= 0)
         return null;

      if(rs == null)
         return null;
      if(is_rsClosed)
         return null;
      rs.absolute(regAtual);
      if(rs.isAfterLast() || rs.isBeforeFirst())
         return null;
      else
      {
         if(rs.getRow() == 0)
            return null;
         else
         {
            try {
//1806
               for(int i=1; i <= rs.getMetaData().getColumnCount(); i++)
               {  if(rs.getMetaData().getColumnName(i).equalsIgnoreCase(nomecoluna))
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
                     Object nada = rs.getObject(1);
                     break;
                  }
                  case Types.ROWID: {
                     col = rs.getRowId(nomecoluna);
                     break;
                  }
                  case Types.TINYINT:
                  case Types.SMALLINT:
                  case Types.INTEGER: {
                     //col = ""+rs.getInt(nomecoluna);
                     col = rs.getString(nomecoluna);
                     break;
                  }
                  case Types.BIGINT: {
                     //col = ""+rs.getLong(nomecoluna);
                     col = rs.getString(nomecoluna);
                     break;
                  }
                  case Types.DECIMAL:
                  case Types.NUMERIC:
                  case Types.DOUBLE:
                  case Types.REAL: {
                     //col = ""+rs.getDouble(nomecoluna);
                     col = rs.getString(nomecoluna);
                     break;
                  }
                  case Types.FLOAT: {
                     //col = ""+rs.getFloat(nomecoluna);
                     col = rs.getString(nomecoluna);
                     break;
                  }
                  case Types.CHAR:
                  case Types.VARCHAR:
                  case Types.LONGVARCHAR: {
                     col = rs.getString(nomecoluna);
                     break;
                  }
                  case Types.TIME:
                  case Types.TIMESTAMP:
                  case Types.DATE: {
                     //col = rs.getDate(nomecoluna);
                     col = rs.getDate(nomecoluna);
                     break;
                  }
                  default: {
                    // mensagem de erro
                    Exception e = new Exception("Tipo de Dados não encontrado!");
                    throw e;
                  }
               }
               //Object col = rs.getObject(nomecoluna);
               if (rs.wasNull())
                  return null;
               else
               {
                 switch (tipo)
                 {
                    case Types.NULL: {
                       Object nada = rs.getObject(1);
                       break;
                    }
                    case Types.ROWID: {
                       col = rs.getRowId(nomecoluna);
                       break;
                    }
                      case Types.TINYINT:
                      case Types.SMALLINT:
                      case Types.INTEGER: {
                       //col = ""+rs.getInt(nomecoluna);
                       col = rs.getString(nomecoluna);
                       break;
                    }
                    case Types.BIGINT: {
                       //col = ""+rs.getLong(nomecoluna);
                       col = rs.getString(nomecoluna);
                       break;
                    }
                    case Types.DECIMAL:
                    case Types.NUMERIC:
                    case Types.DOUBLE:
                    case Types.REAL: {
                       //col = ""+rs.getDouble(nomecoluna);
                       col = rs.getString(nomecoluna);
                       break;
                    }
                    case Types.FLOAT: {
                       //col = ""+rs.getFloat(nomecoluna);
                       col = rs.getString(nomecoluna);
                       break;
                    }
                    case Types.CHAR:
                    case Types.VARCHAR:
                    case Types.LONGVARCHAR: {
                       col = rs.getString(nomecoluna);
                       break;
                    }
                    case Types.TIME:
                    case Types.TIMESTAMP:
                    case Types.DATE: {
                       //col = rs.getDate(nomecoluna);
                       //col = rs.getDate(nomecoluna).timestampValue();
                       col = rs.getDate(nomecoluna);
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
            }
            catch (SQLException osql) {
               String msg = "" + osql;
               if(msg.indexOf("ORA-00904") >= 0)
               {   throw new SQLException(msg + ":" + nomecoluna);
               } else
               {   throw new SQLException(msg);
               }
            }
         }
      }
   }

   int getPosicaoInserir() {
      return 0;
   }
   /*********************** NAVEGACAO NO RESULT SET ************************/

   public void setsaida(JspWriter value) {
      saida = value;
      ParametroPaginacao = "";
   }

//   public void addParametro(String posparametro, String tipo, Object valor) {
   public void addParametro(String tipo, Object valor) {
//      vet_posparametros.addElement(posparametro);
      vet_tipoparametros.addElement(tipo);
      vet_valorparametros.addElement(valor);
   }
   public Vector getVetParametro(int ordem) {
      if(ordem == 1)
        return vet_tipoparametros;
      else
        return vet_valorparametros;
   }

   public void removeParametrosSelect() {
      vet_tipoparametros.removeAllElements();
      vet_valorparametros.removeAllElements();
   }

   void setaParametros(Vector pvet_tipoparametros,
                       Vector pvet_valorparametros,
                       PreparedStatement pstm,
                       String poperacao)
   throws SQLException, Exception
   {
      for(int i=0; i < pvet_tipoparametros.size(); i++)
      {
         switch (Integer.parseInt((String)pvet_tipoparametros.elementAt(i)))
         {
            case Types.ROWID: {
              if((pvet_valorparametros.elementAt(i) == null) ||
                 (pvet_valorparametros.elementAt(i).toString().equals("")))
                 pstm.setNull ((i + 1), Types.ROWID);
              else
              {  pstm.setRowId((i + 1), (RowId)pvet_valorparametros.elementAt(i));
              }
              break;
            }
            case Types.INTEGER: {
              if((pvet_valorparametros.elementAt(i) == null) ||
                 (pvet_valorparametros.elementAt(i).toString().trim().equals("")))
                 pstm.setNull ((i + 1), Types.INTEGER);
              else
              {
//System.out.println("parametro int=" + Integer.parseInt((String)pvet_valorparametros.elementAt(i)) + ".");

//System.out.println("#" + pvet_valorparametros.elementAt(i) + "#");
//System.out.println("#" + pvet_valorparametros.elementAt(i).toString().trim() + "#");

                 pstm.setInt((i + 1),Integer.parseInt(pvet_valorparametros.elementAt(i).toString().trim()));
              }
              break;
            }
            case Types.BIGINT: {
              if((pvet_valorparametros.elementAt(i) == null) ||
                 (pvet_valorparametros.elementAt(i).toString().trim().equals("")))
                 pstm.setNull ((i + 1), Types.BIGINT);
              else
              {
                 pstm.setLong((i + 1), Long.parseLong(pvet_valorparametros.elementAt(i).toString().trim()));
                 //0707 if(pstm == null)
                 //0707    pcurbean.setNUMBER((i + 1), new NUMBER(new java.math.BigInteger((String)pvet_valorparametros.elementAt(i))));
                 //0707 else
                 //0707    pstm.setNUMBER((i + 1), new NUMBER(new java.math.BigInteger((String)pvet_valorparametros.elementAt(i))));
              }
              break;
            }
            case Types.DECIMAL:
            case Types.NUMERIC:
            case Types.DOUBLE:
            case Types.REAL: {
              if((pvet_valorparametros.elementAt(i) == null) ||
                 (pvet_valorparametros.elementAt(i).toString().trim().equals("")))
                 pstm.setNull ((i + 1), Types.DOUBLE);
              else
              {  if(poperacao.equals("INS") || poperacao.equals("UPD"))
                    pstm.setDouble((i + 1),Double.parseDouble(pvet_valorparametros.elementAt(i).toString().trim()));
                 else
                 {  String nada = UtilBean.desformataNumero(pvet_valorparametros.elementAt(i).toString().trim());
                    pstm.setDouble((i + 1),Double.parseDouble(UtilBean.desformataNumero(pvet_valorparametros.elementAt(i).toString().trim())));
                 }
              }
              break;
            }
            case Types.FLOAT: {
              if((pvet_valorparametros.elementAt(i) == null) ||
                 (pvet_valorparametros.elementAt(i).toString().trim().equals("")))
                 pstm.setNull ((i + 1), Types.FLOAT);
              else
              {  if(poperacao.equals("INS") || poperacao.equals("UPD"))
                    pstm.setFloat((i + 1),Float.parseFloat(pvet_valorparametros.elementAt(i).toString().trim()));
                 else
                    pstm.setFloat((i + 1),Float.parseFloat(UtilBean.desformataNumero(pvet_valorparametros.elementAt(i).toString().trim())));
              }
              break;
            }
            case Types.VARCHAR: {
              if((pvet_valorparametros.elementAt(i) == null) ||
                 (pvet_valorparametros.elementAt(i).toString().trim().equals("")))
                 pstm.setNull ((i + 1), Types.VARCHAR);
              else
                 pstm.setString((i + 1),(String) pvet_valorparametros.elementAt(i));
              break;
            }
            case Types.DATE: {
              if((pvet_valorparametros.elementAt(i) == null) ||
                 (pvet_valorparametros.elementAt(i).toString().trim().equals("")) ||
                 (pvet_valorparametros.elementAt(i).toString().equals(" ")))
                 pstm.setNull ((i + 1), Types.DATE);
              else
              {
                 String sdata = "" + pvet_valorparametros.elementAt(i);
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
                 //DATE data = new DATE( sdata );
                  //java.sql.Date data = java.sql.Date.valueOf(sdata);
                 //pstm.setDate((i + 1), data);
                  java.sql.Timestamp data = java.sql.Timestamp.valueOf(sdata);
                  pstm.setTimestamp((i + 1), data);
              }
              break;
            }
/*
            case Types.LONGVARCHAR: {
            case TIME: {
              pcurbean.setTime(Integer.parseInt((String)pvet_posparametros.elementAt(i)),
            case TIMESTAMP: {
              pcurbean.setTimestamp(Integer.parseInt((String)pvet_posparametros.elementAt(i)),
            case SMALLINT: {
              pcurbean.setBigDecimal(Integer.parseInt((String)pvet_posparametros.elementAt(i)),
            case BINARY: {
              pcurbean.setBinaryStream(Integer.parseInt((String)pvet_posparametros.elementAt(i)),
            case BIT: {
              pcurbean.setByte(Integer.parseInt((String)pvet_posparametros.elementAt(i)),
            case BLOB: {
              pcurbean.setBLOB(Integer.parseInt((String)pvet_posparametros.elementAt(i)),
            case CHAR: {
              pcurbean.setCHAR(Integer.parseInt((String)pvet_posparametros.elementAt(i)),
            case CLOB: {
              pcurbean.setCLOB(Integer.parseInt((String)pvet_posparametros.elementAt(i)),
            case JAVA_OBJECT: {
              pcurbean.setObject(Integer.parseInt((String)pvet_posparametros.elementAt(i)),
           case REF: {
              pcurbean.setREF(Integer.parseInt((String)pvet_posparametros.elementAt(i)),
*/
            default: {
              // mensagem de erro
              Exception e = new Exception("Não foi definido um parâmetro!");
              throw e;
            }
         } //fim switch
      } //fim for
   }
    void setaParametros(Vector pvet_tipoparametros,
                        Vector pvet_valorparametros,
                        PreparedStatement pstm,
                        String poperacao,
                        HttpServletRequest  request,
                        HttpSession         session)
    throws SQLException, Exception
    {
        
        UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRO --- setaParametros - 001 - size=" + pvet_tipoparametros.size(), session, request, true);
        
       for(int i=0; i < pvet_tipoparametros.size(); i++)
       {
           UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRO --- setaParametros - 002 - i=" + i, session, request, true);
           UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRO --- setaParametros - 002 - pvet_tipoparametros=" + (String)pvet_tipoparametros.elementAt(i), session, request, true);
           UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRO --- setaParametros - 002 - pvet_valorparametros=" + (String)pvet_valorparametros.elementAt(i), session, request, true);
           
          switch (Integer.parseInt((String)pvet_tipoparametros.elementAt(i)))
          {
             case Types.ROWID: {
               if((pvet_valorparametros.elementAt(i) == null) ||
                  (pvet_valorparametros.elementAt(i).toString().equals("")))
                  pstm.setNull ((i + 1), Types.ROWID);
               else
               {  pstm.setRowId((i + 1), (RowId)pvet_valorparametros.elementAt(i));
               }
               break;
             }
             case Types.INTEGER: {
               if((pvet_valorparametros.elementAt(i) == null) ||
                  (pvet_valorparametros.elementAt(i).toString().trim().equals("")))
                  pstm.setNull ((i + 1), Types.INTEGER);
               else
               {
    //System.out.println("parametro int=" + Integer.parseInt((String)pvet_valorparametros.elementAt(i)) + ".");

    //System.out.println("#" + pvet_valorparametros.elementAt(i) + "#");
    //System.out.println("#" + pvet_valorparametros.elementAt(i).toString().trim() + "#");

                  pstm.setInt((i + 1),Integer.parseInt(pvet_valorparametros.elementAt(i).toString().trim()));
               }
               break;
             }
             case Types.BIGINT: {
               if((pvet_valorparametros.elementAt(i) == null) ||
                  (pvet_valorparametros.elementAt(i).toString().trim().equals("")))
                  pstm.setNull ((i + 1), Types.BIGINT);
               else
               {
                  pstm.setLong((i + 1), Long.parseLong(pvet_valorparametros.elementAt(i).toString().trim()));
                  //0707 if(pstm == null)
                  //0707    pcurbean.setNUMBER((i + 1), new NUMBER(new java.math.BigInteger((String)pvet_valorparametros.elementAt(i))));
                  //0707 else
                  //0707    pstm.setNUMBER((i + 1), new NUMBER(new java.math.BigInteger((String)pvet_valorparametros.elementAt(i))));
               }
               break;
             }
             case Types.DECIMAL:
             case Types.NUMERIC:
             case Types.DOUBLE:
             case Types.REAL: {
               if((pvet_valorparametros.elementAt(i) == null) ||
                  (pvet_valorparametros.elementAt(i).toString().trim().equals("")))
                  pstm.setNull ((i + 1), Types.DOUBLE);
               else
               {  if(poperacao.equals("INS") || poperacao.equals("UPD"))
                     pstm.setDouble((i + 1),Double.parseDouble(pvet_valorparametros.elementAt(i).toString().trim()));
                  else
                  {  String nada = UtilBean.desformataNumero(pvet_valorparametros.elementAt(i).toString().trim());
                     pstm.setDouble((i + 1),Double.parseDouble(UtilBean.desformataNumero(pvet_valorparametros.elementAt(i).toString().trim())));
                  }
               }
               break;
             }
             case Types.FLOAT: {
               if((pvet_valorparametros.elementAt(i) == null) ||
                  (pvet_valorparametros.elementAt(i).toString().trim().equals("")))
                  pstm.setNull ((i + 1), Types.FLOAT);
               else
               {  if(poperacao.equals("INS") || poperacao.equals("UPD"))
                     pstm.setFloat((i + 1),Float.parseFloat(pvet_valorparametros.elementAt(i).toString().trim()));
                  else
                     pstm.setFloat((i + 1),Float.parseFloat(UtilBean.desformataNumero(pvet_valorparametros.elementAt(i).toString().trim())));
               }
               break;
             }
             case Types.VARCHAR: {
               if((pvet_valorparametros.elementAt(i) == null) ||
                  (pvet_valorparametros.elementAt(i).toString().trim().equals("")))
                  pstm.setNull ((i + 1), Types.VARCHAR);
               else
                  pstm.setString((i + 1),(String) pvet_valorparametros.elementAt(i));
               break;
             }
             case Types.DATE: {
               UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRO --- setaParametros - DATE 01 - i=" + i, session, request, true);
               if((pvet_valorparametros.elementAt(i) == null) ||
                  (pvet_valorparametros.elementAt(i).toString().trim().equals("")) ||
                  (pvet_valorparametros.elementAt(i).toString().equals(" ")))
                  pstm.setNull ((i + 1), Types.DATE);
               else
               {
                   UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRO --- setaParametros - DATE 02 - i=" + i, session, request, true);
                  String sdata = "" + pvet_valorparametros.elementAt(i);
                   UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRO --- setaParametros - DATE 03 - sdata=" + sdata, session, request, true);
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
                  //DATE data = new DATE( sdata );
                  UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRO --- setaParametros - DATE 04 - sdata=" + sdata, session, request, true);
                   java.sql.Timestamp data = java.sql.Timestamp.valueOf(sdata);
                   UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRO --- setaParametros - DATE 05 - data=" + data.toString(), session, request, true);

                  pstm.setTimestamp((i + 1), data);
                   UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRO --- setaParametros - DATE 06 - sdata=" + sdata, session, request, true);
               }
               break;
             }
    /*
             case Types.LONGVARCHAR: {
             case TIME: {
               pcurbean.setTime(Integer.parseInt((String)pvet_posparametros.elementAt(i)),
             case TIMESTAMP: {
               pcurbean.setTimestamp(Integer.parseInt((String)pvet_posparametros.elementAt(i)),
             case SMALLINT: {
               pcurbean.setBigDecimal(Integer.parseInt((String)pvet_posparametros.elementAt(i)),
             case BINARY: {
               pcurbean.setBinaryStream(Integer.parseInt((String)pvet_posparametros.elementAt(i)),
             case BIT: {
               pcurbean.setByte(Integer.parseInt((String)pvet_posparametros.elementAt(i)),
             case BLOB: {
               pcurbean.setBLOB(Integer.parseInt((String)pvet_posparametros.elementAt(i)),
             case CHAR: {
               pcurbean.setCHAR(Integer.parseInt((String)pvet_posparametros.elementAt(i)),
             case CLOB: {
               pcurbean.setCLOB(Integer.parseInt((String)pvet_posparametros.elementAt(i)),
             case JAVA_OBJECT: {
               pcurbean.setObject(Integer.parseInt((String)pvet_posparametros.elementAt(i)),
            case REF: {
               pcurbean.setREF(Integer.parseInt((String)pvet_posparametros.elementAt(i)),
    */
             default: {
               // mensagem de erro
               UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRO --- setaParametros - erro", session, request, true);
               Exception e = new Exception("Não foi definido um parâmetro!");
               throw e;
             }
          } //fim switch
       } //fim for
       UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRO --- setaParametros - fim", session, request, true);
    }
// ------------------------Select-------------------------------------------------------
   boolean limpaHashTables = true;
   public void seleciona_so_array(HttpSession session, HttpServletRequest request)
          throws SQLException, java.io.IOException, Exception
   {  limpaHashTables = false;
      seleciona(session, request);
      limpaHashTables = true;
   }
   public void seleciona(HttpSession session, HttpServletRequest request)
          throws SQLException, java.io.IOException, Exception
   {
       boolean bDeveExecutarQuery = true;
       if (curbean == null)
       {  //curbean = (CursorBean) new CursorBean();
       }
       else
       {  if(! sql.equalsIgnoreCase(sqlAnterior))
          {  bDeveExecutarQuery = true;
             liberaRecursos();
             liberaRecursosLock();
             liberaRecursosUp();
             liberaRecursosDelLog();
             liberaRecursosIns();
             liberaRecursosDel();
          }
          else
          {  bDeveExecutarQuery = false;
          }
       }
       //Se o SQL anterior é igual ao SQL atual desta classe, o procedimento
       // lógico seria não executar a query, porém além de verificar o SQL, devo
       // verificar também a página anterior.
       //Caso a página anterior seja igual a página Submit, realmente não preciso
       // executar a query novamente, pois o usuário está trabalhando no mesmo conjunto
       // de registros. Caso contrário, devo executar novamente a query para ler os dados
       // mais atuais do banco de dados. Por exemplo, se o usuário clicou no botão
       // REFRESH/RELOAD de seu Internet Browser, devo executar a query novamente.
       if (! bDeveExecutarQuery)
       {
          if ((request.getParameter("NAVIGATE") == null) ||
              (request.getParameter("NAVIGATE").equals("")))
          {  if (! PaginaAnterior.equals(""))
             {   if ((! PaginaAnterior.equalsIgnoreCase(PaginaSubmit)) &&
                    (! PaginaAnterior.equalsIgnoreCase(PaginaEdit)))
                {  bDeveExecutarQuery = true;
                   liberaRecursos();
                   liberaRecursosLock();
                   liberaRecursosUp();
                   liberaRecursosDelLog();
                   liberaRecursosIns();
                   liberaRecursosDel();
                }
             }
          }
          else
          {  if (request.getParameter("NAVIGATE").equals("REFRESH"))
             {  bDeveExecutarQuery = true;
                liberaRecursos();
                liberaRecursosLock();
                liberaRecursosUp();
                liberaRecursosDelLog();
                liberaRecursosIns();
                liberaRecursosDel();
             }
          }
       }

       try {
         if(sql.equals(""))
         {  //devo verificar se já executou o sql anteriormente..
            varrecomHistQueryBean( session );
         }

         if((bDeveExecutarQuery)
         && (! sql.equals("")))
         {
            //curbean.setExecuteBatch(1);
            //saida.print("<BR>GetPreFetch Inicial=" + curbean.getPreFetch()); // => retorna 10
            // curbean.setPreFetch(5);
            //curbean.setQueryTimeout(int segundos);
            //curbean.setResultSetConcurrency(curbean.CONCUR_UPDATABLE);
            // curbean.setResultSetConcurrency(curbean.CONCUR_READ_ONLY);
            // curbean.setResultSetType(curbean.TYPE_SCROLL_INSENSITIVE);

            // Create a Prepared Statement
            // curbean.create( iconexaobean.getcbean(), CursorBean.PREP_STMT, sql);
//System.out.println("cmDBBeanRO, sql=" + sql);
            curbean = (PreparedStatement)iconexaobean.getConnection().prepareStatement(sql,
                                                       rs.TYPE_SCROLL_INSENSITIVE,
                                                       rs.CONCUR_READ_ONLY);
            // curbean.create ( iconexaobean.getcbean(), CursorBean.PLAIN_STMT, sql);
            sqlAnterior = sql;
String nada = sql;
//System.out.print("SELECT="+sql);
            setaParametros(vet_tipoparametros, vet_valorparametros, curbean, "SEL");
//          //27 vet_posparametros.removeAllElements();
            //27 vet_tipoparametros.removeAllElements();
            //27 vet_valorparametros.removeAllElements();

            //saida.print("<BR>GetPreFetch Depois Create=" + curbean.getPreFetch()); // => retorna 10

            //curbean.registerOutParameter(1,CURSOR);
            //curbean.executeUpdate ();
            //ResultSet rset=curbean.getCursor(1);
            is_rsClosed = true;
            // rs = (ResultSet)curbean.executeQuery();
            rs = (ResultSet)curbean.executeQuery();
            is_rsClosed = false;

            if(UtilBean.getJSP(request).toLowerCase().indexOf("submitpesquisa") >= 0)
               alocacomHistQueryBean( session, sql );

//System.out.println("EXECUTANDO A QUERY !");
            regAtual = 0;
            if(limpaHashTables)
               limpaHashtablesAtualizacao();
            else
               limpaHashTables = true;
            // rs.deleteRow();
            //saida.print("<BR>FetchSize do ResultSet=" + rs.getFetchSize());

if(rs.getWarnings() != null)
{
System.out.print("<BR>WARNING=" + rs.getWarnings().getMessage());
System.out.print("<BR>S=" + rs.getWarnings().getSQLState());
System.out.print("<BR>TS=" + rs.getWarnings().toString());
System.out.print("<BR>LM=" + rs.getWarnings().getLocalizedMessage());
rs.getWarnings().printStackTrace();
}

            //rs.insertRow();
            //boolean bof = rs.isFirst();
            //boolean eof = rs.isLast();
            //rs.moveToInsertRow();
            //boolean prox = rs.next();
            //rs.refreshRow();
            //boolean rd = rs.rowDeleted();
            //boolean ri = rs.rowInserted();
            //boolean ru = rs.rowUpdated();
            //rs.setFetchSize(5);
            //rs.updateInt (6, salarynovo);
            //rs.updateRow();

            //rs.getStatement().

            //out.println(translateToHTMLTable (rs));
            //while (rs.next()) out.print("<BR>" + rs.getString(2) );
            //rs.getRow();
            //rs.getStatement().getConnection().
            //rs.absolute(3);
            //rs.updateInt(6, 150);
            //rs.updateRow();
         }
         //rs.close(); is_rsClosed = true;
         //is_rsClosed = true;
         //curbean.close();
       }
       catch (SQLException e) {
         throw e;
       }
       catch (Exception ex) {
         throw ex;
       }
   }
   private void alocacomHistQueryBean( HttpSession session, String pSql ) {
      Vector vetcomHistQueryBean = new Vector();
      comHistQueryBean auxcomHistQueryBean;
      comHistQueryBean alocacomHistQueryBean = new comHistQueryBean();
      if(session.getAttribute("com_HistQuery") != null)
      {
         vetcomHistQueryBean = (Vector)session.getAttribute("com_HistQuery");
      }
      for(int i=0; i<vetcomHistQueryBean.size(); i++)
      {  //retira do vetor caso já exista...
         auxcomHistQueryBean = (comHistQueryBean)vetcomHistQueryBean.elementAt(i);
         if(auxcomHistQueryBean.getPaginaBrowse().equals(getPaginaBrowse()))
         {   vetcomHistQueryBean.removeElementAt(i);
         }
      }
      alocacomHistQueryBean.setPaginaBrowse( getPaginaBrowse() );
      alocacomHistQueryBean.setSQL( pSql );
      for(int i=0; i < vet_tipoparametros.size(); i++)
      {
         alocacomHistQueryBean.addParametro( ""+vet_tipoparametros.elementAt(i),
                                             vet_valorparametros.elementAt(i) );
      }
      vetcomHistQueryBean.addElement( alocacomHistQueryBean );
      session.setAttribute( "com_HistQuery", vetcomHistQueryBean );
   }
   private void varrecomHistQueryBean( HttpSession session ) {
      Vector vetcomHistQueryBean = new Vector();
      comHistQueryBean auxcomHistQueryBean;
      comHistQueryBean alocacomHistQueryBean = new comHistQueryBean();
      if(session.getAttribute("com_HistQuery") != null)
      {
         vetcomHistQueryBean = (Vector)session.getAttribute("com_HistQuery");
      }
      for(int i=0; i<vetcomHistQueryBean.size(); i++)
      {  //retira do vetor caso já exista...
         auxcomHistQueryBean = (comHistQueryBean)vetcomHistQueryBean.elementAt(i);
         if(auxcomHistQueryBean.getPaginaBrowse().equals(getPaginaBrowse()))
         {
            liberaRecursos();
            liberaRecursosLock();
            liberaRecursosUp();
            liberaRecursosDelLog();
            liberaRecursosIns();
            liberaRecursosDel();
            sql = auxcomHistQueryBean.getSQL();
            removeParametrosSelect();
            for(int j=0; j < auxcomHistQueryBean.vet_tipoparametros.size(); j++)
            {
               addParametro( ""+auxcomHistQueryBean.vet_tipoparametros.elementAt(j),
                             auxcomHistQueryBean.vet_valorparametros.elementAt(j));
            }
         }
      }
   }

   public void limpaHashtablesAtualizacao() {
   }

  public void liberaRecursos() {
     try {
        numPages = -1; //1708
        pagina = 0;
        colunaPaginacao = ""; //1708
        sqlAnterior = "";
        if(rs != null)
        {  try { rs.close(); }
           catch (Exception exclose) { 
              System.out.println("cmdbbeanRO: Erro ao fechar resultset:" + new java.util.Date() + " " + exclose + "." );
           }
        }
        rs = null;
        is_rsClosed = true;
        if(curbean != null)
           curbean.close();
     }
     catch (Exception ignored) {
     }
  }

  public void liberaRecursosLock() {
  }
  public void liberaRecursosUp() {
  }
  public void liberaRecursosDelLog() {
  }
  public void liberaRecursosDel() {
  }
  public void liberaRecursosIns() {
  }

  public void reset()
         throws SQLException, java.sql.SQLException, java.io.IOException, Exception
  {
     liberaRecursos();
     liberaRecursosLock();
     liberaRecursosUp();
     liberaRecursosDelLog();
     liberaRecursosIns();
     liberaRecursosDel();

     regAtual = 0;
     regInicioPagina = 0;
     regContador = 0;
     regAnterior = 0;
     numPages = -1; //1708
     pagina = 0;
     colunaPaginacao = ""; //1708

     sql = ""; //27
     sqlAnterior = ""; //27

     limpaHashtablesAtualizacao();
     setExclusaoLogica(false);
     removeParametrosSelect();
  }
  public void setExclusaoLogica(boolean value) {
  }

  public boolean verificaConsistencia(HttpSession session,
                              HttpServletResponse response,
                              String p_paginabrowse)
         throws java.io.IOException, Exception
  {
     if(! p_paginabrowse.equalsIgnoreCase(PaginaBrowse))
     {
        response.sendRedirect(session.getAttribute("COM_RN") + "jsp/com_referererror.jsp?e=nav");
        return false;
     }
     return true;
  }


   /************************ Metodos do LISTENER da session **********************/
   public void valueBound (HttpSessionBindingEvent event) {
//System.out.print("cmDbbean: VALUEBOUND!!!");
   }

   public synchronized void valueUnbound (HttpSessionBindingEvent event) {
//System.out.print("cmDbbeanRO: VALUEUNBOUND!!!");
java.util.Date data = new java.util.Date();
//System.out.print("cmdbbeanRO: VALUEUNBOUND:" + data + " " + data.getTime() + "." );
     try {
        reset();
        if(curbean != null) curbean.close();
     }
     catch (Exception ignored) {
        System.out.print("cmdbbeanRO: VALUEUNBOUND ERROR:" + ignored + "." );
     }
   }
   /************************ Metodos do LISTENER da session **********************/


/* ----------------------------------- TOOLBAR ----------------------------------- */
   int RangeSize = -1;
   boolean BotaoFirst = true;
   boolean BotaoPrevPage = true;
   boolean BotaoNextPage = true;
   boolean BotaoLast = true;
   boolean BotaoRefresh = true;
   boolean BotaoConsultar = true;
   boolean BotaoInserir = true;
   String PaginaBrowse = "";
   String PaginaInsert = "";
   String PaginaConsulta = "";

   public void setRangeSize(int value) {
      RangeSize = value;
   }
   public int getRangeSize() {
      return RangeSize;
   }
   public void setBotaoFirst(boolean value) {
      BotaoFirst = value;
   }
   public void setBotaoPrevPage(boolean value) {
      BotaoPrevPage = value;
   }
   public void setBotaoNextPage(boolean value) {
      BotaoNextPage = value;
   }
   public void setBotaoLast(boolean value) {
      BotaoLast = value;
   }
   public void setBotaoRefresh(boolean value) {
      BotaoRefresh = value;
   }
   public void setBotaoConsultar(boolean value) {
      BotaoConsultar = value;
   }
   public void setBotaoInserir(boolean value) {
      BotaoInserir = value;
   }
   public void setBotoes(boolean bFirst, boolean bPrevPage, boolean bNextPage,
                         boolean bLast, boolean bRefresh, boolean bConsultar,
                         boolean bInserir) {
      BotaoFirst = bFirst;
      BotaoPrevPage = bPrevPage;
      BotaoNextPage = bNextPage;
      BotaoLast = bLast;
      BotaoRefresh = bRefresh;
      BotaoConsultar = bConsultar;
      BotaoInserir = bInserir;
   }

   public void setPaginaBrowse(HttpSession p_session, String value)
          throws SQLException, java.sql.SQLException, Exception
   {
      //Caso a página Browse anterior não seja igual a página Browse que está sendo
      // setada agora, então devo limpar as variáveis do cmDbBean anteriormente setadas
      // pois estou abrindo uma nova "instância" do cmDbBean ...
      session = p_session;
      COM_DOMINIO = ""+session.getAttribute("COM_DOMINIO");
      if(! PaginaBrowse.equalsIgnoreCase(value))
         this.reset();
      PaginaBrowse = value;
   }
   public String getPaginaBrowse() {
      return PaginaBrowse;
   }
   public void setPaginaInsert(String value) {
      PaginaInsert = value;
   }
   public String getPaginaInsert() {
      return PaginaInsert;
   }
   public void setPaginaConsulta(String value) {
      PaginaConsulta = value;
   }
   public String getPaginaConsulta() {
      return PaginaConsulta;
   }


   public String showButtons(HttpSession session)
          throws SQLException, java.sql.SQLException, Exception
   {
      String sRetorno = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" >";

      if (BotaoConsultar)
      {
         sRetorno += "<tr><td> <img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"1\" height=\"1\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/botoes_novos/pagina_pesquisa.gif\" border=\"0\" alt = \"Ir para página de Pesquisa\"></td>"
                  +  "   <TD><A HREF=\"" + UtilBean.montaURL(session, PaginaConsulta, "") + "\" class=\"txtPretoLink\">Pesquisar </td></tr>";
      }
      if (BotaoInserir)
      {
         sRetorno += "<tr><td> <img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"1\" height=\"1\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/botoes_novos/novo_registro.gif\" border=\"0\" alt = \"Novo Registro\"></td>"
                  +  "   <TD><A HREF=\"" + PaginaInsert + "\" class=\"txtPretoLink\">Novo Registro </td></tr>";
      }
      if (BotaoRefresh)
      {
         sRetorno += "<tr><td> <img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"1\" height=\"1\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/botoes_novos/atualizar.gif\" border=\"0\" alt = \"Buscar novamente do banco de dados\"></td>"
                  +  "   <TD><A HREF=\"javascript:void gorefresh('" + PaginaBrowse + "')\" class=\"txtPretoLink\">Atualizar </td></tr>";
      }

      sRetorno += "<tr><td colspan=\"2\"> <img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"1\" height=\"7\">"
               + "</td></tr></table>"
               + "<INPUT TYPE=\"HIDDEN\" NAME=\"NAVIGATE\" VALUE=\"\">";

      return sRetorno;
   }

   public String showToolbar(HttpSession session)
          throws SQLException, java.sql.SQLException, Exception
   {
      String sRetorno = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" >"
                      + " <tr>"
                      + "  <td>"
                      + "   <img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"1\" height=\"25\"></td>";
      if (existePaginaAnterior() && BotaoFirst)
      {
         sRetorno += "   <td align=\"left\" class=\"txtBlack\"><img src=\"/portalcom/images" 
                  + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"5\" height=\"1\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") 
                  + "/images/botoes_novos/primeiro.gif\" border=\"0\" alt = \"Ir para o Primeiro Registro\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"3\" height=\"1\">"
                  + "<A HREF=\"javascript:void gofirst('" + PaginaBrowse + "')" + "\">"
                  +  "    Primeiro</A></TD>";
      }
      else
      {
         if (BotaoFirst)
         {
           sRetorno += "   <td align=\"left\" class=\"txtChumbo\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"5\" height=\"1\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/botoes_novos/primeiro.gif\" border=\"0\" alt = \"Ir para o Primeiro Registro\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"3\" height=\"1\">"
                    +  "    Primeiro</TD>";
         }
         else
         {
           sRetorno += "   <td align=\"left\" class=\"txtChumbo\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"5\" height=\"1\"></TD>";
         }
      }
      if (existePaginaAnterior() && BotaoPrevPage)
      {
         sRetorno += "   <td><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"1\" height=\"1\"></td> "
                  +  "   <td align=\"left\" class=\"txtBlack\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"5\" height=\"1\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/botoes_novos/anterior.gif\" border=\"0\" alt = \"Ir para Registros Anteriores\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"3\" height=\"1\">"
                  +  "<a href=\"" + "javascript:void goprevious('" + PaginaBrowse + "')" + "\">"
                  +  "       Anterior</a></td>";
      }
      else
      {
         if (BotaoPrevPage)
         {
           sRetorno += "   <td><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"1\" height=\"1\"></td> "
                    +  "   <td align=\"left\" class=\"txtChumbo\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"5\" height=\"1\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/botoes_novos/anterior.gif\" border=\"0\" alt = \"Ir para Registros Anteriores\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"3\" height=\"1\">"
                    +  "       Anterior</td>";
         }
         else
         {
           sRetorno += "   <td align=\"left\" class=\"txtChumbo\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"5\" height=\"1\"></TD>";
         }
      }
      if (existeProximaPagina() && BotaoNextPage)
      {
         sRetorno += "   <td><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"1\" height=\"1\"></td>"
                  +  "   <td align=\"right\" class=\"txtBlack\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"5\" height=\"1\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/botoes_novos/proximo.gif\" border=\"0\" alt = \"Ir para Próximos Registros\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"3\" height=\"1\"><a href=\"" + "javascript:void gonext('" + PaginaBrowse + "')" + "\">"
                  +  "     Próximo</a></td>";
      }
      else
      {
         if (BotaoNextPage)
         {
           sRetorno += "   <td><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"1\" height=\"1\"></td>"
                    +  "   <td align=\"right\" class=\"txtChumbo\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"5\" height=\"1\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/botoes_novos/proximo.gif\" border=\"0\" alt = \"Ir para Próximos Registros\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"3\" height=\"1\">"
                    +  "     Próximo</td>";
         }
         else
         {
           sRetorno += "   <td align=\"left\" class=\"txtChumbo\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"5\" height=\"1\"></TD>";
         }
      }
      if (existeProximaPagina() && BotaoLast)
      {
         sRetorno += "   <td><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"1\" height=\"1\"></td>"
                  +  "   <td align=\"right\" class=\"txtBlack\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"5\" height=\"1\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/botoes_novos/ultimo.gif\" border=\"0\" alt = \"Ir para o Último Registro\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"3\" height=\"1\"><a href=\"" + "javascript:void golast('" + PaginaBrowse + "')" + "\">"
                  +  "       Último</a></td>";
      }
      else
      {
         if (BotaoLast)
         {
           sRetorno += "   <td><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"1\" height=\"1\"></td>"
                    +  "   <td align=\"right\" class=\"txtChumbo\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"5\" height=\"1\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/botoes_novos/ultimo.gif\" border=\"0\" alt = \"Ir para o Último Registro\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"3\" height=\"1\">"
                    +  "       Último</td>";
         }
         else
         {
           sRetorno += "   <td align=\"left\" class=\"txtChumbo\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" border=\"0\" width=\"5\" height=\"1\"></TD>";
         }
      }
      sRetorno += "   </tr></table>";

      return sRetorno;
   }
   //segundo metodo da toolbar para chamar o evento clicouToolBar
   // toda vez que clicar em qualquer botao da toolbar.
   public String showToolbar(javax.servlet.http.HttpSession session, boolean click)
          throws SQLException, java.sql.SQLException, Exception
   {
      String sRetorno = "";
      if(click) {
        sRetorno = "<TABLE BORDER=\"0\" CELLPADDING=\"0\" CELLSPACING=\"0\" CLASS=\"clsToolBar\">"
                        + " <TR>"
                        + "  <TD>"
                        + "   <TD><IMG SRC=\"/portalcom/webapp"+session.getAttribute("COM_DOMINIO")+"/images/FNDGTBL.gif\" border=0></TD>";
        if (existePaginaAnterior() && BotaoFirst)
        {
           sRetorno += "   <TD class=\"txtPretoLink\"><A NAME=\"PRIMEIRO\" HREF=\"" + "javascript:void gofirst('" + PaginaBrowse + "')" + "\" onClick=\"return clicouToolBar(this)\">"
                    +  "       <img BORDER=\"0\" CLASS=\"vrImages\" ID=\"vrImages\" NAME=\"imEx\" src=\"/portalcom/webapp"+session.getAttribute("COM_DOMINIO")+"/images/firstrec.gif\" title = \"Ir para o Primeiro Registro\"  alt = \"Ir para o Primeiro Registro\"  ></A></TD>";
        }
        else
        {
           sRetorno += "   <TD class=\"txtPreto\">"
                    +  "       <img BORDER=\"0\" CLASS=\"vrImages\" ID=\"vrImages\" NAME=\"imEx\" src=\"/portalcom/webapp"+session.getAttribute("COM_DOMINIO")+"/images/firstrecd.gif\" title = \"Ir para o Primeiro Registro\"  alt = \"Ir para o Primeiro Registro\"  ></TD>";
        }
        if (existePaginaAnterior() && BotaoPrevPage)
        {
           sRetorno += "   <TD class=\"txtPretoLink\"><A NAME=\"ANTERIOR\" HREF=\"" + "javascript:void goprevious('" + PaginaBrowse + "')" + "\" onClick=\"return clicouToolBar(this)\">"
                    +  "       <img BORDER=\"0\" CLASS=\"vrImages\" ID=\"vrImages\" NAME=\"imEx\" src=\"/portalcom/webapp"+session.getAttribute("COM_DOMINIO")+"/images/prevpage.gif\" title = \"Ir para Registros Anteriores\" alt = \"Ir para Registros Anteriores\" ></A></TD>";
        }
        else
        {
           sRetorno += "   <TD class=\"txtPreto\">"
                    +  "       <img BORDER=\"0\" CLASS=\"vrImages\" ID=\"vrImages\" NAME=\"imEx\" src=\"/portalcom/webapp"+session.getAttribute("COM_DOMINIO")+"/images/prevpaged.gif\" title = \"Ir para Registros Anteriores\" alt = \"Ir para Registros Anteriores\" ></TD>";
        }
        if (existeProximaPagina() && BotaoNextPage)
        {
           sRetorno += "   <TD class=\"txtPretoLink\"><A NAME=\"PROXIMO\" HREF=\"" + "javascript:void gonext('" + PaginaBrowse + "')" + "\" onClick=\"return clicouToolBar(this)\">"
                    +  "       <img BORDER=\"0\" CLASS=\"vrImages\" ID=\"vrImages\" NAME=\"imEx\" src=\"/portalcom/webapp"+session.getAttribute("COM_DOMINIO")+"/images/nextpage.gif\" title = \"Ir para Próximos Registros\"   alt = \"Ir para Próximos Registros\"   ></A></TD>";
        }
        else
        {
           sRetorno += "   <TD class=\"txtPreto\">"
                    +  "       <img BORDER=\"0\" CLASS=\"vrImages\" ID=\"vrImages\" NAME=\"imEx\" src=\"/portalcom/webapp"+session.getAttribute("COM_DOMINIO")+"/images/nextpaged.gif\" title = \"Ir para Próximos Registros\"   alt = \"Ir para Próximos Registros\"   ></TD>";
        }
        if (existeProximaPagina() && BotaoLast)
        {
           sRetorno += "   <TD class=\"txtPretoLink\"><A NAME=\"ULTIMO\" HREF=\"" + "javascript:void golast('" + PaginaBrowse + "')" + "\" onClick=\"return clicouToolBar(this)\">"
                    +  "       <img BORDER=\"0\" CLASS=\"vrImages\" ID=\"vrImages\" NAME=\"imEx\" src=\"/portalcom/webapp"+session.getAttribute("COM_DOMINIO")+"/images/lastrec.gif\"   title = \"Ir para o Último Registro\"    alt = \"Ir para o Último Registro\"    ></A></TD>";
        }
        else
        {
           sRetorno += "   <TD class=\"txtPreto\">"
                    +  "       <img BORDER=\"0\" CLASS=\"vrImages\" ID=\"vrImages\" NAME=\"imEx\" src=\"/portalcom/webapp"+session.getAttribute("COM_DOMINIO")+"/images/lastrecd.gif\"   title = \"Ir para o Último Registro\"    alt = \"Ir para o Último Registro\"    ></TD>";
        }

        if (BotaoRefresh)
        {
           //separador:
           sRetorno += "   <TD><img BORDER=\"0\" CLASS=\"vrImages\" ID=\"vrImages\" NAME=\"imEx\" src=\"/portalcom/webapp"+session.getAttribute("COM_DOMINIO")+"/images/FNDIWDVD.gif\" ></TD>";
           sRetorno += "   <TD class=\"txtPretoLink\"><A NAME=\"REFRESH\" HREF=\"" + "javascript:void gorefresh('" + PaginaBrowse + "')" + "\" onClick=\"return clicouToolBar(this)\">"
                    +  "       <img BORDER=\"0\" CLASS=\"vrImages\" ID=\"vrImages\" NAME=\"imEx\" src=\"/portalcom/webapp"+session.getAttribute("COM_DOMINIO")+"/images/refresh.gif\"   title = \"Buscar novamente do banco de dados\"    alt = \"Buscar novamente do banco de dados\"    ></A></TD>";
        }
        if (BotaoConsultar)
        {
           //separador:
           sRetorno += "   <TD><img BORDER=\"0\" CLASS=\"vrImages\" ID=\"vrImages\" NAME=\"imEx\" src=\"/portalcom/webapp"+session.getAttribute("COM_DOMINIO")+"/images/FNDIWDVD.gif\" ></TD>";
           sRetorno += "   <TD class=\"txtPretoLink\"><A NAME=\"CONSULTA\" HREF=\"" + UtilBean.montaURL(session, PaginaConsulta, "") + "\" onClick=\"return clicouToolBar(this)\">"
                    +  "       <img BORDER=\"0\" CLASS=\"vrImages\" ID=\"vrImages\" NAME=\"imEx\" src=\"/portalcom/webapp"+session.getAttribute("COM_DOMINIO")+"/images/browse.gif\"   title = \"Ir para página de Pesquisa\"    alt = \"Ir para página de Pesquisa\"    ></A></TD>";
        }
        if (BotaoInserir)
        {
           //separador:
           sRetorno += "   <TD><img BORDER=\"0\" CLASS=\"vrImages\" ID=\"vrImages\" NAME=\"imEx\" src=\"/portalcom/webapp"+session.getAttribute("COM_DOMINIO")+"/images/FNDIWDVD.gif\" ></TD>";
           sRetorno += "   <TD class=\"txtPretoLink\"><A NAME=\"INSERIR\" HREF=\"" + PaginaInsert + "\" onClick=\"return clicouToolBar(this)\">"
                    +  "       <img BORDER=\"0\" CLASS=\"vrImages\" ID=\"vrImages\" NAME=\"imEx\" src=\"/portalcom/webapp"+session.getAttribute("COM_DOMINIO")+"/images/inserir.gif\"   title = \"Novo Registro\"    alt = \"Novo Registro\"    ></A></TD>";
        }

        sRetorno += "   <TD><IMG SRC=\"/portalcom/webapp"+session.getAttribute("COM_DOMINIO")+"/images/FNDGTBR.gif\" border=0></TD>"
                 +  "  </TD>"
                 +  " </TR>"
                 +  "</TABLE>";
        sRetorno += "<INPUT TYPE=\"HIDDEN\" NAME=\"NAVIGATE\" VALUE=\"\">";
      }

      return sRetorno;
   }

   public void PreparaInicioPagina(HttpServletRequest request)
          throws SQLException, java.sql.SQLException, Exception
   {
String debug = request.getParameter("NAVIGATE") + "";
      achouProcuraValorPagina = true;
      regContador = 0;
      if (RangeSize == -1)
      {  regInicioPagina = 0;
         regAtual = getPosicaoInserir();
      }
      else
      {
         if ((request.getParameter("NAVIGATE") == null) ||
             (request.getParameter("NAVIGATE").equals("")))
         {
            if (RangeSize == 1)
            { regInicioPagina = getPosicaoInserir();
              regAtual = getPosicaoInserir();
            }
            else
            { regInicioPagina = 0;
              regAtual = getPosicaoInserir();
            }
         }
         else
         {
            if (request.getParameter("NAVIGATE").equals("FIRST"))
            {
               if (RangeSize == 1)
               { regInicioPagina = getPosicaoInserir();
                 regAtual = getPosicaoInserir();
               }
               else
               { regInicioPagina = 0;
                 regAtual = getPosicaoInserir();
               }
            }

            if (request.getParameter("NAVIGATE").equals("LAST"))
            {
               // procura pelo último registro:
               beforeFirst();
               regInicioPagina = regAtual;
               while(next())
               {
               }
               int regUltimo = regAtual;
//System.out.println("regUltimo=" + regUltimo);
               if (RangeSize == 722002)
               {  //01/02/2002---
                  //regAtual = regUltimo;
                  //int pagina = calculaPagina(regUltimo, -1);
                  regAtual = regUltimo - 2;
                  int pagina = calculaPagina(regUltimo-1, 0);
                  regAtual = getPosicaoInserir();  //01/02/2002
               }
               else
               {  int pagina = calculaPagina(regUltimo, 0);
                  if (RangeSize == 1) //14/02
                     regAtual = regInicioPagina; //14/02
                  else //14/02
                     regAtual = getPosicaoInserir();
               }
            }

            if (request.getParameter("NAVIGATE").equals("PREVPAGE"))
            {
               if (RangeSize == 1)
               {
                 if ((regAtual == 0) && (getRegistrosInseridos() > 0))
                    regAtual--;
                 int pagina = calculaPagina(regAtual, -1);
                 if ((regInicioPagina == -1) && (getRegistrosInseridos() > 0))
                    regInicioPagina--;
                 regAtual = regInicioPagina;
               }
               else
               { int pagina = calculaPagina(regAtual, -1);
                 if (RangeSize == 1) //14/02
                    regAtual = regInicioPagina; //14/02
                 else //14/02
                    regAtual = getPosicaoInserir();
               }
            }

            if (request.getParameter("NAVIGATE").equals("NEXTPAGE"))
            {
               //regAtual++;
               if (RangeSize == 722002)
               { regInicioPagina = regAtual;
                 //regAtual++;
                 regAtual = getPosicaoInserir(); //01/02/2002
               }
               else
               { int pagina = calculaPagina(regAtual, 1);
                 if (RangeSize == 1) //14/02
                    regAtual = regInicioPagina; //14/02
                 else //14/02
                    regAtual = getPosicaoInserir();
               }
            }

            if (request.getParameter("NAVIGATE").equals("ATUAL"))
            {
               if (RangeSize == 722002)
               { int pagina = 0;
                 pagina = calculaPagina(regAtual, 0);
                 regAtual = regInicioPagina;
                 regAtual = getPosicaoInserir(); //01/02/2002
               }
               else
               { int pagina = 0;
                 if(regAtual < 0)
                    pagina = calculaPagina(regAnterior, 0);
                 else
                    pagina = calculaPagina(regAtual, 0);
                 regAtual = getPosicaoInserir();
               }
            }
            if (request.getParameter("NAVIGATE").equals("SUBMITEDIT"))
            {  String op = "";
               if (request.getParameter("OP") != null)
                  op = request.getParameter("OP");
               else
                  op = request.getParameter("SUBMITEDIT");
               if ((op == null) ||
                   (op.equals("")) ||
                   (! op.equals("A")))
               {  // vai para o início
                  if (RangeSize == 1)
                  { regInicioPagina = getPosicaoInserir();
                    regAtual = getPosicaoInserir();
                  }
                  else
                  { regInicioPagina = 0;
                    regAtual = getPosicaoInserir();
                  }
               }
               else
               { //continua na mesma página
                  regAtual = getPosicaoInserir();
               }
            }
            if (request.getParameter("NAVIGATE").equals("SUBMITPESQUISA"))
            {  //mesma coisa do FIRST...
               if (RangeSize == 722002)
               { regInicioPagina = getPosicaoInserir();
                 regAtual = getPosicaoInserir();
               }
               else
               { regInicioPagina = 0;
                 regAtual = getPosicaoInserir();
               }
            }
            if (request.getParameter("NAVIGATE").equals("PAGE"))
            {
               int irpara = Integer.parseInt(request.getParameter("PAGE"));

               regAtual = getPosicaoInserir();
               int pagina = 0; //04/02/2002
               if (RangeSize == 722002) //04/02/2002
                  pagina = calculaPagina(regAtual, 0); //04/02/2002
               else //04/02/2002
                  pagina = calculaPagina(regAtual, 0) + 1;
               if(irpara <= numPages)
               {  while(pagina != irpara)
                  {  regAtual++;
                     if (RangeSize == 722002)
                        pagina = calculaPagina(regAtual, 0);
                     else
                        pagina = calculaPagina(regAtual, 0) + 1;
                  }
               }
               if (RangeSize == 1) //14/02
                  regAtual = regInicioPagina; //14/02
               else //14/02
                  regAtual = getPosicaoInserir();
            }
            if (request.getParameter("NAVIGATE").equals("VALUE"))
            {
               String irpara = request.getParameter("COM___FIND");

               regAtual = getPosicaoInserir();
               boolean achou = false;
               while( (! achou) && (next()) )
               {  if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith(irpara.toUpperCase()))
                  {  achou = true;
                     int pagina = 0; //04/02/2002
                     if (RangeSize == 722002) //04/02/2002
                        pagina = calculaPagina(regAtual, 0); //04/02/2002
                     else //04/02/2002
                        pagina = calculaPagina(regAtual, 0) + 1;
                  }
               }
               if(! achou)
               {  achouProcuraValorPagina = false;
                  //vai para o início
                   if (RangeSize == 722002)
                   { regInicioPagina = getPosicaoInserir();
                     regAtual = getPosicaoInserir();
                   }
                   else
                   { regInicioPagina = 0;
                     regAtual = getPosicaoInserir();
                   }
               }
               else
               {  achouProcuraValorPagina = true;
                  if (RangeSize == 1) //14/02
                     regAtual = regInicioPagina; //14/02
                  else //14/02
                     regAtual = getPosicaoInserir();
               }
            }
         }
      }
      regContador = 0;
   }

   int getRegistrosInseridos() {
      return 0;
   }

   public boolean PreenchePagina()
          throws SQLException, java.sql.SQLException, Exception
   {
      regAnterior = regAtual;

      if (RangeSize == -1)
      {  if ((regAtual == 0) && (getRegistrosInseridos() > 0))
         {   regAtual++;
             return true;
         }
         else
            return next();
      }
      if (RangeSize == 722002)
      {
//System.out.println("PREENCHEPAGINA, REGATUAL=" + regAtual);
         //0if ((regAtual == -1) && (getRegistrosInseridos() > 0))
         //0   regAtual++;
         //01/02/2002---regContador++;
         //01/02/2002---if(regContador > RangeSize)
         //01/02/2002---   return false;
         //01/02/2002---else
         //01/02/2002---   return next();

        if ((regAtual == -1) && (getRegistrosInseridos() > 0))
           regAtual++;
        regContador++;
        if(regContador > RangeSize)
           return false;
        else
        {  regAtual = regInicioPagina;
           return next();
        }

/*********if(regAtual < 0)
          {  if(getPosicaoInserir() >= -1)
             {  //nao existe nenhum registro inserido
                regAtual = regInicioPagina;
                regContador++;
                return next();
             }
             else
             {  if(regAtual == -1)
                {  if(regAtual < regInicioPagina)
                      regAtual = regInicioPagina;
                   regContador++;
                   return next();
                }
                else
                   return next();
             }
          }
          else if( regAtual == 0)
          {  //já varri os registros inseridos e cheguei no 0, agora devo varrer a página:
             if(regAtual < regInicioPagina)
                regAtual = regInicioPagina;
             regContador++;
             if(regContador > RangeSize)
                return false;
             else
                return next();
          }
          else
          {  //regAtual > 0
             regContador++;
             if(regContador > RangeSize)
                return false;
             else
                return next();
          }
*********/
      } //fim if (rangesize==1)
//System.out.println("regInicioPagina=" + regInicioPagina);
//System.out.println("regAtual=" + regAtual);
//System.out.println("regContador=" + regContador);

      //varrer primeiro os inseridos depois os outros...
      if(regAtual < 0)
      {  if(getPosicaoInserir() >= -1)
         {  //nao existe nenhum registro inserido
            regAtual = regInicioPagina;
            regContador++;
            return next();
         }
         else
         {  if(RangeSize==1)
            {  regContador++;
               if(regContador > RangeSize)
                  return false;
               else
                  return next();
            }
            else
               return next();
         }
      }
      else if( regAtual == 0)
      {  //já varri os registros inseridos e cheguei no 0, agora devo varrer a página:
         if(regAtual < regInicioPagina)
            regAtual = regInicioPagina;
         regContador++;
         if(regContador > RangeSize)
            return false;
         else
            return next();
      }
      else
      {  //regAtual > 0
         regContador++;
         if(regContador > RangeSize)
            return false;
         else
            return next();
      }
   }

   public boolean existePaginaAnterior()
          throws SQLException, java.sql.SQLException, Exception
   {
      if (RangeSize == -1)
         return false;
//System.out.println("regAtual=" + regAtual);
      if (RangeSize == 1)
      {
//System.out.println("getPosicaoInserir()=" + getPosicaoInserir());
         if(regAtual <= getPosicaoInserir())
            return false;
         if((getPosicaoInserir() >= -1) && (regAtual <= 0))
            return false;

         int aux1 = regInicioPagina;
         int aux2 = regAtual;
         int auxregAnterior = 0;
         int contador = 0;
         regAtual = getPosicaoInserir();
         if(getRegistrosInseridos() <= 0)
            if(aux1 == 0)
               aux1 = -1;
         boolean bSai = false;
         while((regAtual <= aux1) && (! bSai))
         {   auxregAnterior = regAtual;
             if(next())
                contador++;
             else
                bSai = true;
         }
         if(contador > 1)
         {  regInicioPagina = aux1;
            regAtual = aux2;
            return true;
         }
         if(contador <= 1)
         {  regInicioPagina = aux1;
            regAtual = aux2;
            return false;
         }
      }

      if (regInicioPagina < RangeSize)
         return false;
      else
         return true;
   }
   public boolean existeProximaPagina()
          throws Exception
   {
      if (RangeSize == -1)
         return false;
//System.out.println("regAtual1=" + regAtual);

      int aux1 = regInicioPagina;
      int aux2 = regAtual;
      int aux3 = regContador;

      if (RangeSize == 722002)
      {  if (next())
         {  //01/02/2002---if (next())
            //01/02/2002---{
               regInicioPagina = aux1;
               regAtual = aux2;
               regContador = aux3;
               return true;
            //01/02/2002---}
            //01/02/2002---else
            //01/02/2002---{  regInicioPagina = aux1;
            //01/02/2002---   regAtual = aux2;
            //01/02/2002---   regContador = aux3;
            //01/02/2002---   return false;
            //01/02/2002---}
         }
         else
         {  regInicioPagina = aux1;
            regAtual = aux2;
            regContador = aux3;
            return false;
         }
      }

      while(PreenchePagina())
      {
      }
      if (next())
      {  regInicioPagina = aux1;
         regAtual = aux2;
         regContador = aux3;
         return true;
      }
      else
      {  regInicioPagina = aux1;
         regAtual = aux2;
         regContador = aux3;
         return false;
      }
   }

   int calculaPagina( int pregistro, int psetaInicio )
       throws SQLException, java.sql.SQLException, Exception
   {
      //psetaInicio pode ser -1, 0 ou 1. Caso -1, devo apontar para a página anterior da
      // página do pregistro. Caso 0, deve apontar para a mesma página. Caso 1,
      // deve apontar para a próxima página relativa ao pregistro.
      int auxregAtual = regAtual;
      int pagina = 0;
      int contador = 0;
      int auxInicioPagina = 0;
      int auxInicioPaginaAnterior = 0;
      boolean bSai = false;

      if(RangeSize == 722002)
      {
         //01/02/2002-Christiano-comentado trecho abaixo e adicionado o outro mais abaixo
         /******************************
         regAtual = getPosicaoInserir();
         auxInicioPagina = regAtual;
         while (! bSai)
         {   if( regAtual >= pregistro )
                bSai = true;
             else
             {  auxInicioPagina = regAtual;
                if(next())
                   pagina++;
                else
                   bSai = true;
             }
         }
         if(psetaInicio == 0)
            regInicioPagina = auxInicioPagina;
         else if(psetaInicio == -1)
            regInicioPagina = auxInicioPagina - 1;
         else if(psetaInicio == 1)
            regInicioPagina = regAtual;
         regAtual = regInicioPagina;
         return pagina;
         ******************************/

         if(pregistro<=1) pagina = 1;
         else pagina = pregistro;

         if(psetaInicio == 0)
            regInicioPagina = pregistro - 1;
         else if(psetaInicio == -1)
            regInicioPagina = pregistro - 2;
         else if(psetaInicio == 1)
            regInicioPagina = pregistro;

         if(regInicioPagina < getPosicaoInserir())
            regInicioPagina = getPosicaoInserir();
         regAtual = auxregAtual;
         return pagina;
      }

      //o if abaixo tem a ver com a pendência de número 2:
      if((RangeSize == 1) && (getPosicaoInserir() < -1)) //14/02
      {  contador = 1; //14/02
         regAtual = getPosicaoInserir();
      }
      else
         regAtual = 0;
      while (! bSai)
      {
          if( regAtual >= pregistro )
             bSai = true;
          else
          {
             if(next())
             {
                contador++;
                if(contador > RangeSize)
                {   //quebrou a pagina, armazeno o inicio da pagina anterior e o inicio da atual...
                    contador = 1;
                    auxInicioPaginaAnterior = auxInicioPagina;
                    auxInicioPagina = regAtual;
                    pagina++;
                }
             }
             else
             {  bSai = true;
             }
          }
      }
      if(psetaInicio == 0)
         regInicioPagina = (auxInicioPagina - 1);
      else if(psetaInicio == -1)
         regInicioPagina = (auxInicioPaginaAnterior - 1);
      else if(psetaInicio == 1)
         regInicioPagina = regAtual;

      regAtual = auxregAtual;
//System.out.println("Pagina do registro " + pregistro + " =" + pagina);
      return pagina;
   }

   public void setcolunaPaginacao (String pcoluna)
   {  colunaPaginacao = pcoluna;
   }

   public String showPageNumbers(HttpSession session)
       throws SQLException, java.sql.SQLException, Exception
   {
      String retorno = "";
      if (RangeSize == -1)
         return "1";

      int salvaregContador = regContador;
      int salvaregInicioPagina = regInicioPagina;
      int salvaregAtual = regAtual;
      pagina = 0;

      if(numPages == -1)
      {
         paginaA = 0;
         paginaB = 0;
         paginaC = 0;
         paginaD = 0;
         paginaE = 0;
         paginaF = 0;
         paginaG = 0;
         paginaH = 0;
         paginaI = 0;
         paginaJ = 0;
         paginaK = 0;
         paginaL = 0;
         paginaM = 0;
         paginaN = 0;
         paginaO = 0;
         paginaP = 0;
         paginaQ = 0;
         paginaR = 0;
         paginaS = 0;
         paginaT = 0;
         paginaU = 0;
         paginaV = 0;
         paginaW = 0;
         paginaX = 0;
         paginaY = 0;
         paginaZ = 0;
        //vai para o inicio:
        if (RangeSize == 722002)
        { regInicioPagina = getPosicaoInserir();
          regAtual = getPosicaoInserir();
        }
        else
        { regInicioPagina = 0;
          regAtual = getPosicaoInserir();
        }
        //percorre tudo
        while(next()) //PreenchePagina())
        {
           int salvaregAtual2 = regAtual;
           if (RangeSize == 722002) //04/02/2002
              pagina = calculaPagina(regAtual, 0); //04/02/2002
           else //04/02/2002
              pagina = calculaPagina(regAtual, 0) + 1;
           numPages = pagina;
           regAtual = salvaregAtual2;
           //tratamento da paginacao por letra:
           if(! colunaPaginacao.equals(""))
           {  //vet_colunaPaginacaoValor.addElement(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString());
              //vet_colunaPaginacaoPag.addElement(""+pagina);
              if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("A"))
              {  if(paginaA == 0) paginaA = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("B"))
              {  if(paginaB == 0) paginaB = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("C"))
              {  if(paginaC == 0) paginaC = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("D"))
              {  if(paginaD == 0) paginaD = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("E"))
              {  if(paginaE == 0) paginaE = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("F"))
              {  if(paginaF == 0) paginaF = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("G"))
              {  if(paginaG == 0) paginaG = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("H"))
              {  if(paginaH == 0) paginaH = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("I"))
              {  if(paginaI == 0) paginaI = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("J"))
              {  if(paginaJ == 0) paginaJ = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("K"))
              {  if(paginaK == 0) paginaK = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("L"))
              {  if(paginaL == 0) paginaL = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("M"))
              {  if(paginaM == 0) paginaM = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("N"))
              {  if(paginaN == 0) paginaN = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("O"))
              {  if(paginaO == 0) paginaO = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("P"))
              {  if(paginaP == 0) paginaP = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("Q"))
              {  if(paginaQ == 0) paginaQ = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("R"))
              {  if(paginaR == 0) paginaR = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("S"))
              {  if(paginaS == 0) paginaS = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("T"))
              {  if(paginaT == 0) paginaT = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("U"))
              {  if(paginaU == 0) paginaU = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("V"))
              {  if(paginaV == 0) paginaV = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("X"))
              {  if(paginaX == 0) paginaX = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("W"))
              {  if(paginaW == 0) paginaW = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("Y"))
              {  if(paginaY == 0) paginaY = pagina; }
              else if(UtilBean.getVerificaNull(getColuna(colunaPaginacao)).toString().toUpperCase().startsWith("Z"))
              {  if(paginaZ == 0) paginaZ = pagina; }
           }
        }
      } //fim if(numPages == -1)

      regContador = salvaregContador;
      regInicioPagina = salvaregInicioPagina;
      regAtual = salvaregAtual;
      if (RangeSize == 722002)
         pagina = calculaPagina(regInicioPagina+1, 0);
      else
         pagina = calculaPagina(regInicioPagina+1, 0) + 1;
      regContador = salvaregContador;
      regInicioPagina = salvaregInicioPagina;
      regAtual = salvaregAtual;

      if(numPages <= 20)
      { for(int i=1; i<=numPages; i++)
        {  if(i > 1) retorno += " ";
           if(pagina == i)
              retorno += "<span style=color:#000000;font-family:arial;font-size:10px;text-decoration:underline>"+i+"</span>";
           else
           {  if(ParametroPaginacao.equals(""))
                 retorno += "<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + i ) + "\" class=txtCinza>" + i + "</a>";
              else
                 retorno += "<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + i + "&" + ParametroPaginacao) + "\" class=txtCinza>" + i + "</a>";
           }
        }
      }
      else
      {
        int paginainicial = 0;
        int paginafinal = 0;
        paginainicial = pagina;
        paginafinal = paginainicial + 19;
        if(paginafinal > numPages)
        {   paginafinal = numPages;
            paginainicial = paginafinal - 19;
        }
        if(paginainicial <= 0) paginainicial = 1;

        int i = 0;
        if(paginainicial > 1)
        {  i = paginainicial - 20;
           if(i <= 0) i = 1;
           if(ParametroPaginacao.equals(""))
           {  retorno += " <A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + 1 ) + "\" class=txtCinza>" + "|<< " + "</a>";
              retorno += " <A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + i ) + "\" class=txtCinza>" + "<< " + "</a>";
           }
           else
           {  retorno += " <A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + 1 + "&" + ParametroPaginacao) + "\" class=txtCinza>" + "|<< " + "</a>";
              retorno += " <A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + i + "&" + ParametroPaginacao) + "\" class=txtCinza>" + "<< " + "</a>";
           }
        }
        for(i=paginainicial; i<=paginafinal; i++)
        {  if(pagina == i)
              retorno += " <span style=color:#000000;font-family:arial;font-size:10px;text-decoration:underline>"+i+"</span>";
           else
           {  if(ParametroPaginacao.equals(""))
                 retorno += " <A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + i) + "\" class=txtCinza>" + i + "</a>";
              else
                 retorno += " <A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + i + "&" + ParametroPaginacao) + "\" class=txtCinza>" + i + "</a>";
           }
        }
        if(i <= numPages)
        {  if(ParametroPaginacao.equals(""))
           {  retorno += " <A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + i) + "\" class=txtCinza>" + ">> " + "</a>";
              retorno += " <A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + numPages) + "\" class=txtCinza>" + ">>| " + "</a>";
           }
           else
           {  retorno += " <A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + i + "&" + ParametroPaginacao) + "\" class=txtCinza>" + ">> " + "</a>";
              retorno += " <A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + numPages + "&" + ParametroPaginacao) + "\" class=txtCinza>" + ">>| " + "</a>";
           }
        }
      }
      return retorno;
   }

   public int getNumPages() {
     return numPages;
   }
   public int getPagina() {
     return pagina;
   }

   public String showPageLetters(HttpSession session)
   {  //ATENCAO: caso este método seja chamado, ele deve vir obrigatoriamente
      //  depois de showPageNumbers(session).
      String retorno = "";
      if(! colunaPaginacao.equals(""))
      {
         String paramadicional = "";
         if(! ParametroPaginacao.equals("")) paramadicional = "&" + ParametroPaginacao;
         if( paginaA != 0 ) retorno += "<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaA + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "A" + "</a>";
         else retorno += "<span class=txtCinza>A</span>";
         if( paginaB != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaB + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "B" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;B</span>";
         if( paginaC != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaC + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "C" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;C</span>";
         if( paginaD != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaD + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "D" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;D</span>";
         if( paginaE != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaE + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "E" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;E</span>";
         if( paginaF != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaF + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "F" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;F</span>";
         if( paginaG != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaG + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "G" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;G</span>";
         if( paginaH != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaH + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "H" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;H</span>";
         if( paginaI != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaI + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "I" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;I</span>";
         if( paginaJ != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaJ + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "J" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;J</span>";
         if( paginaK != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaK + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "K" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;K</span>";
         if( paginaL != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaL + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "L" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;L</span>";
         if( paginaM != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaM + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "M" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;M</span>";
         if( paginaN != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaN + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "N" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;N</span>";
         if( paginaO != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaO + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "O" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;O</span>";
         if( paginaP != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaP + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "P" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;P</span>";
         if( paginaQ != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaQ + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "Q" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;Q</span>";
         if( paginaR != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaR + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "R" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;R</span>";
         if( paginaS != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaS + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "S" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;S</span>";
         if( paginaT != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaT + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "T" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;T</span>";
         if( paginaU != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaU + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "U" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;U</span>";
         if( paginaV != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaV + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "V" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;V</span>";
         if( paginaX != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaX + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "X" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;X</span>";
         if( paginaW != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaW + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "W" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;W</span>";
         if( paginaY != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaY + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "Y" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;Y</span>";
         if( paginaZ != 0 ) retorno += "&nbsp;<A HREF=\"" + UtilBean.montaURL(session, PaginaBrowse, "NAVIGATE=PAGE&PAGE=" + paginaZ + paramadicional) + "\" class=txtPreto style=text-decoration:underline>" + "Z" + "</a>";
         else retorno += "<span class=txtCinza>&nbsp;Z</span>";
      }
      return retorno;
   }
   public String showInputTextFindPagina(HttpSession session)
   {
      if(colunaPaginacao.equals(""))
         return "";

      // <INPUT class=\"botButton\" TYPE=\"BUTTON\" TABINDEX=\"1\" NAME=\"COM___BTNFIND\" VALUE=\"Procurar\" onClick=\"gofind('" + getPaginaBrowse() + "')\">"
      return "<INPUT class=\"formBasic\" TYPE=\"TEXT\" NAME=\"COM___FIND\" VALUE=\"\" style=\"width:80px\">"
           + "<img NAME=\"COM___BTNFIND\" src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/botoes/search.gif\" onClick=\"gofind('" + getPaginaBrowse() + "')\">";
   }
   public String showMsgProcuraNaoEncontrou(String pmsg)
   {  if(! achouProcuraValorPagina)
         return "<script LANGUAGE=\"javascript\"> alert(\"" + pmsg + " \"); </script>";
      else
         return "";
   }

   public String showButtonDelete (HttpSession session)
          throws SQLException, java.io.IOException, Exception
   {
      return "&nbsp;";
   }
   public String showButtonUpdate (HttpSession session)
          throws SQLException, java.io.IOException, Exception
   {
      return "&nbsp;";
   }
   public String showButtonPesquisar ()
          throws Exception
   {
      return "<A HREF=\"javascript:void pesquisa()\" TABINDEX=\"1\" NAME=\"PESQUISA\">"
           + "<img src=\"/portalcom/images"+session.getAttribute("COM_DOMINIO")+"/commit/botoes/bot_pesquisar_01.gif\"  border=0  alt=\"Pesquisar\">"
           + "</A>";
   }
   public String showButtonPesquisar (String pname, String plabel)
          throws Exception
   {
      return "<A HREF=\"javascript:void pesquisa()\" TABINDEX=\"1\" NAME=\""+ pname +"\">"
           + "<img src=\"/portalcom/images"+session.getAttribute("COM_DOMINIO")+"/commit/botoes/bot_pesquisar_01.gif\"  border=0  alt=\"Pesquisar\">"
           + "</A>";
   }


// ------------------------ MÉTODOS de CAMPOS (INPUTs)----------------------------------

 Vector vet_campos = new Vector();

   public void removeCampos() {
      vet_campos.removeAllElements();
   }
   public void addCampo(String nome) {
      vet_campos.addElement(nome);
   }

   public String showCampo(String nomeCampo, String value, String oper,
                           verificaAcesso acesso,
                           HttpServletRequest request)
          throws java.io.IOException, Exception
   {
      String retorno = "";
      if( value == null)
         value = (oper.equals("I")? "" : "" + UtilBean.getVerificaNull(getColuna(nomeCampo)));

      if (! acesso.verificaAcessoCampo(acesso.getJSP(request), nomeCampo).equals("I"))
      {  retorno += value;
      }
      return retorno;
   }

//=======================================================================

//=======================================================================
//========== Alexandre ====================
//====================================================
// metodos feitos por alexandre para tela de pesquisa


//=============================================================================
//                 Alteracoes realizadas no dia 17 abril de 2001
//   Os metodos a serem definidos serao para inclusao de campos HTML
//  dinamicamente, caso os metodos acima tiverem que ser redefinidos,
//  serao comentados no seu local e redefinidos daqui para baixo.
//=============================================================================

//variaveis de controle de pesquisa
   private Vector nomeColunaPesquisa = new Vector();
   private Vector tipoColunaPesquisa = new Vector();
   private Vector tipoCampoPesquisa = new Vector();
   private Vector clausulaPesquisa = new Vector();
   private Vector tamCheck = new Vector();
   comboBean comboSql;
   radioBean radioSql;
   checkBean checkSql;
   private String compSql = "";
   private int compTipoParam[];
   private String compValorParam[];
   private String compSqlPost = "";

   private String javascript = "";
   private int linhasPesq = 0;
   private String ordenaPesq = "";
   private Vector tiposColunasOrdenacao = new Vector();
   private Vector nomesColunasOrdenacao = new Vector();
   private boolean layoutPesq = true;
   private boolean layoutSubmitPesq = true;
   private HttpServletRequest cmrequest;
   private String COM_DOMINIO = "";

   private void addColuna(String coluna, int tipo, int campo) {
      nomeColunaPesquisa.addElement(coluna);
      tipoColunaPesquisa.addElement(tipo+"");
      tipoCampoPesquisa.addElement(campo+"");
      clausulaPesquisa.addElement(coluna);
   }
   private void addColuna(String coluna, int tipo, int campo, String clausula) {
      nomeColunaPesquisa.addElement(coluna);
      tipoColunaPesquisa.addElement(tipo+"");
      tipoCampoPesquisa.addElement(campo+"");
      clausulaPesquisa.addElement(clausula);
   }

   public void setsql(String value, String sWhere)
   {
      if (bMontaWhere)                            //27
         bMontaWhere = false;                     //27
      else                                        //27
      {  //vet_posparametros.removeAllElements(); //27
         vet_tipoparametros.removeAllElements();  //27
         vet_valorparametros.removeAllElements(); //27
      }
      if((sWhere == null) || (sWhere.equals(""))) //27
         sql = value;
      else
         sql = value + " " + sWhere;
   }

   public comboBean getComboSql() {
      return comboSql;
   }

   public radioBean getRadioSql() {
      return radioSql;
   }

   public checkBean getCheckSql() {
      return checkSql;
   }

   public String getJavascript(boolean pesqTudo) {
     String resultado = "";
     if(! layoutPesq ) {
       resultado += "<SCRIPT LANGUAGE=\"JavaScript1.2\"> \n";
       resultado += " function validaPesquisa() { \n";
       resultado += "    var preenchido = false; \n";
       resultado += "    var validaPesq = true;  \n";
       resultado += javascript;

       if( pesqTudo ) {
         resultado += " if(! preenchido) { \n";
         resultado += "   jserro = jserro + \"\\nPreencha algum campo para a pesquisa\"; \n";
         resultado += "   validaPesq = false; \n";
         resultado += " }  \n";
       }
       resultado += "   return validaPesq; \n";
       resultado += " } \n";

       resultado += " </SCRIPT> \n";
     }
     return resultado;
   }

/*
// metodo comentado para verificacao, variável "linhas" nao é necessário
//????????????????
   public void setOrdenaPesquisa(int linhas, String ordenaComp) {
     //tabela de ordenação
     linhasPesq = linhas;
     ordenaPesq = ordenaComp;
   }
*/
   public void setQtdeOrdenaPesquisa(int linhas) {
     //tabela de ordenação
     linhasPesq = linhas;
   }

   public void setOrdenaPesquisa(String ordenaComp) {
     //tabela de ordenação
     ordenaPesq = ordenaComp;
   }
   public void setLayoutPesquisa(boolean layout) {
     liberaRecursoPesq();
     layoutPesq = layout;
     layoutSubmitPesq = true;
   }

   public void iniPesquisa(HttpSession p_session, String titulo, boolean layout)
          throws Exception {
      session = p_session;
      COM_DOMINIO = ""+session.getAttribute( "COM_DOMINIO" );
      layoutSubmitPesq = false;
      Internal_iniPesquisa(titulo, layout);
   }
   public void iniPesquisa(HttpSession p_session, String titulo) throws Exception {
      session = p_session;
      COM_DOMINIO = ""+session.getAttribute( "COM_DOMINIO" );
      layoutSubmitPesq = true;
      Internal_iniPesquisa(titulo, true);
   }
   private void Internal_iniPesquisa(String titulo, boolean layout) throws Exception {
     liberaRecursoPesq();
     layoutPesq = layout;
     if(layoutPesq) {
       saida.println("<!-------------------- Inicio da tabela de Pesquisa -------------> ");
       saida.println(" <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> "
                   + "  <tr>"
                   + "   <td><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/botoes_novos/seta_direita.gif\" border=\"0\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" width=\"10\" height=\"1\" border=\"0\"></td> "
                   + "   <td><font class=\"txtBlackBold\"><strong>Parâmetros de Pesquisa</strong></font></td>  "
                   + "  </tr> " );
     }
   }

   public void ordenaPesquisa(String coluna[], String desc[], String p_default[], int linhas, String ordenaComp) throws Exception {
     linhasPesq = linhas;
     ordenaPesq = ordenaComp;
     tiposColunasOrdenacao.removeAllElements();
     nomesColunasOrdenacao.removeAllElements();
     if(layoutPesq) {
       //tabela de ordenação
       saida.println("<!---------------------- Inicio da tabela de Ordenacao da Pesquisa -------------> ");
       saida.println(" <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> "
                 + " <tr><td colspan=\"4\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" width=\"1\" height=\"15\" border=\"0\"></td></tr> "
                 + " <tr> "
                 + "  <td><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/botoes_novos/seta_direita.gif\" border=\"0\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" width=\"10\" height=\"1\" border=\"0\"></td> "
                 + "  <td><font class=\"txtBlackBold\"><strong>Colunas para ordenação</strong></font></td> "
                 + "  <td><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" width=\"40\" height=\"1\" border=\"0\"></td> "
                 + "  <td><font class=\"txtBlackBold\"><strong>Ordenar por</strong></font></td> "
                 + " </tr> ");

       int def = 0;
       for( int i=0; i<linhas; i++ ) {
          saida.println("<tr> "
                    + "   <td><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" width=\"1\" height=\"45\" border=\"0\"></td>  "
                    + "   <td colspan=\"3\"> "
                    + "    <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> "
                    + "     <tr><td colspan=\"2\"><font class=\"txtBlack\">" + (i+1) + "&ordf; coluna para ordenação</font></td></tr> "
                    + "     <tr> "
                    + "       <td><select name=\"cOrdenacaoCol"+ i + "\" class=\"txtBlack\"> ");

          for( int x=0; x<coluna.length; x++ ) {
            if( (p_default != null) && (p_default.length > def)) {
              if( coluna[x].equals(p_default[def]) )
                saida.println( "<option class=\"txtBlack\" value=\""+ coluna[x] +"\" SELECTED >"+ desc[x] + "</option>" );
              else
                saida.println( "<option class=\"txtBlack\" value=\""+ coluna[x] +"\">"+ desc[x] + "</option>");
            }
            else
              saida.println( "<option class=\"txtBlack\" value=\""+ coluna[x] +"\">"+ desc[x] + "</option>");
          }
          def++;

          saida.println(" </select></td> "
              + " <td><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" width=\"5\" height=\"1\" border=\"0\"> "
              + "       <select class=\"txtBlack\" name=\"cOrdenacao"+ i +"\">  "
              + "         <option class=\"txtBlack\" value=\"ASC\" SELECTED> Crescente </option>"
              + "         <option class=\"txtBlack\" value=\"DESC\"> Decrescente </option>"
              + "       </select>  "
              + " </td></tr></table> "
              + " </td></tr> ");
       }

       saida.println(" </table>  ");
       saida.println("<!-------------------- Fim da tabela de Ordenacao da Pesquisa -------------> ");
     }
   }

   public void ordenaPesquisa(String tipocoluna[], String coluna[], String desc[], String p_default[], int linhas, String ordenaComp) throws Exception {
     linhasPesq = linhas;
     ordenaPesq = ordenaComp;
     tiposColunasOrdenacao.removeAllElements();
     nomesColunasOrdenacao.removeAllElements();
     for( int a=0; a<tipocoluna.length; a++ ) {
        tiposColunasOrdenacao.addElement( tipocoluna[a] );
     }
     for( int b=0; b<coluna.length; b++ ) {
        nomesColunasOrdenacao.addElement( coluna[b] );
     }

     if(layoutPesq) {
       //tabela de ordenação
       saida.println("<!-------------------- Inicio da tabela de Ordenacao da Pesquisa -------------> ");
       saida.println(" <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> "
                 + " <tr><td colspan=\"4\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" width=\"1\" height=\"15\" border=\"0\"></td></tr> "
                 + " <tr> "
                 + "  <td><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/botoes_novos/seta_direita.gif\" border=\"0\"><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" width=\"10\" height=\"1\" border=\"0\"></td> "
                 + "  <td><font class=\"txtBlackBold\"><strong>Colunas para ordenação</strong></font></td> "
                 + "  <td><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" width=\"40\" height=\"1\" border=\"0\"></td> "
                 + "  <td><font class=\"txtBlackBold\"><strong>Ordenar por</strong></font></td> "
                 + " </tr> ");

       int def = 0;
       for( int i=0; i<linhas; i++ ) {
          saida.println("<tr> "
                    + "   <td><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" width=\"1\" height=\"45\" border=\"0\"></td>  "
                    + "   <td colspan=\"3\"> "
                    + "    <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> "
                    + "     <tr><td colspan=\"2\"><font class=\"txtBlack\">" + (i+1) + "&ordf; coluna para ordenação</font></td></tr> "
                    + "     <tr> "
                    + "       <td><select name=\"cOrdenacaoCol"+ i + "\" class=\"txtBlack\"> ");

          for( int x=0; x<coluna.length; x++ ) {
            if( (p_default != null) && (p_default.length > def)) {
              if( coluna[x].equals(p_default[def]) )
                saida.println( "<option class=\"txtBlack\" value=\""+ coluna[x] +"\" SELECTED >"+ desc[x] + "</option>" );
              else
                saida.println( "<option class=\"txtBlack\" value=\""+ coluna[x] +"\">"+ desc[x] + "</option>");
            }
            else
              saida.println( "<option class=\"txtBlack\" value=\""+ coluna[x] +"\">"+ desc[x] + "</option>");
          }
          def++;

          saida.println(" </select></td> "
              + " <td><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" width=\"5\" height=\"1\" border=\"0\"> "
              + "       <select class=\"txtBlack\" name=\"cOrdenacao"+ i +"\">  "
              + "         <option class=\"txtBlack\" value=\"ASC\" SELECTED> Crescente </option>"
              + "         <option class=\"txtBlack\" value=\"DESC\"> Decrescente </option>"
              + "       </select>  "
              + " </td></tr></table> "
              + " </td></tr> ");
       }

       saida.println(" </table>  ");
       saida.println("<!-------------------- Fim da tabela de Ordenacao da Pesquisa -------------> ");
     }
   }

   public void fimPesquisa(boolean pesqTudo) throws Exception{
     if(layoutPesq) {
       saida.println("  </table>  ");
       saida.println("<!-------------------- Fim da tabela de Pesquisa -------------> ");

       saida.println("<SCRIPT LANGUAGE=\"JavaScript1.2\"> ");
       saida.println(" function validaPesquisa() { ");
       saida.println("    var preenchido = false; ");
       saida.println("    var validaPesq = true;  ");
       saida.println( javascript );

       if( pesqTudo ) {
         saida.println(" if( preenchido == false ) { ");
         saida.println("   jserro = jserro + \"\\nPreencha algum campo para a pesquisa\"; ");
         saida.println("   validaPesq = false; ");
         saida.println(" }  ");
       }

       saida.println("   return validaPesq; ");
       saida.println(" } ");

       saida.println(" </SCRIPT> ");
     }
   }

//============ metodos para criacao de campos em HTML ==========

/*==============================================================
         ATENÇÃO
================================================================
  - A legenda a seguir é para o controle
  dos tipos de campos que serão adicionados
  na tela de pesquisa, seguindo nessa ordem:

  - 1 : combo
  - 2 : radio
  - 3 : check
  - 4 : campo

  Esses inteiros serão usados no método montaWhere
  para que eu possa saber que tipo de campo eu
  estou recebendo como parametro.
===============================================================*/

/*
  Atenção =  todos os metodos para gerar campos do banco (...sql)
             deverão usar o metodo get da variavel (...sql, Ex: getComboSql())
             para utilizar os metodos do banco.
             Ex.: cmDbBean0.getComboSql().setSql("SELECT....");
*/
//============== inicio da combo SQL =============
   public void inicializaComboSql(String nome, int tipoColuna, String desc, String selected, int size, boolean multiple, String script) throws Exception{
     comboSql = new comboBean();
     comboSql.inicializa("co"+nome.replace('.','_'), desc, selected, size, multiple, script);
     addColuna(nome, tipoColuna, 1);
     if(! desc.equals("") ) {
         javascript += "    if(! (document.iForm.co"+nome.replace('.','_') +".selectedIndex == 0)) { \n"
                     + "      preenchido = true; \n"
                     + "    } \n";
      }
   }
   public void inicializaComboSql(String clausula, String nome, int tipoColuna, String desc, String selected, int size, boolean multiple, String script) throws Exception{
     comboSql = new comboBean();
     comboSql.inicializa("co"+nome.replace('.','_'), desc, selected, size, multiple, script);
     addColuna(nome, tipoColuna, 1, clausula);
     if(! desc.equals("") ) {
         javascript += "    if(! (document.iForm.co"+nome.replace('.','_') +".selectedIndex == 0)) { \n"
                     + "      preenchido = true; \n"
                     + "    } \n";
      }
   }

   public void criaComboSql(String label, boolean ultimo ) throws Exception {
     //if(layoutPesq)
       montaLinhaComboSql(label, ultimo);
   }
//============== fim da combo SQL ===================

//============== inicio da radio SQL =============
  public void inicializaRadioSql(String nome, int tipoColuna, String checked, String script, String complemento) throws Exception {
     radioSql = new radioBean();
     radioSql.inicializa("ra"+nome.replace('.','_'), checked, script, complemento);
     addColuna(nome, tipoColuna, 2);
  }
  public void inicializaRadioSql(String clausula, String nome, int tipoColuna, String checked, String script, String complemento) throws Exception {
     radioSql = new radioBean();
     radioSql.inicializa("ra"+nome.replace('.','_'), checked, script, complemento);
     addColuna(nome, tipoColuna, 2, clausula);
  }
  public void criaRadioSql( String labelPesq, boolean ultimo ) throws Exception {
     montaLinhaRadioSql(labelPesq, ultimo);
  }
//============== fim da radio SQL ===================

//============== inicio da check SQL =============
  public void inicializaCheckSql(String nome, int tipoColuna, String checked[], String script, String complemento) throws Exception {
     checkSql = new checkBean();
     checkSql.inicializa("ch"+nome.replace('.','_'), checked, script, complemento);
     addColuna(nome, tipoColuna, 3);
  }
  public void inicializaCheckSql(String clausula, String nome, int tipoColuna, String checked[], String script, String complemento) throws Exception {
     checkSql = new checkBean();
     checkSql.inicializa("ch"+nome.replace('.','_'), checked, script, complemento);
     addColuna(nome, tipoColuna, 3, clausula);
  }
  public void criaCheckSql( String labelPesq, boolean ultimo ) throws Exception {
     montaLinhaCheckSql(labelPesq, ultimo);
  }
//============== fim da check SQL ===================

   public void addComboPesq(String nome, int tipoColuna, String label, String desc[], String valor[], String selected, int size, boolean multiple, String script, boolean ultimo ) throws Exception {
     addColuna(nome, tipoColuna, 1);
     montaLinhaCombo(nome, label, desc, valor, selected, size, multiple, script, ultimo);
   }
   public void addComboPesq(String clausula, String nome, int tipoColuna, String label, String desc[], String valor[], String selected, int size, boolean multiple, String script, boolean ultimo ) throws Exception {
     addColuna(nome, tipoColuna, 1, clausula);
     montaLinhaCombo(nome, label, desc, valor, selected, size, multiple, script, ultimo);
   }

   //metodo para forçar a query filtrar por uma opção
   public void addRadioPesq(String nome, int tipoColuna, String label[], String valor[], String checked, String script, boolean ultimo ) throws Exception {
     addColuna(nome, tipoColuna, 2);
     montaLinhaRadio(nome, label, valor, checked, script, ultimo);
   }
   public void addRadioPesq(String nome, int tipoColuna, String labelPesq, String label[], String valor[], String checked, String script, boolean ultimo ) throws Exception {
     addColuna(nome, tipoColuna, 2);
     montaLinhaRadio(nome, labelPesq, label, valor, checked, script, ultimo);
   }
   public void addRadioPesq(String clausula, String nome, int tipoColuna, String label[], String valor[], String checked, String script, boolean ultimo ) throws Exception {
     addColuna(nome, tipoColuna, 2, clausula);
     montaLinhaRadio(nome, label, valor, checked, script, ultimo);
   }
   public void addRadioPesq(String clausula, String nome, int tipoColuna, String labelPesq, String label[], String valor[], String checked, String script, boolean ultimo ) throws Exception {
     addColuna(nome, tipoColuna, 2, clausula);
     montaLinhaRadio(nome, labelPesq, label, valor, checked, script, ultimo);
   }

   public void addCheckPesq(String nome, int tipoColuna, String labelPesq, String label[], String valor[], String checked[], String script, boolean ultimo ) throws Exception {
     addColuna(nome, tipoColuna, 3);
     montaLinhaCheck(nome, labelPesq, label, valor, checked, script, ultimo);
     tamCheck.addElement("ch"+nome.replace('.','_')+"Þ"+valor.length);
   }
   public void addCheckPesq(String clausula, String nome, int tipoColuna, String labelPesq, String label[], String valor[], String checked[], String script, boolean ultimo ) throws Exception {
     addColuna(nome, tipoColuna, 3, clausula);
     montaLinhaCheck(nome, labelPesq, label, valor, checked, script, ultimo);
     tamCheck.addElement("ch"+nome.replace('.','_')+"Þ"+valor.length);
   }

   public void addCampoPesq( String nome, int tipoColuna, String label, String valor, int size, int maxlength, String script, boolean ultimo ) throws Exception {
     addColuna(nome, tipoColuna, 4);
     showLinha(nome, tipoColuna, label, valor, size, maxlength, script, ultimo);
   }
   public void addCampoPesq( String clausula, String nome, int tipoColuna, String label, String valor, int size, int maxlength, String script, boolean ultimo ) throws Exception {
     addColuna(nome, tipoColuna, 4, clausula);
     showLinha(nome, tipoColuna, label, valor, size, maxlength, script, ultimo);
   }

   public void addCompPesq( String p_compSql, int tipo[], String valor[] ) throws Exception {
     compSql = p_compSql;
     compTipoParam = tipo;
     compValorParam = valor;
   }
   public void addCompPesqFinal( String p_compSql ) throws Exception {
     compSqlPost = p_compSql;
   }

   //======== fim dos metodos HTML ================

   //============  Atencao ===================
   // caso nao precise fazer nada com os tipos, eliminar este metodo e fazer a
   //  chamada direta para o montaLinha.
   // ========================================
   private void showLinha(String nome, int tipo, String label, String valor, int size, int maxLength, String script, boolean ultimo) throws Exception{

       switch(tipo)
       {
          case Types.DATE: {
            montaLinha("Data", nome, label, valor, size, maxLength, script, ultimo);
            break;
          }
          case Types.BIGINT:
          case Types.INTEGER: {
            montaLinha("Inteiro", nome, label, valor, size, maxLength, script, ultimo);
            break;
          }
          case Types.DECIMAL:
          case Types.NUMERIC:
          case Types.DOUBLE:
          case Types.REAL:
          case Types.FLOAT: {
            montaLinha("Numero", nome, label, valor, size, maxLength, script, ultimo);
            break;
          }
          case Types.LONGVARCHAR:
          case Types.VARCHAR: {
            montaLinha("Caracter", nome, label, valor, size, maxLength, script, ultimo);
            break;
          }
          default: {
            // mensagem de erro
            Exception e = new Exception("Não foi definido um parâmetro!");
            throw e;
          }
       } //fim switch
    }

    //monta a linha com o elemento "campo"
    private void montaLinha(String tipo, String nome, String label, String valor, int size, int maxLength, String script, boolean ultimo) throws Exception{
      if(! layoutSubmitPesq) return; //1107
      if(layoutPesq) {
        saida.println(" <tr> <td></td> <td> "
                + "   <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> "
                + "    <tr><td colspan=\"4\"><font class=\"txtBlack\">" + label + "</font></td></tr> ");
      }

      if( tipo.equals("Caracter") ) {
        saida.println( " <tr><td> <input class=\"cmpText\" TYPE=\"TEXT\" SIZE=\""+ size +"\" MAXLENGTH=\""+ maxLength +"\" NAME=\"ca" + nome.replace('.','_') + "\"" + script + "> "
              + " </td><td> <img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" width=\"10\" height=\"1\" border=\"0\"> "
              + "       <select class=\"cmpSelect\" name=\"c" + nome.replace('.','_') + "like\">  "
              + "         <option class=\"cmpOption\" value=\"\"> </option>"
              + "         <option class=\"cmpOption\" value=\"antes\"> termina com </option>"
              + "         <option class=\"cmpOption\" value=\"depois\"> começa com </option>"
              + "         <option class=\"cmpOption\" value=\"tudo\"> tudo com </option>"
              + "         <option class=\"cmpOption\" value=\"opcoes\"> opções </option>"
              + "       </select>  "
              + "  </td> ");
      }
      else {
        if( tipo.equals("Data") ) {
          saida.println(" <tr><td> <NOBR><input class=\"cmpText\" TYPE=\"TEXT\" SIZE=\""+ size +"\" MAXLENGTH=\""+ maxLength +"\" NAME=\"ca" + nome.replace('.','_') + "ini\" "
               + " onChange=\"this.value = datecheck(this.value,NLSformat);\" "
               + " onselect=\"null\" onfocus=\"this.select()\" "
               + script + "> <a href=\"javascript:void opencal(document.iForm.ca" + nome.replace('.','_') + "ini)\">"
               + "<img src=\"/portalcom/webapp"+COM_DOMINIO+"/images/FNDICLDR.gif\" align=\"absmiddle\" border=\"0\" "
               + " alt=\"Calendário\"></a></NOBR> "
               + "</td><td><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" width=\"10\" height=\"1\" border=\"0\"> " );

          saida.println("<select class=\"cmpSelect\" name=\"c" + nome.replace('.','_') + "inter\" > "
            + "          <option class=\"cmpOption\" value=\"\"> </option>"
            + "          <option class=\"cmpOption\" value=\"ate\"> até  </option>"
            + "          <option class=\"cmpOption\" value=\"maior\"> maior </option>  "
            + "          <option class=\"cmpOption\" value=\"menor\"> menor </option>"
            + "        </select>  "
            + "   </td> " );
        }
        else if( (tipo.equals("Numero")) || (tipo.equals("Inteiro"))) {
          saida.println("<tr><td> <input class=\"cmpText\" TYPE=\"TEXT\" SIZE=\""+ size +"\" MAXLENGTH=\""+ maxLength +"\" NAME=\"ca" + nome.replace('.','_') + "ini\"" + script + "> "
                 + "</td><td><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" width=\"10\" height=\"1\" border=\"0\"> " );

          saida.println("<select class=\"cmpSelect\" name=\"c" + nome.replace('.','_') + "inter\" > "
            + "          <option class=\"cmpOption\" value=\"\"> </option>"
            + "          <option class=\"cmpOption\" value=\"ate\"> até </option>"
            + "          <option class=\"cmpOption\" value=\"opcoes\"> opções </option>"
            + "          <option class=\"cmpOption\" value=\"maior\"> maior </option>"
            + "          <option class=\"cmpOption\" value=\"menor\"> menor </option>"
            + "        </select> "
            + "  </td> " );
        }

        if( tipo.equals("Data") ) {
          saida.println("<td><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" width=\"10\" height=\"1\" border=\"0\"> "
               + "<NOBR><input class=\"cmpText\" TYPE=\"TEXT\" SIZE=\""+ size +"\" MAXLENGTH=\""+ maxLength +"\" NAME=\"ca" + nome.replace('.','_') + "fim\" "
               + " onChange=\"this.value = datecheck(this.value,NLSformat);\" "
               + " onselect=\"null\" onfocus=\"this.select()\" "
               + script + "> <a href=\"javascript:void opencal(document.iForm.ca" + nome.replace('.','_') + "fim)\">"
               + "<img src=\"/portalcom/webapp"+COM_DOMINIO+"/images/FNDICLDR.gif\" align=\"absmiddle\" border=\"0\" "
               + " alt=\"Calendário\"></a></NOBR> </td> " );
        }
        else if( (tipo.equals("Numero")) || (tipo.equals("Inteiro")))
          saida.println("<td><img src=\"/portalcom/images" + session.getAttribute("COM_DOMINIO") + "/images/pixel.gif\" width=\"10\" height=\"1\" border=\"0\"> "
                 + "<input class=\"cmpText\" TYPE=\"TEXT\" SIZE=\""+ size +"\" MAXLENGTH=\""+ maxLength +"\" NAME=\"ca" + nome.replace('.','_') + "fim\"" + script + ">  "
                 + " </td> ");
        else
          saida.println(" <td></td> ");
      }

      if( tipo.equals("Caracter") ) {
        javascript += " if(document.iForm.ca"+nome.replace('.','_') +".value.length > 0) { \n"
                    + "   preenchido = true; \n"
                    + " } \n";
      }
      else {
        javascript += " if(document.iForm.ca"+nome.replace('.','_') +"ini.value.length > 0) { \n" ;
        if( tipo.equals("Inteiro") ) {
          javascript += " if(! isNumber(document.iForm.ca"+nome.replace('.','_') +"ini.value) ) { \n"
                      + "   jserro = jserro + \"\\nO 1° campo " + label + " é numérico\"; \n"
                      + "   validaPesq = false; \n"
                      + " } \n"
                      + " else { \n"
                      + "   if(document.iForm.c"+nome.replace('.','_') +"inter[1].selected) { \n"
                      + "     if(document.iForm.ca"+ nome.replace('.','_') +"fim.value.length==0) { \n"
                      + "        jserro = jserro + \"\\nPreencha o 2° campo do " + label + "\"; \n"
                      + "        validaPesq = false; \n"
                      + "     } \n"
                      + "     else { \n"
                      + "       if(! isNumber(document.iForm.ca"+nome.replace('.','_') +"fim.value) ) { \n"
                      + "         jserro = jserro + \"\\nO 2° campo " + label + " é numérico\"; \n"
                      + "         validaPesq = false; \n"
                      + "       } \n"
                      + "       else if(document.iForm.ca"+ nome.replace('.','_') +"ini.value > document.iForm.ca"+ nome.replace('.','_') +"fim.value) { \n"
                      + "         jserro = jserro + \"\\nO 1° campo do " + label + " é maior que o 2° campo\"; \n"
                      + "         validaPesq = false; \n"
                      + "       } \n"
                      + "     } \n"
                      + "   } \n"
                      + " } \n";
        }
        else if( tipo.equals("Numero") ) {
          javascript += " if(! verificaNumero(document.iForm.ca"+nome.replace('.','_') +"ini) ) { \n"
                      + "   jserro = jserro + \"\\nO 1° campo " + label + " é numérico\"; \n"
                      + "   validaPesq = false; \n"
                      + " } \n"
                      + " else { \n"
                      + "   if(document.iForm.c"+nome.replace('.','_') +"inter[1].selected) { \n"
                      + "     if(document.iForm.ca"+ nome.replace('.','_') +"fim.value.length==0) { \n"
                      + "        jserro = jserro + \"\\nPreencha o 2° campo do " + label + "\"; \n"
                      + "        validaPesq = false; \n"
                      + "     } \n"
                      + "     else { \n"
                      + "       if(! verificaNumero(document.iForm.ca"+nome.replace('.','_') +"fim) ) { \n"
                      + "         jserro = jserro + \"\\nO 2° campo " + label + " é numérico\"; \n"
                      + "         validaPesq = false; \n"
                      + "       } \n"
                      + "       else if(document.iForm.ca"+ nome.replace('.','_') +"ini.value > document.iForm.ca"+ nome.replace('.','_') +"fim.value) { \n"
                      + "         jserro = jserro + \"\\nO 1° campo do " + label + " é maior que o 2° campo\"; \n"
                      + "         validaPesq = false; \n"
                      + "       } \n"
                      + "     } \n"
                      + "   } \n"
                      + " } \n";
        }
        else if( tipo.equals("Data") ) {
          javascript += " if(! ValidaData(document.iForm.ca"+nome.replace('.','_') +"ini) ) { \n"
                      + "   jserro = jserro + \"\\nA 1° data do campo " + label + " é inválida\"; \n"
                      + "   validaPesq = false; \n"
                      + " } \n"
                      + " else { \n"
                      + "   if(document.iForm.c"+nome.replace('.','_') +"inter[1].selected) { \n"
                      + "     if(! ValidaData(document.iForm.ca"+nome.replace('.','_') +"fim) ) { \n"
                      + "       jserro = jserro + \"\\nA 2° data do campo " + label + " é inválida\"; \n"
                      + "       validaPesq = false; \n"
                      + "     } \n"
                      + "     else if( ComparaData(document.iForm.ca"+nome.replace('.','_') +"ini, document.iForm.ca"+nome.replace('.','_') +"fim) > 0 ) { \n"
                      + "         jserro = jserro + \"\\nO 1° campo da " + label + " é maior que o 2° campo\"; \n"
                      + "         validaPesq = false; \n"
                      + "     } \n"
                      + "   } \n"
                      + " } \n";
        }

        javascript += "   preenchido = true; \n"
                    + " } \n";
      }

      if(layoutPesq) {
        if(! ultimo ) {
          saida.println(" <td class=\"txtBlack\">  "
                    + "<font Class=\"txtBlack\"> "
                    + "     <input class=\"cmpRadio\" type=\"RADIO\" name=\"c" + nome.replace('.','_') + "\" value=\"e\" checked> e <br>"
                    + "     <input class=\"cmpRadio\" type=\"RADIO\" name=\"c" + nome.replace('.','_') + "\" value=\"ou\" > ou "
                    + " </font> "
                    + "   </td> "
                    + "  </tr>  " );
        }
        else {
          saida.println(" <td align=\"right\"> &nbsp;&nbsp;"
                    + "   </td> "
                    + "  </tr>  " );
        }
        saida.println(" </table></td></tr> " );
      }
    }

    //monta a linha com o elemento "checkbox"
    private void montaLinhaCheck(String nome, String labelPesq, String label[], String valor[], String checked[], String script, boolean ultimo) throws Exception{
      if(! layoutSubmitPesq) return; //1107
      if(layoutPesq) {
        saida.println(" <tr> <td></td> <td> "
                + "   <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> "
                + "    <tr><td colspan=\"4\"><font class=\"txtBlack\"> "
                + "<input class=\"cmpCheck\" TYPE=\"CHECKBOX\" NAME=\"ch" + nome.replace('.','_') + "ctrl\" VALUE=\"sim\"> "
                + labelPesq + "</font></td></tr> ");
      }
      javascript += " if(document.iForm.ch"+nome.replace('.','_') +"ctrl.checked) { \n";

      int iCheck = 0;
      saida.print("<tr><td colspan=\"4\"><table><tr>");
      for(int i=0; i<label.length; i++) {
        saida.println(" <td><input class=\"cmpCheck\" TYPE=\"CHECKBOX\" NAME=\"ch" + nome.replace('.','_') + i + "\" ");
        if( (checked != null) && (checked[iCheck].equals(valor[i])) ) {
          saida.print(" CHECKED ");
          iCheck++;
        }

        saida.println(" VALUE=\""+ valor[i]+"\"" + script + "> "
               + " </td><td width=\"3\"></td> "
               + " <td class=\"txtBlack\" valign=\"middle\"> "
               + label[i] + "</td> " );

        if(i==0) {
          javascript += " if(document.iForm.ch"+nome.replace('.','_') +i+".checked) { \n"
                      + "    preenchido = true; \n"
                      + " } \n";
        }
        else {
          javascript += " else if(document.iForm.ch"+nome.replace('.','_') +i+".checked) { \n"
                      + "         preenchido = true; \n"
                      + " } \n";
        }
      } //for
      saida.println("</tr></table></td>");

      javascript += " } \n";

      if(layoutPesq) {
        if(! ultimo ) {
          saida.println(" <td class=\"txtBlack\">  "
                    + "<font Class=\"txtBlack\"> "
                    + "     <input class=\"cmpRadio\" type=\"RADIO\" name=\"c" + nome.replace('.','_') + "\" value=\"e\" checked> e <br> "
                    + "     <input class=\"cmpRadio\" type=\"RADIO\" name=\"c" + nome.replace('.','_') + "\" value=\"ou\" > ou "
                    + " </font> "
                    + "   </td> "
                    + "  </tr>  " );
        }
        else {
          saida.println(" <td class=\"txtBlack\" align=\"right\"> &nbsp;&nbsp;"
                    + "   </td> "
                    + "  </tr>  " );
        }
        saida.println(" </table></td></tr> " );
      }

    }

    //monta a linha com o elemento "checkSql"
    private void montaLinhaCheckSql(String labelPesq, boolean ultimo) throws Exception{
      if(! layoutSubmitPesq) return; //1107
      if(layoutPesq) {
        saida.println(" <tr> <td></td> <td> "
                + "   <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> "
                + "    <tr><td colspan=\"4\"><font class=\"txtBlack\"> "
                + " <input class=\"cmpCheck\" TYPE=\"CHECKBOX\" NAME=\"ch" + nomeColunaPesquisa.elementAt(nomeColunaPesquisa.size()-1).toString().replace('.','_') + "ctrl\" VALUE=\"sim\"> "
                + labelPesq + "</font></td></tr> ");
      }

      int tam = checkSql.executePesquisa(saida);
      tamCheck.addElement("ch"+nomeColunaPesquisa.elementAt(nomeColunaPesquisa.size()-1).toString().replace('.','_')+"Þ"+tam);

      javascript += " if(document.iForm.ch"+nomeColunaPesquisa.elementAt(nomeColunaPesquisa.size()-1).toString().replace('.','_') +"ctrl.checked) { \n";
      for(int i=0; i<tam; i++) {
        if(i==0)
          javascript += " if(document.iForm.ch"+nomeColunaPesquisa.elementAt(nomeColunaPesquisa.size()-1).toString().replace('.','_') +i+".checked) { \n"
                      + "    preenchido = true; \n"
                      + " } \n";
        else
          javascript += " else if(document.iForm.ch"+nomeColunaPesquisa.elementAt(nomeColunaPesquisa.size()-1).toString().replace('.','_') +i+".checked) { \n"
                      + "         preenchido = true; \n"
                      + " } \n";

      } //for
      javascript += " } \n";

      if(layoutPesq) {
        if(! ultimo ) {
          saida.println(" <td class=\"txtBlack\">  "
                    + "<font Class=\"txtBlack\"> "
                    + "     <input class=\"cmpRadio\" type=\"RADIO\" name=\"c" + nomeColunaPesquisa.elementAt(nomeColunaPesquisa.size()-1).toString().replace('.','_') + "\" value=\"e\" checked> e <br> "
                    + "     <input class=\"cmpRadio\" type=\"RADIO\" name=\"c" + nomeColunaPesquisa.elementAt(nomeColunaPesquisa.size()-1).toString().replace('.','_') + "\" value=\"ou\" > ou "
                    + " </font> "
                    + "   </td> "
                    + "  </tr>  " );
        }
        else {
          saida.println(" <td class=\"txtBlack\" align=\"right\"> &nbsp;&nbsp;"
                    + "   </td> "
                    + "  </tr>  " );
        }
        saida.println(" </table></td></tr> " );
      }
    }

    //monta a linha com o elemento "radio"
    private void montaLinhaRadio(String nome, String label[], String valor[], String checked, String script, boolean ultimo) throws Exception{
      if(! layoutSubmitPesq) return; //1107
      if(layoutPesq) {
        saida.println(" <tr> <td></td> <td> "
                + "   <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> "
                + "    <tr><td colspan=\"4\"><font class=\"txtBlack\"> "
                + " <input class=\"cmpCheck\" TYPE=\"hidden\" NAME=\"ra" + nome.replace('.','_') + "ctrl\" VALUE=\"sim\"> "
                + "</font></td></tr> ");
      }
//      javascript += " if(document.iForm.ra"+nome.replace('.','_') +"ctrl.checked) { \n";

      saida.print("<tr><td colspan=\"4\"><table><tr>");
      for(int i=0; i<label.length; i++) {
        if(i == 0 )
          javascript += "   if(document.iForm.ra"+nome.replace('.','_') +"["+i+"].checked) { \n"
                      + "     preenchido = true; \n"
                      + "   } \n";
        else
          javascript += "   else if(document.iForm.ra"+nome.replace('.','_') +"["+i+"].checked) { \n"
                      + "     preenchido = true; \n"
                      + "   } \n";

        saida.print("<td><input class=\"cmpRadio\" TYPE=\"RADIO\" NAME=\"ra" + nome.replace('.','_') + "\" ");
        if(checked.equals(valor[i]))
          saida.print(" CHECKED ");
          saida.println(" VALUE=\""+ valor[i]+"\"" + script + ">"
               + "</td><td width=\"3\"></td> "
               + " <td class=\"txtBlack\" valign=\"middle\"> "
               + label[i] + "</td> " );
      } //for
      saida.println("</tr></table></td>");

//      javascript += " } \n";

      if(layoutPesq) {
        if(! ultimo ) {
          saida.println(" <td class=\"txtBlack\">  "
                    + "<font Class=\"txtBlack\"> "
                    + "     <input class=\"cmpRadio\" type=\"RADIO\" name=\"c" + nome.replace('.','_') + "\" value=\"e\" checked> e <br> "
                    + "     <input class=\"cmpRadio\" type=\"RADIO\" name=\"c" + nome.replace('.','_') + "\" value=\"ou\" > ou "
                    + " </font> "
                    + "   </td> "
                    + "  </tr>  " );
        }
        else {
          saida.println(" <td class=\"txtBlack\" align=\"right\"> &nbsp;&nbsp;"
                    + "   </td> "
                    + "  </tr>  " );
        }
        saida.println(" </table></td></tr> " );
      }
    }

    //monta a linha com o elemento "radio"
    private void montaLinhaRadio(String nome, String labelPesq, String label[], String valor[], String checked, String script, boolean ultimo) throws Exception{
      if(! layoutSubmitPesq) return; //1107
      if(layoutPesq) {
        saida.println(" <tr> <td></td> <td> "
                + "   <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> "
                + "    <tr><td colspan=\"4\"><font class=\"txtBlack\"> "
                + " <input class=\"cmpCheck\" TYPE=\"CHECKBOX\" NAME=\"ra" + nome.replace('.','_') + "ctrl\" VALUE=\"sim\"> "
                + labelPesq + "</font></td></tr> ");
      }
      javascript += " if(document.iForm.ra"+nome.replace('.','_') +"ctrl.checked) { \n";

      saida.print("<tr><td colspan=\"4\"><table><tr>");
      for(int i=0; i<label.length; i++) {
        if(i == 0 )
          javascript += "   if(document.iForm.ra"+nome.replace('.','_') +"["+i+"].checked) { \n"
                      + "     preenchido = true; \n"
                      + "   } \n";
        else
          javascript += "   else if(document.iForm.ra"+nome.replace('.','_') +"["+i+"].checked) { \n"
                      + "     preenchido = true; \n"
                      + "   } \n";

        saida.print("<td><input class=\"cmpRadio\" TYPE=\"RADIO\" NAME=\"ra" + nome.replace('.','_') + "\" ");
        if(checked.equals(valor[i]))
          saida.print(" CHECKED ");
          saida.println(" VALUE=\""+ valor[i]+"\"" + script + ">"
               + "</td><td width=\"3\"></td> "
               + " <td class=\"txtBlack\" valign=\"middle\"> "
               + label[i] + "</td> " );
      } //for
      saida.println("</tr></table></td>");

      javascript += " } \n";

      if(layoutPesq) {
        if(! ultimo ) {
          saida.println(" <td class=\"txtBlack\">  "
                    + "<font Class=\"txtBlack\"> "
                    + "     <input class=\"cmpRadio\" type=\"RADIO\" name=\"c" + nome.replace('.','_') + "\" value=\"e\" checked> e <br> "
                    + "     <input class=\"cmpRadio\" type=\"RADIO\" name=\"c" + nome.replace('.','_') + "\" value=\"ou\" > ou "
                    + " </font> "
                    + "   </td> "
                    + "  </tr>  " );
        }
        else {
          saida.println(" <td class=\"txtBlack\" align=\"right\"> &nbsp;&nbsp;"
                    + "   </td> "
                    + "  </tr>  " );
        }
        saida.println(" </table></td></tr> " );
      }
    }

    //monta a linha com o elemento "radioSql"
    private void montaLinhaRadioSql(String labelPesq, boolean ultimo) throws Exception{
      if(! layoutSubmitPesq) return; //1107
      if(layoutPesq) {
        saida.println(" <tr> <td></td> <td> "
                + "   <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> "
                + "    <tr><td colspan=\"4\"><font class=\"txtBlack\"> "
                + " <input class=\"cmpCheck\" TYPE=\"CHECKBOX\" NAME=\"ra" + nomeColunaPesquisa.elementAt(nomeColunaPesquisa.size()-1).toString().replace('.','_') + "ctrl\" VALUE=\"sim\"> "
                + labelPesq + "</font></td></tr> ");
      }

      radioSql.executePesquisa(saida);

      javascript += " if(document.iForm.ra"+nomeColunaPesquisa.elementAt(nomeColunaPesquisa.size()-1).toString().replace('.','_') +"ctrl.checked) { \n"
                  + "    if(document.iForm.ra"+nomeColunaPesquisa.elementAt(nomeColunaPesquisa.size()-1).toString().replace('.','_') +".checked) { \n"
                  + "      preenchido = true; \n"
                  + "    } \n"
                  + "  } \n";

      if(layoutPesq) {
        if(! ultimo ) {
          saida.println(" <td class=\"txtBlack\">  "
                    + "<font Class=\"txtBlack\"> "
                    + "     <input class=\"cmpRadio\" type=\"RADIO\" name=\"c" + nomeColunaPesquisa.elementAt(nomeColunaPesquisa.size()-1).toString().replace('.','_') + "\" value=\"e\" checked> e <br> "
                    + "     <input class=\"cmpRadio\" type=\"RADIO\" name=\"c" + nomeColunaPesquisa.elementAt(nomeColunaPesquisa.size()-1).toString().replace('.','_') + "\" value=\"ou\" > ou "
                    + " </font> "
                    + "   </td> "
                    + "  </tr>  " );
        }
        else {
          saida.println(" <td class=\"txtBlack\" align=\"right\"> &nbsp;&nbsp;"
                    + "   </td> "
                    + "  </tr>  " );
        }
        saida.println(" </table></td></tr> " );
      }
    }

    //monta a linha com o elemento "combo"
    private void montaLinhaCombo(String nome, String label, String desc[], String valor[], String selected, int size, boolean multiple, String script, boolean ultimo) throws Exception{
      if(! layoutSubmitPesq) return; //1107
      if(layoutPesq) {
        saida.println(" <tr> <td></td> <td> "
                + "   <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> "
                + "    <tr><td colspan=\"4\"><font class=\"txtBlack\">" + label + "</font></td></tr> ");
      }
      saida.println("<tr><td colspan=\"4\"><SELECT class=\"cmpSelect\" NAME=\"co" + nome.replace('.','_') + "\" SIZE=\"" + size + "\" ");
      if(multiple)
        saida.print(" MULTIPLE ");

      saida.println( script + " >" );

      for(int i=0; i<desc.length; i++) {
        saida.print("<OPTION class=\"cmpOption\" ");
        if(selected.equals(valor[i]))
          saida.print(" SELECTED ");
        saida.println(" VALUE=\"" + valor[i]+"\">" + desc[i] + "</option>");
      } //for

      saida.println(" </SELECT> </td>");

      if( valor[0].equals("") ) {
         javascript += "    if(! (document.iForm.co"+nome.replace('.','_') +".selectedIndex == 0)) { \n"
                     + "      preenchido = true; \n"
                     + "    } \n";
      }

      if(layoutPesq) {
        if(! ultimo ) {
          saida.println(" <td class=\"txtBlack\">  "
                    + "<font Class=\"txtBlack\"> "
                    + "     <input class=\"cmpRadio\" type=\"RADIO\" name=\"c" + nome.replace('.','_') + "\" value=\"e\" checked> e <br> "
                    + "     <input class=\"cmpRadio\" type=\"RADIO\" name=\"c" + nome.replace('.','_') + "\" value=\"ou\" > ou "
                    + " </font> "
                    + "   </td> "
                    + "  </tr>  " );
        }
        else {
          saida.println(" <td class=\"txtBlack\" align=\"right\"> &nbsp;&nbsp;"
                    + "   </td> "
                    + "  </tr>  " );
        }
        saida.println(" </table></td></tr> " );
      }
    }

    //monta a linha com o elemento "comboSql"
    private void montaLinhaComboSql(String label, boolean ultimo) throws Exception{
      if(! layoutSubmitPesq) return; //1107
      if(layoutPesq) {
        saida.println(" <tr> <td></td> <td> "
                + "   <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> "
                + "    <tr><td colspan=\"4\"><font class=\"txtBlack\">" + label + "</font></td></tr> ");
      }

      saida.println("<tr><td colspan=\"4\">");
      comboSql.execute(saida);
      saida.println(" </td>  " );

      if(layoutPesq) {
        if(! ultimo ) {
          saida.println(" <td class=\"txtBlack\">  "
                    + "<font Class=\"txtBlack\"> "
                    + "     <input class=\"cmpRadio\" type=\"RADIO\" name=\"c" + nomeColunaPesquisa.elementAt(nomeColunaPesquisa.size()-1).toString().replace('.','_') + "\" value=\"e\" checked> e <br> "
                    + "     <input class=\"cmpRadio\" type=\"RADIO\" name=\"c" + nomeColunaPesquisa.elementAt(nomeColunaPesquisa.size()-1).toString().replace('.','_') + "\" value=\"ou\" > ou "
                    + " </font> "
                    + "   </td> "
                    + "  </tr>  " );
        }
        else {
          saida.println(" <td class=\"txtBlack\" align=\"right\"> &nbsp;&nbsp;"
                    + "   </td> "
                    + "  </tr>  " );
        }
        saida.println(" </table></td></tr> " );
      }
    }

//===================== Metodos para montar a clausula WHERE ====================
    public String montaWhere( HttpSession session,
                              HttpServletRequest request,
                              String pPagina )
           throws Exception {
      vet_tipoparametros.removeAllElements();
      vet_valorparametros.removeAllElements();
      bMontaWhere = true;

      String sWhere = "";

      // adiciona os parametros da string complemento
      if(compTipoParam != null ) {
        for(int i=0; i<compTipoParam.length; i++ ) {
          addParametro(compTipoParam[i]+"", compValorParam[i]);
        }
      }

      // adiciona os parametros dos campos da pesquisa
//System.out.println("tam="+tipoColunaPesquisa.size());
      for(int i=0; i<tipoColunaPesquisa.size(); i++ ) {

       switch(Integer.parseInt(tipoCampoPesquisa.elementAt(i).toString())) {
          //combo
          case 1: {
            if( request.getParameter("co"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')) != null ) {
              int ini = 0;
              if( request.getParameter("co"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')).equals("") )
                ini++;

              String valor[] = request.getParameterValues("co"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
              String whereCombo ="";
              for(int x=ini; x<valor.length; x++ ){
                if(whereCombo.length()>0)
                  whereCombo += ",?";
                else
                {  //2911 whereCombo = nomeColunaPesquisa.elementAt(i)+ " IN ( ?";
                   whereCombo = clausulaPesquisa.elementAt(i)+ " IN ( ?";
                }
                addParametro(tipoColunaPesquisa.elementAt(i)+"", valor[x]);
              }

              if( whereCombo.length() > 0 ) {
                 whereCombo += ")";
                 sWhere = comparaWhere(request, sWhere, whereCombo ,"c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
              }
            }
            break;
          }
          //radio
          case 2: {
            if( (request.getParameter("ra"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"ctrl") != null) &&
                (! request.getParameter("ra"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"ctrl").equals("")) ) {

                if( (request.getParameter("ra"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')) != null) &&
                (! request.getParameter("ra"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')).equals("")) ) {
                   //2911 sWhere = comparaWhere(request, sWhere, nomeColunaPesquisa.elementAt(i)+ " = ? ","c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                   sWhere = comparaWhere(request, sWhere, clausulaPesquisa.elementAt(i)+ " = ? ","c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                   addParametro(tipoColunaPesquisa.elementAt(i)+"", request.getParameter("ra"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')));
                }
            }
            break;
          }
          //check
          case 3: {
            if( (request.getParameter("ch"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"ctrl") != null) &&
                (! request.getParameter("ch"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"ctrl").equals("")) ) {

                String check_tam = "";
                String nome = "";
                int tam = 0;
                for(int x=0; x<tamCheck.size(); x++) {
                  check_tam = tamCheck.elementAt(x)+"";
                  int index = check_tam.indexOf("Þ");
                  nome = check_tam.substring(0,index);
                  if(nome.equals("ch"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')) ) {
                     tam = Integer.parseInt(check_tam.substring(index+1));
                     x = tamCheck.size() + 1;
                  }
                }

                String whereCheck ="";
                for( int x=0; x<tam; x++ ){
                  if( (request.getParameter(nome + x) != null) &&
                      (! request.getParameter(nome + x).equals("")) ) {
                        if(whereCheck.length()>0)
                          whereCheck += ",?";
                        else
                        {  //2911 whereCheck = nomeColunaPesquisa.elementAt(i)+ " IN ( ?";
                           whereCheck = clausulaPesquisa.elementAt(i)+ " IN ( ?";
                        }
                        addParametro(tipoColunaPesquisa.elementAt(i)+"", request.getParameter(nome+x));
                  }
                }

                if( whereCheck.length() > 0 ) {
                   whereCheck += ")";
                   sWhere = comparaWhere(request, sWhere, whereCheck ,"c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                }
            }
            break;
          }
          //campo
          case 4: {
            sWhere = montaWhereCampo(request, i, sWhere);
            break;
          }
          default: {
            // mensagem de erro
            Exception e = new Exception("Não foi definido um parâmetro!");
            throw e;
          }
       }//switch
      }//for

      //coloca a clausula where na frente
      if( (sWhere.length() > 0) || (compSql.length() > 0) ) {
        if( (sWhere.length() > 0) && (compSql.length() > 0) ) {
          sWhere = " WHERE (" + compSql + ") AND (" + sWhere.substring(0, (sWhere.length() - 4)) + ") " + compSqlPost;
        }
        else if( (sWhere.length() == 0) && (compSql.length() > 0) ) {
          sWhere = " WHERE " + compSql + " " + compSqlPost;
        }
        else {
         sWhere = " WHERE " + sWhere.substring(0, (sWhere.length() - 4)) + " " + compSqlPost;
        }
      }

//System.out.println("where=" + sWhere);

      String orderby = "";
      String colordena = "";
      String stipo = "";
      for( int i=0; i<linhasPesq; i++ ) {
        if( (request.getParameter("cOrdenacaoCol"+i) != null) &&
            (! request.getParameter("cOrdenacaoCol"+i).equals("")) ) {

            colordena = request.getParameter("cOrdenacaoCol"+i);
            stipo = "";
            try {
               for(int a=0; a<nomesColunasOrdenacao.size(); a++)
               {  if(nomesColunasOrdenacao.elementAt(a) != null)
                  {  if(nomesColunasOrdenacao.elementAt(a).toString().equals(colordena))
                     {  if(tiposColunasOrdenacao.elementAt(a) != null)
                        {  if(! tiposColunasOrdenacao.elementAt(a).toString().equals(""))
                           {   stipo = tiposColunasOrdenacao.elementAt(a).toString();
                           }
                        }
                     }
                  }
               }
            }
            catch (Exception ex) { }
            if(stipo.equals(""))
            {
              if(colordena.startsWith("DT"))
              {  if( orderby.length() > 0 )
                   orderby += " , " + request.getParameter("cOrdenacaoCol"+i) + " ";
                 else
                   orderby += " " + request.getParameter("cOrdenacaoCol"+i) + " ";
              }
              else
              {  if( orderby.length() > 0 )
                   orderby += " , UPPER(" + request.getParameter("cOrdenacaoCol"+i) + ")";
                 else
                   orderby += "UPPER(" + request.getParameter("cOrdenacaoCol"+i) + ")";
              }
            }
            else
            {
              String sUPPER = "";
              if(  stipo.equals(""+Types.VARCHAR) ||
                   stipo.equals(""+Types.LONGVARCHAR) )
                 sUPPER = "UPPER(";
              else if(  stipo.equals(""+Types.INTEGER) ||
                   stipo.equals(""+Types.BIGINT) ||
                   stipo.equals(""+Types.DECIMAL) ||
                   stipo.equals(""+Types.NUMERIC) ||
                   stipo.equals(""+Types.DOUBLE) ||
                   stipo.equals(""+Types.REAL) ||
                   stipo.equals(""+Types.FLOAT) ||
                   stipo.equals(""+Types.DATE) ||
                   stipo.equals(""+Types.ROWID) )
                 sUPPER = "";
             else
                 sUPPER = "UPPER(";
             if( orderby.length() > 0 )
                orderby += " , " + sUPPER + request.getParameter("cOrdenacaoCol"+i) + (sUPPER.equals("")? "" : ")");
             else
                orderby += sUPPER + request.getParameter("cOrdenacaoCol"+i) + (sUPPER.equals("")? "" : ")");
            }
            if( (request.getParameter("cOrdenacao"+i) != null) &&
                (request.getParameter("cOrdenacao"+i).equals("ASC")) )
                orderby += " ASC ";
            else if( (request.getParameter("cOrdenacao"+i) != null) &&
                     (request.getParameter("cOrdenacao"+i).equals("DESC")) )
                orderby += " DESC ";
        }
      }

      if( ordenaPesq.length() > 0 ) {
        if( orderby.length() > 0 )
            sWhere += " ORDER BY " + ordenaPesq + " , " + orderby;
        else
            sWhere += " ORDER BY " + ordenaPesq;
      }
      else {
        if( orderby.length() > 0 )
            sWhere += " ORDER BY " + orderby;
      }

//System.out.println("where/ORDERBY=" + sWhere);
      UtilBean.session_putValue(session, pPagina + "_WHERE", sWhere);
      return sWhere;
    }

    private String montaWhereCampo(HttpServletRequest request, int i, String sWhere ) throws Exception {
       switch(Integer.parseInt(tipoColunaPesquisa.elementAt(i)+""))
       {
          case Types.BIGINT:
          case Types.INTEGER:
          case Types.DECIMAL:
          case Types.NUMERIC:
          case Types.DOUBLE:
          case Types.REAL:
          case Types.FLOAT:
          case Types.DATE: {
              //colunas com comparacoes numericas
              if( (request.getParameter("ca"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"ini")!= null) &&
                  (! request.getParameter("ca"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"ini").equals("")) ) {

                  if( (request.getParameter("c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"inter")!= null) &&
                      (! request.getParameter("c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"inter").equals("")) &&
                      (request.getParameter("ca"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"fim")!= null) &&
                      (! request.getParameter("ca"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"fim").equals("")) ) {

                    if(request.getParameter("c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"inter").equalsIgnoreCase("ate") ){
                      //2911 sWhere = comparaWhere(request, sWhere, nomeColunaPesquisa.elementAt(i)+ " BETWEEN ? AND ? ","c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                      sWhere = comparaWhere(request, sWhere, clausulaPesquisa.elementAt(i)+ " BETWEEN ? AND ? ","c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                      addParametro(tipoColunaPesquisa.elementAt(i)+"", request.getParameter("ca"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"ini"));
                      addParametro(tipoColunaPesquisa.elementAt(i)+"", request.getParameter("ca"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"fim"));
                    }
                  }
                  else if( (request.getParameter("c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"inter")!= null) &&
                      (! request.getParameter("c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"inter").equals("")) ) {

                    if(request.getParameter("c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"inter").equalsIgnoreCase("opcoes") ){
                      String opcao = request.getParameter("ca"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"ini");
                      int index = 0;
                      int indexAnt = 0;
                      String conteudo = "";
                      do {
                        index = opcao.indexOf( ",", indexAnt );
                        if( index >= indexAnt ) {
                          addParametro(tipoColunaPesquisa.elementAt(i)+"", opcao.substring( indexAnt, index ));
                          if( indexAnt == 0 ) {
                            conteudo += "?";
                          }
                          else {
                            conteudo += ",?";
                          }
                          indexAnt = index + 1;
                        }
                        else {
                          addParametro(tipoColunaPesquisa.elementAt(i)+"", opcao.substring(indexAnt));
                          if( conteudo.length() > 0 ) {
                             conteudo += ",?";
                          }
                          else {
                             conteudo = "?";
                          }
                          conteudo = " IN (" + conteudo + ") ";
                          indexAnt = index;
                        }
                      } while( index != indexAnt );
                      //2911 sWhere = comparaWhere(request, sWhere, nomeColunaPesquisa.elementAt(i)+conteudo,"c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                      sWhere = comparaWhere(request, sWhere, clausulaPesquisa.elementAt(i)+conteudo,"c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                    }
                    else if(request.getParameter("c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"inter").equalsIgnoreCase("maior") ){
                      //2911 sWhere = comparaWhere(request, sWhere, nomeColunaPesquisa.elementAt(i)+ " > ?","c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                      sWhere = comparaWhere(request, sWhere, clausulaPesquisa.elementAt(i)+ " > ?","c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                      addParametro(tipoColunaPesquisa.elementAt(i)+"", request.getParameter("ca"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"ini"));
                    }
                    else if(request.getParameter("c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"inter").equalsIgnoreCase("menor") ){
                      //2911 sWhere = comparaWhere(request, sWhere, nomeColunaPesquisa.elementAt(i)+ " < ?","c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                      sWhere = comparaWhere(request, sWhere, clausulaPesquisa.elementAt(i)+ " < ?","c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                      addParametro(tipoColunaPesquisa.elementAt(i)+"", request.getParameter("ca"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"ini"));
                    }
                  }
                  else {
                    //2911 sWhere = comparaWhere(request, sWhere, nomeColunaPesquisa.elementAt(i)+ " = ? ","c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                    sWhere = comparaWhere(request, sWhere, clausulaPesquisa.elementAt(i)+ " = ? ","c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                    addParametro(tipoColunaPesquisa.elementAt(i)+"", request.getParameter("ca"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"ini"));
                  }
              }
            break;
          }
          case Types.LONGVARCHAR:
          case Types.VARCHAR: {
              //colunas com comparacoes de caracteres
              if( (request.getParameter("ca"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'))!= null) &&
                  (! request.getParameter("ca"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')).equals("")) ) {

                  if( (request.getParameter("c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"like")!= null) &&
                      (! request.getParameter("c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"like").equals("")) ) {

                    if(request.getParameter("c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"like").equalsIgnoreCase("antes") ){
                      //2911 sWhere = comparaWhere(request, sWhere, "UPPER("+nomeColunaPesquisa.elementAt(i)+ ") like UPPER(?)","c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                      sWhere = comparaWhere(request, sWhere, "UPPER("+clausulaPesquisa.elementAt(i)+ ") like UPPER(?)","c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                      addParametro(tipoColunaPesquisa.elementAt(i)+"", "%"+request.getParameter("ca"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')));
                    }
                    else if(request.getParameter("c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"like").equalsIgnoreCase("depois") ){
                      //2911 sWhere = comparaWhere(request, sWhere, "UPPER("+nomeColunaPesquisa.elementAt(i)+ ") like UPPER(?)","c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                      sWhere = comparaWhere(request, sWhere, "UPPER("+clausulaPesquisa.elementAt(i)+ ") like UPPER(?)","c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                      addParametro(tipoColunaPesquisa.elementAt(i)+"", request.getParameter("ca"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'))+"%");
                    }
                    else if(request.getParameter("c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"like").equalsIgnoreCase("tudo") ){
                      //2911 sWhere = comparaWhere(request, sWhere, "UPPER("+nomeColunaPesquisa.elementAt(i)+ ") like UPPER(?)","c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                      sWhere = comparaWhere(request, sWhere, "UPPER("+clausulaPesquisa.elementAt(i)+ ") like UPPER(?)","c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                      addParametro(tipoColunaPesquisa.elementAt(i)+"", "%"+request.getParameter("ca"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'))+"%");
                    }
                    else if(request.getParameter("c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')+"like").equalsIgnoreCase("opcoes") ){
                      String opcao = request.getParameter("ca"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                      int index = 0;
                      int indexAnt = 0;
                      String conteudo = "";
                      if( opcao.length() > 0 ) {
                        do {
                          index = opcao.indexOf( ",", indexAnt );
                          if( index >= indexAnt ) {
                            addParametro(tipoColunaPesquisa.elementAt(i)+"", opcao.substring( indexAnt, index ));
                            if( indexAnt == 0 ) {
                              conteudo += "UPPER(?)";
                            }
                            else {
                              conteudo += ",UPPER(?)";
                            }
                            indexAnt = index + 1;
                          }
                          else {
                            addParametro(tipoColunaPesquisa.elementAt(i)+"", opcao.substring(indexAnt));
                            if( conteudo.length() > 0 ) {
                               conteudo += ",UPPER(?)";
                            }
                            else {
                               conteudo = "UPPER(?)";
                            }
                            conteudo = " IN (" + conteudo + ") ";
                            indexAnt = index;
                          }
                        } while( index != indexAnt );
                      }
                      //2911 sWhere = comparaWhere(request, sWhere, "UPPER("+nomeColunaPesquisa.elementAt(i)+") "+conteudo,"c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                      sWhere = comparaWhere(request, sWhere, "UPPER("+clausulaPesquisa.elementAt(i)+") "+conteudo,"c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                    }
                  }
                  else {
                      //2911 sWhere = comparaWhere(request, sWhere, "UPPER("+nomeColunaPesquisa.elementAt(i)+ ") = UPPER(?) ","c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                      sWhere = comparaWhere(request, sWhere, "UPPER("+clausulaPesquisa.elementAt(i)+ ") = UPPER(?) ","c"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_'));
                      addParametro(tipoColunaPesquisa.elementAt(i)+"", request.getParameter("ca"+nomeColunaPesquisa.elementAt(i).toString().replace('.','_')));
                  }
              }
            break;
          }
          default: {
            // mensagem de erro
            Exception e = new Exception("Não foi definido um parâmetro!");
            throw e;
          }
       } //fim switch

       return sWhere;
    }

    private String comparaWhere(HttpServletRequest request, String sWhere, String condicao, String combo){

      if( (request.getParameter(combo)!= null) &&
          (! request.getParameter(combo).equals("")) ) {
        if(request.getParameter(combo).equalsIgnoreCase("E") )
         sWhere += "(" + condicao + ")  AND ";
        else if(request.getParameter(combo).equalsIgnoreCase("OU") )
         sWhere += "(" + condicao + ")  OR  ";
      }
      else {
        sWhere += "(" + condicao + ") AND ";
      }

      return sWhere;
   }

    public void liberaRecursoPesq() {
//System.out.print("Libera Recursos Pesq !!!!!!");

      if(getregAtual() > 0)
         beforeFirst();

      nomeColunaPesquisa.removeAllElements();
      tipoColunaPesquisa.removeAllElements();
      tipoCampoPesquisa.removeAllElements();
      clausulaPesquisa.removeAllElements();
      tamCheck.removeAllElements();
      layoutPesq = true;
      javascript = "";
      compSql = ""; //0606
      compSqlPost = ""; //0606
      compTipoParam = null; //0606
      compValorParam = null; //0606
      comboSql = null; //0606
      radioSql = null; //0606
      checkSql = null; //0606
      linhasPesq = 0; //0606
      ordenaPesq = ""; //0606
    }

//==================== Fim dos Metodos da clausula WHERE ==========================
//---------------------------------- FIM Alexandre -------------------------------------------

   Hashtable ht_session   = new Hashtable(); //ht_session.put(num_reg, value); ht_session.get(key);

   public Object getRepositorio(Object pkey) {
      return ht_session.get(pkey);
   }
   public void putRepositorio(Object pkey, Object value) {
      ht_session.put(pkey, value);
   }
   public Object removeRepositorio(Object pkey) {
      return ht_session.remove(pkey);
   }
   public void limpaRepositorio() {
      ht_session.clear();
   }
   public void setParametroPaginacao( String param )
   {   if(param != null) ParametroPaginacao = param;
   }

} // fim classe
