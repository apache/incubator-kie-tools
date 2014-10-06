package org.kie.uberfire.perspective.editor.client.structure;

import com.google.gwt.user.client.ui.FlowPanel;

public class ScreenEditor implements EditorWidget {

    private final EditorWidget parent;
    private final FlowPanel container;

    public ScreenEditor( EditorWidget parent,
                         FlowPanel container ) {
        this.parent = parent;
        this.container = container;
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

}
