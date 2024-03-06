/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.contextmenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import elemental2.core.JsArray;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.EventTarget;
import elemental2.dom.HTMLDocument;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorItem;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorTextItem;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelector;
import org.mockito.Mockito;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ContextMenuViewTest {

    private ContextMenuView contextMenuView;
    private ContextMenu presenter;
    private ListSelector listSelector;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        presenter = mock(ContextMenu.class);
        listSelector = mock(ListSelector.class);

        contextMenuView = spy(new ContextMenuView(listSelector));
        contextMenuView.init(presenter);

        doReturn(mock(HTMLDocument.class)).when(contextMenuView).getDocument();
    }

    @Test
    public void testWhenShowingContextMenuViewThenAlsoListSelectorIsShown() {
        contextMenuView.show();

        verify(listSelector).bind(any(), anyInt(), anyInt());
        verify(listSelector).show();
    }

    @Test
    public void testWhenHidingContextMenuViewThenAlsoListSelectorIsHidden() {
        contextMenuView.hide();

        verify(listSelector).hide();
    }

    @Test
    public void testWhenGettingItemsThenTheyAreReturned() {
        final String textTitle = "TEXT";
        final Command doNothing = () -> {
        };
        when(presenter.getItems()).thenReturn(Collections.singletonList(ListSelectorTextItem.build(textTitle, true, doNothing)));

        final List<ListSelectorItem> items = contextMenuView.getItems(0, 0);
        final ListSelectorTextItem textItem = (ListSelectorTextItem) items.get(0);
        assertThat(items).isNotNull();
        assertThat(items).isNotEmpty();
        assertThat(items.size()).isEqualTo(1);
        assertThat(textItem).extracting(ListSelectorTextItem::getText).isEqualTo(textTitle);
        assertThat(textItem).extracting(ListSelectorTextItem::isEnabled).isEqualTo(true);
        assertThat(textItem).extracting(ListSelectorTextItem::getCommand).isEqualTo(doNothing);
    }

    @Test
    public void testWhenSelectingAnItemThenAssociatedCommandIsExecutedAndContextMenuGetsHidden() {
        final ListSelectorTextItem textItem = mock(ListSelectorTextItem.class);
        final Command command = mock(Command.class);
        when(presenter.getItems()).thenReturn(Collections.singletonList(textItem));
        when(textItem.getCommand()).thenReturn(command);

        contextMenuView.onItemSelected(textItem);

        verify(command).execute();
        verify(listSelector).hide();
    }

    @Test
    public void testWhenGettingEventPath() {
        final Event event = mock(Event.class);
        final Element element = mock(Element.class);
        final List<EventTarget> pathArray = new ArrayList<>();
        final String value = "test-val";
        event.path = spy(new JsArray<>());
        pathArray.add(element);

        doReturn(pathArray).when(event.path).asList();

        when(element.getAttribute(Mockito.<String>any())).thenReturn(value);

        final List<Element> eventPath = contextMenuView.getEventPath(event);

        assertThat(eventPath).isNotNull();
        assertThat(eventPath).isNotEmpty();
        assertThat(eventPath.size()).isEqualTo(1);
        assertThat(eventPath.get(0)).extracting(elem -> elem.getAttribute("test-attr")).isEqualTo(value);
    }

    @Test
    public void testWhenGettingEventPathAndPathIsNull() {
        final Event event = mock(Event.class);
        final Element element = mock(Element.class);
        final JsArray<EventTarget> composedPath = spy(new JsArray<>());
        final List<EventTarget> composedPathAsList = new ArrayList<>();
        final String value = "test-val";
        event.path = null;
        composedPathAsList.add(element);

        doReturn(composedPathAsList).when(composedPath).asList();
        when(event.composedPath()).thenReturn(composedPath);
        when(element.getAttribute(Mockito.<String>any())).thenReturn(value);

        final List<Element> eventPath = contextMenuView.getEventPath(event);

        assertThat(eventPath).isNotNull();
        assertThat(eventPath).isNotEmpty();
        assertThat(eventPath.size()).isEqualTo(1);
        assertThat(eventPath.get(0)).extracting(elem -> elem.getAttribute("test-attr")).isEqualTo(value);
    }
}
