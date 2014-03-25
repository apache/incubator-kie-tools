package org.uberfire.properties.editor.client;

import com.github.gwtbootstrap.client.ui.Accordion;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.properties.editor.model.PropertyEditorEvent;

public class PropertyEditorWidget extends Composite {

    @UiField
    Accordion propertyMenu;

    String lastOpenAccordionGroupTitle = "";

    PropertyEditorEvent originalEvent;

    @UiField
    TextBox filterBox;

    @UiField
    Button reload;

    public void handle( PropertyEditorEvent event ) {
        if ( PropertyEditorHelper.validade( event ) ) {
            this.originalEvent = event;
            this.filterBox.setText( "" );
            PropertyEditorHelper.extractEditorFrom( this, propertyMenu, event );
        }

    }

    @UiHandler("reload")
    void onReload( ClickEvent e ) {
        this.filterBox.setText( "" );
        PropertyEditorHelper.extractEditorFrom( this, propertyMenu, originalEvent, "" );
    }

    @UiHandler("filterBox")
    public void onKeyUp( KeyUpEvent e ) {
        if ( originalEvent != null ) {
            propertyMenu.clear();
            PropertyEditorHelper.extractEditorFrom( this, propertyMenu, originalEvent, filterBox.getText() );
        }

    }

    public PropertyEditorWidget() {
        initWidget( uiBinder.createAndBindUi( this ) );

    }

    interface MyUiBinder extends UiBinder<Widget, PropertyEditorWidget> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

    public String getLastOpenAccordionGroupTitle() {
        return lastOpenAccordionGroupTitle;
    }

    public void setLastOpenAccordionGroupTitle( String lastOpenAccordionGroupTitle ) {
        this.lastOpenAccordionGroupTitle = lastOpenAccordionGroupTitle;
    }
}
