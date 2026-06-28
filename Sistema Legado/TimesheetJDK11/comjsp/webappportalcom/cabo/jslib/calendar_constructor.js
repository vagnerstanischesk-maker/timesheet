/**********************************************
* calendar_constructor.js                     *  
***********************************************
* Cabo/JSUI version 0.5 November, 1999        *
* Written by Robert Hoexter                   *
* Oracle Corporation                          *
**********************************************/

//Values here are all defaults, and can be overridden
var l_mon   = new Array('Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec');
var l_days  = new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
var l_len  = new Array(2, 3, 4);
var l_yearbreak = 50; //75;
var m_pos = 1;
var d_pos = 0;
var y_pos = 2;
var delim = "-";
var m_type = "N";
var NLSformat = "DD-MON-RRRR";
var numericDate = 0;
var today = new Date();
var day   = today.getDate();
var month = today.getMonth();
var year  = today.getFullYear();
var closeOnPick=true;
Calwindow = new Object;
Calwindow.closed=true;
var calwidth=250;
var calheight=245;
var calfile= jslib_dir + "cabo_calendar.html";
var dateErrorMsg="Sorry, the date you entered was not recognized.";
var calWindowTitle = "Select a Date";

function datecheck (P_date, P_NLS_format) {  
//Check to see if the date is a string, otherwise treat it as an object reference.

  var P_obj = false;
  if (typeof P_date == "string") P_value = P_date;
  else
  {
    P_obj = P_date;
    P_value = P_date.value; 
  } 

//exit if no date passed in 
  if (P_value == null|P_value == ''| P_value == ' ') return '';
  
//evaluate the NLS format and sets up for output formatting.
  if (P_NLS_format) evalNLS(P_NLS_format);
  else evalNLS(NLSformat);
  
  var l_length=P_value.length;
  var l_str = new Array(' ',' ',' ');
  var temp = new Array('','','');
  var l_swap='';
  var l_splitstr='';
  var l_index=0;
  var l_splitcount=1;
  var l_char='';
  var l_prevspace=true;
  var y_extra=0;
  var d_extra=0;
  var m_extra=0; 
  var l_delims = /\W/


//Splits and arranges an all numeric input string into numeric values ordered D M Y
//Assumes 2 characters for month and day, and assigns what is left over to year
  if (parseFloat(P_value) == P_value) {
    if (P_value.length <5) {
      P_value = P_value + "????????".substr(0,8-P_value.length);
    }
    else {
      if (P_value.length <= 6) {
      y_extra=0;
      d_extra=0;
      m_extra=0; 
      } 
      else {
        y_extra = P_value.length - 6 ;
        if (d_pos - y_pos > 0) d_extra = y_extra;
        if (m_pos - y_pos > 0) m_extra = y_extra;
      }
    }
    l_str[0]=P_value.substr(d_pos*2+d_extra,2);
    l_str[1]=P_value.substr(m_pos*2+m_extra,2);
    l_str[2]=P_value.substr(y_pos*2,2+y_extra);
    if (l_str[1] > 12)
    {  l_swap = l_str[0];
      l_str[0]=l_str[1];
      l_str[1]=l_swap; 
    }  
  }
  else {

//Otherwise, splits the string using delimiters after compressing spaces 
//and common delimiter characters
    for (var count=0; count < l_length; ++count ) {
      l_char=P_value.charAt(count);
      if (l_char.search(l_delims)==0) {
         if (l_prevspace==false) { l_splitstr += l_char; }
          l_prevspace = true;
      }
      else  { l_prevspace=false;
          l_splitstr += l_char;
      }
    }
    l_length = l_splitstr.length;


//splits string by delimiter into 3 parts
    for( var count=0; count < l_length; ++count ) {
      l_char=l_splitstr.charAt(count);
      if (l_char.search(l_delims)==0) {
        l_index = l_index + 1; 
      }
      else { temp[l_index] += l_char;
    }  }
    l_str[0]=temp[d_pos];
    l_str[1]=temp[m_pos];
    l_str[2]=temp[y_pos];

//The array is now in the order of the NLS mask, but some sanity
//checking of the contents should still be done
//If the string already in the year slot is > 31, leave it - otherwise
//if any string is > 31 and numeric (probably the year), swap it into the year slot.

    if (!l_str[2] > 31) 
    {
      if (l_str[0] > 31 && !isNaN(l_str[0])) {
        l_swap = l_str[2];
        l_str[2] = l_str[0];
        l_str[0] = l_swap;
      }

      if (l_str[1] > 31 && !isNaN(l_str[1])) {
        l_swap = l_str[2];
        l_str[2] = l_str[1];
        l_str[1] = l_swap;
      }
    }  
    
//strips off non-numeric characters from elements
    var strip = ""
    for (i=0; i<l_str[0].length; i++)
    { if (!isNaN(l_str[0].charAt(i))) strip += l_str[0].charAt(i); }
    //l_str[0] = (temp.length > 0)?temp + '':l_str[0] + '';
    if (strip.length > 0) l_str[0] = parseFloat(l_str[0],10) + ''

    strip = ""
    for (i=0; i<l_str[1].length; i++)
    { if (!isNaN(l_str[1].charAt(i))) strip += l_str[1].charAt(i); }
    if (strip.length > 0) l_str[1] = parseFloat(l_str[1],10) + ''
  
//swaps the first two strings if the first one is not numeric
//so the month string is placed into position 1 in the array, or
//swaps any value > 12 in the months slot into the day slot
    if (isNaN(l_str[0])) { 
      l_swap=l_str[0];
      l_str[0]=l_str[1];
      l_str[1]=l_swap; 
    } 
    else { 
      if (l_str[1] > 12) {
        l_swap = l_str[0];
        l_str[0]=l_str[1];
        l_str[1]=l_swap; 
    }  }
    

//If the month isn't numeric, checks the table of valid
//months to determine the numeric equivalent for the string.  Uses this to
//check whether the day is valid for the particular month later.
    if (isNaN(l_str[1])) {
      for (var count=0; count <= 11; ++count) {
        l_str[1] = l_str[1].substr(0,3);
        if (l_str[1].toUpperCase() == l_mon[count].toUpperCase() ) {
          l_str[1]=(count+1)+'';
            break; }
      }
      if (count == 12) { 
        l_str[1] = "??";
      }
      else { 
        if (l_str[1].length > l_len[1]) {
          l_str[1] = l_str[1].substr(0,l_len[1]);
        }
        else {
          while (l_str[1].length < l_len[1]) {
            l_str[1] = '0'+l_str[1];
      }  }  }
    } 
    else {
    if (l_str[1] > 12 || l_str[1] < 1) {
      l_str[1] = "??" }
    }

//zero fills the day if less than defined length and truncates if more
    if (l_str[0].length > l_len[0]) {
      l_str[0] = l_str[0].substr(0,l_len[0]);
    }
    else {
      while (l_str[0].length < l_len[0]) {
        l_str[0] = '0'+l_str[0]; 
    }  }  

//end of the "else" statement-->
}
//If the year isn't numeric, fill with ? and treat as unrecognizable, otherwise, convert to integer before
//filling to correct length.

if (isNaN(l_str[2])) l_str[2] = "????";
else l_str[2] = parseFloat(l_str[2]) + '';

//If the year comes in as 3 digits, just pads it with a "1", otherwise fills
//it to at least 2 digits.
    if (l_str[2].length > l_len[2]) 
    {
      l_str[2] = l_str[2].substr(0,l_len[2]);
    }
    else 
    {
      if (l_str[2].length == 3) 
    {
        l_str[2] = '1' + l_str[2];
    }
      else 
      {
        while (l_str[2].length < 2) 
        {
          l_str[2] = '0'+l_str[2]; 
        }  
      }  
    }

//adds century to a 2 digit year using an arbitrary split point
  if ((l_str[2].length <=2 || parseFloat(l_str[2])< 100 ) && l_str[2].indexOf("?") == -1 ) {
    l_str[2] = parseFloat(l_str[2])+'';
    while (l_str[2].length < 2) {
      l_str[2] = '0'+l_str[2];
    }
    if (l_str[2] < l_yearbreak) {
      l_str[2]='20'+l_str[2];
    }
    else {
      l_str[2]='19'+l_str[2]; 
  }  }

//Check if day of month is valid for month in year (check leapyear too)
  if (!isNaN(l_str[2]) && !isNaN(l_str[1])) {
       if (((l_str[2] % 4 == 0) && (l_str[2] % 100 != 0)) || (l_str[2] % 400 == 0)) {
      l_days[1] = 29;
    } 
        else {
               l_days[1] = 28;
    }
    if (l_str[0] > l_days[l_str[1]-1]) l_str[0] = l_days[l_str[1]-1];
  }

//If length of month > 2, just use the last two digits in the month.
    if (l_str[1].length > 2) l_str[1] = l_str[1].substring(l_str[1].length-2);
//If length of month < 2, zero fill it
  while (l_str[1].length < 2) { l_str[1] = '0'+l_str[1]; }  
  
//Store numeric date for future range checking.   
  numericDate = parseFloat(l_str[2]+l_str[1]+l_str[0]);
  numericDate = isNaN(numericDate)? 0 : numericDate;
  if (P_obj) P_obj.numericDate = numericDate;
  
//Replaces the numeric month with string from the table if type is "C"
  if (m_type == "C") {
    if (!isNaN(l_str[1]) && l_str[1] <= 12) {
      l_str[1]=l_mon[l_str[1]-1]; 
    }
    else   {
      for(var count=0; count <= 11; ++count) {
         if (l_str[1].toUpperCase() == l_mon[count].toUpperCase() ) {
          l_str[1]=l_mon[count];
            break;
      }  }
      if (count == 12) {
        l_str[1] = '???';
  }  }  }

//Finally, swap items around into output format and add delimiters
  temp[d_pos] = l_str[0];
  temp[m_pos] = l_str[1];
  temp[y_pos] = l_str[2];
  l_splitstr=temp[0]+delim+temp[1]+delim+temp[2];
  
  if (P_obj) P_obj.value = l_splitstr;
  return l_splitstr;
  
//End of Datecheck Function
}

