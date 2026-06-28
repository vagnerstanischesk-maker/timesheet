/**********************************************
* cabo_alert.js                               *  
***********************************************
* Cabo/JSUI version 0.5 November, 1999        *
* Written by Robert Hoexter                   *
* Oracle Corporation                          *
**********************************************/
//Requires Button Constructor, Cabo Utilities 

cancelButton = new button("text:Cancel; shape:RR; actiontype:function; action:handleCancel()")
okButton = new button("text:OK; shape:RR; actiontype:function; action:handleContinue();gap:narrow;")
warningBar = new buttonRow(okButton, cancelButton);
errorInfoButton = new button("text:OK; shape:RR; actiontype:function; action:handleCancel()");

caboAlert = new Object();
caboAlert.file = jslib_dir + "cabo_alert.html";
caboAlert.type = "info"
caboAlert.winTitle = "Information"
caboAlert.text = "Information";
caboAlert.message = "No message here";
caboAlert.Opened = false;
caboAlert.width = 500;
caboAlert.height = 200;
caboAlert.defaultHeight = 200;
caboAlert.left = 0;
caboAlert.top = 0;
caboAlert.scrolling = false;
caboAlert.win = new Object();
caboAlert.error = openError;
caboAlert.warning = openWarning;
caboAlert.info = openInfo;
caboAlert.calcTextHeight = calcTextHeight;
caboAlert.continueFunc = null;
caboAlert.buttons = new Object();
caboAlert.warningIcon = image_dir + "alert_warning.gif";
caboAlert.infoIcon = image_dir + "alert_info.gif";
caboAlert.errorIcon = image_dir + "alert_error.gif";
caboAlert.warningText = "Warning";
caboAlert.infoText = "Information"
caboAlert.errorText = "Error"
caboAlert.icon = "";

function openError ( p_text ) 
{
  this.calcTextHeight(p_text);
  this.type = "error";
  this.icon = this.errorIcon;
  this.text = this.errorText;
  this.message = p_text;
  this.buttons = errorInfoButton;
  attr = buildWinAttributes();
  this.win=window.open(this.file, "CaboAlert", attr);
  this.win.focus();
  this.opened = true;
}
function openWarning ( p_text, p_func )
{
  this.calcTextHeight(p_text);
  this.type = "warning";
  this.icon = this.warningIcon;
  this.text = this.warningText;
  this.message = p_text;
  if (p_func) this.continueFunc = p_func;
  else this.continueFunc = null;
  this.buttons = warningBar;
  attr = buildWinAttributes();
  this.win=window.open(this.file, "CaboAlert", attr);
  this.win.focus();
  this.opened = true;
}
function openInfo ( p_text )
{ 
  this.calcTextHeight(p_text);
  this.type = "info";
  this.icon = this.infoIcon;
  this.text = this.infoText;
  this.message = p_text;
  this.buttons = errorInfoButton;
  attr = buildWinAttributes();
  this.win=window.open(this.file, "CaboAlert", attr);
  this.win.focus();
  this.opened = true;
}
function calcTextHeight (p_text)
{
  var linechars = (p_text.indexOf("<") == -1)?60:40; //there's formatting in this, so allow more vertical room
  var height = this.defaultHeight;
  var x = (p_text.length < 240)?0:p_text.length - 240;
  x = (x==0)?0:Math.round(x / linechars) * 12
  this.height = height + x;
}
function buildWinAttributes ()
{   
  var attr = "scrollbars="
  attr += (caboAlert.scrolling)?"yes,":"no,";
  if (Nav4) 
  { 
    // center on the main window
    //caboAlert.left = window.screenX + ((window.outerWidth - caboAlert.width) / 2)
    //caboAlert.top = window.screenY + ((window.outerHeight - caboAlert.height) / 2)
	caboAlert.left = window.screenX + 150;
	caboAlert.top = window.screenY + 140;
    attr += "screenX=" + caboAlert.left + ",screenY=" + caboAlert.top + 
      ",resizable=no,dependent=yes,width=" + caboAlert.width + ",height=" + caboAlert.height
  } 
  else 
  {
    // best we can do is center in screen
    caboAlert.left = (screen.width - caboAlert.width) / 2
    caboAlert.top = (screen.height - caboAlert.height) / 2
    attr += "left=" + caboAlert.left + ",top=" + caboAlert.top + 
      ",resizable=no,width=" + caboAlert.width + ",height=" + caboAlert.height
  }
  return attr;
}
function prepForAlert() 
{
  if (Nav4) 
  {
    window.captureEvents(Event.CLICK | Event.MOUSEDOWN | Event.MOUSEUP | Event.FOCUS)
    window.onclick = AlertDeadend
    window.onfocus = checkAlert
  } 
  else 
  {
    disableOpener()
  }
}
function cleanUpAlert() 
{
  if (Nav4) 
  {
    window.releaseEvents(Event.CLICK | Event.MOUSEDOWN | Event.MOUSEUP | Event.FOCUS)
    window.onclick = null
    window.onfocus = null
  } 
  else 
  {
    enableOpener()
  }
}
// event handler to prevent any Navigator widget action when modal is active
function AlertDeadend() 
{
  if (caboAlert.opened) 
 {
    caboAlert.win.focus()
    return false;
  }
}

