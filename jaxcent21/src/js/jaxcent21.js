//  jaxcent.js   Copyright (C) Desiderata Software, 2008.  All rights reserved.
//  See complete license terms in the Jaxcent software distribution package.

var JaxcentFrameworkURL = "/servlet/JaxcentServlet21";

// Customizable messages.

function JaxcentOnConnectionLoss( httpStatus )
{
    alert( "Jaxcent Connection Lost (status: " + httpStatus + ")" );
}

function JaxcentServletNotRunning()
{
    alert( "JaxcentServlet is not running or not configured correctly at the URL \"" + JaxcentFrameworkURL + "\"" );
}

function JaxcentAjaxNotSupported()
{
    alert( "Browser does not support AJAX!" );
}

function JaxcentGetXmlHttp()
{
    try {
        return new XMLHttpRequest();
    } catch (e) {}
    try {
        return new ActiveXObject( "MSXML3.XMLHTTP" );
    } catch (e) {}
    try {
        return new ActiveXObject( "MSXML2.XMLHTTP.3.0" );
    } catch (e) {}
    try {
        return new ActiveXObject( "Msxml2.XMLHTTP" );
    } catch (e) {}
    try {
        return new ActiveXObject( "Microsoft.XMLHTTP" );
    } catch (e) {
        JaxcentAjaxNotSupported();
        return null;
    }
}

var jaxcentVersion = "2.1.1";

var jaxcentXmlHttp = JaxcentGetXmlHttp();
var jaxcentFormElements = [];
var jaxcentConnectionId = null;
var jaxcentInitialResponse = null;
var jaxcentPageLoaded = false;
var jaxcentFirstResponse = true;
var jaxcentServletVersion = "";
var jaxcentSendFormDataOnUnload = false;
var jaxcentLocatedElements;
var jaxcentSuppressErrorMessages = false;
var jaxcentEventVerifiers = [];
var jaxcentDragObject = null;
var jaxcentDragReset = null;
var jaxcentDropTargets = [];
var jaxcentDragDropDocEvents = false;
var jaxcentIsMsie = false;
var jaxcentIsOpera = false;
var jaxcentIsSafari = false;
var jaxcentBadBrowser = false;
var jaxcentPageUnloaded = false;
var jaxcentNoResponse = 0;
var jaxcentObjRefs = [];
var jaxcentCellEditInProgress = null;
var jaxcentTableEvents = [];

function JaxcentError( str )
{
    if ( jaxcentSuppressErrorMessages )
        return;
    str += "\n\n[Press cancel to suppress messages]";
    if ( ! confirm( str ))
        jaxcentSuppressErrorMessages = true;
}

function JaxcentDecode( arg )
{
    return decodeURIComponent( arg.replace(/\+/g, ' ' ));
}

function JaxcentPopArray( resp, count )
{
    if ( count <= 0 )
        return null;

    var i;
    var ret = [];
    for ( i = 0; i < count; i++ ) {
        ret.push( JaxcentDecode( resp.shift()));
    }
    return ret;
}

function JaxcentSetAttribute( element, attrname, value )
{
    // Check if attrname starts with "style."
    if ( attrname.indexOf( "style." ) == 0 ) {
        attrname = attrname.substr( 6 );
        element.style[ attrname ] = value;
        return;
    }
    // Check if attrname starts with "property."
    if ( attrname.indexOf( "property." ) == 0 ) {
        attrname = attrname.substr( 9 ).split( "." );
        while ( attrname.length > 1 )
            element = element[ attrname.shift() ];
        element[ attrname.shift() ] = value;
        return;
    }
    element.setAttribute( attrname, value );
    
}

function JaxcentGetAttribute( element, attrname, value )
{
    // Check if attrname starts with "style."
    if ( attrname.indexOf( "style." ) == 0 ) {
        attrname = attrname.substr( 6 );
        return element.style[ attrname ];
    }
    // Check if attrname starts with "property."
    if ( attrname.indexOf( "property." ) == 0 ) {
        attrname = attrname.substr( 9 ).split( "." );
        while ( attrname.length > 1 )
            element = element[ attrname.shift() ];
        return element[ attrname.shift() ];
    }
    return element.setAttribute( attrname, value );
}

function JaxcentAddOption( sel, opt, index )
{
    if ( index <= 0 || index >= sel.options.length ) {
        try {
            sel.add( opt, null );
            return;
        } catch (e) {
        }
        try {
            sel.add( opt, sel.options.length );
            return;
        } catch (e) {}
        sel.add( opt );
        return;
    }
    try {
        sel.add( opt, index );
        return;
    } catch (e) {}
    sel.add( opt, sel.options[index] );
}

function JaxcentFindChild( el, searchType, searchTag )
{
    if ( el == null ) return null;
    switch ( searchType ) {
        case 41: return el.nextSibling;
        case 42: return el.previousSibling;
        case 43: return el.parentNode;
        case 44: return el.firstChild;
        case 45: return el.lastChild;
        case 51:
        case 52:
            if ( searchType == 51 ) el = el.firstChild; else el = el.nextSibling;
            while ( el && el.tagName.toLowerCase() != "li" )
                el = el.nextSibling;
            return el;
        case 8:
        case 9:
            if ( searchType == 8 ) el = el.firstChild; else el = el.nextSibling;
            searchTag = searchTag.toLowerCase();
            while ( el && el.tagName.toLowerCase() != searchTag )
                el = el.nextSibling;
            return el;
    }
    return el;
}

function JaxcentFindElement( resp )
{
    var el, elements, index, str, i, j, oindex, tel;
    var attrCount, attrs, vals, havetext, eltext, elcount;
    var searchType = parseInt( resp.shift());
    if ( searchType <= 10 ) {
        str = JaxcentDecode( resp.shift());
    }
    switch ( searchType ) {
        case 1:   // Find by ID
            el = document.getElementById( str );
            if ( el == null )
                JaxcentError( "Jaxcent Error: Element with ID \"" + str + "\" was not found" );
            else if ( el.id != str )
                JaxcentError( "Jaxcent Warning: Mismatch in ID: \"" + el.id + "\" vs \"" + str + "\"" );
            return el;
        case 2:   // Find element by tag
            elements = document.getElementsByTagName( str );
            index = parseInt( resp.shift());
            if ( elements == null || index >= elements.length || elements[index] == null ) {
                JaxcentError( "Jaxcent Error: Element <" + str + ">[" + index + "] was not found" );
                return null;
            }
            return elements[index];
        case 3:   // Find by name
            elements = document.getElementsByName( str );
            index = parseInt( resp.shift());
            if ( elements == null || index >= elements.length || elements[index] == null ) {
                JaxcentError( "Jaxcent Error: ELEMENT <NAME=" + str + ">[" + index + "] was not found" );
                return null;
            }
            return elements[index];
        case 4:
            // Find INPUT element by INPUT TYPE
            elements = document.getElementsByTagName( "INPUT" );
            index = parseInt( resp.shift());
            oindex = index;
            str = str.toLowerCase();
            for ( i = 0; i < elements.length; i++ ) {
                var t = elements[i].getAttribute( "TYPE" );
                if ( t != null && str == t.toLowerCase()) {
                    if ( index <= 0 )
                        return elements[i];
                    index--;
                }
            }
            JaxcentError( "Jaxcent Error: INPUT Element <TYPE=" + str + ">[" + oindex + "] was not found" );
            return null;
        case 5:
            // Find INPUT element by VALUE
            elements = document.getElementsByTagName( "INPUT" );
            index = parseInt( resp.shift());
            oindex = index;
            str = str.toLowerCase();
            for ( i = 0; i < elements.length; i++ ) {
                var v = elements[i].getAttribute( "VALUE" );
                if ( v != null && str == v.toLowerCase()) {
                    if ( index <= 0 )
                        return elements[i];
                    index--;
                }
            }
            JaxcentError( "Jaxcent Error: INPUT Element <VALUE=" + str + ">[" + oindex + "] was not found" );
            return null;
        case 6:   // Find element by class
            elements = document.getElementsByClassName( str );
            index = parseInt( resp.shift());
            if ( elements == null || index >= elements.length || elements[index] == null ) {
                JaxcentError( "Jaxcent Error: Element <" + str + ">[" + index + "] was not found" );
                return null;
            }
            return elements[index];
        case 7:   // Create element
            havetext = resp.shift();
            if ( havetext != "0" ) {
                eltext = JaxcentDecode( resp.shift());
            }
            attrCount = parseInt( resp.shift());
            el = null;
            if ( attrCount > 0 ) {
                attrs = [];
                vals = [];
                var fullTag = "<" + str;
                for ( i = 0; i < attrCount; i++ ) {
                   attrs[i] = JaxcentDecode( resp.shift());
                   vals[i] = JaxcentDecode( resp.shift());
                   fullTag += " " + attrs[i] + "='" + vals[i] + "'";
               }
               fullTag += ">";
               try {
                   // Internet explorer doesn't handle setAttribute's correctly.  Try using the full tag.
                   el = document.createElement( fullTag );
               } catch (ex) {}
            }
            if ( el == null ) try {
                el = document.createElement( str );
                for ( i = 0; i < attrCount; i++ )
                    el.setAttribute( attrs[i], vals[i] );
            } catch (ex) {
                JaxcentError( "Jaxcent Error: Could not create element '" + str + "': " + ex.description );
                return null;
            }
            if ( havetext != "0" ) try {
                el.appendChild(document.createTextNode( eltext ));
            } catch (ex) {
                JaxcentError( "Jaxcent Error: Could not add text to <" + str + "> element: " + ex.description );
            }
            return el;
        case 11:
            return window;
        case 12:
            return window.screen;
        case 13:
            return document;
        case 14:
            return document.body;
        case 15:
            return location;
        case 21:
        case 22:
        case 23:
        case 24:
            oindex = parseInt( resp.shift());
            index = parseInt( resp.shift());
            tel = jaxcentFormElements[oindex];
            if ( tel == null ) {
                if ( searchType == 21 )
                    JaxcentError( "Jaxcent Error: Parent TABLE for row not found" );
                else if ( searchType == 22 )
                    JaxcentError( "Jaxcent Error: Parent TABLE for table cell (TD) not found" );
                else if ( searchType == 23 )
                    JaxcentError( "Jaxcent Error: Parent table row (TR) for cell (TD) not found" );
                else if ( searchType == 24 )
                    JaxcentError( "Jaxcent Error: Parent SELECT for OPTION not found" );
                return null;
            }
            if ( searchType == 21 )
                elements = tel.rows;
            else if ( searchType == 22 )
                elements = tel.cells;
            else if ( searchType == 23 )
                elements = tel.cells;
            else if ( searchType == 24 )
                elements = tel.options;
            if ( elements == null || index >= elements.length || elements[index] == null ) {
                if ( searchType == 21 )
                    JaxcentError( "Jaxcent Error: Table Row[" + index + "] was not found in table" );
                else if ( searchType == 22 )
                    JaxcentError( "Jaxcent Error: Table Cell[" + index + "] was not found in table" );
                else if ( searchType == 23 )
                    JaxcentError( "Jaxcent Error: Table Cell[" + index + "] was not found in table Row" );
                else
                    JaxcentError( "Jaxcent Error: OPTION[" + index + "] was not found in SELECT" );
                return null;
            }
            return elements[index];
        case 31:
            // Create TR
            oindex = parseInt( resp.shift());
            index = parseInt( resp.shift());
            elcount = parseInt( resp.shift());
            eltext = JaxcentPopArray( resp, elcount );
            attrCount = [];
            attrs = [];
            vals = [];
            for ( i = 0; i < elcount; i++ ) {
                var c2 = parseInt( resp.shift());
                attrCount.push( c2 );
                attrs.push( JaxcentPopArray( resp, c2 ));
                vals.push( JaxcentPopArray( resp, c2 ));
            }
            tel = jaxcentFormElements[oindex];
            if ( tel == null ) {
                JaxcentError( "Jaxcent Error: Parent TABLE for creating row not found" );
                return null;
            }
            try {
                if ( index < 0 || index >= tel.rows.length )
                    index = tel.rows.length;
                el = tel.insertRow( index );
                for ( i = 0; i < elcount; i++ ) {
                    var cell = el.insertCell( i );
                    cell.innerHTML = eltext[i];
                    var ac = attrCount[i];
                    var a = attrs.shift();
                    var v = vals.shift();
                    for ( j = 0; j < ac; j++ ) {
                        JaxcentSetAttribute( cell, a[j], v[j] );
                    }
                }
            } catch (ex) {
                JaxcentError( "Jaxcent Error: Could not create TR: " + ex.description );
                return null;
            }
            return el;
        case 32:
            // Create TD
            oindex = parseInt( resp.shift());
            index = parseInt( resp.shift());
            eltext = JaxcentDecode( resp.shift());
            attrCount = parseInt( resp.shift());
            attrs = JaxcentPopArray( resp, attrCount );
            vals = JaxcentPopArray( resp, attrCount );
            tel = jaxcentFormElements[oindex];
            if ( tel == null ) {
                JaxcentError( "Jaxcent Error: Parent TR for creating TD not found" );
                return null;
            }
            try {
                if ( index < 0 || index >= tel.cells.length )
                    index = tel.cells.length;
                el = tel.insertCell( index );
                el.innerHTML = eltext;
                for ( i = 0; i < attrCount; i++ ) {
                    JaxcentSetAttribute( el, attrs[i], vals[i] );
                }
            } catch (ex) {
                JaxcentError( "Jaxcent Error: Could not create TD: " + ex.description );
                return null;
            }
            return el;
        case 33:
            // Create OPTION
            oindex = parseInt( resp.shift());
            index = parseInt( resp.shift());
            eltext = JaxcentDecode( resp.shift());
            attrCount = parseInt( resp.shift());
            attrs = JaxcentPopArray( resp, attrCount );
            vals = JaxcentPopArray( resp, attrCount );
            tel = jaxcentFormElements[oindex];
            if ( tel == null ) {
                JaxcentError( "Jaxcent Error: Parent SELECT for adding OPTION not found" );
                return null;
            }
            try {
                el = document.createElement( "OPTION" );
                JaxcentAddOption( tel, el, index );
                el.text = eltext;
                for ( i = 0; i < attrCount; i++ ) {
                    if ( attrs[i].toLowerCase() == "value" )
                        el.value = vals[i];
                    else if ( attrs[i].toLowerCase() == "selected" ) 
                        el.selected = ( vals[i].toLowerCase() == "true" );
                    else
                        JaxcentSetAttribute( el, attrs[i], vals[i] );
                }
            } catch (ex) {
                JaxcentError( "Jaxcent Error: Could not create OPTION: " + ex.description );
                return null;
            }
            return el;
        case 41: // Next sibling
        case 42: // Prev sibling
        case 43: // Parent Node
        case 44: // First Child
        case 45: // Last Child
        case 51: // First LI in OL or UL
        case 52: // Next LI in OL or UL
        case 8:  // First Tag
        case 9:  // Next Tag
            return JaxcentFindChild( jaxcentFormElements[parseInt( resp.shift())], searchType, str );
    }
    return null;
}

