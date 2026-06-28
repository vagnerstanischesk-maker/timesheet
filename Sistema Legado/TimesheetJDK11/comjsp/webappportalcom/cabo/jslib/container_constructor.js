/**********************************************
* container_constructor.js                    *  
***********************************************
* Cabo/JSUI version 0.5 November, 1999        *
* Written by Robert Hoexter                   *
* Oracle Corporation                          *
**********************************************/

/**********************************************************************************
tab control prototype object and methods
**********************************************************************************/
tabcontrol.prototype.validargs = p_title+p_helptext+p_objectref+p_targetframe+p_initialtab+p_lightcontainer;
tabcontrol.prototype.title = "No Title supplied";
tabcontrol.prototype.helptext = "";
tabcontrol.prototype.lightcontainer = false;
tabcontrol.prototype.objectref = "";
tabcontrol.prototype.targetframe = "";
tabcontrol.prototype.currentTab = 1;
tabcontrol.prototype.initialtab = 1;
tabcontrol.prototype.titlehtml ="<Table width=100% cellpadding=0 cellspacing=0 border=0><tr class=color3>" +
                        "<td rowspan=3><img src=" + image_dir + "pixel_color3.gif height=1 width=1></td>" +
                        "<td rowspan=3 nowrap><font class=tabtitle>TtTtT</font></td>" +
                        "<td rowspan=3 width=10000></td>";
tabcontrol.prototype.otherTab2 ="<td rowspan=2><img src=" + image_dir + "tab_left_non_selected.gif></td>" +
                                "<td class=othertab height=1><img src='" + image_dir + "pixel_color4.gif'></td>" +
                                "<td rowspan=2><img src='" + image_dir + "tab_right_non_selected.gif'></td>";
tabcontrol.prototype.otherTab3a="<td class=othertab nowrap valign=middle>";
tabcontrol.prototype.otherTab3b="<a class=othertabtext title='HhHhH' " +
                                "href='javascript:parent.BbBbB.switchtab(NnNnN,window,top.FfFfF," +
                                '"UuUuU")' + "'>TtTtT</a>"
tabcontrol.prototype.otherTab3c="</td>";
tabcontrol.prototype.otherTab4= "<td colspan=3 class=highlight><img src='" + image_dir + "pixel_color6.gif'></td>";
tabcontrol.prototype.anyTab1 =  "<td colspan=3><img src='" + image_dir + "pixel_color3.gif' height=6></td>";
tabcontrol.prototype.curTab2 =  "<td rowspan=2><img src='" + image_dir + "IiIiI.gif'></td>" +
                                "<td class=highlight height=1>" +
                                "<img src='" + image_dir + "pixel_color6.gif'></td><td rowspan=2>" + 
                                "<img src='" + image_dir + "IiIiI.gif'></td>";
tabcontrol.prototype.curTab3a = "<td class=SsSsS nowrap valign=middle>";
tabcontrol.prototype.curTab3b = "<font class=currenttabtext>TtTtT</font>";
tabcontrol.prototype.curTab3c = "</td>";
tabcontrol.prototype.curTab3d = "<a class=currenttabtext " +
                                "href='javascript:parent.BbBbB.switchtab(NnNnN,window,top.FfFfF," +
                                '"UuUuU")' + "'>TtTtT</a>"
tabcontrol.prototype.curTab4 =   "<td colspan=3 class=panel><img src='" + image_dir + "pixel_gray5.gif'></td>";
tabcontrol.prototype.curTab4l = "<td colspan=3 class=lightpanel><img src='" + image_dir + "pixel_color6.gif'></td>";
tabcontrol.prototype.disTab3a =  "<td class=othertab nowrap valign=middle>";
tabcontrol.prototype.disTab3b = "<a class=disabledtabtext title='HhHhH'>TtTtT</a>"
tabcontrol.prototype.disTab3c = "</td>";
tabcontrol.prototype.helphtml = "<tr class=SsSsS><td colspan=PpPpP height=200 valign=top><FONT CLASS=helptext>" +
                                "TtTtT</td></tr></table>";
tabcontrol.prototype.tabrow4a = "<tr><td rowspan=2 class=panel valign=top><img src='" + image_dir + "IiIiI.gif'></td>" +
                "<td colspan=2 class=highlight><img src='" + image_dir + "pixel_color6.gif'></td>";
