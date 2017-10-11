/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.forms.dynamic.client.rendering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.CheckBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.DecimalBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.IntegerBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.SliderFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.TextAreaFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.TextBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.DatePickerFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.MultipleSubFormFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform.SubFormFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.listBox.EnumListBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.listBox.StringListBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.radioGroup.StringRadioGroupFieldRenderer;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.type.CheckBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.type.DatePickerFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.type.DecimalBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.type.IntegerBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.EnumListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.StringListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.StringRadioGroupFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.type.RadioGroupFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition.DoubleSliderDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition.IntegerSliderDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.type.SliderFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.type.TextBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.type.MultipleSubFormFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.type.SubFormFieldType;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FieldRendererManagerImplTest {

    @Mock
    SyncBeanManager beanManager;

    @Mock
    ManagedInstance<FieldRenderer> managedInstance;

    Map<Class, FieldRenderer> renderersMap = new HashMap<>();

    FieldRendererManagerImpl fieldRendererManager;

    @Before
    public void init() {

        registerRenderer(CheckBoxFieldRenderer.class,
                         CheckBoxFieldType.NAME,
                         null);
        registerRenderer(DatePickerFieldRenderer.class,
                         DatePickerFieldType.NAME,
                         null);
        registerRenderer(EnumListBoxFieldRenderer.class,
                         ListBoxFieldType.NAME,
                         EnumListBoxFieldDefinition.class);
        registerRenderer(StringListBoxFieldRenderer.class,
                         ListBoxFieldType.NAME,
                         StringListBoxFieldDefinition.class);
        registerRenderer(StringRadioGroupFieldRenderer.class,
                         RadioGroupFieldType.NAME,
                         StringRadioGroupFieldDefinition.class);
        registerRenderer(TextAreaFieldRenderer.class,
                         TextAreaFieldType.NAME,
                         null);
        registerRenderer(SliderFieldRenderer.class,
                         SliderFieldType.NAME,
                         null);
        registerRenderer(MultipleSubFormFieldRenderer.class,
                         MultipleSubFormFieldType.NAME,
                         null);
        registerRenderer(SubFormFieldRenderer.class,
                         SubFormFieldType.NAME,
                         null);
        registerRenderer(TextBoxFieldRenderer.class,
                         TextBoxFieldType.NAME,
                         null);
        registerRenderer(DecimalBoxFieldRenderer.class,
                         DecimalBoxFieldType.NAME,
                         null);
        registerRenderer(IntegerBoxFieldRenderer.class,
                         IntegerBoxFieldType.NAME,
                         null);

        Collection<SyncBeanDef<FieldRenderer>> renderers = new ArrayList<>();

        renderersMap.forEach((fieldRendererClass, fieldRenderer) -> {
            SyncBeanDef<FieldRenderer> def = mock(SyncBeanDef.class);
            when(def.getInstance()).thenReturn(fieldRenderer);
            when(def.newInstance()).thenReturn(fieldRenderer);
            when(def.getBeanClass()).thenReturn(fieldRendererClass);
            renderers.add(def);
        });

        when(beanManager.lookupBeans(FieldRenderer.class)).thenReturn(renderers);

        when(managedInstance.select(any(Class.class))).thenAnswer(invocationOnMock -> {
            ManagedInstance<FieldRenderer> newInstance = mock(ManagedInstance.class);
            when(newInstance.get()).thenReturn(renderersMap.get(invocationOnMock.getArguments()[0]));
            return newInstance;
        });

        fieldRendererManager = new FieldRendererManagerImpl(managedInstance) {
            {
                registerRenderers(renderers);
            }
        };
    }

    @Test
    public void testFunctionallity() {
        testRendererFor(new CheckBoxFieldDefinition());
        testRendererFor(new DatePickerFieldDefinition());
        testRendererFor(new EnumListBoxFieldDefinition());
        testRendererFor(new StringListBoxFieldDefinition());
        testRendererFor(new StringRadioGroupFieldDefinition());
        testRendererFor(new TextAreaFieldDefinition());
        testRendererFor(new IntegerSliderDefinition());
        testRendererFor(new DoubleSliderDefinition());
        testRendererFor(new MultipleSubFormFieldDefinition());
        testRendererFor(new SubFormFieldDefinition());
        testRendererFor(new TextBoxFieldDefinition());
        testRendererFor(new DecimalBoxFieldDefinition());
        testRendererFor(new IntegerBoxFieldDefinition());
    }

    protected void testRendererFor(FieldDefinition field) {
        FieldRenderer renderer = fieldRendererManager.getRendererForField(field);
        assertNotNull(renderer);
        assertTrue(renderersMap.containsValue(renderer));
    }

    @After
    public void destroy() {
        fieldRendererManager.destroy();
        verify(managedInstance).destroyAll();
    }

    protected void registerRenderer(Class<? extends FieldRenderer> rendererClass,
                                    String fieldType,
                                    Class<? extends FieldDefinition> relatedFieldType) {
        FieldRenderer renderer = mock(rendererClass);
        when(renderer.getSupportedCode()).thenReturn(fieldType);
        if (renderer instanceof FieldDefinitionFieldRenderer) {
            when(((FieldDefinitionFieldRenderer) renderer).getSupportedFieldDefinition()).thenReturn(relatedFieldType);
        }
        renderersMap.put(rendererClass,
                         renderer);
    }
}
