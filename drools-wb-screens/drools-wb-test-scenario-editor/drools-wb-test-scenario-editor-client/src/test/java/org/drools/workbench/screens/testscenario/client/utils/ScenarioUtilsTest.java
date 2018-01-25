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

package org.drools.workbench.screens.testscenario.client.utils;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioUtilsTest {

    @Test
    public void testFindExecutionTrace() throws Exception {
        final Scenario scenario = new Scenario();
        final ExecutionTrace executionTrace = new ExecutionTrace();
        scenario.getFixtures().add(executionTrace);

        Assert.assertEquals(executionTrace, ScenarioUtils.findExecutionTrace(scenario));
    }

    @Test
    public void testFindExecutionTraceDefault() throws Exception {
        final Scenario scenario = new Scenario();

        Assert.assertNotNull(ScenarioUtils.findExecutionTrace(scenario));
    }

    @Test
    public void testAddBottomAndRightPaddingToTableCells() throws Exception {
        final Element cellOne = mock(Element.class);
        final Element cellTwo = mock(Element.class);
        final HTMLTable.CellFormatter cellFormatter = mock(HTMLTable.CellFormatter.class);
        final FlexTable testedTable = mock(FlexTable.class);

        doReturn(1).when(testedTable).getRowCount();
        doReturn(2).when(testedTable).getCellCount(0);
        doReturn(cellFormatter).when(testedTable).getCellFormatter();
        doReturn(cellOne).when(cellFormatter).getElement(0, 0);
        doReturn(cellTwo).when(cellFormatter).getElement(0, 1);

        ScenarioUtils.addBottomAndRightPaddingToTableCells(testedTable);

        verify(cellOne).setAttribute("style", ScenarioUtils.BOTTOM_RIGHT_PADDING);
        verify(cellTwo).setAttribute("style", ScenarioUtils.BOTTOM_RIGHT_PADDING);
    }
}
