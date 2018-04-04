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
package org.kie.workbench.common.forms.editor.client.editor.properties;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.service.shared.adf.DynamicFormModelGenerator;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.StaticModelFormRenderingContext;
import org.kie.workbench.common.forms.editor.client.editor.properties.binding.dynamic.DynamicDataBinderEditor;
import org.kie.workbench.common.forms.editor.client.editor.properties.binding.statik.StaticDataBinderEditor;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FieldPropertiesRendererTest {

    public static final String TYPE_CODE = "RadioGroup";

    private FieldDefinition lastNameField;

    private FieldPropertiesRenderer renderer;

    @Mock
    private FieldPropertiesRenderer.FieldPropertiesRendererView view;

    @Mock
    private DynamicFormModelGenerator dynamicFormModelGenerator;

    @Mock
    private StaticDataBinderEditor staticDataBindingEditor;

    @Mock
    private DynamicDataBinderEditor dynamicDataBindingEditor;

    @Mock
    private FieldPropertiesRendererHelper helper;

    private FieldManager fieldManager;

    @Mock
    private StaticModelFormRenderingContext context;

    @Before
    public void setUp() throws Exception {
        initFields();
    }

    protected void loadContent() {
        fieldManager = spy(new TestFieldManager());
        renderer = spy(new FieldPropertiesRenderer(view,
                                                   dynamicFormModelGenerator,
                                                   staticDataBindingEditor,
                                                   dynamicDataBindingEditor,
                                                   fieldManager));
        renderer.init();

        when(helper.getCurrentField()).thenReturn(lastNameField);
        when(helper.getCurrentRenderingContext()).thenReturn(context);

        FormDefinition form = new FormDefinition(null);

        when(context.getRootForm()).thenReturn(form);

        when(dynamicFormModelGenerator.getContextForModel(any())).thenReturn(context);
    }

    @Test
    public void testRender() {
        loadContent();

        renderer.render(helper);

        assertSame(helper,
                   renderer.helper);
        assertNotNull(renderer.fieldCopy);
        verify(renderer,
               times(1)).resetFieldCopy(any());
        verify(fieldManager).getFieldFromProvider(any(),
                                                  any());
    }

    @Test
    public void testOkAndClose() {
        testRender();

        renderer.onPressOk();
        renderer.onClose();

        List<FieldDefinition> fields = helper.getCurrentRenderingContext().getRootForm().getFields();
        assertFalse(fields.contains(renderer.fieldCopy));

        verify(helper,
               times(1)).onPressOk(renderer.fieldCopy);
    }

    @Test
    public void testCloseOrEsc() {
        testRender();

        renderer.onClose();

        List<FieldDefinition> fields = helper.getCurrentRenderingContext().getRootForm().getFields();
        assertFalse(fields.contains(renderer.fieldCopy));
        verify(helper,
               never()).onPressOk(renderer.fieldCopy);
    }

    @Test
    public void testOnFieldTypeChange() {
        testRender();
        when(helper.onFieldTypeChange(any(),
                                      anyString())).thenReturn(lastNameField);

        renderer.onFieldTypeChange(TYPE_CODE);

        verify(helper).onFieldTypeChange(renderer.fieldCopy,
                                         TYPE_CODE);
        verify(renderer,
               atLeastOnce()).render();
    }

    @Test
    public void testOnFieldBindingChange() {
        testRender();
        when(helper.onFieldBindingChange(any(),
                                         anyString())).thenReturn(lastNameField);

        renderer.onFieldBindingChange(lastNameField.getId());

        verify(helper).onFieldBindingChange(renderer.fieldCopy,
                                            lastNameField.getId());
        verify(renderer,
               atLeastOnce()).render();
    }

    @Test
    public void testResetFieldCopy() {
        FieldDefinition originalField = lastNameField;
        loadContent();

        FieldDefinition fieldCopy = renderer.resetFieldCopy(originalField);

        assertEquals(originalField.getId(),
                     fieldCopy.getId());
        assertEquals(originalField.getName(),
                     fieldCopy.getName());
        assertEquals(originalField.getLabel(),
                     fieldCopy.getLabel());
        assertEquals(originalField.getStandaloneClassName(),
                     fieldCopy.getStandaloneClassName());
        assertEquals(originalField.getRequired(),
                     fieldCopy.getRequired());
        assertEquals(originalField.getReadOnly(),
                     fieldCopy.getReadOnly());
        assertEquals(originalField.getValidateOnChange(),
                     fieldCopy.getValidateOnChange());
    }

    @Test
    public void testGetView() {
        loadContent();
        assertSame(view,
                   renderer.getView());
    }

    @Test
    public void testAsWidget() {
        loadContent();
        renderer.asWidget();
        verify(view).asWidget();
    }

    protected void initFields() {
        lastNameField = new TextBoxFieldDefinition();
        lastNameField.setId("lastName");
        lastNameField.setName("employee_lastName");
        lastNameField.setLabel("Last Name");
        lastNameField.setBinding("lastName");
        lastNameField.setStandaloneClassName(String.class.getName());
    }
}
