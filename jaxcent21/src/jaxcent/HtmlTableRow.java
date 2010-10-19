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
 * Module Name:           HtmlTableRow.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Class HtmlTableRow corresponds to TR tags.
 *
 * Change History:
 *
 *    12/30/2007-8/22/2008  MP      Initial versions.
 *
 */


package jaxcent;

import java.util.Vector;

/**
  * The class HtmlTableRow corresponds to TR tags on the page, i.e.
  * to rows of tables.  Objects of this class can also be obtained by calling the
  * getRow method of HtmlTable objects.  To add rows to a table on the page,
  * call the insertRow, insertRowText or insertRowHtml methods of HtmlTable.
  */

public class HtmlTableRow extends JaxcentHtmlElement {

    /**
      * Search for HTML element on page by specified ID
      */
    public HtmlTableRow( JaxcentPage page, String id )
    {
        super( page, SearchType.searchById, id, 0 );
    }

    /**
      * Search for HTML element on page by specified search type and search string.
      * If the search returns multiple objects, use the first one.
      */

    public HtmlTableRow( JaxcentPage page, SearchType searchType, String str )
    {
        super( page, searchType, str, 0 );
    }

    /**
      * Search for HTML Element on page by specified search type and search string, and search index.
      * The search is expected to return multiple results.  The search index is 0-based,
      * and specifies the index in the multiple results.
      */

    public HtmlTableRow( JaxcentPage page, SearchType searchType, String str, int index )
    {
        super( page, searchType, str, index );
    }

    /**
     * Locate the 0-based index'th row in the given table.
     */
    public HtmlTableRow( JaxcentPage page, HtmlTable table, int index )
    {
        this( page, SearchType.SEARCH_TABLE_ROW, index, null, null, null, table, null, null, null );
    }

    HtmlTableRow( JaxcentPage page, int searchType, int index, String text, String[] attributes, String[] values, JaxcentObject otherElement, String[] subElements, String[][] subAttributes, String[][] subValues )
    {
        super( page, searchType, "TR", index, text, attributes, values, otherElement, subElements, subAttributes, subValues );
    }

    String getExpectedTag()
    {
        return "TR";
    }

    void getEvents( Vector events )
    {
        super.getEvents( events );
        checkEvent( events, "click", "onClick", HtmlTableRow.class );
        checkEvent( events, "mousedown", "onMouseDown", HtmlTableRow.class );
        checkEvent( events, "mouseup", "onMouseUp", HtmlTableRow.class );
        checkEvent( events, "focus", "onFocus", HtmlTableRow.class );
        checkEvent( events, "blur", "onBlur", HtmlTableRow.class );
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
      *  Returns the number of cells in the row.
      */

    public int getNumCells() throws Jaxception
    {
        return getPropertyInt( "cells.length" );
    }

    /**
      * Return the cell at the specified index.
      */
    public HtmlTableCell getCell( int index ) throws Jaxception
    {
        return new HtmlTableCell( page, SearchType.SEARCH_TABLE_CELL, index, null, null, null, this, null, null, null );
    }

    /**
      * Delete the cell at the specified index.
      */
    public void deleteCell( int index ) throws Jaxception
    {
        page.deleteCell( this, index );
    }

    /**
      * Insert a cell in the table at the specified index.  If index is -1, the row is inserted
      * at the end.  The HTML content of the cell is provided as a string (not containing the TD tags.)
      */
    public HtmlTableCell insertCell( int index, String innerHTML ) throws Jaxception
    {
        return new HtmlTableCell( page, SearchType.CREATE_CELL, index, innerHTML, null, null, this, null, null, null );
    }

    /**
      * Insert a row in the table at the specified index, and set attributes of the new cell
      * as specified.  To set style values, specify attribute strings starting
      * with "style.", e.g. "style.color".
      */

    public HtmlTableCell insertCell( int index, String innerHTML, String[] attributes, String[] values ) throws Jaxception
    {
        if ( attributes.length != values.length )
            throw new Jaxception( "The length of attributes and values arrays do not match" );
        return new HtmlTableCell( page, SearchType.CREATE_CELL, index, innerHTML, attributes, values, this, null, null, null );
    }

    /**
      * Remove all cells.  Useful before inserting new cells.
      */
    public void deleteAllCells() throws Jaxception
    {
        methodCall( 12 );
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
     * Set the "rowIndex" property
     */
    public void setRowIndex( int value ) throws Jaxception
    {
        setProperty( "rowIndex", value );
    }

    /**
     * Retrieve the "rowIndex" property
     */
    public int getRowIndex() throws Jaxception
    {
        return getPropertyInt( "rowIndex" );
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

