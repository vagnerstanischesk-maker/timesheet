/**********************************************
* toolbar constructor.js                      *  
***********************************************
* Cabo/JSUI version 0.5 November, 1999        *
* Written by Robert Hoexter                   *
* Oracle Corporation                          *
**********************************************/
//this version Written by Michael Peterson and Rob Hoexter
 
var tbar_valid_types = "menu;save;help;reload;print;stop;persopt;custom";
var tbar_url = "url:";
var tbar_function = "function:";
var tbar_helptext = "helptext:";
var tbar_enabled = "enabled:";
var tbar_visible = "visible:";
var tbar_default_image = "default-image:";
var tbar_rollover_image = "rollover-image:";
var tbar_disabled_image = "disabled-image:";
var tbar_name = "name:";
var tbar_frame = "frame:";
var tbar_type = "type:";
var tbar_no = "no";
var tbar_false = "false";
var tbar_yes = "yes";
var tbar_true = "true";
 
toolbar.prototype.output = "";
toolbar.prototype.button_counter = 0;
toolbar.prototype.customButtonCounter = 1;
toolbar.prototype.dividerCounter = 1;
toolbar.prototype.addButton = addToolbarButton;
toolbar.prototype.addDivider = addDivider;
toolbar.prototype.addTitle = addTitle;
toolbar.prototype.setVisible = setVisible;
toolbar.prototype.setEnabled = setEnabled;
toolbar.prototype.render = renderToolbar;


 
//Toolbar constructor
function toolbar() {

//define object properties
	//this.output = "";
	
   //button_counter is a counter for the number of buttons/dividers
   //added to the queue to be rendered in the toolbar
	//this.button_counter = 0;
	
  //keep track of number of custom buttons and assign number to each one
  //first one is custom button 1 (not zero)
	//this.customButtonCounter = 1;

  //like the customButtonCounter, dividerCounter keeps track of dividers
	//this.dividerCounter = 1;
	
  //renderQueue keeps track of the queue of buttons/dividers 
  //to be render in the toolbar
	this.renderQueue = new Array();

  //toolbarHash is an associative array (hash table) that allows
  //easy lookup of the position of the button types in the render queue
  //key = name of button (e.g., "menu" or "save")
  //value = it's index position in the toolbar (as assigned by the button_counter var)
	this.toolbarHash = new Array();

//define object methods

//return this;

} //end Toolbar constructor

/*
 * methodology:  allow developer to add as many buttons/dividers 
 * to toolbar object as desired; 
 * then make call to render() to render the toolbar
 */

//**--------Toolbar "Add" methods---------**//

//new version
function addToolbarButton( type, argString ) {
	type = type.toLowerCase();
	if ( tbar_valid_types.indexOf(type) == -1 ) { return; }
	if (type == "custom") {
		var cb = new Button( type, argString, this.customButtonCounter );
		this.renderQueue[ this.button_counter ] = cb;
		this.toolbarHash[ cb.t_name ] = this.button_counter;
		this.customButtonCounter++;
		if (cb.c_name) { this.toolbarHash[ cb.c_name ] = this.button_counter; }	
	} else {
		var b = new Button( type, argString );
		this.renderQueue[ this.button_counter ] = b;
		this.toolbarHash[ b.t_name ] = this.button_counter;
	}
	this.button_counter++;
}

function addDivider() {
	this.renderQueue[ this.button_counter ] = new Divider();
	this.toolbarHash[ "divider".concat(this.dividerCounter) ] = this.button_counter;
	this.dividerCounter++;
	this.button_counter++;
}

function addTitle( t ) {
	this.renderQueue[ this.button_counter ] = new Title( t );
	this.toolbarHash[ "title" ] = this.button_counter;
	this.button_counter++;
}


//**------Toolbar "setEnabled" and "setVisible" methods-------**/

/* These methods require two parameters:
 * 1: A boolean value for toggling the state of the button;
 * 2: A	string corresponding to the button type; see the
 *    addXXXButton() methods above for the proper hash string key;
 *    custom buttons may use the developer assigned name as the string key
 */ 

function setEnabled( button, b_enabled_state ) {
	var pos = this.toolbarHash[ button ];
	if (pos == null) {
		alert("Developer: You have not a proper button identifier in the method setEnabled().\nSee the toolbar documentation for valid button identifier names for this method");
		return;
	}
	this.renderQueue[ pos ].b_enabled = b_enabled_state;
}

function setVisible( button, b_visible_state ) {
	var pos = this.toolbarHash[ button ];
	if (pos == null) {
		alert("Developer: You have not a proper button identifier in the method setVisible().\nSee the toolbar documentation for valid button identifier names for this method");
		return;
	}
	this.renderQueue[ pos ].b_visible = b_visible_state;
}


//**------Toolbar "Render" method-------**/

