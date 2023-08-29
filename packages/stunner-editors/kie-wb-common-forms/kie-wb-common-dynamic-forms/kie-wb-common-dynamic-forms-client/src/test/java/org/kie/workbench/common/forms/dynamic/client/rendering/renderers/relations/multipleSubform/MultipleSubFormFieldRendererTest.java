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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform;

import java.util.HashMap;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.AbstractFieldRendererTest;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroup;
import org.kie.workbench.common.forms.dynamic.client.resources.i18n.FormRenderingConstants;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MultipleSubFormFieldRendererTest extends AbstractFieldRendererTest<MultipleSubFormFieldRenderer, MultipleSubFormFieldDefinition, FieldSetFormGroup> {

    private static String CREATION_FORM = "creation_form";
    private static String EDITION_FORM = "edition_form";

    private static String NAME = "multipleSubForm";

    private Map<String, FormDefinition> availableForms = new HashMap<>();

    @Mock
    private MultipleSubFormWidget multipleSubFormWidget;

    @Mock
    private FieldSetFormGroup formGroup;

    @InjectMocks
    @Spy
    private MultipleSubFormFieldRenderer multipleSubFormFieldRenderer;

    @Before
    public void init() {
        super.init();

        availableForms.put(CREATION_FORM, mock(FormDefinition.class));
        availableForms.put(EDITION_FORM, mock(FormDefinition.class));

        when(context.getAvailableForms()).thenReturn(availableForms);
        when(formGroupsInstance.get()).thenReturn(formGroup);
    }

    @Test
    public void testGetFormGroup() {
        renderer.getFormGroup(RenderMode.EDIT_MODE);

        verify(formGroupsInstance).get();

        multipleSubFormWidget.config(any(), any());

        verify(formGroup).render(any(), any());
    }

    @Override
    protected void checkName(String name) {
        assertEquals(renderer.RENDERER_NAME, name);
    }

    @Test
    public void testRenderWithConfigErrorsNoColumnMetas() {
        fieldDefinition.getColumnMetas().clear();
        testRenderWithConfigErrors(FormRenderingConstants.MultipleSubformNoColumns);
    }

    @Test
    public void testRenderWithConfigErrorsMissingCreationForm() {
        availableForms.remove(CREATION_FORM);
        testRenderWithConfigErrors(FormRenderingConstants.MultipleSubformWrongCreationForm);
    }

    @Test
    public void testRenderWithConfigErrorsEmptyCreationForm() {
        fieldDefinition.setCreationForm(null);
        testRenderWithConfigErrors(FormRenderingConstants.MultipleSubformNoCreationForm);
    }

    @Test
    public void testRenderWithConfigErrorsMissingEditionForm() {
        availableForms.remove(EDITION_FORM);
        testRenderWithConfigErrors(FormRenderingConstants.MultipleSubformWongEditionForm);
    }

    @Test
    public void testRenderWithConfigErrorsEmptyEditionForm() {
        fieldDefinition.setEditionForm(null);
        testRenderWithConfigErrors(FormRenderingConstants.MultipleSubformNoEditionForm);
    }

    @Override
    protected MultipleSubFormFieldRenderer getRendererInstance() {
        return multipleSubFormFieldRenderer;
    }

    @Override
    protected MultipleSubFormFieldDefinition getFieldDefinition() {
        MultipleSubFormFieldDefinition multipleSubFormFieldDefinition = new MultipleSubFormFieldDefinition();

        multipleSubFormFieldDefinition.setName(NAME);
        multipleSubFormFieldDefinition.setBinding(NAME);

        multipleSubFormFieldDefinition.getColumnMetas().add(new TableColumnMeta("a", "b"));
        multipleSubFormFieldDefinition.setCreationForm(CREATION_FORM);
        multipleSubFormFieldDefinition.setEditionForm(EDITION_FORM);

        return multipleSubFormFieldDefinition;
    }
}
