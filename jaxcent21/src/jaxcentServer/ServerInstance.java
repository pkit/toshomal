package jaxcentServer;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import jaxcentConnector.*;

class ServerInstance extends Thread {

    File htmlRoot;
    JaxcentHttpConnector jaxcent;
    GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

    Socket socket;

    int contentLength;
    String contentTypeIn;
    BufferedInputStream in;
    BufferedOutputStream out;
    byte[] data;
    String queryString;
    String contentType;
    String jaxcentCookie;
    String acceptLangs;
    // Hashtable headers = new Hashtable( 10 );
    boolean isChunked;
    byte[] setCookie;
    boolean cached;
    long lastModified = 0;
    int lastLength = -1;
    byte[] resp;
    byte[] content;
    String method;
    String uri;
    String moved;
    long fileMod;

    DateFormat df = DateFormat.getTimeInstance( DateFormat.MEDIUM );

    static final byte[] httpVer = "HTTP/1.1 ".getBytes();
    static final byte[] crlf = { '\r', '\n' };
    static final byte[] serverHeader = "Server: Jaxcent Development Server\r\n".getBytes();
    static final byte[] keepAlive = "Connection: keep-alive\r\n".getBytes();
    static final byte[] closeConnection = "Connection: close\r\n".getBytes();
    static final byte[] noCacheHeaders = "Cache-Control: no-cache\r\nPragma: no-cache\r\nExpires: 0\r\n".getBytes();
    static final byte[] okResp = "200 OK".getBytes();
    static final byte[] fnf = "404 Not Found".getBytes();
    static final byte[] redir = "302 Temporary Redirection".getBytes();
    static final byte[] fnm = "304 Not Modified".getBytes();
    static final byte[] dateHeader = "Date: ".getBytes();
    static final byte[] ctHeader = "Content-type: ".getBytes();
    static final byte[] clenHeader = "Content-length: ".getBytes();
    static final byte[] modifiedHeader = "Last-Modified: ".getBytes();
    static final byte[] locationHeader = "Location: ".getBytes();
    static final byte[] fnfContent = "<HTML><BODY>Requested resource was not found</BODY></HTML>".getBytes();

    static final String JAXCENT_COOKIE_HEADER = "JAXCENT_SESSIONID=";

    static final String[][] mimeTypes = {
        { "html", "text/html" },
        { "gif",  "image/gif" },
        { "htm",  "text/html" },
        { "jpeg", "image/jpeg" },
        { "jpg",  "image/jpeg" },
        { "css",  "text/css" },
        { "text", "text/plain" },
        { "txt",  "text/plain" },
        { "jpe",  "image/jpeg" },
        { "png",  "image/png" },
        { "js",   "text/javascript" },
        { "java", "text/plain" },
        { "pdf",  "application/pdf" },
        { "tif",  "image/tiff" },
        { "tiff", "image/tiff" },
        { "xml",  "text/xml" },
        { "ief",  "image/ief" },
        { "mpeg", "video/mpeg" },
        { "mpe",  "video/mpeg" },
        { "mpg",  "video/mpeg" },
    };

    static final String[] indexFiles = {
        "index.html",
        "index.htm",
        "default.htm",
        "default.html",
    };


    ServerInstance( Socket s, File htmlRoot, JaxcentHttpConnector jaxcent )
    {
        this.socket = s;
        this.htmlRoot = htmlRoot;
        this.jaxcent = jaxcent;
    }

    void parseHeader( String header )
    {
        int cat = header.indexOf( ':' );
        if ( cat <= 0 )
            return;
        String headerType = header.substring( 0, cat );
        cat++;
        while ( cat < header.length() && Character.isSpaceChar( header.charAt( cat )))
            cat++;
        String value = header.substring( cat );
        // headers.put( headerType.toLowerCase(), value );
        if ( headerType.equalsIgnoreCase( "content-length" )) {
            contentLength = Integer.parseInt( value );
        } else if ( headerType.equalsIgnoreCase( "content-type" )) {
            contentTypeIn = value;
        } else if ( headerType.equalsIgnoreCase( "cookie" )) {
            // Extract Jaxcent cookie.
            StringTokenizer tok = new StringTokenizer( value, ";" );
            while ( tok.hasMoreTokens()) {
                String nextPart = tok.nextToken().trim();
                if ( nextPart.startsWith( JAXCENT_COOKIE_HEADER )) {
                    jaxcentCookie = nextPart.substring( JAXCENT_COOKIE_HEADER.length());
                    break;
                }
            }
        } else if ( headerType.equalsIgnoreCase( "accept-language" )) {
            acceptLangs = value;
        } else if ( headerType.equalsIgnoreCase( "if-modified-since" )) {
            String lastMod = value;
            int semi = lastMod.indexOf( ';' );
            if ( semi > 0 ) {
                int lindex = lastMod.indexOf( "length=", semi );
                if ( lindex >= 0 ) try {
                    lastLength = Integer.parseInt( lastMod.substring( lindex+7 ).trim());
                } catch (Exception ex) {}
                lastMod = lastMod.substring( 0, semi ).trim();
            }
            try {
                lastModified = parseDate( lastMod );
            } catch (Exception ex) {}
        } else if ( headerType.equalsIgnoreCase( "transfer-encoding" )) {
            if ( value.equalsIgnoreCase( "chunked" ))
                isChunked = true;
        }
    }

