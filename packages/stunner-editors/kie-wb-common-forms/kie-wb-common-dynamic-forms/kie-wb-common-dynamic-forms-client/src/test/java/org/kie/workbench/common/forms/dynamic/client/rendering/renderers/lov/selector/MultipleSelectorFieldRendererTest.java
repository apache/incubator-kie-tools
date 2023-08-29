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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector;

import java.util.Arrays;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.AbstractFieldRendererTest;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.input.MultipleSelectorInput;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.AbstractMultipleSelectorFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.impl.StringMultipleSelectorFieldDefinition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MultipleSelectorFieldRendererTest extends AbstractFieldRendererTest<MultipleSelectorFieldRenderer, AbstractMultipleSelectorFieldDefinition, DefaultFormGroup> {

    private static String NAME = "textBox";

    @Mock
    private MultipleSelectorInput<?> selector;

    @Mock
    private TranslationService translationService;

    @Mock
    private DefaultFormGroup formGroup;

    @InjectMocks
    @Spy
    private MultipleSelectorFieldRenderer multipleSelectorFieldRenderer;

    @Before
    public void init() {
        super.init();

        when(formGroupsInstance.get()).thenReturn(formGroup);
    }

    @Test
    public void testGetFormGroup() {
        renderer.getFormGroup(RenderMode.EDIT_MODE);

        verify(formGroupsInstance).get();
        selector.init(any(), any());

        verify(selector).setMaxItems(fieldDefinition.getMaxDropdownElements());
        verify(selector).setEnabled(true);
        verify(selector).setFilterEnabled(fieldDefinition.getAllowFilter());
        verify(selector).setClearSelectionEnabled(fieldDefinition.getAllowClearSelection());

        verify(formGroup).render(any(), eq(fieldDefinition));
    }

    @Override
    protected MultipleSelectorFieldRenderer getRendererInstance() {
        return multipleSelectorFieldRenderer;
    }

    @Override
    protected AbstractMultipleSelectorFieldDefinition getFieldDefinition() {
        StringMultipleSelectorFieldDefinition stringMultipleInputFieldDefinition = new StringMultipleSelectorFieldDefinition();

        stringMultipleInputFieldDefinition.setName(NAME);
        stringMultipleInputFieldDefinition.setBinding(NAME);

        stringMultipleInputFieldDefinition.setListOfValues(Arrays.asList("a", "b", "c"));

        return stringMultipleInputFieldDefinition;
    }
}
