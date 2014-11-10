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

import com.google.gwt.regexp.shared.RegExp;

/**
 * A TextBox to handle integer values
 */
public class NumericIntegerTextBox extends AbstractRestrictedEntryTextBox {

    // A valid integer
    private static final RegExp VALID = RegExp.compile( "(^[-]?\\d*$)" );

    public NumericIntegerTextBox() {
        super( false );
    }

    public NumericIntegerTextBox(final boolean allowEmptyValue) {
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
            Integer.parseInt( value );
        } catch ( NumberFormatException nfe ) {
            isValid = ("".equals( value ) && allowEmptyValue);
        }
        return isValid;
    }

    @Override
    protected String makeValidValue(String value) {
        return "0";
    }

}
