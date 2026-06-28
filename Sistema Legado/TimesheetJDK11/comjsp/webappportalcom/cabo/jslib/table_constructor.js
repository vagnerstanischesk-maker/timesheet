/**********************************************
* table_constructor.js                        *  
***********************************************
* Cabo/JSUI version 0.5 November, 1999        *
* Written by Robert Hoexter                   *
* Oracle Corporation                          *
**********************************************/
//requires Cabo_Utilities.js

/**********************************************************************************
As of version 0.5, only defines and builds tables which have no widget
interaction required other than row selection 
**********************************************************************************/

/**********************************************************************************
Miscellaneous constants and variables
**********************************************************************************/
var emptyCell = "<td class=SsSsS>&nbsp;</td>"

/**********************************************************************************
table prototype object and methods
p_type not allowed yet
**********************************************************************************/
table.prototype.validargs = p_name + p_widthpercent + p_width + p_color + p_grid + p_align;
table.prototype.name = "";
table.prototype.width = "";
table.prototype.widthpercent = "1%";
table.prototype.align = "left";
table.prototype.type = "indented";
table.prototype.setting = "color";
table.prototype.grid = "none";
table.prototype.selecttype = "none";
table.prototype.numbered = "";
table.prototype.selectCell = new Object();
table.prototype.selectColumn = new Object;
table.prototype.displayOnly = null;
table.prototype.formObjRef = "";
table.prototype.formName = ""
table.prototype.windowRef = "";
table.prototype.topHtml = "";
table.prototype.bottomHtml = "";
table.prototype.verticalHtml = "<td width=1 class=tablesurround><img src=" + image_dir + "pixel_color4.gif width=1></td>";
table.prototype.calcTopHtml = calcTopHtml;
table.prototype.calcBottomHtml = calcBottomHtml;
table.prototype.addColumn = addColumn;
table.prototype.render = renderTable;
table.prototype.renderTop = renderTop;
table.prototype.renderBottom = renderBottom;
table.prototype.addSelectColumn = addSelectColumn;
table.prototype.setDataSource = setDataSource;
table.prototype.getSelected = getSelected;
table.prototype.buildFormRef = buildFormRef;
table.prototype.setSelectColumn = setSelectColumn;
table.prototype.deleteRows = deleteRows;
table.prototype.removeRows = removeRows;

/* Original table routine methods - keep and roll into new version
defineTable.prototype.radio = new Array();
//Data table manipulation methods
defineTable.prototype.addcolumn = addcol;
defineTable.prototype.addrow = addrow;
defineTable.prototype.deleterow = deleterow;
defineTable.prototype.getcell = getcell;
defineTable.prototype.putcell = putcell;
defineTable.prototype.getrow = getrow;
defineTable.prototype.putrow = putrow;
//interactive table stuff
defineTable.prototype.refreshwidgets = refreshwidgets;
defineTable.prototype.setradio = setradio;
//should be a method on the coloumn objects now
//defineTable.prototype.setcolproperty = setcolproperty;
*/

