package sample;

import jaxcent.*;
import java.util.*;
import java.text.*;

/**
  * Jaxcent chat sample.
  *
  * The chat sample simply adds messages to a table, keeping the
  * maximum size of table at 24.
  *
  * Since all chat pages need to share the data, a Java static
  * vector is used to keep the message list.
  */

public class ChatSample extends jaxcent.JaxcentPage {

    static final int MAX_MESSAGES = 24;  // How many messages to keep around.

    // Structure to store the messages

    private class Message {
        String time;
        String from;
        String message;
    }

    // Static vector to hold messages.

    static Vector messages = new Vector();


    // Locate the table, the DIV containing it, the message input field,
    // and add a handler to the "send" button.

    HtmlTable messageList = new HtmlTable( this, "messageList" );
    HtmlDiv messageDiv = new HtmlDiv( this, "messageDiv" );
    HtmlInputText messageBox = new HtmlInputText( this, "message" );

    // We need to handle the form submit, and not the
    // button click, because we want either the button
    // click or an "Enter" key to send the message.
    // The "Enter" key will generate a form submit.

    HtmlForm sendForm = new HtmlForm( this, "sendForm" ) {
        public void onSubmit( Map pageData ) {
           onSend( pageData );
        }
    };


    // The last message we have added to the table.

    Message lastMessage = null;

    // The attributes for the three cells to be added.
    static String[][] cellAttributes = {
        { "width" },
        { "width" },
        { "width" }
    };

    static String[][] cellValues = {
        { "15%" },
        { "15%" },
        { "70%" }
    };
    
    // Define a thread that will add messages to the messageList table.

    boolean running = true;

    private class MessageThread extends Thread {
        public void run()
        {
            try {
                synchronized (messages) {
                    while ( running ) {
                        // Look backwards in the vector to find the last message we have seen.
                        int start = 0;
                        if ( lastMessage != null ) {
                            for ( int i = messages.size() - 1; i >= 0 && start == 0; i-- ) {
                                if ( messages.elementAt(i) == lastMessage ) {
                                    start = i+1;
                                }
                            }
                        }

                        if ( start < messages.size()) try {

                            // Have new messages

                            setBatchUpdates( true );  // Improve performance by batching output

                            // Add new messages

                            for ( int i = start; i < messages.size(); i++ ) {
                                lastMessage = (Message) messages.elementAt( i );
                                 messageList.insertRow( -1,
                                        new String[]{ "<I>" + lastMessage.time + "</I>", 
                                                      "<B>" + lastMessage.from + "</B>",
                                                      lastMessage.message },
                                        cellAttributes, cellValues );
                            }
  
                            // If the table has too many rows, delete any extra from the top.

                            messageList.deleteFromTop( - MAX_MESSAGES );
                            messageDiv.scrollToBottom();

                        } finally {
                            setBatchUpdates( false );
                        }

                        messages.wait( 5000 );  // Wait until somebody adds a new message to the vector.
                    }
                }
            } catch (InterruptedException ex) {
            } catch (Jaxception ex) {
                ex.printStackTrace();
            }
        }
    }

    MessageThread messageThread;


    // Start a message thread in the constructor.

    public ChatSample()
    {
        messageThread = new MessageThread();
        messageThread.start();
    }

    // Stop the thread on page unloading.

    protected void onUnload()
    {
        running = false;
        messageThread.interrupt();
    }

    // Add a message to the messages vector, wakeup anybody waiting for it.

    void onSend( Map pageData )
    {
        String name = (String) pageData.get( "name" );
        String message = (String) pageData.get( "message" );
        if ( name.equals( "" )) {
            showMessageDialog( "Please enter your name!" );
            return;
        }
        if ( message.equals( "" )) {
            showMessageDialog( "Please enter a message!" );
            return;
        }
        // Build a new message object.
        Message msg = new Message();
        msg.from = name.replaceAll( "&", "&amp;" ).replaceAll( "<", "&lt;" );
        msg.message = message.replaceAll( "&", "&amp;" ).replaceAll( "<", "&lt;" );
        if ( msg.message.length() > 80 )
            msg.message = msg.message.substring( 0, 79 );
        DateFormat fmt = DateFormat.getTimeInstance( DateFormat.MEDIUM );
        msg.time = fmt.format( new java.util.Date());

        // Add the message object to the vector.
        synchronized (messages) {
            messages.addElement( msg );

            // If vector has too many elements, remove some from the top.
            while ( messages.size() > MAX_MESSAGES )
                messages.remove( 0 );

            // Wakeup everybody.
            messages.notifyAll();
        }

        // Clear out the message field and set focus to it.
        try {
            messageBox.setValue( "" );
            messageBox.focus();
        } catch (Jaxception ex) {
            ex.printStackTrace();
        }
    }
}
