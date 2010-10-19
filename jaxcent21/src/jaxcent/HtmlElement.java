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
 * Module Name:           HtmlElement.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Class HtmlElement corresponds to 0 tags.
 *
 * Change History:
 *
 *    12/30/2007-8/22/2008  MP      Initial versions.
 *
 */


package jaxcent;

import java.util.Vector;

/**
  * The class HtmlElement is a generic catch-all class that can be used for any tags on the page.
  * It provides all methods, properties and events supported by Jaxcent.  However, if the methods or
  * properties are not supported by the actual HTML element on the page, a run time error may occur.
  * If the event is not supported by the HTML element, no callback will come back, and a run
  * time error may also occur.
  */

public class HtmlElement extends JaxcentHtmlElement {

    /**
      * Search for HTML element on page by specified ID
      */
    public HtmlElement( JaxcentPage page, String id )
    {
        super( page, SearchType.searchById, id, 0 );
    }

    /**
      * Search for HTML element on page by specified search type and search string.
      * If the search returns multiple objects, use the first one.
      */

    public HtmlElement( JaxcentPage page, SearchType searchType, String str )
    {
        super( page, searchType, str, 0 );
    }

    /**
      * Search for HTML Element on page by specified search type and search string, and search index.
      * The search is expected to return multiple results.  The search index is 0-based,
      * and specifies the index in the multiple results.  This constructor
      * is not for use with createNew.
      */

    public HtmlElement( JaxcentPage page, SearchType searchType, String str, int index )
    {
        super( page, searchType, str, index );
    }

    /**
      * Locate next sibling, previous sibling, first child, last child, or parent node
      * of "otherElement".
      * Note: DOM structure can have variations between browsers.
      */
    public HtmlElement( JaxcentPage page, JaxcentHtmlElement otherElement, RelationType type )
    {
       super( page, type.relationType, "", 0, null, null, null, otherElement, null, null, null );
    }

    /**
      * Create new HTML Element on page using the specified tag.  Search type must be createNew.
      * If text is non null, the new element is populated with that text.
      */

    public HtmlElement( JaxcentPage page, SearchType searchType, String tag, String text ) throws Jaxception
    {
        super( page, searchType, tag, text );
    }

    /**
      * Create new HTML element on page using the specified attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlElement( JaxcentPage page, SearchType searchType, String tag, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, tag, attributes, values );
    }

    /**
      * Create new HTML element on page using the specified text and attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlElement( JaxcentPage page, SearchType searchType, String tag, String text, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, tag, text, attributes, values );
    }

    public HtmlElement( JaxcentPage page, int elementIndex ) throws Jaxception
    {
        super( page, elementIndex );
    }

   /**
     * Check if next sibling exists.  Applicable to LI elements only.
     */
   public boolean hasNextSibling() throws Jaxception
   {
       return page.checkElementExists( SearchType.SEARCH_NEXT_LI, null, elementIndex );
   }

    /**
      *  Check if any LI elements exist in list.  Applicable to UL or OL only.
      */

    public boolean checkListElementExists() throws Jaxception
    {
       return page.checkElementExists( SearchType.SEARCH_FIRST_LI, null, elementIndex );
    }

    /**
      * Searches for multiple elements by tag-name and/or attribute names.
      * Either of tagName or attributeName can be null.  If tagName is
      * null, searches all elements, otherwise searches elements with that tag.
      * If attributeName is not-null, searches for elements that have that
      * attribute defined.  Upon completion of search, notifies via the
      * callback mechanism.  If attributeName is not-null, also returns
      * an array of values found for that attribute.
      * <P>
      * For best performance, this method can be called in the constructor.
      * The resulting array of HtmlElement's should be processed
      * with batch-updates set to true.
      */

    public static void searchForElements( JaxcentPage page, String tagName, String attributeName, OnMultipleElementSearch onSearch ) throws Jaxception
    {
        page.searchForElements( tagName, attributeName, onSearch );
    }

    String getExpectedTag()
    {
        return "0";
    }

