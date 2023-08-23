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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwt.event.dom.client.ClickEvent;
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
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeUtils;
import org.kie.workbench.common.dmn.client.editors.types.listview.tooltip.StructureTypesTooltip;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPickerEvent;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPickerTarget;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.HIDDEN_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelectView.IS_BUILT_IN_TYPE_ATTR;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeSelectView_CustomTitle;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeSelectView_DefaultTitle;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
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

    @Mock
    private DataTypeStore dataTypeStore;

    @Mock
    private DataTypeManager dataTypeManager;

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @Mock
    private StructureTypesTooltip structureTypesTooltip;

    @Captor
    private ArgumentCaptor<DataType> dataTypeCaptor;

    private DataTypeUtils dataTypeUtils;

    private DataTypeSelectView view;

    @Before
    public void setup() {
        view = spy(new DataTypeSelectView(typeText, typeSelect, typeSelectOptGroup, typeSelectOption, null, translationService, structureTypesTooltip));
        dataTypeUtils = new DataTypeUtils(dataTypeStore, dataTypeManager, dmnGraphUtils);

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

        final HTMLOptGroupElement groupElementStructure = mock(HTMLOptGroupElement.class);
        final HTMLOptGroupElement groupElementCustom = mock(HTMLOptGroupElement.class);
        final HTMLOptGroupElement groupElementDefault = mock(HTMLOptGroupElement.class);
        final List<DataType> defaultDataTypes = new ArrayList<>();
        final List<DataType> customDataTypes = new ArrayList<>();
        final Element element = mock(Element.class);

        when(translationService.format(DataTypeSelectView_DefaultTitle)).thenReturn("Default");
        when(translationService.format(DataTypeSelectView_CustomTitle)).thenReturn("Custom");
        when(presenter.getDefaultDataTypes()).thenReturn(defaultDataTypes);
        when(presenter.getCustomDataTypes()).thenReturn(customDataTypes);
        doReturn(groupElementStructure).when(view).makeOptionStructureGroup();
        doReturn(groupElementDefault).when(view).makeOptionGroup(eq("Default"), eq(defaultDataTypes), any());
        doReturn(groupElementCustom).when(view).makeOptionGroup(eq("Custom"), eq(customDataTypes), any());
        typeSelect.firstChild = element;

        when(typeSelect.removeChild(element)).then(a -> {
            typeSelect.firstChild = null;
            return element;
        });

        view.setupDropdownItems();

        final InOrder inOrder = inOrder(typeSelect, element, groupElementStructure, groupElementDefault, groupElementCustom);

        inOrder.verify(typeSelect).removeChild(element);
        inOrder.verify(typeSelect).appendChild(groupElementStructure);
        inOrder.verify(typeSelect).appendChild(groupElementDefault);
        inOrder.verify(typeSelect).appendChild(groupElementCustom);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMakeOptionGroup() {
        final String dataTypeName = "name";
        final String groupTitle = "Title";
        final DataType dataType = makeDataType(dataTypeName);
        final List<DataType> dataTypes = Collections.singletonList(dataType);
        final HTMLOptGroupElement expectedGroupElement = mock(HTMLOptGroupElement.class);
        final HTMLOptionElement optionElement = mock(HTMLOptionElement.class);

        doReturn(expectedGroupElement).when(view).makeHTMLOptGroupElement();
        doReturn(optionElement).when(view).makeOption(eq(dataType), any(Function.class));

        final HTMLOptGroupElement actualGroupElement = view.makeOptionGroup(groupTitle, dataTypes, DataType::getName);

        verify(expectedGroupElement).appendChild(optionElement);
        assertEquals(expectedGroupElement, actualGroupElement);
        assertEquals(groupTitle, actualGroupElement.label);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOptionGroupSorting() {
        final HTMLOptGroupElement groupElement = mock(HTMLOptGroupElement.class);
        final HTMLOptionElement optionElement = mock(HTMLOptionElement.class);
        final DataType customDataType1 = makeDataType("b");
        final DataType customDataType2 = makeDataType("a");
        final List<DataType> customDataTypes = Stream.of(customDataType1, customDataType2).collect(Collectors.toList());

        doReturn(groupElement).when(view).makeHTMLOptGroupElement();
        doReturn(optionElement).when(view).makeHTMLOptionElement();

        when(translationService.format(DataTypeSelectView_DefaultTitle)).thenReturn("Default");
        when(translationService.format(DataTypeSelectView_CustomTitle)).thenReturn("Custom");
        when(dataTypeStore.getTopLevelDataTypes()).thenReturn(customDataTypes);

        doAnswer(i -> {
            final BuiltInType bit = (BuiltInType) i.getArguments()[0];
            final DataTypeManager dtm = mock(DataTypeManager.class);
            final DataType dt = makeDataType(bit.getName());
            when(dtm.get()).thenReturn(dt);
            return dtm;
        }).when(dataTypeManager).from(any(BuiltInType.class));

        doAnswer(i -> dataTypeUtils.defaultDataTypes()).when(presenter).getDefaultDataTypes();
        doAnswer(i -> dataTypeUtils.customDataTypes()).when(presenter).getCustomDataTypes();

        view.setupDropdownItems();

        final int visibleItems = BuiltInType.values().length - 1;

        //Check all items were added to the group minus the UNDEFINED item
        verify(view, times(visibleItems + customDataTypes.size())).makeOption(dataTypeCaptor.capture(), any(Function.class));
        final List<DataType> dataTypes = dataTypeCaptor.getAllValues();

        //Check the items were sorted correctly
        assertEquals("Any", dataTypes.get(0).getType());
        assertEquals("boolean", dataTypes.get(1).getType());
        assertEquals("context", dataTypes.get(2).getType());
        assertEquals("date", dataTypes.get(3).getType());
        assertEquals("date and time", dataTypes.get(4).getType());
        assertEquals("days and time duration", dataTypes.get(5).getType());
        assertEquals("number", dataTypes.get(6).getType());
        assertEquals("string", dataTypes.get(7).getType());
        assertEquals("time", dataTypes.get(8).getType());
        assertEquals("years and months duration", dataTypes.get(9).getType());

        final int customDataTypesOffset = visibleItems;
        assertEquals("a", dataTypes.get(customDataTypesOffset).getType());
        assertEquals("b", dataTypes.get(customDataTypesOffset + 1).getType());
    }

    @Test
    public void testMakeOption() {
        final String value = "value";
        final DataType dataType = makeDataType(value);
        final HTMLOptionElement htmlOptionElement = mock(HTMLOptionElement.class);

        doReturn(htmlOptionElement).when(view).makeHTMLOptionElement();

        final HTMLOptionElement option = view.makeOption(dataType, DataType::getName);

        assertEquals(value, option.text);
        assertEquals(value, option.value);
    }

    @Test
    public void testSetDataTypeWhenItsNotBuiltInType() {

        final String expectedType = "type";
        final DataType dataType = makeDataType(expectedType);

        view.setDataType(dataType);

        final String actualType = view.getValue();
        final String actualTypeText = typeText.textContent;

        assertEquals(expectedType, actualType);
        assertEquals(expectedType, actualTypeText);
        verify(typeText).setAttribute(IS_BUILT_IN_TYPE_ATTR, false);
    }

    @Test
    public void testSetDataTypeWhenItsBuiltInType() {

        final String expectedType = "string";
        final DataType dataType = makeDataType(expectedType);

        view.setDataType(dataType);

        final String actualType = view.getValue();
        final String actualTypeText = typeText.textContent;

        assertEquals(expectedType, actualType);
        assertEquals(expectedType, actualTypeText);
        verify(typeText).setAttribute(IS_BUILT_IN_TYPE_ATTR, true);
    }

    @Test
    public void testOnTypeTextClickWhenItsNotBuiltInType() {

        final ClickEvent event = mock(ClickEvent.class);
        final String type = "tPerson";
        final DataType dataType = makeDataType(type);
        final HTMLElement element = mock(HTMLElement.class);

        when(presenter.getDataType()).thenReturn(dataType);
        doReturn(element).when(view).getElement();

        view.onTypeTextClick(event);

        verify(structureTypesTooltip).show(element, type);
        verify(event).preventDefault();
        verify(event).stopPropagation();
    }

    @Test
    public void testOnTypeTextClickWhenItsBuiltInType() {

        final ClickEvent event = mock(ClickEvent.class);
        final String type = "string";
        final DataType dataType = makeDataType(type);
        final HTMLElement element = mock(HTMLElement.class);

        when(presenter.getDataType()).thenReturn(dataType);
        doReturn(element).when(view).getElement();

        view.onTypeTextClick(event);

        verify(structureTypesTooltip, never()).show(any(), anyString());
        verify(event).preventDefault();
        verify(event).stopPropagation();
    }

    @Test
    public void testOnSelectChangeWhenOtherValueIsSet() {

        final JQuerySelectPickerEvent event = mock(JQuerySelectPickerEvent.class);
        final JQuerySelectPickerTarget target = mock(JQuerySelectPickerTarget.class);
        final String newValue = "newValue";
        final String oldValue = "oldValue";

        doNothing().when(view).setPickerValue(newValue);
        doReturn(oldValue).when(view).getValue();

        event.target = target;
        target.value = newValue;

        view.onSelectChange(event);

        verify(view).setPickerValue(newValue);
        verify(presenter).clearDataTypesList();
    }

    @Test
    public void testOnSelectChangeWhenTheSameValueIsSet() {

        final JQuerySelectPickerEvent event = mock(JQuerySelectPickerEvent.class);
        final JQuerySelectPickerTarget target = mock(JQuerySelectPickerTarget.class);
        final String newValue = "value";
        final String oldValue = "value";

        doNothing().when(view).setPickerValue(newValue);
        doReturn(oldValue).when(view).getValue();

        event.target = target;
        target.value = newValue;

        view.onSelectChange(event);

        verify(view, never()).setPickerValue(newValue);
        verify(presenter, never()).clearDataTypesList();
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

        verify(view).triggerPickerAction(element, "destroy");
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
        doNothing().when(view).setPickerValue(Mockito.<String>any());
        when(presenter.getDataType()).thenReturn(dataType);
        typeText.classList = mock(DOMTokenList.class);

        view.enableEditMode();

        verify(typeText.classList).add(HIDDEN_CSS_CLASS);
        verify(view).showSelectPicker();
        verify(view).setPickerValue(type);
    }

    @Test
    public void testDisableEditMode() {

        final String expectedTypeText = "type";
        final DataType dataType = makeDataType(expectedTypeText);

        doNothing().when(view).hideSelectPicker();
        when(presenter.getDataType()).thenReturn(dataType);
        typeText.classList = mock(DOMTokenList.class);

        view.disableEditMode();

        assertEquals(expectedTypeText, typeText.textContent);
        verify(typeText.classList).remove(HIDDEN_CSS_CLASS);
        verify(view).hideSelectPicker();
    }

    private DataType makeDataType(final String name) {

        final DataType dataType = spy(new DataType(null));

        doReturn("uuid").when(dataType).getUUID();
        doReturn("parentUUID").when(dataType).getParentUUID();
        doReturn(name).when(dataType).getName();
        doReturn(name).when(dataType).getType();

        return dataType;
    }
}
