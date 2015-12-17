/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
