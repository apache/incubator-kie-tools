package org.uberfire.ext.properties.editor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;

public class PropertyEditorItemButtons extends Composite implements HasClickHandlers {

    @UiField
    Button removalButton;

    public PropertyEditorItemButtons() {
        initWidget( uiBinder.createAndBindUi( this ) );
        removalButton.setType( ButtonType.DANGER );
        removalButton.setSize( ButtonSize.EXTRA_SMALL );
        removalButton.setIcon( IconType.MINUS );
//        removalButton.addClickHandler( clickHandler );
    }

    public void addRemovalButton( ClickHandler clickHandler ) {
        removalButton.setVisible( true );
        removalButton.setType( ButtonType.DANGER );
        removalButton.setSize( ButtonSize.EXTRA_SMALL );
        removalButton.setIcon( IconType.MINUS );
        removalButton.addClickHandler( clickHandler );
    }

    @Override
    public HandlerRegistration addClickHandler( ClickHandler handler ) {
        return removalButton.addClickHandler( handler );
    }

    interface MyUiBinder extends UiBinder<Widget, PropertyEditorItemButtons> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}