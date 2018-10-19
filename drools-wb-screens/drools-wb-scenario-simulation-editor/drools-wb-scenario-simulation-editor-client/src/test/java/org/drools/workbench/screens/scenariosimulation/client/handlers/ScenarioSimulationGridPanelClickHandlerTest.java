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

import com.ait.lienzo.client.core.types.Point2D;
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
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioSimulationGridPanelClickHandlerTest extends AbstractScenarioSimulationGridHandlerTest {

    private ScenarioSimulationGridPanelClickHandler scenarioSimulationGridPanelClickHandler;

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
                rendererHelper = scenarioGridRendererHelperMock;
            }

            @Override
            protected boolean manageRightClick(ContextMenuEvent event) {
                return true;
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
        when(point2DMock.getX()).thenReturn(Double.valueOf(CLICK_POINT_X));
        when(point2DMock.getY()).thenReturn(Double.valueOf(CLICK_POINT_Y));
        doReturn(true).when(scenarioSimulationGridPanelClickHandler).hasEditableHeaderLocal(any());
        doReturn(headerMetaDataMock).when(scenarioSimulationGridPanelClickHandler).getColumnScenarioHeaderMetaDataLocal(scenarioGridMock, point2DMock);
        doReturn(1).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(scenarioGridMock, point2DMock);
        doReturn(true).when(scenarioSimulationGridPanelClickHandler).manageHeaderLeftClick(anyInt(), eq(scenarioGridColumnMock), any());
        assertTrue("testManageLeftClick fail", scenarioSimulationGridPanelClickHandler.manageLeftClick((int) CLICK_POINT_X,
                                                                                                       (int) CLICK_POINT_Y));
    }

    @Test
    public void testManageLeftClick_ReadOnly() {
        when(headerMetaDataMock.isReadOnly()).thenReturn(true);

        scenarioSimulationGridPanelClickHandler.setEventBus(eventBusMock);
        assertFalse("Click to readonly header cell.",
                    scenarioSimulationGridPanelClickHandler.manageLeftClick((int) CLICK_POINT_X,
                                                                            (int) CLICK_POINT_Y));
        verify(scenarioGridMock, never()).setSelectedColumnAndHeader(anyInt(), anyInt());
        verify(eventBusMock, never()).fireEvent(any(EnableRightPanelEvent.class));
    }

    @Test
    public void testManageLeftClick_NextToGrid() {
        assertFalse("Click to point next to table.",
                    scenarioSimulationGridPanelClickHandler.manageLeftClick(GRID_WIDTH.intValue() + (int) CLICK_POINT_X,
                                                                            (int) CLICK_POINT_Y));
        verify(scenarioGridMock, never()).setSelectedColumnAndHeader(anyInt(), anyInt());
        verify(eventBusMock, never()).fireEvent(any(EnableRightPanelEvent.class));
    }

    @Test
    public void testManageLeftClick_BelowHeader() {
        assertFalse("Click to point below header.",
                    scenarioSimulationGridPanelClickHandler.manageLeftClick((int) CLICK_POINT_X,
                                                                            HEADER_HEIGHT.intValue() + (int) CLICK_POINT_Y));
        verify(scenarioGridMock, never()).setSelectedColumnAndHeader(anyInt(), anyInt());
        verify(eventBusMock, never()).fireEvent(any(EnableRightPanelEvent.class));
    }

    @Test
    public void testManageHeaderLeftClick_NoEditableHeader() {
        doReturn(false).when(scenarioSimulationGridPanelClickHandler).hasEditableHeaderLocal(scenarioGridColumnMock);
        assertFalse("NoEditableHeader fail", scenarioSimulationGridPanelClickHandler.manageHeaderLeftClick(1, scenarioGridColumnMock, point2DMock));
        verify(scenarioSimulationGridPanelClickHandler, never()).getUiHeaderRowIndexLocal(eq(scenarioGridMock), any(Point2D.class));
    }

    @Test
    public void testManageHeaderLeftClick_NullUIHeaderRowIndex() {
        doReturn(true).when(scenarioSimulationGridPanelClickHandler).hasEditableHeaderLocal(scenarioGridColumnMock);
        doReturn(null).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(scenarioGridMock, point2DMock);
        assertFalse("NullUIHeaderRowIndex fail", scenarioSimulationGridPanelClickHandler.manageHeaderLeftClick(1, scenarioGridColumnMock, point2DMock));
        verify(scenarioSimulationGridPanelClickHandler, times(1)).getUiHeaderRowIndexLocal(eq(scenarioGridMock), any(Point2D.class));
        verify(scenarioSimulationGridPanelClickHandler, never()).isEditableHeaderLocal(eq(scenarioGridColumnMock), anyInt());
    }

    @Test
    public void testManageHeaderLeftClick_NoIsEditableHeader() {
        doReturn(true).when(scenarioSimulationGridPanelClickHandler).hasEditableHeaderLocal(scenarioGridColumnMock);
        doReturn(1).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(scenarioGridMock, point2DMock);
        doReturn(false).when(scenarioSimulationGridPanelClickHandler).isEditableHeaderLocal(scenarioGridColumnMock, 1);
        assertFalse("NoIsEditableHeader fail", scenarioSimulationGridPanelClickHandler.manageHeaderLeftClick(1, scenarioGridColumnMock, point2DMock));
        verify(scenarioSimulationGridPanelClickHandler, times(1)).isEditableHeaderLocal(eq(scenarioGridColumnMock), anyInt());
        verify(scenarioSimulationGridPanelClickHandler, never()).getColumnScenarioHeaderMetaDataLocal(eq(scenarioGridMock), any(Point2D.class));
    }

    @Test
    public void testManageHeaderLeftClick_NullMetadata() {
        doReturn(true).when(scenarioSimulationGridPanelClickHandler).hasEditableHeaderLocal(scenarioGridColumnMock);
        doReturn(1).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(scenarioGridMock, point2DMock);
        doReturn(true).when(scenarioSimulationGridPanelClickHandler).isEditableHeaderLocal(scenarioGridColumnMock, 1);
        doReturn(null).when(scenarioSimulationGridPanelClickHandler).getColumnScenarioHeaderMetaDataLocal(scenarioGridMock, point2DMock);
        assertFalse("NullMetadata fail", scenarioSimulationGridPanelClickHandler.manageHeaderLeftClick(1, scenarioGridColumnMock, point2DMock));
        verify(scenarioSimulationGridPanelClickHandler, times(1)).getColumnScenarioHeaderMetaDataLocal(eq(scenarioGridMock), any(Point2D.class));
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
        when(scenarioGridCellMock.isEditingMode()).thenReturn(true);
        boolean retrieved = scenarioSimulationGridPanelClickHandler.manageGridLeftClick(UI_ROW_INDEX, UI_COLUMN_INDEX, gridColumnMock);
        verify(scenarioGridCellMock, never()).setEditingMode(anyBoolean());
        assertTrue(retrieved);
        when(scenarioGridCellMock.isEditingMode()).thenReturn(false);
        scenarioSimulationGridPanelClickHandler.manageGridLeftClick(UI_ROW_INDEX, UI_COLUMN_INDEX, gridColumnMock);
        verify(scenarioGridCellMock, times(1)).setEditingMode(eq(false));
        verify(gridColumnMock, times(1)).isReadOnly();
    }

    @Test
    public void testManageGridLeftClickReadOnlyFalse() {
        when(scenarioGridMock.startEditingCell(UI_ROW_INDEX, UI_COLUMN_INDEX)).thenReturn(true);
        when(headerMetaDataMock.isReadOnly()).thenReturn(false);
        scenarioSimulationGridPanelClickHandler.setEventBus(eventBusMock);
        when(scenarioGridCellMock.isEditingMode()).thenReturn(true);
        boolean retrieved = scenarioSimulationGridPanelClickHandler.manageGridLeftClick(UI_ROW_INDEX, UI_COLUMN_INDEX, gridColumnMock);
        assertTrue(retrieved);
        verify(scenarioGridCellMock, never()).setEditingMode(anyBoolean());
        verify(gridColumnMock, never()).isReadOnly();
        when(scenarioGridCellMock.isEditingMode()).thenReturn(false);
        scenarioSimulationGridPanelClickHandler.manageGridLeftClick(UI_ROW_INDEX, UI_COLUMN_INDEX, gridColumnMock);
        verify(scenarioGridCellMock, times(1)).setEditingMode(eq(true));
        verify(gridColumnMock, times(1)).isReadOnly();
    }

    @Test
    public void testManageHeaderRightClick_NullMetadata() {
        doReturn(null).when(scenarioSimulationGridPanelClickHandler).getColumnScenarioHeaderMetaDataLocal(scenarioGridMock, point2DMock);
        assertFalse(scenarioSimulationGridPanelClickHandler.manageHeaderRightClick(scenarioGridMock, 10, 10, point2DMock, 1));
    }

    @Test
    public void testManageHeaderRightClick_NullUIHeaderRowIndex() {
        doReturn(headerMetaDataMock).when(scenarioSimulationGridPanelClickHandler).getColumnScenarioHeaderMetaDataLocal(scenarioGridMock, point2DMock);
        doReturn(null).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(scenarioGridMock, point2DMock);
        assertFalse(scenarioSimulationGridPanelClickHandler.manageHeaderRightClick(scenarioGridMock, 10, 10, point2DMock, 1));
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
        doReturn(headerMetaDataMock).when(scenarioSimulationGridPanelClickHandler).getColumnScenarioHeaderMetaDataLocal(scenarioGridMock, point2DMock);
        doReturn(1).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(scenarioGridMock, point2DMock);
        when(headerMetaDataMock.getColumnGroup()).thenReturn("OTHER");
        scenarioSimulationGridPanelClickHandler.manageHeaderRightClick(scenarioGridMock, 10, 10, point2DMock, 1);
        verify(otherContextMenuMock, times(1)).show(eq(10), eq(10));
        reset(otherContextMenuMock);
        when(headerMetaDataMock.getColumnGroup()).thenReturn("OTHER-SOMETHING");
        scenarioSimulationGridPanelClickHandler.manageHeaderRightClick(scenarioGridMock, 10, 10, point2DMock, 1);
        verify(otherContextMenuMock, times(1)).show(eq(10), eq(10));
    }

    @Test
    public void isHeaderEditable() {
        // rendereHelper == null
        scenarioSimulationGridPanelClickHandler.rendererHelper = null;
        assertFalse(scenarioSimulationGridPanelClickHandler.isHeaderEditable(headerMetaDataMock, scenarioGridColumnMock));
        // headerMetadata already in editing mode
        scenarioSimulationGridPanelClickHandler.rendererHelper = scenarioGridRendererHelperMock;
        when(headerMetaDataMock.isEditingMode()).thenReturn(true);
        assertFalse(scenarioSimulationGridPanelClickHandler.isHeaderEditable(headerMetaDataMock, scenarioGridColumnMock));
        // instance not assigned to column
        when(headerMetaDataMock.isEditingMode()).thenReturn(false);
        when(scenarioGridColumnMock.isInstanceAssigned()).thenReturn(false);
        assertFalse(scenarioSimulationGridPanelClickHandler.isHeaderEditable(headerMetaDataMock, scenarioGridColumnMock));
        // instance assigned to column and headermetadata == instance header
        when(scenarioGridColumnMock.isInstanceAssigned()).thenReturn(true);
        when(headerMetaDataMock.isInstanceHeader()).thenReturn(true);
        assertTrue(scenarioSimulationGridPanelClickHandler.isHeaderEditable(headerMetaDataMock, scenarioGridColumnMock));
        // headermetadata not instance header and not property header
        when(headerMetaDataMock.isInstanceHeader()).thenReturn(false);
        when(headerMetaDataMock.isPropertyHeader()).thenReturn(false);
        assertFalse(scenarioSimulationGridPanelClickHandler.isHeaderEditable(headerMetaDataMock, scenarioGridColumnMock));
        // property not assigned to column and headermetadata property header
        when(scenarioGridColumnMock.isPropertyAssigned()).thenReturn(false);
        when(headerMetaDataMock.isPropertyHeader()).thenReturn(true);
        assertFalse(scenarioSimulationGridPanelClickHandler.isHeaderEditable(headerMetaDataMock, scenarioGridColumnMock));
        // property assigned to column and headermetadata property header
        when(scenarioGridColumnMock.isPropertyAssigned()).thenReturn(true);
        assertTrue(scenarioSimulationGridPanelClickHandler.isHeaderEditable(headerMetaDataMock, scenarioGridColumnMock));
    }

    private void commontTestManageHeaderLeftClick_Group(String group, boolean assertExpected) {
        doReturn(true).when(scenarioSimulationGridPanelClickHandler).hasEditableHeaderLocal(scenarioGridColumnMock);
        doReturn(1).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(scenarioGridMock, point2DMock);
        doReturn(true).when(scenarioSimulationGridPanelClickHandler).isEditableHeaderLocal(scenarioGridColumnMock, 1);
        doReturn(headerMetaDataMock).when(scenarioSimulationGridPanelClickHandler).getColumnScenarioHeaderMetaDataLocal(scenarioGridMock, point2DMock);
        when(headerMetaDataMock.getColumnGroup()).thenReturn(group);
        String message = group + "Group fail";
        if (assertExpected) {
            assertTrue(message, scenarioSimulationGridPanelClickHandler.manageHeaderLeftClick(1, scenarioGridColumnMock, point2DMock));
            verify(scenarioSimulationGridPanelClickHandler, times(1))
                    .manageGivenExpectHeaderLeftClick(eq(headerMetaDataMock),
                                                      eq(scenarioGridColumnMock),
                                                      anyString(),
                                                      anyInt(),
                                                      anyInt(),
                                                      eq(point2DMock));
        } else {
            assertFalse(message, scenarioSimulationGridPanelClickHandler.manageHeaderLeftClick(1, scenarioGridColumnMock, point2DMock));
            verify(scenarioSimulationGridPanelClickHandler, never())
                    .manageGivenExpectHeaderLeftClick(eq(headerMetaDataMock),
                                                      eq(scenarioGridColumnMock),
                                                      anyString(),
                                                      anyInt(),
                                                      anyInt(),
                                                      eq(point2DMock));
            return;
        }
        verify(scenarioSimulationGridPanelClickHandler, times(1))
                .manageGivenExpectHeaderLeftClick(eq(headerMetaDataMock),
                                                  eq(scenarioGridColumnMock),
                                                  anyString(),
                                                  anyInt(),
                                                  anyInt(),
                                                  eq(point2DMock));
        reset(scenarioSimulationGridPanelClickHandler);
        doReturn(true).when(scenarioSimulationGridPanelClickHandler).hasEditableHeaderLocal(scenarioGridColumnMock);
        doReturn(1).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(scenarioGridMock, point2DMock);
        doReturn(true).when(scenarioSimulationGridPanelClickHandler).isEditableHeaderLocal(scenarioGridColumnMock, 1);
        doReturn(headerMetaDataMock).when(scenarioSimulationGridPanelClickHandler)
                .getColumnScenarioHeaderMetaDataLocal(scenarioGridMock, point2DMock);
        when(headerMetaDataMock.getColumnGroup()).thenReturn(group + "-SOMETHING");
        assertTrue(message, scenarioSimulationGridPanelClickHandler.manageHeaderLeftClick(1, scenarioGridColumnMock, point2DMock));
        verify(scenarioSimulationGridPanelClickHandler, times(1))
                .manageGivenExpectHeaderLeftClick(eq(headerMetaDataMock),
                                                  eq(scenarioGridColumnMock),
                                                  anyString(),
                                                  anyInt(),
                                                  anyInt(),
                                                  eq(point2DMock));
    }

    private void commonTestManageHeaderRightClick_NOGroupTitle(String group, AbstractHeaderMenuPresenter menuMock) {
        doReturn(headerMetaDataMock).when(scenarioSimulationGridPanelClickHandler).getColumnScenarioHeaderMetaDataLocal(scenarioGridMock, point2DMock);
        doReturn(1).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(scenarioGridMock, point2DMock);
        when(headerMetaDataMock.getColumnGroup()).thenReturn("");
        when(headerMetaDataMock.getTitle()).thenReturn(group);
        scenarioSimulationGridPanelClickHandler.manageHeaderRightClick(scenarioGridMock, 10, 10, point2DMock, 1);
        verify(menuMock, times(1)).show(eq(10), eq(10));
    }

    private void commonTestManageHeaderRightClick_Group(String group, AbstractColumnMenuPresenter menuMock) {
        doReturn(headerMetaDataMock).when(scenarioSimulationGridPanelClickHandler).getColumnScenarioHeaderMetaDataLocal(scenarioGridMock, point2DMock);
        doReturn(1).when(scenarioSimulationGridPanelClickHandler).getUiHeaderRowIndexLocal(scenarioGridMock, point2DMock);
        when(headerMetaDataMock.getColumnGroup()).thenReturn(group);
        scenarioSimulationGridPanelClickHandler.manageHeaderRightClick(scenarioGridMock, 10, 10, point2DMock, 1);
        verify(menuMock, times(1)).show(eq(10), eq(10), eq(1), eq(group), anyBoolean());
        reset(menuMock);
        when(headerMetaDataMock.getColumnGroup()).thenReturn(group + "-SOMETHING");
        scenarioSimulationGridPanelClickHandler.manageHeaderRightClick(scenarioGridMock, 10, 10, point2DMock, 1);
        verify(menuMock, times(1)).show(eq(10), eq(10), eq(1), eq(group), anyBoolean());
    }

    private void commonCheck() {
        verify(scenarioSimulationGridPanelClickHandler, times(1)).hideMenus();
    }
}