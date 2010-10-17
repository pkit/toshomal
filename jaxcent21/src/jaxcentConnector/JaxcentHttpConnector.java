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
 * Module Name:           JaxcentHttpConnector.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Generic HTTP connector for Jaxcent framework.
 *
 * Change History:
 *
 *     1/20/2008  MP      Initial version.
 *
 */

package jaxcentConnector;

import java.io.*;
import java.util.*;
import java.net.URLDecoder;
import jaxcentFramework.*;

public class JaxcentHttpConnector implements jaxcentFramework.JaxcentHttpMain
{
    JaxcentFramework framework = new JaxcentFramework( this );
    JaxcentAppContext appctx;
    boolean runSessionThread;
    Thread sessionThread = null;

    String[] initParams;
    String[] initValues;
    static int cookieId = 1;

    static final int SESSION_CHECK_INTERVAL = 60000;

    public JaxcentHttpConnector( String[] params, String[] values )
    {
        initParams = params;
        initValues = values;
        appctx = new JaxcentAppContext( params, values );
    }

    public void init()
    {
        framework.init();
    }

    public void terminate()
    {
        stopSessionThread();
    }

    public static String setLogfile( String logfile )
    {
        try {
            PrintStream out = new PrintStream( new FileOutputStream( logfile, true ));
            System.setErr( out );
            System.setOut( out );
            return null;
        } catch (Exception ex) {
            StackTraceElement[] st = ex.getStackTrace();
            StringBuffer str = new StringBuffer( ex.toString());
            for ( int i = 0; i < st.length; i++ ) {
                str.append( "\r\n" );
                str.append( st[i] );
            }
            return str.toString();
        }
    }

    // Turn httpAccept headers into an array of Locales.
    Locale[] getLocales( String httpAcceptLang )
    {
        if ( httpAcceptLang == null )
            return null;
        httpAcceptLang = httpAcceptLang.trim();
        if ( httpAcceptLang.equals( "" ))
            return null;
        StringTokenizer tok = new StringTokenizer( httpAcceptLang, "," );
        Locale[] ret = new Locale[ tok.countTokens() ];
        float[] quality = new float[ ret.length ];
        int index = 0;
        while ( tok.hasMoreElements()) {
            String str = tok.nextToken().trim();
            int qualIndex = str.indexOf( ';' );
            float q = 1.0f;
            if ( qualIndex > 0 ) {
                q = Float.parseFloat( str.substring( qualIndex+1 ).trim());
                str = str.substring( 0, qualIndex ).trim();
            }
            int insert = index;
            while ( insert > 0 && quality[insert-1] < q ) {
                insert--;
            }
            for ( int j = index; j > insert; j-- ) {
               ret[j] = ret[j-1];
            }
            ret[ insert ] = new Locale( str );
            quality[ insert ] = q;
        }
        return ret;
    }

    // On input, request[0] is the incoming data, request[1] is the cookie header.
    // On output, request[0] is the response data, request[1] is any set-cookie if required.

    public void process( Object[] request, String remoteAddr, String httpAcceptLang )
    {
        try {
            String args = (String) request[0];
            String cookie = (String) request[1];

            if ( cookie != null )
                appctx.setSessionAccessed( cookie );
            // Process args and cookie.
            request[0] = parseArgs( (String) request[0] );
            request[0] = framework.process( request, request, remoteAddr, httpAcceptLang );
            if ( cookie != null && request[1] == cookie )
                request[1] = null;
        } catch (Exception ex) {
            ex.printStackTrace();
            StackTraceElement[] st = ex.getStackTrace();
            StringBuffer str = new StringBuffer( ex.toString());
            for ( int i = 0; i < st.length; i++ ) {
                str.append( "\r\n" );
                str.append( st[i] );
            }
            request[0] = str.toString();
            request[1] = null;
        }
    }

    // Process file uploads
    public Object onUploadInit( String queryString, String contentType, int contentLength )
    {
        return framework.onUploadInit( queryString, contentType, contentLength );
    }

    public boolean onUpload( Object ref, byte[] data, int ofs, int len )
    {
        return framework.onUpload( ref, data, ofs, len );
    }

    public boolean onUpload( Object ref, byte[] data )
    {
        return onUpload( ref, data, 0, data.length );
    }

    public byte[] onUploadEnd( Object ref )
    {
        return framework.onUploadEnd( ref );
    }