function renderToolbar( window_Ref ) {

	if ( !window_Ref ) {
		alert( "Developer:  You must pass in a window reference in the render() method call");
		return;
	}
		
	var hiddenElements = 0;
	var buttonHTML = "";

	for (var i = 0; i < this.renderQueue.length; i++) {	
		if ( ! this.renderQueue[i].b_visible ) { 
			hiddenElements++;
			if ( i == (this.renderQueue.length - 1) ) { buttonHTML += "</td></tr>"; }
			continue; 
		}
		
		if (this.renderQueue[i].b_enabled) {
			buttonHTML += this.renderQueue[i].enabledHTML;
		} else {
			buttonHTML += this.renderQueue[i].disabledHTML;
		}
		
		if ( i == (this.renderQueue.length - 1) ) {
			buttonHTML += "</td></tr>";
		} else {
			buttonHTML += "</td><td class=\"toolbar\" nowrap height=\"30\" align=\"middle\">";
		}
	} //end for loop
	
	var cols = this.button_counter - hiddenElements;

   //start table render html
	var output = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">";
	output += "<tr><td rowspan=\"3\"><img src=\"" + image_dir + "toolbar_left.gif\"></td>";
	output += "<td class=\"highlight\" height=\"1\" colspan=\"" + cols;
	output += "\"><img src=\"" + image_dir + "pixel_color6.gif\"></td>";
	output += "<td rowspan=\"3\"><img src=\"" + image_dir + "toolbar_right.gif\"></td></tr>";
	output += "<tr><td class=\"toolbar\" nowrap height=\"30\" align=\"middle\">";
	
   //add buttons and dividers and title(s)
	output += buttonHTML;
	
   //finish table rendering html
	output += "<tr><td class=\"shadow\" height=\"1\" colspan=\"" + cols;
	output += "\"><img src=\"" + image_dir + "pixel_gray2.gif\"></td></tr></table>";
	
   //add necessary IE code for printing
   	output += "<object id=\"IEControl\" width=\"0\" height=\"0\" classid=\"clsid:8856F961-340A-11D0-A96B-00C04FD705A2\"></object>";

  if (window_Ref) 
  { 
    if (window_Ref == "HTML") 
	{ 
	  return output; 
	}
	else 
	{ 
	  window_Ref.document.write(output); 
	  return "";
    }
  }
  else 
  {
    document.write(output);
	return "";
  }
}

//**------END Toolbar Code-------**/

/*
 *--------General Button object----------*
 */

function Button( type, argList, custom_num ) {
	this.b_enabled = true;
	this.b_visible = true;
	this.url;
	this.func;	 
	this.helptext;
	this.enabledHTML;
	this.disabledHTML;
	this.t_name;
	
	if (type == "custom") {
		this.t_name = "custom" + custom_num; 
		this.customArray = parseCustomButtonParams( argList );
		this.defaultImage = this.customArray[0]; 
		this.mouseoverImage = this.customArray[1];
		this.disabledImage = this.customArray[2];
	  //cname = custom name given to custom button by developer
		this.c_name = this.customArray[3];

	} else {
		this.t_name = type;
		this.imageRefs = getImageArray( type );
		this.defaultImage = this.imageRefs[0];
		this.mouseoverImage = this.imageRefs[1];
		this.disabledImage = this.imageRefs[2];
	}

	if ( argList ) {
		var resultArray = parseButtonParameters( type, argList );
	
		this.url = resultArray[0]; 
		this.func = resultArray[1];	
		this.helptext = resultArray[2];	
		this.b_enabled = resultArray[3];
		this.b_visible = resultArray[4];
		this.frameTarget = resultArray[5];
			
		this.disabledHTML = "<img src=\"" + this.disabledImage.src +"\">";
	
		switch (type) {
			case("print") :
				this.specialFunc = ( this.frameTarget ? "javascript:top.printFrame(top.".concat(this.frameTarget).concat(")") : "javascript:top.printFrame(window)" );
				break;
			case("reload"):
				this.specialFunc = ( this.frameTarget ? "javascript:top.".concat(this.frameTarget).concat(".location.reload()") : "javascript:top.location.reload()" );
				break;
			case("stop") :
				this.specialFunc = "javascript:top.stopPage(top)"
				break;
			default: 
				//this.specialFunc = ( this.func ? "javascript:".concat(this.func) : this.url.concat("\" target=\"" + this.frameTarget ) );
				if (this.func)
				{
				  this.specialFunc = "javascript:".concat(this.func)
				} 
				else 
				{
				  this.specialFunc = ( this.frameTarget ? this.url.concat("\" target=\"" + this.frameTarget ) : this.url.concat("\" target=\"_top" ));
				}
		}
	
		this.enabledHTML = "<a href=\"" ;
		this.enabledHTML += this.specialFunc;
		this.enabledHTML += "\" onmouseover=\"document." + this.t_name + ".src='";
		this.enabledHTML += this.mouseoverImage.src + "'; return true\"";
		this.enabledHTML += " onmouseout=\"document." + this.t_name + ".src='";
		this.enabledHTML += this.defaultImage.src + "'; return true\">";
		this.enabledHTML += "<img name=\"" + this.t_name + "\" src=\"" + this.defaultImage.src; 
		this.enabledHTML += "\" align=absmiddle border=0 alt=\"";
		this.enabledHTML += this.helptext + "\"></a>";

	} //end if (argList)
}

