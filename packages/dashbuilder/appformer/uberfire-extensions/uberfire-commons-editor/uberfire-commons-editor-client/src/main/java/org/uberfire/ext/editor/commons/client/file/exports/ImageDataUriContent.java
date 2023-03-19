/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.editor.commons.client.file.exports;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public final class ImageDataUriContent {

    private final String uri;
    private final String mimeType;
    private final String data;

    public static ImageDataUriContent create(final String uri) {
        checkNotNull("uri",
                     uri);
        final String[] uriParts = uri.split(",");
        return new ImageDataUriContent(uri,
                                       parseMimeType(uriParts[0]),
                                       uriParts[1]);
    }

    private ImageDataUriContent(final String uri,
                                final String mimeType,
                                final String data) {
        this.uri = uri;
        this.mimeType = mimeType;
        this.data = data;
    }

    public String getUri() {
        return uri;
    }

    public String getData() {
        return data;
    }

    public String getMimeType() {
        return mimeType;
    }

    private static String parseMimeType(final String uriDataType) {
        final String mimeType = uriDataType.split(":")[1];
        final int encIndex = mimeType.indexOf(";");
        return encIndex > -1 ? mimeType.substring(0,
                                                  encIndex) : mimeType;
    }
}