function JaxcentElementExists( resp )
{
    var el, elements, index, str, i, j, oindex, tel;
    var attrCount, attrs, vals, havetext, eltext, elcount;
    var reqIndex = parseInt( resp.shift());   // Index of request
    var searchType = parseInt( resp.shift());
    if ( searchType <= 10 ) {
        str = JaxcentDecode( resp.shift());
    }
    var result = false;
    switch ( searchType ) {
        case 1:   // Find by ID
            if ( document.getElementById( str ) != null )
                result = true;
            break;
        case 2:   // Find element by tag
            elements = document.getElementsByTagName( str );
            index = parseInt( resp.shift());
            result = elements != null && index < elements.length && elements[index] != null;
            break;
        case 3:   // Find by name
            elements = document.getElementsName( str );
            index = parseInt( resp.shift());
            result = elements != null && index < elements.length && elements[index] != null;
            break;
        case 4:
            // Find INPUT element by INPUT TYPE
            elements = document.getElementsByTagName( "INPUT" );
            index = parseInt( resp.shift());
            str = str.toLowerCase();
            for ( i = 0; i < elements.length; i++ ) {
                var t = elements[i].getAttribute( "TYPE" );
                if ( t != null && str == t.toLowerCase()) {
                    if ( index <= 0 ) {
                        if ( elements[i] != null )
                            result = true;
                        break;
                    }
                    index--;
                }
            }
            break;
        case 5:
            // Find INPUT element by VALUE
            elements = document.getElementsByTagName( "INPUT" );
            index = parseInt( resp.shift());
            str = str.toLowerCase();
            for ( i = 0; i < elements.length; i++ ) {
                var v = elements[i].getAttribute( "VALUE" );
                if ( v != null && str == v.toLowerCase()) {
                    if ( index <= 0 ) {
                        if ( elements[i] != null )
                            result = true;
                        break;
                    }
                }
            }
            break;
        case 6:   // Find element by class
            elements = document.getElementsByClassName( str );
            index = parseInt( resp.shift());
            result = elements != null && index < elements.length && elements[index] != null;
            break;
        case 21:
        case 22:
        case 23:
        case 24:
            oindex = parseInt( resp.shift());
            index = parseInt( resp.shift());
            tel = jaxcentFormElements[oindex];
            if ( tel == null ) {
                result = false;
            } else {
                if ( searchType == 21 )
                    elements = tel.rows;
                else if ( searchType == 22 )
                    elements = tel.cells;
                else if ( searchType == 23 )
                    elements = tel.cells;
                else if ( searchType == 24 )
                    elements = tel.options;
                result = elements != null && index < elements.length && elements[index] != null;
            }
            break;
        case 41: // Next sibling
        case 42: // Prev sibling
        case 43: // Parent Node
        case 44: // First Child
        case 45: // Last Child
        case 51: // First LI in OL or UL
        case 52: // Next LI in OL or UL
        case 8:  // First element with a tag
        case 9:  // Next element with a tag
            result = JaxcentFindChild( jaxcentFormElements[parseInt( resp.shift())], searchType, str ) != null;
            break;
    }
    if ( result )
        return "response=" + reqIndex + "_1&";
    return "response=" + reqIndex + "_0&";
}

function JaxcentProperty( resp, doGet )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var eindex = parseInt( resp.shift());     // 0-based index of the element
    var property = JaxcentDecode( resp.shift());     // Property to get/set
    var jaxcentFormElement = jaxcentFormElements[ eindex ];
    var value;
    if ( ! doGet )
        value = JaxcentDecode( resp.shift());
    if ( jaxcentFormElement == null ) {
        return "error=" + reqIndex + "_Element Not Found&";
    }
    try {
        var props = property.split( "." );
        while ( props.length > 1 )
            jaxcentFormElement = jaxcentFormElement[ props.shift() ];
        property = props.shift();
        if ( doGet ) {
            var result = jaxcentFormElement[ property ];
            if ( typeof( result ) == 'undefined' )
                result = jaxcentFormElement.getAttribute( property );
            return "response=" + reqIndex + "_" + encodeURIComponent( result ) + "&";
        } else {
            jaxcentFormElement.setAttribute( property, value );
            jaxcentFormElement[ property ] = value;
        }
    } catch (ex) {
        if ( doGet )
            JaxcentError( "Jaxcent Error:  Property \"" + property + "\": " + ex.description );
        else
            JaxcentError( "Jaxcent Error:  Property \"" + property + "=" + value + "\": " + ex.description );
        return "error=" + reqIndex + "_" + encodeURIComponent( ex.description ) + "&";
    }
    return "response=" + reqIndex + "&";
}

function JaxcentEventHandler( arg, eventType, eindex, eventObject )
{
    // Check if there are any event verifiers.
    if ( jaxcentEventVerifiers.length > arg && typeof(jaxcentEventVerifiers[arg]) != "undefined" ) {
        var verifiers = jaxcentEventVerifiers[arg];
        for ( var i = 0; i < verifiers.length; i++ ) {
            try {
                if ( eval( verifiers[i] ) != true )
                    return;
            } catch (ex) {
                JaxcentError( "Jaxcent Error:  Error in event verifier: " + ex.description );
                return;
            }
        }
    }
    var xmlHttp = JaxcentGetXmlHttp();
    if ( xmlHttp == null )
        return;
    xmlHttp.open( "POST", JaxcentFrameworkURL, true );
    xmlHttp.setRequestHeader( "Content-Type", "application/x-www-form-urlencoded" ); 
    if ( eventType == 1 )
        xmlHttp.send( "conid=" + jaxcentConnectionId + "&event=" + arg );
    else if ( eventType == 2 )
        xmlHttp.send( "conid=" + jaxcentConnectionId + "&event=" + arg + "_" + JaxcentGetFormData());
    else if ( eventType == 3 ) {
        var el = jaxcentFormElements[ eindex ];
        var val = "";
        if ( el != null ) {
            val = el.value;
            if ( el.tagName.toUpperCase() == "INPUT" && ( el.type.toUpperCase() == "RADIO" || el.type.toUpperCase() == "CHECKBOX" )) {
                if ( ! el.checked )
                    val = "";
            } else if ( el.tagName.toUpperCase() == "SELECT" ) {
                if ( val == null || val == "" )
                    val = el.options[el.selectedIndex].text;
            }
        }
        xmlHttp.send( "conid=" + jaxcentConnectionId + "&event=" + arg + "_" + encodeURIComponent( val ));
    } else if ( eventType == 4 ) {
        // Selected Index
        var el = jaxcentFormElements[ eindex ];
        xmlHttp.send( "conid=" + jaxcentConnectionId + "&event=" + arg + "_" + el.selectedIndex );
    } else if ( eventType == 5 ) {
        xmlHttp.send( "conid=" + jaxcentConnectionId + "&event=" + arg + "_" + eindex );
    } else if ( eventType == 6 ) {
        xmlHttp.send( "conid=" + jaxcentConnectionId + "&event=" + arg + "_" + encodeURIComponent( eindex ));
    }
    try {
        if ( eventObject && eventObject.preventDefault ) eventObject.preventDefault();
    } catch (exc) {}
}

