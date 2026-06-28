var jserro = "";
var vCampo1 = "";

function isNull( field , fieldName) {
  selected = 0;
  fieldIsNull = 0;
  enviamsg = 1;
  if ( field.type == "text" ||
       field.type == "password" ||
       field.type == "textarea" ) {
    if ( field.value == "" )
      fieldIsNull = 1;
    else
    {  //procura por espacos
       valoraux = "";
       for(i=0; i<field.value.length; i++)
       {  //retira os espacos
          if(field.value.substring(i, i+1) != " ")
             valoraux += field.value.substring(i, i+1);
       }
       if(valoraux == "")
          fieldIsNull = 1;
       else
       {
          if( field.value.indexOf("\"") >= 0)
          {  //procura por aspas
             fieldIsNull = 1;
             enviamsg = 0;
             if ( isNull.arguments.length  == 1 )
             {  msgErro( "O Campo não deve possuir aspas" );                 
             }
             else
             {  msgErro( "O Campo não deve possuir aspas: " + fieldName );   
             }
          }
          else
          {  
             if( field.value.indexOf("</") >= 0)
             {  //procura por tag HTML
                fieldIsNull = 1;
                enviamsg = 0;
                if ( isNull.arguments.length  == 1 )
                {  msgErro( "O Campo não deve possuir </" );                 
                }
                else
                {  msgErro( "O Campo não deve possuir </ : " + fieldName );   
                }
             }
          }
       }
    }
  } else if ( field.type == "select-one" ) {
      if ( field.options[field.selectedIndex].value == "")
        fieldIsNull = 1;
  } else if ( field.type == "select-multiple" ) {
      fieldIsNull = 1;
      for ( i = 0; i < field.length; i++ )
        if ( field.options[i].selected )
          fieldIsNull = 0;
  } else if ( field.type == "undefined" ||
              field.type == "checkbox"  ||
              field.type == "radio" ) {
      fieldIsNull = 1;
      for ( i = 0; i < field.length; i++ )
        if ( field[i].checked )
          fieldIsNull = 0;
  }
  if ( fieldIsNull ) {
      if ( isNull.arguments.length  == 1 )
         { if(enviamsg)
              msgErro( "Campo obrigatório" );                 
         }
      else
         { if(enviamsg) 
              msgErro( "Campo obrigatório: " + fieldName );   
         }
      if ( field.type == "text" ||
           field.type == "textarea"  ||
           field.type == "password"  ||
           field.type == "select-one"  ||
           field.type == "select-multiple" )
        field.focus();
     return false;
  }
  return true;
}

function msgErro(par_msg)
{
   if (jserro.length == 0)
   {  jserro = par_msg;         }
   else
   {  jserro += "\n" + par_msg; }
   return true;
}

function Popup(url){
  window.open(url,"usa","toolbar=no,location=no,directories=no,menubar=no,scrollbars=yes,status=no,width=365,height=300,resizable=no");
  return true;
}

function isEmailAddress(theElement, theElementName)
{
  var s = theElement.value;
  var filter=/^[A-Za-z][A-Za-z0-9_.]*@[A-Za-z0-9_]+\.[A-Za-z0-9_.]+[A-za-z]$/;
  if (s.length == 0 ) return true;
  if (filter.test(s))
     return true;
  else
  {
     if( theElementName.length > 0 )
     { msgErro( "O " + theElementName + " é inválido" );
     }
     theElement.focus();
     return false;
  }
}

function trocaStatus( field ) {
  if (field.value == "-")
     field.value = "A";
  if (field.value == "I")
     field.value = "IA";
  return true;
}
function trocaStatusData( fieldData, fieldStatus ) {
  if (fieldStatus.value == "-")
     fieldStatus.value = "A";
  if (fieldStatus.value == "I")
     fieldStatus.value = "IA";
  fieldData.value = datecheck(fieldData.value,NLSformat);

  return true;
}
function trocaStatusDataLOV( fieldData, fieldStatus ) {
  if (fieldStatus.value == "-")
     fieldStatus.value = "A";
  if (fieldStatus.value == "I")
     fieldStatus.value = "IA";
  // fieldData.value = datecheck(fieldData.value,NLSformat);

  opencal(fieldData);

  return true;
}

function verificaNumero( campo ) {

  if (typeof campo == "string") valor = campo;
  else valor = campo.value; 

  if( valor.length <= 0 )
     return true;
  else
  {
      if(valor.indexOf(" ") != -1)
         return false;

      contvirgula = 0;
      for (i=0; i < valor.length; i++ )
      {
         if( valor.substring( i, i+1 ) == "," )
            contvirgula++;
      }
      if(contvirgula > 1)
         return false;

      //pega a parte inteira
      strnumero = '';
      if(valor.indexOf(",") >= 0)
         strnumero = valor.substring(0, valor.indexOf(","));
      else
         strnumero = valor;

      contponto = 0;
      for(j=strnumero.length; j > 0; j--)
      {
         contponto++;
         if( strnumero.substring( j-1, j ) == '.' )
         {  if(contponto == 4)
            {
               if(j == 1)
                  return false;
               else
                  contponto = 0;
            }
            else
               return false;
         }

      }

      strnumero = '';
      //strnumero = valor.replace( '.', '');
      for (var aux=0; aux < valor.length; aux++ )
      {
         //tira os pontos
         if( valor.substring( aux, aux+1 ) != '.' )
            strnumero = strnumero + valor.substring( aux, aux+1 );
      }

      // strnumero = strnumero.replace( ',', '.' );
      strnumeroaux = '';
      for ( k=0; k < strnumero.length; k++)
      {
         if (strnumero.substring(k, k+1) != ',')
            strnumeroaux += strnumero.substring(k, k+1);
         else
            strnumeroaux += '.';
      }
      strnumero=strnumeroaux;

//alert("desformata-strnumero=" + strnumero);
      if( isNumber2( strnumero ) )
      {
         campo.value = strnumero;
         formataNumero( campo );
         return true;
      }
      else
         return false;
  }
  return true;
}

