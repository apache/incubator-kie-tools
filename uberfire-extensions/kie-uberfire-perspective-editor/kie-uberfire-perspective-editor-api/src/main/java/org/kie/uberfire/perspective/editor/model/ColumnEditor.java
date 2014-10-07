package org.kie.uberfire.perspective.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ColumnEditor {

    private String span;

    private List<RowEditor> rows = new ArrayList<RowEditor>();

    private List<ScreenEditor> screens = new ArrayList<ScreenEditor>();

    public ColumnEditor() {
    }

    public ColumnEditor( String span ) {
        this.span = span;
    }

    public void addRowJSON( RowEditor rowEditorJSON ) {
        rows.add( rowEditorJSON );
    }

    public void addScreenJSON( ScreenEditor screenEditorJSON ) {
        screens.add( screenEditorJSON );
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
}
