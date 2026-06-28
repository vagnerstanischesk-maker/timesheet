
package combeans;
import java.util.*;
import javax.servlet.http.*;
/**
 * A Class class.
 * <P>
 * @author Triscal
 */
public class column extends Object {
  private String title;
  private String field;
  private String width;
  private String pattern;
  private String style;
  protected int kind = 0;//0-Default,1-RowId,2-EditBtn,3-DeleteBtn,4-StaticText,5-Input
  protected char align = 'C';
  private String URL;
  private boolean VerificaRO = false;
  private String VerificaROSubstitui = "";
  private String VerificaURLColuna = "";
  private Object VerificaURLValor = null;
  private String VerificaURLSubstitui = "";

  protected ArrayList parameters;
  public boolean addParameter(String newParameterName, String newParameterField){
    parameter par = new parameter();
    par.parameterName = newParameterName;
    par.parameterField = newParameterField;
    return parameters.add(par);
  }
  public void setStyle(String newStyle){
    style = newStyle;
  }
  public String getStyle(){
    return style;
  }
  public void setURL(String newURL){
    URL = newURL;
  }
  public String getURL() {
    return URL;
  }
  public void setVerificaRO(boolean value){
    VerificaRO = value;
  }
  public boolean getVerificaRO() {
    return VerificaRO;
  }
  public void setVerificaROSubstitui(String value){
    VerificaROSubstitui = value;
  }
  public String getVerificaROSubstitui() {
    return VerificaROSubstitui;
  }
  public void setVerificaURL(String pcoluna, Object pvalor, String psubstitui){
    VerificaURLColuna = pcoluna;
    VerificaURLValor  = pvalor;
    VerificaURLSubstitui  = psubstitui;
  }
  public String getVerificaURLColuna() {
    return VerificaURLColuna;
  }
  public Object getVerificaURLValor() {
    return VerificaURLValor;
  }
  public String getVerificaURLSubstitui() {
    return VerificaURLSubstitui;
  }
  public String getField() {
    return field;
  }
  public void setField(String newField) {
    field = newField;
  }
  public String getTitle() {
    return title;
  }
  public String getPattern(){
    return pattern;
  }
  public void setPattern(String newPattern){
    pattern = newPattern;
  }
  public void setTitle(String newTitle) {
    title = newTitle;
  }
  public String getWidth() {
    return width;
  }
  public void setWidth(String newWidth) {
    width = newWidth;
  }
  /*
  public void alignLeft(){
    align = 'L';
  }
  public void alignCenter(){
    align = 'C';
  }
  public void alignRight(){
    align = 'R';
  }
  */
  public void setAlign(char newAlign){
    align = newAlign;
  }
  public column(){
    parameters = new ArrayList();
  }
  public column(String newTitle, String newField, String newWidth, String newURL){
    this();
    setTitle(newTitle);
    setField(newField);
    setWidth(newWidth);
    setURL(newURL);
    setVerificaRO(false);
    setVerificaROSubstitui("");
    setVerificaURL("", null, "");
  }
  public column(String newTitle, String newField, String newWidth, String newURL, boolean newVerificaRO, String psubstitui){
    this();
    setTitle(newTitle);
    setField(newField);
    setWidth(newWidth);
    setURL(newURL);
    setVerificaRO(newVerificaRO);
    setVerificaROSubstitui(psubstitui);
    setVerificaURL("", null, "");
  }
}
