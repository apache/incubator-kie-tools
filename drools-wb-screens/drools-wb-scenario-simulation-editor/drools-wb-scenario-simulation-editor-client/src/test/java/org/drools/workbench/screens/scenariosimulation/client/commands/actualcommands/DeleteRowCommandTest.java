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
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridRow;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ROW_INDEX;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DeleteRowCommandTest extends AbstractScenarioSimulationCommandTest {

    @Before
    public void setup() {
        super.setup();
        command = spy(new DeleteRowCommand());
        assertTrue(command.isUndoable());
    }

    @Test
    public void execute() {
        scenarioSimulationContextLocal.getStatus().setRowIndex(ROW_INDEX);
        when(rowsMock.isEmpty()).thenReturn(false);
        command.execute(scenarioSimulationContextLocal);
        verify(scenarioGridModelMock, times(1)).deleteRow(eq(ROW_INDEX));
        verify(scenarioGridModelMock, never()).insertRow(anyInt(), isA(ScenarioGridRow.class));
        reset(scenarioGridModelMock);
        when(rowsMock.isEmpty()).thenReturn(true);
        command.execute(scenarioSimulationContextLocal);
        verify(scenarioGridModelMock, times(1)).deleteRow(eq(ROW_INDEX));
        verify(scenarioGridModelMock, times(1)).insertRow(eq(0), isA(ScenarioGridRow.class));
    }
}