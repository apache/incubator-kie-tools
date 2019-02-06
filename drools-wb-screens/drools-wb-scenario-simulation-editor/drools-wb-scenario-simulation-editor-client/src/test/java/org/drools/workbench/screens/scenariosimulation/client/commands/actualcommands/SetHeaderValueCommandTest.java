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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SetHeaderValueCommandTest extends AbstractScenarioSimulationCommandTest {

    @Before
    public void setup() {
        super.setup();
        command = spy(new SetHeaderValueCommand());
        scenarioSimulationContextLocal.getStatus().setRowIndex(ROW_INDEX);
        scenarioSimulationContextLocal.getStatus().setColumnIndex(COLUMN_INDEX);
        scenarioSimulationContextLocal.getStatus().setCellValue(VALUE);
        assertTrue(command.isUndoable());
    }

    @Test
    public void executeNotValid() {
        commonExecute(false, false);
    }

    @Test
    public void executeValid() {
        commonExecute(true, true);
    }

    private void commonExecute(boolean validateHeaderUpdate, boolean executed) {
        when(scenarioGridModelMock.validateHeaderUpdate(eq(VALUE), eq(ROW_INDEX), eq(COLUMN_INDEX), anyBoolean())).thenReturn(validateHeaderUpdate);
        command.execute(scenarioSimulationContextLocal);
        verify(dataManagementStrategyMock, times(1)).isADataType(anyString());
        if (executed) {
            verify(scenarioGridModelMock, times(1)).updateHeader(eq(COLUMN_INDEX), eq(ROW_INDEX), eq(VALUE));
        } else {
            verify(scenarioGridModelMock, never()).updateHeader(eq(COLUMN_INDEX), eq(ROW_INDEX), eq(VALUE));
        }
    }
}