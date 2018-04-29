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
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.FormGenerationResult;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RuntimeFormGenerationWithWrongTypesTest extends BPMNRuntimeFormDefinitionGeneratorServiceTest {

    private static final String HTML_PARAM = "HTML_CODE";
    private static final String HEADER = "<div class=\"alert alert-warning\" role=\"alert\"><span class=\"pficon pficon-warning-triangle-o\">";

    public static final String TASK_NAME = "task";
    public static final String PROCESS_ID = "issues.Process";

    // Existing properties
    public static final String NAME_PROPERTY = "name";
    public static final String AGE_PROPERTY = "age";
    public static final String LIST_PROPERTY = "list";
    public static final String ERROR_PROPERTY = "error";

    private TaskFormModel newFormModel;

    @Test
    public void testGeneratedForms() throws ClassNotFoundException {
        when(source.loadClass(anyString())).then(invocationOnMock -> getClass().getClassLoader().loadClass(invocationOnMock.getArguments()[0].toString()));

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

        LayoutComponent formHeader = formDefinition.getLayoutTemplate().getRows().get(0).getLayoutColumns().get(0).getLayoutComponents().get(0);

        Assertions.assertThat(formHeader)
                .isNotNull()
                .hasFieldOrPropertyWithValue("dragTypeName", "org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent");

        String headerContent = formHeader.getProperties().get(HTML_PARAM);

        Assertions.assertThat(headerContent)
                .isNotNull()
                .contains(HEADER);
    }
}