function JaxcentServerRequest()
{
    var xmlHttp = JaxcentGetXmlHttp();
    if ( xmlHttp == null )
        return;
    xmlHttp.open( "POST", JaxcentFrameworkURL, true );
    xmlHttp.setRequestHeader( "Content-Type", "application/x-www-form-urlencoded" );
    var nargs = JaxcentServerRequest.arguments.length;
    var args = nargs.toString();
    var i;
    for ( i = 0; i < nargs; i++ )
        args += "," + encodeURIComponent( JaxcentServerRequest.arguments[i] );
    xmlHttp.send( "conid=" + jaxcentConnectionId + "&request=" + encodeURIComponent( args ));
}

function JaxcentPropertyBool( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var eindex = parseInt( resp.shift());     // 0-based index of the element
    var property = JaxcentDecode( resp.shift());     // Property to get/set
    var jaxcentFormElement = jaxcentFormElements[ eindex ];
    var value = resp.shift();
     if ( jaxcentFormElement == null ) {
        return "error=" + reqIndex + "_Element Not Found&";
    }
    try {
        if ( value == "1" )
            jaxcentFormElement[ property ] = true;
        else
            jaxcentFormElement[ property ] = false;
    } catch (ex) {
        JaxcentError( "Jaxcent Error:  Property \"" + property + "\": " + ex.description );
        return "error=" + reqIndex + "_" + encodeURIComponent( ex.description ) + "&";
    }
    return "response=" + reqIndex + "&";
}

function JaxcentStyleBool( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var eindex = parseInt( resp.shift());     // 0-based index of the element
    var property = JaxcentDecode( resp.shift());     // Property to get/set
    var jaxcentFormElement = jaxcentFormElements[ eindex ];
    var value = resp.shift();
     if ( jaxcentFormElement == null ) {
        return "error=" + reqIndex + "_Element Not Found&";
    }
    try {
        if ( value == "1" )
            jaxcentFormElement.style[ property ] = true;
        else
            jaxcentFormElement.style[ property ] = false;
    } catch (ex) {
        JaxcentError( "Jaxcent Error:  Style \"" + property + "\": " + ex.description );
        return "error=" + reqIndex + "_" + encodeURIComponent( ex.description ) + "&";
    }
    return "response=" + reqIndex + "&";
}

function JaxcentAttribute( resp, doGet )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var eindex = parseInt( resp.shift());     // 0-based index of the element
    var attribute = JaxcentDecode( resp.shift());     // Property to get/set
    var jaxcentFormElement = jaxcentFormElements[ eindex ];
    var value;
    if ( ! doGet )
        value = JaxcentDecode( resp.shift());
    if ( jaxcentFormElement == null ) {
        return "error=" + reqIndex + "_Element Not Found&";
    }
    try {
        if ( doGet ) {
            var result = JaxcentGetAttribute( jaxcentFormElement, attribute );
            return "response=" + reqIndex + "_" + encodeURIComponent( result ) + "&";
        } else {
            JaxcentSetAttribute( jaxcentFormElement, attribute, value );
        }
    } catch (ex) {
        if ( doGet )
            JaxcentError( "Jaxcent Error:  Attribute \"" + attribute + "\": " + ex.description );
        else
            JaxcentError( "Jaxcent Error:  Attribute \"" + attribute + "=" + value + "\": " + ex.description );
       return "error=" + reqIndex + "_" + encodeURIComponent( ex.description ) + "&";
    }
    return "response=" + reqIndex + "&";
}

function JaxcentGetInnerText( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var eindex = parseInt( resp.shift());     // 0-based index of the element
    var jaxcentFormElement = jaxcentFormElements[ eindex ];
    if ( jaxcentFormElement == null ) {
        return "error=" + reqIndex + "_Element Not Found&";
    }
    try {
        if ( jaxcentFormElement.textContent )
            result = jaxcentFormElement.textContent;
        else if ( jaxcentFormElement.innerText )
            result = jaxcentFormElement.innerText;
        else
            result = jaxcentFormElement.innerHTML.replace(/<[^>]+>/g,"");
        return "response=" + reqIndex + "_" + encodeURIComponent( result ) + "&";
    } catch (ex) {
        JaxcentError( "Jaxcent Error:  getInnerText: " + ex.description );
        return "error=" + reqIndex + "_" + encodeURIComponent( ex.description ) + "&";
    }
}

function JaxcentSetInnerText( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var eindex = parseInt( resp.shift());     // 0-based index of the element
    var value = JaxcentDecode( resp.shift());
    var jaxcentFormElement = jaxcentFormElements[ eindex ];
    if ( jaxcentFormElement == null ) {
        return "error=" + reqIndex + "_Element Not Found&";
    }
    try {
        if ( jaxcentFormElement.textContent )
            jaxcentFormElement.textContent = value;
        else if ( jaxcentFormElement.innerText )
            jaxcentFormElement.innerText = value;
        else
            jaxcentFormElement.innerHTML = value;
        return "response=" + reqIndex + "&";
    } catch (ex) {
        JaxcentError( "Jaxcent Error:  setInnerText: " + ex.description );
        return "error=" + reqIndex + "_" + encodeURIComponent( ex.description ) + "&";
    }
}

function JaxcentInsertElement( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var eindex = parseInt( resp.shift());     // 0-based index of the element
    var insertType = parseInt( resp.shift()); // Insert type.
    var target;

    var jaxcentFormElement = jaxcentFormElements[ eindex ];
    if ( jaxcentFormElement == null ) {
        return "error=" + reqIndex + "_Element Not Found&";
    }
    var jaxcentTargetElement;
    if ( insertType != 1 && insertType != 2 ) {
        target = parseInt( resp.shift());     // Index of target element.
        jaxcentTargetElement = jaxcentFormElements[ target ];
        if ( jaxcentFormElement == null ) {
            return "error=" + reqIndex + "_Target element Not Found&";
        }
    }
    try {
        switch ( insertType ) {
            case 1:  // Insert at document beginning.
                document.body.insertBefore( jaxcentFormElement, document.body.firstChild );
                break;
            case 2:  // Insert at document end.
                document.body.appendChild( jaxcentFormElement );
                break;
            case 3:  // Insert just before target element
                jaxcentTargetElement.parentNode.insertBefore( jaxcentFormElement, jaxcentTargetElement );
                break;
            case 4:  // Insert just after target element
                if ( jaxcentTargetElement.nextSibling == null )
                    jaxcentTargetElement.parentNode.appendChild( jaxcentFormElement );
                else
                    jaxcentTargetElement.parentNode.insertBefore( jaxcentFormElement, jaxcentTargetElement.nextSibling );
                break;
            case 5:  // Insert within target element, at beginning
                jaxcentTargetElement.insertBefore( jaxcentFormElement, jaxcentTargetElement.firstChild );
                break;
            case 6:  // Insert within target element, at end
                jaxcentTargetElement.appendChild( jaxcentFormElement );
                break;
        }
    } catch (ex) {
        JaxcentError( "Jaxcent Error:  Element insertion failed: " + ex.description );
        return "error=" + reqIndex + "_" + encodeURIComponent( ex.description ) + "&";
    }
    return "response=" + reqIndex + "&";
}

function JaxcentStyleProperty( resp, doGet )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var eindex = parseInt( resp.shift());     // 0-based index of the element
    var property = JaxcentDecode( resp.shift());     // Property to get/set
    var jaxcentFormElement = jaxcentFormElements[ eindex ];
    var value;
    if ( ! doGet )
        value = JaxcentDecode( resp.shift());
    if ( jaxcentFormElement == null ) {
        return "error=" + reqIndex + "_Element Not Found&";
    }
    try {
        if ( doGet ) {
            var result = jaxcentFormElement.style[ property ];
            return "response=" + reqIndex + "_" + encodeURIComponent( result ) + "&";
        } else {
            jaxcentFormElement.style[ property ] = value;
        }
    } catch (ex) {
        if ( doGet )
            JaxcentError( "Jaxcent Error:  Style \"" + property + "\": " + ex.description );
        else
            JaxcentError( "Jaxcent Error:  Style \"" + property + "=" + value + "\": " + ex.description );
        return "error=" + reqIndex + "_" + encodeURIComponent( ex.description ) + "&";
    }
    return "response=" + reqIndex + "&";
}

function JaxcentDeleteRow( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var eindex = parseInt( resp.shift());     // 0-based index of the table element
    var dindex = parseInt( resp.shift());     // 0-based index of the row to be deleted
    var jaxcentFormElement = jaxcentFormElements[ eindex ];
    if ( jaxcentFormElement == null ) {
        return "error=" + reqIndex + "_Table Not Found&";
    }
    try {
        if ( dindex < 0 || dindex >= jaxcentFormElement.rows.length )
            dindex = jaxcentFormElement.rows.length - 1;
        jaxcentFormElement.deleteRow( dindex );
    } catch (ex) {
        JaxcentError( "Jaxcent Error:  Delete Row failed: " + ex.description );
        return "error=" + reqIndex + "_" + encodeURIComponent( ex.description ) + "&";
    }
    return "response=" + reqIndex + "&";
}

