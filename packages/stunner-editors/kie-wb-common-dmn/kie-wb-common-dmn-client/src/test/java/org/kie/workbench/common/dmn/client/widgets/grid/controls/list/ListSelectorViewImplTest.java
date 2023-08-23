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

package org.kie.workbench.common.dmn.client.widgets.grid.controls.list;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.gwt.core.client.Scheduler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.KeyboardEvent;
import org.jboss.errai.common.client.dom.DOMTokenList;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.EventListener;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.CanBeClosedByKeyboard;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;
import static java.util.Collections.singletonList;
import static org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorViewImpl.OPEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private HTMLElement viewElement;

    @Mock
    private DOMTokenList viewElementCSSClasses;

    @Mock
    private Consumer<CanBeClosedByKeyboard> canBeClosedByKeyboardConsumer;

    @Mock
    private HasListSelectorControl.ListSelectorHeaderItem headerItem;

    private ListSelectorViewImpl view;

    @Before
    public void setUp() {
        view = spy(new ListSelectorViewImpl(itemsContainer,
                                            listSelectorTextItemViews,
                                            listSelectorDividerItemViews,
                                            listSelectorHeaderItemViews));

        doReturn(textItemView).when(listSelectorTextItemViews).get();
        doReturn(textElement).when(textItemView).getElement();
        doReturn(dividerItemView).when(listSelectorDividerItemViews).get();
        doReturn(dividerElement).when(dividerItemView).getElement();
        doReturn(headerItemView).when(listSelectorHeaderItemViews).get();
        doReturn(headerElement).when(headerItemView).getElement();
        doReturn(viewElement).when(view).getElement();

        view.init(presenter);

        when(viewElement.getClassList()).thenReturn(viewElementCSSClasses);

        doAnswer(i -> {
            ((Scheduler.ScheduledCommand) i.getArguments()[0]).execute();
            return null;
        }).when(view).schedule(any(Scheduler.ScheduledCommand.class));
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
        view.setItems(singletonList(mock(HasListSelectorControl.ListSelectorItem.class)));

        verify(itemsContainer, never()).appendChild(any());
    }

    @Test
    public void testShow() {
        view.show(Optional.empty());

        verify(viewElementCSSClasses).add(OPEN);
        verify(viewElement).focus();
    }

    @Test
    public void testHide() {
        view.hide();

        verify(viewElementCSSClasses).remove(OPEN);
    }

    @Test
    public void testRegisterOnCloseHandler() {

        final EventListener<Event> onKeyDown = (e) -> {/* Nothing. */};

        doReturn(onKeyDown).when(view).onKeyDown();

        view.registerOnCloseHandler();

        verify(viewElement).addEventListener(KEYDOWN, onKeyDown, false);
    }

    @Test
    public void testOnKeyDownWhenEscapeIsPressedOnIEOrEdge() {

        final Event event = mock(Event.class);
        final KeyboardEvent keyboardEvent = mock(KeyboardEvent.class);

        doReturn(keyboardEvent).when(view).asElemental2Event(event);
        keyboardEvent.key = "Esc";

        view.onKeyDown().call(event);

        verify(view).hide();
        verify(view).returnFocusToPanel();
    }

    @Test
    public void testOnKeyDownWhenEscapeIsPressedOnOtherBrowser() {

        final Event event = mock(Event.class);
        final KeyboardEvent keyboardEvent = mock(KeyboardEvent.class);

        doReturn(keyboardEvent).when(view).asElemental2Event(event);
        keyboardEvent.key = "Escape";

        view.onKeyDown().call(event);

        verify(view).hide();
        verify(view).returnFocusToPanel();
    }

    @Test
    public void testOnKeyDownWhenEscapeIsNotPressed() {

        final Event event = mock(Event.class);

        doReturn(false).when(view).isEscape(event);

        view.onKeyDown().call(event);

        verify(view, never()).hide();
        verify(view, never()).returnFocusToPanel();
    }

    @Test
    public void testReturnFocusToPanel() {

        view.setOnClosedByKeyboardCallback(canBeClosedByKeyboardConsumer);
        view.returnFocusToPanel();

        verify(canBeClosedByKeyboardConsumer).accept(view);
    }
}
