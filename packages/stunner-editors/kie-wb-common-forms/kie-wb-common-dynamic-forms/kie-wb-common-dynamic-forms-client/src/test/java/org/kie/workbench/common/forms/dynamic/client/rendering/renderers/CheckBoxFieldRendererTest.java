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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.SimpleCheckBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.AbstractFieldRendererTest;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.checkbox.CheckBoxFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CheckBoxFieldRendererTest extends AbstractFieldRendererTest<CheckBoxFieldRenderer, CheckBoxFieldDefinition, CheckBoxFormGroup> {

    private static String NAME = "checkbox";

    @Mock
    private SimpleCheckBox checkBox;

    @Mock
    private CheckBoxFormGroup formGroup;

    @InjectMocks
    @Spy
    private CheckBoxFieldRenderer checkBoxFieldRenderer;

    @Before
    public void init() {
        super.init();

        when(formGroupsInstance.get()).thenReturn(formGroup);
    }

    @Test
    public void testGetFormGroup() {
        renderer.getFormGroup(RenderMode.EDIT_MODE);

        verify(formGroupsInstance).get();
        verify(checkBox).setId(any());
        verify(checkBox).setName(Mockito.<String>any());
        verify(checkBox).setEnabled(eq(!fieldDefinition.getReadOnly()));

        verify(formGroup).render(eq(checkBox), eq(fieldDefinition));
    }

    @Override
    protected CheckBoxFieldRenderer getRendererInstance() {
        return checkBoxFieldRenderer;
    }

    @Override
    protected CheckBoxFieldDefinition getFieldDefinition() {
        CheckBoxFieldDefinition checkBoxFieldDefinition = new CheckBoxFieldDefinition();

        checkBoxFieldDefinition.setName(NAME);
        checkBoxFieldDefinition.setBinding(NAME);

        return checkBoxFieldDefinition;
    }
}
