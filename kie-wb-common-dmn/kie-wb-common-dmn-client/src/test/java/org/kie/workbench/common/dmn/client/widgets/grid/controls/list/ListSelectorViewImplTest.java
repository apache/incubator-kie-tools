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

package org.kie.workbench.common.dmn.client.widgets.grid.controls.list;

import java.util.Arrays;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ListSelectorViewImplTest {

    @Mock
    private UnorderedList itemsContainer;

    @Mock
    private ManagedInstance<ListSelectorTextItemView> listSelectorTextItemViews;

    @Mock
    private ManagedInstance<ListSelectorDividerItemView> listSelectorDividerItemViews;

    @Mock
    private ManagedInstance<ListSelectorHeaderItemView> listSelectorHeaderItemViews;

    @Mock
    private ListSelectorView.Presenter presenter;

    @Mock
    private ListSelectorTextItemView textItemView;

    @Mock
    private HTMLElement textElement;

    @Mock
    private HasListSelectorControl.ListSelectorTextItem textItem;

    @Mock
    private ListSelectorDividerItemView dividerItemView;

    @Mock
    private HTMLElement dividerElement;

    @Mock
    private HasListSelectorControl.ListSelectorDividerItem dividerItem;

    @Mock
    private ListSelectorHeaderItemView headerItemView;

    @Mock
    private HTMLElement headerElement;

    @Mock
    private HasListSelectorControl.ListSelectorHeaderItem headerItem;

    private ListSelectorViewImpl view;

    @Before
    public void setUp() {
        view = new ListSelectorViewImpl(itemsContainer,
                                        listSelectorTextItemViews,
                                        listSelectorDividerItemViews,
                                        listSelectorHeaderItemViews);
        view.init(presenter);
        doReturn(textItemView).when(listSelectorTextItemViews).get();
        doReturn(textElement).when(textItemView).getElement();
        doReturn(dividerItemView).when(listSelectorDividerItemViews).get();
        doReturn(dividerElement).when(dividerItemView).getElement();
        doReturn(headerItemView).when(listSelectorHeaderItemViews).get();
        doReturn(headerElement).when(headerItemView).getElement();
    }

    @Test
    public void testSetItems() {
        final boolean textItemEnabled = true;
        final String textItemText = "Insert rule above";
        doReturn(textItemEnabled).when(textItem).isEnabled();
        doReturn(textItemText).when(textItem).getText();

        final String headerItemText = "Header";
        doReturn(headerItemText).when(headerItem).getText();

        view.setItems(Arrays.asList(textItem, dividerItem, headerItem));

        verify(itemsContainer).appendChild(textElement);
        verify(itemsContainer).appendChild(dividerElement);
        verify(itemsContainer).appendChild(headerElement);

        verify(textItemView).setEnabled(eq(textItemEnabled));
        verify(textItemView).setText(eq(textItemText));

        verify(headerItemView).setText(eq(headerItemText));

        final ArgumentCaptor<Command> commandCaptor = ArgumentCaptor.forClass(Command.class);
        verify(textItemView).addClickHandler(commandCaptor.capture());

        // enabled item
        commandCaptor.getValue().execute();
        verify(presenter).onItemSelected(eq(textItem));

        // disabled item
        reset(presenter);
        doReturn(false).when(textItem).isEnabled();
        commandCaptor.getValue().execute();
        verify(presenter, never()).onItemSelected(any(HasListSelectorControl.ListSelectorTextItem.class));
    }

    @Test
    public void testSetItemsUnknownImplementation() {
        view.setItems(Arrays.asList(mock(HasListSelectorControl.ListSelectorItem.class)));

        verify(itemsContainer, never()).appendChild(any());
    }
}
