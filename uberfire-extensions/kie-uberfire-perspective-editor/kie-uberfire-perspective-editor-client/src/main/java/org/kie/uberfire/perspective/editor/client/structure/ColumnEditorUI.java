package org.kie.uberfire.perspective.editor.client.structure;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;

public class ColumnEditorUI implements EditorWidget {

    private final RowEditorWidgetUI parent;

    private final String span;

    private final FlowPanel container;

    private List<EditorWidget> childs = new ArrayList<EditorWidget>(  );

    public ColumnEditorUI( RowEditorWidgetUI row,
                           FlowPanel container,
                           String span ) {
        this.container = container;
        this.parent = row;
        this.span = span;
        row.addChild( this );
    }

    public FlowPanel getWidget() {
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

}
