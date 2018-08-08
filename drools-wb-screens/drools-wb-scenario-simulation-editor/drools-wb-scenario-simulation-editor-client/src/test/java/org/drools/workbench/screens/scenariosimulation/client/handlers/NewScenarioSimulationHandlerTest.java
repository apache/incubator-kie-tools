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
import org.drools.workbench.screens.scenariosimulation.client.type.ScenarioSimulationResourceType;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioSimulationService;
import org.guvnor.common.services.project.model.Package;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

    private NewScenarioSimulationHandler handler;

    @Before
    public void setUp() throws Exception {

        handler = new NewScenarioSimulationHandler(mockResourceType,
                                                   mockBusyIndicatorView,
                                                   mockNotificationEvent,
                                                   mockNewResourceSuccessEvent,
                                                   mockPlaceManager,
                                                   new CallerMock<>(mockScenarioSimulationService));
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
}