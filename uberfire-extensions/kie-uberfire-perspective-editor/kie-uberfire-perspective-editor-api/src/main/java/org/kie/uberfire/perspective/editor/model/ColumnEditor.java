package org.kie.uberfire.perspective.editor.model;

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
}
