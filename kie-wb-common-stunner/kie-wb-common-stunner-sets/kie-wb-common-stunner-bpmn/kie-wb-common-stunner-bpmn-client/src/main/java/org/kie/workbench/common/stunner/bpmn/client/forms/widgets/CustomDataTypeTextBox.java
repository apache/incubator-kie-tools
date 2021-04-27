/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.widgets;

import com.google.gwt.regexp.shared.RegExp;

public class CustomDataTypeTextBox extends AbstractValidatingTextBox {

    private RegExp regExp;
    private String invalidCharacterTypedMessage;
    private String invalidCharactersInNameErrorMessage;

    public CustomDataTypeTextBox() {
        this.regExp = null;
    }

    public void setRegExp(final String pattern,
                          final String invalidCharactersInNameErrorMessage,
                          final String invalidCharacterTypedMessage) {
        regExp = RegExp.compile(pattern);
        this.invalidCharactersInNameErrorMessage = invalidCharactersInNameErrorMessage;
        this.invalidCharacterTypedMessage = invalidCharacterTypedMessage;
    }

    @Override
    public String isValidValue(final String value,
                               final boolean isOnFocusLost) {
        if (regExp != null) {
            boolean isValid = this.regExp.test(value);
            if (!isValid) {
                String invalidChars = getInvalidCharsInName(regExp, value);
                return (isOnFocusLost ? invalidCharactersInNameErrorMessage : invalidCharacterTypedMessage)
                        + ": " + invalidChars;
            }
        }
        return null;
    }

    @Override
    protected String makeValidValue(final String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        StringBuilder validValue = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (isValidChar(regExp, c)) {
                validValue.append(c);
            }
        }
        return validValue.toString();
    }

    protected String getInvalidCharsInName(final String value) {
        return getInvalidCharsInName(regExp, value);
    }

    protected boolean isValidChar(final char c) {
        return isValidChar(regExp, c);
    }
}
