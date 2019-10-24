/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.messageconsole.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class CSVConverter {

    public static String convertTable(final List<List<String>> table) {
        if (table == null) {
            return "";
        }
        return table.stream()
                    .map(CSVConverter::convertRow)
                    .collect(Collectors.joining("\n"))
                    .toString();
    }

    private static String convertRow(final List<String> row) {
        return row.stream()
                   .map(CSVConverter::convertValue)
                   .collect(Collectors.joining(","));
    }

    private static String convertValue(final String value) {
        return quote(escape(value));
    }

    private static String escape(final String value) {
        return value.replaceAll("\n", "")
                    .replaceAll("\"", "\"\"");
    }

    private static String quote(final String value) {
        return "\"" + value + "\"";
    }
}
