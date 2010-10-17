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
 * Module Name:           JaxcentHtmlElement.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Jaxcent base HTML element class.
 *
 * Change History:
 *
 *    12/29/2007  MP      Initial version.
 *
 */

package jaxcent;

import java.util.Vector;

/** This is the base class in Jaxcent for all HTML elements.
  * It provides basic features common to all HTML elements.
  */

public class JaxcentHtmlElement extends JaxcentObject {

    /**
      * Search for HTML element on page by specified ID
      */
    public JaxcentHtmlElement( JaxcentPage page, String id )
    {
        super( page, SearchType.searchById, id, 0 );
    }

    /**
      * Search for HTML element on page by specified search type and search string.
      * If the search returns multiple objects, use the first one.
      * searchType can be createNew, in which case the string parameter is the
      * tag instead.  The surrounding less than and greater than characters
      * must not be a part of the tag. 
      */

    public JaxcentHtmlElement( JaxcentPage page, SearchType searchType, String str )
    {
        super( page, searchType, str, 0 );
    }

    /**
      * Search for HTML Element on page by specified search type and search string, and search index.
      * The search is expected to return multiple results.  The search index is 0-based,
      * and specifies the index in the multiple results.  This constructor
      * is not for use with createNew.
      */

    public JaxcentHtmlElement( JaxcentPage page, SearchType searchType, String str, int index )
    {
        super( page, searchType, str, index );
    }

    /**
      * Create new HTML Element on page using the specified tag.  Search type must be createNew.
      * If text is non null, the new element is populated with that text.
      */

    public JaxcentHtmlElement( JaxcentPage page, SearchType searchType, String tag, String text ) throws Jaxception
    {
        super( page, searchType, tag, text );
    }

    /**
      * Create new HTML element on page using the specified attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public JaxcentHtmlElement( JaxcentPage page, SearchType searchType, String tag, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, tag, attributes, values );
    }

    /**
      * Create new HTML element on page using the specified text and attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public JaxcentHtmlElement( JaxcentPage page, SearchType searchType, String tag, String text, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, tag, text, attributes, values );
    }

    JaxcentHtmlElement( JaxcentPage page, int searchType, String tag, int index, String text, String[] attributes, String[] values, JaxcentObject otherElement, String[] subElements, String[][] subAttributes, String[][] subValues )
    {
        super( page, searchType, tag, index, text, attributes, values, otherElement, subElements, subAttributes, subValues );
    }


    JaxcentHtmlElement( JaxcentPage page, int elementIndex )
    {
        super( page, elementIndex );
    }

    void getEvents( Vector events )
    {
        super.getEvents( events );
        checkEvent( events, "dragdrop", "onDragDrop", JaxcentHtmlElement.class );
    }

    /**
      * Shortcut for setStyle( "display", "none" );
      *
      * @see jaxcent.JaxcentHtmlElement#show
      * @see jaxcent.JaxcentHtmlElement#setVisible
      */
    public void hide() throws Jaxception
    {
        setStyle( "display", "none" );
    }

