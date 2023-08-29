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
import org.gwtbootstrap3.client.ui.TextArea;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.AbstractFieldRendererTest;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TextAreaFieldRendererTest extends AbstractFieldRendererTest<TextAreaFieldRenderer, TextAreaFieldDefinition, DefaultFormGroup> {

    private static String NAME = "textArea";

    @Mock
    private TextArea textArea;

    @Mock
    private DefaultFormGroup formGroup;

    @InjectMocks
    @Spy
    private TextAreaFieldRenderer textAreaFieldRenderer;

    @Before
    public void init() {
        super.init();

        when(formGroupsInstance.get()).thenReturn(formGroup);
    }

    @Test
    public void testGetFormGroup() {
        renderer.getFormGroup(RenderMode.EDIT_MODE);

        verify(formGroupsInstance).get();
        verify(textArea).setId(any());
        verify(textArea).setName(Mockito.<String>any());
        verify(textArea).setPlaceholder(eq(fieldDefinition.getPlaceHolder()));
        verify(textArea).setVisibleLines(eq(fieldDefinition.getRows()));
        verify(textArea).setEnabled(eq(!fieldDefinition.getReadOnly()));

        verify(formGroup).render(Mockito.<String>any(), eq(textArea), eq(fieldDefinition));
    }

    @Override
    protected TextAreaFieldRenderer getRendererInstance() {
        return textAreaFieldRenderer;
    }

    @Override
    protected TextAreaFieldDefinition getFieldDefinition() {
        TextAreaFieldDefinition textAreaFieldDefinition = new TextAreaFieldDefinition();

        textAreaFieldDefinition.setName(NAME);
        textAreaFieldDefinition.setBinding(NAME);
        textAreaFieldDefinition.setPlaceHolder(NAME);

        return textAreaFieldDefinition;
    }
}
