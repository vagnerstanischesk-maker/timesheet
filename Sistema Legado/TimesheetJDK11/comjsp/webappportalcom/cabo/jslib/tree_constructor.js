/**********************************************
* tree_constructor.js                         *  
***********************************************
* Cabo/JSUI version 0.5 November, 1999        *
* Written by Robert Hoexter                   *
* Oracle Corporation                          *
**********************************************/

//a couple of Globals so data frames can work on the correct tree
var currentTreeObj = new Object();
var currentTreeFrame = null;
var insertIndex = null;

var plus_r = "tree_closed_single.gif";
var plus_rb = "tree_closed_top.gif";
var plus_rt = "tree_closed_bottom.gif";
var plus_rbt = "tree_closed_middle.gif";
var minus_r = "tree_open_single.gif";
var minus_rb = "tree_open_top.gif";
var minus_rt = "tree_open_bottom.gif";
var minus_rbt = "tree_open_middle.gif";
var line_tb = "tree_branch_none.gif";
var line_rb = "tree_branch_top.gif";
var line_rt = "tree_branch_bottom.gif";
var line_rbt = "tree_branch_middle.gif";
var folder_closed = "tree_folder_closed.gif";
var folder_open = "tree_folder_open.gif";
var node = "tree_leaf.gif";
var spacer = "pixel_transparent.gif";
var nextConnector = spacer;

var default_page = jslib_dir + "jsui_empty.html";

var folder_closed_img = new Image()
var folder_open_img = new Image()
folder_closed_img.src = image_dir + folder_closed
folder_open_img.src = image_dir + folder_open

tree.prototype.validargs = p_name + p_targetframe + p_dataframe;
tree.prototype.name = "";
tree.prototype.targetframe = "_self";
tree.prototype.dataframe = null;
tree.prototype.levelOneCount = 0;
tree.prototype.selectedRow = null;
tree.prototype.priorRow = null;
tree.prototype.render = renderTree;
tree.prototype.setDataSource = setTreeDataSource;
tree.prototype.toggle = toggle;
tree.prototype.loadPage = loadPage;
tree.prototype.checkForMoreData = checkForMoreData;
tree.prototype.insertRows = insertRows;
tree.prototype.countRowOne = countRowOne;

function tree (P_args) //tree object constructor
{ 
  if (!P_args) return;
  args = P_args.split(";"); 
  for (var i=0; i < args.length; i++) 
  {
    var arg = args[i];
    if (!arg) break; 
    parsevalues(arg,this,this.validargs);
  }
  this.data = new Array();
}


