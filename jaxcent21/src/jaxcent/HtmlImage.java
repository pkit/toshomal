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
 * Module Name:           HtmlImage.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Class HtmlImage corresponds to IMG tags.
 *
 * Change History:
 *
 *    12/30/2007-8/22/2008  MP      Initial versions.
 *
 */


package jaxcent;

import java.util.Vector;

/**
  * The class HtmlImage corresponds to IMG tags on the page.
  */

public class HtmlImage extends JaxcentHtmlElement {

    /**
      * Search for HTML element on page by specified ID
      */
    public HtmlImage( JaxcentPage page, String id )
    {
        super( page, SearchType.searchById, id, 0 );
    }

    /**
      * Search for HTML element on page by specified search type and search string.
      * If the search returns multiple objects, use the first one.
      */

    public HtmlImage( JaxcentPage page, SearchType searchType, String str )
    {
        super( page, searchType, str, 0 );
    }

    /**
      * Search for HTML Element on page by specified search type and search string, and search index.
      * The search is expected to return multiple results.  The search index is 0-based,
      * and specifies the index in the multiple results.  This constructor
      * is not for use with createNew.
      */

    public HtmlImage( JaxcentPage page, SearchType searchType, String str, int index )
    {
        super( page, searchType, str, index );
    }

    /**
      * Create new HTML Element on page using the specified tag.  Search type must be createNew and tag must be "IMG".
      * If text is non null, the new element is populated with that text.
      */

    public HtmlImage( JaxcentPage page, SearchType searchType, String tag, String text ) throws Jaxception
    {
        super( page, searchType, tag, text );
    }

    /**
      * Create new HTML element on page using the specified attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlImage( JaxcentPage page, SearchType searchType, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, "IMG", attributes, values );
    }

    /**
      * Create new HTML element on page using the specified text and attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlImage( JaxcentPage page, SearchType searchType, String text, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, "IMG", text, attributes, values );
    }

    String getExpectedTag()
    {
        return "IMG";
    }

    void getEvents( Vector events )
    {
        super.getEvents( events );
        checkEvent( events, "click", "onClick", HtmlImage.class );
        checkEvent( events, "focus", "onFocus", HtmlImage.class );
        checkEvent( events, "blur", "onBlur", HtmlImage.class );
        checkEvent( events, "load", "onLoad", HtmlImage.class );
        checkEvent( events, "complete", "onComplete", HtmlImage.class );
        checkEvent( events, "error", "onError", HtmlImage.class );
        checkEvent( events, "abort", "onAbort", HtmlImage.class );
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
     * Override to handle the "load" event
     */
    protected void onLoad()
    {
    }

    /**
     * Override to handle the "complete" event
     */
    protected void onComplete()
    {
    }

    /**
     * Override to handle the "error" event
     */
    protected void onError()
    {
    }

    /**
     * Override to handle the "abort" event
     */
    protected void onAbort()
    {
    }



    private class ImageRenderer extends Thread {
        java.util.HashMap imageList;
        java.awt.image.RenderedImage image;
        String type;
        String key;

        ImageRenderer( java.util.HashMap il, java.awt.image.RenderedImage img, String t, String k )
        {
            imageList = il;
            image = img;
            type = t;
            key = k;
        }

        public void run()
        {
            try {
                synchronized( imageList )
                {
                    java.io.ByteArrayOutputStream bio = new java.io.ByteArrayOutputStream();
                    javax.imageio.ImageIO.write( image, type, bio );
                    bio.flush();
                    imageList.put( key + ".data", bio.toByteArray());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                imageList.put( key + ".data", new byte[]{ 0 } );
            }
        }
    }

    /**
      * Set Image data from a RenderedImage (implementing class, e.g. BufferedImage.)
      * Type must be one of "jpeg" or "png".
      */

    public void setSrc( java.awt.image.RenderedImage image, String type ) throws Jaxception
    {
        if ( image == null )
            throw new NullPointerException();
        String contentType;
        String ext;
        if ( type.equalsIgnoreCase( "jpg" ) || type.equalsIgnoreCase( "jpeg" ) || type.equalsIgnoreCase( "jpe" )) {
            type = "jpeg";
            contentType = "image/jpeg";
            ext = ".jpeg";
        } else if ( type.equalsIgnoreCase( "png" )) {
            type = "png";
            contentType = "image/png";
            ext = ".png";
        } else
            throw new Jaxception( "Only supported types are \"jpeg\" and \"png\"" );
        java.util.HashMap imageList = page.getImageList();
        String key = page.setImageData( this, contentType, null );
        new ImageRenderer( page.getImageList(), image, type, key ).start();
        page.setImageDataSrc( this, ext, key );
    }

    /**
      * Set Image data from bytes.  ContentType should be an image content type
      * such as image/jpeg, image/gif, image/png etc, and the
      * data must be in a matching correct format.
      */

    public void setSrc( byte[] imageData, String imageContentType ) throws Jaxception
    {
        if ( imageData == null || imageContentType == null || imageContentType.equals( "" ))
            throw new NullPointerException();
        page.setImageDataSrc( this, "", page.setImageData( this, imageContentType, imageData ));
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
     * Checks if loading is complete
     */
    public boolean getComplete() throws Jaxception
    {
        return getPropertyBool( "complete" );
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
     * Retrieve the "isMap" property
     */
    public boolean getIsMap() throws Jaxception
    {
        return getPropertyBool( "isMap" );
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
      * Lose the input focus.
      */
    public void blur() throws Jaxception
    {
        methodCall( 3 );
    }

    /**
      * Simulate a mouse click on the HTML element.
      */
    public void click() throws Jaxception
    {
        methodCall( 1 );
    }

    /**
      * Get the input focus.
      */
    public void focus() throws Jaxception
    {
        methodCall( 2 );
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

