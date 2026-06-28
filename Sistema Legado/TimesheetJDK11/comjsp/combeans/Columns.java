
package combeans;
import java.util.*;
import javax.servlet.http.*;
/**
 * A Class class.
 * <P>
 * @author Triscal
 */
public class Columns extends Object {
  protected ArrayList items;
  private String evenColor;
  private String oddColor;
  private int borderSize=0;
  private String cssClasse;
  private int spacing=0;
  private boolean alternate = false;
  protected boolean rowIdDefaultColor = true;
  public boolean alternateColors(){
    return alternate;
  }
  public void setAlternateColors(String newEvenColor, String newOddColor){
    evenColor = newEvenColor;
    oddColor = newOddColor;
    alternate = true;
  }
  private column addColumn(String newTitle, String newField, String newWidth, String newURL, int newKind){
    column col = new column();
    col.setTitle(newTitle);
    col.setField(newField);
    col.setWidth(newWidth);
    col.setURL(newURL);
    col.kind = newKind;
    col.setVerificaURL( "", null, "" );
    items.add(col);
    return col;
  }
  public column addColumn(String newTitle, String newField, String newWidth, String newURL){
    return addColumn(newTitle,newField,newWidth,newURL,0);
  }
  public column addIdColumn(String newTitle, String newWidth, boolean defaultColor, String newURL){
    rowIdDefaultColor = defaultColor;
    column col = addColumn(newTitle,"~%RowId%~",newWidth,newURL,1);
    return col;
  }
  public column addDeleteBtnColumn(String newTitle, String newWidth){
    return addColumn(newTitle,"~%DelBtn%~",newWidth,null,3);
  }
  public column addEditBtnColumn(String newTitle, String newWidth){
    return addColumn(newTitle,"~%EditBtn%~",newWidth,null,2);
  }
  public column addStaticColumn(String newTitle, String staticText, String newWidth){
    return addColumn(newTitle,staticText,newWidth,null,4);
  }
  public column addCheckColumn(String newTitle, String checkName, String newWidth){
    return addColumn(newTitle,checkName,newWidth,null,5);
  }
  public Columns(){
    items = new ArrayList();
  }
  public String getEvenColor(){
    return evenColor;
  }
  public String getOddColor(){
    return oddColor;
  }

  public String getcssClasse(){
    if (cssClasse!=null) return " Class=\""+cssClasse+"\"";
    else return "";
  }
  public String getSpacing(){
    return " cellspacing=\""+spacing+"\"";
  }
  public void setBorderSize(int newBorderSize){
    borderSize=newBorderSize;
  }

  public int getBorderSize(){
    return borderSize;
  }
}
