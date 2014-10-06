package org.kie.uberfire.perspective.editor.client.structure;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;

public class RowEditor implements EditorWidget {

    private final EditorWidget parent;
    private final FlowPanel container;
    private List<String> rowSpans = new ArrayList<String>(  );

    private List<EditorWidget> columnEditors = new ArrayList<EditorWidget>();

    public RowEditor( EditorWidget parent,
                      FlowPanel container,
                      String rowSpamString ) {
        this.parent = parent;
        this.container = container;
        parseRowSpanString( rowSpamString );
        parent.addChild( this );
    }

    public RowEditor( EditorWidget parent,
                      FlowPanel container,
                      List<String> rowSpans ) {
        this.parent = parent;
        this.container = container;
        this.rowSpans = rowSpans;
        parent.addChild( this );
    }


    public FlowPanel getWidget() {
        return container;
    }

    public List<String> getRowSpans() {
        return rowSpans;
    }

    private void parseRowSpanString( String rowSpamString ) {
        //ederign validate
        String[] spans = rowSpamString.split( " " );
        for ( String span : spans ) {
            rowSpans.add( span );
        }

    }

    public void addChild( EditorWidget columnEditor ) {
        columnEditors.add( columnEditor );
    }

    public void removeFromParent() {
        parent.removeChild( this );
    }

    @Override
    public void removeChild( EditorWidget editorWidget ) {
        columnEditors.remove( editorWidget );
    }


    public List<EditorWidget> getColumnEditors() {
        return columnEditors;
    }

}
