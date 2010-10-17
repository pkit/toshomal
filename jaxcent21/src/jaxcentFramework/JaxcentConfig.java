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
 * Module Name:           JaxcentConfig.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Configuration management for the framework.
 *                        Uses SAX parser to parse the Jaxcent XML config file.
 *
 * Change History:
 *
 *    12/25/2007  MP      Initial version.
 *     1/19/2008  MP      Separated into jaxcentFramework package.
 *     6/30/2008  MP      Added extension support.
 *
 */

package jaxcentFramework;

import java.util.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class JaxcentConfig extends DefaultHandler
{
    static SAXParserFactory factory = SAXParserFactory.newInstance();

    String currentElement = null;
    String pageUrl = null;
    String pageClass = null;
    boolean pageSession = false;
    boolean inPage = false;
    boolean autoSessionData = false;

    String error;
    Hashtable urlMap = new Hashtable();
    ClassLoader loader;

    static JaxcentConfigExt[] configExts = null;

    static {
        try {
            Class.forName( "jaxcentFramework.JaxcentConfigPro" );  // Load the pro version if available
        } catch (Exception ex) {}
    }

    static synchronized void addConfigExt( JaxcentConfigExt ext )
    {
        int len = 0;
        if ( configExts != null )
            len = configExts.length;
        JaxcentConfigExt[] exts = new JaxcentConfigExt[len+1];
        if ( configExts != null )
            System.arraycopy( configExts, 0, exts, 0, len );
        exts[len] = ext;
        configExts = exts;
    }

    static synchronized void removeConfigExt( JaxcentConfigExt ext )
    {
        if ( configExts == null ) return;
        int len = configExts.length;
        if ( len <= 1 ) {
            configExts = null;
            return;
        }
        JaxcentConfigExt[] exts = new JaxcentConfigExt[len-1];
        System.arraycopy( configExts, 0, exts, 0, len-1 );
        configExts = exts;
    }

    void parseConfigFile( String configPath, java.io.InputStream configFileInputStream )
    {
        error = null;
        this.loader = loader;
        urlMap.clear();
        if ( configFileInputStream != null ) {
            try {
                SAXParser parser = factory.newSAXParser();
                parser.parse( configFileInputStream, this );
            } catch (Exception ex) {
                error = "Error in Jaxcent configuration file \"" + configPath + "\": " + ex.toString();
            }
            return;
        }
        java.io.File configFile = new java.io.File( configPath );
        if ( ! configFile.exists()) {
            error = "Jaxcent configuration file \"" + configFile.getAbsolutePath() + "\" not found on server";
            return;
        }
        if ( ! configFile.canRead()) {
            error = "Jaxcent configuration file \"" + configFile.getAbsolutePath() + "\" is not readable";
            return;
        }
        try {
            SAXParser parser = factory.newSAXParser();
            parser.parse( configFile, this );
        } catch (Exception ex) {
            error = "Error in Jaxcent configuration file \"" + configFile.getAbsolutePath() + "\": " + ex.toString();
        }
    }

    String getConfigError()
    {
        return error;
    }

    Hashtable getUrlmap()
    {
        return urlMap;
    }

    public void characters(char[] ch, int start, int length) throws SAXException
    {
        if ( configExts != null ) {
            JaxcentConfigExt[] exts = configExts;
            if ( exts != null ) try {
                for ( int i = 0; i < exts.length; i++ )
                    if ( exts[i].onCharacters( this, ch, start, length ))
                        return;
            } catch (Exception ex) {}
        }
        if ( length == 0 || currentElement == null )
            return;
        String s = new String( ch, start, length ).trim();
        if ( s.equals( "" ))
            return;
        if ( currentElement.equalsIgnoreCase( "PagePath" )) {
            pageUrl = s;
        } else if ( currentElement.equalsIgnoreCase( "PageClass" )) {
            pageClass = s;
        } else if ( currentElement.equalsIgnoreCase( "AutoSessionData" )) {
            if ( s.equalsIgnoreCase( "true" ))
                autoSessionData = true;
            else if ( s.equalsIgnoreCase( "false" ))
                autoSessionData = false;
            else if ( error == null ) {
                error = "Error in Jaxcent Configuration file: AutoSessionData must be true or false, found \"" + s + "\"";
            }
        } else if ( currentElement.equalsIgnoreCase( "UseSession" )) {
            if ( s.equalsIgnoreCase( "true" ))
                pageSession = true;
            else if ( s.equalsIgnoreCase( "false" ))
                pageSession = false;
            else if ( error == null ) {
                 error = "Error in Jaxcent Configuration file: UseSession must be true or false, found \"" + s + "\"";
            }
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes)
                 throws SAXException
    {
        if ( configExts != null ) {
            JaxcentConfigExt[] exts = configExts;
            if ( exts != null ) try {
                for ( int i = 0; i < exts.length; i++ )
                    if ( exts[i].onStartElement( this, uri, localName, qName, attributes ))
                        return;
            } catch (Exception ex) {}
        }
        qName = qName.trim();
        currentElement = qName;
        if ( qName.equalsIgnoreCase( "Page" )) {
            pageUrl = null;
            pageClass = null;
            inPage = true;
            pageSession = false;
            autoSessionData = false;
        } else if ( qName.equalsIgnoreCase( "JaxcentConfiguration" )) {
        } else if ( qName.equalsIgnoreCase( "PagePath" ) ||
                    qName.equalsIgnoreCase( "PageClass" ) ||
                    qName.equalsIgnoreCase( "AutoSessionData" ) ||
                    qName.equalsIgnoreCase( "UseSession" )) {
            if ( ! inPage ) {
            if ( error == null )
                error = "Error in Jaxcent Configuration file: Tag \"" + qName + "\" must occur inside a Page tag";
            }
        } else {
            if ( error == null )
                error = "Error in Jaxcent Configuration file: Unknown tag \"" + qName + "\"";
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        if ( configExts != null ) {
            JaxcentConfigExt[] exts = configExts;
            if ( exts != null ) try {
                for ( int i = 0; i < exts.length; i++ )
                    if ( exts[i].onEndElement( this, uri, localName, qName ))
                        return;
            } catch (Exception ex) {}
        }
        currentElement = null;
        if ( qName.equalsIgnoreCase( "page" )) {
            if ( pageUrl == null || pageClass == null ) {
                if ( error == null )
                    error = "Error in Jaxcent Configuration file: PagePath and PageClass must both be specified in PAGE elements";
                return;
            }
            if ( ! pageUrl.startsWith( "/" )) {
                if ( pageUrl.startsWith( "\\" ))
                    pageUrl = pageUrl.replaceAll( "\\", "/" );
                else
                    pageUrl = "/" + pageUrl;
            }
            // Add page url and class to map.
            JaxcentUrlInfo info = new JaxcentUrlInfo();
            info.path = pageUrl;
            info.className = pageClass;
            info.useSession = pageSession;
            info.autoSessionData = autoSessionData;
            info.cls = null;
            info.cinfo = null;
            info.err = null;
            urlMap.put( pageUrl.trim().toLowerCase(), info );
            inPage = false;
        }
    }
}
