package org.drools.workbench.screens.enums.client.editor;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods to parse Enums.
 */
public class EnumParser {

    private EnumParser() {
    }

    /**
     * Parse enum definitions from String
     * @param content
     * @return A List of Enum definitions
     */
    public static List<EnumRow> parseEnums( final String content ) {
        final List<EnumRow> enums = new ArrayList<EnumRow>();

        if ( content == null || content.isEmpty() ) {
            return enums;

        } else {
            final String[] lines = content.split( "\\n" );
            for ( int i = 0; i < lines.length; i++ ) {
                final String line = lines[ i ].trim();
                final EnumRow er = parseEnum( line );
                if ( er != null ) {
                    enums.add( er );
                }
            }
        }
        return enums;
    }

    private static EnumRow parseEnum( final String line ) {
        if ( line.equals( "" ) || line.startsWith( "#" ) || line.startsWith( "//" ) ) {
            return null;
        }

        final int colonIndex = line.indexOf( ":" );
        if ( colonIndex < 0 ) {
            return null;
        }
        String factField = line.substring( 0,
                                           colonIndex );
        factField = factField.trim();
        final int dotIndex = factField.indexOf( "." );
        if ( dotIndex < 0 ) {
            return null;
        }

        String factName = factField.substring( 0,
                                               dotIndex );
        factName = factName.trim();
        String fieldName = factField.substring( dotIndex + 1,
                                                factField.length() );
        fieldName = fieldName.trim();

        if ( !factName.startsWith( "'" ) ) {
            return null;
        }
        if ( !fieldName.endsWith( "'" ) ) {
            return null;
        }
        factName = factName.substring( 1 ).trim();
        fieldName = fieldName.substring( 0, fieldName.length() - 1 ).trim();

        final String context = line.substring( colonIndex + 1 ).trim();

        final EnumRow er = new EnumRow( factName,
                                        fieldName,
                                        context );
        if ( !er.isValid() ) {
            return null;
        }
        return er;
    }

}
