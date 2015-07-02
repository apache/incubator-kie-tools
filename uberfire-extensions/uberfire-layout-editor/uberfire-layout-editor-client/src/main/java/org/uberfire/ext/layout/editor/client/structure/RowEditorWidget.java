package org.uberfire.ext.layout.editor.client.structure;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.ComplexPanel;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;

public class RowEditorWidget implements EditorWidget {

    private final EditorWidget parent;
    private final ComplexPanel container;
    private List<String> rowSpans = new ArrayList<String>();

    private List<EditorWidget> columnEditors = new ArrayList<EditorWidget>();

    public RowEditorWidget( EditorWidget parent,
                            ComplexPanel container,
                            String rowSpamString ) {
        this.parent = parent;
        this.container = container;
        parseRowSpanString( rowSpamString );
        parent.addChild( this );
    }

    public RowEditorWidget( EditorWidget parent,
                            ComplexPanel container,
                            List<String> rowSpans ) {
        this.parent = parent;
        this.container = container;
        this.rowSpans = rowSpans;
        parent.addChild( this );
    }

    public EditorWidget getParent() {
        return parent;
    }

    public ComplexPanel getWidget() {
        return container;
    }

    public List<String> getRowSpans() {
        return rowSpans;
    }

    private void parseRowSpanString( String rowSpamString ) {
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

    @Override
    public LayoutDragComponent getType() {
        return null;
    }

}