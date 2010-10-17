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
 * Module Name:           HtmlInputCheckbox.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Class HtmlInputCheckbox corresponds to INPUT tags.
 *
 * Change History:
 *
 *    12/30/2007-8/22/2008  MP      Initial versions.
 *
 */


package jaxcent;

import java.util.Vector;

/**
  * The class HtmlInputCheckbox corresponds to INPUT tags of TYPE CHECKBOX on the page.
  */

public class HtmlInputCheckbox extends JaxcentHtmlElement {

    /**
      * Search for HTML element on page by specified ID
      */
    public HtmlInputCheckbox( JaxcentPage page, String id )
    {
        super( page, SearchType.searchById, id, 0 );
    }

    /**
      * Search for HTML element on page by specified search type and search string.
      * If the search returns multiple objects, use the first one.  If the searchType
      * is createNew and the thrid parameter is non null, the third parameter
      * specifies the value attribute.
      */

    public HtmlInputCheckbox( JaxcentPage page, SearchType searchType, String str )
    {
        super( page, searchType, str, 0 );
    }

    int createNewType() { return 1; } // 1 == value for empty ctor.

    /**
      * Search for HTML Element on page by specified search type and search string, and search index.
      * The search is expected to return multiple results.  The search index is 0-based,
      * and specifies the index in the multiple results.  This constructor
      * is not for use with createNew.
      */

    public HtmlInputCheckbox( JaxcentPage page, SearchType searchType, String str, int index )
    {
        super( page, searchType, str, index );
    }

    /**
      * Create new HTML Element on page using the specified tag.  Search type must be createNew and tag must be "INPUT".
      * If text is non null, the new element is populated with that text.
      */

    public HtmlInputCheckbox( JaxcentPage page, SearchType searchType, String tag, String text ) throws Jaxception
    {
        super( page, searchType, tag, text );
    }

    /**
      * Create new HTML element on page using the specified attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlInputCheckbox( JaxcentPage page, SearchType searchType, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, "INPUT", attributes, values );
    }

    /**
      * Create new HTML element on page using the specified text and attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlInputCheckbox( JaxcentPage page, SearchType searchType, String text, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, "INPUT", text, attributes, values );
    }

    String getExpectedTag()
    {
        return "INPUT";
    }

    String getExpectedType()
    {
        return "CHECKBOX";
    }

    void getEvents( Vector events )
    {
        super.getEvents( events );
        checkEvent( events, "click", "onClick", HtmlInputCheckbox.class );
        checkEvent( events, "focus", "onFocus", HtmlInputCheckbox.class );
        checkEvent( events, "blur", "onBlur", HtmlInputCheckbox.class );
    }

    /**
     * Override to handle the "click" event
     */
    protected void onClick()
    {
    }

    /**
     * Override to handle the "click" event and receive current element value
     */
    protected void onClick( String value )
    {
    }

    /**
     * Override to handle the "focus" event
     */
    protected void onFocus()
    {
    }

    /**
     * Override to handle the "blur" event
     */
    protected void onBlur()
    {
    }


    /**
     * Set the "checked" property
     */
    public void setChecked( boolean value ) throws Jaxception
    {
        setProperty( "checked", value );
    }

    /**
     * Retrieve the "checked" property
     */
    public boolean getChecked() throws Jaxception
    {
        return getPropertyBool( "checked" );
    }

    /**
     * Returns whether this checkbox/radiobutton is checked on by default
     */
    public boolean getDefaultchecked() throws Jaxception
    {
        return getPropertyBool( "defaultchecked" );
    }

    /**
     * Set the "disabled" property
     */
    public void setDisabled( boolean value ) throws Jaxception
    {
        setProperty( "disabled", value );
    }

    /**
     * Retrieve the "disabled" property
     */
    public boolean getDisabled() throws Jaxception
    {
        return getPropertyBool( "disabled" );
    }

    /**
     * Retrieve the "name" property
     */
    public String getName() throws Jaxception
    {
        return getProperty( "name" );
    }

    /**
     * Retrieve the "value" property
     */
    public String getValue() throws Jaxception
    {
        return getProperty( "value" );
    }

    /**
      * Lose the input focus.
      */
    public void blur() throws Jaxception
    {
        methodCall( 3 );
    }

    /**
      * Simulate a mouse click on the HTML element.
      */
    public void click() throws Jaxception
    {
        methodCall( 1 );
    }

    /**
      * Get the input focus.
      */
    public void focus() throws Jaxception
    {
        methodCall( 2 );
    }

    /**
      * Bring the element into view, by scrolling if necessary.
      * If top is true, the top of element is brought into view,
      * otherwise the bottom of the element is scrolled into view.
      */

    public void scrollIntoView( boolean top ) throws Jaxception
    {
        methodCall( top ? 4 : 5 );
    }

}

