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

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.jboss.errai.marshalling.server.MappingContextSingleton;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.experimental.service.storage.scoped.impl.UserExperimentalFeaturesStorageImpl;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.mocks.FileSystemTestingUtils;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

public class V2StorageMigrationTest {

    private static final List<String> usernames = Arrays.asList("pere", "eder", "tiago");

    protected static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    protected IOService ioService;
    protected FileSystem fileSystem;

    private V2StorageMigration migration;

    private String fileContent;

    @Before
    public void init() throws IOException {
        MappingContextSingleton.get();
        fileSystemTestingUtils.setup();

        ioService = spy(fileSystemTestingUtils.getIoService());
        fileSystem = fileSystemTestingUtils.getFileSystem();

        doNothing().when(ioService).startBatch(any(FileSystem.class));
        doNothing().when(ioService).endBatch();

        fileContent = IOUtils.toString(getClass().getResourceAsStream("/test/global/regularFeatures.txt"), Charset.defaultCharset());

        usernames.forEach(this::createExperimentalSettings);

        migration = new V2StorageMigration(ioService);
    }

    private void createExperimentalSettings(final String userName) {
        Path path = fileSystem.getPath(MessageFormat.format(UserExperimentalFeaturesStorageImpl.USER_FOLDER, userName));

        ioService.write(path, fileContent);

        Assert.assertTrue(ioService.exists(path));
    }

    @Test
    public void testMigration() {
        migration.migrate(fileSystem);

        usernames.forEach(this::validateUserSettings);
    }

    private void validateUserSettings(final String userName) {
        Path oldPath = fileSystem.getPath(MessageFormat.format(UserExperimentalFeaturesStorageImpl.USER_FOLDER, userName));

        Assert.assertFalse(ioService.exists(oldPath));

        String encodedName = UserExperimentalFeaturesStorageImpl.encode(userName);

        Path newPath = fileSystem.getPath(MessageFormat.format(UserExperimentalFeaturesStorageImpl.USER_FOLDER, encodedName));

        Assert.assertTrue(ioService.exists(newPath));
        Assertions.assertThat(ioService.readAllString(newPath))
                .isNotBlank()
                .isEqualTo(fileContent);
    }

    @After
    public void cleanup() {
        fileSystemTestingUtils.cleanup();
    }
}
