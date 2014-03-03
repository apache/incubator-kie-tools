package org.drools.workbench.jcr2vfsmigration.migrater;

import java.util.ArrayList;
import java.util.List;


/**
 * Utility methods to parse a global Config
 */
public final class GlobalParser {

    private static final String KEYWORD = "global ";

    private GlobalParser() {
    }

    public static List<String> parseGlobals( final String content ) {
        List<String> globals = new ArrayList<String>();

        if ( content == null || content.trim().equals( "" ) ) {
            return globals;
        } else {
            final String[] lines = content.split( "\\n" );

            for ( int i = 0; i < lines.length; i++ ) {
                String line = lines[ i ].trim();
                if ( !( line.equals( "" ) || line.startsWith( "#" ) ) ) {
                    if ( line.startsWith( KEYWORD ) ) {
                        line = line.substring( KEYWORD.length() ).trim();
                        if ( line.endsWith( ";" ) ) {
                            line = line.substring( 0, line.length() - 1 );
                        }
                        globals.add( line );
                    }
                }
            }

            return globals;
        }

    }

}
