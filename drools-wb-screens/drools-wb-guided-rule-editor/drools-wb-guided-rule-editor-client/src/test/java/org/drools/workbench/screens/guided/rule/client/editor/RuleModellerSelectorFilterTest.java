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
package org.drools.workbench.screens.guided.rule.client.editor;

import java.util.function.Consumer;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class RuleModellerSelectorFilterTest {

    private static final String FILTER = "filter";

    @Mock
    private Consumer<String> filterChangeConsumer;

    @Mock
    private KeyDownEvent keyDownEvent;

    @Mock
    private ClickEvent clickEvent;

    @GwtMock
    private TextBox txtSearch;

    @GwtMock
    private Button btnSearch;

    @Captor
    private ArgumentCaptor<KeyDownHandler> keyDownHandlerArgumentCaptor;

    @Captor
    private ArgumentCaptor<ClickHandler> clickHandlerArgumentCaptor;

    private RuleModellerSelectorFilter widget;

    @Before
    public void setup() {
        this.widget = new RuleModellerSelectorFilter();
        this.widget.setFilterChangeConsumer(filterChangeConsumer);

        when(txtSearch.getText()).thenReturn(FILTER);
    }

    @Test
    public void testKeyDownEvent_KeyEnter() {
        verify(txtSearch).addKeyDownHandler(keyDownHandlerArgumentCaptor.capture());

        final KeyDownHandler keyDownHandler = keyDownHandlerArgumentCaptor.getValue();

        when(keyDownEvent.getNativeKeyCode()).thenReturn(KeyCodes.KEY_ENTER);
        keyDownHandler.onKeyDown(keyDownEvent);

        verify(filterChangeConsumer).accept(FILTER);
    }

    @Test
    public void testKeyDownEvent_KeyOther() {
        verify(txtSearch).addKeyDownHandler(keyDownHandlerArgumentCaptor.capture());

        final KeyDownHandler keyDownHandler = keyDownHandlerArgumentCaptor.getValue();

        when(keyDownEvent.getNativeKeyCode()).thenReturn(KeyCodes.KEY_A);
        keyDownHandler.onKeyDown(keyDownEvent);

        verify(filterChangeConsumer, never()).accept(anyString());
    }

    @Test
    public void testSearchButtonClick() {
        verify(btnSearch).addClickHandler(clickHandlerArgumentCaptor.capture());

        final ClickHandler clickHandler = clickHandlerArgumentCaptor.getValue();

        clickHandler.onClick(clickEvent);

        verify(filterChangeConsumer).accept(FILTER);
    }

    @Test
    public void testGetFilterText() {
        assertThat(widget.getFilterText()).isEqualTo(FILTER);
    }
}
