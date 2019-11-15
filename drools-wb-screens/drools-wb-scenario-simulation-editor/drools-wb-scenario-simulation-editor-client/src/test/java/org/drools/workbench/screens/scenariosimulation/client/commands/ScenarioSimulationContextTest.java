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

import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationContextTest extends AbstractScenarioSimulationTest {
    

    @Before
    public void setup() {
        super.setup();
    }

    @Test
    public void getSelectedScenarioGridPanelSIMULATION() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(false);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(true);
        assertEquals(Optional.of(scenarioGridPanelMock), scenarioSimulationContextLocal.getSelectedScenarioGridPanel());
    }

    @Test
    public void getSelectedScenarioGridPanelBACKGROUND() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(true);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(false);
        assertEquals(Optional.of(backgroundGridPanelMock), scenarioSimulationContextLocal.getSelectedScenarioGridPanel());
    }

    @Test
    public void getSelectedScenarioGridPanelNone() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(false);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(false);
        assertEquals(Optional.empty(), scenarioSimulationContextLocal.getSelectedScenarioGridPanel());
    }

    @Test
    public void getSelectedScenarioGridWidgetSIMULATION() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(false);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(true);
        assertEquals(Optional.of(scenarioGridWidgetSpy), scenarioSimulationContextLocal.getSelectedScenarioGridWidget());
    }

    @Test
    public void getSelectedScenarioGridWidgetBACKGROUND() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(true);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(false);
        assertEquals(Optional.of(backgroundGridWidgetSpy), scenarioSimulationContextLocal.getSelectedScenarioGridWidget());
    }

    @Test
    public void getSelectedScenarioGridWidgetNone() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(false);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(false);
        assertEquals(Optional.empty(), scenarioSimulationContextLocal.getSelectedScenarioGridWidget());
    }

    @Test(expected = IllegalStateException.class)
    public void getSelectedScenarioGridWidgetFail() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(true);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(true);
        scenarioSimulationContextLocal.getSelectedScenarioGridWidget();
    }

    @Test
    public void getSelectedScenarioGridModelSIMULATION() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(false);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(true);
        assertEquals(Optional.of(scenarioGridModelMock), scenarioSimulationContextLocal.getSelectedScenarioGridModel());
    }

    @Test
    public void getSelectedScenarioGridModelBACKGROUND() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(true);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(false);
        assertEquals(Optional.of(backgroundGridModelMock), scenarioSimulationContextLocal.getSelectedScenarioGridModel());
    }

    @Test
    public void getSelectedScenarioGridModelNone() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(false);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(false);
        assertEquals(Optional.empty(), scenarioSimulationContextLocal.getSelectedScenarioGridModel());
    }

    @Test
    public void getSelectedScenarioGridLayerSIMULATION() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(false);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(true);
        assertEquals(Optional.of(scenarioGridLayerMock), scenarioSimulationContextLocal.getSelectedScenarioGridLayer());
    }

    @Test
    public void getSelectedScenarioGridLayerBACKGROUND() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(true);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(false);
        assertEquals(Optional.of(backgroundGridLayerMock), scenarioSimulationContextLocal.getSelectedScenarioGridLayer());
    }

    @Test
    public void getSelectedScenarioGridLayerNone() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(false);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(false);
        assertEquals(Optional.empty(), scenarioSimulationContextLocal.getSelectedScenarioGridLayer());
    }

    @Test
    public void getScenarioGridPanelByGridWidget() {
        assertEquals(scenarioGridPanelMock, scenarioSimulationContextLocal.getScenarioGridPanelByGridWidget(GridWidget.SIMULATION));
        assertEquals(backgroundGridPanelMock, scenarioSimulationContextLocal.getScenarioGridPanelByGridWidget(GridWidget.BACKGROUND));
    }

    @Test
    public void getScenarioGridPanelByGridWidgetCheckSwitch() {
        // Test to verify there are not new, un-managed, GridWidget
        for (GridWidget gridWidget : GridWidget.values()) {
            scenarioSimulationContextLocal.getScenarioGridPanelByGridWidget(gridWidget);
        }
    }

    @Test
    public void getAbstractScesimGridModelByGridWidget() {
        assertEquals(scenarioGridModelMock, scenarioSimulationContextLocal.getAbstractScesimGridModelByGridWidget(GridWidget.SIMULATION));
        assertEquals(backgroundGridModelMock, scenarioSimulationContextLocal.getAbstractScesimGridModelByGridWidget(GridWidget.BACKGROUND));
    }

    @Test
    public void getAbstractScesimGridModelByGridWidgetCheckSwitch() {
        // Test to verify there are not new, un-managed, GridWidget
        for (GridWidget gridWidget : GridWidget.values()) {
            scenarioSimulationContextLocal.getAbstractScesimGridModelByGridWidget(gridWidget);
        }
    }

    @Test
    public void getAbstractScesimModelByGridWidget() {
        assertEquals(scenarioSimulationContextLocal.status.simulation, scenarioSimulationContextLocal.getAbstractScesimModelByGridWidget(GridWidget.SIMULATION));
        assertEquals(scenarioSimulationContextLocal.status.background, scenarioSimulationContextLocal.getAbstractScesimModelByGridWidget(GridWidget.BACKGROUND));
    }

    @Test
    public void getAbstractScesimModelByGridWidgetCheckSwitch() {
        // Test to verify there are not new, un-managed, GridWidget
        for (GridWidget gridWidget : GridWidget.values()) {
            scenarioSimulationContextLocal.getAbstractScesimModelByGridWidget(gridWidget);
        }
    }

    @Test
    public void getCollectionEditorSingletonDOMElementFactory() {
        assertEquals(collectionEditorSingletonDOMElementFactoryTest,
                     scenarioSimulationContextLocal.getCollectionEditorSingletonDOMElementFactory(GridWidget.SIMULATION));
    }

    @Test
    public void getScenarioCellTextAreaSingletonDOMElementFactory() {
        assertEquals(scenarioCellTextAreaSingletonDOMElementFactoryTest,
                     scenarioSimulationContextLocal.getScenarioCellTextAreaSingletonDOMElementFactory(GridWidget.SIMULATION));
    }

    @Test
    public void getScenarioHeaderTextBoxSingletonDOMElementFactory() {
        assertEquals(scenarioHeaderTextBoxSingletonDOMElementFactoryTest,
                     scenarioSimulationContextLocal.getScenarioHeaderTextBoxSingletonDOMElementFactory(GridWidget.SIMULATION));
    }

    @Test
    public void getScenarioExpressionCellTextAreaSingletonDOMElementFactory() {
        assertEquals(scenarioExpressionCellTextAreaSingletonDOMElementFactoryMock,
                     scenarioSimulationContextLocal.getScenarioExpressionCellTextAreaSingletonDOMElementFactory(GridWidget.SIMULATION));
    }
}