function JaxcentDeleteCell( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var eindex = parseInt( resp.shift());     // 0-based index of the row element
    var dindex = parseInt( resp.shift());     // 0-based index of the cell to be deleted
    var jaxcentFormElement = jaxcentFormElements[ eindex ];
    if ( jaxcentFormElement == null ) {
        return "error=" + reqIndex + "_Table Row Not Found&";
    }
    try {
        if ( jaxcentFormElement.cells.length == 0 ) {
            JaxcentError( "Jaxcent Error:  Deleting Cell from empty row" );
            return "error=" + reqIndex + "_Table Row is empty&";
        }
        if ( dindex < 0 || dindex >= jaxcentFormElement.cells.length )
            dindex = jaxcentFormElement.cells.length - 1;
        jaxcentFormElement.deleteCell( dindex );
    } catch (ex) {
        JaxcentError( "Jaxcent Error:  Delete Cell failed: " + ex.description );
        return "error=" + reqIndex + "_" + encodeURIComponent( ex.description ) + "&";
    }
    return "response=" + reqIndex + "&";
}

function JaxcentDeleteOption( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var eindex = parseInt( resp.shift());     // 0-based index of the SELECT element
    var dindex = parseInt( resp.shift());     // 0-based index of the OPTION to be deleted
    var jaxcentFormElement = jaxcentFormElements[ eindex ];
    if ( jaxcentFormElement == null ) {
        return "error=" + reqIndex + "_SELECT Not Found&";
    }
    try {
        if ( dindex < 0 || dindex >= jaxcentFormElement.options.length )
            dindex = jaxcentFormElement.options.length - 1;
        jaxcentFormElement.remove( dindex );
    } catch (ex) {
        JaxcentError( "Jaxcent Error:  Delete Option failed: " + ex.description );
        return "error=" + reqIndex + "_" + encodeURIComponent( ex.description ) + "&";
    }
    return "response=" + reqIndex + "&";
}

function JaxcentDeleteElementById( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var rid = JaxcentDecode( resp.shift());   // ID of element to delete.
    var el = document.getElementById( rid );
    if ( el == null ) {
        JaxcentError( "Jaxcent Error:  Element with ID " + rid + " not found" );
        return "error=" + reqIndex + "_Element Not Found&";
    }
    el.parentNode.removeChild( el );
    return "response=" + reqIndex + "&";
}

function JaxcentDeleteTableRow( reqIndex, el )
{
    var table = el.parentNode;
    while ( table.tagName && table.tagName.toLowerCase() != "table" ) {
        table = table.parentNode;
    }
    if ( typeof( table.tagName ) == "undefined" ) {
        return "error=" + reqIndex + "_Not Found&";
    } else try {
        table.deleteRow( el.rowIndex );
    } catch (ex) {
        JaxcentError( "Jaxcent Error:  Delete Row failed: " + ex.description );
        return "error=" + reqIndex + "_" + encodeURIComponent( ex.description ) + "&";
    }
    return "response=" + reqIndex + "&";
}

function JaxcentDeleteTableCell( reqIndex, el )
{
    var row = el.parentNode;
    while ( row.tagName && row.tagName.toLowerCase() != "tr" ) {
        row = row.parentNode;
    }
    if ( typeof( row.tagName ) == "undefined" ) {
        return "error=" + reqIndex + "_Not Found&";
    } else try {
        row.deleteCell( el.cellIndex );
    } catch (ex) {
        JaxcentError( "Jaxcent Error:  Delete Cell failed: " + ex.description );
        return "error=" + reqIndex + "_" + encodeURIComponent( ex.description ) + "&";
    }
    return "response=" + reqIndex + "&";
}

function JaxcentDeleteSelectOption( reqIndex, el )
{
    var selectElement = el.parentNode;
    while ( selectElement.tagName && selectElement.tagName.toLowerCase() != "select" ) {
        selectElement = selectElement.parentNode;
    }
    if ( typeof( selectElement.tagName ) == "undefined" ) {
        return "error=" + reqIndex + "_Not a OPTION&";
    } else try {
        selectElement.remove( el.index );
    } catch (ex) {
        JaxcentError( "Jaxcent Error:  Delete OPTION failed: " + ex.description );
        return "error=" + reqIndex + "_" + encodeURIComponent( ex.description ) + "&";
    }
    return "response=" + reqIndex + "&";
}

function JaxcentMethod( resp, returnResults )
{
    var jaxcentReqIndex = parseInt( resp.shift());   // Index of request
    var jaxcentEindex = parseInt( resp.shift());     // 0-based index of the element
    var jaxcentExpr = JaxcentDecode( resp.shift());         // Expression to evaluate
    var jaxcentFormElement = jaxcentFormElements[ jaxcentEindex ];
    if ( jaxcentFormElement == null ) {
        return "error=" + jaxcentReqIndex + "_Element Not Found&";
    }
    try {
        var result = eval( "jaxcentFormElement." + jaxcentExpr );
        if ( returnResults )
            return "response=" + jaxcentReqIndex + "_" + encodeURIComponent( result ) + "&";
    } catch (ex) {
        JaxcentError( "Jaxcent Error:  Evaluating <element>.\"" + jaxcentExpr + "\": " + ex.description );
        return "error=" + jaxcentReqIndex + "_" + encodeURIComponent( ex.description ) + "&";
    }
    return "response=" + jaxcentReqIndex + "&";
}

function JaxcentEvaluateExpr( resp, returnResults )
{
    var jaxcentReqIndex = parseInt( resp.shift());   // Index of request
    var jaxcentExpr = JaxcentDecode( resp.shift());         // Expression to evaluate
    try {
        var result = eval( jaxcentExpr );
        if ( returnResults )
            return "response=" + jaxcentReqIndex + "_" + encodeURIComponent( result ) + "&";
    } catch (ex) {
        JaxcentError( "Jaxcent Error:  Evaluating \"" + jaxcentExpr + "\": " + ex.description );
        return "error=" + jaxcentReqIndex + "_" + encodeURIComponent( ex.description ) + "&";
    }
    return "response=" + jaxcentReqIndex + "&";
}

function JaxcentDeleteItems( itemType, jaxcentFormElement, count, index, length )
{
    if ( count < 0 ) {
        count += length;
    }
    try {
        while ( count-- > 0 ) {
            switch ( itemType ) {
                case 1:
                    jaxcentFormElement.deleteCell( index );
                    break;
                case 2:
                    jaxcentFormElement.deleteRow( index );
                    break;
                case 3:
                    jaxcentFormElement.remove( index );
                    break;
            }
            if ( index > 0 )
                index--;
        }
    } catch (e) {}
}

function JaxcentCalls( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var eindex = parseInt( resp.shift());     // 0-based index of the element
    var index = parseInt( resp.shift());      // Call index
    var jaxcentFormElement = jaxcentFormElements[ eindex ];
    if ( jaxcentFormElement == null ) {
        return "error=" + reqIndex + "_Element Not Found&";
    }
    try {
        switch ( index ) {
            case 1:
                jaxcentFormElement.click();
                break;
            case 2:  
                jaxcentFormElement.focus();
                break;
            case 3:  
                jaxcentFormElement.blur();
                break;
            case 4:  
                jaxcentFormElement.scrollIntoView( true );
                break;
            case 5: 
                jaxcentFormElement.scrollIntoView( false );
                break;
            case 6: 
                jaxcentFormElement.setCapture();
                break;
            case 7: 
                jaxcentFormElement.releaseCapture();
                break;
            case 8: 
                jaxcentFormElement.select();
                break;
            case 9: 
                jaxcentFormElement.submit();
                break;
            case 10: 
                jaxcentFormElement.reset();
                break;
            case 11:
                if ( jaxcentFormElement.tagName.toLowerCase() == "tr" )
                    return JaxcentDeleteTableRow( reqIndex, jaxcentFormElement );
                if ( jaxcentFormElement.tagName.toLowerCase() == "td" )
                    return JaxcentDeleteTableCell( reqIndex, jaxcentFormElement );
                if ( jaxcentFormElement.tagName.toLowerCase() == "option" )
                    return JaxcentDeleteSelectOption( reqIndex, jaxcentFormElement );
               return "error=" + reqIndex + "_Unknown Delete tag " + jaxcentFormElement.tagName + "&";
            case 12:
               JaxcentDeleteItems( 1, jaxcentFormElement, jaxcentFormElement.cells.length, 0, 0 );
               break;
            case 13:
               JaxcentDeleteItems( 2, jaxcentFormElement, jaxcentFormElement.rows.length, 0, 0 );
               break;
            case 14:
               JaxcentDeleteItems( 2, jaxcentFormElement, parseInt( resp.shift()), 0, jaxcentFormElement.rows.length );
               break;
            case 15:
               JaxcentDeleteItems( 2, jaxcentFormElement, parseInt( resp.shift()), jaxcentFormElement.rows.length - 1, jaxcentFormElement.rows.length );
               break;
            case 16:
               JaxcentDeleteItems( 3, jaxcentFormElement, jaxcentFormElement.options.length, 0, 0 );
               break;
            case 17:
               JaxcentDeleteItems( 3, jaxcentFormElement, parseInt( resp.shift()), 0, jaxcentFormElement.options.length );
               break;
            case 18:
               JaxcentDeleteItems( 3, jaxcentFormElement, parseInt( resp.shift()), jaxcentFormElement.options.length - 1, jaxcentFormElement.options.length );
               break;
            case 19:
               jaxcentFormElement.size = jaxcentFormElement.options.length;
               break;
            case 20:
               jaxcentFormElement.scrollTop = jaxcentFormElement.scrollHeight;
               break;
            case 21:
               jaxcentFormElement.scrollTop = 0;
               break;
            case 22:
               // Delete element from DOM
               jaxcentFormElement.parentNode.removeChild( jaxcentFormElement );
               break;
            case 23:
               JaxcentEnableDragSource( jaxcentFormElement, eindex, true );
               break;
            case 24:
               JaxcentEnableDragSource( jaxcentFormElement, eindex, false );
               break;
            case 25:
               JaxcentEnableCellEdit( jaxcentFormElement, resp, true );
               break;
            case 26:
               JaxcentEnableCellEdit( jaxcentFormElement, resp, false );
               break;
            case 27:
               JaxcentAddDeleteButtons( jaxcentFormElement, resp );
               break;
            case 28:
               jaxcentFormElement.setAttribute( "JaxcentTableDataKey", JaxcentDecode( resp.shift()));
               break;
            case 29:
               jaxcentFormElement.src = JaxcentFrameworkURL + JaxcentDecode( resp.shift()).replace(/JaxcentConnectionId/g, jaxcentConnectionId );
               break;
            case 30:
               var jaxcentNewDiv = document.createElement( "DIV" );
               jaxcentNewDiv.innerHTML = '<IFRAME name="inline_' + eindex + '" id="inline_' + eindex + '" style="display:none"></iframe>';
               document.body.appendChild( jaxcentNewDiv );
               jaxcentFormElement.action = JaxcentFrameworkURL + "?conid=" + jaxcentConnectionId + "&objid=" + eindex;
               jaxcentFormElement.encoding = "multipart/form-data";
               jaxcentFormElement.method = "post";
               jaxcentFormElement.setAttribute( "target", "inline_" + eindex );
               break;
            default: 
               return "error=" + reqIndex + "_Unknown Jaxcent Call " + index + "&";
        }
    } catch (ex) {
        JaxcentError( "Jaxcent Error: " + ex.description );
        return "error=" + reqIndex + "_" + encodeURIComponent( ex.description ) + "&";
    }
    return "response=" + reqIndex + "&";
}

