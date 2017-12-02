/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.docks.view.items;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.PlaceRequest;

@RunWith(GwtMockitoTestRunner.class)
public class SideDockItemFocusedTest {

    private SideDockItemFocused focusedItem;

    @Mock
    private SideDockItem sideDockItem;

    @Mock
    private UberfireDock uberfireDock;

    @GwtMock
    private Button button;

    private ClickHandler clickHandler;
    private Element element;
    private Style style;

    @Before
    public void setUp() {
        when(uberfireDock.getDockPosition()).thenReturn(UberfireDockPosition.EAST);
        when(uberfireDock.getIconType()).thenReturn("RANDOM");
        when(uberfireDock.getPlaceRequest()).thenReturn(mock(PlaceRequest.class));

        when(sideDockItem.getDock()).thenReturn(uberfireDock);

        focusedItem = spy(new SideDockItemFocused(sideDockItem));

        when(button.addClickHandler(any(ClickHandler.class))).thenAnswer((Answer) aInvocation -> {
            clickHandler = (ClickHandler) aInvocation.getArguments()[0];
            return null;
        });

        element = mock(Element.class);
        style = mock(Style.class);
        when(sideDockItem.getElement()).thenReturn(element);
        when(element.getStyle()).thenReturn(style);
    }

    @Test
    public void createTest() {
        verify(button).setSize(ButtonSize.SMALL);
        verify(button).setType(ButtonType.INFO);

        verify(sideDockItem).configureText(any(Button.class), eq((String) null));
        verify(sideDockItem).configureIcon(any(Button.class), eq((ImageResource) null));

        verify(sideDockItem, never()).configureImageIcon(any(Button.class), any(ImageResource.class));
    }

    @Test
    public void createWithClosedSideDockTest() {
        when(sideDockItem.isOpened()).thenReturn(false);

        focusedItem.createButton(sideDockItem);

        clickHandler.onClick(new ClickEvent() {
        });

        verify(sideDockItem).openAndExecuteExpandCommand();
        verify(sideDockItem, never()).closeAndExecuteCommand();
        verify(button).setActive(false);
        verify(button, never()).setActive(true);
    }

    @Test
    public void createWithOpenedSideDockTest() {
        when(sideDockItem.isOpened()).thenReturn(true);

        focusedItem.createButton(sideDockItem);

        clickHandler.onClick(new ClickEvent() {
        });

        verify(sideDockItem).closeAndExecuteCommand();
        verify(sideDockItem, never()).openAndExecuteExpandCommand();
        verify(button).setActive(true);
        verify(button, never()).setActive(false);
    }

    @Test
    public void openTest() {
        focusedItem.open();

        verify(sideDockItem).getElement();
        verify(element).getStyle();
        verify(style).setVisibility(Style.Visibility.HIDDEN);
        verify(style, never()).setVisibility(Style.Visibility.VISIBLE);
    }

    @Test
    public void hideTest() {
        focusedItem.hide();

        verify(sideDockItem).getElement();
        verify(element).getStyle();
        verify(style).setVisibility(Style.Visibility.VISIBLE);
        verify(style, never()).setVisibility(Style.Visibility.HIDDEN);
    }

}