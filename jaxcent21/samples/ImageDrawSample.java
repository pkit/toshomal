package sample;

import jaxcent.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;

/**
  * Jaxcent dynamic image drawing sample.
  * Draws specified text as a JPEG image.
  */

public class ImageDrawSample extends jaxcent.JaxcentPage {

    // The IMG tag to write to.

    HtmlImage theImage = new HtmlImage( this, "image" );

    // The "Draw Picture" button.
    HtmlButton buildButton = new HtmlButton( this, "build" ) {
        protected void onClick( java.util.Map data )
        {
            drawImage( data );
        }
    };

    public ImageDrawSample()
    {
        try {
            // In the constructor, we initialize the image to an empty image,
            // otherwise it will display the "missing image" symbol.
            BufferedImage img = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB );
            img.setRGB( 0, 0, 0xFFFFFF );
            theImage.setSrc( img, "jpeg" );

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Methods to check input.

    boolean missing( String value, String name )
    {
        if ( value.equals( "" )) {
            showMessageDialog( name + " is missing" );
            return true;
        }
        return false;
    }

    boolean badInt( String value, String name, int min, int max )
    {
        if ( missing( value, name ))
            return true;
        try {
            int ival = Integer.parseInt( value );
            if ( ival < min ) {
                showMessageDialog( name + " is less than " + min );
                return true;
            }
            if ( ival > max ) {
                showMessageDialog( name + " is greater than " + max );
                return true;
            }
            return false;
        } catch (Exception ex) { 
            showMessageDialog( name + " must be an integer" );
            return true;
        }
    }

    // Data coming from SELECT is formatted as "<index>:<value>
    // This method extracts the value.

    String getSelectValue( String value )
    {
        return value.substring( value.indexOf( ':' ) + 1 );
    }

    // The image building method.

    void drawImage( java.util.Map data )
    {
        try {

            // Collect and verify input.

            String fontName = (String) data.get( "font" );
            String bgcolor = (String) data.get( "bgcolor" );
            String textcolor = (String) data.get( "textcolor" );
            String text = (String) data.get( "text" );
            String sWidth = (String) data.get( "width" );
            String sHeight = (String) data.get( "height" );
            String sFontSize = (String) data.get( "fontsize" );
            if ( missing( fontName, "Font Name" ) ||
                 missing( bgcolor, "Background Color" ) ||
                 missing( textcolor, "Text Color" ) ||
                 missing( text, "Text to Draw" ) ||
                 missing( sWidth, "Image Width" ) ||
                 missing( sHeight, "Image Height" ) ||
                 badInt( sWidth, "Image Width", 200, 800 ) ||
                 badInt( sHeight, "Image Height", 80, 200 ) ||
                 badInt( sFontSize, "Font Size", 20, 100 ))
                return;

            if ( bgcolor.equals( textcolor )) {
                showMessageDialog( "Background and text colors are the same!" );
                return;
            }

            // Everything ok, build an image.

            int width = Integer.parseInt( sWidth );
            int height = Integer.parseInt( sHeight );
            BufferedImage img = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
            Graphics2D gfx = img.createGraphics();
            Color bg = Color.decode( getSelectValue( bgcolor ));
            gfx.setColor( Color.white );
            gfx.fillRect( 0, 0, width, height );
            gfx.setColor( bg );
            gfx.fillRoundRect( 0, 0, width, height, 100, 100 );
            gfx.setColor( Color.decode( getSelectValue( textcolor )));
            int style = Font.PLAIN;
            if ( ! data.get( "bold" ).equals( "" )) {
                if ( ! data.get( "italic" ).equals( "" )) {
                    style = Font.BOLD | Font.ITALIC;
                } else
                    style = Font.BOLD;
            } else if ( ! data.get( "italic" ).equals( "" )) {
                style = Font.ITALIC;
            }
            gfx.setFont( new Font( getSelectValue( fontName ), style, Integer.parseInt( sFontSize )));
            int strWidth = gfx.getFontMetrics().stringWidth( text );
            int strHeight = gfx.getFontMetrics().getHeight();
            gfx.drawString( text, ( width - strWidth ) / 2, height / 2 + gfx.getFontMetrics().getDescent());

            // Set the IMG tag to this image data, using the JPEG format.

            theImage.setSrc( img, "jpeg" );

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
