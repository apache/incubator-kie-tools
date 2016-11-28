/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.workbench.common.forms.jbpm.server.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.commons.layout.impl.DynamicFormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMVariable;
import org.kie.workbench.common.forms.jbpm.server.service.impl.model.LogEntry;
import org.kie.workbench.common.forms.jbpm.server.service.impl.model.Person;
import org.kie.workbench.common.forms.jbpm.server.service.impl.model.PersonType;
import org.kie.workbench.common.forms.jbpm.server.service.impl.model.PersonalData;
import org.kie.workbench.common.forms.jbpm.service.bpmn.util.BPMNVariableUtils;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.PortableJavaModel;
import org.kie.workbench.common.forms.model.impl.basic.selectors.listBox.EnumListBoxFieldDefinition;
import org.kie.workbench.common.forms.model.impl.relations.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.model.impl.relations.SubFormFieldDefinition;
import org.kie.workbench.common.forms.service.mock.TestFieldManager;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public abstract class BPMNFormGenerationTest<MODEL extends JBPMFormModel> {

    @Mock
    private ClassLoader classLoader;

    protected DynamicBPMNFormGeneratorImpl generator;

    private MODEL model;

    @Before
    public void initTest() {
        generator = new DynamicBPMNFormGeneratorImpl( new TestFieldManager(), new DynamicFormLayoutTemplateGenerator() );
    }

    protected abstract String getModelId();

    protected abstract MODEL getModel( String modelId, List<JBPMVariable> variables );

    protected abstract Collection<FormDefinition> getModelForms( MODEL model, ClassLoader classLoader );

    @Test
    public void testSimpleVariables() {
        List<JBPMVariable> variables = new ArrayList<>();

        variables.add( new JBPMVariable( "employee", String.class.getName() ) );
        variables.add( new JBPMVariable( "manager", String.class.getName() ) );
        variables.add( new JBPMVariable( "performance", Integer.class.getName() ) );
        variables.add( new JBPMVariable( "approved", Boolean.class.getName() ) );

        model = getModel( getModelId(), variables );

        Collection<FormDefinition> forms = getModelForms( model, classLoader );

        try {
            verify( classLoader, never() ).loadClass( anyString() );
        } catch ( ClassNotFoundException e ) {
            fail( "We shouldn't be here: " + e.getMessage() );
        }

        assertNotNull( "There should one form", forms );

        assertEquals( "There should one form", 1, forms.size() );

        FormDefinition form = forms.iterator().next();

        assertEquals( getModelId(), form.getId() );
        assertEquals( getModelId() + BPMNVariableUtils.TASK_FORM_SUFFIX, form.getName() );

        assertEquals( form.getModel(), model );

        assertEquals( "There should be 4 fields", 4, form.getFields().size() );

        variables.forEach( variable -> {
            FieldDefinition field = form.getFieldByBinding( variable.getName() );
            assertFieldStatus( field, variable );
        } );
    }

    @Test
    public void testComplexFieldsFromClassLoader() {
        testComplexFields( true );
    }

    @Test
    public void testComplexFieldsFromGeneralClassLoader() {
        testComplexFields( false );
    }

    protected void testComplexFields( boolean fromClassLoader ) {
        if ( fromClassLoader ) {
            try {
                when( classLoader.loadClass( Person.class.getName() ) ).then( new Answer<Class<?>>() {
                    @Override
                    public Class<?> answer( InvocationOnMock invocationOnMock ) throws Throwable {
                        return Person.class;
                    }
                } );
            } catch ( ClassNotFoundException e ) {
                fail( "We shouldn't be here: " + e.getMessage() );
            }
        }
        List<JBPMVariable> variables = new ArrayList<>();
        variables.add( new JBPMVariable( "person", Person.class.getName() ) );

        model = getModel( getModelId(), variables );

        Collection<FormDefinition> forms = getModelForms( model, classLoader );

        Map<String, FormDefinition> allForms = new HashMap<>();

        forms.forEach( form -> allForms.put( form.getId(), form ) );

        try {
            verify( classLoader, times(1) ).loadClass( anyString() );
        } catch ( ClassNotFoundException e ) {
            fail( e.getMessage() );
        }

        assertNotNull( "There should some forms", forms );

        assertEquals( "There should 4 forms", 4, forms.size() );

        FormDefinition form  = allForms.get( getModelId() );
        checkBPMForm( form, allForms );
        form = allForms.get( Person.class.getName() );
        checkPersonForm( form, allForms );
        form = allForms.get( PersonalData.class.getName() );
        checkPersonalDataForm( form, allForms );
        form = allForms.get( LogEntry.class.getName() );
        checkLogEntryForm( form );
    }

    private void checkLogEntryForm( FormDefinition form ) {
        assertNotNull( form );
        assertEquals( LogEntry.class.getName(), form.getId() );
        assertEquals( LogEntry.class.getName(), form.getName() );

        assertEquals( 2, form.getFields().size() );

        FieldDefinition field = form.getFieldByBinding( "date" );
        assertFieldStatus( field, "date", Date.class.getName() );

        field = form.getFieldByBinding( "text" );
        assertFieldStatus( field, "text", String.class.getName() );
    }

    private void checkPersonalDataForm( FormDefinition form, Map<String, FormDefinition> allForms ) {
        assertNotNull( form );
        assertEquals( PersonalData.class.getName(), form.getId() );
        assertEquals( PersonalData.class.getName(), form.getName() );

        assertEquals( 2, form.getFields().size() );

        FieldDefinition field = form.getFieldByBinding( "address" );
        assertFieldStatus( field, "address", String.class.getName() );

        field = form.getFieldByBinding( "phone" );
        assertFieldStatus( field, "phone", String.class.getName() );
    }

    private void checkBPMForm( FormDefinition form, Map<String, FormDefinition> allForms ) {
        assertNotNull( form );
        assertEquals( getModelId(), form.getId() );
        assertEquals( getModelId() + BPMNVariableUtils.TASK_FORM_SUFFIX, form.getName() );
        assertEquals( 1, form.getFields().size() );

        FieldDefinition field = form.getFieldByBinding( "person" );
        assertFieldStatus( field, "person", Person.class.getName() );

        assertTrue( field instanceof SubFormFieldDefinition );

        SubFormFieldDefinition subForm = (SubFormFieldDefinition) field;

        assertNotNull( subForm.getNestedForm() );

        assertEquals( subForm.getNestedForm(), Person.class.getName() );

        assertNotNull( allForms.get( subForm.getNestedForm() ) );
    }

    private void checkPersonForm( FormDefinition form, Map<String, FormDefinition> allForms ) {
        assertNotNull( form );
        assertEquals( Person.class.getName(), form.getId() );
        assertEquals( Person.class.getName(), form.getName() );

        assertTrue( form.getModel() instanceof PortableJavaModel );

        assertEquals( 4, form.getFields().size() );

        FieldDefinition field = form.getFieldByBinding( "name" );
        assertFieldStatus( field, "name", String.class.getName() );

        field = form.getFieldByBinding( "type" );

        assertFieldStatus( field, "type", PersonType.class.getName() );

        assertTrue( field instanceof EnumListBoxFieldDefinition );

        field = form.getFieldByBinding( "personalData" );

        assertFieldStatus( field, "personalData", PersonalData.class.getName() );

        assertTrue( field instanceof SubFormFieldDefinition );

        SubFormFieldDefinition subForm = (SubFormFieldDefinition) field;

        assertNotNull( subForm.getNestedForm() );

        assertEquals( PersonalData.class.getName(), subForm.getNestedForm() );

        assertNotNull( allForms.get( subForm.getNestedForm() ) );

        field = form.getFieldByBinding( "log" );

        assertFieldStatus( field, "log", LogEntry.class.getName() );

        assertTrue( field instanceof MultipleSubFormFieldDefinition );

        MultipleSubFormFieldDefinition multipleSubForm = (MultipleSubFormFieldDefinition) field;

        assertNotNull( multipleSubForm.getCreationForm() );

        assertEquals( multipleSubForm.getCreationForm(), multipleSubForm.getEditionForm() );

        assertEquals( LogEntry.class.getName(), multipleSubForm.getCreationForm() );

        FormDefinition nestedForm = allForms.get( multipleSubForm.getCreationForm() );

        assertNotNull( nestedForm );

        assertNotNull( multipleSubForm.getColumnMetas() );

        assertEquals( nestedForm.getFields().size(), multipleSubForm.getColumnMetas().size() );

        multipleSubForm.getColumnMetas().forEach( columnMeta -> {
            FieldDefinition nestedField = nestedForm.getFieldByBinding( columnMeta.getProperty() );

            assertNotNull( nestedField );
            assertEquals( nestedField.getLabel(), columnMeta.getLabel() );
        } );
    }

    private void assertFieldStatus( FieldDefinition field, JBPMVariable variable ) {
        assertFieldStatus( field, variable.getName(), variable.getType() );
    }

    private void assertFieldStatus( FieldDefinition field, String name, String className ) {
        assertNotNull( field );
        assertEquals( name, field.getName() );
        assertEquals( name.toLowerCase(), field.getName().toLowerCase() );
        assertEquals( name, field.getBinding() );
        assertEquals( className, field.getStandaloneClassName() );
    }
}
