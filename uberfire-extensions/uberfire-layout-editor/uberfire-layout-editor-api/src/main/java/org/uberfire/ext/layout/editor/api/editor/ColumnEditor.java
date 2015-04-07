package org.uberfire.ext.layout.editor.api.editor;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ColumnEditor {

    private String span;

    private List<RowEditor> rows = new ArrayList<RowEditor>();

    private List<LayoutComponent> layoutComponents = new ArrayList<LayoutComponent>();

    public ColumnEditor() {
    }

    public ColumnEditor( String span ) {
        this.span = span;
    }

    public void addRow( RowEditor rowEditor ) {
        rows.add( rowEditor );
    }

    public void addLayoutComponent( LayoutComponent layoutComponent ) {
        layoutComponents.add( layoutComponent );
    }

    public String getSpan() {
        return span;
    }

    public List<RowEditor> getRows() {
        return rows;
    }

    public List<LayoutComponent> getLayoutComponents() {
        return layoutComponents;
    }

    public boolean hasElements() {
        return !rows.isEmpty() || !layoutComponents.isEmpty();
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ColumnEditor ) ) {
            return false;
        }

        ColumnEditor that = (ColumnEditor) o;

        if ( span != null ? !span.equals( that.span ) : that.span != null ) {
            return false;
        }
        if ( rows != null ? !rows.equals( that.rows ) : that.rows != null ) {
            return false;
        }
        return !( layoutComponents != null ? !layoutComponents.equals( that.layoutComponents ) : that.layoutComponents != null );

    }

    @Override
    public int hashCode() {
        int result = span != null ? span.hashCode() : 0;
        result = 31 * result + ( rows != null ? rows.hashCode() : 0 );
        result = 31 * result + ( layoutComponents != null ? layoutComponents.hashCode() : 0 );
        return result;
    }
}
