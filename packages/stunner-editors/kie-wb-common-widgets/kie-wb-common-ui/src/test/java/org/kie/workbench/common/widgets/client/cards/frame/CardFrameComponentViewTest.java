/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.cards.frame;

import elemental2.dom.*;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.KeyCodes;
import org.gwtproject.event.dom.client.KeyDownEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

//@RunWith(GwtMockitoTestRunner.class)
public class CardFrameComponentViewTest {

    @Mock
    private HTMLDivElement view;

    @Mock
    private HTMLElement icon;

    @Mock
    private HTMLHeadingElement titleText;

    @Mock
    private HTMLInputElement titleInput;

    @Mock
    private HTMLButtonElement inputCloseButton;

    @Mock
    private HTMLDivElement editMode;

    @Mock
    private HTMLButtonElement okButton;

    @Mock
    private HTMLButtonElement closeButton;

    @Mock
    private HTMLDivElement content;

    @Mock
    private CardFrameComponent presenter;

    private CardFrameComponentView cardFrameView;

    @Before
    public void setup() {
        cardFrameView = spy(new CardFrameComponentView(view, icon, titleText, titleInput, inputCloseButton, editMode, okButton, closeButton, content));
        cardFrameView.init(presenter);
    }

    //@Test
    public void testOnTitleTextClick() {
        doNothing().when(cardFrameView).enableEditMode();
        cardFrameView.onTitleTextClick(mock(Event.class));
        verify(presenter).enableEditMode();
    }

    //@Test
    public void testOnOkButtonClick() {
        cardFrameView.onOkButtonClick(mock(Event.class));
        verify(presenter).changeTitle();
    }

    //@Test
    public void testOnCloseButtonClick() {
        cardFrameView.onCloseButtonClick(mock(Event.class));
        verify(presenter).refreshView();
    }

    //@Test
    public void testOnInputCloseButtonClick() {
        titleInput.value = "something";
        cardFrameView.onInputCloseButtonClick(mock(Event.class));
        assertEquals("", titleInput.value);
    }

    //@Test
    public void testOnTitleInputKeyDownEventWhenIsEscape() {

        final KeyboardEvent event = mock(KeyboardEvent.class);
        event.code = "Enter";
        cardFrameView.onTitleInputKeyDownEvent(event);
        verify(event).preventDefault();
        verify(presenter).refreshView();
    }

    //@Test
    public void testOnTitleInputKeyDownEventWhenIsEnter() {

        final KeyboardEvent event = mock(KeyboardEvent.class);
        event.code = "Enter";

        cardFrameView.onTitleInputKeyDownEvent(event);

        verify(event).preventDefault();
        verify(presenter).changeTitle();
    }

    //@Test
    public void testOnTitleInputKeyDownEventWhenIsNotEnterAndIsNotEscape() {

        final KeyboardEvent event = mock(KeyboardEvent.class);
        event.ctrlKey = true;
        cardFrameView.onTitleInputKeyDownEvent(event);

        verify(event, never()).preventDefault();
        verifyNoMoreInteractions(presenter);
    }

    //@Test
    public void testSetUUID() {

        final String uuid = "uuid";

        cardFrameView.setUUID(uuid);

        view.setAttribute(CardFrameComponentView.CARD_UUID_ATTR, uuid);
    }

    //@Test
    public void testSetIcon() {

        icon.classList = mock(DOMTokenList.class);
        final String cssClassName = "fa-download";

        cardFrameView.setIcon(cssClassName);

        verify(icon.classList).add(cssClassName);
    }

    //@Test
    public void testSetTitle() {

        final String title = "title";

        titleText.textContent = "something";
        titleInput.value = "something";

        cardFrameView.setTitle(title);

        assertEquals(title, titleText.textContent);
        assertEquals(title, titleInput.value);
    }

    //@Test
    public void testGetTitle() {

        final String expectedTitle = "title";
        titleInput.value = expectedTitle;

        final String actualTitle = cardFrameView.getTitle();

        assertEquals(expectedTitle, actualTitle);
    }

    //@Test
    public void testSetContent() {

        final HTMLElement content = mock(HTMLElement.class);

        cardFrameView.setContent(content);

        verify(this.content).appendChild(content);
    }

    //@Test
    public void testEnableReadOnlyMode() {

        titleText.hidden = true;
        editMode.hidden = false;

        cardFrameView.enableReadOnlyMode();

        assertFalse(titleText.hidden);
        assertTrue(editMode.hidden);
    }

    //@Test
    public void testEnableEditMode() {

        titleText.hidden = false;
        editMode.hidden = true;

        cardFrameView.enableEditMode();

        assertTrue(titleText.hidden);
        assertFalse(editMode.hidden);
        verify(titleInput).focus();
    }
}
