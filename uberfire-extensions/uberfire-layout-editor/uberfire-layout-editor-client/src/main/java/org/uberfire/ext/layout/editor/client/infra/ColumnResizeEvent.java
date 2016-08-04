package org.uberfire.ext.layout.editor.client.infra;


public class ColumnResizeEvent {

    private String columnID;
    private String rowID;
    private Direction direction = Direction.LEFT;

    public ColumnResizeEvent( String columnID, String rowID ) {

        this.columnID = columnID;
        this.rowID = rowID;
    }

    public String getRowID() {
        return rowID;
    }

    public String getColumnID() {
        return columnID;
    }

    public ColumnResizeEvent left() {
        this.direction = Direction.LEFT;
        return this;
    }

    public ColumnResizeEvent right() {
        this.direction = Direction.RIGHT;
        return this;
    }

    public boolean isLeft() {
        return direction == Direction.LEFT;
    }

    private enum Direction {
        LEFT, RIGHT;
    }


    public Direction getDirection() {
        return direction;
    }

}
