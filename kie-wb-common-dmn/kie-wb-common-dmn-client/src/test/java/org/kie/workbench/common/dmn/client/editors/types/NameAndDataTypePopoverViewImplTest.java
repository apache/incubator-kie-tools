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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.Optional;
import java.util.function.Consumer;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Event;
import elemental2.dom.KeyboardEvent;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.EventListener;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverViewImpl.ENTER_KEY;
import static org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverViewImpl.ESCAPE_KEY;
import static org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverViewImpl.ESC_KEY;
import static org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverViewImpl.MANAGE_BUTTON_SELECTOR;
import static org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverViewImpl.TAB_KEY;
import static org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverViewImpl.TYPE_SELECTOR_BUTTON_SELECTOR;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class NameAndDataTypePopoverViewImplTest {

    private static final String NAME = "name";

    @Mock
    private Input nameEditor;

    @Mock
    private DataTypePickerWidget dataTypeEditor;

    @Mock
    private Div popoverElement;

    @Mock
    private Div popoverContentElement;

    @Mock
    private Span nameLabel;

    @Mock
    private Span dataTypeLabel;

    @Mock
    private JQueryProducer.JQuery<Popover> jQueryProducer;

    @Mock
    private NameAndDataTypePopoverView.Presenter presenter;

    @Mock
    private HTMLElement element;

    @Mock
    private Decision decision;

    @Mock
    private QName typeRef;

    @Mock
    private ValueChangeEvent<QName> valueChangeEvent;

    @Mock
    private BlurEvent blurEvent;

    @Mock
    private Popover popover;

    @Mock
    private TranslationService translationService;

    @Mock
    private Button manageButton;

    @Mock
    private Button typeSelectorButton;

    @Captor
    private ArgumentCaptor<ValueChangeHandler<QName>> valueChangeHandlerCaptor;

    private NameAndDataTypePopoverViewImpl view;

    @Before
    public void setUp() {
        view = spy(new NameAndDataTypePopoverViewImpl(nameEditor,
                                                      dataTypeEditor,
                                                      popoverElement,
                                                      popoverContentElement,
                                                      nameLabel,
                                                      dataTypeLabel,
                                                      jQueryProducer,
                                                      translationService) {
            @Override
            public HTMLElement getElement() {
                return element;
            }
        });

        when(element.querySelector(MANAGE_BUTTON_SELECTOR)).thenReturn(manageButton);
        when(element.querySelector(TYPE_SELECTOR_BUTTON_SELECTOR)).thenReturn(typeSelectorButton);

        view.init(presenter);

        when(valueChangeEvent.getValue()).thenReturn(typeRef);
        when(jQueryProducer.wrap(element)).thenReturn(popover);

        doAnswer(i -> i.getArguments()[0]).when(translationService).getTranslation(anyString());
    }

    @Test
    public void testInit() {
        verify(dataTypeEditor).addValueChangeHandler(valueChangeHandlerCaptor.capture());

        valueChangeHandlerCaptor.getValue().onValueChange(valueChangeEvent);

        assertEquals(view.getCurrentTypeRef(), typeRef);

        verify(view).setKeyDownListeners();
    }

    @Test
    public void testSetKeyDownListeners() {

        final EventListener keyDownCallback = mock(EventListener.class);
        final EventListener managerCallback = mock(EventListener.class);
        final EventListener eventListenerCallback = mock(EventListener.class);

        doReturn(keyDownCallback).when(view).getKeyDownEventListener();
        doReturn(managerCallback).when(view).getManageButtonKeyDownEventListener();
        doReturn(eventListenerCallback).when(view).getTypeSelectorKeyDownEventListener();

        view.setKeyDownListeners();

        verify(popoverElement).addEventListener(BrowserEvents.KEYDOWN,
                                                keyDownCallback,
                                                false);

        verify(manageButton).addEventListener(BrowserEvents.KEYDOWN,
                                              managerCallback,
                                              false);

        verify(typeSelectorButton).addEventListener(BrowserEvents.KEYDOWN,
                                                    eventListenerCallback,
                                                    false);
    }

    @Test
    public void testTypeSelectorKeyDownEventListenerEnterKey() {

        final KeyboardEvent keyboardEvent = mock(KeyboardEvent.class);
        doReturn(true).when(view).isEnterKeyPressed(keyboardEvent);
        doNothing().when(view).hide(true);

        view.typeSelectorKeyDownEventListener(keyboardEvent);

        verify(view).hide(true);
        verify(keyboardEvent).preventDefault();
        verify(view).onClosedByKeyboard();
    }

    @Test
    public void testTypeSelectorKeyDownEventListenerEscKey() {

        final KeyboardEvent keyboardEvent = mock(KeyboardEvent.class);
        doReturn(false).when(view).isEnterKeyPressed(keyboardEvent);
        doReturn(true).when(view).isEscapeKeyPressed(keyboardEvent);
        final NameAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor monitor = mock(NameAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor.class);
        doReturn(monitor).when(view).getMonitor();
        final elemental2.dom.Element menuElement = mock(elemental2.dom.Element.class);
        doReturn(menuElement).when(monitor).getMenuElement();

        view.typeSelectorKeyDownEventListener(keyboardEvent);

        verify(view).reset();
        verify(view).hide(false);
        verify(view).onClosedByKeyboard();
    }

    @Test
    public void testTypeSelectorKeyDownEventListenerWhenIsNotAHandledKey() {

        final KeyboardEvent keyboardEvent = mock(KeyboardEvent.class);
        doReturn(false).when(view).isEnterKeyPressed(keyboardEvent);
        doReturn(false).when(view).isEscapeKeyPressed(keyboardEvent);
        doReturn(false).when(view).isTabKeyPressed(keyboardEvent);

        view.typeSelectorKeyDownEventListener(keyboardEvent);

        verify(view, never()).hide(anyBoolean());
        verify(keyboardEvent, never()).preventDefault();
        verify(view, never()).onClosedByKeyboard();
        verify(view, never()).reset();
        verify(manageButton, never()).focus();
        verify(nameEditor, never()).focus();
    }

    @Test
    public void testTypeSelectorKeyDownEventListenerWhenIsNotAKeyboardEvent() {

        final Event event = mock(Event.class);

        view.typeSelectorKeyDownEventListener(event);

        verify(view, never()).hide(anyBoolean());
        verify(event, never()).preventDefault();
        verify(view, never()).onClosedByKeyboard();
        verify(view, never()).reset();
        verify(manageButton, never()).focus();
        verify(nameEditor, never()).focus();
    }

    @Test
    public void testTypeSelectorKeyDownEventListenerTabKey() {

        final KeyboardEvent keyboardEvent = mock(KeyboardEvent.class);
        doReturn(false).when(view).isEnterKeyPressed(keyboardEvent);
        doReturn(false).when(view).isEscapeKeyPressed(keyboardEvent);
        doReturn(true).when(view).isTabKeyPressed(keyboardEvent);

        view.typeSelectorKeyDownEventListener(keyboardEvent);

        verify(nameEditor).focus();
        verify(keyboardEvent).preventDefault();
    }

    @Test
    public void testTypeSelectorKeyDownEventListenerShiftTabKey() {

        final KeyboardEvent keyboardEvent = mock(KeyboardEvent.class);
        keyboardEvent.shiftKey = true;

        doReturn(false).when(view).isEnterKeyPressed(keyboardEvent);
        doReturn(false).when(view).isEscapeKeyPressed(keyboardEvent);
        doReturn(true).when(view).isTabKeyPressed(keyboardEvent);

        view.typeSelectorKeyDownEventListener(keyboardEvent);

        verify(manageButton).focus();
        verify(keyboardEvent).preventDefault();
    }

    @Test
    public void testManagerButtonKeyDownEventListenerEsc() {

        final KeyboardEvent keyboardEvent = mock(KeyboardEvent.class);
        doReturn(false).when(view).isEnterKeyPressed(keyboardEvent);
        doReturn(true).when(view).isEscapeKeyPressed(keyboardEvent);
        final NameAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor monitor = mock(NameAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor.class);
        doReturn(monitor).when(view).getMonitor();
        final elemental2.dom.Element menuElement = mock(elemental2.dom.Element.class);
        doReturn(menuElement).when(monitor).getMenuElement();

        view.manageButtonKeyDownEventListener(keyboardEvent);

        verify(view).hide(false);
        verify(view).reset();
        verify(view).onClosedByKeyboard();
    }

    @Test
    public void testManagerButtonKeyDownEventWhenIsNotEscapeKey() {

        final KeyboardEvent keyboardEvent = mock(KeyboardEvent.class);
        doReturn(false).when(view).isEscapeKeyPressed(keyboardEvent);

        view.manageButtonKeyDownEventListener(keyboardEvent);

        verify(view, never()).hide(false);
        verify(view, never()).reset();
        verify(view, never()).onClosedByKeyboard();
    }

    @Test
    public void testManagerButtonKeyDownEventWhenIsNotKeyboardEvent() {

        final Event event = mock(Event.class);

        view.manageButtonKeyDownEventListener(event);

        verify(view, never()).hide(false);
        verify(view, never()).reset();
        verify(view, never()).onClosedByKeyboard();
    }

    @Test
    public void testKeyDownEventListenerEsc() {

        final KeyboardEvent keyboardEvent = mock(KeyboardEvent.class);
        doReturn(false).when(view).isEnterKeyPressed(keyboardEvent);
        doReturn(true).when(view).isEscapeKeyPressed(keyboardEvent);
        doNothing().when(view).hide(false);

        view.manageButtonKeyDownEventListener(keyboardEvent);

        verify(view).hide(false);
        verify(view).reset();
        verify(view).onClosedByKeyboard();
    }

    @Test
    public void testIsTabKeyPressed() {

        final KeyboardEvent keyboardEvent = mock(KeyboardEvent.class);
        keyboardEvent.key = TAB_KEY;

        boolean actual = view.isTabKeyPressed(keyboardEvent);
        assertTrue(actual);
        keyboardEvent.key = "A";

        actual = view.isTabKeyPressed(keyboardEvent);
        assertFalse(actual);
    }

    @Test
    public void testIsEscapeKeyPressed() {

        final KeyboardEvent keyboardEvent = mock(KeyboardEvent.class);
        keyboardEvent.key = ESC_KEY;

        boolean actual = view.isEscapeKeyPressed(keyboardEvent);
        assertTrue(actual);

        keyboardEvent.key = "A";
        actual = view.isEscapeKeyPressed(keyboardEvent);
        assertFalse(actual);

        keyboardEvent.key = ESCAPE_KEY;
        actual = view.isEscapeKeyPressed(keyboardEvent);
        assertTrue(actual);
    }

    @Test
    public void testIsEnterKeyPressed() {

        final KeyboardEvent keyboardEvent = mock(KeyboardEvent.class);
        keyboardEvent.key = ENTER_KEY;

        boolean actual = view.isEnterKeyPressed(keyboardEvent);
        assertTrue(actual);
        keyboardEvent.key = "A";

        actual = view.isEnterKeyPressed(keyboardEvent);
        assertFalse(actual);
    }

    @Test
    public void testOnNameEditorKeyDownEnter() {

        final KeyDownEvent keyDownEvent = mock(KeyDownEvent.class);
        doNothing().when(view).hide(true);

        doReturn(true).when(view).isEnter(keyDownEvent);

        view.onNameEditorKeyDown(keyDownEvent);

        verify(view).hide(true);
        verify(view).onClosedByKeyboard();
    }

    @Test
    public void testOnNameEditorKeyDownEsc() {

        final KeyDownEvent keyDownEvent = mock(KeyDownEvent.class);
        doReturn(false).when(view).isEnter(keyDownEvent);
        doReturn(true).when(view).isEsc(keyDownEvent);
        final NameAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor monitor = mock(NameAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor.class);
        doReturn(monitor).when(view).getMonitor();
        final elemental2.dom.Element menuElement = mock(elemental2.dom.Element.class);
        doReturn(menuElement).when(monitor).getMenuElement();

        view.onNameEditorKeyDown(keyDownEvent);

        verify(view).reset();
        verify(view).hide(false);
        verify(view).onClosedByKeyboard();
    }

    @Test
    public void testOnNameEditorKeyDownShiftTab() {

        final KeyDownEvent keyDownEvent = mock(KeyDownEvent.class);
        doReturn(false).when(view).isEnter(keyDownEvent);
        doReturn(false).when(view).isEsc(keyDownEvent);
        doReturn(true).when(view).isTab(keyDownEvent);

        when(keyDownEvent.isShiftKeyDown()).thenReturn(true);

        view.onNameEditorKeyDown(keyDownEvent);

        verify(typeSelectorButton).focus();
        verify(keyDownEvent).preventDefault();
        verify(view, never()).onClosedByKeyboard();
    }

    @Test
    public void testIsTab() {

        final KeyDownEvent keyDownEvent = mock(KeyDownEvent.class);
        when(keyDownEvent.getNativeKeyCode()).thenReturn(KeyCodes.KEY_TAB);

        final boolean actual = view.isTab(keyDownEvent);

        assertTrue(actual);
    }

    @Test
    public void testIsEsc() {

        final KeyDownEvent keyDownEvent = mock(KeyDownEvent.class);
        when(keyDownEvent.getNativeKeyCode()).thenReturn(KeyCodes.KEY_ESCAPE);

        final boolean actual = view.isEsc(keyDownEvent);

        assertTrue(actual);
    }

    @Test
    public void testIsEnter() {

        final KeyDownEvent keyDownEvent = mock(KeyDownEvent.class);
        when(keyDownEvent.getNativeKeyCode()).thenReturn(KeyCodes.KEY_ENTER);

        final boolean actual = view.isEnter(keyDownEvent);

        assertTrue(actual);
    }

    @Test
    public void testIsNotTab() {

        final KeyDownEvent keyDownEvent = mock(KeyDownEvent.class);
        when(keyDownEvent.getNativeKeyCode()).thenReturn(KeyCodes.KEY_A);

        final boolean actual = view.isTab(keyDownEvent);

        assertFalse(actual);
    }

    @Test
    public void testIsNotEsc() {

        final KeyDownEvent keyDownEvent = mock(KeyDownEvent.class);
        when(keyDownEvent.getNativeKeyCode()).thenReturn(KeyCodes.KEY_A);

        final boolean actual = view.isEsc(keyDownEvent);

        assertFalse(actual);
    }

    @Test
    public void testIsNotEnter() {

        final KeyDownEvent keyDownEvent = mock(KeyDownEvent.class);
        when(keyDownEvent.getNativeKeyCode()).thenReturn(KeyCodes.KEY_A);

        final boolean actual = view.isEnter(keyDownEvent);

        assertFalse(actual);
    }

    @Test
    public void testSetDMNModel() {
        view.setDMNModel(decision);

        verify(dataTypeEditor).setDMNModel(eq(decision));
    }

    @Test
    public void testInitName() {
        view.initName(NAME);

        verify(nameEditor).setValue(eq(NAME));
        assertEquals(NAME, view.getCurrentName());
    }

    @Test
    public void testInitSelectedTypeRef() {
        view.initSelectedTypeRef(typeRef);

        verify(dataTypeEditor).setValue(eq(typeRef), eq(false));
        assertEquals(typeRef, view.getCurrentTypeRef());
    }

    @Test
    public void testShow() {
        view.show(Optional.empty());

        verify(popover).show();
    }

    @Test
    public void testHideBeforeShown() {
        final NameAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor monitor = mock(NameAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor.class);
        doReturn(monitor).when(view).getMonitor();
        final elemental2.dom.Element menuElement = mock(elemental2.dom.Element.class);
        doReturn(menuElement).when(monitor).getMenuElement();

        view.hide();

        verify(popover, never()).hide();
        verify(popover, never()).destroy();
    }

    @Test
    public void testHideAfterShown() {

        final NameAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor monitor = mock(NameAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor.class);
        doReturn(monitor).when(view).getMonitor();
        final elemental2.dom.Element menuElement = mock(elemental2.dom.Element.class);
        doReturn(menuElement).when(monitor).getMenuElement();
        doReturn(true).when(view).isVisible();

        view.show(Optional.empty());
        view.hide();

        verify(nameEditor).blur();
        verify(monitor).hide();
        verify(view).isVisible();
    }

    @Test
    public void testHideWhenPopupAlreadyIsHidden() {

        final NameAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor monitor = mock(NameAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor.class);
        doReturn(monitor).when(view).getMonitor();
        final elemental2.dom.Element menuElement = mock(elemental2.dom.Element.class);
        doReturn(menuElement).when(monitor).getMenuElement();
        doReturn(false).when(view).isVisible();

        view.hide();

        verify(nameEditor, never()).blur();
        verify(monitor, never()).hide();
        verify(view).isVisible();
    }

    @Test
    public void testOnNameChange() {
        when(nameEditor.getValue()).thenReturn(NAME);

        view.onNameChange(blurEvent);

        verify(presenter, never()).setName(eq(NAME));
        assertEquals(NAME, view.getCurrentName());
    }

    @Test
    public void testOnClosedByKeyboard() {
        final Consumer consumer = mock(Consumer.class);
        final Optional opt = Optional.of(consumer);
        doReturn(opt).when(view).getClosedByKeyboardCallback();

        view.onClosedByKeyboard();

        verify(consumer).accept(view);
    }

    @Test
    public void testSetOnClosedByKeyboardCallback() {
        final Consumer consumer = mock(Consumer.class);
        view.setOnClosedByKeyboardCallback(consumer);

        final Optional<Consumer> actual = view.getClosedByKeyboardCallback();

        assertTrue(actual.isPresent());
        assertEquals(consumer, actual.get());
    }

    @Test
    public void testSetOnClosedByKeyboardCallbackNullCallback() {
        view.setOnClosedByKeyboardCallback(null);

        final Optional<Consumer> actual = view.getClosedByKeyboardCallback();

        assertFalse(actual.isPresent());
        assertEquals(Optional.empty(), actual);
    }

    @Test
    public void testKeyDownEventListenerEnterKey() {
        final KeyboardEvent event = mock(KeyboardEvent.class);

        doNothing().when(view).hide(true);
        doNothing().when(view).onClosedByKeyboard();

        doReturn(true).when(view).isEnterKeyPressed(event);

        view.keyDownEventListener(event);

        verify(view).hide(true);
        verify(event).stopPropagation();
        verify(view).onClosedByKeyboard();
        verify(view, never()).isEscapeKeyPressed(event);
    }

    @Test
    public void testKeyDownEventListenerEscKey() {
        final KeyboardEvent event = mock(KeyboardEvent.class);

        doNothing().when(view).hide(false);
        doNothing().when(view).reset();
        doNothing().when(view).onClosedByKeyboard();

        doReturn(false).when(view).isEnterKeyPressed(event);
        doReturn(true).when(view).isEscapeKeyPressed(event);

        view.keyDownEventListener(event);

        verify(view).hide(false);
        verify(view).reset();
        verify(view).onClosedByKeyboard();
    }

    @Test
    public void testKeyDownEventListenerWhenIsNotAHandledKey() {

        final KeyboardEvent event = mock(KeyboardEvent.class);

        doReturn(false).when(view).isEnterKeyPressed(event);
        doReturn(false).when(view).isEscapeKeyPressed(event);

        view.keyDownEventListener(event);

        verify(view, never()).hide(anyBoolean());
        verify(event, never()).stopPropagation();
        verify(view, never()).onClosedByKeyboard();
        verify(view, never()).reset();
    }

    @Test
    public void testKeyDownEventListenerWhenIsNotKeyboardEvent() {

        final Event event = mock(Event.class);
        view.keyDownEventListener(event);

        verify(view, never()).hide(anyBoolean());
        verify(event, never()).stopPropagation();
        verify(view, never()).onClosedByKeyboard();
        verify(view, never()).isEscapeKeyPressed(any());
        verify(view, never()).reset();
    }

    @Test
    public void testResetName() {
        view.initName(NAME);

        final String newName = "new_name";
        when(nameEditor.getValue()).thenReturn(newName);
        view.onNameChange(blurEvent);
        assertEquals(newName, view.getCurrentName());

        view.reset();

        assertEquals(NAME, view.getCurrentName());
    }

    @Test
    public void testResetTypeRef() {
        view.initSelectedTypeRef(typeRef);

        final QName newTypeRef = mock(QName.class);
        verify(dataTypeEditor).addValueChangeHandler(valueChangeHandlerCaptor.capture());
        when(valueChangeEvent.getValue()).thenReturn(newTypeRef);
        valueChangeHandlerCaptor.getValue().onValueChange(valueChangeEvent);

        assertEquals(newTypeRef, view.getCurrentTypeRef());

        view.reset();

        assertEquals(typeRef, view.getCurrentTypeRef());
    }
}
