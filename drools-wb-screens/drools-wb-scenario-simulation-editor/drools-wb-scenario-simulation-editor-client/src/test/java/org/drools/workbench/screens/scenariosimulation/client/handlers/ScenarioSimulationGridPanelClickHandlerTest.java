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

package org.drools.workbench.screens.scenariosimulation.client.handlers;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationGridPanelClickHandlerTest {

    private ScenarioSimulationGridPanelClickHandler scenarioSimulationGridPanelClickHandler;

    @Mock
    private ScenarioGrid mockScenarioGrid;

    @Mock
    private GridContextMenu mockGridContextMenu;

    @Mock
    private HeaderContextMenu mockHeaderContextMenu;

    @Mock
    private Element mockTarget;

    @Mock
    private NativeEvent mockNativeEvent;

    @Mock
    private Document mockDocument;

    @Mock
    private ContextMenuEvent mockContextMenuEvent;

    @Before
    public void setUp() throws Exception {

        scenarioSimulationGridPanelClickHandler = spy(new ScenarioSimulationGridPanelClickHandler(mockScenarioGrid,
                                                                                                  mockGridContextMenu,
                                                                                                  mockHeaderContextMenu) {
            @Override
            protected void manageRightClick(ContextMenuEvent event) {
                // 
            }
        });

        when(mockNativeEvent.getClientX()).thenReturn(100);
        when(mockNativeEvent.getClientY()).thenReturn(100);

        when(mockTarget.getOwnerDocument()).thenReturn(mockDocument);
        when(mockTarget.getAbsoluteLeft()).thenReturn(50);
        when(mockTarget.getScrollLeft()).thenReturn(20);
        when(mockTarget.getAbsoluteTop()).thenReturn(50);
        when(mockTarget.getScrollTop()).thenReturn(20);

        when(mockDocument.getScrollLeft()).thenReturn(10);
        when(mockDocument.getScrollTop()).thenReturn(10);

        when(mockContextMenuEvent.getNativeEvent()).thenReturn(mockNativeEvent);
        when(mockContextMenuEvent.getRelativeElement()).thenReturn(mockTarget);
    }

    @Test
    public void getRelativeX() {
        int retrieved = scenarioSimulationGridPanelClickHandler.getRelativeX(mockContextMenuEvent);
        assertEquals(80, retrieved);
    }

    @Test
    public void getRelativeY() {
        int retrieved = scenarioSimulationGridPanelClickHandler.getRelativeY(mockContextMenuEvent);
        assertEquals(80, retrieved);
    }

    @Test
    public void onClick() {
        ClickEvent mockEvent = mock(ClickEvent.class);
        when(mockEvent.getNativeEvent()).thenReturn(mockNativeEvent);
        when(mockEvent.getRelativeElement()).thenReturn(mockTarget);
        scenarioSimulationGridPanelClickHandler.onClick(mockEvent);
        commonCheck();
    }

    @Test
    public void onContextMenu() {
        scenarioSimulationGridPanelClickHandler.onContextMenu(mockContextMenuEvent);
        verify(mockContextMenuEvent, times(1)).preventDefault();
        verify(mockContextMenuEvent, times(1)).stopPropagation();
        commonCheck();
    }

    private void commonCheck() {
        verify(mockGridContextMenu, times(1)).hide();
        verify(mockHeaderContextMenu, times(1)).hide();
        reset(mockGridContextMenu);
        reset(mockHeaderContextMenu);
    }
}