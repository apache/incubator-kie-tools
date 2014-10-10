package org.kie.uberfire.perspective.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RowEditor {

    private List<String> rowSpam = new ArrayList<String>();

    private List<ColumnEditor> columnEditors = new ArrayList<ColumnEditor>();

    public RowEditor() {

    }

    public RowEditor( List<String> rowSpam ) {
        this.rowSpam = rowSpam;
    }

    public List<ColumnEditor> getColumnEditors() {
        return columnEditors;
    }

    public void add( ColumnEditor columnEditor ) {
        columnEditors.add( columnEditor );
    }

    public List<String> getRowSpam() {
        return rowSpam;
    }
}
