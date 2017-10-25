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

package org.guvnor.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

public class TestTempFileSystem {

    @Inject
    @Named("ioStrategy")
    IOService ioService;

    @Inject
    Paths paths;

    private final TempFiles tempFiles;
    private final HashMap<Path, File> pathToFile = new HashMap<>();

    public TestTempFileSystem() throws Exception {

        tempFiles = new TempFiles();
    }

    public Path createTempFile(final String fullFileName) throws IOException {
        final File file = tempFiles.createTempFile(fullFileName);
        final Path path = paths.convert(ioService.get(file.toURI()));

        pathToFile.put(path,
                       file);

        return path;
    }

    public Path createTempDirectory(final String fullDirectoryName) throws IOException {
        final File file = tempFiles.createTempDirectory(fullDirectoryName);
        final Path path = paths.convert(ioService.get(file.toURI()));

        pathToFile.put(path,
                       file);

        return path;
    }

    public void deleteFile(final Path path) {
        getFile(path).delete();
    }

    public File getFile(final Path path) {
        return pathToFile.get(path);
    }

    public void tearDown() {
        tempFiles.deleteFiles();
    }
}
