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
public class NewScenarioSimulationHandlerTest {

    @Mock
    private BusyIndicatorView mockBusyIndicatorView;

    @Mock
    private ScenarioSimulationService mockScenarioSimulationService;

    @Mock
    private ScenarioSimulationResourceType mockResourceType;

    @Mock
    private EventSourceMock mockNotificationEvent;

    @Mock
    private EventSourceMock mockNewResourceSuccessEvent;

    @Mock
    private PlaceManager mockPlaceManager;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private User user;

    @Captor
    private ArgumentCaptor<ResourceRef> refArgumentCaptor;

    private NewScenarioSimulationHandler handler;

    @Before
    public void setUp() throws Exception {

        handler = new NewScenarioSimulationHandler(mockResourceType,
                                                   mockBusyIndicatorView,
                                                   mockNotificationEvent,
                                                   mockNewResourceSuccessEvent,
                                                   mockPlaceManager,
                                                   new CallerMock<>(mockScenarioSimulationService),
                                                   authorizationManager,
                                                   sessionInfo);

        when(sessionInfo.getIdentity()).thenReturn(user);
    }

    @Test
    public void checkCanCreateWhenFeatureDisabled() {
        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(ResourceAction.READ),
                                            eq(user))).thenReturn(false);
        assertFalse(handler.canCreate());
        assertResourceRef();
    }

    @Test
    public void checkCanCreateWhenFeatureEnabled() {
        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(ResourceAction.READ),
                                            eq(user))).thenReturn(true);
        assertTrue(handler.canCreate());
        assertResourceRef();
    }

    @Test
    public void checkRightResourceType() throws Exception {
        handler.create(new Package(),
                       "newfile.scesim",
                       mock(NewResourcePresenter.class));

        verify(mockBusyIndicatorView).showBusyIndicator("Saving");
        verify(mockBusyIndicatorView).hideBusyIndicator();

        verify(mockNotificationEvent).fire(any(NotificationEvent.class));
        verify(mockNewResourceSuccessEvent).fire(any(NewResourcePresenter.class));
        verify(mockPlaceManager).goTo(any(Path.class));
    }

    private void assertResourceRef() {
        verify(authorizationManager).authorize(refArgumentCaptor.capture(),
                                               eq(ResourceAction.READ),
                                               eq(user));
        assertEquals(ScenarioSimulationEditorPresenter.IDENTIFIER,
                     refArgumentCaptor.getValue().getIdentifier());
        assertEquals(ActivityResourceType.EDITOR,
                     refArgumentCaptor.getValue().getResourceType());
    }
}