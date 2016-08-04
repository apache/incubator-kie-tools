package org.uberfire.ext.layout.editor.client.components.rows;

import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.components.columns.Column;

public class RowDrop {

    private final LayoutComponent component;
    private final String rowId;
    private String originRowOldColumnId;
    private final Orientation orientation;
    private Type type;
    private Column oldColumn;

    public RowDrop( LayoutComponent component, String rowId, Orientation orientation ) {
        this.component = component;
        this.rowId = rowId;
        this.orientation = orientation;
        this.type = Type.NEW;
    }

    public String getRowId() {
        return rowId;
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

    public RowDrop fromMove( String originRowOldColumnId, Column oldColumn ) {
        this.oldColumn = oldColumn;
        this.type = Type.FROM_MOVE;
        this.originRowOldColumnId = originRowOldColumnId;
        return this;
    }
}
