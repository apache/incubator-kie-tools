package org.uberfire.ext.layout.editor.client.util;

import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.layout.editor.client.structure.EditorWidget;

public abstract class LayoutDragComponent {

    public static final String INTERNAL_DRAG_COMPONENT = "INTERNAL_DRAG_COMPONENT";

    public abstract String label();

    public abstract Widget getDragWidget();

    public boolean externalLayoutDragComponent() {
        return true;
    }

    public abstract IsWidget getComponentPreview();

    public abstract boolean hasConfigureModal();

    public abstract Modal getConfigureModal( EditorWidget editorWidget );

}
