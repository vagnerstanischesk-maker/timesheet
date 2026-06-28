/**************************************************************************
Empresa: Triscal
Autor: Christiano Chamma/Alexandre
Data: Janeiro de 2001

Descrição:
      Leitura do Sysdate do banco de dados.

**************************************************************************/
package combeans;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.text.*;
import oracle.jdbc.*; 
import java.sql.*;
import javax.servlet.http.*;

public class sysdateBean {

  public String selectSysdate(Connection pconn)
         throws SQLException
  {
     String sSysdate = "";
     PreparedStatement pstmSYSDATE = null;
     ResultSet rsSYSDATE = null;
     try {
        pstmSYSDATE = (PreparedStatement)pconn.prepareStatement("SELECT TO_CHAR(SYSDATE,'YYYY-MM-DD HH24:MI:SS') FROM DUAL",
                                          ResultSet.TYPE_FORWARD_ONLY,
                                          ResultSet.CONCUR_READ_ONLY);
        rsSYSDATE = (ResultSet)pstmSYSDATE.executeQuery();
        rsSYSDATE.next();
        //sSysdate = "" + rsSYSDATE.getObject(1);
        //sSysdate = "" + rsSYSDATE.getDate(1).timestampValue();
         //sSysdate = "" + rsSYSDATE.getDate(1).toString();
         sSysdate = "" + rsSYSDATE.getString(1);
//System.out.println("<BR>sysdateBean --- 001 - sSysdate=" + sSysdate);
         UtilBean.GeraDebugLogSimples("<BR>sysdateBean --- 001 - sSysdate=" + sSysdate, null, null, true);
     }
     catch (SQLException ex) {
        System.out.println("sysdatebean: ERRO (1):" + new java.util.Date() + "=" + ex + "." );
     }
     catch (Exception ex2) {
        System.out.println("sysdatebean: ERRO (2):" + new java.util.Date() + "=" + ex2 + "." );
     }
     finally {
        if(rsSYSDATE != null) rsSYSDATE.close();
        if(pstmSYSDATE != null) pstmSYSDATE.close();
     }
     return sSysdate;
  }

}

