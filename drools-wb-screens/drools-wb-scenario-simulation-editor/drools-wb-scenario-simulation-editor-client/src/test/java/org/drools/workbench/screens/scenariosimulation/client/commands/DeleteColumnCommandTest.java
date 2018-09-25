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

package org.drools.workbench.screens.scenariosimulation.client.commands;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DeleteColumnCommandTest extends AbstractCommandTest {



    private DeleteColumnCommand deleteColumnCommand;

    @Before
    public void setup() {
        super.setup();
        deleteColumnCommand = new DeleteColumnCommand(mockScenarioGridModel, COLUMN_INDEX,  COLUMN_GROUP, mockScenarioGridPanel, mockScenarioGridLayer);
    }

    @Test
    public void execute() {
        when(mockScenarioGridModel.getGroupSize(COLUMN_GROUP)).thenReturn(4L);
        deleteColumnCommand.execute();
        verify(mockScenarioGridModel, times(1)).deleteNewColumn(eq(COLUMN_INDEX));
        verify(mockScenarioGridModel,never()).insertNewColumn(anyInt(),anyObject());
        reset(mockScenarioGridModel);
        when(mockScenarioGridModel.getGroupSize(COLUMN_GROUP)).thenReturn(0L);
        deleteColumnCommand.execute();
        verify(mockScenarioGridModel, times(1)).deleteNewColumn(eq(COLUMN_INDEX));
        verify(mockScenarioGridModel,times(1)).insertNewColumn(eq(COLUMN_INDEX),isA(GridColumn.class));
    }
}