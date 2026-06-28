/**********************************************
* cabo_utilities.js                           *  
***********************************************
* Cabo/JSUI version 0.5 November, 1999        *
* Written by Robert Hoexter                   *
* Oracle Corporation                          *
**********************************************/

// Set global boolean variables describing browser and version
var Nav4 = ((navigator.appName == "Netscape") && (parseInt(navigator.appVersion) == 4))
var Nav4plus = ((navigator.appName == "Netscape") && (parseFloat(navigator.appVersion) > 4.04))
var Nav5 = ((navigator.appName == "Netscape") && (parseInt(navigator.appVersion) == 5))
var IE4 = ((navigator.appName.indexOf("Microsoft") > -1)) && (parseInt(navigator.appVersion) == 4);
var IE5 = ((navigator.appName.indexOf("Microsoft") > -1)) && (parseInt(navigator.appVersion) == 5);
var anyIE = (IE4 || IE5)
var anyNav = (Nav4 || Nav4plus || Nav5)

//Directory variables used to construct paths as needed
var image_dir = "images/";
var jslib_dir = "jslib/";

//Code to either determine the baseHref based upon this location, or to write whatever has been loaded
//into a variable "baseHref" in the top page
if (top.baseHref) 
{
  var hrefLocation = top.baseHref + "";
}
else
{
  var hrefLocation = document.location + "";
  //if (hrefLocation.indexOf(".") > -1) hrefLocation = hrefLocation.substr(0,hrefLocation.lastIndexOf("/")+1);
  //removing the file name makes href='#' revert to the parent directory, rather than the top of any given page...
  //so, can't be done.  Seems OK though, since a filename in the base HREF appears to be ignored anyway
} 

//special code to write a base-href into any page loaded as a frame in Netscape
if (anyNav && self.location != top.location) document.write("<BASE HREF='"+hrefLocation+"'>")

// Common routines returning source html for empty "spacer" frames - 
function blankframe() {
  return "<HTML><HEAD><BASE HREF='"+hrefLocation+"'><link rel=stylesheet type='text/css' href='" + image_dir + "cabo_styles.css'></HEAD><BODY class=appswindow ></BODY></HTML>" }

function grayframe() {
        return "<HTML><HEAD><BASE HREF='"+hrefLocation+"'><link rel=stylesheet type='text/css' href='" + image_dir + "cabo_styles.css'></HEAD><BODY class=panel onFocus='if (top.checkModal) top.checkModal()'><img src='" + image_dir + "pixel_gray5.gif' width=10></BODY></HTML>" }


// Common parsed parameter values for all constructor functions, and common regular expressions
// Included here to avoid the possibility of duplicate and conflicting definitions.
/**********************************************************************************
regular expressions used to replace tokens in the HTML for table cells
**********************************************************************************/
var repact = /AaAaA/; //Action, generic href action: "<a href=AaAaA>"
var repval = /VvVvV/; //value, generic value used for display widgets
var repurl = /UuUuU/; //URL, as part of anchor tag: "<a href=UuUuU>"
var repfun = /JjJjJ/; //javascript function call
var repcol = /CcCcC/; //column, when refering back to data table to update object
var reprow = /RrRrR/; //row, when refering back to data table to update object
var repsiz = /ZzZzZ/; //size, for text fields etc. "<input type=text size=ZzZzZ"
var repnum = /NnNnN/; //number, used for automatic object naming
var reptxt = /TtTtT/; //text token for most cells
var repstl = /SsSsS/; //style, for css reference to class: "class=SsSsS"
var repicn = /IiIiI/; //Image or Icon, for image cells
var rephlp = /HhHhH/; //Helptext or Hinttext in general
var repspan = /PpPpP/; //numeric value for table column span
var repfram = /FfFfF/; //frame Reference used for target of buttons and tabs
var repbar = /BbBbB/;