function formatdate (P_Day, P_Month, P_Year, P_Dateformat) {
//This function returns the formatted output when fed the parts of the date.
//It relies on the presence of the same global javascript variables used by the
//evalNLS function.  It also assumes valid date components (as coming from the calendar)
  var temp = new Array('','','');
  var l_splitstr='';
  if (P_Dateformat) evalNLS (P_Dateformat);
  if (m_type == "C") P_Month=l_mon[parseFloat(P_Month)-1]; 
  temp[d_pos] = P_Day;
  temp[m_pos] = P_Month;
  temp[y_pos] = P_Year;
  l_splitstr=temp[0]+delim+temp[1]+delim+temp[2];

  return l_splitstr;
}
//Accepts the NLS format string and populates the variables necessary to apply it.
function evalNLS (P_Dateformat) {
  if (P_Dateformat) { NLSformat = P_Dateformat; }

  m_pos=NLSformat.indexOf('MON'); 
  m_type="C";
  l_len[1]=3;
  if (m_pos < 0) { m_pos=NLSformat.indexOf('MM');  m_type="N"; l_len[1]=2;}
  d_pos=NLSformat.indexOf('DD');
  y_pos=NLSformat.indexOf('RRRR');
  if (m_pos!=0) delim=NLSformat.charAt(m_pos - 1); 
    else delim = NLSformat.charAt(d_pos - 1);
  if (m_pos > 5) m_pos=2; else if (m_pos < 3) m_pos=0; else m_pos=1;
  if (d_pos > 5) d_pos=2; else if (d_pos < 3) d_pos=0; else d_pos=1;
  if (y_pos > 5) y_pos=2; else if (y_pos < 3) y_pos=0; else y_pos=1;
}
//Returns an alert if the date entered is in error.  This should be an official alert.
//If the date is OK, then the date string will be passed to an optionally included
//function which is custom written by the developer.
function checkForError (P_date, P_func) 
{ 
  if (P_date.value.indexOf('?') != -1) 
  {
    if (dateErrorMsg != null) alert(dateErrorMsg);
    P_date.focus();
  } else
  {
    if (P_func) P_func(P_date.value,P_date.numericDate);
  }
}
//Performs same function as checkForError above, but expects only a date string.  This is
//necessary to do separately because of the way javascript treats the object reference.
function dateError (P_date, P_hide_Error)
{
  if (P_date.indexOf('?') != -1) 
  {
    if (P_hide_Error) { }
    else
    {
      if (dateErrorMsg != null) alert(dateErrorMsg);
    }
    return false;
  }
  return true;
}
//Required to open and control the calendar
function opencal(fieldobj,func) {
  var openleft=100;
  var opentop=100;
  if (Calwindow.close && !Calwindow.closed) {
  // don't position calendar if already open
    var attr = "resizable=no,width=" + calwidth + ",height=" + calheight; } 
  else {
  if (Nav4) {
  // center on the main window
    openleft = parseInt(window.screenX + ((window.outerWidth - calwidth) / 2),10);
    opentop = parseInt(window.screenY + ((window.outerHeight - calheight) / 2),10);
    var attr = "screenX=" + openleft + ",screenY=" + opentop;
    attr += ",resizable=no,width=" + calwidth + ",height=" + calheight; } 
  else {
  // best we can do is center in screen
    openleft = parseInt(((screen.width - calwidth) / 2),10);
    opentop = parseInt(((screen.height - calheight) / 2),10);
    
    var attr = "left=" + openleft + ",top=" + opentop;
    attr += ",resizable=no,width=" + calwidth + ",height=" + calheight; }
   }
  Calwindow=open(calfile, 'Calendar', attr);
  window.fillfield = fieldobj;
  if (func) {window.afterfunc = func}
  if (Calwindow.opener == null) {
    Calwindow.opener = self; 
  }
}

function closecal() { 
  if (Calwindow.close && !Calwindow.closed) { 
    delete window.afterfunc;
    delete window.fillfield;
    Calwindow.close();
}  }

