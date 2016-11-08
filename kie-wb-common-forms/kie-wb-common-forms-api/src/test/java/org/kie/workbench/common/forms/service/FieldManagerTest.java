/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.model.DefaultFieldTypeInfo;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldTypeInfo;
import org.kie.workbench.common.forms.service.impl.fieldProviders.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.service.mock.TestFieldManager;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FieldManagerTest extends TestCase {

    protected TestFieldManager fieldManager;

    protected final Class[] basicTypesSupported = new Class[] {
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
            Enum.class,
    };

    @Before
    public void initTest() {
        fieldManager = new TestFieldManager();
        assertNotNull( fieldManager.getBaseFieldTypes() );
        assertNotSame( 0, fieldManager.getBaseFieldTypes().size() );
    }

    @Test
    public void testGetDefaultFieldTypes() {
        for ( String typeCode : fieldManager.getBaseFieldTypes() ) {
            FieldDefinition fieldDefinition = fieldManager.getDefinitionByTypeCode( typeCode );
            assertNotNull( fieldDefinition );
            assertEquals( typeCode, fieldDefinition.getCode() );
        }
    }

    @Test
    public void testGetFieldByTypeInfo() {
        for ( Class clazz : basicTypesSupported ) {
            FieldTypeInfo typeInfo = new DefaultFieldTypeInfo( clazz.getName(), false, clazz.isEnum() );
            checkFieldExists( typeInfo );
        }

        // check nested form
        checkFieldExists( new DefaultFieldTypeInfo( Object.class.getName(), false, false ) );

        // check multiple subform
        checkFieldExists( new DefaultFieldTypeInfo( Object.class.getName(), true, false ) );

    }

    protected void checkFieldExists( FieldTypeInfo typeInfo ) {
        FieldDefinition fieldDefinition = fieldManager.getDefinitionByValueType( typeInfo );
        assertNotNull( fieldDefinition );
    }

    @Test
    public void testGetCompatibleFields() {
        testCompatiblefields( true );
        testCompatiblefields( false );
    }

    protected void testCompatiblefields( boolean addFieldType ) {
        for ( Class clazz : basicTypesSupported ) {
            FieldTypeInfo typeInfo = new DefaultFieldTypeInfo( clazz.getName(), false, clazz.isEnum() );

            FieldDefinition fieldDefinition = fieldManager.getDefinitionByValueType( typeInfo );

            assertNotNull( fieldDefinition );

            if ( addFieldType ) {
                fieldDefinition.setStandaloneClassName( typeInfo.getType() );
            }

            Collection<String> compatibles = fieldManager.getCompatibleFields( fieldDefinition );

            assertNotNull( compatibles );
            assertNotSame( 0, compatibles.size() );
        }
    }

    @Test
    public void testGettingAllProvidersDefinitions() {
        for ( BasicTypeFieldProvider provider : fieldManager.getAllBasicTypeProviders() ) {
            for ( String className : provider.getSupportedTypes() ) {
                try {
                    Class clazz = Class.forName( className );
                    FieldTypeInfo typeInfo = new DefaultFieldTypeInfo( clazz.getName(), false, clazz.isEnum() );

                    FieldDefinition fieldDefinition = fieldManager.getFieldFromProvider( provider.getProviderCode(), typeInfo );
                    assertNotNull( fieldDefinition );
                } catch ( ClassNotFoundException e ) {
                    // swallow error caused by looking up simple types
                }
            }
        }
    }
}
