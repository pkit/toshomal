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
 * Module Name:           JaxcentAppContext.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Application context class for connectors.
 *
 * Change History:
 *
 *     1/20/2008  MP      Initial version.
 *
 */

package jaxcentConnector;

import java.util.*;

/**
  * This class provides an application context for direct Jaxcent connections
  * to an HTTP server (instead of via a Java application server.)
  */

public class JaxcentAppContext {

    Hashtable table = new Hashtable(5);
    Hashtable initTable = new Hashtable( 5 );
    Hashtable sessions = new Hashtable( 20 );

    int sessionInactivityDefaultTimeout = 600;

    JaxcentAppContext( String[] params, String[] values )
    {
        if ( params != null && values != null ) {
            for ( int i = 0; i < params.length && i < values.length; i++ )
                initTable.put( params[i], values[i] );
        }
        Object str = getAttribute( "SessionInactivityTimeout" );
        if ( str != null && str instanceof String ) try {
            int ival = Integer.parseInt( (String) str );
            if ( ival < 2 )
                ival = 2;
            if ( ival > 7200 )
                ival = 7200;
            sessionInactivityDefaultTimeout = ival;
        } catch (Exception ex) {}
    }

    void setSessionAccessed( String id )
    {
        if ( id == null )
            return;
        JaxcentSession session = (JaxcentSession) sessions.get( id );
        if ( session != null )
            session.setLastAccessedTime();
    }

    void checkSessions()
    {
        Enumeration en = sessions.keys();
        while ( en.hasMoreElements()) {
            String id = (String) en.nextElement();
            JaxcentSession session = (JaxcentSession) sessions.get( id );
            if ( session != null ) {
                if ( session.accessedAt + ( 1000L * session.inactiveInterval ) < System.currentTimeMillis()) {
                    removeSession( id );
                }
            }
        }
    }

    void removeSession( String id )
    {
        sessions.remove( id );
    }

    /**
      * Return object saved at the given attribute name, or null.
      */
    public Object getAttribute( String name )
    {
        return table.get( name );
    }

    /**
      * Returns names of any attributes saved in this context object.
      */
    public Enumeration getAttributeNames()
    {
        return table.keys();
    }

    /**
      * Return the value of the given initialization parameter, or null.
      */
    public String getInitParameter( String name )
    {
        return (String) initTable.get( name );
    }

    /**
      * Returns names of initialization parameters.
      */
    public Enumeration getInitParameterNames()
    {
        return initTable.keys();
    }

    /**
      * Remove an attribute.
      */
    public void removeAttribute( String name )
    {
        table.remove( name );
    }

    /**
      * Set an attribute.
      */
    public void setAttribute( String name, Object value )
    {
        table.put( name, value );
    }

    JaxcentSession getSession( String id )
    {
        if ( id == null )
            return null;
        return (JaxcentSession) sessions.get( id );
    }

    JaxcentSession createSession( String id )
    {
        JaxcentSession session = new JaxcentSession( id, sessionInactivityDefaultTimeout, this );
        sessions.put( id, session );
        return session;
    }
}
