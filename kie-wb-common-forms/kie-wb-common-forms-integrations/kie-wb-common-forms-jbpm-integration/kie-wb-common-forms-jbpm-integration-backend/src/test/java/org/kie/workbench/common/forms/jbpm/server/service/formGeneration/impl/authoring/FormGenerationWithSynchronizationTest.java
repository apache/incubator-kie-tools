package org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.authoring;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.FormGenerationResult;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FormGenerationWithSynchronizationTest extends BPMNVFSFormDefinitionGeneratorServiceTest {

    public static final Integer ORIGINAL_FORM_FIELDS = 5;

    public static final String TASK_NAME = "task";
    public static final String PROCESS_ID = "issues.Process";

    // Existing properties
    public static final String NAME_PROPERTY = "name";
    public static final String LASTNAME_PROPERTY = "lastName";
    public static final String AGE_PROPERTY = "age";
    public static final String MARRIED_PROPERTY = "married";
    public static final String ADDRESS_PROPERTY = "address";

    // New Properties
    public static final String JOB_PROPERTY = "job";
    public static final String HOBBIES_PROPERTY = "hobbies";

    public static final Integer ALL_FORM_FIELDS = ORIGINAL_FORM_FIELDS + 2;

    private TaskFormModel newFormModel;

    @Override
    public void setup() throws IOException {
        super.setup();

        when(ioService.exists(any())).thenReturn(true);

        when(ioService.readAllString(any())).thenReturn(IOUtils.toString(new InputStreamReader(this.getClass().getResourceAsStream("/forms/synchronizationtest-form.frm"))));
    }

    @Test
    public void testRemoveModelProperties() {
        List<ModelProperty> modelProperties = new ArrayList<>();

        modelProperties.add(new ModelPropertyImpl(NAME_PROPERTY,
                                                  new TypeInfoImpl(String.class.getName())));
        modelProperties.add(new ModelPropertyImpl(LASTNAME_PROPERTY,
                                                  new TypeInfoImpl(String.class.getName())));

        newFormModel = new TaskFormModel(PROCESS_ID,
                                         TASK_NAME,
                                         modelProperties);

        FormGenerationResult generationResult = service.generateForms(newFormModel,
                                                                      source);

        assertNotNull(generationResult);

        assertNotNull(generationResult.getRootForm());

        FormDefinition formDefinition = generationResult.getRootForm();

        assertEquals(newFormModel,
                     formDefinition.getModel());

        assertEquals(ORIGINAL_FORM_FIELDS,
                     Integer.valueOf(formDefinition.getFields().size()));

        FieldDefinition field = formDefinition.getFieldByBinding(NAME_PROPERTY);

        assertNotNull(field);
        assertEquals(field.getStandaloneClassName(),
                     String.class.getName());

        field = formDefinition.getFieldByBinding(LASTNAME_PROPERTY);

        assertNotNull(field);
        assertEquals(field.getStandaloneClassName(),
                     String.class.getName());

        assertNull(formDefinition.getFieldByBinding(AGE_PROPERTY));
        assertNull(formDefinition.getFieldByBinding(MARRIED_PROPERTY));
        assertNull(formDefinition.getFieldByBinding(ADDRESS_PROPERTY));

        assertNotNull(formDefinition.getFieldByName(AGE_PROPERTY));
        assertNotNull(formDefinition.getFieldByName(MARRIED_PROPERTY));
        assertNotNull(formDefinition.getFieldByName(ADDRESS_PROPERTY));
    }

    @Test
    public void testModelPropertiesConflict() {
        List<ModelProperty> modelProperties = new ArrayList<>();

        modelProperties.add(new ModelPropertyImpl(NAME_PROPERTY,
                                                  new TypeInfoImpl(Integer.class.getName())));
        modelProperties.add(new ModelPropertyImpl(LASTNAME_PROPERTY,
                                                  new TypeInfoImpl(Integer.class.getName())));
        modelProperties.add(new ModelPropertyImpl(AGE_PROPERTY,
                                                  new TypeInfoImpl(String.class.getName())));
        modelProperties.add(new ModelPropertyImpl(MARRIED_PROPERTY,
                                                  new TypeInfoImpl(Integer.class.getName())));
        modelProperties.add(new ModelPropertyImpl(ADDRESS_PROPERTY,
                                                  new TypeInfoImpl(Boolean.class.getName())));

        newFormModel = new TaskFormModel(PROCESS_ID,
                                         TASK_NAME,
                                         modelProperties);

        FormGenerationResult generationResult = service.generateForms(newFormModel,
                                                                      source);

        assertNotNull(generationResult);

        assertNotNull(generationResult.getRootForm());

        FormDefinition formDefinition = generationResult.getRootForm();

        assertEquals(newFormModel,
                     formDefinition.getModel());

        assertEquals(ORIGINAL_FORM_FIELDS,
                     Integer.valueOf(formDefinition.getFields().size()));

        FieldDefinition field = formDefinition.getFieldByBinding(NAME_PROPERTY);

        assertNotNull(field);
        assertEquals(field.getStandaloneClassName(),
                     Integer.class.getName());
        assertTrue(field instanceof IntegerBoxFieldDefinition);

        field = formDefinition.getFieldByBinding(LASTNAME_PROPERTY);

        assertNotNull(field);
        assertEquals(field.getStandaloneClassName(),
                     Integer.class.getName());
        assertTrue(field instanceof IntegerBoxFieldDefinition);

        field = formDefinition.getFieldByBinding(AGE_PROPERTY);
        assertEquals(field.getStandaloneClassName(),
                     String.class.getName());
        assertTrue(field instanceof TextBoxFieldDefinition);

        field = formDefinition.getFieldByBinding(MARRIED_PROPERTY);
        assertNotNull(field);
        assertEquals(field.getStandaloneClassName(),
                     Integer.class.getName());
        assertTrue(field instanceof IntegerBoxFieldDefinition);

        field = formDefinition.getFieldByBinding(ADDRESS_PROPERTY);
        assertNotNull(field);
        assertEquals(field.getStandaloneClassName(),
                     Boolean.class.getName());
        assertTrue(field instanceof CheckBoxFieldDefinition);
    }

    @Test
    public void testAddModelProperties() {
        List<ModelProperty> modelProperties = new ArrayList<>();

        modelProperties.add(new ModelPropertyImpl(NAME_PROPERTY,
                                                  new TypeInfoImpl(String.class.getName())));
        modelProperties.add(new ModelPropertyImpl(LASTNAME_PROPERTY,
                                                  new TypeInfoImpl(String.class.getName())));
        modelProperties.add(new ModelPropertyImpl(AGE_PROPERTY,
                                                  new TypeInfoImpl(Integer.class.getName())));
        modelProperties.add(new ModelPropertyImpl(MARRIED_PROPERTY,
                                                  new TypeInfoImpl(Boolean.class.getName())));
        modelProperties.add(new ModelPropertyImpl(ADDRESS_PROPERTY,
                                                  new TypeInfoImpl(String.class.getName())));

        modelProperties.add(new ModelPropertyImpl(JOB_PROPERTY,
                                                  new TypeInfoImpl(String.class.getName())));

        modelProperties.add(new ModelPropertyImpl(HOBBIES_PROPERTY,
                                                  new TypeInfoImpl(String.class.getName())));

        newFormModel = new TaskFormModel(PROCESS_ID,
                                         TASK_NAME,
                                         modelProperties);

        FormGenerationResult generationResult = service.generateForms(newFormModel,
                                                                      source);

        assertNotNull(generationResult);

        assertNotNull(generationResult.getRootForm());

        FormDefinition formDefinition = generationResult.getRootForm();

        assertEquals(newFormModel,
                     formDefinition.getModel());

        assertEquals(ALL_FORM_FIELDS,
                     Integer.valueOf(formDefinition.getFields().size()));

        FieldDefinition field = formDefinition.getFieldByBinding(NAME_PROPERTY);
        assertNotNull(field);
        assertEquals(field.getStandaloneClassName(),
                     String.class.getName());
        assertTrue(field instanceof TextBoxFieldDefinition);

        field = formDefinition.getFieldByBinding(LASTNAME_PROPERTY);
        assertNotNull(field);
        assertEquals(field.getStandaloneClassName(),
                     String.class.getName());
        assertTrue(field instanceof TextBoxFieldDefinition);

        field = formDefinition.getFieldByBinding(AGE_PROPERTY);
        assertEquals(field.getStandaloneClassName(),
                     Integer.class.getName());
        assertTrue(field instanceof IntegerBoxFieldDefinition);

        field = formDefinition.getFieldByBinding(MARRIED_PROPERTY);
        assertNotNull(field);
        assertEquals(field.getStandaloneClassName(),
                     Boolean.class.getName());
        assertTrue(field instanceof CheckBoxFieldDefinition);

        field = formDefinition.getFieldByBinding(ADDRESS_PROPERTY);
        assertNotNull(field);
        assertEquals(field.getStandaloneClassName(),
                     String.class.getName());
        assertTrue(field instanceof TextBoxFieldDefinition);

        field = formDefinition.getFieldByBinding(JOB_PROPERTY);
        assertNotNull(field);
        assertEquals(field.getStandaloneClassName(),
                     String.class.getName());
        assertTrue(field instanceof TextBoxFieldDefinition);

        field = formDefinition.getFieldByBinding(HOBBIES_PROPERTY);
        assertNotNull(field);
        assertEquals(field.getStandaloneClassName(),
                     String.class.getName());
        assertTrue(field instanceof TextBoxFieldDefinition);
    }
}