function verificaNumero( campo, inteiros, decimais ) {

  if (typeof campo == "string") valor = campo;
  else valor = campo.value; 

  if( valor.length <= 0 )
     return true;
  else
  {
      if(valor.indexOf(" ") != -1)
         return false;

      contvirgula = 0;
      for (i=0; i < valor.length; i++ )
      {
         if( valor.substring( i, i+1 ) == "," )
            contvirgula++;
      }
      if(contvirgula > 1)
         return false;

      //pega a parte inteira
      strnumero = '';
      if(valor.indexOf(",") >= 0)
         strnumero = valor.substring(0, valor.indexOf(","));
      else
         strnumero = valor;

      contponto = 0;
      for(j=strnumero.length; j > 0; j--)
      {
         contponto++;
         if( strnumero.substring( j-1, j ) == '.' )
         {  if(contponto == 4)
            {
               if(j == 1)
                  return false;
               else
                  contponto = 0;
            }
            else
               return false;
         }

      }

      strnumero = '';
      //strnumero = valor.replace( '.', '');
      for (var aux=0; aux < valor.length; aux++ )
      {
         //tira os pontos
         if( valor.substring( aux, aux+1 ) != '.' )
            strnumero = strnumero + valor.substring( aux, aux+1 );
      }

      // strnumero = strnumero.replace( ',', '.' );
      strnumeroaux = '';
      for ( k=0; k < strnumero.length; k++)
      {
         if (strnumero.substring(k, k+1) != ',')
            strnumeroaux += strnumero.substring(k, k+1);
         else
            strnumeroaux += '.';
      }
      strnumero=strnumeroaux;


      strinteiros = '';
      strdecimais = '';
      if(strnumero.indexOf(".") >= 0)
      {  strinteiros = strnumero.substring(0, strnumero.indexOf("."));
         strdecimais = strnumero.substring(strnumero.indexOf(".") + 1);
      }
      else
         strinteiros = strnumero;

      if(strinteiros.length > inteiros)
         return false;

      if(strdecimais.length > decimais)
         return false;

//alert("desformata-strnumero=" + strnumero);
      if( isNumber2( strnumero ) )
      {
         campo.value = strnumero;
         formataNumero( campo );
         return true;
      }
      else
         return false;
  }
  return true;
}

function desformataNumero( campo ) {
  if (typeof campo == "string") valor = campo;
  else valor = campo.value;

  if( valor.length <= 0 )
     return true;
  else
  {
      strnumero = '';
      //strnumero = valor.replace( '.', '');
      for (var aux=0; aux < valor.length; aux++ )
      {
         //tira os pontos
         if( valor.substring( aux, aux+1 ) != '.' )
            strnumero = strnumero + valor.substring( aux, aux+1 );
      }
      // strnumero = strnumero.replace( ',', '.' );
      strnumeroaux = '';
      for ( k=0; k < strnumero.length; k++)
      {
         if (strnumero.substring(k, k+1) != ',')
            strnumeroaux += strnumero.substring(k, k+1);
         else
            strnumeroaux += '.';
      }
      strnumero=strnumeroaux;

//alert("desformata-strnumero=" + strnumero);
      if( isNumber( strnumero ) )
      {
         campo.value = strnumero;
         return true;
      }
      else
         return false;
  }
  return true;
}

function verificazero(pvalor) {

   if (typeof pvalor == "string") s = pvalor;
   else s = pvalor.value; 

   if(s == "")
      return true;

   strnumero = '';
   for(i=0; i<s.length; i++)
   {
      if( (s.substring(i, i+1) == '0') ||
          (s.substring(i, i+1) == '1') ||
          (s.substring(i, i+1) == '2') ||
          (s.substring(i, i+1) == '3') ||
          (s.substring(i, i+1) == '4') ||
          (s.substring(i, i+1) == '5') ||
          (s.substring(i, i+1) == '6') ||
          (s.substring(i, i+1) == '7') ||
          (s.substring(i, i+1) == '8') ||
          (s.substring(i, i+1) == '9') )
      {
         strnumero += s.substring(i, i+1);
      }
   } 
   if(Math.max(strnumero,0) == 0)
      return false;
   else
      return true; 
}

function verificanegativo(pvalor) {

  if (typeof pvalor == "string") s = pvalor;
  else s = pvalor.value; 

   if(s == "")
      return true;

   posinicial = 0;
   for(i=0; i<s.length; i++)
   {
      if(s.substring(i, i+1) != ' ')
      {
         if(posinicial == 0)
            posinicial = i;
         break;
      }
   } 
   s = s.substring(posinicial);
   if(s.length == 0)
      return true;
   else
   {
      if(s.substring(0,1) == '-')
         return false;
      else
         return true; 
   }
}