tabcontrol.prototype.tabrow4b = "<td rowspan=2 class=panel align=top>" + 
                "<img src='" + image_dir + "IiIiI.gif'></td></tr>";  
//tabcontrol.prototype.iconhtml = "<a href='javascript:JjJjJ'><img src='IiIiI' align=absmiddle alt='HhHhH'></a>"
tabcontrol.prototype.render = rendertabs;
tabcontrol.prototype.renderbottom = renderbottom;
tabcontrol.prototype.addtab = addtab;
tabcontrol.prototype.switchtab = switchtab;
tabcontrol.prototype.modifytab = modifytab;
tabcontrol.prototype.loadTabPage = loadTabPage;
tabcontrol.prototype.setTabControlProperty = settabcontrolproperty;

//**********************************************************************************
function tabcontrol ( P_args) //tabcontrol object constructor function
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
  this.tabarray = new Array;
}

//**********************************************************************************
function settabcontrolproperty ( P_args) //tabcontrol object modifier
{ 
  validargs = p_title+p_helptext+p_initialtab;
  if (P_args)
  {
    args = P_args.split(";"); 
    for (var i=0; i < args.length; i++) 
    {
      var arg = args[i];
      if (!arg) break; 
      parsevalues(arg,this,validargs);
    }
  }
}

//**********************************************************************************
function addtab (tab_obj) //adds a tab object reference to the array attached to the tab control object
{
  this.tabarray[this.tabarray.length] = tab_obj;
}
//this can also be accomplished by simply re-arranging or replacing the objects in the tab array

//**********************************************************************************
function modifytab ( tab, P_args, Frame_ref) //allows changing of any tab properties - in the tab
//object, but in relation to the tabcontrol into which they are included rather than by addressing
//the tab objects directly.  This is almost the same as the tab.setTabProperty() method, except that
//it will also reload the frame containing the tab control IF a frame_ref is passed to it.
{
  if (!P_args) return;
  if (isNaN(tab)) {
    tabnum = -1;
    for (var i=0; i<this.tabarray.length; i++) {
      if (tab == this.tabarray[i].name) tabnum = i;
    }
  } else {
    var tabnum = tab-1;
  }
  if (tabnum == -1) return;
  args = P_args.split(";"); 
  for (var i=0; i < args.length; i++) {
    parsevalues (args[i], this.tabarray[tabnum], this.tabarray[tabnum].validargs)
  }
  if (Frame_ref) Frame_ref.location.reload(); 
}

