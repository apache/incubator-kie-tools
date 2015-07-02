package org.uberfire.ext.properties.editor.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.constants.ValidationState;

public class PropertyEditorItemsWidget extends Composite {

    @UiField
    FormGroup items;

    @UiField
    HelpBlock helpInline;

    public PropertyEditorItemsWidget() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public void add( Widget item ) {
        items.add( item );
    }

    public void setError(String errorMessage){
        items.setValidationState( ValidationState.ERROR );
        helpInline.setText( errorMessage );
    }

    public void clearError() {
        helpInline.setText( "" );
        items.setValidationState( ValidationState.NONE );
    }

    interface MyUiBinder extends UiBinder<Widget, PropertyEditorItemsWidget> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}