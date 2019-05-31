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

package org.kie.workbench.common.dmn.client.editors.types.shortcuts;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.KeyboardEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.selectpicker.JQuery;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeShortcuts.SELECT_DATATYPE_MENU;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeShortcutsTest {

    @Mock
    private DataTypeListShortcuts listShortcuts;

    @Mock
    private KeyboardEvent event;

    private DataTypeShortcuts shortcuts;

    @Before
    public void setup() {
        shortcuts = spy(new DataTypeShortcuts(listShortcuts));
    }

    @Test
    public void testSetup() {
        doNothing().when(shortcuts).addEventListener(anyString(), any());

        doReturn(false).when(shortcuts).isLoaded();

        shortcuts.setup();

        verify(shortcuts).addEventListener(KEYDOWN, shortcuts.KEY_DOWN_LISTENER);
        verify(shortcuts).addEventListener(KEYDOWN, shortcuts.KEY_DOWN_LISTENER);
    }

    @Test
    public void testSetupWhenItIsLoaded() {
        doNothing().when(shortcuts).addEventListener(anyString(), any());

        doReturn(true).when(shortcuts).isLoaded();

        shortcuts.setup();

        verify(shortcuts, never()).addEventListener(KEYDOWN, shortcuts.KEY_DOWN_LISTENER);
        verify(shortcuts, never()).addEventListener(KEYDOWN, shortcuts.KEY_DOWN_LISTENER);
    }

    @Test
    public void testTeardown() {
        doNothing().when(shortcuts).removeEventListener(anyString(), any());

        doReturn(true).when(shortcuts).isLoaded();

        shortcuts.teardown();

        verify(shortcuts).removeEventListener(KEYDOWN, shortcuts.KEY_DOWN_LISTENER);
        verify(shortcuts).removeEventListener(CLICK, shortcuts.CLICK_LISTENER);
    }

    @Test
    public void testTeardownWhenItIsNotLoaded() {
        doNothing().when(shortcuts).removeEventListener(anyString(), any());

        doReturn(false).when(shortcuts).isLoaded();

        shortcuts.teardown();

        verify(shortcuts, never()).removeEventListener(KEYDOWN, shortcuts.KEY_DOWN_LISTENER);
        verify(shortcuts, never()).removeEventListener(CLICK, shortcuts.CLICK_LISTENER);
    }

    @Test
    public void testClickListenerWhenTabContentContainsTarget() {

        final Event target = mock(Event.class);
        final Element targetElement = mock(Element.class);
        final Element tabContentElement = mock(Element.class);
        final JQuery jQuery = mock(JQuery.class);

        JQuery.$ = jQuery;
        target.target = targetElement;
        doReturn(tabContentElement).when(shortcuts).querySelector(".tab-content");
        when(jQuery.contains(tabContentElement, targetElement)).thenReturn(true);

        shortcuts.clickListener(target);

        verify(listShortcuts).focusIn();
        verify(listShortcuts, never()).reset();
    }

    @Test
    public void testClickListenerWhenDropdownMenuContainsTarget() {

        final Event target = mock(Event.class);
        final Element targetElement = mock(Element.class);
        final Element targetMenu = mock(Element.class);
        final JQuery jQuery = mock(JQuery.class);

        JQuery.$ = jQuery;
        target.target = targetElement;
        doReturn(targetMenu).when(targetElement).closest(SELECT_DATATYPE_MENU);
        doReturn(false).when(shortcuts).tabContentContainsTarget(target);

        shortcuts.clickListener(target);

        verify(listShortcuts).focusIn();
        verify(listShortcuts, never()).reset();
    }

    @Test
    public void testClickListenerWhenDatatypeElementIsClicked() {

        final Event target = mock(Event.class);
        final Element targetElement = mock(Element.class);
        final Element targetMenu = mock(Element.class);
        final Element dataTypeRowElement = mock(Element.class);
        final JQuery jQuery = mock(JQuery.class);

        JQuery.$ = jQuery;
        target.target = targetElement;
        doReturn(targetMenu).when(targetElement).closest(SELECT_DATATYPE_MENU);
        doReturn(false).when(shortcuts).tabContentContainsTarget(target);
        doReturn(dataTypeRowElement).when(targetElement).closest(".list-group-item");

        shortcuts.clickListener(target);

        verify(listShortcuts).focusIn();
        verify(listShortcuts).highlight(dataTypeRowElement);
        verify(listShortcuts, never()).reset();
    }

    @Test
    public void testClickListenerWhenTabContentDoesNotContainTarget() {

        final Event target = mock(Event.class);
        final Element targetElement = mock(Element.class);
        final Element tabContentElement = mock(Element.class);
        final JQuery jQuery = mock(JQuery.class);

        JQuery.$ = jQuery;
        target.target = targetElement;
        doReturn(tabContentElement).when(shortcuts).querySelector(".tab-content");
        when(jQuery.contains(tabContentElement, targetElement)).thenReturn(false);

        shortcuts.clickListener(target);

        verify(listShortcuts).reset();
        verify(listShortcuts, never()).focusIn();
    }

    @Test
    public void testClickListenerWhenComponentIsNotEnabled() {
        shortcuts.disable();
        shortcuts.clickListener(mock(Event.class));

        verifyNoMoreInteractions(listShortcuts);
    }

    @Test
    public void testKeyDownListenerWhenKeyEscIsPressed() {

        event.key = "Esc";

        shortcuts.keyDownListener(event);

        verify(listShortcuts).onEscape();
    }

    @Test
    public void testKeyDownListenerWhenKeyEscapeIsPressed() {

        event.key = "Escape";

        shortcuts.keyDownListener(event);

        verify(listShortcuts).onEscape();
    }

    @Test
    public void testKeyDownListenerWhenKeyBackspaceIsPressed() {

        event.key = "Backspace";
        event.ctrlKey = true;

        shortcuts.keyDownListener(event);

        verify(listShortcuts).onCtrlBackspace();
    }

    @Test
    public void testKeyDownListenerWhenKeyBackspaceIsPressedButAltIsNotPressed() {

        event.key = "Backspace";
        event.ctrlKey = false;

        shortcuts.keyDownListener(event);

        verify(listShortcuts, never()).onCtrlBackspace();
    }

    @Test
    public void testKeyDownListenerWhenKeyTabIsPressed() {

        event.key = "Tab";

        doReturn(true).when(shortcuts).isSearchBarTarget(any());

        shortcuts.keyDownListener(event);

        verify(event).preventDefault();
        verify(listShortcuts).onTab();
    }

    @Test
    public void testKeyDownListenerWhenKeyTabIsPressedButTargetElementIsNotSearchBar() {

        event.key = "Tab";

        doReturn(false).when(shortcuts).isSearchBarTarget(any());

        shortcuts.keyDownListener(event);

        verify(event, never()).preventDefault();
        verify(listShortcuts, never()).onTab();
    }

    @Test
    public void testKeyDownListenerWhenKeySIsPressed() {

        event.key = "s";
        event.ctrlKey = true;

        shortcuts.keyDownListener(event);

        verify(event).preventDefault();
        verify(listShortcuts).onCtrlS();
    }

    @Test
    public void testKeyDownListenerWhenKeySIsPressedButAltIsNotPressed() {

        event.key = "s";
        event.ctrlKey = false;

        shortcuts.keyDownListener(event);

        verify(event, never()).preventDefault();
        verify(listShortcuts, never()).onCtrlS();
    }

    @Test
    public void testKeyDownListenerWhenKeyBIsPressed() {

        event.key = "b";
        event.ctrlKey = true;

        shortcuts.keyDownListener(event);

        verify(event).preventDefault();
        verify(listShortcuts).onCtrlB();
    }

    @Test
    public void testKeyDownListenerWhenKeyBIsPressedButAltIsNotPressed() {

        event.key = "b";
        event.ctrlKey = false;

        shortcuts.keyDownListener(event);

        verify(event, never()).preventDefault();
        verify(listShortcuts, never()).onCtrlB();
    }

    @Test
    public void testKeyDownListenerWhenKeyUIsPressed() {

        event.key = "u";
        event.ctrlKey = true;

        shortcuts.keyDownListener(event);

        verify(event).preventDefault();
        verify(listShortcuts).onCtrlU();
    }

    @Test
    public void testKeyDownListenerWhenKeyUIsPressedButAltIsNotPressed() {

        event.key = "u";
        event.ctrlKey = false;

        shortcuts.keyDownListener(event);

        verify(event, never()).preventDefault();
        verify(listShortcuts, never()).onCtrlU();
    }

    @Test
    public void testKeyDownListenerWhenKeyDIsPressed() {

        event.key = "d";
        event.ctrlKey = true;

        shortcuts.keyDownListener(event);

        verify(event).preventDefault();
        verify(listShortcuts).onCtrlD();
    }

    @Test
    public void testKeyDownListenerWhenKeyDIsPressedButAltIsNotPressed() {

        event.key = "d";
        event.ctrlKey = false;

        shortcuts.keyDownListener(event);

        verify(event, never()).preventDefault();
        verify(listShortcuts, never()).onCtrlD();
    }

    @Test
    public void testKeyDownListenerWhenKeyEIsPressed() {

        event.key = "e";
        event.ctrlKey = true;

        shortcuts.keyDownListener(event);

        verify(event).preventDefault();
        verify(listShortcuts).onCtrlE();
    }

    @Test
    public void testKeyDownListenerWhenKeyEIsPressedButAltIsNotPressed() {

        event.key = "e";
        event.ctrlKey = false;

        shortcuts.keyDownListener(event);

        verify(event, never()).preventDefault();
        verify(listShortcuts, never()).onCtrlE();
    }

    @Test
    public void testKeyDownListenerWhenIsAnInputEventAndSearchBarIsNotTarget() {

        doReturn(true).when(shortcuts).isInputEvent(any());
        doReturn(false).when(shortcuts).isSearchBarTarget(any());

        event.key = "Down";

        shortcuts.keyDownListener(event);

        verifyNoMoreInteractions(event);
        verifyNoMoreInteractions(listShortcuts);
    }

    @Test
    public void testKeyDownListenerWhenKeDownIsPressed() {

        doReturn(false).when(shortcuts).isInputEvent(any());
        doReturn(false).when(shortcuts).isSearchBarTarget(any());

        event.key = "Down";
        event.ctrlKey = false;

        shortcuts.keyDownListener(event);

        verify(listShortcuts).onArrowDown();
    }

    @Test
    public void testKeyDownListenerWhenKeyArrowDownIsPressed() {

        doReturn(false).when(shortcuts).isInputEvent(any());
        doReturn(false).when(shortcuts).isSearchBarTarget(any());

        event.key = "ArrowDown";
        event.ctrlKey = false;

        shortcuts.keyDownListener(event);

        verify(listShortcuts).onArrowDown();
    }

    @Test
    public void testKeyDownListenerWhenKeyUpIsPressed() {

        doReturn(false).when(shortcuts).isInputEvent(any());
        doReturn(false).when(shortcuts).isSearchBarTarget(any());

        event.key = "Up";
        event.ctrlKey = false;

        shortcuts.keyDownListener(event);

        verify(listShortcuts).onArrowUp();
    }

    @Test
    public void testKeyDownListenerWhenKeyArrowUpIsPressed() {

        doReturn(false).when(shortcuts).isInputEvent(any());
        doReturn(false).when(shortcuts).isSearchBarTarget(any());

        event.key = "ArrowUp";
        event.ctrlKey = false;

        shortcuts.keyDownListener(event);

        verify(listShortcuts).onArrowUp();
    }

    @Test
    public void testKeyDownListenerWhenKeyLeftIsPressed() {

        doReturn(false).when(shortcuts).isInputEvent(any());
        doReturn(false).when(shortcuts).isSearchBarTarget(any());

        event.key = "Left";
        event.ctrlKey = false;

        shortcuts.keyDownListener(event);

        verify(listShortcuts).onArrowLeft();
    }

    @Test
    public void testKeyDownListenerWhenKeyArrowLeftIsPressed() {

        doReturn(false).when(shortcuts).isInputEvent(any());
        doReturn(false).when(shortcuts).isSearchBarTarget(any());

        event.key = "ArrowLeft";
        event.ctrlKey = false;

        shortcuts.keyDownListener(event);

        verify(listShortcuts).onArrowLeft();
    }

    @Test
    public void testKeyDownListenerWhenComponentIsNotEnabled() {
        shortcuts.disable();
        shortcuts.keyDownListener(event);

        verifyNoMoreInteractions(listShortcuts);
    }

    @Test
    public void testKeyDownListenerWhenKeyRightIsPressed() {

        doReturn(false).when(shortcuts).isInputEvent(any());
        doReturn(false).when(shortcuts).isSearchBarTarget(any());

        event.key = "Right";
        event.ctrlKey = false;

        shortcuts.keyDownListener(event);

        verify(listShortcuts).onArrowRight();
    }

    @Test
    public void testKeyDownListenerWhenKeyArrowRightIsPressed() {

        doReturn(false).when(shortcuts).isInputEvent(any());
        doReturn(false).when(shortcuts).isSearchBarTarget(any());

        event.key = "ArrowRight";
        event.ctrlKey = false;

        shortcuts.keyDownListener(event);

        verify(listShortcuts).onArrowRight();
    }

    @Test
    public void testIsTargetElementAnInputWhenItReturnsTrue() {

        event.target = mock(HTMLInputElement.class);

        assertTrue(shortcuts.isTargetElementAnInput(event));
    }

    @Test
    public void testIsTargetElementAnInputWhenItReturnsFalse() {

        event.target = mock(HTMLDivElement.class);

        assertFalse(shortcuts.isTargetElementAnInput(event));
    }

    @Test
    public void testIsDropdownOpenedWhenItReturnsTrue() {

        doReturn(null).when(shortcuts).querySelector(".bs-container.btn-group.bootstrap-select.open");

        assertFalse(shortcuts.isDropdownOpened());
    }

    @Test
    public void testIsDropdownOpenedWhenItReturnsFalse() {

        doReturn(mock(Element.class)).when(shortcuts).querySelector(".bs-container.btn-group.bootstrap-select.open");

        assertTrue(shortcuts.isDropdownOpened());
    }

    @Test
    public void testIsSearchBarTargetWhenItReturnsTrue() {

        final HTMLInputElement element = mock(HTMLInputElement.class);
        event.target = element;

        when(element.getAttribute("data-field")).thenReturn("search-bar");

        assertTrue(shortcuts.isTargetElementAnInput(event));
    }

    @Test
    public void testIsSearchBarTargetWhenItReturnsFalse() {

        final HTMLDivElement element = mock(HTMLDivElement.class);
        event.target = element;

        when(element.getAttribute("data-field")).thenReturn("something");

        assertFalse(shortcuts.isSearchBarTarget(event));
    }

    @Test
    public void testIsInputEventWhenTargetElementIsNotAnInputAndDropdownIsNotOpened() {

        doReturn(false).when(shortcuts).isTargetElementAnInput(any());
        doReturn(false).when(shortcuts).isDropdownOpened();

        assertFalse(shortcuts.isInputEvent(event));
    }

    @Test
    public void testIsInputEventWhenTargetElementIsAnInput() {

        doReturn(true).when(shortcuts).isTargetElementAnInput(any());
        doReturn(false).when(shortcuts).isDropdownOpened();

        assertTrue(shortcuts.isInputEvent(event));
    }

    @Test
    public void testIsInputEventWhenDropdownIsOpened() {

        doReturn(false).when(shortcuts).isTargetElementAnInput(any());
        doReturn(true).when(shortcuts).isDropdownOpened();

        assertTrue(shortcuts.isInputEvent(event));
    }

    @Test
    public void testIsInputEventWhenTargetElementIsAnInputAndDropdownIsOpened() {

        doReturn(true).when(shortcuts).isTargetElementAnInput(any());
        doReturn(true).when(shortcuts).isDropdownOpened();

        assertTrue(shortcuts.isInputEvent(event));
    }

    @Test
    public void testIsNotEnabledWhenItReturnsFalse() {
        shortcuts.enable();
        assertFalse(shortcuts.isNotEnabled());
    }

    @Test
    public void testIsNotEnabledWhenItReturnsTrue() {
        shortcuts.disable();
        assertTrue(shortcuts.isNotEnabled());
    }
}
