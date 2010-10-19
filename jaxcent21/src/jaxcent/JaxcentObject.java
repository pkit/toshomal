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
 * Module Name:           JaxcentObject.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Jaxcent base class for HTML objects.
 *
 * Change History:
 *
 *    12/27/2007  MP      Initial version.
 *
 */

package jaxcent;

import java.lang.reflect.Method;
import java.util.Vector;

/**
  * This is the base class for Jaxcent objects.
  * Normally there is no need to use this class directly.
  */

public class JaxcentObject  {

    JaxcentPage page;
    int elementIndex;

    static Class[] pageDataEventType = { java.util.Map.class };
    static Class[] elementValueEventType = { java.lang.String.class };
    static Class[] elementIndexEventType = { java.lang.Integer.TYPE };
    static Class[] elementHtmlElementType = { JaxcentHtmlElement.class };
    static Class[] cellEditedEventType = { java.lang.Integer.TYPE, java.lang.Integer.TYPE, java.lang.String.class, java.lang.String.class };


    /**
      * Search for object on page by specified ID
      */

    protected JaxcentObject( JaxcentPage page, String id )
    {
        this( page, SearchType.searchById, id, 0 );
    }

    /**
      * Search for object on page by specified search type and search string.
      * If the search returns multiple objects, use the first one.
      * searchType can be createNew, in which case the string parameter is the
      * tag instead.  The surrounding less than and greater than characters
      * must not be a part of the tag. 
      */
    protected JaxcentObject( JaxcentPage page, SearchType searchType, String str )
    {
        this( page, searchType, str, 0 );
    }

    /**
      * Search for object on page by specified search type and search string, and search index.
      * The search is expected to return multiple results.  The search index is 0-based,
      * and specifies the index in the multiple results.  This constructor
      * is not for use with createNew.
      */

    protected JaxcentObject( JaxcentPage page, SearchType searchType, String str, int index )
    {
        this.page = page;
        page.addElement( this, searchType.searchType, str, index, null, null, null, null );
    }

    JaxcentObject( JaxcentPage page, int searchType, String str, int index )
    {
        this.page = page;
        page.addElement( this, searchType, str, index, null, null, null, null );
    }

    JaxcentObject( JaxcentPage page, int elementIndex )
    {
        this.page = page;
        this.elementIndex = elementIndex;
    }

    int createNewType() { return 0; }

    /**
      * Create new object on page using the specified tag.  Search type must be createNew.
      * If text is non null, the new element is populated with that text.
      */

    protected JaxcentObject( JaxcentPage page, SearchType searchType, String tag, String text ) throws Jaxception
    {
        this( page, searchType, tag, text, null, null );
    }

    /**
      * Create new object on page using the specified attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    protected JaxcentObject( JaxcentPage page, SearchType searchType, String tag, String[] attributes, String[] values ) throws Jaxception
    {
        this( page, searchType, tag, null, attributes, values );
    }

    /**
      * Create new object on page using the specified attributes and values and text.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    protected JaxcentObject( JaxcentPage page, SearchType searchType, String tag, String text, String[] attributes, String[] values ) throws Jaxception
    {
        if ( searchType.searchType != SearchType.CREATE_NEW )
            throw new Jaxception( "This form of the constructor can only be used with SearchType.createNew" );
        if ( tag != null ) tag = tag.trim();
        if ( tag.indexOf( '<' ) >= 0 || tag.indexOf( '>' ) >= 0 )
            throw new Jaxception( "The tag must not contain surrounding angle brace characters" );
        if ( attributes != null && ( values == null || values.length != attributes.length )) {
            throw new Jaxception( "Attributes and values arrays must have the same array length" );
        }
        this.page = page;
        page.addElement( this, searchType.searchType, tag, 0, text, attributes, values, null );
    }

    JaxcentObject( JaxcentPage page, int searchType, String tag, int index, String text, String[] attributes, String[] values, JaxcentObject otherElement, String[] subElements, String[][] subAttributes, String[][] subValues )
    {
        this.page = page;
        page.addElement( this, searchType, tag, index, text, attributes, values, otherElement, subElements, subAttributes, subValues );
    }

    String getExpectedTag()
    {
        return "0";  // Not expecting any particular tag.
    }

    String getExpectedType()
    {
        return "0";
    }

    Method getEventMethod( String method, Class baseClass, Class[] params )
    {
        try {
            Method mth = getClass().getDeclaredMethod( method, params );
            if ( mth.getDeclaringClass().equals( baseClass ))
                return null;
            return mth;
        } catch (Throwable th) {}
        return null;
    }

    void checkEvent( Vector events, String eventName, String method, Class baseClass )
    {
        String eventType = "1";
        Method mth = getEventMethod( method, baseClass, null );
        if ( mth == null ) {
            mth = getEventMethod( method, baseClass, pageDataEventType );
            if ( mth != null ) eventType = "2";
        }
        if ( mth == null ) {
            mth = getEventMethod( method, baseClass, elementValueEventType );
            if ( mth != null ) eventType = "3";
        }
        if ( mth == null ) {
            mth = getEventMethod( method, baseClass, elementIndexEventType );
            if ( mth != null ) eventType = "4";
        }
        if ( mth == null ) {
            mth = getEventMethod( method, baseClass, elementHtmlElementType );
            if ( mth != null ) eventType = "5";
        }
        if ( mth == null ) {
            mth = getEventMethod( method, baseClass, cellEditedEventType );
            if ( mth != null ) eventType = "6";
        }
        if ( mth == null )
            return;
        events.addElement( eventName );
        events.addElement( mth );
        events.addElement( eventType );
    }

    void getEvents( Vector events )
    {
    }

    /**
      * Get the value of a a property.
      */
    public String getProperty( String propName ) throws Jaxception
    {
        if ( propName == null )
            throw new NullPointerException( "propName is null" );
        return page.queryGetProperty( this, false, propName );
    }

