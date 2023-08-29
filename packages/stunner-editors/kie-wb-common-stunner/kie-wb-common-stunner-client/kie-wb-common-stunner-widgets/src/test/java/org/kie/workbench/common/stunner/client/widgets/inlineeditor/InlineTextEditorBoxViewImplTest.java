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


package org.kie.workbench.common.stunner.client.widgets.inlineeditor;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.CSSStyleDeclaration;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class InlineTextEditorBoxViewImplTest {

    public static final String NAME = "MyComponent";
    public static final String CONTENT = "Content\nContent";
    public static final String NAME_BR = "MyComponent<br>";
    public static final String CONTENT_BR = "Content<br>Content";
    public static final double BOX_WIDTH = 50d;
    public static final double BOX_HEIGHT = 50d;
    public static final String FONT_FAMILY = "verdana";
    public static final double FONT_SIZE = 14d;
    public static final String ALIGN_MIDDLE = "MIDDLE";
    public static final String ALIGN_LEFT = "LEFT";
    public static final String ALIGN_TOP = "TOP";

    @Mock
    private InlineTextEditorBoxViewImpl.Presenter presenter;

    @Mock
    private Event event;

    @Mock
    private TranslationService translationService;

    @Mock
    private Div editNameBox;

    @Mock
    private Div nameField;

    @Mock
    private CSSStyleDeclaration editNameBoxStyle;

    @Mock
    private CSSStyleDeclaration nameFieldStyle;

    @Mock
    private Command showCommand;

    @Mock
    private Command hideCommand;

    private InlineTextEditorBoxViewImpl tested;

    @Mock
    private org.jboss.errai.common.client.dom.HTMLElement parentElement;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        this.tested = spy(new InlineTextEditorBoxViewImpl(translationService, editNameBox, nameField, showCommand, hideCommand));
        this.tested.init(presenter);
        doNothing().when(tested).selectText(any());

        when(editNameBox.getStyle()).thenReturn(editNameBoxStyle);
        when(nameField.getStyle()).thenReturn(nameFieldStyle);
        when(editNameBox.getParentElement()).thenReturn(parentElement);
        doAnswer(i -> {
            ((Scheduler.ScheduledCommand) i.getArguments()[0]).execute();
            return null;
        }).when(tested).scheduleDeferredCommand(any());
    }

    @Test
    public void testInlineTextEditorBoxViewImpl() {
        tested = spy(new InlineTextEditorBoxViewImpl(translationService));
        assertEquals(translationService, tested.translationService);
    }

    @Test
    public void testOnBlurEvent() {
        when(event.getTypeInt()).thenReturn(Event.ONBLUR);
        when(nameField.getInnerHTML()).thenReturn(NAME);
        doAnswer(i -> true).when(tested).isVisible();
        tested.onChangeName(event);

        verify(presenter,
               times(1)).onChangeName(any());
        verify(presenter,
               times(1)).onSave();
    }

    @Test
    public void testOnBlurEventNotVisible() {
        doAnswer(i -> false).when(tested).isVisible();
        when(event.getTypeInt()).thenReturn(Event.ONBLUR);
        tested.onChangeName(event);

        verify(presenter,
               never()).onSave();
    }

    @Test
    public void testOnKeyDownEscEvent() {
        when(event.getTypeInt()).thenReturn(Event.ONKEYDOWN);
        when(event.getKeyCode()).thenReturn(KeyCodes.KEY_ESCAPE);
        doAnswer(i -> true).when(tested).isVisible();

        tested.onChangeName(event);

        verify(presenter,
               times(1)).onClose();
    }

    @Test
    public void testUntreatedEvent() {
        when(event.getTypeInt()).thenReturn(Event.ONFOCUS);
        doAnswer(i -> true).when(tested).isVisible();
        tested.onChangeName(event);

        verify(presenter, never()).onSave();
        verify(presenter, never()).onClose();
        verify(presenter, never()).onChangeName(anyString());
    }

    @Test
    public void testNonKeyDownEventPropagation() {
        when(event.getTypeInt()).thenReturn(Event.ONKEYDOWN);
        when(event.getKeyCode()).thenReturn(KeyCodes.KEY_A);
        when(event.getShiftKey()).thenReturn(false);
        when(nameField.getInnerHTML()).thenReturn(NAME);
        doAnswer(i -> true).when(tested).isVisible();
        tested.onChangeName(event);

        verify(event,
               times(1)).stopPropagation();
    }

    @Test
    public void testNonKeyUpEventPropagation() {
        when(event.getTypeInt()).thenReturn(Event.ONKEYUP);
        when(event.getKeyCode()).thenReturn(KeyCodes.KEY_A);
        when(event.getShiftKey()).thenReturn(false);
        when(nameField.getInnerHTML()).thenReturn(NAME);
        doAnswer(i -> true).when(tested).isVisible();
        tested.onChangeName(event);

        verify(event,
               times(1)).stopPropagation();
    }

    @Test
    public void testNonKeyPressEventPropagation() {
        when(event.getTypeInt()).thenReturn(Event.ONKEYPRESS);
        when(event.getKeyCode()).thenReturn(KeyCodes.KEY_A);
        when(event.getShiftKey()).thenReturn(false);
        when(nameField.getInnerHTML()).thenReturn(NAME);
        doAnswer(i -> true).when(tested).isVisible();
        tested.onChangeName(event);

        verify(event,
               times(1)).stopPropagation();
    }

    @Test
    public void testOnKeyDownEnterEvent() {
        when(event.getTypeInt()).thenReturn(Event.ONKEYDOWN);
        when(event.getKeyCode()).thenReturn(KeyCodes.KEY_ENTER);
        when(event.getShiftKey()).thenReturn(false);
        when(nameField.getInnerHTML()).thenReturn(NAME);
        doAnswer(i -> true).when(tested).isVisible();
        tested.onChangeName(event);

        verify(presenter,
               times(1)).onChangeName(any());
        verify(presenter,
               times(1)).onSave();
    }

    @Test
    public void testOnKeyDownEnterEventFirefoxEndsWithBR() {
        when(event.getTypeInt()).thenReturn(Event.ONKEYDOWN);
        when(event.getKeyCode()).thenReturn(KeyCodes.KEY_ENTER);
        when(event.getShiftKey()).thenReturn(false);
        when(nameField.getInnerHTML()).thenReturn(NAME_BR);
        doAnswer(i -> true).when(tested).isVisible();
        tested.onChangeName(event);

        verify(presenter,
               times(1)).onChangeName(NAME);
        verify(presenter,
               times(1)).onSave();
    }

    @Test
    public void testOnKeyDownEnterEventFirefoxContentBR() {
        when(event.getTypeInt()).thenReturn(Event.ONKEYDOWN);
        when(event.getKeyCode()).thenReturn(KeyCodes.KEY_ENTER);
        when(event.getShiftKey()).thenReturn(false);
        when(nameField.getInnerHTML()).thenReturn(CONTENT_BR);
        doAnswer(i -> true).when(tested).isVisible();
        tested.onChangeName(event);

        verify(presenter,
               times(1)).onChangeName(CONTENT);
        verify(presenter,
               times(1)).onSave();
    }

    @Test
    public void testOnKeyDownShiftEnterIsMultilineEvent() {
        tested.setMultiline(true);
        when(nameField.getTextContent()).thenReturn(NAME);
        when(event.getTypeInt()).thenReturn(Event.ONKEYDOWN);
        when(event.getKeyCode()).thenReturn(KeyCodes.KEY_ENTER);
        when(event.getShiftKey()).thenReturn(true);
        doAnswer(i -> true).when(tested).isVisible();
        tested.onChangeName(event);

        verify(presenter,
               never()).onChangeName(NAME);
    }

    @Test
    public void testOnKeyDownShiftEnterNotMultilineEvent() {
        tested.setMultiline(false);
        when(nameField.getTextContent()).thenReturn(NAME);
        when(event.getTypeInt()).thenReturn(Event.ONKEYDOWN);
        when(event.getKeyCode()).thenReturn(KeyCodes.KEY_ENTER);
        when(event.getShiftKey()).thenReturn(true);
        doAnswer(i -> true).when(tested).isVisible();
        tested.onChangeName(event);

        verify(presenter,
               never()).onChangeName(NAME);
    }

    @Test
    public void testOnKeyDownTabEvent() {
        when(nameField.getTextContent()).thenReturn(NAME);
        when(event.getTypeInt()).thenReturn(Event.ONKEYDOWN);
        when(event.getKeyCode()).thenReturn(KeyCodes.KEY_TAB);
        doAnswer(i -> true).when(tested).isVisible();
        tested.onChangeName(event);

        verify(presenter,
               never()).onChangeName(NAME);
    }

    @Test
    public void testInitialize() {
        tested.initialize();

        assertEquals(buildStyleString(InlineTextEditorBoxViewImpl.ALIGN_MIDDLE,
                                      BOX_WIDTH,
                                      BOX_HEIGHT,
                                      InlineTextEditorBoxViewImpl.DEFAULT_FONT_FAMILY,
                                      InlineTextEditorBoxViewImpl.DEFAULT_FONT_SIZE),
                     tested.buildStyle(BOX_WIDTH, BOX_HEIGHT));
    }

    @Test
    public void testSelectedTextOnEdit() {
        tested.setTextBoxInternalAlignment(ALIGN_MIDDLE);
        tested.show("Task", BOX_WIDTH, BOX_HEIGHT);

        verify(nameField,
               times(1)).setTextContent(eq("Task"));
        verify(showCommand, times(1)).execute();
        verify(tested, times(1)).selectText(any());
    }

    @Test
    public void testShowNullName() {
        tested.setTextBoxInternalAlignment(ALIGN_MIDDLE);
        tested.show(null, BOX_WIDTH, BOX_HEIGHT);

        verify(nameField,
               times(1)).setTextContent(eq(null));
        verify(showCommand, times(1)).execute();
    }

    @Test
    public void testShowEmptyName() {
        tested.setTextBoxInternalAlignment(ALIGN_MIDDLE);
        tested.show("", BOX_WIDTH, BOX_HEIGHT);

        verify(nameField,
               times(1)).setTextContent(eq(""));
        verify(showCommand, times(1)).execute();
    }

    @Test
    public void testShowAlignMiddle() {
        tested.setTextBoxInternalAlignment(ALIGN_MIDDLE);
        tested.show(NAME, BOX_WIDTH, BOX_HEIGHT);

        verify(nameField,
               times(1)).setTextContent(eq(NAME));
        verify(showCommand, times(1)).execute();
    }

    @Test
    public void testShowAlignLeft() {
        tested.setTextBoxInternalAlignment(ALIGN_LEFT);
        tested.show(NAME, BOX_WIDTH, BOX_HEIGHT);

        verify(nameField,
               times(1)).setTextContent(eq(NAME));
        verify(showCommand, times(1)).execute();
    }

    @Test
    public void testShowAlignTop() {
        tested.setTextBoxInternalAlignment(ALIGN_TOP);
        tested.show(NAME, BOX_WIDTH, BOX_HEIGHT);

        verify(nameField,
               times(1)).setTextContent(eq(NAME));
        verify(showCommand, times(1)).execute();
    }

    @Test
    public void testShowPlaceholder() {
        tested.setTextBoxInternalAlignment(ALIGN_MIDDLE);
        tested.setPlaceholder("any name");
        tested.show("", BOX_WIDTH, BOX_HEIGHT);

        verify(nameField,
               times(1)).setAttribute(eq("data-text"), eq("any name"));
        verify(showCommand, times(1)).execute();
    }

    @Test
    public void testBuildStyle() {
        tested.setFontFamily(FONT_FAMILY);
        tested.setFontSize(FONT_SIZE);
        tested.setMultiline(true);
        tested.setTextBoxInternalAlignment(InlineTextEditorBoxViewImpl.ALIGN_MIDDLE);

        assertEquals(buildStyleString(InlineTextEditorBoxViewImpl.ALIGN_MIDDLE,
                                      BOX_WIDTH,
                                      BOX_HEIGHT,
                                      FONT_FAMILY,
                                      FONT_SIZE),
                     tested.buildStyle(BOX_WIDTH, BOX_HEIGHT));
    }

    @Test
    public void testBuildStyleNoMatchAlign() {
        tested.setFontFamily(FONT_FAMILY);
        tested.setFontSize(FONT_SIZE);
        tested.setMultiline(true);
        tested.setTextBoxInternalAlignment("someAlign");

        assertEquals(buildStyleString("someAlign",
                                      BOX_WIDTH,
                                      BOX_HEIGHT,
                                      FONT_FAMILY,
                                      FONT_SIZE),
                     tested.buildStyle(BOX_WIDTH, BOX_HEIGHT));
    }

    private String buildStyleString(final String align,
                                    final double width,
                                    final double height,
                                    final String fontFamily,
                                    final double fontsize) {
        StringBuffer style = new StringBuffer();
        if (align.equals(InlineTextEditorBoxViewImpl.ALIGN_MIDDLE)) {
            style.append(InlineTextEditorBoxViewImpl.ALIGN_MIDDLE_STYLE);
        } else if (align.equals(InlineTextEditorBoxViewImpl.ALIGN_LEFT)) {
            style.append(InlineTextEditorBoxViewImpl.ALIGN_LEFT_STYLE);
        } else if (align.equals(InlineTextEditorBoxViewImpl.ALIGN_TOP)) {
            style.append(InlineTextEditorBoxViewImpl.TEXT_ALIGN_CENTER);
        }
        style.append("max-width: " + width + "px;" +
                             "max-height: " + height + "px;" +
                             "width: " + width + "px;");
        style.append("font-family: \"" + fontFamily + "\";" +
                             "font-size: " + fontsize + "px;");

        return style.toString();
    }

    @Test
    public void testHide() {
        tested.hide();
        verify(hideCommand, times(1)).execute();
    }
}