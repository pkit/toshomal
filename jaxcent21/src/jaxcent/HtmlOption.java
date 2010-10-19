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
 * Module Name:           HtmlOption.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Class HtmlOption corresponds to OPTION tags.
 *
 * Change History:
 *
 *    12/30/2007-8/22/2008  MP      Initial versions.
 *
 */


package jaxcent;

import java.util.Vector;

/**
  * The class HtmlOption corresponds to OPTION tags on the page, i.e.
  * to individual elements of SELECT tags.  Objects of this class can also be obtained
  * by calling the getOption method of HtmlSelect objects.  To add new options,
  * call the insertOption method of HtmlSelect.
  */

public class HtmlOption extends JaxcentHtmlElement {

    /**
      * Search for HTML element on page by specified ID
      */
    public HtmlOption( JaxcentPage page, String id )
    {
        super( page, SearchType.searchById, id, 0 );
    }

    /**
      * Search for HTML element on page by specified search type and search string.
      * If the search returns multiple objects, use the first one.
      */

    public HtmlOption( JaxcentPage page, SearchType searchType, String str )
    {
        super( page, searchType, str, 0 );
    }

    /**
      * Search for HTML Element on page by specified search type and search string, and search index.
      * The search is expected to return multiple results.  The search index is 0-based,
      * and specifies the index in the multiple results.  This constructor
      * is not for use with createNew.
      */

    public HtmlOption( JaxcentPage page, SearchType searchType, String str, int index )
    {
        super( page, searchType, str, index );
    }

    HtmlOption( JaxcentPage page, int searchType, int index, String text, String[] attributes, String[] values, JaxcentObject otherElement, String[] subElements, String[][] subAttributes, String[][] subValues ) throws Jaxception
    {
        super( page, searchType, "OPTION", index, text, attributes, values, otherElement, subElements, subAttributes, subValues );
    }

    String getExpectedTag()
    {
        return "OPTION";
    }

    void getEvents( Vector events )
    {
        super.getEvents( events );
    }


    /**
     * Retrieve the "defaultSelected" property
     */
    public boolean getDefaultSelected() throws Jaxception
    {
        return getPropertyBool( "defaultSelected" );
    }

    /**
     * Set the "index" property
     */
    public void setIndex( int value ) throws Jaxception
    {
        setProperty( "index", value );
    }

    /**
     * Retrieve the "index" property
     */
    public int getIndex() throws Jaxception
    {
        return getPropertyInt( "index" );
    }

    /**
      * Delete this element.
      */
    public void delete() throws Jaxception
    {
        methodCall( 11 );
    }

    /**
     * Set the "selected" property
     */
    public void setSelected( boolean value ) throws Jaxception
    {
        setProperty( "selected", value );
    }

    /**
     * Retrieve the "selected" property
     */
    public boolean getSelected() throws Jaxception
    {
        return getPropertyBool( "selected" );
    }

    /**
     * Set the "text" property
     */
    public void setText( String value ) throws Jaxception
    {
        setProperty( "text", value );
    }

    /**
     * Retrieve the "text" property
     */
    public String getText() throws Jaxception
    {
        return getProperty( "text" );
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

}

