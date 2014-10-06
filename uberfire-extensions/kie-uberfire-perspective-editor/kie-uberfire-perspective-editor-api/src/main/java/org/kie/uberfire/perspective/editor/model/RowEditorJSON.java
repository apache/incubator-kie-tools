package org.kie.uberfire.perspective.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RowEditorJSON {

    private List<String> rowSpam = new ArrayList<String>();

    private List<ColumnEditorJSON> columnEditorsJSON = new ArrayList<ColumnEditorJSON>();

    public RowEditorJSON() {

    }

    public RowEditorJSON( List<String> rowSpam ) {
        this.rowSpam = rowSpam;
    }

    public List<ColumnEditorJSON> getColumnEditorsJSON() {
        return columnEditorsJSON;
    }

    public void add( ColumnEditorJSON columnEditor ) {
        columnEditorsJSON.add( columnEditor );
    }

    public List<String> getRowSpam() {
        return rowSpam;
    }
}
