package org.uberfire.ext.layout.editor.client.structure;

import com.google.gwt.user.client.ui.ComplexPanel;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;

public class ComponentEditorWidget implements EditorWidget {

    private final EditorWidget parent;
    private final ComplexPanel container;
    private final LayoutDragComponent type;

    public ComponentEditorWidget( final EditorWidget parent,
                                  final ComplexPanel container,
                                  final LayoutDragComponent type ) {
        this.parent = parent;
        this.container = container;
        this.type = type;
        parent.addChild( this );
    }

    @Override
    public EditorWidget getParent() {
        return parent;
    }

    @Override
    public ComplexPanel getWidget() {
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