/**********************************************
* data_constructor.js                         *  
***********************************************
* Cabo/JSUI version 0.5 November, 1999        *
* Written by Robert Hoexter                   *
* Oracle Corporation                          *
**********************************************/
//used to construct data arrays for table_constructor.js and tree_constructor.js

function dispArray () //constructor for the simple data array object.  The object is an array, which
//cannot be created as a prototype 
{ 
  cleanarray = new Array();
  cleanarray.selectedrows = new Array()
  cleanarray.selectedrow = 0;
  cleanarray.addRow = addRow;
  return cleanarray;
}
function addRow(P_obj)
{ 
  this[this.length] = P_obj;
}

//constructor for the display-table data-only cell object that accepts name-value pairs
dataOnly.prototype.validargs = p_text + p_action + p_image;
dataOnly.prototype.type = "dataOnly";
dataOnly.prototype.text = null;
dataOnly.prototype.action = null;
dataOnly.prototype.image = null;
function dataOnly (P_args )
{ 
  if (!P_args) return;
  args = P_args.split(";"); 
  for (var i=0; i < args.length; i++) 
  {
    var arg = args[i];
    if (!arg) break; 
    parsevalues(arg,this,this.validargs);
  }
}
//constructor for the display-table data-only cell that just accepts raw values.
rawDataOnly.prototype.type = "rawDataOnly";
function rawDataOnly ()
{
  this.rawdata = new Array(rawDataOnly.arguments.length)
  for (x=0; x<rawDataOnly.arguments.length; x++)
  {
    this.rawdata[x] = rawDataOnly.arguments[x];
  }
}
  
//Constructor for the tree row object (tree_constructor.js)
row.prototype.validargs = p_level + p_state + p_text + p_childcount + p_url + p_populated + p_nodeimage + p_moredatahref;
// p_command + p_closedicon + p_openicon;
row.prototype.level = 0;
row.prototype.state = "closed";
row.prototype.text = "";
row.prototype.childcount = 0;
row.prototype.populated = "false"
row.prototype.url = "";
row.prototype.nodeimage = "";
row.prototype.moredatahref = null;

function row (P_args) //row object constructor for tree row object
{ 
  if (!P_args) return;
  args = P_args.split(";"); 
  for (var i=0; i < args.length; i++) 
  {
    var arg = args[i];
    if (!arg) break; 
    parsevalues(arg,this,this.validargs);
  }
  this.level = parseFloat(this.level);
  this.childcount = parseFloat(this.childcount);

}
function mergeRows (p_array)
{ 
  //references the variables defining the tree currently being worked on.  These variables are defined in the
  //tree_constructor.js library, which should always be copied at the frameset (top) level.
  alert(top.currentTreeObj)
  top.currentTreeObj.data = top.currentTreeObj.insertRows(p_array, top.insertIndex)
  eval("top." + top.currentTreeFrame).location.reload()
}
