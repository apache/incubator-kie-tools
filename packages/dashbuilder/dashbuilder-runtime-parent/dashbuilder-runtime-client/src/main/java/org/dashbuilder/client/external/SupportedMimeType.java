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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.dashbuilder.client.external.csv.CSVColumnsFunction;
import org.dashbuilder.client.external.csv.CSVParser;
import org.dashbuilder.client.external.metrics.MetricsColumnsFunction;
import org.dashbuilder.client.external.metrics.MetricsParser;
import org.dashbuilder.dataset.def.DataColumnDef;

enum SupportedMimeType {

    // JSON is a no-op transformer
    JSON("application/json", "json", v -> v),
    CSV("text/csv", "csv", new CSVParser(), new CSVColumnsFunction()),
    // metrics is only matched by URL, otherwise it takes precedence on CSV when it is text/plain
    METRIC("", "metrics", new MetricsParser(), new MetricsColumnsFunction());

    String mimeType;

    String extension;

    UnaryOperator<String> tranformer;

    Function<String, List<DataColumnDef>> columnsFunction;

    private SupportedMimeType(String type, String extension, UnaryOperator<String> transformer) {
        this(type, extension, transformer, v -> Collections.emptyList());
    }

    private SupportedMimeType(String type, String extension, UnaryOperator<String> tranformer,
                              Function<String, List<DataColumnDef>> columnsFunction) {
        this.mimeType = type;
        this.extension = extension;
        this.tranformer = tranformer;
        this.columnsFunction = columnsFunction;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public static Optional<SupportedMimeType> byMimeTypeOrUrl(String mimeType, String url) {
        // not working with GWT...
        //return byMimeType(mimeType).or(() -> byUrl(url));
        var op = byMimeType(mimeType);
        if (!op.isPresent()) {
            op = byUrl(url);
        }
        return op;
    }

    public static Optional<SupportedMimeType> byMimeType(String mimeType) {
        if (mimeType == null || mimeType.trim().isEmpty()) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(t -> !t.getMimeType().isEmpty() && mimeType.toLowerCase().startsWith(t.getMimeType()))
                .findFirst();
    }

    public static Optional<SupportedMimeType> byUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(t -> url.toLowerCase().endsWith(t.getExtension()))
                .findFirst();
    }
}
