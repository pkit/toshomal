/*
 * Copyright (c) 2008, Desiderata Software
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 
 * 
 * Neither the name of Desiderata Software nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific
 * prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * Module Name:           JaxcentPage.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Base class for Jaxcent user-written pages.
 *
 * Change History:
 *
 *    12/25/2007  MP      Initial version.
 *     6/30/2008  MP      Ext support.
 */

package jaxcent;

import java.lang.reflect.*;
import java.util.*;

/**
  * JaxcentPage is the base class from which all Jaxcent URL handlers need to be derived.
  * In cases where no particular handler is needed (when using auto-session-management
  * and when no data verification is needed), this class can be specified directly as the handler.
  *
  * This class provides over-ridable methods to process operations like load and unload.
  * It also provides various common utility methods, such as showing JavaScript
  * alert (dialog box) messages.
  */

public class JaxcentPage {
    Vector elements = new Vector();
    boolean loaded = false;
    StringBuffer pageQuery = new StringBuffer();
    Object[] pageLoadObject;
    Vector eventObjects = null;
    Vector eventMethods = null;
    Vector eventTypes = null;
    Hashtable reqSync = new Hashtable();
    Hashtable reqError = new Hashtable();
    int eventsIndex = 0;
    int reqIndex = 1;
    Object session = null;
    Object ctx = null;
    Hashtable map = null;
    HashMap tableKeys = null;
    boolean autoSession = false;
    String path = null;
    String remoteAddr = null;
    String remoteAcceptLanguages = null;
    Hashtable finalData = null;
    HashMap imageList = null;
    boolean doBatchUpdates = false;
    boolean unloaded = false;
    boolean sessionUpdatingEnabled = true;
    Object extObject;
    String clientVersion;
    String clientUrl = "/servlet/JaxcentServlet21";
    static String jaxcentVersion = "2.1.1";   // A copy is also maintained in JaxcentFramework.java

    static int imageIndex = 1;

    static final String defaultCellEditorAttrs = "border,1px solid lightblue,backgroundColor,lightyellow,"; // Default cell editor attributes

    static ThreadLocal tls = new ThreadLocal();

    static JaxcentPageExt[] pageExts = null;

    static long QUERY_TIMEOUT = 20000L;

    static {
        try {
            Class.forName( "jaxcent.JaxcentPagePro" );  // Load the pro version if available
        } catch (Exception ex) {}
    }

    static class ThreadLocalData {
        Object tlsSession;
        Object tlsCtx;
        Hashtable tlsMap;
        boolean tlsAutoSession;
        String tlsPath;
        String tlsRemoteAddr;
        String tlsRemoteAcceptLanguages;
    }

    static class IgnorecaseMap implements Map {

        Map map;

        IgnorecaseMap( Map parent )
        {
            this.map = parent;
        }

        Object remap( Object key )
        {
            if ( key instanceof String )
                return ((String) key).trim().toLowerCase();
            return key;
        }

        public void clear() { map.clear(); }
        public boolean containsKey(Object key) { return map.containsKey( key ); }
        public boolean containsValue(Object value)  { return map.containsValue( value ); }
        public Set entrySet()  { return map.entrySet(); }
        public boolean equals(Object o) { return map.equals( o ); }

        public Object get(Object key)
        {
            return map.get( remap( key ));
        }

        public int hashCode() { return map.hashCode(); }
        public boolean isEmpty() { return map.isEmpty(); }
        public Set keySet()  { return map.keySet(); }

        public Object put(Object key, Object value)
        {
            return map.put( remap( key ), value );
        }

        public void putAll(Map t)  { map.putAll( t ); }
        public Object remove(Object key)  { return map.remove( key ); }
        public int size()  { return map.size(); }
        public Collection values()  { return map.values(); }

        public String toString() { return map.toString(); }
    };


    static String encode( String str )
    {
        try {
            if ( str == null )
                return "";
            return java.net.URLEncoder.encode( str, "UTF-8" );
        } catch (Exception ex) {}
        return "";
    }

    static String decode( String str )
    {
        try {
            if ( str == null )
                return "";
            return java.net.URLDecoder.decode( str, "UTF-8" );
        } catch (Exception ex) {}
        return "";
    }

    static String[] split( String line, char splitter )
    {
        int index = -1;
        int count = 1;
        while (( index = line.indexOf( splitter, index+1 )) >= 0 && index < line.length() - 1 )
            count++;
        String[] result = new String[ count ];
        index = 0;
        for ( int i = 0; i < count; i++ ) {
            int nextIndex = line.indexOf( splitter, index );
            if ( nextIndex < 0 )
                nextIndex = line.length();
            result[ i ] = line.substring( index, nextIndex );
            index = nextIndex + 1;
        }
        return result;
    }

    static synchronized void addPageExt( JaxcentPageExt ext )
    {
        int len = 0;
        if ( pageExts != null )
            len = pageExts.length;
        JaxcentPageExt[] exts = new JaxcentPageExt[len+1];
        if ( pageExts != null )
            System.arraycopy( pageExts, 0, exts, 0, len );
        exts[len] = ext;
        pageExts = exts;
    }

    static synchronized void removePageExt( JaxcentPageExt ext )
    {
        if ( pageExts == null ) return;
        int len = pageExts.length;
        if ( len <= 1 ) {
            pageExts = null;
            return;
        }
        JaxcentPageExt[] exts = new JaxcentPageExt[len-1];
        System.arraycopy( pageExts, 0, exts, 0, len-1 );
        pageExts = exts;
    }

    /**
      * Default constructor.  Called by the Jaxcent framework, do not call directly.
      * Usually this class is overridden.
      */
    public JaxcentPage()
    {
        // Retrieve members from Thread local storage.
        ThreadLocalData tld = (ThreadLocalData) tls.get();
        if ( tld != null ) {
            session = tld.tlsSession;
            ctx = tld.tlsCtx;
            map = tld.tlsMap;
            autoSession = tld.tlsAutoSession;
            path = tld.tlsPath;
            remoteAddr = tld.tlsRemoteAddr;
            remoteAcceptLanguages = tld.tlsRemoteAcceptLanguages;
        }
        tls.set( null );
        if ( pageExts != null ) {
            JaxcentPageExt[] exts = pageExts;
            if ( exts != null ) try {
                for ( int i = 0; i < exts.length; i++ )
                    exts[i].onPageConstructed( this );
            } catch (Exception ex) {}
        }
    }

    void addElement( JaxcentObject element, int searchType, String searchString, int searchIndex, String text, String[] attributes, String[] values, JaxcentObject otherElement )
    {
        addElement( element, searchType, searchString, searchIndex, text, attributes, values, otherElement, null, null, null );
    }

    void appendArray( String[] s1, String[]s2 )
    {
        if ( s1 == null ) {
            pageQuery.append( "0&" );
            return;
        }
        pageQuery.append( s1.length );
        pageQuery.append( "&" );
        for ( int i = 0; i < s1.length; i++ ) {
            pageQuery.append( encode( s1[i] ));
            pageQuery.append( "&" );
        }
        if ( s2 != null ) {
            for ( int i = 0; i < s1.length; i++ ) {
                pageQuery.append( encode( s2[i] ));
                pageQuery.append( "&" );
            }
        }
    }

    synchronized void addMultipleElements( JaxcentObject[] multipleElements, String ref ) throws Jaxception
    {
        int startIndex = elements.size();
        for ( int i = 0; i < multipleElements.length; i++ ) {
            multipleElements[i] = new HtmlElement( this, elements.size());
            elements.addElement( multipleElements[i] );
        }
        pageQuery.append( "70&" );
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( startIndex );
        pageQuery.append( "&" );
        pageQuery.append( multipleElements.length );
        pageQuery.append( "&" );
        pageQuery.append( ref );
        pageQuery.append( "&" );
        sendPageQuery();
    }

