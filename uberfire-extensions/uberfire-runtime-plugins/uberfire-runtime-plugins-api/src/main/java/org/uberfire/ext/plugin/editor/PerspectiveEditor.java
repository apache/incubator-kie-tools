package org.uberfire.ext.plugin.editor;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PerspectiveEditor {

    private String name;

    private List<String> tags = new ArrayList<String>();

    private List<RowEditor> rows = new ArrayList<RowEditor>();

    public PerspectiveEditor( String name,
                              List<String> tags ) {

        this.name = name;
        this.tags = tags;
    }

    public PerspectiveEditor() {
    }

    public void addRow( RowEditor rowEditor ) {
        rows.add( rowEditor );
    }

    public List<RowEditor> getRows() {
        return rows;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public boolean isAValidPerspective() {
        return name != null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + ( tags != null ? tags.hashCode() : 0 );
        for ( RowEditor row : rows ) {
            result = 31 * result + ( row != null ? row.hashCode() : 0 );
        }
        return result;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof PerspectiveEditor ) ) {
            return false;
        }

        PerspectiveEditor that = (PerspectiveEditor) o;

        if ( name != null ? !name.equals( that.name ) : that.name != null ) {
            return false;
        }
        if ( rows != null ? !rows.equals( that.rows ) : that.rows != null ) {
            return false;
        }
        if ( tags != null ? !tags.equals( that.tags ) : that.tags != null ) {
            return false;
        }

        return true;
    }

}
