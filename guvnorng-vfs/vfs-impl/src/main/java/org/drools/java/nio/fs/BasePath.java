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

package org.drools.java.nio.fs;

import java.io.File;
import java.net.URI;
import java.util.Iterator;

import org.drools.java.nio.IOException;
import org.drools.java.nio.file.ClosedWatchServiceException;
import org.drools.java.nio.file.FileSystem;
import org.drools.java.nio.file.InvalidPathException;
import org.drools.java.nio.file.LinkOption;
import org.drools.java.nio.file.Path;
import org.drools.java.nio.file.WatchKey;
import org.drools.java.nio.file.WatchService;

import static org.drools.java.nio.file.WatchEvent.*;
import static org.drools.java.nio.util.Preconditions.*;

public class BasePath implements Path {

    private final FileSystem fs;
    private final boolean isAbsolute;
    private final String[] names;
    private String toStringFormat;

    public BasePath(final FileSystem fs, final String name, final boolean isAbsolute) {
        checkNotNull("name", name);
        this.fs = checkNotNull("fs", fs);
        this.isAbsolute = isAbsolute;
        this.names = name.split(fs.getSeparator());
    }

    public BasePath(final FileSystem fs, final String[] names, final boolean isAbsolute) {
        this.fs = checkNotNull("fs", fs);
        this.names = checkNotNull("names", names);
        this.isAbsolute = isAbsolute;
    }

    @Override
    public FileSystem getFileSystem() {
        return fs;
    }

    @Override
    public boolean isAbsolute() {
        return isAbsolute;
    }

    @Override
    public Path getRoot() {
        return null;
    }

    @Override
    public Path getFileName() {
        return getName(getNameCount() - 1);
    }

    @Override public Path getParent() {
        if (getNameCount() < 2) {
            return null;
        }
        return subpath(0, getNameCount() - 1);
    }

    @Override
    public int getNameCount() {
        return names.length;
    }

    @Override
    public Path getName(int index) throws IllegalArgumentException {
        return newPath();
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) throws IllegalArgumentException {
        if (isAbsolute && beginIndex == 0 && endIndex == 0) {
            return getRoot();
        }

        return null;
    }

    @Override
    public boolean startsWith(Path other) {
        return false;
    }

    @Override
    public boolean startsWith(String other) throws InvalidPathException {
        return false;
    }

    @Override
    public boolean endsWith(Path other) {
        return false;
    }

    @Override
    public boolean endsWith(String other) throws InvalidPathException {
        return false;
    }

    @Override
    public Path normalize() {
        return null;
    }

    @Override
    public Path resolve(Path other) {
        return null;
    }

    @Override
    public Path resolve(String other) throws InvalidPathException {
        return null;
    }

    @Override
    public Path resolveSibling(Path other) {
        return null;
    }

    @Override
    public Path resolveSibling(String other) throws InvalidPathException {
        return null;
    }

    @Override
    public Path relativize(Path other) throws IllegalArgumentException {
        return null;
    }

    @Override
    public URI toUri() throws IOException, SecurityException {
        return URI.create(fs.provider().getScheme() + "://" + toString());
    }

    @Override
    public Path toAbsolutePath() throws IOException, SecurityException {
        if (isAbsolute()) {
            return this;
        }
        return null;
    }

    @Override
    public Path toRealPath(final LinkOption... options)
            throws IOException, SecurityException {
        return this;
    }

    @Override
    public File toFile()
            throws UnsupportedOperationException {
        return new File(toString());
    }

    @Override
    public int compareTo(Path o) {
        return 0;
    }

    @Override
    public Iterator<Path> iterator() {
        return null;
    }

    @Override
    public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers)
            throws UnsupportedOperationException, IllegalArgumentException,
            ClosedWatchServiceException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WatchKey register(WatchService watcher, Kind<?>... events)
            throws UnsupportedOperationException, IllegalArgumentException,
            ClosedWatchServiceException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        if (toStringFormat == null) {
            synchronized (this) {
                final StringBuilder sb = new StringBuilder();
                if (getRoot() != null) {
                    sb.append(getRoot().toString()).append(getSeparator());
                }
                for (int i = 0; i < names.length; i++) {
                    final String name = names[i];
                    sb.append(name);
                    if (i != (names.length - 1)) {
                        sb.append(getSeparator());
                    }
                }
                toStringFormat = sb.toString();
            }
        }
        return toStringFormat;
    }

    private String getSeparator() {
        return fs.getSeparator();
    }

    private Path newPath() {
        return null;
    }
}
