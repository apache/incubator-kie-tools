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
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.AbstractHeaderMenuPresenter;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.ExpectedContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GivenContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderExpectedContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderGivenContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.OtherContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.UnmodifiableColumnGridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableRightPanelEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
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
    private OtherContextMenu mockOtherContextMenu;
    @Mock
    private HeaderGivenContextMenu mockHeaderGivenContextMenu;
    @Mock
    private HeaderExpectedContextMenu mockHeaderExpectedContextMenu;
    @Mock
    private GivenContextMenu mockGivenContextMenu;
    @Mock
    private ExpectedContextMenu mockExpectedContextMenu;
    @Mock
    private GridContextMenu mockGridContextMenu;
    @Mock
    private UnmodifiableColumnGridContextMenu mockUnmodifiableColumnGridContextMenu;
    @Mock
    private Set<AbstractHeaderMenuPresenter> mockManagedMenus;

    @Mock
    private EventBus mockEventBus;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        scenarioSimulationGridPanelClickHandler = spy(new ScenarioSimulationGridPanelClickHandler() {
            {
                scenarioGrid = mockScenarioGrid;
                otherContextMenu = mockOtherContextMenu;
                headerGivenContextMenu = mockHeaderGivenContextMenu;
                headerExpectedContextMenu = mockHeaderExpectedContextMenu;
                givenContextMenu = mockGivenContextMenu;
                expectedContextMenu = mockExpectedContextMenu;
                gridContextMenu = mockGridContextMenu;
                unmodifiableColumnGridContextMenu = mockUnmodifiableColumnGridContextMenu;
                managedMenus.add(mockOtherContextMenu);
                managedMenus.add(mockHeaderGivenContextMenu);
                managedMenus.add(mockHeaderExpectedContextMenu);
                managedMenus.add(mockGivenContextMenu);
                managedMenus.add(mockExpectedContextMenu);
                managedMenus.add(mockGridContextMenu);
                managedMenus.add(mockUnmodifiableColumnGridContextMenu);
            }

            @Override
            protected boolean manageRightClick(ContextMenuEvent event) {
                return true;
            }
        });
        mockManagedMenus = spy(scenarioSimulationGridPanelClickHandler.managedMenus);
    }

    @Test
    public void setScenarioGrid() {
        scenarioSimulationGridPanelClickHandler.setScenarioGrid(mockScenarioGrid);
        assertEquals(mockScenarioGrid, scenarioSimulationGridPanelClickHandler.scenarioGrid);
    }

    @Test
    public void setOtherContextMenu() {
        scenarioSimulationGridPanelClickHandler.setOtherContextMenu(mockOtherContextMenu);
        assertEquals(mockOtherContextMenu, scenarioSimulationGridPanelClickHandler.otherContextMenu);
    }

    @Test
    public void setHeaderGivenContextMenu() {
        scenarioSimulationGridPanelClickHandler.setHeaderGivenContextMenu(mockHeaderGivenContextMenu);
        assertEquals(mockHeaderGivenContextMenu, scenarioSimulationGridPanelClickHandler.headerGivenContextMenu);
    }

    @Test
    public void setHeaderExpectedContextMenu() {
        scenarioSimulationGridPanelClickHandler.setHeaderExpectedContextMenu(mockHeaderExpectedContextMenu);
        assertEquals(mockHeaderExpectedContextMenu, scenarioSimulationGridPanelClickHandler.headerExpectedContextMenu);
    }

    @Test
    public void setGivenContextMenu() {
        scenarioSimulationGridPanelClickHandler.setGivenContextMenu(mockGivenContextMenu);
        assertEquals(mockGivenContextMenu, scenarioSimulationGridPanelClickHandler.givenContextMenu);
    }

    @Test
    public void setExpectedContextMenu() {
        scenarioSimulationGridPanelClickHandler.setExpectedContextMenu(mockExpectedContextMenu);
        assertEquals(mockExpectedContextMenu, scenarioSimulationGridPanelClickHandler.expectedContextMenu);
    }

    @Test
    public void setGridContextMenu() {
        scenarioSimulationGridPanelClickHandler.setGridContextMenu(mockGridContextMenu);
        assertEquals(mockGridContextMenu, scenarioSimulationGridPanelClickHandler.gridContextMenu);
    }

    @Test
    public void setUnmodifiableColumnGridContextMenu() {
        scenarioSimulationGridPanelClickHandler.setUnmodifiableColumnGridContextMenu(mockUnmodifiableColumnGridContextMenu);
        assertEquals(mockUnmodifiableColumnGridContextMenu, scenarioSimulationGridPanelClickHandler.unmodifiableColumnGridContextMenu);
    }

    @Test
    public void setEventBus() {
        scenarioSimulationGridPanelClickHandler.setEventBus(mockEventBus);
        verify(mockOtherContextMenu, times(1)).setEventBus(eq(mockEventBus));
        verify(mockHeaderGivenContextMenu, times(1)).setEventBus(eq(mockEventBus));
        verify(mockHeaderExpectedContextMenu, times(1)).setEventBus(eq(mockEventBus));
        verify(mockGivenContextMenu, times(1)).setEventBus(eq(mockEventBus));
        verify(mockExpectedContextMenu, times(1)).setEventBus(eq(mockEventBus));
        verify(mockGridContextMenu, times(1)).setEventBus(eq(mockEventBus));
        verify(mockUnmodifiableColumnGridContextMenu, times(1)).setEventBus(eq(mockEventBus));
    }

    @Test
    public void getRelativeX() {
        int retrieved = scenarioSimulationGridPanelClickHandler.getRelativeX(mockContextMenuEvent);
        assertEquals(EXPECTED_RELATIVE_X, retrieved);
    }

    @Test
    public void getRelativeY() {
        int retrieved = scenarioSimulationGridPanelClickHandler.getRelativeY(mockContextMenuEvent);
        assertEquals(EXPECTED_RELATIVE_Y, retrieved);
    }

    @Test
    public void commonClickManagement() {
        scenarioSimulationGridPanelClickHandler.hideMenus();
        verify(mockOtherContextMenu, times(1)).hide();
        verify(mockHeaderGivenContextMenu, times(1)).hide();
        verify(mockHeaderExpectedContextMenu, times(1)).hide();
        verify(mockGivenContextMenu, times(1)).hide();
        verify(mockExpectedContextMenu, times(1)).hide();
        verify(mockGridContextMenu, times(1)).hide();
        verify(mockUnmodifiableColumnGridContextMenu, times(1)).hide();
    }

    @Test
    public void onContextMenu() {
        scenarioSimulationGridPanelClickHandler.onContextMenu(mockContextMenuEvent);
        verify(mockContextMenuEvent, times(1)).preventDefault();
        verify(mockContextMenuEvent, times(1)).stopPropagation();
        commonCheck();
    }

    @Test
    public void testManageLeftClick() {
        scenarioSimulationGridPanelClickHandler.setEventBus(mockEventBus);
        assertTrue("Click to only header cell of the only present column.",
                   scenarioSimulationGridPanelClickHandler.manageLeftClick(CLICK_POINT_X,
                                                                           CLICK_POINT_Y,
                                                                           SHIFT_PRESSED,
                                                                           CTRL_PRESSED));
        verify(mockScenarioGrid).selectColumn(UI_COLUMN_INDEX);
        verify(mockEventBus).fireEvent(any(EnableRightPanelEvent.class));
    }

    @Test
    public void testManageLeftClick_ReadOnly() {
        when(headerMetaData.isReadOnly()).thenReturn(true);

        scenarioSimulationGridPanelClickHandler.setEventBus(mockEventBus);
        assertFalse("Click to readonly header cell.",
                    scenarioSimulationGridPanelClickHandler.manageLeftClick(CLICK_POINT_X,
                                                                            CLICK_POINT_Y,
                                                                            SHIFT_PRESSED,
                                                                            CTRL_PRESSED));
        verify(mockScenarioGrid, never()).selectColumn(anyInt());
        verify(mockEventBus, never()).fireEvent(any(EnableRightPanelEvent.class));
    }

    @Test
    public void testManageLeftClick_NextToGrid() {
        assertFalse("Click to point next to table.",
                    scenarioSimulationGridPanelClickHandler.manageLeftClick(GRID_WIDTH.intValue() + CLICK_POINT_X,
                                                                            CLICK_POINT_Y,
                                                                            SHIFT_PRESSED,
                                                                            CTRL_PRESSED));
        verify(mockScenarioGrid, never()).selectColumn(anyInt());
        verify(mockEventBus, never()).fireEvent(any(EnableRightPanelEvent.class));
    }

    @Test
    public void testManageLeftClick_BelowHeader() {
        assertFalse("Click to point below header.",
                    scenarioSimulationGridPanelClickHandler.manageLeftClick(CLICK_POINT_X,
                                                                            HEADER_HEIGHT.intValue() + CLICK_POINT_X,
                                                                            SHIFT_PRESSED,
                                                                            CTRL_PRESSED));
        verify(mockScenarioGrid, never()).selectColumn(anyInt());
        verify(mockEventBus, never()).fireEvent(any(EnableRightPanelEvent.class));
    }

    private void commonCheck() {
        verify(scenarioSimulationGridPanelClickHandler, times(1)).hideMenus();
    }
}