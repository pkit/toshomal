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
 * Module Name:           HtmlObjectTag.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Class HtmlObjectTag corresponds to OBJECT tags.
 *
 * Change History:
 *
 *    12/30/2007-8/22/2008  MP      Initial versions.
 *
 */


package jaxcent;

import java.util.Vector;

/**
  * The class HtmlObjectTag corresponds to OBJECT tags on the page.
  */

public class HtmlObjectTag extends JaxcentHtmlElement {

    /**
      * Search for HTML element on page by specified ID
      */
    public HtmlObjectTag( JaxcentPage page, String id )
    {
        super( page, SearchType.searchById, id, 0 );
    }

    /**
      * Search for HTML element on page by specified search type and search string.
      * If the search returns multiple objects, use the first one.
      */

    public HtmlObjectTag( JaxcentPage page, SearchType searchType, String str )
    {
        super( page, searchType, str, 0 );
    }

    /**
      * Search for HTML Element on page by specified search type and search string, and search index.
      * The search is expected to return multiple results.  The search index is 0-based,
      * and specifies the index in the multiple results.  This constructor
      * is not for use with createNew.
      */

    public HtmlObjectTag( JaxcentPage page, SearchType searchType, String str, int index )
    {
        super( page, searchType, str, index );
    }

    /**
      * Create new HTML Element on page using the specified tag.  Search type must be createNew and tag must be "OBJECT".
      * If text is non null, the new element is populated with that text.
      */

    public HtmlObjectTag( JaxcentPage page, SearchType searchType, String tag, String text ) throws Jaxception
    {
        super( page, searchType, tag, text );
    }

    /**
      * Create new HTML element on page using the specified attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlObjectTag( JaxcentPage page, SearchType searchType, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, "OBJECT", attributes, values );
    }

    /**
      * Create new HTML element on page using the specified text and attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlObjectTag( JaxcentPage page, SearchType searchType, String text, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, "OBJECT", text, attributes, values );
    }

    /**
      *  Check if any PARAM elements exist in the OBJECT tag.
      */

    public boolean checkParamsExist() throws Jaxception
    {
       return page.checkElementExists( SearchType.SEARCH_FIRST_TAG, "PARAM", elementIndex );
    }

    String getExpectedTag()
    {
        return "OBJECT";
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
     * Set the "archive" property
     */
    public void setArchive( String value ) throws Jaxception
    {
        setProperty( "archive", value );
    }

    /**
     * Retrieve the "archive" property
     */
    public String getArchive() throws Jaxception
    {
        return getProperty( "archive" );
    }

    /**
     * Set the "border" property
     */
    public void setBorder( int value ) throws Jaxception
    {
        setProperty( "border", value );
    }

    /**
     * Retrieve the "border" property
     */
    public int getBorder() throws Jaxception
    {
        return getPropertyInt( "border" );
    }

    /**
     * Set the "classid" property
     */
    public void setClassid( String value ) throws Jaxception
    {
        setProperty( "classid", value );
    }

    /**
     * Retrieve the "classid" property
     */
    public String getClassid() throws Jaxception
    {
        return getProperty( "classid" );
    }

    /**
     * Set the "codebase" property
     */
    public void setCodebase( String value ) throws Jaxception
    {
        setProperty( "codebase", value );
    }

    /**
     * Retrieve the "codebase" property
     */
    public String getCodebase() throws Jaxception
    {
        return getProperty( "codebase" );
    }

    /**
     * Set the "data" property
     */
    public void setData( String value ) throws Jaxception
    {
        setProperty( "data", value );
    }

    /**
     * Retrieve the "data" property
     */
    public String getData() throws Jaxception
    {
        return getProperty( "data" );
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
     * Set the "hspace" property
     */
    public void setHspace( int value ) throws Jaxception
    {
        setProperty( "hspace", value );
    }

    /**
     * Retrieve the "hspace" property
     */
    public int getHspace() throws Jaxception
    {
        return getPropertyInt( "hspace" );
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
     * Set the "standby" property
     */
    public void setStandby( String value ) throws Jaxception
    {
        setProperty( "standby", value );
    }

    /**
     * Retrieve the "standby" property
     */
    public String getStandby() throws Jaxception
    {
        return getProperty( "standby" );
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
     * Set the "useMap" property
     */
    public void setUseMap( String value ) throws Jaxception
    {
        setProperty( "useMap", value );
    }

    /**
     * Retrieve the "useMap" property
     */
    public String getUseMap() throws Jaxception
    {
        return getProperty( "useMap" );
    }

    /**
     * Set the "vspace" property
     */
    public void setVspace( int value ) throws Jaxception
    {
        setProperty( "vspace", value );
    }

    /**
     * Retrieve the "vspace" property
     */
    public int getVspace() throws Jaxception
    {
        return getPropertyInt( "vspace" );
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

}

