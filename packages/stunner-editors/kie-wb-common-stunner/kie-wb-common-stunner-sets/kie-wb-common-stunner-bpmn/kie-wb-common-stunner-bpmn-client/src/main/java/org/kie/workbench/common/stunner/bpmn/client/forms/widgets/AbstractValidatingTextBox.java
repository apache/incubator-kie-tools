/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.widgets;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.regexp.shared.RegExp;
import org.gwtbootstrap3.client.ui.TextBox;
import org.uberfire.workbench.events.NotificationEvent;

public abstract class AbstractValidatingTextBox extends TextBox {

    @Inject
    private Event<NotificationEvent> notification;

    private static int lastKey = 0;

    public AbstractValidatingTextBox() {
        super();
        setup();
    }

    protected void setup() {
        final TextBox me = this;
        //Validate value as it is entered
        this.addKeyPressHandler(new KeyPressHandler() {

            public void onKeyPress(final KeyPressEvent event) {
                // Permit navigation
                int keyCode = getKeyCodeFromKeyPressEvent(event);
                boolean repeatingDots = (keyCode == KeyCodes.KEY_DELETE && lastKey == KeyCodes.KEY_DELETE);
                lastKey = keyCode;

                if (event.isControlKeyDown()) {
                    return;
                }
                if (!event.isShiftKeyDown()) {
                    if ((keyCode == KeyCodes.KEY_BACKSPACE
                            || keyCode == KeyCodes.KEY_DELETE
                            || keyCode == KeyCodes.KEY_LEFT
                            || keyCode == KeyCodes.KEY_RIGHT
                            || keyCode == KeyCodes.KEY_ENTER
                            || keyCode == KeyCodes.KEY_TAB
                            || keyCode == KeyCodes.KEY_HOME
                            || keyCode == KeyCodes.KEY_END) && !repeatingDots) {
                        return;
                    }
                }
                // Get new value and validate
                int charCode = event.getCharCode();
                String oldValue = me.getValue();
                String newValue = oldValue.substring(0,
                                                     me.getCursorPos());
                newValue = newValue + ((char) charCode);
                newValue = newValue + oldValue.substring(me.getCursorPos() + me.getSelectionLength());
                String validationError = isValidValue(newValue,
                                                      false);
                if (validationError != null) {
                    event.preventDefault();
                    fireValidationError(validationError);
                }
            }
        });
        //Add validation when loses focus (for when values are pasted in by user)
        this.addBlurHandler(new BlurHandler() {

            @Override
            public void onBlur(final BlurEvent event) {
                String value = me.getText();
                String validationError = isValidValue(value,
                                                      true);
                if (validationError != null) {
                    fireValidation(value, validationError, me);
                } else {
                    validationError = isBalancedGTLT(value);
                    if (validationError != null) {
                        fireValidation(value, validationError, me);
                    }
                }
            }
        });
    }

    private void fireValidation(String value, String validationError, TextBox me) {
        fireValidationError(validationError);
        String validValue = makeValidValue(value);
        me.setValue(validValue);
        ValueChangeEvent.fire(AbstractValidatingTextBox.this,
                              validValue);
    }

    /**
     * Tests whether a string has balanced <> hence indicating if it looks like valid generics format
     * @param string
     * @return an error message to be reported
     */
    public abstract String isBalancedGTLT(String string);

    /**
     * Tests whether a value is valid
     * @param value
     * @param isOnFocusLost
     * @return an error message to be reported
     */
    public abstract String isValidValue(final String value,
                                        final boolean isOnFocusLost);

    /**
     * If validation fails (e.g. as a result of a user pasting a value) when the
     * TextBox looses focus this method is called to transform the current value
     * into one which is valid.
     * @param value Current value
     * @return A valid value
     */
    protected abstract String makeValidValue(final String value);

    protected void fireValidationError(final String validationError) {
        notification.fire(new NotificationEvent(validationError,
                                                NotificationEvent.NotificationType.ERROR));
    }

    protected int getKeyCodeFromKeyPressEvent(final KeyPressEvent event) {
        return event.getNativeEvent().getKeyCode();
    }

    public static String getInvalidCharsInName(final RegExp regExp,
                                               final String value) {
        if (value == null || value.isEmpty()) {
            return "";
        } else {
            StringBuilder invalidChars = new StringBuilder(value.length());
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (!isValidChar(regExp, c)) {
                    invalidChars.append(c);
                }
            }
            return invalidChars.toString();
        }
    }

    public static boolean isValidChar(final RegExp regExp,
                                      final char c) {
        if (regExp != null) {
            return regExp.test("" + c);
        } else {
            return true;
        }
    }

}
