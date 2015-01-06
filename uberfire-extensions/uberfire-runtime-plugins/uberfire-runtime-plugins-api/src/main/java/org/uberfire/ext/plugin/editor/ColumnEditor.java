package org.uberfire.ext.plugin.editor;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ColumnEditor {

    private String span;

    private List<RowEditor> rows = new ArrayList<RowEditor>();

    private List<ScreenEditor> screens = new ArrayList<ScreenEditor>();

    private List<HTMLEditor> htmls = new ArrayList<HTMLEditor>();

    public ColumnEditor() {
    }

    public ColumnEditor( String span ) {
        this.span = span;
    }

    public void addRow( RowEditor rowEditor ) {
        rows.add( rowEditor );
    }

    public void addScreen( ScreenEditor screenEditor ) {
        screens.add( screenEditor );
    }

    public void addHTML( HTMLEditor htmlEditor ) {
        htmls.add( htmlEditor );
    }

    public String getSpan() {
        return span;
    }

    public List<RowEditor> getRows() {
        return rows;
    }

    public List<ScreenEditor> getScreens() {
        return screens;
    }

    public List<HTMLEditor> getHtmls() {
        return htmls;
    }

    public boolean hasElements() {
        return !rows.isEmpty() || !screens.isEmpty() || !htmls.isEmpty();
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

        if ( htmls != null ? !htmls.equals( that.htmls ) : that.htmls != null ) {
            return false;
        }
        if ( rows != null ? !rows.equals( that.rows ) : that.rows != null ) {
            return false;
        }
        if ( screens != null ? !screens.equals( that.screens ) : that.screens != null ) {
            return false;
        }
        if ( span != null ? !span.equals( that.span ) : that.span != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = span != null ? span.hashCode() : 0;
        result = 31 * result + ( rows != null ? rows.hashCode() : 0 );
        result = 31 * result + ( screens != null ? screens.hashCode() : 0 );
        result = 31 * result + ( htmls != null ? htmls.hashCode() : 0 );
        return result;
    }
}
