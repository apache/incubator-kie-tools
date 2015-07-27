package org.uberfire.ext.plugin.client.perspective.editor.layout.editor;

import java.util.Map;
import javax.enterprise.context.Dependent;

import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlternateSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.ext.layout.editor.client.components.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.components.RenderingContext;
import org.uberfire.ext.layout.editor.client.components.HasModalConfiguration;
import org.uberfire.ext.plugin.client.perspective.editor.api.PerspectiveEditorDragComponent;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.popups.EditHTML;

@Dependent
public class HTMLLayoutDragComponent implements PerspectiveEditorDragComponent, HasModalConfiguration {

    public static final String HTML_CODE_PARAMETER = "HTML_CODE";

    @Override
    public IsWidget getDragWidget() {
        TextBox textBox = GWT.create( TextBox.class );
        textBox.setPlaceholder( "HTML Component" );
        textBox.setReadOnly( true );
        textBox.setAlternateSize( AlternateSize.MEDIUM );
        return textBox;
    }

    @Override
    public IsWidget getPreviewWidget(RenderingContext container) {
        return getShowWidget(container);
    }

    @Override
    public IsWidget getShowWidget(RenderingContext context) {
        Map<String, String> properties = context.getComponent().getProperties();
        String html = properties.get( HTMLLayoutDragComponent.HTML_CODE_PARAMETER );
        return html == null ? null: new HTMLPanel( html );
    }

    @Override
    public Modal getConfigurationModal(ModalConfigurationContext ctx) {
        return new EditHTML(ctx);
    }
}
