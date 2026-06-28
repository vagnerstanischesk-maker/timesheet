/**************************************************************************
Empresa: Triscal
Autor: Christiano Chamma
Data: Janeiro de 2001

Descri��o:
      Classe de acesso ao banco de dados. Esta classe efetua SELECT, INSERT,
   UPDATE e DELETE. Ela "extende" a classe cmDbBeanRO, portanto possui tudo
   o que aquela classe possui (pagina��o, toolbar, etc).
      Esta classe cria um ResultSet READ-ONLY. A medida que as opera��es
   v�o sendo realizadas pelo usu�rio, esta classe armazena as altera��es
   ocorridas em um array de "alterados", armazena as exclus�es em um
   array de "deletados" e armazena as inser��es em um array de "inseridos".
      Os arrays devem ser limpos sempre que o ResultSet � recriado, pois
   eles servem apenas para fazer o "track" das opera��es realizadas ap�s
   a montagem inicial do ResultSet.
      A cada opera��o de navega��o pelos registros em mem�ria, esta classe
   deve tratar as inser��es/altera��es/exclus�es. Ou seja, ao acessar um
   determinado n�mero de registro, ela deve procurar no array de exclu�do
   para saber se o registro foi deletado; assim como deve procurar no array
   de alterados para saber se o registro foi alterado e para obter os dados
   p�s-altera��o. Os registros inclu�dos v�o sendo empilhados no in�cio do
   conjunto, eles recebem um n�mero de registro negativo.
      Esse esquema complexo que envolve os arrays � devido ao seguinte fato:
   quando eu crio um ResultSet que possua cl�usula "ORDER BY", este ResultSet
   vira automaticamente para READ-ONLY, por isso eu n�o posso fazer NENHUMA
   opera��o de escrita direto neste ResultSet (se pudesse inserir, atualizar
   ou deletar direto no ResultSet com ORDER BY seria extremamente mais
   f�cil !!!).
-----------------------------------------------------------------

   Nota sobre PaginaAnterior e PaginaSubmit:
   ----------------------------------------
         Essas vari�veis tem o objetivo de controlar o REFRESH da p�gina.
         O modo de trabalhar com os
      registros em mem�ria � legal enquanto o usu�rio est� atualizando
      muitos registros, pois assim eu n�o preciso ficar lendo do banco
      toda hora. Por�m esse modo de trabalho n�o vai mostrar as altera��es
      feitas por outros usu�rios que porventura estejam alterando os mesmos
      registros.
         Se o usu�rio clicar no bot�o de "Refresh" do browser ou caso ele
      venha atrav�s de um link em outra p�gina, o ResultSet deve
      obrigatoriamente ser relido do banco de dados. Para tratar isso,
      recomendo que esta classe verifique se a p�gina anteriormente acessada
      � a p�gina de "submit" relativa a p�gina de "browse". Em caso afirmativo,
      n�o l� do banco, caso contr�rio deve ler do banco. Por exemplo:
         Na p�gina rbs_Emp_Browse.jsp � chamado o m�todo .seleciona() que
      dever� verificar se a p�gina anterior era rbs_Emp_SubmitEdit.jsp ou
      rbs_Emp_Edit.jsp. Nesses 2 casos, n�o far� a leitura. Caso contr�rio,
      dever� refazer a leitura.

   Nota sobre Tratamento de altera��es feitas por outros usu�rios:
   --------------------------------------------------------------
         Antes de excluir ou alterar um registro, esta classe l�
      novamente o registro do banco de dados, acessando pelo ROWID e fazendo
      o LOCK do registro. Caso o registro tenha sido alterado ou exclu�do por outro
      usu�rio, � enviada uma mensagem de erro antes de fazer o UPDATE ou o DELETE.
         O controle de altera��o � realizado em cima de um campo de data/hora que deve
      existir nas tabelas e seu nome � registrado atrav�s da vari�vel ColunaAlteracao.
         Para ler o registro fazendo LOCK, usar o comando como exemplo:
      "SELECT EMP_DT_ULT_ALT FROM EMP WHERE ROWID = :1 FOR UPDATE"

-----------------------------------------------------------------
Altera��es:
   Data: Janeiro de 2001
   Autor: Alexandre
   Empresa: Triscal
   Altera��o: M�todos e p�ginas de Pesquisa
-----------------------------------------------------------------


************ PEND�NCIAS: *************
   1) Apagar (comentar) os System.out.print e verificar se existe alguma interroga��o (?).

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
import java.sql.Types.*;

public class cmDbBeanRW
extends cmDbBeanRO implements HttpSessionBindingListener {
// implements JspScopeListener {
   String sqlUp           = "";
   String sqlUpAnterior   = "";
   String sqlDelLogica   = "";
   String sqlDelLogicaAnterior = "";
   String sqlDel          = "";
   String sqlDelAnterior  = "";
   String sqlIns          = "";
   String sqlInsAnterior  = "";
   String sqlLock         = "";
   String sqlLockAnterior = "";
   String sqlLockExecuta  = "";

   ResultSet rsLock           = null;
   PreparedStatement pstmLock = null;
   PreparedStatement pstmUp   = null;
   PreparedStatement pstmDelLog = null;
   PreparedStatement pstmDel  = null;
   CallableStatement pstmIns  = null;

   //update
//   Vector vet_posparametrosUp = new Vector();
   Vector vet_tipoparametrosUp  = new Vector();
   Vector vet_valorparametrosUp = new Vector();
   Vector vet_nomeparametrosUp  = new Vector();
   Vector vet_calctipoparametrosUp  = new Vector();
   Vector vet_calcvalorparametrosUp = new Vector();
   Vector vet_calcnomeparametrosUp  = new Vector();
   //exclusao logica
//   Vector vet_posparametrosDelLog = new Vector();
   Vector vet_tipoparametrosDelLog  = new Vector();
   Vector vet_valorparametrosDelLog = new Vector();
   Vector vet_nomeparametrosDelLog  = new Vector();
   Vector vet_calctipoparametrosDelLog  = new Vector();
   Vector vet_calcvalorparametrosDelLog = new Vector();
   Vector vet_calcnomeparametrosDelLog  = new Vector();
   //insert
//   Vector vet_posparametrosIns = new Vector();
   Vector vet_tipoparametrosIns  = new Vector();
   Vector vet_valorparametrosIns = new Vector();
   Vector vet_nomeparametrosIns  = new Vector();
   Vector vet_calctipoparametrosIns  = new Vector();
   Vector vet_calcvalorparametrosIns = new Vector();
   Vector vet_calcnomeparametrosIns  = new Vector();

   Vector vet_tipoparametrosInsBatch  = new Vector();
   Vector vet_nomeparametrosInsBatch  = new Vector();
   Vector vet_calctipoparametrosInsBatch  = new Vector();
   Vector vet_calcnomeparametrosInsBatch  = new Vector();

   Vector vet_tipoparametrosUpBatch  = new Vector();
   Vector vet_nomeparametrosUpBatch  = new Vector();
   Vector vet_calctipoparametrosUpBatch  = new Vector();
   Vector vet_calcnomeparametrosUpBatch  = new Vector();

   Hashtable ht_deletados   = new Hashtable(); //ht_deletados.put(num_reg, value);
   Hashtable ht_atualizados = new Hashtable(); //ht_atualizados.put(num_reg + nome_coluna, value);
   Hashtable ht_inseridos   = new Hashtable(); //ht_atualizados.put(num_reg + nome_coluna, value);

   Hashtable ht_deletados_backup   = new Hashtable();
   Hashtable ht_atualizados_backup = new Hashtable();
   Hashtable ht_inseridos_backup   = new Hashtable();

   String ColunaAlteracao = "";

   // Variaveis para controle dos metodos de
   //  showCampoInputCombo e showCampoInputComboOption
   Vector comboOptionValue = new Vector();
   Vector comboOptionDesc = new Vector();

   boolean ExclusaoLogica = false;
   boolean ExclusaoLogicaMantem = false;

  public String getVersao()
  {   return("25/02/2002");
  }
   public boolean getExclusaoLogica() {
      return ExclusaoLogica;
   }
   public void setExclusaoLogica(boolean value) {
      ExclusaoLogica = value;
      ExclusaoLogicaMantem = false;
   }
   public void setExclusaoLogica(boolean value, boolean mantem) {
      ExclusaoLogica = value;
      ExclusaoLogicaMantem = mantem;
   }

   public void seticonexaobean(conexaoBean value) {
      super.seticonexaobean(value);

      //update
//      vet_posparametrosUp.removeAllElements();
      vet_tipoparametrosUp.removeAllElements();
      vet_valorparametrosUp.removeAllElements();
      vet_nomeparametrosUp.removeAllElements();
      vet_calctipoparametrosUp.removeAllElements();
      vet_calcvalorparametrosUp.removeAllElements();
      vet_calcnomeparametrosUp.removeAllElements();
      //exclusao logica
//      vet_posparametrosDelLog.removeAllElements();
      vet_tipoparametrosDelLog.removeAllElements();
      vet_valorparametrosDelLog.removeAllElements();
      vet_nomeparametrosDelLog.removeAllElements();
      vet_calctipoparametrosDelLog.removeAllElements();
      vet_calcvalorparametrosDelLog.removeAllElements();
      vet_calcnomeparametrosDelLog.removeAllElements();
      //insert
//      vet_posparametrosIns.removeAllElements();
      vet_tipoparametrosIns.removeAllElements();
      vet_valorparametrosIns.removeAllElements();
      vet_nomeparametrosIns.removeAllElements();
      vet_calctipoparametrosIns.removeAllElements();
      vet_calcvalorparametrosIns.removeAllElements();
      vet_calcnomeparametrosIns.removeAllElements();
   }

   public void setsqlLock(String value) {
      sqlLock = value;
   }
   public String getsqlLock() {
      return sqlLock;
   }
   public String getsqlUp() {
      return sqlUp;
   }
   public void setsqlUp(String value) {
      sqlUp = value;
//      vet_posparametrosUp.removeAllElements();
      vet_tipoparametrosUp.removeAllElements();
      vet_valorparametrosUp.removeAllElements();
      vet_nomeparametrosUp.removeAllElements();
      vet_calctipoparametrosUp.removeAllElements();
      vet_calcvalorparametrosUp.removeAllElements();
      vet_calcnomeparametrosUp.removeAllElements();
   }
   public String getsqlDellogica() {
      return sqlDelLogica;
   }
   public void setsqlDelLogica(String value) {
      sqlDelLogica = value;
//      vet_posparametrosDelLog.removeAllElements();
      vet_tipoparametrosDelLog.removeAllElements();
      vet_valorparametrosDelLog.removeAllElements();
      vet_nomeparametrosDelLog.removeAllElements();
      vet_calctipoparametrosDelLog.removeAllElements();
      vet_calcvalorparametrosDelLog.removeAllElements();
      vet_calcnomeparametrosDelLog.removeAllElements();
   }
   public String getsqlDel() {
      return sqlDel;
   }
   public void setsqlDel(String value) {
      sqlDel = value;
   }
   public String getsqlIns() {
      return sqlIns;
   }
   public void setsqlIns(String value) {
      sqlIns = value;
//      vet_posparametrosIns.removeAllElements();
      vet_tipoparametrosIns.removeAllElements();
      vet_valorparametrosIns.removeAllElements();
      vet_nomeparametrosIns.removeAllElements();
      vet_calctipoparametrosIns.removeAllElements();
      vet_calcvalorparametrosIns.removeAllElements();
      vet_calcnomeparametrosIns.removeAllElements();
   }
   public void removeParametrosUp() {
      vet_tipoparametrosUp.removeAllElements();
      vet_valorparametrosUp.removeAllElements();
      vet_nomeparametrosUp.removeAllElements();
      vet_calctipoparametrosUp.removeAllElements();
      vet_calcvalorparametrosUp.removeAllElements();
      vet_calcnomeparametrosUp.removeAllElements();
   }
   public void removeParametrosDelLog() {
      vet_tipoparametrosDelLog.removeAllElements();
      vet_valorparametrosDelLog.removeAllElements();
      vet_nomeparametrosDelLog.removeAllElements();
      vet_calctipoparametrosDelLog.removeAllElements();
      vet_calcvalorparametrosDelLog.removeAllElements();
      vet_calcnomeparametrosDelLog.removeAllElements();
   }
   public void removeParametrosIns() {
      vet_tipoparametrosIns.removeAllElements();
      vet_valorparametrosIns.removeAllElements();
      vet_nomeparametrosIns.removeAllElements();
      vet_calctipoparametrosIns.removeAllElements();
      vet_calcvalorparametrosIns.removeAllElements();
      vet_calcnomeparametrosIns.removeAllElements();
   }

   public void setColunaAlteracao(String value) {
      ColunaAlteracao = value;
   }


   /*********************** NAVEGACAO NO RESULT SET ************************/
   public void PreparaInserir() {
      regAtual = getPosicaoInserir();
   }
   public void beforeFirst() {
      regAtual = getPosicaoInserir();
      if (regAtual == -1)
         regAtual = 0;
   }
   public boolean next()
          throws SQLException, java.io.IOException, Exception
   {
      regAtual++;
      if(regAtual == 0)
      {
         // regAtual++;
         if (ht_inseridos.size() > 0)
         {  if(RangeSize == 1)
               regAtual++;
            else
               return true;
         }
         else
            regAtual++;
      }

      if (verificaDeletado())
      {
        return next();
      }
      else
      {
        if (regAtual < 0)
        {  // verificar e pegar o registro no array de inseridos
           if (ht_inseridos.containsKey(regAtual + "NUMERO REGISTRO INSERIDO"))
              return true;
           else
              return false;
        }
        else
        {
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
      }
   }
/***public Object getColuna(int coluna)
          throws SQLException, java.io.IOException, Exception
   {
      if( regAtual == 0)
         return null;

      String nomecoluna = "";
      if(verificaDeletado())
         return null;

      nomecoluna = rs.getMetaData().getColumnName(coluna);
      if (ht_atualizados.containsKey(regAtual + nomecoluna))
      {
          return ht_atualizados.get(regAtual + nomecoluna);
      }
      else
      {
         if(regAtual <= 0)
         {
            //registro inserido, procurar no array de inseridos
           if (! ht_inseridos.containsKey(regAtual + "NUMERO REGISTRO INSERIDO"))
              return null;
           return ht_inseridos.get(regAtual + nomecoluna);
         }
         else
         {
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
      }
   }
***/
   public Object getColuna(String nomecoluna)
          throws SQLException, java.io.IOException, Exception
   {
      return internalGetColuna(nomecoluna, false);
   }
   public Object getColuna(String nomecoluna, boolean pegaExcluidosNaoSalvos)
          throws SQLException, java.io.IOException, Exception
   {
      return internalGetColuna(nomecoluna, pegaExcluidosNaoSalvos);
   }
   public Object internalGetColuna(String nomecoluna, boolean pegaExcluidosNaoSalvos)
          throws SQLException, java.io.IOException, Exception
   {  int tipo = 0;
      String classname="";
      int tam = 0;
      String typename = "";
      int prec = 0;
      int scale = 0;

      Object col = null;
      if( regAtual == 0)
      {  if(nomecoluna.equals("CMREADONLY")) return "";
         else return null;
      }

      if(verificaDeletado(pegaExcluidosNaoSalvos))
      {  if(nomecoluna.equals("CMREADONLY")) return "";
         else return null;
      }

      if (ht_atualizados.containsKey(regAtual + nomecoluna))
      {  return ht_atualizados.get(regAtual + nomecoluna);
      }
      else
      {  if(regAtual <= 0)
         {  //registro inserido, procurar no array de inseridos
            if (! ht_inseridos.containsKey(regAtual + "NUMERO REGISTRO INSERIDO"))
            {  if(nomecoluna.equals("CMREADONLY")) return "";
               else return null;
            }
            if(ht_inseridos.get(regAtual + nomecoluna) == null)
               return "";
            else
               return ht_inseridos.get(regAtual + nomecoluna);
         }
         else
         {
            if(rs == null)
            {  if(nomecoluna.equals("CMREADONLY")) return "";
               else return null;
            }
            if(is_rsClosed)
            {  if(nomecoluna.equals("CMREADONLY")) return "";
               else return null;
            }
            rs.absolute(regAtual);
            if(rs.isAfterLast() || rs.isBeforeFirst())
            {
               if(nomecoluna.equals("CMREADONLY")) return "";
               else return null;
            }
            else
            {
               if(rs.getRow() == 0)
               {
                  if(nomecoluna.equals("CMREADONLY")) return "";
                  else return null;
               }
               else
               {
                  boolean achou = false;
                  if(nomecoluna.equals("CMREADONLY"))
                  {  try {
                        for(int i=1; i <= rs.getMetaData().getColumnCount(); i++)
                        {  if(rs.getMetaData().getColumnName(i).equalsIgnoreCase("CMREADONLY"))
                           {  achou = true;
                              i = 999;
                           }
                        }
                     }
                     catch (Exception errometadata) {
                     }
                  }
                  else {
                     achou = true;
                  }
                  if(! achou) return "";
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
/*
Types.ARRAY=2003
Types.BFILE=-13
Types.BIGINT=-5
Types.BINARY=-2
Types.BIT=-7
Types.BLOB=2004
Types.CHAR=1
Types.CLOB=2005
Types.CURSOR=-10
Types.DATE=91
Types.DECIMAL=3
Types.DOUBLE=8
Types.FIXED_CHAR=999
Types.FLOAT=6
Types.INTEGER=4
Types.LONGVARBINARY=-4
Types.LONGVARCHAR=-1
Types.NULL=0
Types.NUMBER=2
Types.NUMERIC=2
Types.OTHER=1111
Types.RAW=-2
Types.REAL=7
Types.REF=2006
Types.ROWID=-8
Types.SMALLINT=5
Types.STRUCT=2002
Types.TIME=92
Types.TIMESTAMP=93
Types.TINYINT=-6
Types.VARBINARY=-3
Types.VARCHAR=12
-----------------------------
Types.BIGINT=-5
Types.BINARY=-2
Types.BIT=-7
Types.CHAR=1
Types.DATE=91
Types.DECIMAL=3
Types.DOUBLE=8
Types.FLOAT=6
Types.INTEGER=4
Types.LONGVARBINARY=-4
Types.LONGVARCHAR=-1
Types.REAL=7
Types.SMALLINT=5
Types.TIME=92
Types.TIMESTAMP=93
Types.TINYINT=-6
Types.VARBINARY=-3
Types.VARCHAR=12
*/
                     switch (tipo)
                     {
                        case Types.NULL: {
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
                          Exception e = new Exception("Tipo de Dados n�o encontrado!");
                          throw e;
                        }
                     }
                     //Object col = rs.getObject(nomecoluna);
                     //col = rs.getString(nomecoluna);
                     if (rs.wasNull())
                     {
                        if(nomecoluna.equals("CMREADONLY")) return "";
                        else return null;
                     }
                     else
                     {
                         switch (tipo)
                         {
                            case Types.NULL: {
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
                               //col = ""+rs.getDate(nomecoluna);
                               //col = rs.getDate(nomecoluna).timestampValue();
                                col = rs.getDate(nomecoluna);
                               break;
                            }
                            default: {
                              // mensagem de erro
                              Exception e = new Exception("Tipo de Dados n�o encontrado!");
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
      }
   }
   public boolean verificaDeletado(boolean pegaExcluidosNaoSalvos) {
      boolean retorno = false;
      if(pegaExcluidosNaoSalvos)
      {  if(ht_deletados.containsKey(regAtual + ""))
           if( ht_deletados.get(regAtual + "SALVOU BANCO") == null)
              retorno = true;
           else
              if( ht_deletados.get(regAtual + "SALVOU BANCO").toString().equals("S"))
                 retorno = true;
      }
      else
      {   retorno = ht_deletados.containsKey(regAtual + "");
      }
      return retorno;
   }
   public boolean verificaDeletado() {
      boolean retorno = false;
      retorno = ht_deletados.containsKey(regAtual + "");
      return retorno;
   }

   int getPosicaoInserir() {
      int posinserir = 0;
      int posinseriraux = 0;
      String chave = "";
      for(Enumeration el = ht_inseridos.keys(); el.hasMoreElements();)
      {
         chave = "" + el.nextElement();
         if(chave.indexOf("NUMERO REGISTRO INSERIDO") != -1)
         {
            posinseriraux = Integer.parseInt( chave.substring(0, chave.indexOf("NUMERO REGISTRO INSERIDO")) );
            if(posinseriraux < posinserir)
               posinserir = posinseriraux;
         }
      }
      for(Enumeration el = ht_deletados.keys(); el.hasMoreElements();)
      {  chave = "" + el.nextElement();
         if(chave.indexOf("NUMERO REGISTRO DELETADO") != -1)
         {  posinseriraux = Integer.parseInt( chave.substring(0, chave.indexOf("NUMERO REGISTRO DELETADO")) );
            if(posinseriraux < posinserir)
               posinserir = posinseriraux;
         }
      }
      return (posinserir - 1);
   }
   /*********************** NAVEGACAO NO RESULT SET ************************/

//   public void addParametroUp(String posparametro, String tipo, Object valor) {
   public void addParametroUp(String tipo, Object valor, String nome) {
//      vet_posparametrosUp.addElement(posparametro);
      vet_tipoparametrosUp.addElement(tipo);
      vet_nomeparametrosUp.addElement(nome);
       switch (Integer.parseInt(tipo))
       {
          case Types.DECIMAL:
          case Types.NUMERIC:
          case Types.DOUBLE:
          case Types.REAL: {
            if((valor != null) &&
               (! valor.toString().trim().equals("")))
            {  String aux = UtilBean.desformataNumero(valor.toString().trim());
               vet_valorparametrosUp.addElement(aux);
            }
            else
               vet_valorparametrosUp.addElement(valor);
            break;
          }
          case Types.FLOAT: {
            if((valor != null) &&
               (! valor.toString().trim().equals("")))
               vet_valorparametrosUp.addElement(UtilBean.desformataNumero(valor.toString().trim()));
            else
               vet_valorparametrosUp.addElement(valor);
            break;
          }
          default: {
             vet_valorparametrosUp.addElement(valor);
          }
       } //fim switch
   }
   public void addParametroCalculadoUp(String tipo, Object valor, String nome) {
      vet_calctipoparametrosUp.addElement(tipo);
      vet_calcvalorparametrosUp.addElement(valor);
      vet_calcnomeparametrosUp.addElement(nome);
   }
   public void addParametroDelLog(String tipo, Object valor, String nome) {
      vet_tipoparametrosDelLog.addElement(tipo);
      vet_valorparametrosDelLog.addElement(valor);
      vet_nomeparametrosDelLog.addElement(nome);
   }
   public void addParametroCalculadoDelLog(String tipo, Object valor, String nome) {
      vet_calctipoparametrosDelLog.addElement(tipo);
      vet_calcvalorparametrosDelLog.addElement(valor);
      vet_calcnomeparametrosDelLog.addElement(nome);
   }
//   public void addParametroIns(String posparametro, String tipo, Object valor) {
   public void addParametroIns(String tipo, Object valor, String nome) {
//      vet_posparametrosIns.addElement(posparametro);
      vet_tipoparametrosIns.addElement(tipo);
      vet_nomeparametrosIns.addElement(nome);

       switch (Integer.parseInt(tipo))
       {
          case Types.DECIMAL:
          case Types.NUMERIC:
          case Types.DOUBLE:
          case Types.REAL: {
            if((valor != null) &&
               (! valor.toString().trim().equals("")))
            {  String aux = UtilBean.desformataNumero(valor.toString().trim());
               vet_valorparametrosIns.addElement(aux);
            }
            else
               vet_valorparametrosIns.addElement(valor);
            break;
          }
          case Types.FLOAT: {
            if((valor != null) &&
               (! valor.toString().trim().equals("")))
               vet_valorparametrosIns.addElement(UtilBean.desformataNumero(valor.toString().trim()));
            else
               vet_valorparametrosIns.addElement(valor);
            break;
          }
          default: {
             vet_valorparametrosIns.addElement(valor);
          }
       } //fim switch
   }

   public void addParametroCalculadoIns(String tipo, Object valor, String nome) {
      vet_calctipoparametrosIns.addElement(tipo);
      vet_calcvalorparametrosIns.addElement(valor);
      vet_calcnomeparametrosIns.addElement(nome);
   }

  public void limpaHashtablesAtualizacao() {
            ht_atualizados.clear();
            ht_deletados.clear();
            ht_inseridos.clear();
  }

  public void liberaRecursosLock() {
     try {
        sqlLockAnterior = "";
        if(rsLock != null)
           rsLock.close();
        if(pstmLock != null)
           pstmLock.close();
     }
     catch (Exception ignored) {
     }
  }
  public void liberaRecursosUp() {
     try {
        sqlUpAnterior = "";
        if(pstmUp != null)
           pstmUp.close();
     }
     catch (Exception ignored) {
     }
  }
  public void liberaRecursosDelLog() {
     try {
        sqlDelLogicaAnterior = "";
        if(pstmDelLog != null)
           pstmDelLog.close();
     }
     catch (Exception ignored) {
     }
  }
  public void liberaRecursosDel() {
     try {
        sqlDelAnterior = "";
        if(pstmDel != null)
           pstmDel.close();
     }
     catch (Exception ignored) {
     }
  }
  public void liberaRecursosIns() {
     try {
        sqlInsAnterior = "";
        if(pstmIns != null)
           pstmIns.close();
     }
     catch (Exception ignored) {
     }
  }


   /************************ Metodos do LISTENER da session **********************/
   public synchronized void valueUnbound (HttpSessionBindingEvent event) {
//System.out.print("cmDbbeanRW: VALUEUNBOUND!!!");
java.util.Date data = new java.util.Date();
//System.out.print("cmdbbeanRW: VALUEUNBOUND:" + data + " " + data.getTime() + "." );
     try {
        if(rs != null)
        {  rs.close();
           is_rsClosed = true;
        }
        if(rsLock != null) rsLock.close();
        if(pstmLock != null) pstmLock.close();
        if(pstmUp != null) pstmUp.close();
        if(pstmDelLog != null) pstmDelLog.close();
        if(pstmIns != null) pstmIns.close();
        if(pstmDel != null) pstmDel.close();
        if(curbean != null) curbean.close();
     }
     catch (Exception ignored) {
        System.out.print("cmdbbeanRW: VALUEUNBOUND ERROR:" + ignored + "." );
     }
   }
   /************************ Metodos do LISTENER da session **********************/

   /*INI**************** INSERIR, ATUALIZAR E DELETAR SO DOS ARRAYS *****************/
   public boolean inserir_so_array(HttpServletRequest  request,
                          HttpServletResponse response,
                          HttpSession         session)
          throws java.io.IOException, Exception
   {
       try {
         regAtual = getPosicaoInserir();
         ht_inseridos.put(regAtual + "NUMERO REGISTRO INSERIDO",
                          regAtual + "");
         ht_inseridos.put(regAtual + "SALVOU BANCO",
                          regAtual + "N");
         ht_inseridos.put(regAtual + "CMROWID",
                          "0");

         vet_tipoparametrosInsBatch.removeAllElements();
         vet_nomeparametrosInsBatch.removeAllElements();
         for(int i=0; i < vet_valorparametrosIns.size(); i++)
         {  ht_inseridos.put(regAtual + (String)vet_nomeparametrosIns.elementAt(i),
                             vet_valorparametrosIns.elementAt(i));
            vet_tipoparametrosInsBatch.addElement(vet_tipoparametrosIns.elementAt(i));
            vet_nomeparametrosInsBatch.addElement(vet_nomeparametrosIns.elementAt(i));
         }

         vet_calctipoparametrosInsBatch.removeAllElements();
         vet_calcnomeparametrosInsBatch.removeAllElements();
         for(int i=0; i < vet_calcvalorparametrosIns.size(); i++)
         {  ht_inseridos.put(regAtual + (String)vet_calcnomeparametrosIns.elementAt(i),
                             vet_calcvalorparametrosIns.elementAt(i));
            vet_calctipoparametrosInsBatch.addElement(vet_calctipoparametrosIns.elementAt(i));
            vet_calcnomeparametrosInsBatch.addElement(vet_calcnomeparametrosIns.elementAt(i));
         }
         vet_tipoparametrosIns.removeAllElements();
         vet_valorparametrosIns.removeAllElements();
         vet_nomeparametrosIns.removeAllElements();
         vet_calctipoparametrosIns.removeAllElements();
         vet_calcvalorparametrosIns.removeAllElements();
         vet_calcnomeparametrosIns.removeAllElements();
         return true;
       }
       catch (Exception ex) {
         throw ex;
         //return false;
       }
   }

   public boolean deletar_so_array(HttpServletRequest  request,
                          HttpServletResponse response,
                          HttpSession         session,
                          String request_rowid)
          throws java.io.IOException, Exception
   {
       if(ExclusaoLogica)
       {  throw new Exception("Exclusao Logica so' nos arrays ainda nao implementada.");
          //return atualizarExcluindo_so_arrays(request, response, session, request_rowid);
       }

       try {
          //verificando se o ROWID � v�lido:
          if ((request_rowid != null) &&
              (! request_rowid.equals("")))
          {  if(! request_rowid.equalsIgnoreCase("" + getColuna("CMROWID")))
             {  response.sendRedirect(request.getSession().getAttribute("COM_RN") + "jsp/com_referererror.jsp?e=nav");
                return false;
             }
          }
          numPages = -1; //1708
          ht_deletados.put(regAtual + "", "S");
          ht_deletados.put(regAtual + "NUMERO REGISTRO DELETADO", regAtual + "");
          ht_deletados.put(regAtual + "SALVOU BANCO", regAtual + "N");
          //ht_deletados.put(regAtual + "CMROWID", getColuna("CMROWID"));
          return true;
       }
       catch (Exception ex) {
         throw ex;
         //return false;
       }
   }

   public boolean atualizar_so_array(HttpServletRequest  request,
                            HttpServletResponse response,
                            HttpSession         session,
                            String request_rowid)
          throws java.io.IOException, Exception
   {
       try {

          //verificando se o ROWID � v�lido:
          if ((request_rowid != null) &&
              (! request_rowid.equals("")))
          {  if(! request_rowid.equalsIgnoreCase("" + getColuna("CMROWID")))
             {  response.sendRedirect(request.getSession().getAttribute("COM_RN") + "jsp/com_referererror.jsp?e=nav");
                return false;
             }
          }

         ht_atualizados.put(regAtual + "SALVOU BANCO", regAtual + "N");
         ht_atualizados.put(regAtual + "NUMERO REGISTRO ALTERADO", regAtual + "");

         vet_tipoparametrosUpBatch.removeAllElements();
         vet_nomeparametrosUpBatch.removeAllElements();
         for(int i=0; i < vet_valorparametrosUp.size(); i++)
         {  ht_atualizados.put(regAtual + (String)vet_nomeparametrosUp.elementAt(i),
                               vet_valorparametrosUp.elementAt(i));
            vet_tipoparametrosUpBatch.addElement(vet_tipoparametrosUp.elementAt(i));
            vet_nomeparametrosUpBatch.addElement(vet_nomeparametrosUp.elementAt(i));
         }
         vet_calctipoparametrosUpBatch.removeAllElements();
         vet_calcnomeparametrosUpBatch.removeAllElements();
         for(int i=0; i < vet_calcvalorparametrosUp.size(); i++)
         {  ht_atualizados.put(regAtual + (String)vet_calcnomeparametrosUp.elementAt(i),
                               vet_calcvalorparametrosUp.elementAt(i));
            vet_calctipoparametrosUpBatch.addElement(vet_calctipoparametrosUp.elementAt(i));
            vet_calcnomeparametrosUpBatch.addElement(vet_calcnomeparametrosUp.elementAt(i));
         }

         vet_tipoparametrosUp.removeAllElements();
         vet_valorparametrosUp.removeAllElements();
         vet_nomeparametrosUp.removeAllElements();
         vet_calctipoparametrosUp.removeAllElements();
         vet_calcvalorparametrosUp.removeAllElements();
         vet_calcnomeparametrosUp.removeAllElements();
         return true;
       }
       catch (Exception ex) {
         throw ex;
         //return false;
       }
   }
   /*FIM**************** INSERIR, ATUALIZAR E DELETAR SO DOS ARRAYS *****************/

   /**************************  UPDATE **************************/
   public boolean atualizar(HttpServletRequest  request,
                            HttpServletResponse response,
                            HttpSession         session,
                            String request_rowid)
          throws SQLException, java.io.IOException, Exception
   {
       UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRW --- atualizar - 001 - " + request_rowid, session, request, true);
       
       boolean bRealizouComando = false;
       boolean bDeveExecutarQuery = true;
       if(! sqlUp.equalsIgnoreCase(sqlUpAnterior))
       {  bDeveExecutarQuery = true;
          liberaRecursosUp();
          liberaRecursosLock();
       }
       else
       {  bDeveExecutarQuery = false;
       }

       try {

           UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRW --- atualizar - 002 - " + request_rowid, session, request, true);
          //verificando se o ROWID � v�lido:
          if ((request_rowid != null) &&
              (! request_rowid.equals("")))
          {  if(! request_rowid.equalsIgnoreCase("" + getColuna("CMROWID")))
             {  response.sendRedirect(request.getSession().getAttribute("COM_RN") + "jsp/com_referererror.jsp?e=nav");
                return false;
             }
          }

           UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRW --- atualizar - 003 - " + request_rowid, session, request, true);
         /* Verifica se o registro foi alterado por outro usu�rio --- INICIO */
         if((! sqlLock.equals("")) && (! ColunaAlteracao.equals("")))
         {  if(! sqlLock.equalsIgnoreCase(sqlLockAnterior))
            {  liberaRecursosLock();
               sqlLockExecuta = sqlLock + " WHERE ROWID = :1 FOR UPDATE";
               pstmLock = (PreparedStatement)iconexaobean.getConnection().prepareStatement(sqlLockExecuta,
                                                       rs.TYPE_FORWARD_ONLY,
                                                       rs.CONCUR_READ_ONLY);
               // pstmLock = (PreparedStatement)iconexaobean.getConnection().prepareStatement(sqlLock);
               sqlLockAnterior = sqlLock;
            }
            pstmLock.setRowId(1, (RowId)getColuna("CMROWID"));
            rsLock = (ResultSet)pstmLock.executeQuery();
            if( ! rsLock.next() )
            {   iconexaobean.rollback();
                Exception eLock = new Exception("Um ou mais Registros foram Exclu�dos por outro Usu�rio. Realize nova consulta para obter os dados mais atualizados.");
                throw eLock;
            }
//System.out.println("getColuna=" + getColuna(ColunaAlteracao));
//System.out.println("getObject=" + rsLock.getObject(ColunaAlteracao));

       UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRW --- atualizar - 004 - " + request_rowid, session, request, true);
            boolean bValido = true; //for(int i=1; i <= rs.getMetaData().getColumnCount(); i++)
            String auxColunaAlteracao = "";
            String auxLockAlteracao = "";
            if(getColuna(ColunaAlteracao) != null )
               auxColunaAlteracao = "" + getColuna(ColunaAlteracao);
            //if(rsLock.getObject(ColunaAlteracao) != null )
            //   auxLockAlteracao = "" + rsLock.getObject(ColunaAlteracao);
            if(rsLock.getString(ColunaAlteracao) != null )
               auxLockAlteracao = "" + rsLock.getString(ColunaAlteracao);

            if (auxColunaAlteracao.length() >= 18 && auxLockAlteracao.length() >= 18 ) {
              if (! auxColunaAlteracao.substring(1, 18).equals(auxLockAlteracao.substring(1, 18)) ) {
                 bValido = false;
                 UtilBean.geraDebugLog("<BR>cmdbbeanrw - auxColunaAlteracao=" + auxColunaAlteracao + ",auxLockAlteracao=" + auxLockAlteracao + ".", null, session, request, response, null); 
              }   
            }   
            if (! bValido)
            {   iconexaobean.rollback();
                Exception eLock = new Exception("Um ou mais Registros foram Alterados por outro Usu�rio. Realize nova consulta para obter os dados mais atualizados.");
                throw eLock;
            }
         }
         /* Verifica se o registro foi alterado por outro usu�rio --- FIM -- */
//System.out.println("sqlUp="+sqlUp);
       UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRW --- atualizar - 005 - " + request_rowid, session, request, true);
           UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRW --- atualizar - 005 - sqlUp=" + sqlUp, session, request, true);
         if(bDeveExecutarQuery)
         {  if(! sqlUp.equals(""))
            {  // Create a Prepared Statement
               if(sqlUp.startsWith("BEGIN"))
               {  pstmUp = (PreparedStatement)iconexaobean.getConnection().prepareStatement(sqlUp);
               }
               else if(sqlUp.indexOf("WHERE  ") >= 0) //atencao para os 2 espacos
               {  pstmUp = (PreparedStatement)iconexaobean.getConnection().prepareStatement(sqlUp);
               }
               else
               {  sqlUp += " WHERE ROWID = :" + (vet_tipoparametrosUp.size()+1);
                  pstmUp = (PreparedStatement)iconexaobean.getConnection().prepareStatement(sqlUp);
               }
            }
//System.out.print("UPDATE=" + sqlUp);
         }
           UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRW --- atualizar - 005,5 - " + request_rowid, session, request, true);
         sqlUpAnterior = sqlUp;
         setaParametros(vet_tipoparametrosUp, vet_valorparametrosUp, pstmUp, "UPD", request, session);

           UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRW --- atualizar - 006 - " + request_rowid, session, request, true);
         if((! sqlUp.equals(""))
            && (! sqlUp.startsWith("BEGIN"))
            && (sqlUp.indexOf("WHERE  ") < 0))
         {  // pstmUp.setRowId((vet_tipoparametrosUp.size()+1),rs.getRowId("CMROWID"));
            pstmUp.setRowId((vet_tipoparametrosUp.size()+1),(RowId)getColuna("CMROWID"));
         }

         bRealizouComando = true;
         if(! sqlUp.equals(""))
         {         UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRW --- atualizar - 007 - " + request_rowid, session, request, true);

              pstmUp.executeUpdate();
         }
           UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRW --- atualizar - 008 - " + request_rowid, session, request, true);
         //rs.refreshRow();
         ht_atualizados.put(regAtual + "NUMERO REGISTRO ALTERADO", regAtual + "");
         ht_atualizados.put(regAtual + "SALVOU BANCO", regAtual + "S");
         for(int i=0; i < vet_valorparametrosUp.size(); i++)
         {
            ht_atualizados.put(regAtual + (String)vet_nomeparametrosUp.elementAt(i),
                               vet_valorparametrosUp.elementAt(i));
         }
         for(int i=0; i < vet_calcvalorparametrosUp.size(); i++)
         {
            ht_atualizados.put(regAtual + (String)vet_calcnomeparametrosUp.elementAt(i),
                               vet_calcvalorparametrosUp.elementAt(i));
         }

//for(Enumeration el = ht_atualizados.elements(); el.hasMoreElements();)
//   System.out.print( "ELEMENTO=" + el.nextElement() );
       UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRW --- atualizar - 009 - " + request_rowid, session, request, true);

         vet_tipoparametrosUp.removeAllElements();
         vet_valorparametrosUp.removeAllElements();
         vet_nomeparametrosUp.removeAllElements();
         vet_calctipoparametrosUp.removeAllElements();
         vet_calcvalorparametrosUp.removeAllElements();
         vet_calcnomeparametrosUp.removeAllElements();
           UtilBean.GeraDebugLogSimples("<BR>CMDBBEANRW --- atualizar - 010 - " + request_rowid, session, request, true);

         return true;
       }
       catch (SQLException e) {
         if (bRealizouComando)
         {  try { iconexaobean.rollback(); }
            catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(1):" + new java.util.Date() + " " + ignored + "." ); }
         }
         throw e;
         //return false;
       }
       catch (Exception ex) {
         if (bRealizouComando)
         {  try { iconexaobean.rollback(); }
            catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(2):" + new java.util.Date() + " " + ignored + "." ); }
         }
         throw ex;
         //return false;
       }
   }

   /**************************  INSERT **************************/
   public boolean inserir(HttpServletRequest  request,
                          HttpServletResponse response,
                          HttpSession         session)
          throws SQLException, java.io.IOException, Exception
   {
       boolean bRealizouComando = false;
       boolean bDeveExecutarQuery = true;
       if(! sqlIns.equalsIgnoreCase(sqlInsAnterior))
       {  bDeveExecutarQuery = true;
          liberaRecursosIns();
       }
       else
       {  bDeveExecutarQuery = false;
       }

       try {
         if(bDeveExecutarQuery)
         {  if(! sqlIns.equals(""))
            {  // Create a Prepared Statement
               pstmIns = (CallableStatement)iconexaobean.getConnection().prepareCall(sqlIns);
            }
            sqlInsAnterior = sqlIns;
//System.out.println("INSERT=" + sqlIns);
         }
         setaParametros(vet_tipoparametrosIns, vet_valorparametrosIns, pstmIns, "INS");
         if(! sqlIns.equals(""))
         {  pstmIns.registerOutParameter((vet_tipoparametrosIns.size() + 1), Types.ROWID);
         }
         //pstmIns.setExecuteBatch(1);
         //pstmIns.sendBatch();
         bRealizouComando = true;
         if(! sqlIns.equals(""))
         {  pstmIns.execute();
         }
         //pstmIns.executeUpdate();
         //rs.refreshRow();

         regAtual = getPosicaoInserir();
         ht_inseridos.put(regAtual + "NUMERO REGISTRO INSERIDO",
                          regAtual + "");
         ht_inseridos.put(regAtual + "CMROWID",
                          pstmIns.getRowId((vet_tipoparametrosIns.size() + 1)));
                          //pstmIns.getString((vet_tipoparametrosIns.size() + 1)));
                          //pstmIns.getObject((vet_tipoparametrosIns.size() + 1)));
         ht_inseridos.put(regAtual + "SALVOU BANCO",
                          regAtual + "S");

         for(int i=0; i < vet_valorparametrosIns.size(); i++)
         {  ht_inseridos.put(regAtual + (String)vet_nomeparametrosIns.elementAt(i),
                             vet_valorparametrosIns.elementAt(i));
         }
         for(int i=0; i < vet_calcvalorparametrosIns.size(); i++)
         {  ht_inseridos.put(regAtual + (String)vet_calcnomeparametrosIns.elementAt(i),
                             vet_calcvalorparametrosIns.elementAt(i));
         }
         vet_tipoparametrosIns.removeAllElements();
         vet_valorparametrosIns.removeAllElements();
         vet_nomeparametrosIns.removeAllElements();
         vet_calctipoparametrosIns.removeAllElements();
         vet_calcvalorparametrosIns.removeAllElements();
         vet_calcnomeparametrosIns.removeAllElements();
         return true;
       }
       catch (SQLException e) {
         if (bRealizouComando)
         {  try { iconexaobean.rollback(); }
            catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(3):" + new java.util.Date() + " " + ignored + "." ); }
         }
         throw e;
         //return false;
       }
       catch (Exception ex) {
         if (bRealizouComando)
         {  try { iconexaobean.rollback(); }
            catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(4):" + new java.util.Date() + " " + ignored + "." ); }
         }
         throw ex;
         //return false;
       }
   }
   public boolean inserir_sem_banco(HttpServletRequest  request,
                                    HttpServletResponse response,
                                    HttpSession         session,
                                    Object              pROWID)
          throws SQLException, java.io.IOException, Exception
   {
       try {
         regAtual = getPosicaoInserir();
         ht_inseridos.put(regAtual + "NUMERO REGISTRO INSERIDO",
                          regAtual + "");
         ht_inseridos.put(regAtual + "CMROWID",
                          (RowId)pROWID);
         ht_inseridos.put(regAtual + "SALVOU BANCO",
                          regAtual + "S");

         for(int i=0; i < vet_valorparametrosIns.size(); i++)
         {  ht_inseridos.put(regAtual + (String)vet_nomeparametrosIns.elementAt(i),
                             vet_valorparametrosIns.elementAt(i));
         }
         for(int i=0; i < vet_calcvalorparametrosIns.size(); i++)
         {  ht_inseridos.put(regAtual + (String)vet_calcnomeparametrosIns.elementAt(i),
                             vet_calcvalorparametrosIns.elementAt(i));
         }
         vet_tipoparametrosIns.removeAllElements();
         vet_valorparametrosIns.removeAllElements();
         vet_nomeparametrosIns.removeAllElements();
         vet_calctipoparametrosIns.removeAllElements();
         vet_calcvalorparametrosIns.removeAllElements();
         vet_calcnomeparametrosIns.removeAllElements();
         return true;
       }
       catch (Exception ex) {
         try { iconexaobean.rollback(); }
         catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(5):" + new java.util.Date() + " " + ignored + "." ); }
         throw ex;
         //return false;
       }
   }

   /************************* DELETE *************************/
   public boolean deletar(HttpServletRequest  request,
                          HttpServletResponse response,
                          HttpSession         session,
                          String request_rowid)
          throws SQLException, java.io.IOException, Exception
   {

       if(ExclusaoLogica)
       {  return atualizarExcluindo(request, response, session, request_rowid);
       }

       boolean bRealizouComando = false;
       boolean bDeveExecutarQuery = true;
       if(! sqlDel.equalsIgnoreCase(sqlDelAnterior))
       {  bDeveExecutarQuery = true;
          liberaRecursosDel();
          liberaRecursosLock();
       }
       else
       {  bDeveExecutarQuery = false;
       }

       try {

          //verificando se o ROWID � v�lido:
          if ((request_rowid != null) &&
              (! request_rowid.equals("")))
          {  if(! request_rowid.equalsIgnoreCase("" + getColuna("CMROWID", true)))
             {  response.sendRedirect(request.getSession().getAttribute("COM_RN") + "jsp/com_referererror.jsp?e=nav");
                return false;
             }
          }

         /* Verifica se o registro foi alterado por outro usu�rio --- INICIO */
         if((! sqlLock.equals("")) && (! ColunaAlteracao.equals("")))
         {  if(! sqlLock.equalsIgnoreCase(sqlLockAnterior))
            {  liberaRecursosLock();
               sqlLockExecuta = sqlLock + " WHERE ROWID = :1 FOR UPDATE";
               pstmLock = (PreparedStatement)iconexaobean.getConnection().prepareStatement(sqlLockExecuta,
                                                       rs.TYPE_FORWARD_ONLY,
                                                       rs.CONCUR_READ_ONLY);
               //pstmLock = (PreparedStatement)iconexaobean.getConnection().prepareStatement(sqlLock);
               sqlLockAnterior = sqlLock;
            }
            pstmLock.setRowId(1, (RowId)getColuna("CMROWID", true));
            rsLock = (ResultSet)pstmLock.executeQuery();
            if( ! rsLock.next() )
            {   iconexaobean.rollback();
                Exception eLock = new Exception("Um ou mais Registros foram Exclu�dos por outro Usu�rio. Realize nova consulta para obter os dados mais atualizados.");
                throw eLock;
                //return false;
            }
            boolean bValido = true;  //for(int i=1; i <= rs.getMetaData().getColumnCount(); i++)
            String auxColunaAlteracao = "";
            String auxLockAlteracao = "";
            if(getColuna(ColunaAlteracao, true) != null )
               auxColunaAlteracao = "" + getColuna(ColunaAlteracao, true);
            //if(rsLock.getObject(ColunaAlteracao) != null )
            //   auxLockAlteracao = "" + rsLock.getObject(ColunaAlteracao);
            if(rsLock.getString(ColunaAlteracao) != null )
               auxLockAlteracao = "" + rsLock.getString(ColunaAlteracao);

            if (auxColunaAlteracao.length() >= 18 && auxLockAlteracao.length() >= 18 ) {
              if (! auxColunaAlteracao.substring(1,18).equals(auxLockAlteracao.substring(1,18)) )
                bValido = false;
            }
            if (! bValido)
            {   iconexaobean.rollback();
                Exception eLock = new Exception("Um ou mais Registros foram Alterados por outro Usu�rio. Realize nova consulta para obter os dados mais atualizados.");
                throw eLock;
                //return false;
            }
         }
         /* Verifica se o registro foi alterado por outro usu�rio --- FIM -- */

         if(bDeveExecutarQuery)
         {
             if(! sqlDel.equals(""))
             {  // Create a Prepared Statement
                sqlDel += " WHERE ROWID = :1";
                pstmDel = (PreparedStatement)iconexaobean.getConnection().prepareStatement(sqlDel);
             }
             sqlDelAnterior = sqlDel;
//System.out.print("DELETE=" + sqlDel);
         }
          //pstmDel.setRowId(1, rs.getRowId("CMROWID"));
          if(! sqlDel.equals(""))
          {  pstmDel.setRowId(1, (RowId)getColuna("CMROWID", true));
          }

          bRealizouComando = true;
          if(! sqlDel.equals(""))
          {  pstmDel.executeUpdate();
          }
          numPages = -1; //1708
          ht_deletados.put(regAtual + "", "S");
          ht_deletados.put(regAtual + "NUMERO REGISTRO DELETADO", regAtual + "");
          ht_deletados.put(regAtual + "SALVOU BANCO", regAtual + "S");
          //ht_deletados.put(regAtual + "CMROWID", getColuna("CMROWID"));
          return true;
       }
       catch (SQLException e) {
         if (bRealizouComando)
         {  try { iconexaobean.rollback(); }
            catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(6):" + new java.util.Date() + " " + ignored + "." ); }
         }
         throw e;
         //return false;
       }
       catch (Exception ex) {
         if (bRealizouComando)
         {  try { iconexaobean.rollback(); }
            catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(7):" + new java.util.Date() + " " + ignored + "." ); }
         }
         throw ex;
         //return false;
       }
   }

   /**************************  EXCLUS�O L�GICA **************************/
   public boolean atualizarExcluindo(HttpServletRequest  request,
                                     HttpServletResponse response,
                                     HttpSession         session,
                                     String request_rowid)
          throws SQLException, java.io.IOException, Exception
   {
       boolean bRealizouComando = false;
       boolean bDeveExecutarQuery = true;
       if(! sqlDelLogica.equalsIgnoreCase(sqlDelLogicaAnterior))
       {  bDeveExecutarQuery = true;
          liberaRecursosDelLog();
          liberaRecursosLock();
       }
       else
       {  bDeveExecutarQuery = false;
       }

       try {

          //verificando se o ROWID � v�lido:
          if ((request_rowid != null) &&
              (! request_rowid.equals("")))
          {  if(! request_rowid.equalsIgnoreCase("" + getColuna("CMROWID", true)))
             {  response.sendRedirect(request.getSession().getAttribute("COM_RN") + "jsp/com_referererror.jsp?e=nav");
                return false;
             }
          }

         /* Verifica se o registro foi alterado por outro usu�rio --- INICIO */
         if((! sqlLock.equals("")) && (! ColunaAlteracao.equals("")))
         {  if(! sqlLock.equalsIgnoreCase(sqlLockAnterior))
            {  liberaRecursosLock();
               sqlLockExecuta = sqlLock + " WHERE ROWID = :1 FOR UPDATE";
               pstmLock = (PreparedStatement)iconexaobean.getConnection().prepareStatement(sqlLockExecuta,
                                                       rs.TYPE_FORWARD_ONLY,
                                                       rs.CONCUR_READ_ONLY);
               // pstmLock = (PreparedStatement)iconexaobean.getConnection().prepareStatement(sqlLock);
               sqlLockAnterior = sqlLock;
            }
            pstmLock.setRowId(1, (RowId)getColuna("CMROWID", true));
            rsLock = (ResultSet)pstmLock.executeQuery();
            if( ! rsLock.next() )
            {   iconexaobean.rollback();
                Exception eLock = new Exception("Um ou mais Registros foram Exclu�dos por outro Usu�rio. Realize nova consulta para obter os dados mais atualizados.");
                throw eLock;
                //return false;
            }
  //System.out.println("getColuna=" + getColuna(ColunaAlteracao));
  //System.out.println("getObject=" + rsLock.getObject(ColunaAlteracao));

            boolean bValido = true;  //for(int i=1; i <= rs.getMetaData().getColumnCount(); i++)
            String auxColunaAlteracao = "";
            String auxLockAlteracao = "";
            if(getColuna(ColunaAlteracao, true) != null )
               auxColunaAlteracao = "" + getColuna(ColunaAlteracao, true);
            //if(rsLock.getObject(ColunaAlteracao) != null )
            //   auxLockAlteracao = "" + rsLock.getObject(ColunaAlteracao);
            if(rsLock.getString(ColunaAlteracao) != null )
               auxLockAlteracao = "" + rsLock.getString(ColunaAlteracao);

            if (auxColunaAlteracao.length() >= 18 && auxLockAlteracao.length() >= 18 ) {
              if (! auxColunaAlteracao.substring(1,18).equals(auxLockAlteracao.substring(1,18)) )
                 bValido = false;
            }
            if (! bValido)
            {   iconexaobean.rollback();
                Exception eLock = new Exception("Um ou mais Registros foram Alterados por outro Usu�rio. Realize nova consulta para obter os dados mais atualizados.");
                throw eLock;
                //return false;
            }
         }
         /* Verifica se o registro foi alterado por outro usu�rio --- FIM -- */

         if(bDeveExecutarQuery)
         {
            if(! sqlDelLogica.equals(""))
            {  // Create a Prepared Statement
               if(sqlDelLogica.startsWith("BEGIN"))
               {  pstmDelLog = (PreparedStatement)iconexaobean.getConnection().prepareStatement(sqlDelLogica);
               }
               else if(sqlDelLogica.indexOf("WHERE  ") >= 0) //atencao para os 2 espacos
               {  pstmDelLog = (PreparedStatement)iconexaobean.getConnection().prepareStatement(sqlDelLogica);
               }
               else
               {  sqlDelLogica += " WHERE ROWID = :" + (vet_tipoparametrosDelLog.size()+1);
                  pstmDelLog = (PreparedStatement)iconexaobean.getConnection().prepareStatement(sqlDelLogica);
               }
            }
            sqlDelLogicaAnterior = sqlDelLogica;
//System.out.print("EXCL LOGICA=" + sqlDelLogica);
         }
         setaParametros(vet_tipoparametrosDelLog, vet_valorparametrosDelLog, pstmDelLog, "DLG");

         if((! sqlDelLogica.equals(""))
            && (! sqlDelLogica.startsWith("BEGIN"))
            && (sqlDelLogica.indexOf("WHERE  ") < 0))
         {  // pstmDelLog.setRowId((vet_tipoparametrosDelLog.size()+1),rs.getRowId("CMROWID"));
            pstmDelLog.setRowId((vet_tipoparametrosDelLog.size()+1),(RowId)getColuna("CMROWID", true));
         }

         bRealizouComando = true;
         if(! sqlDelLogica.equals(""))
         {  pstmDelLog.executeUpdate();
         }
         //rs.refreshRow();

         numPages = -1; //1708
         if(! ExclusaoLogicaMantem)
         { ht_deletados.put(regAtual + "", "S");
           ht_deletados.put(regAtual + "NUMERO REGISTRO DELETADO", regAtual + "");
           ht_deletados.put(regAtual + "SALVOU BANCO", regAtual + "S");
           //ht_deletados.put(regAtual + "CMROWID", getColuna("CMROWID"));
         }
         else
         { ht_atualizados.put(regAtual + "NUMERO REGISTRO ALTERADO", regAtual + "");
           ht_atualizados.put(regAtual + "SALVOU BANCO", regAtual + "S");
           ht_atualizados.put(regAtual + "CMREADONLY", "S");
         }

         vet_tipoparametrosDelLog.removeAllElements();
         vet_valorparametrosDelLog.removeAllElements();
         vet_nomeparametrosDelLog.removeAllElements();
         vet_calctipoparametrosDelLog.removeAllElements();
         vet_calcvalorparametrosDelLog.removeAllElements();
         vet_calcnomeparametrosDelLog.removeAllElements();
         return true;
       }
       catch (SQLException e) {
         if (bRealizouComando)
         {  try { iconexaobean.rollback(); }
            catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(8):" + new java.util.Date() + " " + ignored + "." ); }
         }
         throw e;
         //return false;
       }
       catch (Exception ex) {
         if (bRealizouComando)
         {  try { iconexaobean.rollback(); }
            catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(9):" + new java.util.Date() + " " + ignored + "." ); }
         }
         throw ex;
         //return false;
       }
   }

/* ----------------------------------- TOOLBAR ----------------------------------- */
   int getRegistrosInseridos() {
      return ht_inseridos.size();
   }

   public String showButtonDelete (HttpSession session)
          throws SQLException, java.io.IOException, Exception
   {
      if(regAtual != 0)
      {  if(! getColuna("CMREADONLY").equals("S"))
         {
            return "<A HREF=\"javascript:void excluir(" + regAtual + ", '" + getColuna("CMROWID") + "', '" + getPaginaSubmit() + "')\">"
                 + "<img src=\"/portalcom/webapp"+session.getAttribute("COM_DOMINIO")+"/images/deleterec.gif\" border=0 alt=\"Excluir\">"
                 + "</A>";
         }
         else return "&nbsp;";
      }
      else return "&nbsp;";
   }
   public String showButtonUpdate (HttpSession session)
          throws SQLException, java.io.IOException, Exception
   {
      if(regAtual != 0)
      {  //2406 if(! getColuna("CMREADONLY").equals("S"))
         //2406 {
            return "<A HREF=\"javascript:void editar(" + regAtual + ", '" + getPaginaEdit() + "')\">"
                 + "<img src=\"/portalcom/webapp"+session.getAttribute("COM_DOMINIO")+"/images/alterar.gif\"  border=0  alt=\"Editar\">"
                 + "</A>";
         //2406 }
         //2406 else return "&nbsp;";
      }
      else return "&nbsp;";
   }
   public String showButtonDeleteEdit (HttpSession session)
          throws SQLException, java.io.IOException, Exception
   {
      //if(regAtual != 0)
      //{
         if(! getColuna("CMREADONLY").equals("S"))
         {
            return "<A HREF=\"javascript:void exclui()\" TABINDEX=\"3\" NAME=\"EXCLUI\">"
                 + "<img src=\"/portalcom/images"+session.getAttribute("COM_DOMINIO")+"/commit/botoes/bot_excluir_01.gif\"  border=\"0\"  alt=\"Excluir\">"
                 + "</A>";
         }
         else return "&nbsp;";
      //}
      //else return "&nbsp;";
   }
   public String showButtonSalvarEdit (HttpSession session)
          throws SQLException, java.io.IOException, Exception
   {
      //if(regAtual != 0)
      //{
         if(! getColuna("CMREADONLY").equals("S"))
         {
            return "<A HREF=\"javascript:void salva()\" TABINDEX=\"1\" NAME=\"SALVA\">"
                 + "<img src=\"/portalcom/images"+session.getAttribute("COM_DOMINIO")+"/commit/botoes/bot_salvar_01.gif\"  border=\"0\"  alt=\"Salvar\">"
                 + "</A>";
         }
         else return "&nbsp;";
      //}
      //else return "&nbsp;";
   }

// ------------------------ M�TODOS de CAMPOS (INPUTs)----------------------------------

   public String showCamposPrevious(String oper,
                                    verificaAcesso acesso,
                                    HttpServletRequest request,
                                    int nIndex)
          throws java.io.IOException, Exception
   {
      String retorno = "";
      String sufixo = "";
      if(nIndex != -1)
         sufixo = "" + numeroTabeladetalhe + nIndex;

      retorno += "<input type=\"hidden\" name=\"CMROWID_PREVIOUS" + sufixo + "\" "
                      + " value=\"" + (oper.equals("I")? "" : "" + UtilBean.getVerificaNull(getColuna("CMROWID"))) + "\">";
      retorno += "<input type=\"hidden\" name=\"CMROWID" + sufixo + "\" "
                      + " value=\"" + (oper.equals("I")? "" : "" + UtilBean.getVerificaNull(getColuna("CMROWID"))) + "\">";

      for (int i=0; i < vet_campos.size(); i++)
      {  //vet_campos.elementAt(i) + ""
         if (! acesso.verificaAcessoCampo(acesso.getJSP(request), (vet_campos.elementAt(i) + "")).equals("I"))
         {
            retorno += "<input type=\"hidden\" name=\"" + vet_campos.elementAt(i) + "_PREVIOUS" + sufixo + "\" "
                            + " value=\"" + (oper.equals("I")? "" : "" + UtilBean.getVerificaNull(getColuna(vet_campos.elementAt(i) + ""))) + "\">";
         } //fim if
      } // fim for
      return retorno;
   } // fim metodo

   public String showCampoCheckApagar(int nIndex,
                                      String oper,
                                      verificaAcesso acesso,
                                      HttpServletRequest request,
                                      String script)
          throws java.io.IOException, Exception
   {
      if(getregAtual() == 0)
         return "&nbsp;";

      String retorno = "";
      String sufixo = "";
      if(nIndex != -1)
         sufixo = "" + numeroTabeladetalhe + nIndex;

      String TIPO_ACESSO = acesso.verificaAcessoMenu(acesso.getJSP(request));
      if(! oper.equals("I"))
      {  if(getColuna("CMREADONLY").equals("S"))
         { if(TIPO_ACESSO.equals("T"))
             TIPO_ACESSO = "L";
         }
      }
      if (TIPO_ACESSO.equals("T"))
      {
         retorno += "<input class=\"cmpCheck\" type=\"CHECKBOX\" name=\"APAGAR" + sufixo + "\" "
                  + " value=\"S\" ";
         //if(nIndex != -1)
         //{ retorno += " onChange=\"javascript:trocaStatus(document.iForm.STATUS" + sufixo + ")\" ";
         //}
         retorno += script + ">";
      } else if (TIPO_ACESSO.equals("L"))
             {
                retorno += "<input type=\"hidden\" name=\"APAGAR" + sufixo + "\" "
                        +  " value=\"N\">";
             }
      return retorno;
   }

   public String showCampoInputText(String nomeCampo, String value, int nIndex,
                                    String psize, String pmax, String oper,
                                    verificaAcesso acesso,
                                    HttpServletRequest request,
                                    String script )
          throws java.io.IOException, Exception
   {
      String retorno = "";
      String sufixo = "";
      if(nIndex != -1)
         sufixo = "" + numeroTabeladetalhe + nIndex;
      if( value == null)
         value = (oper.equals("I")? "" : "" + UtilBean.getVerificaNull(getColuna(nomeCampo)));

      String TIPO_ACESSO = acesso.verificaAcessoCampo(acesso.getJSP(request), nomeCampo);
      if(! oper.equals("I"))
      {  if(getColuna("CMREADONLY").equals("S"))
         { if(TIPO_ACESSO.equals("T"))
             TIPO_ACESSO = "L";
         }
      }
      if (TIPO_ACESSO.equals("T"))
      {
         retorno += "<input class=\"cmpText\" type=\"TEXT\" size=\"" + psize + "\" maxlength=\"" + pmax + "\" name=\"" + nomeCampo + sufixo + "\" "
                  + " value=\"" + value + "\" ";
         if(nIndex != -1)
         { retorno += " onChange=\"javascript:trocaStatus(document.iForm.STATUS" + sufixo + ")\" ";
         }
         retorno += script + ">";
      } else if (TIPO_ACESSO.equals("L"))
             {
                retorno += "<input type=\"hidden\" name=\"" + nomeCampo + sufixo + "\" "
                        +  " value=\"" + value + "\">";
                retorno += value;
             }

      return retorno;
   }
   public String showCampoInputText(String nomeCampo, String value, int nIndex,
                                    String psize, String pmax, String oper,
                                    verificaAcesso acesso,
                                    HttpServletRequest request,
                                    String script, String sOnChange )
          throws java.io.IOException, Exception
   {
      String retorno = "";
      String sufixo = "";
      if(nIndex != -1)
         sufixo = "" + numeroTabeladetalhe + nIndex;
      if( value == null)
         value = (oper.equals("I")? "" : "" + UtilBean.getVerificaNull(getColuna(nomeCampo)));

      String TIPO_ACESSO = acesso.verificaAcessoCampo(acesso.getJSP(request), nomeCampo);
      if(! oper.equals("I"))
      {  if(getColuna("CMREADONLY").equals("S"))
         { if(TIPO_ACESSO.equals("T"))
             TIPO_ACESSO = "L";
         }
      }
      if (TIPO_ACESSO.equals("T"))
      {
         retorno += "<input class=\"cmpText\" type=\"TEXT\" size=\"" + psize + "\" maxlength=\"" + pmax + "\" name=\"" + nomeCampo + sufixo + "\" "
                  + " value=\"" + value + "\" ";
         if(nIndex != -1)
           retorno += " onChange=\"javascript:trocaStatus(document.iForm.STATUS" + sufixo + "); " + sOnChange + "\" ";
         else
           retorno += " onChange=\"javascript:" + sOnChange + "\" ";

         retorno += script + ">";
      } else if (TIPO_ACESSO.equals("L"))
             {
                retorno += "<input type=\"hidden\" name=\"" + nomeCampo + sufixo + "\" "
                        +  " value=\"" + value + "\">";
                retorno += value;
             }

      return retorno;
   }
   public String showCampoInputTextRO(String nomeCampo, String value, int nIndex,
                                    String psize, String pmax, String oper,
                                    verificaAcesso acesso,
                                    HttpServletRequest request,
                                    String script )
          throws java.io.IOException, Exception
   {
      String retorno = "";
      String sufixo = "";
      if(nIndex != -1)
         sufixo = "" + numeroTabeladetalhe + nIndex;
      if( value == null)
         value = (oper.equals("I")? "" : "" + UtilBean.getVerificaNull(getColuna(nomeCampo)));

      String TIPO_ACESSO = acesso.verificaAcessoCampo(acesso.getJSP(request), nomeCampo);
      if(! oper.equals("I"))
      {  if(getColuna("CMREADONLY").equals("S"))
         { if(TIPO_ACESSO.equals("T"))
             TIPO_ACESSO = "L";
         }
      }
      if (TIPO_ACESSO.equals("T"))
      {
         retorno += "<input class=\"cmpText\" type=\"TEXT\" size=\"" + psize + "\" maxlength=\"" + pmax + "\" name=\"" + nomeCampo + sufixo + "\" "
                  + " value=\"" + value + "\" readonly ";
         if(nIndex != -1)
         { retorno += " onChange=\"javascript:trocaStatus(document.iForm.STATUS" + sufixo + ")\" ";
         }
         retorno += script + ">";
      } else if (TIPO_ACESSO.equals("L"))
             {
                retorno += "<input type=\"hidden\" name=\"" + nomeCampo + sufixo + "\" "
                        +  " value=\"" + value + "\">";
                retorno += value;
             }

      return retorno;
   }
   public String showCampoInputTextRO(String nomeCampo, String value, int nIndex,
                                    String psize, String pmax, String oper,
                                    verificaAcesso acesso,
                                    HttpServletRequest request,
                                    String script, String sOnChange )
          throws java.io.IOException, Exception
   {
      String retorno = "";
      String sufixo = "";
      if(nIndex != -1)
         sufixo = "" + numeroTabeladetalhe + nIndex;
      if( value == null)
         value = (oper.equals("I")? "" : "" + UtilBean.getVerificaNull(getColuna(nomeCampo)));

      String TIPO_ACESSO = acesso.verificaAcessoCampo(acesso.getJSP(request), nomeCampo);
      if(! oper.equals("I"))
      {  if(getColuna("CMREADONLY").equals("S"))
         { if(TIPO_ACESSO.equals("T"))
             TIPO_ACESSO = "L";
         }
      }
      if (TIPO_ACESSO.equals("T"))
      {
         retorno += "<input class=\"cmpText\" type=\"TEXT\" size=\"" + psize + "\" maxlength=\"" + pmax + "\" name=\"" + nomeCampo + sufixo + "\" "
                  + " value=\"" + value + "\" readonly ";
         if(nIndex != -1)
           retorno += " onChange=\"javascript:trocaStatus(document.iForm.STATUS" + sufixo + "); " + sOnChange + "\" ";
         else
           retorno += " onChange=\"javascript:" + sOnChange + "\" ";

         retorno += script + ">";
      } else if (TIPO_ACESSO.equals("L"))
             {
                retorno += "<input type=\"hidden\" name=\"" + nomeCampo + sufixo + "\" "
                        +  " value=\"" + value + "\">";
                retorno += value;
             }

      return retorno;
   }

   public String showCampoInputTextLov(String nomeCampo, String value, int nIndex,
                                       String psize, String pmax, String oper,
                                       verificaAcesso acesso,
                                       HttpServletRequest request,
                                       String script, String campoAlt, boolean editavel  )
            throws java.io.IOException, Exception
   {
      String retorno = "";
      String sufixo = "";
      if(nIndex != -1)
         sufixo = "" + numeroTabeladetalhe + nIndex;
      if( value == null)
         value = (oper.equals("I")? "" : "" + UtilBean.getVerificaNull(getColuna(nomeCampo)));

      String TIPO_ACESSO = acesso.verificaAcessoCampo(acesso.getJSP(request), nomeCampo);
      if(! oper.equals("I"))
      {  if(getColuna("CMREADONLY").equals("S"))
         { if(TIPO_ACESSO.equals("T"))
             TIPO_ACESSO = "L";
         }
      }
      if (TIPO_ACESSO.equals("T"))
      {
         retorno += "<NOBR><input class=\"cmpText\" type=\"TEXT\" size=\"" + psize + "\" maxlength=\"" + pmax + "\" name=\"" + nomeCampo + sufixo + "\" "
                  + " value=\"" + value + "\" readonly ";
         if(nIndex != -1)
         {  retorno += " onChange=\"javascript:trocaStatus(document.iForm.STATUS" + sufixo + ")\" ";
         }

         if(! editavel ) {
           retorno += " onFocus=\"javascript: vCampo1 = document.iForm." + nomeCampo + sufixo + ".value\" ";
           retorno += " onBlur=\"javascript: (document.iForm." + nomeCampo + sufixo + ".value == ''? null:"
                    + " document.iForm." + nomeCampo + sufixo + ".value = vCampo1)\" ";
         }
         retorno += script + ">";

         //coloca o botao para a lov
         String conteudo = "";
         int indexAnt = 0;
         int index = 0;
         while(index > -1 ) {
           index = campoAlt.indexOf(",", indexAnt );
           if( index == -1 ) {
             if( conteudo.length() > 0 ) {
               conteudo += ", document.iForm." + campoAlt.substring(indexAnt) + sufixo;
             }
             else
               conteudo = " document.iForm." + campoAlt + sufixo;
           }
           else {
             if( conteudo.length() > 0 )
               conteudo += ", document.iForm." + campoAlt.substring(indexAnt, index) + sufixo;
             else
               conteudo += " document.iForm." + campoAlt.substring(indexAnt, index) + sufixo;
           }
           indexAnt = index + 1;
         }

         retorno += " <A HREF=\"javascript:void abrelov("+ conteudo + ");\">"
                  + "<img src=\"/portalcom/images"+request.getSession().getAttribute("COM_DOMINIO")+"/images/botoes_novos/ico_vermais.gif\" align=\"absmiddle\" "
                  + " border=\"0\" alt=\"Localizar\"></a></NOBR> ";

      } else if (TIPO_ACESSO.equals("L"))
             {
                retorno += "<input type=\"hidden\" name=\"" + nomeCampo + sufixo + "\" "
                        +  " value=\"" + value + "\">";
                retorno += value;
             }
      return retorno;
   }

   public String showCampoInputTextData(String nomeCampo, String value, int nIndex,
                                        String psize, String pmax, String oper,
                                        verificaAcesso acesso,
                                        HttpServletRequest request,
                                        String script )
          throws java.io.IOException, Exception
   {
      String retorno = "";
      String sufixo = "";
      if(nIndex != -1)
         sufixo = "" + numeroTabeladetalhe + nIndex;
      if( value == null)
         value = (oper.equals("I")? "" : "" + UtilBean.formataData(getColuna(nomeCampo), "dd/MM/yyyy"));

      String TIPO_ACESSO = acesso.verificaAcessoCampo(acesso.getJSP(request), nomeCampo);
      if(! oper.equals("I"))
      {  if(getColuna("CMREADONLY").equals("S"))
         { if(TIPO_ACESSO.equals("T"))
             TIPO_ACESSO = "L";
         }
      }
      if (TIPO_ACESSO.equals("T"))
      {
         retorno += "<NOBR><input class=\"cmpText\" type=\"TEXT\" size=\"" + psize + "\" maxlength=\"" + pmax + "\" name=\"" + nomeCampo + sufixo + "\" "
                  + " value=\"" + value + "\" ";
         if(nIndex != -1)
         {  retorno += " onChange=\"javascript:trocaStatusData(this, document.iForm.STATUS" + sufixo + ")\" "
                     + " onselect=\"null\" onfocus=\"this.select()\" ";
         }
         else
         {
            retorno += " onChange=\"this.value = datecheck(this.value,NLSformat);\" "
                     + " onselect=\"null\" onfocus=\"this.select()\" ";
         }
         retorno += script + ">";
         //coloca o botao para o calendario
         if(nIndex != -1)
         {  retorno += "  <a href=\"javascript:void trocaStatusDataLOV(document.iForm."+ nomeCampo + sufixo
                  + ",document.iForm.STATUS" + sufixo + ")\"> "
                  + " <img src=\"/portalcom/webapp"+request.getSession().getAttribute("COM_DOMINIO")+"/images/FNDICLDR.gif\" align=\"absmiddle\" border=\"0\" "
                  + " alt=\"Calend�rio\"></a></NOBR> ";
         }
         else
         {  retorno += "  <a href=\"javascript:void opencal(document.iForm."+ nomeCampo + sufixo
                  + ")\"> "
                  + " <img src=\"/portalcom/webapp"+request.getSession().getAttribute("COM_DOMINIO")+"/images/FNDICLDR.gif\" align=\"absmiddle\" border=\"0\" "
                  + " alt=\"Calend�rio\"></a></NOBR> ";
         }

      } else if (TIPO_ACESSO.equals("L"))
             {
                retorno += "<input type=\"hidden\" name=\"" + nomeCampo + sufixo + "\" "
                        +  " value=\"" + value + "\">";
                retorno += value;
             }
      return retorno;
   }

   public String showCampoInputTextData(String nomeCampo, String value, int nIndex,
                                        String psize, String pmax, String oper,
                                        verificaAcesso acesso,
                                        HttpServletRequest request,
                                        String script, String sOnChange )
          throws java.io.IOException, Exception
   {
      String retorno = "";
      String sufixo = "";
      if(nIndex != -1)
         sufixo = "" + numeroTabeladetalhe + nIndex;
      if( value == null)
         value = (oper.equals("I")? "" : "" + UtilBean.formataData(getColuna(nomeCampo), "dd/MM/yyyy"));

      String TIPO_ACESSO = acesso.verificaAcessoCampo(acesso.getJSP(request), nomeCampo);
      if(! oper.equals("I"))
      {  if(getColuna("CMREADONLY").equals("S"))
         { if(TIPO_ACESSO.equals("T"))
             TIPO_ACESSO = "L";
         }
      }
      if (TIPO_ACESSO.equals("T"))
      {
         retorno += "<NOBR><input class=\"cmpText\" type=\"TEXT\" size=\"" + psize + "\" maxlength=\"" + pmax + "\" name=\"" + nomeCampo + sufixo + "\" "
                  + " value=\"" + value + "\" ";
         if(nIndex != -1)
         {  retorno += " onChange=\"javascript:trocaStatusData(this, document.iForm.STATUS" + sufixo + "); "
                     + sOnChange + "\" onselect=\"null\" onfocus=\"this.select()\" ";
         }
         else
         {
            retorno += " onChange=\"this.value = datecheck(this.value,NLSformat); "
                     + sOnChange + "\" onselect=\"null\" onfocus=\"this.select()\" ";
         }
         retorno += script + ">";
         //coloca o botao para o calendario
         if(nIndex != -1)
         {  retorno += "  <a href=\"javascript:void trocaStatusDataLOV(document.iForm."+ nomeCampo + sufixo
                  + ",document.iForm.STATUS" + sufixo + ")\"> "
                  + " <img src=\"/portalcom/webapp"+request.getSession().getAttribute("COM_DOMINIO")+"/images/FNDICLDR.gif\" align=\"absmiddle\" border=\"0\" "
                  + " alt=\"Calend�rio\"></a></NOBR> ";
         }
         else
         {  retorno += "  <a href=\"javascript:void opencal(document.iForm."+ nomeCampo + sufixo
                  + ")\"> "
                  + " <img src=\"/portalcom/webapp"+request.getSession().getAttribute("COM_DOMINIO")+"/images/FNDICLDR.gif\" align=\"absmiddle\" border=\"0\" "
                  + " alt=\"Calend�rio\"></a></NOBR> ";
         }

      } else if (TIPO_ACESSO.equals("L"))
             {
                retorno += "<input type=\"hidden\" name=\"" + nomeCampo + sufixo + "\" "
                        +  " value=\"" + value + "\">";
                retorno += value;
             }
      return retorno;
   }

   public String showCampoInputCheck(String nomeCampo, String label, String value, int nIndex,
                                     String oper,
                                     verificaAcesso acesso,
                                     HttpServletRequest request,
                                     String script, String checked, String pdefault )
          throws java.io.IOException, Exception
   {
      String retorno = "";
      String sufixo = "";
      if(nIndex != -1)
         sufixo = "" + numeroTabeladetalhe + nIndex;
      if( value == null) {
         value = (oper.equals("I")? pdefault : "" + UtilBean.getVerificaNull(getColuna(nomeCampo)));
      }

      String TIPO_ACESSO = acesso.verificaAcessoCampo(acesso.getJSP(request), nomeCampo);
      if(! oper.equals("I"))
      {  if(getColuna("CMREADONLY").equals("S"))
         { if(TIPO_ACESSO.equals("T"))
             TIPO_ACESSO = "L";
         }
      }
      if (TIPO_ACESSO.equals("T"))
      {
         retorno += "<input class=\"cmpCheck\" type=\"CHECKBOX\" name=\"" + nomeCampo + sufixo + "\" "
                  + " value=\"" + checked + "\" ";

         if( checked.equals(value) ) retorno += " CHECKED ";

         if(nIndex != -1)
         {  retorno += " onClick=\"javascript:trocaStatus(document.iForm.STATUS" + sufixo + ")\" ";
         }
         retorno += script + "> " + label;
      } else if (TIPO_ACESSO.equals("L"))
             {
                retorno += "<input type=\"hidden\" name=\"" + nomeCampo + sufixo + "\" "
                        +  " value=\"" + value + "\">";
                retorno += value;
             }
      return retorno;
   }
   public String showCampoInputCheck(String nomeCampo, String label, String value, int nIndex,
                                     String oper,
                                     verificaAcesso acesso,
                                     HttpServletRequest request,
                                     String script, String checked, String pdefault,
                                     String sOnClick )
          throws java.io.IOException, Exception
   {
      String retorno = "";
      String sufixo = "";
      if(nIndex != -1)
         sufixo = "" + numeroTabeladetalhe + nIndex;
      if( value == null) {
         value = (oper.equals("I")? pdefault : "" + UtilBean.getVerificaNull(getColuna(nomeCampo)));
      }

      String TIPO_ACESSO = acesso.verificaAcessoCampo(acesso.getJSP(request), nomeCampo);
      if(! oper.equals("I"))
      {  if(getColuna("CMREADONLY").equals("S"))
         { if(TIPO_ACESSO.equals("T"))
             TIPO_ACESSO = "L";
         }
      }
      if (TIPO_ACESSO.equals("T"))
      {
         retorno += "<input class=\"cmpCheck\" type=\"CHECKBOX\" name=\"" + nomeCampo + sufixo + "\" "
                  + " value=\"" + checked + "\" ";

         if( checked.equals(value) ) retorno += " CHECKED ";

         if(nIndex != -1)
           retorno += " onClick=\"javascript:trocaStatus(document.iForm.STATUS" + sufixo + "); " + sOnClick + "\" ";
         else
           retorno += " onClick=\"javascript:" + sOnClick + "\" ";

         retorno += script + "> " + label;
      } else if (TIPO_ACESSO.equals("L"))
             {
                retorno += "<input type=\"hidden\" name=\"" + nomeCampo + sufixo + "\" "
                        +  " value=\"" + value + "\">";
                retorno += value;
             }
      return retorno;
   }
   public String showCampoInputRadio(String nomeCampo, String label, String value, int nIndex,
                                     String oper,
                                     verificaAcesso acesso,
                                     HttpServletRequest request,
                                     String script, String checked, String pdefault )
          throws java.io.IOException, Exception
   {
      String retorno = "";
      String sufixo = "";
      if(nIndex != -1)
         sufixo = "" + numeroTabeladetalhe + nIndex;
      if( value == null)
         value = (oper.equals("I")? pdefault : "" + UtilBean.getVerificaNull(getColuna(nomeCampo)));

      String TIPO_ACESSO = acesso.verificaAcessoCampo(acesso.getJSP(request), nomeCampo);
      if(! oper.equals("I"))
      {  if(getColuna("CMREADONLY").equals("S"))
         { if(TIPO_ACESSO.equals("T"))
             TIPO_ACESSO = "L";
         }
      }
      if (TIPO_ACESSO.equals("T"))
      {
         retorno += "<input class=\"cmpRadio\" type=\"RADIO\" name=\"" + nomeCampo + sufixo + "\" "
                  + " value=\"" + checked + "\" ";

         if( checked.equals(value) ) retorno += " CHECKED ";

         if(nIndex != -1)
         {  retorno += " onClick=\"javascript:trocaStatus(document.iForm.STATUS" + sufixo + ")\" ";
         }
         retorno += script + "> " + label ;
      } else if (TIPO_ACESSO.equals("L"))
             {
                retorno += "<input type=\"hidden\" name=\"" + nomeCampo + sufixo + "\" "
                        +  " value=\"" + value + "\">";
                retorno += value;
             }
      return retorno;
   }
   public String showCampoInputTextarea(String nomeCampo, String value, int nIndex,
                                        int linhas, int colunas,
                                        String oper,
                                        verificaAcesso acesso,
                                        HttpServletRequest request,
                                        String script )
          throws java.io.IOException, Exception
   {
      String retorno = "";
      String sufixo = "";
      if(nIndex != -1)
         sufixo = "" + numeroTabeladetalhe + nIndex;
      if( value == null)
         value = (oper.equals("I")? "" : "" + UtilBean.getVerificaNull(getColuna(nomeCampo)));

      String TIPO_ACESSO = acesso.verificaAcessoCampo(acesso.getJSP(request), nomeCampo);
      if(! oper.equals("I"))
      {  if(getColuna("CMREADONLY").equals("S"))
         { if(TIPO_ACESSO.equals("T"))
             TIPO_ACESSO = "L";
         }
      }
      if (TIPO_ACESSO.equals("T"))
      {
         retorno += "<TEXTAREA class=\"cmpTextarea\" name=\"" + nomeCampo + sufixo + "\" "
                  + " rows=\"" + linhas + "\" cols=\"" + colunas + "\" ";

         if(nIndex != -1)
         {  retorno += " onChange=\"javascript:trocaStatus(document.iForm.STATUS" + sufixo + ")\" ";
         }
         retorno += script + ">" + value + "</TEXTAREA> ";
      } else if (TIPO_ACESSO.equals("L"))
             {
                retorno += "<input type=\"hidden\" name=\"" + nomeCampo + sufixo + "\" "
                        +  " value=\"" + value + "\">";
                retorno += value;
             }
      return retorno;
   }

  /* ==================================================
       metodos do banco para inputCampo
                  Alexandre

  Aten��o =  todos os metodos para gerar campos do banco (...sql)
             dever�o usar o metodo get da variavel (...sql, Ex: getComboSql())
             da classe cmDbBeanRO para utilizar os metodos do banco.
             Ex.: cmDbBean0.getComboSql().setSql("SELECT....");

  ==================================================*/

   public String inicializaCampoInputComboSql(String nomeCampo, int nIndex, String psize,
                                     String oper,
                                     verificaAcesso acesso,
                                     HttpServletRequest request,
                                     String script, String desc, String selected, boolean multiple)
          throws java.io.IOException, Exception
   {
      comboSql = new comboBean();
      String retorno = "";
      String sufixo = "";
      if(nIndex != -1)
         sufixo = "" + numeroTabeladetalhe + nIndex;

      if( selected == null)
         selected = (oper.equals("I")? "" : "" + UtilBean.getVerificaNull(getColuna(nomeCampo)));

      String TIPO_ACESSO = acesso.verificaAcessoCampo(acesso.getJSP(request), nomeCampo);
      if(! oper.equals("I"))
      {  if(getColuna("CMREADONLY").equals("S"))
         { if(TIPO_ACESSO.equals("T"))
             TIPO_ACESSO = "L";
         }
      }
      if (TIPO_ACESSO.equals("T"))
      {
         if(nIndex != -1)
           script = " onChange=\"javascript:trocaStatus(document.iForm.STATUS" + sufixo + ")\" " + script;

         comboSql.inicializa(nomeCampo+sufixo, desc, selected, Integer.parseInt(psize), multiple, script);
         retorno = "inicializou";

      } else if (TIPO_ACESSO.equals("L"))
             {
                retorno += "<input type=\"hidden\" name=\"" + nomeCampo + sufixo + "\" "
                        +  " value=\"" + selected + "\">";
                retorno += selected;
             }
      return retorno;
   }
   public String inicializaCampoInputComboSql(String nomeCampo, int nIndex, String psize,
                                     String oper,
                                     verificaAcesso acesso,
                                     HttpServletRequest request,
                                     String script, String desc, String selected, boolean multiple, String sOnChange)
          throws java.io.IOException, Exception
   {
      comboSql = new comboBean();
      String retorno = "";
      String sufixo = "";
      if(nIndex != -1)
         sufixo = "" + numeroTabeladetalhe + nIndex;

      if( selected == null)
         selected = (oper.equals("I")? "" : "" + UtilBean.getVerificaNull(getColuna(nomeCampo)));

      String TIPO_ACESSO = acesso.verificaAcessoCampo(acesso.getJSP(request), nomeCampo);
      if(! oper.equals("I"))
      {  if(getColuna("CMREADONLY").equals("S"))
         { if(TIPO_ACESSO.equals("T"))
             TIPO_ACESSO = "L";
         }
      }
      if (TIPO_ACESSO.equals("T"))
      {
         if(nIndex != -1)
           script = " onChange=\"javascript:trocaStatus(document.iForm.STATUS" + sufixo + "); " + sOnChange + "\" " + script;
         else
           script = " onChange=\"javascript:" + sOnChange + "\" " + script;

         comboSql.inicializa(nomeCampo+sufixo, desc, selected, Integer.parseInt(psize), multiple, script);
         retorno = "inicializou";

      } else if (TIPO_ACESSO.equals("L"))
             {
                retorno += "<input type=\"hidden\" name=\"" + nomeCampo + sufixo + "\" "
                        +  " value=\"" + selected + "\">";
                retorno += selected;
             }
      return retorno;
   }

   public void showCampoInputComboOption( String value, String desc )
                 throws java.io.IOException, Exception {

     comboOptionValue.addElement(value);
     comboOptionDesc.addElement(desc);
   }
   public String showCampoInputCombo(String nomeCampo, int nIndex, String psize,
                                     String oper,
                                     verificaAcesso acesso,
                                     HttpServletRequest request,
                                     String script, String selected, boolean multiple )
          throws java.io.IOException, Exception
   {
      String retorno = "";
      String sufixo = "";
      if(nIndex != -1)
         sufixo = "" + numeroTabeladetalhe + nIndex;

      if( selected == null)
         selected = (oper.equals("I")? "" : "" + UtilBean.getVerificaNull(getColuna(nomeCampo)));

      String TIPO_ACESSO = acesso.verificaAcessoCampo(acesso.getJSP(request), nomeCampo);
      if(! oper.equals("I"))
      {  if(getColuna("CMREADONLY").equals("S"))
         { if(TIPO_ACESSO.equals("T"))
             TIPO_ACESSO = "L";
         }
      }
      if (TIPO_ACESSO.equals("T"))
      {
         retorno += "<SELECT class=\"cmpSelect\" name=\"" + nomeCampo + sufixo + "\" "
                  + " size=\"" + psize + "\" ";

         if( multiple ) retorno += " MULTIPLE ";

         if(nIndex != -1)
         {  retorno += " onChange=\"javascript:trocaStatus(document.iForm.STATUS" + sufixo + ")\" ";
         }
         retorno += script + "> \n ";

         for( int c=0; c<comboOptionValue.size(); c++ ) {
            if( selected.equals(comboOptionValue.elementAt(c)) )
               retorno += " <OPTION value=\"" + comboOptionValue.elementAt(c) + "\" SELECTED> " + comboOptionDesc.elementAt(c) + " \n ";
            else
               retorno += " <OPTION value=\"" + comboOptionValue.elementAt(c) + "\"> " + comboOptionDesc.elementAt(c) + " \n ";
         }
         retorno += " </SELECT> ";
      } else if (TIPO_ACESSO.equals("L"))
             {
                retorno += "<input type=\"hidden\" name=\"" + nomeCampo + sufixo + "\" "
                        +  " value=\"" + selected + "\">";
                retorno += selected;
             }
//alex 2606
      comboOptionValue.removeAllElements();
      comboOptionDesc.removeAllElements();
      return retorno;
   }
   public String showCampoInputCombo(String nomeCampo, int nIndex, String psize,
                                     String oper,
                                     verificaAcesso acesso,
                                     HttpServletRequest request,
                                     String script, String selected, boolean multiple, String sOnChange )
          throws java.io.IOException, Exception
   {
      String retorno = "";
      String sufixo = "";
      if(nIndex != -1)
         sufixo = "" + numeroTabeladetalhe + nIndex;

      if( selected == null)
         selected = (oper.equals("I")? "" : "" + UtilBean.getVerificaNull(getColuna(nomeCampo)));

      String TIPO_ACESSO = acesso.verificaAcessoCampo(acesso.getJSP(request), nomeCampo);
      if(! oper.equals("I"))
      {  if(getColuna("CMREADONLY").equals("S"))
         { if(TIPO_ACESSO.equals("T"))
             TIPO_ACESSO = "L";
         }
      }
      if (TIPO_ACESSO.equals("T"))
      {
         retorno += "<SELECT class=\"cmpSelect\" name=\"" + nomeCampo + sufixo + "\" "
                  + " size=\"" + psize + "\" ";

         if( multiple ) retorno += " MULTIPLE ";

         if(nIndex != -1)
           retorno += " onChange=\"javascript:trocaStatus(document.iForm.STATUS" + sufixo + "); " + sOnChange + "\" ";
         else
           retorno += " onChange=\"javascript:" + sOnChange + "\" ";
         retorno += script + "> \n ";

         for( int c=0; c<comboOptionValue.size(); c++ ) {
            if( selected.equals(comboOptionValue.elementAt(c)) )
               retorno += " <OPTION value=\"" + comboOptionValue.elementAt(c) + "\" SELECTED> " + comboOptionDesc.elementAt(c) + " \n ";
            else
               retorno += " <OPTION value=\"" + comboOptionValue.elementAt(c) + "\"> " + comboOptionDesc.elementAt(c) + " \n ";
         }
         retorno += " </SELECT> ";
      } else if (TIPO_ACESSO.equals("L"))
             {
                retorno += "<input type=\"hidden\" name=\"" + nomeCampo + sufixo + "\" "
                        +  " value=\"" + selected + "\">";
                retorno += selected;
             }
//alex 2606
      comboOptionValue.removeAllElements();
      comboOptionDesc.removeAllElements();
      return retorno;
   }
   public String showCampoInputHidden(String nomeCampo, String value, int nIndex,
                                      String oper,
                                      verificaAcesso acesso,
                                      HttpServletRequest request,
                                      boolean mostraValor)
          throws java.io.IOException, Exception
   {
      String retorno = "";
      String sufixo = "";
      if(nIndex != -1)
         sufixo = "" + numeroTabeladetalhe + nIndex;

      if( value == null)
         value = (oper.equals("I")? "" : "" + UtilBean.getVerificaNull(getColuna(nomeCampo)));

      if (! acesso.verificaAcessoCampo(acesso.getJSP(request), nomeCampo).equals("I"))
      {
         retorno += "<input type=\"hidden\" name=\"" + nomeCampo + sufixo + "\" "
                 +  " value=\"" + value + "\">";
         if(mostraValor)
            retorno += value;
      }
      return retorno;
   }

// ----------------------------------- IN�CIO M�TODOS MULTIROW -------------------------------
Vector vet_tipoparametrosTabelaMasterIns  = new Vector();
Vector vet_valorparametrosTabelaMasterIns = new Vector();
Vector vet_nomeparametrosTabelaMasterIns  = new Vector();
Vector vet_calctipoparametrosTabelaMasterIns  = new Vector();
Vector vet_calcvalorparametrosTabelaMasterIns = new Vector();
Vector vet_calcnomeparametrosTabelaMasterIns  = new Vector();

Vector vet_tipoparametrosTabelaIns         = new Vector();
Vector vet_colunaparametrosTabelaIns       = new Vector();
Vector vet_nomeparametrosTabelaIns         = new Vector();
Vector vet_isCheckparametrosTabelaIns      = new Vector();
Vector vet_valueUncheckparametrosTabelaIns = new Vector();
Vector vet_calctipoparametrosTabelaIns     = new Vector();
Vector vet_calccolunaparametrosTabelaIns   = new Vector();
Vector vet_calcnomeparametrosTabelaIns     = new Vector();

Vector vet_tipoparametrosTabelaMasterUp       = new Vector();
Vector vet_valorparametrosTabelaMasterUp      = new Vector();
Vector vet_nomeparametrosTabelaMasterUp       = new Vector();
Vector vet_calctipoparametrosTabelaMasterUp   = new Vector();
Vector vet_calcvalorparametrosTabelaMasterUp  = new Vector();
Vector vet_calcnomeparametrosTabelaMasterUp   = new Vector();

Vector vet_tipoparametrosTabelaUp         = new Vector();
Vector vet_colunaparametrosTabelaUp       = new Vector();
Vector vet_nomeparametrosTabelaUp         = new Vector();
Vector vet_isCheckparametrosTabelaUp      = new Vector();
Vector vet_valueUncheckparametrosTabelaUp = new Vector();
Vector vet_calctipoparametrosTabelaUp     = new Vector();
Vector vet_calccolunaparametrosTabelaUp   = new Vector();
Vector vet_calcnomeparametrosTabelaUp     = new Vector();

public String numeroTabeladetalhe = "";

   public void removeParametrosTabelaIns() {
      vet_tipoparametrosTabelaMasterIns.removeAllElements();
      vet_valorparametrosTabelaMasterIns.removeAllElements();
      vet_nomeparametrosTabelaMasterIns.removeAllElements();
      vet_calctipoparametrosTabelaMasterIns.removeAllElements();
      vet_calcvalorparametrosTabelaMasterIns.removeAllElements();
      vet_calcnomeparametrosTabelaMasterIns.removeAllElements();

      vet_tipoparametrosTabelaIns.removeAllElements();
      vet_colunaparametrosTabelaIns.removeAllElements();
      vet_nomeparametrosTabelaIns.removeAllElements();
      vet_isCheckparametrosTabelaIns.removeAllElements();
      vet_valueUncheckparametrosTabelaIns.removeAllElements();
      vet_calctipoparametrosTabelaIns.removeAllElements();
      vet_calccolunaparametrosTabelaIns.removeAllElements();
      vet_calcnomeparametrosTabelaIns.removeAllElements();
   }
   public void addParametroTabelaMasterIns(String tipo, Object valor, String nome) {
      vet_tipoparametrosTabelaMasterIns.addElement(tipo);
      vet_valorparametrosTabelaMasterIns.addElement(valor);
      vet_nomeparametrosTabelaMasterIns.addElement(nome);
   }
   public void addParametroCalculadoTabelaMasterIns(String tipo, Object valor, String nome) {
      vet_calctipoparametrosTabelaMasterIns.addElement(tipo);
      vet_calcvalorparametrosTabelaMasterIns.addElement(valor);
      vet_calcnomeparametrosTabelaMasterIns.addElement(nome);
   }
   public void addParametroTabelaIns(String tipo, String coluna, String nome) {
      vet_tipoparametrosTabelaIns.addElement(tipo);
      vet_colunaparametrosTabelaIns.addElement(coluna);
      vet_nomeparametrosTabelaIns.addElement(nome);
      vet_isCheckparametrosTabelaIns.addElement("N");
      vet_valueUncheckparametrosTabelaIns.addElement("");
   }
   public void addParametroCheckTabelaIns(String tipo, String coluna, String nome, String unchecked) {
      vet_tipoparametrosTabelaIns.addElement(tipo);
      vet_colunaparametrosTabelaIns.addElement(coluna);
      vet_nomeparametrosTabelaIns.addElement(nome);
      vet_isCheckparametrosTabelaIns.addElement("S");
      vet_valueUncheckparametrosTabelaIns.addElement(unchecked);
   }
   public void addParametroCalculadoTabelaIns(String tipo, String coluna, String nome) {
      vet_calctipoparametrosTabelaIns.addElement(tipo);
      vet_calccolunaparametrosTabelaIns.addElement(coluna);
      vet_calcnomeparametrosTabelaIns.addElement(nome);
   }

   public void removeParametrosTabelaUp() {
      vet_tipoparametrosTabelaMasterUp.removeAllElements();
      vet_valorparametrosTabelaMasterUp.removeAllElements();
      vet_nomeparametrosTabelaMasterUp.removeAllElements();
      vet_calctipoparametrosTabelaMasterUp.removeAllElements();
      vet_calcvalorparametrosTabelaMasterUp.removeAllElements();
      vet_calcnomeparametrosTabelaMasterUp.removeAllElements();

      vet_tipoparametrosTabelaUp.removeAllElements();
      vet_colunaparametrosTabelaUp.removeAllElements();
      vet_nomeparametrosTabelaUp.removeAllElements();
      vet_isCheckparametrosTabelaUp.removeAllElements();
      vet_valueUncheckparametrosTabelaUp.removeAllElements();
      vet_calctipoparametrosTabelaUp.removeAllElements();
      vet_calccolunaparametrosTabelaUp.removeAllElements();
      vet_calcnomeparametrosTabelaUp.removeAllElements();
   }
   //comentado por falta de utilizacao. Caso venha a ser utilizado, descomentar...
   //public void addParametroTabelaMasterUp(String tipo, Object valor, String nome) {
   //   vet_tipoparametrosTabelaMasterUp.addElement(tipo);
   //   vet_valorparametrosTabelaMasterUp.addElement(valor);
   //   vet_nomeparametrosTabelaMasterUp.addElement(nome);
   //}
   public void addParametroCalculadoTabelaMasterUp(String tipo, Object valor, String nome) {
      vet_calctipoparametrosTabelaMasterUp.addElement(tipo);
      vet_calcvalorparametrosTabelaMasterUp.addElement(valor);
      vet_calcnomeparametrosTabelaMasterUp.addElement(nome);
   }
   public void addParametroTabelaUp(String tipo, String coluna, String nome) {
      vet_tipoparametrosTabelaUp.addElement(tipo);
      vet_colunaparametrosTabelaUp.addElement(coluna);
      vet_nomeparametrosTabelaUp.addElement(nome);
      vet_isCheckparametrosTabelaUp.addElement("N");
      vet_valueUncheckparametrosTabelaUp.addElement("");
   }
   public void addParametroCheckTabelaUp(String tipo, String coluna, String nome, String unchecked) {
      vet_tipoparametrosTabelaUp.addElement(tipo);
      vet_colunaparametrosTabelaUp.addElement(coluna);
      vet_nomeparametrosTabelaUp.addElement(nome);
      vet_isCheckparametrosTabelaUp.addElement("S");
      vet_valueUncheckparametrosTabelaUp.addElement(unchecked);
   }
   public void addParametroCalculadoTabelaUp(String tipo, String coluna, String nome) {
      vet_calctipoparametrosTabelaUp.addElement(tipo);
      vet_calccolunaparametrosTabelaUp.addElement(coluna);
      vet_calcnomeparametrosTabelaUp.addElement(nome);
   }

   public void preparaBackupArrays() {
      ht_deletados_backup.clear();
      ht_atualizados_backup.clear();
      ht_inseridos_backup.clear();
      Object elemento = new Object();
      for(Enumeration el = ht_inseridos.keys(); el.hasMoreElements();)
      {  elemento = el.nextElement();
         ht_inseridos_backup.put( elemento, ht_inseridos.get(elemento) );
      }
      for(Enumeration el = ht_atualizados.keys(); el.hasMoreElements();)
      {  elemento = el.nextElement();
         ht_atualizados_backup.put( elemento, ht_atualizados.get(elemento) );
      }
      for(Enumeration el = ht_deletados.keys(); el.hasMoreElements();)
      {  elemento = el.nextElement();
         ht_deletados_backup.put( elemento, ht_deletados.get(elemento) );
      }
   }
   public void restauraBackupArrays() {
      ht_deletados.clear();
      ht_atualizados.clear();
      ht_inseridos.clear();
      Object elemento = new Object();
      for(Enumeration el = ht_inseridos_backup.keys(); el.hasMoreElements();)
      {  elemento = el.nextElement();
         ht_inseridos.put( elemento, ht_inseridos_backup.get(elemento) );
      }
      for(Enumeration el = ht_atualizados_backup.keys(); el.hasMoreElements();)
      {  elemento = el.nextElement();
         ht_atualizados.put( elemento, ht_atualizados_backup.get(elemento) );
      }
      for(Enumeration el = ht_deletados_backup.keys(); el.hasMoreElements();)
      {  elemento = el.nextElement();
         ht_deletados.put( elemento, ht_deletados_backup.get(elemento) );
      }
   }

   //public void executeTabela(HttpJsp pagina)
   public boolean executeTabela(HttpServletRequest  request,
                                HttpServletResponse response,
                                HttpSession         session,
                                boolean so_array, boolean excluitudo)
          throws SQLException, java.sql.SQLException, Exception
   {
      return internalExecuteTabela(request, response, session, so_array, excluitudo);
   }
   public boolean executeTabela(HttpServletRequest  request,
                                HttpServletResponse response,
                                HttpSession         session)
          throws SQLException, java.sql.SQLException, Exception
   {
      return internalExecuteTabela(request, response, session, false, false);
   }
   public boolean executeTabela(HttpServletRequest  request,
                                HttpServletResponse response,
                                HttpSession         session,
                                boolean so_array)
          throws SQLException, java.sql.SQLException, Exception
   {
      return internalExecuteTabela(request, response, session, so_array, false);
   }
   public boolean internalExecuteTabela(HttpServletRequest  request,
                                HttpServletResponse response,
                                HttpSession         session,
                                boolean so_array, boolean excluitudo)
          throws SQLException, java.sql.SQLException, Exception
   {
      //efetuando backup dos arrays. Caso d� algum erro, eles
      // ser�o restaurados dentro do catch().
      preparaBackupArrays();

      boolean   isNewRow = false;
      String    sNewRow = "";
      int totalLinhas = Integer.parseInt(request.getParameter("TOTALLINHAS" + numeroTabeladetalhe));
      boolean apagar = false;
      boolean inserir = false;
      boolean alterar = false;
      int numLinhasApagadas = 0;
      String request_rowid = "";

  if(! excluitudo)
  {
   // in�cio do FOR para processar as inser��es/altera��es:
   for (int nIndex=0; nIndex < totalLinhas; nIndex++)
   {
      isNewRow = false;
      apagar = false;
      inserir = false;
      alterar = false;
      sNewRow = request.getParameter("_ISNEWROW" + numeroTabeladetalhe + nIndex);
      if (sNewRow != null && sNewRow.equalsIgnoreCase("true"))
      {
         isNewRow = true;
      }

      if (request.getParameter("APAGAR" + numeroTabeladetalhe + nIndex) != null)
      {
         if (request.getParameter("APAGAR" + numeroTabeladetalhe + nIndex).equals("S"))
         {   apagar = true;
         }
         else
            apagar = false;
      }
      else
        apagar = false;

      if (apagar)
      {  // n�o faz nada, pois nessa primeira passagem do FOR eu processo
         // apenas as inser��es/altera��es
         apagar = true;
      }
      else
      {
        if (request.getParameter("STATUS" + numeroTabeladetalhe + nIndex).equals("IA"))
           inserir = true;
        if (request.getParameter("STATUS" + numeroTabeladetalhe + nIndex).equals("A"))
           alterar = true;

        if(request.getParameter("reg" + numeroTabeladetalhe + nIndex).equals("0"))
        {  //est� no registro n�mero 0, ent�o n�o devo fazer nada...
           inserir = false;
           alterar = false;
        }

        if (inserir || alterar)
        {
          try
          {
             // goto the appropriate row
             Integer   RowNumber;
             String    sName;
             String    sNewValue;
             Object    OldValue;

             if (alterar)
             {
                absolute( Integer.parseInt(request.getParameter("reg" + numeroTabeladetalhe + nIndex)) );
//System.out.println("absolute=" + request.getParameter("reg" + numeroTabeladetalhe + nIndex) + ".");

                //verificando se o Types.ROWID � v�lido:
                request_rowid = request.getParameter("CMROWID" + numeroTabeladetalhe + nIndex);
                if ((request_rowid != null) &&
                    (! request_rowid.equals("")))
                {  
//System.out.println("request_rowid=" + request_rowid + ".");
//System.out.println("getColuna(\"CMROWID\")=" + "" + getColuna("CMROWID") + ".");
                   if(! request_rowid.equalsIgnoreCase("" + getColuna("CMROWID")))
                   {  //restaurando os arrays atraves do backup efetuado no inicio do processamento.
                      restauraBackupArrays();
                      try { iconexaobean.rollback(); }
                      catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(10):" + new java.util.Date() + " " + ignored + "." ); }
                      response.sendRedirect(request.getSession().getAttribute("COM_RN") + "jsp/com_referererror.jsp?e=nav");
                      return false;
                   }
                }

                removeParametrosUp();
                for (int i=0; i < vet_tipoparametrosTabelaMasterUp.size(); i++)
                {  addParametroUp(vet_tipoparametrosTabelaMasterUp.elementAt(i) + "",
                                  vet_valorparametrosTabelaMasterUp.elementAt(i),
                                  "" + vet_nomeparametrosTabelaMasterUp.elementAt(i) );
                }
                for (int i=0; i < vet_calctipoparametrosTabelaMasterUp.size(); i++)
                {  addParametroCalculadoUp(vet_calctipoparametrosTabelaMasterUp.elementAt(i) + "",
                                  vet_calcvalorparametrosTabelaMasterUp.elementAt(i),
                                  "" + vet_calcnomeparametrosTabelaMasterUp.elementAt(i) );
                }

                for (int i=0; i < vet_tipoparametrosTabelaUp.size(); i++)
                {
                   if(vet_isCheckparametrosTabelaUp.elementAt(i).toString().equals("S"))
                   {  if(request.getParameter("" + vet_colunaparametrosTabelaUp.elementAt(i) + numeroTabeladetalhe + nIndex) == null)
                      {  addParametroUp(vet_tipoparametrosTabelaUp.elementAt(i) + "",
                                  vet_valueUncheckparametrosTabelaUp.elementAt(i) + "",
                                  "" + vet_nomeparametrosTabelaUp.elementAt(i) );
                      }
                      else
                      {  addParametroUp(vet_tipoparametrosTabelaUp.elementAt(i) + "",
                                  request.getParameter("" + vet_colunaparametrosTabelaUp.elementAt(i) + numeroTabeladetalhe + nIndex),
                                  "" + vet_nomeparametrosTabelaUp.elementAt(i) );
                      }
                   }
                   else
                   {  addParametroUp(vet_tipoparametrosTabelaUp.elementAt(i) + "",
                                  request.getParameter("" + vet_colunaparametrosTabelaUp.elementAt(i) + numeroTabeladetalhe + nIndex),
                                  "" + vet_nomeparametrosTabelaUp.elementAt(i) );
                   }
                }
                for (int i=0; i < vet_calctipoparametrosTabelaUp.size(); i++)
                {  addParametroCalculadoUp(vet_calctipoparametrosTabelaUp.elementAt(i) + "",
                                  request.getParameter("" + vet_calccolunaparametrosTabelaUp.elementAt(i) + numeroTabeladetalhe + nIndex),
                                  "" + vet_calcnomeparametrosTabelaUp.elementAt(i) );
                }
                if(so_array)
                {  if(! atualizar_so_array(request, response, session, request.getParameter("CMROWID" + numeroTabeladetalhe + nIndex)))
                   {  //restaurando os arrays atraves do backup efetuado no inicio do processamento.
                      restauraBackupArrays();
                      try { iconexaobean.rollback(); }
                      catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(11):" + new java.util.Date() + " " + ignored + "." ); }
                      return false;
                   }
                }
                else
                {  if(! atualizar(request, response, session, request.getParameter("CMROWID" + numeroTabeladetalhe + nIndex)))
                   {  //restaurando os arrays atraves do backup efetuado no inicio do processamento.
                      restauraBackupArrays();
                      try { iconexaobean.rollback(); }
                      catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(12):" + new java.util.Date() + " " + ignored + "." ); }
                      return false;
                   }
                }
             }
             if (inserir)
             {
                removeParametrosIns();
                for (int i=0; i < vet_tipoparametrosTabelaMasterIns.size(); i++)
                {  addParametroIns(vet_tipoparametrosTabelaMasterIns.elementAt(i) + "",
                                   vet_valorparametrosTabelaMasterIns.elementAt(i),
                                   "" + vet_nomeparametrosTabelaMasterIns.elementAt(i) );
                }
                for (int i=0; i < vet_calctipoparametrosTabelaMasterIns.size(); i++)
                {  addParametroCalculadoIns(vet_calctipoparametrosTabelaMasterIns.elementAt(i) + "",
                                   vet_calcvalorparametrosTabelaMasterIns.elementAt(i),
                                   "" + vet_calcnomeparametrosTabelaMasterIns.elementAt(i) );
                }

                for (int i=0; i < vet_tipoparametrosTabelaIns.size(); i++)
                {  if(vet_isCheckparametrosTabelaIns.elementAt(i).toString().equals("S"))
                   {  if(request.getParameter("" + vet_colunaparametrosTabelaIns.elementAt(i) + numeroTabeladetalhe + nIndex) == null)
                      {  addParametroIns(vet_tipoparametrosTabelaIns.elementAt(i) + "",
                                  vet_valueUncheckparametrosTabelaIns.elementAt(i) + "",
                                  "" + vet_nomeparametrosTabelaIns.elementAt(i) );
                      }
                      else
                      {  addParametroIns(vet_tipoparametrosTabelaIns.elementAt(i) + "",
                                  request.getParameter("" + vet_colunaparametrosTabelaIns.elementAt(i) + numeroTabeladetalhe + nIndex),
                                  "" + vet_nomeparametrosTabelaIns.elementAt(i) );
                      }
                   }
                   else
                   {  addParametroIns(vet_tipoparametrosTabelaIns.elementAt(i) + "",
                                  request.getParameter("" + vet_colunaparametrosTabelaIns.elementAt(i) + numeroTabeladetalhe + nIndex),
                                  "" + vet_nomeparametrosTabelaIns.elementAt(i) );
                   }
                }
                for (int i=0; i < vet_calctipoparametrosTabelaIns.size(); i++)
                {  addParametroCalculadoIns(vet_calctipoparametrosTabelaIns.elementAt(i) + "",
                                   request.getParameter("" + vet_calccolunaparametrosTabelaIns.elementAt(i) + numeroTabeladetalhe + nIndex),
                                   "" + vet_calcnomeparametrosTabelaIns.elementAt(i) );
                }
                if(so_array)
                { if(! inserir_so_array(request, response, session))
                  {  //restaurando os arrays atraves do backup efetuado no inicio do processamento.
                     restauraBackupArrays();
                     try { iconexaobean.rollback(); }
                     catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(13):" + new java.util.Date() + " " + ignored + "." ); }
                     return false;
                  }
                }
                else
                { if(! inserir(request, response, session))
                  {  //restaurando os arrays atraves do backup efetuado no inicio do processamento.
                     restauraBackupArrays();
                     try { iconexaobean.rollback(); }
                     catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(14):" + new java.util.Date() + " " + ignored + "." ); }
                     return false;
                  }
                }
             }

          }
          catch (SQLException osqlex)
          {
             //restaurando os arrays atraves do backup efetuado no inicio do processamento.
             restauraBackupArrays();
             try { iconexaobean.rollback(); }
             catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(15):" + new java.util.Date() + " " + ignored + "." ); }
             throw osqlex;
             //return false;
          }
          catch (Exception ex)
          {
             //restaurando os arrays atraves do backup efetuado no inicio do processamento.
             restauraBackupArrays();
             try { iconexaobean.rollback(); }
             catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(17):" + new java.util.Date() + " " + ignored + "." ); }
             throw ex;
             //return false;
          }
        } // fim do if (inserir || alterar)
      } // fim else do if (apagar)
   } // fim do for de inser��es/altera��es (int i=0; i<; i++)
  } // fim if ! excluitudo

   // in�cio do FOR para processar as exclus�es:
   for (int nIndex=0; nIndex < totalLinhas; nIndex++)
   {
      isNewRow = false;
      apagar = false;
      inserir = false;
      alterar = false;
      sNewRow = request.getParameter("_ISNEWROW" + numeroTabeladetalhe + nIndex);
      if (sNewRow != null && sNewRow.equalsIgnoreCase("true"))
      {
         isNewRow = true;
      }

      if(excluitudo)
      {  apagar = true;
      }
      else
      {  if (request.getParameter("APAGAR" + numeroTabeladetalhe + nIndex) != null)
         {  if (request.getParameter("APAGAR" + numeroTabeladetalhe + nIndex).equals("S"))
            {   apagar = true;
            }
            else
               apagar = false;
         }
         else
           apagar = false;
      }

      if (apagar)
      {
         if(! isNewRow)
         {
// inicio delete ----------------------------------------------
            try
            {
               absolute( Integer.parseInt(request.getParameter("reg" + numeroTabeladetalhe + nIndex)) );

               if(! excluitudo)
               {
                  //verificando se o ROWID � v�lido:
                  request_rowid = request.getParameter("CMROWID" + numeroTabeladetalhe + nIndex);
                  if ((request_rowid != null) &&
                      (! request_rowid.equals("")))
                  {  if(! request_rowid.equalsIgnoreCase("" + getColuna("CMROWID")))
                     {  //restaurando os arrays atraves do backup efetuado no inicio do processamento.
                        restauraBackupArrays();
                        try { iconexaobean.rollback(); }
                        catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(18):" + new java.util.Date() + " " + ignored + "." ); }
                        response.sendRedirect(request.getSession().getAttribute("COM_RN") + "jsp/com_referererror.jsp?e=nav");
                        return false;
                     }
                  }
               }
               if(so_array)
               { if(! deletar_so_array(request, response, session, ""+getColuna("CMROWID")))
                 {   //restaurando os arrays atraves do backup efetuado no inicio do processamento.
                     restauraBackupArrays();
                     try { iconexaobean.rollback(); }
                     catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(19):" + new java.util.Date() + " " + ignored + "." ); }
                     return false;
                 }
               }
               else
               { if(! deletar(request, response, session, ""+getColuna("CMROWID", true)))
                 {   //restaurando os arrays atraves do backup efetuado no inicio do processamento.
                     restauraBackupArrays();
                     try { iconexaobean.rollback(); }
                     catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(20):" + new java.util.Date() + " " + ignored + "." ); }
                     return false;
                 }
               }
               numLinhasApagadas++;
            }
            catch (SQLException osqlexd)
            {
             //restaurando os arrays atraves do backup efetuado no inicio do processamento.
             restauraBackupArrays();
             try { iconexaobean.rollback(); }
             catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(21):" + new java.util.Date() + " " + ignored + "." ); }
             throw osqlexd;
             //return false;
            }
            catch (Exception exd)
            {
             //restaurando os arrays atraves do backup efetuado no inicio do processamento.
             restauraBackupArrays();
             try { iconexaobean.rollback(); }
             catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(23):" + new java.util.Date() + " " + ignored + "." ); }
             throw exd;
             //return false;
            }
// fim delete -------------------------------------------------
         }
      }
      // else: n�o � apagar ent�o n�o faz nada, pois j� processei as inser��es/altera��es acima
   } // fim do for de exclus�o (int i=0; i<; i++)
   return true;
 } // fim funcao

   public boolean executeTabelaBatch(HttpServletRequest  request,
                                HttpServletResponse response,
                                HttpSession         session)
          throws SQLException, java.sql.SQLException, Exception
   {
      //efetuando backup dos arrays. Caso d� algum erro, eles
      // ser�o restaurados dentro do catch().
      preparaBackupArrays();

      boolean   isNewRow = false;
      String    sNewRow = "";
      //int totalLinhas = Integer.parseInt(request.getParameter("TOTALLINHAS" + numeroTabeladetalhe));

      boolean apagar = false;
      boolean inserir = false;
      boolean alterar = false;
      int numLinhasApagadas = 0;
      String request_rowid = "";
      int nIndex=0;

      Vector varre_inseridos = new Vector();
      int posinseriraux = 0;
      String chave = "";
      for(Enumeration el = ht_inseridos.keys(); el.hasMoreElements();)
      {  chave = "" + el.nextElement();
         if(chave.indexOf("NUMERO REGISTRO INSERIDO") != -1)
         {  posinseriraux = Integer.parseInt( chave.substring(0, chave.indexOf("NUMERO REGISTRO INSERIDO")) );
            varre_inseridos.addElement("" + posinseriraux);
         }
      }

   // in�cio do FOR para processar as inser��es:
   //for (nIndex=0; nIndex < ht_inseridos.size(); nIndex++)
   for (nIndex=0; nIndex < varre_inseridos.size(); nIndex++)
   {
      isNewRow = false;
      apagar = false;
      inserir = false;
      alterar = false;

      //regAtual = Integer.parseInt(ht_inseridos.get(regAtual + "NUMERO REGISTRO INSERIDO"));
      regAtual = Integer.parseInt("" + varre_inseridos.elementAt(nIndex));
      //absolute( Integer.parseInt(ht_inseridos.get(regAtual + "NUMERO REGISTRO INSERIDO")) );
      if (verificaDeletado())
         apagar = true;

      if (apagar)
      {  // n�o faz nada, pois nessa primeira passagem do FOR eu processo apenas as inser��es/altera��es
         apagar = true;
      }
      else
      {
        if((ht_atualizados.containsKey( regAtual + "SALVOU BANCO" )) &&
           (ht_atualizados.get( regAtual + "SALVOU BANCO" ).toString().equals(regAtual + "N")))
           alterar = true;
        if((ht_inseridos.containsKey( regAtual + "SALVOU BANCO" )) &&
           (ht_inseridos.get( regAtual + "SALVOU BANCO" ).toString().equals(regAtual + "N")))
           inserir = true;

        if (inserir || alterar)
        {
          try
          {
             // goto the appropriate row
             Integer   RowNumber;
             String    sName;
             String    sNewValue;
             Object    OldValue;

             //if (inserir)
             //{
                removeParametrosIns();
                for(int i=0; i < vet_tipoparametrosInsBatch.size(); i++) {
                   //addParametroIns(vet_tipoparametrosInsBatch.elementAt(i) + "",
                   //                ht_inseridos.get(regAtual + "" + vet_nomeparametrosInsBatch.elementAt(i)),
                   //                "" + vet_nomeparametrosInsBatch.elementAt(i) );
                   addParametroIns(vet_tipoparametrosInsBatch.elementAt(i) + "",
                                   getColuna("" + vet_nomeparametrosInsBatch.elementAt(i)),
                                   "" + vet_nomeparametrosInsBatch.elementAt(i) );
                }
                for(int i=0; i < vet_calctipoparametrosInsBatch.size(); i++) {
                   addParametroCalculadoIns(vet_calctipoparametrosInsBatch.elementAt(i) + "",
                                   getColuna("" + vet_calcnomeparametrosInsBatch.elementAt(i)),
                                   "" + vet_calcnomeparametrosInsBatch.elementAt(i) );
                }

                if(! inserir(request, response, session))
                {
                   //restaurando os arrays atraves do backup efetuado no inicio do processamento.
                   restauraBackupArrays();
                   try { iconexaobean.rollback(); }
                   catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(24):" + new java.util.Date() + " " + ignored + "." ); }
                   return false;
                }
             //}

          }
          catch (SQLException osqlex)
          {  //restaurando os arrays atraves do backup efetuado no inicio do processamento.
             restauraBackupArrays();
             try { iconexaobean.rollback(); }
             catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(25):" + new java.util.Date() + " " + ignored + "." ); }
             throw osqlex;
             //return false;
          }
          catch (Exception ex)
          {  //restaurando os arrays atraves do backup efetuado no inicio do processamento.
             restauraBackupArrays();
             try { iconexaobean.rollback(); }
             catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(27):" + new java.util.Date() + " " + ignored + "." ); }
             throw ex;
             //return false;
          }
        } // fim do if (inserir || alterar)
      } // fim else do if (apagar)
   } // fim do for de inser��es (int i=0; i<; i++)

      Vector varre_alterados = new Vector();
      posinseriraux = 0;
      for(Enumeration el = ht_atualizados.keys(); el.hasMoreElements();)
      {  chave = "" + el.nextElement();
         if(chave.indexOf("NUMERO REGISTRO ALTERADO") != -1)
         {  posinseriraux = Integer.parseInt( chave.substring(0, chave.indexOf("NUMERO REGISTRO ALTERADO")) );
            varre_alterados.addElement("" + posinseriraux);
         }
      }

   // in�cio do FOR para processar as altera��es:
   //for (nIndex=0; nIndex < ht_atualizados.size(); nIndex++)
   for (nIndex=0; nIndex < varre_alterados.size(); nIndex++)
   {
      isNewRow = false;
      apagar = false;
      inserir = false;
      alterar = false;

      //regAtual = Integer.parseInt(ht_atualizados.get(regAtual + "NUMERO REGISTRO ALTERADO"));
      regAtual = Integer.parseInt("" + varre_alterados.elementAt(nIndex));
      //absolute( );

      if (verificaDeletado())
         apagar = true;

      if (apagar)
      {  // n�o faz nada, pois nessa primeira passagem do FOR eu processo apenas as inser��es/altera��es
         apagar = true;
      }
      else
      {

        if((ht_atualizados.containsKey( regAtual + "SALVOU BANCO" )) &&
           (ht_atualizados.get( regAtual + "SALVOU BANCO" ).toString().equals(regAtual + "N")))
           alterar = true;
        if((ht_inseridos.containsKey( regAtual + "SALVOU BANCO" )) &&
           (ht_inseridos.get( regAtual + "SALVOU BANCO" ).toString().equals(regAtual + "N")))
           inserir = true;

        if ((! inserir) && alterar)
        {
          try
          {
             // goto the appropriate row
             Integer   RowNumber;
             String    sName;
             String    sNewValue;
             Object    OldValue;

             //if (alterar)
             //{

                removeParametrosUp();
                for (int i=0; i < vet_tipoparametrosUpBatch.size(); i++)
                {  //addParametroUp(vet_tipoparametrosUpBatch.elementAt(i) + "",
                   //                ht_atualizados.get(regAtual + "" + vet_nomeparametrosUpBatch.elementAt(i)),
                   //                "" + vet_nomeparametrosUpBatch.elementAt(i) );
                   addParametroUp(vet_tipoparametrosUpBatch.elementAt(i) + "",
                                   getColuna("" + vet_nomeparametrosUpBatch.elementAt(i)),
                                   "" + vet_nomeparametrosUpBatch.elementAt(i) );
                }
                for (int i=0; i < vet_calctipoparametrosUpBatch.size(); i++)
                {  addParametroCalculadoUp(vet_calctipoparametrosUpBatch.elementAt(i) + "",
                                   getColuna("" + vet_calcnomeparametrosUpBatch.elementAt(i)),
                                   "" + vet_calcnomeparametrosUpBatch.elementAt(i) );
                }

                if(! atualizar(request, response, session, ""))
                {
                   //restaurando os arrays atraves do backup efetuado no inicio do processamento.
                   restauraBackupArrays();
                   try { iconexaobean.rollback(); }
                   catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(28):" + new java.util.Date() + " " + ignored + "." ); }
                   return false;
                }
             //}
          }
          catch (SQLException osqlex)
          {  //restaurando os arrays atraves do backup efetuado no inicio do processamento.
             restauraBackupArrays();
             try { iconexaobean.rollback(); }
             catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(29):" + new java.util.Date() + " " + ignored + "." ); }
             throw osqlex;
             //return false;
          }
          catch (Exception ex)
          {  //restaurando os arrays atraves do backup efetuado no inicio do processamento.
             restauraBackupArrays();
             try { iconexaobean.rollback(); }
             catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(31):" + new java.util.Date() + " " + ignored + "." ); }
             throw ex;
             //return false;
          }
        } // fim do if (inserir || alterar)
      } // fim else do if (apagar)
   } // fim do for de altera��es (int i=0; i<; i++)

      Vector varre_excluidos = new Vector();
      posinseriraux = 0;
      for(Enumeration el = ht_deletados.keys(); el.hasMoreElements();)
      {  chave = "" + el.nextElement();
         if(chave.indexOf("NUMERO REGISTRO DELETADO") != -1)
         {  posinseriraux = Integer.parseInt( chave.substring(0, chave.indexOf("NUMERO REGISTRO DELETADO")) );
            varre_excluidos.addElement("" + posinseriraux);
         }
      }

   // in�cio do FOR para processar as exclus�es:
   for (nIndex=0; nIndex < varre_excluidos.size(); nIndex++)
   {
      isNewRow = false;
      apagar = false;
      inserir = false;
      alterar = false;

      //regAtual = Integer.parseInt(ht_deletados.get(regAtual + "NUMERO REGISTRO DELETADO"));
      regAtual = Integer.parseInt("" + varre_excluidos.elementAt(nIndex));
      //absolute( Integer.parseInt(ht_deletados.get(regAtual + "NUMERO REGISTRO DELETADO")) );

      if((ht_atualizados.containsKey( regAtual + "SALVOU BANCO" )) &&
         (ht_atualizados.get( regAtual + "SALVOU BANCO" ).toString().equals(regAtual + "N")))
         alterar = true;
      if((ht_inseridos.containsKey( regAtual + "SALVOU BANCO" )) &&
         (ht_inseridos.get( regAtual + "SALVOU BANCO" ).toString().equals(regAtual + "N")))
         inserir = true;
      if((ht_deletados.containsKey( regAtual + "SALVOU BANCO" )) &&
         (ht_deletados.get( regAtual + "SALVOU BANCO" ).toString().equals(regAtual + "N")))
         apagar = true;

      if ((apagar) && (! inserir))
      {
// inicio delete ----------------------------------------------
            try
            {
               //verificar se o ROWID � v�lido
               if(! deletar(request, response, session, "" + getColuna("CMROWID", true)))
               {
                   //restaurando os arrays atraves do backup efetuado no inicio do processamento.
                   restauraBackupArrays();
                   try { iconexaobean.rollback(); }
                   catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(32):" + new java.util.Date() + " " + ignored + "." ); }
                   return false;
               }
               numLinhasApagadas++;
            }
            catch (SQLException osqlexd)
            {
             //restaurando os arrays atraves do backup efetuado no inicio do processamento.
             restauraBackupArrays();
             try { iconexaobean.rollback(); }
             catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(33):" + new java.util.Date() + " " + ignored + "." ); }
             throw osqlexd;
             //return false;
            }
            catch (Exception exd)
            {
             //restaurando os arrays atraves do backup efetuado no inicio do processamento.
             restauraBackupArrays();
             try { iconexaobean.rollback(); }
             catch (Exception ignored) { System.out.println("cmdbbeanRW: Erro ao efetuar rollback(35):" + new java.util.Date() + " " + ignored + "." ); }
             throw exd;
             //return false;
            }
// fim delete -------------------------------------------------
      }
      // else: n�o � apagar ent�o n�o faz nada, pois j� processei as inser��es/altera��es acima
   } // fim do for de exclus�o (int i=0; i<; i++)
   return true;
 } // fim funcao

// ----------------------------------- FIM M�TODOS MULTIROW -------------------------------
} // fim classe
