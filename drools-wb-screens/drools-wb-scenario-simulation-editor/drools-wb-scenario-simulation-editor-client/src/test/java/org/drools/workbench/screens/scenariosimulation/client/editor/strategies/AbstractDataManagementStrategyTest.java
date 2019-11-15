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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.client.TestProperties;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.editor.AbstractScenarioSimulationEditorTest;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.jgroups.util.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.backend.vfs.ObservablePath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractDataManagementStrategyTest extends AbstractScenarioSimulationEditorTest {

    protected AbstractDataManagementStrategy abstractDataManagementStrategySpy;

    public void setup() {
        super.setup();
        abstractDataManagementStrategySpy = spy(new AbstractDataManagementStrategy() {

            @Override
            public void populateTestTools(TestToolsView.Presenter testToolsPresenter, ScenarioSimulationContext context, GridWidget gridWidget) {

            }

            @Override
            public void manageScenarioSimulationModelContent(ObservablePath currentPath, ScenarioSimulationModelContent toManage) {

            }

            @Override
            public boolean isADataType(String value) {
                return false;
            }
        });
    }

    @Test
    public void getSimpleClassFactModelTree() {
        Class[] expectedClazzes = {String.class, Boolean.class, Integer.class, Double.class, Number.class};
        for (Class expectedClazz : expectedClazzes) {
            final FactModelTree retrieved = AbstractDataManagementStrategy.getSimpleClassFactModelTree(
                    expectedClazz.getSimpleName(),
                    expectedClazz.getCanonicalName());
            assertNotNull(retrieved);
            String key = expectedClazz.getSimpleName();
            assertEquals(key, retrieved.getFactName());
            String fullName = expectedClazz.getCanonicalName();
            String packageName = fullName.substring(0, fullName.lastIndexOf("."));
            assertEquals(packageName, retrieved.getFullPackage());
            Map<String, String> simpleProperties = retrieved.getSimpleProperties();
            assertNotNull(simpleProperties);
            assertEquals(1, simpleProperties.size());
            Util.assertTrue(simpleProperties.containsKey(TestProperties.LOWER_CASE_VALUE));
            String simplePropertyValue = simpleProperties.get(TestProperties.LOWER_CASE_VALUE);
            assertNotNull(simplePropertyValue);
            assertEquals(fullName, simplePropertyValue);
        }
    }

    @Test
    public void setModel() {
        abstractDataManagementStrategySpy.model = null;
        ScenarioSimulationModel modelMock = mock(ScenarioSimulationModel.class);
        abstractDataManagementStrategySpy.setModel(modelMock);
        assertEquals(modelMock, abstractDataManagementStrategySpy.model);
    }

    @Test
    public void getPropertiesToHideMapNotSelectedColumnNotInstanceAssigned() {
        commonGetPropertiesToHideMap(true, false);
    }

    @Test
    public void getPropertiesToHideMapSelectedColumnNotInstanceAssigned() {
        commonGetPropertiesToHideMap(false, false);
    }

    @Test
    public void getPropertiesToHideMapSelectedColumnInstanceAssigned() {
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

    @Test
    public void storeDataSimulation() {
        ScenarioSimulationContext scenarioSimulationContextSpy = spy(scenarioSimulationContextLocal);
        doReturn(simulationMock).when(scenarioSimulationContextSpy).getAbstractScesimModelByGridWidget(GridWidget.SIMULATION);
        final FactModelTuple factModelTuple = getFactTuple();
        abstractDataManagementStrategySpy.storeData(factModelTuple, testToolsPresenterMock, scenarioSimulationContextSpy, GridWidget.SIMULATION);
        verify(testToolsPresenterMock, times(1)).setDataObjectFieldsMap(isA(SortedMap.class));
        verify(testToolsPresenterMock, times(1)).setSimpleJavaTypeFieldsMap(isA(SortedMap.class));
        verify(testToolsPresenterMock, times(1)).setInstanceFieldsMap(isA(SortedMap.class));
        verify(testToolsPresenterMock, times(1)).setSimpleJavaInstanceFieldsMap(isA(SortedMap.class));
        verify(testToolsPresenterMock, times(1)).setHiddenFieldsMap(isA(SortedMap.class));
        verify(testToolsPresenterMock, times(1)).hideProperties(isA(Map.class));
        verify(scenarioSimulationContextSpy, times(1)).setDataObjectFieldsMap(isA(SortedMap.class));
        verify(scenarioSimulationContextSpy, times(1)).setDataObjectsInstancesName(isA(Set.class));
        verify(scenarioGridModelMock, times(1)).setSimpleJavaTypeInstancesName(isA(Set.class));
    }

    @Test
    public void storeDataBackground() {
        ScenarioSimulationContext scenarioSimulationContextSpy = spy(scenarioSimulationContextLocal);
        doReturn(backgroundMock).when(scenarioSimulationContextSpy).getAbstractScesimModelByGridWidget(GridWidget.BACKGROUND);
        final FactModelTuple factModelTuple = getFactTuple();
        abstractDataManagementStrategySpy.storeData(factModelTuple, testToolsPresenterMock, scenarioSimulationContextSpy, GridWidget.BACKGROUND);
        verify(testToolsPresenterMock, times(1)).setDataObjectFieldsMap(isA(SortedMap.class));
        verify(testToolsPresenterMock, times(1)).setSimpleJavaTypeFieldsMap(isA(SortedMap.class));
        verify(testToolsPresenterMock, times(1)).setInstanceFieldsMap(isA(SortedMap.class));
        verify(testToolsPresenterMock, times(1)).setSimpleJavaInstanceFieldsMap(isA(SortedMap.class));
        verify(testToolsPresenterMock, times(1)).setHiddenFieldsMap(isA(SortedMap.class));
        verify(testToolsPresenterMock, times(1)).hideProperties(isA(Map.class));
        verify(scenarioSimulationContextSpy, times(1)).setDataObjectFieldsMap(isA(SortedMap.class));
        verify(scenarioSimulationContextSpy, never()).setDataObjectsInstancesName(isA(Set.class));
        verify(backgroundGridModelMock, never()).setSimpleJavaTypeInstancesName(isA(Set.class));
    }

    @Test
    public void getInstanceMap() {
        SortedMap<String, FactModelTree> sourceMap = getSourceMap();
        SortedMap<String, FactModelTree> retrieved = abstractDataManagementStrategySpy.getInstanceMap(sourceMap);
        assertNotNull(retrieved);
        assertTrue(retrieved.isEmpty());
        abstractDataManagementStrategySpy.setModel(modelLocal);
        retrieved = abstractDataManagementStrategySpy.getInstanceMap(sourceMap);
        assertNotNull(retrieved);
        // " is the number of factmappings - inside modelLocal - whose class is "Void"
        assertEquals(2, retrieved.size());
    }

    private SortedMap<String, FactModelTree> getSourceMap() {
        SortedMap<String, FactModelTree> toReturn = new TreeMap<>();
        FactModelTree toPut = new FactModelTree("Void", "package", new HashMap<>(), new HashMap<>());
        toReturn.put("Void", toPut);
        return toReturn;
    }

    private FactModelTuple getFactTuple() {
        return new FactModelTuple(new TreeMap<>(), new TreeMap<>());
    }

    private void commonGetPropertiesToHideMap(boolean selectedColumnNull, boolean isInstanceAssigned) {
        if (selectedColumnNull) {
            doReturn(null).when(scenarioGridModelMock).getSelectedColumn();
        } else if (isInstanceAssigned) {
            doReturn(gridColumnMock).when(scenarioGridModelMock).getSelectedColumn();
            doReturn(true).when(gridColumnMock).isInstanceAssigned();
            doReturn(new ArrayList<>()).when(abstractDataManagementStrategySpy).getPropertiesToHide(eq(gridColumnMock), eq(scenarioGridModelMock));
        }
        final Map<String, List<List<String>>> retrieved = abstractDataManagementStrategySpy.getPropertiesToHide(scenarioGridModelMock);
        if (selectedColumnNull) {
            assertTrue(retrieved.isEmpty());
            verify(abstractDataManagementStrategySpy, never()).getPropertiesToHide(isA(ScenarioGridColumn.class), eq(scenarioGridModelMock));
        } else if (isInstanceAssigned) {
            verify(abstractDataManagementStrategySpy, times(1)).getPropertiesToHide(eq(gridColumnMock), eq(scenarioGridModelMock));
        } else {
            verify(abstractDataManagementStrategySpy, never()).getPropertiesToHide(isA(ScenarioGridColumn.class), eq(scenarioGridModelMock));
        }
        reset(abstractDataManagementStrategySpy);
    }

    private void commonGetPropertiesToHideList(boolean isPropertyAssigned) {
        doReturn(isPropertyAssigned).when(gridColumnMock).isPropertyAssigned();
        List<List<String>> retrieved = abstractDataManagementStrategySpy.getPropertiesToHide(gridColumnMock, scenarioGridModelMock);
        if (isPropertyAssigned) {
            assertTrue(retrieved.isEmpty());
            verify(scenarioGridModelMock, never()).getAbstractScesimModel();
        } else {
            verify(scenarioGridModelMock, times(1)).getAbstractScesimModel();
            verify(scenarioGridModelMock, times(1)).getInstanceScenarioGridColumns(eq(gridColumnMock));
        }
        reset(scenarioGridModelMock);
    }
}