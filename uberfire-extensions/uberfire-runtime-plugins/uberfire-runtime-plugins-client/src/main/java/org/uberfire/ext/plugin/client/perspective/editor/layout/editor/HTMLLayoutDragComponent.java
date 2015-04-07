package org.uberfire.ext.plugin.client.perspective.editor.layout.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlternateSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.layout.editor.client.LayoutEditorPluginAPI;
import org.uberfire.ext.layout.editor.client.structure.EditorWidget;
import org.uberfire.ext.layout.editor.client.util.LayoutDragComponent;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.popups.EditHTML;

@Dependent
public class HTMLLayoutDragComponent extends LayoutDragComponent {

    public static final String HTML_CODE_PARAMETER = "HTML_CODE";

    @Inject
    private LayoutEditorPluginAPI layoutEditorPluginAPI;

    @Override
    public String label() {
        return "Html Component";
    }

    @Override
    public Widget getDragWidget() {
        TextBox textBox = GWT.create( TextBox.class );
        textBox.setPlaceholder( "HTML Component" );
        textBox.setReadOnly( true );
        textBox.setAlternateSize( AlternateSize.MEDIUM );
        return textBox;
    }

    @Override
    public IsWidget getComponentPreview() {
        return new Label( "HTML Component" );
    }

    @Override
    public boolean hasConfigureModal() {
        return true;
    }

    @Override
    public Modal getConfigureModal( EditorWidget editorWidget ) {
        return new EditHTML( editorWidget, layoutEditorPluginAPI );
    }
}
