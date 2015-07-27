package org.uberfire.ext.layout.editor.api.editor;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class LayoutRow {

    private List<String> rowSpam = new ArrayList<String>();

    private List<LayoutColumn> layoutColumns = new ArrayList<LayoutColumn>();

    public LayoutRow() {

    }

    public LayoutRow(List<String> rowSpam) {
        this.rowSpam = rowSpam;
    }

    public List<LayoutColumn> getLayoutColumns() {
        return layoutColumns;
    }

    public void add( LayoutColumn layoutColumn) {
        layoutColumns.add(layoutColumn);
    }

    public List<String> getRowSpam() {
        return rowSpam;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof LayoutRow) ) {
            return false;
        }

        LayoutRow layoutRow = (LayoutRow) o;

        if ( layoutColumns != null ? !layoutColumns.equals( layoutRow.layoutColumns) : layoutRow.layoutColumns != null ) {
            return false;
        }
        if ( rowSpam != null ? !rowSpam.equals( layoutRow.rowSpam ) : layoutRow.rowSpam != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = rowSpam != null ? rowSpam.hashCode() : 0;
        result = 31 * result + ( layoutColumns != null ? layoutColumns.hashCode() : 0 );
        return result;
    }
}
