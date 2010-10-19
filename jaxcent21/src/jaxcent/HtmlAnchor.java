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
 * Module Name:           HtmlAnchor.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Class HtmlAnchor corresponds to A tags.
 *
 * Change History:
 *
 *    12/30/2007-8/22/2008  MP      Initial versions.
 *
 */


package jaxcent;

import jaxcent.JaxcentHtmlElement;
import jaxcent.JaxcentPage;
import jaxcent.Jaxception;
import jaxcent.SearchType;

import java.util.Vector;

/**
  * The class HtmlAnchor corresponds to A tags on the page.
  */

public class HtmlAnchor extends JaxcentHtmlElement {

    /**
      * Search for HTML element on page by specified ID
      */
    public HtmlAnchor( JaxcentPage page, String id )
    {
        super( page, SearchType.searchById, id, 0 );
    }

    /**
      * Search for HTML element on page by specified search type and search string.
      * If the search returns multiple objects, use the first one.
      */

    public HtmlAnchor( JaxcentPage page, SearchType searchType, String str )
    {
        super( page, searchType, str, 0 );
    }

    /**
      * Search for HTML Element on page by specified search type and search string, and search index.
      * The search is expected to return multiple results.  The search index is 0-based,
      * and specifies the index in the multiple results.  This constructor
      * is not for use with createNew.
      */

    public HtmlAnchor( JaxcentPage page, SearchType searchType, String str, int index )
    {
        super( page, searchType, str, index );
    }

    /**
      * Create new HTML Element on page using the specified tag.  Search type must be createNew and tag must be "A".
      * If text is non null, the new element is populated with that text.
      */

    public HtmlAnchor( JaxcentPage page, SearchType searchType, String tag, String text ) throws Jaxception
    {
        super( page, searchType, tag, text );
    }

    /**
      * Create new HTML element on page using the specified attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlAnchor( JaxcentPage page, SearchType searchType, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, "A", attributes, values );
    }

    /**
      * Create new HTML element on page using the specified text and attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlAnchor( JaxcentPage page, SearchType searchType, String text, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, "A", text, attributes, values );
    }

    String getExpectedTag()
    {
        return "A";
    }

    void getEvents( Vector events )
    {
        super.getEvents( events );
        checkEvent( events, "click", "onClick", HtmlAnchor.class );
        checkEvent( events, "focus", "onFocus", HtmlAnchor.class );
        checkEvent( events, "blur", "onBlur", HtmlAnchor.class );
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
     * Set the "coords" property
     */
    public void setCoords( String value ) throws Jaxception
    {
        setProperty( "coords", value );
    }

    /**
     * Retrieve the "coords" property
     */
    public String getCoords() throws Jaxception
    {
        return getProperty( "coords" );
    }

    /**
     * Set the "href" property
     */
    public void setHref( String value ) throws Jaxception
    {
        setProperty( "href", value );
    }

    /**
     * Retrieve the "href" property
     */
    public String getHref() throws Jaxception
    {
        return getProperty( "href" );
    }

    /**
     * Set the "shape" property
     */
    public void setShape( String value ) throws Jaxception
    {
        setProperty( "shape", value );
    }

    /**
     * Retrieve the "shape" property
     */
    public String getShape() throws Jaxception
    {
        return getProperty( "shape" );
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
     * Set the "type" property
     */
    public void setType( String value ) throws Jaxception
    {
        setProperty( "type", value );
    }

    /**
     * Retrieve the "type" property
     */
    public String getType() throws Jaxception
    {
        return getProperty( "type" );
    }

    /**
     * Set the "charset" property
     */
    public void setCharset( String value ) throws Jaxception
    {
        setProperty( "charset", value );
    }

    /**
     * Retrieve the "charset" property
     */
    public String getCharset() throws Jaxception
    {
        return getProperty( "charset" );
    }

    /**
     * Set the "rel" property
     */
    public void setRel( String value ) throws Jaxception
    {
        setProperty( "rel", value );
    }

    /**
     * Retrieve the "rel" property
     */
    public String getRel() throws Jaxception
    {
        return getProperty( "rel" );
    }

    /**
     * Set the "rev" property
     */
    public void setRev( String value ) throws Jaxception
    {
        setProperty( "rev", value );
    }

    /**
     * Retrieve the "rev" property
     */
    public String getRev() throws Jaxception
    {
        return getProperty( "rev" );
    }

    /**
     * Set the "target" property
     */
    public void setTarget( String value ) throws Jaxception
    {
        setProperty( "target", value );
    }

    /**
     * Retrieve the "target" property
     */
    public String getTarget() throws Jaxception
    {
        return getProperty( "target" );
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

