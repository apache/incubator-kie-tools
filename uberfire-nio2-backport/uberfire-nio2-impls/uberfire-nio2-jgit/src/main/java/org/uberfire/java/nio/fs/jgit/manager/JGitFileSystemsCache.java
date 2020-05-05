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

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProxy;

public class JGitFileSystemsCache {

    //supplier for creation of new fs
    final Map<String, Supplier<JGitFileSystem>> fileSystemsSuppliers = new ConcurrentHashMap<>();

    //limited amount of real instances of FS
    final Map<String, Supplier<JGitFileSystem>> memoizedSuppliers;

    public JGitFileSystemsCache(JGitFileSystemProviderConfiguration config) {

        memoizedSuppliers = JGitFileSystemsCacheDataStructure.create(config);
    }

    public void addSupplier(String fsKey,
                            Supplier<JGitFileSystem> createFSSupplier) {
        PortablePreconditions.checkNotNull("fsKey",
                                           fsKey);
        PortablePreconditions.checkNotNull("fsSupplier",
                                           createFSSupplier);

        fileSystemsSuppliers.putIfAbsent(fsKey,
                                         createFSSupplier);
        createMemoizedSupplier(fsKey,
                               createFSSupplier);
    }

    public void replaceSupplier(String fsKey,
                                Supplier<JGitFileSystem> fsSupplier) {
        PortablePreconditions.checkNotNull("fsKey",
                                           fsKey);
        PortablePreconditions.checkNotNull("fsSupplier",
                                           fsSupplier);

        fileSystemsSuppliers.replace(fsKey,
                                     fsSupplier);
        memoizedSuppliers.replace(fsKey,
                                  fsSupplier);
    }

    public void remove(String fsName) {
        fileSystemsSuppliers.remove(fsName);
        memoizedSuppliers.remove(fsName);
    }

    public JGitFileSystem get(String fsName) {

        Supplier<JGitFileSystem> memoizedSupplier = memoizedSuppliers.get(fsName);
        if (memoizedSupplier != null) {
            return new JGitFileSystemProxy(fsName,
                                           memoizedSupplier);
        } else if (fileSystemsSuppliers.get(fsName) != null) {
            Supplier<JGitFileSystem> newMemoizedSupplier = createMemoizedSupplier(fsName,
                                                                                  fileSystemsSuppliers.get(fsName));
            return new JGitFileSystemProxy(fsName,
                                           newMemoizedSupplier);
        }
        return null;
    }

    private Supplier<JGitFileSystem> createMemoizedSupplier(String fsKey,
                                                            Supplier<JGitFileSystem> createFSSupplier) {
        Supplier<JGitFileSystem> memoizedFSSupplier = MemoizedFileSystemsSupplier.of(createFSSupplier);
        memoizedSuppliers.putIfAbsent(fsKey,
                                      memoizedFSSupplier);
        return memoizedFSSupplier;
    }

    public void clear() {
        memoizedSuppliers.clear();
        fileSystemsSuppliers.clear();
    }

    public boolean containsKey(String fsName) {
        return fileSystemsSuppliers.containsKey(fsName);
    }

    public Collection<String> getFileSystems() {
        return fileSystemsSuppliers.keySet();
    }

    public JGitFileSystemsCacheInfo getCacheInfo() {
        return new JGitFileSystemsCacheInfo();
    }

    public class JGitFileSystemsCacheInfo {

        public int fileSystemsCacheSize() {
            return memoizedSuppliers.size();
        }

        public Set<String> memoizedFileSystemsCacheKeys() {
            return memoizedSuppliers.keySet();
        }

        @Override
        public String toString() {
            return "JGitFileSystemsCacheInfo{fileSystemsCacheSize[" + fileSystemsCacheSize() + "], memoizedFileSystemsCacheKeys[" + memoizedFileSystemsCacheKeys() + "]}";
        }
    }
}
