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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.test.model.Address;
import org.kie.workbench.common.forms.dynamic.test.model.Employee;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.impl.basic.checkBox.CheckBoxFieldDefinition;
import org.kie.workbench.common.forms.model.impl.basic.textArea.TextAreaFieldDefinition;
import org.kie.workbench.common.forms.model.impl.basic.textBox.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.model.impl.relations.SubFormFieldDefinition;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class InheritanceFormRenderingContextGeneratorTest extends BasicFormRenderingContextGeneratorTest {

    @Before
    @Override
    public void init() {
        super.init();
    }

    @Test
    public void testCreateContextForModelWithInheritance() {
        initTest( new Employee(), 8 );

        FormDefinition form = context.getRootForm();

        checkMarried( form.getFieldById( "married" ) );
        checkAge( form.getFieldById( "age" ) );
        checkAddress( form.getFieldById( "address" ) );
        checkRole( form.getFieldById( "roleDescription" ) );
    }

    protected void checkMarried( FieldDefinition field ) {
        assertNotNull( field );
        assertTrue( field instanceof CheckBoxFieldDefinition );
    }

    protected void checkAge( FieldDefinition field ) {
        assertNotNull( field );
        assertTrue( field instanceof TextBoxFieldDefinition );
        assertEquals( Integer.class.getName(), field.getStandaloneClassName() );
    }

    protected void checkAddress( FieldDefinition field ) {
        assertNotNull( field );
        assertTrue( field instanceof SubFormFieldDefinition );
        assertEquals( Address.class.getName(), field.getStandaloneClassName() );
    }

    protected void checkRole( FieldDefinition field ) {
        assertNotNull( field );
        assertTrue( field instanceof TextAreaFieldDefinition );
        TextAreaFieldDefinition textArea = (TextAreaFieldDefinition) field;
        assertEquals( 4, textArea.getRows().intValue() );
        assertEquals( "Role Description", textArea.getPlaceHolder() );
    }
}
