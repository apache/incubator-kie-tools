/*
 * Copyright 2012 JBoss Inc
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

package org.drools.java.nio.fs.eclipse;

import org.drools.java.nio.IOException;
import org.drools.java.nio.file.FileStore;
import org.drools.java.nio.file.attribute.FileAttributeView;
import org.drools.java.nio.file.attribute.FileStoreAttributeView;

public class EclipseFileStore implements FileStore {

    @Override
    public String name() {
        return "/";
    }

    @Override
    public String type() {
        return "root";
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public long getTotalSpace() throws IOException {
        throw new IOException();
    }

    @Override
    public long getUsableSpace() throws IOException {
        throw new IOException();
    }

    @Override
    public long getUnallocatedSpace() throws IOException {
        throw new IOException();
    }

    @Override
    public boolean supportsFileAttributeView(final Class<? extends FileAttributeView> type) {
        return false;
    }

    @Override
    public boolean supportsFileAttributeView(final String name) {
        return false;
    }

    @Override
    public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> type) {
        return null;
    }

    @Override
    public Object getAttribute(final String attribute) throws UnsupportedOperationException, IOException {
        throw new UnsupportedOperationException("Attribute '" + attribute + "' not available");
    }
}
