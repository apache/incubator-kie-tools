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

package org.uberfire.experimental.client.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.experimental.service.backend.BackendExperimentalFeaturesRegistryService;
import org.uberfire.experimental.service.backend.impl.ExperimentalFeaturesSessionImpl;
import org.uberfire.experimental.service.events.NonPortableExperimentalFeatureModifiedEvent;
import org.uberfire.experimental.service.events.PortableExperimentalFeatureModifiedEvent;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeatureImpl;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeaturesRegistryImpl;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.experimental.client.test.TestExperimentalFeatureDefRegistry.FEATURE_1;
import static org.uberfire.experimental.client.test.TestExperimentalFeatureDefRegistry.FEATURE_2;
import static org.uberfire.experimental.client.test.TestExperimentalFeatureDefRegistry.FEATURE_3;

@RunWith(MockitoJUnitRunner.class)
public class ClientExperimentalFeaturesRegistryServiceImplTest {

    private static final String WRONG_FEATURE_ID = "this feature shouldn't exist";

    @Mock
    private BackendExperimentalFeaturesRegistryService backendService;

    @Mock
    private Event<NonPortableExperimentalFeatureModifiedEvent> event;

    private CallerMock<BackendExperimentalFeaturesRegistryService> callerMock;

    private ExperimentalFeaturesRegistryImpl registry;

    private ClientExperimentalFeaturesRegistryServiceImpl service;

    @Before
    public void init() {
        callerMock = new CallerMock<>(backendService);

        service = new ClientExperimentalFeaturesRegistryServiceImpl(callerMock, event);
    }

    @Test
    public void tesBasicTestExperimentalEnabled() {

        doBasicTest(true);
    }

    @Test
    public void tesBasicTestExperimentalDisabled() {

        doBasicTest(false);
    }

    @Test
    public void testNotifyFeatureUpdate() {

        initService(true);

        ExperimentalFeatureImpl feature = new ExperimentalFeatureImpl(FEATURE_1, false);

        service.updateExperimentalFeature(feature.getFeatureId(), feature.isEnabled());

        checkNotifyFeatureUpdate(feature);
    }

    @Test
    public void testNotifyFeatureUpdateViaEvent() {

        initService(true);

        ExperimentalFeatureImpl feature = new ExperimentalFeatureImpl(FEATURE_1, false);

        service.onGlobalFeatureModified(new PortableExperimentalFeatureModifiedEvent(feature));

        checkNotifyFeatureUpdate(feature);
    }

    private void checkNotifyFeatureUpdate(ExperimentalFeatureImpl feature) {

        ArgumentCaptor<NonPortableExperimentalFeatureModifiedEvent> eventCaptor = ArgumentCaptor.forClass(NonPortableExperimentalFeatureModifiedEvent.class);

        verify(event).fire(eventCaptor.capture());

        assertEquals(feature, eventCaptor.getValue().getFeature());
    }

    @Test
    public void testWrongNotifyFeatureUpdate() {

        initService(true);

        ExperimentalFeatureImpl feature = new ExperimentalFeatureImpl(FEATURE_1, true);

        service.updateExperimentalFeature(feature.getFeatureId(), feature.isEnabled());

        verify(event, never()).fire(any());

        feature = new ExperimentalFeatureImpl(WRONG_FEATURE_ID, true);

        service.updateExperimentalFeature(feature.getFeatureId(), feature.isEnabled());

        verify(event, never()).fire(any());
    }

    @Test
    public void testWrongNotifyFeatureUpdateViaEvent() {

        initService(true);

        ExperimentalFeatureImpl feature = new ExperimentalFeatureImpl(FEATURE_1, true);

        service.onGlobalFeatureModified(new PortableExperimentalFeatureModifiedEvent(feature));

        verify(event, never()).fire(any());

        feature = new ExperimentalFeatureImpl(WRONG_FEATURE_ID, true);

        service.onGlobalFeatureModified(new PortableExperimentalFeatureModifiedEvent(feature));

        verify(event, never()).fire(any());
    }

    @Test
    public void testNotifyFeatureUpdateWhenExperimentalDisabled() {

        initService(false);

        ExperimentalFeatureImpl feature = new ExperimentalFeatureImpl(FEATURE_1, false);

        service.updateExperimentalFeature(feature.getFeatureId(), feature.isEnabled());

        verify(event, never()).fire(any());
    }

    @Test
    public void testNotifyFeatureUpdateWhenExperimentalDisabledViaEvent() {

        initService(false);

        ExperimentalFeatureImpl feature = new ExperimentalFeatureImpl(FEATURE_1, false);

        service.onGlobalFeatureModified(new PortableExperimentalFeatureModifiedEvent(feature));

        verify(event, never()).fire(any());
    }

    private void doBasicTest(boolean experimentalEnabled) {

        initService(experimentalEnabled);

        assertEquals(experimentalEnabled, service.isExperimentalEnabled());

        /*
         FEATURE_1 & FEATURE_2 are enabled by default BUT when experimental is disabled
         the experimental service makes them disabled
        */
        assertEquals(experimentalEnabled, service.isFeatureEnabled(FEATURE_1));
        assertEquals(experimentalEnabled, service.isFeatureEnabled(FEATURE_2));

        assertFalse(service.isFeatureEnabled(FEATURE_3));

        // WRONG_FEATURE_ID isn't an experimental feature, so when asking the framework it should be always enabled.
        assertTrue(service.isFeatureEnabled(WRONG_FEATURE_ID));

        assertEquals(registry, service.getFeaturesRegistry());
    }

    private void initService(boolean experimentalEnabled) {

        List<ExperimentalFeatureImpl> features = new ArrayList<>();

        features.add(new ExperimentalFeatureImpl(FEATURE_1, true));
        features.add(new ExperimentalFeatureImpl(FEATURE_2, true));
        features.add(new ExperimentalFeatureImpl(FEATURE_3, false));

        registry = spy(new ExperimentalFeaturesRegistryImpl(features));

        when(backendService.getExperimentalFeaturesSession()).thenReturn(new ExperimentalFeaturesSessionImpl(experimentalEnabled, registry));

        service.loadRegistry();
    }
}
