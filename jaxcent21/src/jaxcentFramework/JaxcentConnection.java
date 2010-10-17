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
 *
 * Module Name:           JaxcentConnection.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Connection object for incoming connections.
 *                        Instantiates Jaxcent page and handles
 *                        data transfers to/from the page.
 *
 * Change History:
 *
 *    12/25/2007  MP      Initial version.
 *     1/19/2008  MP      Separated into jaxcentFramework package.
 *
 *
 */

package jaxcentFramework;

import java.util.*;
import java.lang.reflect.*;

public class JaxcentConnection {
    int id;
    Object page;
    JaxcentContext ctx;
    boolean connected = true;
    boolean autoSession = false;
    Object session = null;

    Object[] syncObject = new Object[]{ new StringBuffer() };
    long[] readerSync = new long[1];
    StringBuffer query = new StringBuffer();

    long lastClientActivity = System.currentTimeMillis();
    static byte[] uploadDone = "Jaxcent File Upload Received".getBytes();

    Thread readerThread = new Thread() {
        public void run() {
            try {
                long readerSyncWait = ctx.framework.getConnectionLostTimeout() / 4;
                    // Loop wakeup interval.
                synchronized ( syncObject ) {
                    while ( connected ) {
                        if ( System.currentTimeMillis() > lastClientActivity + ctx.framework.getConnectionLostTimeout()) {
                            connected = false;
                            ctx.disconnect( JaxcentConnection.this );
                            return;
                        }
                        StringBuffer buffer = (StringBuffer) syncObject[0];
                        if ( buffer.length() > 0 ) {
                            synchronized ( readerSync ) {
                                query.append( buffer );
                                syncObject[0] = new StringBuffer();
                                readerSync.notify();
                            }
                        }
                        syncObject.wait( readerSyncWait );
                    }
                }
            } catch (InterruptedException ex) {
            }
        }
    };

