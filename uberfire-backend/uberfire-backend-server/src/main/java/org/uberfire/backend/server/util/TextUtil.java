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

package org.uberfire.backend.server.util;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    private static final String EMPTY_STRING = "";

    // Any character except unicode letters and digits 0-9
    private static final Pattern nonASCIIp1 = Pattern.compile( "[^\\p{L}\\p{Nd}]" );
    private static final Matcher nonASCIIm1 = nonASCIIp1.matcher( EMPTY_STRING );

    // Any ASCII character except those between code point 0 (NULL) and 127 (DEL)
    private static final Pattern nonASCIIp2 = Pattern.compile( "[^\\x00-\\x7f]" );
    private static final Matcher nonASCIIm2 = nonASCIIp2.matcher( EMPTY_STRING );

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

    public static String normalizeUserName( String input ) {
        nonASCIIm1.reset( Normalizer.normalize( input, Normalizer.Form.NFD) );
        return nonASCIIm2.reset(nonASCIIm1.replaceAll(EMPTY_STRING)).replaceAll(EMPTY_STRING);
    }

}
