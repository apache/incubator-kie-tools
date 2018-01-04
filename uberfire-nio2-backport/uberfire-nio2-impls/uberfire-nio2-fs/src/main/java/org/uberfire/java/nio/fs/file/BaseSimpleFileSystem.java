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

package org.uberfire.java.nio.fs.file;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.base.GeneralPathImpl;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.InvalidPathException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.PathMatcher;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.attribute.UserPrincipalLookupService;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

import static org.kie.soup.commons.validation.Preconditions.checkCondition;
import static org.kie.soup.commons.validation.Preconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.Preconditions.checkNotNull;

public abstract class BaseSimpleFileSystem implements FileSystem,
                                                      FileSystemId {

    public static final char UNIX_SEPARATOR = '/';
    public static final char WINDOWS_SEPARATOR = '\\';
    public static final String UNIX_SEPARATOR_STRING = "/";
    public static final String WINDOWS_SEPARATOR_STRING = "\\";

    private final FileSystemProvider provider;
    private final String defaultDirectory;
    private final Set<String> supportedFileAttributeViews;
    private final File[] roots;
    private final String name;

    BaseSimpleFileSystem(final FileSystemProvider provider,
                         final String path) {
        this(File.listRoots(),
             provider,
             path);
    }

    BaseSimpleFileSystem(final File[] roots,
                         final FileSystemProvider provider,
                         final String path) {
        checkNotNull("roots",
                     roots);
        checkCondition("should have at least one root",
                       roots.length > 0);
        this.name = path != null ? path : id();
        this.roots = roots;
        this.provider = provider;
        this.defaultDirectory = validateDefaultDir(path);
        this.supportedFileAttributeViews = Collections.unmodifiableSet(new HashSet<String>() {{
            add("basic");
        }});
    }

    @Override
    public String id() {
        return "/";
    }

    private String validateDefaultDir(final String path) throws IllegalArgumentException {
        checkNotEmpty("path",
                      path);
        if (!GeneralPathImpl.create(this,
                                    path,
                                    false).isAbsolute()) {
            throw new IllegalArgumentException("Path needs to be absolute, got: " + path);
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
        return System.getProperty("file.separator",
                                  UNIX_SEPARATOR_STRING);
    }

    public String getSeparator(final String path) {
        int unixIndex = path.indexOf(UNIX_SEPARATOR);
        int windowsIndex = path.indexOf(WINDOWS_SEPARATOR);
        if (unixIndex >= 0) {
            if (windowsIndex >= 0) {
                // path contains a mix of '/' and '\' so pick the first one
                return (unixIndex < windowsIndex) ? UNIX_SEPARATOR_STRING : WINDOWS_SEPARATOR_STRING;
            } else {
                return UNIX_SEPARATOR_STRING;
            }
        } else {
            if (windowsIndex >= 0) {
                return WINDOWS_SEPARATOR_STRING;
            }
        }
        return getSeparator();
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        return supportedFileAttributeViews;
    }

    @Override
    public Path getPath(String first,
                        String... more) throws InvalidPathException {
        if (more == null || more.length == 0) {
            return GeneralPathImpl.create(this,
                                          removeTrailingSlash(first),
                                          false);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(removeTrailingSlash(first));
        String separator = getSeparator(first);
        for (final String segment : more) {
            if (segment.length() > 0) {
                if (sb.length() > 0 && sb.lastIndexOf(separator) != sb.length()-1) {
                    sb.append(separator);
                }
                sb.append(segment);
            }
        }
        return GeneralPathImpl.create(this,
                                      sb.toString(),
                                      false);
    }

    private String removeTrailingSlash(final String path) {
        if (path.equals("/")) {
            return path;
        }
        for (final File root : roots) {
            if (root.toString().equals(path)) {
                return path;
            }
        }
        if (path.endsWith(UNIX_SEPARATOR_STRING)) {
            return path.substring(0, path.length() - UNIX_SEPARATOR_STRING.length());
        }
        if (path.endsWith(WINDOWS_SEPARATOR_STRING)) {
            return path.substring(0, path.length() - WINDOWS_SEPARATOR_STRING.length());
        }
        return path;
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

    @Override
    public String toString() {
        return "file://" + id();
    }

    @Override
    public String getName() {
        return name;
    }
}