    /**
      * Shortcut for setStyle( "display", "inline" ); except for
      * P, DIV, FORM, BODY, OL, UL, HR, TABLE etc where it is
      * a shortcut for setStyle( "display", "block" );
      *
      * @see jaxcent.JaxcentHtmlElement#hide
      * @see jaxcent.JaxcentHtmlElement#setVisible
      */
    public void show() throws Jaxception
    {
        String tag = getExpectedTag();
        if ( tag == null ) {
            setStyle( "display", "inline" );
            return;
        }
        if ( tag.equalsIgnoreCase( "P" ) ||
             tag.equalsIgnoreCase( "address" ) ||
             tag.equalsIgnoreCase( "blockquote" ) ||
             tag.equalsIgnoreCase( "dir" ) ||
             tag.equalsIgnoreCase( "div" ) ||
             tag.equalsIgnoreCase( "dl" ) ||
             tag.equalsIgnoreCase( "fieldset" ) ||
             tag.equalsIgnoreCase( "form" ) ||
             tag.equalsIgnoreCase( "h1" ) ||
             tag.equalsIgnoreCase( "h2" ) ||
             tag.equalsIgnoreCase( "h3" ) ||
             tag.equalsIgnoreCase( "h4" ) ||
             tag.equalsIgnoreCase( "h5" ) ||
             tag.equalsIgnoreCase( "h6" ) ||
             tag.equalsIgnoreCase( "hr" ) ||
             tag.equalsIgnoreCase( "isindex" ) ||
             tag.equalsIgnoreCase( "menu" ) ||
             tag.equalsIgnoreCase( "noframes" ) ||
             tag.equalsIgnoreCase( "noscript" ) ||
             tag.equalsIgnoreCase( "ol" ) ||
             tag.equalsIgnoreCase( "pre" ) ||
             tag.equalsIgnoreCase( "table" ) ||
             tag.equalsIgnoreCase( "ul" ) ||
             tag.equalsIgnoreCase( "dd" ) ||
             tag.equalsIgnoreCase( "dt" ) ||
             tag.equalsIgnoreCase( "frameset" ) ||
             tag.equalsIgnoreCase( "li" ) ||
             tag.equalsIgnoreCase( "tbody" ) ||
             tag.equalsIgnoreCase( "tfoot" ))
            setStyle( "display", "block" );
        else
            setStyle( "display", "inline" );
    }

    /**
      * Shortcut for setStyle( "visibility", "visible" or "hidden" );
      *
      * @see jaxcent.JaxcentHtmlElement#hide
      * @see jaxcent.JaxcentHtmlElement#show
      * @see jaxcent.JaxcentHtmlElement#setVisible
      */
    public void setVisible( boolean visible ) throws Jaxception
    {
        setStyle( "visibility", visible ? "visible" : "hidden" );
    }


    /**
      * Shortcut for setProperty( "disabled", ! enabled );
      */
    public void setEnabled( boolean enabled ) throws Jaxception
    {
        setProperty( "disabled", ! enabled );
    }


    /**
      * Set the specified attribute.
      */

    public void setAttribute( String attrName, String value ) throws Jaxception
    {
        if ( attrName == null )
            throw new NullPointerException( "attrName is null" );
        if ( value == null )
            value = "";
        page.addSetAttribute( this, attrName, value );
        page.sendPageQuery();
    }

    /**
      * Retrieve the specified attribute.
      */

    public String getAttribute( String attrName ) throws Jaxception
    {
        if ( attrName == null )
            throw new NullPointerException( "attrName is null" );
        return page.queryGetAttribute( this, attrName );
    }

    private String getStyleName( String name )
    {
        int dashAt = name.indexOf( '-' );
        if ( dashAt <= 0 || dashAt == name.length() - 1 )
            return name;
        return name.substring( 0, dashAt ) + Character.toUpperCase( name.charAt( dashAt+1 )) + name.substring( dashAt+2 );
    }

    /**
      * Set the specified style element.
      */

    public void setStyle( String styleName, String value ) throws Jaxception
    {
        if ( styleName == null )
            throw new NullPointerException( "styleName is null" );
        if ( value == null )
            value = "";
        page.addSetProperty( this, true, false, getStyleName( styleName ), value );
        page.sendPageQuery();
    }


    /**
      * Set the specified style element from an int.
      */

    public void setStyle( String styleName, int value ) throws Jaxception
    {
        setStyle( styleName, String.valueOf( value ));
    }

    /**
      * Set the specified style element from a java.awt.Color value.
      */

    public void setStyle( String styleName, java.awt.Color value ) throws Jaxception
    {
        setStyle( styleName, getColorString( value ));
    }

    /**
      * Set the specified boolean style element.
      */

    public void setStyle( String styleName, boolean value ) throws Jaxception
    {
        if ( styleName == null )
            throw new NullPointerException( "style name is null" );
        page.addSetProperty( this, true, true, getStyleName( styleName ), value ? "1" : "0" );
        page.sendPageQuery();
    }

