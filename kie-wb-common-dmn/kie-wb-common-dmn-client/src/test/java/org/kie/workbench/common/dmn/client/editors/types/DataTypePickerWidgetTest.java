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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.thirdparty.guava.common.collect.Ordering;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.extras.select.client.ui.OptGroup;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.CSSStyleDeclaration;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase.Namespace;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypePickerWidgetTest {

    private static final QName VALUE = new QName();

    private static final String WIDGET_VALUE = "[][<Undefined>][]";

    @Mock
    private Anchor typeButton;

    @Mock
    private Div manageContainer;

    @Mock
    private CSSStyleDeclaration manageContainerStyle;

    @Mock
    private Span manageLabel;

    @Mock
    private TranslationService translationService;

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @Mock
    private EventSourceMock<DataTypePageTabActiveEvent> dataTypePageActiveEvent;

    @GwtMock
    @SuppressWarnings("unused")
    private Select typeSelector;

    @Mock
    private com.google.gwt.user.client.Element typeSelectorElement;

    @GwtMock
    @SuppressWarnings("unused")
    private Option option;

    @GwtMock
    @SuppressWarnings("unused")
    private OptGroup group;

    @Mock
    private com.google.gwt.user.client.Element optionElement;

    @Mock
    private DMNModelInstrumentedBase dmnModel;

    private ItemDefinitionUtils itemDefinitionUtils;

    @Captor
    private ArgumentCaptor<BuiltInType> builtInTypeCaptor;

    @Captor
    private ArgumentCaptor<ItemDefinition> itemDefinitionCaptor;

    private Definitions definitions;

    private DataTypePickerWidget picker;

    private QNameConverter qNameConverter;

    @Before
    public void setup() {
        this.definitions = new Definitions();
        this.definitions.getItemDefinition().add(new ItemDefinition());
        this.definitions.getNsContext().put(Namespace.FEEL.getPrefix(),
                                            Namespace.FEEL.getUri());

        this.qNameConverter = spy(new QNameConverter());
        this.itemDefinitionUtils = spy(new ItemDefinitionUtils(dmnGraphUtils));

        when(typeSelector.getElement()).thenReturn(typeSelectorElement);
        when(option.getElement()).thenReturn(optionElement);
        when(dmnGraphUtils.getDefinitions()).thenReturn(definitions);
        when(dmnModel.getPrefixForNamespaceURI(anyString())).thenReturn(Optional.empty());
        when(manageContainer.getStyle()).thenReturn(manageContainerStyle);

        when(translationService.getTranslation(anyString())).thenAnswer(i -> i.getArguments()[0]);

        this.picker = spy(new DataTypePickerWidget(typeButton,
                                                   manageContainer,
                                                   manageLabel,
                                                   translationService,
                                                   qNameConverter,
                                                   dmnGraphUtils,
                                                   dataTypePageActiveEvent,
                                                   itemDefinitionUtils));
    }

    @Test
    public void testInitialisation() {
        verify(typeSelector).setShowTick(eq(true));
        verify(typeSelector).setLiveSearch(eq(true));
        verify(typeSelectorElement).setAttribute(eq("data-container"), eq("body"));
        verify(typeSelector).refresh();

        verify(typeSelector).setLiveSearchPlaceholder(eq(DMNEditorConstants.TypePickerWidget_Choose));
        verify(manageLabel).setTextContent(eq(DMNEditorConstants.TypePickerWidget_Manage));
    }

    @Test
    public void testSetDMNModel_BasicInitialisation() {
        reset(typeSelector);
        picker.setDMNModel(dmnModel);

        verify(qNameConverter).setDMNModel(eq(dmnModel));
        verify(typeSelector).clear();
        verify(picker).addBuiltInTypes();
        verify(picker).addItemDefinitions();
        verify(typeSelector).refresh();
    }

    @Test
    public void testSetDMNModel_BuiltInTypes() {
        picker.addBuiltInTypes();

        final BuiltInType[] bits = BuiltInType.values();
        verify(picker, times(bits.length)).makeTypeSelector(builtInTypeCaptor.capture());
        verify(group).setLabel(DMNEditorConstants.DataTypeSelectView_DefaultTitle);
        verify(group, times(bits.length)).add(eq(option));
        verify(typeSelector).add(group);

        //Checks all BuiltInTypes were handled by makeTypeSelector(BuiltInType)
        final List<BuiltInType> builtInTypes = Arrays.asList(bits);
        assertFalse(builtInTypes.isEmpty());
        final List<BuiltInType> builtInTypesAddedToWidget = builtInTypeCaptor.getAllValues();

        assertThat(builtInTypes).hasSameElementsAs(builtInTypesAddedToWidget);

        //Check the items were sorted correctly
        assertTrue(Ordering.from(DataTypePickerWidget.BUILT_IN_TYPE_COMPARATOR).isOrdered(builtInTypesAddedToWidget));

        //First item must be "<Undefined>"
        assertEquals(builtInTypesAddedToWidget.get(0).getName(), BuiltInType.UNDEFINED.getName());
    }

    @Test
    public void testSetDMNModel_ItemDefinitions() {
        definitions.getItemDefinition().add(new ItemDefinition() {{
            setName(new Name("user_defined_data_type"));
        }});

        picker.addItemDefinitions();

        final List<ItemDefinition> itemDefinitions = definitions.getItemDefinition();
        verify(picker, times(itemDefinitions.size())).makeTypeSelector(itemDefinitionCaptor.capture());
        verify(group).setLabel(DMNEditorConstants.DataTypeSelectView_CustomTitle);
        verify(group, times(itemDefinitions.size())).add(eq(option));
        verify(typeSelector).add(group);

        //Checks all ItemDefinitions were handled by makeTypeSelector(ItemDefinition)
        assertFalse(itemDefinitions.isEmpty());
        final List<ItemDefinition> itemDefinitionsAddedToWidget = itemDefinitionCaptor.getAllValues();
        itemDefinitions.removeAll(itemDefinitionsAddedToWidget);
        assertTrue(itemDefinitions.isEmpty());

        //Check the items were sorted correctly
        assertTrue(Ordering.from(DataTypePickerWidget.ITEM_DEFINITION_COMPARATOR).isOrdered(itemDefinitionsAddedToWidget));
    }

    @Test
    public void testSetDMNModel_NoItemDefinitions() {
        definitions.getItemDefinition().clear();

        picker.setDMNModel(dmnModel);

        verify(picker, never()).makeTypeSelector(any(ItemDefinition.class));
    }

    @Test
    public void testMakeTypeSelectorForBuiltInType() {
        final BuiltInType bit = BuiltInType.ANY;

        final ArgumentCaptor<String> optionTextCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> optionValueCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<QName> qNameCaptor = ArgumentCaptor.forClass(QName.class);

        final Optional<Option> oo = picker.makeTypeSelector(bit);
        verify(option).setText(optionTextCaptor.capture());
        verify(option).setValue(optionValueCaptor.capture());
        verify(qNameConverter).toWidgetValue(qNameCaptor.capture());

        final QName normalisedQName = qNameCaptor.getValue();
        assertEquals("", normalisedQName.getNamespaceURI());
        assertEquals(QName.NULL_NS_URI, normalisedQName.getPrefix());
        assertEquals(bit.getName(), normalisedQName.getLocalPart());

        assertTrue(oo.isPresent());
        assertEquals(bit.getName(), optionTextCaptor.getValue());
        assertEquals("[][Any][]", optionValueCaptor.getValue());
    }

    @Test
    public void testMakeTypeSelectorForItemDefinition() {
        final String itemDefinitionNameValue = "person";
        final ItemDefinition itemDefinition = definitions.getItemDefinition().get(0);
        itemDefinition.setName(new Name(itemDefinitionNameValue));

        final ArgumentCaptor<String> optionTextCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> optionValueCaptor = ArgumentCaptor.forClass(String.class);

        final ArgumentCaptor<QName> qNameCaptor = ArgumentCaptor.forClass(QName.class);
        doReturn(itemDefinition.getName().getValue()).when(qNameConverter).toWidgetValue(any(QName.class));

        final Optional<Option> oo = picker.makeTypeSelector(itemDefinition);
        verify(option).setText(optionTextCaptor.capture());
        verify(option).setValue(optionValueCaptor.capture());
        verify(qNameConverter).toWidgetValue(qNameCaptor.capture());

        assertTrue(oo.isPresent());
        assertEquals(itemDefinitionNameValue, optionTextCaptor.getValue());
        assertEquals(itemDefinitionNameValue, optionValueCaptor.getValue());
        assertEquals(itemDefinitionNameValue, qNameCaptor.getValue().getLocalPart());
    }

    @Test
    public void testMakeTypeSelectorForNullItemDefinition() {
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        doReturn(null).when(itemDefinition).getName();
        assertFalse(picker.makeTypeSelector(itemDefinition).isPresent());
        verify(option, never()).setText(anyString());
        verify(option, never()).setValue(anyString());
        verify(qNameConverter, never()).toWidgetValue(any(QName.class));
    }

    @Test
    public void testSetValue() {
        picker.setValue(VALUE);

        verify(typeSelector).setValue(eq(WIDGET_VALUE), eq(false));
        verify(picker, never()).fireValueChangeEvent(any());
    }

    @Test
    public void testSetValueFireEvent() {
        picker.setValue(VALUE, true);

        verify(typeSelector).setValue(eq(WIDGET_VALUE), eq(false));
        verify(picker).fireValueChangeEvent(eq(null));
    }

    @Test
    public void testSetValueDoNotFireEvent() {
        picker.setValue(VALUE, false);

        verify(typeSelector).setValue(eq(WIDGET_VALUE), eq(false));
        verify(picker, never()).fireValueChangeEvent(any());
    }

    @Test
    public void testGetValue() {
        picker.setValue(VALUE);

        assertEquals(VALUE, picker.getValue());
    }

    @Test
    public void testDisable() {
        picker.setEnabled(false);

        verify(typeSelector).setEnabled(eq(false));

        assertFalse(picker.isEnabled());
    }

    @Test
    public void testEnable() {
        picker.setEnabled(true);

        verify(typeSelector).setEnabled(eq(true));

        assertTrue(picker.isEnabled());
    }

    @Test
    public void testOnClickTypeButton() {
        final ClickEvent clickEvent = mock(ClickEvent.class);

        picker.onClickTypeButton(clickEvent);

        verify(dataTypePageActiveEvent).fire(any(DataTypePageTabActiveEvent.class));
    }

    @Test
    public void testShowManageLabel() {
        picker.showManageLabel();

        verify(manageContainerStyle).removeProperty(eq(DataTypePickerWidget.CSS_DISPLAY));
    }

    @Test
    public void testHideManageLabel() {
        picker.hideManageLabel();

        verify(manageContainerStyle).setProperty(eq(DataTypePickerWidget.CSS_DISPLAY),
                                                 eq(DataTypePickerWidget.CSS_DISPLAY_NONE));
    }

    @Test
    public void testNormaliseBuiltInTypeTypeRef() {

        final QName typeRef = mock(QName.class);
        final QName expectedTypeRef = mock(QName.class);

        doReturn(expectedTypeRef).when(itemDefinitionUtils).normaliseTypeRef(typeRef);

        final QName actualTypeRef = picker.normaliseBuiltInTypeTypeRef(typeRef);

        assertEquals(expectedTypeRef, actualTypeRef);
    }

    @Test
    public void testPopulateTypeSelector(){

        picker.populateTypeSelector();

        final InOrder inOrder = inOrder(typeSelector);
        inOrder.verify(typeSelector).clear();
        verify(picker).addBuiltInTypes();
        verify(picker).addItemDefinitions();

        inOrder.verify(typeSelector).refresh();
    }
}
