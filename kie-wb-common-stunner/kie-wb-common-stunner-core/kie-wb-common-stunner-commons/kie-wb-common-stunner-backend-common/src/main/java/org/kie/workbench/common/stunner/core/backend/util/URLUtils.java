/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.backend.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

import org.apache.commons.io.IOUtils;
import org.kie.workbench.common.stunner.core.backend.service.BackendFileSystemManager;

public class URLUtils {

    public static byte[] readBytesFromURL(final String uri) throws IOException {
        return readBytes(new URL(uri).openStream());
    }

    public static byte[] readBytes(final InputStream is) throws IOException {
        try {
            return IOUtils.toByteArray(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static String readFromURL(final String uri) throws IOException {
        return readFromURL(new URL(uri));
    }

    public static String readFromURL(final URL url) throws IOException {
        final StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(),
                                                                              BackendFileSystemManager.UT8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
            return builder.toString();
        }
    }

    public static String buildDataURIFromURL(final String url) throws Exception {
        return buildDataURIFromURL(new URL(url));
    }

    public static String buildDataURIFromURL(final URL url) throws Exception {
        return buildDataURIFromStream(url.getFile(),
                                      url.openStream());
    }

    public static String buildDataURIFromStream(final String fileName,
                                                final InputStream inputStream) throws Exception {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final String contentType = guessContentType(fileName,
                                                    inputStream);
        if (null != contentType) {
            final byte[] chunk = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(chunk)) > 0) {
                os.write(chunk, 0, bytesRead);
            }
            os.flush();
            inputStream.close();
            return "data:" + contentType + ";base64," +
                    Base64.getEncoder().encodeToString(os.toByteArray());
        } else {
            throw new UnsupportedOperationException("Content type is undefined.");
        }
    }

    public static String guessContentType(final String fileName,
                                          final InputStream stream) throws Exception {
        final String contentType = URLConnection.guessContentTypeFromStream(stream);
        if (null == contentType) {
            final int index = fileName.lastIndexOf(".");
            return index >= 0 ?
                    "image/" + fileName.substring(index + 1, fileName.length()) : null;
        }
        return contentType;
    }
}
