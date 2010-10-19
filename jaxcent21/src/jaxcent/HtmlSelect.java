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
 * Module Name:           HtmlSelect.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Class HtmlSelect corresponds to SELECT tags.
 *
 * Change History:
 *
 *    12/30/2007-8/22/2008  MP      Initial versions.
 *
 */


package jaxcent;

import java.util.Vector;

/**
  * The class HtmlSelect corresponds to SELECT tags on the page.
  */

public class HtmlSelect extends JaxcentHtmlElement {

    /**
      * Search for HTML element on page by specified ID
      */
    public HtmlSelect( JaxcentPage page, String id )
    {
        super( page, SearchType.searchById, id, 0 );
    }

    /**
      * Search for HTML element on page by specified search type and search string.
      * If the search returns multiple objects, use the first one.
      */

    public HtmlSelect( JaxcentPage page, SearchType searchType, String str )
    {
        super( page, searchType, str, 0 );
    }

    /**
      * Search for HTML Element on page by specified search type and search string, and search index.
      * The search is expected to return multiple results.  The search index is 0-based,
      * and specifies the index in the multiple results.  This constructor
      * is not for use with createNew.
      */

    public HtmlSelect( JaxcentPage page, SearchType searchType, String str, int index )
    {
        super( page, searchType, str, index );
    }

    /**
      * Create new HTML Element on page using the specified tag.  Search type must be createNew and tag must be "SELECT".
      * If text is non null, the new element is populated with that text.
      */

    public HtmlSelect( JaxcentPage page, SearchType searchType, String tag, String text ) throws Jaxception
    {
        super( page, searchType, tag, text );
    }

    /**
      * Create new HTML element on page using the specified attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlSelect( JaxcentPage page, SearchType searchType, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, "SELECT", attributes, values );
    }

    /**
      * Create new HTML element on page using the specified text and attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlSelect( JaxcentPage page, SearchType searchType, String text, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, "SELECT", text, attributes, values );
    }

    String getExpectedTag()
    {
        return "SELECT";
    }

    void getEvents( Vector events )
    {
        super.getEvents( events );
        checkEvent( events, "click", "onClick", HtmlSelect.class );
        checkEvent( events, "focus", "onFocus", HtmlSelect.class );
        checkEvent( events, "blur", "onBlur", HtmlSelect.class );
        checkEvent( events, "change", "onChange", HtmlSelect.class );
    }

    /**
     * Override to handle the "click" event
     */
    protected void onClick()
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
     * Override to handle the "change" event
     */
    protected void onChange()
    {
    }

    /**
     * Override to handle the "change" event and receive current element value
     */
    protected void onChange( String value )
    {
    }


    /**
      *  Returns the number of OPTIONS in the select.
      */

    public int getNumOptions() throws Jaxception
    {
        return getPropertyInt( "options.length" );
    }

    /**
      * Return the OPTION at the specified index.
      */
    public HtmlOption getOption( int index ) throws Jaxception
    {
        return new HtmlOption( page, SearchType.SEARCH_SELECT_OPTION, index, null, null, null, this, null, null, null );
    }

    /**
      * Delete the OPTION at the specified index.
      */
    public void deleteOption( int index ) throws Jaxception
    {
        page.deleteOption( this, index );
    }

    /**
      * Insert an OPTION in the SELECT at the specified index.  If index is -1, the OPTION is inserted
      * at the end.
      */
    public HtmlOption insertOption( int index, String optionText ) throws Jaxception
    {
        return new HtmlOption( page, SearchType.CREATE_OPTION, index, optionText, null, null, this, null, null, null );
    }

    /**
      * Insert an OPTION in the SELECT at the specified index with the specified text and value.  If index is -1, the OPTION is inserted
      * at the end.
      */
    public HtmlOption insertOption( int index, String optionText, String optionValue ) throws Jaxception
    {
        String[] attributes = { "value" };
        String[] values = { optionValue };
        return new HtmlOption( page, SearchType.CREATE_OPTION, index, optionText, attributes, values, this, null, null, null );
    }

    /**
      * Insert an OPTION in the SELECT at the specified index.  If index is -1, the OPTION is inserted
      * at the end.  The attributes and value arrays must have the same length.  These attributes
      * are added to the option.
      */
    public HtmlOption insertOption( int index, String optionText, String[] attributes, String[] values ) throws Jaxception
    {
        if ( attributes.length != values.length )
            throw new Jaxception( "The length of attributes and values arrays do not match" );
        return new HtmlOption( page, SearchType.CREATE_OPTION, index, optionText, attributes, values, this, null, null, null );
    }


    /**
      * Remove all OPTIONs.  Useful before inserting new OPTIONs.
      */
    public void deleteAllOptions() throws Jaxception
    {
        methodCall( 16 );
    }

    /**
      * Remove N options from top.  If N is negative, keep -N (abs N) options, remove rest from top.
      */
    public void deleteFromTop( int n ) throws Jaxception
    {
        methodCall( 17, n );
    }

    /**
      * Remove N options from bottom.  If N is negative, keep -N (abs N) options, remove rest from bottom.
      */
    public void deleteFromBottom( int n ) throws Jaxception
    {
        methodCall( 18, n );
    }

    /**
      * Set SIZE of SELECT same as the number of options.
      */
    public void sizeToOptions() throws Jaxception
    {
        methodCall( 19 );
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
     * Retrieve the "length" property
     */
    public int getLength() throws Jaxception
    {
        return getPropertyInt( "length" );
    }

    /**
     * Sets whether multiple items can be selected
     */
    public void setMultiple( boolean value ) throws Jaxception
    {
        setProperty( "multiple", value );
    }

    /**
     * Whether multiple items can be selected
     */
    public boolean getMultiple() throws Jaxception
    {
        return getPropertyBool( "multiple" );
    }

    /**
     * Set the "name" property
     */
    public void setName( String value ) throws Jaxception
    {
        setProperty( "name", value );
    }

    /**
     * Retrieve the "name" property
     */
    public String getName() throws Jaxception
    {
        return getProperty( "name" );
    }

    /**
     * Set the "selectedIndex" property
     */
    public void setSelectedIndex( int value ) throws Jaxception
    {
        setProperty( "selectedIndex", value );
    }

    /**
     * Retrieve the "selectedIndex" property
     */
    public int getSelectedIndex() throws Jaxception
    {
        return getPropertyInt( "selectedIndex" );
    }

    /**
     * Set the "size" property
     */
    public void setSize( int value ) throws Jaxception
    {
        setProperty( "size", value );
    }

    /**
     * Retrieve the "size" property
     */
    public int getSize() throws Jaxception
    {
        return getPropertyInt( "size" );
    }

    /**
      * Lose the input focus.
      */
    public void blur() throws Jaxception
    {
        methodCall( 3 );
    }

    /**
      * Get the input focus.
      */
    public void focus() throws Jaxception
    {
        methodCall( 2 );
    }

}