function renderTree(window_Ref) 
{
  if (this.levelOneCount == 0) this.countRowOne();
  var selectedshown = (this.selectedRow == null)?true:false;
  var lastshown = 0;
  output = ""
  var connector = new Array(spacer,line_tb,line_tb,line_tb,line_tb)
  show_level = 1;
  var currlevel = 1;
  var newlevel = 1;
  var rowcount = new Array(0,this.levelOneCount);
  
  //if there is no data in the source yet, return an empty string.  This is in the case that the frame containing
  //this tree is being loaded before the frame containing the data array is complete.  That data frame will reload
  //the tree frame when it is completely defined, so it is best to leave this frame blank until all the data is there.
  if (this.data.length == 0) return;
  
  output += "<table cellpadding=0 cellspacing=0 border=0>"
  for (i=0; i<this.data.length; i++)
  {  
  //check to make sure that the selectedrow wasn't skipped when rendering, and reset the selected row
    if (i > this.selectedRow && !selectedshown)
  { 
    if (this.data[lastshown].url == "") top.main.location = default_page;
      else top.main.location = this.data[lastshown].url;
    this.selectedRow = lastshown;
    this.render(window_Ref)
    return;
  }
    
    //set the current level, and the determine the level for the next loop
    currlevel = newlevel;
  //if there is an item count for this level, decrement it
    if (rowcount[currlevel] > 0) rowcount[currlevel]--;
  if (this.data[i].childcount > 0 && this.data[i].populated == "true")  
  {  
      newlevel = currlevel + 1;
      rowcount[newlevel] = (this.data[i].populated == "true")?this.data[i].childcount:0;
  }
  else // determine next level - (no lower than 1)
  {
    if (rowcount[currlevel] > 0) newlevel = currlevel;
    else
    {   
      newlevel = currlevel;
    while (rowcount[newlevel] == 0 && newlevel > 1) newlevel--;
    }
  }
  
    //establish the level above which to display by looking at the opened state of the current row
  if (this.data[i].state == "closed") { if (currlevel < show_level) show_level = currlevel; }
  else if (currlevel == show_level) show_level = currlevel + 1;
  
  
  //evaluate the display level - if beyond it, adjust counters and continue
  if (currlevel > 1 && show_level < currlevel) continue;
  //store the index of this row so it can become the selected row if necessary
  lastshown = i;
    //otherwise... write the output row

    output += "<tr><td nowrap valign=bottom>"  
    output += "<img align=left hspace=0 src='" + image_dir + "pixel_transparent.gif' height=22 width=16>";
  //this writes the vertical spacer lines to connect rows with their parents 
  for (x=1; x<currlevel; x++)
  {
    output += "<img align=left hspace=0 src='" + image_dir
    output += connector[x];
    output += "' width=16>"
  }
  
  
  if (this.data[i].childcount > 0) 
  {
    output += "<a name=" + i + " href='javascript:void ";
    if (this.targetframe != "_self")
    {
      output += "parent."
    }
    output += this.name + ".toggle(" + i +", window); ";
    output += "'><img align=left hspace=0 src='" + image_dir;
    output += whichConnector(currlevel,rowcount[currlevel],this.data[i].childcount,this.data[i].state,this.levelOneCount)
    connector[currlevel] = nextConnector;
    output += "' border=no></a>";
  }
  else 
  {
    output += "<img align=left hspace=0 src='" + image_dir;
    output += whichConnector(currlevel,rowcount[currlevel],this.data[i].childcount,"state",this.levelOneCount)
    connector[currlevel] = nextConnector;
    output +="' width=16>"; 
  }
  
  output += "<a href='javascript:void ";
  if (this.targetframe != "_self")
  {
    output += "parent."
  }
  output += this.name + ".loadPage(" + i + ", window )' ";
  output += " onDblClick = '";
  if (this.targetframe != "_self")
  {
    output += "parent."
  }
  output += this.name + ".toggle(" + i +", window)'>"
  output += "<img align=left hspace=0 src='" + image_dir;
  if (this.data[i].childcount > 0) output += (this.selectedRow == i)?folder_open:folder_closed;
  else output += (this.data[i].nodeimage != "")?this.data[i].nodeimage:node;
  output += "' border=no name=folder" + i + ">";
    output += "<span id='select" + i + "' class="
  if (i == this.selectedRow) 
  { 
    output += "treeselected";
    selectedshown = true;
  }
  else output += "tree";
  output += ">" + this.data[i].text + "</span></a>";
    output+= "</td></tr>";
  
  } //end of loop
  output += "</table>"
  //final check, in case the last row was the selected row and the node is now closed
     if (!selectedshown)
  { 
    if (this.data[lastshown].url == "") top.main.location = default_page;
      else top.main.location = this.data[lastshown].url;
    this.selectedRow = lastshown;
    this.render(window_Ref)
    return;
  }
  
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

function whichConnector(level,levelcount,children,state,levelonecount)
{ 
  if (children == 0) //draw lines only, no pluses or minuses
  {
    if (level > 1) //not the first/primary level
    {
      if (levelcount > 0) //first or middle node on this level
      {  
        nextConnector = line_tb;
        return line_rbt; 
      }
      //otherwise, must be last or only node
      nextConnector = spacer;
      return line_rt;
    }
    else //level = 1
    {
      if (levelonecount == 1) //only one node at the top level
      {  
        nextConnector = spacer;
        return spacer; 
      }
      if (levelcount == 0) //levelonecount > 1 and this is the last node this level
      {
        nextConnector = spacer;
        return line_rt;
      }
      if (levelcount == levelonecount - 1) //levelonecount > 1, first node of several
      { 
        nextConnector = line_tb;
        return line_rb;
      } //otherwise, this is a middle node
      nextConnector = line_tb;
      return line_rbt;
    }
  } 
  else //childcount > 0
  {
    if (level > 1) //not the first/primary level
    {
      if (levelcount > 0) //first or middle node on this level
      {  
        nextConnector = line_tb;
        return (state=="open")?minus_rbt:plus_rbt; 
      }
      //otherwise, must be last or only node
      nextConnector = spacer;
      return (state=="open")?minus_rt:plus_rt; 
    }
    else //level = 1
    {
      if (levelonecount == 1) //only one node at the top level
      {  
        nextConnector = spacer;
        return (state=="open")?minus_r:plus_r; 
      }
      if (levelcount == 0) //levelonecount > 1 and this is the last node this level
      {
        nextConnector = spacer;
        return (state=="open")?minus_rt:plus_rt; 
      }
      if (levelcount == levelonecount - 1) //levelonecount > 1, first node of several
      {
        nextConnector = line_tb;
        return (state=="open")?minus_rb:plus_rb; 
      } //otherwise this is a middle node
      nextConnector = line_tb;
      return (state=="open")?minus_rbt:plus_rbt; 
    }
  } 
}
function countRowOne ()
{ 
//this duplicates the row-counting process of the tree render function, in order to determine the first 
//level "orphan" count - which may be passed in to the tree object when it is created should that be known.  It
//is, however, a data-related number and should be independent of the tree object.
  var currlevel = 1;
  var newlevel = 1;
  var rowcount = new Array();
  for (i=0; i<this.data.length; i++)
  {  
    currlevel = newlevel;
    if (rowcount[currlevel] > 0) rowcount[currlevel]--;
  if (this.data[i].childcount > 0 && this.data[i].populated == "true")  
  {  
      newlevel = currlevel + 1;
      rowcount[newlevel] = (this.data[i].populated == "true")?this.data[i].childcount:0;
  }
  else
  {
    if (rowcount[currlevel] > 0) newlevel = currlevel;
    else
    {   
      newlevel = currlevel;
    while (rowcount[newlevel] == 0 && newlevel > 1) newlevel--;
    }
  }
  if (currlevel == 1) this.levelOneCount++
  }
}

function loadPage (index, window_Ref)
{ 
  this.priorRow = this.selectedRow;
  this.selectedRow = index;
  
  if (index == this.priorRow) return; //either exits if row is already selected, or toggles on double-click
  
  if (this.data[index].url == "") top[this.targetframe].location = default_page;
  else top[this.targetframe].location = this.data[index].url;
 
  if (Nav4) reDrawTree(index,window_Ref)
  else 
  { 
    // it would be nice to pick up the attrubutes from the named styles... If I can figure it out
  if (this.priorRow != null)
  { 
    if (this.data[this.priorRow].childcount > 0)
    { var fieldname = "folder" + this.priorRow;
      window_Ref.document[fieldname].src = folder_closed_img.src }
    var oldRow = "select" + this.priorRow; //Style attributes match the "tree" style
    window_Ref.document.all(oldRow).style.color = "#000000";
      window_Ref.document.all(oldRow).style.fontWeight = "normal";
    window_Ref.document.all(oldRow).style.backgroundColor = window_Ref.document.bgColor;
  }
    if (this.data[index].childcount > 0)
    { var fieldname = "folder" + index; 
      window_Ref.document[fieldname].src = folder_open_img.src }
    var newRow = "select" + index; //Style attributes match the "treeselected" style
  window_Ref.document.all(newRow).style.color = "#FFFFFF";
    window_Ref.document.all(newRow).style.fontWeight = "bold";
    window_Ref.document.all(newRow).style.backgroundColor = "#000099";
  }
}
function toggle (index, window_Ref)
{ 
  if (this.data[index].childcount > 0) 
  {
    if (this.data[index].state == "closed") this.checkForMoreData(index);
    this.data[index].state = (this.data[index].state == "open")?"closed":"open";
  }
  else
  {
  this.data[index].state = "open";
  }
  if (Nav4) setTimeout(reDrawTree,250,index,window_Ref)
  else reDrawTree(index,window_Ref);
}

function checkForMoreData (index, dataHref )
{  
  if (this.data[index].moredatahref == null) return; // nothing here to indicate where to get the data rows
  if (this.dataframe == null) return; // no hidden data frame define, so no way to load extra lines;
  
  if (this.data[index].populated == "false" && this.data[index].childcount > 0)
  {
    top.currentTreeObj = this;
    insertIndex = index;
  //execute a procedure which populates the hidden tree frame with a page containing new data
  eval(this.dataframe).location.href = this.data[index].moredatahref;
    this.data[index].populated = "true";
  }
}

function reDrawTree(index,window_Ref)
{ 
  var hashpos = (window_Ref.location.href.indexOf("#"));
  if (hashpos > 0) newHref = window_Ref.location.href.substring(0,hashpos)
  else newHref = window_Ref.location.href;
  newHref += "#" + index; 
  window_Ref.location.reload();
  
  if (Nav4) setTimeout(setHref,250,window_Ref,newHref)
  else window_Ref.location.href = newHref;

}
function setHref (window_Ref,newHref)
{ window_Ref.location.href = newHref; }

function setTreeDataSource(array_ref)
{ 
  this.data = array_ref;
}
function insertRows (p_insert, insertIndex)
{  alert(this.data[insertIndex].state)
// returns reconstructed array to the mergeRows function in data_constructor.js 
   this.data[insertIndex].childcount = p_insert.length;
   if (p_insert.length == 0) return this.data;
  
  
   alert(this.data.join(",  ") + " " + insertIndex)
   var tempArray = this.data.slice(0,insertIndex+1)
   var tempArray = tempArray.concat(p_insert)
   return tempArray.concat(this.data.slice(insertIndex+1));
   
}

/*function rowAlt(p_text,p_level,p_state,p_childcount) //accepts parameters only
{
  this.text = p_text;
  if (p_level && !isNaN(p_level)) this.level = p_level;
  this.state = (p_state == "open")?p_state:"closed";
  this.childcount = (p_childcount)?p_childcount:0;
} */
