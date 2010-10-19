package sample;

import jaxcent.*;

/**
  * Jaxcent sample.
  *
  * Writes and updates a "Hello, World" string.
  */

public class HelloWorldSample extends jaxcent.JaxcentPage {

    HtmlDiv hello = new HtmlDiv( this, "helloWorld" );    // Reference to the DIV tag with id "helloWorld"

    Thread helloThread = null;
    boolean update = false;

    // Random number generator to change text properties randomly.
    java.util.Random rand = new java.util.Random();

    // Text properties.
    String[] fontStyles =   { "normal", "italic", "oblique" };
    String[] fontWeights =  { "normal", "bold" };
    String[] colors =       { "red", "green", "orange", "blue", "purple", "black", "maroon", "fuchsia" };
    String[] fontFamilies = { "Script", "Helvetica", "Arial" };

    // Start the thread on page load.

    protected void onLoad() {
        try {
            hello.setInnerHTML( "Hello, World!" );
        } catch (Jaxception jax) {
            jax.printStackTrace();
        }
        update = true;
        helloThread = new Thread() {
            public void run() {
                updateText();
            }
        };
        helloThread.start();
    }

    // Stop the thread on page unloading.

    protected void onUnload()
    {
        update = false;
        helloThread.interrupt();
    }

    // The updater method.

    void updateText()
    {
        try {
            while ( update ) {
                java.awt.Dimension windowSize = getWindowSize();

                int steps = ( windowSize.width - 120 ) / 2;

                int leftPosition = 0;
                int topPosition = 0;
                int fontSize = 8;
                float topFloat = topPosition;
                float topRatio =  (float) ( windowSize.height - 100 ) / (float) ( windowSize.width - 120 );
                float sizeRatio = 92.0F / (float) steps;
                float sizeFloat = fontSize;

                for ( int i = 0; i < 4 * steps; i++ ) {
                    hello.setStyle( "left", leftPosition );
                    hello.setStyle( "top", topPosition );
                    hello.setStyle( "font-size", String.valueOf( fontSize ) + "pX" );
                    if ( i < 2 * steps )
                        leftPosition++;
                    else
                        leftPosition--;
                    if (( i < steps ) || ( i > 2 * steps && i < 3 * steps )) {
                        topFloat += topRatio;
                        sizeFloat += sizeRatio;
                        topPosition = (int) topFloat;
                        fontSize = (int) sizeFloat;
                    } else {
                        topFloat -= topRatio;
                        sizeFloat -= sizeRatio;
                        topPosition = (int) topFloat;
                        fontSize = (int) sizeFloat;
                    }
                    if ( i == 2 * steps ) {
                        setRandomStyles();
                    }
                    Thread.sleep( 15 );
                }
                setRandomStyles();
            }
        } catch (Exception ex) {
            // Exit thread on interrupted exception
        }
    }

    void setRandomStyles() throws Jaxception
    {
        setRandomStyle( "font-style", fontStyles );
        setRandomStyle( "font-weight", fontWeights );
        setRandomStyle( "color", colors );
        setRandomStyle( "font-family", fontFamilies );
    }

    void setRandomStyle( String styleName, String[] values ) throws Jaxception
    {
        hello.setStyle( styleName, values[ rand.nextInt( values.length ) ] );
    }
}