    synchronized void addElement( JaxcentObject element, int searchType, String searchString, int searchIndex, String text, String[] attributes, String[] values, JaxcentObject otherElement, String[] subElements, String[][] subAttributes, String[][] subValues )
    {
        element.elementIndex = elements.size();
        elements.addElement( element );
        pageQuery.append( "2&" );                         // 2 == New Form Element
        pageQuery.append( element.elementIndex );         // 0-based index of element
        pageQuery.append( "&" );
        String expected = element.getExpectedTag();
        String expectedType = "";
        pageQuery.append( expected );      // Are we expecting any particular tag?
        if ( expected.equalsIgnoreCase( "input" )) {
            expectedType = element.getExpectedType();
            if ( ! expectedType.equals( "0" )) {
                pageQuery.append( "," );
                pageQuery.append( expectedType );
            }
        }
        pageQuery.append( "&" );
        
        // Add search details.
        switch ( searchType ) {
            case SearchType.SEARCH_BY_ID:
                pageQuery.append( "1&" );
                pageQuery.append( searchString );
                pageQuery.append( "&" );
                break;
            case SearchType.SEARCH_TABLE_ROW:
            case SearchType.SEARCH_TABLE_CELL:
            case SearchType.SEARCH_ROW_CELL:
            case SearchType.SEARCH_SELECT_OPTION:
                pageQuery.append( searchType );
                pageQuery.append( "&" );
                pageQuery.append( otherElement.elementIndex );
                pageQuery.append( "&" );
                pageQuery.append( searchIndex );
                pageQuery.append( "&" );
                break;
            case SearchType.CREATE_NEW:
                int cnType = 0;
                if ( searchIndex == 0 && text == null && attributes == null && values == null && otherElement == null && subElements == null && subAttributes == null && subValues == null ) {
                    cnType = element.createNewType();
                    if ( cnType == 2 ) {
                        text = searchString;
                        searchString = element.getExpectedTag();
                    } else if ( cnType == 1 ) {
                        attributes = new String[]{ "value" };
                        values = new String[]{ searchString };
                        searchString = element.getExpectedTag();
                    }
                }
                pageQuery.append( "7&" );
                pageQuery.append( searchString );
                pageQuery.append( "&" );
                if ( text == null )
                    pageQuery.append( "0&" );
                else {
                    pageQuery.append( "1&" );
                    pageQuery.append( encode( text ));
                    pageQuery.append( "&" );
                }
                boolean needType = expected.equalsIgnoreCase( "input" ) && !expectedType.equals("0");
                boolean needOnClick = false;
                                  // expected.equalsIgnoreCase( "button" ) ||
                                  // ( expected.equalsIgnoreCase( "input" ) && expectedType.equalsIgnoreCase( "button" ));
                if ( attributes == null || attributes.length == 0 ) {
                    int len = 0;
                    if ( needType )
                        len++;
                    if ( needOnClick )
                        len++;
                    pageQuery.append( len );
                    pageQuery.append( "&" );
                    if ( needType ) {
                        pageQuery.append( "TYPE&" );
                        pageQuery.append( encode( expectedType ));
                        pageQuery.append( "&" );
                    }
                    if ( needOnClick ) {
                        pageQuery.append( "onclick&" );
                        pageQuery.append( encode( "return false;" ));
                        pageQuery.append( "&" );
                    }
                } else {
                    int len = attributes.length;
                    if ( needType || needOnClick ) {
                        for ( int i = 0; i < attributes.length; i++ ) {
                            if ( attributes[i].equalsIgnoreCase( "type" ))
                                needType = false;
                            if ( attributes[i].equalsIgnoreCase( "onclick" ))
                                needOnClick = false;
                        }
                        if ( needType )
                            len++;
                        if ( needOnClick )
                            len++;
                    }
                    pageQuery.append( len );
                    pageQuery.append( "&" );
                    for ( int i = 0; i < attributes.length; i++ ) {
                        pageQuery.append( encode( attributes[i] ));
                        pageQuery.append( "&" );
                        pageQuery.append( encode( values[i] ));
                        pageQuery.append( "&" );
                    }
                    if ( needType ) {
                        pageQuery.append( "TYPE&" );
                        pageQuery.append( encode( expectedType ));
                        pageQuery.append( "&" );
                    }
                    if ( needOnClick ) {
                        pageQuery.append( "onclick&" );
                        pageQuery.append( encode( "return false;" ));
                        pageQuery.append( "&" );
                    }
                }
                break;
            case SearchType.CREATE_ROW:
                pageQuery.append( searchType );
                pageQuery.append( "&" );
                pageQuery.append( otherElement.elementIndex );
                pageQuery.append( "&" );
                pageQuery.append( searchIndex );
                pageQuery.append( "&" );
                appendArray( subElements, null );
                if ( subElements.length > 0 ) {
                    for ( int i = 0; i < subElements.length; i++ ) {
                       if ( subAttributes == null || subAttributes[i] == null )
                           pageQuery.append( "0&" );
                       else
                           appendArray( subAttributes[i], subValues[i] );
                    }
                }
                break;
            case SearchType.CREATE_CELL:
                pageQuery.append( searchType );
                pageQuery.append( "&" );
                pageQuery.append( otherElement.elementIndex );
                pageQuery.append( "&" );
                pageQuery.append( searchIndex );
                pageQuery.append( "&" );
                pageQuery.append( encode( text ));
                pageQuery.append( "&" );
                if ( attributes == null || attributes.length == 0 )
                    pageQuery.append( "0&" );
                else
                    appendArray( attributes, values );
                break;
            case SearchType.CREATE_OPTION:
                pageQuery.append( searchType );
                pageQuery.append( "&" );
                pageQuery.append( otherElement.elementIndex );
                pageQuery.append( "&" );
                pageQuery.append( searchIndex );
                pageQuery.append( "&" );
                pageQuery.append( encode( text ));
                pageQuery.append( "&" );
                if ( attributes == null || attributes.length == 0 )
                    pageQuery.append( "0&" );
                else
                    appendArray( attributes, values );
                break;
            case SearchType.SEARCH_FIRST_TAG:
            case SearchType.SEARCH_NEXT_TAG:
            case SearchType.SEARCH_NEXT_SIBLING:
            case SearchType.SEARCH_PREV_SIBLING:
            case SearchType.SEARCH_PARENT_NODE:
            case SearchType.SEARCH_FIRST_CHILD:
            case SearchType.SEARCH_LAST_CHILD:
            case SearchType.SEARCH_FIRST_LI:
            case SearchType.SEARCH_NEXT_LI:
                pageQuery.append( searchType );
                pageQuery.append( "&" );
                if ( searchType <= 10 ) {
                    pageQuery.append( searchString );
                    pageQuery.append( "&" );
                }
                pageQuery.append( otherElement.elementIndex );
                pageQuery.append( "&" );
                break;
            default:
                pageQuery.append( searchType );
                pageQuery.append( "&" );
                if ( searchType <= 10 ) {
                    pageQuery.append( searchString );
                    pageQuery.append( "&" );
                }
                pageQuery.append( searchIndex );
                pageQuery.append( "&" );
        }
        // Add events.
        Vector events = new Vector();
        element.getEvents( events );
        if ( events.size() > 0 ) {
            int eindex = 0;
            boolean first = true;
            while ( eindex < events.size()) {
                String eventName = (String) events.elementAt( eindex++ );
                Method eventMethod = (Method) events.elementAt( eindex++ );
                String eventType = (String) events.elementAt( eindex++ );
                if ( first ) 
                   first = false;
                else
                   pageQuery.append( "," );
                pageQuery.append( eventName );
                pageQuery.append( "," );
                pageQuery.append( eventsIndex );
                pageQuery.append( "," );
                pageQuery.append( eventType );
                if ( eventObjects == null ) {
                    eventObjects = new Vector();
                    eventMethods = new Vector();
                    eventTypes = new Vector();
                }
                eventObjects.addElement( element );
                eventMethod.setAccessible( true );
                eventMethods.addElement( eventMethod );
                eventTypes.addElement( eventType );
                eventsIndex++;
            }
            pageQuery.append( "&" );
        } else
            pageQuery.append( "0&" );
        sendPageQuery();
    }

    synchronized void addSetProperty( JaxcentObject element, boolean isStyle, boolean isBool, String property, String value )
    {
        if ( isBool ) {
            if ( isStyle )
                pageQuery.append( "47&" );             // 46 == set bool style
            else
                pageQuery.append( "46&" );             // 46 == set bool property
        } else if ( isStyle )
            pageQuery.append( "9&" );              // 3 == Set Style Property
        else
            pageQuery.append( "3&" );              // 3 == Set Property
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( element.elementIndex );  // 0-based index of element
        pageQuery.append( "&" );
        pageQuery.append( encode( translatePropertyName( property )));
        pageQuery.append( "&" );
        pageQuery.append( encode( value ));
        pageQuery.append( "&" );
    }

    String getReqReturn( int req, String reqName ) throws Jaxception
    {
        Object reqObject = new Object();
        Integer reqInt = new Integer( req );
        reqSync.put( reqInt, reqObject );
        Object obj = null;
        String error = null;
        try {
            synchronized ( reqObject ) {
                sendPageQuery();
                reqObject.wait( QUERY_TIMEOUT );
                obj = reqSync.get( reqInt );
                error = (String) reqError.get( reqInt );
            }
        } catch (Exception ex) {
        } finally {
           reqSync.remove( reqInt );
           reqError.remove( reqInt );
        }
        if ( error != null )
            throw new Jaxception( error );
        if ( reqObject.equals( obj )) {
            if ( doBatchUpdates )
                throw new Jaxception( "\"" + reqName + "\" timed out.  Batch-updates is true; do not request page data while batch-updates is true!" );
            else
                throw new Jaxception( "\"" + reqName + "\" timed out" );
        }
        return (String) obj;
    }

    String translatePropertyName( String property )
    {
        if ( property.equalsIgnoreCase( "colspan" )) return "colSpan";
        if ( property.equalsIgnoreCase( "float" )) return "styleFloat";
        if ( property.indexOf( '-' ) < 0 )
            return property;
        StringBuffer result = new StringBuffer();
        boolean toUpper = false;
        for ( int i = 0; i < property.length(); i++ ) {
            char ch = property.charAt( i );
            if ( ch == '-' || ch == ':' ) {
                toUpper = true;
                continue;
            }
            if ( toUpper ) {
                if ( Character.isWhitespace( ch ))
                    continue;
                ch = Character.toUpperCase( ch );
                toUpper = false;
            }
            result.append( ch );
        }
        return result.toString();
    }

    synchronized String queryGetProperty( JaxcentObject element, boolean isStyle, String property ) throws Jaxception
    {
        if ( pageLoadObject == null )
            throw new PageLoadingException();
        if ( isStyle )
            pageQuery.append( "10&" );              // 4 == Get Style Property
        else
            pageQuery.append( "4&" );               // 4 == Get Property
        int req = reqIndex++;
        pageQuery.append( req );                   // Request index
        pageQuery.append( "&" );
        pageQuery.append( element.elementIndex );  // 0-based index of element
        pageQuery.append( "&" );
        pageQuery.append( encode( translatePropertyName( property )));
        pageQuery.append( "&" );
        return getReqReturn( req, "getProperty" );
    }


    synchronized void addSetAttribute( JaxcentObject element, String attribute, String value )
    {
        pageQuery.append( "32&" );                 // 3 == Set Attribute
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( element.elementIndex );  // 0-based index of element
        pageQuery.append( "&" );
        pageQuery.append( encode( attribute ));
        pageQuery.append( "&" );
        pageQuery.append( encode( value ));
        pageQuery.append( "&" );
    }

    synchronized String queryGetAttribute( JaxcentObject element, String attribute ) throws Jaxception
    {
        if ( pageLoadObject == null )
            throw new PageLoadingException();
        pageQuery.append( "33&" );                 // 4 == Get Attribute
        int req = reqIndex++;
        pageQuery.append( req );                   // Request index
        pageQuery.append( "&" );
        pageQuery.append( element.elementIndex );  // 0-based index of element
        pageQuery.append( "&" );
        pageQuery.append( encode( attribute ));
        pageQuery.append( "&" );
        return getReqReturn( req, "getAttribute" );
    }

    synchronized void addSetInnerText( JaxcentObject element, String value )
    {
        pageQuery.append( "42&" );                 // 42 == Set inner text
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( element.elementIndex );  // 0-based index of element
        pageQuery.append( "&" );
        pageQuery.append( encode( value ));
        pageQuery.append( "&" );
    }

