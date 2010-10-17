package sample;

import jaxcent.*;
import java.util.*;

/**
  * Jaxcent file upload sample.
  *
  */

public class UploadSample extends jaxcent.JaxcentPage {

    // The upload form.

    HtmlUploadForm uploadForm = new HtmlUploadForm( this, "uploadForm" ) {

        // Override the feedback and file-uploaded methods.

        protected void onUploadStart( int totalBytes )
        {
            try {
                execJavaScriptCode( "progressBarInit()" );
            } catch (Jaxception ex) {
                ex.printStackTrace();
            }
        }

        protected void onUploadProgress( int bytesReceived, int totalBytes )
        {
            int percent = ( bytesReceived * 100 ) / totalBytes;
            try {
                execJavaScriptCode( "progressBarSet( " + percent + " )" );
            } catch (Jaxception ex) {
                ex.printStackTrace();
            }
        }

        protected void onUploadDone( boolean success )
        {
            try {
                execJavaScriptCode( "progressBarDone()" );
            } catch (Jaxception ex) {
                ex.printStackTrace();
            }
        }

        protected void onUploadData( Map params, Map fileBytes )
        {
            try {
                String imageDesc = (String) params.get( "description" );
                byte[] imageBytes = (byte[]) fileBytes.get( "imagefile" );

                // Make sure bytes look like JPEG.
                if ( imageBytes == null || imageBytes.length < 8 ) {
                    showMessageDialog( "A valid file was not received" );
                    return;
                }

                // First two bytes of JPEG files are 0xFF 0xDF
                if ( ((int) imageBytes[0] & 0xFF ) != 0xFF || ((int) imageBytes[1] & 0xFF ) != 0xD8 ) {
                    showMessageDialog( "The uploaded file is not valid JPEG" );
                    return;
                }

                imageDescription.setInnerText( imageDesc );
                theImage.setSrc( imageBytes, "jpeg" );
            } catch (Jaxception ex) {
                ex.printStackTrace();
            }
        }

    };

    // The IMG tag to display the uploaded images to.

    HtmlImage theImage = new HtmlImage( this, "uploadedImage" );

    // Description of the uploaded image.
    HtmlElement imageDescription = new HtmlElement( this, "imageDescription" );
}
