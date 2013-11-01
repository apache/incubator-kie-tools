package org.uberfire.backend.server.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class TextUtil {

    private static final String EMPTY_STRING = "";

    // Any character except unicode letters and digits 0-9, allowing '.' and '-'
    private static final Pattern repoP1 = Pattern.compile( "[^\\p{L}\\p{Nd}\\x2D\\x2E]" );

    // Any ASCII character except those between code point 0 (NULL) and 127 (DEL), allowing '.' and '-'
    private static final Pattern repoP2 = Pattern.compile( "[^\\x00-\\x7f\\x2D\\x2E]" );

    // Match repetitions of '.', '-' or combinations thereof
    private static final Pattern repoP3 = Pattern.compile( "[\\x2D\\x2E][\\x2D\\x2E]{1,}+" );

    public static String normalizeRepositoryName( String input ) {
        // Remove leading and/or trailing '.' and '-'
        if ( input.startsWith( "." ) || input.startsWith( "-" ) ) input = normalizeRepositoryName( input.substring( 1 ) );
        if ( input.endsWith( "." ) || input.endsWith( "-" ) ) input = normalizeRepositoryName( input.substring( 0, input.length() - 1 ) );
        // Repository operations are not too frequent so instantiate corresponding matchers on demand
        return repoP3.matcher(
                    repoP2.matcher(
                        repoP1.matcher( Normalizer.normalize( input, Normalizer.Form.NFD ) ).replaceAll( EMPTY_STRING )
                                  ).replaceAll( EMPTY_STRING )
                              ).replaceAll( EMPTY_STRING );
    }

}