    public long parseDate( String dateValue ) throws IllegalArgumentException
    {
        // Parse header (form  Sun, 13-Feb-2000 00:00:00 GMT)
        StringTokenizer tk = new StringTokenizer( dateValue, ",- :" );
        try {
            String dayOfWeek = tk.nextToken();
            String dayOfMonth;
            if ( Character.isDigit(dayOfWeek.charAt(0)))
                dayOfMonth = dayOfWeek;
            else
                dayOfMonth = tk.nextToken();
            String month = tk.nextToken();
            String year = tk.nextToken();
            String hh = tk.nextToken();
            String mm = tk.nextToken();
            String ss = tk.nextToken();
            String tz = tk.nextToken();
            
            int mon = "janfebmaraprmayjunjulaugsepoctnovdec".indexOf( month.substring(0,3).toLowerCase()) / 3;
            int yy = Integer.parseInt( year );
            if ( yy < 100 )
                yy += 2000;
            GregorianCalendar gcal = null;
            if ( ! tz.equalsIgnoreCase( "GMT" )) {
                try {
                    TimeZone tzone = TimeZone.getTimeZone( tz.toUpperCase());
                    if ( tzone != null )
                        gcal = new GregorianCalendar( tzone );
                } catch (Exception ex) {}
            }
            if ( gcal == null )
                gcal = cal;
            gcal.clear();
            gcal.set( yy,
                      mon,
                      Integer.parseInt( dayOfMonth ),
                      Integer.parseInt( hh ),
                      Integer.parseInt( mm ),
                      Integer.parseInt( ss ));
            return gcal.getTime().getTime();
        } catch (Exception ex) {
            throw new IllegalArgumentException(dateValue);
        }
    }

    static String twoDigitFormat( int val )
    {
        if ( val > 9 )
            return String.valueOf( val );
        return "0" + String.valueOf( val );
    }
    
    String dateFormat( long date )
    {
        cal.setTime(new Date( date ));
        int dayOfWeekIndex = ( cal.get( Calendar.DAY_OF_WEEK) - 1 ) * 3;
        int monthIndex = cal.get( Calendar.MONTH ) * 3;

        return "SunMonTueWedThuFriSat".substring( dayOfWeekIndex, dayOfWeekIndex + 3 ) +
               ", " + String.valueOf( cal.get( Calendar.DAY_OF_MONTH )) + " " +
               "JanFebMarAprMayJunJulAugSepOctNovDec".substring( monthIndex, monthIndex + 3) + " " +
               String.valueOf( cal.get( Calendar.YEAR )) + " " +
               twoDigitFormat( cal.get( Calendar.HOUR_OF_DAY )) + ":" +
               twoDigitFormat( cal.get( Calendar.MINUTE )) + ":" +
               twoDigitFormat( cal.get( Calendar.SECOND )) + " GMT";
    }

    String readLine() throws IOException
    {
        StringBuffer buf = new StringBuffer();
        int ch;
        while (( ch = in.read()) >= 0 ) {
            if ( ch == '\r' ) {
                in.read();
                return buf.toString();
            }
            buf.append( (char) ch );
        }
        return null;
    }

