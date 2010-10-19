package sample;

import jaxcent.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;

/**
  * Jaxcent dynamic image drawing sample.
  *
  * Draws the locus of equation
  *
  *    X = R cos( n Theta ) cos( Theta )
  *    Y = R cos( n Theta ) sin( Theta )
  *
  */

public class ImageDrawSample2 extends jaxcent.JaxcentPage {

    // The IMG tag to write to.

    HtmlImage theImage = new HtmlImage( this, "image" );

    // The "Draw Picture" button.
    HtmlButton buildButton = new HtmlButton( this, "build" ) {
        protected void onClick( java.util.Map data )
        {
            drawImage( data );
        }
    };

    // A constructor to initialize the image is not needed in this case,
    // because this time we are initializing the image with a PlaceHolder.jpeg
    // image, which is just a small white square.


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

            String sNum = (String) data.get( "n" );
            String bgcolor = (String) data.get( "bgcolor" );
            String fgcolor = (String) data.get( "fgcolor" );
            String sRadius = (String) data.get( "radius" );
            if ( missing( bgcolor, "Background Color" ) ||
                 missing( fgcolor, "Foreground Color" ) ||
                 badInt( sRadius, "Radius", 80, 200 ) ||
                 badInt( sNum, "Value of N", 2, 20 ))
                return;

            if ( bgcolor.equals( fgcolor )) {
                showMessageDialog( "Background and foreground colors are the same!" );
                return;
            }

            // Everything ok, build an image.

            int radius = Integer.parseInt( sRadius );
            int size = ( radius * 22 ) / 10;
            BufferedImage img = new BufferedImage( size, size, BufferedImage.TYPE_INT_RGB );
            Graphics2D gfx = img.createGraphics();
            Color bg = Color.decode( getSelectValue( bgcolor ));
            Color fg = Color.decode( getSelectValue( fgcolor ));
            gfx.setColor( bg );
            gfx.fillRect( 0, 0, size, size );
            gfx.setColor( fg );

            // Draw locus of COS N * theta for theta going from 0 to 2 * PI

            int nDivs = 200;
            float pi = 3.1415926523F;
            float inc = 2.0f * pi / nDivs;

            int prevX = 0;
            int prevY = 0;
            float theta = 0.0f;
            int cx = size / 2;
            int cy = size / 2;
            int n = Integer.parseInt( sNum );
            for ( int i = 0; i < nDivs+1; i++ ) {
                int x = (int) ( (float) radius * Math.cos( n * theta ) * Math.cos( theta ));
                int y = (int) ( (float) radius * Math.cos( n * theta ) * Math.sin( theta ));
                if ( i > 0 )
                    gfx.drawLine( cx + prevX, cy + prevY, cx + x, cy + y );
                prevX = x;
                prevY = y;
                theta += inc;
            }

            // Set the IMG tag to this image data, using the JPEG format.

            theImage.setSrc( img, "jpeg" );

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
