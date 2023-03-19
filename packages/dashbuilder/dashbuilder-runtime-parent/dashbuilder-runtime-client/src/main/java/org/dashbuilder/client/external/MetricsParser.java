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

import org.dashbuilder.json.Json;
import org.dashbuilder.json.JsonArray;

/**
 * Convert micrometer metrics format to JSON array
 *
 */
public class MetricsParser implements Function<String, String> {

    private static final char METRIC_DELIMITER = ' ';
    private static final char LABEL_OPEN = '{';
    private static final char LABEL_CLOSE = '}';
    private static final String COMMENT = "#";

    protected JsonArray metricToJsonArray(String line) {
        var array = Json.createArray();
        var chars = line.toCharArray();

        var metricBuffer = new StringBuffer();
        var labelsBuffer = new StringBuffer();
        var valueBuffer = new StringBuffer();
        var currentBuffer = metricBuffer;

        for (int i = 0; i < chars.length; i++) {
            var ch = chars[i];

            if (ch == LABEL_OPEN) {
                currentBuffer = labelsBuffer;
            }

            else if ((ch == METRIC_DELIMITER && currentBuffer != labelsBuffer) || ch == LABEL_CLOSE) {
                currentBuffer = valueBuffer;
            } else {
                currentBuffer.append(ch);
            }
        }

        if (metricBuffer.length() > 0 &&
            valueBuffer.length() > 0 &&
            !metricBuffer.toString().trim().isEmpty() &&
            !valueBuffer.toString().trim().isEmpty()) {
            array.set(0, metricBuffer.toString());
            array.set(1, labelsBuffer.toString());
            array.set(2, valueBuffer.toString());
        }
        return array;
    }

    protected JsonArray metricsToJsonArray(String input) {
        var array = Json.createArray();
        var lines = input.split("\n");
        var i = new AtomicInteger(0);
        Arrays.stream(lines)
                .filter(line -> !line.startsWith(COMMENT))
                .map(this::metricToJsonArray)
                .filter(a -> !a.isEmpty())
                .forEach(row -> array.set(i.getAndIncrement(), row));
        return array;
    }

    @Override
    public String apply(String input) {
        var array = metricsToJsonArray(input);
        return array.toJson();
    }

}
