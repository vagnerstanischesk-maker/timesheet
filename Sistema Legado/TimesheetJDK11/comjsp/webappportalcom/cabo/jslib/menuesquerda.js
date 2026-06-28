var menuopts, menulnks, menutarget;

var menu_in = '';
var menu_out = '';

var img_folder  = 'folder.gif';
var img_ofolder = 'ofolder.gif';

var menu_big_in  = '<table width=162 border=0 cellspacing=0 cellpadding=0>';
var menu_big_sep = '</td></tr><tr><td width=170 height=21 align=LEFT valign=TOP><img src="/portalcom/imagesportalcom/images/layout/seta_nor.gif"> &nbsp;&nbsp;';
var menu_big_out = '</td></tr></table>';

var menu_sub_in  = '<tr><td align=LEFT valign=TOP colspan=10><div id="smenu$" style="display:None"><table border=0 cellpadding=0 cellspacing=0 align=left><tr><td valign=middle><img src="/portalcom/imagesportalcom/images/pixelAzul.gif" width="8" height="1"></td></tr></table><table border=0 cellpadding=0 cellspacing=0><tr><td width=8 valign=top><img src="/portalcom/imagesportalcom/images/pixel.gif" width="6" height="1"></td><td>';
var menu_sub_out = '</td></tr></table></div></td></tr>';
var menu_sub_sep = '</td></tr><tr><td width=8 valign=top></td><td>';

var menu_folder = '';
var menu_ofolder = '';
var menu_item = '';

//var menu_target = '_blank';
var menu_target = '';
var menu_wtarget = document;






var menu_buff = '';
var menu_stat;

function setCookie (name, value)
{
	document.cookie = name + "=" + escape (value) + "; expires=" + expdate.toGMTString() +  "; path=/";
}

function getCookie (name) {
var dcookie = document.cookie; 
var cname = name + "=";
var clen = dcookie.length;
var cbegin = 0;
        while (cbegin < clen) {
        var vbegin = cbegin + cname.length;
                if (dcookie.substring(cbegin, vbegin) == cname) { 
                var vend = dcookie.indexOf (";", vbegin);
                        if (vend == -1) vend = clen;
                return unescape(dcookie.substring(vbegin, vend));
                }
        cbegin = dcookie.indexOf(" ", cbegin) + 1;
                if (cbegin == 0) break;
        }
return null;
}



function menuabre(num,flag)
{
	var obj = eval('smenu' + num );
	var img = eval('document.mimg' + num );
	var sj, sa;

	if( menu_stat[num] && !flag )
	{
		obj.style.display = 'None';
//		if( img ) img.src = img_folder;
		menu_stat[num] = 0;
	}
	else
	{
		obj.style.display = 'Block';
//		if( img ) img.src = img_ofolder;
		menu_stat[num] = 1;
	}

	sj = menu_stat.join( ',' );
	setCookie( "menustat", sj );
}


