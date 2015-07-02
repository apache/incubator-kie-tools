package org.uberfire.ext.plugin.client.perspective.editor.layout.editor;

import java.util.Map;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.InputSize;
import org.uberfire.ext.layout.editor.client.components.HasModalConfiguration;
import org.uberfire.ext.layout.editor.client.components.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.components.RenderingContext;
import org.uberfire.ext.plugin.client.perspective.editor.api.PerspectiveEditorDragComponent;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.popups.EditHTML;

@Dependent
public class HTMLLayoutDragComponent implements PerspectiveEditorDragComponent,
                                                HasModalConfiguration {

    public static final String HTML_CODE_PARAMETER = "HTML_CODE";

    @Override
    public IsWidget getDragWidget() {
        TextBox textBox = GWT.create( TextBox.class );
        textBox.setPlaceholder( "HTML Component" );
        textBox.setReadOnly( true );
        textBox.setSize( InputSize.DEFAULT );
        return textBox;
    }

    @Override
    public IsWidget getPreviewWidget( RenderingContext container ) {
        return getShowWidget( container );
    }

    @Override
    public IsWidget getShowWidget( RenderingContext context ) {
        Map<String, String> properties = context.getComponent().getProperties();
        String html = properties.get( HTMLLayoutDragComponent.HTML_CODE_PARAMETER );
        return html == null ? null : new HTMLPanel( html );
    }

    @Override
    public Modal getConfigurationModal( ModalConfigurationContext ctx ) {
        return new EditHTML( ctx );
    }
}