    synchronized String queryGetInnerText( JaxcentObject element ) throws Jaxception
    {
        if ( pageLoadObject == null )
            throw new PageLoadingException();
        pageQuery.append( "41&" );                 // 41 == Get Inner text
        int req = reqIndex++;
        pageQuery.append( req );                   // Request index
        pageQuery.append( "&" );
        pageQuery.append( element.elementIndex );  // 0-based index of element
        pageQuery.append( "&" );
        return getReqReturn( req, "getInnerText" );
    }

    synchronized void addPageCall( JaxcentObject element, int index )
    {
        pageQuery.append( "31&" );                 // 31 == Jaxcent call
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( element.elementIndex );  // 0-based index of element
        pageQuery.append( "&" );
        pageQuery.append( index );               // Call index
        pageQuery.append( "&" );
    }

    synchronized void addPageCall( JaxcentObject element, int index, int arg )
    {
        pageQuery.append( "31&" );                 // 31 == Jaxcent call
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( element.elementIndex );  // 0-based index of element
        pageQuery.append( "&" );
        pageQuery.append( index );                 // Call index
        pageQuery.append( "&" );
        pageQuery.append( arg );                   // Call arg
        pageQuery.append( "&" );
    }

    synchronized void addPageCall( JaxcentObject element, int index, String arg )
    {
        pageQuery.append( "31&" );                 // 31 == Jaxcent call
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( element.elementIndex );  // 0-based index of element
        pageQuery.append( "&" );
        pageQuery.append( index );                 // Call index
        pageQuery.append( "&" );
        pageQuery.append( encode( arg ));          // Call arg
        pageQuery.append( "&" );
    }

    synchronized void addInsertElement( int insertType, JaxcentObject element, JaxcentObject target )
    {
        pageQuery.append( "34&" );                 // 34 == Insert element
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( element.elementIndex );  // 0-based index of element
        pageQuery.append( "&" );
        pageQuery.append( insertType );
        pageQuery.append( "&" );
        if ( insertType != 1 && insertType != 2 ) {
            pageQuery.append( target.elementIndex );  // 0-based index of target
            pageQuery.append( "&" );
        }
    }

    class EventInvokerThread extends Thread {
        Method method;
        JaxcentObject element;
        String eventType;
        String data;

        EventInvokerThread( Method method, JaxcentObject element, String eventType, String data )
        {
            this.method = method;
            this.element = element;
            this.eventType = eventType;
            this.data = data;
        }

        public void run()
        {
            boolean flush = false;
            try {
                if ( eventType.equals( "1" ))
                    method.invoke( element, null );
                else if ( eventType.equals( "2" ))
                    method.invoke( element, new Object[]{ new IgnorecaseMap( processFormData( data )) } );
                else if ( eventType.equals( "3" ))
                    method.invoke( element, new Object[]{ data } );
                else if ( eventType.equals( "4" ))
                    method.invoke( element, new Object[]{ Integer.valueOf( data ) } );
                else if ( eventType.equals( "5" )) {
                    flush = true;
                    flushDragDrop();
                    method.invoke( element, new Object[]{ elements.elementAt( Integer.parseInt( data )) } );
                } else if ( eventType.equals( "6" )) {
                    Object[] obj = new Object[4];
                    int prev = -1;
                    for ( int i = 0; i < 4; i++ ) {
                        int index = data.indexOf( ',', prev+1 );
                        if ( index < 0 ) index = data.length();
                        String s = data.substring( prev+1, index );
                        obj[i] = i < 2 ? (Object) Integer.valueOf( s ) : (Object) decode( s );
                        prev = index;
                    }
                    method.invoke( element, obj );
                }
            } catch (InvocationTargetException ex) {
                if ( ex.getCause() != null )
                    ex.getCause().printStackTrace();
                else
                    ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if ( flush && pageQuery.length() > 0 ) {
                    sendPageQuery();
                }
            }
        }
    }

    void onPageEvent( int index, String data )
    {
        if ( eventObjects == null  )
            return;
        Object method = eventMethods.elementAt( index );
        JaxcentObject element = (JaxcentObject) eventObjects.elementAt( index );
        String eventType = (String) eventTypes.elementAt( index );
        new EventInvokerThread( (Method) method, element, eventType, data ).start();
    }

    void onImageRequest( Object[] req )
    {
        String imageName = (String) req[0];
        req[0] = null;
        req[1] = "image/jpeg";
        // Locate the image.
        try {
            for ( int i = 0; i < 20; i++ ) {
                synchronized ( imageList ) {
                    req[0] = imageList.get( imageName + ".data" );
                    req[1] = imageList.get( imageName + ".type" );
                }
                if ( req[0] != null )
                    break;
                Thread.sleep( 50 );
            }
        } catch (Exception ex) {
        }
    }

    synchronized HashMap getImageList()
    {
        if ( imageList == null )
            imageList = new HashMap();
        return imageList;
    }

    String setImageData( HtmlImage img, String contentType, byte[] imageData )
    {
        String imageName = "image" + img.elementIndex;
        HashMap map = getImageList();
        if ( imageData != null )
            imageList.put( imageName + ".data", imageData );
        imageList.put( imageName + ".type", contentType );
        return imageName;
    }

    synchronized void setImageDataSrc( HtmlImage img, String extension, String name ) throws Jaxception
    {
        addPageCall( img, 29, "/imageJaxcentConnectionId_" + img.elementIndex + "_" + ( imageIndex++ ) + extension + "?conid=JaxcentConnectionId&image=" + name );
        sendPageQuery();
    }

    static void onPageData( Object pageSession, Object pageCtx, Hashtable pageMap, boolean pageAutoSession, String pagePath, String addr, String acceptLanguages )
    {
        ThreadLocalData tld = new ThreadLocalData();
        tld.tlsSession = pageSession;
        tld.tlsCtx = pageCtx;
        tld.tlsMap = pageMap;
        tld.tlsAutoSession = pageAutoSession;
        tld.tlsPath = pagePath;
        tld.tlsRemoteAddr = addr;
        tld.tlsRemoteAcceptLanguages = acceptLanguages;
        tls.set( tld );
    }

