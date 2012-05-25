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

package org.drools.java.nio.fs.file;

import java.util.Set;
import java.util.regex.PatternSyntaxException;

import org.drools.java.nio.IOException;
import org.drools.java.nio.file.FileStore;
import org.drools.java.nio.file.FileSystem;
import org.drools.java.nio.file.InvalidPathException;
import org.drools.java.nio.file.Path;
import org.drools.java.nio.file.PathMatcher;
import org.drools.java.nio.file.WatchService;
import org.drools.java.nio.file.attribute.UserPrincipalLookupService;
import org.drools.java.nio.file.spi.FileSystemProvider;
import org.drools.java.nio.fs.BasePath;

public final class SimpleFileSystem implements FileSystem {

    private final FileSystemProvider provider;

    SimpleFileSystem(final FileSystemProvider provider) {
        this.provider = provider;
    }

    @Override
    public FileSystemProvider provider() {
        return provider;
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public String getSeparator() {
        return System.getProperty("path.separator", "/");
    }

    @Override
    public Iterable<Path> getRootDirectories() {
//        return File.listRoots();
        return null;
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path getPath(String first, String... more) throws InvalidPathException {
        if (more == null) {
            return new BasePath(this, first, false);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(first);
        for (final String segment : more) {
            if (segment.length() > 0) {
                if (sb.length() > 0) {
                    sb.append(getSeparator());
                }
                sb.append(segment);
            }
        }
        return new BasePath(this, sb.toString(), false);
    }

    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) throws IllegalArgumentException, PatternSyntaxException, UnsupportedOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() throws UnsupportedOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WatchService newWatchService() throws UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void close() throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("can't close this file system.");
    }
}
