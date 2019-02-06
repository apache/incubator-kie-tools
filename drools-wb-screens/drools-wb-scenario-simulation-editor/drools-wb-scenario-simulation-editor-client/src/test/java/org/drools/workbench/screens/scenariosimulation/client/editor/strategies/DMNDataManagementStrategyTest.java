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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.editor.AbstractScenarioSimulationEditorTest;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.drools.workbench.screens.scenariosimulation.service.DMNTypeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDataManagementStrategyTest extends AbstractScenarioSimulationEditorTest {

    @Mock
    private DMNTypeService dmnTypeServiceMock;


    private DMNDataManagementStrategy.ResultHolder factModelTreeHolderlocal;

    private FactModelTuple factModelTupleLocal;
    private SortedMap<String, FactModelTree> visibleFactsLocal = new TreeMap<>();
    private SortedMap<String, FactModelTree> hiddenFactsLocal = new TreeMap<>();
    private DMNDataManagementStrategy dmnDataManagementStrategy;

    @Before
    public void setup() {
        super.setup();
        factModelTupleLocal = new FactModelTuple(visibleFactsLocal, hiddenFactsLocal);
        factModelTreeHolderlocal = new DMNDataManagementStrategy.ResultHolder();
        factModelTreeHolderlocal.factModelTuple = factModelTupleLocal;
        when(dmnTypeServiceMock.retrieveType(any(), anyString())).thenReturn(mock(FactModelTuple.class));
        modelLocal.getSimulation().getSimulationDescriptor().setDmnFilePath("dmn_file_path");
        dmnDataManagementStrategy = spy(new DMNDataManagementStrategy(new CallerMock<>(dmnTypeServiceMock), scenarioSimulationContextLocal) {
            {
                this.currentPath = mock(Path.class);
                this.model = modelLocal;
                this.scenarioSimulationContext = scenarioSimulationContextLocal;
            }
        });
        dmnDataManagementStrategy.factModelTreeHolder = factModelTreeHolderlocal;
    }

    @Test
    public void populateRightPanelWithoutFactModelTuple() {
        factModelTreeHolderlocal.factModelTuple = null;
        dmnDataManagementStrategy.populateRightPanel(rightPanelPresenterMock, scenarioGridModelMock);
        verify(dmnTypeServiceMock, times(1)).retrieveType(any(), anyString());
        verify(dmnDataManagementStrategy, times(1)).getSuccessCallback(rightPanelPresenterMock);
    }

    @Test
    public void populateRightPanelWithFactModelTuple() {
        dmnDataManagementStrategy.populateRightPanel(rightPanelPresenterMock, scenarioGridModelMock);
        verify(dmnTypeServiceMock, never()).retrieveType(any(), anyString());
        verify(dmnDataManagementStrategy, times(1)).getSuccessCallback(rightPanelPresenterMock);
    }

    @Test
    public void manageScenarioSimulationModelContent() {
        final ScenarioSimulationModelContent contentMock = spy(content);
        dmnDataManagementStrategy.manageScenarioSimulationModelContent(observablePathMock, contentMock);
        verify(observablePathMock, times(1)).getOriginal();
        verify(contentMock, times(1)).getModel();
    }

    @Test
    public void successCallbackContent() {
        scenarioSimulationContextLocal.setDataObjectFieldsMap(null);
        dmnDataManagementStrategy.successCallbackContent(mock(FactModelTuple.class), rightPanelPresenterMock);
        assertNotNull(scenarioSimulationContextLocal.getDataObjectFieldsMap());
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