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

package org.uberfire.experimental.client.service.auth;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.enterprise.event.Event;

import org.assertj.core.api.Assertions;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.events.PerspectiveVisibiltiyChangeEvent;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesRegistryService;
import org.uberfire.experimental.client.test.TestExperimentalActivityReference;
import org.uberfire.experimental.client.test.model.TestExperimentalScreen1Activity;
import org.uberfire.experimental.client.test.model.TestExperimentalScreen2Activity;
import org.uberfire.experimental.client.test.model.TestNonExperimentalScreenActivity;
import org.uberfire.experimental.service.events.NonPortableExperimentalFeatureModifiedEvent;
import org.uberfire.experimental.service.events.PortableExperimentalFeatureModifiedEvent;
import org.uberfire.experimental.service.registry.ExperimentalFeature;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeatureImpl;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.ConditionalPlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExperimentalActivitiesAuthorizationManagerImplTest {

    private static final String ACTIVITY_1_ID_TYPENAME = "org.uberfire.experimental.client.test.model.TestExperimentalScreen1Activity";
    private static final String ACTIVITY_1_ID = "TestExperimentalScreen1";
    private static final String FEATURE_1_ID = "org.uberfire.experimental.client.test.model.TestExperimentalScreen1";

    private static final String ACTIVITY_2_ID_TYPENAME = "org.uberfire.experimental.client.test.model.TestExperimentalScreen2Activity";
    private static final String ACTIVITY_2_ID = "TestExperimentalScreen2";
    private static final String FEATURE_2_ID = "org.uberfire.experimental.client.test.model.TestExperimentalScreen2";

    private static final String NON_EXPERIMENTAL_ACTIVITY_ID = "TestNonExperimentalScreen";

    @Mock
    private PlaceManager placeManager;

    @Mock
    private SyncBeanManager syncBeanManager;

    @Mock
    private ClientExperimentalFeaturesRegistryService registryService;

    @Mock
    private Event<PerspectiveVisibiltiyChangeEvent> perspectiveVisibleEvent;

    private ExperimentalActivitiesAuthorizationManagerImpl authorizationManager;

    @Before
    public void init() {
        when(syncBeanManager.lookupBeans(ExperimentalActivityReference.class)).thenAnswer((Answer<Collection<SyncBeanDef<ExperimentalActivityReference>>>) invocationOnMock -> getReferences());

        authorizationManager = spy(new ExperimentalActivitiesAuthorizationManagerImpl(syncBeanManager, registryService, perspectiveVisibleEvent, () -> UUID.randomUUID().toString()));

        authorizationManager.init();

        when(registryService.isFeatureEnabled(FEATURE_1_ID)).thenReturn(true);
        when(registryService.isFeatureEnabled(FEATURE_2_ID)).thenReturn(false);
    }

    @Test
    public void testAuthorizeActivities() {
        assertTrue(authorizationManager.authorizeActivity(new TestExperimentalScreen1Activity(placeManager)));

        verify(registryService).isFeatureEnabled(FEATURE_1_ID);

        assertFalse(authorizationManager.authorizeActivity(new TestExperimentalScreen2Activity(placeManager)));

        verify(registryService).isFeatureEnabled(FEATURE_2_ID);

        assertTrue(authorizationManager.authorizeActivity(new TestNonExperimentalScreenActivity(placeManager)));

        verify(registryService, times(2)).isFeatureEnabled(anyString());
    }

    @Test
    public void testSecurePartWithPathPlaceRequest() {
        testSecure(ACTIVITY_1_ID, PathPlaceRequest.class, this::validateDoNothingSecure);
    }

    @Test
    public void testSecurePartWithExperimentalPlaceRequest() {
        testSecure(ACTIVITY_1_ID, DefaultPlaceRequest.class, this::validateSecure);
    }

    @Test
    public void testSecurePartWithOUTExperimentalPlaceRequest() {
        testSecure(NON_EXPERIMENTAL_ACTIVITY_ID, DefaultPlaceRequest.class, this::validateDoNothingSecure);
    }

    @Test
    public void testOnFeatureModifiedEvent() {
        testOnFeatureModified(feature -> authorizationManager.onFeatureModified(new NonPortableExperimentalFeatureModifiedEvent(feature)));
    }

    @Test
    public void testOnFeatureModifiedGlobalEvent() {
        testOnFeatureModified(feature -> authorizationManager.onFeatureModified(new PortableExperimentalFeatureModifiedEvent(feature)));
    }

    private void testOnFeatureModified(Consumer<ExperimentalFeature> authorizationEventConsumer) {

        authorizationEventConsumer.accept(new ExperimentalFeatureImpl(FEATURE_2_ID, true));

        verify(perspectiveVisibleEvent, never()).fire(any());

        authorizationEventConsumer.accept(new ExperimentalFeatureImpl(FEATURE_1_ID, true));

        ArgumentCaptor<PerspectiveVisibiltiyChangeEvent> captor = ArgumentCaptor.forClass(PerspectiveVisibiltiyChangeEvent.class);

        verify(perspectiveVisibleEvent).fire(captor.capture());

        Assertions.assertThat(captor.getValue())
                .isNotNull()
                .hasFieldOrPropertyWithValue("perspectiveId", ACTIVITY_1_ID)
                .hasFieldOrPropertyWithValue("visible", true);

        authorizationEventConsumer.accept(new ExperimentalFeatureImpl(FEATURE_1_ID, false));

        captor = ArgumentCaptor.forClass(PerspectiveVisibiltiyChangeEvent.class);

        verify(perspectiveVisibleEvent, times(2)).fire(captor.capture());

        Assertions.assertThat(captor.getValue())
                .isNotNull()
                .hasFieldOrPropertyWithValue("perspectiveId", ACTIVITY_1_ID)
                .hasFieldOrPropertyWithValue("visible", false);
    }

    private void testSecure(String activityId, Class<? extends PlaceRequest> requestType, BiConsumer<PanelDefinition, PartDefinition> validation) {
        PlaceRequest request = mock(requestType);

        when(request.getIdentifier()).thenReturn(activityId);

        PanelDefinition panel = mock(PanelDefinition.class);

        PartDefinition part = mock(PartDefinition.class);

        when(part.getPlace()).thenReturn(request);

        authorizationManager.securePart(part, panel);

        validation.accept(panel, part);
    }

    private void validateDoNothingSecure(PanelDefinition panel, PartDefinition part) {
        verify(panel, never()).removePart(part);
        verify(part, never()).setPlace(any());
    }

    private void validateSecure(PanelDefinition panel, PartDefinition part) {
        verify(panel).removePart(part);
        verify(part).setPlace(any(ConditionalPlaceRequest.class));
    }

    private Collection<SyncBeanDef<ExperimentalActivityReference>> getReferences() {
        return Arrays.asList(createReference(new TestExperimentalActivityReference(ACTIVITY_1_ID_TYPENAME, ACTIVITY_1_ID, FEATURE_1_ID, ActivityResourceType.PERSPECTIVE)),
                             createReference(new TestExperimentalActivityReference(ACTIVITY_2_ID_TYPENAME, ACTIVITY_2_ID, FEATURE_2_ID, ActivityResourceType.SCREEN)));
    }

    private SyncBeanDef<ExperimentalActivityReference> createReference(ExperimentalActivityReference activityReference) {
        SyncBeanDef<ExperimentalActivityReference> def = mock(SyncBeanDef.class);
        when(def.getInstance()).thenReturn(activityReference);
        when(def.newInstance()).thenReturn(activityReference);
        return def;
    }
}