    byte[] runJaxcent()
    {
        contentType = "text/plain";
        Object[] request = new Object[3];
        request[0] = null;
        if ( data != null )
            request[0] = new String( data );
        request[1] = jaxcentCookie;
        request[2] = null;
        byte[] ipBytes = socket.getInetAddress().getAddress();
        StringBuffer ip = new StringBuffer();
        ip.append( ipBytes[0] );
        for ( int i = 1; i < ipBytes.length; i++ ) {
            ip.append( '.' );
            ip.append( ipBytes[i] );
        }
        jaxcent.process( request, ip.toString(), acceptLangs );
        if ( request[1] != null ) {
            setCookie = ( "Set-Cookie: " + JAXCENT_COOKIE_HEADER + request[1] + ";PATH=/\r\n" ).getBytes();
        }
        if ( request[2] != null )
            contentType = (String) request[2];
        if ( request[0] == null )
            return null;
        return (byte[]) request[0];
    }

    boolean checkUnmodified( int fileLength )
    {
        if ( lastModified != 0 && fileMod == lastModified && ( lastLength == -1 || lastLength == fileLength )) {
            resp = fnm;
            content = null;
            contentType = null;
            fileMod = 0;
            return true;
        }
        return false;
    }

    void processRequest() throws IOException
    {
        boolean isJaxcentJs = uri.equalsIgnoreCase( "/jaxcent21.js" );
        boolean isJaxcentServlet = uri.equalsIgnoreCase( "/servlet/jaxcentservlet21" ) || uri.toLowerCase().startsWith( "/servlet/jaxcentservlet21/" );
        if (( ! ( isJaxcentJs || isJaxcentServlet )) || ( isJaxcentServlet && method.equals( "GET" ) && uri.equalsIgnoreCase( "/servlet/JaxcentServlet21" )))
            System.out.println( df.format( new java.util.Date()) + ": " + method + " " + uri );
        resp = okResp;
        contentType = "text/plain";
        if ( isJaxcentServlet ) {
            content = runJaxcent();
            if ( content == null ) {
                contentType = "text/html";
                resp = fnf;
                content = fnfContent;
            }
            return;
        }
        if ( isJaxcentJs ) {
            // Provide content from JAR file.
            cached = true;
            fileMod = ServerMain.jsfileLastModified;
            if ( checkUnmodified( ServerMain.jsfileBytes.length )) {
                return;
            }
            contentType = "text/javascript";
            content = ServerMain.jsfileBytes;
            return;
        }
        File file = new File( htmlRoot, uri );
        if ( file.isDirectory()) {
            if ( ! uri.endsWith( "/" )) {
                contentType = null;
                resp = redir;
                moved = uri + "/";
                return;
            }
            File check = null;
            for ( int i = 0; i < indexFiles.length; i++ ) {
                check = new File( file, indexFiles[i] );
                if ( check.canRead())
                    break;
            }
            file = check;
        }
        if ( ! ( file.exists() && file.canRead())) {
            contentType = "text/html";
            resp = fnf;
            content = fnfContent;
            return;
        }
        fileMod = file.lastModified();
        int fileLength = (int) file.length();
        if ( checkUnmodified( fileLength )) {
            return;
        }
        cached = true;
        content = new byte[ fileLength ];
        InputStream fin = new FileInputStream( file );
        fin.read( content );
        fin.close();
        String name = file.getName();
        int suf = name.lastIndexOf( '.' );
        contentType = "application/x-unknown";
        if ( suf >= 0 ) {
            String suffix = name.substring( suf+1 ).toLowerCase();
            for ( int i = 0; i < mimeTypes.length; i++ ) {
                if ( suffix.equals( mimeTypes[i][0] )) {
                    contentType = mimeTypes[i][1];
                    break;
                }
            }
        }
    }

    public void run()
    {
        try {
            socket.setSoTimeout( 300000 ); // Disconnect timeout, 5 minutes
            in = new BufferedInputStream( socket.getInputStream());
            out = new BufferedOutputStream( socket.getOutputStream());
            while ( readRespond())
                ;
        } catch (Exception ex) {
            // ex.printStackTrace();
        } finally {
            try { out.close(); } catch (Exception ex) {}
            try { in.close(); } catch (Exception ex) {}
        }
    }

