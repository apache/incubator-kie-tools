package org.uberfire.ext.plugin.client.perspective.editor.layout.editor;

import java.util.Map;
import java.util.Random;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlternateSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
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
        textBox.setAlternateSize( AlternateSize.MEDIUM );
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
