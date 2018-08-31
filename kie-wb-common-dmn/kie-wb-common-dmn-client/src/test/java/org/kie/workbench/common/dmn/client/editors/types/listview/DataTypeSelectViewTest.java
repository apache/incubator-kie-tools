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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLOptGroupElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.HiddenHelper.HIDDEN_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeSelectView_CustomTitle;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeSelectView_DefaultTitle;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeSelectViewTest {

    @Mock
    private HTMLDivElement typeText;

    @Mock
    private HTMLSelectElement typeSelect;

    @Mock
    private HTMLOptGroupElement typeSelectOptGroup;

    @Mock
    private HTMLOptionElement typeSelectOption;

    @Mock
    private DataTypeSelect presenter;

    @Mock
    private TranslationService translationService;

    private DataTypeSelectView view;

    @Before
    public void setup() {
        view = spy(new DataTypeSelectView(typeText, typeSelect, typeSelectOptGroup, typeSelectOption, null, translationService));

        doNothing().when(view).setupDropdown();

        view.init(presenter);
    }

    @Test
    public void testSetupDropdown() {

        doCallRealMethod().when(view).setupDropdown();
        doNothing().when(view).setupDropdownItems();
        doNothing().when(view).setupSelectPicker();
        doNothing().when(view).hideSelectPicker();
        doNothing().when(view).setupSelectPickerOnChangeHandler();

        view.setupDropdown();

        verify(view).setupDropdownItems();
        verify(view).setupSelectPicker();
        verify(view).hideSelectPicker();
        verify(view).setupSelectPickerOnChangeHandler();
    }

    @Test
    public void testSetupDropdownItems() {

        final HTMLOptGroupElement groupElementCustom = mock(HTMLOptGroupElement.class);
        final HTMLOptGroupElement groupElementDefault = mock(HTMLOptGroupElement.class);
        final List<DataType> defaultDataTypes = new ArrayList<>();
        final List<DataType> customDataTypes = new ArrayList<>();

        when(translationService.format(DataTypeSelectView_DefaultTitle)).thenReturn("Default");
        when(translationService.format(DataTypeSelectView_CustomTitle)).thenReturn("Custom");
        when(presenter.getDefaultDataTypes()).thenReturn(defaultDataTypes);
        when(presenter.getCustomDataTypes()).thenReturn(customDataTypes);
        doReturn(groupElementCustom).when(view).makeOptionGroup(eq("Default"), eq(defaultDataTypes), any());
        doReturn(groupElementDefault).when(view).makeOptionGroup(eq("Custom"), eq(customDataTypes), any());

        typeSelect.innerHTML = "previousContent";

        view.setupDropdownItems();

        assertFalse(typeSelect.innerHTML.contains("previousContent"));
        verify(typeSelect).appendChild(groupElementCustom);
        verify(typeSelect).appendChild(groupElementDefault);
    }

    @Test
    public void testMakeOptionGroup() {

        final String dataTypeName = "name";
        final String groupTitle = "Title";
        final DataType dataType = makeDataType(dataTypeName);
        final List<DataType> dataTypes = Collections.singletonList(dataType);
        final HTMLOptGroupElement expectedGroupElement = mock(HTMLOptGroupElement.class);
        final HTMLOptionElement optionElement = mock(HTMLOptionElement.class);

        doReturn(expectedGroupElement).when(view).makeHTMLOptGroupElement();
        doReturn(optionElement).when(view).makeOption(dataTypeName);

        final HTMLOptGroupElement actualGroupElement = view.makeOptionGroup(groupTitle, dataTypes, DataType::getName);

        verify(expectedGroupElement).appendChild(optionElement);
        assertEquals(expectedGroupElement, actualGroupElement);
        assertEquals(groupTitle, actualGroupElement.label);
    }

    @Test
    public void testMakeOption() {

        final String value = "value";
        final HTMLOptionElement htmlOptionElement = mock(HTMLOptionElement.class);

        doReturn(htmlOptionElement).when(view).makeHTMLOptionElement();

        final HTMLOptionElement option = view.makeOption(value);

        assertEquals(value, option.text);
        assertEquals(value, option.value);
    }

    @Test
    public void testSetDataType() {

        final String type = "type";
        final DataType dataType = makeDataType(type);
        final String expectedTypeText = "(type)";

        view.setDataType(dataType);

        final String actualTypeText = typeText.textContent;

        assertEquals(expectedTypeText, actualTypeText);
    }

    @Test
    public void testOnSelectChange() {
        view.onSelectChange();

        verify(presenter).refreshView(typeSelect.value);
    }

    @Test
    public void testSetPickerValue() {

        final Element element = mock(Element.class);
        final String value = "value";

        doReturn(element).when(view).getSelectPicker();

        view.setPickerValue(value);

        verify(view).setPickerValue(element, value);
    }

    @Test
    public void testSetupSelectPickerOnChangeHandler() {

        final Element element = mock(Element.class);
        doReturn(element).when(view).getSelectPicker();

        view.setupSelectPickerOnChangeHandler();

        verify(view).setupOnChangeHandler(element);
    }

    @Test
    public void testHideSelectPicker() {

        final Element element = mock(Element.class);
        doReturn(element).when(view).getSelectPicker();

        view.hideSelectPicker();

        verify(view).triggerPickerAction(element, "hide");
    }

    @Test
    public void testShowSelectPicker() {

        final Element element = mock(Element.class);
        doReturn(element).when(view).getSelectPicker();

        view.showSelectPicker();

        verify(view).triggerPickerAction(element, "show");
    }

    @Test
    public void testSetupSelectPicker() {

        final Element element = mock(Element.class);
        doReturn(element).when(view).getSelectPicker();

        view.setupSelectPicker();

        verify(view).triggerPickerAction(element, "refresh");
    }

    @Test
    public void testOpenSelectPicker() {

        final Element element = mock(Element.class);
        doReturn(element).when(view).getSelectPicker();

        view.openSelectPicker();

        verify(view).triggerPickerAction(element, "toggle");
    }

    @Test
    public void testGetSelectPicker() {

        final HTMLElement element = mock(HTMLElement.class);
        final HTMLElement expectedSelect = mock(HTMLElement.class);
        doReturn(element).when(view).getElement();

        when(element.querySelector("[data-field='type-select']")).thenReturn(expectedSelect);

        final Element actualSelect = view.getSelectPicker();

        assertEquals(expectedSelect, actualSelect);
    }

    @Test
    public void testEnableEditMode() {

        final String type = "type";
        final DataType dataType = makeDataType(type);

        doNothing().when(view).showSelectPicker();
        doNothing().when(view).setPickerValue(anyString());
        when(presenter.getDataType()).thenReturn(dataType);
        typeText.classList = mock(DOMTokenList.class);

        view.enableEditMode();

        verify(typeText.classList).add(HIDDEN_CSS_CLASS);
        verify(view).showSelectPicker();
        verify(view).setPickerValue(type);
    }

    @Test
    public void testDisableEditMode() {

        final String type = "type";
        final String expectedTypeText = "(type)";
        final DataType dataType = makeDataType(type);

        doNothing().when(view).hideSelectPicker();
        when(presenter.getDataType()).thenReturn(dataType);
        typeText.classList = mock(DOMTokenList.class);
        typeSelect.value = type;

        view.disableEditMode();

        assertEquals(expectedTypeText, typeText.textContent);
        verify(typeText.classList).remove(HIDDEN_CSS_CLASS);
        verify(view).hideSelectPicker();
    }

    @Test
    public void testGetValue() {

        final String expectedValue = "type";

        typeSelect.value = expectedValue;

        final String actualValue = view.getValue();

        assertEquals(expectedValue, actualValue);
    }

    private DataType makeDataType(final String name) {
        return new DataType("uuid", "parentUUID", name, name, new ArrayList<>(), false, false, false, null);
    }
}
