package jaxcentServer;

import java.net.*;
import java.io.*;

import jaxcentConnector.*;
import toshomalBackend.BackendInstance;
import toshomalBackend.ToshomalMessage;

public class ServerMain extends Thread {

    int serverPort;
    File htmlRoot;
    ServerSocket serverSocket;
    boolean bRun;
    JaxcentHttpConnector jaxcent;

    static byte[] jsfileBytes;
    static long jsfileLastModified = 0;

    static synchronized void loadJaxcentJS() throws IOException
    {
        if ( jsfileBytes == null ) {
            java.net.URL url = ServerMain.class.getResource( "/jaxcent21.js" );
            java.net.URLConnection conn = url.openConnection();
            jsfileLastModified = conn.getLastModified();
            conn.connect();
            InputStream in = (InputStream) conn.getContent();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int ch;
            while (( ch = in.read()) >= 0 )
                baos.write( ch );
            baos.close();
            jsfileBytes = baos.toByteArray();
        }
    }

    public ServerMain( int port, String htmlDir, String configFile, String classPath ) throws IOException
    {
        loadJaxcentJS();
        String[] params = null;
        String[] values = null;
        if ( classPath != null && ! classPath.equals( "" )) {
            params = new String[]{ "JaxcentConfigXML", "ReloadableClasses" };
            values = new String[]{ configFile, classPath };
        } else {
            params = new String[]{ "JaxcentConfigXML" };
            values = new String[]{ configFile };
        }

        htmlRoot = new File( htmlDir );
        if ( ! htmlRoot.isDirectory()) {
            throw new IOException( htmlRoot.getAbsolutePath() + " is not a directory" );
        }
        if ( ! new File( configFile ).canRead()) {
            throw new IOException( configFile + ": Cannot read" );
        }
        jaxcent = new JaxcentHttpConnector( params, values );
        jaxcent.init();
        serverPort = port;
        serverSocket = new ServerSocket( port );
    }

    public void terminate() throws IOException
    {
        bRun = false;
        serverSocket.close();
        jaxcent.terminate();
    }

    public void run()
    {
        System.out.println( "Jaxcent Server started at " + new java.util.Date());
        System.out.println( "  Port: " + serverPort );
        System.out.println( "  HTML Root: " + htmlRoot.getAbsolutePath());
        bRun = true;
        try {
            ToshomalMessage femsg = new ToshomalMessage(500);
            ToshomalMessage bemsg = new ToshomalMessage(500);
            new BackendInstance(bemsg, femsg).start();
            while ( bRun ) {
                Socket s = serverSocket.accept();
                new ServerInstance( s, htmlRoot, jaxcent ).start();
                if(femsg.put("Frontend Data"))
                { System.out.println("Successfully sent to backend"); }
                else
                { System.out.println("Failed to send to backend, will try later"); }
                String msg = bemsg.take();
                if(msg != null)
                { System.out.println("Got message from backend: " + msg); }
                else
                { System.out.println("Failed to get message from backend, will try later"); }
            }
        } catch (Exception ex) {
            if ( bRun ) {
                System.err.println( "***  SERVER STOPPED ***" );
                ex.printStackTrace();
            }
        }
    }

    static void usage()
    {
        System.out.println( "Usage: " );
        System.out.println( "  java jaxcentServer.ServerMain <port-number> <html-dir> <config-file>" );
        System.out.println( "  java jaxcentServer.ServerMain <port-number> <html-dir> <config-file> <reloadable-classpath>" );
        System.out.println();
        System.out.println( "e.g." );
        System.out.println();
        System.out.println( "  java jaxcentServer.ServerMain 80 c:\\MyHtmlFiles c:\\MyConfigFile.xml" );
        System.exit( 1 );
    }

    public static void main( String[] args )
    {
        // Retrieve args, start server main
        if ( args.length < 3 || args.length > 4 ) {
            usage();
        }
        int port = 80;
        try {
            port = Integer.parseInt( args[0] );
        } catch (Exception ex) {
            usage();
        }

        String reloadableClasspath = null;
        if ( args.length == 4 )
            reloadableClasspath = args[3];
        try {
            new ServerMain( port, args[1], args[2], reloadableClasspath ).start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
