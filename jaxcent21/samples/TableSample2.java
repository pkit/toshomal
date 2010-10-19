package sample;

import jaxcent.*;
import java.util.*;

/**
  * Jaxcent sample.
  *
  * Shows adding rows to a table, and two user interfaces for deleting rows,
  * using HTML insertion techniques.
  *
  * Note that newer ui's for table editing, such as in-place editing,
  * as shown in TableSample.java, can often be easier to work with.
  */

public class TableSample2 extends jaxcent.JaxcentPage {

    // Get objects to match things on the page.

    HtmlTable table1 = new HtmlTable( this, "table1" );
    HtmlInputText name1 = new HtmlInputText( this, "name1" );
    HtmlInputText num1 = new HtmlInputText( this, "num1" ) {
        public void onKeyUp( String value ) {
           checkNumeric( num1, value );
        }
    };

    HtmlInputButton add1 = new HtmlInputButton( this, "add1" ) {
        public void onClick( Map pageData ) {
           onAdd1( pageData );
        }
    };

    HtmlInputButton delete1 = new HtmlInputButton( this, "delete1" ) {
        public void onClick( Map pageData ) {
           onDelete1( pageData );
        }
    };

    HtmlTable table2 = new HtmlTable( this, "table2" );
    HtmlInputText name2 = new HtmlInputText( this, "name2" );
    HtmlInputText num2 = new HtmlInputText( this, "num2" ) {
        public void onKeyUp( String value ) {
           checkNumeric( num2, value );
        }
    };

    HtmlInputButton add2 = new HtmlInputButton( this, "add2" ) {
        public void onClick( Map pageData ) {
           onAdd2( pageData );
        }
    };

    // Attributes and values for adding 3 cell rows to the table.
    static String[][] cellAttributes = {
        { "style.backgroundColor", "style.color" },
        { "style.backgroundColor" },
        { "style.backgroundColor" }
    };

    static String[][] cellValues = {
        { "brown", "white" },
        { "yellow" },
        { "cyan" }
    };

    int rowId = 1;  // Uniquely increasing id for locating rows.

    // Constructor.  If constructors are provided, there must be a default (public, no-args) constructor
    // for use by the Jaxcent framework.

    public TableSample2()
    {
        try {
            table1.setVisible( false );
            table2.setVisible( false );
            // Disable the delete1 button, it has no function yet.
            delete1.setDisabled( true );
        } catch (Jaxception ex) {
            ex.printStackTrace();
        }
    }

    // Make sure only numeric data is entered in the number fields.

    void checkNumeric( HtmlInputText field, String value )
    {
        try {
            StringBuffer buf = new StringBuffer();
            boolean badData = false;
            for ( int i = 0; i < value.length(); i++ ) {
                char ch = value.charAt( i );
                if ( Character.isDigit( ch ))
                    buf.append( ch );
                else {
                    badData = true;
                }
            }
            if ( badData ) {
                showMessageDialog( "Please only input numeric values in the number fields" );
                field.setValue( buf.toString());
            }
        } catch (Jaxception ex) {
            ex.printStackTrace();
        }
    }

    // Check if a field is not empty.

    boolean checkString( String str, String fieldName )
    {
        if ( str == null || str.trim().equals( "" )) {
            showMessageDialog( "Please enter some data in the field \"" + fieldName + "\"" );
            return false;
        }
        return true;
    }

    // Add the items from the name1 and num1 fields to table1.

    void onAdd1( Map pageData )
    {
        try {
            String name = (String) pageData.get( "name1" );
            String num = (String) pageData.get( "num1" );
            if (( ! checkString( name, "Name of Widget 1" )) || ! checkString( num, "Number of Widgets 1" ))
                return;
            HtmlTableRow row = table1.insertRow( -1, new String[]{ name, num, "<INPUT NAME=deleteCheckbox VALUE=" + rowId + " TYPE=CHECKBOX>" },
                                                     cellAttributes, cellValues );
            row.setId( "row" + rowId );
            rowId++;
            delete1.setDisabled( false );  // We have items, we can delete them now.
            name1.setValue( "" );
            num1.setValue( "" );
            table1.setVisible( true );
        } catch (Jaxception ex) {
            ex.printStackTrace();
        }
    }

    // Delete from table 1.  Retrieves all checked values for "deleteCheckbox", and
    // deletes matching rows.

    void onDelete1( Map pageData )
    {
        try {
            // Find all checkboxes that are deleted.
            String deleted = (String) pageData.get( "deleteCheckbox" );
            if ( deleted == null || deleted.equals( "" )) {
                showMessageDialog( "No delete checkboxes have been checked." );
                return;
            }
            // The data is a comma separated list in case more than one have been checked.
            StringTokenizer tokenizer = new StringTokenizer( deleted, "," );
            while ( tokenizer.hasMoreTokens()) {
                String rowId = tokenizer.nextToken();
                // The row has an id "row" + rowId
                deleteElementById( "row" + rowId );
            }
            if ( table1.getNumRows() <= 1 )
                delete1.setDisabled( true );
        } catch (Jaxception ex) {
            ex.printStackTrace();
        }
    }

    // DeleteButton class allows adding a button handler while saving the row for later deletion.

    private class DeleteButton extends HtmlInputButton {
        HtmlTableRow row;

        DeleteButton( JaxcentPage page, String id, HtmlTableRow r )
        {
            super( page, id );
            this.row = r;
        }

        protected void onClick()
        {
            try {
                row.delete();  // Delete the row.
            } catch (Jaxception ex) {
                ex.printStackTrace();
            }
        }
    }

    // Add method for second table.

    void onAdd2( Map pageData )
    {
        try {
            String name = (String) pageData.get( "name2" );
            String num = (String) pageData.get( "num2" );
            if (( ! checkString( name, "Name of Widget 2" )) || ! checkString( num, "Number of Widgets 2" ))
                return;
            HtmlTableRow row = table2.insertRow( -1, new String[]{ name, num, "<INPUT TYPE=BUTTON ID=btn" + rowId + " VALUE=Delete>" },
                                                 cellAttributes, cellValues );

            // Search for the button, using a class that has a click-handler and that will save the "rowId"
            new DeleteButton( this, "btn" + rowId, row );

            rowId++;
            name2.setValue( "" );
            num2.setValue( "" );
            table2.setVisible( true );
        } catch (Jaxception ex) {
            ex.printStackTrace();
        }
    }
}
