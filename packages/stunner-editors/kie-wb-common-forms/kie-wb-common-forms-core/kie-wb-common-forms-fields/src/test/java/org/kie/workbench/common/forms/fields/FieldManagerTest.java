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


package org.kie.workbench.common.forms.fields;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Date;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.kie.workbench.common.forms.model.impl.meta.entries.FieldLabelEntry;
import org.kie.workbench.common.forms.model.impl.meta.entries.FieldPlaceHolderEntry;
import org.kie.workbench.common.forms.model.impl.meta.entries.FieldReadOnlyEntry;
import org.kie.workbench.common.forms.model.impl.meta.entries.FieldRequiredEntry;
import org.kie.workbench.common.forms.model.impl.meta.entries.FieldTypeEntry;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FieldManagerTest {

    private static final String METADATA_LABEL = "Name label";
    private static final String METADATA_PLACEHOLDER = "Name placeholder";
    private static final Boolean METADATA_READONLY = Boolean.TRUE;
    private static final Boolean METADATA_REQUIRED = Boolean.TRUE;

    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_LABEL = "Name";

    protected TestFieldManager fieldManager;

    protected ModelProperty property;

    protected final Class[] basicTypesSupported = new Class[]{
            String.class,
            Character.class,
            char.class,
            Date.class,
            Boolean.class,
            boolean.class,
            Integer.class,
            int.class,
            Double.class,
            double.class,
            Float.class,
            float.class,
            Long.class,
            long.class,
            Byte.class,
            byte.class,
            BigInteger.class,
            BigDecimal.class,
            Short.class,
            short.class,
            MyTestEnum.class,
    };

    protected final Class[] basicMultipleTypesSupported = new Class[]{
            BigInteger.class,
            Byte.class,
            Integer.class,
            Long.class,
            Short.class,

            // Decimal types
            BigDecimal.class,
            Double.class,
            Float.class,

            // Date types
            Date.class,
            LocalDate.class,
            LocalDateTime.class,
            LocalTime.class,
            OffsetDateTime.class,

            Character.class,
            String.class,
            Boolean.class,
    };

    @Before
    public void initTest() {
        fieldManager = new TestFieldManager();
        Assertions.assertThat(fieldManager.getBaseFieldTypes())
                .isNotNull()
                .isNotEmpty();

        property = new ModelPropertyImpl("name",
                                         new TypeInfoImpl(String.class.getName()));
    }

    @Test
    public void testGetDefaultFieldTypes() {
        for (String typeCode : fieldManager.getBaseFieldTypes()) {
            FieldDefinition fieldDefinition = fieldManager.getDefinitionByFieldTypeName(typeCode);
            Assertions.assertThat(fieldDefinition).isNotNull();
            Assertions.assertThat(fieldDefinition.getFieldType().getTypeName()).isEqualTo(typeCode);
        }
    }

    @Test
    public void testGetFieldByTypeInfo() {
        for (Class clazz : basicTypesSupported) {

            TypeInfo typeInfo = new TypeInfoImpl(clazz.isEnum() ? TypeKind.ENUM : TypeKind.BASE,
                                                 clazz.getName(),
                                                 false);

            checkFieldExists(typeInfo);
        }

        for (Class clazz : basicMultipleTypesSupported) {

            TypeInfo typeInfo = new TypeInfoImpl(TypeKind.BASE,
                                                 clazz.getName(),
                                                 true);

            checkFieldExists(typeInfo);
        }

        // check nested form
        checkFieldExists(new TypeInfoImpl(TypeKind.OBJECT,
                                          Object.class.getName(),
                                          false));

        // check multiple subform
        checkFieldExists(new TypeInfoImpl(TypeKind.OBJECT,
                                          Object.class.getName(),
                                          true));
    }

    protected void checkFieldExists(TypeInfo typeInfo) {
        FieldDefinition fieldDefinition = fieldManager.getDefinitionByDataType(typeInfo);
        Assertions.assertThat(fieldDefinition).isNotNull();
    }

    @Test
    public void testGetCompatibleFields() {
        testCompatiblefields(true);
        testCompatiblefields(false);
    }

    protected void testCompatiblefields(boolean addFieldType) {
        for (Class clazz : basicTypesSupported) {

            TypeInfo typeInfo = new TypeInfoImpl(clazz.isEnum() ? TypeKind.ENUM : TypeKind.BASE,
                                                 clazz.getName(),
                                                 false);

            FieldDefinition fieldDefinition = fieldManager.getDefinitionByDataType(typeInfo);

            Assertions.assertThat(fieldDefinition).isNotNull();

            if (addFieldType) {
                fieldDefinition.setStandaloneClassName(typeInfo.getClassName());
            }

            Collection<String> compatibles = fieldManager.getCompatibleFields(fieldDefinition);

            Assertions.assertThat(compatibles).isNotNull()
                    .isNotEmpty();
        }
    }

    @Test
    public void testGettingAllProvidersDefinitions() {
        for (BasicTypeFieldProvider provider : fieldManager.getAllBasicTypeProviders()) {
            for (String className : provider.getSupportedTypes()) {
                try {
                    Class clazz = Class.forName(className);
                    TypeInfo typeInfo = new TypeInfoImpl(clazz.isEnum() ? TypeKind.ENUM : TypeKind.BASE,
                                                         clazz.getName(),
                                                         false);

                    FieldDefinition fieldDefinition = fieldManager.getFieldFromProvider(provider.getFieldTypeName(),
                                                                                        typeInfo);
                    Assertions.assertThat(fieldDefinition).isNotNull();
                } catch (ClassNotFoundException e) {
                    // swallow error caused by looking up simple types
                }
            }
        }
    }

    @Test
    public void testGettingAllMultipleProvidersDefinitions() {
        for (BasicTypeFieldProvider provider : fieldManager.getAllBasicMultipleTypeProviders()) {
            for (String className : provider.getSupportedTypes()) {
                TypeInfo typeInfo = new TypeInfoImpl(Object.class.getName().equals(className) ? TypeKind.OBJECT : TypeKind.BASE, className, true);

                FieldDefinition fieldDefinition = fieldManager.getFieldFromProvider(provider.getFieldTypeName(), typeInfo);
                Assertions.assertThat(fieldDefinition).isNotNull();
            }
        }
    }

    @Test
    public void testGetDefinitionByModelPropertyWithoutMetaData() {
        FieldDefinition fieldDefinition = fieldManager.getDefinitionByModelProperty(property);

        Assertions.assertThat(fieldDefinition)
                .isNotNull()
                .isInstanceOf(TextBoxFieldDefinition.class)
                .hasFieldOrPropertyWithValue("name",
                                             PROPERTY_NAME)
                .hasFieldOrPropertyWithValue("label",
                                             PROPERTY_LABEL)
                .hasFieldOrPropertyWithValue("placeHolder",
                                             PROPERTY_LABEL)
                .hasFieldOrPropertyWithValue("required",
                                             Boolean.FALSE)
                .hasFieldOrPropertyWithValue("readOnly",
                                             Boolean.FALSE)
                .hasFieldOrPropertyWithValue("binding",
                                             PROPERTY_NAME);
    }

    @Test
    public void testGetDefinitionByModelPropertyWithMetaData() {
        property.getMetaData().addEntry(new FieldTypeEntry(TextAreaFieldType.NAME));
        property.getMetaData().addEntry(new FieldLabelEntry(METADATA_LABEL));
        property.getMetaData().addEntry(new FieldPlaceHolderEntry(METADATA_PLACEHOLDER));
        property.getMetaData().addEntry(new FieldReadOnlyEntry(METADATA_READONLY));
        property.getMetaData().addEntry(new FieldRequiredEntry(METADATA_REQUIRED));

        FieldDefinition fieldDefinition = fieldManager.getDefinitionByModelProperty(property);

        Assertions.assertThat(fieldDefinition)
                .isNotNull()
                .isInstanceOf(TextAreaFieldDefinition.class)
                .hasFieldOrPropertyWithValue("name",
                                             PROPERTY_NAME)
                .hasFieldOrPropertyWithValue("label",
                                             METADATA_LABEL)
                .hasFieldOrPropertyWithValue("placeHolder",
                                             METADATA_PLACEHOLDER)
                .hasFieldOrPropertyWithValue("required",
                                             METADATA_REQUIRED)
                .hasFieldOrPropertyWithValue("readOnly",
                                             METADATA_READONLY)
                .hasFieldOrPropertyWithValue("binding",
                                             PROPERTY_NAME);
    }
}
