/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.runtime;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jbpm.bpmn2.handler.WorkItemHandlerRuntimeException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.MultipleInputFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.StringMultipleInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.FormGenerationResult;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.kie.workbench.common.forms.model.impl.meta.entries.FieldTypeEntry;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RuntimeFormGenerationWithWrongTypesTest extends BPMNRuntimeFormDefinitionGeneratorServiceTest {

    public static final String TASK_NAME = "task";
    public static final String PROCESS_ID = "issues.Process";

    // Existing properties
    public static final String NAME_PROPERTY = "name";
    public static final String AGE_PROPERTY = "age";
    public static final String WRONG_OBJECT_PROPERTY = "wrongObject";
    public static final String OBJECT_PROPERTY = "object";
    public static final String WRONG_LIST_PROPERTY = "wrongList";
    public static final String LIST_PROPERTY = "list";
    public static final String ERROR_PROPERTY = "error";

    private TaskFormModel newFormModel;

    @Test
    public void testGeneratedForms() throws ClassNotFoundException {
        when(source.loadClass(anyString())).then(invocationOnMock -> getClass().getClassLoader().loadClass(invocationOnMock.getArguments()[0].toString()));

        List<ModelProperty> modelProperties = new ArrayList<>();

        modelProperties.add(new ModelPropertyImpl(NAME_PROPERTY, new TypeInfoImpl(String.class.getName())));
        modelProperties.add(new ModelPropertyImpl(AGE_PROPERTY, new TypeInfoImpl(Integer.class.getName())));

        modelProperties.add(new ModelPropertyImpl(WRONG_OBJECT_PROPERTY, new TypeInfoImpl(TypeKind.OBJECT, Object.class.getName(), false)));
        ModelPropertyImpl objectProperty = new ModelPropertyImpl(OBJECT_PROPERTY, new TypeInfoImpl(TypeKind.OBJECT, Object.class.getName(), false));
        objectProperty.getMetaData().addEntry(new FieldTypeEntry(TextAreaFieldType.NAME));
        modelProperties.add(objectProperty);

        modelProperties.add(new ModelPropertyImpl(WRONG_LIST_PROPERTY, new TypeInfoImpl(TypeKind.OBJECT, Object.class.getName(), true)));
        ModelPropertyImpl listModelProperty = new ModelPropertyImpl(LIST_PROPERTY, new TypeInfoImpl(TypeKind.OBJECT, Object.class.getName(), true));
        listModelProperty.getMetaData().addEntry(new FieldTypeEntry(MultipleInputFieldType.NAME));
        modelProperties.add(listModelProperty);

        modelProperties.add(new ModelPropertyImpl(ERROR_PROPERTY, new TypeInfoImpl(TypeKind.OBJECT, WorkItemHandlerRuntimeException.class.getName(), false)));

        newFormModel = new TaskFormModel(PROCESS_ID, TASK_NAME, modelProperties);

        FormGenerationResult generationResult = service.generateForms(newFormModel, source);

        assertNotNull(generationResult);

        assertNotNull(generationResult.getRootForm());

        FormDefinition formDefinition = generationResult.getRootForm();

        assertEquals(newFormModel, formDefinition.getModel());

        Assertions.assertThat(formDefinition.getFields())
                .isNotNull()
                .isNotEmpty()
                .hasSize(4);

        Assertions.assertThat(formDefinition.getFieldByBinding(NAME_PROPERTY))
                .isNotNull()
                .isInstanceOf(TextBoxFieldDefinition.class);

        Assertions.assertThat(formDefinition.getFieldByBinding(AGE_PROPERTY))
                .isNotNull()
                .isInstanceOf(IntegerBoxFieldDefinition.class);

        Assertions.assertThat(formDefinition.getFieldByBinding(OBJECT_PROPERTY))
                .isNotNull()
                .isInstanceOf(TextAreaFieldDefinition.class);

        Assertions.assertThat(formDefinition.getFieldByBinding(LIST_PROPERTY))
                .isNotNull()
                .isInstanceOf(StringMultipleInputFieldDefinition.class);

        Assertions.assertThat(formDefinition.getFieldByBinding(WRONG_LIST_PROPERTY))
                .isNull();
        Assertions.assertThat(formDefinition.getFieldByBinding(WRONG_OBJECT_PROPERTY))
                .isNull();
        Assertions.assertThat(formDefinition.getFieldByBinding(ERROR_PROPERTY))
                .isNull();
    }
}
