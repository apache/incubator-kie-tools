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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.gwt.event.shared.EventBus;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.editor.AbstractScenarioSimulationEditorTest;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.uberfire.backend.vfs.Path;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractDMNDataManagementStrategyTest extends AbstractScenarioSimulationEditorTest {

    private AbstractDMNDataManagementStrategy abstractDMNDataManagementStrategySpy;
    private AbstractDMNDataManagementStrategy.ResultHolder factModelTreeHolderlocal;

    private FactModelTuple factModelTupleLocal;
    private SortedMap<String, FactModelTree> visibleFactsLocal = new TreeMap<>();
    private SortedMap<String, FactModelTree> hiddenFactsLocal = new TreeMap<>();

    private final String DMN_FILE_PATH = "DMN_FILE_PATH";

    @Before
    public void setup() {
        super.setup();
        factModelTupleLocal = new FactModelTuple(visibleFactsLocal, hiddenFactsLocal);
        factModelTreeHolderlocal = new AbstractDataManagementStrategy.ResultHolder();
        factModelTreeHolderlocal.setFactModelTuple(factModelTupleLocal);
        when(dmnTypeServiceMock.retrieveFactModelTuple(any(), anyString())).thenReturn(factModelTupleLocal);
        modelLocal.getSettings().setDmnFilePath(DMN_FILE_PATH);
        abstractDMNDataManagementStrategySpy = spy(new AbstractDMNDataManagementStrategy(mock(EventBus.class)) {
            @Override
            protected void retrieveFactModelTuple(TestToolsView.Presenter testToolsPresenter, ScenarioSimulationContext context, GridWidget gridWidget) {

            }

            {
                this.currentPath = mock(Path.class);
                this.model = modelLocal;
                this.factModelTreeHolder = factModelTreeHolderlocal;
                this.dmnFilePath = DMN_FILE_PATH;
            }
        });
    }

    @Test
    public void populateTestToolsWithoutFactModelTuple() {
        factModelTreeHolderlocal.setFactModelTuple(null);
        abstractDMNDataManagementStrategySpy.populateTestTools(testToolsPresenterMock, scenarioSimulationContextLocal, GridWidget.SIMULATION);
        verify(abstractDMNDataManagementStrategySpy, times(1)).retrieveFactModelTuple(eq(testToolsPresenterMock), eq(scenarioSimulationContextLocal), eq(GridWidget.SIMULATION));
        verify(abstractDMNDataManagementStrategySpy, never()).getSuccessCallback(testToolsPresenterMock, scenarioSimulationContextLocal, GridWidget.SIMULATION);
        verify(abstractDMNDataManagementStrategySpy, never()).getSuccessCallbackMethod(eq(factModelTupleLocal), eq(testToolsPresenterMock), eq(scenarioSimulationContextLocal), eq(GridWidget.SIMULATION));
    }

    @Test
    public void populateTestToolsWithoutFactModelTupleAndDifferentDMNPath() {
        factModelTreeHolderlocal.setFactModelTuple(null);
        modelLocal.getSettings().setDmnName("/src/test.dmn");
        abstractDMNDataManagementStrategySpy.populateTestTools(testToolsPresenterMock, scenarioSimulationContextLocal, GridWidget.SIMULATION);
        verify(abstractDMNDataManagementStrategySpy, times(1)).retrieveFactModelTuple(eq(testToolsPresenterMock), eq(scenarioSimulationContextLocal), eq(GridWidget.SIMULATION));
        verify(abstractDMNDataManagementStrategySpy, never()).getSuccessCallback(testToolsPresenterMock, scenarioSimulationContextLocal, GridWidget.SIMULATION);
        verify(abstractDMNDataManagementStrategySpy, never()).getSuccessCallbackMethod(eq(factModelTupleLocal), eq(testToolsPresenterMock), eq(scenarioSimulationContextLocal), eq(GridWidget.SIMULATION));
    }

    @Test
    public void populateTestToolsWithFactDifferentDMNPath() {
        modelLocal.getSettings().setDmnName("/src/test.dmn");
        abstractDMNDataManagementStrategySpy.populateTestTools(testToolsPresenterMock, scenarioSimulationContextLocal, GridWidget.SIMULATION);
        verify(abstractDMNDataManagementStrategySpy, never()).retrieveFactModelTuple(any(), any(), any());
        verify(abstractDMNDataManagementStrategySpy, times(1)).getSuccessCallback(testToolsPresenterMock, scenarioSimulationContextLocal, GridWidget.SIMULATION);
        verify(abstractDMNDataManagementStrategySpy, times(1)).getSuccessCallbackMethod(eq(factModelTupleLocal), eq(testToolsPresenterMock), eq(scenarioSimulationContextLocal), eq(GridWidget.SIMULATION));
    }

    @Test
    public void populateTestToolsWithFactModelTuple() {
        abstractDMNDataManagementStrategySpy.populateTestTools(testToolsPresenterMock, scenarioSimulationContextLocal, GridWidget.SIMULATION);
        verify(abstractDMNDataManagementStrategySpy, never()).retrieveFactModelTuple(any(), any(), any());
        verify(abstractDMNDataManagementStrategySpy, times(1)).getSuccessCallback(testToolsPresenterMock, scenarioSimulationContextLocal, GridWidget.SIMULATION);
        verify(abstractDMNDataManagementStrategySpy, times(1)).getSuccessCallbackMethod(eq(factModelTupleLocal), eq(testToolsPresenterMock), eq(scenarioSimulationContextLocal), eq(GridWidget.SIMULATION));
    }

    @Test
    public void manageScenarioSimulationModelContent() {
        final ScenarioSimulationModelContent contentMock = Mockito.spy(content);
        abstractDMNDataManagementStrategySpy.manageScenarioSimulationModelContent(observablePathMock, contentMock);
        verify(observablePathMock, times(1)).getOriginal();
        verify(contentMock, times(1)).getModel();
    }

    @Test
    public void isADataType() {
        visibleFactsLocal.clear();
        hiddenFactsLocal.clear();
        commonIsADataType("TEST", false);
        visibleFactsLocal.put("TEST", new FactModelTree());
        commonIsADataType("TOAST", false);
        commonIsADataType("TEST", true);
        visibleFactsLocal.clear();
        hiddenFactsLocal.put("TEST", new FactModelTree());
        commonIsADataType("TOAST", false);
        commonIsADataType("TEST", true);
    }

    private void commonIsADataType(String value, boolean expected) {
        boolean retrieved = abstractDMNDataManagementStrategySpy.isADataType(value);
        if (expected) {
            assertTrue(retrieved);
        } else {
            assertFalse(retrieved);
        }
    }

    @Test
    public void getSuccessCallbackMethod() {
        Map<String, List<String>> alreadyAssignedProperties = new HashMap<>();
        factModelTreeHolderlocal.setFactModelTuple(null);
        doReturn(alreadyAssignedProperties).when(abstractDMNDataManagementStrategySpy).getPropertiesToHide(scenarioGridModelMock);
        abstractDMNDataManagementStrategySpy.getSuccessCallbackMethod(factModelTupleLocal, testToolsPresenterMock, scenarioSimulationContextLocal, GridWidget.SIMULATION);
        verify(abstractDMNDataManagementStrategySpy, times(1)).getPropertiesToHide(eq(scenarioGridModelMock));
        verify(abstractDMNDataManagementStrategySpy, times(1)).storeData(eq(factModelTupleLocal), eq(testToolsPresenterMock), eq(scenarioSimulationContextLocal), eq(GridWidget.SIMULATION));
        verify(abstractDMNDataManagementStrategySpy, times(1)).showErrorsAndCleanupState(eq(factModelTupleLocal));
        assertEquals(factModelTupleLocal, factModelTreeHolderlocal.getFactModelTuple());
    }
}