    void getEvents( Vector events )
    {
        super.getEvents( events );
        checkEvent( events, "reset", "onReset", HtmlElement.class );
        checkEvent( events, "mousedown", "onMouseDown", HtmlElement.class );
        checkEvent( events, "abort", "onAbort", HtmlElement.class );
        checkEvent( events, "keydown", "onKeyDown", HtmlElement.class );
        checkEvent( events, "mouseup", "onMouseUp", HtmlElement.class );
        checkEvent( events, "error", "onError", HtmlElement.class );
        checkEvent( events, "select", "onSelect", HtmlElement.class );
        checkEvent( events, "keypress", "onKeypress", HtmlElement.class );
        checkEvent( events, "celledited", "onCellEdited", HtmlElement.class );
        checkEvent( events, "keyup", "onKeyUp", HtmlElement.class );
        checkEvent( events, "blur", "onBlur", HtmlElement.class );
        checkEvent( events, "focus", "onFocus", HtmlElement.class );
        checkEvent( events, "rowdeleted", "onRowDeleted", HtmlElement.class );
        checkEvent( events, "submit", "onSubmit", HtmlElement.class );
        checkEvent( events, "change", "onChange", HtmlElement.class );
        checkEvent( events, "complete", "onComplete", HtmlElement.class );
        checkEvent( events, "load", "onLoad", HtmlElement.class );
        checkEvent( events, "click", "onClick", HtmlElement.class );
    }

    /**
     * Override to handle the "reset" event
     */
    protected void onReset()
    {
    }

    /**
     * Override to handle the "mouseDown" event
     */
    protected void onMouseDown()
    {
    }

    /**
     * Override to handle the "mouseDown" event and receive current element value
     */
    protected void onMouseDown( String value )
    {
    }

    /**
     * Override to handle the "abort" event
     */
    protected void onAbort()
    {
    }

    /**
     * Override to handle the "keyDown" event
     */
    protected void onKeyDown()
    {
    }

    /**
     * Override to handle the "keyDown" event and receive current element value
     */
    protected void onKeyDown( String value )
    {
    }

    /**
     * Override to handle the "mouseUp" event
     */
    protected void onMouseUp()
    {
    }

    /**
     * Override to handle the "mouseUp" event and receive current element value
     */
    protected void onMouseUp( String value )
    {
    }

    /**
     * Override to handle the "error" event
     */
    protected void onError()
    {
    }

    /**
     * Override to handle the "select" event
     */
    protected void onSelect()
    {
    }

    /**
     * Override to handle the "keypress" event
     */
    protected void onKeypress()
    {
    }

    /**
     * Override to handle the "keypress" event and receive current element value
     */
    protected void onKeypress( String value )
    {
    }

    /**
     * Override to handle cell in-place editing.
     * @see jaxcent.HtmlTable#enableCellEditing
     */
    protected void onCellEdited( int rowIndex, int cellIndex, String oldContent, String newContent )
    {
    }

    /**
     * Override to handle the "keyUp" event
     */
    protected void onKeyUp()
    {
    }

    /**
     * Override to handle the "keyUp" event and receive current element value
     */
    protected void onKeyUp( String value )
    {
    }

    /**
     * Override to handle the "blur" event
     */
    protected void onBlur()
    {
    }

    /**
     * Override to handle the "focus" event
     */
    protected void onFocus()
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
     * Override to handle the "submit" event
     */
    protected void onSubmit()
    {
    }

    /**
     * Override to handle the "submit" event and receive current page data
     */
    protected void onSubmit( java.util.Map pageData )
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
     * Override to handle the "complete" event
     */
    protected void onComplete()
    {
    }

    /**
     * Override to handle the "load" event
     */
    protected void onLoad()
    {
    }

    /**
     * Override to handle the "click" event
     */
    protected void onClick()
    {
    }