    /**
      * Set a property to the specified value.
      */
    public void setProperty( String propName, String value ) throws Jaxception
    {
        if ( propName == null )
            throw new NullPointerException( "propName is null" );
        if ( value == null )
            value = "";
        page.addSetProperty( this, false, false, propName, value );
        page.sendPageQuery();
    }

    /**
      * Set a property to the specified int value.
      */
    public void setProperty( String propName, int value ) throws Jaxception
    {
        setProperty( propName, String.valueOf( value ));
    }

    /**
      * Set a property to the specified boolean value.
      */
    public void setProperty( String propName, boolean value ) throws Jaxception
    {
        if ( propName == null )
            throw new NullPointerException( "propName is null" );
        page.addSetProperty( this, false, true, propName, value ? "1" : "0" );
        page.sendPageQuery();
    }

    /**
      * Set ID property.
      */
    public void setId( String id ) throws Jaxception
    {
        setProperty( "id", id );
    }

    /**
      * Retrieve any ID property.
      */
    public String getId() throws Jaxception
    {
        return getProperty( "id" );
    }

    /**
      * Set a property to the specified java.awt.Color value.
      */

    public void setProperty( String propName, java.awt.Color value ) throws Jaxception
    {
        setProperty( propName, getColorString( value ));
    }

    /**
      * Add JavaScript verification code on a handler.  If the event is being over-ridden,
      * the verification code is called first.  If it does not return a value of true, or
      * if it fails, the Java event handler is not called.<P>This method can be used to reduct client-server
      * trips by doing some of the verification on the client side before calling the event handler.
      * <P>The arguments array can contain Strings, Integers, Doubles, Strings, Boolean,
      * other Jaxcent HTML Elements (or "this"), or can be null.  If the argument array is not null,
      * the "verifier" must not contain the parentheses character.  The characters "( )" containing
      * any arguments are appended to the "verifier".  The array specifies the list of parameters.
      * If the argument array is null, the "verifier" is evaluated as is, as an expression.  To
      * call a function with no args, use the form <TT>addJavaScriptVerification( "click", "myCheckFunction()", null );</TT>
      */

    public void addJavaScriptVerification( String event, String verifier, Object[] args ) throws Jaxception
    {
        if ( event == null || event.trim().equals( "" ))
            throw new NullPointerException();
        page.addEvenVerifier( this, event, verifier, args );
    }

    void methodCall( int index )
    {
        page.addPageCall( this, index );
        page.sendPageQuery();
    }

    void methodCall( int index, int arg )
    {
        page.addPageCall( this, index, arg );
        page.sendPageQuery();
    }

    void methodCall( int index, String arg )
    {
        if ( arg == null )
            throw new NullPointerException();
        page.addPageCall( this, index, arg );
        page.sendPageQuery();
    }

    int getPropertyInt( String propName ) throws Jaxception
    {
        String str = getProperty( propName );
        if ( str == null || str.equals( "" ))
            return 0;
        return Integer.parseInt( str );
    }

    boolean getPropertyBool( String propName ) throws Jaxception
    {
        String str = getProperty( propName );
        return str != null && str.equalsIgnoreCase( "true" );
    }

    void append2hex( StringBuffer buf, int value )
    {
        buf.append( Character.forDigit( value / 16, 16 ));
        buf.append( Character.forDigit( value % 16, 16 ));
    }

    String getColorString( java.awt.Color value )
    {
        StringBuffer buf = new StringBuffer( "#" );
        append2hex( buf, value.getRed());
        append2hex( buf, value.getGreen());
        append2hex( buf, value.getBlue());
        return buf.toString();
    }

    /**
      * Utility method to retrieve the index from the String returned
      * in the Map for SELECT items.  The String is of the
      * form "index: value".  This method parses it and returns
      * the index as an integer.  If the input is ill-formatted,
      * returns -2.  -1 is a valid value, indicating nothing
      * was selected.
      */

    public static int getSelectedIndex( Object valueFromMap )
    {
       if ( valueFromMap == null )
           return -2;
       String str = (String) valueFromMap;
       int c = str.indexOf( ':' );
       if ( c > 0 ) try {
           return Integer.parseInt( str.substring( 0, c ).trim());
       } catch (Exception ex) {}
       return -2;
    }

    /**
      * Utility method to retrieve the value from the String returned
      * in the Map for SELECT items.  The String is of the
      * form "index: value".  This method parses it and returns
      * the value as a String, without the leading index.  If the
      * input is ill-formatted, returns null.
      */

    public static String getSelectedValue( Object valueFromMap )
    {
       if ( valueFromMap == null )
           return null;
       String str = (String) valueFromMap;
       if ( str.startsWith( "-" ))
           return "";
       int c = str.indexOf( ':' );
       if ( c > 0 ) {
           c++;
           if ( c < str.length() && Character.isSpaceChar( str.charAt( c )))
               c++;
           return str.substring( c );
       }
       return null;
    }
}
