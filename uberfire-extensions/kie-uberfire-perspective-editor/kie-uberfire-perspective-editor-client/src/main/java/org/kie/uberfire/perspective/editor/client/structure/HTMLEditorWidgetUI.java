package org.kie.uberfire.perspective.editor.client.structure;

import com.google.gwt.user.client.ui.FlowPanel;

public class HTMLEditorWidgetUI implements EditorWidget {

    private final EditorWidget parent;
    private final FlowPanel container;
    private String htmlCode;

    public HTMLEditorWidgetUI( EditorWidget parent,
                               FlowPanel container,
                               String htmlCode ) {
        this.parent = parent;
        this.container = container;
        this.htmlCode = htmlCode;
        parent.addChild( this );
    }


    public HTMLEditorWidgetUI( EditorWidget parent,
                               FlowPanel container ) {
        this.parent = parent;
        this.container = container;
        this.htmlCode = "";
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

    public String getHtmlCode() {
        return htmlCode;
    }

    public void setHtmlCode( String htmlCode ) {
        this.htmlCode = htmlCode;
    }
}
