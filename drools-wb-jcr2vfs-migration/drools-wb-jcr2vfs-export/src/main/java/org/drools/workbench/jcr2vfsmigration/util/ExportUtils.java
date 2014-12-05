/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.jcr2vfsmigration.util;

import java.util.ArrayList;
import java.util.List;

public class ExportUtils {

    private static final String[] JAVA_KEYWORDS = { "package", "import",
            "class", "public", "protected", "private", "extends", "implements",
            "return", "if", "while", "for", "do", "else", "try", "new", "void",
            "catch", "throws", "throw", "static", "final", "break", "continue",
            "super", "finally", "true", "false", "true;", "false;", "null",
            "boolean", "int", "char", "long", "float", "double", "short",
            "abstract", "this", "switch", "assert", "default", "goto", "synchronized",
            "byte", "case", "enum", "instanceof", "transient", "interface", "strictfp", "volatile", "const", "native" };

    private static final String KEYWORD = "global ";

    public String normalizePackageName( String stringToEscape ) {
        String[] nameSplit = stringToEscape.split( "\\." );
        StringBuilder normalizedPkgNameBuilder = new StringBuilder();

        for ( int j = 0; j < nameSplit.length; j++ ) {
            int i = 0;
            if ( j > 0 && j < nameSplit.length ) {
                normalizedPkgNameBuilder.append( "." );
            }
            for (; i < JAVA_KEYWORDS.length; i++ ) {
                if ( JAVA_KEYWORDS[ i ].equals( nameSplit[ j ] ) ) {
                    normalizedPkgNameBuilder.append( "mod_" );
                    normalizedPkgNameBuilder.append( nameSplit[ j ].toLowerCase() );
                    break;
                }
            }
            if ( i == JAVA_KEYWORDS.length ) {
                normalizedPkgNameBuilder.append( nameSplit[ j ].toLowerCase() );
            }
        }
        return normalizedPkgNameBuilder.toString();
    }

    // Method moved from GlobalParser
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

    // Method moved from DRLMigrationUtils
    /**
     * Replaces the hash tag (#) character with two slashes (//), but only for non String occurrences and '#/' occurrences used
     * for DSL and DSLR debugging.
     * <p/>
     * For example:
     * for >>some text with hash tag #<<< is returned >>>some text with hash tag //<<<
     * but for >>>hash tag inside quotes "#"<<< is returned the same string >>>hash tag inside quotes "#"<<<
     */
    public static String migrateStartOfCommentChar(String source) {
        boolean isSingleQuoted = false;
        boolean isDoubleQuoted = false;
        StringBuilder sbResult = new StringBuilder();
        for (int charIndex = 0; charIndex < source.length(); charIndex++) {
            char currentChar = source.charAt(charIndex);
            switch (currentChar) {
                case '\'':
                    boolean isSingleQuoteEscaped = charIndex > 0 && source.charAt(charIndex - 1) == '\\';
                    if (!isSingleQuoteEscaped) {
                        if (isDoubleQuoted) {
                            isSingleQuoted = false;
                        } else {
                            isSingleQuoted = !isSingleQuoted;
                        }
                    } else {
                        // single quote is escaped -> do nothing
                    }
                    sbResult.append(currentChar);
                    break;

                case '"':
                    boolean isDoubleQuoteEscaped = charIndex > 0 && source.charAt(charIndex - 1) == '\\';
                    if (!isDoubleQuoteEscaped) {
                        if (isSingleQuoted) {
                            isDoubleQuoted = false;
                        } else {
                            isDoubleQuoted = !isDoubleQuoted;
                        }
                    } else {
                        // double quote is escaped -> do nothing
                    }
                    sbResult.append(currentChar);
                    break;

                case '#':
                    // '#/' is used in DSL and DSLR for debugging so don't replace it
                    boolean isDslDebugChar = (charIndex + 1 <= source.length() - 1) && source.charAt(charIndex + 1) == '/';
                    if (isSingleQuoted || isDoubleQuoted || isDslDebugChar) {
                        sbResult.append(currentChar);
                    } else {
                        sbResult.append("//");
                    }
                    break;

                default:
                    sbResult.append(currentChar);
            }
        }
        return sbResult.toString();
    }
}
