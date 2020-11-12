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
import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.models.AbstractScesimGridModel;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
            ScenarioGridPanel gridPanel = scenarioSimulationContextLocal.getScenarioGridPanelByGridWidget(gridWidget);
            if (GridWidget.BACKGROUND.equals(gridWidget)) {
                assertEquals(backgroundGridPanelMock, gridPanel);
            } else {
                assertEquals(scenarioGridPanelMock, gridPanel);
            }
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
            AbstractScesimGridModel model = scenarioSimulationContextLocal.getAbstractScesimGridModelByGridWidget(gridWidget);
            if (GridWidget.BACKGROUND.equals(gridWidget)) {
                assertEquals(backgroundGridModelMock, model);
            } else {
                assertEquals(scenarioGridModelMock, model);
            }
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
            AbstractScesimModel<AbstractScesimData> abstractScesimData = scenarioSimulationContextLocal.getAbstractScesimModelByGridWidget(gridWidget);
            if (GridWidget.BACKGROUND.equals(gridWidget)) {
                assertEquals(backgroundMock, abstractScesimData);
            } else {
                assertEquals(simulationMock, abstractScesimData);
            }
        }
    }

    @Test
    public void getScenarioSimulationModel() {
        ScenarioSimulationModel model = scenarioSimulationContextLocal.getScenarioSimulationModel();
        assertSame(scenarioSimulationModelMock, model);
    }

    @Test
    public void getCollectionEditorSingletonDOMElementFactory() {
        assertEquals(collectionEditorSingletonDOMElementFactoryTest,
                     scenarioSimulationContextLocal.getCollectionEditorSingletonDOMElementFactory(GridWidget.SIMULATION));
    }

    @Test
    public void getScenarioCellTextAreaSingletonDOMElementFactory() {
        assertEquals(scenarioCellTextAreaSingletonDOMElementFactorySpy,
                     scenarioSimulationContextLocal.getScenarioCellTextAreaSingletonDOMElementFactory(GridWidget.SIMULATION));
    }

    @Test
    public void getScenarioHeaderTextBoxSingletonDOMElementFactory() {
        assertEquals(scenarioHeaderTextBoxSingletonDOMElementFactorySpy,
                     scenarioSimulationContextLocal.getScenarioHeaderTextBoxSingletonDOMElementFactory(GridWidget.SIMULATION));
    }

    @Test
    public void getScenarioExpressionCellTextAreaSingletonDOMElementFactory() {
        assertEquals(scenarioExpressionCellTextAreaSingletonDOMElementFactorySpy,
                     scenarioSimulationContextLocal.getScenarioExpressionCellTextAreaSingletonDOMElementFactory(GridWidget.SIMULATION));
    }

    @Test
    public void setUndoButtonEnabledStatus() {
        scenarioSimulationContextLocal.setUndoButtonEnabledStatus(true);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setUndoButtonEnabledStatus(eq(true));
    }

    @Test
    public void setRedoButtonEnabledStatus() {
        scenarioSimulationContextLocal.setRedoButtonEnabledStatus(true);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setRedoButtonEnabledStatus(eq(true));
    }
}