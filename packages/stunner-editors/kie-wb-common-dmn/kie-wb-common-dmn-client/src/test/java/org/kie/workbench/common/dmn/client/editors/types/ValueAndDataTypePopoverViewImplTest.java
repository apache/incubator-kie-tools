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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.Optional;

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
import org.mockito.Mockito;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl.ENTER_KEY;
import static org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl.ESCAPE_KEY;
import static org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl.ESC_KEY;
import static org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl.MANAGE_BUTTON_SELECTOR;
import static org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl.TAB_KEY;
import static org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl.TYPE_SELECTOR_BUTTON_SELECTOR;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ValueAndDataTypePopoverViewImplTest {

    private static final String VALUE = "value";

    private static final String VALUE_LABEL = "label";

    @Mock
    private Input valueEditor;

    @Mock
    private DataTypePickerWidget dataTypeEditor;

    @Mock
    private Div popoverElement;

    @Mock
    private Div popoverContentElement;

    @Mock
    private Span valueLabel;

    @Mock
    private Span dataTypeLabel;

    @Mock
    private JQueryProducer.JQuery<Popover> jQueryProducer;

    @Mock
    private ValueAndDataTypePopoverView.Presenter presenter;

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

    @Mock
    private EventListener keyDownCallback;

    @Mock
    private EventListener managerCallback;

    @Mock
    private EventListener eventListenerCallback;

    @Captor
    private ArgumentCaptor<ValueChangeHandler<QName>> valueChangeHandlerCaptor;

    private ValueAndDataTypePopoverViewImpl view;

    @Before
    public void setUp() {
        view = spy(new ValueAndDataTypePopoverViewImpl(valueEditor,
                                                       dataTypeEditor,
                                                       popoverElement,
                                                       popoverContentElement,
                                                       valueLabel,
                                                       dataTypeLabel,
                                                       jQueryProducer,
                                                       translationService) {
            @Override
            public HTMLElement getElement() {
                return element;
            }

            @Override
            protected EventListener getKeyDownEventListener() {
                return keyDownCallback;
            }

            @Override
            EventListener getManageButtonKeyDownEventListener() {
                return managerCallback;
            }

            @Override
            EventListener getTypeSelectorKeyDownEventListener() {
                return eventListenerCallback;
            }
        });

        when(element.querySelector(MANAGE_BUTTON_SELECTOR)).thenReturn(manageButton);
        when(element.querySelector(TYPE_SELECTOR_BUTTON_SELECTOR)).thenReturn(typeSelectorButton);
        when(presenter.getValueLabel()).thenReturn(VALUE_LABEL);

        view.init(presenter);

        when(valueChangeEvent.getValue()).thenReturn(typeRef);
        when(jQueryProducer.wrap(element)).thenReturn(popover);

        doAnswer(i -> i.getArguments()[0]).when(translationService).getTranslation(Mockito.<String>any());
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
        reset(manageButton, typeSelectorButton);

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
        final ValueAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor monitor = mock(ValueAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor.class);
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
        verify(valueEditor, never()).focus();
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
        verify(valueEditor, never()).focus();
    }

    @Test
    public void testTypeSelectorKeyDownEventListenerTabKey() {

        final KeyboardEvent keyboardEvent = mock(KeyboardEvent.class);
        doReturn(false).when(view).isEnterKeyPressed(keyboardEvent);
        doReturn(false).when(view).isEscapeKeyPressed(keyboardEvent);
        doReturn(true).when(view).isTabKeyPressed(keyboardEvent);

        view.typeSelectorKeyDownEventListener(keyboardEvent);

        verify(valueEditor).focus();
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
        final ValueAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor monitor = mock(ValueAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor.class);
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
    public void testOnValueEditorKeyDownEnter() {

        final KeyDownEvent keyDownEvent = mock(KeyDownEvent.class);
        doNothing().when(view).hide(true);

        doReturn(true).when(view).isEnter(keyDownEvent);

        view.onValueEditorKeyDown(keyDownEvent);

        verify(view).hide(true);
        verify(view).onClosedByKeyboard();
    }

    @Test
    public void testOnValueEditorKeyDownEsc() {

        final KeyDownEvent keyDownEvent = mock(KeyDownEvent.class);
        doReturn(false).when(view).isEnter(keyDownEvent);
        doReturn(true).when(view).isEsc(keyDownEvent);
        final ValueAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor monitor = mock(ValueAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor.class);
        doReturn(monitor).when(view).getMonitor();
        final elemental2.dom.Element menuElement = mock(elemental2.dom.Element.class);
        doReturn(menuElement).when(monitor).getMenuElement();

        view.onValueEditorKeyDown(keyDownEvent);

        verify(view).reset();
        verify(view).hide(false);
        verify(view).onClosedByKeyboard();
    }

    @Test
    public void testOnValueEditorKeyDownShiftTab() {

        final KeyDownEvent keyDownEvent = mock(KeyDownEvent.class);
        doReturn(false).when(view).isEnter(keyDownEvent);
        doReturn(false).when(view).isEsc(keyDownEvent);
        doReturn(true).when(view).isTab(keyDownEvent);

        when(keyDownEvent.isShiftKeyDown()).thenReturn(true);

        view.onValueEditorKeyDown(keyDownEvent);

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
    public void testInitValue() {
        view.initValue(VALUE);

        verify(valueEditor).setValue(eq(VALUE));
        assertEquals(VALUE, view.getCurrentValue());
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

        verify(valueLabel).setTextContent(VALUE_LABEL);
        verify(popover).show();
    }

    @Test
    public void testHideBeforeShown() {
        final ValueAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor monitor = mock(ValueAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor.class);
        doReturn(monitor).when(view).getMonitor();
        final elemental2.dom.Element menuElement = mock(elemental2.dom.Element.class);
        doReturn(menuElement).when(monitor).getMenuElement();

        view.hide();

        verify(popover, never()).hide();
        verify(popover, never()).destroy();
    }

    @Test
    public void testHideAfterShown() {
        final ValueAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor monitor = mock(ValueAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor.class);
        doReturn(monitor).when(view).getMonitor();
        final elemental2.dom.Element menuElement = mock(elemental2.dom.Element.class);
        doReturn(menuElement).when(monitor).getMenuElement();
        doReturn(true).when(view).isVisible();

        view.show(Optional.empty());
        view.hide();

        verify(valueEditor).blur();
        verify(monitor).hide();
        verify(view).isVisible();
    }

    @Test
    public void testHideWhenPopupAlreadyIsHidden() {

        final ValueAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor monitor = mock(ValueAndDataTypePopoverViewImpl.BootstrapSelectDropDownMonitor.class);
        doReturn(monitor).when(view).getMonitor();
        final elemental2.dom.Element menuElement = mock(elemental2.dom.Element.class);
        doReturn(menuElement).when(monitor).getMenuElement();
        doReturn(false).when(view).isVisible();

        view.hide();

        verify(valueEditor, never()).blur();
        verify(monitor, never()).hide();
        verify(view).isVisible();
    }

    @Test
    public void testOnValueChange() {
        when(presenter.normaliseValue(Mockito.<String>any())).thenAnswer(i -> i.getArguments()[0]);
        when(valueEditor.getValue()).thenReturn(VALUE);

        view.onValueChange(blurEvent);

        verify(presenter, never()).setValue(eq(VALUE));
        assertEquals(VALUE, view.getCurrentValue());
    }

    @Test
    public void testOnValueChangeWithNormalisedValue() {
        final String normalisedValue = "normalised";
        when(presenter.normaliseValue(Mockito.<String>any())).thenAnswer(i -> normalisedValue);
        when(valueEditor.getValue()).thenReturn(VALUE);

        view.onValueChange(blurEvent);

        verify(presenter, never()).setValue(eq(VALUE));
        verify(valueEditor).setValue(normalisedValue);
        assertEquals(normalisedValue, view.getCurrentValue());
    }

    @Test
    public void testResetValue() {
        view.initValue(VALUE);

        final String newValue = "new_value";
        when(presenter.normaliseValue(Mockito.<String>any())).thenAnswer(i -> i.getArguments()[0]);
        when(valueEditor.getValue()).thenReturn(newValue);

        view.onValueChange(blurEvent);
        assertEquals(newValue, view.getCurrentValue());

        view.reset();

        assertEquals(VALUE, view.getCurrentValue());
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
