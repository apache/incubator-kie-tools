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

package org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FACT_NAME;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class EnableTestToolsCommandTest extends AbstractScenarioSimulationCommandTest {

    @Before
    public void setup() {
        super.setup();
        command = spy(new EnableTestToolsCommand());
        assertFalse(command.isUndoable());
    }

    @Test
    public void executeWithFactName() {
        scenarioSimulationContextLocal.setTestToolsPresenter(testToolsPresenterMock);
        scenarioSimulationContextLocal.getStatus().setFilterTerm(FACT_NAME);
        scenarioSimulationContextLocal.getStatus().setPropertyNameElements(null);
        scenarioSimulationContextLocal.getStatus().setNotEqualsSearch(true);
        command.execute(scenarioSimulationContextLocal);
        verify(scenarioSimulationEditorPresenterMock, times(1)).expandToolsDock();
        verify(scenarioSimulationEditorPresenterMock, times(1)).reloadTestTools(eq(false));
        verify(testToolsPresenterMock, times(1)).onEnableEditorTab(eq(FACT_NAME), eq(null), eq(true));
        verify(testToolsPresenterMock, never()).onEnableEditorTab();
    }

    @Test
    public void executeWithoutFactName() {
        scenarioSimulationContextLocal.setTestToolsPresenter(testToolsPresenterMock);
        scenarioSimulationContextLocal.getStatus().setFilterTerm(null);
        scenarioSimulationContextLocal.getStatus().setPropertyNameElements(null);
        scenarioSimulationContextLocal.getStatus().setNotEqualsSearch(true);
        command.execute(scenarioSimulationContextLocal);
        verify(scenarioSimulationEditorPresenterMock, times(1)).expandToolsDock();
        verify(scenarioSimulationEditorPresenterMock, times(1)).reloadTestTools(eq(false));
        verify(testToolsPresenterMock, times(1)).onEnableEditorTab();
        verify(testToolsPresenterMock, never()).onEnableEditorTab(anyString(), any(), anyBoolean());
    }
}