/*
 *--------Divider object----------*
 */

function Divider() {
	this.b_enabled = true;
	this.b_visible = true;
	
	m_divider = new Image();
	m_divider.src = image_dir + "toolbar_divider.gif";
	
	this.enabledHTML = "&nbsp;<img src=\"" + m_divider.src + "\" align=\"absmiddle\">&nbsp;";
}


/*
 *---------Title object-----------*
 *
 * Note: a title is the only object added to the toolbar that 
 * creates a new column within the toolbar table.
 * Therefore, it is the only object to increment the "cols"
 * variable of the Toolbar object (in the addTitle() Toolbar method)
 */

function Title( titleText ) {

	this.b_enabled = true;
	this.b_visible = true;
	this.title = "";
	if (titleText) {
		this.title = trimAll( titleText.substring(titleText.indexOf(":")) );
	} //end if (titleText)
	
	this.disabledHTML = "";
	this.enabledHTML = "&nbsp;" + this.title + "&nbsp;";
}


/*
 *-------- UTILITY METHODS --------*
 */
 
function parseButtonParameters( type, argList ) {
  //set a usable default url if no function or url is specified
	var urlParsed = "#";
	var funcParsed = null;
	var helpParsed = "";
	var enabledParsed = true;
	var visibleParsed = true;
	var frameParsed = null;

	if ( argList.indexOf(tbar_url) != -1  && argList.indexOf(tbar_function) != -1 ) {	
		exceptionAlert( type );
		return new Array(5);
	}

	args = argList.split(";");
	for (var i = 0; i < args.length; i++) {
		var arg = args[i];
		if (!arg) break; 
		if (arg.indexOf(tbar_url) != -1) {
			urlParsed = trimAll( arg.substring(arg.indexOf(":") + 1) ); 
			continue; 
		}
	
		if ( arg.indexOf(tbar_function) != -1 ) {
 			funcParsed = trimAll( arg.substring(arg.indexOf(":") + 1) ); 
 			var re = /"/g;   //"
 			funcParsed = funcParsed.replace( re, "'" ); 
			continue; 		
		}
	
		if (arg.indexOf(tbar_helptext) != -1) {
 			helpParsed = trimAll( arg.substring(arg.indexOf(":") + 1) ); 		
			continue; 		
		}	
		
		if (arg.indexOf(tbar_enabled) != -1) {
 			if ( arg.indexOf(tbar_no) != -1 || arg.indexOf(tbar_false) != -1 ) {
 				enabledParsed = false; 
 			} //else it stays true, as set at the beginning of the method
			continue; 		
		}
		
		if (arg.indexOf(tbar_visible) != -1) {
 			if ( arg.indexOf(tbar_no) != -1 || arg.indexOf(tbar_false) != -1 ) {
 				visibleParsed = false; 
 			} //else it stays true, as set at the beginning of the method
			continue; 		
		}
		
		if (arg.indexOf(tbar_frame) != -1) {
			frameParsed = trimAll( arg.substring(arg.indexOf(":") + 1) ); 
			continue;
		}
	} //end for loop

	var parsed = new Array(6);
	parsed[0] = urlParsed;
	parsed[1] = funcParsed;
	parsed[2] = helpParsed;
	parsed[3] = enabledParsed;
	parsed[4] = visibleParsed;
	parsed[5] = frameParsed;
	return parsed;
} //end function parseButtonParameters


function parseCustomButtonParams( argList ) {

	var defaultImage = new Image();
	var disabledImage = new Image();
	var mouseoverImage = new Image();
	var nameParsed = null;
	var arg;

	args = argList.split(";");
	for (var i = 0; i < args.length; i++) {
		arg = args[i];
		if (arg.indexOf(tbar_default_image) != -1) {
		 	defaultImage.src = trimAll( arg.substring(arg.indexOf(":") + 1) ); 		
 			continue;
		}
		
		if (arg.indexOf(tbar_rollover_image) != -1) {
		 	mouseoverImage.src = trimAll( arg.substring(arg.indexOf(":") + 1) ); 		
 			continue;
		}
		
		if (arg.indexOf(tbar_disabled_image) != -1) {
		 	disabledImage.src = trimAll( arg.substring(arg.indexOf(":") + 1) ); 		
 			continue;
		}
		
		if (arg.indexOf(tbar_name) != -1) {
			nameParsed = trimAll( arg.substring(arg.indexOf(":") + 1) );
			continue;
		}

	} //end for loop
	
	var custA = new Array(3);
	custA[0] = defaultImage;
	custA[1] = mouseoverImage;
	custA[2] = disabledImage;
	custA[3] = nameParsed;
	return custA;
	
} //end parseCustomButtonParams()