function isNumber(pvalor) {
   s = pvalor;

   if(s == "")
      return true;

   if(s.indexOf(" ") != -1)
      return false;
   if(s.indexOf("-") != -1)
      return false;
   if(s.indexOf("+") != -1)
      return false;
   if(s.indexOf(".") != -1)
      return false;
   if(s.indexOf(",") != -1)
      return false;

   //if (isNaN(Math.abs(pvalor)) && (s.charAt(0) != '#'))
   //{
         //for (var i=0; (i <= s.length && s.charAt(i) != '.'); )
         for (var i=0; (i < s.length); )
         {
           //if (((s.charAt(i) >= 0) && (s.charAt(i) <= 9)) ||
           //     (s.charAt(i) == ',' && i != 0 && i != s.length-1) || (s.charAt(i) == '.') )
           if ( (s.charAt(i) >= 0) && (s.charAt(i) <= 9) )
             i++;
           else
           {
             //alert( theElementname + " Value must be a number/must be of proper format" );
             //theElement.focus();
             //theElement.select();
             return false;
           }
         }
         //if (s.charAt(i) == '.')
         //{
	 //  for (i++;i <= s.length; )
         //  {
         //     if (((s.charAt(i) >= 0) && (s.charAt(i) <= 9)))
         //       i++;
         //     else
         //     {
         //       //alert( theElementname + " Value must be a number/must be of proper format" );
         //       //theElement.focus();
         //       //theElement.select();
         //       return false;
         //     }
         //  }
         //}
   //}
   return true;
}

function isNumber2(pvalor) {
   s = pvalor;
   if (isNaN(Math.abs(pvalor)) && (s.charAt(0) != '#'))
   {
         for (var i=0; (i <= s.length && s.charAt(i) != '.'); )
         {
           if (((s.charAt(i) >= 0) && (s.charAt(i) <= 9)) ||
                (s.charAt(i) == ',' && i != 0 && i != s.length-1) || (s.charAt(i) == '.') )
             i++;
           else
           {
             //alert( theElementname + " Value must be a number/must be of proper format" );
             //theElement.focus();
             //theElement.select();
             return false;
           }
         }
         if (s.charAt(i) == '.')
         {
	   for (i++;i <= s.length; )
           {
              if (((s.charAt(i) >= 0) && (s.charAt(i) <= 9)))
                i++;
              else
              {
                //alert( theElementname + " Value must be a number/must be of proper format" );
                //theElement.focus();
                //theElement.select();
                return false;
              }
           }
         }
   }
   return true;
}

function formataNumero( campo ) {
  if (typeof campo == "string") valor = campo;
  else valor = campo.value;

//alert("valor inicial=" + valor);

  if( valor.length > 0 )
  {
     sinal = '';
     if( valor.charAt(0) == '-')
     {
        sinal = '-';
        if( valor.length > 1 )
           valor = valor.substring(1);
        else
        {
           valor = '';
           return true;
        }
     }

     // valor = valor.replace( '.', ',' );
     valoraux = '';
     for ( k=0; k < valor.length; k++)
     {
        if (valor.substring(k, k+1) != '.')
           valoraux += valor.substring(k, k+1);
        else
           valoraux += ',';
     }
     valor=valoraux;


     strdecimais = '';
     if( valor.indexOf(',') >= 0 )
     {
        strinteiros = valor.substring( 0, valor.indexOf(',') );
        strdecimais = valor.substring( valor.indexOf(',') );
     }
     else
     {
        strinteiros = valor;
     }
//alert("strinteiros=" + strinteiros);
     cont = 0;
     fmtinteiros = '';
     for( i = strinteiros.length; i > 0; i-- )
     {
//alert("inicio loop, fmtinteiros=" + fmtinteiros + ", i=" + i);
        cont++;
        if( cont == 4)
        {   cont = 1;
            fmtinteiros = '.' + fmtinteiros;
        }
        fmtinteiros = strinteiros.substring( i - 1, i ) + fmtinteiros;
     }
//alert("depois loop, fmtinteiros=" + fmtinteiros);
     campo.value = sinal + fmtinteiros + strdecimais;
  }
  return true;
}

// ---------------------------------------------------------------------

// funcoes para validacao de data:
/* 
ValidaData ( oData )
   Recebe uma data qualquer e verifica se a data e valida .
   Retorna true ou false.

FormataData ( oData , tipo )
   Recebe uma data valida e um tipo ( S , C )
   Retorna data formatada (dd/mm/yyyy)

   S = data simples (esta implementado )
   C = data composta ( nao esta implementado , retorna false )

ComparaData ( oData1 , oData2 )
   Racebe duas datas formatadas e validas (dd/mm/yyyy)
   se  oData1 > oData2 retorna  1
   se  oData1 = oData2 retorna  0 
   se  oData1 < oData2 retorna -1
*/

function VerAnoBi ( N )
{  if ( ( N%4==0 && N%100 !=0 ) || ( N%400==0 ) )
    {  return true ;
       // ano bissexto
    }
   else 
    { return false ;
    }
}

