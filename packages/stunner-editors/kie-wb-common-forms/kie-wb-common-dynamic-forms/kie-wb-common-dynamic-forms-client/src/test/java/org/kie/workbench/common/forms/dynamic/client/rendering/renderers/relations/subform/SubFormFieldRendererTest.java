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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform;

import java.util.HashMap;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.AbstractFieldRendererTest;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.AbstractNestedFormFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform.widget.SubFormWidget;
import org.kie.workbench.common.forms.dynamic.client.resources.i18n.FormRenderingConstants;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.Container;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SubFormFieldRendererTest extends AbstractFieldRendererTest<SubFormFieldRenderer, SubFormFieldDefinition, FieldSetFormGroup> {

    private static String NESTED_FORM = "nested_form";

    private static String NAME = "subForm";

    private Map<String, FormDefinition> availableForms = new HashMap<>();

    @Mock
    private SubFormWidget subFormWidget;

    @Mock
    private FieldSetFormGroup fieldSetFormGroup;

    @Mock
    private ManagedInstance<CollapsibleFormGroup> collapsibleFormGroupManagedInstance;

    @Mock
    private CollapsibleFormGroup collapsibleFormGroup;

    @InjectMocks
    @Spy
    private SubFormFieldRenderer subFormFieldRenderer;

    @Before
    public void init() {
        super.init();

        availableForms.put(NESTED_FORM, mock(FormDefinition.class));

        when(context.getAvailableForms()).thenReturn(availableForms);
        when(context.getCopyFor(Mockito.<String>any(), any(), any())).thenReturn(context);

        when(collapsibleFormGroupManagedInstance.get()).thenReturn(collapsibleFormGroup);

        when(formGroupsInstance.select(any(Class.class))).thenReturn(formGroupsInstance);

        when(formGroupsInstance.get()).thenAnswer(invocation -> {
            if (Container.COLLAPSIBLE.equals(fieldDefinition.getContainer())) {
                return collapsibleFormGroup;
            }
            return fieldSetFormGroup;
        });
    }

    @Test
    public void testGetCollapsibleFormGroup() {
        fieldDefinition.setContainer(Container.COLLAPSIBLE);
        testGetFormGroup(CollapsibleFormGroup.class);
    }

    @Test
    public void testGetFieldSetFormGroup() {
        fieldDefinition.setContainer(Container.FIELD_SET);
        testGetFormGroup(FieldSetFormGroup.class);
    }

    private void testGetFormGroup(Class<? extends AbstractNestedFormFormGroup> expectedGroupType) {
        AbstractNestedFormFormGroup group = (AbstractNestedFormFormGroup) renderer.getFormGroup(RenderMode.EDIT_MODE);

        Assertions.assertThat(group)
                .isNotNull()
                .isInstanceOf(expectedGroupType);

        verify(context).getCopyFor(Mockito.<String>any(), any(), any());

        verify(formGroupsInstance).get();

        subFormWidget.render(eq(context));

        verify(group).render(any(), any());
    }

    @Test
    public void testRenderWithConfigErrorsMissingNestedForm() {
        availableForms.remove(NESTED_FORM);
        testRenderWithConfigErrors(FormRenderingConstants.SubFormWrongForm);
    }

    @Test
    public void testRenderWithConfigErrorsEmptyNestedForm() {
        fieldDefinition.setNestedForm(null);
        testRenderWithConfigErrors(FormRenderingConstants.SubFormNoForm);
    }

    @Override
    protected SubFormFieldRenderer getRendererInstance() {
        return subFormFieldRenderer;
    }

    @Override
    protected SubFormFieldDefinition getFieldDefinition() {
        SubFormFieldDefinition subFormFieldDefinition = new SubFormFieldDefinition();

        subFormFieldDefinition.setName(NAME);
        subFormFieldDefinition.setBinding(NAME);
        subFormFieldDefinition.setNestedForm(NESTED_FORM);

        return subFormFieldDefinition;
    }
}
