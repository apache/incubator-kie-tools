/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.javaeditor.client.widget;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;

public class ViewJavaSourceWidget extends Composite {

    private final FlexTable table = new FlexTable();

    public ViewJavaSourceWidget() {
        initWidget( table );
    }

    public void setContent( final String content ) {
        clearContent();
        final String[] rows = content.split( "\n" );

        boolean inSideBlockComment = false;
        boolean lineComment = false;

        for ( int i = 0; i < rows.length; i++ ) {

            String s = rows[i].trim();

            // Only evaluate if a line starts with '/*' if you're not already inside one
            if (!inSideBlockComment) inSideBlockComment = s.startsWith( "/*" );
            lineComment = !inSideBlockComment && s.startsWith( "//" );

            // Escape leading white spaces and html brackets BEFORE applying syntax highlighting
            // (which wraps the text with '<span ...')
            String escaped = replaceLeadingWhitespaces( rows [ i ] );
            escaped = escapeHtmlBrackets( escaped );

            table.setHTML( i,
                    0,
                    "<span style='font-family:monospace; color:grey;'>"
                            + ( i + 1 )
                            + ".</span>" );
            table.setHTML( i,
                    1,
                    "<span style='font-family:monospace; color:green;' >|</span>" );
            table.setHTML( i,
                    2,
                    addSyntaxHighlights( escaped, inSideBlockComment || lineComment )
            );
            // While inside a block comment, evaluate if line ends with '*/'
            if (inSideBlockComment) inSideBlockComment = !s.endsWith("*/");
        }
    }

    public void clearContent() {
        table.removeAllRows();
    }

    private String replaceLeadingWhitespaces( String text ) {
        String s = text.trim();
        if ( s.length() == 0) return "";
        StringBuilder sb = new StringBuilder("");
        int i = 0;
        while ( ' ' == text.charAt( i ) ) {
            sb.append( "&nbsp;" );
            i++;
        }
        sb.append( text.substring( i ) );
        return sb.toString();
    }

    private String escapeHtmlBrackets( String s ) {
        return s.replaceAll( "<", "&lt;" ).replaceAll( ">", "&gt;" );
    }

    private String addSyntaxHighlights( String text, boolean isComment ) {

        if ( isComment ) {
            text = "<span style='font-family:monospace; color:green'>"
                    + text
                    + "</span>";
        } else {

            String [] kws = {
                    "abstract", "continue",     "for",          "new",          "switch",
                    "assert",   "default",      "if",           "package",      "synchronized",
                    "boolean",  "do",           "goto",         "private",      "this",
                    "break",    "double",       "implements",   "protected",    "throw",
                    "byte",     "else",         "import",       "public",       "throws",
                    "case",     "enum",         "instanceof",   "return",       "transient",
                    "catch",    "extends",      "int",          "short",        "try",
                    "char",     "final",        "interface",    "static",       "void",
                    "class",    "finally",      "long",         "strictfp",     "volatile",
                    "const",    "float",        "native",       "super",        "while",
                    // literals
                    "null",     "true",         "false"
            };

            for ( String keyword : kws ) {
                final String match = "\\b" + keyword + "\\b";
                text = text.replaceAll( match, "<span style='font-family:monospace; color:red;'>"
                                                + keyword
                                                + "</span>" );
            }

            text = handleStrings( "\"", text );
        }

        return text;
    }

    private String handleStrings( String character,
                                  String text ) {
        int stringStart = text.indexOf( character );
        while ( stringStart >= 0 ) {
            int stringEnd = text.indexOf( character,
                    stringStart + 1 );
            if ( stringEnd < 0 ) {
                stringStart = -1;
                break;
            }

            String oldString = text.substring( stringStart,
                    stringEnd + 1 );

            String newString = "<span style='font-family:monospace; color:green;'>"
                    + oldString
                    + "</span>";

            String beginning = text.substring( 0,
                    stringStart );
            String end = text.substring( stringEnd + 1 );

            text = beginning
                    + newString
                    + end;

            int searchStart = stringStart
                    + newString.length()
                    + 1;

            if ( searchStart < text.length() ) {
                stringStart = text.indexOf( character,
                        searchStart );
            } else {
                stringStart = -1;
            }
        }
        return text;
    }
}
