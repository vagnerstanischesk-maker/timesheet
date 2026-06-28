// Copyright (c) 2001 RBS
package combeans;

//import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
//import java.text.*;

public class menuBean2 extends menuBean {

  private String imageDir = "";
  private int seta = 0;
  private String pagAtual = "";
  private int TablesAbertas = 0;
  private String JsForShowItem = "";

  public String getVersao()
  {   return("22/03/2002");
  }
  public void setImageDir( String dir ){
    // Imagens - /portalcom/webapp"+session.getAttribute("COM_DOMINIO")+"/images/cartao.gif
    imageDir = dir;
  }

  public void setPaginaAtual( String pagina ){
    pagAtual = pagina;
  }


 private String abreMenu(){
    TablesAbertas += 2;
    return "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">"+"\n"
         + "		<tr>"+"\n"
         + "		<!--td width=\"10\"><img src=\""+imageDir+"/images/pixel.gif\" width=\"10\" height=\"1\"></td-->"+"\n"
         + "		<td width=\"150\"><img src=\""+imageDir+"/images/pixel.gif\" width=\"150\" height=\"1\"><br>"+"\n"
         + "			<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"150\">"+"\n"; //  135 -> 150
  }

  private String fechaMenu(){
    TablesAbertas -= 2;

    String retorno = " </table>"+"\n"
         + "		</td>"+"\n"
         + "		</tr>"+"\n"
         + "		</table>"+"\n";

   return retorno;

  }


	 /** 23/01/2002 --->> Isaac
   *Método responsável pela geração,
   *do Js que exibe no menu
   *a categoria atual
   */

  private synchronized void setJsForShowItem(int elemento){
  	int	cdgNivel = Integer.parseInt(vet_MENU_NIVEL.elementAt(elemento).toString());
		int cdgMenuPai = Integer.parseInt(vet_MENU_CDG_MENU_PAI.elementAt(elemento).toString());

		JsForShowItem = "<script language=\"javascript\">\n";

		while(cdgNivel > 0){
				try {
       	JsForShowItem += "mostra(idNivel"+cdgNivel+"_idMenuPai"+cdgMenuPai+");\n";
        cdgMenuPai = Integer.parseInt(getMENU_CDG_MENU_PAI(cdgMenuPai+""));
        }catch ( NumberFormatException nfEx){
        	//do nothing
        }
      --cdgNivel;
  	}//end

		JsForShowItem += "</script>";

  }//end  setJsForShowItem


  private String spacer(){
    return "			<tr>"+"\n"
         + "			<td colspan=\"3\"><img src=\""+imageDir+"/images/pixel.gif\" width=\"1\" height=\"11\"></td>"+"\n"
         + "			</tr>"+"\n";
  }

  private String novoNivel( boolean ultimoNivel, int cdgNivel, int cdgMenuPai ){
    TablesAbertas += 2;
    return "			<tr>"+"\n"
         + "			<td colspan=\"3\">"+"\n"
         + "			<span id=\"idNivel"+cdgNivel+"_idMenuPai"+cdgMenuPai+"\" style=\"display:'none'\">"+"\n"
         + "				<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">"+"\n"
         + "				<tr>"+"\n"
         + "				<td width=\""+(ultimoNivel?"17":"18")+"\"><img src=\""+imageDir+"/images/pixel.gif\" width=\""+(ultimoNivel?"17":"18")+"\" height=\"1\"></td>"+"\n"
         + "				<td width=\"1\" bgcolor=\"#660000\" valign=\"bottom\"><img src=\""+imageDir+"/images/pixelazulescuro.gif\" width=\"1\" height=\"6\"></td>"+"\n"
         + "				<td>"+"\n"
         + "					<table border=\"0\" width=\""+(ultimoNivel?"100%":"100%")+"\" cellspacing=\"0\" cellpadding=\"0\">"+"\n" // 125 -> 100%
         + "					<tr><td colspan=\"3\"><img src=\""+imageDir+"/images/pixel.gif\" width=\"1\" height=\"4\"></td></tr>"+"\n";
  }


