/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.client.editor.menu;

import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.Event;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.events.AppendColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.PrependColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderGivenContextMenu.HEADERGIVENCONTEXTMENU_GRID_TITLE;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class HeaderGivenColumnGridContextMenuTest {

    @Mock
    private RootPanel rootPanelMock;

    @Mock
    private LIElement appendColumnElementMock;
    @Mock
    private LIElement prependColumnElementMock;
    @Mock
    private LIElement gridTitleElementMock;
    @Mock
    private BaseMenuView viewMock;
    @Mock
    private UListElement contextMenuDropdownMock;
    @Mock
    private Style styleMock;

    private HeaderGivenContextMenu headerGivenContextMenuSpy;

    @Before
    public void setup() {
        when(contextMenuDropdownMock.getStyle()).thenReturn(styleMock);
        when(viewMock.getContextMenuDropdown()).thenReturn(contextMenuDropdownMock);
        headerGivenContextMenuSpy = spy(new HeaderGivenContextMenu() {

            {
                this.appendColumnElement = appendColumnElementMock;
                this.prependColumnElement = prependColumnElementMock;
                this.gridTitleElement = gridTitleElementMock;
                this.view = viewMock;
            }

            @Override
            public void mapEvent(LIElement executableMenuItem, Event toBeMapped) {
                //Do nothing
            }

            @Override
            protected void updateExecutableMenuItemAttributes(LIElement toUpdate, String id, String label, String i18n) {
                //Do nothing
            }

            @Override
            public void show(final int mx, final int my) {
                //Do nothing
            }

            @Override
            public void hide() {

            }

            @Override
            protected RootPanel getRootPanel() {
                return rootPanelMock;
            }
        });
    }

    @Test
    public void show_Simulation() {
        headerGivenContextMenuSpy.show(GridWidget.SIMULATION, 0, 0);
        verify(headerGivenContextMenuSpy, never()).updateMenuItemAttributes(eq(gridTitleElementMock), eq(HEADERGIVENCONTEXTMENU_GRID_TITLE), eq(ScenarioSimulationEditorConstants.INSTANCE.background()), eq("background"));
        verify(headerGivenContextMenuSpy, times(1)).updateMenuItemAttributes(eq(gridTitleElementMock), eq(HEADERGIVENCONTEXTMENU_GRID_TITLE), eq(ScenarioSimulationEditorConstants.INSTANCE.scenario()), eq("scenario"));
        verify(headerGivenContextMenuSpy, times(1)).mapEvent(eq(appendColumnElementMock), isA(AppendColumnEvent.class));
        verify(headerGivenContextMenuSpy, times(1)).mapEvent(eq(prependColumnElementMock), isA(PrependColumnEvent.class));
    }

    @Test
    public void show_Background() {
        headerGivenContextMenuSpy.show(GridWidget.BACKGROUND, 0, 0);
        verify(headerGivenContextMenuSpy, times(1)).updateMenuItemAttributes(eq(gridTitleElementMock), eq(HEADERGIVENCONTEXTMENU_GRID_TITLE), eq(ScenarioSimulationEditorConstants.INSTANCE.background()), eq("background"));
        verify(headerGivenContextMenuSpy, never()).updateMenuItemAttributes(eq(gridTitleElementMock), eq(HEADERGIVENCONTEXTMENU_GRID_TITLE), eq(ScenarioSimulationEditorConstants.INSTANCE.scenario()), eq("scenario"));
        verify(headerGivenContextMenuSpy, times(1)).mapEvent(eq(appendColumnElementMock), isA(AppendColumnEvent.class));
        verify(headerGivenContextMenuSpy, times(1)).mapEvent(eq(prependColumnElementMock), isA(PrependColumnEvent.class));
    }
}