function JaxcentGetAllCookies( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    return "response=" + reqIndex + "_" + encodeURIComponent( document.cookie ) + "&";
}

function JaxcentGetCookie( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var cname = JaxcentDecode( resp.shift()) + "=";
    var cs = document.cookie.split( ';' );
    var c;
    while (( c = cs.shift()) != null ) {
        var i = 0;
        while ( c.charAt( i ) == ' ' && i < c.length )
            i++;
        if ( i > 0 )
            c = c.substr( i );
        if ( c.indexOf( cname ) == 0 ) {
            return "response=" + reqIndex + "_1" + encodeURIComponent( c.substr( cname.length )) + "&";
        }
    }
    return "response=" + reqIndex + "_0&";
}

function JaxcentDeleteCookie( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    document.cookie = JaxcentDecode( resp.shift()) + "=deleted; expires=Mon, 31 Dec 2007 01:00:00 UTC;";
    return "response=" + reqIndex + "&";
}

function JaxcentSetCookie( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var cookiestr = JaxcentDecode( resp.shift());
    document.cookie = cookiestr;
    return "response=" + reqIndex + "&";
}

function JaxcentAlert( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    alert( JaxcentDecode( resp.shift()));
    return "response=" + reqIndex + "&";
}


function JaxcentConfirm( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var result = confirm( JaxcentDecode( resp.shift()));
    if ( result == false )
        return "response=" + reqIndex + "_0&";
    else
        return "response=" + reqIndex + "_1&";
}

function JaxcentPrompt( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var result = prompt( JaxcentDecode( resp.shift()), JaxcentDecode( resp.shift()));
    if ( result == null )
        return "response=" + reqIndex + "_0&";
    else
        return "response=" + reqIndex + "_1" + encodeURIComponent( result ) + "&";
}

function JaxcentMiscSet( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var arg = JaxcentDecode( resp.shift());
    switch ( parseInt( resp.shift())) {       // Misc request id
        case 1:			// Navigate
            window.location.href = arg;
	    break;
        case 2:			// Set Status
            window.status = arg;
	    break;
        case 3:                 // Back
            history.back();
            break;
        case 4:                 // Forward
            history.forward();
            break;
    }
    return "response=" + reqIndex + "&";
}


function JaxcentMiscGet( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var result = null;
    switch ( parseInt( resp.shift())) {
        case 1:			// Get URL
            result = window.location.href;
	    break;
        case 2:			// Get Status
            result = window.status;
	    break;
        case 3:			// Get Screen Width
            result = screen.width;
	    break;
        case 4:			// Get Screen Height
            result = screen.height;
	    break;
        case 5:
            result = screen.width + "," + screen.height;
            break;
        case 6:
            if( typeof( window.innerWidth ) == 'number' ) {
                result = window.innerWidth + "," + window.innerHeight;
            } else {
                result = document.body.offsetWidth + "," + document.body.offsetHeight;
            }
            break;
    }
    if ( result == null )
        return "response=" + reqIndex + "_0&";
    else
        return "response=" + reqIndex + "_1" + encodeURIComponent( result ) + "&";
}

function JaxcentTableData( table, resp, doGet, attrs )
{
    var i, j, attrlist;
    if ( attrs )
        attrlist = JaxcentDecode( resp.shift()).split( "," );
    var firstRow = parseInt( resp.shift());
    var firstCol = parseInt( resp.shift());
    var nRows = parseInt( resp.shift());
    var aCols;
    if ( attrs ) {
        aCols = parseInt( resp.shift());
    }
    var allRows = nRows == -1;
    if (( doGet || attrs ) && firstRow == -1 ) firstRow = table.rows.length-1;
    if (( doGet || attrs ) && ( nRows == -1 || firstRow + nRows > table.rows.length ))
        nRows = table.rows.length - firstRow;
    var result = "";
    if ( doGet )
        result += "_" + nRows + ",";
    for ( i = 0; i < nRows; i++ ) {
        var nCols = attrs ? aCols : ( allRows ? -1 : parseInt( resp.shift()));
        var row = (firstRow + i) >= table.rows.length ? table.insertRow( table.rows.length ) : table.rows[firstRow+i];
        if ( doGet || attrs ) {
            if ( nCols == -1 || firstCol + nCols > row.cells.length )
                nCols = row.cells.length - firstCol;
        } else while ( row.cells.length < firstCol ) {
            row.insertCell( row.cells.length ).innerHTML = "&nbsp;";
        }
        if ( doGet )
            result += nCols + ",";
        for ( j = 0; j < nCols; j++ ) {
            var cell = (firstCol+j) >= row.cells.length ? row.insertCell( row.cells.length ) : row.cells[firstCol+j];
            if ( doGet )
                result += encodeURIComponent( cell.innerHTML ) + ",";
            else if ( attrs ) {
                for ( var k = 0; k < attrlist.length-1; k += 2 ) {
                    cell.style[ attrlist[k]] = attrlist[k+1];
                }
            } else {
                cell.innerHTML = JaxcentDecode( resp.shift());
            }
        }
    }
    return encodeURIComponent( result ) + "&";
}

function JaxcentMatchExpectedTag( expected, actual )
{
    expected = expected.toLowerCase();
    var aname = actual.tagName.toLowerCase();
    if ( expected.indexOf( "input," ) == 0 ) {
        if ( aname != "input" ) {
            JaxcentError( "Jaxcent Error: Expecting INPUT tag, found \"" + actual.tagName + "\"" );
            return;
        }
        expected = expected.substr( 6 );
        if ( expected != actual.type.toLowerCase()) {
            JaxcentError( "Jaxcent Error: Expecting INPUT element of type \"" + expected + "\", found \"" + actual.type + "\"" );
        }
        return;
    }
    if ( expected == aname )
            return;
    JaxcentError( "Jaxcent Error: Expecting tag \"" + expected + "\", found \"" + actual.tagName + "\"" );
}

function JaxcentDecodeArray( str )
{
    var ret = [];
    var s = str.split( "," );
    var count = parseInt( s.shift());
    for ( var i = 0; i < count; i++ ) {
        switch ( parseInt( s.shift())) {
            case 1:
                ret.push( jaxcentFormElements[ parseInt(s.shift())]);
                break;
            case 2:
                ret.push( parseInt(s.shift()));
                break;
            case 4:
                if ( s.shift() == "true" )
                    ret.push( true );
                else
                    ret.push( false );
                break;
            case 5:
                ret.push( parseFloat(s.shift()));
                break;
            case 3:
                ret.push( JaxcentDecode( s.shift()));
                break;
        }
    }
    return ret;
}

function JaxcentBuildEvalString( resp )
{
    var expr = JaxcentDecode( resp.shift());
    var exprCount, i;
    switch ( parseInt( resp.shift())) {
        case 1:
            if ( resp.shift() == '1' )
                expr += "(JaxcentDecode('" + resp.shift() + "'))";
            else
                expr += "(jaxcentFormElements[" + resp.shift() + "])";
            break;
        case 2:
            expr += "(JaxcentDecodeArray('" + resp.shift() + "'))";
            break;
        case 3:
            exprCount = parseInt( resp.shift());
            expr += "(";
            for ( i = 0; i < exprCount; i++ ) {
                if ( i > 0 )
                    expr += ",";
                switch ( parseInt( resp.shift())) {
                    case 1:
                        expr += "jaxcentFormElements[" + resp.shift() + "]";
                        break;
                    case 2:
                    case 4:
                    case 5:
                        expr += resp.shift();
                        break;
                    case 3:
                        expr += "JaxcentDecode('" + resp.shift() + "')";
                        break;
                }
            }
            expr += ")";
            break;
        case 4:
            break;
    }
    return expr;
}

function JaxcentEvalJS( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var result = "";
    var expr = JaxcentBuildEvalString( resp );
    try {
        result = eval( expr );
    } catch (ex) {
        JaxcentError( "Jaxcent Error in evaluating JavaScript: " + ex.description );
        return "error=" + reqIndex + "_" + encodeURIComponent( ex.description ) + "&";
    }
    return "response=" + reqIndex + "_" + encodeURIComponent( result ) + "&";
}

function JaxcentAddEventVerifier( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var eventIndex = parseInt( resp.shift());
    if ( typeof( jaxcentEventVerifiers[eventIndex] ) == 'undefined' ) {
        jaxcentEventVerifiers[eventIndex] = [];
    }
    jaxcentEventVerifiers[eventIndex].push( JaxcentBuildEvalString( resp ));
    return "response=" + reqIndex + "&";
}

function JaxcentSearchMultiple( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var reqTag = resp.shift();                // Tag to search for, if any
    var reqAttr = resp.shift();               // Attribute to search for, if any
    // Search elements that match.
    var elements;
    if ( reqTag == "" || reqTag == null )
        elements = document.all;
    else
        elements = document.getElementsByTagName( reqTag );
    var result = [];
    var values = [];
    if ( reqAttr != null && reqAttr != "" ) {
        var nel;
        for ( nel = 0; nel < elements.length; nel++ ) {
            var val = elements[nel].getAttribute( reqAttr );
            if ( val != null ) {
                result.push( elements[nel] );
                values.push( val );
            }
        }
    } else
        result = elements;
    if ( result.length == 0 ) {
        return "response=" + reqIndex + "_0_0_0&";
    }
    var retindex = JaxcentMakeObjectReference( result );
    var retstr = "response=" + reqIndex + "_" + result.length + "_" + retindex + "_" + values.length;
    if ( values.length > 0 ) {
        var str = "";
        var i;
        for ( i = 0; i < values.length; i++ ) {
            str += "+";
            str += encodeURIComponent( values[i] );
        }
        retstr += encodeURIComponent( str );
    }
    return retstr + "&";
}

