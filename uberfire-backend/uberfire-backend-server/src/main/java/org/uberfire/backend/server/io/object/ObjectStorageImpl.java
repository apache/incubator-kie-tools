/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.backend.server.io.object;

import java.net.URI;
import java.util.HashMap;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.marshalling.server.MappingContextSingleton;
import org.jboss.errai.marshalling.server.ServerMarshalling;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Path;

public class ObjectStorageImpl implements ObjectStorage {

    private IOService ioService;

    private FileSystem fileSystem;

    @Inject
    public ObjectStorageImpl(@Named("configIO") final IOService ioService) {
        this.ioService = ioService;
    }

    @Override
    public void init(URI rootURI) {
        initializeMarshaller();
        initializeFileSystem(rootURI);
    }

    @Override
    public boolean exists(final String path) {
        Path fsPath = fileSystem.getPath(path);

        try {
            return ioService.exists(fsPath);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T read(final String path) {
        Path fsPath = fileSystem.getPath(path);
        try {
            if (ioService.exists(fsPath)) {
                String content = ioService.readAllString(fsPath);
                return (T) ServerMarshalling.fromJSON(content);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public <T> void write(final String path,
                          final T value) {
        this.write(path,
                   value,
                   true);
    }

    @Override
    public <T> void write(final String path,
                          final T value,
                          final boolean lock) {
        try {
            if (lock) {
                ioService.startBatch(fileSystem);
            }
            Path fsPath = fileSystem.getPath(path);
            String content = ServerMarshalling.toJSON(value);
            ioService.write(fsPath,
                            content);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (lock) {
                ioService.endBatch();
            }
        }
    }

    @Override
    public void delete(final String path) {
        ioService.deleteIfExists(fileSystem.getPath(path));
    }

    @Override
    public Path getPath(String first,
                        String... paths) {
        return this.fileSystem.getPath(first,
                                       paths);
    }

    @Override
    public void close() {
        this.fileSystem.close();
        this.fileSystem.dispose();
    }

    private void initializeMarshaller() {
        MappingContextSingleton.get();
    }

    private void initializeFileSystem(final URI rootURI) {
        try {
            fileSystem = ioService.newFileSystem(rootURI,
                                                 new HashMap<String, Object>() {{
                                                     put("init",
                                                         Boolean.TRUE);
                                                     put("internal",
                                                         Boolean.TRUE);
                                                 }});
        } catch (FileSystemAlreadyExistsException e) {
            fileSystem = ioService.getFileSystem(rootURI);
        }
    }
}
