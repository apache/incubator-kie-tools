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

package org.uberfire.java.nio.fs.base;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.ClosedWatchServiceException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.InvalidPathException;
import org.uberfire.java.nio.file.LinkOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.java.nio.file.WatchService;

import static org.uberfire.java.nio.file.WatchEvent.*;
import static org.uberfire.java.nio.util.Preconditions.*;

public abstract class AbstractPath implements Path, AttrHolder<GeneralFileAttributes> {

    private static final Pattern WINDOWS_DRIVER = Pattern.compile("^/?[A-Z|a-z]+(:).*");
    private static final String DEFAULT_WINDOWS_DRIVER = "C:";

    protected final FileSystem fs;
    protected final boolean usesWindowsFormat;

    protected final boolean isAbsolute;
    protected final byte[] path;
    protected final List<Pair> offsets = new ArrayList<Pair>();
    protected final boolean isRoot;
    protected final boolean isRealPath;

    protected String toStringFormat;
    protected File file;
    protected GeneralFileAttributes attrs;

    abstract Path newPath(FileSystem fs, String substring, boolean realPath);

    abstract Path newRoot(FileSystem fs, String substring, boolean realPath);

    protected AbstractPath(final FileSystem fs, final File file) {
        this(checkNotNull("fs", fs), checkNotNull("file", file).getAbsolutePath(), true, false);
    }

    protected AbstractPath(final FileSystem fs, final String path, boolean isRoot, boolean isRealPath) {
        this.fs = checkNotNull("fs", fs);
        this.path = checkNotNull("path", path).getBytes();
        this.isRealPath = isRealPath;
        this.usesWindowsFormat = path.matches(".*\\\\.*");

        final boolean isRooted = isRoot ? true : path.startsWith("/");
        final Matcher hasWindowsDrive = WINDOWS_DRIVER.matcher(path);

        if (isRooted || hasWindowsDrive.matches()) {
            this.isAbsolute = true;
        } else {
            this.isAbsolute = false;
        }

        int lastOffset = this.isAbsolute ? 1 : 0;
        int windowsDriveEndsAt = -1;
        if (isAbsolute && hasWindowsDrive.matches()) {
            windowsDriveEndsAt = hasWindowsDrive.toMatchResult().end(1) + 1;
            lastOffset = windowsDriveEndsAt;
        }

        for (int i = lastOffset; i < this.path.length; i++) {
            final byte b = this.path[i];
            if (b == getSeparator() && i >= windowsDriveEndsAt) {
                offsets.add(new Pair(lastOffset, i));
                i++;
                lastOffset = i;
            }
        }
        if (lastOffset < this.path.length) {
            offsets.add(new Pair(lastOffset, this.path.length));
        }

        if (this.path.length == 1 && offsets.size() == 0) {
            this.isRoot = true;
        } else if (hasWindowsDrive.matches() && offsets.size() == 0) {
            this.isRoot = true;
        } else {
            this.isRoot = isRoot;
        }
    }

    private boolean hasWindowsDriver(final String text) {
        checkNotEmpty("text", text);
        return WINDOWS_DRIVER.matcher(text).matches();
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
        if (isRoot) {
            return this;
        }
        if (isAbsolute) {
            return newRoot(fs, substring(-1), isRealPath);
        }
        return null;
    }

    private String substring(int index) {
        final byte[] result;
        if (index == -1) {
            result = new byte[offsets.get(0).getA()];
            System.arraycopy(path, 0, result, 0, result.length);
        } else {
            final Pair offset = offsets.get(index);
            result = new byte[offset.getB() - offset.getA()];
            System.arraycopy(path, offset.getA(), result, 0, result.length);
        }

        return new String(result);
    }

    private String substring(int beginIndex, int endIndex) {
        final int initPos;
        if (beginIndex == -1) {
            initPos = 0;
        } else {
            initPos = offsets.get(beginIndex).getA();
        }
        final Pair offsetEnd = offsets.get(endIndex);
        byte[] result = new byte[offsetEnd.getB() - initPos];
        System.arraycopy(path, initPos, result, 0, result.length);

        return new String(result);
    }

    @Override
    public Path getFileName() {
        if (getNameCount() == 0) {
            return null;
        }
        return getName(getNameCount() - 1);
    }

    @Override
    public Path getParent() {
        if (getNameCount() <= 0) {
            return null;
        }
        if (getNameCount() == 1) {
            return getRoot();
        }
        return newPath(fs, substring(-1, getNameCount() - 2), isRealPath);
    }

    @Override
    public int getNameCount() {
        return offsets.size();
    }