    void onPageResponse( String resp )
    {
        try {
            int index = resp.indexOf( '_' );
            if ( index > 0 ) {
                Integer req = Integer.valueOf( resp.substring( 0, index ));
                resp = resp.substring( index+1 );
                Object reqObject = reqSync.get( req );
                if ( reqObject != null && ! ( reqObject instanceof String )) synchronized ( reqObject ) {
                    reqSync.put( req, resp );
                    reqObject.notify();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void onPageError( String resp )
    {
        try {
            int index = resp.indexOf( '_' );
            if ( index > 0 ) {
                Integer req = Integer.valueOf( resp.substring( 0, index ));
                resp = resp.substring( index+1 );
                Object reqObject = reqSync.get( req );
                if ( reqObject != null && ! ( reqObject instanceof String )) synchronized ( reqObject ) {
                    reqError.put( req, resp );
                    reqObject.notify();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    synchronized String onPageLoad( Object[] loadObject )
    {
        loaded = true;
        String query = needFormData() ? "54&" : "";
        query += pageQuery.toString();
        pageQuery = new StringBuffer();
        this.pageLoadObject = loadObject;
        return query;
    }

    void onPageRequest( String req )
    {
        new PageRequestThread( req ).start();
    }

    private class PageRequestThread extends Thread {
        String req;

        PageRequestThread( String req )
        {
            this.req = req;
        }

        public void run()
        {
		    String[] tokens = split( req, ',' );
            int nArgs = 0;
            int tcount = tokens.length;
			int tok = 0;
            if ( tcount > 0 ) try {
                nArgs = Integer.parseInt( tokens[tok++] );
                if ( nArgs > tcount - 1 )
                    nArgs = tcount-1;
            } catch (Exception ex) {}
            try {
                if ( nArgs == 0 ) {
                    onJavaScriptRequest( null, null );
                } else if ( nArgs == 1 ) {
                    onJavaScriptRequest( decode( tokens[tok++] ), null );
                } else {
                    String cmd = decode( tokens[tok++] );
                    String[] args = new String[ nArgs-1 ];
                    for ( int i = 0; i < nArgs-1; i++ )
                        args[i] = decode( tokens[tok++] );
                    onJavaScriptRequest( cmd, args );
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    void onPageLoadDone()
    {
        new Thread() {
            public void run() {
                if ( pageExts != null ) {
                    JaxcentPageExt[] exts = pageExts;
                    if ( exts != null ) try {
                        for ( int i = 0; i < exts.length; i++ )
                            exts[i].onPageLoaded( JaxcentPage.this );
                    } catch (Exception ex) {}
                }
                onLoad();
            }
        }.start();
    }

    void onPageUnload()
    {
        pageLoadObject = null;
        new Thread() {
            public void run() {
                if ( finalData != null ) try {
                    onFinalFormData( new IgnorecaseMap( finalData ));
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                if ( pageExts != null ) {
                    JaxcentPageExt[] exts = pageExts;
                    if ( exts != null ) try {
                        for ( int i = 0; i < exts.length; i++ )
                            exts[i].onPageUnloaded( JaxcentPage.this );
                    } catch (Exception ex) {}
                }
                try {
                    onUnload();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                unloaded = true;
            }
        }.start();
    }

   /**
     * If "UseSession" or "AutoSessionData" have been configured as true in the configuration
     * file for this page, this method will return the HTTP session object from the application
     * server or Jaxcent connector.
     * <P>
     * If running with a standard Java servlet-container/application-server,
     * the object returned is of type javax.servlet.http.HttpSession, and
     * can be cast to that type.
     * <P>
     * If running with a direct Jaxcent connector for IIS/Apache, the object
     * returned is of type jaxcentConnector.JaxcentSession.
     */

   public Object getHttpSession() throws Jaxception
   {
       return session;
   }

   /**
     * If "UseSession" or "AutoSessionData" have been configured as true in the configuration
     * file for this page, this method can be used to invalidate the session.
     */

   public void invalidateHttpSession() throws Jaxception
   {
       if ( session == null )
           throw new Jaxception( "No session" );
       try {
           Method inv = session.getClass().getMethod( "invalidate", null );
           inv.invoke( session, null );
       } catch (Exception ex) {}
   }

   /**
     * This method will return the application server's or Jaxcent connector's
     * application context.
     * <P>
     * If running with a standard Java servlet-container/application-server,
     * the object returned is of type javax.servlet.ServletContext, and
     * can be cast to that type.
     * <P>
     * If running with a direct Jaxcent connector for IIS/Apache, the object
     * returned is of type jaxcentConnector.JaxcentAppContext.
     *
     * @see jaxcent.JaxcentPage#saveInAppContext
     * @see jaxcent.JaxcentPage#getFromAppContext
     * @see jaxcent.JaxcentPage#getAppContextKeys
     */

   public Object getAppContext() throws Jaxception
   {
       return ctx;
   }

    /**
      * Override to handle page loading.  Data can be retrieved from the page at this time.
      */
    protected void onLoad()
    {
    }

   /**
     * Override to receive form data during page unloading.  The data has the
     * same format as getAllFormData.  But at this time, no further
     * interactions with the page are possible.  The data can be saved in the session,
     * or otherwise processed.  Field verifications cannot be done here.  For field
     * verifications, add handler on buttons or other fields, and retrieve the data
     * either individually or by calling getAllFormData().
     */

    protected void onFinalFormData( Map formData )
    {
    }

    Hashtable processFormData( String data )
    {
        String[] tokens = split( data, '&' );
        Hashtable t = new Hashtable();
        for ( int i = 0; i < tokens.length; i++ ) {
            String str = tokens[i];
            int index = str.indexOf( '=' );
            if ( index >= 0 ) {
                String name = decode( str.substring( 0, index )).trim().toLowerCase();
                String value = decode( str.substring( index+1 ));
                if ( tableKeys != null && tableKeys.get( name.toLowerCase()) != null ) {
                    t.put( name, parseTableContents( value.substring(1)));
                    continue;
                }
                Object oldValue = t.get( name );
                if ( oldValue != null && ( oldValue instanceof String ) && ! "".equals( oldValue )) {
                    if ( value.equals( "" ))
                        continue;
                    value = (String) oldValue + "," + value;
                }
                t.put( name, value );
            }
        }
        return t;
    }

    /**
      * Retrieve all form data on page.  The data must be identifiable with a name or an ID.
      * In addition to INPUT tags, also retrieves data from SELECT and TEXTAREA tags.
      * <P>
      * The returned Map maps names or ids, to values.
      * <P>
      * Multile checkboxes can have the same name and multiple values.  These are separated
      * by commas.  If the checkbox (or none of the checkboxes) is not selected,
      * there will still be a value, and this value will be an empty string.
      * <P>
      * For lists (SELECT), the value contains of a selected index, followed
      * by a colon character and the text selected. (Convenient utility
      * functions JaxcentObject.getSelectedIndex and JaxcentObject.getSelectedValue
      * are available to parse it.)
      */

    public synchronized Map getAllFormData() throws Jaxception
    {
        if ( pageLoadObject == null )
            throw new PageLoadingException();
        pageQuery.append( "57&" );                 // 57 = get all form data
        int req = reqIndex++;
        pageQuery.append( req );                   // Request index
        pageQuery.append( "&" );
        return new IgnorecaseMap( processFormData( getReqReturn( req, "getAllFormData" )));
    }

    void onFormData( String data )
    {
        finalData = processFormData( data );
        if ( session == null || ( ! autoSession ) || ( ! sessionUpdatingEnabled ))
           return;
        // Put the data in session.

        try {
            if ( map == null )
                return;
            Enumeration en = finalData.keys();
            while ( en.hasMoreElements()) {
                String name = (String) en.nextElement();
                map.put( name, finalData.get( name ));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    boolean needFormData()
    {
        if ( autoSession )
            return true;
        try {
            Method mth = getClass().getDeclaredMethod( "onFinalFormData", new Class[]{ Map.class } );
            if ( ! mth.getDeclaringClass().equals( JaxcentPage.class ))
                return true;
        } catch (Throwable t) {}
        return false;
    }

    /**
      * Override to handle page unloading.
      */
    protected void onUnload()
    {
    }

    /**
      * Get all data from session so far.  If the "getFromCurrentPage" parameter
      * is true, first retrieve current page's data into session, otherwise
      * just returns the data collected so far.
      *
      * This method is only available if auto-session-data is set to true.
      * If getFromCurrentPage is true, this method cannot be called from
      * the constructor (before page loading.)
      */

    public Map getAllSessionData( boolean getFromCurrentPage ) throws Jaxception
    {
        if ( ! autoSession )
           throw new Jaxception( "This method is only available if auto-session-data is configured" );

        if ( getFromCurrentPage && pageLoadObject == null )
            throw new PageLoadingException();

        if ( session == null ) {
           return null;
        }

        try {
            if ( map == null )
                return null;
            if ( getFromCurrentPage ) {
                Map pageData = getAllFormData();
                Iterator it = pageData.keySet().iterator();
                while ( it.hasNext()) {
                    String name = (String) it.next();
                    map.put( name, pageData.get( name ));
                }
            }
            return new IgnorecaseMap( map );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
      * Navigate to a given URL.
      */
    public void navigate( String url ) throws Jaxception
    {
        miscSet( 1, url );
    }

    /**
      * Navigate back.
      */
    public void goBack() throws Jaxception
    {
        miscSet( 3, "" );
    }

    /**
      * Navigate forward.
      */
    public void goForward() throws Jaxception
    {
        miscSet( 4, "" );
    }

    void checkJSArgs( String code, Object[] args ) throws Jaxception
    {
        if ( code == null || code.trim().equals( "" ))
            throw new NullPointerException();
        if ( args != null ) {
            // Check code for being a valid name.
            for ( int i = 0; i < code.length(); i++ ) {
                char ch = code.charAt( i );
                if ( Character.isLetter( ch ))
                    continue;
                if ( Character.isDigit( ch )) {
                    if ( i == 0 )
                        throw new Jaxception( "Function name \"" + code + "\" starts with a digit" );
                    continue;
                }
                if ( ch == '(' || ch == ')' ) {
                    throw new Jaxception( "If arguments are provided, function name must not contain parentheses" );
                }
                if ( ch == ' ' ) {
                    throw new Jaxception( "Function names must not contain spaces." );
                }
                if ( ch != '_' )
                    throw new Jaxception( "Function name \"" + code + "\" containes illegal characters" );
            }
            for ( int i = 0; i < args.length; i++ ) {
                if ( args[i] instanceof JaxcentObject )
                    continue;
                if ( args[i] instanceof Integer )
                    continue;
                if ( args[i] instanceof String )
                    continue;
                if ( args[i] instanceof Boolean )
                    continue;
                if ( args[i] instanceof Float )
                    continue;
                if ( args[i] instanceof Double )
                    continue;
                if ( args[i] instanceof Long )
                    continue;
                if ( args[i] instanceof Short )
                    continue;
                if ( args[i] instanceof Character )
                    continue;
                if ( args[i] instanceof Byte )
                    continue;
                throw new Jaxception( "Argument of invalid type \"" + args[i].getClass() + "\"" );
            }
        }
    }

    void appendList( char sep, Object[] args )
    {
        pageQuery.append( args.length );
        pageQuery.append( sep );
        for ( int i = 0; i < args.length; i++ ) {
            if ( args[i] instanceof JaxcentObject ) {
                pageQuery.append( "1" );
                pageQuery.append( sep );
                pageQuery.append( ((JaxcentObject) args[i]).elementIndex );
                pageQuery.append( sep );
                continue;
            } 
            if (( args[i] instanceof Integer ) ||
                ( args[i] instanceof Short ) ||
                ( args[i] instanceof Long ) ||
                ( args[i] instanceof Byte )) {
                pageQuery.append( "2" );  // append as is, or use parseInt
                pageQuery.append( sep );
                pageQuery.append( args[i].toString() );
                pageQuery.append( sep );
                continue;
            }
            if ( args[i] instanceof String ) {
                pageQuery.append( "3" );
                pageQuery.append( sep );
                pageQuery.append( encode((String) args[i]));
                pageQuery.append( sep );
                continue;
            }
            if ( args[i] instanceof Boolean ) {
                pageQuery.append( "4" );
                pageQuery.append( sep );
                pageQuery.append( ((Boolean) args[i]).booleanValue() ? "true" : "false" );
                pageQuery.append( sep );
                continue;
            }
            if (( args[i] instanceof Float ) ||
                ( args[i] instanceof Double )) {
                pageQuery.append( "5" );  // append as is, or use parseFloat
                pageQuery.append( sep );
                pageQuery.append( args[i].toString() );
                pageQuery.append( sep );
                continue;
            }
            if ( args[i] instanceof Character ) {
                pageQuery.append( "3" );
                pageQuery.append( sep );
                pageQuery.append( encode( args[i].toString()));
                pageQuery.append( sep );
                continue;
            }
            pageQuery.append( "3" );  // Treat as string
            pageQuery.append( sep );
            pageQuery.append( encode( args[i].toString()));
            pageQuery.append( sep );
        }
    }

    void appendJSArgs( String code, Object[] args, boolean argsAsArray )
    {
        pageQuery.append( encode( code ));
        pageQuery.append( "&" );
        if ( args == null ) {
            pageQuery.append( "4&" );
            return;
        }
        if ( argsAsArray ) {
            pageQuery.append( "2&" );
            appendList( ',', args );
        } else {
            pageQuery.append( "3&" );
            appendList( '&', args );
        }
    }

    synchronized void addEvenVerifier( JaxcentObject el, String event, String verifier, Object[] args ) throws Jaxception
    {
        checkJSArgs( verifier, args );

        // Look for index of event
        int eventIndex = -1;
        event = event.trim().toLowerCase();
        if ( event.startsWith( "on" ))
            event = event.substring( 2 );
        if ( eventObjects != null ) {
            for ( int i = 0; eventIndex == -1 && i < eventObjects.size(); i++ ) {
                if ( eventObjects.elementAt( i ) == el ) {
                    Method mth = (Method) eventMethods.elementAt( i );
                    String name = mth.getName().toLowerCase();
                    if ( name.startsWith( "on" )) // Should
                        name = name.substring( 2 );
                    if ( name.equalsIgnoreCase( event ))
                        eventIndex = i;
                }
            }
        }
        if ( eventIndex == -1 )
            throw new Jaxception( "No handler found for event \"" + event + "\" on this object" );

        pageQuery.append( "60&" );                 // Add event handler
        int req = reqIndex++;
        pageQuery.append( req );                   // Request index
        pageQuery.append( "&" );
        pageQuery.append( eventIndex );            // Event index
        pageQuery.append( "&" );
        appendJSArgs( verifier, args, false );
        sendPageQuery();
    }

    synchronized String execJSFunc( boolean getResult, String code, Object[] args, boolean argsAsArray ) throws Jaxception
    {
        checkJSArgs( code, args );
        if ( getResult && pageLoadObject == null )
            throw new PageLoadingException();
        pageQuery.append( "59&" );                 // 59 == Exec JS
        int req = reqIndex++;
        pageQuery.append( req );                   // Request index
        pageQuery.append( "&" );
        appendJSArgs( code, args, argsAsArray );

        if ( getResult )
            return getReqReturn( req, "evalJavaScriptCode" );
        else
            sendPageQuery();
        return null;
    }

    /**
      * Call JavaScript, do not wait for results.
      * <P>The arguments array can contain Strings, Integers, Doubles, Strings, Boolean,
      * Integers, Jaxcent HTML Elements, or it can be null. If the argument array is not null,
      * the "code" is just a JavaScript function name, and must not contain the parentheses character.  In this case,
      * the characters "( )" containing any arguments are appended to the "code".  The array specifies
      * the list of parameters. If the argument array is null, the "code" is evaluated as is, as an
      * expression.  To call a function with no args, use the form <TT>execJavaScriptCode( "myFunction()", null, false );</TT>
      * <P>If the "argsAsArray" argument is true, the JavaScript function
      * will be called with a single argument, which will be an array.  Otherwise, the
      * "args" array will be used as a list of arguments.
      */

    public void execJavaScriptCode( String code, boolean argsAsArray, Object[] args ) throws Jaxception
    {
        execJSFunc( false, code, args, argsAsArray );
    }

    /**
      * Execute JavaScript code, do not wait for results.<P>
      * Same as execJavaScriptCode( code, false, null );
      */

    public void execJavaScriptCode( String code ) throws Jaxception
    {
        execJavaScriptCode( code, false, null );
    }

    /**
      * Same as execJavaScriptCode, but waits for the result of the evaluation and returns it.
      */
    public String evalJavaScriptCode( String code, boolean argsAsArray, Object[] args ) throws Jaxception
    {
        return execJSFunc( true, code, args, argsAsArray );
    }

    /**
      * Execute JavaScript code, wait for results.<P>
      * Same as evalJavaScriptCode( code, false, null );
      */

    public String evalJavaScriptCode( String code ) throws Jaxception
    {
        return evalJavaScriptCode( code, false, null );
    }

   /**
     * Override to process data from JavaScript methods sent by
     * calling the "JaxcentServerRequest( args... );" function
     * defined by the Jaxcent JavaScript file.<P>The JavaScript call
     * is asynchronous and does not wait for server response -- if
     * it is required to process a server response in JavaScript,
     * define a function in JavaScript and call that function
     * via execJavaScriptCode or evalJavaScriptCode,
     * from your override of this method.<P> If JaxcentServerRequest
     * was called at the client with no arguments, this method is called with
     * null args.  If JaxcentServerRequest was called with 1 arg,
     * this method is passed that arg as "cmd", the "args" is null.
     * Otherwise, "cmd" is the first arg passed to JaxcentServerRequest,
     * and "args" contains the rest of the args.
     */

    protected void onJavaScriptRequest( String cmd, String[] args )
    {
    }

    /**
      * Set status text.
      */
    public void setStatusText( String text ) throws Jaxception
    {
        miscSet( 2, text );
    }

    /**
      * Get status text.
      */
    public String getStatusText() throws Jaxception
    {
        return miscGet( 2, "getStatusText" );
    }

    java.awt.Dimension getDimensionFromString( String str )
    {
        if ( str != null ) {
            int index = str.indexOf( "," );
            if ( index > 0 ) try {
                return new java.awt.Dimension( Integer.parseInt( str.substring( 0, index )), Integer.parseInt( str.substring( index+1 )));
            } catch (Exception ex) {
            }
        }
        return new java.awt.Dimension( 0, 0 );
    }

    /**
      * Get screen dimensions
      */
    public java.awt.Dimension getScreenSize() throws Jaxception
    {
        return getDimensionFromString( miscGet( 5, "getScreenSize" ));
    }

    /**
      * Get window dimensions
      */
    public java.awt.Dimension getWindowSize() throws Jaxception
    {
        return getDimensionFromString( miscGet( 6, "getWindowSize" ));
    }

    /**
      * Get the path of the URL that is being processed.
      */

    public String getCurrentPath()
    {
        return path;
    }

    /**
      * Get the client IP address.
      */

    public String getRemoteAddr()
    {
        return remoteAddr;
    }

    private java.util.Locale[] getLocalesInternal()
    {
        if ( remoteAcceptLanguages == null )
            return null;
        remoteAcceptLanguages = remoteAcceptLanguages.trim();
        if ( remoteAcceptLanguages.equals( "" ))
            return null;

        String[] tokens = split( remoteAcceptLanguages, ',' );
        Locale[] ret = new Locale[ tokens.length ];
        float[] quality = new float[ ret.length ];
        int index = 0;
        for ( int tok = 0; tok < tokens.length; tok++ ) {
            String str = tokens[tok].trim();
            int qualIndex = str.indexOf( ';' );
            float q = 1.0f;
            if ( qualIndex > 0 ) {
                int eqIndex = str.indexOf( '=', qualIndex );
                if ( eqIndex > 0 ) {
                    try {
                        q = Float.parseFloat( str.substring( eqIndex+1 ).trim());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                str = str.substring( 0, qualIndex ).trim();
            }
            int insert = index;
            while ( insert > 0 && quality[insert-1] < q ) {
                insert--;
            }
            for ( int j = index; j > insert; j-- ) {
               ret[j] = ret[j-1];
            }
            ret[ insert ] = new Locale( str );
            quality[ insert ] = q;
            index++;
        }
        return ret;
    }

    /**
      * Get the remote locale if specified in HTTP Accept-Language header.
      * (If not specified, returns server's default locale.)
      */
    public java.util.Locale getLocale()
    {
        Locale[] locales = getLocalesInternal();
        if ( locales != null && locales.length > 0 )
            return locales[0];
        return Locale.getDefault();
    }


    /**
      * Get a list of remote locales if specified in HTTP Accept-Language header.
      * The most preferred locale is first, followed
      * by other locales in order of preference.
      *
      * (If none specified, returns server's default locale.)
      */

    public java.util.Locale[] getLocales()
    {
        Locale[] locales = getLocalesInternal();
        if ( locales != null && locales.length > 0 )
            return locales;
        return new Locale[]{ Locale.getDefault() };
    }


    /**
      * Show JavaScript alert dialog box to the user.
      */

    public synchronized void showMessageDialog( String message )
    {
        if ( message == null )
            message = "";
        pageQuery.append( "43&" );                 // 43 = alert
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( encode( message ));
        pageQuery.append( "&" );
        sendPageQuery();
    }

    /**
      * Show JavaScript confirm dialog box to the user.
      * Returns true for confirmation, false for cancellation.
      */

    public synchronized boolean showConfirmDialog( String message ) throws Jaxception
    {
        if ( pageLoadObject == null )
            throw new PageLoadingException();
        if ( message == null )
            message = "";
        pageQuery.append( "44&" );                 // 44 = confirm
        int req = reqIndex++;
        pageQuery.append( req );                   // Request index
        pageQuery.append( "&" );
        pageQuery.append( encode( message ));      // confirm message
        pageQuery.append( "&" );
        String ret = getReqReturn( req, "showConfirmDialog" );
        return ret.startsWith( "1" );
    }

    /**
      * Show JavaScript prompt dialog box to the user.
      * If the defaultResult is not null, the input dialog is initialized
      * with this text. If the user cancels, the returned value is null,
      * otherwise it is the input from user.
      */

    public synchronized String showInputDialog( String prompt, String defaultResult ) throws Jaxception
    {
        if ( pageLoadObject == null )
            throw new PageLoadingException();
        if ( prompt == null )
            prompt = "";
        if ( defaultResult == null )
            defaultResult = "";
        pageQuery.append( "45&" );                 // 44 = prompt
        int req = reqIndex++;
        pageQuery.append( req );                   // Request index
        pageQuery.append( "&" );
        pageQuery.append( encode( prompt ));
        pageQuery.append( "&" );
        pageQuery.append( encode( defaultResult ));
        pageQuery.append( "&" );
        String ret = getReqReturn( req, "showInputDialog" );
        if ( ret.startsWith( "1" ))
            return ret.substring( 1 );
        return null;
    }

    /**
      * Get cookies from page as name->value pairs.
      */
    public synchronized Map getCookies() throws Jaxception
    {
        if ( pageLoadObject == null )
            throw new PageLoadingException();
        pageQuery.append( "37&" );                 // 34 == Get Cookies
        int req = reqIndex++;
        pageQuery.append( req );                   // Request index
        pageQuery.append( "&" );
        String cookies = getReqReturn( req, "getCookies" );
        String[] tokens = split( cookies, ';' );
        HashMap map = new HashMap();
        for ( int tok = 0; tok < tokens.length; tok++ ) {
            String str = tokens[ tok ];
            int index = str.indexOf( '=' );
            if ( index >= 0 ) {
                map.put( str.substring( 0, index ).trim(), str.substring( index+1 ).trim());
            }
        }
        return map;
    }

    /**
      * Get cookie value.
      */
    public synchronized String getCookie( String cookieName ) throws Jaxception
    {
        if ( pageLoadObject == null )
            throw new PageLoadingException();
        pageQuery.append( "38&" );                 // 34 == Get Cookies
        int req = reqIndex++;
        pageQuery.append( req );                   // Request index
        pageQuery.append( "&" );
        pageQuery.append( encode( cookieName ));   // Cookie name
        pageQuery.append( "&" );
        String ret = getReqReturn( req, "getCookie" );
        if ( ret.startsWith( "1" ))
            return ret.substring( 1 ).trim();
        return null;
    }

    /**
      * Add cookie to page.
      * cookieString must be a correctly formatted cookie string.
      */
    public synchronized void setCookie( String cookieString )
    {
        pageQuery.append( "40&" );                 // 40 == set cookie
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( encode( cookieString ));
        pageQuery.append( "&" );
        sendPageQuery();
    }


    /**
      * Add cookie to page.
      * Expires, domain, path may be null.
      */
    public synchronized void setCookie( String name, String value, java.util.Date expires, String domain, String path, boolean isSecure ) throws Jaxception
    {
        if ( name.indexOf( '=' ) >= 0  ||
             name.indexOf( ';' ) >= 0  ||
             value.indexOf( '=' ) >= 0 ||
             value.indexOf( ';' ) >= 0   )
            throw new Jaxception( "The = and ; characters are not allowed in cookie name or value" );
        // Build cookie String
        StringBuffer cookieString = new StringBuffer();
        cookieString.append( name.trim());
        cookieString.append( '=' );
        cookieString.append( value.trim());
        if ( expires != null ) {
            cookieString.append( ";EXPIRES=" );
            GregorianCalendar cal = new GregorianCalendar();
            long t = expires.getTime();
            cal.setTimeInMillis( t - TimeZone.getDefault().getOffset( t ));
            int dayOfWeekIndex = ( cal.get( Calendar.DAY_OF_WEEK) - 1 ) * 3;
            int monthIndex = cal.get( Calendar.MONTH ) * 3;
            cookieString.append( "SunMonTueWedThuFriSat".substring( dayOfWeekIndex, dayOfWeekIndex + 3 ));
            cookieString.append( ", " );
            cookieString.append( cal.get( Calendar.DAY_OF_MONTH ));
            cookieString.append( " " );
            cookieString.append( "JanFebMarAprMayJunJulAugSepOctNovDec".substring( monthIndex, monthIndex + 3));
            cookieString.append( " " );
            cookieString.append( cal.get( Calendar.YEAR ));
            cookieString.append( " " );
            if ( cal.get( Calendar.HOUR_OF_DAY ) < 10 )
                cookieString.append( '0' );
            cookieString.append( cal.get( Calendar.HOUR_OF_DAY ));
            cookieString.append( ':' );
            if ( cal.get( Calendar.MINUTE ) < 10 )
                cookieString.append( '0' );
            cookieString.append( cal.get( Calendar.MINUTE ));
            cookieString.append( ':' );
            if ( cal.get( Calendar.SECOND ) < 10 )
                cookieString.append( '0' );
            cookieString.append( cal.get( Calendar.SECOND ));
            cookieString.append( " GMT" );
        }

        if ( domain != null && ! domain.equals("")) {
            cookieString.append( ";DOMAIN=" );
            cookieString.append( domain );
        }

        if ( path != null && ! path.equals("")) {
            cookieString.append( ";PATH=" );
            cookieString.append( path );
        }

        if ( isSecure ) {
            cookieString.append( ";SECURE" );
        }

        pageQuery.append( "40&" );                 // 40 == set cookie
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( encode( cookieString.toString()));
        pageQuery.append( "&" );
        sendPageQuery();
    }


    /**
      * Delete cookie.
      */
    public synchronized void deleteCookie( String cookieName )
    {
        pageQuery.append( "39&" );                 // 39 = delete cookie
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( encode( cookieName ));
        pageQuery.append( "&" );
        sendPageQuery();
    }
    

   synchronized void deleteRow( JaxcentObject table, int index )
    {
        pageQuery.append( "49&" );                 // 49 = delete row
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( table.elementIndex );    // 0-based index of element
        pageQuery.append( "&" );
        pageQuery.append( index );                 // index to delete
        pageQuery.append( "&" );
        sendPageQuery();
    }
    
    synchronized void deleteCell( JaxcentObject row, int index )
    {
        pageQuery.append( "50&" );                 // 50 = delete cell
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( row.elementIndex );      // 0-based index of element
        pageQuery.append( "&" );
        pageQuery.append( index );                 // index to delete
        pageQuery.append( "&" );
        sendPageQuery();
    }
    
    synchronized void deleteOption( JaxcentObject sel, int index )
    {
        pageQuery.append( "51&" );                 // 51 = delete option
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( sel.elementIndex );      // 0-based index of element
        pageQuery.append( "&" );
        pageQuery.append( index );                 // index to delete
        pageQuery.append( "&" );
        sendPageQuery();
    }
    

    synchronized void miscSet( int fn, String arg )
    {
        if ( arg == null )
            arg = "";
        pageQuery.append( "52&" );                 // 52 = Misc Set
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( encode( arg ));
        pageQuery.append( "&" );
        pageQuery.append( fn );
        pageQuery.append( "&" );
        sendPageQuery();
    }


    synchronized String miscGet( int fn, String fnName ) throws Jaxception
    {
        if ( pageLoadObject == null )
            throw new PageLoadingException();
        pageQuery.append( "53&" );                 // 34 == Misc Get
        int req = reqIndex++;
        pageQuery.append( req );                   // Request index
        pageQuery.append( "&" );
        pageQuery.append( fn );
        pageQuery.append( "&" );
        String str = getReqReturn( req, fnName );
        if ( str != null && str.startsWith( "1" ))
            return str.substring( 1 );
        return null;
    }

    /**
      * Delete an element referenced by an ID.
      */
    public synchronized void deleteElementById( String id )
    {
        if ( id == null || id.trim().equals( "" ))
            throw new NullPointerException();
        pageQuery.append( "61&" );                 // 61 = Delete by ID
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( encode( id ));
        pageQuery.append( "&" );
        sendPageQuery();
    }
    /**
      * Check if an element exists, without attempting to assign it to an object.
      */
    public boolean checkElementExists( SearchType searchType, String searchString, int searchIndex ) throws Jaxception
    {
        return checkElementExists( searchType.searchType, searchString, searchIndex );
    }

    synchronized boolean checkElementExists( int searchType, String searchString, int searchIndex ) throws Jaxception
    {
        switch ( searchType ) {
            case SearchType.SEARCH_TABLE_ROW:
            case SearchType.SEARCH_TABLE_CELL:
            case SearchType.SEARCH_ROW_CELL:
            case SearchType.SEARCH_SELECT_OPTION:
                throw new Jaxception( "This search type is not supported for checkElementExists" );
        }
        if ( searchString == null )
            searchString = "";
        pageQuery.append( "58&" );
        int req = reqIndex++;
        pageQuery.append( req );                   // Request index
        pageQuery.append( "&" );
        switch ( searchType ) {
            case SearchType.SEARCH_BY_ID:
                pageQuery.append( "1&" );
                pageQuery.append( searchString );
                pageQuery.append( "&" );
                break;
            default:
                pageQuery.append( searchType );
                pageQuery.append( "&" );
                if ( searchType <= 10 ) {
                    pageQuery.append( searchString );
                    pageQuery.append( "&" );
                }
                pageQuery.append( searchIndex );
                pageQuery.append( "&" );
        }
        String str = getReqReturn( req, "checkElementExists" );
        if ( str != null && str.startsWith( "1" ))
            return true;
        return false;
    }

    boolean matchMultiple( String item, String value )
    {
        if ( value == null )
            return false;
        item = item.trim();
        if ( value.indexOf( ',' ) >= 0 ) {
            String[] tokens = split( value, ',' );
            for ( int tok = 0; tok < tokens.length; tok++ ) {
                if ( tokens[ tok ].trim().equalsIgnoreCase( item ))
                    return true;
            }
            return false;
        }
        return value.trim().equalsIgnoreCase( item );
    }

    synchronized void autoFormData( String data, Hashtable table )
    {
        try {
            if ( table == null )
                return;
            String[] tokens = split( data, '&' );
            int formIndex = 0;
            boolean first = true;
            for ( int tok = 0; tok < tokens.length; tok++ ) {
                formIndex++;
                String str = tokens[ tok ];
                int index = str.indexOf( '=' );
                if ( index <= 0 )
                    continue;
                if ( first ) {
                    first = false;
                    pageQuery.append( "56&" );
                }
                String name = decode( str.substring( 0, index )).trim().toLowerCase();
                Object val = table.get( name );
                if ( val != null && ! ( val instanceof String ))
                    continue;
                String value = (String) val;
                //if ( value == null )
                //    continue;
                String type = decode( str.substring( index+1 ));
                if ( type.startsWith( "1" ) || type.startsWith( "2" )) {
                    if ( matchMultiple( type.substring(1), value )) {
                        pageQuery.append( formIndex );
                        pageQuery.append( "&1&" );
                    } else {
                        pageQuery.append( formIndex );
                        pageQuery.append( "&4&" );
                    }
                } else if ( type.startsWith( "3" )) {
                    if ( value == null )
                        value = "-1: ";
                    index = value.indexOf( ':' );
                    if ( index > 0 ) {
                        pageQuery.append( formIndex );
                        pageQuery.append( "&3&" );
                        pageQuery.append( value.substring( 0, index ));
                        pageQuery.append( "&" );
                    }
                } else {
                    if ( value == null )
                        value = "";
                    pageQuery.append( formIndex );
                    pageQuery.append( "&2&" );
                    pageQuery.append( encode( value ));
                    pageQuery.append( "&" );
                }
            }
            if ( ! first ) {
                pageQuery.append( "0&" );
                sendPageQuery();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    String parseAttributes( String attrs, boolean filter ) throws Jaxception
    {
        if ( attrs == null )
            return defaultCellEditorAttrs;
        StringBuffer result = new StringBuffer();
        String[] tokens = split( attrs, ';' );
        for ( int tok = 0; tok < tokens.length; tok++ ) {
            String attr = tokens[ tok ];
            int c = attr.indexOf( ':' );
            if ( c <= 0 )
                throw new Jaxception( "Ill-formed attributes: \"" + attrs + "\"" );
            String name = attr.substring( 0, c ).trim();
            String value = attr.substring( c+1 ).trim();
            if ( filter && 
			    ( name.equalsIgnoreCase( "position" ) ||
                  name.equalsIgnoreCase( "left" ) ||
                  name.equalsIgnoreCase( "top" ) ||
                  name.equalsIgnoreCase( "width" ) ||
                  name.equalsIgnoreCase( "height" ) ||
                  name.equalsIgnoreCase( "display" ) ||
                  name.equalsIgnoreCase( "visibility" )))
                continue;
            name = translatePropertyName( name );
            result.append( encode( name ));
            result.append( ',' );
            result.append( encode( value ));
            result.append( ',' );
        }
        if ( result.length() == 0 )
            return defaultCellEditorAttrs;
        return result.toString();
    }

    synchronized void setCellEditable( HtmlTable table, boolean enable, int firstRow, int firstCol, int lastRow, int lastCol, boolean doubleClick, boolean allowHtmlInput, String editorAttributes ) throws Jaxception
    {
        String attrs = "";
        if ( enable )
            attrs = parseAttributes( editorAttributes, true );
        pageQuery.append( "31&" );                 // 31 == Jaxcent call
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( table.elementIndex );    // 0-based index of table
        pageQuery.append( "&" );
        pageQuery.append( enable ? "25&" : "26&" ); // Call index
        pageQuery.append( firstRow );
        pageQuery.append( "&" );
        pageQuery.append( firstCol );
        pageQuery.append( "&" );
        pageQuery.append( lastRow );
        pageQuery.append( "&" );
        pageQuery.append( lastCol );
        pageQuery.append( "&" );
        pageQuery.append( doubleClick ? "2&" : "1&" );
        pageQuery.append( allowHtmlInput ? "1&" : "0&" );
        pageQuery.append( attrs );
        pageQuery.append( "&" );
        sendPageQuery();
    }

    synchronized void addDeleteButtons( HtmlTable table, int firstRow, int lastRow, String buttonText, String buttonStyleAttributes ) throws Jaxception
    {
        StringBuffer html = new StringBuffer( "<BUTTON" );
        if ( buttonStyleAttributes != null && ! buttonStyleAttributes.equals( "" )) {
            html.append( " STYLE=\"" );
            for ( int i = 0; i < buttonStyleAttributes.length(); i++ ) {
                char ch = buttonStyleAttributes.charAt( i );
                if ( ch == '"' || ch == '\'' || ch == '\\' )
                    html.append( '\\' );
                html.append( ch );
            }
            html.append( '\"' );
        }
        html.append( ">" );
        if ( buttonText == null || buttonText.equals( "" ))
            buttonText = "Delete";
        html.append( buttonText );
        html.append( "</BUTTON>" );
        if ( buttonStyleAttributes == null )
            buttonStyleAttributes = "";
        pageQuery.append( "31&" );                 // 31 == Jaxcent call
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( table.elementIndex );    // 0-based index of table
        pageQuery.append( "&27&" );                // Call index
        pageQuery.append( firstRow );
        pageQuery.append( "&" );
        pageQuery.append( lastRow );
        pageQuery.append( "&" );
        pageQuery.append( encode( html.toString()));
        pageQuery.append( "&" );
        sendPageQuery();
    }

    /**
      * Reset form data from session.  Session must be available.
      * This can be used to implement a "Reset" button.
      */
    public synchronized void resetFromSession() throws Jaxception
    {
        if ( pageLoadObject == null )
            throw new PageLoadingException();
        if ( session == null )
            throw new Jaxception( "Session is not available" );
        pageQuery.append( "55&" );                 // 55 = Populate page data
        sendPageQuery();
    }

    synchronized void setCellContents( HtmlTable table, int firstRow, int firstCol, String[][] contents ) throws Jaxception
    {
        if ( contents == null || table == null )
            throw new NullPointerException();
        int nRows = contents.length;
        if ( nRows == 0 )
            return;
        pageQuery.append( "66&" );                 // 66 = set table contents
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( table.elementIndex );    // 0-based index of table
        pageQuery.append( "&" );
        pageQuery.append( firstRow );
        pageQuery.append( "&" );
        pageQuery.append( firstCol );
        pageQuery.append( "&" );
        pageQuery.append( nRows );
        pageQuery.append( "&" );
        for ( int i = 0; i < nRows; i++ ) {
            int nCols = contents[i] == null ? 0 : contents[i].length;
            pageQuery.append( nCols );
            pageQuery.append( "&" );
            for ( int j = 0; j < nCols; j++ ) {
                pageQuery.append( encode( contents[i][j] ));
                pageQuery.append( "&" );
            }
        }
        sendPageQuery();
    }

    int distance( int first, int last )
	{
	    if ( first < 0 || last < 0 )
		    return last;
        return last - first + 1;
	}

    synchronized void setCellAttributes( HtmlTable table, int firstRow, int firstCol, int lastRow, int lastCol, String cellAttributes ) throws Jaxception
    {
        if ( cellAttributes == null || table == null )
            throw new NullPointerException();
        String attrs = parseAttributes( cellAttributes, false );
        pageQuery.append( "67&" );                 // 67 = set table attributes
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( table.elementIndex );    // 0-based index of table
        pageQuery.append( "&" );
		pageQuery.append( attrs );
        pageQuery.append( "&" );
        pageQuery.append( firstRow );
        pageQuery.append( "&" );
        pageQuery.append( firstCol );
        pageQuery.append( "&" );
        pageQuery.append( distance( firstRow, lastRow ));
        pageQuery.append( "&" );
        pageQuery.append( distance( firstCol, lastCol ));
        pageQuery.append( "&" );
        sendPageQuery();
    }

	synchronized void startCellEdit( HtmlTable table, int row, int col ) throws Jaxception
	{
        if ( table == null )
            throw new NullPointerException();
        pageQuery.append( "68&" );                 // 68 = start table cell editing
        pageQuery.append( reqIndex++ );            // Request index
        pageQuery.append( "&" );
        pageQuery.append( table.elementIndex );    // 0-based index of table
        pageQuery.append( "&" );
        pageQuery.append( row );
        pageQuery.append( "&" );
        pageQuery.append( col );
        pageQuery.append( "&" );
        sendPageQuery();
	}

    synchronized String[][] getCellContents( HtmlTable table, int firstRow, int firstCol, int nRows, int[] nCols ) throws Jaxception
    {
        if ( table == null )
            throw new NullPointerException();
        if ( nRows == 0 )
            return null;
        if ( nRows < -1 )
            throw new Jaxception( "Number of rows is not valid" );
        int i;
        if ( nRows > 0 && nCols != null ) {
            if ( nCols.length < nRows )
                throw new Jaxception( "Number of elements in nCols is less than nRows" );
            for ( i = 0; i < nRows; i++ ) {
                if ( nCols[i] < -1 ) {
                    throw new Jaxception( "Number of cols[" + i + "] = " + nCols[i] + " is not valid" );
                }
            }
        }
        pageQuery.append( "65&" );                 // 65 = get table contents
        int req = reqIndex++;
        pageQuery.append( req );                 // Request index
        pageQuery.append( "&" );
        pageQuery.append( table.elementIndex );    // 0-based index of table
        pageQuery.append( "&" );
        pageQuery.append( firstRow );
        pageQuery.append( "&" );
        pageQuery.append( firstCol );
        pageQuery.append( "&" );
        pageQuery.append( nRows );
        pageQuery.append( "&" );
        boolean all = nRows == -1;
        for ( i = 0; i < nRows; i++ ) {
            if ( nCols == null )
                pageQuery.append( "-1&" );
            else {
                pageQuery.append( nCols[i] );
                pageQuery.append( "&" );
            }
        }
        String str = getReqReturn( req, "getTableContents" );
        return parseTableContents( str );
    }

    String[][] parseTableContents( String str )
    {
        if ( str == null || str.equals( "" ))
            return null;
        String[] contents = split( str, ',' );
        int tok = 0;
        int nRows = Integer.parseInt( contents[ tok++ ] );
        if ( nRows <= 0 )
            return new String[0][0];
        String[][] ret = new String[ nRows ][];
        for ( int i = 0; i < nRows; i++ ) {
            int nCol = Integer.parseInt( contents[ tok++ ] );
            String[] row = new String[ nCol > 0 ? nCol : 0 ];
            for ( int j = 0; j < nCol; j++ )
                row[j] = decode( contents[ tok++ ] );
            ret[i] = row;
        }
        return ret;
    }


    /**
      * Enable or disable saving of form data from current page
      * into session, for AutoSessionData management.  By default, this
      * is enabled.  To program the Reset/Submit type of behavior
      * of earlier FORMs based web applications, disable saving
      * form to session in the constructor, then in the Submit
      * (or other button) handler, enable saving again before
      * navigating away from the page.
      */

    public void setFormSaveEnabled( boolean enable )
    {
        sessionUpdatingEnabled = enable;
    }

    /**
      * For AutoSessionData pages, returns whether saving form data
      * in session upon page unloads, is enabled.  This behavior
      * is enabled by default, but can be turned off programmatically.
      */
    public boolean getFormSaveEnabled()
    {
        return sessionUpdatingEnabled;
    }

    /**
      * Used to hold updates without sending them out to the client.  This can improve
      * performance by sending out a number of updates all at once.
      * <P>If batch-updates has been set to true, no further data will be sent to the
      * client until batch-updates is reset to false!  This should be done in a try-finally clause.
      * <P><B>Important:</B>  Do not attempt to retrieve data from page while batch-updates is true!
      */

    public void setBatchUpdates( boolean batchUpdates )
    {
        this.doBatchUpdates = batchUpdates;
        if ( ! batchUpdates )
            sendPageQuery();
    }

    synchronized void sendPageQuery()
    {
        if ( unloaded )
            throw new PageUnloadedError();  // Help terminate stray threads
        if ( pageLoadObject == null || doBatchUpdates )
            return;
        synchronized ( pageLoadObject ) {
            StringBuffer buffer = (StringBuffer) pageLoadObject[0];
            buffer.append( pageQuery );
            pageQuery = new StringBuffer();
            pageLoadObject.notify();
        }
    }

    synchronized void flushDragDrop()
    {
        pageQuery.append( "64&" );
    }

    synchronized void includeTableInFormData( HtmlTable table, String saveKeyName )
    {
        if ( tableKeys == null )
            tableKeys = new HashMap();
        tableKeys.put( saveKeyName.toLowerCase(), table );
        addPageCall( table, 28, saveKeyName);
        sendPageQuery();
    }

    private class SearchWait extends Thread {
        Object sync;
        Object reqInt;
        int req;
        OnMultipleElementSearch onSearch;
        boolean running = false;

        SearchWait( Object sn, Object ri, int r, OnMultipleElementSearch s )
        {
                this.sync = sn;
                this.reqInt = ri;
                this.req = r;
                this.onSearch = s;
        }

        public void run()
        {
            Object obj = null;
            String error = null;
            try {
                synchronized (sync) {
                    running = true;
                    sync.wait( QUERY_TIMEOUT );
                    obj = reqSync.get( reqInt );
                    error = (String) reqError.get( reqInt );
                }
            } catch (InterruptedException ex) {
            } finally {
                reqSync.remove( reqInt );
                reqError.remove( reqInt );
            }
            if ( obj.equals( sync )) {
                onSearch.onTimeout();
                // Timeout
            } else {
                StringTokenizer tok = new StringTokenizer( (String) obj, "_" );
                int numberOfElements = Integer.parseInt( tok.nextToken());
                String reference = tok.nextToken();
                tok = new StringTokenizer( tok.nextToken(), "+", true );
                int nValues = Integer.parseInt( tok.nextToken());
                boolean delim = false;
                int index = 0;
                String[] v = null;
                if ( nValues > 0 )
                    v = new String[ nValues ];
                while ( index < nValues ) {
                    String value = tok.nextToken();
                    if ( value.equals( "+" )) {
                        if ( delim ) {
                            value = "";
                        } else {
                            delim = true;
                            continue;
                        }
                    } else {
                        delim = false;
                        value = decode( value );
                    }
                    v[index] = value;
                    index++;
                }
                // Allocate 'numberOfElements' HtmlElements
                HtmlElement [] el = null;
                try {
                    if ( numberOfElements > 0 ) {
                        el = new HtmlElement[ numberOfElements ];
                        addMultipleElements( el, reference );
                    }
                    onSearch.onSearchComplete( el, v );
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    synchronized void searchForElements( String tagName, String attributeName, OnMultipleElementSearch onSearch ) throws Jaxception
    {
        if ( doBatchUpdates )
            throw new Jaxception( "Batch-updates is true; cannot retrieve elements!" );
        if ( onSearch == null )
            throw new NullPointerException( "onSearch is null" );
        if (( tagName == null || tagName.equals( "" )) &&
            ( attributeName == null || attributeName.equals( "" )))
               throw new Jaxception( "Tag name and attribute name are both null or empty" );

        pageQuery.append( "69&" );                 // 69 = search for multiple elements
        int req = reqIndex++;
        pageQuery.append( req );                 // Request index
        pageQuery.append( "&" );
        pageQuery.append( encode( tagName ));
        pageQuery.append( "&" );
        pageQuery.append( encode( attributeName ));
        pageQuery.append( "&" );
        Object reqObject = new Object();
        Integer reqInt = new Integer( req );
        SearchWait sw = new SearchWait( reqObject, reqInt, req, onSearch );
        reqSync.put( reqInt, reqObject );
        sw.start();
        for (;;) {
            synchronized (reqObject) {
                if ( sw.running )
                    break;
            }
            try { Thread.sleep( 1 ); } catch (Exception ex) {}
        }
        sendPageQuery();
    }

    /** Save data in the application context, using a given key (like a Map.)
      */

    public void saveInAppContext( String key, Object value ) throws Jaxception
    {
        if ( key == null || value == null )
            throw new NullPointerException();
        if ( key.equalsIgnoreCase( "jaxcent.context" ) ||
             key.equalsIgnoreCase( "jaxcentServlet.context" )) {
            throw new Jaxception( "Illegal key" );
        }
        try {
            Method saveInCtx = ctx.getClass().getMethod( "setAttribute",
                                          new Class[]{ String.class, Object.class } );
            saveInCtx.invoke( ctx, new Object[]{ key, value });
        } catch (InvocationTargetException ite) {
            Throwable t = ite.getCause();
            if ( t instanceof RuntimeException )
                throw (RuntimeException) t;
            if ( t instanceof Error )
                throw (Error) t;
            throw new Jaxception( t.toString());
         } catch (Exception ex) {
            throw new Jaxception( ex.toString());
        }
    }

    /** Get data from application context.
      *
      * @see jaxcent.JaxcentPage#saveInAppContext
      */
    public Object getFromAppContext( String key ) throws Jaxception
    {
        if ( key == null )
            throw new NullPointerException();
        if ( key.equalsIgnoreCase( "jaxcent.context" ) ||
             key.equalsIgnoreCase( "jaxcentServlet.context" )) {
            throw new Jaxception( "Illegal key" );
        }
        try {
            Method getFromCtx = ctx.getClass().getMethod( "getAttribute",
                                          new Class[]{ String.class } );
            return getFromCtx.invoke( ctx, new Object[]{ key } );
        } catch (InvocationTargetException ite) {
            Throwable t = ite.getCause();
            if ( t instanceof RuntimeException )
                throw (RuntimeException) t;
            if ( t instanceof Error )
                throw (Error) t;
            throw new Jaxception( t.toString());
         } catch (Exception ex) {
            throw new Jaxception( ex.toString());
        }
    }

    /** Remove key from application context.
      *
      * @see jaxcent.JaxcentPage#saveInAppContext
      * @see jaxcent.JaxcentPage#getFromAppContext
      */
    public void removeAppContextKey( String key ) throws Jaxception
    {
        if ( key == null )
            throw new NullPointerException();
        if ( key.equalsIgnoreCase( "jaxcent.context" ) ||
             key.equalsIgnoreCase( "jaxcentServlet.context" )) {
            throw new Jaxception( "Illegal key" );
        }
        try {
            Method removeFromCtx = ctx.getClass().getMethod( "removeAttribute",
                                          new Class[]{ String.class } );
            removeFromCtx.invoke( ctx, new Object[]{ key } );
        } catch (InvocationTargetException ite) {
            Throwable t = ite.getCause();
            if ( t instanceof RuntimeException )
                throw (RuntimeException) t;
            if ( t instanceof Error )
                throw (Error) t;
            throw new Jaxception( t.toString());
         } catch (Exception ex) {
            throw new Jaxception( ex.toString());
        }
    }

    /** Get list of key names from application context.
      *
      * @see jaxcent.JaxcentPage#saveInAppContext
      * @see jaxcent.JaxcentPage#getFromAppContext
      */
    public java.util.Enumeration getAppContextKeys() throws Jaxception
    {
        try {
            Method getNames = ctx.getClass().getMethod( "getAttributeNames", null );
            return (java.util.Enumeration) getNames.invoke( ctx, null );
        } catch (InvocationTargetException ite) {
            Throwable t = ite.getCause();
            if ( t instanceof RuntimeException )
                throw (RuntimeException) t;
            if ( t instanceof Error )
                throw (Error) t;
            throw new Jaxception( t.toString());
         } catch (Exception ex) {
            throw new Jaxception( ex.toString());
        }
    }

    // File upload handling.

    void onUploadBegin( int objid, String contentType, int contentLength )
    {
        try {
            HtmlUploadForm uploadForm = (HtmlUploadForm) elements.elementAt( objid );
            uploadForm.initUpload( contentType, contentLength );
        } catch (Exception ex) {}
    }

    boolean onUpload( int objid, byte[] data, int ofs, int len )
    {
        try {
            HtmlUploadForm uploadForm = (HtmlUploadForm) elements.elementAt( objid );
            return uploadForm.upload( data, ofs, len );
        } catch (Exception ex) {}
        return false;
    }

    void onUploadEnd( int objid )
    {
        try {
           HtmlUploadForm uploadForm = (HtmlUploadForm) elements.elementAt( objid );
           uploadForm.termUpload();
        } catch (Exception ex) {}
    }

    void setClientInfo( String cver, String curl )
    {
        clientVersion = cver;
        clientUrl = curl;
    }

    /**
      * Returns a string describing the connected page's version number, e.g. "2.1.1".  It should be the same as the Jaxcent version.
      */
    public String getClientVersion()
    {
        return clientVersion;
    }

    /**
      * Returns the URL used in the client for Jaxcent.  The default is /JaxcentServlet21
      */
    public String getClientJaxcentURL()
    {
        return clientUrl;
    }

    /**
      * Return Jaxcent version.
      */
    public static String getJaxcentVersion()
    {
        return jaxcentVersion;
    }

    /**
      * Returns a URL that will return a given content-type and bytes.  The information is placed
      * in the session.  The URL is based upon the Jaxcent URL, and can be used
      * for applications such as generating a PDF file.  "UseSession" or "AutoSessionData"
      * must be configured for the page.  The "filename" argument can be null.  If it is non-null,
      * it will be appended to the servlet part of the URL and before the query string.
      */
    public String makeContentURL( String contentType, byte[] content, String filename ) throws Jaxception
    {
        if ( session == null )
           throw new Jaxception( "Session is not available" );

        StringBuffer url = new StringBuffer();
        url.append( clientUrl );
        if ( filename != null && ! filename.trim().equals( "" )) {
            url.append( "/" );
            url.append( filename );
        }
        StringBuffer uidb = new StringBuffer();
        int hcode = hashCode();
        uidb.append( Integer.toHexString( hcode ));
        uidb.append( "_" );
        uidb.append( Long.toHexString( System.currentTimeMillis()));
        uidb.append( "_" );
        uidb.append( imageIndex++ );
        String uid = uidb.toString();
        url.append( "?JaxcentBinaryData=" );
        url.append( uid );
        Object[] data = new Object[]{ contentType, content };
        try {
            Method setAttr = session.getClass().getMethod( "setAttribute", new Class[]{ String.class, Object.class } );
            setAttr.invoke( session, new Object[]{ uid, data } );
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Jaxception( ex.toString());
        }
        return url.toString();
    }
}
