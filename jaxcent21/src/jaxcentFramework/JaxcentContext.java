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
 * Module Name:           JaxcentContext.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Context management for the framework.  Handles class
 *                        reloading, management of class methods, and
 *                        the multiple connection objects.
 *
 * Change History:
 *
 *    12/25/2007  MP      Initial version.
 *     1/19/2008  MP      Separated into jaxcentFramework package.
 *
 */

package jaxcentFramework;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

public class JaxcentContext {
    String configPath;
    File configFile;
    boolean configIsStream;
    long lastmod = -1;
    String configError = null;
    int conid = 101;

    JaxcentClassloader classLoader = null;
    Hashtable urlMap;
    Method eventMethod;
    Method pageLoadMethod;
    Method pageLoadDoneMethod;
    Method pageUnloadMethod;
    Method responseMethod;
    Method errorMethod;
    Method dataMethod;
    Method onFormDataMethod;
    Method autoFormDataMethod;
    Method onPageRequestMethod;
    Method onImageMethod;
    Method onUploadBeginMethod;
    Method onUploadMethod;
    Method onUploadEndMethod;
    Method clientInfoMethod;
    JaxcentFramework framework;

    Hashtable connections = new Hashtable();

    public JaxcentContext( String path, JaxcentFramework fwrk )
    {
        configPath = path;
        framework = fwrk;
	configFile = new File( configPath );
	if ( ! ( configFile.exists() && configFile.canRead())) {
            InputStream in = framework.getResourceAsStream( configPath );
            if ( in != null ) {
                try { in.close(); } catch (Exception ex) {}
                configIsStream = true;
                readConfig();
                return;
            }
        }
        configIsStream = false;
        refresh();
    }

    public void refresh()
    {
        if ( configIsStream || ( lastmod != -1 && lastmod == configFile.lastModified()))
            return;
        if ( configFile.exists() && configFile.canRead())
            lastmod = configFile.lastModified();
        readConfig();
    }

    void readConfig()
    {
        JaxcentConfig config = new JaxcentConfig();
        InputStream in = framework.getResourceAsStream( configPath );
        config.parseConfigFile( configPath, in );
        if ( in != null ) try { in.close(); } catch (Exception ex) {}
        configError = config.getConfigError();
        if ( configError != null )
            return;
        classLoader = new JaxcentClassloader( framework.getClassPath(), framework.getClassLoader());
        urlMap = config.getUrlmap();
        loadClasses();
    }

    boolean displayMessage()
    {
        return true;
    }

    public String getConfigError()
    {
        return configError;
    }

