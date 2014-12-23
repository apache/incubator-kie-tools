package org.uberfire.ext.plugin.client.perspective.editor.structure;

import com.google.gwt.user.client.ui.FlowPanel;

public interface EditorWidget {

    public FlowPanel getWidget();

    public void addChild( EditorWidget editorWidget );

    public void removeChild( EditorWidget editorWidget );

}
