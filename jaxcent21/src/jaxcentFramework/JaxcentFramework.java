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
 * Module Name:           JaxcentFramework.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Main framework, for use by servlets, connectors, etc.
 *
 * Change History:
 *
 *     1/19/2008  MP      Initial version.  Separated code out from
 *                        the initial version of servlet.
 *
 */

package jaxcentFramework;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

public class JaxcentFramework {

    JaxcentHttpMain httpMain;

    static final String version = "2.1.1";  // A copy is also maintained in JaxcentPage.java

    JaxcentContext context;

    String configPath = null;
    String configError = null;
    boolean displayMessage = true;
    long keepAliveTimeout = 145000L; // 145 seconds is default keep-alive
    long connectionLostTimeout = 4 * keepAliveTimeout;
    long queryTimeout = 20000L;
    boolean refreshUrlMap = true;
    String classpath = null;

    static byte[] empty = "".getBytes();

    public JaxcentFramework( JaxcentHttpMain main )
    {
        this.httpMain = main;
    }

    void loadContext()
    {
        context = httpMain.getJaxcentContext();
        if ( context == null ) {
            configError = null;
            context = new JaxcentContext( configPath, this );
            configError = context.getConfigError();
            if ( configError != null ) {
                context = null;
            } else {
                httpMain.setJaxcentContext( context );
            }
        }

    }

    public void init()
    {
        // Load params.
        String str = httpMain.getConfigItem( "DisplayServletMessage" );
        if ( str != null && str.equalsIgnoreCase( "false" ))
            displayMessage = false;

        str = httpMain.getConfigItem( "RefreshURLMap" );
        if ( str != null && str.equalsIgnoreCase( "false" ))
            refreshUrlMap = false;

        str = httpMain.getConfigItem( "KeepAliveTimeout" );
        if ( str != null ) try {
            int kat = Integer.parseInt( str );
            if ( kat >= 1000 ) {
                configError = "Jaxcent Configuration Error: \"KeepAliveTimeout\" cannot be larger than 1000 seconds.";
                return;
            }
            keepAliveTimeout = kat * 1000L;
        } catch (Exception ex) {
            configError = "Jaxcent Configuration Error: \"KeepAliveTimeout\" must be an integer, found \"" + str + "\"";
            return;
        }

        str = httpMain.getConfigItem( "ConnectionLostTimeout" );
        if ( str == null ) {
            connectionLostTimeout = keepAliveTimeout * 5;
        } else try {
            int clt = Integer.parseInt( str );
            int limit = (int) ( keepAliveTimeout / 100 );
            if ( clt >= limit ) {
                configError = "Jaxcent Configuration Error: \"ConnectionLostTime\" cannot be larger than 10 x KeepAliveTimeout (= " + limit + " seconds.)";
                return;
            }
            connectionLostTimeout = clt * 1000L;
        } catch (Exception ex) {
            configError = "Jaxcent Configuration Error: \"ConnectionLostTimeout\" must be an integer, found \"" + str + "\"";
            return;
        }

        str = httpMain.getConfigItem( "ClientResponseTimeout" );
        if ( str != null ) try {
            int crt = Integer.parseInt( str );
            if ( crt >= 45 ) {
                configError = "Jaxcent Configuration Error: \"ClientResponseTimeout\" cannot be larger than 45 seconds.)";
                return;
            }
            queryTimeout = crt * 1000L;
        } catch (Exception ex) {
            configError = "Jaxcent Configuration Error: \"ConnectionLostTimeout\" must be an integer, found \"" + str + "\"";
            return;
        }

        classpath = httpMain.getConfigItem( "ReloadableClasses" );

        // Locate the config file, either in server config or in context.
        configPath = httpMain.getConfigItem( "JaxcentConfigXML" );
        if ( configPath == null ) {
            configError = "Jaxcent Configuration Error: \"JaxcentConfigXML\" is not configured in application or servlet parameters.";
            return;
        }
        loadContext();
    }

    String encode( String str )
    {
        try {
            return java.net.URLEncoder.encode( str, "UTF-8" );
        } catch (Exception ex) { ex.printStackTrace(); }
        return "";
    }

