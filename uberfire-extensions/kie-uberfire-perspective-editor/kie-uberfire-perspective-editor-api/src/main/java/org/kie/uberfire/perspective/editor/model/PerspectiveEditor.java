package org.kie.uberfire.perspective.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PerspectiveEditor {

    private String name;

    private List<RowEditor> rows = new ArrayList<RowEditor>(  );

    public PerspectiveEditor(){

    }

    public PerspectiveEditor( String name ) {
        this.name = name;
    }


    public void addRow( RowEditor rowEditor ) {
        rows.add(rowEditor);
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
}
