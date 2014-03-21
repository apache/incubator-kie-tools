package org.uberfire.properties.editor.client.fields;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.properties.editor.client.widgets.PropertyEditorTextBox;

@Dependent
public class TextField extends AbstractField {

    @Inject
    Event<PropertyEditorChangeEvent> propertyEditorChangeEventEvent;

    @Override
    public Widget widget( final PropertyEditorFieldInfo property ) {
        final PropertyEditorTextBox textBox = GWT.create( PropertyEditorTextBox.class );
        textBox.setText( property.getCurrentStringValue() );
        addEnterKeyHandler( property, textBox );
        return textBox;
    }

    private void addEnterKeyHandler( final PropertyEditorFieldInfo property,
                                     final PropertyEditorTextBox textBox ) {
        textBox.addKeyDownHandler( new KeyDownHandler() {
            @Override
            public void onKeyDown( KeyDownEvent event ) {
                if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
                    if ( validate( property, textBox.getText() ) ) {
                        textBox.clearOldValidationErrors();
                        property.setCurrentStringValue( textBox.getText() );
                        propertyEditorChangeEventEvent.fire( new PropertyEditorChangeEvent( property, textBox.getText() ) );
                    } else {
                        textBox.setValidationError( getValidatorErrorMessage( property, textBox.getText() ) );
                        textBox.setText( property.getCurrentStringValue() );
                    }
                }

            }

        } );
    }

}