    @Override
    public Path getName(int index) throws IllegalArgumentException {
        if (index < 0) {
            throw new IllegalArgumentException();
        }
        if (index >= offsets.size()) {
            throw new IllegalArgumentException();
        }

        return newPath(fs, substring(index), isRealPath);
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) throws IllegalArgumentException {
        if (beginIndex < 0) {
            throw new IllegalArgumentException();
        }
        if (beginIndex >= offsets.size()) {
            throw new IllegalArgumentException();
        }
        if (endIndex > offsets.size()) {
            throw new IllegalArgumentException();
        }
        if (beginIndex >= endIndex) {
            throw new IllegalArgumentException();
        }

        return newPath(fs, substring(beginIndex, endIndex - 1), isRealPath);
    }

    @Override
    public URI toUri() throws IOException, SecurityException {
        if (!isAbsolute()) {
            return toAbsolutePath().toUri();
        }
        if (fs.provider().isDefault() && !isRealPath) {
            try {
                return new URI("default", "", toURIString(), null);
            } catch (URISyntaxException e) {
                return null;
            }
        }
        try {
            return new URI(fs.provider().getScheme(), "", toURIString(), null);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private String toURIString() {
        if (usesWindowsFormat) {
            return "/" + toString().replace("\\", "/");
        }
        return toString();
    }

    @Override
    public Path toAbsolutePath() throws IOException, SecurityException {
        if (isAbsolute()) {
            return this;
        }

        return newPath(fs, defaultDirectory() + toString(), isRealPath);
    }

    private String defaultDirectory() {
        if (usesWindowsFormat) {
            final String result = new File("").getAbsolutePath().replaceAll("/", "\\\\") + "\\";

            if (!hasWindowsDriver(result)) {
                return DEFAULT_WINDOWS_DRIVER + result;
            }
            return result;
        }
        return new File("").getAbsolutePath() + "/";
    }

    @Override
    public Path toRealPath(final LinkOption... options)
            throws IOException, SecurityException {
        if (isRealPath) {
            return this;
        }
        return newPath(fs, toString(), true);
    }

    @Override
    public File toFile()
            throws UnsupportedOperationException {
        if (file == null) {
            synchronized (this) {
                file = new File(toString());
            }
        }
        return file;
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
    public boolean startsWith(final Path other) {
        checkNotNull("other", other);
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean startsWith(final String other) throws InvalidPathException {
        checkNotNull("other", other);
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean endsWith(final Path other) {
        checkNotNull("other", other);
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean endsWith(final String other) throws InvalidPathException {
        checkNotNull("other", other);
        throw new UnsupportedOperationException();
    }

    @Override
    public Path normalize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Path resolve(final Path other) {
        checkNotNull("other", other);
        if (other.isAbsolute()) {
            return other;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public Path resolve(final String other) throws InvalidPathException {
        checkNotNull("other", other);
        throw new UnsupportedOperationException();
    }

    @Override
    public Path resolveSibling(final Path other) {
        checkNotNull("other", other);
        throw new UnsupportedOperationException();
    }

    @Override
    public Path resolveSibling(final String other) throws InvalidPathException {
        checkNotNull("other", other);
        throw new UnsupportedOperationException();
    }

    @Override
    public Path relativize(final Path other) throws IllegalArgumentException {
        checkNotNull("other", other);
        throw new UnsupportedOperationException();
    }

    @Override
    public int compareTo(final Path other) {
        checkNotNull("other", other);
        throw new UnsupportedOperationException();
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
            toStringFormat = new String(path);
        }
        return toStringFormat;
    }

    private char getSeparator() {
        if (usesWindowsFormat) {
            return '\\';
        }
        return fs.getSeparator().toCharArray()[0];
    }

    public GeneralFileAttributes getAttrs() {
        if (attrs == null) {
            this.attrs = new GeneralFileAttributes(this);
        }
        return attrs;
    }

    public void clearCache() {
        attrs = null;
        file = null;
    }

    @Override
    public boolean equals(final Object o) {
        checkNotNull("o", o);

        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractPath)) {
            return false;
        }

        AbstractPath paths = (AbstractPath) o;

        if (isAbsolute != paths.isAbsolute) {
            return false;
        }
        if (isRealPath != paths.isRealPath) {
            return false;
        }
        if (isRoot != paths.isRoot) {
            return false;
        }
        if (usesWindowsFormat != paths.usesWindowsFormat) {
            return false;
        }
        if (!fs.equals(paths.fs)) {
            return false;
        }
        if (!Arrays.equals(path, paths.path)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = fs != null ? fs.hashCode() : 0;
        result = 31 * result + (usesWindowsFormat ? 1 : 0);
        result = 31 * result + (isAbsolute ? 1 : 0);
        result = 31 * result + (path != null ? Arrays.hashCode(path) : 0);
        result = 31 * result + (isRoot ? 1 : 0);
        result = 31 * result + (isRealPath ? 1 : 0);
        return result;
    }

    private static class Pair {

        private final int a;
        private final int b;

        Pair(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public int getB() {
            return b;
        }
    }
}
