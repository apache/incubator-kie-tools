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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.AbstractFieldRendererTest;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.MultipleInput;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.AbstractMultipleInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.StringMultipleInputFieldDefinition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MultipleInputFieldRendererTest extends AbstractFieldRendererTest<MultipleInputFieldRenderer, AbstractMultipleInputFieldDefinition, DefaultFormGroup> {

    private static String NAME = "multipleInput";

    @Mock
    private MultipleInput multipleInput;

    @Mock
    private DefaultFormGroup formGroup;

    @InjectMocks
    @Spy
    private MultipleInputFieldRenderer multipleInputFieldRenderer;

    @Before
    public void init() {
        super.init();

        when(formGroupsInstance.get()).thenReturn(formGroup);
    }

    @Test
    public void testGetFormGroup() {
        testGetFormGroup(false);
    }

    @Test
    public void testGetFormGroupReadOnly() {
        fieldDefinition.setReadOnly(true);
        testGetFormGroup(true);
    }

    private void testGetFormGroup(boolean readOnly) {
        renderer.getFormGroup(RenderMode.EDIT_MODE);

        verify(formGroupsInstance).get();
        verify(multipleInput).setPageSize(eq(fieldDefinition.getPageSize()));
        verify(multipleInput).init(eq(fieldDefinition.getStandaloneClassName()));
        verify(multipleInput, readOnly ? times(1) : never()).setReadOnly(eq(true));
        verify(multipleInput).asWidget();

        verify(formGroup).render(any(), eq(fieldDefinition));
    }

    @Override
    protected MultipleInputFieldRenderer getRendererInstance() {
        return multipleInputFieldRenderer;
    }

    @Override
    protected AbstractMultipleInputFieldDefinition getFieldDefinition() {
        StringMultipleInputFieldDefinition multipleInputFieldDefinition = new StringMultipleInputFieldDefinition();

        multipleInputFieldDefinition.setName(NAME);
        multipleInputFieldDefinition.setBinding(NAME);

        return multipleInputFieldDefinition;
    }
}
