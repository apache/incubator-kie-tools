/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.editor.strategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.workbench.screens.scenariosimulation.client.editor.AbstractScenarioSimulationEditorTest;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public abstract class AbstractDataManagementStrategyTest extends AbstractScenarioSimulationEditorTest {

    protected AbstractDataManagementStrategy abstractDataManagementStrategySpy;

    public void setUp() throws Exception {
        super.setup();
    }

    @Test
    public void getSimpleClassFactModelTree() {
        final FactModelTree retrieved = AbstractDataManagementStrategy.getSimpleClassFactModelTree(String.class);
        assertNotNull(retrieved);
        assertEquals(String.class.getSimpleName(), retrieved.getFactName());
        assertEquals("java.lang", retrieved.getFullPackage());
        assertNotNull(retrieved.getSimpleProperties());
    }

    @Test
    public void getPropertiesToHideMap() {
        commonGetPropertiesToHideMap(true, false);
        commonGetPropertiesToHideMap(false, false);
        commonGetPropertiesToHideMap(false, true);
    }

    @Test
    public void getPropertiesToHideListNoPropertyAssigned() {
        commonGetPropertiesToHideList(false);
    }

    @Test
    public void getPropertiesToHideListPropertyAssigned() {
        commonGetPropertiesToHideList(true);
    }

    private void commonGetPropertiesToHideMap(boolean selectedColumnNull, boolean isInstanceAssigned) {
        if (selectedColumnNull) {
            doReturn(null).when(scenarioGridModelMock).getSelectedColumn();
        } else {
            doReturn(gridColumnMock).when(scenarioGridModelMock).getSelectedColumn();
            doReturn(isInstanceAssigned).when(gridColumnMock).isInstanceAssigned();
            if (isInstanceAssigned) {
                doReturn(new ArrayList<>()).when(abstractDataManagementStrategySpy).getPropertiesToHide(eq(gridColumnMock), eq(scenarioGridModelMock));
            }
        }
        final Map<String, List<List<String>>> retrieved = abstractDataManagementStrategySpy.getPropertiesToHide(scenarioGridModelMock);
        if (selectedColumnNull) {
            assertTrue(retrieved.isEmpty());
            verify(abstractDataManagementStrategySpy, never()).getPropertiesToHide(isA(ScenarioGridColumn.class), eq(scenarioGridModelMock));
        } else {
            if (isInstanceAssigned) {
                verify(abstractDataManagementStrategySpy, times(1)).getPropertiesToHide(eq(gridColumnMock), eq(scenarioGridModelMock));
            } else {
                verify(abstractDataManagementStrategySpy, never()).getPropertiesToHide(isA(ScenarioGridColumn.class), eq(scenarioGridModelMock));
            }
        }
        reset(abstractDataManagementStrategySpy);
    }

    private void commonGetPropertiesToHideList(boolean isPropertyAssigned) {
        doReturn(isPropertyAssigned).when(gridColumnMock).isPropertyAssigned();
        List<List<String>> retrieved = abstractDataManagementStrategySpy.getPropertiesToHide(gridColumnMock, scenarioGridModelMock);
        if (isPropertyAssigned) {
            assertTrue(retrieved.isEmpty());
            verify(scenarioGridModelMock, never()).getSimulation();
        } else {
            verify(scenarioGridModelMock, times(1)).getSimulation();
            verify(scenarioGridModelMock, times(1)).getInstanceScenarioGridColumns(eq(gridColumnMock));
        }
        reset(scenarioGridModelMock);
    }
}