/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.backend.gzip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;

class GzipResponseServletOutputStream extends ServletOutputStream {

    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String CONTENT_ENCODING_HEADER = "Content-Encoding";

    private ByteArrayOutputStream baos;
    private GZIPOutputStream gzipStream;
    private HttpServletResponse response;
    private ServletOutputStream outputStream;
    private boolean closed;

    GzipResponseServletOutputStream(final HttpServletResponse response) throws IOException {
        super();
        this.closed = false;
        this.response = response;
        this.outputStream = response.getOutputStream();
        this.baos = new ByteArrayOutputStream();
        this.gzipStream = new GZIPOutputStream(baos);
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }

        gzipStream.finish();
        final byte[] bytes = baos.toByteArray();

        response.addHeader(CONTENT_LENGTH_HEADER, Integer.toString(bytes.length));
        response.addHeader(CONTENT_ENCODING_HEADER, GzipFilter.GZIP);

        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
        closed = true;
    }

    @Override
    public void flush() throws IOException {
        if (closed) {
            return;
        }

        gzipStream.flush();
    }

    @Override
    public void write(final int b) throws IOException {
        if (closed) {
            throw new IOException("Output stream already closed.");
        }

        gzipStream.write((byte) b);
    }

    @Override
    public void write(final byte[] data) throws IOException {
        write(data, 0, data.length);
    }

    @Override
    public void write(final byte[] data, final int offset, final int length) throws IOException {
        if (closed) {
            throw new IOException("Output stream already closed.");
        }

        gzipStream.write(data, offset, length);
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(final WriteListener writeListener) {
        // Empty on purpose
    }
}