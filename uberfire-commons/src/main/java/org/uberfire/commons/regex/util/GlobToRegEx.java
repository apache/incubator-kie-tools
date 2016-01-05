/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.commons.regex.util;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * GlobToRegEx utility class for glob patterns.
 * <p/>
 * This code has been borrowed and then adapted from <a href="http://http://jakarta.apache.org/oro/">Jakarta ORO</a>.
 * <p/>
 */
public final class GlobToRegEx {

    private GlobToRegEx() {

    }

    public static String globToRegex( final String glob ) {
        checkNotNull( "glob", glob );
        boolean inCharSet = false;
        final StringBuilder buffer = new StringBuilder( 2 * glob.length() );

        final char[] pattern = glob.toCharArray();
        int ch;

        for ( ch = 0; ch < pattern.length; ch++ ) {
            switch ( pattern[ ch ] ) {
                case '*':
                    if ( inCharSet ) {
                        buffer.append( '*' );
                    } else {
                        buffer.append( ".*" );
                    }
                    break;
                case '?':
                    if ( inCharSet ) {
                        buffer.append( '?' );
                    } else {
                        buffer.append( ".?" );
                    }
                    break;
                case '[':
                    inCharSet = true;
                    buffer.append( pattern[ ch ] );

                    if ( ch + 1 < pattern.length ) {
                        switch ( pattern[ ch + 1 ] ) {
                            case '!':
                            case '^':
                                buffer.append( '^' );
                                ++ch;
                                continue;
                            case ']':
                                buffer.append( ']' );
                                ++ch;
                                continue;
                        }
                    }
                    break;
                case ']':
                    inCharSet = false;
                    buffer.append( pattern[ ch ] );
                    break;
                case '\\':
                    buffer.append( '\\' );
                    if ( ch == pattern.length - 1 ) {
                        buffer.append( '\\' );
                    } else if ( __isGlobMetaCharacter( pattern[ ch + 1 ] ) ) {
                        buffer.append( pattern[ ++ch ] );
                    } else {
                        buffer.append( '\\' );
                    }
                    break;
                default:
                    if ( !inCharSet && __isRegExMetaCharacter( pattern[ ch ] ) ) {
                        buffer.append( '\\' );
                    }
                    buffer.append( pattern[ ch ] );
                    break;
            }
        }

        return buffer.toString();
    }

    private static boolean __isRegExMetaCharacter( char ch ) {
        return ( "'*?+[]()|^$.{}\\".indexOf( ch ) >= 0 );
    }

    private static boolean __isGlobMetaCharacter( char ch ) {
        return ( "*?[]".indexOf( ch ) >= 0 );
    }

}
