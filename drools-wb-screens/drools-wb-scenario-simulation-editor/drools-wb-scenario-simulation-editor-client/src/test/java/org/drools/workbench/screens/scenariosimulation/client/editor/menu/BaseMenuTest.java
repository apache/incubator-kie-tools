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

package org.drools.workbench.screens.scenariosimulation.client.editor.menu;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.events.RefreshMenusEvent;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BaseMenuTest {

    @Mock
    private RootPanel mockRootPanel;

    @Mock
    private MenuItemPresenter mockMenuItemPresenter;

    @Mock
    private BaseMenuView mockView;

    @Mock
    private UListElement mockContextMenuDropdown;

    @Mock
    private Style mockStyle;

    private BaseMenu baseMenu;

    @Before
    public void setup() {
        when(mockContextMenuDropdown.getStyle()).thenReturn(mockStyle);
        when(mockView.getContextMenuDropdown()).thenReturn(mockContextMenuDropdown);
        this.baseMenu = spy(new BaseMenu() {
            {
                this.view = mockView;
                this.menuItemPresenter = mockMenuItemPresenter;
            }

            @Override
            protected RootPanel getRootPanel() {
                return mockRootPanel;
            }
        });
    }

    @Test
    public void initialise() {
        baseMenu.initialise();
        verify(mockView, times(1)).init(baseMenu);
    }

    @Test
    public void addMenuItem() {
        Command mockCommand = mock(Command.class);
        String id = "TEST-ID";
        String label = "TEST-LABEL";
        String i18n = "TEST-i18n";
        baseMenu.addMenuItem(id, label, i18n, mockCommand);
        verify(mockContextMenuDropdown, times(1)).appendChild(mockMenuItemPresenter.getLIElement(id, label, mockCommand));
    }

    @Test
    public void onRefreshMenusEvent() {
        baseMenu.onRefreshMenusEvent(mock(RefreshMenusEvent.class));
        verify(baseMenu, times(1)).initialise();
    }

    @Test
    public void asWidget() {
        baseMenu.asWidget();
        verify(mockView, times(1)).asWidget();
    }

    @Test
    public void show() {
        int x = 10;
        int y = 20;
        baseMenu.show(x, y);
        verify(baseMenu, times(1)).hide();
        verify(mockStyle, times(1)).setLeft(x, Style.Unit.PX);
        verify(mockStyle, times(1)).setTop(y, Style.Unit.PX);
        verify(mockStyle, times(1)).setDisplay(Style.Display.BLOCK);
    }

    @Test
    public void hide() {
        when(mockRootPanel.getWidgetIndex(mockView)).thenReturn(1);
        baseMenu.hide();
        verify(baseMenu, times(1)).isShown();
        verify(mockStyle, times(1)).setDisplay(Style.Display.NONE);
        when(mockRootPanel.getWidgetIndex(mockView)).thenReturn(-1);
        reset(baseMenu);
        reset(mockStyle);
        baseMenu.hide();
        verify(baseMenu, times(1)).isShown();
        verify(mockStyle, times(0)).setDisplay(Style.Display.NONE);
    }

    @Test
    public void getView() {
        BaseMenuView retrieved = baseMenu.getView();
        assertNotNull(retrieved);
    }

    @Test
    public void isShown() {
        when(mockRootPanel.getWidgetIndex(mockView)).thenReturn(1);
        assertTrue(baseMenu.isShown());
        when(mockRootPanel.getWidgetIndex(mockView)).thenReturn(-1);
        assertFalse(baseMenu.isShown());
    }

    @Test
    public void onContextMenuEvent() {
        ContextMenuEvent mockEvent = mock(ContextMenuEvent.class);
        baseMenu.onContextMenuEvent(mockEvent);
        verify(mockEvent, times(1)).preventDefault();
        verify(mockEvent, times(1)).stopPropagation();
        verify(baseMenu, times(1)).hide();
    }

    @Test
    public void enableElement() {
        Element mockElement = mock(Element.class);
        baseMenu.enableElement(mockElement, true);
        verify(mockElement, times(1)).removeClassName(Styles.DISABLED);
        reset(mockElement);
        baseMenu.enableElement(mockElement, false);
        verify(mockElement, times(1)).addClassName(Styles.DISABLED);
    }

}