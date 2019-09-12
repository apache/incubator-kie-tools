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
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.Event;
import org.drools.workbench.screens.scenariosimulation.client.events.RefreshMenusEvent;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BaseMenuTest {

    @Mock
    private RootPanel rootPanelMock;

    @Mock
    private MenuItemPresenter menuItemPresenterMock;

    @Mock
    private ExecutableMenuItemPresenter executableMenuItemPresenterMock;

    @Mock
    private BaseMenuView viewMock;

    @Mock
    private UListElement contextMenuDropdownMock;

    @Mock
    private Style styleMock;

    private BaseMenu baseMenu;

    @Before
    public void setup() {
        when(contextMenuDropdownMock.getStyle()).thenReturn(styleMock);
        when(viewMock.getContextMenuDropdown()).thenReturn(contextMenuDropdownMock);
        this.baseMenu = spy(new BaseMenu() {
            {
                this.view = viewMock;
                this.menuItemPresenter = menuItemPresenterMock;
                this.executableMenuItemPresenter = executableMenuItemPresenterMock;
            }

            @Override
            protected RootPanel getRootPanel() {
                return rootPanelMock;
            }
        });
    }

    @Test
    public void initialise() {
        baseMenu.initialise();
        verify(viewMock, times(1)).init(baseMenu);
    }

    @Test
    public void addMenuItem() {
        String id = "TEST-ID";
        String label = "TEST-LABEL";
        String i18n = "TEST-i18n";
        baseMenu.addMenuItem(id, label, i18n);
        verify(contextMenuDropdownMock, times(1)).appendChild(menuItemPresenterMock.getLabelMenuElement(id, label));
    }

    @Test
    public void addExecutableMenuItemWithEvent() {
        Event mockEvent = mock(Event.class);
        String id = "TEST-ID";
        String label = "TEST-LABEL";
        String i18n = "TEST-i18n";
        baseMenu.addExecutableMenuItem(id, label, i18n, mockEvent);
        verify(contextMenuDropdownMock, times(1)).appendChild(executableMenuItemPresenterMock.getLExecutableMenuElement(id, label, mockEvent));
    }

    @Test
    public void addExecutableMenuItemWithoutEvent() {
        String id = "TEST-ID";
        String label = "TEST-LABEL";
        String i18n = "TEST-i18n";
        baseMenu.addExecutableMenuItem(id, label, i18n);
        verify(contextMenuDropdownMock, times(1)).appendChild(executableMenuItemPresenterMock.getLExecutableMenuElement(id, label));
    }

    @Test
    public void removeMenuItem() {
        LIElement toRemoveMock = mock(LIElement.class);
        baseMenu.removeMenuItem(toRemoveMock);
        verify(contextMenuDropdownMock, times(1)).removeChild(eq(toRemoveMock));
    }

    @Test
    public void onRefreshMenusEvent() {
        baseMenu.onRefreshMenusEvent(mock(RefreshMenusEvent.class));
        verify(baseMenu, times(1)).initialise();
    }

    @Test
    public void mapEvent() {
        LIElement executableMenuItemMock = mock(LIElement.class);
        Event toBeMappedMock = mock(Event.class);
        baseMenu.mapEvent(executableMenuItemMock, toBeMappedMock);
        verify(executableMenuItemPresenterMock, times(1)).mapEvent(eq(executableMenuItemMock), eq(toBeMappedMock));
    }

    @Test
    public void asWidget() {
        baseMenu.asWidget();
        verify(viewMock, times(1)).asWidget();
    }

    @Test
    public void show() {
        int x = 10;
        int y = 20;
        baseMenu.show(x, y);
        verify(baseMenu, times(1)).hide();
        verify(styleMock, times(1)).setLeft(x, Style.Unit.PX);
        verify(styleMock, times(1)).setTop(y, Style.Unit.PX);
        verify(styleMock, times(1)).setDisplay(Style.Display.BLOCK);
    }

    @Test
    public void hide() {
        when(rootPanelMock.getWidgetIndex(viewMock)).thenReturn(1);
        baseMenu.hide();
        verify(baseMenu, times(1)).isShown();
        verify(styleMock, times(1)).setDisplay(Style.Display.NONE);
        when(rootPanelMock.getWidgetIndex(viewMock)).thenReturn(-1);
        reset(baseMenu);
        reset(styleMock);
        baseMenu.hide();
        verify(baseMenu, times(1)).isShown();
        verify(styleMock, times(0)).setDisplay(Style.Display.NONE);
    }

    @Test
    public void getView() {
        BaseMenuView retrieved = baseMenu.getView();
        assertNotNull(retrieved);
    }

    @Test
    public void isShown() {
        when(rootPanelMock.getWidgetIndex(viewMock)).thenReturn(1);
        assertTrue(baseMenu.isShown());
        when(rootPanelMock.getWidgetIndex(viewMock)).thenReturn(-1);
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

    @Test
    public void isDisabled() {
        Element elementMock = mock(Element.class);
        when(elementMock.getClassName()).thenReturn(Styles.DISABLED);
        assertTrue(baseMenu.isDisabled(elementMock));
        when(elementMock.getClassName()).thenReturn(Styles.DROPDOWN_MENU);
        assertFalse(baseMenu.isDisabled(elementMock));
    }

    @Test
    public void  updateMenuItemAttributes() {
        LIElement toUpdateMock = mock(LIElement.class);
        NodeList<Element> elementsByTagNameMock = mock(NodeList.class);
        Element itemMock = mock(Element.class);
        when(elementsByTagNameMock.getItem(0)).thenReturn(itemMock);
        when(toUpdateMock.getElementsByTagName("span")).thenReturn(elementsByTagNameMock);
        String id = "TEST-ID";
        String label = "TEST-LABEL";
        String i18n = "TEST-i18n";
        baseMenu.updateMenuItemAttributes(toUpdateMock, id, label, i18n);
        verify(toUpdateMock, times(1)).setId(eq(id));
        verify(itemMock, times(1)).setInnerHTML(eq(label));
    }
}