function abreProfile( P_CDG_USUR ) {
  var vheight;
    vheight=360;
  var vwidth;
    vwidth=540;
  var vtop;
      vtop= (screen.availheight-vheight)/2
  var vleft;
      vleft= (screen.availwidth-vwidth)/2

  window.open("com_Popup_Forum_Profile.jsp?CDG_USUR=" + P_CDG_USUR,"profile","toolbar=no,location=no,directories=no,menubar=no, scrollbars=no,status=no,top="+vtop+", left="+vleft+",  width="+vwidth+", height="+vheight+", resizable=no");
  return true;
}

function submeteviewwatch(P_FORUM_CD_FORUM) {
   if(document.iFormW.clicou.value == "N") {
      document.iFormW.FORUM_CD_FORUM.value = P_FORUM_CD_FORUM;
      document.iFormW.clicou.value = "S";
      document.iFormW.submit();
   }
}

function setaclicouForumW()
{  var elemento;
   var bPossuiForm = false;
   var bPossuiCampo = false;
   for(elemento in document)
   {  if(elemento == "iFormW")
      {  bPossuiForm = true; }
   }
   if(bPossuiForm)
   {  for(elemento in document.iFormW)
      {  if(elemento == "clicou")
         {  bPossuiCampo = true; }
      }
   }
   if(bPossuiCampo)
   {  document.iFormW.clicou.value = "N"; }
   return true;
}

function submetevoltawatch() {
   if(document.iFormW.clicou.value == "N") {
      if(document.iFormW.origem.value.indexOf("Thread") > 0)
         document.iFormW.action = "com_Forum_Thread_Browse.jsp";
      else
         document.iFormW.action = "com_Forum_Browse.jsp";
      document.iFormW.clicou.value = "S";
      document.iFormW.submit();
   }
}

function editarForum(registro, pagina, pFORUM_CD_FORUM)
{
   document.iForm.FORUM_CD_FORUM.value = pFORUM_CD_FORUM;
   editar(registro, pagina);
}

function forumabreajuda() {

  var vheight;
    vheight=360;
  var vwidth;
    vwidth=480;

  var vtop;
      vtop= (screen.availheight-vheight)/2
  var vleft;
      vleft= (screen.availwidth-vwidth)/2
  var url;

  window.open("Forum_Ajuda.html","ajudaforum","toolbar=no,location=no,directories=no,menubar=no, scrollbars=yes,status=no,top="+vtop+", left="+vleft+",  width="+vwidth+", height="+vheight+", resizable=no");
  return true;
}
