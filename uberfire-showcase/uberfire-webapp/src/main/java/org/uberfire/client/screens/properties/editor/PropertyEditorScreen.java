package org.uberfire.client.screens.properties.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.properties.editor.client.PropertyEditorWidget;
import org.uberfire.properties.editor.model.PropertyEditorEvent;

@Dependent
@WorkbenchScreen(identifier = "PropertyEditorScreen")
public class PropertyEditorScreen
        extends Composite
        implements RequiresResize {

    @UiField
    FlowPanel panel;

    @Inject
    Event<PropertyEditorEvent> propertyEditorEvent;

    private PropertyEditorWidget propertyEditorWidget;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
        propertyEditorWidget = GWT.create(PropertyEditorWidget.class);
        panel.add( propertyEditorWidget );

    }

    interface MyUiBinder extends UiBinder<Widget, PropertyEditorScreen> {

    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Property Editor Screen";
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        setPixelSize( width, height );
    }

    public void propertyEditorEventObserver( @Observes PropertyEditorEvent event ) {
        propertyEditorWidget.handle( event );
    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}