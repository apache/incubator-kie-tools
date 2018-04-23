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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.CSSStyleDeclaration;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.TextArea;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TextEditorMultiLineBoxViewTest {

    private static final String NAME = "name";

    @Mock
    private TextEditorMultiLineBoxView.Presenter presenter;

    @Mock
    private Event event;

    @Mock
    private TranslationService translationService;

    @Mock
    private ClickEvent clickEvent;

    @Mock
    private Div editNameBox;

    @Mock
    private TextArea nameField;

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

    private TextEditorMultiLineBoxView tested;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        this.tested = new TextEditorMultiLineBoxView(translationService, editNameBox, nameField, showCommand, hideCommand, closeButton, saveButton);
        this.tested.init(presenter);
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
        when(event.getTypeInt()).thenReturn(Event.ONCHANGE);
        tested.onChangeName(event);
        verify(presenter, times(1)).onChangeName(anyString());
    }

    @Test
    public void testOnChangeNameKeyPressEvent() {
        when(event.getTypeInt()).thenReturn(Event.ONKEYPRESS);
        tested.onChangeName(event);
        verify(presenter, times(1)).onKeyPress(anyInt(), eq(false), anyString());
    }

    @Test
    public void testClickCloseButton() {
        tested.onClose(clickEvent);
        verify(presenter,
               times(1)).onClose();
    }

    @Test
    public void testShow() {

        tested.show(NAME);

        verify(nameField,
               times(1)).setValue(eq(NAME));
        verify(nameField,
               times(1)).setTextContent(eq(NAME));

        verify(nameField,
               times(1)).setRows(eq(2));
        verify(nameField,
               times(1)).setCols(eq(25));

        verify(showCommand, times(1)).execute();
    }

    @Test
    public void testHide() {
        tested.hide();
        verify(hideCommand, times(1)).execute();
    }
}