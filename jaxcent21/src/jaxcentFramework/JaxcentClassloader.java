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
 * Module Name:           JaxcentClassLoader.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Jaxcent needs a separate classloader to allow
 *                        dynamic reloading.
 *
 * Change History:
 *
 *    12/24/2007  MP      Initial version.
 *     1/19/2008  MP      Moved to jaxcentFramework package.
 *
 */

package jaxcentFramework;

import java.util.*;
import java.io.*;
import java.util.zip.*;

class JaxcentClassloader extends ClassLoader {

    String classpath;
    Hashtable classInfo = new Hashtable();

    JaxcentClassloader( String cp, ClassLoader parent )
    {
        super( parent );
        classpath = cp;
    }

    protected Class loadClass( String name, boolean resolve )
                   throws ClassNotFoundException
    {
        Class cls = findLoadedClass( name );
        if ( cls != null )
            return cls;
        // if we have a classpath, look in there for the class.
        if ( classpath != null ) {
            String fname = name.replace( '.', '/' ) + ".class";
            StringTokenizer tok = new StringTokenizer( classpath, System.getProperty( "path.separator" ));
            while ( tok.hasMoreTokens()) {
                File f = new File( tok.nextToken());
                InputStream in = null;
                ZipInputStream zin = null;
                try {
                    if ( ! f.exists())
                        continue;
                    if ( f.isDirectory()) {
                        f = new File( f, fname );
                        if ( f.canRead()) {
                            byte[] data = new byte[ (int) f.length() ];
                            in = new FileInputStream( f );
                            in.read( data );
                            in.close();
                            in = null;
                            cls = defineClass( name, data, 0, data.length );
                            JaxcentClassInfo info = new JaxcentClassInfo();
                            info.className = name;
                            info.cls = cls;
                            info.classSource = f;
                            info.timestamp = f.lastModified();
                            classInfo.put( name, info );
                            if ( resolve )
                                resolveClass( cls );
                            return cls;
                        }
                    } else {
                        // JAR or ZIP file
                        ZipFile zf = new ZipFile( f );
                        ZipEntry entry = zf.getEntry(fname);
                        if ( entry != null ) {
                            in = new BufferedInputStream( new FileInputStream( f ));
                            zin  = new ZipInputStream(in);
                            int size = (int) entry.getSize();
                            while (( entry = zin.getNextEntry()) != null ) try {
                                if ( entry.getName().equals(fname))
                                    break;  // Found it
                            } catch ( Exception any ) { entry = null; }
                            if ( entry != null ) {
                                byte[] data = null;
                                if ( size == -1 ) {
                                    // Read one at a time...
                                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                                    int ch;
                                    while (( ch = zin.read()) != -1 ) {
                                        bout.write( ch );
                                    }
                                    data = bout.toByteArray();
                                } else {
                                    data = new byte[ size ];
                                    int ofs = 0;
                                    while ( size > 0 ) {
                                        int n = zin.read( data, ofs, size );
                                        ofs += n;
                                        size -= n;
                                    }
                                }
                                cls = defineClass( name, data, 0, data.length );
                                JaxcentClassInfo info = new JaxcentClassInfo();
                                info.className = name;
                                info.cls = cls;
                                info.classSource = f;
                                info.timestamp = f.lastModified();
                                classInfo.put( name, info );
                                if ( resolve )
                                    resolveClass( cls );
                                return cls;
                            }
                        }
                    }
                } catch (Exception ex) {
                } finally {
                    if ( in != null ) try { in.close(); } catch (Exception ignore) {}
                    if ( zin != null ) try { zin.close(); } catch (Exception ignore) {}
                }
            }
        }
        
        return super.loadClass( name, resolve );
    }

    JaxcentClassInfo getClassInfo( String name )
    {
        return (JaxcentClassInfo) classInfo.get( name );
    }

/*
    void printInfo( String name )
    {
        System.out.println( "Class: " + name );
        JaxcentClassInfo cinfo = (JaxcentClassInfo) classInfo.get( name );
        if ( cinfo != null ) {
            System.out.println( " Source: " + cinfo.classSource.getAbsolutePath());
            System.out.println( " Last Modified: " + cinfo.timestamp );
            System.out.println( " Class: " + cinfo.cls );
        }
    }

    public static void main( String[] args )
    {
        JaxcentClassloader cl = new JaxcentClassloader( "C:\\BlazixServer\\blazix.jar;c:\\BlazixServer\\classes" );
        try {
            cl.loadClass( "jaxcentServlet.JaxcentServlet" );
            cl.loadClass( "java.io.File" );
            cl.loadClass( "javax.servlet.Servlet" );
             
            cl.printInfo( "jaxcentServlet.JaxcentServlet" );
            cl.printInfo( "java.io.File" );
            cl.printInfo( "javax.servlet.Servlet" );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
*/
}
