package org.uberfire.ext.layout.editor.client.infra;

import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.components.columns.Column;
import org.uberfire.ext.layout.editor.client.components.rows.RowDnDEvent;
import org.uberfire.ext.layout.editor.client.components.rows.RowDrop;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@ApplicationScoped
public class DnDManager {

    @Inject
    Event<ColumnResizeEvent> columnResizeEvent;

    @Inject
    Event<RowDnDEvent> rowDnDEvent;


    private boolean isOnColumnResize;
    private int columnHashCode;
    private int beginColumnX;

    private boolean isOnRowMove;
    private int rowHashBegin;

    private boolean isOnComponentMove;
    private LayoutComponent layoutComponentMove;
    private int rowHashCode;
    private Column draggedColumn;

    public void beginColumnResize( int columnHashCode, int beginX ) {
        this.columnHashCode = columnHashCode;
        this.isOnColumnResize = true;
        this.beginColumnX = beginX;
    }

    public void endColumnResize( int rowHashCodeEnd, int endX ) {
        if ( isOnColumnResize ) {
            handleColumnResize( endX, rowHashCodeEnd );
            this.isOnColumnResize = false;
        }
    }

    public void resetColumnResize() {
        if ( isOnColumnResize ) {
            this.isOnColumnResize = false;
        }
    }

    private boolean left( int endX ) {
        return endX < beginColumnX;
    }

    private void handleColumnResize( int endX, int rowHashCodeEnd ) {
        if ( left( endX ) ) {
            columnResizeEvent.fire( new ColumnResizeEvent( columnHashCode, rowHashCodeEnd ).left() );
        } else {
            columnResizeEvent.fire( new ColumnResizeEvent( columnHashCode, rowHashCodeEnd ).right() );
        }
    }

    public void beginRowMove( int rowHashBegin ) {
        if ( !isOnColumnResize ) {
            this.rowHashBegin = rowHashBegin;
            this.isOnRowMove = true;
        }
    }

    public void endRowMove( int rowHashCodeEnd, RowDrop.Orientation orientation ) {
        if ( isOnRowMove ) {
            rowDnDEvent.fire( new RowDnDEvent( rowHashBegin, rowHashCodeEnd, orientation ) );
            isOnRowMove = false;
        }
    }

    public void dragEndMove() {
        this.isOnRowMove = false;
    }

    public Column getDraggedColumn() {
        return draggedColumn;
    }

    public void endComponentMove() {
        if ( isOnComponentMove ) {
            isOnComponentMove = false;
        }
    }


    public boolean isOnRowMove() {
        return isOnRowMove;
    }

    public boolean isOnComponentMove() {
        return isOnComponentMove;
    }

    public boolean canMoveRow() {
        return !isOnComponentMove();
    }

    public void dragComponent( LayoutComponent layoutComponentMove, int rowHashCode, Column draggedColumn ) {
        this.layoutComponentMove = layoutComponentMove;
        this.rowHashCode = rowHashCode;
        this.draggedColumn = draggedColumn;
        this.isOnComponentMove = true;
    }

    public void dragEndComponent() {
        this.isOnComponentMove = false;
    }

    public LayoutComponent getLayoutComponentMove() {
        return layoutComponentMove;
    }

    public int getRowHashCode() {
        return rowHashCode;
    }
}
