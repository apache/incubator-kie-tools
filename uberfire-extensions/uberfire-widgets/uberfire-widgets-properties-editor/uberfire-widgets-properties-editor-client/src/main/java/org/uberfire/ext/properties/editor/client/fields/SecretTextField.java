/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.properties.editor.client.fields;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.properties.editor.client.widgets.PropertyEditorPasswordTextBox;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

@Dependent
public class SecretTextField extends AbstractField {

    @Inject
    Event<PropertyEditorChangeEvent> propertyEditorChangeEventEvent;

    public SecretTextField() {
    }

    @Override
    public Widget widget( final PropertyEditorFieldInfo property ) {
        final PropertyEditorPasswordTextBox passwordTextBox = GWT.create( PropertyEditorPasswordTextBox.class );
        passwordTextBox.setText( property.getCurrentStringValue() );
        addLostFocusHandler( property,passwordTextBox );
        addKeyDownHandler( property, passwordTextBox );
        return passwordTextBox;
    }

    private void addLostFocusHandler(final PropertyEditorFieldInfo property,
                                     final PropertyEditorPasswordTextBox passwordTextBox ) {

        passwordTextBox.addBlurHandler( new BlurHandler() {
            @Override
            public void onBlur( BlurEvent event ) {
                if ( validate( property, passwordTextBox.getText() ) ) {
                    passwordTextBox.clearOldValidationErrors();
                    property.setCurrentStringValue( passwordTextBox.getText() );
                    propertyEditorChangeEventEvent.fire( new PropertyEditorChangeEvent( property, passwordTextBox.getText() ) );
                } else {
                    passwordTextBox.setValidationError( getValidatorErrorMessage( property, passwordTextBox.getText() ) );
                    passwordTextBox.setText( property.getCurrentStringValue() );
                }

            }

        } );
    }
    private void addKeyDownHandler( final PropertyEditorFieldInfo property,
                                    final PropertyEditorPasswordTextBox passwordTextBox ) {
        passwordTextBox.addKeyDownHandler( new KeyDownHandler() {
            @Override
            public void onKeyDown( KeyDownEvent event ) {
                if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
                    if ( validate( property, passwordTextBox.getText() ) ) {
                        passwordTextBox.clearOldValidationErrors();
                        property.setCurrentStringValue( passwordTextBox.getText() );
                        propertyEditorChangeEventEvent.fire( new PropertyEditorChangeEvent( property, passwordTextBox.getText() ) );
                    } else {
                        passwordTextBox.setValidationError( getValidatorErrorMessage( property, passwordTextBox.getText() ) );
                        passwordTextBox.setText( property.getCurrentStringValue() );
                    }
                }

            }

        } );
    }
}
