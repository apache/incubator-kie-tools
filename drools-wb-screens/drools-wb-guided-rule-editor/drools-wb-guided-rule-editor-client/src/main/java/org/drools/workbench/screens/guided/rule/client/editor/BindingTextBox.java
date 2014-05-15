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

import org.kie.workbench.common.widgets.client.util.Unicode;
import org.uberfire.client.common.AbstractRestrictedEntryTextBox;

/**
 * A TextBox to handle entry of Pattern\Field bindings
 */
public class BindingTextBox
        extends AbstractRestrictedEntryTextBox {

    @Override
    public boolean isValidValue( String value,
                                 boolean isOnFocusLost ) {
        //Unable to use a RegEx to validate value as GWT uses the JS RegEx object that is does not handle Unicode.
        //Furthermore we're unable to use GWT's Character class emulation as this too doesn't support Unicode fully.
        //See https://gwt.googlesource.com/gwt/+/2.5.1/user/super/com/google/gwt/emul/java/lang/Character.java
        //See https://bugzilla.redhat.com/show_bug.cgi?id=1086462
        if ( value == null ) {
            return true;
        }
        final char[] chars = value.toCharArray();
        for ( int i = 0; i < chars.length; i++ ) {
            final char c = chars[ i ];
            if ( i == 0 ) {
                if ( c == '$' ) {
                    continue;
                } else if ( !Unicode.isLetter( c ) ) {
                    return false;
                }
            } else if ( !( Character.isDigit( c ) || Unicode.isLetter( c ) ) ) {
                return false;
            }
        }
        return true;
    }

}
