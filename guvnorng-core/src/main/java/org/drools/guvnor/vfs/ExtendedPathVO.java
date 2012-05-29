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

package org.drools.guvnor.vfs;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.drools.java.nio.IOException;
import org.drools.java.nio.file.ClosedWatchServiceException;
import org.drools.java.nio.file.ExtendedPath;
import org.drools.java.nio.file.FileSystem;
import org.drools.java.nio.file.InvalidPathException;
import org.drools.java.nio.file.LinkOption;
import org.drools.java.nio.file.Path;
import org.drools.java.nio.file.WatchEvent;
import org.drools.java.nio.file.WatchKey;
import org.drools.java.nio.file.WatchService;
import org.drools.java.nio.file.attribute.FileTime;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ExtendedPathVO implements ExtendedPath {

    private String toStringFormat;
    private boolean isAbsolute;
    private ExtendedPathVO root;
    private boolean exists;
    private boolean isDirectory;
    private boolean isRegularFile;
    private List<ExtendedPathVO> names = new ArrayList<ExtendedPathVO>();
    private String toUriStringFormat;

    public ExtendedPathVO(final ExtendedPath extendedPath) {
        this.toStringFormat = extendedPath.toString();
        this.toUriStringFormat = extendedPath.toUriAsString();

        this.isAbsolute = extendedPath.isAbsolute();
        if (extendedPath.getRoot() != null) {
            if (extendedPath.getRoot() instanceof ExtendedPathVO) {
                this.root = (ExtendedPathVO) extendedPath.getRoot();
            } else {
                this.root = new ExtendedPathVO(extendedPath.getRoot());
            }
        } else {
            this.root = null;
        }
        this.exists = extendedPath.exists();
        this.isDirectory = extendedPath.isDirectory();
        this.isRegularFile = extendedPath.isRegularFile();
        for (final Path path : extendedPath) {
            names.add(new ExtendedPathVO(path));
        }
    }

    public ExtendedPathVO(final Path path) {
        this.toStringFormat = path.toString();
        if (path instanceof ExtendedPath) {
            this.toUriStringFormat = ((ExtendedPath) path).toUriAsString();
        } else {
            this.toUriStringFormat = "";
        }

        this.isAbsolute = path.isAbsolute();
        if (path.getRoot() != null) {
            if (path.getRoot() instanceof ExtendedPathVO) {
                this.root = (ExtendedPathVO) path.getRoot();
            } else {
                this.root = new ExtendedPathVO(path.getRoot());
            }
        } else {
            this.root = null;
        }
        this.exists = false;
        this.isDirectory = false;
        this.isRegularFile = false;
    }

    public ExtendedPathVO() {
    }

    @Override
    public FileSystem getFileSystem() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAbsolute() {
        return isAbsolute;
    }

    @Override
    public Path getRoot() {
        return root;
    }

    @Override
    public Path getFileName() {
        return names.get(getNameCount() - 1);
    }

    @Override
    public Path getParent() {
        return null;
    }

    @Override
    public int getNameCount() {
        return names.size();
    }

    @Override
    public Path getName(int index) throws IllegalArgumentException {
        return names.get(index);
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean startsWith(Path other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean startsWith(String other) throws InvalidPathException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean endsWith(Path other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean endsWith(String other) throws InvalidPathException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Path normalize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Path resolve(Path other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Path resolve(String other) throws InvalidPathException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Path resolveSibling(Path other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Path resolveSibling(String other) throws InvalidPathException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Path relativize(Path other) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public URI toUri() throws IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Path toAbsolutePath() throws IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Path toRealPath(LinkOption... options) throws IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public File toFile() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int compareTo(Path o) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("all")
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

            @Override public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws UnsupportedOperationException, IllegalArgumentException, ClosedWatchServiceException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events) throws UnsupportedOperationException, IllegalArgumentException, ClosedWatchServiceException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean exists() {
        return exists;
    }

    @Override public FileTime lastModifiedTime() {
        return null;
    }

    @Override public FileTime lastAccessTime() {
        return null;
    }

    @Override public FileTime creationTime() {
        return null;
    }

    @Override public boolean isRegularFile() {
        return isRegularFile;
    }

    @Override public boolean isDirectory() {
        return isDirectory;
    }

    @Override public boolean isSymbolicLink() {
        throw new UnsupportedOperationException();
    }

    @Override public boolean isOther() {
        throw new UnsupportedOperationException();
    }

    @Override public long size() {
        return 0;
    }

    @Override public Object fileKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return toStringFormat;
    }

    @Override
    public String toUriAsString() {
        return toUriStringFormat;
    }

}
