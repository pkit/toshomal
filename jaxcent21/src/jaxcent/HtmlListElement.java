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
 * Module Name:           HtmlListElement.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Class HtmlListElement corresponds to LI tags.
 *
 * Change History:
 *
 *    12/30/2007-8/22/2008  MP      Initial versions.
 *
 */


package jaxcent;

import java.util.Vector;

/**
  * The class HtmlListElement corresponds to LI tags on the page.
  */

public class HtmlListElement extends JaxcentHtmlElement {

    /**
      * Search for HTML element on page by specified ID
      */
    public HtmlListElement( JaxcentPage page, String id )
    {
        super( page, SearchType.searchById, id, 0 );
    }

   /**
     * First child of an OL.
     */
   public HtmlListElement( JaxcentPage page, HtmlNumberedList ol )
   {
       super( page, SearchType.SEARCH_FIRST_LI, "LI", 0, null, null, null, ol, null, null, null );
   }

   /**
     * First child of an UL.
     */
   public HtmlListElement( JaxcentPage page, HtmlUnnumberedList ul )
   {
       super( page, SearchType.SEARCH_FIRST_LI, "LI", 0, null, null, null, ul, null, null, null );
   }

   /**
     * First child of an HtmlElement that is an UL or a OL.
     */
   public HtmlListElement( JaxcentPage page, HtmlElement list )
   {
       super( page, SearchType.SEARCH_FIRST_LI, "LI", 0, null, null, null, list, null, null, null );
   }

   /**
     * Next sibling of a LI.
     */
   public HtmlListElement( JaxcentPage page, HtmlListElement li )
   {
       super( page, SearchType.SEARCH_NEXT_LI, "LI", 0, null, null, null, li, null, null, null );
   }

   /**
     *
     * Check if next sibling exists.
     */
   public boolean hasNextSibling() throws Jaxception
   {
       return page.checkElementExists( SearchType.SEARCH_NEXT_LI, null, elementIndex );
   }

    /**
      * Search for HTML element on page by specified search type and search string.
      * If the search returns multiple objects, use the first one.  If the
      * searchType is createNew, the third parameter specifies contained text.
      */

    public HtmlListElement( JaxcentPage page, SearchType searchType, String str )
    {
        super( page, searchType, str, 0 );
    }

    int createNewType() { return 2; } // 2 == inner text for empty ctor.

    /**
      * Search for HTML Element on page by specified search type and search string, and search index.
      * The search is expected to return multiple results.  The search index is 0-based,
      * and specifies the index in the multiple results.  This constructor
      * is not for use with createNew.
      */

    public HtmlListElement( JaxcentPage page, SearchType searchType, String str, int index )
    {
        super( page, searchType, str, index );
    }

    /**
      * Create new HTML Element on page using the specified tag.  Search type must be createNew and tag must be "LI".
      * If text is non null, the new element is populated with that text.
      */

    public HtmlListElement( JaxcentPage page, SearchType searchType, String tag, String text ) throws Jaxception
    {
        super( page, searchType, tag, text );
    }

    /**
      * Create new HTML element on page using the specified attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlListElement( JaxcentPage page, SearchType searchType, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, "LI", attributes, values );
    }

    /**
      * Create new HTML element on page using the specified text and attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlListElement( JaxcentPage page, SearchType searchType, String text, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, "LI", text, attributes, values );
    }

    String getExpectedTag()
    {
        return "LI";
    }

    void getEvents( Vector events )
    {
        super.getEvents( events );
        checkEvent( events, "click", "onClick", HtmlListElement.class );
        checkEvent( events, "focus", "onFocus", HtmlListElement.class );
        checkEvent( events, "blur", "onBlur", HtmlListElement.class );
        checkEvent( events, "mousedown", "onMouseDown", HtmlListElement.class );
        checkEvent( events, "mouseup", "onMouseUp", HtmlListElement.class );
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
     * Override to handle the "mouseDown" event
     */
    protected void onMouseDown()
    {
    }

    /**
     * Override to handle the "mouseUp" event
     */
    protected void onMouseUp()
    {
    }


    /**
     * Set the "innerHTML" property
     */
    public void setInnerHTML( String value ) throws Jaxception
    {
        setProperty( "innerHTML", value );
    }

    /**
     * Retrieve the "innerHTML" property
     */
    public String getInnerHTML() throws Jaxception
    {
        return getProperty( "innerHTML" );
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

