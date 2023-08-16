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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.provider;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.CharacterRadioGroupFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.DecimalRadioGroupFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.IntegerRadioGroupFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.RadioGroupBaseDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.StringRadioGroupFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.type.RadioGroupFieldType;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.TypeKind;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class RadioGroupFieldProviderTest {

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

    private RadioGroupFieldProvider provider;

    private TypeInfo typeInfo;

    @Before
    public void init() {
        provider = spy(new RadioGroupFieldProvider());

        provider.doRegisterFields();

        String[] classNames = Arrays.stream(SUPPORTED_TYPES).map(Class::getName).toArray(String[]::new);

        when(provider.getSupportedTypes()).thenReturn(classNames);

        typeInfo = mock(TypeInfo.class);
        when(typeInfo.getType()).thenReturn(TypeKind.BASE);
    }

    @Test
    public void testCreateFieldByType() {
        testCreateFieldByType(byte.class, IntegerRadioGroupFieldDefinition.class);
        testCreateFieldByType(Byte.class, IntegerRadioGroupFieldDefinition.class);
        testCreateFieldByType(short.class, IntegerRadioGroupFieldDefinition.class);
        testCreateFieldByType(Short.class, IntegerRadioGroupFieldDefinition.class);
        testCreateFieldByType(int.class, IntegerRadioGroupFieldDefinition.class);
        testCreateFieldByType(Integer.class, IntegerRadioGroupFieldDefinition.class);
        testCreateFieldByType(long.class, IntegerRadioGroupFieldDefinition.class);
        testCreateFieldByType(Long.class, IntegerRadioGroupFieldDefinition.class);
        testCreateFieldByType(BigInteger.class, IntegerRadioGroupFieldDefinition.class);

        testCreateFieldByType(float.class, DecimalRadioGroupFieldDefinition.class);
        testCreateFieldByType(Float.class, DecimalRadioGroupFieldDefinition.class);
        testCreateFieldByType(double.class, DecimalRadioGroupFieldDefinition.class);
        testCreateFieldByType(Double.class, DecimalRadioGroupFieldDefinition.class);
        testCreateFieldByType(BigDecimal.class, DecimalRadioGroupFieldDefinition.class);

        testCreateFieldByType(char.class, CharacterRadioGroupFieldDefinition.class);
        testCreateFieldByType(Character.class, CharacterRadioGroupFieldDefinition.class);

        testCreateFieldByType(String.class, StringRadioGroupFieldDefinition.class);

        // unsupported type
        when(typeInfo.getClassName()).thenReturn(Boolean.class.getName());
        assertNull(provider.createFieldByType(typeInfo));
    }

    public void testCreateFieldByType(Class fieldClass, Class expectedFieldDefinitionClass) {
        when(typeInfo.getClassName()).thenReturn(fieldClass.getName());
        RadioGroupBaseDefinition fieldDefinition = provider.createFieldByType(typeInfo);
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
        assertEquals(8, provider.getPriority());
    }

    @Test
    public void testGetFieldTypeName() {
        assertEquals(RadioGroupFieldType.NAME, provider.getFieldTypeName());
    }

    @Test
    public void testGetFieldType() {
        assertEquals(RadioGroupFieldType.class, provider.getFieldType());
    }

    @Test
    public void testGetDefaultField() {
        RadioGroupBaseDefinition fieldDef = provider.getDefaultField();
        assertEquals(StringRadioGroupFieldDefinition.class, fieldDef.getClass());
    }
}
