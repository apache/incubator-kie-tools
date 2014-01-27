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

package org.uberfire.java.nio.file;

import java.util.Set;

import org.uberfire.commons.lifecycle.Disposable;
import org.uberfire.java.nio.Closeable;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.attribute.UserPrincipalLookupService;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

public interface FileSystem extends Closeable,
                                    Disposable {

    FileSystemProvider provider();

    boolean isOpen();

    boolean isReadOnly();

    String getSeparator();

    Iterable<Path> getRootDirectories();

    Iterable<FileStore> getFileStores();

    Set<String> supportedFileAttributeViews();

    Path getPath( String first,
                  String... more ) throws InvalidPathException;

    PathMatcher getPathMatcher( String syntaxAndPattern ) throws IllegalArgumentException, PatternSyntaxException, UnsupportedOperationException;

    UserPrincipalLookupService getUserPrincipalLookupService() throws UnsupportedOperationException;

    WatchService newWatchService() throws UnsupportedOperationException, IOException;
}
