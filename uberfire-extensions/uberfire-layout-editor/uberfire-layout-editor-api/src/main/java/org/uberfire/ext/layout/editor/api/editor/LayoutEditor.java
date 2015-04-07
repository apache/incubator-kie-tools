package org.uberfire.ext.layout.editor.api.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class LayoutEditor {

    private String name;

    private Map<String, String> layoutProperties = new HashMap<String, String>();

    private List<RowEditor> rows = new ArrayList<RowEditor>();

    public LayoutEditor( String name ) {
        this.name = name;
    }

    public LayoutEditor( String name,
                         Map<String, String> layoutProperties ) {
        this.name = name;
        this.layoutProperties = layoutProperties;
    }

    public LayoutEditor() {
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

    public Map<String, String> getLayoutProperties() {
        return layoutProperties;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof LayoutEditor ) ) {
            return false;
        }

        LayoutEditor that = (LayoutEditor) o;

        if ( name != null ? !name.equals( that.name ) : that.name != null ) {
            return false;
        }
        if ( layoutProperties != null ? !layoutProperties.equals( that.layoutProperties ) : that.layoutProperties != null ) {
            return false;
        }
        return !( rows != null ? !rows.equals( that.rows ) : that.rows != null );

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + ( layoutProperties != null ? layoutProperties.hashCode() : 0 );
        result = 31 * result + ( rows != null ? rows.hashCode() : 0 );
        return result;
    }

    public static LayoutEditor defaultContent( String pluginName ) {

        final LayoutEditor layoutEditor = new LayoutEditor( pluginName );
        final RowEditor rowEditor = new RowEditor( new ArrayList<String>() {{
            add( "12" );
        }} );
        rowEditor.add( new ColumnEditor( "12" ) );
        layoutEditor.addRow( rowEditor );

        return layoutEditor;
    }

}
