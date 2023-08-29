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

package org.kie.workbench.common.forms.dynamic.client.rendering;

import org.jboss.errai.ioc.client.api.ManagedInstance;
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
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.picture.PictureFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.MultipleSubFormFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform.SubFormFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.listBox.EnumListBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.listBox.IntegerListBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.listBox.StringListBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.radioGroup.IntegerRadioGroupFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.radioGroup.StringRadioGroupFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.test.TestFieldRendererTypesProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.image.definition.PictureFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.EnumListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.IntegerListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.StringListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.IntegerRadioGroupFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.StringRadioGroupFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition.DoubleSliderDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition.IntegerSliderDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FieldRendererManagerImplTest {

    @Mock
    ManagedInstance<FieldRenderer> managedInstance;

    FieldRendererManagerImpl fieldRendererManager;

    @Before
    public void init() {
        FieldRendererTypeRegistry.load(new TestFieldRendererTypesProvider());

        fieldRendererManager = new FieldRendererManagerImpl(managedInstance);

        when(managedInstance.select(any(Class.class))).thenAnswer(invocationOnMock -> {
            ManagedInstance<FieldRenderer> newInstance = mock(ManagedInstance.class);
            when(newInstance.get()).thenReturn(mock((Class<FieldRenderer>)invocationOnMock.getArguments()[0]));
            return newInstance;
        });
    }

    @Test
    public void testFunctionallity() {
        testRendererFor(new CheckBoxFieldDefinition(), CheckBoxFieldRenderer.class, 1);
        testRendererFor(new DatePickerFieldDefinition(), DatePickerFieldRenderer.class, 1);
        testRendererFor(new EnumListBoxFieldDefinition(), EnumListBoxFieldRenderer.class, 1);
        testRendererFor(new StringListBoxFieldDefinition(), StringListBoxFieldRenderer.class, 1);
        testRendererFor(new IntegerListBoxFieldDefinition(), IntegerListBoxFieldRenderer.class, 1);
        testRendererFor(new StringRadioGroupFieldDefinition(), StringRadioGroupFieldRenderer.class, 1);
        testRendererFor(new IntegerRadioGroupFieldDefinition(), IntegerRadioGroupFieldRenderer.class, 1);
        testRendererFor(new TextAreaFieldDefinition(), TextAreaFieldRenderer.class, 1);
        testRendererFor(new IntegerSliderDefinition(), SliderFieldRenderer.class, 1);
        testRendererFor(new DoubleSliderDefinition(), SliderFieldRenderer.class, 2);
        testRendererFor(new MultipleSubFormFieldDefinition(), MultipleSubFormFieldRenderer.class, 1);
        testRendererFor(new SubFormFieldDefinition(), SubFormFieldRenderer.class, 1);
        testRendererFor(new TextBoxFieldDefinition(), TextBoxFieldRenderer.class, 1);
        testRendererFor(new CharacterBoxFieldDefinition(), TextBoxFieldRenderer.class, 2);
        testRendererFor(new DecimalBoxFieldDefinition(), DecimalBoxFieldRenderer.class, 1);
        testRendererFor(new IntegerBoxFieldDefinition(), IntegerBoxFieldRenderer.class, 1);

        testUnexistingRenderer(new PictureFieldDefinition());
    }

    private void testRendererFor(FieldDefinition field, Class<? extends FieldRenderer> expectedRenderer, int count) {
        FieldRenderer renderer = fieldRendererManager.getRendererForField(field);

        assertNotNull(renderer);

        verify(managedInstance, times(count)).select(expectedRenderer);
    }

    private void testUnexistingRenderer(FieldDefinition field) {
        FieldRenderer renderer = fieldRendererManager.getRendererForField(field);

        assertNull(renderer);

        verify(managedInstance, never()).select(PictureFieldRenderer.class);
    }

    @After
    public void destroy() {
        fieldRendererManager.destroy();
        verify(managedInstance).destroyAll();
    }
}
