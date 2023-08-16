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

import com.google.gwt.regexp.shared.RegExp;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;

public class CustomDataTypeTextBox extends AbstractValidatingTextBox {

    private RegExp regExp;
    private String invalidCharacterTypedMessage;
    private String invalidCharactersInNameErrorMessage;
    private String unbalancedGTLTMessage;

    public CustomDataTypeTextBox() {
        this.regExp = null;
    }

    public void setRegExp(final String pattern,
                          final String invalidCharactersInNameErrorMessage,
                          final String invalidCharacterTypedMessage,
                          final String unbalancedGTLTMessage) {
        regExp = RegExp.compile(pattern);
        this.invalidCharactersInNameErrorMessage = invalidCharactersInNameErrorMessage;
        this.invalidCharacterTypedMessage = invalidCharacterTypedMessage;
        this.unbalancedGTLTMessage = unbalancedGTLTMessage;
    }

    @Override
    public String isValidValue(final String value,
                               final boolean isOnFocusLost) {
        if (regExp != null) {
            boolean isValid = this.regExp.test(value);
            boolean repeatingDots = value.contains(StringUtils.REPEATING_DOTS);
            boolean emptyGenerics = value.contains(StringUtils.EMPTY_GENERICS);
            boolean malformedGenerics = value.contains(StringUtils.MALFORMED_GENERICS);
            if (!isValid || repeatingDots || emptyGenerics || malformedGenerics) {
                String invalidChars = getInvalidCharsInName(regExp, value);
                if (repeatingDots) {
                    invalidChars = StringUtils.REPEATING_DOTS_MSG;
                }

                if (emptyGenerics) {
                    invalidChars = StringUtils.EMPTY_GENERICS_MSG;
                }

                if (malformedGenerics) {
                    invalidChars = StringUtils.MALFORMED_GENERICS_MSG;
                }
                return (isOnFocusLost ? invalidCharactersInNameErrorMessage : invalidCharacterTypedMessage)
                        + ": " + invalidChars;
            }
        }
        return null;
    }

    @Override
    public String isBalancedGTLT(String string) {
        if (!StringUtils.isOkWithGenericsFormat(string)) {
            return unbalancedGTLTMessage;
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
