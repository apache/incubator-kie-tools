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

package org.uberfire.java.nio.file;

import java.io.File;
import java.net.URI;

import org.uberfire.java.nio.IOException;

public interface Path extends Comparable<Path>, Iterable<Path>, Watchable {

    FileSystem getFileSystem();

    boolean isAbsolute();

    Path getRoot();

    Path getFileName();

    Path getParent();

    int getNameCount();

    Path getName(int index) throws IllegalArgumentException;

    Path subpath(int beginIndex, int endIndex) throws IllegalArgumentException;

    boolean startsWith(Path other);

    boolean startsWith(String other) throws InvalidPathException;

    boolean endsWith(Path other);

    boolean endsWith(String other) throws InvalidPathException;

    Path normalize();

    // resolution and relativization

    Path resolve(Path other);

    Path resolve(String other) throws InvalidPathException;

    Path resolveSibling(Path other);

    Path resolveSibling(String other) throws InvalidPathException;

    Path relativize(Path other) throws IllegalArgumentException;

    URI toUri() throws IOException, SecurityException;

    Path toAbsolutePath() throws IOException, SecurityException;

    Path toRealPath(LinkOption... options) throws IOException, SecurityException;

    File toFile() throws UnsupportedOperationException;
}