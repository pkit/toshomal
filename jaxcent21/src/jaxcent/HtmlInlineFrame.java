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
 * Module Name:           HtmlInlineFrame.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Class HtmlInlineFrame corresponds to IFRAME tags.
 *
 * Change History:
 *
 *    12/30/2007-8/22/2008  MP      Initial versions.
 *
 */


package jaxcent;

import java.util.Vector;

/**
  * The class HtmlInlineFrame corresponds to IFRAME tags on the page.
  */

public class HtmlInlineFrame extends JaxcentHtmlElement {

    /**
      * Search for HTML element on page by specified ID
      */
    public HtmlInlineFrame( JaxcentPage page, String id )
    {
        super( page, SearchType.searchById, id, 0 );
    }

    /**
      * Search for HTML element on page by specified search type and search string.
      * If the search returns multiple objects, use the first one.
      */

    public HtmlInlineFrame( JaxcentPage page, SearchType searchType, String str )
    {
        super( page, searchType, str, 0 );
    }

    /**
      * Search for HTML Element on page by specified search type and search string, and search index.
      * The search is expected to return multiple results.  The search index is 0-based,
      * and specifies the index in the multiple results.  This constructor
      * is not for use with createNew.
      */

    public HtmlInlineFrame( JaxcentPage page, SearchType searchType, String str, int index )
    {
        super( page, searchType, str, index );
    }

    /**
      * Create new HTML Element on page using the specified tag.  Search type must be createNew and tag must be "IFRAME".
      * If text is non null, the new element is populated with that text.
      */

    public HtmlInlineFrame( JaxcentPage page, SearchType searchType, String tag, String text ) throws Jaxception
    {
        super( page, searchType, tag, text );
    }

    /**
      * Create new HTML element on page using the specified attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlInlineFrame( JaxcentPage page, SearchType searchType, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, "IFRAME", attributes, values );
    }

    /**
      * Create new HTML element on page using the specified text and attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlInlineFrame( JaxcentPage page, SearchType searchType, String text, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, "IFRAME", text, attributes, values );
    }

    String getExpectedTag()
    {
        return "IFRAME";
    }

    void getEvents( Vector events )
    {
        super.getEvents( events );
    }


    /**
     * Set the "align" property
     */
    public void setAlign( String value ) throws Jaxception
    {
        setProperty( "align", value );
    }

    /**
     * Retrieve the "align" property
     */
    public String getAlign() throws Jaxception
    {
        return getProperty( "align" );
    }

    /**
     * Set the "frameborder" property
     */
    public void setFrameborder( int value ) throws Jaxception
    {
        setProperty( "frameborder", value );
    }

    /**
     * Retrieve the "frameborder" property
     */
    public int getFrameborder() throws Jaxception
    {
        return getPropertyInt( "frameborder" );
    }

    /**
     * Set the "height" property
     */
    public void setHeight( String value ) throws Jaxception
    {
        setProperty( "height", value );
    }

    /**
     * Retrieve the "height" property
     */
    public String getHeight() throws Jaxception
    {
        return getProperty( "height" );
    }

    /**
     * Set the "marginheight" property
     */
    public void setMarginheight( int value ) throws Jaxception
    {
        setProperty( "marginheight", value );
    }

    /**
     * Retrieve the "marginheight" property
     */
    public int getMarginheight() throws Jaxception
    {
        return getPropertyInt( "marginheight" );
    }

    /**
     * Set the "marginwidth" property
     */
    public void setMarginwidth( int value ) throws Jaxception
    {
        setProperty( "marginwidth", value );
    }

    /**
     * Retrieve the "marginwidth" property
     */
    public int getMarginwidth() throws Jaxception
    {
        return getPropertyInt( "marginwidth" );
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
     * Set the "src" property
     */
    public void setSrc( String value ) throws Jaxception
    {
        setProperty( "src", value );
    }

    /**
     * Retrieve the "src" property
     */
    public String getSrc() throws Jaxception
    {
        return getProperty( "src" );
    }

    /**
     * Set the "longdesc" property
     */
    public void setLongdesc( String value ) throws Jaxception
    {
        setProperty( "longdesc", value );
    }

    /**
     * Retrieve the "longdesc" property
     */
    public String getLongdesc() throws Jaxception
    {
        return getProperty( "longdesc" );
    }

    /**
     * Set the "width" property
     */
    public void setWidth( String value ) throws Jaxception
    {
        setProperty( "width", value );
    }

    /**
     * Retrieve the "width" property
     */
    public String getWidth() throws Jaxception
    {
        return getProperty( "width" );
    }

    /**
     * Set the "scrolling" property
     */
    public void setScrolling( String value ) throws Jaxception
    {
        setProperty( "scrolling", value );
    }

    /**
     * Retrieve the "scrolling" property
     */
    public String getScrolling() throws Jaxception
    {
        return getProperty( "scrolling" );
    }

}

