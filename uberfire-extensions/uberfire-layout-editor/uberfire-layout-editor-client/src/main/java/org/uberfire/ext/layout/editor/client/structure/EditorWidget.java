package org.uberfire.ext.layout.editor.client.structure;

import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;

public interface EditorWidget {

    FlowPanel getWidget();

    EditorWidget getParent();

    void addChild(EditorWidget editorWidget);

    void removeChild(EditorWidget editorWidget);

    LayoutDragComponent getType();
}
