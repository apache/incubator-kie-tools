/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.experimental.service;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

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
import org.uberfire.experimental.service.backend.ExperimentalFeaturesSession;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefinition;
import org.uberfire.experimental.service.definition.impl.ExperimentalFeatureDefRegistryImpl;
import org.uberfire.experimental.service.editor.EditableExperimentalFeature;
import org.uberfire.experimental.service.events.PortableExperimentalFeatureModifiedEvent;
import org.uberfire.experimental.service.registry.ExperimentalFeaturesRegistry;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeatureImpl;
import org.uberfire.experimental.service.storage.ExperimentalFeaturesStorage;
import org.uberfire.experimental.service.storage.impl.GlobalExperimentalFeaturesStorageImpl;
import org.uberfire.experimental.service.storage.impl.UserExperimentalFeaturesStorageImpl;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mocks.FileSystemTestingUtils;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.SpacesAPI;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BackendExperimentalFeaturesRegistryServiceImplTest {

    protected static final String GLOBAL_FEATURE_1 = "globalFeature_1";
    protected static final String GLOBAL_FEATURE_2 = "globalFeature_2";
    protected static final String GLOBAL_FEATURE_3 = "globalFeature_3";

    protected static final String FEATURE_1 = "feature_1";
    protected static final String FEATURE_2 = "feature_2";
    protected static final String FEATURE_3 = "feature_3";

    protected static final String UNEXISTING_FEATURE = "unexisting featureID";

    protected static final String USER_NAME = "my-user";

    protected static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    protected SessionInfo sessionInfo;

    @Mock
    protected SpacesAPI spaces;

    @Mock
    protected IOService ioService;

    @Mock
    protected ExperimentalFeatureDefRegistryImpl defRegistry;

    protected FileSystem fileSystem;

    @Mock
    private EventSourceMock<PortableExperimentalFeatureModifiedEvent> event;

    private GlobalExperimentalFeaturesStorageImpl globalStorage;

    private UserExperimentalFeaturesStorageImpl userStorage;

    private BackendExperimentalFeaturesRegistryServiceImpl service;

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

        defRegistry = new ExperimentalFeatureDefRegistryImpl();
        defRegistry.register(new ExperimentalFeatureDefinition(GLOBAL_FEATURE_1, true, "", GLOBAL_FEATURE_1, GLOBAL_FEATURE_1));
        defRegistry.register(new ExperimentalFeatureDefinition(GLOBAL_FEATURE_2, true, "", GLOBAL_FEATURE_2, GLOBAL_FEATURE_2));
        defRegistry.register(new ExperimentalFeatureDefinition(GLOBAL_FEATURE_3, true, "", GLOBAL_FEATURE_3, GLOBAL_FEATURE_3));
        defRegistry.register(new ExperimentalFeatureDefinition(FEATURE_1, false, "", FEATURE_1, FEATURE_1));
        defRegistry.register(new ExperimentalFeatureDefinition(FEATURE_2, false, "", FEATURE_2, FEATURE_2));
        defRegistry.register(new ExperimentalFeatureDefinition(FEATURE_3, false, "", FEATURE_3, FEATURE_3));

        globalStorage = spy(new GlobalExperimentalFeaturesStorageImpl(sessionInfo, spaces, ioService, defRegistry, event));

        userStorage = spy(new UserExperimentalFeaturesStorageImpl(sessionInfo, spaces, ioService, defRegistry));
    }

    @Test
    public void testLoadRegistryWithExistingData() {
        init(true, true);

        assertTrue(service.isFeatureEnabled(GLOBAL_FEATURE_1));
        assertFalse(service.isFeatureEnabled(GLOBAL_FEATURE_2));
        assertTrue(service.isFeatureEnabled(GLOBAL_FEATURE_3));

        assertTrue(service.isFeatureEnabled(FEATURE_1));
        assertFalse(service.isFeatureEnabled(FEATURE_2));
        assertTrue(service.isFeatureEnabled(FEATURE_3));

        assertTrue(service.isFeatureEnabled(UNEXISTING_FEATURE));
    }

    @Test
    public void testLoadRegistryWithExistingDataExperimentalDisabled() {
        init(false, true);

        assertFalse(service.isFeatureEnabled(GLOBAL_FEATURE_1));
        assertFalse(service.isFeatureEnabled(GLOBAL_FEATURE_2));
        assertFalse(service.isFeatureEnabled(GLOBAL_FEATURE_3));

        assertFalse(service.isFeatureEnabled(FEATURE_1));
        assertFalse(service.isFeatureEnabled(FEATURE_2));
        assertFalse(service.isFeatureEnabled(FEATURE_3));

        assertTrue(service.isFeatureEnabled(UNEXISTING_FEATURE));
    }

    @Test
    public void testLoadRegistryWithoutExistingData() {
        init(true, false);

        assertFalse(service.isFeatureEnabled(GLOBAL_FEATURE_1));
        assertFalse(service.isFeatureEnabled(GLOBAL_FEATURE_2));
        assertFalse(service.isFeatureEnabled(GLOBAL_FEATURE_3));

        assertFalse(service.isFeatureEnabled(FEATURE_1));
        assertFalse(service.isFeatureEnabled(FEATURE_2));
        assertFalse(service.isFeatureEnabled(FEATURE_3));

        assertTrue(service.isFeatureEnabled(UNEXISTING_FEATURE));
    }

    @Test
    public void testLoadRegistryWithoutExistingDataExperimentalDisabled() {
        init(false, false);

        assertFalse(service.isFeatureEnabled(GLOBAL_FEATURE_1));
        assertFalse(service.isFeatureEnabled(GLOBAL_FEATURE_2));
        assertFalse(service.isFeatureEnabled(GLOBAL_FEATURE_3));

        assertFalse(service.isFeatureEnabled(FEATURE_1));
        assertFalse(service.isFeatureEnabled(FEATURE_2));
        assertFalse(service.isFeatureEnabled(FEATURE_3));

        assertTrue(service.isFeatureEnabled(UNEXISTING_FEATURE));
    }

    @Test
    public void testStoreUserLevelFeature() {
        testStoreFeature(FEATURE_1, false, userStorage, globalStorage);
    }

    @Test
    public void testStoreGlobalFeature() {
        testStoreFeature(GLOBAL_FEATURE_1, false, globalStorage, userStorage);
    }

    @Test
    public void testStoreWrongFeature() {
        init(true, true);

        Assertions.assertThatThrownBy(() -> service.save(new EditableExperimentalFeature(UNEXISTING_FEATURE, true)))
                .hasMessage("Cannot find ExperimentalFeature '" + UNEXISTING_FEATURE + "'")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testStoreFeatureExperimentalDisabled() {
        init(false, true);

        testStoreFeatureExperimentalFeatureDisabled(GLOBAL_FEATURE_1, globalStorage);
        testStoreFeatureExperimentalFeatureDisabled(FEATURE_1, userStorage);
        testStoreFeatureExperimentalFeatureDisabled(UNEXISTING_FEATURE, globalStorage);
        testStoreFeatureExperimentalFeatureDisabled(UNEXISTING_FEATURE, userStorage);
    }

    private void testStoreFeatureExperimentalFeatureDisabled(final String featureId, final ExperimentalFeaturesStorage storage) {
        Assertions.assertThatThrownBy(() -> service.save(new EditableExperimentalFeature(featureId, true)))
                .hasMessage("Impossible edit feature '" + featureId + "': Experimental Framework is disabled")
                .isInstanceOf(IllegalStateException.class);

        verify(storage, never()).store(any());
    }

    private void testStoreFeature(String featureId, boolean newValue, ExperimentalFeaturesStorage storage, ExperimentalFeaturesStorage otherStorage) {
        init(true, true);

        service.save(new EditableExperimentalFeature(featureId, newValue));

        ArgumentCaptor<ExperimentalFeatureImpl> captor = ArgumentCaptor.forClass(ExperimentalFeatureImpl.class);

        verify(storage).store(captor.capture());
        verify(otherStorage, never()).store(any());

        ExperimentalFeatureImpl feature = captor.getValue();

        Assertions.assertThat(feature)
                .isNotNull()
                .hasFieldOrPropertyWithValue("featureId", featureId)
                .hasFieldOrPropertyWithValue("enabled", newValue);
    }

    private void init(Boolean enableExperimental, boolean loadData) {
        if (loadData) {
            try {
                Path path = fileSystem.getPath(globalStorage.getStoragePath());
                ioService.write(path, IOUtils.toString(getClass().getResourceAsStream("/test/global/regularFeatures.txt"), Charset.defaultCharset()));

                path = fileSystem.getPath(userStorage.getStoragePath());
                ioService.write(path, IOUtils.toString(getClass().getResourceAsStream("/test/user/regularFeatures.txt"), Charset.defaultCharset()));
            } catch (Exception ex) {
                ex.printStackTrace();
                fail();
            }
        }

        globalStorage.init();
        userStorage.init();

        System.setProperty(BackendExperimentalFeaturesRegistryServiceImpl.EXPERIMENTAL_FEATURES_PROPERTY_NAME, enableExperimental.toString());
        service = new BackendExperimentalFeaturesRegistryServiceImpl(defRegistry, globalStorage, userStorage);

        ExperimentalFeaturesSession session = service.getExperimentalFeaturesSession();

        verify(globalStorage).getFeatures();
        verify(userStorage).getFeatures();

        Assertions.assertThat(session)
                .isNotNull()
                .hasFieldOrPropertyWithValue("experimentalFeaturesEnabled", enableExperimental)
                .hasFieldOrProperty("registry");

        ExperimentalFeaturesRegistry registry = session.getFeaturesRegistry();

        Assertions.assertThat(registry.getAllFeatures())
                .isNotNull()
                .hasSize(6);
    }

    @After
    public void clean() {
        ioService.delete(fileSystem.getPath(globalStorage.getStoragePath()));
        ioService.delete(fileSystem.getPath(userStorage.getStoragePath()));
        fileSystemTestingUtils.cleanup();
    }
}
