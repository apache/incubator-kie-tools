package org.uberfire.ext.properties.editor.client.widgets;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class PropertyEditorItemRemovalButton extends Composite implements HasClickHandlers {

    @UiField
    Button button;

    public PropertyEditorItemRemovalButton() {
        initWidget( uiBinder.createAndBindUi( this ) );
        button.setType( ButtonType.DANGER );
        button.setText( "Delete" );
    }

    @Override
    public HandlerRegistration addClickHandler( ClickHandler handler ) {
        return button.addClickHandler( handler );
    }

    interface MyUiBinder extends UiBinder<Widget, PropertyEditorItemRemovalButton> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}