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

import java.math.BigInteger;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.common.rendering.client.util.valueConverters.BigIntegerToLongConverter;
import org.kie.workbench.common.forms.common.rendering.client.util.valueConverters.ByteToLongConverter;
import org.kie.workbench.common.forms.common.rendering.client.util.valueConverters.IntegerToLongConverter;
import org.kie.workbench.common.forms.common.rendering.client.util.valueConverters.ShortToLongConverter;
import org.kie.workbench.common.forms.common.rendering.client.widgets.integerBox.IntegerBox;
import org.kie.workbench.common.forms.dynamic.client.rendering.AbstractFieldRendererTest;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class IntegerBoxFieldRendererTest extends AbstractFieldRendererTest<IntegerBoxFieldRenderer, IntegerBoxFieldDefinition, DefaultFormGroup> {

    private static String NAME = "integerBox";

    @Mock
    private IntegerBox integerBox;

    @Mock
    private DefaultFormGroup formGroup;

    @InjectMocks
    @Spy
    private IntegerBoxFieldRenderer integerBoxFieldRenderer;

    @Before
    public void init() {
        super.init();

        when(formGroupsInstance.get()).thenReturn(formGroup);
    }

    @Test
    public void testGetFormGroup() {
        renderer.getFormGroup(RenderMode.EDIT_MODE);

        verify(formGroupsInstance).get();
        verify(integerBox).setId(any());
        verify(integerBox).setPlaceholder(eq(fieldDefinition.getPlaceHolder()));
        verify(integerBox).setMaxLength(eq(fieldDefinition.getMaxLength()));
        verify(integerBox).setEnabled(eq(!fieldDefinition.getReadOnly()));

        verify(integerBox).asWidget();

        verify(formGroup).render(Mockito.<String>any(), any(), eq(fieldDefinition));
    }

    @Test
    public void testGetConverter() {
        checkConverter(BigInteger.class.getName(), BigIntegerToLongConverter.class);
        checkConverter(Byte.class.getName(), ByteToLongConverter.class);
        checkConverter(byte.class.getName(), ByteToLongConverter.class);
        checkConverter(Integer.class.getName(), IntegerToLongConverter.class);
        checkConverter(int.class.getName(), IntegerToLongConverter.class);
        checkConverter(Short.class.getName(), ShortToLongConverter.class);
        checkConverter(short.class.getName(), ShortToLongConverter.class);
    }

    private void checkConverter(String className, Class expectedType) {
        when(fieldDefinition.getStandaloneClassName()).thenReturn(className);
        Assertions.assertThat(renderer.getConverter())
                .isNotNull()
                .isInstanceOf(expectedType);
    }

    @Override
    protected IntegerBoxFieldRenderer getRendererInstance() {
        return integerBoxFieldRenderer;
    }

    @Override
    protected IntegerBoxFieldDefinition getFieldDefinition() {
        IntegerBoxFieldDefinition integerBoxFieldDefinition = new IntegerBoxFieldDefinition();

        integerBoxFieldDefinition.setName(NAME);
        integerBoxFieldDefinition.setBinding(NAME);
        integerBoxFieldDefinition.setPlaceHolder(NAME);

        return integerBoxFieldDefinition;
    }
}
