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

package org.drools.workbench.screens.scenariosimulation.client.editor.strategies;

import java.util.SortedMap;
import java.util.TreeMap;

import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelView;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.drools.workbench.screens.scenariosimulation.service.DMNTypeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.mocks.CallerMock;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNDataManagementStrategyTest {

    @Mock
    private DMNTypeService dmnTypeServiceMock;


    private DMNDataManagementStrategy.ResultHolder factModelTreeHolderlocal;

    private FactModelTuple factModelTupleLocal;
    private SortedMap<String, FactModelTree> visibleFactsLocal = new TreeMap<>();
    private SortedMap<String, FactModelTree> hiddenFactsLocal = new TreeMap<>();
    private DMNDataManagementStrategy dmnDataManagementStrategy;

    @Before
    public void init() {
        factModelTupleLocal = new FactModelTuple(visibleFactsLocal, hiddenFactsLocal);
        factModelTreeHolderlocal = new DMNDataManagementStrategy.ResultHolder();
        factModelTreeHolderlocal.factModelTuple = factModelTupleLocal;
        when(dmnTypeServiceMock.retrieveType(any(), anyString())).thenReturn(mock(FactModelTuple.class));
        dmnDataManagementStrategy = new DMNDataManagementStrategy(new CallerMock<>(dmnTypeServiceMock));
        dmnDataManagementStrategy.factModelTreeHolder = factModelTreeHolderlocal;
    }

    @Test
    public void populateRightPanel() {
        ScenarioSimulationModelContent scenarioSimulationModelContentMock = mock(ScenarioSimulationModelContent.class);
        ScenarioSimulationModel scenarioSimulationModel = mock(ScenarioSimulationModel.class);
        when(scenarioSimulationModelContentMock.getModel()).thenReturn(scenarioSimulationModel);
        Simulation simulationMock = mock(Simulation.class);
        when(scenarioSimulationModel.getSimulation()).thenReturn(simulationMock);
        SimulationDescriptor simulationDescriptorMock = mock(SimulationDescriptor.class);
        when(simulationMock.getSimulationDescriptor()).thenReturn(simulationDescriptorMock);
        dmnDataManagementStrategy.factModelTreeHolder.factModelTuple = null;
        dmnDataManagementStrategy.manageScenarioSimulationModelContent(mock(ObservablePath.class), scenarioSimulationModelContentMock);
        dmnDataManagementStrategy.populateRightPanel(mock(RightPanelView.Presenter.class), mock(ScenarioGridModel.class));
        verify(dmnTypeServiceMock, times(1)).retrieveType(any(), anyString());
    }

    @Test
    public void manageScenarioSimulationModelContent() {
        ObservablePath observablePathMock = mock(ObservablePath.class);
        ScenarioSimulationModelContent scenarioSimulationModelContentMock = mock(ScenarioSimulationModelContent.class);
        dmnDataManagementStrategy.manageScenarioSimulationModelContent(observablePathMock, scenarioSimulationModelContentMock);
        verify(observablePathMock, times(1)).getOriginal();
        verify(scenarioSimulationModelContentMock, times(1)).getModel();
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
        boolean retrieved = dmnDataManagementStrategy.isADataType(value);
        if (expected) {
            assertTrue(retrieved);
        } else {
            assertFalse(retrieved);
        }
    }
}