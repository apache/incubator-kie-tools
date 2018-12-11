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
package org.drools.workbench.screens.scenariosimulation.client.handlers;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.type.ScenarioSimulationResourceType;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioSimulationService;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.screens.assets.AssetQueryService;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.ActivityResourceType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class NewScenarioSimulationHandlerTest extends AbstractNewScenarioTest {

    @Mock
    private BusyIndicatorView busyIndicatorViewMock;
    @Mock
    private ScenarioSimulationService scenarioSimulationServiceMock;
    @Mock
    private ScenarioSimulationResourceType resourceTypeMock;
    @Mock
    private EventSourceMock notificationEventMock;
    @Mock
    private EventSourceMock newResourceSuccessEventMock;
    @Mock
    private PlaceManager placeManagerMock;
    @Mock
    private AuthorizationManager authorizationManagerMock;
    @Mock
    private SessionInfo sessionInfoMock;
    @Mock
    private  AssetQueryService assetQueryServiceMock;
    @Mock
    private User userMock;

    @Captor
    private ArgumentCaptor<ResourceRef> refArgumentCaptor;

    private NewScenarioSimulationHandler handler;

    @Before
    public void setUp() throws Exception {

        handler = new NewScenarioSimulationHandler(resourceTypeMock,
                                                   busyIndicatorViewMock,
                                                   notificationEventMock,
                                                   newResourceSuccessEventMock,
                                                   placeManagerMock,
                                                   new CallerMock<>(scenarioSimulationServiceMock),
                                                   authorizationManagerMock,
                                                   sessionInfoMock,
                                                   libraryPlacesMock,
                                                   assetQueryServiceMock);

        when(sessionInfoMock.getIdentity()).thenReturn(userMock);
    }

    @Test
    public void checkCanCreateWhenFeatureDisabled() {
        when(authorizationManagerMock.authorize(any(ResourceRef.class),
                                                eq(ResourceAction.READ),
                                                eq(userMock))).thenReturn(false);
        assertFalse(handler.canCreate());
        assertResourceRef();
    }

    @Test
    public void checkCanCreateWhenFeatureEnabled() {
        when(authorizationManagerMock.authorize(any(ResourceRef.class),
                                                eq(ResourceAction.READ),
                                                eq(userMock))).thenReturn(true);
        assertTrue(handler.canCreate());
        assertResourceRef();
    }

    @Test
    public void checkRightResourceType() throws Exception {
        handler.create(new Package(),
                       "newfile.scesim",
                       mock(NewResourcePresenter.class));

        verify(busyIndicatorViewMock).showBusyIndicator("Saving");
        verify(busyIndicatorViewMock).hideBusyIndicator();
        verify(notificationEventMock).fire(any(NotificationEvent.class));
        verify(newResourceSuccessEventMock).fire(any(NewResourcePresenter.class));
        verify(placeManagerMock).goTo(any(Path.class));
    }

    private void assertResourceRef() {
        verify(authorizationManagerMock).authorize(refArgumentCaptor.capture(),
                                                   eq(ResourceAction.READ),
                                                   eq(userMock));
        assertEquals(ScenarioSimulationEditorPresenter.IDENTIFIER,
                     refArgumentCaptor.getValue().getIdentifier());
        assertEquals(ActivityResourceType.EDITOR,
                     refArgumentCaptor.getValue().getResourceType());
    }


}