
package combeans;

import java.util.*;
/**
 * A Class class.
 * <P>
 * @author Triscal
 */
public class appGrafico extends Object {

   private String titulo = "";
   private Hashtable ht_grafico = new Hashtable(); //ht_grafico.put(num_reg, value); ht_grafico.get(key);

   public String getTitulo() {
      return titulo;
   }
   public void setTitulo(String pTitulo) {
      titulo = pTitulo;
   }
   public int size() {
      return ht_grafico.size();
   }
   public Object getRepositorio(int pkey) {
      return ht_grafico.get(pkey+"");
   }
   public void putRepositorio(int pkey, Object value) {
      ht_grafico.put(pkey+"", value);
   }
   public Object removeRepositorio(int pkey) {
      return ht_grafico.remove(pkey+"");
   }
   public void limpaRepositorio() {
      ht_grafico.clear();
   }

}

 