//**********************************************************************************
function rendertabs(window_Ref) //renders the contents of the tab control frame
{
    if (this.initialtab != null)
  {
    this.currentTab = this.initialtab;
    this.initialtab = null;
  }
  output =   "";
  spancount = 2;
  tabrow1 =   this.titlehtml.replace(reptxt,this.title);
  tabrow2 =   "<tr class=color3>"
  tabrow3 =   "<tr class=color3>";
  tabrow4 =   this.tabrow4a;
  tabrow4 =   tabrow4.replace(repicn,(this.lightcontainer=="true")?"container_top_left_tabs_light":"container_top_left_tabs");
  for (i=0;i<this.tabarray.length;i++) 
  {
    if (this.tabarray[i].visible == "false"||eval(this.tabarray[i].visible) == false) continue;
    spancount = spancount + 3
    tabrow1 += this.anyTab1;
    if (i == this.currentTab - 1) {
      tabrow2 += this.curTab2;
      tabrow2 = tabrow2.replace(repicn,(this.lightcontainer=="true")?"tab_left_selected_light":"tab_left_selected");
      tabrow2 = tabrow2.replace(repicn,(this.lightcontainer=="true")?"tab_right_selected_light":"tab_right_selected");
      tabrow3 += this.curTab3a;
      tabrow3 = tabrow3.replace(repstl,(this.lightcontainer=="true")?"lightcurrenttab":"currenttab");
      if (this.tabarray[i].iconobj != null) tabrow3 += this.tabarray[i].iconobj.render("left",false,true)
      if (this.tabarray[i].alwaysactive == "true" || this.tabarray[i].alwaysactive == true)
      {
        temptext = this.curTab3d.replace(reptxt,this.tabarray[i].text);
        temptext = temptext.replace(rephlp,this.tabarray[i].hint);
        temptext = temptext.replace(repnum,i+1);
        temptext = temptext.replace(repbar,this.objectref);
        temptext = temptext.replace(repurl,this.tabarray[i].url);
        temptext = temptext.replace(repfram,this.targetframe);
        tabrow3 += temptext;
      }
      else
      {
        tabrow3 += this.curTab3b.replace(reptxt,this.tabarray[i].text);
      }
      if (this.tabarray[i].iconobj != null) tabrow3 += this.tabarray[i].iconobj.render("right",false,true)    
      tabrow3 += this.curTab3c;
      tabrow4 += (this.lightcontainer=="true")?this.curTab4l:this.curTab4;
    } else 
    {
      tabrow2 += this.otherTab2;
      if (this.tabarray[i].enabled == "false"||eval(this.tabarray[i].enabled) == false) 
      {
        tabrow3 += this.disTab3a;
        if (this.tabarray[i].iconobj != null) tabrow3 += this.tabarray[i].iconobj.render("left",true,false)
        tabrow3 += this.disTab3b.replace(reptxt,this.tabarray[i].text);
        tabrow3 = tabrow3.replace(rephlp,this.tabarray[i].disabledhint); 
        if (this.tabarray[i].iconobj != null) tabrow3 += this.tabarray[i].iconobj.render("right",true,false)
        tabrow3 += this.disTab3c;
      } 
      else 
      {
          temptext = this.otherTab3a;
        if (this.tabarray[i].iconobj != null) temptext += this.tabarray[i].iconobj.render("left",false,false)
        temptext += this.otherTab3b.replace(reptxt,this.tabarray[i].text);
        temptext = temptext.replace(rephlp,this.tabarray[i].hint);
        temptext = temptext.replace(repnum,i+1);
        temptext = temptext.replace(repbar,this.objectref);
        temptext = temptext.replace(repurl,this.tabarray[i].url);
        temptext = temptext.replace(repfram,this.targetframe);
        if (this.tabarray[i].iconobj != null) temptext += this.tabarray[i].iconobj.render("right",false,false)
        temptext += this.otherTab3c;
      tabrow3 += temptext;
      }
      tabrow4 += this.otherTab4;
    }
  }
  tabrow1 += "<td rowspan=3><img src='" + image_dir + "pixel_color3.gif'></td></tr>";
  tabrow2 += "</tr>";
  tabrow3 += "</tr>";
  tabrow4 += this.tabrow4b;
  tabrow4 =   tabrow4.replace(repicn,(this.lightcontainer=="true")?"container_top_right_tabs_light":"container_top_right_tabs");
  output +=   tabrow1 + tabrow2 + tabrow3 + tabrow4;
  temptext = this.helphtml.replace(reptxt,this.helptext);
  temptext = temptext.replace(repstl,(this.lightcontainer=="true")?"lightpanel":"panel");
  temptext = temptext.replace(repspan,spancount);
  output +=  temptext

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
function switchtab ( currtab, window_Ref, frame_ref, newurl ) 
//called by the tabs when generated in the page, this handles 
//the action of switching tabs and the target frame content.
{   
  this.currentTab = currtab;
  
  // reload the tab frame
  window_Ref.location.reload();
  
  // load the tab content
  if (frame_ref) 
  {
    // Navigator requires the path (must remove file name from hrefLocation
    if (anyNav) 
    { 
      if (hrefLocation.indexOf(".") > -1)
         newurl = hrefLocation.substr(0,hrefLocation.lastIndexOf("/")+1) + newurl; 
      else 
         newurl = hrefLocation + newurl; 
    }   
    // Force the reload - must fake out IEs caching with parameter
    frame_ref.location = newurl + "?a=" + Math.random()
  }
}

//**********************************************************************************
function loadTabPage ( currtab ) //loads the url stored with the referenced tab in the target frame
{
  var target = eval("top." + this.targetframe);
  var newurl = this.tabarray[currtab-1].url;
  //top.lines.location = newurl;
  target.location = newurl;
}

/**********************************************************************************
tab object constructor and methods
**********************************************************************************/
tab.prototype.validargs = p_name+p_text+p_hint+p_disabledhint+p_url+p_enabled+p_visible+p_alwaysactive+p_iconobj;
tab.prototype.name = ""
tab.prototype.text = "";
tab.prototype.hint = "";
tab.prototype.disabledhint = "";
tab.prototype.url = "#";
tab.prototype.alwaysactive = false;
tab.prototype.enabled = true;
tab.prototype.visible = true;
tab.prototype.setTabProperty = setTabProperty;

//**********************************************************************************
function tab(P_args) //creates a new tab object to include in a tab control
{ 
  this.iconobj = null;
  if (!P_args) return;
  args = P_args.split(";"); 
  for (var i=0; i < args.length; i++) 
  {
    var arg = args[i];
    if (!arg) break; 
    parsevalues(arg,this,this.validargs);
  }
  if (this.iconobj != null) this.iconobj = eval(this.iconobj);
}

//**********************************************************************************
function setTabProperty(P_args) //allows modification of properties for an existing tab object
{ 
  var holdiconobj = this.iconobj;
  this.iconobj = null;
  if (!P_args) return;
  args = P_args.split(";"); 
  for (var i=0; i < args.length; i++) 
  {
    var arg = args[i];
    if (!arg) break; 
    parsevalues(arg,this,this.validargs);
  }
  if (this.iconobj != null) this.iconobj = eval(this.iconobj);
  else this.iconobj = holdiconobj;
}

/**********************************************************************************
tabicon object construtor and methods
**********************************************************************************/
tabicon.prototype.validargs = p_iconname + p_disablediconname + p_iconposition + p_hint + p_disabledhint + p_actiontype + p_url + p_targetframe + p_action + p_enabled + p_showcurrentonly; 
tabicon.prototype.iconname = null;
tabicon.prototype.disablediconname = null;
tabicon.prototype.iconposition = "right";
tabicon.prototype.hint = "";
tabicon.prototype.disabledhint = "";
tabicon.prototype.actiontype = "url"
tabicon.prototype.url = "#";
tabicon.prototype.targetframe = "_self";
tabicon.prototype.action = "";
tabicon.prototype.enabled = "true";
tabicon.prototype.showcurrentonly = false;
tabicon.prototype.calcHtml = calcTabiconHtml;
tabicon.prototype.render = renderTabicon;
tabicon.prototype.html = "<a AaAaA><img src='IiIiI' align=VvVvV border=no alt='HhHhH'></a>"
tabicon.prototype.disabledhtml = "<img src='IiIiI' align=VvVvV alt='HhHhH'>"

//**********************************************************************************
function tabicon(P_args) //creates a new icon object to include in any tab as needed
{
  if (!P_args) return;
  args = P_args.split(";"); 
  for (var i=0; i < args.length; i++) 
  {
    var arg = args[i];
    if (!arg) break; 
    parsevalues(arg,this,this.validargs);
  }
  if (this.disablediconname == null) this.disablediconname = this.iconname;
  this.calcHtml();
}

//**********************************************************************************
function calcTabiconHtml() //calculates, the html for the icon - once, at object creation
{ 
  command = "href=";
  if (this.actiontype=="url") command += "'" + this.url + "'";
  else command += "'javascript:void " + this.action + "'";
  command += (this.targetframe=="")?"":" target=" + this.targetframe;
  
  this.html = this.html.replace(repact,command);
  this.html = this.html.replace(repicn,this.iconname)
  this.html = this.html.replace(rephlp,this.hint)
  if (anyNav)
    this.html = this.html.replace(repval,(this.iconposition=="right")?"absmiddle":"left hspace=0");
  else
    this.html = this.html.replace(repval,"absmiddle");
  this.disabledhtml = this.disabledhtml.replace(repicn,this.disablediconname)
  this.disabledhtml = this.disabledhtml.replace(rephlp,this.disabledhint)
  if (anyNav)
    this.disabledhtml = this.disabledhtml.replace(repval,(this.iconposition=="right")?"absmiddle":"left hspace=0");
  else
    this.disabledhtml = this.disabledhtml.replace(repval,"absmiddle");
}
  
//**********************************************************************************
function renderTabicon(p_position, p_disabled, p_current) //returns the html from the icon object to be 
//included in the tab cell, either nothing or the appropriate code if in the correct position in the cell
{  
  if (p_position != this.iconposition) return "";
  if (!p_current && this.showcurrentonly=="true") return  "";
  if (p_disabled) return this.disabledhtml;
  return this.html;
}

/**********************************************************************************
notabcontrol object construtor and methods
**********************************************************************************/
notabcontrol.prototype.validargs = p_title+p_helptext+p_longpage+p_lightcontainer;
notabcontrol.prototype.title = "No Title supplied";
notabcontrol.prototype.helptext = "";
notabcontrol.prototype.lightcontainer = false;
notabcontrol.prototype.longpage = "true";
notabcontrol.prototype.tophtml =  "<TABLE CELLPADDING=0 CELLSPACING=0 BORDER=0 WIDTH=100%><TR><TD " +
                                  "class=paneltitle ROWSPAN=2 " +
                                  "valign=top width=1><IMG SRC='" + image_dir + "container_top_left_notabs.gif'></TD>" + 
                                  "<TD class=highlight WIDTH=10000 HEIGHT=1><IMG SRC='" + image_dir + "pixel_color6.gif' height=1>" +
                                  "</TD><TD ROWSPAN=2 class=paneltitle valign=top width=1>" +
                                  "<IMG SRC='" + image_dir + "container_top_right_notabs.gif'></TD></TR>" + 
                                  "<TR><TD class=paneltitle valign=top>TtTtT</TD></TR><TR>" +
                                  "<TD class=highlight COLSPAN=3 HEIGHT=1><img src=" + image_dir + "pixel_color6.gif></TD></TR><TR>" +
                                  "<TD class=SsSsS colspan=3 " 
notabcontrol.prototype.render = rendernotabs;
notabcontrol.prototype.renderbottom = renderbottom;
notabcontrol.prototype.setNotTabProperty = setnotabproperty;

//**********************************************************************************
function notabcontrol(P_args) //defines a no tab panel top object
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
  this.tophtml += (this.longpage == "true")? " height=100 valign=top>" : "valign=top>";
  this.tophtml += "<font class=helptext>&nbsp;HhHhH</font></td></tr></table>" 
  this.tophtml = this.tophtml.replace(repstl,(this.lightcontainer=="true")?"lightpanel":"panel");
}

