package sample;

import jaxcent.*;
import java.util.Calendar;

/**
  * Jaxcent sample.
  *
  * Similar to the basic clock sample, but uses images instead of writing text.
  * The "src" property of the images is changed to show the current time at the server.
  */

public class ClockImageSample extends jaxcent.JaxcentPage {

    // The timer object.
    Timer timer = null;

    // A calendar instance to use in the clock.
    Calendar cal = Calendar.getInstance();

    // The image elements

    HtmlImage[] clockImages = {
	new HtmlImage( this, "hh1" ),
	new HtmlImage( this, "hh2" ),
	new HtmlImage( this, "mm1" ),
	new HtmlImage( this, "mm2" ),
	new HtmlImage( this, "ss1" ),
	new HtmlImage( this, "ss2" ),
    };

    // The start and stop buttons.

    HtmlInputButton startClockButton = new HtmlInputButton( this, "startClock" ) {
        protected void onClick()
        {
            onStartButtonClicked();
        }
    };

    // The stop button is not on the page.  It is created on the fly and added the first
    // time the clock is started.  The "SearchType.createNew" directs Jaxcent
    // to create the button instead of searching for it.

    HtmlInputButton stopClockButton = null;

    void onStartButtonClicked()
    {
        try {
            timer = new Timer();                  // Start the timer.
            timer.start();
            startClockButton.setDisabled( true ); // Disable the "Start Clock" button
            if ( stopClockButton == null ) {
                stopClockButton = new HtmlInputButton( this, SearchType.createNew, "Stop Clock" ) {
                    protected void onClick()
                    {
                        onStopButtonClicked();
                    }
                };
                stopClockButton.insertAtEnd( new HtmlForm( this, SearchType.searchByTag, "FORM" ));
            }
            stopClockButton.setDisabled( false ); // Enable the "Stop Clock" button if disabled.
        } catch (Jaxception ex) {
            ex.printStackTrace();
        }
    }

    void onStopButtonClicked()
    {
        try {
            timer.stopTimer();                     // Stop the timer.
            timer = null;
            startClockButton.setDisabled( false ); // Enable the "Start Clock" button
            stopClockButton.setDisabled( true );   // Disable the "Stop Clock" button
            for ( int i = 0; i < clockImages.length; i++ )
                clockImages[i].setSrc( "images/0.jpg" );
        } catch (Jaxception ex) {
            ex.printStackTrace();
        }
    }

    // The timer class implements the runnable that updates the clock once a second.
    class Timer extends Thread {
        boolean stop = false;

        void stopTimer()
        {
            stop = true;
        }

        public void run()
        {
            try {
                while ( ! stop ) {
                    cal.setTimeInMillis( System.currentTimeMillis());
                    int hh = cal.get( Calendar.HOUR );
                    if ( hh == 0 )
                        hh = 12;
                    setTime( 0, 1, hh );
                    setTime( 2, 3, cal.get( Calendar.MINUTE ));
                    setTime( 4, 5, cal.get( Calendar.SECOND ));

		    Thread.sleep( 1000 );
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    // Stop the timer thread on unload
    protected void onUnload()
    {
        if ( timer != null )
            timer.stopTimer();
    }


    // Set the time images.
    void setTime( int tensIndex, int unitsIndex, int value )
    {
        int tens = value / 10;
        int units = value % 10;
        try {
            clockImages[ tensIndex ].setSrc( "images/" + tens + ".jpg" );
            clockImages[ unitsIndex ].setSrc( "images/" + units + ".jpg" );
        } catch (Jaxception ex) {
            ex.printStackTrace();
        }
    }
}
