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

package org.uberfire.experimental.service.storage.scoped.impl;

import java.io.IOException;
import java.net.URI;
import java.util.Comparator;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jboss.errai.marshalling.server.MappingContextSingleton;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.uberfire.experimental.service.definition.impl.ExperimentalFeatureDefRegistryImpl;
import org.uberfire.experimental.service.registry.ExperimentalFeature;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeatureImpl;
import org.uberfire.experimental.service.util.TestUtils;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.mocks.FileSystemTestingUtils;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.rpc.SessionInfo;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public abstract class AbstractExperimentalFeaturesStorageTest<STORAGE extends AbstractScopedExperimentalFeaturesStorage> {

    protected static final String USER_NAME = "my-user";

    protected static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    protected SessionInfo sessionInfo;

    protected IOService ioService;

    @Mock
    protected ExperimentalFeatureDefRegistryImpl defRegistry;

    protected FileSystem fileSystem;

    protected STORAGE storage;

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

        storage = getStorageInstance();
    }

    abstract STORAGE getStorageInstance();

    protected void verifyLoadedFeatures(List<ExperimentalFeatureImpl> features, ExperimentalFeature feature1, ExperimentalFeature feature2, ExperimentalFeature feature3) {

        features.sort(Comparator.comparing(ExperimentalFeatureImpl::getFeatureId));

        Assertions.assertThat(features)
                .isNotNull()
                .hasSize(3);

        Assertions.assertThat(features.get(0))
                .isNotNull()
                .hasFieldOrPropertyWithValue("featureId", feature1.getFeatureId())
                .hasFieldOrPropertyWithValue("enabled", feature1.isEnabled());

        Assertions.assertThat(features.get(1))
                .isNotNull()
                .hasFieldOrPropertyWithValue("featureId", feature2.getFeatureId())
                .hasFieldOrPropertyWithValue("enabled", feature2.isEnabled());

        Assertions.assertThat(features.get(2))
                .isNotNull()
                .hasFieldOrPropertyWithValue("featureId", feature3.getFeatureId())
                .hasFieldOrPropertyWithValue("enabled", feature3.isEnabled());
    }

    @After
    public void clean() {
        fileSystemTestingUtils.cleanup();
        try {
            ioService.delete(fileSystem.getPath(storage.getStoragePath()));
        } catch (Exception ex) {

        }
    }
}
