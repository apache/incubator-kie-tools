/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.experimental.service.storage.migration.impl.migrations;

import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.backend.server.util.Paths;
import org.uberfire.experimental.service.storage.migration.StorageMigration;
import org.uberfire.experimental.service.storage.scoped.impl.UserExperimentalFeaturesStorageImpl;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

@Dependent
public class V2StorageMigration implements StorageMigration {

    private IOService ioService;

    @Inject
    public V2StorageMigration(@Named("configIO") final IOService ioService) {
        this.ioService = ioService;
    }

    @Override
    public int getTargetVersion() {
        return 2;
    }

    @Override
    public void migrate(final FileSystem fileSystem) {
        Path usersRoot = fileSystem.getPath(UserExperimentalFeaturesStorageImpl.USER_FOLDER_ROOT);

        try (DirectoryStream<Path> stream = ioService.newDirectoryStream(usersRoot)) {
            stream.forEach(folder -> {
                String folderName = Paths.convert(folder).getFileName();

                Path newFolder = usersRoot.resolve(UserExperimentalFeaturesStorageImpl.encode(folderName));

                ioService.move(folder, newFolder);
            });
        }
    }
}
