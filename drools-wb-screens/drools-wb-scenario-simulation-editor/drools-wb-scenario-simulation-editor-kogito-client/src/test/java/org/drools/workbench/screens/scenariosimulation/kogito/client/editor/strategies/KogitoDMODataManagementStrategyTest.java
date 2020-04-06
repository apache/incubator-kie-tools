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
package org.drools.workbench.screens.scenariosimulation.kogito.client.editor.strategies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmo.KogitoAsyncPackageDataModelOracle;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class KogitoDMODataManagementStrategyTest {

    private static final String FACT_NAME = "factName";

    @Mock
    private KogitoAsyncPackageDataModelOracle oracleMock;
    @Mock
    private ObservablePath observablePathMock;
    @Mock
    private ScenarioSimulationModelContent scenarioSimulationModelContentMock;
    @Mock
    private ScenarioSimulationContext scenarioSimulationContextMock;
    @Mock
    private TestToolsPresenter testToolsPresenterMock;
    @Mock
    private FactModelTree factModelTreeMock;
    @Mock
    private GridWidget gridWidgetMock;

    private KogitoDMODataManagementStrategy kogitoDMODataManagementStrategySpy;

    @Before
    public void setup() {
        kogitoDMODataManagementStrategySpy = spy(new KogitoDMODataManagementStrategy(oracleMock) {
            @Override
            public FactModelTree getFactModelTree(String factName, ModelField[] modelFields) {
                return factModelTreeMock;
            }
            @Override
            public void aggregatorCallbackMethod(TestToolsView.Presenter testToolsPresenter, int expectedElements, SortedMap<String, FactModelTree> factTypeFieldsMap, ScenarioSimulationContext context, FactModelTree result, List<String> simpleJavaTypes, GridWidget gridWidget) {
                // DO nothing
            }
        });
    }

    @Test
    public void manageScenarioSimulationModelContent() {
        kogitoDMODataManagementStrategySpy.manageScenarioSimulationModelContent(observablePathMock,
                                                                                scenarioSimulationModelContentMock);
        verify(scenarioSimulationModelContentMock, times(1)).getModel();
        verify(oracleMock, times(1)).init(eq(observablePathMock));
    }

    @Test
    public void isADataTypeEmpty() {
        when(oracleMock.getFactTypes()).thenReturn(new String[0]);
        assertFalse(kogitoDMODataManagementStrategySpy.isADataType("Test"));
    }

    @Test
    public void isADataTypeTrue() {
        String[] array = {"Test"};
        when(oracleMock.getFactTypes()).thenReturn(array);
        assertTrue(kogitoDMODataManagementStrategySpy.isADataType("Test"));
    }

    @Test
    public void isADataTypeFalse() {
        String[] array = {"Est"};
        when(oracleMock.getFactTypes()).thenReturn(array);
        assertFalse(kogitoDMODataManagementStrategySpy.isADataType("Test"));
    }

    @Test
    public void retrieveFactModelTuple() {
        String factType = "factType";
        ModelField[] modelFields = new ModelField[1];
        when(oracleMock.getFieldCompletions(eq(factType))).thenReturn(modelFields);
        List<String> dataObjectsType = Arrays.asList(factType);
        SortedMap<String, FactModelTree> dataObjectsFieldMap = new TreeMap<>();
        List<String> javaSimpleType = new ArrayList<>();
        kogitoDMODataManagementStrategySpy.manageDataObjects(dataObjectsType, testToolsPresenterMock, 1, dataObjectsFieldMap, scenarioSimulationContextMock, javaSimpleType, gridWidgetMock);
        verify(oracleMock, times(1)).getFieldCompletions(eq(factType));
        verify(kogitoDMODataManagementStrategySpy, times(1)).getFactModelTree(eq(factType), eq(modelFields));
        verify(kogitoDMODataManagementStrategySpy, times(1)).aggregatorCallbackMethod(eq(testToolsPresenterMock), eq(1), eq(dataObjectsFieldMap), eq(scenarioSimulationContextMock), eq(factModelTreeMock), eq(javaSimpleType), eq(gridWidgetMock));
    }

    @Test
    public void getFactTypesEmpty() {
        when(oracleMock.getFactTypes()).thenReturn(new String[0]);
        List<String> facts = kogitoDMODataManagementStrategySpy.getFactTypes();
        assertTrue(facts.isEmpty());
    }

    @Test
    public void getFactTypes() {
        String[] array = {"Test"};
        when(oracleMock.getFactTypes()).thenReturn(array);
        List<String> facts = kogitoDMODataManagementStrategySpy.getFactTypes();
        assertTrue(facts.size() == 1);
        assertEquals("Test", facts.get(0));
    }

    @Test
    public void skipPopulateTestToolsEmptyArray() {
        when(oracleMock.getFactTypes()).thenReturn(new String[0]);
        assertTrue(kogitoDMODataManagementStrategySpy.skipPopulateTestTools());
    }

    @Test
    public void skipPopulateTestToolsArrayPopulated() {
        String[] array = {"Test"};
        when(oracleMock.getFactTypes()).thenReturn(array);
        assertFalse(kogitoDMODataManagementStrategySpy.skipPopulateTestTools());
    }

    @Test
    public void getFQCNByFactName() {
        kogitoDMODataManagementStrategySpy.getFQCNByFactName(FACT_NAME);
        verify(oracleMock, times(1)).getFQCNByFactName(eq(FACT_NAME));
    }

    @Test
    public void getParametricFieldType() {
        kogitoDMODataManagementStrategySpy.getParametricFieldType(FACT_NAME, "propertyName");
        verify(oracleMock, times(1)).getParametricFieldType(eq(FACT_NAME), eq("propertyName"));
    }
}
