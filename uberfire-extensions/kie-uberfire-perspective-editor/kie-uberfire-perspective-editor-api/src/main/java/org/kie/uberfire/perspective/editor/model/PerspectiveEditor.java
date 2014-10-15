package org.kie.uberfire.perspective.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PerspectiveEditor {

    private String name;

    private List<String> tags;

    private List<RowEditor> rows = new ArrayList<RowEditor>(  );

    public PerspectiveEditor( String name,
                              List<String> tags ){

        this.name = name;
        this.tags = tags;
    }

    public PerspectiveEditor(){}

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

    public List<String> getTags() {
        return tags;
    }
}
