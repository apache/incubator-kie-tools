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
public class PrependColumnCommandTest extends AbstractCommandTest {



    private PrependColumnCommand prependColumnCommand;

    @Before
    public void setup() {
        super.setup();
        prependColumnCommand = new PrependColumnCommand(mockScenarioGridModel, COLUMN_ID, COLUMN_GROUP, mockScenarioGridPanel, mockScenarioGridLayer);
    }

    @Test
    public void execute() {
        prependColumnCommand.execute();
        verify(mockScenarioGridModel, times(1)).getFirstIndexLeftOfGroup(eq(COLUMN_GROUP));
        verify(mockScenarioGridModel, times(1)).insertColumn(eq(FIRST_INDEX_LEFT), isA(ScenarioGridColumn.class));
    }
}