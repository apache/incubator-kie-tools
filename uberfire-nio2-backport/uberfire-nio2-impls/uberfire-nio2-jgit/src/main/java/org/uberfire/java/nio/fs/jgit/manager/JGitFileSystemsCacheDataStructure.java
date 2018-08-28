/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;

public class JGitFileSystemsCacheDataStructure {

    public static Map<String, Supplier<JGitFileSystem>> create(JGitFileSystemProviderConfiguration config) {

        return Collections.synchronizedMap(new LinkedHashMap<String, Supplier<JGitFileSystem>>(config.getJgitFileSystemsInstancesCache() + 1,
                                                                                               0.75f,
                                                                                               true) {

            //prevent infinite loop if all FS instances are in use
            private Integer removeEldestEntryIterations = 0;

            @Override
            public Supplier<JGitFileSystem> putIfAbsent(String key, Supplier<JGitFileSystem> value) {
                Supplier<JGitFileSystem> jGitFileSystemSupplier = super.putIfAbsent(key, value);
                if (size() > config.getJgitFileSystemsInstancesCache()) {
                    fitListToCacheSize();
                }
                return jGitFileSystemSupplier;
            }

            private void fitListToCacheSize() {
                List<String> itemsToRemove = new ArrayList<>();
                int maxIterations = config.getJgitCacheOverflowCleanupSize();
                Object[] entries =  this.entrySet().toArray();
                for (int i = this.size() - 1; (i >= 0 && (this.size() - i < maxIterations)); i--) {
                    Map.Entry<String, Supplier<JGitFileSystem>> entry = (Map.Entry) entries[i];
                    JGitFileSystem targetFS = (JGitFileSystem) ((MemoizedFileSystemsSupplier) entry.getValue()).get();
                    if (!targetFS.hasBeenInUse()) {
                        itemsToRemove.add(entry.getKey());
                    }
                }
                itemsToRemove.forEach(item -> this.remove(item));
            }

            @Override
            public boolean removeEldestEntry(Map.Entry eldest) {
                if (removeEldestEntryIterations > config.getJgitRemoveEldestEntryIterations()) {
                    removeEldestEntryIterations = 0;
                    return false;
                }
                if (size() > config.getJgitFileSystemsInstancesCache()) {
                    JGitFileSystem targetFS = (JGitFileSystem) ((MemoizedFileSystemsSupplier) eldest.getValue()).get();
                    if (targetFS.hasBeenInUse()) {
                        removeEldestEntryIterations++;
                        this.remove(eldest.getKey());
                        this.put((String) eldest.getKey(), (MemoizedFileSystemsSupplier) eldest.getValue());
                        return false;
                    } else {
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