// preserve IE link onclick event handlers while they're disabled;
// restore when re-enabling the main window
var IEOpenerLinkClicks

// disable form elements and links in all frames for IE
function disableOpener() 
{ 
  IEOpenerLinkClicks = new Array()
  if (frames.length > 0)
  { 
    for (var h = 0; h < frames.length; h++) 
    {
      for (var i = 0; i < frames[h].document.forms.length; i++) 
      {
        for (var j = 0; j < frames[h].document.forms[i].elements.length; j++) 
        {
          frames[h].document.forms[i].elements[j].disabled = true
        }
      }
      IEOpenerLinkClicks[h] = new Array()
      for (i = 0; i < frames[h].document.links.length; i++) 
      {
        IEOpenerLinkClicks[h][i] = frames[h].document.links[i].onclick
        frames[h].document.links[i].onclick = AlertDeadend
      }
    }
  }
  else
  {
    for (var i = 0; i < document.forms.length; i++) 
    {
      for (var j = 0; j < document.forms[i].elements.length; j++) 
      {
        document.forms[i].elements[j].disabled = true
      }
    }
	for (i = 0; i < document.links.length; i++) 
    {
      IEOpenerLinkClicks[i] = document.links[i].onclick
      document.links[i].onclick = AlertDeadend
    }
  }
}

// restore IE form elements and links to normal behavior
function enableOpener() 
{
  if (frames.length > 0)
  {
  for (var h = 0; h < frames.length; h++) 
    {
      for (var i = 0; i < frames[h].document.forms.length; i++) 
      {
        for (var j = 0; j < frames[h].document.forms[i].elements.length; j++) 
        {
          frames[h].document.forms[i].elements[j].disabled = false
        }
      }
      for (i = 0; i < frames[h].document.links.length; i++) 
      {
        frames[h].document.links[i].onclick = IEOpenerLinkClicks[h][i]
      }
    }
  }
  else
  {
      for (var i = 0; i < document.forms.length; i++) 
    {
      for (var j = 0; j < document.forms[i].elements.length; j++) 
      {
        document.forms[i].elements[j].disabled = false
      }
    }
	for (i = 0; i < document.links.length; i++) 
    {
      document.links[i].onclick = IEOpenerLinkClicks[i]
    }
  }
}
// invoked by onFocus event handler of alert window
function checkAlert() 
{ 
  if (caboAlert.opened && caboAlert.win) 
  {  
    caboAlert.win.focus()    
  }
}
function cancelAlert() 
{
  if (caboAlert.opened) 
  {
    caboAlert.opened = false;
    caboAlert.win.opener = null;
    caboAlert.win.close(); 
  }
}