    /** Retrieve the specified style element.
      */
    public String getStyle( String styleName ) throws Jaxception
    {
        if ( styleName == null )
            throw new NullPointerException( "styleName is null" );
        return page.queryGetProperty( this, true, getStyleName( styleName ));
    }

    /** Sets the CSS class of an element.
      */
    public void setCssClass( String cssClassName ) throws Jaxception
    {
        if ( cssClassName == null )
            throw new NullPointerException( "CSS class name is null" );
        setProperty( "class", cssClassName );
        setProperty( "className", cssClassName );
    }

    /** Returns the tag of the HTML element.  For instance "P" for para,
      * "IMG" for images, "TABLE" for tables, "TD" for table cells, etc.
      */
    public String getTag() throws Jaxception
    {
        return getProperty( "tagName" );
    }

    /** Returns the ID of the HTML element if specified, or null;
      */
    public String getID() throws Jaxception
    {
	return getProperty( "id" );
    }

    /** Returns the inner text of the element.
      */
    public String getInnerText() throws Jaxception
    {
        return page.queryGetInnerText( this );
    }

    /** Sets the inner text of the element.
      */
    public void setInnerText( String text ) throws Jaxception
    {
        if ( text == null )
            text = "";
        page.addSetInnerText( this, text );
        page.sendPageQuery();
    }

    void insertElement( int insertType, JaxcentHtmlElement otherElement ) throws Jaxception
    {
        if ( insertType != 1 && insertType != 2 && otherElement == null )
            throw new NullPointerException( "Target element is null" );
        page.addInsertElement( insertType, this, otherElement );
        page.sendPageQuery();
    }

    /** Insert element at document beginning.
      * This element could have been constructed using createNew,
      * or be an existing element.
      */

    public void insertAtBeginning() throws Jaxception
    {
        insertElement( 1, null );
    }

    /** Insert element at document end.
      * This element could have been constructed using createNew,
      * or be an existing element.
      */

    public void insertAtEnd() throws Jaxception
    {
        insertElement( 2, null );
    }

    /** Insert element just before target element.
      * This element could have been constructed using createNew,
      * or be an existing element.
      */
    public void insertBefore( JaxcentHtmlElement targetElement ) throws Jaxception
    {
        insertElement( 3, targetElement );
    }


    /** Insert element just after target element.
      * This element could have been constructed using createNew,
      * or be an existing element.
      */
    public void insertAfter( JaxcentHtmlElement targetElement ) throws Jaxception
    {
        insertElement( 4, targetElement );
    }

    /** Insert element inside target element, at beginning.
      * This element could have been constructed using createNew,
      * or be an existing element.
      */

    public void insertAtBeginning( JaxcentHtmlElement targetElement ) throws Jaxception
    {
        insertElement( 5, targetElement );
    }

    /** Insert element inside target element, at end.
      * This element could have been constructed using createNew,
      * or be an existing element.
      */

    public void insertAtEnd( JaxcentHtmlElement targetElement ) throws Jaxception
    {
        insertElement( 6, targetElement );
    }

    /** Override to enable element as a drag-and-drop target.
      * Drop sources must be enabled by calling setDraggable.
      */
    protected void onDragDrop( JaxcentHtmlElement dragDropSource )
    {
    }

    /** Call with true argument to enable element as a drag-and-drop source.
      * Call with false to disable dragging.  The target element must
      * have "onDragDrop" overridden.
      */
    public void setDraggable( boolean enable ) throws Jaxception
    {
        methodCall( enable ? 23 : 24 );
    }

    /** Delete element from DOM.
      */
    public void deleteElement() throws Jaxception
    {
        methodCall( 22 );
    }

    /**
     *
     * Check if a node exists with the specified relationship.
     * Relationship can be "next sibling", "previous sibling", "parent node",
     * "first child", or "last child".  Node can be retrieved
     * as a generic HtmlElement.
     */

    public boolean checkNodeExists( RelationType type ) throws Jaxception
    {
       return page.checkElementExists( type.relationType, null, elementIndex );
    }
}
