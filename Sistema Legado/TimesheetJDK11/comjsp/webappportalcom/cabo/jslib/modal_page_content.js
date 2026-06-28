/**********************************************
* modal_page_content.js                       *  
***********************************************
* Cabo/JSUI version 0.5 November, 1999        *
* Written by Robert Hoexter                   *
* Oracle Corporation                          *
**********************************************/

// close the dialog
function closeme( ) 
{
  if (opener) opener.unblockEvents();
  opener.modalWin.opened = false;
  window.close();
}

// respond to the click of OK.  If a return function is specified, then it will be called.  If 
// a form object reference is passed, then its value property will be populated.
function handleOK( P_value ) 
{
  if (opener) {
    if (opener.modalWin.returnFunc) opener.modalWin.returnFunc(P_value);
    if (opener.modalWin.returnObj) opener.modalWin.returnObj.value = P_value;
  } 
  else 
  {
    //unless something goes very wrong in the parent and it blows up, this alert will never happen
    alert("You have closed the main window.\n\nNo action will be taken on the choices in this dialog box.");
  }
  closeme();
  return false;
}

// respond to the click of Cancel
function handleCancel() 
{
  closeme();
  return false;
}

// force focus on one of the dialog frames for IE
function forceFocus() 
{
  if (opener) 
  {
    if (!Nav4) 
	{
      top.window.focus();
    }
  }
}
