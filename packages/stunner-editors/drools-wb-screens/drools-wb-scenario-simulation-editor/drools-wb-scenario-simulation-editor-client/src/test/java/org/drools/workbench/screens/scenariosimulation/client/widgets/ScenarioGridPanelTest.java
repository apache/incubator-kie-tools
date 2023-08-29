/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.drools.workbench.screens.scenariosimulation.client.widgets;

import com.ait.lienzo.client.core.event.NodeMouseWheelEvent;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridPanelClickHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridPanelMouseMoveHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioGridPanelTest extends AbstractScenarioSimulationTest {

    @Mock
    private NodeMouseWheelEvent nodeMouseWheelEvent;

    @Mock
    private ScenarioSimulationGridPanelClickHandler clickHandlerMock;

    @Mock
    private ScenarioSimulationGridPanelMouseMoveHandler scenarioSimulationGridPanelMouseMoveHandlerMock;

    @Mock
    private AbsolutePanel domElementContainerMock;

    @Mock
    private AbsolutePanel scrollPanelMock;

    private ScenarioGridPanel scenarioGridPanelSpy;

    @Before
    public void setup() {
        super.setup();
        scenarioGridPanelSpy = spy(new ScenarioGridPanel());
        when(scenarioGridPanelSpy.getDefaultGridLayer()).thenReturn(scenarioGridLayerMock);
        when(scenarioGridPanelSpy.getDomElementContainer()).thenReturn(domElementContainerMock);
        when(scenarioGridPanelSpy.getScrollPanel()).thenReturn(scrollPanelMock);
    }

    @Test
    public void addHandlers() {
        scenarioGridPanelSpy.addHandlers(clickHandlerMock, scenarioSimulationGridPanelMouseMoveHandlerMock);
        assertEquals(clickHandlerMock, scenarioGridPanelSpy.clickHandler);
        assertEquals(scenarioSimulationGridPanelMouseMoveHandlerMock, scenarioGridPanelSpy.mouseMoveHandler);
        verify(scenarioGridPanelSpy, times(1)).unregister();
        verify(domElementContainerMock, times(1)).addDomHandler(clickHandlerMock, ClickEvent.getType());
        verify(domElementContainerMock, times(1)).addDomHandler(clickHandlerMock, ContextMenuEvent.getType());
        verify(scenarioGridLayerMock, times(1)).addNodeMouseOutHandler(scenarioGridPanelSpy);
        verify(scenarioGridLayerMock, times(1)).addNodeMouseMoveHandler(scenarioSimulationGridPanelMouseMoveHandlerMock);
        verify(scenarioGridLayerMock, times(1)).addNodeMouseWheelHandler(scenarioGridPanelSpy);
        verify(scrollPanelMock, times(1)).addDomHandler(scenarioGridPanelSpy, ScrollEvent.getType());
    }

    @Test
    public void onNodeMouseWheel() {
        scenarioGridPanelSpy.onNodeMouseWheel(nodeMouseWheelEvent);
        verify(scenarioGridModelMock, times(1)).flushAllTextAreaDOMElementFactoryResources();
    }

    @Test
    public void onResize() {
        scenarioGridPanelSpy.addHandlers(clickHandlerMock, scenarioSimulationGridPanelMouseMoveHandlerMock);
        scenarioGridPanelSpy.onResize();
        verify(scenarioGridLayerMock, times(1)).batch();
        verify(clickHandlerMock, times(1)).hideMenus();
        verify(scenarioSimulationGridPanelMouseMoveHandlerMock, times(1)).hidePopover();
    }

}