function JaxcentDefineMultiple( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var eindex = parseInt( resp.shift());     // Element index start
    var count = parseInt( resp.shift());      // Number of elements
    var ref = resp.shift();                   // Client side reference

    var elements = JaxcentGetReferencedObject( ref );
    JaxcentRemoveObjectReference( ref );
    var i;
    for ( i = 0; i < count; i++ ) {
        jaxcentFormElements[ eindex ] = elements[i];
        eindex++;
    }
    return "response=" + reqIndex + "&";
}

function JaxcentSendRequest( query )
{
    if ( jaxcentPageUnloaded )
        return;
    // Send the response(s)
    jaxcentXmlHttp = JaxcentGetXmlHttp();
    jaxcentXmlHttp.onreadystatechange = JaxcentOnXmlHttpReadyStateChange;
    jaxcentXmlHttp.open( "POST", JaxcentFrameworkURL, true );
    jaxcentXmlHttp.setRequestHeader( "Content-Type", "application/x-www-form-urlencoded" ); 
    jaxcentXmlHttp.send( "conid=" + jaxcentConnectionId + "&" + query );
}

// Parse the response from server.

function JaxcentProcessResponse( status, responseText )
{
    var resp = null;
    if ( status == 200 && responseText != null && responseText != "" ) {
        resp = responseText.split("&");
    }
    if ( resp == null || resp.shift() != "jaxcent" ) {
        if ( jaxcentConnectionId == null ) {
            if ( jaxcentFirstResponse ) {
                if ( ! jaxcentPageUnloaded ) JaxcentServletNotRunning();
                jaxcentFirstResponse = false;
            }
        } else {
            jaxcentNoResponse++;
            if ( jaxcentNoResponse > 10 ) {
                JaxcentOnConnectionLoss( status );
            } else
                setTimeout( 'JaxcentSendRequest("")', 250 );
        }
        return;
    }
    jaxcentNoResponse = 0;
    jaxcentFirstResponse = false;
    jaxcentConnectionId = resp.shift();
    var action, addEvent, el, eindex, str, expectedTag;
    var query = "";
    if ( jaxcentDragReset )
        JaxcentResetDragSrc();
    try {
      while (( action = resp.shift()) != null ) {
        switch ( parseInt( action )) {
            case 1:
                jaxcentFormElements = []; // Initialize
                query += "load=1&";
                break;
            case 2:
                // Form element info
                eindex = parseInt( resp.shift());            // 0-based index of the element
                expectedTag = JaxcentDecode( resp.shift());  // Any expected tag?
                el = JaxcentFindElement( resp );             // Locate the element itself
                jaxcentFormElements[ eindex ] = el;
                addEvent = resp.shift();                     // Event handlers.
                if ( el == null ) {
                    query += "elementNotFound=" + eindex + "&";
                } else {
                    if ( expectedTag != "0" ) {
                        JaxcentMatchExpectedTag( expectedTag, el );
                    }
                    query += "elementFound=" + eindex + "&";
                    if ( addEvent != "0" ) {
                        var eventsList = addEvent.split( "," );
                        var eventName;
                        while (( eventName = eventsList.shift()) != null ) {
			    var eventIndex = parseInt( eventsList.shift());
                            var eventType = parseInt( eventsList.shift());
                            if ( eventName == "dragdrop" ) {
                                jaxcentDropTargets.push( { element: el, index: eventIndex, type: eventType, eindex: eindex } );
                            } else if ( eventName == "rowdeleted" || eventName == "celledited" ) {
                                jaxcentTableEvents.push( { table: el, event: eventName, index: eventIndex, type: eventType, eindex: eindex } );
                            } else
                                JaxcentAddEvent( el, eventName,
                                   new Function( "jaxcentEventObject", "JaxcentEventHandler(" + eventIndex + "," + eventType + "," + eindex + ",jaxcentEventObject)" ));
                        }
                    }
                }
                break;
            case 3:
                // Set property
                query += JaxcentProperty( resp, false );
                break;
            case 4:
                // Get Property
                query += JaxcentProperty( resp, true );
                break;
            case 5:
                // evaluate method call, do not return result
                query += JaxcentMethod( resp, true );
                break;
            case 6:
                // evaluate method call, return result
                query += JaxcentMethod( resp, true );
                break;
            case 7:
                // evaluate generic expression, do not return result
                query += JaxcentEvaluateExpr( resp, true );
                break;
            case 8:
                // evaluate generic expression, return result
                query += JaxcentEvaluateExpr( resp, true );
                break;
            case 9:
                // Set style property
                query += JaxcentStyleProperty( resp, false );
                break;
            case 10:
                // Get Style Property
                query += JaxcentStyleProperty( resp, true );
                break;
            case 21:
                // Configuration error
                alert( JaxcentDecode(resp.shift()));
                break;
            case 31:
                query += JaxcentCalls( resp );
                break;
            case 32:
                query += JaxcentAttribute( resp, false );
                break;
            case 33:
                query += JaxcentAttribute( resp, true );
                break;
            case 34:
                query += JaxcentInsertElement( resp );  // Insert a newly created element.
                break;
            case 35:
                query += JaxcentCreateTag( resp );      // Create element with a given tag
                break;
            case 36:
                query += JaxcentCreateTagText( resp );  // Create element with a given tag and given text.
                break;
            case 37:
                query += JaxcentGetAllCookies( resp );  // Get all cookies
                break;
            case 38:
                query += JaxcentGetCookie( resp );      // Get value of a cookie.
                break;
            case 39:
                query += JaxcentDeleteCookie( resp );   // Delete a cookie.
                break;
            case 40:
                query += JaxcentSetCookie( resp );      // Set a cookie.
                break;
            case 41:
                query += JaxcentGetInnerText( resp );
                break;
            case 42:
                query += JaxcentSetInnerText( resp );
                break;
            case 43:
                query += JaxcentAlert( resp );         // Show alert
                break;
            case 44:
                query += JaxcentConfirm( resp );       // Show confirm
                break;
            case 45:
                query += JaxcentPrompt( resp );        // Show prompt
                break;
            case 46:
                query += JaxcentPropertyBool( resp );  // Set boolean property
                break;
            case 47:
                query += JaxcentStyleBool( resp );     // Set boolean style
                break;
            case 48:
                jaxcentServletVersion = JaxcentDecode( resp.shift());
                break;
            case 49:
                query += JaxcentDeleteRow( resp );     // Delete table row
                break;
            case 50:
                query += JaxcentDeleteCell( resp );    // Delete table cell
                break;
            case 51:
                query += JaxcentDeleteOption( resp );  // Delete Select option
                break;
            case 52:
                query += JaxcentMiscSet( resp );       // Miscellaneous operations
                break;
            case 53:
                query += JaxcentMiscGet( resp );       // Miscellaneous operations
                break;
            case 54:
                jaxcentSendFormDataOnUnload = true;
                break;
            case 55:
                // Retrieve names of all INPUT elements.
                query += JaxcentFormNames();
                break;
            case 56:
                JaxcentFormSet( resp );                // Set values for retrieved names.
                break;
            case 57:                                   // Retrieve all form data
                query += "response=" + resp.shift() + "_" + JaxcentGetFormData();
                break;
            case 58:
                query += JaxcentElementExists( resp ); // Check if element exists.
                break;
            case 59:
                query += JaxcentEvalJS( resp );        // JavaScript evaluation.
                break;
            case 60:
                query += JaxcentAddEventVerifier( resp );   // JavaScript event verifier
                break;
            case 61:
                query += JaxcentDeleteElementById( resp );  
                break;
            case 64:
                break; // No action
            case 65:
                query += "response=" + resp.shift() + JaxcentTableData( jaxcentFormElements[ parseInt( resp.shift()) ], resp, true, false );
                break;
            case 66:
                query += "response=" + resp.shift() + JaxcentTableData( jaxcentFormElements[ parseInt( resp.shift()) ], resp, false, false );
                break;
            case 67:
                query += "response=" + resp.shift() + JaxcentTableData( jaxcentFormElements[ parseInt( resp.shift()) ], resp, false, true );
                break;
            case 68:
                query += JaxcentCellEditStart( resp );
                break;
            case 69:
                query += JaxcentSearchMultiple( resp );
                break;
            case 70:
                query += JaxcentDefineMultiple( resp );
                break;
        }
      }
    } catch (e) {}

    if ( jaxcentConnectionId == "0" )
        return; // Bad connection

    // Send the response(s)
    JaxcentSendRequest( query );
}

function JaxcentOnXmlHttpReadyStateChange()
{
    if ( jaxcentXmlHttp.readyState != 4 )
        return;
    if ( jaxcentPageLoaded ) {
        JaxcentProcessResponse( jaxcentXmlHttp.status, jaxcentXmlHttp.responseText );
    } else
        jaxcentInitialResponse = jaxcentXmlHttp.responseText;
}

function JaxcentAddEvent( obj, ev, handler )
{
    if ( obj.addEventListener ) {
        obj.addEventListener( ev, handler, false );
        return true;
    }
    if ( obj.attachEvent ) {
        obj.attachEvent( "on" + ev, handler );
        return true;
    }
    return false;
}

function JaxcentGetFormDataFromElements( elements, isInput, isSelect )
{
    if ( elements == null )
        return "";
    var str = "";
    for ( var i = 0; i < elements.length; i++ ) {
        var el = elements[i];
        name = el.name;
        if ( name == null || name == "" )
            name = el.id;
        if ( name == null || name == "" )
            continue;
        str += encodeURIComponent( name );
        str += "=";
        if ( isInput ) {
            var t = el.getAttribute( "type" );
            if ( t != null ) {
                t = t.toLowerCase();
                if (( t != "checkbox" && t != "radio" ) || el.checked ) {
                    str += encodeURIComponent( el.value );
                }
            } else
                str += encodeURIComponent( el.value );
        } else if ( isSelect ) {
            try {
                var val = el.options[el.selectedIndex].value;
                if ( val == null || val == "" )
                    val = el.options[el.selectedIndex].text;
                str += encodeURIComponent( el.selectedIndex + ":" + val );
            } catch (ex) {
                str += encodeURIComponent( "-1:" );
            }
        } else {
            str += encodeURIComponent( el.value );
        }
        str += "&";
    }
    return str;
}