//**********************************************************************************
function table (P_args) // table object constructor function 
{ 
  if (!P_args) return;
  args = P_args.split(";"); 
  for (var i=0; i < args.length; i++) 
  {
    var arg = args[i];
    if (!arg) break; 
    parsevalues(arg,this,this.validargs);
  }
  this.columns = new Array();
  this.data = new Array();
  this.selectedrows = new Array();
  this.topHtml = this.calcTopHtml();
  this.bottomHtml = this.calcBottomHtml();
}
//**********************************************************************************
function calcTopHtml() //builds top of rounded table and store the HTML string as a table object property
{
  var output= "<table cellpadding=0 cellspacing=0 border=0 align=" + this.align + " ";
  if (this.width && !this.widthpercent) output += " width=" + parseFloat(this.width);
  if (this.widthpercent && !this.width) output += " width=" + parseFloat(this.widthpercent) + "%"; 
  output += "><tr><td rowspan=2></td><td></td><td class=darkshadow width=10000>" +
  "<img src=" + image_dir + "pixel_color1.gif height=1 width=1></td><td></td><td rowspan=2></td></tr>" +
  "<tr><td><img src= "
  output += (this.setting == "white") ? image_dir + "table_top_left_onwhite.gif" : image_dir + "table_top_left.gif"; 
  output += " width=5 height=5></td><td class=tableheader width=10000>" + 
  "<img src=" + image_dir + "pixel_color4.gif width=1 height=1></td><td><img src="
  output += (this.setting == "white") ? image_dir + "table_top_right_onwhite.gif" : image_dir + "table_top_right.gif";
  output += " width=5 height=5></td></tr><tr>" +
  "<td class=darkshadow><img src=" + image_dir + "pixel_color1.gif></td><td class=tablesurround colspan=3>"
  return output;
}
//**********************************************************************************
function calcBottomHtml() //Build bottom of rounded table, and store the HTML string as a table object property
{
  var output = "</td><td class=";
  output += (this.setting == "white") ? "panel" : "highlight";
  output += "><img src=";
  output += (this.setting == "white") ? image_dir + "pixel_gray5.gif" : image_dir + "pixel_color6.gif";
  output += "></td></tr><tr><td rowspan=2></td><td><img src=";
  output += (this.setting == "white") ? image_dir + "table_bottom_left_onwhite.gif" : image_dir + "table_bottom_left.gif";
  output += " width=5 height=5></td><td class=tableheader width=10000>" +
  "<img src=" + image_dir + "pixel_color3.gif></td><td><img src=" ;
  output += (this.setting == "white") ? image_dir + "table_bottom_right_onwhite.gif" : image_dir + "table_bottom_right.gif";
  output += " width=5 height=5></td><td rowspan=2></td></tr><tr><td>" +
  "</td><td class="
  output += (this.setting == "white") ? "panel" : "highlight";
   output += " width=1000><img src="
  output += (this.setting == "white") ? image_dir + "pixel_gray5.gif" : image_dir + "pixel_color6.gif";
  output += "></td><td></td></tr></table>";
  return output;
}
//**********************************************************************************
function renderTable(window_Ref, form_ref) //The core of this library, this function writes the HTML for the entire table
{
  //If there is no data table, or no rows of data = skip the render process altogether.
  if (!this.data || this.data.length < 1) return;
  //the output is initialized by placing the previously built HTML for the top of the table structure in it
  output = this.topHtml;
  //start the data table.  This is always structured this way.  Spacing is adjusted if there is a full grid.
  output += "<table cellpadding=0 border=0 width=100% cellspacing=";
  output += (this.grid == "both") ? "1" : "0";
  output += "><tr>";
  //write the empty cell for the select column header if there is one
  if (this.selecttype != "none") output += "<td class=headercell>&nbsp;</td>";
  //add the vertical grid cell after the select column if specified
  if (this.grid=="vertical") output += this.verticalHtml;
  //write the column headings by reading through the column objects
  for (k=0; k<this.columns.length; k++)
  {
    output += this.columns[k].render();
    //write vertical grid cell if either grid is vertical or line specified specially for this column
    if ((this.columns[k].gridline && !(this.grid == "both")) || this.grid=="vertical") 
    { 
      output += this.verticalHtml;
    }
  }
  output += "</tr>";
  //read each row in the data array
  for (i=0; i<this.data.length; i++)
  { 
    output +="<tr>"
    //write the selection column for every row present in the data table
    if (this.selecttype != "none") 
    {
      output += this.selectCell.render(i);
    }
    //add the vertical grid cell after the select column if specified
    if (this.grid=="vertical") output += this.verticalHtml;
    //read through columns again.  Each one determines what to do with the data in the row
    //for interactive tables, objects containing cell data will be identified by column name.
    //for display-only tables, columns will indicate how many items to read from the data array.
    var d = 0;
    //d indexes the items in the row for display-only tables
    for (k=0; k<this.columns.length; k++)
    { 
      //test to see if the cell is a display-only object, which reads raw data items from the datasource
      if (this.columns[k].cellobject.displayOnly)
      { if (this.data[i][k].type)
	    {
		  //******************************* Data-only Object Reading approach **************************************
		  if (this.data[i][k].type == "dataOnly") 
		  { 
		    if (this.columns[k].cellobject.type == "displayText") parmarray = new Array(this.data[i][k].text);
			else 
			if (this.columns[k].cellobject.type == "displayLink") parmarray = new Array(this.data[i][k].text,
			  this.data[i][k].action);
			else parmarray = new Array(this.data[i][k].image,this.data[i][k].action,this.data[i][k].text);
			output += this.columns[k].cellobject.render(parmarray);
		  }
		  else if (this.data[i][k].type == "rawDataOnly")
		  {
		    output += this.columns[k].cellobject.render(this.data[i][k].rawdata);
		  }
		  //******************************* end Data-Only Object reading approach **********************************
		}
		else
		{
 	      //******************************* Raw Data Reading approach **********************************************
          //add the HTML from the output of the column's cell object's render method.  Since the cell is a display
          //only cell, it reads from a raw data array rather than looking for an object.  The array slice() method
          //creates an array that is a subset of the data array and that is passed to the render method
          output += this.columns[k].cellobject.render(this.data[i].slice(d,d+this.columns[k].cellobject.dataitems));
          //increment the item counter by the number of items the column specifies it requires
          d = d+this.columns[k].cellobject.dataitems;
		  //******************************* end Raw data Reading approach *******************************************
		}
        //write vertical grid cell if either grid is vertical or line specified specially for this column
        //add the vertical grid cell after the select column if specified
        if ((this.columns[k].gridline && !(this.grid == "both")) || this.grid=="vertical") 
        { 
          output += this.verticalHtml;
        }
      } 
      else 
      {
        //Interactive table rendering is controlled by the objects stored in the data array.  Each item in
        //that array is an object which contains attributes for its own value, visual characteristics and
        //placement in the table (rowspan, colspan etc.) The HTML for each of these cells is added to the
        //table HTML here.
        output += this.data[i][this.columns[k]].render();
      }
    }
    output += "</tr>"
    //A row with no cells is written if a horizontal-only grid is specified.  This results in a one-pixel gap
    //through which the background color will show through creating a gridline.
    if (i < this.data.length - 1 && this.grid == "horizontal") output += "<tr></tr>";
  }
  //The inner table of data is closed, and the HTML for the rounded corners and background is completed.
  output += "</table>" + this.bottomHtml;
  //The resulting HTML for the table is written into the document object passed to this method.
  if (window_Ref) 
  { 
    if (window_Ref == "HTML") 
	{ 
	  return output; 
	}
	else 
	{ 
	  window_Ref.document.write(output); 
    }
  }
  else 
  {
    document.write(output);
  }
  this.buildFormRef(window_Ref,form_ref);
  if (this.selecttype != "none") this.setSelectColumn();
  return "";
}
//**********************************************************************************
function renderTop(window_Ref) //output the html which draws the top of the rounded table - from before any contents
{
  if (window_Ref) 
  { 
    if (window_Ref == "HTML") 
	{ 
	  return this.topHTML; 
	}
	else 
	{ 
	  window_Ref.document.write(this.topHTML); 
	  return "";
    }
  }
  else 
  {
    document.write(this.topHTML);
	return "";
  }
}
//**********************************************************************************
function renderBottom(window_Ref) //output the html which draws the bottom of the rounded table from after any contents
{
  if (window_Ref) 
  { 
    if (window_Ref == "HTML") 
	{ 
	  return this.bottomHTML; 
	}
	else 
	{ 
	  window_Ref.document.write(this.bottomHTML); 
	  return "";
    }
  }
  else 
  {
    document.write(this.bottomHTML);
	return "";
  }
}
//**********************************************************************************
function addColumn (P_obj) //inserts an object reference into the tables array of columns
//and tests to see if display-only and interactive columns are mixed in the same table
{ 
  this.columns[this.columns.length] = P_obj;
  if (this.displayOnly == null)
  {
    this.displayOnly = P_obj.cellobject.displayOnly;
  }
  else
  { 
    if (this.displayOnly != P_obj.cellobject.displayOnly) 
	{
	  alert("Developer Alert: Display Only and interactive cells cannot be used in the same table")
	}
  }
}
//**********************************************************************************
function addSelectColumn(P_args) //creates a select column cell object and column object in the table object
{  
  var validargs = p_selecttype + p_numbered;
  if (!P_args) return;
  args = P_args.split(";"); 
  for (var i=0; i < args.length; i++) 
  {
    var arg = args[i];
    if (!arg) break; 
    parsevalues(arg,this,validargs);
  }
  if (this.selecttype == "none") return;
  this.selectCell = new selectCell(this.selecttype, this.numbered, this.name)
  this.selectColumn = new column("cellobject:" + this.name + ".selectCell;");
}
//**********************************************************************************
function setDataSource (P_obj, P_clear) //assigns a reference to a data/object array to the table object
{ 
  if (typeof P_obj != "object") return false;
  this.data = P_obj;
  //When a new datasource is assigned, the selected rows are cleared from the table variables
  //unless instructed not to
  if (P_clear == true)
  {
    if (this.selecttype == "multiple") this.data.selectedrows = new Array();
    else this.data.selectedrow = 0;
  }
  return true;
}
//**********************************************************************************
function getSelected () //returns either an array or a single value for rows selected in the data array
{
  if (this.selecttype == "multiple") 
  {
    var output = new Array;
    for (x=0;x<this.data.selectedrows.length; x++) 
    {
      if (this.data.selectedrows[x] == true) output[output.length] = x+1;
    }
    return output;
  } 
  else 
  {
    output = parseFloat(this.data.selectedrow);
    return output+1;
  }
}
//**********************************************************************************
function buildFormRef(window_Ref,form_ref) //Build object reference to current form where the table is located
{ 
//These values are used when widgets and the selection column objects are updated, to point
//directly to the table. This method is called whenever the table is rendered, and the values are
//updated if the table object is rendered in a different document, a rare occurance.  Problems
//will arise if the table object is rendered in a different form in the same document.  
  if (this.windowRef == window_Ref) return;
  this.windowRef = window_Ref;
  if (typeof form_ref == "object") 
  {
    this.formObjRef = form_ref;
  }
  else
  {
    if (window_Ref.document.forms.length>1) 
    {
      if (form_ref) 
    {
        this.formObjRef = window_Ref.document.forms[form_ref]; 
      }
      else  
      {
        window.alert("Developer: Reference to form not passed to table buildFormRef method");
        this.formObjRef = "undefined"; 
        this.formName = "";
        return;
      }
    } 
    else
    {
    //The form can be extracted from the window object if there is only one form in the document.
      this.formObjRef = window_Ref.document.forms[0];
    }
  }
  //build complete window and form reference string
  var p_window_name = ""
  var winobj = window_Ref
  var top_name = top.name
  while (top_name != winobj.name ) 
  {
    p_window_name = winobj.name + "." + p_window_name;
    winobj = winobj.parent;
  }
  this.formName = p_window_name + "document." + this.formObjRef.name;
}
//**********************************************************************************
function setSelectColumn() //process through data and sets the selection column to show what is checked
{ 
  if (this.selecttype == "multiple") 
  {
    for (k=0; k<this.data.length; k++) 
    {
      if (this.data.selectedrows[k] == true) 
      {
        //this.formObjRef["row"+k].checked = true  //This should work, doesn't in IE, does in Netscape!
		//  The following line works in both browsers, but is something of a kludge.
		eval("top." + this.formName + ".row" + k).checked = true;
      }
      else 
      {
        //this.formObjRef["row"+k].checked = false  //This should work but doesn't.  see above.
		eval("top." + this.formName + ".row" + k).checked = false;
      }
    }
  } 
  else 
  { 
    if (!this.data.selectedrow) this.data.selectedrow = 0;
    this.formObjRef.Selectbox[parseFloat(this.data.selectedrow)].checked = true;
  }
}

