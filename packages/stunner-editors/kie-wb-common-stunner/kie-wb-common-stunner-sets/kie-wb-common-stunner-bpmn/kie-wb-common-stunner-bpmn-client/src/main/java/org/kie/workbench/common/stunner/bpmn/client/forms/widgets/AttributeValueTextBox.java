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

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.regexp.shared.RegExp;
import org.kie.workbench.common.stunner.bpmn.client.StunnerSpecific;

@StunnerSpecific
public class AttributeValueTextBox extends AbstractValidatingTextBox {

    Set<String> invalidValues = null;
    boolean isCaseSensitive = false;
    String invalidValueErrorMessage = null;

    protected String invalidCharacterTypedMessage;
    protected String invalidCharactersInValueErrorMessage;

    // Pattern for valid value
    protected RegExp regExp = null;

    public void setRegExp(final String pattern,
                          final String invalidCharactersInValueErrorMessage,
                          final String invalidCharacterTypedMessage) {
        regExp = RegExp.compile(pattern);
        this.invalidCharactersInValueErrorMessage = invalidCharactersInValueErrorMessage;
        this.invalidCharacterTypedMessage = invalidCharacterTypedMessage;
    }

    public void setInvalidValues(final Set<String> invalidValues,
                                 final boolean isCaseSensitive,
                                 final String invalidValueErrorMessage) {
        if (isCaseSensitive) {
            this.invalidValues = invalidValues;
        } else {
            this.invalidValues = new HashSet<>();
            for (String value : invalidValues) {
                this.invalidValues.add(value.toLowerCase());
            }
        }
        this.isCaseSensitive = isCaseSensitive;
        this.invalidValueErrorMessage = invalidValueErrorMessage;
    }

    private String testForInvalidValue(final String value) {
        if (value == null || value.isEmpty() || invalidValues == null) {
            return null;
        }
        String testValue;
        if (!isCaseSensitive) {
            testValue = value.toLowerCase();
        } else {
            testValue = value;
        }
        if (invalidValues.contains(testValue)) {
            return invalidValueErrorMessage;
        } else {
            return null;
        }
    }

    @Override
    public String isValidValue(String value, boolean isOnFocusLost) {
        if (invalidValues != null && !invalidValues.isEmpty() && isOnFocusLost) {
            String err = testForInvalidValue(value);
            if (err != null && !err.isEmpty()) {
                return err;
            }
        }
        if (regExp != null) {
            boolean isValid = this.regExp.test(value);
            if (!isValid) {
                String invalidChars = getInvalidCharsInName(value);
                return (isOnFocusLost ? invalidCharactersInValueErrorMessage : invalidCharacterTypedMessage)
                        + ": " + invalidChars;
            }
        }
        return null;
    }

    @Override
    public String isBalancedGTLT(String string) {
        return null;
    }

    @Override
    protected String makeValidValue(final String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        // It's a known invalid value
        if (testForInvalidValue(value) != null) {
            return "";
        } else {
            StringBuilder validValue = new StringBuilder(value.length());
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (isValidChar(c)) {
                    validValue.append(c);
                }
            }
            return validValue.toString();
        }
    }

    private boolean isValidChar(final char c) {
        return isValidChar(regExp, c);
    }

    private String getInvalidCharsInName(final String value) {
        return getInvalidCharsInName(regExp, value);
    }
}
