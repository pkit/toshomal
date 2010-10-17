package sample;

import jaxcent.*;

/**
  * Jaxcent sample.
  *
  * Inserts a new para at and, at each click.
  */

public class ClickMeSample extends jaxcent.JaxcentPage {

    HtmlPara clickPara = new HtmlPara( this, "clicker" ) {    // Reference to the P tag with id "clicker"
        public void onClick() {
            clicked();
        }
    };
    HtmlPara firstPara = new HtmlPara( this, SearchType.searchByTag, "P", 0 );

    int resetCount = 0;

    void clicked() {
        try {
            setBatchUpdates( true );
            HtmlPara newPara = new HtmlPara( this, SearchType.createNew, "P", "You clicked at " + new java.util.Date());

            newPara.insertBefore( clickPara );
            resetCount++;

            if ( resetCount == 3 ) {
                clickPara.insertAfter( firstPara );
                resetCount = 0;
            }

        } catch (Jaxception jax) {
            jax.printStackTrace();
        } finally {
            setBatchUpdates( false );
        }
    }
}
