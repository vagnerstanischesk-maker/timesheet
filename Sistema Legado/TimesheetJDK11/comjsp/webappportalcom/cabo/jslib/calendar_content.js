/**********************************************
* calendar_content.js                         *  
***********************************************
* Cabo/JSUI version 0.5 November, 1999        *
* Written by Robert Hoexter                   *
* Oracle Corporation                          *
**********************************************/
//this library is only used by the cabo_calendar.html file, also included in the jsui directory

//Names arrays can be overridden by creating transated arrays of day and month names in the
//document that opens this window.
var names = new Array('January','February','March','April','May','June',
    'July','August','September','October','November','December');
if (opener.m_names) names = opener.m_names;

var cancelText = "Cancel";
if (opener.cancelText) cancelText = opener.cancelText;

var dow   = new Array('S','M','T','W','T','F','S');
if (opener.d_letters) dow = opener.d_letters;

//Note - for now, Days of the month are constant
var days  = new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);

function padout(number) { return (number < 10) ? '0' + number : number; }

function Calendar(Month,Year) {
    var output = '';
    firstDay = new Date(Year,Month,1);
    startDay = firstDay.getDay();

    if (((Year % 4 == 0) && (Year % 100 != 0)) || (Year % 400 == 0))
         days[1] = 29; 
    else
         days[1] = 28;

//Begin top of Outer gray container 
    output += '<FORM NAME="Cal"><TABLE CELLPADDING=0 CELLSPACING=0 BORDER=0 WIDTH=100%><TR class=panel><TD ROWSPAN=2 colspan=2><IMG SRC=' + image_dir + 'panel_top_left.gif height=5 width=5><\/TD><TD WIDTH=1000 HEIGHT=1 class=highlight><IMG SRC=' + image_dir + 'pixel_color6.gif><\/TD><TD ROWSPAN=2 colspan=2><IMG SRC=' + image_dir + 'panel_top_right.gif height=5 width=5><\/TD></TR><TR><TD height=4 class=panel><img src=' + image_dir + 'pixel_gray5.gif><\/td></tr><tr><td width=1 class=highlight><img src=' + image_dir + 'pixel_color6.gif><\/td><td width=4 class=panel><img src=' + image_dir + 'pixel_gray5.gif><\/td><td class=panel>';
//End top of Outer gray container

//Begin Calendar inside container
    //Month title area
    output += '<table border=0 width=100%><tr><td align=left valign=middle width=15%><a href="javascript:skipback();"><img src=' + image_dir + 'icon_previous.gif border=0><\/a><\/td>';

    output += '<td align=center valign=middle>';
    output += '<SELECT NAME="Month" onChange="changeMonth();">';

    for (month=0; month<12; month++) {
        if (month == Month) output += '<OPTION VALUE="' + month + '" SELECTED>' + names[month] + '<\/OPTION>';
        else                output += '<OPTION VALUE="' + month + '">'          + names[month] + '<\/OPTION>';
    }

    output += '<\/SELECT>&nbsp;<SELECT NAME="Year" onChange="changeYear();">';

    //christiano if (Year < 1910) var startyear = 1900; else var startyear = parseInt(Year)-10;
    //christiano if (Year > 2089) var endyear = 2101; else var endyear = parseInt(Year)+11;
    var startyear = 2000;
    var endyear = 2050;

    //for (year=1900; year<2101; year++) {
    for (year=startyear; year<endyear; year++) {
        if (year == Year) output += '<OPTION VALUE="' + year + '" SELECTED>' + year + '<\/OPTION>';
        else              output += '<OPTION VALUE="' + year + '">'          + year + '<\/OPTION>';
    }

    output += '<\/SELECT><\/TD><td align=right valign=middle width=15%><a href="javascript:skipforward();"><img src=' + image_dir + 'icon_next.gif border=0><\/a><\/td><\/tr><\/table>';

    //Calendar Table
    output += '<TABLE CELLSPACING=0 CELLPADDING=0 BORDER=0><TR><TD height=5><\/td><\/tr><TR>';

    //Weekday header row
    output += '<TD><img src=' + image_dir + 'calendar_top_left.gif></td>';
    for (i=0; i<7; i++)
        output += '<TD WIDTH=35  class=weekdaycell ALIGN=CENTER VALIGN=MIDDLE>' + dow[i] +'<\/TD>';
    output += '<TD><img src=' + image_dir + 'calendar_top_right.gif></td>';


    output += '<\/TR><TR ALIGN=CENTER VALIGN=MIDDLE><TD class=monthdaycell><img src=' + image_dir + 'pixel_transparent.gif><\/td>';

    var column = 0;
    var row=1;
    var lastMonth = Month - 1;
    if (lastMonth == -1) lastMonth = 11;

    for (i=0; i<startDay; i++, column++)
        output += '<TD WIDTH=35 HEIGHT=20 class=monthdaycell><A class=disableddaylink HREF="javascript:skipback()">' + (days[lastMonth]-startDay+i+1) + '<\/a><\/TD>';

    for (i=1; i<=days[Month]; i++, column++) {
        output += '<TD WIDTH=35 HEIGHT=20 class=monthdaycell>' + '<A class=daylink HREF="javascript:changeDay(' + i + ')">' + i + '<\/A>' +'<\/TD>';
        if (column == 6) {
    column = -1;
    output += '<TD class=monthdaycell><img src=' + image_dir + 'pixel_transparent.gif><\/TD><\/TR>';
    if (i<days[Month]) {
      row ++;
              output += '<TR ALIGN=CENTER VALIGN=MIDDLE><TD class=monthdaycell><img src=' + image_dir + 'pixel_transparent.gif><\/TD>';
    }
        }
    }
    if (column > 0) {
        for (i=1; column<7; i++, column++) {
            output += '<TD WIDTH=35 HEIGHT=20 class=monthdaycell><A class=disableddaylink HREF="javascript:skipforward()">' + i + '<\/A><\/TD>';
    }
    output += '<TD class=monthdaycell><img src=' + image_dir + 'pixel_transparent.gif><\/TD><\/TR>';
    }

    output += '<TR><TD><img src=' + image_dir + 'white_container_bottom_left.gif><\/td><td class=monthdaycell colspan=7><img src=' + image_dir + 'pixel_transparent.gif><\/td><TD><img src=' + image_dir + 'white_container_bottom_right.gif><\/td><\/tr>'

  while (row < 6) {
    output += '<tr><td colspan=9 height=20><\/td><\/tr>';
    row++; }

    output += '<\/TABLE>';

//begin bottom of outer container
output += '<\/td><td width=4 class=panel><img src=' + image_dir + 'pixel_gray5.gif width=4><\/td><td width=1 class=darkshadow><img src=' + image_dir + 'pixel_gray2.gif><\/td><\/tr><TR class=panel><TD ROWSPAN=2 colspan=2><IMG SRC=' + image_dir + 'panel_bottom_left.gif height=5 width=5><\/TD><TD HEIGHT=4 class=panel><IMG SRC=' + image_dir + 'pixel_gray5.gif><\/TD><TD ROWSPAN=2 colspan=2><IMG SRC=' + image_dir + 'panel_bottom_right.gif height=5 width=5><\/TD></TR><TR><TD height=1 class=darkshadow><img src=' + image_dir + 'pixel_gray2.gif><\/td><\/tr><tr><td height=3><\/td><\/TR>\</table>';

    output += '<table border=0 width=100%><tr><td align=right valign=middle>';
    output += '<TABLE CELLPADDING=0 CELLSPACING=0 BORDER=0><TR><TD ROWSPAN=5><IMG SRC=' + image_dir + 'button_left_round_default.gif></TD><TD class=darkshadow><IMG SRC=' + image_dir + 'pixel_gray2.gif></TD><TD ROWSPAN=5><IMG SRC=' + image_dir + 'button_right_round_default.gif></TD><TR><TD class=highlight><IMG SRC=' + image_dir + 'pixel_color6.gif></TD></TR><TR><TD class=button HEIGHT=20 NOWRAP><A class=buttontext HREF="" onClick="parent.calopen=false; window.close();">' + cancelText + '</TD></TR><TR><TD class=shadow><IMG SRC=' + image_dir + 'pixel_gray3.gif></TD></TR><TR><TD class=darkshadow><IMG SRC=' + image_dir + 'pixel_gray2.gif></TD></TR></TABLE>';
    output += '<\/td><\/tr><\/table><\/FORM>';

    return output;
}

