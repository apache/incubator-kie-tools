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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item;

import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.TypedValueComponentSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.TypedValueSelector;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.HIDDEN_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItem.NULL;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView.DATA_POSITION;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView.HIGHLIGHTED_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView.NONE_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeConstraintEnumerationItemView_None;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeConstraintEnumerationItemViewTest {

    @Mock
    private HTMLElement valueText;

    @Mock
    private HTMLDivElement valueInput;

    @Mock
    private HTMLAnchorElement saveAnchor;

    @Mock
    private HTMLAnchorElement clearFieldAnchor;

    @Mock
    private HTMLAnchorElement removeAnchor;

    @Mock
    private TranslationService translationService;

    @Mock
    private DataTypeConstraintEnumerationItem presenter;

    @Mock
    private TypedValueComponentSelector componentSelector;

    @Mock
    private TypedValueSelector typedValueSelector;

    private DataTypeConstraintEnumerationItemView view;

    @Before
    public void setup() {
        view = spy(new DataTypeConstraintEnumerationItemView(valueText, valueInput, saveAnchor, removeAnchor, clearFieldAnchor, translationService, componentSelector));
        view.init(presenter);

        clearFieldAnchor.classList = mock(DOMTokenList.class);
        removeAnchor.classList = mock(DOMTokenList.class);

        when(componentSelector.makeSelectorForType(any())).thenReturn(typedValueSelector);
        view.setComponentSelector("someType");
    }

    @Test
    public void testShowValueText() {

        valueText.classList = mock(DOMTokenList.class);
        valueInput.classList = mock(DOMTokenList.class);

        view.showValueText();

        verify(valueText.classList).remove(HIDDEN_CSS_CLASS);
        verify(valueInput.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testShowValueInput() {

        valueText.classList = mock(DOMTokenList.class);
        valueInput.classList = mock(DOMTokenList.class);

        view.showValueInput();

        verify(valueInput.classList).remove(HIDDEN_CSS_CLASS);
        verify(valueText.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testFocusValueInput() {
        view.focusValueInput();

        verify(typedValueSelector).select();
    }

    @Test
    public void testShowSaveButton() {

        saveAnchor.classList = mock(DOMTokenList.class);

        view.showSaveButton();

        verify(saveAnchor.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testHideSaveButton() {

        saveAnchor.classList = mock(DOMTokenList.class);

        view.hideSaveButton();

        verify(saveAnchor.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testEnableHighlight() {

        final HTMLElement element = mock(HTMLElement.class);

        element.classList = mock(DOMTokenList.class);
        doReturn(element).when(view).getElement();

        view.enableHighlight();

        verify(element.classList).add(HIGHLIGHTED_CSS_CLASS);
    }

    @Test
    public void testDisableHighlight() {

        final HTMLElement element = mock(HTMLElement.class);

        element.classList = mock(DOMTokenList.class);
        doReturn(element).when(view).getElement();

        view.disableHighlight();

        verify(element.classList).remove(HIGHLIGHTED_CSS_CLASS);
    }

    @Test
    public void testOnValueTextClick() {
        view.onValueTextClick(mock(ClickEvent.class));
        verify(presenter).enableEditMode();
    }

    @Test
    public void testOnSaveAnchorClick() {

        final String value = "value";

        typedValueSelector.setValue(value);

        when(typedValueSelector.getValue()).thenReturn(value);

        view.onSaveAnchorClick(mock(ClickEvent.class));

        verify(presenter).save(value);
    }

    @Test
    public void testOnRemoveAnchorClick() {
        view.onRemoveAnchorClick(mock(ClickEvent.class));
        verify(presenter).remove();
    }

    @Test
    public void testOnValueInputBlurWhenRelatedTargetIsNotSaveButton() {

        final BlurEvent blurEvent = mock(BlurEvent.class);
        final EventTarget mock = mock(EventTarget.class);

        doReturn(mock).when(view).getEventTarget(blurEvent);
        doReturn(saveAnchor).when(view).getSaveAnchorTarget();

        view.onValueInputBlur(blurEvent);

        verify(presenter).discardEditMode();
    }

    @Test
    public void testOnValueInputBlurWhenRelatedTargetNotSaveButton() {

        final BlurEvent blurEvent = mock(BlurEvent.class);

        doReturn(saveAnchor).when(view).getEventTarget(blurEvent);
        doReturn(saveAnchor).when(view).getSaveAnchorTarget();

        view.onValueInputBlur(blurEvent);

        verify(presenter, never()).discardEditMode();
    }

    @Test
    public void testOnValueInputBlurWhenRelatedTargetIsNotClearButton() {

        final BlurEvent blurEvent = mock(BlurEvent.class);
        final EventTarget mock = mock(EventTarget.class);

        doReturn(mock).when(view).getEventTarget(blurEvent);
        doReturn(clearFieldAnchor).when(view).getClearAnchorTarget();

        view.onValueInputBlur(blurEvent);

        verify(presenter).discardEditMode();
    }

    @Test
    public void testOnValueInputBlurWhenRelatedTargetIsClearButton() {

        final BlurEvent blurEvent = mock(BlurEvent.class);

        doReturn(clearFieldAnchor).when(view).getEventTarget(blurEvent);
        doReturn(clearFieldAnchor).when(view).getClearAnchorTarget();

        view.onValueInputBlur(blurEvent);

        verify(presenter, never()).discardEditMode();
    }

    @Test
    public void testGetEventTarget() {

        final BlurEvent blurEvent = mock(BlurEvent.class);
        final NativeEvent nativeEvent = mock(NativeEvent.class);
        final EventTarget expectedTarget = mock(EventTarget.class);

        when(blurEvent.getNativeEvent()).thenReturn(nativeEvent);
        when(nativeEvent.getRelatedEventTarget()).thenReturn(expectedTarget);

        final Object actualTarget = view.getEventTarget(blurEvent);

        assertEquals(expectedTarget, actualTarget);
    }

    @Test
    public void testSetValue() {

        final String inputValue = "123";
        final String expected = "display:" + inputValue;

        when(typedValueSelector.toDisplay(inputValue)).thenReturn(expected);
        valueText.classList = mock(DOMTokenList.class);

        view.setValue(inputValue);

        final String actualContent = valueText.textContent;

        verify(valueText.classList).remove(NONE_CSS_CLASS);
        verify(typedValueSelector).toDisplay(inputValue);

        assertEquals(expected, actualContent);
        verify(typedValueSelector).setValue(inputValue);
    }

    @Test
    public void testSetValueWhenValueIsNULL() {

        final String expectedValue = "- None -";

        when(translationService.format(DataTypeConstraintEnumerationItemView_None)).thenReturn(expectedValue);
        valueText.classList = mock(DOMTokenList.class);

        view.setValue(NULL);

        final String actualContent = valueText.textContent;

        verify(valueText.classList).add(NONE_CSS_CLASS);
        assertEquals(expectedValue, actualContent);
        verify(typedValueSelector).setValue("");
    }

    @Test
    public void testSetPlaceholder() {

        final String value = "value";

        view.setPlaceholder(value);

        verify(typedValueSelector).setPlaceholder(value);
    }

    @Test
    public void testShowClearButton() {

        view.showClearButton();

        verify(clearFieldAnchor.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void hideClearButton() {

        view.hideClearButton();

        verify(clearFieldAnchor.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void hideDeleteButton() {

        view.hideDeleteButton();

        verify(removeAnchor.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void showDeleteButton() {

        view.showDeleteButton();

        verify(removeAnchor.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testOnClearFieldAnchorClick() {

        view.onClearFieldAnchorClick(mock(ClickEvent.class));

        verify(presenter).setValue("");
        verify(typedValueSelector).select();
    }

    @Test
    public void testSetComponentSelector() {

        final HTMLElement element = mock(HTMLElement.class);
        final String type = "type";

        doReturn(element).when(typedValueSelector).getElement();

        view.setComponentSelector(type);

        verify(componentSelector).makeSelectorForType(type);
        verify(valueInput).appendChild(element);
    }

    @Test
    public void testGetOrder() {

        final HTMLElement element = mock(HTMLElement.class);
        final int expected = 1;

        when(element.getAttribute(DATA_POSITION)).thenReturn(String.valueOf(expected));
        doReturn(element).when(view).getElement();

        final int actual = view.getOrder();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetOrderEmptyString() {

        final HTMLElement element = mock(HTMLElement.class);
        final int expected = 0;

        when(element.getAttribute(DATA_POSITION)).thenReturn("");
        doReturn(element).when(view).getElement();

        final int actual = view.getOrder();

        assertEquals(expected, actual);
    }

    @Test
    public void testSetOrder() {

        final HTMLElement element = mock(HTMLElement.class);

        doReturn(element).when(view).getElement();

        view.setOrder(1);

        verify(element).setAttribute(DATA_POSITION, 1);
    }
}
