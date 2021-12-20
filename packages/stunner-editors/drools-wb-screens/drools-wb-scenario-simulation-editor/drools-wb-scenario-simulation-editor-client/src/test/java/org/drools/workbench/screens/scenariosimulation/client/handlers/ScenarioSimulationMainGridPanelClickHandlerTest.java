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
import com.google.gwt.event.shared.GwtEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DisableTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.menu.ScenarioContextMenuRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.CLICK_POINT_X;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.CLICK_POINT_Y;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_INDEX;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_WIDTH;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.HEADER_HEIGHT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ROW_INDEX;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.UI_COLUMN_INDEX;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.UI_ROW_INDEX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
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
public class ScenarioSimulationMainGridPanelClickHandlerTest extends AbstractScenarioSimulationGridHandlerTest {

    private ScenarioSimulationMainGridPanelClickHandler scenarioSimulationMainGridPanelClickHandler;

    @Mock
    private EventBus eventBusMock;
    @Mock
    private ScenarioContextMenuRegistry scenarioContextMenuRegistryMock;

    @Before
    public void setUp() {
        super.setUp();
        scenarioSimulationMainGridPanelClickHandler = spy(new ScenarioSimulationMainGridPanelClickHandler() {
            {
                scenarioGrid = scenarioGridMock;
                scenarioContextMenuRegistry = scenarioContextMenuRegistryMock;
                eventBus = eventBusMock;
                rendererHelper = scenarioGridRendererHelperMock;
            }
        });
    }

    @Test
    public void setScenarioGridPanel() {
        scenarioSimulationMainGridPanelClickHandler.setScenarioGridPanel(scenarioGridPanelMock);
        assertEquals(scenarioGridMock, scenarioSimulationMainGridPanelClickHandler.scenarioGrid);
    }

    @Test
    public void setEventBus() {
        scenarioSimulationMainGridPanelClickHandler.setEventBus(eventBusMock);
        assertSame(scenarioSimulationMainGridPanelClickHandler.eventBus, eventBusMock);
    }

    @Test
    public void onContextMenu() {
        doReturn(true).when(scenarioContextMenuRegistryMock).manageRightClick(scenarioGridMock, contextMenuEventMock);
        scenarioSimulationMainGridPanelClickHandler.onContextMenu(contextMenuEventMock);
        verify(contextMenuEventMock, times(1)).preventDefault();
        verify(contextMenuEventMock, times(1)).stopPropagation();
        verify(scenarioContextMenuRegistryMock, times(1)).hideMenus();
    }

    @Test
    public void hideMenus() {
        scenarioSimulationMainGridPanelClickHandler.hideMenus();
        verify(scenarioContextMenuRegistryMock, times(1)).hideMenus();
    }

    @Test
    public void testManageLeftClick() {
        when(point2DMock.getX()).thenReturn(Double.valueOf(CLICK_POINT_X));
        when(point2DMock.getY()).thenReturn(Double.valueOf(CLICK_POINT_Y));
        assertTrue("testManageLeftClick fail", scenarioSimulationMainGridPanelClickHandler.manageCoordinates((int) CLICK_POINT_X,
                                                                                                             (int) CLICK_POINT_Y));
    }

    @Test
    public void testManageLeftClick_ReadOnly() {
        when(informationHeaderMetaDataMock.isReadOnly()).thenReturn(true);
        scenarioSimulationMainGridPanelClickHandler.setEventBus(eventBusMock);
        assertTrue("Click to readonly header cell.",
                   scenarioSimulationMainGridPanelClickHandler.manageCoordinates((int) CLICK_POINT_X,
                                                                                 (int) CLICK_POINT_Y));
        verify(scenarioGridMock, times(1)).setSelectedColumn(anyInt());
        verify(eventBusMock, times(1)).fireEvent(any(EnableTestToolsEvent.class));
    }

    @Test
    public void testManageLeftClick_NextToGrid() {
        assertFalse("Click to point next to table.",
                    scenarioSimulationMainGridPanelClickHandler.manageCoordinates(GRID_WIDTH.intValue() + (int) CLICK_POINT_X,
                                                                                  (int) CLICK_POINT_Y));
        verify(scenarioGridMock, never()).setSelectedColumnAndHeader(anyInt(), anyInt());
        verify(eventBusMock, never()).fireEvent(any(EnableTestToolsEvent.class));
    }

    @Test
    public void testManageLeftClick_BelowHeader() {
        assertFalse("Click to point below header.",
                    scenarioSimulationMainGridPanelClickHandler.manageCoordinates((int) CLICK_POINT_X,
                                                                                  HEADER_HEIGHT.intValue() + (int) CLICK_POINT_Y));
        verify(scenarioGridMock, never()).setSelectedColumnAndHeader(anyInt(), anyInt());
        verify(eventBusMock, never()).fireEvent(any(EnableTestToolsEvent.class));
    }

    @Test
    public void testManageHeaderLeftClick_NoEditableHeader() {
        when(informationHeaderMetaDataMock.isReadOnly()).thenReturn(true);
        assertTrue("NoEditableHeader fail", scenarioSimulationMainGridPanelClickHandler.manageCoordinates((int) CLICK_POINT_X,
                                                                                                          (int) CLICK_POINT_Y));
    }

    @Test
    public void testManageHeaderLeftClick_NullUIHeaderRowIndex() {
        assertFalse("NullUIHeaderRowIndex fail", scenarioSimulationMainGridPanelClickHandler.manageCoordinates((int) CLICK_POINT_X,
                                                                                                               (int) CLICK_POINT_Y + 10));
    }

