/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.dashbuilder.client.external;

import java.util.Arrays;
import java.util.function.UnaryOperator;

import javax.enterprise.context.ApplicationScoped;

/**
 * Beta CSV parser which need to be improved according to the rfc4180 specification.
 *
 */
@ApplicationScoped
public class CSVParser implements UnaryOperator<String> {

    private static final char SEPARATOR = ',';
    private static final char QUOTE = '\"';

    public String toJsonArray(String csvContent) {
        var jsonArray = new StringBuilder("[");
        var lines = csvContent.split("\n");
        Arrays.stream(lines).skip(1).forEach(line -> buildRow(jsonArray, line));
        String finalJsonArray = jsonArray.toString();
        finalJsonArray = finalJsonArray.substring(0, jsonArray.length() - 1);
        finalJsonArray += "]";
        finalJsonArray = finalJsonArray.replace("True", "true");
        finalJsonArray = finalJsonArray.replace("False", "false");
        return finalJsonArray;
    }

    private void buildRow(StringBuilder jsonArray, String line) {
        var row = new StringBuilder("[\"");
        int i = 0;
        var len = line.length();
        while (i < len) {
            if (line.charAt(i) != SEPARATOR && line.charAt(i) != QUOTE) {
                row.append(line.charAt(i));
            } else if (i < len - 2 && line.charAt(i) == QUOTE) {
                i++;
                i = retrieveQuotedField(line, row, i);
            } else if (line.charAt(i) == SEPARATOR) {
                row.append("\",\"");
            }
            i++;
        }
        row.append("\"]");
        jsonArray.append(row);
        jsonArray.append(",");
    }

    private int retrieveQuotedField(String line, StringBuilder row, int i) {
        // quoted field
        boolean isEscapedQuote = false;
        while (i < line.length() - 1 && (line.charAt(i) != QUOTE || (isEscapedQuote = isEscapedQuote(line, i)))) {
            if (isEscapedQuote) {
                row.append("\\\"");
                i += 2;
                isEscapedQuote = false;
            } else {
                row.append(line.charAt(i));
                i++;
            }
        }
        return i;
    }

    boolean isEscapedQuote(String line, int i) {
        return line.charAt(i) == QUOTE && i < line.length() - 1 && line.charAt(i + 1) == QUOTE;
    }

    @Override
    public String apply(String input) {
        return toJsonArray(input);
    }

}
