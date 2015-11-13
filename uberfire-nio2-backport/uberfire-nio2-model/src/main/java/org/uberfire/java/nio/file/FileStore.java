/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.java.nio.file;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.java.nio.file.attribute.FileStoreAttributeView;

public interface FileStore {

    String name();

    String type();

    boolean isReadOnly();

    long getTotalSpace() throws IOException;

    long getUsableSpace() throws IOException;

    long getUnallocatedSpace() throws IOException;

    boolean supportsFileAttributeView(Class<? extends FileAttributeView> type);

    boolean supportsFileAttributeView(String name);

    <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> type);

    Object getAttribute(String attribute) throws UnsupportedOperationException, IOException;
}
