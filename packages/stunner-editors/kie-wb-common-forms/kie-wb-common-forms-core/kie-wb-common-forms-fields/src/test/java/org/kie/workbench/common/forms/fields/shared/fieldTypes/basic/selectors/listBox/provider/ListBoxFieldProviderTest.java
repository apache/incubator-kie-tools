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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.provider;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.CharacterListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.DecimalListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.EnumListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.IntegerListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.ListBoxBaseDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.StringListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.TypeKind;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ListBoxFieldProviderTest {

    public static final Class[] SUPPORTED_TYPES = new Class[]{
            byte.class,
            Byte.class,
            short.class,
            Short.class,
            int.class,
            Integer.class,
            long.class,
            Long.class,
            BigInteger.class,
            float.class,
            Float.class,
            double.class,
            Double.class,
            BigDecimal.class,
            char.class,
            Character.class,
            String.class
    };

    private ListBoxFieldProvider provider;

    private TypeInfo typeInfo;

    @Before
    public void init() {
        provider = spy(new ListBoxFieldProvider());
        String[] classNames = Arrays.stream(SUPPORTED_TYPES).map(Class::getName).toArray(String[]::new);

        when(provider.getSupportedTypes()).thenReturn(classNames);

        typeInfo = mock(TypeInfo.class);
        when(typeInfo.getType()).thenReturn(TypeKind.BASE);
    }

    @Test
    public void testCreateFieldByType() {
        testCreateFieldByType(byte.class, IntegerListBoxFieldDefinition.class);
        testCreateFieldByType(Byte.class, IntegerListBoxFieldDefinition.class);
        testCreateFieldByType(short.class, IntegerListBoxFieldDefinition.class);
        testCreateFieldByType(Short.class, IntegerListBoxFieldDefinition.class);
        testCreateFieldByType(int.class, IntegerListBoxFieldDefinition.class);
        testCreateFieldByType(Integer.class, IntegerListBoxFieldDefinition.class);
        testCreateFieldByType(long.class, IntegerListBoxFieldDefinition.class);
        testCreateFieldByType(Long.class, IntegerListBoxFieldDefinition.class);
        testCreateFieldByType(BigInteger.class, IntegerListBoxFieldDefinition.class);

        testCreateFieldByType(float.class, DecimalListBoxFieldDefinition.class);
        testCreateFieldByType(Float.class, DecimalListBoxFieldDefinition.class);
        testCreateFieldByType(double.class, DecimalListBoxFieldDefinition.class);
        testCreateFieldByType(Double.class, DecimalListBoxFieldDefinition.class);
        testCreateFieldByType(BigDecimal.class, DecimalListBoxFieldDefinition.class);

        testCreateFieldByType(char.class, CharacterListBoxFieldDefinition.class);
        testCreateFieldByType(Character.class, CharacterListBoxFieldDefinition.class);

        testCreateFieldByType(String.class, StringListBoxFieldDefinition.class);

        when(typeInfo.getType()).thenReturn(TypeKind.ENUM);
        testCreateFieldByType(Enum.class, EnumListBoxFieldDefinition.class);
    }

    public void testCreateFieldByType(Class fieldClass, Class expectedFieldDefinitionClass) {
        when(typeInfo.getClassName()).thenReturn(fieldClass.getName());
        ListBoxBaseDefinition fieldDefinition = provider.createFieldByType(typeInfo);
        assertEquals("should return an instance of '" + expectedFieldDefinitionClass.getName() + "' for a field of type '" + fieldClass.getName() + "'",
                     expectedFieldDefinitionClass,
                     fieldDefinition.getClass());
    }

    @Test
    public void testDoRegisterFields() {
        provider.doRegisterFields();
        Arrays.stream(SUPPORTED_TYPES).forEach(type -> {
            assertTrue(provider.supports(type));
        });
    }

    @Test
    public void testGetPriority() {
        assertEquals(7, provider.getPriority());
    }

    @Test
    public void testGetFieldTypeName() {
        assertEquals(ListBoxFieldType.NAME, provider.getFieldTypeName());
    }

    @Test
    public void testGetFieldType() {
        assertEquals(ListBoxFieldType.class, provider.getFieldType());
    }

    @Test
    public void testGetDefaultField() {
        ListBoxBaseDefinition fieldDef = provider.getDefaultField();
        assertEquals(StringListBoxFieldDefinition.class, fieldDef.getClass());
    }

    @Test
    public void testisSupported() {
        when(typeInfo.getType()).thenReturn(TypeKind.ENUM);
        assertTrue(provider.isSupported(typeInfo));
    }
}
