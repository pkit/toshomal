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
 * Module Name:           HtmlUploadForm.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Class HtmlUploadForm handles file uploads.
 *
 * Change History:
 *
 *     7/10/2008  MP      Initial version.
 *
 */


package jaxcent;

import java.util.*;

/**
  * The class HtmlUploadForm extends HtmlForm, and is meant for
  * processing file uploading.  It corresponds to FORM tags.
  * The FORM should contain an INPUT of type FILE, and a SUBMIT button.
  * It may contain other input tags.  The value of these inputs at
  * the time of file upload can be retrieved in "onUploadData".
  * <P>The action, method, encoding type and target of the form
  * are set by Jaxcent on page load, and do not need to be set.
  * (If specified in the FORM tag, these will be over-written.)
  */

public class HtmlUploadForm extends HtmlForm {

    String boundaryString = null;
    byte[] uploadData = null;
    int uploadLen = 0;


    /**
      * Search for HTML element on page by specified ID
      */
    public HtmlUploadForm( JaxcentPage page, String id )
    {
        super( page, id );
        init();
    }

    /**
      * Search for HTML element on page by specified search type and search string.
      * If the search returns multiple objects, use the first one.
      */

    public HtmlUploadForm( JaxcentPage page, SearchType searchType, String str )
    {
        super( page, searchType, str );
        init();
    }

    /**
      * Search for HTML Element on page by specified search type and search string, and search index.
      * The search is expected to return multiple results.  The search index is 0-based,
      * and specifies the index in the multiple results.  This constructor
      * is not for use with createNew.
      */

    public HtmlUploadForm( JaxcentPage page, SearchType searchType, String str, int index )
    {
        super( page, searchType, str, index );
        init();
    }

    /**
      * Create new HTML Element on page using the specified tag.  Search type must be createNew and tag must be "FORM".
      * If text is non null, the new element is populated with that text.
      */

    public HtmlUploadForm( JaxcentPage page, SearchType searchType, String tag, String text ) throws Jaxception
    {
        super( page, searchType, tag, text );
        init();
    }

    /**
      * Create new HTML element on page using the specified attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlUploadForm( JaxcentPage page, SearchType searchType, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, attributes, values );
        init();
    }

    /**
      * Create new HTML element on page using the specified text and attributes and values.
      * Search type must be createNew.  Attributes and values arrays must have
      * the same length.
      */

    public HtmlUploadForm( JaxcentPage page, SearchType searchType, String text, String[] attributes, String[] values ) throws Jaxception
    {
        super( page, searchType, text, attributes, values );
        init();
    }

    /**
      * onUploadStart can be overridden to receive notification when
      * uploads are started.  The totalBytes specifies total number
      * of bytes to be received (including file data, header etc.)
      */
    protected void onUploadStart( int totalBytes )
    {
    }

    /**
      * onUploadProgress can be overridden to receive intermediate notifications
      * of upload progress.  It can be used to display feedback to the user.
      * It indicates "bytesReceived" out of "totalBytes" bytes have been received.
      * "totalSize" includes length of any files, plus header bytes.
      */

    protected void onUploadProgress( int bytesReceived, int totalBytes )
    {
    }

    /**
      * onUploadDone can be overridden to receive notification of
      * upload completion.  The "success" argument is true if the
      * upload was successfully completed.  It is false if the upload
      * was aborted due to any reason.  If "success" is true, this
      * call will be followed by a call to onUploadData.
      */

    protected void onUploadDone( boolean success )
    {
    }

    /**
      * onUploadData is called after the completion of an upload operation.
      * The "params" map contains String values for any data in the form.
      * For file data, it will contain a filename.  The "fileBytes" map contains
      * byte[] values for any file(s) in the form.
      */
    protected void onUploadData( Map params, Map fileBytes )
    {
    }

    
    private void init()
    {
        methodCall( 30 );
    }

    void initUpload( String contentType, int contentLength )
    {
        boundaryString = null;
        uploadData = null;
        // Retrieve the boundary.
        int boundaryAt = contentType.indexOf( "boundary=" );
        if ( boundaryAt < 0 ) {
            return;
        }
        boundaryString = contentType.substring( boundaryAt + 9 );
        uploadData = new byte[ contentLength ];
        uploadLen = 0;
        try {
            onUploadStart( contentLength );
        } catch (Exception ex) {}
    }

