package org.uberfire.ext.layout.editor.client.structure;

import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.ext.layout.editor.client.util.LayoutDragComponent;

public class LayoutComponentWidgetUI implements EditorWidget {

    private final EditorWidget parent;
    private final FlowPanel container;
    private final LayoutDragComponent type;

    public LayoutComponentWidgetUI( EditorWidget parent,
                                    FlowPanel container,
                                    LayoutDragComponent type ) {
        this.parent = parent;
        this.container = container;
        this.type = type;
        parent.addChild( this );
    }

    public FlowPanel getWidget() {
        return container;
    }

    public void removeFromParent() {
        parent.removeChild( this );
    }

    @Override
    public void addChild( EditorWidget editorWidget ) {
    }

    @Override
    public void removeChild( EditorWidget editorWidget ) {

    }

    @Override
    public LayoutDragComponent getType() {
        return type;
    }
}
