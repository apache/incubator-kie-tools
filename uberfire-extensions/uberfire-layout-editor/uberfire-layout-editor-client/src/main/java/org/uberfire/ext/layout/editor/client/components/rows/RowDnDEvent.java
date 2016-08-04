package org.uberfire.ext.layout.editor.client.components.rows;

public class RowDnDEvent {

    private final String rowIdBegin;
    private final String rowIdEnd;
    private final RowDrop.Orientation orientation;


    public RowDnDEvent( String rowIdBegin, String rowIdEnd, RowDrop.Orientation orientation ) {
        this.rowIdBegin = rowIdBegin;
        this.rowIdEnd = rowIdEnd;
        this.orientation = orientation;
    }

    public RowDrop.Orientation getOrientation() {
        return orientation;
    }

    public String getRowIdBegin() {
        return rowIdBegin;
    }

    public String getRowIdEnd() {
        return rowIdEnd;
    }
}
