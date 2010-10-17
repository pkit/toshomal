package sample;

import jaxcent.*;

/**
  * Jaxcent sample.
  *
  * Displays some simulated production counts in a table.  The table
  * is identified by an ID "widgetsTable".
  */

public class WidgetsSample extends jaxcent.JaxcentPage {

    static final int NSECTIONS = 4;

    // The TABLE
    HtmlTable table = new HtmlTable( this, "widgetsTable" );

    // Button for "Refresh Now"
    HtmlButton refreshNowButton = new HtmlButton( this, "refreshNow" ) {
        protected void onClick()
        {
            synchronized ( timerThread ) {
                timerThread.notify();  // Wakeup the timer thread.
            }
        }
    };

    // The timer thread for generating simulated data.  In a real-life situation,
    // the data may be arriving from other sources asynchronously, instead
    // of from a timer!

    Thread timerThread = null;

    boolean running = false;

    // Objects of interest on the page.
    HtmlTableCell[][] tableCells = new HtmlTableCell[3][];

    // We initialize the table cells in the constructor.

    // Note: If there are any constructors specified for a JaxcentPage-derived
    // handler, there must be a public no-arg constructor.

    public WidgetsSample()
    {
        try {

            // Initialize the table cells array.

            for ( int i = 0; i < 3; i++ ) {
                tableCells[i] = new HtmlTableCell[NSECTIONS];
                HtmlTableRow row = table.getRow( 1+i );
                    // The "1+i" is because of the header row.

                for ( int j = 0; j < NSECTIONS; j++ )
                    tableCells[i][j] = row.getCell( 1+j );
            }

            // Start a timer for updates.

            running = true;
            timerThread = new Thread() {
                public void run() {
                    simulateData();
                }
            };
            timerThread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Stop the thread on page unloading.

    protected void onUnload()
    {
        running = false;
        timerThread.interrupt();
    }

    // Data simulation
    void simulateData()
    {
        java.util.Random r = new java.util.Random();

        // Initialize some data.
        int[] productionCounts = new int[NSECTIONS];
        int[] defectiveCounts = new int[NSECTIONS];
        for ( int i = 0; i < NSECTIONS; i++ ) {
            productionCounts[i] = 4000 + r.nextInt( 2000 );
            defectiveCounts[i] = r.nextInt( productionCounts[i] / 4 );
        }
        long lastTime = System.currentTimeMillis();
        while ( running ) {
             // Update the counts proportional to the time elapsed.
             int elapsed = (int)( System.currentTimeMillis() - lastTime );
             if ( elapsed < 200 )
                 elapsed = 200;
             for ( int i = 0; i < NSECTIONS; i++ ) {
                 int prod = r.nextInt( elapsed / 20 );
                 int defective = r.nextInt( prod > 4 ? prod / 4 : 1 );
                 productionCounts[i] += prod;
                 defectiveCounts[i] += defective;
             }
             // Fill the table.
             try {
                 for ( int i = 0; i < NSECTIONS; i++ ) {
                     tableCells[0][i].setInnerText( String.valueOf( productionCounts[i] ));
                     tableCells[1][i].setInnerHTML( "<FONT COLOR=\"red\">" + defectiveCounts[i]  + "</FONT>" );
                       // Just for demo -- the better approach is to set style on the cell in ctor.

                     tableCells[2][i].setInnerText( String.valueOf( productionCounts[i] - defectiveCounts[i] ));
                 }
             } catch (Exception ex) {
                 ex.printStackTrace();
             }
             lastTime = System.currentTimeMillis();
             synchronized (timerThread) {
                try {
                     timerThread.wait( r.nextInt( 5000 )); // Wait upto 5 seconds or until notify
                 } catch (Exception ex) {}
             }
        }
    }
}
