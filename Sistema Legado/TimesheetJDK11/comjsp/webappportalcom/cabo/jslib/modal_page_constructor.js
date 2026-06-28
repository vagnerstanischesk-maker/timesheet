/**********************************************
* modal_page_constructor.js                   *  
***********************************************
* Cabo/JSUI version 0.5 November, 1999        *
* Written by Robert Hoexter                   *
* Oracle Corporation                          *
**********************************************/

// One object tracks the current modal dialog spawned from this window.  
// A new object is created so that one is always present when referring to
// or checking the existence of its methods or properties.

// the .win property will become a new window object when created, however several
// functions reference the native 'closed' property of the window object, and if it
// does not exist this causes syntax errors. 
 
var modalWin = new Object();
modalWin.win = "";
modalWin.opened=false;

// generate a modal window; invoked by button or link from any page or this
// any frame within the frameset in which this code is contained.

// Function call: top.openModal(parms)
//  Parameters:
//    url     -- URL of the page/frameset to be loaded into the dialog
//    width   -- pixel width of the dialog window
//    height   -- pixel height of the dialog window
//    returnFunc   -- reference to the function (on the calling page)
//      that is to handle the returned data from the dialog
//    returnObj   -- object reference to the field where returned values may be placed.  This
//      is the most efficient way to populate a field on the calling page.  Use the 
//      returnFunc reference in the case of more complex actions.
//    winTitle   -- String used by the "page_title_content.js" library, which places this string
//      into the titlebar of the modal window.
//    args     -- [optional] any data you need to pass to the dialog

function openModal(url, width, height, returnFunc, returnObj, winTitle, scrolling, args) {

  // load up properties of the modal window object for this excursion
  modalWin.returnFunc = returnFunc;
  modalWin.returnObj = returnObj;
  modalWin.returnVal = "";
  modalWin.scrolling = "no";
  modalWin.args = args;
  modalWin.windowTitle = winTitle;
  modalWin.url = url;
  if (width) modalWin.width = (width > 99)?width:100;
  else modalWin.width = 400;
  if (height) modalWin.height = (height > 99)?height:100;
  else modalWin.height = 400;
  // keep name relatively unique so Navigator doesn't overwrite an existing dialog
  modalWin.name = (new Date()).getSeconds().toString()
  if (scrolling == "Y") modalWin.scrolling = "yes";
  var attr = "scrollbars="+modalWin.scrolling + ","
  if (Nav4) {
    // center on the main window
    modalWin.left = window.screenX + ((window.outerWidth - modalWin.width) / 2)
    modalWin.top = window.screenY + ((window.outerHeight - modalWin.height) / 2)
    attr += "screenX=" + modalWin.left + ",screenY=" + modalWin.top + 
      ",resizable=no,dependent=yes,width=" + modalWin.width + ",height=" + modalWin.height
  } 
  else 
  {
    // best we can do is center in screen
    modalWin.left = (screen.width - modalWin.width) / 2
    modalWin.top = (screen.height - modalWin.height) / 2
    attr += "left=" + modalWin.left + ",top=" + modalWin.top + 
      ",resizable=no,width=" + modalWin.width + ",height=" + modalWin.height
  }
  // generate the window and make sure it has focus
  modalWin.win=window.open(modalWin.url, modalWin.name, attr)
  modalWin.win.focus();
  modalWin.opened = true;
}

// event handler to prevent any Navigator widget action when modal is active
function deadend() 
{
 if (modalWin.opened) 
 {
    modalWin.win.focus()
    return false
  }
}

// preserve IE link onclick event handlers while they're disabled;
// restore when re-enabling the main window
var IELinkClicks

// disable form elements and links in all frames for IE
function disableForms() 
{
  IELinkClicks = new Array()
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
      IELinkClicks[h] = new Array()
      for (i = 0; i < frames[h].document.links.length; i++) 
	  {
        IELinkClicks[h][i] = frames[h].document.links[i].onclick
        frames[h].document.links[i].onclick = deadend
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
      IELinkClicks[i] = document.links[i].onclick
      document.links[i].onclick = deadend
	}
  }
}

// restore IE form elements and links to normal behavior
function enableForms() 
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
        frames[h].document.links[i].onclick = IELinkClicks[h][i]
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
      document.links[i].onclick = IELinkClicks[i]
    }
  }
}

// a little extra help for Navigator
function blockEvents() {
    if (Nav4) {
        window.captureEvents(Event.CLICK | Event.MOUSEDOWN | Event.MOUSEUP | Event.FOCUS)
        window.onclick = deadend
        window.onfocus = checkModal
    } else {
        disableForms()
    }
}

function unblockEvents() {
    
    if (Nav4) {
        window.releaseEvents(Event.CLICK | Event.MOUSEDOWN | Event.MOUSEUP | Event.FOCUS)
        window.onclick = null
        window.onfocus = null
    } else {
        enableForms()
    }
}


// invoked by onFocus event handler of EVERY frame's document
function checkModal() { 
if (modalWin.opened && modalWin.win) {  
 //if (modalWin.win && !modalWin.win.closed) {
   modalWin.win.focus()  
    }
}

// clear 'opener' reference in a modal if dialog is showing;
// takes care of case when user closes main window while dialog is showing.
// tests for existance of close method as well as the closed window condition
// automatically set by the close method on the window object

function cancelModal() {
//  if (modalWin.win.close && !modalWin.win.closed) {
if (modalWin.opened) {
  modalWin.opened = false;
    modalWin.win.opener = null;
    modalWin.win.close(); }
}