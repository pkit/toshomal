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
 * Module Name:           HtmlTable.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Class HtmlTable corresponds to TABLE tags.
 *
 * Change History:
 *
 *    12/30/2007-8/22/2008  MP      Initial versions.
 *
 */


package jaxcent;

import java.util.Vector;

/**
  * The class HtmlTable corresponds to TABLE tags on the page.
  */

public class HtmlTable extends JaxcentHtmlElement {

    /**
      * Search for HTML element on page by specified ID
      */
    public HtmlTable( JaxcentPage page, String id )
    {
        super( page, SearchType.searchById, id, 0 );
    }

    /**
      * Search for HTML element on page by specified search type and search string.
      * If the search returns multiple objects, use the first one.
      */

    public HtmlTable( JaxcentPage page, SearchType searchType, String str )
    {
        super( page, searchType, str, 0 );
    }

    /**
      * Search for HTML Element on page by specified search type and search string, and search index.
      * The search is expected to return multiple results.  The search index is 0-based,
      * and specifies the index in the multiple results.  This constructor
      * is not for use with createNew.
      */

    public HtmlTable( JaxcentPage page, SearchType searchType, String str, int index )
    {
        super( page, searchType, str, index );
    }

    /**
      * Create new HTML Element on page using the specified tag.  Search type must be createNew and tag must be "TABLE".
      * If text is non null, the new element is populated with that text.
      */

    public HtmlTable( JaxcentPage page, SearchType searchType, String tag, String text ) throws Jaxception
    {
        super( page, searchType, tag, text );
    }

    /**
      * Create new HTML element on page using the specified attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlTable( JaxcentPage page, SearchType searchType, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, "TABLE", attributes, values );
    }

    /**
      * Create new HTML element on page using the specified text and attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlTable( JaxcentPage page, SearchType searchType, String text, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, "TABLE", text, attributes, values );
    }

    String getExpectedTag()
    {
        return "TABLE";
    }

    void getEvents( Vector events )
    {
        super.getEvents( events );
        checkEvent( events, "celledited", "onCellEdited", HtmlTable.class );
        checkEvent( events, "rowdeleted", "onRowDeleted", HtmlTable.class );
        checkEvent( events, "click", "onClick", HtmlTable.class );
        checkEvent( events, "mousedown", "onMouseDown", HtmlTable.class );
        checkEvent( events, "mouseup", "onMouseUp", HtmlTable.class );
        checkEvent( events, "focus", "onFocus", HtmlTable.class );
        checkEvent( events, "blur", "onBlur", HtmlTable.class );
    }

    /**
     * Override to handle cell in-place editing.
     * @see jaxcent.HtmlTable#enableCellEditing
     */
    protected void onCellEdited( int rowIndex, int cellIndex, String oldContent, String newContent )
    {
    }

