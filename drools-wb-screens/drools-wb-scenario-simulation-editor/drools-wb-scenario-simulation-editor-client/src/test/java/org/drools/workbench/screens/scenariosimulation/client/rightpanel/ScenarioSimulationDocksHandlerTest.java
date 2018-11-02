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
package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import java.util.Collection;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationDocksHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.workbench.client.docks.AuthoringWorkbenchDocks;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.workbench.docks.UberfireDock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationDocksHandlerTest {

    @Mock
    AuthoringWorkbenchDocks authoringWorkbenchDocks;

    @InjectMocks
    ScenarioSimulationDocksHandler scenarioSimulationDocksHandler;

    @Test
    public void correctAmountOfItems() {
        assertEquals(2, scenarioSimulationDocksHandler.provideDocks("identifier").size());
    }

    @Test
    public void expandToolsDock() {
        final Collection<UberfireDock> docks = scenarioSimulationDocksHandler.provideDocks("id");
        final UberfireDock toolsDock = (UberfireDock) docks.toArray()[0];

        scenarioSimulationDocksHandler.expandToolsDock();

        verify(authoringWorkbenchDocks).expandAuthoringDock(toolsDock);
    }

    @Test
    public void expandTestResultsDock() {
        final Collection<UberfireDock> docks = scenarioSimulationDocksHandler.provideDocks("id");
        final UberfireDock reportDock = (UberfireDock) docks.toArray()[1];

        scenarioSimulationDocksHandler.expandTestResultsDock();

        verify(authoringWorkbenchDocks).expandAuthoringDock(reportDock);
    }
}