function JaxcentGetFormDataTables()
{
    var str = "";
    var tables = document.getElementsByTagName( "TABLE" );
    if ( tables == null )
        return str;
    for ( var i = 0; i < tables.length; i++ ) {
        var table = tables[i];
        var key = table.getAttribute( "JaxcentTableDataKey" );
        if ( key == null || key == "" )
            continue;
        str += encodeURIComponent( key );
        str += "=";
        str += JaxcentTableData( table, [0,0,-1], true, false );
    }
    return str;
}

function JaxcentGetFormData()
{
    return encodeURIComponent(
             JaxcentGetFormDataFromElements( document.getElementsByTagName( "INPUT" ), true, false ) +
             JaxcentGetFormDataFromElements( document.getElementsByTagName( "SELECT" ), false, true ) +
             JaxcentGetFormDataFromElements( document.getElementsByTagName( "TEXTAREA" ), false, false ) +
             JaxcentGetFormDataTables()) + "&";
}

function JaxcentBadBrowserBlur()
{
    // Bad browser, send all data on any blur.
    var xmlHttp = JaxcentGetXmlHttp();
    if ( xmlHttp == null )
        return;
    xmlHttp.open( "POST", JaxcentFrameworkURL, true );
    xmlHttp.setRequestHeader( "Content-Type", "application/x-www-form-urlencoded" ); 
    var req = "conid=" + jaxcentConnectionId + "&async=1&formdata=" + JaxcentGetFormData();
    xmlHttp.send( req );
}

function JaxcentGetFormNamesFromElements( elements, isInput, isSelect )
{
    if ( elements == null )
        return "";
    var str = "";
    for ( var i = 0; i < elements.length; i++ ) {
        var el = elements[i];
        name = el.name;
        if ( name == null || name == "" )
            name = el.id;
        if ( name == null || name == "" )
            continue;
        if ( jaxcentBadBrowser ) {
            JaxcentAddEvent( el, "blur", JaxcentBadBrowserBlur );
        }
        if ( isInput ) {
            var t = el.getAttribute( "type" );
            if ( t == null )
                continue;
            t = t.toLowerCase();
            if ( t == "hidden" )
                continue;
            jaxcentLocatedElements.push( el );
            str += encodeURIComponent( name );
            str += "=";
            if ( t == "checkbox" )
                str += "1" + encodeURIComponent( el.value );
            else if ( t == "radio" )
                str += "2" + encodeURIComponent( el.value );
            else
                str += encodeURIComponent( t );
        } else if ( isSelect ) {
            jaxcentLocatedElements.push( el );
            str += encodeURIComponent( name ) + "=3";
        } else {
            jaxcentLocatedElements.push( el );
            str += encodeURIComponent( name ) + "=4";
        }
        str += "&";
    }
    return str;
}

function JaxcentFormNames()
{
    jaxcentLocatedElements = [];
    return "formnames=" + encodeURIComponent(
             JaxcentGetFormNamesFromElements( document.getElementsByTagName( "INPUT" ), true, false ) +
             JaxcentGetFormNamesFromElements( document.getElementsByTagName( "SELECT" ), false, true ) +
             JaxcentGetFormNamesFromElements( document.getElementsByTagName( "TEXTAREA" ), false, false )) + "&";;
}

function JaxcentFormSet( resp )
{
    var index;
    while ((index = parseInt( resp.shift())) > 0 ) {
        index--;
        // Set value at index'th element.
        var valType = parseInt( resp.shift());
        switch ( valType ) {
            case 1:
                jaxcentLocatedElements[ index ].checked = true;
                break;
            case 2:
                jaxcentLocatedElements[ index ].value = JaxcentDecode( resp.shift());
                break;
            case 3:
                jaxcentLocatedElements[ index ].selectedIndex = parseInt( resp.shift());
                break;
            case 4:
                jaxcentLocatedElements[ index ].checked = false;
                break;
        }
    }
    window.focus();
    jaxcentLocatedElements = null;
}

function JaxcentRemoveEvent( obj, ev, handler )
{
    if ( obj.addEventListener ) {
        obj.removeEventListener( ev, handler, false );
        return true;
    }
    if ( obj.attachEvent ) {
        obj.detachEvent( "on" + ev, handler );
        return true;
    }
    return false;
}

function JaxcentGetMousePos( e )
{
    if ( e && e.pageX ) {
        return { x: e.pageX, y: e.pageY };
    } else {
        return { x: window.event.clientX + document.body.scrollLeft - document.body.clientLeft,
                 y: window.event.clientY + document.body.scrollTop - document.body.clientTop };
    }
}

function JaxcentGetOffsets( obj )
{
    try {
        var left = obj.offsetLeft;
        var top = obj.offsetTop;
        while ( obj.offsetParent ) {
            obj = obj.offsetParent;
            left += obj.offsetLeft;
            top += obj.offsetTop;
        }
        return { x: left, y: top };
    } catch (e) {
        return null;
    }
}

function JaxcentDragMouseDown( e )
{
    if ( jaxcentDragObject ) {
        jaxcentDragReset = jaxcentDragObject;
        jaxcentDragObject;
        JaxcentResetDragSrc();
    }

    e = e || window.event;
    var mousePos = JaxcentGetMousePos( e );
    var obj = e.target != null ? e.target : e.srcElement;
    while ( obj ) {
        if ( obj.getAttribute( "JaxcentDragSource" ))
            break;
        obj = obj.parentNode;
    }
    if ( ! obj )
        return true;

    var ofs = JaxcentGetOffsets( obj );
    if ( ofs == null )
        return true;
    jaxcentDragObject = { object: obj, savedPos: obj.style.position, savedLeft: obj.style.left, savedTop: obj.style.top, dragX: mousePos.x - ofs.x, dragY: mousePos.y - ofs.y };
    return false;
}

function JaxcentDocumentMouseMove( event )
{
    if ( jaxcentDragObject ) {
        event = event || window.event;
        var mousePos = JaxcentGetMousePos( event );
        jaxcentDragObject.object.style.position = "absolute";
        jaxcentDragObject.object.style.left = mousePos.x - jaxcentDragObject.dragX;
        jaxcentDragObject.object.style.top = mousePos.y - jaxcentDragObject.dragY;
        if ( document.selection )
            document.selection.empty();
        else if ( window.getSelection )
            window.getSelection().removeAllRanges();
        return false;
    }
    return true;
}

function JaxcentGetDropTarget( event, src )
{
    var mousePos = JaxcentGetMousePos( event );
    // Search drop targets.
    for ( var i = 0; i < jaxcentDropTargets.length; i++ ) {
        var dt = jaxcentDropTargets[i].element;
        if ( dt == src || typeof( dt ) == 'undefined' )
            continue;
        var ofs = JaxcentGetOffsets( dt );
        if ( ofs == null )
            continue;
        if ( mousePos.x > ofs.x && mousePos.x < ( ofs.x + dt.offsetWidth )
            && mousePos.y > ofs.y && mousePos.y < ( ofs.y + dt.offsetHeight )) {
            // Found
            return jaxcentDropTargets[i];
        }
    }
    return null;
}


function JaxcentResetDragSrc()
{
    if ( jaxcentDragReset ) {
        jaxcentDragReset.object.style.left = jaxcentDragReset.savedLeft;
        jaxcentDragReset.object.style.top = jaxcentDragReset.savedTop;
        jaxcentDragReset.object.style.position = jaxcentDragReset.savedPos;
        if ( jaxcentIsMsie && jaxcentDragReset.object.tagName && jaxcentDragReset.object.tagName.toUpperCase() == "TR" ) {
            // IE saves offsets to last absolute pos, reset.
            jaxcentDragReset.object.style.display = "none";
            jaxcentDragReset.object.style.display = "block";
        }
        jaxcentDragReset = null;
    }
}

function JaxcentDocumentMouseUp( event )
{
    if ( jaxcentDragObject ) {
        var dt = JaxcentGetDropTarget( event, jaxcentDragObject.object );
        jaxcentDragReset = jaxcentDragObject;
        var eindex = jaxcentDragObject.object.getAttribute( "JaxcentElementIndex" );
        jaxcentDragObject = null;
        if ( dt == null )
            JaxcentResetDragSrc();
        else {
            // Send event to server
            JaxcentEventHandler( dt.index, 5, eindex, null );
            setTimeout( "JaxcentResetDragSrc()", 500 );
        }
    }
}

function JaxcentDisableOnDrag( event )
{
    event = event || window.event;
    event.cancelBubble = true;
    return false;
}

function JaxcentEnableDragSource( el, eindex, enable )
{
    if ( enable ) {
        el.setAttribute( "JaxcentDragSource", true );
        el.setAttribute( "JaxcentElementIndex", eindex );
        el.setAttribute( "JaxcentSavedCursor", el.style.cursor );
        el.setAttribute( "JaxcentSavedMousedown", el.onmousedown );
        el.style.cursor = "move";
        // JaxcentAddEvent( el, "mousedown", JaxcentDragMouseDown );
        el.onmousedown = JaxcentDragMouseDown;
        if ( jaxcentIsMsie ) {
            el.ondrag = JaxcentDisableOnDrag;
        }
        if ( ! jaxcentDragDropDocEvents ) {
            jaxcentDragDropDocEvents = true;
            JaxcentAddEvent( document, "mousemove", JaxcentDocumentMouseMove );
            JaxcentAddEvent( document, "mouseup", JaxcentDocumentMouseUp );
        }
    } else {
        el.removeAttribute( "JaxcentDragSource" );
        el.removeAttribute( "JaxcentElementIndex" );
        el.style.cursor = el.getAttribute( "JaxcentSavedCursor" );
        // JaxcentRemoveEvent( el, "mousedown", JaxcentDragMouseDown );
        el.onmousedown = el.getAttribute( "JaxcentSavedMousedown" );
    }
}

function JaxcentFireTableEvent( table, eventname, src, prev )
{
    for ( var i = 0; i < jaxcentTableEvents.length; i++ ) {
        if ( jaxcentTableEvents[i].table == table && jaxcentTableEvents[i].event == eventname ) {
            if ( eventname == "celledited" ) {
                var row = src.parentNode;
                while ( row && row.tagName && row.tagName.toLowerCase() != "tr" ) {
                    row = row.parentNode;
                }
                JaxcentEventHandler( jaxcentTableEvents[i].index, 6, row.rowIndex + "," + src.cellIndex + "," + encodeURIComponent( prev ) + "," + encodeURIComponent( src.innerHTML ), null );
            } else
                JaxcentEventHandler( jaxcentTableEvents[i].index, 5, src, null );
            return;
        }
    }
}

