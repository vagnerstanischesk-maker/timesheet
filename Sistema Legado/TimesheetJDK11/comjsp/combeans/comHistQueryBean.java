
package combeans;
import java.util.*;
import javax.servlet.http.*;
/**
 * @author Triscal
 */
public class comHistQueryBean extends Object {
       // implements HttpSessionBindingListener {

   String PAGINABROWSE = "";
   String SQL = "";
   public Vector vet_tipoparametros    = new Vector();
   public Vector vet_valorparametros   = new Vector();

   //public comHistQueryBean () {
   //   this = new comHistQueryBean();
   //}

   public void addParametro(String tipo, Object valor) {
      vet_tipoparametros.addElement(tipo);
      vet_valorparametros.addElement(valor);
   }
   public void removeParametros() {
      vet_tipoparametros.removeAllElements();
      vet_valorparametros.removeAllElements();
   }

   public void setPaginaBrowse( String value ) {
      PAGINABROWSE = value;
   }
   public String getPaginaBrowse() {
      return PAGINABROWSE;
   }
   public void setSQL( String value ) {
      SQL = value;
   }
   public String getSQL() {
      return SQL;
   }
}
