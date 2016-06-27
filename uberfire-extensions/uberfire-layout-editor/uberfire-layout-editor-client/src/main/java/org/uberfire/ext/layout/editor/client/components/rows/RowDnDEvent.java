package org.uberfire.ext.layout.editor.client.components.rows;

public class RowDnDEvent {

    private final int rowHashEndBegin;
    private final int rowHashCodeEnd;
    private final RowDrop.Orientation orientation;


    public RowDnDEvent( int rowHashBegin, int rowHashCodeEnd, RowDrop.Orientation orientation ){
        this.rowHashEndBegin = rowHashBegin;
        this.rowHashCodeEnd = rowHashCodeEnd;
        this.orientation = orientation;
    }

    public RowDrop.Orientation getOrientation() {
        return orientation;
    }

    public int getRowHashCodeBegin() {
        return rowHashEndBegin;
    }


    public int getRowHashCodeEnd() {
        return rowHashCodeEnd;
    }
}
