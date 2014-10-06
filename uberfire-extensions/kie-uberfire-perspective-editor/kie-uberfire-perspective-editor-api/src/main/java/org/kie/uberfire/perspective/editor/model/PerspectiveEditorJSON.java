package org.kie.uberfire.perspective.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PerspectiveEditorJSON {

    private String name;

    private List<RowEditorJSON> rows = new ArrayList<RowEditorJSON>(  );

    public PerspectiveEditorJSON(){

    }

    public PerspectiveEditorJSON( String name ) {
        this.name = name;
    }


    public void addRowJSON( RowEditorJSON rowEditorJSON ) {
        rows.add(rowEditorJSON);
    }

    public List<RowEditorJSON> getRows() {
        return rows;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }
}
