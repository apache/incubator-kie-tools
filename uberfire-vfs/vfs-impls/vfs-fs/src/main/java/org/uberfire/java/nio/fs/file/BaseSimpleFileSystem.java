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

package org.uberfire.java.nio.fs.file;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.InvalidPathException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.PathMatcher;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.attribute.UserPrincipalLookupService;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.fs.base.GeneralPathImpl;

import static org.uberfire.java.nio.util.Preconditions.*;

public abstract class BaseSimpleFileSystem implements FileSystem {

    private final FileSystemProvider provider;
    private final String defaultDirectory;
    private final Set<String> supportedFileAttributeViews;
    private final File[] roots;

    BaseSimpleFileSystem(final FileSystemProvider provider, final String path) {
        this(File.listRoots(), provider, path);
    }

    BaseSimpleFileSystem(final File[] roots, final FileSystemProvider provider, final String path) {
        checkNotNull("roots", roots);
        checkCondition("should have at least one root", roots.length > 0);
        this.roots = roots;
        this.provider = provider;
        this.defaultDirectory = validateDefaultDir(path);
        this.supportedFileAttributeViews = Collections.unmodifiableSet(new HashSet<String>() {{
            add("basic");
        }});
    }

    private String validateDefaultDir(final String path) throws IllegalArgumentException {
        checkNotEmpty("path", path);
        if (!GeneralPathImpl.create(this, path, false).isAbsolute()) {
            throw new IllegalArgumentException();
        }
        return path;
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
        return System.getProperty("file.separator", "/");
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        return supportedFileAttributeViews;
    }

    @Override
    public Path getPath(String first, String... more) throws InvalidPathException {
        if (more == null) {
            return GeneralPathImpl.create(this, first, false);
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
        return GeneralPathImpl.create(this, sb.toString(), false);
    }

    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern)
            throws IllegalArgumentException, PatternSyntaxException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService()
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WatchService newWatchService()
            throws UnsupportedOperationException, IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("can't close this file system.");
    }

    File[] listRoots() {
        return roots;
    }
}
