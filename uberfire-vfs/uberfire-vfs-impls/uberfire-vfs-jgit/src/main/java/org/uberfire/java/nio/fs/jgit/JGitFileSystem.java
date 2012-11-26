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

package org.uberfire.java.nio.fs.jgit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.lib.Ref;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileStore;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.InvalidPathException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.PathMatcher;
import org.uberfire.java.nio.file.PatternSyntaxException;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.attribute.UserPrincipalLookupService;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

import static org.eclipse.jgit.lib.Repository.*;
import static org.kie.commons.validation.PortablePreconditions.*;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.*;

public class JGitFileSystem implements FileSystem {

    private static final Set<String> SUPPORTED_ATTR_VIEWS = Collections.unmodifiableSet(new HashSet<String>() {{
        add("basic");
    }});

    private final FileSystemProvider provider;
    private final Git gitRepo;
    private final ListBranchCommand.ListMode listMode;
    private boolean isClose = false;
    private final FileStore fileStore;
    private final String name;

    JGitFileSystem(final FileSystemProvider provider, final Git git, final String name) {
        this(provider, git, name, null);
    }

    JGitFileSystem(final FileSystemProvider provider, final Git git, final String name, final ListBranchCommand.ListMode listMode) {
        this.provider = checkNotNull("provider", provider);
        this.gitRepo = checkNotNull("git", git);
        this.name = checkNotEmpty("name", name);
        this.listMode = listMode;
        this.fileStore = new JGitFileStore(gitRepo.getRepository());
    }

    public String getName() {
        return name;
    }

    public Git gitRepo() {
        return gitRepo;
    }

    @Override
    public FileSystemProvider provider() {
        return provider;
    }

    @Override
    public boolean isOpen() {
        return !isClose;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public String getSeparator() {
        return "/";
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        checkClose();
        return new Iterable<Path>() {
            @Override
            public Iterator<Path> iterator() {
                return new Iterator<Path>() {

                    Iterator<Ref> branches = null;

                    @Override
                    public boolean hasNext() {
                        if (branches == null) {
                            init();
                        }
                        return branches.hasNext();
                    }

                    private void init() {
                        branches = branchList(gitRepo, listMode).iterator();
                    }

                    @Override
                    public Path next() {
                        if (branches == null) {
                            init();
                        }
                        return JGitPathImpl.createRoot(JGitFileSystem.this, "/", shortenRefName(branches.next().getName()) + "@" + name, false);
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        checkClose();
        return new Iterable<FileStore>() {
            @Override
            public Iterator<FileStore> iterator() {
                return new Iterator<FileStore>() {

                    private int i = 0;

                    @Override
                    public boolean hasNext() {
                        return i < 1;
                    }

                    @Override public FileStore next() {
                        if (i < 1) {
                            i++;
                            return fileStore;
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
        };
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        checkClose();
        return SUPPORTED_ATTR_VIEWS;
    }

    @Override
    public Path getPath(final String first, final String... more)
            throws InvalidPathException {
        checkClose();
        checkNotEmpty("first", first);

        if (more == null || more.length == 0) {
            return JGitPathImpl.create(this, first, JGitPathImpl.DEFAULT_REF_TREE + "@" + name, false);
        }

        final StringBuilder sb = new StringBuilder();
        for (final String segment : more) {
            if (segment.length() > 0) {
                if (sb.length() > 0) {
                    sb.append(getSeparator());
                }
                sb.append(segment);
            }
        }
        return JGitPathImpl.create(this, sb.toString(), first + "@" + name, false);
    }

    @Override
    public PathMatcher getPathMatcher(final String syntaxAndPattern)
            throws IllegalArgumentException, PatternSyntaxException, UnsupportedOperationException {
        checkClose();
        checkNotEmpty("syntaxAndPattern", syntaxAndPattern);
        throw new UnsupportedOperationException();
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService()
            throws UnsupportedOperationException {
        checkClose();
        throw new UnsupportedOperationException();
    }

    @Override
    public WatchService newWatchService()
            throws UnsupportedOperationException, IOException {
        checkClose();
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
        checkClose();
        gitRepo.getRepository().close();
        isClose = true;
    }

    private void checkClose() throws IllegalStateException {
        if (isClose) {
            throw new IllegalStateException("FileSystem is close.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JGitFileSystem that = (JGitFileSystem) o;

        if (isClose != that.isClose) {
            return false;
        }
        if (fileStore != null ? !fileStore.equals(that.fileStore) : that.fileStore != null) {
            return false;
        }
        if (!gitRepo.equals(that.gitRepo)) {
            return false;
        }
        if (listMode != that.listMode) {
            return false;
        }
        if (!name.equals(that.name)) {
            return false;
        }
        if (!provider.equals(that.provider)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = provider.hashCode();
        result = 31 * result + gitRepo.hashCode();
        result = 31 * result + (listMode != null ? listMode.hashCode() : 0);
        result = 31 * result + (isClose ? 1 : 0);
        result = 31 * result + (fileStore != null ? fileStore.hashCode() : 0);
        result = 31 * result + name.hashCode();
        return result;
    }
}
