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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.google.gwt.thirdparty.guava.common.collect.Ordering;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.property.dmn.QNameFieldConverter;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TypePickerWidgetTest {

    private static final String VALUE = "value";

    @Mock
    private Button typeButton;

    @Mock
    private TranslationService translationService;

    @Mock
    private QNameFieldConverter qNameFieldConverter;

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @GwtMock
    @SuppressWarnings("unused")
    private Select typeSelector;

    @Mock
    private com.google.gwt.user.client.Element typeSelectorElement;

    @GwtMock
    @SuppressWarnings("unused")
    private Option option;

    @Mock
    private com.google.gwt.user.client.Element optionElement;

    @Mock
    private DMNModelInstrumentedBase dmnModel;

    @Captor
    private ArgumentCaptor<BuiltInType> builtInTypeCaptor;

    @Captor
    private ArgumentCaptor<ItemDefinition> itemDefinitionCaptor;

    private Definitions definitions;

    private TypePickerWidget picker;

    @Before
    public void setup() {
        this.definitions = new Definitions();
        this.definitions.getItemDefinition().add(new ItemDefinition());

        when(typeSelector.getElement()).thenReturn(typeSelectorElement);
        when(option.getElement()).thenReturn(optionElement);
        when(dmnGraphUtils.getDefinitions()).thenReturn(definitions);
        when(translationService.getTranslation(anyString())).thenAnswer(i -> i.getArguments()[0]);

        this.picker = spy(new TypePickerWidget(typeButton,
                                               translationService,
                                               qNameFieldConverter,
                                               dmnGraphUtils));
    }

    @Test
    public void testInitialisation() {
        verify(typeSelector).setShowTick(eq(true));
        verify(typeSelector).setLiveSearch(eq(true));
        verify(typeSelector).setLiveSearchPlaceholder(eq(DMNEditorConstants.TypePickerWidget_Choose));
        verify(typeSelectorElement).setAttribute(eq("data-container"), eq("body"));
        verify(typeSelector).refresh();
    }

    @Test
    public void testSetDMNModel_BasicInitialisation() {
        picker.setDMNModel(dmnModel);

        verify(qNameFieldConverter).setDMNModel(eq(dmnModel));
        verify(typeSelector).clear();
    }

    @Test
    public void testSetDMNModel_BuiltInTypes() {
        picker.setDMNModel(dmnModel);

        final BuiltInType[] bits = BuiltInType.values();
        verify(picker, times(bits.length)).makeTypeSelector(builtInTypeCaptor.capture());

        //Checks all BuiltInTypes were handled by makeTypeSelector(BuiltInType)
        final List<BuiltInType> builtInTypes = new ArrayList<>(Arrays.asList(bits));
        assertFalse(builtInTypes.isEmpty());
        final List<BuiltInType> builtInTypesAddedToWidget = builtInTypeCaptor.getAllValues();
        builtInTypes.removeAll(builtInTypesAddedToWidget);
        assertTrue(builtInTypes.isEmpty());

        //Check the items were sorted correctly
        assertTrue(Ordering.from(TypePickerWidget.BUILT_IN_TYPE_COMPARATOR).isOrdered(builtInTypesAddedToWidget));
    }

    @Test
    public void testSetDMNModel_ItemDefinitions() {
        definitions.getItemDefinition().add(new ItemDefinition() {{
            setName(new Name("user_defined_data_type"));
        }});

        picker.setDMNModel(dmnModel);

        final List<ItemDefinition> itemDefinitions = definitions.getItemDefinition();
        verify(picker, times(itemDefinitions.size())).makeTypeSelector(itemDefinitionCaptor.capture());

        //Checks all ItemDefinitions were handled by makeTypeSelector(ItemDefinition)
        assertFalse(itemDefinitions.isEmpty());
        final List<ItemDefinition> itemDefinitionsAddedToWidget = itemDefinitionCaptor.getAllValues();
        itemDefinitions.removeAll(itemDefinitionsAddedToWidget);
        assertTrue(itemDefinitions.isEmpty());

        //Check the items were sorted correctly
        assertTrue(Ordering.from(TypePickerWidget.ITEM_DEFINITION_COMPARATOR).isOrdered(itemDefinitionsAddedToWidget));
    }

    @Test
    public void testSetDMNModel_NoItemDefinitions() {
        definitions.getItemDefinition().clear();

        picker.setDMNModel(dmnModel);

        verify(picker, never()).addDivider();
        verify(picker, never()).makeTypeSelector(any(ItemDefinition.class));
    }

    @Test
    public void testSetDMNModel_Divider() {
        final InOrder order = inOrder(picker);

        picker.setDMNModel(dmnModel);

        order.verify(picker, atLeastOnce()).makeTypeSelector(any(BuiltInType.class));
        order.verify(picker).addDivider();
        order.verify(picker, atLeastOnce()).makeTypeSelector(any(ItemDefinition.class));
    }

    @Test
    public void testMakeTypeSelectorForBuiltInType() {
        final BuiltInType bit = BuiltInType.ANY;

        final String[] optionText = new String[]{""};
        doAnswer(i -> {
            optionText[0] = (String) i.getArguments()[0];
            return null;
        }).when(option).setText(anyString());

        final String[] optionValue = new String[]{""};
        doAnswer(i -> {
            optionValue[0] = (String) i.getArguments()[0];
            return null;
        }).when(option).setValue(anyString());

        when(qNameFieldConverter.toWidgetValue(any(QName.class))).thenReturn(bit.getName());

        final Optional<Option> oo = picker.makeTypeSelector(bit);

        assertTrue(oo.isPresent());
        assertEquals(bit.getName(),
                     optionText[0]);
        assertEquals(bit.getName(),
                     optionValue[0]);
    }

    @Test
    public void testMakeTypeSelectorForItemDefinition() {
        final ItemDefinition itemDefinition = definitions.getItemDefinition().get(0);

        final String[] optionText = new String[]{""};
        doAnswer(i -> {
            optionText[0] = (String) i.getArguments()[0];
            return null;
        }).when(option).setText(anyString());

        final String[] optionValue = new String[]{""};
        doAnswer(i -> {
            optionValue[0] = (String) i.getArguments()[0];
            return null;
        }).when(option).setValue(anyString());

        when(qNameFieldConverter.toWidgetValue(any(QName.class))).thenReturn(itemDefinition.getName().getValue());

        final Optional<Option> oo = picker.makeTypeSelector(itemDefinition);

        assertTrue(oo.isPresent());
        assertEquals(itemDefinition.getName().getValue(),
                     optionText[0]);
        assertEquals(itemDefinition.getName().getValue(),
                     optionValue[0]);
    }

    @Test
    public void testSetValue() {
        picker.setValue(VALUE);

        verify(typeSelector).setValue(eq(VALUE), eq(false));
        verify(picker, never()).fireValueChangeEvent(anyString());
    }

    @Test
    public void testSetValueFireEvent() {
        picker.setValue(VALUE, true);

        verify(typeSelector).setValue(eq(VALUE), eq(false));
        verify(picker).fireValueChangeEvent(eq(null));
    }

    @Test
    public void testSetValueDoNotFireEvent() {
        picker.setValue(VALUE, false);

        verify(typeSelector).setValue(eq(VALUE), eq(false));
        verify(picker, never()).fireValueChangeEvent(anyString());
    }

    @Test
    public void testGetValue() {
        picker.setValue(VALUE);

        assertEquals(VALUE, picker.getValue());
    }

    @Test
    public void testDisable() {
        picker.setEnabled(false);

        verify(typeButton).setEnabled(eq(false));
        typeSelector.setEnabled(eq(false));

        assertFalse(picker.isEnabled());
    }

    @Test
    public void testEnable() {
        picker.setEnabled(true);

        verify(typeButton).setEnabled(eq(true));
        typeSelector.setEnabled(eq(true));

        assertTrue(picker.isEnabled());
    }
}
