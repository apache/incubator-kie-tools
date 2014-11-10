package org.uberfire.properties.editor.client.widgets;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class PropertyEditorCheckBox extends AbstractPropertyEditorWidget {

    @UiField
    CheckBox checkBox;

    public PropertyEditorCheckBox() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public void setValue( Boolean value ) {
        checkBox.setValue( value );
    }

    public Boolean getValue() {
        return checkBox.getValue();
    }

    public void addValueChangeHandler( ValueChangeHandler<Boolean> valueChangeHandler ) {
        checkBox.addValueChangeHandler( valueChangeHandler );
    }

    interface MyUiBinder extends UiBinder<Widget, PropertyEditorCheckBox> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}