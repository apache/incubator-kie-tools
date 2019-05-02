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

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.menu.ScenarioContextMenuRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.CLICK_POINT_X;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.CLICK_POINT_Y;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_WIDTH;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.HEADER_HEIGHT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.UI_COLUMN_INDEX;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.UI_ROW_INDEX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioSimulationGridPanelClickHandlerTest extends AbstractScenarioSimulationGridHandlerTest {

    private ScenarioSimulationGridPanelClickHandler scenarioSimulationGridPanelClickHandler;

    @Mock
    private EventBus eventBusMock;
    @Mock
    private ScenarioContextMenuRegistry scenarioContextMenuRegistryMock;

    @Before
    public void setUp() {
        super.setUp();
        scenarioSimulationGridPanelClickHandler = spy(new ScenarioSimulationGridPanelClickHandler() {
            {
                scenarioGrid = scenarioGridMock;
                scenarioContextMenuRegistry = scenarioContextMenuRegistryMock;
                eventBus = eventBusMock;
                rendererHelper = scenarioGridRendererHelperMock;
            }
        });
    }

    @Test
    public void setScenarioGrid() {
        scenarioSimulationGridPanelClickHandler.setScenarioGrid(scenarioGridMock);
        assertEquals(scenarioGridMock, scenarioSimulationGridPanelClickHandler.scenarioGrid);
    }

    @Test
    public void setEventBus() {
        scenarioSimulationGridPanelClickHandler.setEventBus(eventBusMock);
        verify(scenarioContextMenuRegistryMock, times(1)).setEventBus(eq(eventBusMock));
    }

    @Test
    public void onContextMenu() {
        doReturn(true).when(scenarioContextMenuRegistryMock).manageRightClick(scenarioGridMock, contextMenuEventMock);
        scenarioSimulationGridPanelClickHandler.onContextMenu(contextMenuEventMock);
        verify(contextMenuEventMock, times(1)).preventDefault();
        verify(contextMenuEventMock, times(1)).stopPropagation();
        verify(scenarioContextMenuRegistryMock, times(1)).hideMenus();
    }

    @Test
    public void hideMenus() {
        scenarioSimulationGridPanelClickHandler.hideMenus();
        verify(scenarioContextMenuRegistryMock, times(1)).hideMenus();
    }

    @Test
    public void testManageLeftClick() {
        when(point2DMock.getX()).thenReturn(Double.valueOf(CLICK_POINT_X));
        when(point2DMock.getY()).thenReturn(Double.valueOf(CLICK_POINT_Y));
        assertTrue("testManageLeftClick fail", scenarioSimulationGridPanelClickHandler.manageLeftClick((int) CLICK_POINT_X,
                                                                                                       (int) CLICK_POINT_Y));
    }

    @Test
    public void testManageLeftClick_ReadOnly() {
        when(informationHeaderMetaDataMock.isReadOnly()).thenReturn(true);
        scenarioSimulationGridPanelClickHandler.setEventBus(eventBusMock);
        assertTrue("Click to readonly header cell.",
                   scenarioSimulationGridPanelClickHandler.manageLeftClick((int) CLICK_POINT_X,
                                                                           (int) CLICK_POINT_Y));
        verify(scenarioGridMock, times(1)).setSelectedColumnAndHeader(anyInt(), anyInt());
        verify(eventBusMock, times(1)).fireEvent(any(EnableTestToolsEvent.class));
    }

    @Test
    public void testManageLeftClick_NextToGrid() {
        assertFalse("Click to point next to table.",
                    scenarioSimulationGridPanelClickHandler.manageLeftClick(GRID_WIDTH.intValue() + (int) CLICK_POINT_X,
                                                                            (int) CLICK_POINT_Y));
        verify(scenarioGridMock, never()).setSelectedColumnAndHeader(anyInt(), anyInt());
        verify(eventBusMock, never()).fireEvent(any(EnableTestToolsEvent.class));
    }

    @Test
    public void testManageLeftClick_BelowHeader() {
        assertFalse("Click to point below header.",
                    scenarioSimulationGridPanelClickHandler.manageLeftClick((int) CLICK_POINT_X,
                                                                            HEADER_HEIGHT.intValue() + (int) CLICK_POINT_Y));
        verify(scenarioGridMock, never()).setSelectedColumnAndHeader(anyInt(), anyInt());
        verify(eventBusMock, never()).fireEvent(any(EnableTestToolsEvent.class));
    }

    @Test
    public void testManageHeaderLeftClick_NoEditableHeader() {
        when(informationHeaderMetaDataMock.isReadOnly()).thenReturn(true);
        assertTrue("NoEditableHeader fail", scenarioSimulationGridPanelClickHandler.manageLeftClick((int) CLICK_POINT_X,
                                                                                                    (int) CLICK_POINT_Y));
    }

    @Test
    public void testManageHeaderLeftClick_NullUIHeaderRowIndex() {
        assertFalse("NullUIHeaderRowIndex fail", scenarioSimulationGridPanelClickHandler.manageLeftClick((int) CLICK_POINT_X,
                                                                                                         (int) CLICK_POINT_Y + 10));
    }

    @Test
    public void testManageHeaderLeftClick_NullMetadata() {
        when(headerMetaDatasMock.get(anyInt())).thenReturn(null);
        assertFalse("NullMetadata fail", scenarioSimulationGridPanelClickHandler.manageLeftClick((int) CLICK_POINT_X,
                                                                                                 (int) CLICK_POINT_Y));
    }

    @Test
    public void testManageHeaderLeftClick_GIVENGroup() {
        commontTestManageHeaderLeftClick_Group("GIVEN", true);
    }

    @Test
    public void testManageHeaderLeftClick_EXPECTGroup() {
        commontTestManageHeaderLeftClick_Group("EXPECT", true);
    }

    @Test
    public void testManageHeaderLeftClick_OTHERGroup() {
        commontTestManageHeaderLeftClick_Group("OTHER", false);
    }

    @Test
    public void testManageGridLeftClickReadOnlyTrue() {
        when(informationHeaderMetaDataMock.isReadOnly()).thenReturn(true);
        scenarioSimulationGridPanelClickHandler.setEventBus(eventBusMock);
        when(scenarioGridCellMock.isEditingMode()).thenReturn(false);
        doReturn(null).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(isA(Point2D.class));
        doReturn(UI_ROW_INDEX).when(scenarioSimulationGridPanelClickHandler).getUiRowIndexLocal(anyDouble());
        doReturn(UI_COLUMN_INDEX).when(scenarioSimulationGridPanelClickHandler).getUiColumnIndexLocal(anyDouble());
        assertTrue(scenarioSimulationGridPanelClickHandler.manageLeftClick((int) CLICK_POINT_X, (int) CLICK_POINT_Y));
        verify(scenarioGridModelMock, times(1)).selectCell(eq(1), eq(0));
    }

    @Test
    public void testManageGridLeftClickReadOnlyFalse() {
        when(scenarioGridMock.startEditingCell(UI_ROW_INDEX, UI_COLUMN_INDEX)).thenReturn(true);
        when(informationHeaderMetaDataMock.isReadOnly()).thenReturn(false);
        scenarioSimulationGridPanelClickHandler.setEventBus(eventBusMock);
        when(scenarioGridCellMock.isEditingMode()).thenReturn(false);
        doReturn(null).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(isA(Point2D.class));
        doReturn(UI_ROW_INDEX).when(scenarioSimulationGridPanelClickHandler).getUiRowIndexLocal(anyDouble());
        doReturn(UI_COLUMN_INDEX).when(scenarioSimulationGridPanelClickHandler).getUiColumnIndexLocal(anyDouble());
        doReturn(scenarioGridCellMock).when(scenarioGridModelMock).getCell(UI_ROW_INDEX, UI_COLUMN_INDEX);
        assertTrue(scenarioSimulationGridPanelClickHandler.manageLeftClick((int) CLICK_POINT_X, (int) CLICK_POINT_Y));
        verify(scenarioGridModelMock, times(1)).selectCell(eq(1), eq(0));
    }

    private void commontTestManageHeaderLeftClick_Group(String group, boolean assertExpected) {
        doReturn(1).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(point2DMock);
        when(informationHeaderMetaDataMock.isReadOnly()).thenReturn(false);
        when(informationHeaderMetaDataMock.getColumnGroup()).thenReturn(group);
        String message = group + "Group fail";
        if (assertExpected) {
            assertTrue(message, scenarioSimulationGridPanelClickHandler.manageLeftClick((int) CLICK_POINT_X, (int) CLICK_POINT_Y));
            verify(scenarioGridMock, times(1)).setSelectedColumnAndHeader(eq(0), eq(0));
            verify(eventBusMock, times(1)).fireEvent(isA(EnableTestToolsEvent.class));
        } else {
            assertFalse(message, scenarioSimulationGridPanelClickHandler.manageLeftClick((int) CLICK_POINT_X, (int) CLICK_POINT_Y));
            verify(scenarioGridMock, never()).setSelectedColumnAndHeader(eq(0), eq(0));
            verify(eventBusMock, never()).fireEvent(any());
            return;
        }
    }
}