    boolean upload( byte[] data, int ofs, int len )
    {
        if (( uploadData == null ) || ( uploadLen + len > uploadData.length ))
            return false;
        System.arraycopy( data, ofs, uploadData, uploadLen, len );
        uploadLen += len;
        
        try {
            onUploadProgress( uploadLen, uploadData.length );
        } catch (Exception ex) {}
        return true;
    }

    void termUpload()
    {
        if ( uploadLen != uploadData.length ) {
            // Transfer was aborted.
            try {
                onUploadDone( false );
            } catch (Exception ex) {}
        } else {
            try {
                onUploadDone( true );
            } catch (Exception ex) {}
            new UploadParser( uploadData, boundaryString ).start();
        }
        uploadData = null;
        boundaryString = null;
    }

    private class UploadParser extends Thread {

        byte[] data;
        String bstr;
        String contentName = null;
        String contentFile = null;
        Map params = new JaxcentPage.IgnorecaseMap( new HashMap( 3 ));
        Map fileBytes = new JaxcentPage.IgnorecaseMap( new HashMap( 3 ));

        UploadParser( byte[] buf, String s )
        {
            data = buf;
            bstr = s;
        }


        boolean boundaryMatch( byte[] buf, byte[] boundary, int index )
        {
            for ( int i = 0; i < boundary.length; i++ ) {
              if ( buf[index+i] != boundary[i] )
                  return false;
            }
            return true;
        }

        void processHeader( byte[] buf, int start, int end )
        {	
            String hdr = new String( buf, start, end-start );
            if ( hdr.toLowerCase().startsWith( "content-disposition:" )) {
                StringTokenizer tokens = new StringTokenizer( hdr.substring( 19 ), ";" );
                while ( tokens.hasMoreTokens()) {
                    String tok = tokens.nextToken().trim();
                    int eq = tok.indexOf( '=' );
                    if ( eq > 0 ) {
                        String name = tok.substring( 0, eq );
                        String value = tok.substring( eq+1 );
                        int vlen = value.length();
                        if ( vlen > 1 && value.charAt(0) == '"' && value.charAt( vlen - 1 ) == '"' ) {
                            value = value.substring( 1, vlen-1 );
                        }
                        if ( name.equalsIgnoreCase( "name" )) {
                            contentName = value;
                        } else if ( name.equalsIgnoreCase( "filename" )) {
                            contentFile = value;
                        }
                    }
                }
            }
        }

        void processBlock( byte[] buf, int start, int end )
        {
            contentName = null;
            contentFile = null;
            // Read lines from the beginning.
            int i = start;
            while ( i < end && buf[i] != '\r' || buf[i+1] != '\n' ) {
                while ( i < end && buf[i] != '\r' || buf[i+1] != '\n' )
                    i++;
                if ( i >= end )
                    return;
                processHeader( buf, start, i );
                i += 2;
                start = i;
            }
            i += 2;
            if ( contentName == null )
                return;
            if ( contentFile == null ) {
                params.put( contentName, new String( buf, i, end-i ));
            } else {
                params.put( contentName, contentFile );
                int len = end - i;
                byte[] b = new byte[ len ];
                System.arraycopy( buf, i, b, 0, len );
                fileBytes.put( contentName, b );
            }
        }

        public void run() {
            // Parse the buffer.
            int i = 0;
            int lastBoundary = -1;
            int blen = bstr.length();
            byte[] boundary = new byte[ blen ];
            System.arraycopy( bstr.getBytes(), 0, boundary, 0, blen );
            if ( data[0] == '-' && data[1] == '-' && boundaryMatch( data, boundary, 2 )) {
                i += blen + 2;
                if ( data[i] == '\r' && data[i+1] == '\n' )
                    i += 2;
                lastBoundary = i;
            }
            while ( i < data.length - blen - 4 ) {
                if ( data[i] == '\r' && data[i+1] == '\n' && data[i+2] == '-' && data[i+3] == '-' ) {
                    // Check for boundary match.
                    if ( boundaryMatch( data, boundary, i+4 )) {
                        if ( lastBoundary != -1 ) {
                            processBlock( data, lastBoundary, i );
                         }
                         i = i + 4 + blen;
                         if ( i >= data.length - blen - 4 )
                             break;
                         if ( data[i] == '-' && data[i+1] == '-' ) {
                             break;
                         }
                         if ( data[i] == '\r' && data[i+1] == '\n' )
                             i += 2;
                         lastBoundary = i;
                    }
                }
                i++;
            }
            onUploadData( params, fileBytes );
        }
    }
}