    public void printUrlMap( StringBuffer buffer )
    {
        if ( urlMap == null || urlMap.size() == 0 )
            return;

        try {
            buffer.append( "Current URL Map\r\n" );
            buffer.append( "===============\r\n" );
            buffer.append( "\r\n" );
            for ( Enumeration en = urlMap.keys(); en.hasMoreElements(); ) {
                String path = (String) en.nextElement();
                JaxcentUrlInfo info = (JaxcentUrlInfo) urlMap.get( path );
                buffer.append( "  " );
                buffer.append( info.path );
                for ( int i = info.path.length(); i < 40; i++ )
                    buffer.append( ' ' );
                buffer.append( ' ' );
                buffer.append( info.className );
                if ( info.autoSessionData || info.useSession ) {
                    for ( int i = info.className.length(); i < 40; i++ )
                        buffer.append( ' ' );
                    buffer.append( ' ' );
                    if ( info.autoSessionData )
                        buffer.append( "auto-session-data" );
                    else
                        buffer.append( "use-session" );
                }
                buffer.append( "\r\n" );
                if ( info.err != null ) {
                    buffer.append( "    **** " + info.err + " ****\r\n" );
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public JaxcentConnection newConnection( String path, String remoteAddr, String httpAcceptLanguage, Object request, String[] err )
    {
        String origPath = path;
        // Lookup path in map.
        path = path.trim().toLowerCase();
        JaxcentUrlInfo info = (JaxcentUrlInfo) urlMap.get( path );
        if ( info == null ) {
            int index = path.indexOf( '?' );
            if ( index > 0 ) {
                path = path.substring( 0, index );
                info = (JaxcentUrlInfo) urlMap.get( path );
            }
        }
	if ( info == null ) {
            int index = path.indexOf( ';' );
            if ( index > 0 ) {
                path = path.substring( 0, index );
                info = (JaxcentUrlInfo) urlMap.get( path );
            }
	}
        if ( info == null ) {
            err[0] = "URL " + path + " is not configured in the URL map";
            return null;
        }
        // Check for refreshing
        if (( info.cinfo != null &&
              info.cinfo.classSource != null &&
              info.cinfo.classSource.lastModified() != info.cinfo.timestamp ) ||
             info.cls == null ) {
            // Reload the classes.
            classLoader = new JaxcentClassloader( framework.getClassPath(), framework.getClassLoader());
            loadClasses();
            info = (JaxcentUrlInfo) urlMap.get( path );
        }
        if ( info.err != null ) {
            err[0] = "URL " + path + " Error: " + info.err;
            return null;
        }
        if ( info.cls == null ) {
            err[0] = "No class found for " + path + " in the URL map";
            return null;
        }
        // Instantiate the object, create a new connection and return
        try {
            JaxcentConnection conn = new JaxcentConnection( this, request, info, origPath, remoteAddr, httpAcceptLanguage );
            conn.id = newId( conn );
            return conn;
        } catch (Throwable ex) {
            ex.printStackTrace();
            err[0] = "Error loading Jaxcent Class for " + path + ": " + ex.toString();
        }
        return null;
    }

    synchronized int newId( JaxcentConnection conn )
    {
        connections.put( new Integer( conid ), conn );
        return conid++;
    }

    public JaxcentConnection getConnection( String id )
    {
        try {
            return (JaxcentConnection) connections.get( Integer.valueOf( id ));
        } catch (Exception ex) {
        }
        return null;
    }

    public void disconnect( JaxcentConnection conn )
    {
        conn.onDisconnect();
        connections.remove( new Integer( conn.id ));
    }

    boolean hasDefaultConstructor( Class cls )
    {
        try {
            java.lang.reflect.Constructor ctor = cls.getConstructor( null );
            return ctor != null && ( ctor.getModifiers() & java.lang.reflect.Modifier.PUBLIC ) != 0;
        } catch (NoSuchMethodException ex) {
        }
        return false;
    }

    void loadClasses()
    {
        Class jaxcentPageClass = null;
        try {
            jaxcentPageClass = classLoader.loadClass( "jaxcent.JaxcentPage" );
        } catch (Exception ex) {
            configError = "Jaxcent Configuration Error: Could not load class \"jaxcent.JaxcentPage\": " + ex.toString();
            return;
        }
        try {
            eventMethod = jaxcentPageClass.getDeclaredMethod( "onPageEvent", new Class[]{ Integer.TYPE, String.class } );
            pageLoadMethod = jaxcentPageClass.getDeclaredMethod( "onPageLoad", new Class[]{ Object[].class } );
            responseMethod = jaxcentPageClass.getDeclaredMethod( "onPageResponse", new Class[]{ String.class } );
            errorMethod = jaxcentPageClass.getDeclaredMethod( "onPageError", new Class[]{ String.class } );
            dataMethod = jaxcentPageClass.getDeclaredMethod( "onPageData", new Class[]{ Object.class, Object.class, java.util.Hashtable.class, Boolean.TYPE, String.class, String.class, String.class } );
            pageLoadDoneMethod = jaxcentPageClass.getDeclaredMethod( "onPageLoadDone", null );
            pageUnloadMethod = jaxcentPageClass.getDeclaredMethod( "onPageUnload", null );
            onFormDataMethod = jaxcentPageClass.getDeclaredMethod( "onFormData", new Class[]{ String.class } );
            autoFormDataMethod = jaxcentPageClass.getDeclaredMethod( "autoFormData", new Class[]{ String.class, Hashtable.class } );
            onPageRequestMethod = jaxcentPageClass.getDeclaredMethod( "onPageRequest", new Class[]{ String.class } );
            onImageMethod = jaxcentPageClass.getDeclaredMethod( "onImageRequest", new Class[]{ Object[].class } );
            onUploadBeginMethod = jaxcentPageClass.getDeclaredMethod( "onUploadBegin", new Class[]{ Integer.TYPE, String.class, Integer.TYPE } );
            onUploadMethod = jaxcentPageClass.getDeclaredMethod( "onUpload", new Class[]{ Integer.TYPE, byte[].class, Integer.TYPE, Integer.TYPE } );
            onUploadEndMethod = jaxcentPageClass.getDeclaredMethod( "onUploadEnd", new Class[]{ Integer.TYPE } );
            clientInfoMethod = jaxcentPageClass.getDeclaredMethod( "setClientInfo", new Class[]{ String.class, String.class } );

            eventMethod.setAccessible( true );
            pageLoadMethod.setAccessible( true );
            responseMethod.setAccessible( true );
            errorMethod.setAccessible( true );
            dataMethod.setAccessible( true );
            autoFormDataMethod.setAccessible( true );
            pageLoadDoneMethod.setAccessible( true );
            pageUnloadMethod.setAccessible( true );
            onFormDataMethod.setAccessible( true );
            onPageRequestMethod.setAccessible( true );
            onImageMethod.setAccessible( true );
            onUploadBeginMethod.setAccessible( true );
            onUploadMethod.setAccessible( true );
            onUploadEndMethod.setAccessible( true );
            clientInfoMethod.setAccessible( true );
            Field queryTimeoutField = jaxcentPageClass.getDeclaredField( "QUERY_TIMEOUT" );
            queryTimeoutField.setAccessible( true );
            queryTimeoutField.setLong( null, framework.getQueryTimeout());
        } catch (Throwable ex) {
            configError = "Jaxcent Configuration Error: Class \"jaxcent.JaxcentPage\" is not accessible";
            ex.printStackTrace();
            return;
        }
        Enumeration en = urlMap.keys();
        while ( en.hasMoreElements()) {
            String url = (String) en.nextElement();
            JaxcentUrlInfo info = (JaxcentUrlInfo) urlMap.get( url );
            info.err = null;
            String className = info.className;
            try {
                info.cls = classLoader.loadClass( className, true );
                info.cinfo = classLoader.getClassInfo( className );
                if ( ! jaxcentPageClass.isAssignableFrom( info.cls )) {
                    info.err = "Configured class \"" + className + "\" is not derived from jaxcent.JaxcentPage";
                } else if ( ! hasDefaultConstructor( info.cls )) {
                    info.err = "Configured class \"" + className + "\" does not have a public default constructor (constructor with no args)";
                }
            } catch (Throwable ex) {
                info.err = "Could not load configured class \"" + className + "\": " + ex.toString();
            }
        }
    }
}