function VerMes ( N )
{ if ( ( N >= 1 ) && ( N <= 12 ))
   { return true ;
    // mes valido 
   }
  else 
   { return false ;
   }
}

function VerHora ( N )
{ if ( ( N >= 0 ) && ( N <= 23 ) )
   { return true ;
   }
  else
   { return false ;
   }
} 

function VerMin ( N )
{ if ( ( N >= 0 ) && ( N <= 59 ))
   { return true ;
   }
  else
   { return false ;
   }
}

function VerSeg ( N )
{ if ( ( N >= 0 ) && ( N <= 59 ))
   { return true ;
   }
  else
   { return false ;
   }
}

function VerDia ( pDia , pMes , pAno )
{

if ( pDia >= 1 )
  {
  if (   pDia <= 28  )
    { return true ;
    }
  else
    {
      if ( ( ( pMes == 1 ) || ( pMes == 3 ) || ( pMes == 5 ) || ( pMes == 7 ) || ( pMes == 8 ) || ( pMes == 10 ) || ( pMes == 12 ) ) && ( pDia <= 31 ) )
         { return true ;
         }
      else
         {
           if ( ( ( pMes == 4 ) || ( pMes == 6 ) || ( pMes == 9 ) || ( pMes == 11 ) ) && ( pDia <= 30 ) )
             { return true ;
             }
           else
             {
               if ( ( pMes == 2 ) && ( VerAnoBi (pAno) )  && ( pDia == 29 ))
                 { return true ;
                 }
               else 
                 { return false ;
                 }
             }
         } 
     }
  }
else
  { return false ;
  }
} // fim VerDia funcao

function ValidaHora( oHora )
{
  sHora = oHora.value;

  var filtroA = /^\d{1,2}:\d{1,2}:\d{1,2}$/ ; 
  var filtroB  = /^\d{1,2}:\d{1,2}$/ ;    

  var n = 0 ;

  if ( filtroA.test(sHora)  ) 
   { return VerFormatoHora (oHora , "A" )  ;                                                       
   }
  else
   {
     if ( filtroB.test(sHora) ) 
      { return VerFormatoHora (oHora , "B") ;     
      }  
     else 
      { return false ;   
      } 
   } 
}


function VerFormatoHora ( oHora , tipo  )
{
 var vHora = oHora.value ; 

 var indHora = vHora.indexOf(":"); 
 var indSeg  = vHora.lastIndexOf(":");

 var sHora  = "00"   ; 
 var sMin   = "00"   ; 
 var sSeg   = "00"   ; 

 sHora = vHora.substring( 0, 2 ) ;
 sMin  = vHora.substring( 3, 5  ) ;
 if ( tipo == "A" )
  { sSeg  = vHora.substring( 6 ) ;
  }
 if ( tipo == "A" )
   {
     if ( ( VerHora( eval(sHora) ) ) && ( VerMin ( eval( sMin ) ) ) && ( VerSeg ( eval( sSeg ) ) ) ) 
       { return true ;
       }
     else 
       { return false ;
       }       
   }
 else
   {
     if ( ( VerHora( eval(sHora) ) ) && ( VerMin ( eval( sMin ) ) ) ) 
       { return true ;
       }
     else 
       { return false ;
       }       
   } 
}

function ValidaData( oData )
{
  sData = oData.value;

  var filtroA  = /^\d{1,2}\/\d{1,2}\/\d{1,4}$/ ;  
  var filtroNA = /^\d{1,2}\/\d{1,2}\/\d{3}$/ ;  
  var filtroB  = /^\d{1,2}\/\d{1,2}\/\d{1,4} \d{1,2}:\d{1,2}:\d{1,2}$/ ;    
  var filtroNB = /^\d{1,2}\/\d{1,2}\/\d{3} \d{1,2}:\d{1,2}:\d{1,2}$/ ; 

  var n = 0 ;

  if ( ( filtroA.test(sData)  ) && ( ! filtroNA.test(sData) ) )
   { return VerFormato (oData , "A" )  ;                                                       
   }
  else
   {
     if ( ( filtroB.test(sData) ) && ( ! filtroNB.test(sData) ) ) 
      { return VerFormato (oData , "B") ;     
      }  
     else 
      { return false ;   
      } 
   } 

}

function VerFormato ( oData , tipo  )
{
 var sData  = oData.value ; 

 var indDia  = sData.indexOf("\/");
 var indMes  = sData.lastIndexOf("\/");
 var indHora = sData.indexOf(":"); 
 var indSeg  = sData.lastIndexOf(":");

 var sDia   = "00"   ;
 var sMes   = "00"   ;
 var sAno   = "0000" ; 
 var sHora  = "00"   ; 
 var sMin   = "00"   ; 
 var sSeg   = "00"   ; 
 
 var fimData ;
 var fimDataHora ;

 if ( tipo == "A" )
   { fimData = sData.length ;
   }
 else
   { fimData = sData.indexOf(" ");
     fimDataHora = sData.length ;
   } 


 if ( tipo == "B" )
  { sHora = sData.substring( fimData + 1 , indHora     ) ;
    sMin  = sData.substring( indHora + 1 , indSeg      ) ;
    sSeg  = sData.substring( indSeg + 1 , fimDataHora ) ;
  }

 sDia   = sData.substring( 0 , indDia ) ;
 sMes  = sData.substring( indDia + 1 , indMes  );
 sAno  = sData.substring( indMes + 1 , fimData ) ;

 if ( tipo == "A" )
   {
     if (( VerDia( eval( sDia ) , eval( sMes ) , eval( sAno ) ) ) && ( VerMes ( eval( sMes ) ) ))
       { return true ;
       }
     else 
       { return false ;
       }       
   }
 else
   {
     if (( VerDia( eval( sDia ) , eval( sMes ) , eval( sAno ) ) ) && ( VerMes ( eval( sMes ) ) ) && ( VerHora( eval(sHora) ) ) && ( VerMin ( eval( sMin ) ) ) && ( VerSeg ( eval( sSeg ) ) ) ) 
       { return true ;
       }
     else 
       { return false ;
       }       
   } 
}