    /**
     * Override to handle row deletion.
     * @see jaxcent.HtmlTable#addDeleteButtons
     */
    protected void onRowDeleted( int rowIndex )
    {
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
      *  Returns the number of rows in the table.
      */

    public int getNumRows() throws Jaxception
    {
        return getPropertyInt( "rows.length" );
    }

    /**
      * Return the row at the specified index.
      */
    public HtmlTableRow getRow( int index ) throws Jaxception
    {
        return new HtmlTableRow( page, SearchType.SEARCH_TABLE_ROW, index, null, null, null, this, null, null, null );
    }

    /**
      * Delete the row at the specified index.
      */
    public void deleteRow( int index ) throws Jaxception
    {
        page.deleteRow( this, index );
    }

    /**
      * Insert a row in the table at the specified index.  If index is -1, the row is inserted
      * at the end.  The HTML content of the cells of the row are provided
      * as a string array.  Note that the HTML content is "inner" HTML, so the beginning
      * and ending TD tags must not be part of the html.
      */
    public HtmlTableRow insertRow( int index, String[] innerHTML ) throws Jaxception
    {
        return new HtmlTableRow( page, SearchType.CREATE_ROW, index, null, null, null, this, innerHTML, null, null );
    }

    /**
      * Insert a row in the table at the specified index, and set attributes of each
      * new cell as specified.  The attribute and value arrays are two dimensional.
      * Their first dimension must be the same length as the "innerHTML" array.  Their
      * second dimensions must match with each other.
      * To set style values, specify attribute strings starting with "style.", e.g. "style.color".
      */
    public HtmlTableRow insertRow( int index, String[] innerHTML, String[][] attributes, String[][] values ) throws Jaxception
    {
        if ( innerHTML == null || attributes == null || values == null )
            throw new NullPointerException();
        if ( attributes.length != innerHTML.length )
            throw new Jaxception( "The length of innerHTML and attributes arrays do not match" );
        if ( attributes.length != values.length )
            throw new Jaxception( "The length of attributes and values arrays do not match" );
        for ( int i = 0; i < attributes.length; i++ ) {
            if ( attributes[i].length != values[i].length ) {
                throw new Jaxception( "The length of attributes and values array elements[" + i + "] do not match" );
            }
        }
        return new HtmlTableRow( page, SearchType.CREATE_ROW, index, null, null, null, this, innerHTML, attributes, values );
    }


    /**
      * Remove all Rows.  Useful before inserting new rows.
      */
    public void deleteAllRows() throws Jaxception
    {
        methodCall( 13 );
    }

    /**
      * Remove N rows from top.  If N is negative, keep -N (abs N) rows, remove rest from top.
      */
    public void deleteFromTop( int n ) throws Jaxception
    {
        methodCall( 14, n );
    }

    /**
      * Remove N rows from bottom.  If N is negative, keep -N (abs N) rows, remove rest from bottom.
      */
    public void deleteFromBottom( int n ) throws Jaxception
    {
        methodCall( 15, n );
    }

    /**
      * Make table cells editable in-place.  Click events cannot be received on table cells
      * that have been made editable.  Edits are completed by the ENTER key, cancelled by the ESC key.
      * Completed cell edits can be detected by overriding the <CODE>onCellEdited</CODE> method.
      *
      * @param firstRow         First row where cells are to be made editable.  (0-based.  -1 means last row.)
      * @param firstCol         First column where cells are to be made editable.  (0-based.  -1 means last cell.)
      * @param lastRow          Last row where cells are to be made editable.  (0-based.  -1 means last row.)
      * @param lastCol          Last column where cells are to be made editable.  (0-based.  -1 means last cell.)
      * @param doubleClick      False to enable editing on single-click, true to enable editing on double-clicks.
      * @param allowHtmlInput   If true, user can enter HTML input, e.g. <CODE>&LT;B&GT;user-input&LT;/B&GT;</CODE> to enter bold text.  If false, any &LT; &GT; &amp; characters will be converted to escaped sequences.
      * @param editorStyles     Style attributes to be used on the editor. If <CODE>null</CODE> or empty, default of "<CODE>border:&nbsp;1px&nbsp;solid&nbsp;lightblue;&nbsp;backbround-color:&nbsp;lightyellow;</CODE>" will be used.
      * @see jaxcent.HtmlTable#onCellEdited
      */

    public void enableCellEditing( int firstRow, int firstCol, int lastRow, int lastCol,
                                   boolean doubleClick, boolean allowHtmlInput, String editorStyles ) throws Jaxception
    {
        page.setCellEditable( this, true, firstRow, firstCol, lastRow, lastCol, doubleClick, allowHtmlInput, editorStyles );
    }

    /**
      * Disable table cells in-place editing.
      */

    public void disableCellEditing( int firstRow, int firstCol, int lastRow, int lastCol ) throws Jaxception
    {
        page.setCellEditable( this, false, firstRow, firstCol, lastRow, lastCol, false, false, null );
    }


    /**
      * Add <CODE>Delete</CODE> button(s) to table rows.  Row deletes can be detected by
      * overriding the <CODE>onRowDeleted</CODE> method.
      * @param firstRow         First row to add a delete button.  (0-based.  -1 means last row.)
      * @param lastRow          Last row to add a delete button.  (0-based.  -1 means last row.)
      * @param buttonHtml       Text on the button.  Can contain HTML markup.  If <CODE>null</CODE> or empty, defaults to "<CODE>Delete</CODE>".
      * @param buttonStyles     Style attributes to be used on the button. For instance, <CODE>"color:&nbsp;red;&nbsp;font-size:&nbsp;8px;"</CODE>If <CODE>null</CODE> or empty, button is created with standard attributes.
      * @see jaxcent.HtmlTable#onRowDeleted
      */
    public void addDeleteButtons( int firstRow, int lastRow, String buttonHtml, String buttonStyles ) throws Jaxception
    {
        page.addDeleteButtons( this, firstRow, lastRow, buttonHtml, buttonStyles );
    }

    /**
      * Set HTML content of a cell.  Table will be grown if required.
      * Specify rowIndex/cellIndex as -1 to insert past last row/cell.
      */
    public void setCellContent( int rowIndex, int cellIndex, String html ) throws Jaxception
    {
        page.setCellContents( this, rowIndex, cellIndex, new String[][]{ new String[]{ html }});
    }

    /**
      * Replace HTML content of a row, starting at a given cell index.  Table will be grown if required.
      * Specify rowIndex -1 to insert after last row.
      * Specify firstCellIndex as -1 to new cells at end of rows.
      */
    public void setRowContent( int rowIndex, int firstCellIndex, String[] html ) throws Jaxception
    {
        page.setCellContents( this, rowIndex, firstCellIndex, new String[][]{ html });
    }

    /**
      * Replace HTML content of table, starting at a given row/cell index.  Table will be grown if required.
      * Specify firstRowIndex -1 to insert after last row. Specify firstCellIndex as -1 to new cells at end of rows.
      */
    public void setTableContent( int firstRowIndex, int firstCellIndex, String[][] html ) throws Jaxception
    {
        page.setCellContents( this, firstRowIndex, firstCellIndex, html );
    }

    /**
      * Set cell STYLE attributes of specified cells.
      */
    public void setCellStyles( int firstRow, int firstCol, int lastRow, int lastCol, String cellStyleAttributes ) throws Jaxception
    {
        page.setCellAttributes( this, firstRow, firstCol, lastRow, lastCol, cellStyleAttributes );
    }

    /**
      * Retrieve HTML content of a cell.  Specify -1 for last row or cell.
      */
    public String getCellContent( int rowIndex, int cellIndex ) throws Jaxception
    {
        String[][] result = page.getCellContents( this, rowIndex, cellIndex, 1, new int[]{ 1 } );
        return result[0][0];
    }

    /**
      * Retrieve HTML content of a row, starting at a given cellIndex and for a given number of cells.
      * Specify cellIndex 0 and numCells -1 to retrieve entire row.
      */
    public String[] getRowContent( int rowIndex, int cellIndex, int numCells ) throws Jaxception
    {
        if ( cellIndex < 0 || numCells == 0 )
            throw new Jaxception( "Invalid cell range" );
        String[][] result = page.getCellContents( this, rowIndex, cellIndex, 1, new int[]{ numCells } );
        if ( result == null || result.length == 0 )
            return new String[0];
        return result[0];
    }

    /**
      * Retrieve HTML content of multiple rows, starting at a given cellIndex and for the specified number
      * of cells from each row.  numCells can be null, indicating the contents are to be
      * retrieved from each row, starting at cellIndex and until the end of the row.  Otherwise,
      * numCells can be an array of size "numRows", containing the number of cells to
      * be retrieved from each row.
      * Specify numRows -1 to retrieve until end of table,  getCellContent( 0, 0, -1, null )
      * will retrieve contents of the entire table.
      */
    public String[][] getTableContent( int rowIndex, int cellIndex, int numRows, int[] numCells ) throws Jaxception
    {
        return page.getCellContents( this, rowIndex, cellIndex, numRows, numCells );
    }

    /**
      * If cell in-place editing has been enabled on a cell, start in-place cell editing programmatically (instead
      * of upon user click or double-click.)
      */
    public void startCellEdit( int rowIndex, int cellIndex ) throws Jaxception
    {
        page.startCellEdit( this, rowIndex, cellIndex );
    }


    /**
      * Mark table for its data being sent along with form data.
      * This will cause the table data to be included in the
      * data map, as an array of arrays of strings.  The data
      * will be saved using the specified "name" as key.  The name
      * should not conflict with any form names.
      * <P>The data will also be saved in the session, if auto-session-data
      * is set.  However, the "includeInFormData" setting will not be saved and
      * the table will not be automatically populated at next visit.
      * Resetting the "includeInFormData" setting, as well as populating the table
      * from the session, can be done in the page constructor.
      */
    public void includeInFormData( String saveKeyName )
    {
        if ( saveKeyName == null || saveKeyName.equals( "" ))
            throw new NullPointerException();
        page.includeTableInFormData( this, saveKeyName );
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
     * Set the "caption" property
     */
    public void setCaption( String value ) throws Jaxception
    {
        setProperty( "caption", value );
    }

    /**
     * Retrieve the "caption" property
     */
    public String getCaption() throws Jaxception
    {
        return getProperty( "caption" );
    }

    /**
     * Set the "cellPadding" property
     */
    public void setCellPadding( int value ) throws Jaxception
    {
        setProperty( "cellPadding", value );
    }

    /**
     * Retrieve the "cellPadding" property
     */
    public int getCellPadding() throws Jaxception
    {
        return getPropertyInt( "cellPadding" );
    }

    /**
     * Set the "cellSpacing" property
     */
    public void setCellSpacing( int value ) throws Jaxception
    {
        setProperty( "cellSpacing", value );
    }

    /**
     * Retrieve the "cellSpacing" property
     */
    public int getCellSpacing() throws Jaxception
    {
        return getPropertyInt( "cellSpacing" );
    }

    /**
     * Set the "frame" property
     */
    public void setFrame( String value ) throws Jaxception
    {
        setProperty( "frame", value );
    }

    /**
     * Retrieve the "frame" property
     */
    public String getFrame() throws Jaxception
    {
        return getProperty( "frame" );
    }

    /**
     * Set the "rules" property
     */
    public void setRules( String value ) throws Jaxception
    {
        setProperty( "rules", value );
    }

    /**
     * Retrieve the "rules" property
     */
    public String getRules() throws Jaxception
    {
        return getProperty( "rules" );
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

