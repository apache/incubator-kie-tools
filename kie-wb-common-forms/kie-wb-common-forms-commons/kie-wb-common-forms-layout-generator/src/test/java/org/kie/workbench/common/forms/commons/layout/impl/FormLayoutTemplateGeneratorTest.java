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

package org.kie.workbench.common.forms.commons.layout.impl;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.commons.layout.FormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormLayoutComponent;
import org.kie.workbench.common.forms.model.impl.basic.checkBox.CheckBoxFieldDefinition;
import org.kie.workbench.common.forms.model.impl.basic.datePicker.DatePickerFieldDefinition;
import org.kie.workbench.common.forms.model.impl.basic.textBox.TextBoxFieldDefinition;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static org.junit.Assert.*;

public abstract class FormLayoutTemplateGeneratorTest {
    private FormDefinition form;
    private FormLayoutTemplateGenerator templateGenerator;

    @Before
    public void init() {
        templateGenerator = getTemplateGenerator();

        form = new FormDefinition();
        form.setName( "Test" );
        form.setId( "Test-ID" );

        TextBoxFieldDefinition name = new TextBoxFieldDefinition();
        name.setId( "name" );
        name.setName( "employee_name" );
        name.setLabel( "Name" );
        name.setPlaceHolder( "Name" );
        name.setModelName( "employee" );
        name.setBoundPropertyName( "name" );
        name.setStandaloneClassName( String.class.getName() );

        TextBoxFieldDefinition lastName = new TextBoxFieldDefinition();
        lastName.setId( "lastName" );
        lastName.setName( "employee_lastName" );
        lastName.setLabel( "Last Name" );
        lastName.setPlaceHolder( "Last Name" );
        lastName.setModelName( "employee" );
        lastName.setBoundPropertyName( "lastName" );
        lastName.setStandaloneClassName( String.class.getName() );

        DatePickerFieldDefinition birthday = new DatePickerFieldDefinition();
        birthday.setId( "birthday" );
        birthday.setName( "employee_birthday" );
        birthday.setLabel( "Birthday" );
        birthday.setModelName( "employee" );
        birthday.setBoundPropertyName( "birthday" );
        birthday.setStandaloneClassName( Date.class.getName() );

        CheckBoxFieldDefinition married = new CheckBoxFieldDefinition();
        married.setId("married");
        married.setName( "employee_married" );
        married.setLabel( "Married" );
        married.setModelName( "employee" );
        married.setBoundPropertyName( "married" );
        married.setStandaloneClassName( Boolean.class.getName() );

        form.getFields().add( name );
        form.getFields().add( lastName );
        form.getFields().add( birthday );
        form.getFields().add( married );
    }

    @Test
    public void testTemplateGeneration() {
        LayoutTemplate layout = templateGenerator.generateLayoutTemplate( form );

        assertNotNull( layout );

        assertNotNull( layout.getRows() );

        assertEquals( 4, layout.getRows().size() );

        for( LayoutRow row : layout.getRows() ) {
            assertEquals( 1, row.getLayoutColumns().size() );

            for ( LayoutColumn col : row.getLayoutColumns() ) {
                assertEquals( "12", col.getSpan() );

                assertEquals( 0, col.getRows().size() );

                assertEquals( 1, col.getLayoutComponents().size() );

                for ( LayoutComponent component : col.getLayoutComponents() ) {
                    assertEquals( templateGenerator.getDraggableType(), component.getDragTypeName() );

                    assertEquals( form.getId(), component.getProperties().get( FormLayoutComponent.FORM_ID ) );

                    String fieldId = component.getProperties().get( FormLayoutComponent.FIELD_ID );

                    assertNotNull( fieldId );

                    assertNotNull( form.getFieldById( fieldId ) );

                }

            }

        }
    }

    protected abstract FormLayoutTemplateGenerator getTemplateGenerator();
}
