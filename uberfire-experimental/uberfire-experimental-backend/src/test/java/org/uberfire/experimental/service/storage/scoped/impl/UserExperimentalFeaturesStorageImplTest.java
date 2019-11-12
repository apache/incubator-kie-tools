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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeatureImpl;
import org.uberfire.java.nio.file.Path;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.uberfire.experimental.service.util.TestUtils.FEATURE_1;
import static org.uberfire.experimental.service.util.TestUtils.FEATURE_2;
import static org.uberfire.experimental.service.util.TestUtils.FEATURE_3;
import static org.uberfire.experimental.service.util.TestUtils.GLOBAL_FEATURE_1;

@RunWith(MockitoJUnitRunner.class)
public class UserExperimentalFeaturesStorageImplTest extends AbstractExperimentalFeaturesStorageTest<UserExperimentalFeaturesStorageImpl> {

    @Test
    public void testFirstLoad() {
        storage.init(fileSystem);

        List<ExperimentalFeatureImpl> features = new ArrayList<>(storage.getFeatures());

        verify(ioService, times(2)).exists(any());
        verify(ioService, never()).newInputStream(any());
        verify(ioService).createDirectory(any());

        verify(ioService, times(2)).newOutputStream(any());
        verify(ioService).startBatch(fileSystem);
        verify(ioService).endBatch();

        verifyLoadedFeatures(features, new ExperimentalFeatureImpl(FEATURE_1, false), new ExperimentalFeatureImpl(FEATURE_2, false), new ExperimentalFeatureImpl(FEATURE_3, false));
    }

    @Test
    public void testRegularLoad() throws IOException {

        Path path = fileSystem.getPath(storage.getStoragePath());

        ioService.write(path, IOUtils.toString(getClass().getResourceAsStream("/test/user/regularFeatures.txt"), Charset.defaultCharset()));

        storage.init(fileSystem);

        List<ExperimentalFeatureImpl> features = new ArrayList<>(storage.getFeatures());

        verify(ioService, times(3)).exists(any());
        verify(ioService, never()).createDirectory(any());
        verify(ioService).newInputStream(any());

        verify(ioService, never()).newOutputStream(any());
        verify(ioService, never()).startBatch(fileSystem);
        verify(ioService, never()).endBatch();

        verifyLoadedFeatures(features, new ExperimentalFeatureImpl(FEATURE_1, true), new ExperimentalFeatureImpl(FEATURE_2, false), new ExperimentalFeatureImpl(FEATURE_3, true));
    }

    @Test
    public void testExtraFeaturesLoad() throws IOException {
        Path path = fileSystem.getPath(storage.getStoragePath());

        ioService.write(path, IOUtils.toString(getClass().getResourceAsStream("/test/user/extraFeatures.txt"), Charset.defaultCharset()));

        storage.init(fileSystem);

        List<ExperimentalFeatureImpl> features = new ArrayList<>(storage.getFeatures());

        verify(ioService, times(3)).exists(any());
        verify(ioService, never()).createDirectory(any());
        verify(ioService).newInputStream(any());

        verify(ioService).newOutputStream(any());
        verify(ioService).startBatch(fileSystem);
        verify(ioService).endBatch();

        verifyLoadedFeatures(features, new ExperimentalFeatureImpl(FEATURE_1, true), new ExperimentalFeatureImpl(FEATURE_2, false), new ExperimentalFeatureImpl(FEATURE_3, true));
    }

    @Test
    public void testMissingFeaturesLoad() throws IOException {
        Path path = fileSystem.getPath(storage.getStoragePath());

        ioService.write(path, IOUtils.toString(getClass().getResourceAsStream("/test/user/missingFeatures.txt"), Charset.defaultCharset()));

        storage.init(fileSystem);

        List<ExperimentalFeatureImpl> features = new ArrayList<>(storage.getFeatures());

        verify(ioService, times(3)).exists(any());
        verify(ioService, never()).createDirectory(any());
        verify(ioService).newInputStream(any());

        verify(ioService).newOutputStream(any());
        verify(ioService).startBatch(fileSystem);
        verify(ioService).endBatch();

        verifyLoadedFeatures(features, new ExperimentalFeatureImpl(FEATURE_1, true), new ExperimentalFeatureImpl(FEATURE_2, false), new ExperimentalFeatureImpl(FEATURE_3, true));
    }

    @Test
    public void testStoreFeature() throws IOException {
        testRegularLoad();

        storage.store(new ExperimentalFeatureImpl(GLOBAL_FEATURE_1, true));

        verify(ioService, never()).newOutputStream(any());
        verify(ioService, never()).startBatch(fileSystem);
        verify(ioService, never()).endBatch();

        storage.store(new ExperimentalFeatureImpl(FEATURE_1, false));

        verify(ioService).newOutputStream(any());
        verify(ioService).startBatch(fileSystem);
        verify(ioService).endBatch();

        verifyLoadedFeatures(new ArrayList<>(storage.getFeatures()), new ExperimentalFeatureImpl(FEATURE_1, false), new ExperimentalFeatureImpl(FEATURE_2, false), new ExperimentalFeatureImpl(FEATURE_3, true));
    }

    @Override
    protected UserExperimentalFeaturesStorageImpl getStorageInstance() {
        return new UserExperimentalFeaturesStorageImpl(sessionInfo, ioService, defRegistry);
    }
}
