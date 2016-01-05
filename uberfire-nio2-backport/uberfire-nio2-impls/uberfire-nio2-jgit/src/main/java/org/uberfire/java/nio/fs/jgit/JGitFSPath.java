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

package org.uberfire.java.nio.fs.jgit;

import java.io.File;
import java.net.URI;
import java.util.Iterator;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.FSPath;
import org.uberfire.java.nio.file.ClosedWatchServiceException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.InvalidPathException;
import org.uberfire.java.nio.file.LinkOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.java.nio.file.WatchService;

public class JGitFSPath implements FSPath {

    private final JGitFileSystem fs;

    public JGitFSPath( final JGitFileSystem fs ) {
        this.fs = fs;
    }

    @Override
    public FileSystem getFileSystem() {
        return fs;
    }

    @Override
    public boolean isAbsolute() {
        return true;
    }

    @Override
    public Path getRoot() {
        return null;
    }

    @Override
    public Path getFileName() {
        return null;
    }

    @Override
    public Path getParent() {
        return null;
    }

    @Override
    public int getNameCount() {
        return -1;
    }

    @Override
    public Path getName( int index ) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Path subpath( int beginIndex,
                         int endIndex ) throws IllegalArgumentException {
        return null;
    }

    @Override
    public boolean startsWith( Path other ) {
        return false;
    }

    @Override
    public boolean startsWith( String other ) throws InvalidPathException {
        return false;
    }

    @Override
    public boolean endsWith( Path other ) {
        return false;
    }

    @Override
    public boolean endsWith( String other ) throws InvalidPathException {
        return false;
    }

    @Override
    public Path normalize() {
        return this;
    }

    @Override
    public Path resolve( Path other ) {
        return null;
    }

    @Override
    public Path resolve( String other ) throws InvalidPathException {
        return null;
    }

    @Override
    public Path resolveSibling( Path other ) {
        return null;
    }

    @Override
    public Path resolveSibling( String other ) throws InvalidPathException {
        return null;
    }

    @Override
    public Path relativize( Path other ) throws IllegalArgumentException {
        return null;
    }

    @Override
    public URI toUri() throws IOException, SecurityException {
        return URI.create( fs.toString() );
    }

    @Override
    public Path toAbsolutePath() throws IOException, SecurityException {
        return this;
    }

    @Override
    public Path toRealPath( LinkOption... options ) throws IOException, SecurityException {
        return this;
    }

    @Override
    public File toFile() throws UnsupportedOperationException {
        return null;
    }

    @Override
    public int compareTo( Path path ) {
        return 0;
    }

    @Override
    public Iterator<Path> iterator() {
        return null;
    }

    @Override
    public WatchKey register( WatchService watcher,
                              WatchEvent.Kind<?>[] events,
                              WatchEvent.Modifier... modifiers ) throws UnsupportedOperationException, IllegalArgumentException, ClosedWatchServiceException, IOException, SecurityException {
        return null;
    }

    @Override
    public WatchKey register( WatchService watcher,
                              WatchEvent.Kind<?>... events ) throws UnsupportedOperationException, IllegalArgumentException, ClosedWatchServiceException, IOException, SecurityException {
        return null;
    }
}
