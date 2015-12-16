/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.datamodeller.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.lang3.text.StrBuilder;

public class StringEscapeUtils {

    /**
     * Comes from  org.apache.commons.lang.StringEscapeUtils
     */
    public static String escapeJava( String str ) {
        return org.apache.commons.lang.StringEscapeUtils.escapeJava( str );
    }

    /**
     * Comes from  org.apache.commons.lang.StringEscapeUtils
     */
    public static String unescapeJava( String str ) {
        return org.apache.commons.lang.StringEscapeUtils.unescapeJava( str );
    }

    /**
     * Comes from  org.apache.commons.lang.StringEscapeUtils
     */
    public static String unescapeJavaUTF( String str ) {
        if ( str == null ) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter( str.length() );
            unescapeJavaUTF( writer, str );
            return writer.toString();
        } catch ( IOException ioe ) {
            // this should never ever happen while writing to a StringWriter
            throw new RuntimeException( ioe );
        }
    }

    /**
     * Comes from  org.apache.commons.lang.StringEscapeUtils
     */
    public static void unescapeJavaUTF( Writer out,
                                        String str ) throws IOException {
        if ( out == null ) {
            throw new IllegalArgumentException( "The Writer must not be null" );
        }
        if ( str == null ) {
            return;
        }
        int sz = str.length();
        StrBuilder unicode = new StrBuilder( 4 );
        boolean hadSlash = false;
        boolean inUnicode = false;
        for ( int i = 0; i < sz; i++ ) {
            char ch = str.charAt( i );
            if ( inUnicode ) {
                // if in unicode, then we're reading unicode
                // values in somehow
                unicode.append( ch );
                if ( unicode.length() == 4 ) {
                    // unicode now contains the four hex digits
                    // which represents our unicode character
                    try {
                        int value = Integer.parseInt( unicode.toString(), 16 );
                        out.write( (char) value );
                        unicode.setLength( 0 );
                        inUnicode = false;
                        hadSlash = false;
                    } catch ( NumberFormatException nfe ) {
                        throw new RuntimeException( "Unable to parse unicode value: " + unicode, nfe );
                    }
                }
                continue;
            }
            if ( hadSlash ) {
                // handle an escaped value
                hadSlash = false;
                if ( ch == 'u' ) {
                    // uh-oh, we're in unicode country....
                    inUnicode = true;
                } else {
                    out.write( '\\' );
                    out.write( ch );
                }
                continue;
            } else if ( ch == '\\' ) {
                hadSlash = true;
                continue;
            }
            out.write( ch );
        }
        if ( hadSlash ) {
            // then we're in the weird case of a \ at the end of the
            // string, let's output it anyway.
            out.write( '\\' );
        }
    }

    /**
     * Comes from org.apache.commons.lang.StringEscapeUtils
     */
    public static String escapeJavaNonUTFChars( String str ) {
        return escapeJavaNonUTFChars( str, false, false );
    }

    /**
     * Comes from org.apache.commons.lang.StringEscapeUtils
     */
    private static String escapeJavaNonUTFChars( String str,
                                                 boolean escapeSingleQuotes,
                                                 boolean escapeForwardSlash ) {
        if ( str == null ) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter( str.length() * 2 );
            escapeJavaNonUTFChars( writer, str, escapeSingleQuotes, escapeForwardSlash );
            return writer.toString();
        } catch ( IOException ioe ) {
            // this should never ever happen while writing to a StringWriter
            throw new RuntimeException( ioe );
        }
    }

    /**
     * Comes from org.apache.commons.lang.StringEscapeUtils
     */
    private static void escapeJavaNonUTFChars( Writer out,
                                               String str,
                                               boolean escapeSingleQuote,
                                               boolean escapeForwardSlash ) throws IOException {
        if ( out == null ) {
            throw new IllegalArgumentException( "The Writer must not be null" );
        }
        if ( str == null ) {
            return;
        }
        int sz;
        sz = str.length();
        for ( int i = 0; i < sz; i++ ) {
            char ch = str.charAt( i );
            if ( ch < 32 ) {
                switch ( ch ) {
                    case '\b':
                        out.write( '\\' );
                        out.write( 'b' );
                        break;
                    case '\n':
                        out.write( '\\' );
                        out.write( 'n' );
                        break;
                    case '\t':
                        out.write( '\\' );
                        out.write( 't' );
                        break;
                    case '\f':
                        out.write( '\\' );
                        out.write( 'f' );
                        break;
                    case '\r':
                        out.write( '\\' );
                        out.write( 'r' );
                        break;
                    default:
                        out.write( ch );
                        break;
                }
            } else {
                switch ( ch ) {
                    case '\'':
                        if ( escapeSingleQuote ) {
                            out.write( '\\' );
                        }
                        out.write( '\'' );
                        break;
                    case '"':
                        out.write( '\\' );
                        out.write( '"' );
                        break;
                    case '\\':
                        out.write( '\\' );
                        out.write( '\\' );
                        break;
                    case '/':
                        if ( escapeForwardSlash ) {
                            out.write( '\\' );
                        }
                        out.write( '/' );
                        break;
                    default:
                        out.write( ch );
                        break;
                }
            }
        }
    }

    public static String unquote( String str ) {
        return PortableStringUtils.removeLastChar( PortableStringUtils.removeFirstChar( str, '"' ), '"' );
    }

    public static String unquoteSingle( String str ) {
        return PortableStringUtils.removeLastChar( PortableStringUtils.removeFirstChar( str, '\'' ), '\'' );
    }

    public static boolean isSingleQuoted( String str ) {
        return str != null && str.length() >= 2 && str.charAt( 0 ) == '\'' && str.charAt( str.length() -1 ) == '\'';
    }
}