  private String novoSubItem( String nome, String url, int elemento){
    String classe;
    String link;
 		int cdgNivel = Integer.parseInt(vet_MENU_NIVEL.elementAt(elemento).toString());
   	int cdgMenu = Integer.parseInt(vet_MENU_CDG_MENU.elementAt(elemento).toString());

        if (url.equals("")){
        	if ( possuiFilho(elemento) ){
						   ++cdgNivel;
		        	 link = "href=\"javascript:mostra(idNivel"+cdgNivel+"_idMenuPai"+cdgMenu+")\"";
          }else
           		link = "href=\"javascript:void(0)\"";

          classe = "menuAzul";
        }else{
          if (url.equals(pagAtual)){
            classe = "menuAmarelo";
            setJsForShowItem(elemento);
          }else{
            classe = "menuAzul";
          }//end else

          if(vet_MENU_IN_POPUP.elementAt(elemento).toString().equals("S"))
             link = "href=\"javascript: void menuAbreTela( '"+url+"', 440, 560 )\" onClick=\"return TESTAFNCNAVEGA( '"+url+"' )\"";
          else
             link = "href=\"javascript: void FNCNAVEGA( '"+url+"' )\" onClick=\"return TESTAFNCNAVEGA( '"+url+"' )\"";
        }
        return "			<tr>"+"\n"
             + "					<td width=\"8\"><img src=\""+imageDir+"/images/pixelazul.gif\" width=\"8\" height=\"1\"></td>"+"\n"
             + "					<td width=\"6\"><img src=\""+imageDir+"/images/pixel.gif\" width=\"6\" height=\"1\"></td>"+"\n"
             + "					<td width=\"100%\"><a "+link+" class=\""+classe+"\" onMouseOver=\"window.status='"+nome+"';return true\" onMouseOut=\"window.status='';return true\">"+nome+"</a></td>"+"\n"
             + "					</tr>"+"\n";
  }


  private String fimNivel(){
    TablesAbertas -= 2;
    return "					</table>"+"\n"
         + "				</td>"+"\n"
         + "				</tr>"+"\n"
         + "				</table>"+"\n"
         + "				</span>"+"\n"
         + "			</td>"+"\n"
         + "			</tr>"+"\n";
  }

  private String novoItem( String nome, String url, int elemento){
  	String classe;
    String link;
    seta++;
    String setaFig = "seta_nor.gif";

        if (url.equals("")){

        	if ( possuiFilho(elemento) ){
						int cdgNivel = Integer.parseInt(vet_MENU_NIVEL.elementAt(elemento).toString());
						int cdgMenu = Integer.parseInt(vet_MENU_CDG_MENU.elementAt(elemento).toString());
						++cdgNivel;
          	link = "href=\"javascript:mostra(idNivel"+cdgNivel+"_idMenuPai"+cdgMenu+")\"";
          }else
          	link = "href=\"javascript:void(0)\"";

          classe = "menuAzul";
        }else{
          if (url.equals(pagAtual)){
            setaFig = "seta_vis.gif";
            classe = "menuAmarelo";
          }else{
            setaFig = "seta_nor.gif";
            classe = "menuAzul";
          }//end else
          if(vet_MENU_IN_POPUP.elementAt(elemento).toString().equals("S"))
             link = "href=\"javascript: void menuAbreTela( '"+url+"', 440, 560 )\" onClick=\"return TESTAFNCNAVEGA( '"+url+"' )\"";
          else
             link = "href=\"javascript: void FNCNAVEGA( '"+url+"' )\" onClick=\"return TESTAFNCNAVEGA( '"+url+"' )\" ";
        }//end else

       return "	<tr>"+"\n"
           + "			<td width=\"11\"><img src=\""+imageDir+"/images/layout/"+setaFig+"\" name=\"seta"+ seta +"\"></td>"+"\n"
           + "			<td width=\"7\"><img src=\""+imageDir+"/images/pixel.gif\" width=\"7\" height=\"1\"></td>"+"\n"
           + "			<td width=\"100%\"><a "+link+" class=\""+classe+"\" onMouseOver=\"swapImage('seta"+ seta +"','','"+imageDir+"/images/layout/seta_ovr.gif',1);window.status='"+nome+"';return true\" onMouseOut=\"swapImgRestore();window.status='';return true\">"+nome+"</a></td>"+"\n" // 117 -> 100%
           + "			</tr>"+"\n";
  }//end novoItem


