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
package org.drools.workbench.screens.guided.rule.client.editor;

import com.google.gwt.regexp.shared.RegExp;
import org.uberfire.client.common.AbstractRestrictedEntryTextBox;

/**
 * A TextBox to handle entry of Template Keys
 */
public class TemplateKeyTextBox
        extends AbstractRestrictedEntryTextBox {

    // A valid Template Key. This is more restrictive than that in ColumnFactory but the extension is for more advanced use
    private final static RegExp VALID = RegExp.compile( "(^((\\$)*([a-zA-Z0-9_]+))$)" );

    private final static String DEFAULT_KEY = "$default";

    @Override
    public void setText( String text ) {
        if ( text == null || text.isEmpty() ) {
            text = DEFAULT_KEY;
        }
        super.setText( text );
    }

    @Override
    public void setValue( String value ) {
        if ( value == null || value.isEmpty() ) {
            value = DEFAULT_KEY;
        }
        super.setValue( value );
    }

    @Override
    public void setValue( String value,
                          boolean fireEvents ) {
        if ( value == null || value.isEmpty() ) {
            value = DEFAULT_KEY;
        }
        super.setValue( value,
                        fireEvents );
    }

    @Override
    public boolean isValidValue( String value,
                                 boolean isOnFocusLost ) {
        return VALID.test( value );
    }

    @Override
    protected String makeValidValue( String value ) {
        return DEFAULT_KEY;
    }

}
