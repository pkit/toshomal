package sample;

import jaxcent.*;
import java.util.*;

/**
  * Jaxcent sample.
  *
  * Demonstrates in-place editing, row deletions, listening
  * to in-place edits and row deletions, inserting items.
  */

public class TableSample extends jaxcent.JaxcentPage {

    // Objects to match HTML items on page.

    HtmlTable table = new HtmlTable( this, "table" ) {
        protected void onCellEdited( int rowIndex, int cellIndex, String oldContent, String newContent )
        {
            message( "Cell [" + rowIndex + "][" + cellIndex + "] was modified to \"" + newContent + "\"" );
        }
        protected void onRowDeleted( int rowIndex )
        {
            message( "Row " + rowIndex + " was deleted" );
        }
    };

    HtmlInputButton addItemButton = new HtmlInputButton( this, "addItem" ) {
        protected void onClick( java.util.Map data ) {
            addItem( (String) data.get( "item" ), (String) data.get( "description" ));
        }
    };

    HtmlTable output = new HtmlTable( this, "output" );

    HtmlInputText nameField = new HtmlInputText( this, SearchType.searchByName, "item" );
    HtmlInputText descriptionField = new HtmlInputText( this, SearchType.searchByName, "description" );


    static String[][] sampleData = {
       { "harry-potter-1", "Harry Potter and the Sorcerer's Stone" },
       { "harry-potter-2", "Harry Potter and the Chamber of Secrets" },
       { "mac-pro-17", "MacBook Pro 17\"" },
       { "mccoy-1", "McCoy Potter Bird Wall Pocket" },
       { "sony-dsc", "Sony Cyber-Shot 7.2 MP Camera" },
    };

    public TableSample()
    {
        try {
            // Add in a header.
            table.insertRow( 0, new String[]{ "<B>Item</B>", "<B>Description</B>", "&nbsp;" } );

            // Put in some initial data
            for ( int i = 0; i < sampleData.length; i++ )
                table.insertRow( -1, sampleData[i] );

            // Enable in-place editing.
            table.enableCellEditing( 1, 0, -1, 1, false, false, null );

            // Add "delete" buttons.
            table.addDeleteButtons( 1, -1, null, null );

        } catch (Jaxception ex) {
            ex.printStackTrace();
        }
    }

    void message( String str )
    {
        try {
            output.insertRow( -1, new String[]{ "<I>" + str + "</I>" } );
        } catch (Jaxception ex) {
            ex.printStackTrace();
        }
    }

    void addItem( String item, String description )
    {
        try {
            if ( item == null || item.equals( "" )) {
                showMessageDialog( "Item field is empty" );
                return;
            }
            if ( description == null || description.equals( "" )) {
                showMessageDialog( "Description field is empty" );
                return;
            }
            table.insertRow( -1, new String[]{ item, description } );
            nameField.setValue( "" );
            descriptionField.setValue( "" );
            
            // Enable cell editing.

            table.enableCellEditing( -1, 0, -1, 1, false, false, null );

            // Add "delete" button.
            table.addDeleteButtons( -1, -1, null, null );

        } catch (Jaxception ex) {
            ex.printStackTrace();
        }
    }
}