function FormataData( oData , tipo   )
{
 var  sData = oData.value ;
 var  sDia ;
 var  sMes ;  
 var  sAno ;

 var fimData = sData.length ;
 var indDia  = sData.indexOf("\/");
 var indMes  = sData.lastIndexOf("\/");
// var indHora = sData.indexOf(":"); 
// var indSeg  = sData.lastIndexOf(":");
 

 if ( tipo =="C" )
  { return false ;
  }
 else
  {  sDia   = sData.substring( 0 , indDia ) ;
     sMes  = sData.substring( indDia + 1 , indMes  ) ;
     sAno  = sData.substring( indMes + 1 , fimData ) ;
   
   if ( sDia.length < 2 )
    { sDia = "0" + sDia ;
    }

   if ( sMes.length < 2 )
    { sMes = "0" + sMes ;
    }

    if ( sAno.length == 1)
     { sAno = "200" + sAno ;
     }
    else
     {  if (sAno.length == 2 ) 
         { if ( eval(sAno ) > 50 )
             { sAno = "19" + sAno ;
             }
           else 
             { sAno = "20" + sAno ;
             } 
         }
     }

//   alert ( sDia ) ;
//   alert (sMes ) ;
//   alert (sAno ) ;
   sData = sDia + "\/" + sMes + "\/" + sAno ;
   oData.value = sData ;
 }
}

function ComparaData ( oData1 , oData2 )
{
 var sData1 ;
 var sData2 ;

 var fimData = oData1.value.length ;
 var indDia   = oData1.value.indexOf("\/");
 var indMes  = oData1.value.lastIndexOf("\/");
  
 var    sDia1  = oData1.value.substring( 0 , indDia ) ;
 var    sMes1  = oData1.value.substring( indDia + 1 , indMes  ) ;
 var    sAno1  = oData1.value.substring( indMes + 1 , fimData ) ;

 fimData = oData2.value.length ;
 indDia   = oData2.value.indexOf("\/");
 indMes  = oData2.value.lastIndexOf("\/");

 var     sDia2   = oData2.value.substring( 0 , indDia ) ;
 var    sMes2  = oData2.value.substring( indDia + 1 , indMes  ) ;
 var    sAno2  = oData2.value.substring( indMes + 1 , fimData ) ;

 sData1 = sAno1 + sMes1 + sDia1 ;
 sData2 = sAno2 + sMes2 + sDia2 ;

 if ( sData1 > sData2 )
  { return 1 ;   
  }
 else
 { if ( sData1 < sData2 )
    { return -1 ;   
    }
   else
    { return 0 ;
    } 
 }
}
// ---------------------------------------------------------------------
function validaEspeciais( campo ) {
     if(campo.value == "")
        return true;

     testa = "ABCDEFGHIJKLMNOPQRSTUVXWYZ0123456789";
     for ( k=0; k < campo.value.length; k++)
     {  if( testa.indexOf(campo.value.substring(k, k+1).toUpperCase()) == -1 )
           return false;
     }
     return true;
}
function tiraAcentos( campo ) {
     if(campo.value == "")
        return true;
     
     straux = '';
     for ( k=0; k < campo.value.length; k++)
     {
        substituiu = 0;
        if (campo.value.substring(k, k+1) == 'Ç')
        {  straux += 'C'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'Á')
        {  straux += 'A'; 
           substituiu = 1;
        } 
        if (campo.value.substring(k, k+1) == 'Ã')
        {  straux += 'A'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'À')
        {  straux += 'A'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'Â')
        {  straux += 'A'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'É')
        {  straux += 'E'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'È')
        {  straux += 'E'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'Ê')
        {  straux += 'E'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'Í')
        {  straux += 'I'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'Ì')
        {  straux += 'I'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'Î')
        {  straux += 'I'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'Ó')
        {  straux += 'O'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'Õ')
        {  straux += 'O'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'Ò')
        {  straux += 'O'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'Ô')
        {  straux += 'O'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'Ú')
        {  straux += 'U'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'Ù')
        {  straux += 'U'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'Û')
        {  straux += 'U'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'Ü')
        {  straux += 'U'; 
           substituiu = 1;
        }

        if (campo.value.substring(k, k+1) == 'ç')
        {  straux += 'c'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'á')
        {  straux += 'a'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'ã')
        {  straux += 'a';
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'à')
        {  straux += 'a'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'â')
        {  straux += 'a'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'é')
        {  straux += 'e'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'è')
        {  straux += 'e'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'ê')
        {  straux += 'e'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'í')
        {  straux += 'i'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'ì')
        {  straux += 'i'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'î')
        {  straux += 'i'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'ó')
        {  straux += 'o'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'õ')
        {  straux += 'o'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'ò')
        {  straux += 'o';
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'ô')
        {  straux += 'o'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'ú')
        {  straux += 'u'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'ù')
        {  straux += 'u'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'û')
        {  straux += 'u'; 
           substituiu = 1;
        }
        if (campo.value.substring(k, k+1) == 'ü')
        {  straux += 'u'; 
           substituiu = 1;
        }
     
        if (substituiu == 0)
        {  straux += campo.value.substring(k, k+1); }
     }
     campo.value=straux;
     return true;
}

