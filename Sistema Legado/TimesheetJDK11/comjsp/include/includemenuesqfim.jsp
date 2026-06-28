<!-- Inicio includemenuesqfim.jsp -->
  </td>
 </tr>
 <tr height="20">
  <td colspan="2">
    <!--| rodapé |-->
     <table bgcolor="#E6E6E6" border="0" width="100%" height="20" cellspacing="0" cellpadding="0">
      <tr>
<% try { 
      if (UtilBean.getJSP(request).equals("TelaInicial.jsp")) { %>
       <td width="75%" align="left" class="txtBlack" style="background-color:#EEEEEE;">&nbsp;
<script>
function checkBrowser(){
 this.ver=navigator.appVersion
 this.dom=document.getElementById?1:0
 this.ie6=(this.ver.indexOf("MSIE 6")>-1 && this.dom)?1:0;
 this.bw=(this.ie6 || this.ie5 || this.ie4 || this.ns4 || this.ns5)
 if(this.bw != 1) {
   //document.write("<b> e <a href=\"http://www.microsoft.com/windows/ie/downloads/critical/ie6sp1/default.asp\" target=\"_blank\"><U>Internet Explorer 6</U></a></b>");
 }
 return this
}
var bw=new checkBrowser();
</script>
       </td>
       <td width="25%" style="background-color:#EEEEEE; font-family:Helvetica,Verdana,Arial; font-size:7pt;" align="right"><a href="#topo">topo</a></td>
<%    } else { %>
       <td width="100%" style="background-color:#EEEEEE; font-family:Helvetica,Verdana,Arial; font-size:7pt;" align="right"><a href="#topo">topo</a></td>
<%    } 
      } catch (Exception exignored) { %>
       <td width="100%" style="background-color:#EEEEEE; font-family:Helvetica,Verdana,Arial; font-size:7pt;" align="right"><a href="#topo">topo</a></td>
<%    } %>
       <td width="5" style="background-color:#EEEEEE; font-family:Helvetica,Verdana,Arial; font-size:7pt;" align="right">&nbsp;</td>
      </tr>
     </table>
    <!--| end : rodapé |-->
  </td>
 </tr>
</table>
<script LANGUAGE="javascript">
  setaclicou2(); //12/12/2001
</script>

<!-- Fim includemenuesqfim.jsp -->