/**********************************************************************************
column prototype object and methods
**********************************************************************************/
column.prototype.validargs = p_name + p_text + p_cellobject + p_wrap + p_align;
column.prototype.name = "";
column.prototype.text = "&nbsp;";
column.prototype.cellobject = null;
column.prototype.colspan = 1;
column.prototype.align = "";
column.prototype.style = "tableheader";
column.prototype.width = 0;
column.prototype.totaled = false;
column.prototype.skiprow = 0;
column.prototype.html = "";
column.prototype.calcHtml = calcColumnHtml;
column.prototype.render = renderColumn;

//**********************************************************************************
function column ( P_args) //column object constructor function
{ 
  if (!P_args) return;
  args = P_args.split(";"); 
  for (var i=0; i < args.length; i++) 
  {
    var arg = args[i];
    if (!arg) break; 
    parsevalues(arg,this,this.validargs);
  }
  //the cell object name is passed in as a string.  the eval transforms it to an object reference
  if (this.cellobject) this.cellobject = eval(this.cellobject);
  //if no alignment is specified for the column (header) alignment, it defaults to match the data cell
  if (!this.align) this.align = this.cellobject.align;
  
  //styles for the header include alignment.  - Bi-Di support will further assign these as needed
  if (this.align == "center") { this.style = "tableheadercenter" }
  else if (this.align == "left") { this.style = "tableheaderleft" }
  else if (this.align == "right") { this.style = "tableheaderright" }
  else { this.style = "tableheader" }  
  
  this.html = this.calcHtml();
}
//**********************************************************************************
function calcColumnHtml() //build the HTML for the column header cell.  This has the text built into it
{ 
  output = "<td class=" + this.style;
  output += (this.wrap=="false")?" nowrap":"";
  output += ">&nbsp;"+ this.text + "&nbsp;</td>";
  return output;
}
function renderColumn() //simply return the html for the column header cell.
{ 
  return this.html;
}

