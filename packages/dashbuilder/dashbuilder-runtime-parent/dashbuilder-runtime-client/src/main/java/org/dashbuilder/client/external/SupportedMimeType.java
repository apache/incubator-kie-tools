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
import java.util.Optional;

enum SupportedMimeType {

    JSON("application/json", "json"),
    CSV("text/csv", "csv");

    String mimeType;

    String extension;

    private SupportedMimeType(String type, String extension) {
        this.mimeType = type;
        this.extension = extension;
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
        if (mimeType == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(t -> mimeType.toLowerCase().startsWith(t.getMimeType()))
                .findFirst();
    }

    public static Optional<SupportedMimeType> byUrl(String url) {
        if (url == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(t -> url.toLowerCase().endsWith(t.getExtension()))
                .findFirst();
    }
}
