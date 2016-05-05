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

package org.uberfire.ext.layout.editor.client.dnd;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.ext.layout.editor.client.resources.i18n.CommonConstants;

public class GridValueValidator {

    public String getValidationError() {
        return CommonConstants.INSTANCE.InvalidGridConfiguration();
    }

    public boolean isValid( String gridSystem ) {
        if ( gridSystem == null || gridSystem.isEmpty() ) {
            return Boolean.FALSE;
        }
        final List<String> rowSpans = parseRowSpanString( gridSystem );

        int totalSpan = 0;
        for ( String rowSpan : rowSpans ) {
            try {
                final int integerSpan = Integer.parseInt( rowSpan );
                totalSpan = integerSpan + totalSpan;
            } catch ( NumberFormatException e ) {
                return false;
            }
        }

        if ( totalSpan != 12 ) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private List<String> parseRowSpanString( String rowSpamString ) {
        List<String> rowSpans = new ArrayList<String>();
        String[] spans = rowSpamString.split( " " );
        for ( String span : spans ) {
            rowSpans.add( span );
        }
        return rowSpans;
    }

}
