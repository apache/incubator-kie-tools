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
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class AppendColumnCommandTest extends AbstractCommandTest {



    private AppendColumnCommand appendColumnCommand;

    @Before
    public void setup() {
        super.setup();
        appendColumnCommand = new AppendColumnCommand(mockScenarioGridModel, COLUMN_ID, COLUMN_GROUP, mockScenarioGridPanel, mockScenarioGridLayer);
    }

    @Test
    public void execute() {
        appendColumnCommand.execute();
        verify(mockScenarioGridModel, times(1)).getFirstIndexRightOfGroup(eq(COLUMN_GROUP));
        verify(mockScenarioGridModel, times(1)).insertNewColumn(eq(FIRST_INDEX_RIGHT), isA(ScenarioGridColumn.class));
    }
}