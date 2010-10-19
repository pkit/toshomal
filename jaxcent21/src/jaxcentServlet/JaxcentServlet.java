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
 * Module Name:           JaxcentServlet.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Primary servlet class for Jaxcent servlet.
 *
 * Change History:
 *
 *    12/25/2007  MP      Initial version.
 *     1/20/2008  MP      Separated into servlet-specific (vs Jaxcent framework) code.
 *
 */

package jaxcentServlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

import jaxcentFramework.*;

public class JaxcentServlet extends HttpServlet implements jaxcentFramework.JaxcentHttpMain
{
    JaxcentFramework framework = new JaxcentFramework( this );

    public void init()
          throws ServletException
    {
        framework.init();
    }

    public String getServletInfo()
    {
        return "Jaxcent Servlet, Copyright (C) Desiderata Software 2008";
    }

    void serviceUpload( HttpServletRequest request, HttpServletResponse resp, String contentType )
              throws ServletException, java.io.IOException
    {
        int contentLength = request.getContentLength();
        String queryString = request.getQueryString();
        Object ref = framework.onUploadInit( queryString, contentType, contentLength );
        ServletInputStream in = request.getInputStream();
        byte[] buf = new byte[ 8192 ];
        int n;
        while (( n = in.read( buf, 0, 8192 )) > 0 ) {
            if ( ! framework.onUpload( ref, buf, 0, n ))
                break;
        }
        byte[] content = framework.onUploadEnd( ref );
        if ( content != null ) {
            ServletOutputStream out = resp.getOutputStream();
            out.write( content );
        }
    }

    protected void service( HttpServletRequest request, HttpServletResponse resp )
              throws ServletException, java.io.IOException
    {
        resp.setHeader( "Content-Type", "text/plain" );
        resp.setHeader("Cache-Control","no-cache");
        resp.setHeader("Pragma","no-cache");
        resp.setDateHeader ("Expires", 0);

        String ctype = request.getContentType();
        if ( ctype != null && ctype.toLowerCase().startsWith( "multipart/form-data" )) {
            serviceUpload( request, resp, ctype );
            return;
        }
        byte[] responseText = framework.process( request, resp, request.getRemoteAddr(), request.getHeader( "Accept-Language" ));
        if ( responseText == null ) {
            resp.sendError( 404 );
            return;
        }

        ServletOutputStream out = resp.getOutputStream();
        out.write( responseText );
    }

    public ClassLoader getClassLoader()
    {
        return getClass().getClassLoader();
    }

    public Object getAppContext()
    {
        return  getServletConfig().getServletContext();
    }

    public Object getSession( Object request, boolean create )
    {
        return ((HttpServletRequest) request).getSession( create );
    }

    public synchronized java.util.Hashtable getJaxcentHashtable( Object sessionObj, boolean create )
    {
        if ( sessionObj == null )
            return null;
        javax.servlet.http.HttpSession session = (javax.servlet.http.HttpSession) sessionObj;
        java.util.Hashtable table = (java.util.Hashtable) session.getAttribute( "jaxcent.SessionData" );
        if ( table == null && create ) {
            table = new java.util.Hashtable();
            session.setAttribute( "jaxcent.SessionData", table );
        }
        return table;
    }

    public JaxcentContext getJaxcentContext()
    {
        return (JaxcentContext) getServletContext().getAttribute( "jaxcentServlet.context" );
    }

    public void setJaxcentContext( JaxcentContext ctx )
    {
        getServletContext().setAttribute( "jaxcentServlet.context", ctx );
    }

    public String getConfigItem( String item )
    {
        String value = getInitParameter( item );
        if ( value == null )
            value = getServletContext().getInitParameter( item );
        return value;
    }

    public String getRequestParameter( Object request, String param )
    {
        try {
            ((HttpServletRequest) request).setCharacterEncoding( "UTF-8" );
        } catch (Exception ex) {}
        return ((HttpServletRequest) request).getParameter( param );
    }

    public String[] getRequestParameterValues( Object request, String param )
    {
        try {
            ((HttpServletRequest) request).setCharacterEncoding( "UTF-8" );
        } catch (Exception ex) {}
        return ((HttpServletRequest) request).getParameterValues( param );
    }

    public void setContentType( Object response, String contentType )
    {
        ((HttpServletResponse) response).setHeader( "Content-Type", contentType );
    }

    public java.io.InputStream getResourceAsStream( String resource )
    {
        try {
            return getServletContext().getResourceAsStream( resource );
        } catch (Exception ex) {}
        return null;
    }

}
