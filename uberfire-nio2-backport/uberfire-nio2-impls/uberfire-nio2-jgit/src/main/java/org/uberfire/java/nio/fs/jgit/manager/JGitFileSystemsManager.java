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
package org.uberfire.java.nio.fs.jgit.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.uberfire.java.nio.file.extensions.FileSystemHooks;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemImpl;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemLock;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.ws.JGitFileSystemsEventsManager;

import static org.eclipse.jgit.lib.Constants.DOT_GIT_EXT;

public class JGitFileSystemsManager {

    private final Set<String> closedFileSystems = new HashSet<>();

    private final Set<String> fileSystemsRoot = new HashSet<>();

    private final JGitFileSystemProvider jGitFileSystemProvider;

    private final JGitFileSystemsCache fsCache;

    private final JGitFileSystemProviderConfiguration config;

    final Map<String, JGitFileSystemLock> fileSystemsLocks = new ConcurrentHashMap<>();

    public JGitFileSystemsManager(final JGitFileSystemProvider jGitFileSystemProvider,
                                  final JGitFileSystemProviderConfiguration config) {
        this.jGitFileSystemProvider = jGitFileSystemProvider;
        this.config = config;
        this.fsCache = new JGitFileSystemsCache(config);
    }

    public void newFileSystem(Supplier<Map<String, String>> fullHostNames,
                              Supplier<Git> git,
                              Supplier<String> fsName,
                              Supplier<CredentialsProvider> credential,
                              Supplier<JGitFileSystemsEventsManager> fsManager,
                              Supplier<Map<FileSystemHooks, ?>> fsHooks) {

        Supplier<JGitFileSystem> fsSupplier = createFileSystemSupplier(fullHostNames,
                                                                       git,
                                                                       fsName,
                                                                       credential,
                                                                       fsManager,
                                                                       fsHooks);

        fsCache.addSupplier(fsName.get(),
                            fsSupplier);
        fileSystemsRoot.addAll(parseFSRoots(fsName.get()));
    }

    public void updateFSCacheEntry(String fsKey, JGitFileSystem jGitFileSystem) {
        if (getFsCache().containsKey(fsKey)) {
            fsCache.replaceSupplier(fsKey, () -> jGitFileSystem);
        }
    }

    List<String> parseFSRoots(String fsKey) {
        List<String> roots = new ArrayList<>();
        fsKey = cleanupFsName(fsKey);
        int index = fsKey.indexOf("/");
        while (index >= 0) {
            roots.add(fsKey.substring(0,
                                      index));
            index = fsKey.indexOf("/",
                                  index + 1);
        }
        roots.add(fsKey);
        return roots;
    }

    private String cleanupFsName(String fsKey) {
        if (fsKey.startsWith("/")) {
            fsKey = fsKey.substring(1);
        }
        if (fsKey.endsWith("/")) {
            fsKey = fsKey.substring(0,
                                    fsKey.length() - 1);
        }

        return fsKey;
    }

    private Supplier<JGitFileSystem> createFileSystemSupplier(Supplier<Map<String, String>> fullHostNames,
                                                              Supplier<Git> git,
                                                              Supplier<String> fsName,
                                                              Supplier<CredentialsProvider> credential,
                                                              Supplier<JGitFileSystemsEventsManager> fsManager,
                                                              Supplier<Map<FileSystemHooks, ?>> fsHooks) {

        return () -> newFileSystem(fullHostNames.get(),
                                   git.get(),
                                   fsName.get(),
                                   credential.get(),
                                   fsManager.get(),
                                   fsHooks.get());
    }

    private JGitFileSystem newFileSystem(Map<String, String> fullHostNames,
                                         Git git,
                                         String fsName,
                                         CredentialsProvider credential,
                                         JGitFileSystemsEventsManager fsEventsManager,
                                         Map<FileSystemHooks, ?> fsHooks) {
        fileSystemsLocks.putIfAbsent(fsName, createLock(git));
        final JGitFileSystem fs = new JGitFileSystemImpl(jGitFileSystemProvider,
                                                         fullHostNames,
                                                         git,
                                                         fileSystemsLocks.get(fsName),
                                                         fsName,
                                                         credential,
                                                         fsEventsManager,
                                                         fsHooks);

        fs.getGit().gc();

        return fs;
    }

    JGitFileSystemLock createLock(Git git) {
        return new JGitFileSystemLock(git, config.getDefaultJgitCacheEvictThresholdTimeUnit(),
                                      config.getJgitCacheEvictThresholdDuration());
    }

    public void remove(String realFSKey) {
        fsCache.remove(realFSKey);
        fileSystemsRoot.remove(realFSKey);
        closedFileSystems.remove(realFSKey);
    }

    public JGitFileSystem get(String fsName) {
        return fsCache.get(fsName);
    }

    public void clear() {
        fsCache.clear();
        closedFileSystems.clear();
        fileSystemsRoot.clear();
    }

    public boolean containsKey(String fsName) {

        return fsCache.getFileSystems().contains(fsName) && !closedFileSystems.contains(fsName);
    }

    public boolean containsRoot(String fsName) {
        return fileSystemsRoot.contains(fsName);
    }

    public void addClosedFileSystems(JGitFileSystem fileSystem) {
        String realFSKey = fileSystem.getName();
        closedFileSystems.add(realFSKey);
        fileSystemsRoot.remove(fileSystem.getName());
    }

    public boolean allTheFSAreClosed() {
        return closedFileSystems.size() == fsCache.getFileSystems().size();
    }

    public JGitFileSystem get(Repository db) {
        String key = extractFSNameFromRepo(db);
        return fsCache.get(key);
    }

    public Set<JGitFileSystem> getOpenFileSystems() {
        return fsCache.getFileSystems().stream().filter(fsName -> !closedFileSystems.contains(fsName))
                .map(fsName -> get(fsName)).collect(Collectors.toSet());
    }

    public JGitFileSystemsCache getFsCache() {
        return fsCache;
    }

    private String extractFSNameFromRepo(Repository db) {
        final String fullRepoName = config.getGitReposParentDir().toPath().relativize(db.getDirectory().toPath()).toString();
        return fullRepoName.substring(0, fullRepoName.indexOf(DOT_GIT_EXT)).replace('\\', '/');
    }

    Set<String> getClosedFileSystems() {
        return closedFileSystems;
    }
}