var p_name = "name:";
var p_text = "text:";
var p_image = "image:";
var p_url = "url:";
var p_target = "target:";
var p_rowspan = "rowspan:";
var p_colspan = "colspan:";
var p_type = "type";
var p_align = "align:";
var p_width = "width:";
var P_size = "size:";
var p_totaled = "totaled:";
var p_colname = "colname:";
var p_cellobject = "cellobject:";
var p_iconobj = "iconobj:";
var p_style = "style:";
var p_wrap = "wrap:";
var p_dataitems = "dataitems:";
var p_defaultaction = "defaultaction:";
var p_actiontype = "actiontype:";
var p_iconname = "iconname:";
var p_disablediconname = "disablediconname:";
var p_textposition = "textposition:";
var p_iconposition = "iconposition:"
var p_selecttype = "selecttype:";
var p_gridline = "gridline:";
var p_width = "width:";
var p_widthpercent = "widthpercent:";
var p_color = "color:";
var p_grid = "grid:";
var p_numbered = "numbered:";
var p_hint = "hint:"
var p_disabledhint = "disabledhint:";
var p_enabled = "enabled:";
var p_alwaysactive = "alwaysactive:"
var p_initialtab = "initialtab:";
var p_visible = "visible:";
var p_title = "title:";
var p_helptext = "helptext:";
var p_objectref = "objectref:";
var p_targetframe = "targetframe:";
var p_longpage = "longpage:"
var p_showcurrentonly = "showcurrentonly:";
var p_lightcontainer = "lightcontainer:";

//mostly from button constructor
var p_shape = "shape:";
var p_action = "action:";
var p_defaultbutton = "defaultbutton:";
var p_gap = "gap:";

//mostly from container constructor locator bar
var p_steps = "steps:";
var p_maxchars = "maxchars:";
var p_currentstep = "currentstep:";
var p_locatorcontrol = "locatorcontrol:";
var p_locatorobject = "locatorobject:";
var p_backbuttonobject = "backbuttonobject:";
var p_nextbuttonobject = "nextbuttonobject:"

//mostly from tree constructor
var p_populated = "populated:";
var p_startopenlevel = "startopenlevel:";
var p_startopen = "startopen:";
var p_level = "level:";
var p_state = "state:";
var p_childcount = "childcount:";
var p_nodeimage = "nodeimage:";
var p_dataframe = "dataframe:";
var p_moredatahref = "moredatahref:";


/* parameter names for future table functionality
var p_headerlines = "headerlines:"; //number of lines to break header into if > header-chars
var p_headerchars = "headerchars:"; //maximum number of characters on a header line before wrapping
var p_minwidth = "minwidth:"; //minimum width for column, set by a sized gif in the header
var p_maximize = "maximize:"; //designate the specific column to 100% to force it to expand and take up slack
var p_hidden = "hidden:"; //column attribute - allows columns to be hidden. (flag on col object array in table)
var p_stripe = "stripe:"; //vertical stripes
var p_banding = "banding:"; //horizontal striping
var p_sortable = "sortable:"; //sort the array by the contents.  Complicated!
var p_disabled = "disabled:"; //for individual cells or whole columns 
var p_options = "options:"; //for poplists items?
var p_optionsarray = "optionsarray:";
var p_defaultvalue = "defaultvalue:";
*/

// Common routine used by all constructor libraries to parse and trim name:value; pairs
function parsevalues (arg, obj, validargs) {
  while (arg.charAt(0) == " ") { arg = arg.substring(1) }
  while (arg.charAt(arg.length-1) == " ") { arg = arg.substr(0,arg.length-1); }  
  var name_arg = arg.substring(0,arg.indexOf(":"));
  var search_arg = name_arg + ":";
  var value_arg = arg.substring(arg.indexOf(":")+1);
  while (value_arg.charAt(0) == " ") { value_arg = value_arg.substring(1) }
  while (value_arg.charAt(arg.length-1) == " ") { value_arg = value_arg.substr(0,value_arg.length-1); }  
  if (validargs.indexOf(search_arg) > -1) obj[name_arg] = value_arg;
}
