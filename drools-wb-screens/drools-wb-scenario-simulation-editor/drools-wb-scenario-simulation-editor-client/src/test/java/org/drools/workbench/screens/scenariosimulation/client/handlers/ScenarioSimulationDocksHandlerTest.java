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

import java.util.Collection;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.workbench.client.docks.AuthoringWorkbenchDocks;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.workbench.docks.UberfireDock;

import static org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationDocksHandler.SCESIMEDITOR_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationDocksHandlerTest {

    @Mock
    private AuthoringWorkbenchDocks authoringWorkbenchDocks;
    @InjectMocks
    private ScenarioSimulationDocksHandler scenarioSimulationDocksHandler;


    private enum MANAGED_DOCKS {
        SETTINGS,
        TOOLS,
        CHEATSHEET,
        REPORT,
        COVERAGE;
    }

    @Test
    public void correctAmountOfItems() {
        assertEquals(MANAGED_DOCKS.values().length, scenarioSimulationDocksHandler.provideDocks("identifier").size());
    }

    @Test
    public void expandToolsDock() {
        final Collection<UberfireDock> docks = scenarioSimulationDocksHandler.provideDocks("id");
        final UberfireDock toolsDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.TOOLS.ordinal()];

        scenarioSimulationDocksHandler.expandToolsDock();

        verify(authoringWorkbenchDocks).expandAuthoringDock(toolsDock);
    }

    @Test
    public void expandTestResultsDock() {
        final Collection<UberfireDock> docks = scenarioSimulationDocksHandler.provideDocks("id");
        final UberfireDock reportDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.REPORT.ordinal()];

        scenarioSimulationDocksHandler.expandTestResultsDock();

        verify(authoringWorkbenchDocks).expandAuthoringDock(reportDock);
    }

    @Test
    public void setScesimPath() {
        final Collection<UberfireDock> docks = scenarioSimulationDocksHandler.provideDocks("id");
        final UberfireDock settingsDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.SETTINGS.ordinal()];
        final UberfireDock toolsDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.TOOLS.ordinal()];
        final UberfireDock cheatSheetDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.CHEATSHEET.ordinal()];
        String TEST_PATH = "TEST_PATH";
        scenarioSimulationDocksHandler.setScesimEditorId(TEST_PATH);
        assertTrue(settingsDock.getPlaceRequest().getParameters().containsKey(SCESIMEDITOR_ID));
        assertEquals(TEST_PATH, settingsDock.getPlaceRequest().getParameter(SCESIMEDITOR_ID, "null"));
        assertTrue(toolsDock.getPlaceRequest().getParameters().containsKey(SCESIMEDITOR_ID));
        assertEquals(TEST_PATH, toolsDock.getPlaceRequest().getParameter(SCESIMEDITOR_ID, "null"));
        assertTrue(cheatSheetDock.getPlaceRequest().getParameters().containsKey(SCESIMEDITOR_ID));
        assertEquals(TEST_PATH, cheatSheetDock.getPlaceRequest().getParameter(SCESIMEDITOR_ID, "null"));
    }
}