/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.common.services.backend.archive;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipWriter {

    private ZipOutputStream outputStream;

    public ZipWriter(final OutputStream outputStream) {
        this.outputStream = new ZipOutputStream(new BufferedOutputStream(outputStream));
    }

    public void addFile(final ZipEntry zipEntry,
                        final InputStream inputStream) throws IOException {
        final int BUFFER = 2048;
        byte data[] = new byte[BUFFER];

        BufferedInputStream origin = new BufferedInputStream(inputStream,
                                                             BUFFER);

        outputStream.putNextEntry(zipEntry);
        int count;
        while ((count = origin.read(data,
                                    0,
                                    BUFFER)) != -1) {
            outputStream.write(data,
                               0,
                               count);
        }

        outputStream.flush();
        origin.close();
    }

    public void close() throws IOException {
        outputStream.close();
    }
}
