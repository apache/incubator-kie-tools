package org.uberfire.ext.plugin.client.perspective.editor.layout.editor;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
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
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.popups.EditScreen;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

@Dependent
public class ScreenLayoutDragComponent extends LayoutDragComponent {

    public static final String PLACE_NAME_PARAMETER = "Place Name";

    @Inject
    private LayoutEditorPluginAPI layoutEditorPluginAPI;

    @Override
    public String label() {
        return "Screen Component";
    }

    @Override
    public Widget getDragWidget() {
        TextBox textBox = GWT.create( TextBox.class );
        textBox.setPlaceholder( "Screen Component" );
        textBox.setReadOnly( true );
        textBox.setAlternateSize( AlternateSize.MEDIUM );
        return textBox;
    }

    @Override
    public IsWidget getComponentPreview() {
        return new Label( "Screen Component" );
    }

    @Override
    public boolean hasConfigureModal() {
        return true;
    }

    @Override
    public Modal getConfigureModal( EditorWidget editorWidget ) {
        return new EditScreen( editorWidget , layoutEditorPluginAPI);
    }

    public void observeEditComponentEventFromPropertyEditor( @Observes PropertyEditorChangeEvent event ) {

        PropertyEditorFieldInfo property = event.getProperty();
        if ( property.getEventId().equalsIgnoreCase( EditScreen.PROPERTY_EDITOR_KEY ) ) {
            layoutEditorPluginAPI.addPropertyToLayoutComponentByKey( property.getKey(), property.getLabel(), property.getCurrentStringValue() );
        }
    }
}
