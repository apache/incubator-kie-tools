package org.uberfire.ext.layout.editor.client.resources;

import com.google.gwt.resources.client.CssResource;

public interface LayoutEditorCss extends CssResource {

    @ClassName("dropBorder")
    String dropBorder();

    @ClassName("dropInactive")
    String dropInactive();

    @ClassName("rowDragOver")
    String rowDragOver();

    @ClassName("rowDragOut")
    String rowDragOut();

    @ClassName("componentDragOver")
    String componentDragOver();

    @ClassName("componentDragOut")
    String componentDragOut();
}
