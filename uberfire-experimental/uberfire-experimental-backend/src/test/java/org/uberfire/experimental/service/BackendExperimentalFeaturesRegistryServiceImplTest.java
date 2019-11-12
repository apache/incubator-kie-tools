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

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.experimental.service.backend.ExperimentalFeaturesSession;
import org.uberfire.experimental.service.definition.impl.ExperimentalFeatureDefRegistryImpl;
import org.uberfire.experimental.service.editor.EditableExperimentalFeature;
import org.uberfire.experimental.service.registry.ExperimentalFeaturesRegistry;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeatureImpl;
import org.uberfire.experimental.service.storage.impl.ExperimentalFeaturesStorageImpl;
import org.uberfire.experimental.service.util.TestUtils;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.experimental.service.util.TestUtils.FEATURE_1;
import static org.uberfire.experimental.service.util.TestUtils.FEATURE_2;
import static org.uberfire.experimental.service.util.TestUtils.FEATURE_3;
import static org.uberfire.experimental.service.util.TestUtils.GLOBAL_FEATURE_1;
import static org.uberfire.experimental.service.util.TestUtils.GLOBAL_FEATURE_2;
import static org.uberfire.experimental.service.util.TestUtils.GLOBAL_FEATURE_3;

@RunWith(MockitoJUnitRunner.class)
public class BackendExperimentalFeaturesRegistryServiceImplTest {

    protected static final String UNEXISTING_FEATURE = "unexisting featureID";

    protected static final String USER_NAME = "my-user";

    @Mock
    private ExperimentalFeaturesStorageImpl storage;

    private BackendExperimentalFeaturesRegistryServiceImpl service;
    protected SessionInfo sessionInfo;
    protected ExperimentalFeatureDefRegistryImpl defRegistry;

    @Before
    public void init() {
        sessionInfo = new SessionInfoMock(USER_NAME);

        defRegistry = TestUtils.getRegistry();
    }

    @Test
    public void testLoadRegistryWithExistingData() {
        init(true);

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
        init(false);

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
        init(false);

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
        testStoreFeature(FEATURE_1, false);
    }

    @Test
    public void testStoreGlobalFeature() {
        testStoreFeature(GLOBAL_FEATURE_1, false);
    }

    @Test
    public void testStoreWrongFeature() {
        init(true);

        Assertions.assertThatThrownBy(() -> service.save(new EditableExperimentalFeature(UNEXISTING_FEATURE, true)))
                .hasMessage("Cannot find ExperimentalFeature '" + UNEXISTING_FEATURE + "'")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testStoreFeatureExperimentalDisabled() {
        init(false);

        testStoreFeatureExperimentalFeatureDisabled(GLOBAL_FEATURE_1);
        testStoreFeatureExperimentalFeatureDisabled(FEATURE_1);
        testStoreFeatureExperimentalFeatureDisabled(UNEXISTING_FEATURE);
        testStoreFeatureExperimentalFeatureDisabled(UNEXISTING_FEATURE);
    }

    private void testStoreFeatureExperimentalFeatureDisabled(final String featureId) {
        Assertions.assertThatThrownBy(() -> service.save(new EditableExperimentalFeature(featureId, true)))
                .hasMessage("Impossible edit feature '" + featureId + "': Experimental Framework is disabled")
                .isInstanceOf(IllegalStateException.class);

        verify(storage, never()).store(any());
    }

    private void testStoreFeature(String featureId, boolean newValue) {
        init(true);

        service.save(new EditableExperimentalFeature(featureId, newValue));

        ArgumentCaptor<ExperimentalFeatureImpl> captor = ArgumentCaptor.forClass(ExperimentalFeatureImpl.class);

        verify(storage).store(captor.capture());

        ExperimentalFeatureImpl feature = captor.getValue();

        Assertions.assertThat(feature)
                .isNotNull()
                .hasFieldOrPropertyWithValue("featureId", featureId)
                .hasFieldOrPropertyWithValue("enabled", newValue);
    }

    private void init(Boolean enableExperimental) {

        System.setProperty(BackendExperimentalFeaturesRegistryServiceImpl.EXPERIMENTAL_FEATURES_PROPERTY_NAME, enableExperimental.toString());

        Collection<String> disableFeatures = Arrays.asList(GLOBAL_FEATURE_2, FEATURE_2);

        when(storage.getFeatures()).thenAnswer((Answer<Collection<ExperimentalFeatureImpl>>) invocationOnMock -> defRegistry.getAllFeatures().stream()
                .map(featureDefinition -> new ExperimentalFeatureImpl(featureDefinition.getId(), !disableFeatures.contains(featureDefinition.getId())))
                .collect(Collectors.toList()));

        service = new BackendExperimentalFeaturesRegistryServiceImpl(defRegistry, storage);

        ExperimentalFeaturesSession session = service.getExperimentalFeaturesSession();

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
    public void clear() {
        System.clearProperty(BackendExperimentalFeaturesRegistryServiceImpl.EXPERIMENTAL_FEATURES_PROPERTY_NAME);
    }
}