/**********************************************************************************
selection cell prototype object and methods
**********************************************************************************/
selectCell.prototype.type = "Selection";
selectCell.prototype.align = "left";
selectCell.prototype.style = "tablecell";
selectCell.prototype.html = "";
selectCell.prototype.render = renderSelectCell;

//**********************************************************************************
function selectCell(P_type, P_numbered, P_tableName) // Selection cell constructor method
//This cell is built via a call from the table 'addSelectColumn' method which is called as
//part of adding columns to a table during definition.  Therefore, the parameters to this are
//always fixed and no name:value parsing is necessary.
//*** If needed, code which produces disabled selection cells may be added.
//*** A third type of selection, actually an indicator arrow will be added as a second specialized
//*** column type.
{
  output = "<td class=tablecell>"
  if (P_type == "multiple") 
  {
    output += "<input type=checkbox name='rowRrRrR'"
    output += " onclick= '" + P_tableName + ".data.selectedrows[RrRrR] = this.checked'" 
  } 
  else 
  {
    output += "<input type=radio class=tablecell name='Selectbox' value='RrRrR'";
    output += " onclick= '" + P_tableName + ".data.selectedrow = this.value'";
  }
  output += ">";
  if (P_numbered == "true") { output += "&nbsp;NnNnN"; }
  output += "</td>";
  this.html = output;
 }
