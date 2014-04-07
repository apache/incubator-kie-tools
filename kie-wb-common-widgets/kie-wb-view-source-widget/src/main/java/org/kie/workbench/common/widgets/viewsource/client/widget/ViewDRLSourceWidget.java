/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.widgets.viewsource.client.widget;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;

public class ViewDRLSourceWidget
        extends Composite {

    private final FlexTable table = new FlexTable();

    public ViewDRLSourceWidget() {
        initWidget(table);
    }

    public void setContent( final String content ) {
        clearContent();
        final String[] rows = content.split( "\n" );

        for ( int i = 0; i < rows.length; i++ ) {


            table.setHTML( i,
                           0,
                           "<span style='font-family: Courier, monospace; color:grey;'>"
                                   + ( i + 1 )
                                   + ".</span>" );
            table.setHTML( i,
                           1,
                           "<span style='font-family: Courier, monospace; color:green;' >|</span>" );
            table.setHTML( i,
                           2,
                           addSyntaxHighlights( rows[ i ] ) );

        }
    }

    public void clearContent() {
        table.removeAllRows();
    }

    private String addSyntaxHighlights( String text ) {

        if ( text.trim().startsWith( "#" ) ) {
            text = "<span style='font-family: Courier, monospace; color:green'>"
                    + text
                    + "</span>";
        } else {

            String[] keywords = { "rule", "when", "then", "end", "accumulate", "collect", "from", "null", "over", "lock-on-active", "date-effective", "date-expires", "no-loop", "auto-focus", "activation-group", "agenda-group", "ruleflow-group",
                    "entry-point", "duration", "package", "import", "dialect", "salience", "enabled", "attributes", "extend", "template", "query", "declare", "function", "global", "eval", "exists", "forall", "action", "reverse", "result", "end",
                    "init" };

            for ( String keyword : keywords ) {
                final String match = "\\b" + keyword + "\\b";
                text = text.replaceAll( match,
                                        "<span style='font-family: Courier, monospace; color:red;'>"
                                                + keyword
                                                + "</span>" );
            }

            text = handleStrings( "\"",
                                  text );
        }
        text = text.replace( "\t",
                             "&nbsp;&nbsp;&nbsp;&nbsp;" );

        return "<span style='font-family: Courier, monospace;'>" + text + "</span>";
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

            String newString = "<span style='font-family: Courier, monospace; color:green;'>"
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
