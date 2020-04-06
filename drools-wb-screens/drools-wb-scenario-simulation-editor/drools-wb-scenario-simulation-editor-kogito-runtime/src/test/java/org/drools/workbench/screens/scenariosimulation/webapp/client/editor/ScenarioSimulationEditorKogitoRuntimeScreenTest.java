/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.webapp.client.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.kogito.client.editor.ScenarioSimulationEditorKogitoWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;

import static org.drools.workbench.screens.scenariosimulation.webapp.client.editor.ScenarioSimulationEditorKogitoRuntimeScreen.SCENARIO_SIMULATION_KOGITO_RUNTIME_SCREEN_DEFAULT_REQUEST;
import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.spy;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationEditorKogitoRuntimeScreenTest {

    @Mock
    private PlaceManager placeManagerMock;
    @Mock
    private ScenarioSimulationEditorKogitoWrapper scenarioSimulationEditorKogitoWrapperMock;

    private ScenarioSimulationEditorKogitoRuntimeScreen scenarioSimulationEditorKogitoRuntimeScreenSpy;

    @Before
    public void setup() {
        scenarioSimulationEditorKogitoRuntimeScreenSpy = spy(new ScenarioSimulationEditorKogitoRuntimeScreen(placeManagerMock) {
            {
                this.scenarioSimulationEditorKogitoWrapper = scenarioSimulationEditorKogitoWrapperMock;
            }
        });
    }

    @Test
    public void getPlaceRequest() {
        assertEquals(SCENARIO_SIMULATION_KOGITO_RUNTIME_SCREEN_DEFAULT_REQUEST, scenarioSimulationEditorKogitoRuntimeScreenSpy.getPlaceRequest());
    }

}
