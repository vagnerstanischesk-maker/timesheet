<!-- Inicio includebody.jsp -->
<script language="javascript">
function setaclicou()
{  var elemento;
   var bPossuiForm = false;
   var bPossuiCampo = false;
   for(elemento in document)
   {  if(elemento == "iForm")
      {  bPossuiForm = true; }
   }
   if(bPossuiForm)
   {  for(elemento in document.iForm)
      {  if(elemento == "clicou")
         {  bPossuiCampo = true; }
      }
   }
   if(bPossuiCampo)
   {  document.iForm.clicou.value = "N"; }

   setaclicou2();
   return true;
}

function setaclicou2()
{  var elemento2;
   var bPossuiForm2 = false;
   var bPossuiCampo2 = false;
   for(elemento2 in document)
   {  if(elemento2 == "iNavega")
      {  bPossuiForm2 = true; }
   }
   if(bPossuiForm2)
   {  for(elemento2 in document.iNavega)
      {  if(elemento2 == "INAVEGACLICOU")
         {  bPossuiCampo2 = true; }
      }
   }
   if(bPossuiCampo2)
   {  document.iNavega.INAVEGACLICOU.value = "N"; }
   return true;
}

function FNCNAVEGA( destino ) {
   if(document.iNavega.INAVEGACLICOU.value == "N")
   {  document.iNavega.INAVEGACLICOU.value = "S";
      setTimeout('setaclicou2()', 15000);

      if(destino.substring(0,4) == "REL*") {
         window.open("chamareport.jsp?rel="+destino.substring(4),"relatorio","toolbar=no,location=no,directories=no,menubar=yes,scrollbars=yes,status=no,width=700,height=550,resizable=yes");
      }
      else
         parent.location.href = destino;
      //return true;
   }
   //return false;
}

function TESTAFNCNAVEGA( destino )
{
   if(document.iNavega.INAVEGACLICOU.value == "N")
   {  //document.iNavega.INAVEGACLICOU.value = "S";
      //setTimeout('setaclicou2()', 15000);
      //document.location.href = destino;
      return true;
   }
   return false;
}

function carrega()
{  setaclicou(); //03/12/2001
   return true;
}
function fnc_onunload() {
   //alert("goodbye1");
   if (navigator.appVersion.indexOf('MSIE') != -1)
   {  var pagina = "com_libconn.jsp?p=<%=UtilBean.getJSP(request) %>";
      if((window.screenLeft > 10000) && (window.screenTop > 10000))
      {  window.open(pagina, "lib", "toolbar=no,location=no,directories=no,menubar=no,scrollbars=no,status=no,top=2000,left=2000,width=10,height=10,resizable=no");
      }
   }
   return true;
}
</script>
<body id="iBody" bgcolor="#ffffff" topmargin="0" leftmargin="0" text="#000000" link="#000000" alink="#000000" vlink="#000000" onLoad="carrega()" OnUnload="fnc_onunload()">
<a name="topo"></a>
<!-- Fim includebody.jsp -->