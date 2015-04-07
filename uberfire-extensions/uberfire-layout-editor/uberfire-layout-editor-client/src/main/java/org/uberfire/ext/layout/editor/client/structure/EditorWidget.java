package org.uberfire.ext.layout.editor.client.structure;

import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.ext.layout.editor.client.util.LayoutDragComponent;

public interface EditorWidget {

    public FlowPanel getWidget();

    public void addChild( EditorWidget editorWidget );

    public void removeChild( EditorWidget editorWidget );

    LayoutDragComponent getType();
}
