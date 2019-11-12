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

package org.uberfire.experimental.service.storage.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Instance;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.jboss.errai.marshalling.server.MappingContextSingleton;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.experimental.service.definition.impl.ExperimentalFeatureDefRegistryImpl;
import org.uberfire.experimental.service.events.PortableExperimentalFeatureModifiedEvent;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeatureImpl;
import org.uberfire.experimental.service.storage.ExperimentalFeaturesStorage;
import org.uberfire.experimental.service.storage.migration.StorageMigrationService;
import org.uberfire.experimental.service.storage.scoped.ScopedExperimentalFeaturesStorage;
import org.uberfire.experimental.service.storage.scoped.impl.GlobalExperimentalFeaturesStorageImpl;
import org.uberfire.experimental.service.storage.scoped.impl.UserExperimentalFeaturesStorageImpl;
import org.uberfire.experimental.service.storage.util.ExperimentalConstants;
import org.uberfire.experimental.service.util.TestUtils;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mocks.FileSystemTestingUtils;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.SpacesAPI;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.experimental.service.util.TestUtils.FEATURE_1;
import static org.uberfire.experimental.service.util.TestUtils.FEATURE_2;
import static org.uberfire.experimental.service.util.TestUtils.FEATURE_3;
import static org.uberfire.experimental.service.util.TestUtils.GLOBAL_FEATURE_1;
import static org.uberfire.experimental.service.util.TestUtils.GLOBAL_FEATURE_2;
import static org.uberfire.experimental.service.util.TestUtils.GLOBAL_FEATURE_3;

@RunWith(MockitoJUnitRunner.class)
public class ExperimentalFeaturesStorageImplTest {

    private static final String USER_NAME = "my-user";

    private static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    private SessionInfo sessionInfo;

    @Mock
    protected SpacesAPI spaces;

    @Mock
    protected IOService ioService;

    @Mock
    protected ExperimentalFeatureDefRegistryImpl defRegistry;

    private FileSystem fileSystem;

    @Mock
    private StorageMigrationService storageMigrationService;

    @Mock
    private EventSourceMock<PortableExperimentalFeatureModifiedEvent> event;

    private ExperimentalFeaturesStorageImpl storage;

    private GlobalExperimentalFeaturesStorageImpl globalStorage;

    private UserExperimentalFeaturesStorageImpl userStorage;

    @Before
    public void init() throws IOException {
        MappingContextSingleton.get();
        fileSystemTestingUtils.setup();

        sessionInfo = new SessionInfoMock(USER_NAME);
        fileSystem = fileSystemTestingUtils.getFileSystem();
        ioService = spy(fileSystemTestingUtils.getIoService());

        doNothing().when(ioService).startBatch(any(FileSystem.class));
        doNothing().when(ioService).endBatch();
        doReturn(fileSystem).when(ioService).newFileSystem(any(URI.class), anyMap());

        defRegistry = TestUtils.getRegistry();

        globalStorage = spy(new GlobalExperimentalFeaturesStorageImpl(sessionInfo, ioService, defRegistry, event));

        userStorage = spy(new UserExperimentalFeaturesStorageImpl(sessionInfo, ioService, defRegistry));
    }

    private void initSkipMigration() {
        // Skipping migration
        ioService.write(fileSystem.getPath(ExperimentalConstants.EXPERIMENTAL_SETTINGS_PATH), "version=2");

        initStorage();

        verify(storageMigrationService, never()).migrate(any(), any());
    }

    private void initStorage() {
        Instance<ScopedExperimentalFeaturesStorage> instance = mock(Instance.class);

        List<ScopedExperimentalFeaturesStorage> storages = new ArrayList<>();

        storages.add(userStorage);
        storages.add(globalStorage);

        when(instance.iterator()).thenReturn(storages.iterator());

        storage = new ExperimentalFeaturesStorageImpl(spaces, ioService, defRegistry, instance, storageMigrationService);

        storage.init();

        verifyInit();
    }

    @Test
    public void testStoreUserLevelFeature() {
        initSkipMigration();

        testStoreFeature(FEATURE_1, false, userStorage, globalStorage, 1);
        testStoreFeature(FEATURE_2, true, userStorage, globalStorage, 2);
        testStoreFeature(FEATURE_3, true, userStorage, globalStorage, 3);
    }

    @Test
    public void testStoreGlobalFeature() {
        initSkipMigration();

        testStoreFeature(GLOBAL_FEATURE_1, false, globalStorage, userStorage, 1);
        testStoreFeature(GLOBAL_FEATURE_2, true, globalStorage, userStorage, 2);
        testStoreFeature(GLOBAL_FEATURE_3, true, globalStorage, userStorage, 3);
    }

    @Test
    public void testMigrationWithoutSettingsFile() {
        initStorage();

        verify(storageMigrationService).migrate(ExperimentalConstants.EXPERIMENTAL_VERSION, fileSystem);
    }

    @Test
    public void testMigrationWithSettingsFileOldVersion() {
        ioService.write(fileSystem.getPath(ExperimentalConstants.EXPERIMENTAL_SETTINGS_PATH), "version=1");

        initStorage();

        verify(storageMigrationService).migrate(ExperimentalConstants.EXPERIMENTAL_VERSION, fileSystem);
    }

    private void testStoreFeature(String featureId, boolean newValue, ExperimentalFeaturesStorage storage, ExperimentalFeaturesStorage otherStorage, int times) {

        this.storage.store(new ExperimentalFeatureImpl(featureId, newValue));

        ArgumentCaptor<ExperimentalFeatureImpl> captor = ArgumentCaptor.forClass(ExperimentalFeatureImpl.class);

        verify(storage, times(times)).store(captor.capture());
        verify(otherStorage, never()).store(any());

        ExperimentalFeatureImpl feature = captor.getValue();

        Assertions.assertThat(feature)
                .isNotNull()
                .hasFieldOrPropertyWithValue("featureId", featureId)
                .hasFieldOrPropertyWithValue("enabled", newValue);
    }

    @After
    public void clean() {
        ioService.delete(fileSystem.getPath(globalStorage.getStoragePath()));
        ioService.delete(fileSystem.getPath(userStorage.getStoragePath()));
        fileSystemTestingUtils.cleanup();
    }

    private void verifyInit() {
        verify(spaces).resolveFileSystemURI(any(), any(), any());
        verify(ioService).newFileSystem(any(), any());

        Assertions.assertThat(storage.getFeatures())
                .isNotNull()
                .hasSize(6);
    }
}