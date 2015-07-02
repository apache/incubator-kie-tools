package org.uberfire.ext.plugin.client.perspective.editor.layout.editor;

import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.InputSize;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.layout.editor.client.components.HasModalConfiguration;
import org.uberfire.ext.layout.editor.client.components.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.components.RenderingContext;
import org.uberfire.ext.plugin.client.perspective.editor.api.PerspectiveEditorDragComponent;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.popups.EditScreen;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@ApplicationScoped
public class ScreenLayoutDragComponent implements PerspectiveEditorDragComponent, HasModalConfiguration {

    public static final String PLACE_NAME_PARAMETER = "Place Name";

    @Inject
    private PlaceManager placeManager;

    @Override
    public IsWidget getDragWidget() {
        TextBox textBox = GWT.create( TextBox.class );
        textBox.setPlaceholder( "Screen Component" );
        textBox.setReadOnly( true );
        textBox.setSize( InputSize.DEFAULT );
        return textBox;
    }

    @Override
    public IsWidget getPreviewWidget(RenderingContext ctx) {
        Map<String, String> properties = ctx.getComponent().getProperties();
        String placeName = properties.get(PLACE_NAME_PARAMETER);
        if (placeName == null) return null;

        FlowPanel panel = new FlowPanel();
        panel.setWidth("95%");
        panel.setHeight(500 + "px");
        placeManager.goTo(new DefaultPlaceRequest(placeName, properties), panel);
        return panel;
    }

    @Override
    public IsWidget getShowWidget(RenderingContext ctx) {
        Map<String, String> properties = ctx.getComponent().getProperties();
        String placeName = properties.get(PLACE_NAME_PARAMETER);
        if (placeName == null) return null;

        FlowPanel panel = new FlowPanel();
        panel.setWidth("95%");
        panel.setHeight(500 + "px");
        placeManager.goTo(new DefaultPlaceRequest(placeName, properties), panel);
        return panel;
    }

    @Override
    public Modal getConfigurationModal(ModalConfigurationContext ctx) {
        this.configContext = ctx;
        return new EditScreen(ctx);
    }

    private ModalConfigurationContext configContext;

    public void observeEditComponentEventFromPropertyEditor( @Observes PropertyEditorChangeEvent event ) {

        PropertyEditorFieldInfo property = event.getProperty();
        if ( property.getEventId().equalsIgnoreCase( EditScreen.PROPERTY_EDITOR_KEY ) ) {
            configContext.setComponentProperty(property.getLabel(), property.getCurrentStringValue() );
        }
    }
}
