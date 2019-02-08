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
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.MenuInitializer;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.HIDDEN_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItem.NULL;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView.HIGHLIGHTED_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView.NONE_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeConstraintEnumerationItemView_None;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
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
    private HTMLInputElement valueInput;

    @Mock
    private HTMLAnchorElement saveAnchor;

    @Mock
    private HTMLAnchorElement editAnchor;

    @Mock
    private HTMLAnchorElement removeAnchor;

    @Mock
    private HTMLAnchorElement moveUpAnchor;

    @Mock
    private HTMLAnchorElement moveDownAnchor;

    @Mock
    private HTMLDivElement kebabMenu;

    @Mock
    private TranslationService translationService;

    @Mock
    private DataTypeConstraintEnumerationItem presenter;

    private DataTypeConstraintEnumerationItemView view;

    @Before
    public void setup() {
        view = spy(new DataTypeConstraintEnumerationItemView(valueText, valueInput, saveAnchor, editAnchor, removeAnchor, moveUpAnchor, moveDownAnchor, kebabMenu, translationService));
        view.init(presenter);
    }

    @Test
    public void testSetupKebabElement() {

        final MenuInitializer menuInitializer = mock(MenuInitializer.class);

        doReturn(menuInitializer).when(view).makeMenuInitializer(any(), anyString());

        view.setupKebabElement();

        verify(view).makeMenuInitializer(kebabMenu, ".dropdown");
        verify(menuInitializer).init();
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

        verify(valueInput).select();
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
        valueInput.value = value;

        view.onSaveAnchorClick(mock(ClickEvent.class));

        verify(presenter).save(value);
    }

    @Test
    public void testOnEditAnchorClick() {
        view.onEditAnchorClick(mock(ClickEvent.class));
        verify(presenter).enableEditMode();
    }

    @Test
    public void testOnRemoveAnchorClick() {
        view.onRemoveAnchorClick(mock(ClickEvent.class));
        verify(presenter).remove();
    }

    @Test
    public void testOnMoveUpAnchorClick() {
        view.onMoveUpAnchorClick(mock(ClickEvent.class));
        verify(presenter).moveUp();
    }

    @Test
    public void testOnMoveDownAnchorClick() {
        view.onMoveDownAnchorClick(mock(ClickEvent.class));
        verify(presenter).moveDown();
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

        final String expectedValue = "123";

        valueText.classList = mock(DOMTokenList.class);
        valueInput.value = "something";

        view.setValue(expectedValue);

        final String actualContent = valueText.textContent;

        verify(valueText.classList).remove(NONE_CSS_CLASS);
        assertEquals(expectedValue, actualContent);
        assertEquals(expectedValue, valueInput.value);
    }

    @Test
    public void testSetValueWhenValueIsNULL() {

        final String expectedValue = "- None -";

        when(translationService.format(DataTypeConstraintEnumerationItemView_None)).thenReturn(expectedValue);
        valueText.classList = mock(DOMTokenList.class);
        valueInput.value = "something";

        view.setValue(NULL);

        final String actualContent = valueText.textContent;

        verify(valueText.classList).add(NONE_CSS_CLASS);
        assertEquals(expectedValue, actualContent);
        assertEquals("", valueInput.value);
    }
}