//**********************************************************************************
function setnotabproperty(P_args) //changes selected properties of an existing notab panel object
{ var validargs = p_title + p_helptext;
  if (P_args)
  {
    args = P_args.split(";"); 
    for (var i=0; i < args.length; i++) 
    {
      var arg = args[i];
      if (!arg) break; 
      parsevalues(arg,this,validargs);
    }
  }
  this.tophtml += (this.longpage == "true")? " height=100 valign=top>" : "valign=top>";
  this.tophtml += "<font class=helptext>&nbsp;HhHhH</font></td></tr></table>" 
}

//**********************************************************************************
function rendernotabs(window_Ref, p_content) //renders the no tab panel html
{
  output = this.tophtml;
  output = output.replace(reptxt,this.title);
  output = output.replace(rephlp,this.helptext);

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
container bottom standalone object construtor and methods
**********************************************************************************/
panelbottom = new Object;
panelbottom.lightcontainer = false;
panelbottom.render = renderbottom;

function renderbottom (window_Ref) {
  if (this.lightcontainer=="true")
  {
    output = "<table width=100% cellpadding=0 cellspacing=0 border=0>" +
           "<tr><td class=lightpanel rowspan=3 align=left valign=bottom width=10>" +
      "<img src='" + image_dir + "container_bottom_left.gif' height=5 width=5></td>" +
      "<td class=lightpanel height=5 width=1000><img src='" + image_dir + "pixel_color6.gif' height=2></td>" +
      "<td class=lightpanel rowspan=3 align=right valign=bottom width=10>" +
      "<img src='" + image_dir + "container_bottom_right.gif' height=5 width=5></td></tr>" +
      "<tr><td class=lightpanel nowrap><img src='" + image_dir + "pixel_color6.gif' width=1></td></tr>" +
      "<tr><td class=lightpanel><img src='" + image_dir + "pixel_color6.gif' height=1></td></tr></TABLE>";
  }
  else
  {
    output = "<table width=100% cellpadding=0 cellspacing=0 border=0>" +
           "<tr><td class=panel rowspan=3 align=left valign=bottom width=10>" +
      "<img src='" + image_dir + "container_bottom_left.gif' height=5 width=5></td>" +
      "<td class=panel height=5 width=1000><img src='" + image_dir + "pixel_gray5.gif' height=2></td>" +
      "<td class=panel rowspan=3 align=right valign=bottom width=10>" +
      "<img src='" + image_dir + "container_bottom_right.gif' height=5 width=5></td></tr>" +
      "<tr><td class=panel nowrap><img src='" + image_dir + "pixel_gray5.gif' width=1></td></tr>" +
      "<tr><td class=panel><img src='" + image_dir + "pixel_gray5.gif' height=1></td></tr></TABLE>";

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
