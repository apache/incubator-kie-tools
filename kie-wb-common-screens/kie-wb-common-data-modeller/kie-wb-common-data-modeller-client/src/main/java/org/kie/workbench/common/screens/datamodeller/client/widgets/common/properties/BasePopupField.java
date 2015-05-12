package org.kie.workbench.common.screens.datamodeller.client.widgets.common.properties;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.properties.editor.client.fields.AbstractField;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

public abstract class BasePopupField extends AbstractField {

    @Inject
    Event<PropertyEditorChangeEvent> propertyEditorChangeEvent;

    @Override
    public Widget widget( final PropertyEditorFieldInfo property ) {

        final BasePopupPropertyEditorWidget popupPropertyEditor = createPopupPropertyEditor( property );
        popupPropertyEditor.setValue( property.getCurrentStringValue() );
        popupPropertyEditor.addChangeHandler(
                new ValueChangeHandler<String>() {
                    public void onValueChange( ValueChangeEvent event ) {
                        String value = popupPropertyEditor.getValue();
                        if ( validate( property, value ) ) {
                            popupPropertyEditor.clearOldValidationErrors();
                            property.setCurrentStringValue( value );
                            propertyEditorChangeEvent.fire( new PropertyEditorChangeEvent( property, value ) );
                        } else {
                            popupPropertyEditor.setValidationError( getValidatorErrorMessage( property, value ) );
                            popupPropertyEditor.setValue( property.getCurrentStringValue() );
                        }
                    }
                } );
        return popupPropertyEditor;
    }

    protected abstract BasePopupPropertyEditorWidget createPopupPropertyEditor( PropertyEditorFieldInfo propertyInfo );

}
