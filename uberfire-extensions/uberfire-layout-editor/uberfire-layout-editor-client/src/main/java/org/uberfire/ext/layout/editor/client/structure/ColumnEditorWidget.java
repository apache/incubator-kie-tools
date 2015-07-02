package org.uberfire.ext.layout.editor.client.structure;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.ComplexPanel;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;

public class ColumnEditorWidget implements EditorWidget {

    private final RowEditorWidget parent;

    private final String span;

    private final ComplexPanel container;

    private List<EditorWidget> childs = new ArrayList<EditorWidget>();

    public ColumnEditorWidget( RowEditorWidget row,
                               ComplexPanel container,
                               String span ) {
        this.container = container;
        this.parent = row;
        this.span = span;
        row.addChild( this );
    }

    public EditorWidget getParent() {
        return parent;
    }

    public ComplexPanel getWidget() {
        return container;
    }

    @Override
    public void addChild( EditorWidget editorWidget ) {
        childs.add( editorWidget );
    }

    @Override
    public void removeChild( EditorWidget editorWidget ) {
        childs.remove( editorWidget );
    }

    public List<EditorWidget> getChilds() {
        return childs;
    }

    public String getSpan() {
        return span;
    }

    @Override
    public LayoutDragComponent getType() {
        return null;
    }

    public boolean childsIsRowEditorWidgetUI() {
        return getChilds().get( 0 ) instanceof RowEditorWidget;
    }
}