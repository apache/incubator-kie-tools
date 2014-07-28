package org.kie.workbench.common.screens.server.management.client.util;

import java.math.BigDecimal;

import com.google.gwt.regexp.shared.RegExp;

public class NumericTextBox extends AbstractRestrictedEntryTextBox {

    // A valid number
    private static final RegExp VALID = RegExp.compile( "(^[-]?[0-9]*\\.?[0-9]*([eE][-+]?[0-9]*)?$)" );

    public NumericTextBox() {
        super( false );
    }

    public NumericTextBox( final boolean allowEmptyValue ) {
        super( allowEmptyValue );
    }

    @Override
    public boolean isValidValue( String value,
                                 boolean isOnFocusLost ) {
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
            isValid = ( "".equals( value ) && allowEmptyValue );
        }
        return isValid;
    }

}
