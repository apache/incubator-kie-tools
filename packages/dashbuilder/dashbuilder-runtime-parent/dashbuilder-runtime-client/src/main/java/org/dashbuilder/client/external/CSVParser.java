/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.external;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.json.Json;
import org.dashbuilder.json.JsonArray;

/**
 * Beta CSV parser which need to be improved according to the rfc4180 specification.
 *
 */
@ApplicationScoped
public class CSVParser implements Function<String, String> {

    final static char DELIMITER = ',';
    final static char QUOTE = '"';

    public JsonArray toJsonArray(String csvContent) {
        var array = Json.createArray();
        var lines = csvContent.split("\n");
        var rowIndex = new AtomicInteger(0);
        // assumes that there a header which we skip
        Arrays.stream(lines)
                .skip(1)
                .forEach(l -> {
                    var chars = l.toCharArray();
                    var row = Json.createArray();
                    var field = new StringBuffer();
                    var n = chars.length;
                    var quoted = false;
                    int colIndex = 0;

                    for (int i = 0; i < n; i++) {
                        var ch = chars[i];
                        if (ch == QUOTE) {
                            quoted = true;
                            ch = chars[++i];
                        }
                        while (i < n) {
                            ch = chars[i];
                            if (ch == QUOTE && quoted) {
                                i++;
                                if (i < n - 1 &&
                                    chars[i] != QUOTE &&
                                    field.length() > 0) {
                                    quoted = false;
                                }
                            }
                            if (i == n) {
                                break;
                            }
                            ch = chars[i];
                            if (ch == DELIMITER && !quoted) {
                                break;
                            }
                            field.append(ch);
                            i++;
                        }
                        quoted = false;
                        row.set(colIndex++, field.toString());
                        field.delete(0, field.length());
                    }
                    array.set(rowIndex.getAndIncrement(), row);
                });

        return array;
    }

    @Override
    public String apply(String input) {
        return toJsonArray(input).toJson();
    }

}
