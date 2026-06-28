function goinserir( pagina )
{
   document.iForm.oper.value = "I";
   document.iForm.action = pagina;

   if(document.iForm.clicou.value == "N")
   {  document.iForm.clicou.value = "S";
      document.iForm.submit();
   }
   return true;
}
function gofirst( pagina )
{
   document.iForm.NAVIGATE.value = "FIRST";
   document.iForm.action = pagina;

   if(document.iForm.clicou.value == "N")
   {  document.iForm.clicou.value = "S";
      document.iForm.submit();
   }
   return true;
}
function goprevious( pagina )
{
   document.iForm.NAVIGATE.value = "PREVPAGE";
   document.iForm.action = pagina;

   if(document.iForm.clicou.value == "N")
   {  document.iForm.clicou.value = "S";
      document.iForm.submit();
   }
   return true;
}
function gonext( pagina )
{
   document.iForm.NAVIGATE.value = "NEXTPAGE";
   document.iForm.action = pagina;

   if(document.iForm.clicou.value == "N")
   {  document.iForm.clicou.value = "S";
      document.iForm.submit();
   }
   return true;
}
function golast( pagina )
{
   document.iForm.NAVIGATE.value = "LAST";
   document.iForm.action = pagina;

   if(document.iForm.clicou.value == "N")
   {  document.iForm.clicou.value = "S";
      document.iForm.submit();
   }
   return true;
}
function gorefresh( pagina )
{
   document.iForm.NAVIGATE.value = "REFRESH";
   document.iForm.action = pagina;

   if(document.iForm.clicou.value == "N")
   {  document.iForm.clicou.value = "S";
      document.iForm.submit();
   }
   return true;
}
function gofind( pagina )
{
   document.iForm.NAVIGATE.value = "VALUE";
   document.iForm.action = pagina;

   if(document.iForm.clicou.value == "N")
   {  document.iForm.clicou.value = "S";
      document.iForm.submit();
   }
   return true;
}

function editar(registro, pagina)
{
   document.iForm.oper.value = "E";
   document.iForm.rec.value = registro;
   document.iForm.action = pagina;

   if(document.iForm.clicou.value == "N")
   {  document.iForm.clicou.value = "S";
      document.iForm.submit();
   }
   return true;
}
function excluir(registro, srowid, pagina)
{   
   if(pagina.substring(pagina.length-9,pagina.length)=="?inclui=S" 
      || confirm("Tem certeza que deseja excluir?")) {
     document.iForm.rec.value = registro;
     document.iForm.CMROWID.value = srowid;
     document.iForm.EXCLUSAO.value = "S";
     document.iForm.action = pagina;

     if(document.iForm.clicou.value == "N")
     {  document.iForm.clicou.value = "S";
        document.iForm.submit();
     }
   }
   return true;
}

function salva()
{ 
   document.iForm.EXCLUSAO.value = "N";
   if( validaform() ) {
      if(document.iForm.clicou.value == "N")
      {  document.iForm.clicou.value = "S";
         document.iForm.submit();
      }
   }
   return true;
}
function exclui()
{
   document.iForm.EXCLUSAO.value = "S";

   if(document.iForm.clicou.value == "N")
   {  document.iForm.clicou.value = "S";
      document.iForm.submit();
   }
   return true;
}

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