function substitui( campo, strprocura, strsubstitui ) {

  valoraux = '';
  for ( k=0; k < campo.value.length; k++)
  {
     if (campo.value.substring(k, k+1) != strprocura)
        valoraux += campo.value.substring(k, k+1);
     else
        valoraux += strsubstitui;
  }
  campo.value=valoraux;

  return true;
}
function substituiStr( valor, strprocura, strsubstitui ) {

  valoraux = '';
  for ( k=0; k < valor.length; k++)
  {
     if (valor.substring(k, k+1) != strprocura)
        valoraux += valor.substring(k, k+1);
     else
        valoraux += strsubstitui;
  }
  return valoraux;
}
function pesquisa()
{ 
   if( validaForm() ) {
      if(document.iForm.clicou.value == "N")
      {  document.iForm.clicou.value = "S";
         document.iForm.submit();
      }
   }
   return true;
}

function setaclicou()
{  var elemento;
   var bPossuiForm = false;
   var bPossuiCampo = false;
   for(elemento in document)
   {  if(elemento == "iForm")
      {  bPossuiForm = true; }
   }
   if(bPossuiForm)
   {  for(elemento in document.iForm)
      {  if(elemento == "clicou")
         {  bPossuiCampo = true; }
      }
   }
   if(bPossuiCampo)
   {  document.iForm.clicou.value = "N"; }

   setaclicou2();
   return true;
}

function setaclicou2()
{  var elemento2;
   var bPossuiForm2 = false;
   var bPossuiCampo2 = false;
   for(elemento2 in document)
   {  if(elemento2 == "iNavega")
      {  bPossuiForm2 = true; }
   }
   if(bPossuiForm2)
   {  for(elemento2 in document.iNavega)
      {  if(elemento2 == "INAVEGACLICOU")
         {  bPossuiCampo2 = true; }
      }
   }
   if(bPossuiCampo2)
   {  document.iNavega.INAVEGACLICOU.value = "N"; }
   return true;
}

function numberToCurrency( campo )
{
  strValor = ""+campo;
  var inicio = 0;
  var sinal = "R$ ";
  var valor = parseFloat(strValor);
  if (valor<0){
    sinal = "-R$ ";
    inicio = 1;
  }
  valor = (Math.round(valor*100))/100;
  strValor = ""+valor;
  var idx = strValor.indexOf('.'); 
  if (idx > -1){
    casas = strValor.length-idx;
    if (casas < 3){
      for (x = 1; x < casas; x++){

        strValor += "0";
      }
    } 
  }else{
    idx = strValor.length;
    strValor += ",00";
  }
  novoValor = sinal;
  novoValor += strValor.substring(inicio,idx);
  novoValor += ",";
  novoValor += strValor.substring( (idx + 1));
  idx = novoValor.indexOf(',');
  while ( idx >(6+inicio) ){
    idx -= 3;
    novoValor = novoValor.substring(0,idx)+"."+novoValor.substring(idx);
  }

  return novoValor;
}

// este metodo é para substituir do cmvalidacoes
function verificaNumeroAlex( campo, inteiros, decimais ) {
  if (typeof campo == "string") valor = campo;
  else valor = campo.value;

  if( valor.length <= 0 ) {
     return true;
  }
  else
  {
     if(valor.indexOf(" ") != -1)
         return false;

     while(valor.indexOf('.') != -1)
       valor = valor.replace( ".", "");

     var strinteiro = 0;
     var strdecimais = 0;
     if(valor.indexOf(',') != -1) {
       strinteiro = valor.substring(0,valor.indexOf(','));
       strdecimais = valor.substring(valor.indexOf(',')+1);
     }
     else
       strinteiro = valor;

     if(strinteiro.substring(0,1) == "-")
       strinteiro = strinteiro.substring(1);

      if(strinteiro.length > inteiros)
         return false;

      if(strdecimais.length > decimais)
         return false;

      if( (isNumber(strinteiro)) && (isNumber(strdecimais)) )
      {
         valor = valor.replace( ",", ".");
         campo.value = valor;
         formataNumero( campo );
         return true;
      }
      else
         return false;
  }
}

// este metodo é para substituir do cmvalidacoes
function desformataNumeroAlex( campo, inteiros, decimais ) {
  if (typeof campo == "string") valor = campo;
  else valor = campo.value;

  if( valor.length <= 0 )
     return true;
  else
  {
     if( verificaNumeroAlex(campo,inteiros,decimais) )
     {
        while(valor.indexOf('.') != -1)
          valor = valor.substring(0,valor.indexOf('.')) + valor.substring(valor.indexOf('.')+1);

        valor = valor.replace( ",", ".");
        campo.value = valor;
        return true;
     }
     else {
        campo.value = "?";
        return false;
     }
  }
}

