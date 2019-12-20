/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.fs.k8s;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.LockableFileSystem;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.fs.file.SimpleUnixFileSystem;

import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.K8S_FS_NO_IMPL;

public class K8SFileSystem extends SimpleUnixFileSystem implements LockableFileSystem {

    private static final Logger logger = LoggerFactory.getLogger(K8SFileSystem.class);

    K8SFileSystem(final FileSystemProvider provider, final String path) {
        super(provider, path);
        fileStore = new K8SFileStore(null);
    }

    @Override
    public WatchService newWatchService() {
        return new K8SWatchService(this);
    }

    @Override
    public void lock() {
        logger.debug(K8S_FS_NO_IMPL);
    }

    @Override
    public void unlock() {
        logger.debug(K8S_FS_NO_IMPL);
    }
}
