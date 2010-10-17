package sample;

import jaxcent.*;

public class DragDropSample extends jaxcent.JaxcentPage {

    // The two tables.

    HtmlTable table1 = new HtmlTable( this, "table1" );
    HtmlTable table2 = new HtmlTable( this, "table2" );

    // For locating rows and attaching a drag-drop handler,
    // we define a class with onDragDrop over-ridden.

    class DropTargetRow extends HtmlTableRow {
        DropTargetRow( JaxcentPage page, HtmlTable table, int index )
        {
            super( page, table, index );
        }

        protected void onDragDrop( JaxcentHtmlElement dragDropSrc )
        {
            try {
                // If the drag-drop source is a row, insert it before this one.
                if ( dragDropSrc instanceof HtmlTableRow )
                    dragDropSrc.insertBefore( this );
            } catch (Jaxception ex ) {
                ex.printStackTrace();
            }
        }
    }

    // This para serves as a "Trashcan"!  Can be changed
    // to HtmlImage if page has a trashcan image.

    HtmlPara p = new HtmlPara( this, "trash" ) {
        protected void onDragDrop( JaxcentHtmlElement dragDropSrc )
        {
            try {
                if ( dragDropSrc instanceof HtmlTableRow )
                    dragDropSrc.deleteElement();
            } catch (Jaxception ex ) {
                ex.printStackTrace();
            }
        }
    };

    HtmlPara justAPara = new HtmlPara( this, "justAPara" );
        // This para is set to draggable, but drop-targets don't do anything with it!
        // Just shows dragging a para.


    // Constructor.  Locate rows, assign to class objects, and set items to be draggable.

    public DragDropSample()
    {
      try {
         DropTargetRow row;
         for ( int i = 0; i < 6; i++ ) {

             // Create new objects that will locate and connect to
             // the table rows.

             row = new DropTargetRow( this, table1, i );

             // The rows are both sources and targets.  Set draggable.
             row.setDraggable( true );

             // Same for table2 rows.

             row = new DropTargetRow( this, table2, i );
             row.setDraggable( true );
         }


         // Also set "Just a Para" to be draggable!

         justAPara.setDraggable( true );

      } catch (Jaxception ex) {
         ex.printStackTrace();
      }
    }
}