//=========== Estes métodos deverão ser padronizados =============
//======= As substituições desses códigos na tela de pacote ======
//======== pelos métodos acima com o mesmo nome ==================

// este metodo é para substituir do cmvalidacoes
function verificaNumeroAlex2( campo, inteiros, decimais ) {
  if (typeof campo == "string") valor = campo;
  else valor = campo.value;

  var filtro  = /^\d{0,3}\.\d{0,3}\.\d{0,3}\.\d{0,3}\,\d{0,decimais}$/ ;  

  if( valor.length <= 0 )
     return true;
  else
  {
     if ( filtro.test(valor)  )  {
       return false;
     }

     if(valor.indexOf(" ") != -1)
         return false;

     while(valor.indexOf('.') != -1)
       valor = valor.substring(0,valor.indexOf('.')) + valor.substring(valor.indexOf('.')+1);

     var strinteiro = 0;
     var strdecimais = 0;
     if(valor.indexOf(',') != -1) {
       strinteiro = valor.substring(0,valor.indexOf(','));
       strdecimais = valor.substring(valor.indexOf(',')+1);
     }
     else
       strinteiro = valor;

     if(strinteiro.substring(0,1) == "-")
       strinteiro = strinteiro.substring(1);

      if(strinteiro.length > inteiros)
         return false;

      if(strdecimais.length > decimais)
         return false;

      if( (isNumber(strinteiro)) && (isNumber(strdecimais)) )
      {
         valor = valor.replace( ",", ".");
         campo.value = valor;
         formataNumero( campo );
         return true;
      }
      else
         return false;
  }
}

// este metodo é para substituir do cmvalidacoes
function desformataNumeroAlex2( campo, inteiros, decimais ) {
  var valor = campo.value;

  if( valor.length <= 0 )
     return true;
  else
  {
     if( verificaNumeroAlex(campo,inteiros,decimais) )
     {
        while(valor.indexOf('.') != -1)
          valor = valor.substring(0,valor.indexOf('.')) + valor.substring(valor.indexOf('.')+1);

        valor = valor.replace( ",", ".");
        campo.value = valor;
        return true;
     }
     else
        return false;
  }
}

// este metodo é para substituir do cmvalidacoes
function formataNumeroAlex2( campo, decimais ) {
  valor = campo.value;
  if( valor.length > 0 )
  {
     sinal = '';
     if( valor.charAt(0) == '-')
     {
        sinal = '-';
        if( valor.length > 1 )
           valor = valor.substring(1);
        else
        {
           valor = '';
           return true;
        }
     }

     // valor = valor.replace( '.', ',' );
     valoraux = '';
     for ( k=0; k < valor.length; k++)
     {
        if (valor.substring(k, k+1) != '.')
           valoraux += valor.substring(k, k+1);
        else
           valoraux += ',';
     }
     valor=valoraux;


     strdecimais = '';
     if( valor.indexOf(',') >= 0 )
     {
        strinteiros = valor.substring( 0, valor.indexOf(',') );
        strdecimais = valor.substring( valor.indexOf(',') );
     }
     else
     {
        strinteiros = valor;
     }
     cont = 0;
     fmtinteiros = '';
     for( i = strinteiros.length; i > 0; i-- )
     {
        cont++;
        if( cont == 4)
        {   cont = 1;
            fmtinteiros = '.' + fmtinteiros;
        }
        fmtinteiros = strinteiros.substring( i - 1, i ) + fmtinteiros;
     }

     if(strdecimais.length == 0)
       strdecimais = ",";

     for( i=strdecimais.length; i <= decimais; i++ )
       strdecimais += "0";

     campo.value = sinal + fmtinteiros + strdecimais.substring(0,decimais+1);
  }
  return true;
}



//====================== Fim =====================================

function desformataValor( campo, inteiros, decimais ) {
  if (typeof campo == "string") desValor = campo;
  else desValor = campo.value;

  if( desValor.length <= 0 ) {
     return "";
  } 
  else
  {
     if( verificaNumeroAlex(campo,inteiros,decimais) )
     {
        while(desValor.indexOf('.') != -1) {
          desValor = desValor.substring(0,desValor.indexOf('.')) + desValor.substring(desValor.indexOf('.')+1);
        }

        desValor = desValor.replace( ",", ".");
        return desValor;
     }
     else {
        return "?";
     }
  }
}

// este metodo é para substituir do cmvalidacoes
function formataNumeroAlex( campo, inteiros, decimais ) {

  if(! desformataNumeroAlex(campo,inteiros,decimais)) {
    return false;
  }

  if (typeof campo == "string") valor = campo;
  else valor = campo.value;

  if( valor.length > 0 )
  {
     sinal = '';
     if( valor.charAt(0) == '-')
     {
        sinal = '-';
        if( valor.length > 1 )
           valor = valor.substring(1);
        else
        {
           valor = '';
           return true;
        }
     }

     valor = valor.replace( '.', ',' );

     strdecimais = '';
     if( valor.indexOf(',') >= 0 )
     {
        strinteiros = valor.substring( 0, valor.indexOf(',') );
        strdecimais = valor.substring( valor.indexOf(',') );
     }
     else
     {
        strinteiros = valor;
     }
     cont = 0;
     fmtinteiros = '';
     for( i = strinteiros.length; i > 0; i-- )
     {
        cont++;
        if( cont == 4)
        {   cont = 1;
            fmtinteiros = '.' + fmtinteiros;
        }
        fmtinteiros = strinteiros.substring( i - 1, i ) + fmtinteiros;
     }

     if(strdecimais.length == 0)
       strdecimais = ",";

     for( i=strdecimais.length; i <= decimais; i++ )
       strdecimais += "0";

     campo.value = sinal + fmtinteiros + strdecimais.substring(0,decimais+1);
  }
  return true;
}

