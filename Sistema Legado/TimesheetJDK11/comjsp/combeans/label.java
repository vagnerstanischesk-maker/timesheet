
package combeans;
import javax.servlet.http.*;

/**
 * A Class class.
 * <P>
 * @author Triscal
 */
public class label extends Object {
  public String text;
  public String align;
  public String cssClasse;
  public String color;
  public String font;
  public String style;
  public String link;
  public String getLabel(){
    String buff="";
    if((text != null) && (text.indexOf("font")>=0))
    {
    }
    else
    {
       buff+= "<font ";
       if (font!=null) buff+="face=\""+font+"\"";
       if (color!=null) buff+="color=\""+color+"\"";
       buff+=">";
    }
    if (link!=null) buff+="<a href = \""+link+"\">";
    buff+=text;
    if (link!=null) buff+="</a>";
    if((text != null) && (text.indexOf("font")>=0))
    {
    }
    else
    {  buff+="</font>";
    }
    return buff;
  }
  public label(String newText,String newAlign,String newcssClasse,String newColor,
               String newFont,String newStyle,String newLink){
    text = newText;
    align = newAlign;
    cssClasse = newcssClasse;
    color = newColor;
    font = newFont;
    style = newStyle;
    link = newLink;
  }

  public label(){
  }

  public static void main(String[] args) {
    label label = new label("<b>Teste</b>",null,null,"#0000FF","Arial",null,"teste.html");
//System.out.println(label.getLabel());
  }
}