//**********************************************************************************
function renderSelectCell(P_row) //render the cell by replacing tokens
{ 
  output =  this.html.replace(reprow,P_row);
  output = output.replace(reprow,P_row);
  output = output.replace(repnum,P_row + 1);
  return output;
}

/**********************************************************************************
Display only text cell prototype and methods
**********************************************************************************/
displayTextCell.prototype.validargs = p_style + p_wrap + p_align;
displayTextCell.prototype.type = "displayText";
displayTextCell.prototype.displayOnly = true;
displayTextCell.prototype.align = "left";
displayTextCell.prototype.dataitems = 1;
displayTextCell.prototype.wrap = "true";
displayTextCell.prototype.style = "tablecell";
displayTextCell.prototype.html = "<td>&nbsp;</td>";
displayTextCell.prototype.render = renderDisplayTextCell;
displayTextCell.prototype.calcHtml = calcDisplayTextHtml;

//**********************************************************************************
function displayTextCell(P_args) // display-only text cell constructor method
{ 
  if (P_args)
  {  args = P_args.split(";"); 
    for (var i=0; i < args.length; i++) 
    {
      var arg = args[i];
      if (!arg) break; 
      parsevalues(arg,this,this.validargs);
    }
  }
  this.html = this.calcHtml();
}
//**********************************************************************************
function calcDisplayTextHtml () //construct the html for the cell, including tokens for data replacement
{
  output = "<td class=" + this.style + " align=" + this.align;
  output += (this.wrap=="false")?" nowrap":"";
  output += ">&nbsp;TtTtT&nbsp;</td>" 
  return output;
}
//**********************************************************************************
function renderDisplayTextCell(indata) //render the cell by replacing tokens
{ //indata array contains only one item: indata[0] = the text to display
  //draw an empty cell with the correct style if no data passed to function
  if (indata.length == 0) return emptyCell.replace(repstl,this.style);
  //replace the token with the passed-in text and return the cell html
  return this.html.replace(reptxt,indata[0]);
}

