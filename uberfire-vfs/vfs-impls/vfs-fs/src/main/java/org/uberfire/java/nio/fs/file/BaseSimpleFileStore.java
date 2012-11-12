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

package org.uberfire.java.nio.fs.file;

import java.io.File;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileStore;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.java.nio.file.attribute.FileStoreAttributeView;

import static org.kie.commons.validation.PortablePreconditions.*;

public abstract class BaseSimpleFileStore implements FileStore {

    BaseSimpleFileStore(final Path path) {
    }

    BaseSimpleFileStore(final File[] roots, final Path path) {
    }

    @Override
    public String type() {
        return null;
    }

    @Override
    public boolean isReadOnly() {
        return false;
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
        checkNotEmpty("name", name);

        if (name.equals("basic")) {
            return true;
        }
        return false;
    }

    @Override
    public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> type) {
        checkNotNull("type", type);

        return null;
    }

    @Override
    public Object getAttribute(final String attribute) throws UnsupportedOperationException, IOException {
        checkNotEmpty("attribute", attribute);

        if (attribute.equals("totalSpace")) {
            return getTotalSpace();
        }
        if (attribute.equals("usableSpace")) {
            return getUsableSpace();
        }
        if (attribute.equals("readOnly")) {
            return isReadOnly();
        }
        if (attribute.equals("name")) {
            return name();
        }
        throw new UnsupportedOperationException("Attribute '" + attribute + "' not available");
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof FileStore)) {
            return false;
        }

        final FileStore ofs = (FileStore) o;

        return name().equals(ofs.name());
    }

    @Override
    public int hashCode() {
        return name().hashCode();
    }

}
