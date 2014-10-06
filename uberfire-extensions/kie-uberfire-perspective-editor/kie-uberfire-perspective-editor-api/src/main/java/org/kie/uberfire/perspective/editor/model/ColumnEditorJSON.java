package org.kie.uberfire.perspective.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ColumnEditorJSON {

    private String span;

    private List<RowEditorJSON> rows = new ArrayList<RowEditorJSON>();

    private List<ScreenEditorJSON> screens = new ArrayList<ScreenEditorJSON>();

    public ColumnEditorJSON() {
    }

    public ColumnEditorJSON( String span ) {
        this.span = span;
    }

    public void addRowJSON( RowEditorJSON rowEditorJSON ) {
        rows.add( rowEditorJSON );
    }

    public void addScreenJSON( ScreenEditorJSON screenEditorJSON ) {
        screens.add( screenEditorJSON );
    }

    public String getSpan() {
        return span;
    }

    public List<RowEditorJSON> getRows() {
        return rows;
    }

    public List<ScreenEditorJSON> getScreens() {
        return screens;
    }
}
