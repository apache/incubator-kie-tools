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
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.FileSystemState;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileStore;
import org.uberfire.java.nio.file.InvalidPathException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.PathMatcher;
import org.uberfire.java.nio.file.PatternSyntaxException;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.attribute.UserPrincipalLookupService;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.model.CommitInfo;
import org.uberfire.java.nio.fs.jgit.ws.JGitFileSystemsEventsManager;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import static org.eclipse.jgit.lib.Repository.shortenRefName;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class JGitFileSystemImpl implements JGitFileSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(JGitFileSystemImpl.class);
    private static final Set<String> SUPPORTED_ATTR_VIEWS = unmodifiableSet(new HashSet<>(asList("basic",
                                                                                                 "version")));
    private final JGitFileSystemProvider provider;
    private final Git git;
    private final String toStringContent;
    private boolean isClosed = false;
    private final FileStore fileStore;
    private final String name;
    private final CredentialsProvider credential;
    private final AtomicInteger numberOfCommitsSinceLastGC = new AtomicInteger(0);
    private FileSystemState state = FileSystemState.NORMAL;
    private CommitInfo batchCommitInfo = null;
    private Map<Path, Boolean> hadCommitOnBatchState = new ConcurrentHashMap<>();
    private Lock lock;
    private JGitFileSystemsEventsManager fsEventsManager;

    private List<WatchEvent<?>> postponedWatchEvents = Collections.synchronizedList(new ArrayList<>());

    public JGitFileSystemImpl(final JGitFileSystemProvider provider,
                              final Map<String, String> fullHostNames,
                              final Git git,
                              final String name,
                              final CredentialsProvider credential,
                              JGitFileSystemsEventsManager fsEventsManager) {
        this.fsEventsManager = fsEventsManager;
        this.provider = checkNotNull("provider",
                                     provider);
        this.git = checkNotNull("git",
                                git);
        this.name = checkNotEmpty("name",
                                  name);

        this.lock = new Lock(git.getRepository().getDirectory().toURI());
        this.credential = checkNotNull("credential",
                                       credential);
        this.fileStore = new JGitFileStore(this.git.getRepository());
        if (fullHostNames != null && !fullHostNames.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            final Iterator<Map.Entry<String, String>> iterator = fullHostNames.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<String, String> entry = iterator.next();
                sb.append(entry.getKey()).append("://").append(entry.getValue()).append("/").append(name);
                if (iterator.hasNext()) {
                    sb.append("\n");
                }
            }
            toStringContent = sb.toString();
        } else {
            toStringContent = "git://" + name;
        }
    }

    @Override
    public String id() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Git getGit() {
        return git;
    }

    @Override
    public CredentialsProvider getCredential() {
        return credential;
    }

    @Override
    public FileSystemProvider provider() {
        return provider;
    }

    @Override
    public boolean isOpen() {
        return !isClosed;
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
        checkClosed();
        return () -> new Iterator<Path>() {

            Iterator<Ref> branches = null;

            @Override
            public boolean hasNext() {
                if (branches == null) {
                    init();
                }
                return branches.hasNext();
            }

            private void init() {
                branches = git.listRefs().iterator();
            }

            @Override
            public Path next() {

                if (branches == null) {
                    init();
                }
                try {
                    return JGitPathImpl.createRoot(JGitFileSystemImpl.this,
                                                   "/",
                                                   shortenRefName(branches.next().getName()) + "@" + name,
                                                   false);
                } catch (NoSuchElementException e) {
                    throw new IllegalStateException(
                            "The gitnio directory is in an invalid state. " +
                                    "If you are an IntelliJ IDEA user, " +
                                    "there is a known bug which requires specifying " +
                                    "a custom directory for your git repository. " +
                                    "You can specify a custom directory using '-Dorg.uberfire.nio.git.dir=/tmp/dir'. " +
                                    "For more details please see https://issues.jboss.org/browse/UF-275.",
                            e);
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        checkClosed();
        return () -> new Iterator<FileStore>() {

            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < 1;
            }

            @Override
            public FileStore next() {
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

    @Override
    public Set<String> supportedFileAttributeViews() {
        checkClosed();
        return SUPPORTED_ATTR_VIEWS;
    }

    @Override
    public Path getPath(final String first,
                        final String... more)
            throws InvalidPathException {
        checkClosed();
        if (first == null || first.trim().isEmpty()) {
            return new JGitFSPath(this);
        }

        if (more == null || more.length == 0) {
            return JGitPathImpl.create(this,
                                       first,
                                       JGitPathImpl.DEFAULT_REF_TREE + "@" + name,
                                       false);
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
        return JGitPathImpl.create(this,
                                   sb.toString(),
                                   first + "@" + name,
                                   false);
    }

    @Override
    public PathMatcher getPathMatcher(final String syntaxAndPattern)
            throws IllegalArgumentException, PatternSyntaxException, UnsupportedOperationException {
        checkClosed();
        checkNotEmpty("syntaxAndPattern",
                      syntaxAndPattern);
        throw new UnsupportedOperationException();
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService()
            throws UnsupportedOperationException {
        checkClosed();
        throw new UnsupportedOperationException();
    }

    @Override
    public WatchService newWatchService()
            throws UnsupportedOperationException, IOException {
        checkClosed();
        return fsEventsManager.newWatchService(name);
    }

    @Override
    public void close() throws IOException {
        if (isClosed) {
            return;
        }
        git.getRepository().close();
        isClosed = true;
        try {
            fsEventsManager.close(name);
        } catch (final Exception ex) {
            LOGGER.error("Error during close of WatchServices [" + toString() + "]",
                         ex);
        } finally {
            provider.onCloseFileSystem(this);
        }
    }

    @Override
    public void checkClosed() throws IllegalStateException {
        if (isClosed) {
            throw new IllegalStateException("FileSystem is closed.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            if (o != null && o instanceof JGitFileSystemProxy) {
                o = ((JGitFileSystemProxy) o).getRealJGitFileSystem();
            } else {
                return false;
            }
        }

        JGitFileSystemImpl that = (JGitFileSystemImpl) o;

        if (!name.equals(that.name)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return toStringContent;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        return result;
    }

    @Override
    public void publishEvents(final Path watchableRoot,
                              final List<WatchEvent<?>> elist) {
        fsEventsManager.publishEvents(name,
                                      watchableRoot,
                                      elist);
    }

    @Override
    public void dispose() {
        if (!isClosed) {
            close();
        }
        provider.onDisposeFileSystem(this);
    }

    @Override
    public boolean isOnBatch() {
        return state.equals(FileSystemState.BATCH);
    }

    @Override
    public void setState(String state) {
        try {
            this.state = FileSystemState.valueOf(state);
        } catch (final Exception ex) {
            this.state = FileSystemState.NORMAL;
        }
    }

    @Override
    public CommitInfo buildCommitInfo(final String defaultMessage,
                                      final CommentedOption op) {
        String sessionId = null;
        String name = null;
        String email = null;
        String message = defaultMessage;
        TimeZone timeZone = null;
        Date when = null;

        if (op != null) {
            sessionId = op.getSessionId();
            name = op.getName();
            email = op.getEmail();
            if (op.getMessage() != null && !op.getMessage().trim().isEmpty()) {
                message = op.getMessage();
            }
            timeZone = op.getTimeZone();
            when = op.getWhen();
        }

        return new CommitInfo(sessionId,
                              name,
                              email,
                              message,
                              timeZone,
                              when);
    }

    @Override
    public void setBatchCommitInfo(final String defaultMessage,
                                   final CommentedOption op) {
        this.batchCommitInfo = buildCommitInfo(defaultMessage,
                                               op);
    }

    @Override
    public void setHadCommitOnBatchState(final Path path,
                                         final boolean hadCommitOnBatchState) {
        final Path root = checkNotNull("path",
                                       path).getRoot();
        this.hadCommitOnBatchState.put(root.getRoot(),
                                       hadCommitOnBatchState);
    }

    @Override
    public void setHadCommitOnBatchState(final boolean value) {
        for (Map.Entry<Path, Boolean> entry : hadCommitOnBatchState.entrySet()) {
            entry.setValue(value);
        }
    }

    @Override
    public boolean isHadCommitOnBatchState(final Path path) {
        final Path root = checkNotNull("path",
                                       path).getRoot();
        return hadCommitOnBatchState.containsKey(root) ? hadCommitOnBatchState.get(root) : false;
    }

    @Override
    public void setBatchCommitInfo(CommitInfo batchCommitInfo) {
        this.batchCommitInfo = batchCommitInfo;
    }

    @Override
    public CommitInfo getBatchCommitInfo() {
        return batchCommitInfo;
    }

    @Override
    public int incrementAndGetCommitCount() {
        return numberOfCommitsSinceLastGC.incrementAndGet();
    }

    @Override
    public void resetCommitCount() {
        numberOfCommitsSinceLastGC.set(0);
    }

    @Override
    public int getNumberOfCommitsSinceLastGC() {
        return numberOfCommitsSinceLastGC.get();
    }

    @Override
    public FileSystemState getState() {
        return state;
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    //testing purposes
    public boolean isLocked() {
        return lock.isLocked();
    }

    public static class Lock {

        private ReentrantLock lock = new ReentrantLock(true);
        private FileLock physicalLock;
        private java.nio.file.Path lockFile;
        private FileChannel fileChannel;

        public Lock(URI repoURI) {
            this.lockFile = createLockInfra(repoURI);
        }

        public void lock() {
            lock.lock();

            if (needToCreatePhysicalLock()) {
                physicalLockOnFS();
            }
        }

        private boolean needToCreatePhysicalLock() {
            return ((physicalLock == null || !physicalLock.isValid()) && lock.getHoldCount() == 1);
        }

        public boolean isLocked() {
            return lock.isLocked();
        }

        public void unlock() {
            if (lock.isLocked()) {
                if (releasePhysicalLock()) {
                    physicalUnLockOnFS();
                }
                lock.unlock();
            }
        }

        private boolean releasePhysicalLock() {
            return physicalLock != null && physicalLock.isValid() && lock.isLocked() && lock.getHoldCount() == 1;
        }

        java.nio.file.Path createLockInfra(URI uri) {
            java.nio.file.Path lockFile = null;
            try {
                java.nio.file.Path repo = Paths.get(uri);
                lockFile = repo.resolve("db.lock");
                Files.createFile(lockFile);
            } catch (FileAlreadyExistsException ignored) {
            } catch (Exception e) {
                LOGGER.error("Error building lock infra [" + toString() + "]",
                             e);
            }
            return lockFile;
        }

        void physicalLockOnFS() {
            try {
                File file = lockFile.toFile();
                RandomAccessFile raf = new RandomAccessFile(file,
                                                            "rw");
                fileChannel = raf.getChannel();
                physicalLock = fileChannel.lock();
            } catch (FileNotFoundException e) {
                LOGGER.error("Error during lock of FS [" + toString() + "]",
                             e);
            } catch (java.io.IOException e) {
                LOGGER.error("Error during lock of FS [" + toString() + "]",
                             e);
            }
        }

        void physicalUnLockOnFS() {
            try {
                physicalLock.release();
                fileChannel.close();
                fileChannel = null;
                physicalLock = null;
            } catch (java.io.IOException e) {
                LOGGER.error("Error during unlock of FS [" + toString() + "]",
                             e);
            }
        }
    }

    @Override
    public void addPostponedWatchEvents(List<WatchEvent<?>> postponedWatchEvents) {
        this.postponedWatchEvents.addAll(postponedWatchEvents);
    }

    @Override
    public List<WatchEvent<?>> getPostponedWatchEvents() {
        return postponedWatchEvents;
    }

    @Override
    public void clearPostponedWatchEvents() {
        this.postponedWatchEvents = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public boolean hasPostponedEvents() {
        return !getPostponedWatchEvents().isEmpty();
    }
}