    /**
     * Override to handle the "click" event and receive current page data
     */
    protected void onClick( java.util.Map pageData )
    {
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
     * Set the "isMap" property
     */
    public void setIsMap( boolean value ) throws Jaxception
    {
        setProperty( "isMap", value );
    }

    /**
     * Retrieve the "isMap" property
     */
    public boolean getIsMap() throws Jaxception
    {
        return getPropertyBool( "isMap" );
    }

    /**
     * Set the "length" property
     */
    public void setLength( int value ) throws Jaxception
    {
        setProperty( "length", value );
    }

    /**
     * Retrieve the "length" property
     */
    public int getLength() throws Jaxception
    {
        return getPropertyInt( "length" );
    }

    /**
     * Set the "rows" property
     */
    public void setRows( int value ) throws Jaxception
    {
        setProperty( "rows", value );
    }

    /**
     * Retrieve the "rows" property
     */
    public int getRows() throws Jaxception
    {
        return getPropertyInt( "rows" );
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
     * 
     */
    public void setDefaultchecked( boolean value ) throws Jaxception
    {
        setProperty( "defaultchecked", value );
    }

    /**
     * Returns whether this checkbox/radiobutton is checked on by default
     */
    public boolean getDefaultchecked() throws Jaxception
    {
        return getPropertyBool( "defaultchecked" );
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
      * Get the input focus.
      */
    public void focus() throws Jaxception
    {
        methodCall( 2 );
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
     * 
     */
    public void setComplete( boolean value ) throws Jaxception
    {
        setProperty( "complete", value );
    }

    /**
     * Checks if loading is complete
     */
    public boolean getComplete() throws Jaxception
    {
        return getPropertyBool( "complete" );
    }

    /**
     * Set the "defaultValue" property
     */
    public void setDefaultValue( String value ) throws Jaxception
    {
        setProperty( "defaultValue", value );
    }

    /**
     * Retrieve the "defaultValue" property
     */
    public String getDefaultValue() throws Jaxception
    {
        return getProperty( "defaultValue" );
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
      * Delete this element.
      */
    public void delete() throws Jaxception
    {
        methodCall( 11 );
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
      * Select the text in the field.
      */
    public void select() throws Jaxception
    {
        methodCall( 8 );
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
     * Set the "method" property
     */
    public void setMethod( String value ) throws Jaxception
    {
        setProperty( "method", value );
    }

    /**
     * Retrieve the "method" property
     */
    public String getMethod() throws Jaxception
    {
        return getProperty( "method" );
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
     * Set the "checked" property
     */
    public void setChecked( boolean value ) throws Jaxception
    {
        setProperty( "checked", value );
    }

    /**
     * Retrieve the "checked" property
     */
    public boolean getChecked() throws Jaxception
    {
        return getPropertyBool( "checked" );
    }

    /**
      * Simulate a mouse click on the HTML element.
      */
    public void click() throws Jaxception
    {
        methodCall( 1 );
    }

    /**
      * Lose the input focus.
      */
    public void blur() throws Jaxception
    {
        methodCall( 3 );
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
     * Set the "defaultSelected" property
     */
    public void setDefaultSelected( boolean value ) throws Jaxception
    {
        setProperty( "defaultSelected", value );
    }

    /**
     * Retrieve the "defaultSelected" property
     */
    public boolean getDefaultSelected() throws Jaxception
    {
        return getPropertyBool( "defaultSelected" );
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
     * Set the "readOnly" property
     */
    public void setReadOnly( boolean value ) throws Jaxception
    {
        setProperty( "readOnly", value );
    }

    /**
     * Retrieve the "readOnly" property
     */
    public boolean getReadOnly() throws Jaxception
    {
        return getPropertyBool( "readOnly" );
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
     * Set the "lowsrc" property
     */
    public void setLowsrc( String value ) throws Jaxception
    {
        setProperty( "lowsrc", value );
    }

    /**
     * Retrieve the "lowsrc" property
     */
    public String getLowsrc() throws Jaxception
    {
        return getProperty( "lowsrc" );
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
      * Reset the form.
      */
    public void reset() throws Jaxception
    {
        methodCall( 10 );
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
     * Set the "action" property
     */
    public void setAction( String value ) throws Jaxception
    {
        setProperty( "action", value );
    }

    /**
     * Retrieve the "action" property
     */
    public String getAction() throws Jaxception
    {
        return getProperty( "action" );
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

    /**
      * Submit the form.
      */
    public void submit() throws Jaxception
    {
        methodCall( 11 );
    }

    /**
     * Sets the maximum number of characters allowed in field
     */
    public void setMaxLength( String value ) throws Jaxception
    {
        setProperty( "maxLength", value );
    }

    /**
     * Returns the maximum number of characters allowed in field
     */
    public String getMaxLength() throws Jaxception
    {
        return getProperty( "maxLength" );
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
      * Bring the element into view, by scrolling if necessary.
      * If top is true, the top of element is brought into view,
      * otherwise the bottom of the element is scrolled into view.
      */

    public void scrollIntoView( boolean top ) throws Jaxception
    {
        methodCall( top ? 4 : 5 );
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

}

