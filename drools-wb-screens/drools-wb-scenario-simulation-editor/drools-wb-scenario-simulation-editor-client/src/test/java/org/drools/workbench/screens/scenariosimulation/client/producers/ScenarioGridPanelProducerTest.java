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
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationView;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationMainGridPanelClickHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationMainGridPanelMouseMoveHandler;
import org.drools.workbench.screens.scenariosimulation.client.menu.ScenarioContextMenuRegistry;
import org.drools.workbench.screens.scenariosimulation.client.models.BackgroundGridModel;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.popover.ErrorReportPopoverPresenter;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridWidget;
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
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioGridPanelProducerTest extends AbstractProducerTest {

    @Mock
    private ScenarioContextMenuRegistry scenarioContextMenuRegistryMock;

    @Mock
    private ErrorReportPopoverPresenter errorReportPopupPresenterMock;

    @Mock
    private ScenarioSimulationMainGridPanelClickHandler clickHandlerMock;

    @Mock
    private ScenarioSimulationMainGridPanelMouseMoveHandler mouseMoveHandlerMock;

    @Captor
    private ArgumentCaptor<ScenarioGrid> scenarioGridArgumentCaptor;

    private ScenarioGridPanelProducer scenarioGridPanelProducer;

    @Before
    public void setup() {
        super.setup();
        scenarioGridPanelProducer = spy(new ScenarioGridPanelProducer() {
            {
                this.simulationGridLayer = scenarioGridLayerMock;
                this.simulationGridPanel = scenarioGridPanelMock;
                this.backgroundGridLayer = backgroundGridLayerMock;
                this.backgroundGridPanel = backgroundGridPanelMock;
                this.scenarioContextMenuRegistry = scenarioContextMenuRegistryMock;
                this.simulationGridWidget = scenarioGridWidgetSpy;
                this.backgroundGridWidget = backgroundGridWidgetSpy;
                this.scenarioSimulationView = scenarioSimulationViewMock;
                this.errorReportPopupPresenter = errorReportPopupPresenterMock;
                this.simulationGridPanelClickHandler = clickHandlerMock;
                this.simulationGridPanelMouseMoveHandler = mouseMoveHandlerMock;
                this.backgroundGridPanelClickHandler = clickHandlerMock;
                this.backgroundGridPanelMouseMoveHandler = mouseMoveHandlerMock;
            }
        });
        when(backgroundGridPanelMock.getScenarioGridLayer()).thenReturn(backgroundGridLayerMock);
        when(backgroundGridLayerMock.getScenarioGrid()).thenReturn(backgroundGridMock);
    }

    @Test
    public void init() {
        assertNull(scenarioGridPanelProducer.scenarioSimulationContext);
        scenarioGridPanelProducer.init();
        ScenarioSimulationContext retrieved = scenarioGridPanelProducer.scenarioSimulationContext;
        assertNotNull(retrieved);
        verify(scenarioGridPanelProducer, times(1)).initializeGrid(eq(scenarioGridLayerMock), eq(scenarioGridPanelMock), isA(ScenarioGridModel.class), eq(retrieved));
        verify(scenarioGridPanelProducer, times(1)).initializeGrid(eq(backgroundGridLayerMock), eq(backgroundGridPanelMock), isA(BackgroundGridModel.class), eq(retrieved));
    }

    @Test
    public void initializeGrid() {
        scenarioGridPanelProducer.initializeGrid(scenarioGridLayerMock, scenarioGridPanelMock, scenarioGridModelMock, scenarioSimulationContextLocal);
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
        assertNotNull(scenarioGridArgumentCaptor.getValue().getModel().getScenarioExpressionCellTextAreaSingletonDOMElementFactory());
    }

    @Test
    public void getSimulationGridPanel() {
        final ScenarioGridPanel retrieved = scenarioGridPanelProducer.getSimulationGridPanel();
        assertEquals(scenarioGridPanelMock, retrieved);
    }

    @Test
    public void getBackgroundGridPanel() {
        final ScenarioGridPanel retrieved = scenarioGridPanelProducer.getBackgroundGridPanel();
        assertEquals(backgroundGridPanelMock, retrieved);
    }

    @Test
    public void getScenarioSimulationView() {
        final ScenarioSimulationView retrieved = scenarioGridPanelProducer.getScenarioSimulationView(eventBusMock);
        assertEquals(scenarioSimulationViewMock, retrieved);
        verify(scenarioGridPanelProducer, times(1)).getScenarioMainGridWidget(eq(eventBusMock));
        verify(scenarioSimulationViewMock, times(1)).setScenarioGridWidget(scenarioGridWidgetSpy);
    }

    @Test
    public void getBackgroundGridWidget() {
        ScenarioGridWidget retrieved = scenarioGridPanelProducer.getBackgroundGridWidget(eventBusMock);
        assertEquals(backgroundGridWidgetSpy, retrieved);
        verify(scenarioGridPanelProducer, times(1)).initGridWidget(eq(backgroundGridWidgetSpy), eq(backgroundGridPanelMock), eq(clickHandlerMock), eq(mouseMoveHandlerMock), eq(eventBusMock));
    }

    @Test
    public void initGridWidget() {
        scenarioGridPanelProducer.initGridWidget(scenarioGridWidgetSpy, scenarioGridPanelMock, clickHandlerMock, mouseMoveHandlerMock, eventBusMock);
        verify(scenarioGridPanelMock, times(1)).setEventBus(eq(eventBusMock));
        verify(scenarioContextMenuRegistryMock).setEventBus(eventBusMock);
        verify(clickHandlerMock, times(1)).setScenarioContextMenuRegistry(eq(scenarioGridPanelProducer.getScenarioContextMenuRegistry()));
        verify(clickHandlerMock, times(1)).setScenarioGridPanel(eq(scenarioGridPanelMock));
        verify(clickHandlerMock, times(1)).setEventBus(eq(eventBusMock));
        verify(scenarioContextMenuRegistryMock, times(1)).setErrorReportPopoverPresenter(errorReportPopupPresenterMock);
        verify(mouseMoveHandlerMock, times(1)).setScenarioGridPanel(eq(scenarioGridPanelMock));
        verify(mouseMoveHandlerMock, times(1)).setErrorReportPopupPresenter(eq(errorReportPopupPresenterMock));
        verify(scenarioGridPanelMock, times(1)).addHandlers(eq(clickHandlerMock), eq(mouseMoveHandlerMock));
        verify(scenarioGridWidgetSpy, times(1)).setScenarioGridPanel(eq(scenarioGridPanelMock));
    }

    @Test
    public void getScenarioContextMenuRegistry() {
        assertEquals(scenarioContextMenuRegistryMock, scenarioGridPanelProducer.getScenarioContextMenuRegistry());
    }
}