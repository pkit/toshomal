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
 * Module Name:           HtmlTableCell.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Class HtmlTableCell corresponds to TD tags.
 *
 * Change History:
 *
 *    12/30/2007-8/22/2008  MP      Initial versions.
 *
 */


package jaxcent;

import java.util.Vector;

/**
  * The class HtmlTableCell corresponds to TD tags on the page, i.e.
  * to cells of tables.  Objects of this class can also be obtained by calling the
  * getCell method of HtmlTableRow objects, or by calling the getCell method
  * of HtmlTable objects.  To create new cells on the page, insert rows in the
  * table, and then retrieve the cells from the new row.  New cells can also
  * be added to rows by calling insertCell method of HtmlTableRow.
  */

public class HtmlTableCell extends JaxcentHtmlElement {

    /**
      * Search for HTML element on page by specified ID
      */
    public HtmlTableCell( JaxcentPage page, String id )
    {
        super( page, SearchType.searchById, id, 0 );
    }

    /**
      * Search for HTML element on page by specified search type and search string.
      * If the search returns multiple objects, use the first one.
      */

    public HtmlTableCell( JaxcentPage page, SearchType searchType, String str )
    {
        super( page, searchType, str, 0 );
    }

    /**
      * Search for HTML Element on page by specified search type and search string, and search index.
      * The search is expected to return multiple results.  The search index is 0-based,
      * and specifies the index in the multiple results.  This constructor
      * is not for use with createNew.
      */

    public HtmlTableCell( JaxcentPage page, SearchType searchType, String str, int index )
    {
        super( page, searchType, str, index );
    }

    /**
     * Locate the 0-based index'th cell in the given row.
     */
    public HtmlTableCell( JaxcentPage page, HtmlTableRow row, int index )
    {
        this( page, SearchType.SEARCH_TABLE_CELL, index, null, null, null, row, null, null, null );
    }

    HtmlTableCell( JaxcentPage page, int searchType, int index, String text, String[] attributes, String[] values, JaxcentObject otherElement, String[] subElements, String[][] subAttributes, String[][] subValues )
    {
        super( page, searchType, "TD", index, text, attributes, values, otherElement, subElements, subAttributes, subValues );
    }

    String getExpectedTag()
    {
        return "TD";
    }

    void getEvents( Vector events )
    {
        super.getEvents( events );
        checkEvent( events, "click", "onClick", HtmlTableCell.class );
        checkEvent( events, "mousedown", "onMouseDown", HtmlTableCell.class );
        checkEvent( events, "mouseup", "onMouseUp", HtmlTableCell.class );
        checkEvent( events, "focus", "onFocus", HtmlTableCell.class );
        checkEvent( events, "blur", "onBlur", HtmlTableCell.class );
    }

    /**
     * Override to handle the "click" event
     */
    protected void onClick()
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
     * Set the "abbr" property
     */
    public void setAbbr( String value ) throws Jaxception
    {
        setProperty( "abbr", value );
    }

    /**
     * Retrieve the "abbr" property
     */
    public String getAbbr() throws Jaxception
    {
        return getProperty( "abbr" );
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
     * Set the "axis" property
     */
    public void setAxis( String value ) throws Jaxception
    {
        setProperty( "axis", value );
    }

    /**
     * Retrieve the "axis" property
     */
    public String getAxis() throws Jaxception
    {
        return getProperty( "axis" );
    }

    /**
     * Set the "cellIndex" property
     */
    public void setCellIndex( int value ) throws Jaxception
    {
        setProperty( "cellIndex", value );
    }

    /**
     * Retrieve the "cellIndex" property
     */
    public int getCellIndex() throws Jaxception
    {
        return getPropertyInt( "cellIndex" );
    }

    /**
     * Set the "colSpan" property
     */
    public void setColSpan( int value ) throws Jaxception
    {
        setProperty( "colSpan", value );
    }

    /**
     * Retrieve the "colSpan" property
     */
    public int getColSpan() throws Jaxception
    {
        return getPropertyInt( "colSpan" );
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
     * Set the "rowSpan" property
     */
    public void setRowSpan( int value ) throws Jaxception
    {
        setProperty( "rowSpan", value );
    }

    /**
     * Retrieve the "rowSpan" property
     */
    public int getRowSpan() throws Jaxception
    {
        return getPropertyInt( "rowSpan" );
    }

    /**
     * Set the "vAlign" property
     */
    public void setVAlign( String value ) throws Jaxception
    {
        setProperty( "vAlign", value );
    }

    /**
     * Retrieve the "vAlign" property
     */
    public String getVAlign() throws Jaxception
    {
        return getProperty( "vAlign" );
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
      * Bring the element into view, by scrolling if necessary.
      * If top is true, the top of element is brought into view,
      * otherwise the bottom of the element is scrolled into view.
      */

    public void scrollIntoView( boolean top ) throws Jaxception
    {
        methodCall( top ? 4 : 5 );
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
      * Delete this element.
      */
    public void delete() throws Jaxception
    {
        methodCall( 11 );
    }

}