    @Test
    public void testManageHeaderLeftClick_NullMetadata() {
        when(headerMetaDatasMock.get(anyInt())).thenReturn(null);
        assertFalse("NullMetadata fail", scenarioSimulationMainGridPanelClickHandler.manageCoordinates((int) CLICK_POINT_X,
                                                                                                       (int) CLICK_POINT_Y));
    }

    @Test
    public void testManageHeaderLeftClick_GIVENGroup() {
        commonTestManageHeaderLeftClick_Group("GIVEN", true);
    }

    @Test
    public void testManageHeaderLeftClick_EXPECTGroup() {
        commonTestManageHeaderLeftClick_Group("EXPECT", true);
    }

    @Test
    public void testManageHeaderLeftClick_OTHERGroup() {
        commonTestManageHeaderLeftClick_Group("OTHER", false);
    }

    @Test
    public void testManageGridLeftClickReadOnlyTrue() {
        when(informationHeaderMetaDataMock.isReadOnly()).thenReturn(true);
        scenarioSimulationMainGridPanelClickHandler.setEventBus(eventBusMock);
        when(scenarioGridCellMock.isEditingMode()).thenReturn(false);
        doReturn(null).when(scenarioSimulationMainGridPanelClickHandler).getUiHeaderRowIndexLocal(isA(Point2D.class));
        doReturn(UI_ROW_INDEX).when(scenarioSimulationMainGridPanelClickHandler).getUiRowIndexLocal(anyDouble());
        doReturn(UI_COLUMN_INDEX).when(scenarioSimulationMainGridPanelClickHandler).getUiColumnIndexLocal(anyDouble());
        assertTrue(scenarioSimulationMainGridPanelClickHandler.manageCoordinates((int) CLICK_POINT_X, (int) CLICK_POINT_Y));
        verify(scenarioGridModelMock, times(1)).selectCell(eq(1), eq(0));
    }

    @Test
    public void testManageGridLeftClickReadOnlyFalse() {
        when(scenarioGridMock.startEditingCell(UI_ROW_INDEX, UI_COLUMN_INDEX)).thenReturn(true);
        when(informationHeaderMetaDataMock.isReadOnly()).thenReturn(false);
        scenarioSimulationMainGridPanelClickHandler.setEventBus(eventBusMock);
        when(scenarioGridCellMock.isEditingMode()).thenReturn(false);
        doReturn(null).when(scenarioSimulationMainGridPanelClickHandler).getUiHeaderRowIndexLocal(isA(Point2D.class));
        doReturn(UI_ROW_INDEX).when(scenarioSimulationMainGridPanelClickHandler).getUiRowIndexLocal(anyDouble());
        doReturn(UI_COLUMN_INDEX).when(scenarioSimulationMainGridPanelClickHandler).getUiColumnIndexLocal(anyDouble());
        doReturn(scenarioGridCellMock).when(scenarioGridModelMock).getCell(UI_ROW_INDEX, UI_COLUMN_INDEX);
        assertTrue(scenarioSimulationMainGridPanelClickHandler.manageCoordinates((int) CLICK_POINT_X, (int) CLICK_POINT_Y));
        verify(scenarioGridModelMock, times(1)).selectCell(eq(1), eq(0));
    }

    private void commonTestManageHeaderLeftClick_Group(String group, boolean assertExpected) {
        doReturn(1).when(scenarioSimulationMainGridPanelClickHandler).getUiHeaderRowIndexLocal(point2DMock);
        when(informationHeaderMetaDataMock.isReadOnly()).thenReturn(false);
        when(informationHeaderMetaDataMock.getColumnGroup()).thenReturn(group);
        String message = group + "Group fail";
        if (assertExpected) {
            assertTrue(message, scenarioSimulationMainGridPanelClickHandler.manageCoordinates((int) CLICK_POINT_X, (int) CLICK_POINT_Y));
            verify(scenarioGridMock, times(1)).setSelectedColumn(eq(0));
            verify(eventBusMock, times(1)).fireEvent(isA(GwtEvent.class));
        } else {
            assertFalse(message, scenarioSimulationMainGridPanelClickHandler.manageCoordinates((int) CLICK_POINT_X, (int) CLICK_POINT_Y));
            verify(scenarioGridMock, never()).setSelectedColumn(anyInt());
            verify(eventBusMock, never()).fireEvent(any());
            return;
        }
    }

    @Test
    public void manageBodyCoordinates_NullCell() {
        scenarioSimulationMainGridPanelClickHandler.manageBodyCoordinates(ROW_INDEX, COLUMN_INDEX);
        verify(scenarioGridMock.getModel(), times(1)).getCell(eq(ROW_INDEX), eq(COLUMN_INDEX));
        verify(scenarioGridMock.getModel(), never()).selectCell(anyInt(), anyInt());
        verify(eventBusMock, times(0)).fireEvent(isA(GwtEvent.class));
    }

    @Test
    public void manageBodyCoordinates_WithCell() {
        doReturn(scenarioGridCellMock).when(scenarioGridModelMock).getCell(ROW_INDEX, COLUMN_INDEX);
        scenarioSimulationMainGridPanelClickHandler.manageBodyCoordinates(ROW_INDEX, COLUMN_INDEX);
        verify(scenarioGridMock.getModel(), times(1)).getCell(eq(ROW_INDEX), eq(COLUMN_INDEX));
        verify(scenarioGridMock.getModel(), times(1)).selectCell(ROW_INDEX, COLUMN_INDEX);
        verify(eventBusMock, times(1)).fireEvent(isA(DisableTestToolsEvent.class));
    }
}