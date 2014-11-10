/*
 * Copyright 2012 JBoss Inc
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
package org.uberfire.client.common;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A TextBox to handle restricted entry
 */
public abstract class AbstractRestrictedEntryTextBox extends TextBox {

    protected final boolean allowEmptyValue;

    public AbstractRestrictedEntryTextBox() {
        this( false );
    }

    public AbstractRestrictedEntryTextBox( final boolean allowEmptyValue ) {
        this.allowEmptyValue = allowEmptyValue;
        setup();
    }

    protected void setup() {
        final TextBox me = this;

        //Validate value as it is entered
        this.addKeyPressHandler( new KeyPressHandler() {

            public void onKeyPress( KeyPressEvent event ) {

                // Permit navigation
                int keyCode = event.getNativeEvent().getKeyCode();
                if ( event.isControlKeyDown()
                        || keyCode == KeyCodes.KEY_BACKSPACE
                        || keyCode == KeyCodes.KEY_DELETE
                        || keyCode == KeyCodes.KEY_LEFT
                        || keyCode == KeyCodes.KEY_RIGHT
                        || keyCode == KeyCodes.KEY_TAB
                        || keyCode == KeyCodes.KEY_HOME
                        || keyCode == KeyCodes.KEY_END
                        ) {
                    return;
                }

                // Get new value and validate
                int charCode = event.getCharCode();
                String oldValue = me.getValue();
                String newValue = oldValue.substring( 0,
                                                      me.getCursorPos() );
                newValue = newValue
                        + ( (char) charCode );
                newValue = newValue
                        + oldValue.substring( me.getCursorPos() + me.getSelectionLength() );
                if ( !isValidValue( newValue,
                                    false ) ) {
                    event.preventDefault();
                }

            }

        } );

        //Add validation when looses focus (for when values are pasted in by users')
        this.addBlurHandler( new BlurHandler() {

            @Override
            public void onBlur( BlurEvent event ) {
                final String value = me.getText();
                if ( !isValidValue( value,
                                    true ) ) {
                    final String validValue = makeValidValue( value );
                    me.setText( validValue );
                    ValueChangeEvent.fire( AbstractRestrictedEntryTextBox.this,
                                           validValue );
                }
            }

        } );

    }

    @Override
    public void setText( final String text ) {
        if ( !isValidValue( text,
                            false ) ) {
            final String validValue = makeValidValue( text );
            super.setText( validValue );
            ValueChangeEvent.fire( AbstractRestrictedEntryTextBox.this,
                                   validValue );
        } else {
            super.setText( text );
        }
    }

    @Override
    public void setValue( final String value ) {
        if ( !isValidValue( value,
                            false ) ) {
            final String validValue = makeValidValue( value );
            super.setValue( validValue );
            ValueChangeEvent.fire( AbstractRestrictedEntryTextBox.this,
                                   validValue );
        } else {
            super.setValue( value );
        }
    }

    @Override
    public void setValue( final String value,
                          final boolean fireEvents ) {
        if ( !isValidValue( value,
                            false ) ) {
            final String validValue = makeValidValue( value );
            super.setValue( validValue,
                            fireEvents );
            ValueChangeEvent.fire( AbstractRestrictedEntryTextBox.this,
                                   validValue );
        } else {
            super.setValue( value,
                            fireEvents );
        }
    }

    /**
     * Validate value of TextBox
     * @param value
     * @param isOnFocusLost Focus has been lost from the TextBox
     * @return True if valid
     */
    public abstract boolean isValidValue( final String value,
                                          final boolean isOnFocusLost );

    /**
     * If validation fails (e.g. as a result of a user pasting a value) when the
     * TextBox looses focus this method is called to transform the current value
     * into one which is valid. This default implementation returns an empty
     * String, however numerical TextBoxes could check the value is numerical
     * and scale to that suitable for the type.
     * @param value Current value
     * @return A valid value
     */
    protected String makeValidValue( String value ) {
        return "";
    }

}
