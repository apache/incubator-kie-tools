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

import java.util.Set;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.AbstractColumnMenuPresenter;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.AbstractHeaderMenuPresenter;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.ExpectedContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GivenContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderExpectedContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderGivenContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.OtherContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.UnmodifiableColumnGridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableRightPanelEvent;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioSimulationGridPanelClickHandlerTest extends AbstractScenarioSimulationGridPanelClickHandlerTest {

    private ScenarioSimulationGridPanelClickHandler scenarioSimulationGridPanelClickHandler;

    private final int EXPECTED_RELATIVE_X = NATIVE_EVENT_CLIENT_X - TARGET_ABSOLUTE_LEFT + TARGET_SCROLL_LEFT + DOCUMENT_SCROLL_LEFT;
    private final int EXPECTED_RELATIVE_Y = NATIVE_EVENT_CLIENT_Y - TARGET_ABSOLUTE_TOP + TARGET_SCROLL_TOP + DOCUMENT_SCROLL_TOP;

    @Mock
    private OtherContextMenu otherContextMenuMock;
    @Mock
    private HeaderGivenContextMenu headerGivenContextMenuMock;
    @Mock
    private HeaderExpectedContextMenu headerExpectContextMenuMock;
    @Mock
    private GivenContextMenu givenContextMenuMock;
    @Mock
    private ExpectedContextMenu expectContextMenuMock;
    @Mock
    private GridContextMenu gridContextMenuMock;
    @Mock
    private UnmodifiableColumnGridContextMenu nnmodifiableColumnGridContextMenuMock;
    @Mock
    private Set<AbstractHeaderMenuPresenter> managedMenusMock;
    @Mock
    private ScenarioGridColumn gridColumnMock;

    @Mock
    private EventBus eventBusMock;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        scenarioSimulationGridPanelClickHandler = spy(new ScenarioSimulationGridPanelClickHandler() {
            {
                scenarioGrid = scenarioGridMock;
                otherContextMenu = otherContextMenuMock;
                headerGivenContextMenu = headerGivenContextMenuMock;
                headerExpectedContextMenu = headerExpectContextMenuMock;
                givenContextMenu = givenContextMenuMock;
                expectedContextMenu = expectContextMenuMock;
                gridContextMenu = gridContextMenuMock;
                unmodifiableColumnGridContextMenu = nnmodifiableColumnGridContextMenuMock;
                managedMenus.add(otherContextMenuMock);
                managedMenus.add(headerGivenContextMenuMock);
                managedMenus.add(headerExpectContextMenuMock);
                managedMenus.add(givenContextMenuMock);
                managedMenus.add(expectContextMenuMock);
                managedMenus.add(gridContextMenuMock);
                managedMenus.add(nnmodifiableColumnGridContextMenuMock);
                eventBus = eventBusMock;
            }

            @Override
            protected boolean manageRightClick(ContextMenuEvent event) {
                return true;
            }

            @Override
            protected String getExistingInstances(String group, ScenarioGridModel scenarioGridModel) {
                return "test1;test2;test3";
            }

            @Override
            protected String getPropertyName(Simulation simulation, int columnIndex) {
                return "test.name";
            }
        });
        managedMenusMock = spy(scenarioSimulationGridPanelClickHandler.managedMenus);
    }

    @Test
    public void setScenarioGrid() {
        scenarioSimulationGridPanelClickHandler.setScenarioGrid(scenarioGridMock);
        assertEquals(scenarioGridMock, scenarioSimulationGridPanelClickHandler.scenarioGrid);
    }

    @Test
    public void setOtherContextMenu() {
        scenarioSimulationGridPanelClickHandler.setOtherContextMenu(otherContextMenuMock);
        assertEquals(otherContextMenuMock, scenarioSimulationGridPanelClickHandler.otherContextMenu);
    }

    @Test
    public void setHeaderGivenContextMenu() {
        scenarioSimulationGridPanelClickHandler.setHeaderGivenContextMenu(headerGivenContextMenuMock);
        assertEquals(headerGivenContextMenuMock, scenarioSimulationGridPanelClickHandler.headerGivenContextMenu);
    }

    @Test
    public void setHeaderExpectedContextMenu() {
        scenarioSimulationGridPanelClickHandler.setHeaderExpectedContextMenu(headerExpectContextMenuMock);
        assertEquals(headerExpectContextMenuMock, scenarioSimulationGridPanelClickHandler.headerExpectedContextMenu);
    }

    @Test
    public void setGivenContextMenu() {
        scenarioSimulationGridPanelClickHandler.setGivenContextMenu(givenContextMenuMock);
        assertEquals(givenContextMenuMock, scenarioSimulationGridPanelClickHandler.givenContextMenu);
    }

    @Test
    public void setExpectedContextMenu() {
        scenarioSimulationGridPanelClickHandler.setExpectedContextMenu(expectContextMenuMock);
        assertEquals(expectContextMenuMock, scenarioSimulationGridPanelClickHandler.expectedContextMenu);
    }

    @Test
    public void setGridContextMenu() {
        scenarioSimulationGridPanelClickHandler.setGridContextMenu(gridContextMenuMock);
        assertEquals(gridContextMenuMock, scenarioSimulationGridPanelClickHandler.gridContextMenu);
    }

    @Test
    public void setUnmodifiableColumnGridContextMenu() {
        scenarioSimulationGridPanelClickHandler.setUnmodifiableColumnGridContextMenu(nnmodifiableColumnGridContextMenuMock);
        assertEquals(nnmodifiableColumnGridContextMenuMock, scenarioSimulationGridPanelClickHandler.unmodifiableColumnGridContextMenu);
    }

    @Test
    public void setEventBus() {
        scenarioSimulationGridPanelClickHandler.setEventBus(eventBusMock);
        verify(otherContextMenuMock, times(1)).setEventBus(eq(eventBusMock));
        verify(headerGivenContextMenuMock, times(1)).setEventBus(eq(eventBusMock));
        verify(headerExpectContextMenuMock, times(1)).setEventBus(eq(eventBusMock));
        verify(givenContextMenuMock, times(1)).setEventBus(eq(eventBusMock));
        verify(expectContextMenuMock, times(1)).setEventBus(eq(eventBusMock));
        verify(gridContextMenuMock, times(1)).setEventBus(eq(eventBusMock));
        verify(nnmodifiableColumnGridContextMenuMock, times(1)).setEventBus(eq(eventBusMock));
    }

    @Test
    public void getRelativeX() {
        int retrieved = scenarioSimulationGridPanelClickHandler.getRelativeX(contextMenuEventMock);
        assertEquals(EXPECTED_RELATIVE_X, retrieved);
    }

    @Test
    public void getRelativeY() {
        int retrieved = scenarioSimulationGridPanelClickHandler.getRelativeY(contextMenuEventMock);
        assertEquals(EXPECTED_RELATIVE_Y, retrieved);
    }

    @Test
    public void commonClickManagement() {
        scenarioSimulationGridPanelClickHandler.hideMenus();
        verify(otherContextMenuMock, times(1)).hide();
        verify(headerGivenContextMenuMock, times(1)).hide();
        verify(headerExpectContextMenuMock, times(1)).hide();
        verify(givenContextMenuMock, times(1)).hide();
        verify(expectContextMenuMock, times(1)).hide();
        verify(gridContextMenuMock, times(1)).hide();
        verify(nnmodifiableColumnGridContextMenuMock, times(1)).hide();
    }

    @Test
    public void onContextMenu() {
        scenarioSimulationGridPanelClickHandler.onContextMenu(contextMenuEventMock);
        verify(contextMenuEventMock, times(1)).preventDefault();
        verify(contextMenuEventMock, times(1)).stopPropagation();
        commonCheck();
    }

    @Test
    public void testManageLeftClick() {
        doReturn(true).when(scenarioSimulationGridPanelClickHandler).hasEditableHeaderLocal(any());
        doReturn(headerMetaDataMock).when(scenarioSimulationGridPanelClickHandler).getColumnScenarioHeaderMetaDataLocal(scenarioGridMock, scenarioGridColumnMock, CLICK_POINT_Y);
        doReturn(1).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(scenarioGridMock, scenarioGridColumnMock, CLICK_POINT_Y);
        doReturn(true).when(scenarioSimulationGridPanelClickHandler).manageHeaderLeftClick(eq(scenarioGridMock), anyInt(), eq(scenarioGridColumnMock), any());
        assertTrue("testManageLeftClick fail",scenarioSimulationGridPanelClickHandler.manageLeftClick(CLICK_POINT_X,
                                                                CLICK_POINT_Y,
                                                                SHIFT_PRESSED,
                                                                CTRL_PRESSED));
    }

    @Test
    public void testManageLeftClick_ReadOnly() {
        when(headerMetaDataMock.isReadOnly()).thenReturn(true);

        scenarioSimulationGridPanelClickHandler.setEventBus(eventBusMock);
        assertFalse("Click to readonly header cell.",
                    scenarioSimulationGridPanelClickHandler.manageLeftClick(CLICK_POINT_X,
                                                                            CLICK_POINT_Y,
                                                                            SHIFT_PRESSED,
                                                                            CTRL_PRESSED));
        verify(scenarioGridMock, never()).setSelectedColumnAndHeader(anyInt(), anyInt());
        verify(eventBusMock, never()).fireEvent(any(EnableRightPanelEvent.class));
    }

    @Test
    public void testManageLeftClick_NextToGrid() {
        assertFalse("Click to point next to table.",
                    scenarioSimulationGridPanelClickHandler.manageLeftClick(GRID_WIDTH.intValue() + CLICK_POINT_X,
                                                                            CLICK_POINT_Y,
                                                                            SHIFT_PRESSED,
                                                                            CTRL_PRESSED));
        verify(scenarioGridMock, never()).setSelectedColumnAndHeader(anyInt(), anyInt());
        verify(eventBusMock, never()).fireEvent(any(EnableRightPanelEvent.class));
    }

    @Test
    public void testManageLeftClick_BelowHeader() {
        assertFalse("Click to point below header.",
                    scenarioSimulationGridPanelClickHandler.manageLeftClick(CLICK_POINT_X,
                                                                            HEADER_HEIGHT.intValue() + CLICK_POINT_X,
                                                                            SHIFT_PRESSED,
                                                                            CTRL_PRESSED));
        verify(scenarioGridMock, never()).setSelectedColumnAndHeader(anyInt(), anyInt());
        verify(eventBusMock, never()).fireEvent(any(EnableRightPanelEvent.class));
    }

    @Test
    public void testManageHeaderLeftClick_NoEditableHeader() {
        doReturn(false).when(scenarioSimulationGridPanelClickHandler).hasEditableHeaderLocal(scenarioGridColumnMock);
        assertFalse("NoEditableHeader fail", scenarioSimulationGridPanelClickHandler.manageHeaderLeftClick(scenarioGridMock, 1, scenarioGridColumnMock, point2DMock));
        verify(scenarioSimulationGridPanelClickHandler, never()).getUiHeaderRowIndexLocal(eq(scenarioGridMock), eq(scenarioGridColumnMock), anyDouble());
    }

    @Test
    public void testManageHeaderLeftClick_NullUIHeaderRowIndex() {
        double GRID_Y = 10.0;
        when(point2DMock.getY()).thenReturn(GRID_Y);
        doReturn(true).when(scenarioSimulationGridPanelClickHandler).hasEditableHeaderLocal(scenarioGridColumnMock);
        doReturn(null).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(scenarioGridMock, scenarioGridColumnMock, GRID_Y);
        assertFalse("NullUIHeaderRowIndex fail", scenarioSimulationGridPanelClickHandler.manageHeaderLeftClick(scenarioGridMock, 1, scenarioGridColumnMock, point2DMock));
        verify(scenarioSimulationGridPanelClickHandler, times(1)).getUiHeaderRowIndexLocal(eq(scenarioGridMock), eq(scenarioGridColumnMock), anyDouble());
        verify(scenarioSimulationGridPanelClickHandler, never()).isEditableHeaderLocal(eq(scenarioGridColumnMock), anyInt());
    }

    @Test
    public void testManageHeaderLeftClick_NoIsEditableHeader() {
        double GRID_Y = 10.0;
        when(point2DMock.getY()).thenReturn(GRID_Y);
        doReturn(true).when(scenarioSimulationGridPanelClickHandler).hasEditableHeaderLocal(scenarioGridColumnMock);
        doReturn(1).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(scenarioGridMock, scenarioGridColumnMock, GRID_Y);
        doReturn(false).when(scenarioSimulationGridPanelClickHandler).isEditableHeaderLocal(scenarioGridColumnMock, 1);
        assertFalse("NoIsEditableHeader fail", scenarioSimulationGridPanelClickHandler.manageHeaderLeftClick(scenarioGridMock, 1, scenarioGridColumnMock, point2DMock));
        verify(scenarioSimulationGridPanelClickHandler, times(1)).isEditableHeaderLocal(eq(scenarioGridColumnMock), anyInt());
        verify(scenarioSimulationGridPanelClickHandler, never()).getColumnScenarioHeaderMetaDataLocal(eq(scenarioGridMock), eq(scenarioGridColumnMock), anyDouble());
    }

    @Test
    public void testManageHeaderLeftClick_NullMetadata() {
        double GRID_Y = 10.0;
        when(point2DMock.getY()).thenReturn(GRID_Y);
        doReturn(true).when(scenarioSimulationGridPanelClickHandler).hasEditableHeaderLocal(scenarioGridColumnMock);
        doReturn(1).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(scenarioGridMock, scenarioGridColumnMock, GRID_Y);
        doReturn(true).when(scenarioSimulationGridPanelClickHandler).isEditableHeaderLocal(scenarioGridColumnMock, 1);
        doReturn(null).when(scenarioSimulationGridPanelClickHandler).getColumnScenarioHeaderMetaDataLocal(scenarioGridMock, scenarioGridColumnMock, GRID_Y);
        assertFalse("NullMetadata fail", scenarioSimulationGridPanelClickHandler.manageHeaderLeftClick(scenarioGridMock, 1, scenarioGridColumnMock, point2DMock));
        verify(scenarioSimulationGridPanelClickHandler, times(1)).getColumnScenarioHeaderMetaDataLocal(eq(scenarioGridMock), eq(scenarioGridColumnMock), anyDouble());
        verify(headerMetaDataMock, never()).getColumnGroup();
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
        when(headerMetaDataMock.isReadOnly()).thenReturn(true);
        scenarioSimulationGridPanelClickHandler.setEventBus(eventBusMock);
        when(scenarioGridCellMock.isEditing()).thenReturn(true);
        boolean retrieved = scenarioSimulationGridPanelClickHandler.manageGridLeftClick(scenarioGridMock, UI_ROW_INDEX, UI_COLUMN_INDEX, gridColumnMock);
        verify(scenarioGridCellMock, never()).setEditing(anyBoolean());
        assertTrue(retrieved);
        when(scenarioGridCellMock.isEditing()).thenReturn(false);
        scenarioSimulationGridPanelClickHandler.manageGridLeftClick(scenarioGridMock, UI_ROW_INDEX, UI_COLUMN_INDEX, gridColumnMock);
        verify(scenarioGridCellMock, times(1)).setEditing(eq(false));
        verify(gridColumnMock, times(1)).isReadOnly();
    }

    @Test
    public void testManageGridLeftClickReadOnlyFalse() {
        when(scenarioGridMock.startEditingCell(UI_ROW_INDEX, UI_COLUMN_INDEX)).thenReturn(true);
        when(headerMetaDataMock.isReadOnly()).thenReturn(false);
        scenarioSimulationGridPanelClickHandler.setEventBus(eventBusMock);
        when(scenarioGridCellMock.isEditing()).thenReturn(true);
        boolean retrieved = scenarioSimulationGridPanelClickHandler.manageGridLeftClick(scenarioGridMock, UI_ROW_INDEX, UI_COLUMN_INDEX, gridColumnMock);
        assertTrue(retrieved);
        verify(scenarioGridCellMock, never()).setEditing(anyBoolean());
        verify(gridColumnMock, never()).isReadOnly();
        when(scenarioGridCellMock.isEditing()).thenReturn(false);
        scenarioSimulationGridPanelClickHandler.manageGridLeftClick(scenarioGridMock, UI_ROW_INDEX, UI_COLUMN_INDEX, gridColumnMock);
        verify(scenarioGridCellMock, times(1)).setEditing(eq(true));
        verify(gridColumnMock, times(1)).isReadOnly();
    }

    @Test
    public void testManageHeaderRightClick_NullMetadata() {
        int GRID_Y = 10;
        doReturn(scenarioGridColumnMock).when(columnsMock).get(1);
        doReturn(null).when(scenarioSimulationGridPanelClickHandler).getColumnScenarioHeaderMetaDataLocal(scenarioGridMock, scenarioGridColumnMock, GRID_Y);
        assertFalse(scenarioSimulationGridPanelClickHandler.manageHeaderRightClick(scenarioGridMock, 10, 10, GRID_Y, 1));
    }

    @Test
    public void testManageHeaderRightClick_NullUIHeaderRowIndex() {
        int GRID_Y = 10;
        doReturn(scenarioGridColumnMock).when(columnsMock).get(1);
        doReturn(null).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(scenarioGridMock, scenarioGridColumnMock, GRID_Y);
        assertFalse(scenarioSimulationGridPanelClickHandler.manageHeaderRightClick(scenarioGridMock, 10, 10, GRID_Y, 1));
    }

    @Test
    public void testManageHeaderRightClick_NOGroupGIVENTitle() {
        commonTestManageHeaderRightClick_NOGroupTitle("GIVEN", headerGivenContextMenuMock);
    }

    @Test
    public void testManageHeaderRightClick_NOGroupHEADERTitle() {
        commonTestManageHeaderRightClick_NOGroupTitle("EXPECT", headerExpectContextMenuMock);
    }

    @Test
    public void testManageHeaderRightClick_NOGroupOTHERTitle() {
        commonTestManageHeaderRightClick_NOGroupTitle("OTHER", otherContextMenuMock);
    }

    @Test
    public void testManageHeaderRightClick_GIVENGroup() {
        commonTestManageHeaderRightClick_Group("GIVEN", givenContextMenuMock);
    }

    @Test
    public void testManageHeaderRightClick_EXPECTGroup() {
        commonTestManageHeaderRightClick_Group("EXPECT", expectContextMenuMock);
    }

    @Test
    public void testManageHeaderRightClick_OTHERGroup() {
        int GRID_Y = 10;
        doReturn(scenarioGridColumnMock).when(columnsMock).get(1);
        doReturn(headerMetaDataMock).when(scenarioSimulationGridPanelClickHandler).getColumnScenarioHeaderMetaDataLocal(scenarioGridMock, scenarioGridColumnMock, GRID_Y);
        doReturn(1).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(scenarioGridMock, scenarioGridColumnMock, GRID_Y);
        when(headerMetaDataMock.getColumnGroup()).thenReturn("OTHER");
        scenarioSimulationGridPanelClickHandler.manageHeaderRightClick(scenarioGridMock, 10, 10, GRID_Y, 1);
        verify(otherContextMenuMock, times(1)).show(eq(10), eq(10));
        reset(otherContextMenuMock);
        when(headerMetaDataMock.getColumnGroup()).thenReturn("OTHER-SOMETHING");
        scenarioSimulationGridPanelClickHandler.manageHeaderRightClick(scenarioGridMock, 10, 10, GRID_Y, 1);
        verify(otherContextMenuMock, times(1)).show(eq(10), eq(10));
    }

    private void commontTestManageHeaderLeftClick_Group(String group, boolean assertExpected) {
        double GRID_Y = 10.0;
        when(point2DMock.getY()).thenReturn(GRID_Y);
        doReturn(true).when(scenarioSimulationGridPanelClickHandler).hasEditableHeaderLocal(scenarioGridColumnMock);
        doReturn(1).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(scenarioGridMock, scenarioGridColumnMock, GRID_Y);
        doReturn(true).when(scenarioSimulationGridPanelClickHandler).isEditableHeaderLocal(scenarioGridColumnMock, 1);
        doReturn(headerMetaDataMock).when(scenarioSimulationGridPanelClickHandler).getColumnScenarioHeaderMetaDataLocal(scenarioGridMock, scenarioGridColumnMock, GRID_Y);
        when(headerMetaDataMock.getColumnGroup()).thenReturn(group);
        String message = group + "Group fail";
        if (assertExpected) {
            assertTrue(message, scenarioSimulationGridPanelClickHandler.manageHeaderLeftClick(scenarioGridMock, 1, scenarioGridColumnMock, point2DMock));
            verify(scenarioSimulationGridPanelClickHandler, times(1)).manageGivenExpectGridLeftClick(eq(headerMetaDataMock), eq(scenarioGridColumnMock), eq(group), anyInt(), anyInt(), eq(point2DMock));
        } else {
            assertFalse(message, scenarioSimulationGridPanelClickHandler.manageHeaderLeftClick(scenarioGridMock, 1, scenarioGridColumnMock, point2DMock));
            verify(scenarioSimulationGridPanelClickHandler, never()).manageGivenExpectGridLeftClick(eq(headerMetaDataMock), eq(scenarioGridColumnMock), eq(group), anyInt(), anyInt(), eq(point2DMock));
            return;
        }
        verify(scenarioSimulationGridPanelClickHandler, times(1)).manageGivenExpectGridLeftClick(eq(headerMetaDataMock), eq(scenarioGridColumnMock), eq(group), anyInt(), anyInt(), eq(point2DMock));
        reset(scenarioSimulationGridPanelClickHandler);
        doReturn(true).when(scenarioSimulationGridPanelClickHandler).hasEditableHeaderLocal(scenarioGridColumnMock);
        doReturn(1).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(scenarioGridMock, scenarioGridColumnMock, GRID_Y);
        doReturn(true).when(scenarioSimulationGridPanelClickHandler).isEditableHeaderLocal(scenarioGridColumnMock, 1);
        doReturn(headerMetaDataMock).when(scenarioSimulationGridPanelClickHandler).getColumnScenarioHeaderMetaDataLocal(scenarioGridMock, scenarioGridColumnMock, GRID_Y);
        when(headerMetaDataMock.getColumnGroup()).thenReturn(group + "-SOMETHING");
        assertTrue(message, scenarioSimulationGridPanelClickHandler.manageHeaderLeftClick(scenarioGridMock, 1, scenarioGridColumnMock, point2DMock));
        verify(scenarioSimulationGridPanelClickHandler, times(1)).manageGivenExpectGridLeftClick(eq(headerMetaDataMock), eq(scenarioGridColumnMock), eq(group), anyInt(), anyInt(), eq(point2DMock));
    }

    private void commonTestManageHeaderRightClick_NOGroupTitle(String group, AbstractHeaderMenuPresenter menuMock) {
        int GRID_Y = 10;
        doReturn(scenarioGridColumnMock).when(columnsMock).get(1);
        doReturn(headerMetaDataMock).when(scenarioSimulationGridPanelClickHandler).getColumnScenarioHeaderMetaDataLocal(scenarioGridMock, scenarioGridColumnMock, GRID_Y);
        doReturn(1).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(scenarioGridMock, scenarioGridColumnMock, GRID_Y);
        when(headerMetaDataMock.getColumnGroup()).thenReturn("");
        when(headerMetaDataMock.getTitle()).thenReturn(group);
        scenarioSimulationGridPanelClickHandler.manageHeaderRightClick(scenarioGridMock, 10, 10, GRID_Y, 1);
        verify(menuMock, times(1)).show(eq(10), eq(10));
    }

    private void commonTestManageHeaderRightClick_Group(String group, AbstractColumnMenuPresenter menuMock) {
        int GRID_Y = 10;
        doReturn(scenarioGridColumnMock).when(columnsMock).get(1);
        doReturn(headerMetaDataMock).when(scenarioSimulationGridPanelClickHandler).getColumnScenarioHeaderMetaDataLocal(scenarioGridMock, scenarioGridColumnMock, GRID_Y);
        doReturn(1).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(scenarioGridMock, scenarioGridColumnMock, GRID_Y);
        when(headerMetaDataMock.getColumnGroup()).thenReturn(group);
        scenarioSimulationGridPanelClickHandler.manageHeaderRightClick(scenarioGridMock, 10, 10, GRID_Y, 1);
        verify(menuMock, times(1)).show(eq(10), eq(10), eq(1), eq(group), anyBoolean());
        reset(menuMock);
        when(headerMetaDataMock.getColumnGroup()).thenReturn(group + "-SOMETHING");
        scenarioSimulationGridPanelClickHandler.manageHeaderRightClick(scenarioGridMock, 10, 10, GRID_Y, 1);
        verify(menuMock, times(1)).show(eq(10), eq(10), eq(1), eq(group), anyBoolean());
    }

    private void commonCheck() {
        verify(scenarioSimulationGridPanelClickHandler, times(1)).hideMenus();
    }
}