/**********************************************************************************
Display Only link cell prototype and methods
**********************************************************************************/
displayLinkCell.prototype.validargs = p_style + p_wrap + p_actiontype + p_align + p_defaultaction + p_targetframe;  
displayLinkCell.prototype.type = "displayLink";
displayLinkCell.prototype.displayOnly = true;
displayLinkCell.prototype.dataitems = 2;
displayLinkCell.prototype.wrap = "false";
displayLinkCell.prototype.align = "left";
displayLinkCell.prototype.style = "tablecell";
displayLinkCell.prototype.actiontype = "url";
displayLinkCell.prototype.defaultaction = "#";
displayLinkCell.prototype.targetframe = "_self";
displayLinkCell.prototype.html = "<td>&nbsp;</td>";
displayLinkCell.prototype.render = renderDisplayLinkCell;
displayLinkCell.prototype.calcHtml = calcDisplayLinkHtml;

//**********************************************************************************
function displayLinkCell(P_args) //display-only link cell constructor method
{ 
  if (P_args)
  {  args = P_args.split(";"); 
    for (var i=0; i < args.length; i++) 
    {
      var arg = args[i];
      if (!arg) break; 
      parsevalues(arg,this,this.validargs);
    }
  }
    this.html = this.calcHtml();
}
//**********************************************************************************
function calcDisplayLinkHtml() //construct the html for the cell, including tokens for data replacement
{ 
  output = "<td class=" + this.style + " align=" + this.align;
  output += (this.wrap=="false")?" nowrap":"";
  output += ">&nbsp;<a href='";
  output += (this.actiontype=="url")?"UuUuU'":"javascript:void 'JjJjJ'";
  output += (this.targetframe=="")?">":" target=" + this.targetframe + ">";
  output += "TtTtT&nbsp;</td>";
  return output;
}
//**********************************************************************************
function renderDisplayLinkCell(indata) //render the cell by replacing tokens
{ //indata array contains two items: indata[0] = text, indata[1] = the url or action.
  //draw an empty cell with the correct style if no data passed to function
  if (indata.length == 0) return emptyCell.replace(repstl,this.style); 
  //perform a series of token replacements using the passed-in data
  output = this.html.replace(reptxt,indata[0]);
  //build action string based upon type (url or function), and whether or not an action was passed in
  var action = (indata[1])?indata[1]:(this.actiontype=="url")?this.defaultaction:"";
  output = output.replace(repurl,action)
  output = output.replace(repfun,action);
  return output;
}

/**********************************************************************************
display only icon/image cell prototype and methods
**********************************************************************************/
displayIconCell.prototype.validargs = p_style + p_wrap + p_actiontype + p_defaultaction + p_targetframe + p_dataitems + p_iconname + p_textposition + p_align;  
//note that this prototype, unlike the other display-only cells, allows a change to the dataitems number.  This is
//because the application may need a different icon on each row, or may use the same one in every row, in which
//case the iconname from this object will be used instead of the second data item.  similarly, if there is to be
//text rendered with the icon, then the dataitems number would be changed to 3 to make the routine read 3 data items
//from the row, the third being the text to show.
displayIconCell.prototype.type = "displayIcon";
displayIconCell.prototype.displayOnly = true;
displayIconCell.prototype.dataitems = 3;
displayIconCell.prototype.align = "left";
displayIconCell.prototype.wrap = "false";
displayIconCell.prototype.iconname = "";
displayIconCell.prototype.actiontype = "none";
displayIconCell.prototype.defaultaction = "#";
displayIconCell.prototype.targetframe = "";
displayIconCell.prototype.style = "tablecell";
displayIconCell.prototype.textposition = "after";
displayIconCell.prototype.render = renderDisplayIconCell;
displayIconCell.prototype.calcHtml = calcDisplayIconHtml;

