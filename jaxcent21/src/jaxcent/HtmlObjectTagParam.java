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
 * Module Name:           HtmlObjectTagParam.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Class HtmlObjectTagParam corresponds to PARAM tags.
 *
 * Change History:
 *
 *    12/30/2007-8/22/2008  MP      Initial versions.
 *
 */


package jaxcent;

import java.util.Vector;

/**
  * The class HtmlObjectTagParam corresponds to PARAM tags on the page.
  */

public class HtmlObjectTagParam extends JaxcentHtmlElement {

    /**
      * Search for HTML element on page by specified ID
      */
    public HtmlObjectTagParam( JaxcentPage page, String id )
    {
        super( page, SearchType.searchById, id, 0 );
    }

   /**
     * First child of an OBJECT tag.
     */
   public HtmlObjectTagParam( JaxcentPage page, HtmlObjectTag otag )
   {
       super( page, SearchType.SEARCH_FIRST_TAG, "PARAM", 0, null, null, null, otag, null, null, null );
   }

   /**
     * Next sibling of a PARAM inside an OBJECT TAG.
     */
   public HtmlObjectTagParam( JaxcentPage page, HtmlObjectTagParam ptag )
   {
       super( page, SearchType.SEARCH_NEXT_TAG, "PARAM", 0, null, null, null, ptag, null, null, null );
   }

   /**
     *
     * Check if next PARAM sibling exists.
     */
   public boolean hasNextSibling() throws Jaxception
   {
       return page.checkElementExists( SearchType.SEARCH_NEXT_TAG, "PARAM", elementIndex );
   }

    /**
      * Search for HTML element on page by specified search type and search string.
      * If the search returns multiple objects, use the first one.  If the
      * searchType is createNew, the third parameter specifies contained text.
      */

    public HtmlObjectTagParam( JaxcentPage page, SearchType searchType, String str )
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

    public HtmlObjectTagParam( JaxcentPage page, SearchType searchType, String str, int index )
    {
        super( page, searchType, str, index );
    }

    /**
      * Create new HTML Element on page using the specified tag.  Search type must be createNew and tag must be "PARAM".
      * If text is non null, the new element is populated with that text.
      */

    public HtmlObjectTagParam( JaxcentPage page, SearchType searchType, String tag, String text ) throws Jaxception
    {
        super( page, searchType, tag, text );
    }

    /**
      * Create new HTML element on page using the specified attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlObjectTagParam( JaxcentPage page, SearchType searchType, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, "PARAM", attributes, values );
    }

    /**
      * Create new HTML element on page using the specified text and attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlObjectTagParam( JaxcentPage page, SearchType searchType, String text, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, "PARAM", text, attributes, values );
    }

    String getExpectedTag()
    {
        return "PARAM";
    }

    void getEvents( Vector events )
    {
        super.getEvents( events );
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
     * Set the "value" property
     */
    public void setValue( String value ) throws Jaxception
    {
        setProperty( "value", value );
    }

    /**
     * Retrieve the "value" property
     */
    public String getValue() throws Jaxception
    {
        return getProperty( "value" );
    }

    /**
     * Set the "valuetype" property
     */
    public void setValuetype( String value ) throws Jaxception
    {
        setProperty( "valuetype", value );
    }

    /**
     * Retrieve the "valuetype" property
     */
    public String getValuetype() throws Jaxception
    {
        return getProperty( "valuetype" );
    }

}

