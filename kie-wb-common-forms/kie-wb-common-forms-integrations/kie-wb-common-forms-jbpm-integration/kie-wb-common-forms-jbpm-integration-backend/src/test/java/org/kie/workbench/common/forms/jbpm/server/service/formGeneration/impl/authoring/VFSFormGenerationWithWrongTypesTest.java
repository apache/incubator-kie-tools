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
package org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.authoring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jbpm.bpmn2.handler.WorkItemHandlerRuntimeException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.FormGenerationResult;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class VFSFormGenerationWithWrongTypesTest extends BPMNVFSFormDefinitionGeneratorServiceTest {

    public static final String TASK_NAME = "task";
    public static final String PROCESS_ID = "issues.Process";

    // Existing properties
    public static final String NAME_PROPERTY = "name";
    public static final String AGE_PROPERTY = "age";
    public static final String LIST_PROPERTY = "list";
    public static final String ERROR_PROPERTY = "error";

    private TaskFormModel newFormModel;

    @Override
    public void setup() throws IOException {
        super.setup();
    }

    @Test
    public void testGeneratedForms() {
        List<ModelProperty> modelProperties = new ArrayList<>();

        modelProperties.add(new ModelPropertyImpl(NAME_PROPERTY,
                                                  new TypeInfoImpl(String.class.getName())));
        modelProperties.add(new ModelPropertyImpl(AGE_PROPERTY,
                                                  new TypeInfoImpl(Integer.class.getName())));
        modelProperties.add(new ModelPropertyImpl(LIST_PROPERTY,
                                                  new TypeInfoImpl(TypeKind.OBJECT, Object.class.getName(), true)));
        modelProperties.add(new ModelPropertyImpl(ERROR_PROPERTY,
                                                  new TypeInfoImpl(TypeKind.OBJECT, WorkItemHandlerRuntimeException.class.getName(), false)));

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

        Assertions.assertThat(formDefinition.getFields())
                .isNotNull()
                .isNotEmpty()
                .hasSize(2);

        assertNotNull(formDefinition.getFieldByBinding(NAME_PROPERTY));
        assertNotNull(formDefinition.getFieldByBinding(AGE_PROPERTY));

        assertNull(formDefinition.getFieldByBinding(LIST_PROPERTY));
        assertNull(formDefinition.getFieldByBinding(ERROR_PROPERTY));
    }
}
