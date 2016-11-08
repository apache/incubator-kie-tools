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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.test.model.Address;
import org.kie.workbench.common.forms.dynamic.test.model.Age;
import org.kie.workbench.common.forms.dynamic.test.model.Department;
import org.kie.workbench.common.forms.dynamic.test.model.Employee;
import org.kie.workbench.common.forms.dynamic.test.model.Title;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.impl.relations.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.model.impl.relations.SubFormFieldDefinition;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class NestedFormsFormRenderingContextGeneratorTest extends AbstractFormRenderingContextGeneratorTest<Department> {

    @Before
    @Override
    public void init() {
        super.init();
    }

    @Test
    public void testCreateContextForModelWithNestedFormsAndMetaProperties() {
        initTest( getTestModel(), 5 );

        assertEquals( "There should be 3 forms", 3, context.getAvailableForms().size() );

        assertNotNull( "There should be a form for Department", context.getAvailableForms().get( Department.class.getName() ) );

        FormDefinition form = context.getAvailableForms().get( Department.class.getName() );

        assertEquals( "Department form should have 5 fields", 5, form.getFields().size() );

        FieldDefinition field = form.getFieldById( "address" );

        assertNotNull( "Department form should have an address field", field );

        assertTrue( "Address field must be a SubForm", field instanceof SubFormFieldDefinition );

        SubFormFieldDefinition address = (SubFormFieldDefinition) field;

        assertEquals( "Address field className should be " + Address.class.getName(), Address.class.getName(), field.getStandaloneClassName() );

        assertEquals( "Address nested form id should be " + Address.class.getName(), Address.class.getName(), address.getNestedForm() );

        field = form.getFieldById( "employees" );

        assertNotNull( "Department form should have an employees field", field );

        assertEquals( "Employees field className should be " + Employee.class.getName(), Employee.class.getName(), field.getStandaloneClassName() );

        assertTrue( "Employees field must be a MultipleSubForm", field instanceof MultipleSubFormFieldDefinition );

        MultipleSubFormFieldDefinition employees = (MultipleSubFormFieldDefinition) field;

        assertEquals( "Employees creation form id should be " + Employee.class.getName(), Employee.class.getName(), employees.getCreationForm() );

        assertEquals( "Employees edition form id should be " + Employee.class.getName(), Employee.class.getName(), employees.getEditionForm() );

        assertEquals( "Employees field should have 7 columns", 7, employees.getColumnMetas().size() );

        field = form.getFieldById( "metaAddress" );

        assertTrue( "metaAddress field must be a SubForm", field instanceof SubFormFieldDefinition );

        SubFormFieldDefinition metaAddress = (SubFormFieldDefinition) field;

        assertEquals( "metaAddress field className should be " + Address.class.getName(), Address.class.getName(), field.getStandaloneClassName() );

        assertEquals( "metaAddress nested form id should be " + Address.class.getName(), Address.class.getName(), metaAddress.getNestedForm() );

        field = form.getFieldById( "metaAddresses" );

        assertTrue( "metaAddresses field must be a MultipleSubForm", field instanceof MultipleSubFormFieldDefinition );

        MultipleSubFormFieldDefinition metaAddresses = (MultipleSubFormFieldDefinition) field;

        assertEquals( "metaAddresses creation form id should be " + Address.class.getName(), Address.class.getName(), metaAddresses.getCreationForm() );

        assertEquals( "metaAddresses edition form id should be " + Address.class.getName(), Address.class.getName(), metaAddresses.getEditionForm() );

        assertEquals( "metaAddresses field className should be " + Address.class.getName(), Address.class.getName(), metaAddresses.getStandaloneClassName() );

        // Check Existing forms

        assertNotNull( "There should be a form for Address", context.getAvailableForms().get( Address.class.getName() ) );

        form = context.getAvailableForms().get( Address.class.getName() );

        assertEquals( "Address form should have 2 fields", 2, form.getFields().size() );

        assertNotNull( "There should be a form for Employee", context.getAvailableForms().get( Employee.class.getName() ) );

        form = context.getAvailableForms().get( Employee.class.getName() );

        assertEquals( "Employee form should have 8 fields", 8, form.getFields().size() );
    }

    protected Department getTestModel() {

        Address address = new Address();

        address.setStreet( "Winterfell Street" );
        address.setNum( 1 );

        List<Employee> employees = new ArrayList<>();

        Employee employee = new Employee();
        employee.setTitle( Title.MR );
        employee.setName( "John" );
        employee.setSurname( "Snow" );
        employee.setBirthday( new Date() );

        Address employeeAddress = new Address();

        address.setStreet( "The Wall" );
        address.setNum( 1 );

        employee.setAge( new Age( 25 ) );
        employee.setMarried( Boolean.FALSE );
        employee.setAddress( employeeAddress );
        employee.setRoleDescription( "King in the North" );

        employees.add( employee );

        return new Department( "R & D", address, employees );
    }
}
