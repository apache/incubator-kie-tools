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

package org.uberfire.java.nio.fs.jgit;

import java.io.File;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileStore;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.java.nio.file.attribute.FileStoreAttributeView;

import static org.uberfire.commons.util.Preconditions.checkNotNull;

public class JGitFileStore implements FileStore {

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
        return File.listRoots()[0].getTotalSpace();
    }

    @Override
    public long getUsableSpace() throws IOException {
        return File.listRoots()[0].getUsableSpace();
    }

    @Override
    public long getUnallocatedSpace() throws IOException {
        return -1;
    }

    @Override
    public boolean supportsFileAttributeView(final Class<? extends FileAttributeView> type) {
        checkNotNull("type", type);

        if (type.equals(BasicFileAttributeView.class)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean supportsFileAttributeView(final String name) {
        if (name.equals("basic")) {
            return true;
        }
        return false;
    }

    @Override
    public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> type) {
        return null;
    }

    @Override
    public Object getAttribute(final String attribute) throws UnsupportedOperationException, IOException {
        if (attribute.equals("totalSpace")) {
            return getTotalSpace();
        }
        if (attribute.equals("usableSpace")) {
            return getUsableSpace();
        }
        if (attribute.equals("unallocatedSpace")) {
            return getUnallocatedSpace();
        }
        if (attribute.equals("readOnly")) {
            return isReadOnly();
        }
        if (attribute.equals("name")) {
            return name();
        }
        if (attribute.equals("type")) {
            return type();
        }
        throw new UnsupportedOperationException("Attribute '" + attribute + "' not available");
    }
}