  public void initialize ( String paginaAtual, String imagens ){
    setImageDir( imagens );
    pagAtual = paginaAtual;
    seta = 0;
  }

  public String montaMenuDinamico(HttpSession session,
                                  HttpServletRequest request,
                                  HttpServletResponse response)
  throws Exception{
    String retorno = "";
    String espacos = "";
    int nivel = 0;
    boolean primeiro = true;

    try{
      retorno += abreMenu();
      int elemento = 0;
      for (int i=0; i < vet_ORDEM.size(); i++){
        String nome="";
        String apl="";
        elemento = Integer.parseInt(""+vet_ORDEM.elementAt(i));
        if(vet_MENU_IN_VISIVEL.elementAt(elemento).toString().equals("S")){
          if(!vet_MENU_IN_VOLTAR.elementAt(elemento).toString().equals("S")){
            nome = vet_MENU_DESCR_RESUMIDA.elementAt(elemento)+"";
            if( vet_APP_EXECUTAVEL.elementAt(elemento).toString().equals("")
              || !vet_MENU_IN_BLOQUEADO.elementAt(elemento).toString().equals("N") ){
              apl = "";
            }else{
              apl = UtilBean.montaURL(session, vet_APP_EXECUTAVEL.elementAt(elemento).toString(), "" );
            }

            if (primeiro) {
              primeiro = false;
              if( nivel>0 ){
                retorno += novoSubItem(nome,apl,elemento);
              }else{
                if (elemento>0) retorno += spacer();
	                retorno += novoItem(nome,apl,elemento);
              }
              nivel = Integer.parseInt(vet_MENU_NIVEL.elementAt(elemento)+"");
            }else if (Integer.parseInt(vet_MENU_NIVEL.elementAt(elemento)+"") > nivel){
              if (nivel>0){
                retorno += novoNivel(true, Integer.parseInt(vet_MENU_NIVEL.elementAt(elemento).toString()),  Integer.parseInt(vet_MENU_CDG_MENU_PAI.elementAt(elemento).toString()) );
                //retorno += novoNivel(true, elemento);
              }else{
                retorno += novoNivel(false, Integer.parseInt(vet_MENU_NIVEL.elementAt(elemento).toString()),  Integer.parseInt(vet_MENU_CDG_MENU_PAI.elementAt(elemento).toString()) );
                //retorno += novoNivel(false, elemento);
              }
                retorno += novoSubItem(nome,apl,elemento);
              nivel = Integer.parseInt(vet_MENU_NIVEL.elementAt(elemento)+"");
            }else if (Integer.parseInt(vet_MENU_NIVEL.elementAt(elemento)+"") == nivel){
              if( nivel>0 ){
                	retorno += novoSubItem(nome,apl,elemento);
              }else{
                if (elemento>0) retorno += spacer();
	                retorno += novoItem(nome,apl,elemento);
              }
            }else if (Integer.parseInt(vet_MENU_NIVEL.elementAt(elemento)+"") < nivel){
                 retorno += fimNivel();
              nivel = Integer.parseInt(vet_MENU_NIVEL.elementAt(elemento)+"");
              if (nivel>0){
	                retorno += novoSubItem(nome,apl,elemento);
              }else{
                retorno += spacer();
                retorno += novoItem(nome,apl,elemento);
               }
            }
          }
        }
      }

      retorno += fechaMenu();
      //inicio 01/11
      while (TablesAbertas > 0) {
         //retorno += "</table>";
         retorno += fechaMenu();
      }
      //fim 01/11

      retorno += JsForShowItem;

    }catch (Exception ex) { throw ex; }
      if(retorno.equals(""))
        retorno = "";
      return retorno;
  }
}