function validaClicou()
{
   if(document.iForm.clicou.value == "N")
   {  document.iForm.clicou.value = "S";
      return true;
   }
   else
   {  return false;
   }
}

function TESTACLICOU( destino )
{
   if(document.iForm.clicou.value == "N")
   {  return true;
   }
   return false;
}

function FNCNAVEGA( destino ) {
   if(document.iNavega.INAVEGACLICOU.value == "N")
   {  document.iNavega.INAVEGACLICOU.value = "S";
      setTimeout('setaclicou2()', 15000);
      document.location.href = destino;
      //return true;
   }
   //return false;
}

function TESTAFNCNAVEGA( destino )
{
   if(document.iNavega.INAVEGACLICOU.value == "N")
   {  //document.iNavega.INAVEGACLICOU.value = "S";
      //setTimeout('setaclicou2()', 15000);
      //document.location.href = destino;
      return true;
   }
   return false;
}

function FNCNAVEGACLICOU( destino ) {
   if(document.iForm.clicou.value == "N")
   {  document.iForm.clicou.value = "S";
      //setTimeout('setaclicou2()', 15000);
      document.iNavega.action = destino;
      document.iNavega.submit();
      //return true;
   }
   //return false;
}

function validaCGC (pCGC) {
  var W_CGC;
  var W_DV;
  var W_DV_AUX=0;
  var W_VAL_MULT;
  var y=0;
  var x=0;
      if(pCGC.length == 0)
      {  return true;
      }

      for(y=1; y <= 2; y++) {
        W_DV=0;
        W_VAL_MULT=2;

        while(pCGC.length < 14) {
            pCGC = "0" + pCGC;
        }
        W_CGC=pCGC.substring(0, y+11);

        for(x=y+10; x >= 0; x--) {
          if(W_VAL_MULT == 10)
          {  W_VAL_MULT = 2;
          }
          W_DV = W_DV + (W_CGC.substring(x,x+1)* W_VAL_MULT);
          W_VAL_MULT++;
        }
        W_DV = W_DV % 11;

        if(W_DV==0 || W_DV==1) {
          W_DV = 11;
        }

        if(y==1) {
          W_DV_AUX=(11-W_DV) * 10;
        } else {
        W_DV_AUX = W_DV_AUX + (11 - W_DV);
        }
      }
      while(pCGC.length < 14) {
          pCGC = "0" + pCGC;
      }
      if (W_DV_AUX==pCGC.substring(12,pCGC.length)) {
          return true;
      } else {
          return false;
      }
}

function validaCPF(str)
{
    var W_CPF;
    var W_DV=0;
    var W_DV_AUX=0;
    var W_VAL_MULT=0;
    var y = 0;
    var x = 0;
    
    for(y=1; y <= 2; y++)
    {
      W_DV=0;
      W_VAL_MULT=2;

      while(str.length < 11)
      { str = "0" + str;
      }

      W_CPF=str.substring(0, y+8);

      for(x=y+7; x >= 0; x--)
      {
        W_DV = W_DV + (W_CPF.substring(x,x+1) * W_VAL_MULT);
        W_VAL_MULT++;
      }

      W_DV = W_DV % 11;
      if(W_DV==0 || W_DV==1)
      { W_DV = 11;
      }

      if(y==1)
      {  W_DV_AUX=(11-W_DV) * 10;
      }
      else
      { W_DV_AUX = W_DV_AUX + (11 - W_DV);
      }
    }

    while(str.length < 11)
    {  str = "0" + str;
    }

    if (W_DV_AUX==str.substring(9))
    { return true;
    }
    else
    { return false;
    }
}

function validaValor( campo, inteiros, decimais ) {
  var filtro  = /^\d{0,3}\.\d{0,3}\.\d{0,3}\.\d{0,3}\,\d{0,decimais}$/ ;  

  if( campo.value.length <= 0 )
     return true;
  else
  {
     if ( filtro.test(campo.value)  )  
       return false;

     if(campo.value.indexOf(" ") != -1)
         return false;

     if(campo.value.indexOf('.') != -1)
       return false;

     var strinteiro = 0;
     var strdecimais = 0;
     if(campo.value.indexOf(',') != -1) {
       strinteiro = campo.value.substring(0,campo.value.indexOf(','));
       strdecimais = campo.value.substring(campo.value.indexOf(',')+1);
     }
     else
       strinteiro = campo.value;

     if(strinteiro.length > inteiros)
        return false;

     if(strdecimais.length > decimais)
        return false;

     if( (isNumber(strinteiro)) && (isNumber(strdecimais)) ) 
        return true;
     else
        return false;
  }
}
