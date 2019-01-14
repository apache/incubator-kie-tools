/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.m2repo.backend.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;

@SuppressWarnings("serial")
class MockFileItem implements FileItem {

    private final String fileName;
    private final InputStream fileStream;

    MockFileItem(final String fileName,
                 final InputStream fileStream) {
        this.fileName = fileName;
        this.fileStream = fileStream;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return fileStream;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public byte[] get() {
        return null;
    }

    @Override
    public String getString(String encoding) throws UnsupportedEncodingException {
        return null;
    }

    @Override
    public String getString() {
        return null;
    }

    @Override
    public void write(File file) throws Exception {
    }

    @Override
    public void delete() {
    }

    @Override
    public String getFieldName() {
        return null;
    }

    @Override
    public void setFieldName(String name) {
    }

    @Override
    public boolean isFormField() {
        return false;
    }

    @Override
    public void setFormField(boolean state) {
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public FileItemHeaders getHeaders() {
        return null;
    }

    @Override
    public void setHeaders(FileItemHeaders fileItemHeaders) {
    }
}
