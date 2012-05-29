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
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.drools.java.nio.IOException;
import org.drools.java.nio.file.ClosedWatchServiceException;
import org.drools.java.nio.file.ExtendedPath;
import org.drools.java.nio.file.FileSystem;
import org.drools.java.nio.file.InvalidPathException;
import org.drools.java.nio.file.LinkOption;
import org.drools.java.nio.file.Path;
import org.drools.java.nio.file.WatchKey;
import org.drools.java.nio.file.WatchService;
import org.drools.java.nio.file.attribute.FileTime;

import static org.drools.java.nio.file.WatchEvent.*;
import static org.drools.java.nio.util.Preconditions.*;

public class BasePath implements ExtendedPath {

    private static final Pattern PATH_PATTERN = Pattern.compile("\\\\|/");

    private final FileSystem fs;
    private final boolean isAbsolute;
    private final boolean isRealPath;
    private final boolean usesWindowsFormat;
    private final String[] names;
    private final String root;
    private String toStringFormat;

    private final boolean isFile;
    private final boolean isDirectory;
    private final boolean exists;

    public BasePath(final FileSystem fs, final String root) {
        checkNotNull("path", root);
        this.fs = checkNotNull("fs", fs);
        this.isAbsolute = true;
        this.usesWindowsFormat = false;
        this.isRealPath = false;
        this.names = null;
        this.root = null;
        this.toStringFormat = root;
        this.isFile = false;
        this.isDirectory = true;
        this.exists = true;
    }

    public BasePath(final FileSystem fs, final File file) {
        this(fs, file.getAbsolutePath(), false);
    }

    public BasePath(final FileSystem fs, final String path, final boolean isRealPath) {
        checkNotNull("path", path);
        this.fs = checkNotNull("fs", fs);
        this.isRealPath = isRealPath;
        final boolean isRooted = path.startsWith("/");
        final boolean hasWindowsDrive = path.matches("^/?[A-Za-z]+\\:.*");
        this.usesWindowsFormat = path.matches(".*\\\\.*");

        final String workPath;
        if (isRooted && hasWindowsDrive) {
            workPath = path.substring(1);
        } else {
            workPath = path;
        }

        if (isRooted || hasWindowsDrive) {
            isAbsolute = true;
            String[] regexResult = PATH_PATTERN.split(workPath);
            String[] tempNames = new String[regexResult.length - 1];
            System.arraycopy(regexResult, 1, tempNames, 0, regexResult.length - 1);
            if (hasWindowsDrive) {
                this.root = regexResult[0] + getSeparator();
            } else {
                this.root = "/";
            }
            this.names = tempNames;
        } else {
            isAbsolute = false;
            this.root = null;
            this.names = PATH_PATTERN.split(workPath);
        }
        boolean tempExists = false;
        boolean tempIsDirectory = false;
        boolean tempIsFile = false;

        try {
            final File temp = toFile();
            tempExists = temp.exists();
            if (tempExists) {
                tempIsDirectory = temp.isDirectory();
                tempIsFile = temp.isFile();
            } else {
                tempIsDirectory = false;
                tempIsFile = false;
            }
        } catch (Exception ex) {
        }

        this.exists = tempExists;
        this.isDirectory = tempIsDirectory;
        this.isFile = tempIsFile;
    }

    public BasePath(final FileSystem fs, final String[] names, final String root, final boolean isAbsolute, final boolean isRealPath, final boolean usesWindowsFormat) {
        this.fs = checkNotNull("fs", fs);
        this.names = checkNotNull("names", names);
        this.isAbsolute = isAbsolute;
        this.isRealPath = isRealPath;
        this.usesWindowsFormat = usesWindowsFormat;
        this.root = root;

        boolean tempExists = false;
        boolean tempIsDirectory = false;
        boolean tempIsFile = false;

        try {
            final File temp = toFile();
            tempExists = temp.exists();
            if (tempExists) {
                tempIsDirectory = temp.isDirectory();
                tempIsFile = temp.isFile();
            } else {
                tempIsDirectory = false;
                tempIsFile = false;
            }
        } catch (Exception ex) {
        }

        this.exists = tempExists;
        this.isDirectory = tempIsDirectory;
        this.isFile = tempIsFile;
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
        if (root == null) {
            return null;
        }
        return new BasePath(fs, root);
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
        return new BasePath(fs, names[index], false);
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
    public Path resolve(final Path other) {
        if (other.isAbsolute()) {
            return other;
        }
        return new BasePath(fs, toString() + getSeparator() + other.toString(), isRealPath);
    }

    @Override
    public Path resolve(String other) throws InvalidPathException {
        return resolve(getFileSystem().getPath(other));
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
        if (!isRealPath && fs.provider().isDefault()) {
            return URI.create("default://" + toString());
        }
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
        if (!isRealPath) {
            return new BasePath(fs, names, root, isAbsolute, true, usesWindowsFormat);
        }
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
        return new Iterator<Path>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < getNameCount();
            }

            @Override
            public Path next() {
                if (i < getNameCount()) {
                    Path result = getName(i);
                    i++;
                    return result;
                } else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
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
                    sb.append(getRoot().toString());
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

    @Override
    public String toUriAsString() {
        return toUri().toString();
    }

    @Override
    public boolean exists() {
        return exists;
    }

    @Override
    public FileTime lastModifiedTime() {
        return null;
    }

    @Override
    public FileTime lastAccessTime() {
        return null;
    }

    @Override
    public FileTime creationTime() {
        return null;
    }

    @Override
    public boolean isRegularFile() {
        return isFile;
    }

    @Override
    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public boolean isSymbolicLink() {
        return false;
    }

    @Override
    public boolean isOther() {
        return false;
    }

    @Override
    public long size() {
        return -1;
    }

    @Override
    public Object fileKey() {
        return null;
    }

    private String getSeparator() {
        if (usesWindowsFormat) {
            return "\\";
        }
        return fs.getSeparator();
    }

    private Path newPath() {
        return null;
    }
}