    public byte[] process( Object request, Object responseObj, String ip, String httpAcceptLanguage )
    {
        if ( context != null && ( configError != null || refreshUrlMap )) {
            configError = null;
            context.refresh();
        } else if ( context == null && configPath != null ) {
            configError = null;
            loadContext();
        }

        String url = httpMain.getRequestParameter( request, "url" );
        String conId = httpMain.getRequestParameter( request, "conid" );
        JaxcentConnection conn;

        if ( configError != null && ( url != null || conId != null )) {
            return ("jaxcent&0&21&" + encode( configError )).getBytes();
        }
        if ( url != null ) {
            String jsVersion = httpMain.getRequestParameter( request, "version" );
            String jaxurl = httpMain.getRequestParameter( request, "jaxurl" );
            // Create object for this URL.
            String[] err = new String[1];
            conn = context.newConnection( url, ip, httpAcceptLanguage, request, err );
            if ( conn == null ) {
                return ( "jaxcent&0&21&" + encode( err[0] )).getBytes();
            }
            conn.setClientInfo( jsVersion, jaxurl );
            return ("jaxcent&" + conn.getId() + "&48&" + version + "&" + conn.getInitString()).getBytes();
        }
        String binaryData = httpMain.getRequestParameter( request, "JaxcentBinaryData" );
        if ( binaryData != null ) {
            return getBinaryData( binaryData, getSession( request, false ), responseObj );
        }
        if ( conId == null ) {
            // Are we configured to display a welcome message?  If not, send status 404
            if ( ! displayMessage ) {
                return null;
            }
            String resp = "Jaxcent Servlet, Copyright (C) Desiderata Software 2008\r\n\r\n";
            if ( configError != null ) {
               resp += "There is an error in Jaxcent servlet configuration:\r\n\r\n" + configError;
            } else {
               resp += "The Jaxcent servlet is configured ok.\r\n";
               resp += "  Reloadable Classpath: " + classpath + "\r\n";
               resp += "  Configuration XML File: " + configPath + "\r\n";
               resp += "  Refresh Configuration XML File: " + refreshUrlMap + "\r\n\r\n";
               StringBuffer buffer = new StringBuffer();
               context.printUrlMap( buffer );
               resp += buffer.toString();
            }
            return resp.getBytes();
        }

        // Retrieve connection object.
        conn = context.getConnection( conId );
        if ( conn == null )
            return empty;

        String image = httpMain.getRequestParameter( request, "image" );
        if ( image != null ) {
            Object[] obj = new Object[]{ image, null };
            conn.onImage( obj );
            if ( obj[1] != null )
                httpMain.setContentType( responseObj, (String) obj[1] );
            return (byte[]) obj[0];
        }
        String param = httpMain.getRequestParameter( request, "event" );
        if ( param != null ) {
            conn.onEvent( param );
            return empty;
        }

        param = httpMain.getRequestParameter( request, "formdata" );  // Before unload!
        if ( param != null ) {
            conn.onFormData( param );
        }

        param = httpMain.getRequestParameter( request, "unload" );
        if ( param != null && param.equals( "1" )) {
            context.disconnect( conn );
            return empty;
        }

        param = httpMain.getRequestParameter( request, "load" );
        if ( param != null && param.equals( "1" )) {
            conn.onLoad();
        }
        String[] response = httpMain.getRequestParameterValues( request, "response" );
        // Process any responses.
        if ( response != null && response.length > 0 )
            conn.onResponse( response );
        String[] error = httpMain.getRequestParameterValues( request, "error" );

        param = httpMain.getRequestParameter( request, "formnames" );
        if ( param != null ) {
            conn.onFormNames( param );
        }

        param = httpMain.getRequestParameter( request, "request" );
        if ( param != null ) {
            conn.onPageRequest( param );
            return empty;
        }

        param = httpMain.getRequestParameter( request, "async" );
        if ( param != null && param.equals( "1" )) {
            // Don't chain into the message sequence.
            return empty;
        }

        // Process any responses.
        if ( error != null && error.length > 0 )
            conn.onError( error );
        return ( "jaxcent&" + conn.getId() + "&" + conn.getNextQuery( keepAliveTimeout )).getBytes();
    }

    byte[] getBinaryData( String ref, Object session, Object responseObj )
    {
        try {
            if ( session != null ) {
                Method getAttr = session.getClass().getMethod( "getAttribute", new Class[]{ String.class } );
                Object[] data = (Object[]) getAttr.invoke( session, new Object[]{ ref } );
                if ( data != null ) {
                    httpMain.setContentType( responseObj, (String) data[0] );
                    return (byte[]) data[1];
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        httpMain.setContentType( responseObj, "text/plain" );
        return "Data not found (your session may have expired or been deleted)".getBytes();
    }

    Object[] parseUploadQuery( String queryString )
    {
        StringTokenizer tok = new StringTokenizer( queryString, "&" );
        String conid = null;
        String objid = null;
        while ( tok.hasMoreTokens()) {
            String s = tok.nextToken().toLowerCase();
            if ( s.startsWith( "conid=" )) {
                conid = s.substring( 6 );
            } else if ( s.startsWith( "objid=" )) {
                objid = s.substring( 6 );
            }
        }
        if ( conid == null || objid == null )
            return null;
        // Retrieve connection object.
        JaxcentConnection conn = context.getConnection( conid );
        if ( conn == null )
            return null;
        return new Object[]{ conn, Integer.valueOf( objid ) };
    }

    // Process file uploads
    public Object onUploadInit( String queryString, String contentType, int contentLength )
    {
        Object[] q = parseUploadQuery( queryString );
        if ( q == null )
            return null;
        ((JaxcentConnection) q[0]).onUploadInit( (Integer) q[1], contentType, contentLength );
        return q;
    }

    public boolean onUpload( Object ref, byte[] data, int ofs, int len )
    {
        Object[] q = (Object[]) ref;
        if ( q == null )
            return false;
        return ((JaxcentConnection) q[0]).onUpload( (Integer) q[1], data, ofs, len );
    }

    public byte[] onUploadEnd( Object ref )
    {
        Object[] q = (Object[]) ref;
        if ( q == null )
            return null;
        return ((JaxcentConnection) q[0]).onUploadDone( (Integer) q[1] );
    }

    public long getConnectionLostTimeout()
    {
        return connectionLostTimeout;
    }

    public long getQueryTimeout()
    {
        return queryTimeout;
    }

    public String getClassPath()
    {
        return classpath;
    }

    public ClassLoader getClassLoader()
    {
        return httpMain.getClassLoader();
    }

    public Object getAppContext()
    {
        return httpMain.getAppContext();
    }

    public Object getSession( Object request, boolean create )
    {
        return httpMain.getSession( request, create );
    }

    public synchronized java.util.Hashtable getJaxcentHashtable( Object sessionObj, boolean create )
    {
        return httpMain.getJaxcentHashtable( sessionObj, create );
    }

    public java.io.InputStream getResourceAsStream( String resource )
    {
        return httpMain.getResourceAsStream( resource );
    }
}