function getImageArray( type ) {
	var imA = new Array(3);
	imA[0] = new Image(); //default
	imA[1] = new Image(); //rollover
	imA[2] = new Image(); //disabled
	switch (type) {
		case "save" :
			imA[0].src = image_dir + "toolbar_icon_save.gif";
			imA[1].src = image_dir + "toolbar_icon_save_active.gif";
			imA[2].src = image_dir + "toolbar_icon_save_disabled.gif";
			break;
		
		case "menu" :
			imA[0].src = image_dir + "toolbar_icon_menu.gif";
			imA[1].src = image_dir + "toolbar_icon_menu_active.gif";
			imA[2].src = image_dir + "toolbar_icon_menu_disabled.gif";
			break;

		case "reload" :
			imA[0].src = image_dir + "toolbar_icon_reload.gif";
			imA[1].src = image_dir + "toolbar_icon_reload_active.gif";
			imA[2].src = image_dir + "toolbar_icon_reload_disabled.gif";
			break;
	
		case "print" :
			imA[0].src = image_dir + "toolbar_icon_print.gif";
			imA[1].src = image_dir + "toolbar_icon_print_active.gif";
			imA[2].src = image_dir + "toolbar_icon_print_disabled.gif";
			break;
		
		case "stop" :
			imA[0].src = image_dir + "toolbar_icon_stop.gif";	
			imA[1].src = image_dir + "toolbar_icon_stop_active.gif";	
			imA[2].src = image_dir + "toolbar_icon_stop_disabled.gif";		
			break;
			
		case "persopt" :			
			imA[0].src = image_dir + "toolbar_icon_prefs.gif";
			imA[1].src = image_dir + "toolbar_icon_prefs_active.gif";
			imA[2].src = image_dir + "toolbar_icon_prefs_disabled.gif";	
			break;
		
		case "help" :
			imA[0].src = image_dir + "toolbar_icon_help.gif";
			imA[1].src = image_dir + "toolbar_icon_help_active.gif";
			imA[2].src = image_dir + "toolbar_icon_help_disabled.gif";
			break;
	}
	return imA;
}	

//-- trim() removes all spaces around a string --//
function trim( s ) {
	var change = false;
	var inString = s;
    var frontIndex = 0
    var backIndex = inString.length - 1;        
    while (inString.charAt( frontIndex ) == " ") {
    	frontIndex++;
    	change = true;
    }
    while (inString.charAt( backIndex ) == " ") {
    	backIndex--;
    	change = true;
    }
    return (change ? inString.substring( frontIndex, (backIndex + 1) ) : inString);
} //end trim()


//--trimAll() removes leading and trailing space AND single or
//--double quotes around the string passed to it 
function trimAll( s ) {
	var holder = trim( s );
 	if ( (holder.charAt(0) == "\"" || holder.charAt(0) == "'") && (holder.charAt(holder.length - 1) == "\"" || holder.charAt(holder.length - 1) == "'") )
 	{
 		holder = holder.substring( 1, holder.length - 1 );
 	}
	return holder;
} //end trimAll()

function exceptionAlert( caller ) {
 	var msg = "Developer Alert: \nYou have specified both a url and a JavaScript ";
 	msg += "function for the " + caller + " object.";
 	msg += "\nYou may only specify one or the other."
	alert( msg );
}

var Win32;
if (Nav4) {
	Win32 = ((navigator.userAgent.indexOf("Win") != -1) && (navigator.userAgent.indexOf("Win16") == -1));
} else {
	Win32 = ((navigator.userAgent.indexOf("Windows") != -1) && (navigator.userAgent.indexOf("Windows 3.1") == -1));
}

function printFrame(wind) {

	// no single frame printing available for Mac
	if (Win32) {
		if (Nav4) {
			wind.print()
		} else {
			// traps all script error messages hereafter until page reloads
			window.onerror = doNothing
			// make sure desired frame has focus
			wind.focus()
			// change second parameter to 2 if you don't want the print dialog to appear
			IEControl.ExecWB(6, 1)
		}
	} else {
		alert("Sorry. Printing is available only from Windows 95/98/NT.")
	}
}

function doNothing() { return true }

function stopPage(win) {
	if (Nav4) {
		win.stop()
	} else if (Win32) {
		win.focus()
		// requires same <OBJECT> as printing
		IEControl.ExecWB(23, 0)
	}
}
