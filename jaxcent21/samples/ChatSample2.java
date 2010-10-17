package sample;

import jaxcent.*;
import java.util.*;
import java.text.*;

/**
  * Jaxcent chat sample 2.  Similar to the original chat sample,
  * but uses the Jaxcent simple-publish-and-subscribe model.
  *
  */

public class ChatSample2 extends jaxcent.JaxcentPage {

    static final int MAX_MESSAGES = 24;  // How many messages to keep around.

    // Structure to store the messages

    private class Message {
        String time;
        String from;
        String message;
    }

    // The topic

    static JaxcentTopic topic = new JaxcentTopic( MAX_MESSAGES, 0 );

    // Locate the table, the DIV containing it, the message input field,
    // and add a handler to the "send" button.

    HtmlTable messageList = new HtmlTable( this, "messageList" );
    HtmlDiv messageDiv = new HtmlDiv( this, "messageDiv" );
    HtmlInputText messageBox = new HtmlInputText( this, "message" );

    // The submit handler.

    HtmlForm sendForm = new HtmlForm( this, "sendForm" ) {
        public void onSubmit( Map pageData ) {
           onSend( pageData );
        }
    };


    // The last message we have added to the table.

    JaxcentTopicItem lastMessage = null;

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
                JaxcentTopicItem lastItem = null;

                // Post any already available messages.
                JaxcentTopicItem item = topic.getItem( lastItem, 0 );
                while ( item != null ) {
                     lastItem = item;
                     Message message = (Message) item.getItem();
                     messageList.insertRow( -1,
                                    new String[]{ "<I>" + message.time + "</I>", 
                                                  "<B>" + message.from + "</B>",
                                                  message.message },
                                    cellAttributes, cellValues );
                     item = topic.getItem( lastItem, 0 );
                }
                messageList.deleteFromTop( - MAX_MESSAGES );
                messageDiv.scrollToBottom();

                while ( running ) {
                    item = topic.getItem( lastItem );
                    lastItem = item;
                    Message message = (Message) item.getItem();
                    messageList.insertRow( -1,
                                    new String[]{ "<I>" + message.time + "</I>", 
                                                  "<B>" + message.from + "</B>",
                                                  message.message },
                                    cellAttributes, cellValues );
                    // If the table has too many rows, delete any extra from the top.
                    messageList.deleteFromTop( - MAX_MESSAGES );
                    messageDiv.scrollToBottom();
                }
            } catch (InterruptedException ex) {
            } catch (Jaxception ex) {
                ex.printStackTrace();
            }
        }
    }

    MessageThread messageThread;


    // Start the message thread in the constructor

    public ChatSample2()
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

        // Post the message to the topic.
        topic.postItem( msg );

        // Clear out the message field and set focus to it.
        try {
            messageBox.setValue( "" );
            messageBox.focus();
        } catch (Jaxception ex) {
            ex.printStackTrace();
        }
    }
}
