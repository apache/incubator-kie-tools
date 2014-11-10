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

import java.math.BigDecimal;

import com.google.gwt.regexp.shared.RegExp;

/**
 * A TextBox to handle numeric values. This is only for *LEGACY* support when
 * all numerical data-types were handled as BigDecimals. Please refer to the
 * sibling classes NumericXXXTextBox
 */
public class NumericTextBox extends AbstractRestrictedEntryTextBox {

    // A valid number
    private static final RegExp VALID = RegExp.compile( "(^[-]?[0-9]*\\.?[0-9]*([eE][-+]?[0-9]*)?$)" );

    public NumericTextBox() {
        super( false );
    }

    public NumericTextBox(final boolean allowEmptyValue) {
        super( allowEmptyValue );
    }

    @Override
    public boolean isValidValue(String value,
                                boolean isOnFocusLost) {
        boolean isValid = VALID.test( value );
        if ( !isValid ) {
            return isValid;
        }
        if ( !isOnFocusLost && "-".equals( value ) ) {
            return true;
        }
        try {
            @SuppressWarnings("unused")
            BigDecimal check = new BigDecimal( value );
        } catch ( NumberFormatException nfe ) {
            isValid = ("".equals( value ) && allowEmptyValue);
        }
        return isValid;
    }

}