function menuinit()
{
	var args = menuinit.arguments;
	var i, j, level, l, samelevel;
	var lflag = new Array;
	var seps = new Array;
	var nome, img, stat = 0, inn, lnk, trg, js;
	var sj, sa, dom, dlb, dlc;
	var abretudo = 0;
	var style = '<STYLE type="text/css">\n' +
				'<!-- \n' +
				'	.menubig { font-family:Verdana, Arial, Helvetica; font-size:7pt; line-height:10pt; color:black; cursor:hand; text-decoration:none; ffont-weight:bold; }\n' +
				'	.menubig:hover {color: black}\n' +
				'  	.menusub { font-family:Verdana, Arial, Helvetica; font-size:7pt; line-height:8pt; color:white; cursor:hand; text-decoration:none; ffont-weight:bold; }\n' +
				'	.menusub:hover {color: white}\n' +
				'  	.thepage { font-family:Verdana, Arial, Helvetica; font-size:12pt; line-height:8pt; color:white; cursor:hand; text-decoration:none; font-weight:bold; }\n' +
				'//-->\n' +
				'</STYLE>\n';

/*	img1 = new Image;
	img1.src = img_folder;
	img2 = new Image;
	img2.src = img_ofolder;
	img3 = new Image;
	img3.src = 'dot.gif';
*/


/*
	//dominio
	dom = document.location + '';
	i = dom.lastIndexOf('?'); if( i != -1 ) dom = dom.substring( 0, i);
	while( (i = dom.lastIndexOf('/')) != -1 && dom.charAt(i-1) != '/' )
		dom = dom.substring( 0,i);

	//diretorio
	dlb = document.location + '';
	if( dlb.indexOf( dom ) == 0 ) dlb = dlb.substring( dom.length, dlb.length );
	i = dlb.lastIndexOf('?'); if( i != -1 ) dlb = dlb.substring( 0, i);
	i = dlb.lastIndexOf('/'); if( i != -1 ) dlb = dlb.substring( 0, i);

	//url do target
	dlc = menu_wtarget.location + '';
*/	

	document.write( style );

	sj = getCookie( "menustat" );
	menu_stat = new Array;
	if( sj )
	{
		sa = sj.split( ',' );
		for( i = 0 ; i < sa.length ; i++ )
			if( sa[i] == '1' ) menu_stat[i] = 1;
	}
	else abretudo = 1;

	menuopts = new Array;
	menulnks = new Array;
	i = 0;
//	seps[0] = menu_big_sep;
//	seps[1] = menu_sub_sep;

	for( i = 0 ; i < args.length ; i+=2 )
	{
		menuopts[i/2] = args[i];
		menulnks[i/2] = args[i+1];
	}


	level = 0;
	i = 0;
	menu_buff = menu_in;
	menu_buff += menu_big_in;
	for( i = 0 ; i < menuopts.length ; i++ )
	{
		j = menuopts[i].lastIndexOf( '>' );
		forcaabre = 0;
		forcafecha = 0;
		if( menuopts[i].lastIndexOf( '^' ) >= 0 )
		{
			var x;
			forcaabre = 1;
			while( (x=menuopts[i].indexOf( '^' )) != -1 )
				menuopts[i] = menuopts[i].substr( 0, x) + menuopts[i].substr( x+1, menuopts[i].length-x-1 );
		}
		if( menuopts[i].lastIndexOf( '#' ) >= 0 )
		{
			var x;
			forcafecha = 1;
			while( (x=menuopts[i].indexOf( '#' )) != -1 )
				menuopts[i] = menuopts[i].substr( 0, x) + menuopts[i].substr( x+1, menuopts[i].length-x-1 );
		}
		l = j >= 0 ? j+1 : 0;

		j = level;
		while( j > l )
		{
			menu_buff += menu_sub_out;
			j--;
		}

		if( l > level )
		{
			inn = menu_sub_in;
			while( (j=inn.indexOf( '$' )) != -1 )
				inn = inn.substr( 0, j) + stat + inn.substr( j+1, inn.length-j-1 );
			if( abretudo || forcaabre ) menu_stat[stat] = 1;
			if( menu_stat[stat] )
			{
				j = inn.indexOf( 'display:None' );
				inn = inn.substr( 0, j) + 'display:Block' + inn.substr( j+12, inn.length-j-12 );
			}
			menu_buff += inn;
			stat++;
		}
		else
		{
			if( l )
				menu_buff += menu_sub_sep;
			else
				menu_buff += menu_big_sep;
		}

		nome = menuopts[i].substr( l, menuopts[i].length-l );
		lnk = menulnks[i];
		trg = menu_target;
		if( lnk == '' ) { lnk = 'javascript:void(0)'; trg = ''; }
		img = menu_item;
		if( (i+1) < menuopts.length && (menuopts[i+1].lastIndexOf( '>' )+1) > l )
		{
			if( forcaabre ) menu_stat[stat] = 1;
			if( forcafecha ) menu_stat[stat] = 0;
/*
			if( menu_stat[stat] )
				img = menu_ofolder;
			else
				img = menu_folder;
			while( (j=img.indexOf( '$' )) != -1 )
				img = img.substr( 0, j) + stat + img.substr( j+1, img.length-j-1 );
*/
			while( (j=menu_buff.indexOf( '*' )) != -1 )
				menu_buff = menu_buff.substr( 0, j) + '2' + menu_buff.substr( j+1, menu_buff.length-j-1 );
			js = ' onClick="menuabre(' + stat + ',0);clearTimeout(tid)" onMouseOver="tid=setTimeout(\'menuabre(' + stat + ',1)\', 1000 )" onMouseOut="clearTimeout(tid)"';
		}
		else
		{
			while( (j=menu_buff.indexOf( '*' )) != -1 )
				menu_buff = menu_buff.substr( 0, j) + '' + menu_buff.substr( j+1, menu_buff.length-j-1 );
			js = '';
		}

		if( l > 0 )
			nome = '<a href="' + lnk + '" target="' + trg + '"' + js + ' class="menuAzul">' + nome + '</a>';
		else
			nome = '<a href="' + lnk + '" target="' + trg + '"' + js + ' class="menuAzul">' + nome + '</a>';

		menu_buff += img + nome;		
//		alert( img + nome );
		level = l;
	}

	j = level;
	while( j > 0 )
	{
		menu_buff += menu_sub_out;
		j--;
	}
	menu_buff += menu_big_out;
	menu_buff += menu_out;
}

function menushow()
{
	document.write( menu_buff );
}

var expdate = new Date();
expdate.setTime (expdate.getTime() +  (24 * 60 * 60 * 1000 * 365)); 
