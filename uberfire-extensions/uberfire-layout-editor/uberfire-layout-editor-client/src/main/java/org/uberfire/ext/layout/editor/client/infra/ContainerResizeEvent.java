package org.uberfire.ext.layout.editor.client.infra;


public class ContainerResizeEvent {

    private int columnHashCode;
    private int rowHashCode;
    private Direction direction = Direction.LEFT;

    public ContainerResizeEvent( int columnHashCode , int rowHashCode) {

        this.columnHashCode = columnHashCode;
        this.rowHashCode = rowHashCode;
    }

    public int getRowHashCode() {
        return rowHashCode;
    }

    public int getColumnHashCode() {
        return columnHashCode;
    }

    public ContainerResizeEvent left() {
        this.direction = Direction.LEFT;
        return this;
    }

    public ContainerResizeEvent right() {
        this.direction = Direction.RIGHT;
        return this;
    }

    public boolean isLeft(){
        return direction == Direction.LEFT;
    }

    private enum Direction {
        LEFT, RIGHT;
    }


    public Direction getDirection() {
        return direction;
    }


    public ContainerResizeEvent(){};
}
