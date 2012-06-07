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

package org.drools.java.nio.fs.base;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class GeneralPathImpl implements Path {

    private static final Pattern PATH_PATTERN = Pattern.compile("\\\\|/");
    private static final Pattern WINDOWS_DRIVER = Pattern.compile("^/?[A-Z|a-z]+(:).*");

    private final FileSystem fs;
    private final boolean usesWindowsFormat;

    private final boolean isAbsolute;
    private final byte[] path;
    private final List<Pair> offsets = new ArrayList<Pair>();
    private final boolean isRoot;
    private final boolean isRealPath;

    private String toStringFormat;
    private GeneralFileAttributes attrs;
    private File file;

    public static GeneralPathImpl createRoot(final FileSystem fs, final String root, boolean isRealPath) {
        checkNotNull("fs", fs);
        checkNotNull("path", root);

        return new GeneralPathImpl(fs, root, true, isRealPath);
    }

    public static GeneralPathImpl createFromFile(final FileSystem fs, final File file) {
        checkNotNull("fs", fs);
        checkNotNull("file", file);

        return new GeneralPathImpl(fs, file);
    }

    public static GeneralPathImpl create(final FileSystem fs, final String path, boolean isRealPath) {
        checkNotNull("fs", fs);
        checkNotNull("path", path);

        return new GeneralPathImpl(fs, path, false, isRealPath);
    }

    private GeneralPathImpl(final FileSystem fs, final File file) {
        this(checkNotNull("fs", fs), checkNotNull("file", file).getAbsolutePath(), true, false);
    }

    private GeneralPathImpl(final FileSystem fs, final String path, boolean isRoot, boolean isRealPath) {
        this.fs = checkNotNull("fs", fs);
        this.path = checkNotNull("path", path).getBytes();
        this.isRoot = isRoot;
        this.isRealPath = isRealPath;
        this.usesWindowsFormat = path.matches(".*\\\\.*");

        final boolean isRooted = isRoot ? true : path.startsWith("/");
        final Matcher hasWindowsDrive = WINDOWS_DRIVER.matcher(path);

        if (isRooted || hasWindowsDrive.matches()) {
            this.isAbsolute = true;
        } else {
            this.isAbsolute = false;
        }

        int lastOffset = 0;
        int windowsDriveEndsAt = -1;
        if (isRooted && hasWindowsDrive.matches()) {
            windowsDriveEndsAt = hasWindowsDrive.toMatchResult().end(1);
            lastOffset = 1;
        }

        for (int i = 0; i < this.path.length; i++) {
            final byte b = this.path[i];
            if (b == getSeparator() && i >= windowsDriveEndsAt) {
                if (i > 1) {
                    offsets.add(new Pair(lastOffset, i - 1));
                } else {
                    offsets.add(new Pair(lastOffset, i));
                }
                i++;
                lastOffset = i;
            }
        }
        offsets.add(new Pair(lastOffset, this.path.length - 1));
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
            return GeneralPathImpl.createRoot(fs, substring(0), isRealPath);
        }
        return null;
    }

    private String substring(int index) {
        final Pair offset = offsets.get(index);
        byte[] result = new byte[offset.getB() - offset.getA() + 1];
        System.arraycopy(path, offset.getA(), result, 0, result.length);

        return new String(result);
    }

    private String substring(int beginIndex, int endIndex) {
        final Pair offsetBegin = offsets.get(beginIndex);
        final Pair offsetEnd = offsets.get(endIndex);
        byte[] result = new byte[offsetEnd.getB() - offsetBegin.getA()];
        System.arraycopy(path, offsetBegin.getA(), result, 0, offsetEnd.getB());

        return new String(result);
    }

    @Override
    public Path getFileName() {
        return getName(getNameCount() - 1);
    }

    @Override
    public Path getParent() {
        if (getNameCount() < 2) {
            return null;
        }
        return subpath(0, getNameCount() - 1);
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

        return GeneralPathImpl.create(fs, substring(index), isRealPath);
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

        if (isAbsolute && beginIndex == 0 && endIndex == 0) {
            return getRoot();
        }

        return GeneralPathImpl.create(fs, substring(beginIndex, endIndex), isRealPath);
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
        return GeneralPathImpl.create(fs, toString() + getSeparator() + other.toString(), isRealPath);
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
        final StringBuilder result = new StringBuilder();
        if (!toString().startsWith("/")) {
            result.append("/");
        }
        result.append(toString().toString().replace("\\", "/"));
        return result.toString();
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
        if (isRealPath) {
            return this;
        }
        return GeneralPathImpl.create(fs, toString(), true);
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
