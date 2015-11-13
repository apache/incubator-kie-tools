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
package org.uberfire.ext.widgets.common.client.common;

import java.math.BigDecimal;

import com.google.gwt.regexp.shared.RegExp;

/**
 * A TextBox to handle numeric BigDecimal values
 */
public class NumericBigDecimalTextBox extends AbstractRestrictedEntryTextBox {

    // A valid number
    private static final RegExp VALID = RegExp.compile( "(^[-]?[0-9]*\\.?[0-9]*([eE][-+]?[0-9]*)?$)" );

    public NumericBigDecimalTextBox() {
        super( false );
    }

    public NumericBigDecimalTextBox(final boolean allowEmptyValue) {
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

    @Override
    protected String makeValidValue(String value) {
        return "0.0";
    }

}
