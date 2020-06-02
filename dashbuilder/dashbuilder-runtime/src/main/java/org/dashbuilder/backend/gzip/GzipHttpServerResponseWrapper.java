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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

class GzipHttpServletResponseWrapper extends HttpServletResponseWrapper {

    private HttpServletResponse response;
    private ServletOutputStream stream;
    private PrintWriter writer;

    GzipHttpServletResponseWrapper(final HttpServletResponse response) {
        super(response);
        this.response = response;
        this.stream = null;
        this.writer = null;
    }

    @Override
    public void flushBuffer() throws IOException {
        stream.flush();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException("getOutputStream() was already called");
        }

        if (stream == null) {
            stream = createOutputStream();
        }

        return stream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer != null) {
            return writer;
        }

        if (stream != null) {
            throw new IllegalStateException("getWriter() was already called");
        }

        stream = createOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
        return writer;
    }

    @Override
    public void setContentLength(final int length) {
        // Empty on purpose
    }

    private ServletOutputStream createOutputStream() throws IOException {
        return new GzipResponseServletOutputStream(response);
    }

    void close() {
        try {
            if (writer != null) {
                writer.close();
            } else if (stream != null) {
                stream.close();
            }
        } catch (final IOException e) {
            // Error on closing writer or stream.
        }
    }
}
