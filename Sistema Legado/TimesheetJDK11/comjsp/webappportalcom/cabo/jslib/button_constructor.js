/**********************************************
* button_constructor.js                       *  
***********************************************
* Cabo/JSUI version 0.5 November, 1999        *
* Written by Robert Hoexter                   *
* Oracle Corporation                          *
**********************************************/

var leftImage = new Array();
leftImage.RD = image_dir + "button_left_round_default.gif";
leftImage.SD = image_dir + "button_left_square_default.gif";
leftImage.R = image_dir + "button_left_round.gif";
leftImage.S = image_dir + "button_left_square.gif";
var rightImage = new Array();
rightImage.RD = image_dir + "button_right_round_default.gif";
rightImage.SD = image_dir + "button_right_square_default.gif";
rightImage.R = image_dir + "button_right_round.gif";
rightImage.S = image_dir + "button_right_square.gif";
var gapSize = new Array()
gapSize.narrow = "3";
gapSize.wide = "15";
var drop_steps = 0;

/**********************************************************************************
button bar prototype and methods
**********************************************************************************/
buttonRow.prototype.addButton = addButton;
buttonRow.prototype.render = renderButtonRow;

//**********************************************************************************
function buttonRow()
{ 
  //no arguments for a row other than the buttons that may be in it
  this.buttons = new Array();
  
  //specify buttons with one command - overloaded constructor function, but useful
  if (buttonRow.arguments.length > 0)
  {
    for (i=0; i<buttonRow.arguments.length; i++)
	{ 
	  this.buttons[this.buttons.length] = buttonRow.arguments[i];
	}
  }
}
//**********************************************************************************
function addButton (p_obj)
{ 
  this.buttons[this.buttons.length] = p_obj;
}
//**********************************************************************************
function renderButtonRow(window_Ref)
{
  tempRow = new Array("","","","","");
  for (i=0; i<this.buttons.length; i++)
  {
    tempRow[0] += this.buttons[i].getRow(0)
    tempRow[1] += this.buttons[i].getRow(1)
    tempRow[2] += this.buttons[i].getRow(2)
    tempRow[3] += this.buttons[i].getRow(3)
    tempRow[4] += this.buttons[i].getRow(4)
  }
  output = "<table cellpadding=0 cellspacing=0 border=0>"
  output += "<tr><td height=4></td></tr>"
  output += "<tr>" + tempRow[0] +"</tr>"
  output += "<tr>" + tempRow[1] +"</tr>"
  output += "<tr>" + tempRow[2] +"</tr>"
  output += "<tr>" + tempRow[3] +"</tr>"
  output += "<tr>" + tempRow[4] +"</tr>"
  output += "</table>"

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


/**********************************************************************************
button prototype and methods
**********************************************************************************/
button.prototype.validargs = p_name + p_shape + p_text + p_actiontype + p_url + p_targetframe + p_action + p_enabled + p_defaultbutton + p_gap + p_locatorcontrol + p_locatorobject;
button.prototype.name = "";
button.prototype.shape = "RR";
button.prototype.text = "No Text"
button.prototype.actiontype = "url"
button.prototype.url = "#";
button.prototype.targetframe = "_self";
button.prototype.action = "";
button.prototype.enabled = "true"
button.prototype.defaultbutton = "false";
button.prototype.gap = "none";
button.prototype.type = "button";
button.prototype.locatorcontrol = "none"; //values are "next", "back", "none"
button.prototype.locatorobject = null;

button.prototype.calcHtml = calcButtonHtml;
button.prototype.render = renderSingleButton;
button.prototype.getRow = getButtonRow;
button.prototype.setButtonProperty = setButtonProperty;
//**********************************************************************************
function button (P_args)
{ 
  if (P_args)
  {
    args = P_args.split(";"); 
    for (var i=0; i < args.length; i++) 
    {
      var arg = args[i];
      if (!arg) break; 
      parsevalues(arg,this,this.validargs);
    }
  }
  //if (this.locatorobject) this.locatorobject = eval(this.locatorobject); 
  this.row = new Array(5);
  this.calcHtml();
}
//**********************************************************************************
function calcButtonHtml ()
{
  this.row[0] = "<td rowspan=5><a AaAaA><img src='IiIiI' border=no></a></td>" +
                "<td SsSsS><img src='IiIiI'></td>" +
                "<td rowspan=5><a AaAaA><img src='IiIiI' border=no></a></td>" +
                "<td width=NnNnN rowspan=5></td>";
  this.row[1] = "<td class=highlight><img src=" + image_dir + "pixel_color6.gif ></td>";
  this.row[2] = "<td class=button height=20 nowrap><a AaAaA class=SsSsS>TtTtT</a></td>"
  this.row[3] = "<td class=shadow><img src=" + image_dir + "pixel_gray3.gif ></td>";
  this.row[4] = "<td SsSsS><img src='IiIiI'></td>";
  //generate the command to take when clicking the button
  var command = ""
  if (this.enabled == "true") 
  { 
    command += "href=";
    if (this.actiontype=="url") command += "'" + this.url + "'";
    else
	{
	  command += "'javascript:void "
	  if (this.locatorcontrol == "back")  {command += this.locatorobject + ".reverse(window); "; }
      else if (this.locatorcontrol == "next") { command += this.locatorobject + ".advance(window); "; }
	  command +=  this.action + "'";
	}
    command += (this.targetframe=="")?"":" target=" + this.targetframe;
  }
  //replace the (href AaAaA) tokens with the command in both end images places
  this.row[0] = this.row[0].replace(repact,command);
  this.row[0] = this.row[0].replace(repact,command);
  //replace the left end image, depending upon the button shape
  var lkey = this.shape.charAt(0);
  lkey += (this.defaultbutton == "true")?"D":"";
  this.row[0] = this.row[0].replace(repicn,leftImage[lkey]);
  //replace the shadow cell image, depending upon if this is a default button 
  this.row[0] = this.row[0].replace(repicn,(this.defaultbutton == "true")?image_dir + "pixel_gray2.gif":image_dir + "pixel_transparent.gif");
  //replace the right end image, depending upon the button shape
  var rkey = this.shape.charAt(1);
  rkey += (this.defaultbutton == "true")?"D":"";
  this.row[0] = this.row[0].replace(repicn,rightImage[rkey]);		
  //set the class of the shadow cell depending upon if this is a default button
  this.row[0] = this.row[0].replace(repstl,(this.defaultbutton == "true")?"class=darkshadow":"");
  if (this.gap == null) this.row[0] = this.row[0].replace(repnum,"1");
  else
  {
    if (!isNaN(this.gap)) this.row[0] = this.row[0].replace(repnum,this.gap)
    else this.row[0] = this.row[0].replace(repnum,gapSize[this.gap]);
  }
  //replace the href (AaAaA) token in row 2
  this.row[2] = this.row[2].replace(repact,command)
  //set the style for the button text
  this.row[2] = this.row[2].replace(repstl,(this.enabled == "true")?"buttontext":"disabledbuttontext")
  //put in the button text
  this.row[2] = this.row[2].replace(reptxt,this.text);
  //set the shadow characteristics of row 4
  this.row[4] = this.row[4].replace(repstl,(this.defaultbutton == "true")?"class=darkshadow":"");
  this.row[4] = this.row[4].replace(repicn,(this.defaultbutton == "true")?image_dir + "pixel_gray2.gif":image_dir + "pixel_transparent.gif");
}
//**********************************************************************************
function renderSingleButton (window_Ref)
{
  output = "<table cellpadding=0 cellspacing=0 border=0>"
  output += "<tr>" + this.row[0] +"</tr>"
  output += "<tr>" + this.row[1] +"</tr>"
  output += "<tr>" + this.row[2] +"</tr>"
  output += "<tr>" + this.row[3] +"</tr>"
  output += "<tr>" + this.row[4] +"</tr>"
  output += "</table>"

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
//**********************************************************************************
function getButtonRow(ndx)
{
  return this.row[ndx];
}
//**********************************************************************************
function setButtonProperty (P_args)
{ 
  if (P_args)
  {
    args = P_args.split(";"); 
    for (var i=0; i < args.length; i++) 
    {
      var arg = args[i];
      if (!arg) break; 
      parsevalues(arg,this,this.validargs);
    }
  }
  this.calcHtml();
}
/**********************************************************************************
locator bar prototype and methods
**********************************************************************************/
locator.prototype.validargs = p_steps + p_maxchars + p_text + p_currentstep + p_backbuttonobject + p_nextbuttonobject;
locator.prototype.steps = 4;
locator.prototype.maxchars = 60;
locator.prototype.currentstep = 1;
locator.prototype.allsteps = 0;
locator.prototype.type = "locator";
locator.prototype.backbuttonobject = null;
locator.prototype.nextbuttonobject = null;

locator.prototype.calcHtml = calcLocatorHtml;
locator.prototype.render = renderSingleLocator;
locator.prototype.formatLocator = formatLocator;
locator.prototype.getRow = getLocatorRow;
locator.prototype.advance = advanceLocator;
locator.prototype.reverse = reverseLocator;
locator.prototype.setStep = setStep;

//**********************************************************************************
function locator (P_args)
{ 
  if (P_args)
  {
    args = P_args.split(";"); 
    for (var i=0; i < args.length; i++) 
    {
      var arg = args[i];
      if (!arg) break; 
      parsevalues(arg,this,this.validargs);
    }
  }
  this.row = new Array(5);
  this.calcHtml();
}
//**********************************************************************************
function setStep(p_step)
{
  this.currentstep = (p_step && !isNaN(p_step))?p_step:1;
}
//**********************************************************************************
function calcLocatorHtml()
{
  this.allsteps = this.text.split(">").length
  this.row[0] = "<td rowspan=5 ><img src=" + image_dir + "locator_bar_left.gif ></td>";
  this.row[0] += "<td class=darkshadow><img src=" + image_dir + "pixel_gray2.gif ></td>";
  this.row[0] += "<td rowspan=5 ><img src=" + image_dir + "locator_bar_right.gif ></td>";
  this.row[0] += "<td width=5 rowspan=5></td>";
  this.row[1] = "<td></td>";

  this.row[3] = "<td></td>";
  this.row[4] = "<td class=color4><img src=" + image_dir + "pixel_color4.gif ></td>"
  
  this.row[2] = new Array()
  for (i=1; i<(this.allsteps + 1); i++)
  { rowHtml = "<td height=20 nowrap><font class=promptwhite>TtTtT</font></td>";
    locString = this.formatLocator(i);
    this.row[2][i] = rowHtml.replace(reptxt,locString);
  }
}
//**********************************************************************************
function renderSingleLocator(window_Ref)
{ 
  output = "<table cellpadding=0 cellspacing=0 border=0>"
  output += "<tr>" + this.row[0] +"</tr>"
  output += "<tr>" + this.row[1] +"</tr>"
  output += "<tr>" + this.row[2][this.currentstep] +"</tr>"
  output += "<tr>" + this.row[3] +"</tr>"
  output += "<tr>" + this.row[4] +"</tr>"
  output += "</table>"

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

function getLocatorRow(ndx)
{
  return (ndx == 2)?this.row[2][this.currentstep]:this.row[ndx];
}
//**********************************************************************************
function formatLocator (p_current, p_drop_step) 
{
  var hold_current = p_current;
  var howmany = (this.steps > 1) ? this.steps - 1 : 1;
  if (p_drop_step) 
  {
    drop_steps ++
    howmany = (howmany > 1) ? howmany - drop_steps : 1;
  }
  var output = ""
  if (!this.text) return locator;
  if (!isNaN(p_current)) p_current = p_current - 1;
  var optarray = this.text.split(">");
  output += "<a title='On step " + hold_current + " of " + optarray.length +"'>";
  var lastindex = optarray.length - 1
  var w_start = 0;
  var w_end = (lastindex < howmany) ? lastindex : howmany;
  if (lastindex > howmany) 
  {
    if (p_current < howmany) 
	{ 
      w_start = 0; 
      w_end = howmany;
    }
    else 
	{
      if (p_current > lastindex-howmany) 
	  {
        w_start = lastindex - howmany; 
        w_end = lastindex;
      }
      else 
	  {
        w_start = p_current - 1; 
        w_end = p_current + (howmany - 1);
      }  
    }  
  }
  var check_length = 0;
  if (w_start > 0) 
  {
    output += " ... > ";
    check_length = 6;
  }
  for (var x=w_start; x<=w_end; x++) 
  {
    if (x == p_current) 
	{ 
      output += "<b>" + optarray[x] + "</b>";
    }
    else 
	{
      output += optarray[x];
    }
    check_length = check_length + optarray[x].length
    if (x < w_end) 
	{ 
      output += "&nbsp;&gt;&nbsp;";
      check_length = check_length + 3;
    }
  }
  if (w_end < optarray.length - 1) 
  {
    output += " > ... ";
    check_length = check_length + 6;
  }
  output += "</a>";
  if (check_length > this.maxchars) 
  {
    return formatLocator (hold_current, true);
  } 
  else 
  {
    return output;
  }
}

function advanceLocator(window_Ref) 
{ 
  nextobj = eval(this.nextbuttonobject)
  backobj = eval(this.backbuttonobject)  

  if (this.currentstep < this.allsteps) 
  {
    this.currentstep++
	backobj.enabled = "true";
	backobj.calcHtml();
  }
  if (this.currentstep == this.allsteps) 
  {
  	nextobj.enabled = "false";
	nextobj.calcHtml();
  }
  window_Ref.document.location.reload();
}

function reverseLocator(window_Ref) 
{ 
  nextobj = eval(this.nextbuttonobject)
  backobj = eval(this.backbuttonobject)  
  if (this.currentstep > 1) 
  {
    this.currentstep--
	nextobj.enabled = "true";
	nextobj.calcHtml();
  }
  if (this.currentstep == 1) 
  {
	backobj.enabled = "false";
	backobj.calcHtml();
  }
  window_Ref.document.location.reload();
}