function JaxcentCancelCellEdit()
{
    if ( jaxcentCellEditInProgress == null )
        return true;

    jaxcentCellEditInProgress.editor.parentNode.removeChild( jaxcentCellEditInProgress.editor );
    jaxcentCellEditInProgress.measure.parentNode.removeChild( jaxcentCellEditInProgress.measure );

    jaxcentCellEditInProgress = null;    
    JaxcentRemoveEvent( document, "mouseup", JaxcentCellEditingCancelCheck );
    return true;
}

function JaxcentCellEditingCancelCheck( event )
{
    event = event || window.event;
    if ( jaxcentCellEditInProgress != null ) {
        // Check if mouse is inside the table.
        var mousePos = JaxcentGetMousePos( event );
        var ofs = JaxcentGetOffsets( jaxcentCellEditInProgress.table );
        if ( ofs != null && mousePos.x > ofs.x && mousePos.x < ( ofs.x + jaxcentCellEditInProgress.table.offsetWidth )
             && mousePos.y > ofs.y && mousePos.y < ( ofs.y + jaxcentCellEditInProgress.table.offsetHeight )) {
            return; // Inside the table
        }
        JaxcentCancelCellEdit();
    }
    return true;
}

function JaxcentCompleteCellEdit()
{
    if ( jaxcentCellEditInProgress == null )
        return;

    var prev = jaxcentCellEditInProgress.editCell.innerHTML;
    if ( jaxcentCellEditInProgress.allowHTML ) {
        jaxcentCellEditInProgress.editCell.innerHTML = jaxcentCellEditInProgress.editor.value;
    } else
        jaxcentCellEditInProgress.editCell.innerHTML = jaxcentCellEditInProgress.editor.value.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;");

    if ( prev != jaxcentCellEditInProgress.editCell.innerHTML )
        JaxcentFireTableEvent( jaxcentCellEditInProgress.table, "celledited", jaxcentCellEditInProgress.editCell, prev );
    JaxcentCancelCellEdit();
}

function JaxcentCellEditKeypress( e )
{
    if ( jaxcentCellEditInProgress == null )
        return;

    e = e || window.event;
    var key = e.keyCode ? e.keyCode : e.charCode;
    if ( key == 27 ) {
        JaxcentCancelCellEdit();
    } else if ( key == 13 ) {
        JaxcentCompleteCellEdit();
    } else {
        jaxcentCellEditInProgress.measure.innerHTML = jaxcentCellEditInProgress.editor.value.replace(/&/g,"&amp;").replace(/ /g,"&nbsp:" ).replace(/</g,"&lt;") + "&nbsp;" + String.fromCharCode( key );
        if ( jaxcentCellEditInProgress.editCell.offsetWidth < jaxcentCellEditInProgress.measure.offsetWidth )
            jaxcentCellEditInProgress.editor.style.width = jaxcentCellEditInProgress.measure.offsetWidth + "px";
        else
            jaxcentCellEditInProgress.editor.style.width = jaxcentCellEditInProgress.editCell.offsetWidth + "px";
    }
    return true;
}

function JaxcentCancelMouseup( e )
{
    e = e || window.event;
    e.cancelBubble = true;
    return false;
}

function JaxcentCellEditStart( resp )
{
    var reqIndex = parseInt( resp.shift());   // Index of request
    var table = jaxcentFormElements[ parseInt( resp.shift()) ]; // Table
    var row = parseInt( resp.shift());
    var col = parseInt( resp.shift());
    if ( row < table.rows.length && col < table.rows[row].cells.length ) {
        var cell = table.rows[row].cells[col];
        var editFn = cell.jaxcentEditFunction;
        if ( editFn ) editFn( { target: cell } );
    }
    return "response=" + reqIndex + "&";
}

function JaxcentCellEdit( e, attrs )
{
    e = e || window.event;
    var cell = e.target ? e.target : e.srcElement;

    if ( jaxcentCellEditInProgress != null ) {
        JaxcentCompleteCellEdit();
    }
    JaxcentAddEvent( document, "mouseup", JaxcentCellEditingCancelCheck );
    e.cancelBubble = true;
    var text = document.createElement( "INPUT" );
    document.body.appendChild( text );
    var ofs = JaxcentGetOffsets( cell );
    text.style.position = "absolute";
    text.style.left = ofs.x + "px";
    text.style.top = ofs.y + "px";
    text.style.width = cell.offsetWidth + "px";
    text.style.height = cell.offsetHeight + "px";
    var span = document.createElement( "SPAN" );
    span.style.visibility = "hidden";
    var editAttrs = attrs.editAttrs;
    for ( var i = 0; i < editAttrs.length - 1; i += 2 ) try {
        text.style[ editAttrs[i]] = editAttrs[i+1];
        span.style[ editAttrs[i]] = editAttrs[i+1];
    } catch (e) {}
    text.type = "text";
    text.value = cell.innerHTML;
    text.onkeypress = JaxcentCellEditKeypress;
    text.onmouseup = JaxcentCancelMouseup;
    text.focus();

    document.body.appendChild( span );
    jaxcentCellEditInProgress = { table: attrs.editTable, editCell: cell, editor: text, measure: span, allowHTML: attrs.html };
    return false;
}

function JaxcentEnableCellEdit( table, resp, enable )
{
    var i, j, k, row, clickfn, eAttrs;
    var startRow = parseInt( resp.shift());
    var startCol = parseInt( resp.shift());
    var endRow = parseInt( resp.shift());
    var endCol = parseInt( resp.shift());
    var singleClick = resp.shift() == "1";
    var allowHTML = resp.shift() == "1";
    var attrs = JaxcentDecode( resp.shift()).split( "," );
    if ( startRow == -1 ) startRow = table.rows.length - 1;
    if ( endRow == -1 || endRow >= table.rows.length ) endRow = table.rows.length - 1;
    if ( enable ) {
        var eAttrs = { editTable: table, editAttrs: attrs, html: allowHTML };
        clickfn = function(e) { JaxcentCellEdit( e, eAttrs ); };
    } else
        clickfn = null;
 
    for ( i = startRow; i <= endRow; i++ ) {
        row = table.rows[ i ];
        j = startCol;
        if ( j == -1 ) j = row.cells.length - 1;
        k = endCol;
        if ( k == -1 || k >= row.cells.length ) k = row.cells.length - 1;
        while ( j <= k ) {
            if ( singleClick )
                row.cells[j].onclick = clickfn;
            else
                row.cells[j].ondblclick = clickfn;
            row.cells[j].jaxcentEditFunction = clickfn;
            j++;
        }
    }
}

function JaxcentTableRowDelete( table, row )
{
    JaxcentCancelCellEdit(); // Cancel any in-place edits.
    var index = row.rowIndex;
    table.deleteRow( index );
    JaxcentFireTableEvent( table, "rowdeleted", index, null );
}

function JaxcentTableRowDeleteClosure( table, row )
{
    return function(e) { JaxcentTableRowDelete( table, row ); };
}

function JaxcentAddDeleteButtons( table, resp )
{
    var startRow = parseInt( resp.shift());
    var endRow = parseInt( resp.shift());
    var html = JaxcentDecode( resp.shift());
    if ( startRow == -1 ) startRow = table.rows.length - 1;
    if ( endRow == -1 || endRow >= table.rows.length ) endRow = table.rows.length - 1;
    for ( var i = startRow; i <= endRow; i++ ) {
        var row = table.rows[ i ];
        var cell = row.insertCell( row.cells.length );
        cell.innerHTML = html;
        cell.onclick = JaxcentTableRowDeleteClosure( table, row );
    }
}

function JaxcentMakeObjectReference( obj )
{
    for ( var i = 0; i < jaxcentObjRefs.length; i++ ) {
        if ( jaxcentObjRefs[i] == null ) {
            jaxcentObjRefs[i] = obj;
            return i;
        }
    }
    jaxcentObjRefs.push( obj );
    return jaxcentObjRefs.length - 1;
}

function JaxcentGetReferencedObject( ref )
{
    try {
        return jaxcentObjRefs[ref];
    } catch (e) {}
    return null;
}

function JaxcentRemoveObjectReference( ref )
{
    try {
        jaxcentObjRefs[ref] = null;
    } catch (e) {}
}

function JaxcentOnUnload()
{
    jaxcentPageUnloaded = true;
    var xmlHttp = JaxcentGetXmlHttp();
    if ( xmlHttp == null )
        return;
    xmlHttp.open( "POST", JaxcentFrameworkURL, true );
    xmlHttp.setRequestHeader( "Content-Type", "application/x-www-form-urlencoded" ); 
    var req = "conid=" + jaxcentConnectionId + "&unload=1";
    if ( jaxcentSendFormDataOnUnload ) {
        req += "&formdata=" + JaxcentGetFormData();
    }
    xmlHttp.send( req );
    try {
        jaxcentXmlHttp.abort();
    } catch (e) {}
}

function JaxcentOnLoad()
{
    jaxcentPageLoaded = true;
    JaxcentAddEvent( window, "unload", JaxcentOnUnload );

    if ( jaxcentInitialResponse != null ) {
        JaxcentProcessResponse( 200, jaxcentInitialResponse );
        jaxcentInitialResponse = null;
    }
}

function JaxcentInitialize()
{
    if ( ! JaxcentAddEvent( window, "load", JaxcentOnLoad )) {
        alert( "Failed to add event for OnLoad, Jaxcent not initialized" );
        return;
    }
    if ( jaxcentXmlHttp == null )
        return;

    jaxcentIsMsie = navigator.userAgent.toLowerCase().indexOf( "msie" ) >= 0 && navigator.userAgent.toLowerCase().indexOf( "opera" ) < 0;
    jaxcentIsOpera = window.opera ? true : false;
    jaxcentIsSafari = ( navigator.vendor && navigator.vendor.toLowerCase().indexOf( "apple" ) >= 0 ) ? true : false;
    jaxcentBadBrowser = jaxcentIsOpera || jaxcentIsSafari;  // Add any more here that don't handle XMLHTTP correctly during termination

    jaxcentXmlHttp.onreadystatechange = JaxcentOnXmlHttpReadyStateChange;
    var query = "url=" + encodeURIComponent( location.pathname + location.search ) + "&version=" + jaxcentVersion + "&jaxurl=" + encodeURIComponent( JaxcentFrameworkURL );
    jaxcentXmlHttp.open( "POST", JaxcentFrameworkURL, true );
    jaxcentXmlHttp.setRequestHeader( "Content-Type", "application/x-www-form-urlencoded" ); 
    jaxcentXmlHttp.send( query );
}

JaxcentInitialize();