    void processUpload() throws IOException
    {
        Object ref = jaxcent.onUploadInit( queryString, contentTypeIn, contentLength );
        boolean send = true;
        if ( isChunked ) {
              for (;;) {
                  String cHeader = readLine();
                  int semi = cHeader.indexOf( ';' );
                  if ( semi > 0 )
                      cHeader = cHeader.substring( 0, semi );
                  int chunkLen = Integer.parseInt( cHeader, 16 );
                 if ( chunkLen == 0 )
                     break;
                byte[] chunk = new byte[ chunkLen ];
                in.read( chunk );
                if ( send ) send = jaxcent.onUpload( ref, chunk );
            }
            String header = readLine();
            while ( header != null && ! header.equals( "" )) {
                parseHeader( header );
                header = readLine();
            }
        } else if ( contentLength > 0 ) {
            int size = contentLength;
            if ( size > 8192 ) size = 8192;
            data = new byte[ size ];
            int clen = contentLength;
            while ( clen > 0 ) {
                int n = size;
                if ( n > clen )
                    n = clen;
                n = in.read( data, 0, n );
                if ( n <= 0 )
                    break;
                clen -= n;
                if ( send ) {
                    send = jaxcent.onUpload( ref, data, 0, n );
                }
            }
        }
        contentType = "text/plain";
        content = jaxcent.onUploadEnd( ref );
    }

    boolean readRespond() throws IOException
    {
        contentLength = 0;
        queryString = null;
        jaxcentCookie = null;
        acceptLangs = null;
        setCookie = null;
        isChunked = false;
        cached = false;
        lastModified = 0;
        lastLength = -1;
        fileMod = 0;
        data = null;
        moved = null;
        contentTypeIn = null;

        String line = readLine();
        if ( line == null )
            return false;
        StringTokenizer tok = new StringTokenizer( line );
        method = tok.nextToken().toUpperCase();
        uri = tok.nextToken();
        String ver = tok.nextToken();
        boolean persistent = ! ver.endsWith( "1.0" );
        // Locate the URI.
        if ( uri.startsWith( "//" )) {
            uri = uri.substring( uri.indexOf( '/', 2 ));
        }
        int qat = uri.indexOf( '?' );
        if ( qat >= 0 ) {
            queryString = uri.substring( qat+1 );
            uri = uri.substring( 0, qat );
        }
        // headers.clear();
        // Read headers.
        String header = readLine();
        while ( header != null && ! header.equals( "" )) {
            parseHeader( header );
            header = readLine();
        }
        // If POST, read content.
        boolean upload = false;
        if ( method.equals( "POST" )) {
            if ( contentTypeIn != null && contentTypeIn.toLowerCase().startsWith( "multipart/form-data" ) &&
                 uri.equalsIgnoreCase( "/servlet/jaxcentservlet21" )) {
                upload = true;
                processUpload();
            } else if ( isChunked ) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                for (;;) {
                    String cHeader = readLine();
                    int semi = cHeader.indexOf( ';' );
                    if ( semi > 0 )
                        cHeader = cHeader.substring( 0, semi );
                    int chunkLen = Integer.parseInt( cHeader, 16 );
                    if ( chunkLen == 0 )
                        break;
                    byte[] chunk = new byte[ chunkLen ];
                    in.read( chunk );
                    bos.write( chunk );
                }
                header = readLine();
                while ( header != null && ! header.equals( "" )) {
                    parseHeader( header );
                    header = readLine();
                }
                bos.flush();
                data = bos.toByteArray();
            } else if ( contentLength > 0 ) {
                data = new byte[ contentLength ];
                in.read( data );
            }
        } else {
            if ( queryString != null ) {
                data = queryString.getBytes();
            }
        }
        if ( ! upload )
            processRequest();
        out.write( httpVer );
        out.write( resp );
        out.write( crlf );
        out.write( dateHeader );
        out.write( dateFormat( System.currentTimeMillis()).getBytes());
        out.write( crlf );
        out.write( serverHeader );
        if ( persistent )
            out.write( keepAlive );
        else
            out.write( closeConnection );
        if ( fileMod != 0 ) {
            out.write( modifiedHeader );
            out.write( dateFormat( fileMod ).getBytes());
            out.write( crlf );
        }
        if ( ! cached )
            out.write( noCacheHeaders );
        if ( contentType != null ) {
            out.write( ctHeader );
            out.write( contentType.getBytes());
            out.write( crlf );
        }
        if ( moved != null ) {
            out.write( locationHeader );
            out.write( moved.getBytes());
            out.write( crlf );
        }
        if ( setCookie != null )
            out.write( setCookie );
        out.write( clenHeader );
        if ( content != null ) {
            out.write( Integer.toString( content.length ).getBytes());
        } else
            out.write( '0' );
        out.write( crlf );
        out.write( crlf );
        if ( content != null && ! method.equalsIgnoreCase( "HEAD" ))
            out.write( content );
        out.flush();
        return persistent;
    }
}
