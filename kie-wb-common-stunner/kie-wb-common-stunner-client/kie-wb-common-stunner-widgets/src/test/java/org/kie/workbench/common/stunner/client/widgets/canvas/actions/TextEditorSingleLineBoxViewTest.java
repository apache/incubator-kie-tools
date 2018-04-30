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

package org.kie.workbench.common.stunner.client.widgets.canvas.actions;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.CSSStyleDeclaration;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TextEditorSingleLineBoxViewTest {

    private static final String NAME = "name";

    @Mock
    private TextEditorSingleLineBoxView.Presenter presenter;

    @Mock
    private Event event;

    @Mock
    private TranslationService translationService;

    @Mock
    private ClickEvent clickEvent;

    @Mock
    private Div editNameBox;

    @Mock
    private TextInput nameField;

    @Mock
    private HTMLElement htmlElement;

    @Mock
    private CSSStyleDeclaration style;

    @Mock
    private HTMLElement element;

    @Mock
    private Command showCommand;

    @Mock
    private Command hideCommand;

    @Mock
    private HTMLElement closeButton;

    @Mock
    private HTMLElement saveButton;

    private TextEditorSingleLineBoxView tested;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        this.tested = spy(new TextEditorSingleLineBoxView(translationService, editNameBox, nameField, showCommand, hideCommand, closeButton, saveButton));
        this.tested.init(presenter);

        doAnswer(i -> {
            ((Scheduler.ScheduledCommand) i.getArguments()[0]).execute();
            return null;
        }).when(tested).scheduleDeferredCommand(any(Scheduler.ScheduledCommand.class));
    }

    @Test
    public void testOnKeyDownEscEvent() {
        when(event.getTypeInt()).thenReturn(Event.ONKEYDOWN);
        when(event.getKeyCode()).thenReturn(KeyCodes.KEY_ESCAPE);
        tested.editNameBoxEsc(event);
        verify(presenter,
               times(1)).onClose();
    }

    @Test
    public void testInitialize() {
        tested.initialize();
        verify(nameField,
               times(1)).setAttribute(eq("placeHolder"), anyString());
    }

    @Test
    public void testOnMouseOver() {
        when(event.getTypeInt()).thenReturn(Event.ONMOUSEOVER);
        tested.editNameBoxEsc(event);
        verify(editNameBox,
               times(1)).focus();
    }

    @Test
    public void testClickSaveButton() {
        tested.onSave(clickEvent);
        verify(presenter,
               times(1)).onSave();
    }

    @Test
    public void testOnChangeNameChangeEvent() {
        when(nameField.getValue()).thenReturn(NAME);
        when(event.getTypeInt()).thenReturn(Event.ONCHANGE);
        tested.onChangeName(event);
        verify(presenter, times(1)).onChangeName(eq(NAME));
    }

    @Test
    public void testOnChangeNameKeyPressEvent() {
        when(nameField.getValue()).thenReturn(NAME);
        when(event.getTypeInt()).thenReturn(Event.ONKEYPRESS);
        when(event.getKeyCode()).thenReturn(KeyCodes.KEY_A);
        tested.onChangeName(event);
        verify(presenter, times(1)).onKeyPress(eq(KeyCodes.KEY_A), eq(false), eq(NAME));
    }

    @Test
    public void testOnChangeNameKeyDownEvent() {
        when(nameField.getValue()).thenReturn(NAME);
        when(event.getTypeInt()).thenReturn(Event.ONKEYDOWN);
        when(event.getKeyCode()).thenReturn(KeyCodes.KEY_A);
        tested.onChangeName(event);
        verify(presenter, times(1)).onKeyDown(eq(KeyCodes.KEY_A), eq(NAME));
    }

    @Test
    public void testShow() {
        tested.show(NAME);

        verify(nameField,
               times(1)).setValue(eq(NAME));
        verify(nameField,
               times(1)).setTextContent(eq(NAME));

        verify(showCommand, times(1)).execute();
    }

    @Test
    public void testHide() {
        tested.hide();
        verify(hideCommand, times(1)).execute();
    }
}