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
package org.kie.workbench.common.widgets.client.widget;

import com.google.gwt.i18n.client.DateTimeFormat;
import org.kie.workbench.common.services.shared.config.ApplicationPreferences;
import org.uberfire.client.common.AbstractRestrictedEntryTextBox;

import java.util.Date;

/**
 * A TextBox to handle numeric BigDecimal values
 */
public class DateTextBox extends AbstractRestrictedEntryTextBox {

    private static final String DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();

    private static final DateTimeFormat DATE_FORMATTER = DateTimeFormat.getFormat( DATE_FORMAT );

    public DateTextBox() {
        super( false );
    }

    public DateTextBox( final boolean allowEmptyValue ) {
        super( allowEmptyValue );
    }

    @Override
    public boolean isValidValue( String value,
                                 boolean isOnFocusLost ) {
        //Allow anything to be typed in, but validate on loss of Focus
        if ( !isOnFocusLost ) {
            return true;
        }
        boolean isValid = true;
        try {
            DATE_FORMATTER.parseStrict( value );
        } catch ( IllegalArgumentException iae ) {
            isValid = ( "".equals( value ) && allowEmptyValue );
        }
        return isValid;
    }

    @Override
    protected String makeValidValue( String value ) {
        return DATE_FORMATTER.format( new Date() );
    }

}