    JaxcentConnection( JaxcentContext ctx, Object requestObject, JaxcentUrlInfo info, String path, String ipAddr, String acceptLanguage )
                throws IllegalAccessException, InstantiationException
    {
        try {
            Object sctx = ctx.framework.getAppContext();
            Hashtable table = null;
            if ( info.useSession || info.autoSessionData ) {
                session = ctx.framework.getSession( requestObject, true );
                if ( info.autoSessionData ) {
                    table = ctx.framework.getJaxcentHashtable( session, true );
                }
            }
            this.ctx = ctx;
            this.autoSession = info.autoSessionData;
            ctx.dataMethod.invoke( null, new Object[]{ session, sctx, table, Boolean.valueOf( info.autoSessionData ), path, ipAddr, acceptLanguage } );
            this.page = info.cls.newInstance();
            readerThread.start();
        } catch (InvocationTargetException ite) {
            if ( ite.getCause() != null )
                ite.getCause().printStackTrace();
            else
                ite.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        lastClientActivity = System.currentTimeMillis();
    }

    public int getId()
    {
        return id;
    }

    public void onDisconnect()
    {
        connected = false;
        try {
            ctx.pageUnloadMethod.invoke( page, null );
        } catch (InvocationTargetException ite) {
            if ( ite.getCause() != null )
                ite.getCause().printStackTrace();
            else
                ite.printStackTrace();
        } catch (Throwable t) {}
        try {
            if ( ! Thread.currentThread().equals( readerThread ))
                readerThread.interrupt();
        } catch (Exception ex) {}
    }

    public void onLoad()
    {
        try {
            ctx.pageLoadDoneMethod.invoke( page, null );
        } catch (InvocationTargetException ite) {
            if ( ite.getCause() != null )
                ite.getCause().printStackTrace();
            else
                ite.printStackTrace();
        } catch (Throwable t) {}
    }

    public void onEvent( String id )
    {
        try {
            int index = id.indexOf( '_' );
            if ( index < 0 )
                ctx.eventMethod.invoke( page, new Object[]{ Integer.valueOf( id ), null });
            else {
                ctx.eventMethod.invoke( page, new Object[]{ Integer.valueOf( id.substring( 0, index )), id.substring( index+1 ) });
            }
        } catch (InvocationTargetException ite) {
            if ( ite.getCause() != null )
                ite.getCause().printStackTrace();
            else
                ite.printStackTrace();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public void onImage( Object[] image )
    {
        try {
            ctx.onImageMethod.invoke( page, new Object[]{ image } );
        } catch (InvocationTargetException ite) {
            if ( ite.getCause() != null )
                ite.getCause().printStackTrace();
            else
                ite.printStackTrace();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public void onFormData( String str )
    {
        try {
            ctx.onFormDataMethod.invoke( page, new Object[]{ str } );
        } catch (InvocationTargetException ite) {
            if ( ite.getCause() != null )
                ite.getCause().printStackTrace();
            else
                ite.printStackTrace();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public void onFormNames( String str )
    {
        if ( session == null )
            return;
        try {
            ctx.autoFormDataMethod.invoke( page, new Object[]{ str, ctx.framework.getJaxcentHashtable( session, false ) } ); // SessionAttribute( "jaxcent.SessionData" ) } );
        } catch (InvocationTargetException ite) {
            if ( ite.getCause() != null )
                ite.getCause().printStackTrace();
            else
                ite.printStackTrace();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public void onPageRequest( String str )
    {
        try {
            ctx.onPageRequestMethod.invoke( page, new Object[]{ str } );
        } catch (InvocationTargetException ite) {
            if ( ite.getCause() != null )
                ite.getCause().printStackTrace();
            else
                ite.printStackTrace();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public void onResponse( String[] resp )
    {
        if ( resp != null ) for ( int i = 0; i < resp.length; i++ ) {
            try {
                ctx.responseMethod.invoke( page, new Object[]{ resp[i] });
            } catch (InvocationTargetException ite) {
                if ( ite.getCause() != null )
                    ite.getCause().printStackTrace();
                else
                    ite.printStackTrace();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    public void onError( String[] resp )
    {
        if ( resp != null ) for ( int i = 0; i < resp.length; i++ ) {
            try {
                ctx.errorMethod.invoke( page, new Object[]{ resp[i] });
            } catch (InvocationTargetException ite) {
                if ( ite.getCause() != null )
                    ite.getCause().printStackTrace();
                else
                    ite.printStackTrace();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getInitString()
    {
        try {
            String initString = (String) ctx.pageLoadMethod.invoke( page, new Object[]{ syncObject });
            if ( autoSession )
                return "1&55&" + initString;
            else
                return "1&" + initString;
        } catch (InvocationTargetException ite) {
            if ( ite.getCause() != null )
                ite.getCause().printStackTrace();
            else
                ite.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    
    public String getNextQuery( long timeout )
    {
       lastClientActivity = System.currentTimeMillis();
       String ret = null;
       synchronized( readerSync ) {
           int len = query.length();
           if ( len == 0 ) {
               long marker = System.currentTimeMillis(); // Queue marker.
               readerSync[0] = marker;
               try {
                   readerSync.wait( timeout );
               } catch (Exception ex) {}
               if ( readerSync[0] != marker )
                   return "";  // We have a newer request, bail out from this one.
                               // The wait queue is LIFO, as earlier requests may have 
                               // timed-out from the client side.
               len = query.length();
               if ( len > 0 ) {
                  // Gather data until it stops changing for 2 ms, but no longer than 5 ms
                  // (That doesn't mean 2.5 or 3 times, it could be several times.)
                  long ms = System.currentTimeMillis() + 5;
                  int olen;
                  do {
                      olen = len;
                      try {
                          readerSync.wait( 2 );
                      } catch (Exception ex) {}
                      len = query.length();
                  } while ( len != olen && System.currentTimeMillis() < ms );
               }
           }
           ret = query.toString();
           query.setLength( 0 );
       }
       if ( ret == null )
           return "";
       return ret;
    }

    public void onUploadInit( Integer objid, String contentType, int contentLength )
    {
        try {
            ctx.onUploadBeginMethod.invoke( page, new Object[]{ objid, contentType, new Integer( contentLength ) } );
        } catch (InvocationTargetException ite) {
            if ( ite.getCause() != null )
                ite.getCause().printStackTrace();
            else
                ite.printStackTrace();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public boolean onUpload( Integer objid, byte[] data, int ofs, int len )
    {
        try {
            return ((Boolean) ctx.onUploadMethod.invoke( page, new Object[]{ objid, data, new Integer( ofs ), new Integer( len ) } )).booleanValue();
        } catch (InvocationTargetException ite) {
            if ( ite.getCause() != null )
                ite.getCause().printStackTrace();
            else
                ite.printStackTrace();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public byte[] onUploadDone( Integer objid )
    {
        try {
            ctx.onUploadEndMethod.invoke( page, new Object[]{ objid } );
        } catch (InvocationTargetException ite) {
            if ( ite.getCause() != null )
                ite.getCause().printStackTrace();
            else
                ite.printStackTrace();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return uploadDone;
    }

    public void setClientInfo( String version, String url )
    {
        try {
            ctx.clientInfoMethod.invoke( page, new Object[]{ version, url } );
        } catch (InvocationTargetException ite) {
            if ( ite.getCause() != null )
                ite.getCause().printStackTrace();
            else
                ite.printStackTrace();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