function changeDay(day) {
  opener.day = day + '';
  var monthout = padout(parseInt(opener.month)+1);
  var pickedDay = opener.formatdate(padout(opener.day),monthout,opener.year,opener.NLSformat);
  var numDate = opener.year + monthout + padout(opener.day)

  if (opener.fillfield)
    opener.fillfield.numericDate = numDate;
    opener.fillfield.value = pickedDay;
    opener.checkForError(opener.fillfield,opener.afterfunc);
  if (opener.closeOnPick) {
    opener.calopen=false;
    window.close();
  }
}

function changeMonth() {
  opener.month = document.Cal.Month.options[document.Cal.Month.selectedIndex].value + '';
  location.reload();
}

function changeYear() {
   opener.year = document.Cal.Year.options[document.Cal.Year.selectedIndex].value + '';
  location.reload();
}

function skipback() { 
  var monthIndex=document.Cal.Month.selectedIndex;
  var yearIndex= document.Cal.Year.selectedIndex;
  if (monthIndex == 0 ) {
    if (yearIndex == 0) return; 
    else   {yearIndex = yearIndex - 1;
       monthIndex = 11;
      } 
  }
  else { monthIndex = monthIndex - 1; }
  document.Cal.Month.selectedIndex=monthIndex;
  document.Cal.Year.selectedIndex=yearIndex;
  opener.month = document.Cal.Month.options[document.Cal.Month.selectedIndex].value + '';
  opener.year = document.Cal.Year.options[document.Cal.Year.selectedIndex].value + '';
  location.reload();
}

function skipforward() { 
  var monthIndex=document.Cal.Month.selectedIndex;
  var yearIndex= document.Cal.Year.selectedIndex;
  if (monthIndex == 11 ) {
    if (yearIndex == 200) return; 
    else   {yearIndex = yearIndex + 1;
       monthIndex = 0;
      } 
  }
  else { monthIndex = monthIndex + 1; }
  document.Cal.Month.selectedIndex=monthIndex;
  document.Cal.Year.selectedIndex=yearIndex;
  opener.month = document.Cal.Month.options[document.Cal.Month.selectedIndex].value + '';
  opener.year = document.Cal.Year.options[document.Cal.Year.selectedIndex].value + '';
  location.reload();
}
