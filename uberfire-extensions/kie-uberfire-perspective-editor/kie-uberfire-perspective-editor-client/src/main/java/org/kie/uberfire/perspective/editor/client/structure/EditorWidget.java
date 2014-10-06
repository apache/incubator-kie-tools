package org.kie.uberfire.perspective.editor.client.structure;

import com.google.gwt.user.client.ui.FlowPanel;

public interface EditorWidget {

    public FlowPanel getWidget();

    public void addChild( EditorWidget editorWidget );

    public void removeChild( EditorWidget editorWidget );

}
