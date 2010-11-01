// Basic Element Animator (11-October-2008) DRAFT
// by Vic Phillips http://www.vicsjavascripts.org.uk

// To progressively change the Left, Top, Width, Height or Opacity of an element over a specified period of time.

// **** Application Notes

// **** The HTML Code
//
// when moving an element the inline or class rule style position of the element should be assigned as
// 'position:relative;' or 'position:absolute;'
//
// The element would normally be assigned a unique ID name.
//

// **** Executing the Effect(Script)
//
// The effect is executed by an event call to function 'zxcBAnimator('left','tst1',20,260,2000);'
// where:
// parameter 0 = the mode(see Note 1).                                                                     (string)
// parameter 1 = the unique ID name or element object.                                                     (string or element object)
// parameter 2 = the start position of the effect.                                                         (digits, for opacity minimum 0, maximum 100)
// parameter 3 = the finish position of the effect.                                                        (digits, for opacity minimum 0, maximum 100)
// parameter 4 = (optional) period of time between the start and finish of the effect in milliseconds.     (digits or defaults to 2000 milliSeconds)
//
//  Note 1:  The default units(excepting opacity) are 'px'.
//  Note 2:  Examples modes: 'left', 'top', 'width', 'height', 'opacity.
//           For hyphenated modes, the first character after the hyphen must be upper case, all others lower case.
//  Note 3:  To 'toggle' the effect include '#' in parameter 0.
//           The first call will set the toggle parameters.
//           Subsequent calls with '#' in parameter 0 and the same start and finish parameters will 'toggle' the effect.
//  Note 4:  The function may be re-executed with a different set of parameters (start/finish time or period)
//           whenever required, say from an onclick/mouseover/out event.
//           The period parameter will be retained unless re-specified.
//
// **** Advanced Applications
//
//  It may be required to access the current value of the effect.
//  The element effect is accessible from the element property
//  element effect = elementobject[mode.replace(/[-#]/g,'')+'oop'];
//  where mode is parameter 0 of the initial call.
//  An array storing the current, start and finish values of the element effect may be accessed
//  from the element effect.data as fields 0, 1 and 2 respectively
//

// **** General
//
// All variable, function etc. names are prefixed with 'zxc' to minimise conflicts with other JavaScripts.
// These characters may be changed to characters of choice using global find and replace.
//
// The Functional Code(about 2K) is best as an External JavaScript.
//
// Tested with IE7 and Mozilla FireFox on a PC.
//



// **** Functional Code - NO NEED to Change


function zxcBAnimator(zxcmde,zxcobj,zxcsrt,zxcfin,zxctime){
if (typeof(zxcobj)=='string'){ zxcobj=document.getElementById(zxcobj); }
if (!zxcobj||(!zxcsrt&&!zxcfin)||zxcsrt==zxcfin) return;
var zxcoop=zxcobj[zxcmde.replace(/[-#]/g,'')+'oop'];
if (zxcoop){
clearTimeout(zxcoop.to);
if (zxcoop.srtfin[0]==zxcsrt&&zxcoop.srtfin[1]==zxcfin&&zxcmde.match('#')) zxcoop.update([zxcoop.data[0],(zxcoop.srtfin[0]==zxcoop.data[2])?zxcfin:zxcsrt],zxctime);
else zxcoop.update([zxcsrt,zxcfin],zxctime);
}
else zxcobj[zxcmde.replace(/[-#]/g,'')+'oop']=new zxcBAnimatorOOP(zxcmde,zxcobj,zxcsrt,zxcfin,zxctime);
}

function zxcBAnimatorOOP(zxcmde,zxcobj,zxcsrt,zxcfin,zxctime){
this.srtfin=[zxcsrt,zxcfin];
this.to=null;
this.obj=zxcobj;
this.mde=zxcmde.replace(/[-#]/g,'');
this.update([zxcsrt,zxcfin],zxctime);
}

zxcBAnimatorOOP.prototype.update=function(zxcsrtfin,zxctime){
this.time=zxctime||this.time||2000;
this.data=[zxcsrtfin[0],zxcsrtfin[0],zxcsrtfin[1]];
this.srttime=new Date().getTime();
this.cng();
}

zxcBAnimatorOOP.prototype.cng=function(){
var zxcms=new Date().getTime()-this.srttime;
this.data[0]=(this.data[2]-this.data[1])/this.time*zxcms+this.data[1];
if (this.mde!='left'&&this.mde!='top'&&this.data[0]<0) this.data[0]=0;
if (this.mde!='opacity') this.obj.style[this.mde]=this.data[0]+'px';
else  zxcOpacity(this.obj,this.data[0]);
if (zxcms<this.time) this.to=setTimeout(function(zxcoop){return function(){zxcoop.cng();}}(this),10);
else {
this.data[0]=this.data[2];
if (this.mde!='opacity') this.obj.style[this.mde]=this.data[0]+'px';
else zxcOpacity(this.obj,this.data[0]);
}
}

function zxcOpacity(zxcobj,zxcopc){
if (zxcopc<0||zxcopc>100) return;
zxcobj.style.filter='alpha(opacity='+zxcopc+')';
zxcobj.style.opacity=zxcobj.style.MozOpacity=zxcobj.style.KhtmlOpacity=zxcopc/100-.001;
}

function slide(zxcobj,zxcid,zxcsrt,zxcfin){
clearTimeout(zxcobj.to);
zxcBAnimator('top#',zxcid,zxcsrt,zxcfin,400);
zxcobj.to=setTimeout(function(){ zxcobj.value=zxcobj.value=='Click to Open'?'Click to Close':'Click to Open'; },1000);
}

var visible = false;

function slideEditor()
{
    if(visible)
    {
        document.getElementById('EditorCont').style.zIndex=0;
        slide(this,'EditorDiv',0,-410);
        visible = false;
        //this.obj.style['z-index']=-1;
    }
    else
    {
        document.getElementById('EditorCont').style.zIndex=0;
        //this.obj.style['z-index']=5;
        slide(this,'EditorDiv',-410,0);
        visible = true;
        document.getElementById('EditorCont').style.zIndex=2;
    }
}