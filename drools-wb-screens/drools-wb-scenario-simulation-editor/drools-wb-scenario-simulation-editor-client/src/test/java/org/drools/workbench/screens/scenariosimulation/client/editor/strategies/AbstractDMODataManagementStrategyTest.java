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
import java.util.SortedMap;
import java.util.TreeMap;

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
import org.uberfire.backend.vfs.ObservablePath;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractDMODataManagementStrategyTest extends AbstractScenarioSimulationEditorTest {


    private final static String PARAMETRIC_FIELD_TYPE = "ParametricFieldType";
    private final static String FQCN_BY_FACTNAME = "FQCNByFactName";
    private AbstractDMODataManagementStrategy abstractDMODataManagementStrategySpy;
    private AbstractDMODataManagementStrategy.ResultHolder factModelTreeHolderlocal;


    private FactModelTuple factModelTupleLocal;
    private SortedMap<String, FactModelTree> visibleFactsLocal = new TreeMap<>();
    private SortedMap<String, FactModelTree> hiddenFactsLocal = new TreeMap<>();

    @Before
    public void setup() {
        super.setup();
        factModelTupleLocal = new FactModelTuple(visibleFactsLocal, hiddenFactsLocal);
        factModelTreeHolderlocal = new AbstractDataManagementStrategy.ResultHolder();
        factModelTreeHolderlocal.setFactModelTuple(factModelTupleLocal);
        abstractDMODataManagementStrategySpy = spy(new AbstractDMODataManagementStrategy() {

            @Override
            public void manageScenarioSimulationModelContent(ObservablePath currentPath, ScenarioSimulationModelContent toManage) {

            }

            @Override
            public boolean isADataType(String value) {
                return false;
            }

            @Override
            protected String getFQCNByFactName(String factName) {
                return FQCN_BY_FACTNAME;
            }

            @Override
            protected String getParametricFieldType(String factName, String propertyName) {
                return PARAMETRIC_FIELD_TYPE;
            }

            @Override
            protected List<String> getFactTypes() {
                return new ArrayList<>();
            }

            @Override
            protected boolean skipPopulateTestTools() {
                return false;
            }

            @Override
            protected void manageDataObjects(List<String> dataObjectsTypes, TestToolsView.Presenter testToolsPresenter, int expectedElements, SortedMap<String, FactModelTree> dataObjectsFieldsMap, ScenarioSimulationContext context, List<String> simpleJavaTypes, GridWidget gridWidget) {

            }

            {
                this.model = modelLocal;
                this.factModelTreeHolder = factModelTreeHolderlocal;
            }


        });
    }

    @Test
    public void populateTestToolsWithoutFactModelTuple() {
        factModelTreeHolderlocal.setFactModelTuple(null);
        abstractDMODataManagementStrategySpy.populateTestTools(testToolsPresenterMock, scenarioSimulationContextLocal, GridWidget.SIMULATION);
        verify(abstractDMODataManagementStrategySpy, never()).storeData(eq(factModelTupleLocal), eq(testToolsPresenterMock), eq(scenarioSimulationContextLocal), eq(GridWidget.SIMULATION));
    }

    @Test
    public void populateTestToolsWithFactModelTuple() {
        abstractDMODataManagementStrategySpy.populateTestTools(testToolsPresenterMock, scenarioSimulationContextLocal, GridWidget.SIMULATION);
        verify(abstractDMODataManagementStrategySpy, times(1)).storeData(eq(factModelTupleLocal), eq(testToolsPresenterMock), eq(scenarioSimulationContextLocal), eq(GridWidget.SIMULATION));
    }
}