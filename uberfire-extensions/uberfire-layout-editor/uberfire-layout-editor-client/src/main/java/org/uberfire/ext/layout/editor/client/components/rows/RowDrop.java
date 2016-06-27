package org.uberfire.ext.layout.editor.client.components.rows;

import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.components.columns.Column;

public class RowDrop {

    private final LayoutComponent component;
    private final int rowHashCode;
    private int originRowOldColumnHashCode;
    private final Orientation orientation;
    private Type type;
    private Column oldColumn;

    public RowDrop( LayoutComponent component, int rowHashCode, Orientation orientation ) {
        this.component = component;
        this.rowHashCode = rowHashCode;
        this.orientation = orientation;
        this.type = Type.NEW;
    }

    public int getRowHashCode() {
        return rowHashCode;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public LayoutComponent getComponent() {
        return component;
    }

    public Column getOldColumn() {
        return oldColumn;
    }

    public int getOriginRowOldColumnHashCode() {
        return originRowOldColumnHashCode;
    }

    public Type getType() {
        return type;
    }

    public boolean newComponent() {
        return type == Type.NEW;
    }

    public enum Orientation {
        BEFORE, AFTER
    }

    public enum Type {
        NEW, FROM_MOVE
    }

    public RowDrop fromMove( int originRowOldColumnHashCode, Column oldColumn ) {
        this.oldColumn = oldColumn;
        this.type = Type.FROM_MOVE;
        this.originRowOldColumnHashCode = originRowOldColumnHashCode;
        return this;
    }
}
