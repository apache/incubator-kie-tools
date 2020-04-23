/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.ReceiveCommand;
import org.eclipse.jgit.transport.UploadPack;
import org.jboss.errai.security.shared.api.identity.User;
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

public class JGitFileSystemProxy implements JGitFileSystem {

    private String fsName;
    private Supplier<JGitFileSystem> cachedSupplier;

    public JGitFileSystemProxy(String fsName,
                               Supplier<JGitFileSystem> cachedSupplier) {
        this.fsName = fsName;

        this.cachedSupplier = cachedSupplier;
    }

    @Override
    public void setPublicURI(Map<String, String> fullHostNames) {
        cachedSupplier.get().setPublicURI(fullHostNames);
    }

    @Override
    public String getName() {
        return fsName;
    }

    @Override
    public Git getGit() {
        return cachedSupplier.get().getGit();
    }

    @Override
    public CredentialsProvider getCredential() {
        return cachedSupplier.get().getCredential();
    }

    @Override
    public void checkClosed() throws IllegalStateException {
        cachedSupplier.get().checkClosed();
    }

    @Override
    public void publishEvents(Path watchable,
                              List<WatchEvent<?>> elist) {
        cachedSupplier.get().publishEvents(watchable,
                                           elist);
    }

    @Override
    public boolean isOnBatch() {
        return cachedSupplier.get().isOnBatch();
    }

    @Override
    public void setState(String state) {
        cachedSupplier.get().setState(state);
    }

    @Override
    public CommitInfo buildCommitInfo(String defaultMessage,
                                      CommentedOption op) {
        return cachedSupplier.get().buildCommitInfo(defaultMessage,
                                                    op);
    }

    @Override
    public void setBatchCommitInfo(String defaultMessage,
                                   CommentedOption op) {
        cachedSupplier.get().setBatchCommitInfo(defaultMessage,
                                                op);
    }

    @Override
    public void setHadCommitOnBatchState(Path path,
                                         boolean hadCommitOnBatchState) {
        cachedSupplier.get().setHadCommitOnBatchState(path,
                                                      hadCommitOnBatchState);
    }

    @Override
    public void setHadCommitOnBatchState(boolean value) {
        cachedSupplier.get().setHadCommitOnBatchState(value);
    }

    @Override
    public boolean isHadCommitOnBatchState(Path path) {
        return cachedSupplier.get().isHadCommitOnBatchState(path);
    }

    @Override
    public void setBatchCommitInfo(CommitInfo batchCommitInfo) {
        cachedSupplier.get().setBatchCommitInfo(batchCommitInfo);
    }

    @Override
    public CommitInfo getBatchCommitInfo() {
        return cachedSupplier.get().getBatchCommitInfo();
    }

    @Override
    public int incrementAndGetCommitCount() {
        return cachedSupplier.get().incrementAndGetCommitCount();
    }

    @Override
    public void resetCommitCount() {
        cachedSupplier.get().resetCommitCount();
    }

    @Override
    public int getNumberOfCommitsSinceLastGC() {
        return cachedSupplier.get().getNumberOfCommitsSinceLastGC();
    }

    @Override
    public void lock() {
        cachedSupplier.get().lock();
    }

    @Override
    public void unlock() {
        cachedSupplier.get().unlock();
    }

    @Override
    public void addPostponedWatchEvents(List<WatchEvent<?>> postponedWatchEvents) {
        cachedSupplier.get().addPostponedWatchEvents(postponedWatchEvents);
    }

    @Override
    public List<WatchEvent<?>> getPostponedWatchEvents() {
        return cachedSupplier.get().getPostponedWatchEvents();
    }

    @Override
    public void clearPostponedWatchEvents() {
        cachedSupplier.get().clearPostponedWatchEvents();
    }

    @Override
    public boolean hasPostponedEvents() {
        return cachedSupplier.get().hasPostponedEvents();
    }

    @Override
    public boolean hasBeenInUse() {
        return cachedSupplier.get().hasBeenInUse();
    }

    @Override
    public void notifyExternalUpdate() {
        cachedSupplier.get().notifyExternalUpdate();
    }

    @Override
    public void notifyPostCommit(int exitCode) {
        cachedSupplier.get().notifyPostCommit(exitCode);
    }

    @Override
    public void checkBranchAccess(final ReceiveCommand command,
                                  final User user) {
        cachedSupplier.get().checkBranchAccess(command,
                                               user);
    }

    @Override
    public void filterBranchAccess(final UploadPack uploadPack,
                                   final User user) {
        cachedSupplier.get().filterBranchAccess(uploadPack,
                                                user);
    }

    @Override
    public FileSystemProvider provider() {
        return cachedSupplier.get().provider();
    }

    @Override
    public boolean isOpen() {
        return cachedSupplier.get().isOpen();
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
        return cachedSupplier.get().getRootDirectories();
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        return cachedSupplier.get().getFileStores();
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        return cachedSupplier.get().supportedFileAttributeViews();
    }

    @Override
    public Path getPath(String first,
                        String... more) throws InvalidPathException {
        return cachedSupplier.get().getPath(first,
                                            more);
    }

    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) throws IllegalArgumentException, PatternSyntaxException, UnsupportedOperationException {
        return cachedSupplier.get().getPathMatcher(syntaxAndPattern);
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() throws UnsupportedOperationException {
        return cachedSupplier.get().getUserPrincipalLookupService();
    }

    @Override
    public WatchService newWatchService() throws UnsupportedOperationException, IOException {
        return cachedSupplier.get().newWatchService();
    }

    @Override
    public void close() {
        cachedSupplier.get().close();
    }

    @Override
    public void dispose() {
        cachedSupplier.get().dispose();
    }

    @Override
    public String id() {
        return fsName;
    }

    @Override
    public FileSystemState getState() {
        return cachedSupplier.get().getState();
    }

    public JGitFileSystem getRealJGitFileSystem() {
        return cachedSupplier.get();
    }

    @Override
    public boolean equals(Object obj) {
        return cachedSupplier.get().equals(obj);
    }

    @Override
    public int hashCode() {
        return cachedSupplier.get().hashCode();
    }

    @Override
    public String toString() {
        return cachedSupplier.get().toString();
    }
}
