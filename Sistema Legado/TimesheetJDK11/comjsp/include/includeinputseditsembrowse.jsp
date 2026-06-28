<!-- Inicio includeinputseditsembrowse.jsp -->
   <INPUT TYPE="HIDDEN" NAME="oper" VALUE="<%=oper %>">
   <INPUT TYPE="HIDDEN" NAME="rec" VALUE="<%=(oper.equals("I")? "" : rec)%>">
   <INPUT TYPE="HIDDEN" NAME="EXCLUSAO" VALUE="N">
   <INPUT TYPE="HIDDEN" NAME="clicou" VALUE="N">
<script language="javascript">
   setaclicou();
</script>
<!-- Fim includeinputseditsembrowse.jsp -->