//**********************************************************************************
function displayIconCell(P_args) //display-only icon constructor method
{ 
  if (P_args)
  {  args = P_args.split(";"); 
    for (var i=0; i < args.length; i++) 
    {
      var arg = args[i];
      if (!arg) break; 
      parsevalues(arg,this,this.validargs);
    }
  }
    this.html = this.calcHtml();
}
//**********************************************************************************
function calcDisplayIconHtml() //construct the html for the cell, including tokens for data replacement
{ 
//slightly more complex, this cell is both a link around the image and displays text either before or 
//after the image in the cell if desired
  output = "<td class=" + this.style + " align=" + this.align;
  output += (this.wrap=="false")?" nowrap":"";
  output += ">";
  if (this.textposition == "before") output += "&nbsp;TtTtT&nbsp;";
  if (this.actiontype == "url" || this.actiontype == "link")
  { 
    output += "<a href='";
    output += (this.actiontype=="url")?"UuUuU'":"javascript:void 'JjJjJ'";
    output += (this.targetframe=="")?">":" target=" + this.targetframe + ">";
    output += "<img src='IiIiI' border=no align=absmiddle></a>";
  } 
  else 
  { 
    output += "<img src='IiIiI' align=absmiddle>";
  }
  if (this.textposition == "after") output += "&nbsp;TtTtT&nbsp;";
  output += "</td>";
  return output;
}
//**********************************************************************************
function renderDisplayIconCell(indata)
//All items are optional.  If none is sent, just the default icon will fill the cell if present.
//indata array can contain 3 items: indata[0] = action (url or function), 
//indata[1] =icon file name to override default, indata[2]text to show beside icon
{ 
  //draw an empty cell with the correct style if no data passed to function
  if (indata.length == 0) return emptyCell.replace(repstl,this.style);
  output = this.html;
  //perform a series of token replacements using the passed-in data
  //build action string based upon type (url or function), and whether or not an action was passed in
  var action = (indata[1])?indata[1]:(this.actiontype=="url")?this.defaultaction:"";
  output = output.replace(repurl,action)
  output = output.replace(repfun,action);
  //insert the icon name, either passed in or from the object default
  output = output.replace(repicn,(indata[0]=="" || indata[0]==null)?this.iconname:indata[0]);
  //if text was passed, replace it 
  output = output.replace(reptxt,(indata[2])?indata[2]:"");
  return output;
}

/**********************************************************************************
Data Manipulation functions - act on the table object data array object reference
**********************************************************************************/
function dispArray () //constructor for the simple data array object.  The object is an array, which
//cannot be created as a prototype 
{ 
  cleanarray = new Array();
  cleanarray.selectedrows = new Array()
  cleanarray.selectedrow = 0;
  cleanarray.name = "good data"
  return cleanarray;
}

function deleteRows() 
{
  var deletecount = 0;
  if (deleteRows.arguments.length == 0) 
  { 
    deletecount = 1;
  } 
  else
  {
    for (i=0; i<deleteRows.arguments.length; i++) 
	{
      var row = deleteRows.arguments[i] - 1;
      this.data[row] = null;
      deletecount++; 
	  //if the row deleted is the row selected in the radio group, then selected row reverts to the first row
	  if (this.selecttype != "multiple" && this.data.selectedrow == row) this.data.selectedrow = 0;
    }
  }
  this.data = this.removeRows();
}
//**********************************************************************************
function removeRows() //compress the null rows from the table and clean up the selected row values
// this acts like a constructor function and replaces the data array rather than referencing another object
{
  cleanArray = new Array();
  //initialize the selectedrow attribute
  cleanArray.selectedrows = new Array();
  cleanArray.selectedrow = this.data.selectedrow;
  var x=0;
  for (i=0;i<this.data.length; i++)
  { 
    if (this.data[i] != null) 
    {
      cleanArray[cleanArray.length] = this.data[i];
	  if (this.selecttype == "multiple")
      {
        cleanArray.selectedrows[i] = this.data.selectedrows[x];
        x++;
      }
    }
  }
  return cleanArray;
}
