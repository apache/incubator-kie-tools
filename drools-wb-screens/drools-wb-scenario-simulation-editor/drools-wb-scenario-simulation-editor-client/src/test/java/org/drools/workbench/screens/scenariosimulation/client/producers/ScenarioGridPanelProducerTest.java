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

package org.drools.workbench.screens.scenariosimulation.client.producers;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.Command;
import org.drools.workbench.screens.scenariosimulation.client.menu.ScenarioContextMenuRegistry;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidgetKeyboardHandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioGridPanelProducerTest extends AbstractProducerTest {

    @Mock
    private ScenarioGridLayer scenarioGridLayerMock;

    @Mock
    private ScenarioGridLayer scenarioBackgroundGridLayerMock;

    @Mock
    private ScenarioGridPanel scenarioBackgroundGridPanelMock;

    @Mock
    private ScenarioContextMenuRegistry scenarioContextMenuRegistryMock;

    @Captor
    private ArgumentCaptor<ScenarioGrid> scenarioGridArgumentCaptor;

    private ScenarioGridPanelProducer scenarioGridPanelProducer;

    @Before
    public void setup() {
        super.setup();
        scenarioGridPanelProducer = spy(new ScenarioGridPanelProducer() {
            {
                this.scenarioMainGridLayer = scenarioGridLayerMock;
                this.scenarioMainGridPanel = scenarioGridPanelMock;
                this.scenarioBackgroundGridLayer = scenarioBackgroundGridLayerMock;
                this.scenarioBackgroundGridPanel = scenarioBackgroundGridPanelMock;
                this.scenarioContextMenuRegistry = scenarioContextMenuRegistryMock;
            }
        });
        when(scenarioBackgroundGridPanelMock.getScenarioGridLayer()).thenReturn(scenarioBackgroundGridLayerMock);
        when(scenarioBackgroundGridLayerMock.getScenarioGrid()).thenReturn(scenarioGridMock);
    }

    @Test
    public void init() {
        scenarioGridPanelProducer.init();
        verify(scenarioGridPanelProducer, times(1)).initializeGrid(eq(scenarioGridLayerMock), eq(scenarioGridPanelMock));
        verify(scenarioGridPanelProducer, times(1)).initializeGrid(eq(scenarioBackgroundGridLayerMock), eq(scenarioBackgroundGridPanelMock));
    }

    @Test
    public void initializeGrid() {
        scenarioGridPanelProducer.initializeGrid(scenarioGridLayerMock, scenarioGridPanelMock);
        verify(scenarioGridLayerMock, times(1)).addScenarioGrid(isA(ScenarioGrid.class));
        verify(scenarioGridLayerMock, times(1)).addScenarioGrid(scenarioGridArgumentCaptor.capture());
        verify(scenarioGridLayerMock, times(1)).enterPinnedMode(eq(scenarioGridArgumentCaptor.getValue()), isA(Command.class));
        verify(scenarioGridPanelMock, times(1)).add(eq(scenarioGridLayerMock));
        verify(scenarioGridPanelMock, times(1)).addKeyDownHandler(isA(BaseGridWidgetKeyboardHandler.class));
        assertFalse(scenarioGridArgumentCaptor.getValue().isDraggable());
        assertNotNull(scenarioGridArgumentCaptor.getValue().getScenarioSimulationContext());
        assertNotNull(scenarioGridArgumentCaptor.getValue().getModel());
        assertNotNull(scenarioGridArgumentCaptor.getValue().getModel().getCollectionEditorSingletonDOMElementFactory());
        assertNotNull(scenarioGridArgumentCaptor.getValue().getModel().getScenarioCellTextAreaSingletonDOMElementFactory());
        assertNotNull(scenarioGridArgumentCaptor.getValue().getModel().getScenarioHeaderTextBoxSingletonDOMElementFactory());
    }

    @Test
    public void getScenarioMainGridPanel() {
        final ScenarioGridPanel retrieved = scenarioGridPanelProducer.getScenarioMainGridPanel();
        assertEquals(scenarioGridPanelMock, retrieved);
    }

    @Test
    public void getScenarioBackgroundGridPanel() {
        final ScenarioGridPanel retrieved = scenarioGridPanelProducer.getScenarioBackgroundGridPanel();
        assertEquals(scenarioBackgroundGridPanelMock, retrieved);
    }

    @Test
    public void getScenarioContextMenuRegistry() {
        final ScenarioContextMenuRegistry retrieved = scenarioGridPanelProducer.getScenarioContextMenuRegistry();
        assertEquals(scenarioContextMenuRegistryMock, retrieved);
    }
}