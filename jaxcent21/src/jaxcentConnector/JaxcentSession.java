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
 * Module Name:           JaxcentSession.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Session class for connectors.
 *
 * Change History:
 *
 *     1/20/2008  MP      Initial version.
 *
 */

package jaxcentConnector;

import java.util.*;

/**
  * This class provides a session object for direct Jaxcent connections
  * to an HTTP server (instead of via a Java application server.)
  */

public class JaxcentSession {

    Hashtable table = new Hashtable(1);
    long createdAt;
    long accessedAt;
    String id;
    int inactiveInterval;
    JaxcentAppContext context;

    JaxcentSession( String sid, int inactive, JaxcentAppContext ctx )
    {
        this.createdAt = System.currentTimeMillis();
        this.accessedAt = System.currentTimeMillis();
        this.id = sid;
        this.inactiveInterval = inactive;
        this.context = ctx;
    }

    /**
      * Return object saved at the given attribute name, or null.
      */
    public Object getAttribute( String name )
    {
        return table.get( name );
    }

    /**
      * Returns names of any attributes saved in session.
      */
    public Enumeration getAttributeNames()
    {
        return table.keys();
    }

    /**
      * Returns time of creation of the session.
      */
    public long getCreationTime()
    {
        return createdAt;
    }

    /**
      * Returns a unique id associated with session.
      */
    public String getId()
    {
        return id;
    }

    /**
      * Returns last access time
      */
    public long getLastAccessedTime()
    {
        return accessedAt;
    }

    void setLastAccessedTime()
    {
        accessedAt = System.currentTimeMillis();
    }

    /**
      * Returns the maximum inactivity interval before session deletion, in seconds.
      */
    public int getMaxInaciveInterval()
    {
        return inactiveInterval;
    }

    /**
      * Gets the context.
      */
    public JaxcentAppContext getAppContext()
    {
        return context;
    }

    /**
      * Invalidate the session.
      */
    public void invalidate()
    {
        context.removeSession( id );
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

    /**
      * Set maximum inactivity interval before session deletion, in seconds.
      */
    public void setMaxInaciveInterval( int interval )
    {
        this.inactiveInterval = interval;
    }
}
