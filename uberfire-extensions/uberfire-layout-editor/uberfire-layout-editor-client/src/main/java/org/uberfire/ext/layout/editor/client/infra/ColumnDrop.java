package org.uberfire.ext.layout.editor.client.infra;

import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.components.columns.Column;

public class ColumnDrop {

    private final int endHash;
    private final Orientation orientation;
    private LayoutComponent component;
    private Type type;
    private Column oldColumn;

    public ColumnDrop( LayoutComponent component, int endHash,
                       Orientation orientation ) {
        this.component = component;
        this.endHash = endHash;
        this.orientation = orientation;
        this.type = Type.NEW;
    }

    public enum Orientation {
        LEFT, RIGHT, UP, DOWN
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public int getEndHash() {
        return endHash;
    }

    public LayoutComponent getComponent() {
        return component;
    }

    public boolean isASideDrop() {
        return getOrientation() == ColumnDrop.Orientation.LEFT ||
                getOrientation() == ColumnDrop.Orientation.RIGHT;
    }

    public boolean isALeftDrop() {
        return getOrientation() == ColumnDrop.Orientation.LEFT;
    }

    public boolean isADownDrop() {
        return getOrientation() == ColumnDrop.Orientation.DOWN;
    }

    public Column getOldColumn() {
        return oldColumn;
    }

    public enum Type {
        NEW, FROM_MOVE
    }

    public boolean newComponent() {
        return type == Type.NEW;
    }

    public ColumnDrop fromMove( Column oldColumn ) {
        this.oldColumn = oldColumn;
        this.type = Type.FROM_MOVE;
        return this;
    }

}