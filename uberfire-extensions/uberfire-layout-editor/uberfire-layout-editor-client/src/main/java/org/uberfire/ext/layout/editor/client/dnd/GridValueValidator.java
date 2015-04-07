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
