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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.UListElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.Event;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public abstract class AbstractMenuTest {

    protected AbstractColumnMenuPresenter abstractColumnMenuPresenter;

    protected AbstractColumnMenuPresenter abstractColumnMenuPresenterSpy;

    @Mock
    protected MenuItemPresenter menuItemPresenterMock;
    @Mock
    protected ExecutableMenuItemPresenter executableMenuItemPresenterMock;
    @Mock
    protected UListElement contextMenuDropdownMock;
    @Mock
    protected BaseMenuView viewMock;
    @Mock
    protected LIElement menuItemMock;
    @Mock
    protected LIElement executableMenuItemMock;
    @Mock
    protected LIElement gridTitleElementMock;
    @Mock
    protected NodeList<Element> elementsByTagNameMock;
    @Mock
    protected Element itemMock;


    protected void setup() {
        when(elementsByTagNameMock.getItem(0)).thenReturn(itemMock);
        doReturn(menuItemMock).when(menuItemPresenterMock).getLabelMenuElement(any(), any());
        doReturn(gridTitleElementMock).when(menuItemPresenterMock).getLabelMenuElement(any(), eq("scenario"));
        doReturn(executableMenuItemMock).when(executableMenuItemPresenterMock).getLExecutableMenuElement(any(), any(), isA(Event.class));
        doReturn(executableMenuItemMock).when(executableMenuItemPresenterMock).getLExecutableMenuElement(any(), any());
        //doReturn(du).when(executableMenuItemPresenterMock).getLExecutableMenuElement(anyString(), eq("duplicateInstance"));

        when(menuItemMock.getElementsByTagName("span")).thenReturn(elementsByTagNameMock);
        when(executableMenuItemMock.getElementsByTagName("span")).thenReturn(elementsByTagNameMock);
        when(viewMock.getContextMenuDropdown()).thenReturn(contextMenuDropdownMock);
        abstractColumnMenuPresenter.menuItemPresenter = menuItemPresenterMock;
        abstractColumnMenuPresenter.executableMenuItemPresenter = executableMenuItemPresenterMock;
        abstractColumnMenuPresenter.view = viewMock;
        abstractColumnMenuPresenter.gridTitleElement = gridTitleElementMock;
        abstractColumnMenuPresenterSpy = spy(abstractColumnMenuPresenter);
    }

}