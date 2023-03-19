/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.common.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import org.dashbuilder.json.JsonArray;
import org.dashbuilder.json.JsonObject;
import org.dashbuilder.json.JsonString;
import org.dashbuilder.json.JsonValue;

public class JsonSourceViewer extends Composite {

    private final static String INDENT = "    ";

    public FlexTable table;

    public JsonSourceViewer() {
        this.table = new FlexTable();
        initWidget( table );
    }

    public void setContent(JsonObject jsonSource) {
        clearContent();

        String jsonSourceString = formatJsonObjectAsString(jsonSource, "");
        final String[] rows = jsonSourceString.split( "\n" );
        for ( int i = 0; i < rows.length; i++ ) {
            String escaped = replaceLeadingWhitespaces( rows [ i ] );
            table.setHTML( i, 0, escaped );
        }
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
        sb.append(text.substring(i));
        return sb.toString();
    }

    private void clearContent() {
        table.removeAllRows();
    }

    private String formatJsonValueAsString(JsonValue jsonValue, String indent) {
        if (jsonValue == null) {
            return "";
        }
        if (jsonValue instanceof JsonObject) {
            return formatJsonObjectAsString((JsonObject) jsonValue, indent);
        }
        else if (jsonValue instanceof JsonArray) {
            return formatJsonArrayAsString((JsonArray) jsonValue, indent);
        }
        else if (jsonValue instanceof JsonString) {
            return "\"" + ((JsonString) jsonValue).getString() + "\"";
        }
        else {
            return jsonValue.asString();
        }
    }

    private String formatJsonObjectAsString(JsonObject jsonObject, String indent) {
        if ( jsonObject == null ) return "";

        String newIndent = indent + INDENT;
        StringBuilder sb = new StringBuilder( "{" );

        String[] keys = jsonObject.keys();
        for ( int i = 0; i < keys.length; i++ ) {
            sb.append("\n");
            sb.append(newIndent).append("\"").append(keys[i]).append("\"").append(": ");
            sb.append(newIndent).append(formatJsonValueAsString(jsonObject.get(keys[i]), newIndent));
            sb.append(i == keys.length - 1 ? "" : ",").append("\n");
        }
        sb.append(indent).append("}");
        return sb.toString();
    }

    private String formatJsonArrayAsString(JsonArray jsonArray, String indent) {
        if (jsonArray == null) {
            return "";
        }

        String newIndent = indent + INDENT;
        StringBuilder sb = new StringBuilder("[");

        for (int i = 0; i < jsonArray.length(); i++) {
            sb.append("\n");
            sb.append(newIndent ).append(formatJsonValueAsString(jsonArray.get(i), newIndent ));
            sb.append(i == jsonArray.length() - 1 ? "\n" : ", ");
        }
        sb.append(indent).append("]");
        return sb.toString();
    }
}
