/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.contextmenu;

import java.util.Collections;
import java.util.List;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLDocument;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorItem;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorTextItem;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelector;
import org.powermock.reflect.Whitebox;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ContextMenuViewTest {

    private ContextMenuView contextMenuView;
    private ContextMenu presenter;
    private ListSelector listSelector;

    @Before
    public void setUp() {
        presenter = mock(ContextMenu.class);
        listSelector = mock(ListSelector.class);
        Whitebox.setInternalState(DomGlobal.class, "document", mock(HTMLDocument.class));

        contextMenuView = new ContextMenuView(listSelector);
        contextMenuView.init(presenter);
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
        final String value = "test-val";
        event.path = new Element[] {element};

        when(element.getAttribute(anyString())).thenReturn(value);

        final Element[] eventPath = contextMenuView.getEventPath(event);

        assertThat(eventPath).isNotNull();
        assertThat(eventPath).isNotEmpty();
        assertThat(eventPath.length).isEqualTo(1);
        assertThat(eventPath[0]).extracting(elem -> elem.getAttribute("test-attr")).isEqualTo(value);
    }

    @Test
    public void testWhenGettingEventPathAndPathIsNull() {
        final Event event = mock(Event.class);
        final Element element = mock(Element.class);
        final String value = "test-val";
        event.path = null;

        when(event.composedPath()).thenReturn(new Event.ComposedPathArrayUnionType[]{buildComposedPathArrayUnionType(element)});
        when(element.getAttribute(anyString())).thenReturn(value);

        final Element[] eventPath = contextMenuView.getEventPath(event);

        assertThat(eventPath).isNotNull();
        assertThat(eventPath).isNotEmpty();
        assertThat(eventPath.length).isEqualTo(1);
        assertThat(eventPath[0]).extracting(elem -> elem.getAttribute("test-attr")).isEqualTo(value);
    }

    private Event.ComposedPathArrayUnionType buildComposedPathArrayUnionType(Element element) {
        return new Event.ComposedPathArrayUnionType() {
                @Override
                public boolean isElement() {
                    return true;
                }

                @Override
                public Element asElement() {
                    return element;
                }
            };
    }
}