    String[] parseArgs( String args )
    {
        if ( args == null)
            return null;

        // Process args and cookie.
        StringTokenizer tok = new StringTokenizer( args, "&" );
        int nArgs = tok.countTokens();
        if ( nArgs <= 0 )
            return null;
        String[] argmap = new String[ nArgs + nArgs ];
        int index = 0;
        while ( tok.hasMoreTokens()) {
            String token = tok.nextToken();
            int cut = token.indexOf( "=" );
            try {
                if ( cut < 0 ) {
                    argmap[ index++ ] = URLDecoder.decode( token, "UTF-8" );
                    argmap[ index++ ] = "";
                } else {
                    argmap[ index++ ] = URLDecoder.decode( token.substring( 0, cut ), "UTF-8" );
                    argmap[ index++ ] = URLDecoder.decode( token.substring( cut+1 ), "UTF-8" );
                }
            } catch (java.io.UnsupportedEncodingException ex) {}
        }
        return argmap;
    }

    public ClassLoader getClassLoader()
    {
        return getClass().getClassLoader();
    }

    public Object getAppContext()
    {
        return appctx;
    }

    synchronized void startSessionThread()
    {
        if ( sessionThread != null )
            return;
        sessionThread = new Thread() {
            public void run() {
                checkSessions();
            }
        };
        runSessionThread = true;
        sessionThread.start();
    }

    void checkSessions()
    {
        while ( runSessionThread ) {
            appctx.checkSessions();
            try {
                Thread.sleep( SESSION_CHECK_INTERVAL );
            } catch (Exception ex) {}
        }
    }

    synchronized void stopSessionThread()
    {
        if ( sessionThread != null ) {
            runSessionThread = false;
            sessionThread.interrupt();
        }
    }

    public Object getSession( Object reqObj, boolean create )
    {
        Object[] request = (Object[]) reqObj;
        String cookie = (String) request[1];
        JaxcentSession session = appctx.getSession( cookie );
        if ( session == null && create ) {
            // Make new cookie.
            cookie = Long.toHexString( System.currentTimeMillis()) + "_" + Integer.toHexString( cookieId++ );
            session = appctx.createSession( cookie );
            // Create new session.
            request[1] = cookie;
            startSessionThread();
        }
        return session;
    }

    public synchronized java.util.Hashtable getJaxcentHashtable( Object sessionObj, boolean create )
    {
        if ( sessionObj == null )
            return null;
        JaxcentSession session = (JaxcentSession) sessionObj;
        java.util.Hashtable table = (java.util.Hashtable) session.getAttribute( "jaxcent.SessionData" );
        if ( table == null && create ) {
            table = new java.util.Hashtable();
            session.setAttribute( "jaxcent.SessionData", table );
        }
        return table;
    }

    public JaxcentContext getJaxcentContext()
    {
        return (JaxcentContext) appctx.getAttribute( "jaxcent.context" );
    }

    public void setJaxcentContext( JaxcentContext ctx )
    {
        appctx.setAttribute( "jaxcent.context", ctx );
    }

    public String getConfigItem( String item )
    {
        if ( initParams != null )
            for ( int i = 0; i < initParams.length; i++ )
                if ( item.equalsIgnoreCase( initParams[i] ))
                    return initValues[i];

        return null;
    }

    public String getRequestParameter( Object request, String param )
    {
        if ( request == null )
            return null;
        String[] args = (String[]) ((Object[]) request)[0];
        if ( args == null )
            return null;
        for ( int i = 0; i < args.length; i += 2 ) {
            if ( args[i].equalsIgnoreCase( param ))
                return args[i+1];
        }
        return null;
    }

    public String[] getRequestParameterValues( Object request, String param )
    {
        if ( request == null )
            return null;
        String[] args = (String[]) ((Object[]) request)[0];
        if ( args == null )
            return null;
        int nCount = 0;
        for ( int i = 0; i < args.length; i += 2 ) {
            if ( args[i].equalsIgnoreCase( param )) {
                nCount++;
            }
        }
        if ( nCount == 0 )
            return null;
        String[] ret = new String[ nCount ];
        int index = 0;
        for ( int i = 0; i < args.length; i += 2 ) {
            if ( args[i].equalsIgnoreCase( param )) {
                ret[ index++ ] = args[i+1];
            }
        }
        return ret;
    }

    public void setContentType( Object response, String contentType )
    {
        ((Object[]) response)[2] = contentType;
    }

    public java.io.InputStream getResourceAsStream( String resource )
    {
        return null;
    }
}
