package org.uberfire.ext.layout.editor.api.editor;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RowEditor {

    private List<String> rowSpam = new ArrayList<String>();

    private List<ColumnEditor> columnEditors = new ArrayList<ColumnEditor>();

    public RowEditor() {

    }

    public RowEditor( List<String> rowSpam ) {
        this.rowSpam = rowSpam;
    }

    public List<ColumnEditor> getColumnEditors() {
        return columnEditors;
    }

    public void add( ColumnEditor columnEditor ) {
        columnEditors.add( columnEditor );
    }

    public List<String> getRowSpam() {
        return rowSpam;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof RowEditor ) ) {
            return false;
        }

        RowEditor rowEditor = (RowEditor) o;

        if ( columnEditors != null ? !columnEditors.equals( rowEditor.columnEditors ) : rowEditor.columnEditors != null ) {
            return false;
        }
        if ( rowSpam != null ? !rowSpam.equals( rowEditor.rowSpam ) : rowEditor.rowSpam != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = rowSpam != null ? rowSpam.hashCode() : 0;
        result = 31 * result + ( columnEditors != null ? columnEditors.hashCode() : 0 );
        return result;
    }
}
