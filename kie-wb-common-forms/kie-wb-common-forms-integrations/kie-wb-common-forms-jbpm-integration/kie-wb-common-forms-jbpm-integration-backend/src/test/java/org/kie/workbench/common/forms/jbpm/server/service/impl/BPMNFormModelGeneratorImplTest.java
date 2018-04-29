/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.forms.jbpm.server.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.eclipse.bpmn2.Definitions;
import org.jbpm.bpmn2.handler.WorkItemHandlerRuntimeException;
import org.jbpm.simulation.util.BPMN2Utils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.jbpm.model.authoring.AbstractJBPMFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.service.bpmn.util.BPMNVariableUtils;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNFormModelGeneratorImplTest {

    private static final String
            PROJECT_NAME = "myProject",
            RESOURCES_PATH = "/definitions/",
            BPMN2_SUFFIX = ".bpmn2",
            PROCESS_WITHOUT_VARIABLES_NAME = "process-without-variables",
            PROCESS_WITH_ALL_VARIABLES_NAME = "process-with-all-possible-variables",
            PROCESS_WITH_WRONG_TYPES = "process-with-wrong-types",
            PROCESS_WITH_SHARED_FORMS_NAME = "process-with-tasks-sharing-forms",
            PROCESS_WITH_SHARED_FORMS_WRONG_MAPPINGS_NAME = "process-with-tasks-sharing-forms-with-wrong-mapping",
            PROCESS_WITH_SHARED_FORMS_ID = "myProject.processTaskSharedForms",
            PROCESS_WITH_SHARED_FORMS_WRONG_MAPPINGS_ID = "myProject.processTaskSharedFormsWrongMappings",
            PROCESS_WITH_ALL_VARIABLES_ID = PROJECT_NAME + "." + PROCESS_WITH_ALL_VARIABLES_NAME,
            DATA_OBJECT_TYPE = "com.myteam.myproject.Person", // Selected from list of known data object types in Designer variable editor
            CUSTOM_TYPE = "com.test.MyType"; //Entered into the variable type declaration as naked String

    private static final Map<String, String> EXPECTED_INPUT_VARIABLES = new HashMap<String, String>() {{
        put("_string",
            String.class.getName());
        put("_integer",
            Integer.class.getName());
        put("_boolean",
            Boolean.class.getName());
        put("_float",
            Float.class.getName());
        put("_object",
            Object.class.getName());
        put("_dataObject",
            DATA_OBJECT_TYPE);
        put("_customType",
            CUSTOM_TYPE);
    }};

    private static final Map<String, String> EXPECTED_OUTPUT_VARIABLES = new HashMap<String, String>() {{
        put("string_",
            String.class.getName());
        put("integer_",
            Integer.class.getName());
        put("boolean_",
            Boolean.class.getName());
        put("float_",
            Float.class.getName());
        put("object_",
            Object.class.getName());
        put("dataObject_",
            DATA_OBJECT_TYPE);
        put("customType_",
            CUSTOM_TYPE);
    }};
    private static BPMNFormModelGeneratorImpl generator;
    private static Definitions
            processWithoutVariablesDefinitions,
            processWithAllVariablesDefinitions,
            processWithSharedForms,
            processWithSharedFormsWrongMappings,
            processWithWrongTypes;
    @Mock
    private Path path;
    @Mock
    private KieModuleService projectService;
    @Mock
    private KieModule module;
    @Mock
    private ModuleClassLoaderHelper projectClassLoaderHelper;
    @Mock
    private ClassLoader projectClassLoader;

    @BeforeClass
    public static void setUp() throws Exception {
        processWithoutVariablesDefinitions = BPMN2Utils.getDefinitions(BPMNFormModelGeneratorImplTest.class.getResourceAsStream(RESOURCES_PATH + PROCESS_WITHOUT_VARIABLES_NAME + BPMN2_SUFFIX));
        processWithAllVariablesDefinitions = BPMN2Utils.getDefinitions(BPMNFormModelGeneratorImplTest.class.getResourceAsStream(RESOURCES_PATH + PROCESS_WITH_ALL_VARIABLES_NAME + BPMN2_SUFFIX));
        processWithSharedForms = BPMN2Utils.getDefinitions(BPMNFormModelGeneratorImplTest.class.getResourceAsStream(RESOURCES_PATH + PROCESS_WITH_SHARED_FORMS_NAME + BPMN2_SUFFIX));
        processWithSharedFormsWrongMappings = BPMN2Utils.getDefinitions(BPMNFormModelGeneratorImplTest.class.getResourceAsStream(RESOURCES_PATH + PROCESS_WITH_SHARED_FORMS_WRONG_MAPPINGS_NAME + BPMN2_SUFFIX));
        processWithWrongTypes  = BPMN2Utils.getDefinitions(BPMNFormModelGeneratorImplTest.class.getResourceAsStream(RESOURCES_PATH + PROCESS_WITH_WRONG_TYPES + BPMN2_SUFFIX));
    }

    @Before
    public void init() throws Exception {
        when(projectService.resolveModule(any())).thenReturn(module);
        when(module.getRootPath()).thenReturn(path);
        when(projectClassLoaderHelper.getModuleClassLoader(module)).thenReturn(projectClassLoader);
        when(projectClassLoader.loadClass(anyString())).thenAnswer(invocation -> Object.class);

        generator = new BPMNFormModelGeneratorImpl(projectService,
                                                   projectClassLoaderHelper);
    }

    @Test
    public void testGenerateAllForProcessWithoutProcessVariables() {
        //generate all = generateProcessFormModel + generateTaskFormModels
        BusinessProcessFormModel processFormModel = generator.generateProcessFormModel(processWithoutVariablesDefinitions,
                                                                                       path);
        assertProcessFormModelFieldsAreCorrect(processFormModel,
                                               PROCESS_WITHOUT_VARIABLES_NAME);
        assertTrue(processFormModel.getProperties().isEmpty());

        List<TaskFormModel> taskFormModels = generator.generateTaskFormModels(processWithoutVariablesDefinitions,
                                                                              path);
        assertTrue(taskFormModels.isEmpty());
    }

    @Test
    public void testGenerateAllForProcessWithAllPossibleProcessVariables() {
        final Map<String, String> EXPECTED_PROCESS_VARIABLES = new HashMap<String, String>() {{
            put("string",
                String.class.getName());
            put("integer",
                Integer.class.getName());
            put("boolean",
                Boolean.class.getName());
            put("float",
                Float.class.getName());
            put("object",
                Object.class.getName());
            put("dataObject",
                DATA_OBJECT_TYPE);
            put("customType",
                CUSTOM_TYPE);
        }};

        //generate all = generateProcessFormModel + generateTaskFormModels
        BusinessProcessFormModel processFormModel = generator.generateProcessFormModel(processWithAllVariablesDefinitions,
                                                                                       path);
        assertProcessFormModelFieldsAreCorrect(processFormModel,
                                               PROCESS_WITH_ALL_VARIABLES_NAME);
        assertJBPMVariablesAreCorrect(processFormModel,
                                      EXPECTED_PROCESS_VARIABLES);

        List<TaskFormModel> taskFormModels = generator.generateTaskFormModels(processWithAllVariablesDefinitions,
                                                                              path);
        final int EXPECTED_NUMBER_OF_HUMAN_TASKS = 5; // taskOnlyWithOutputs-taskform, emptyTask-taskform, taskOnlyWithInputs-taskform, taskWithDifferentInputsAndOutputs-taskform, taskWithTheSameInputsAndOutputs-taskform
        assertEquals("Forms should be generated for all human tasks including tasks in subprocesses and swimlanes",
                     EXPECTED_NUMBER_OF_HUMAN_TASKS,
                     taskFormModels.size());
    }

    @Test
    public void testCorrectTaskFormModelIsGeneratedForTaskWithoutAnyInputsOrOutputsInSwimlane() {
        final String
                TASK_ID = "_23BBA464-615A-405F-8C3B-4F643BE522D6",
                TASK_NAME = "emptyTask";

        TaskFormModel taskFormModel = generator.generateTaskFormModel(processWithAllVariablesDefinitions,
                                                                      TASK_ID,
                                                                      path);
        assertTaskFormModelIsCorrect(taskFormModel,
                                     PROCESS_WITH_ALL_VARIABLES_ID,
                                     TASK_NAME);
        assertTrue(taskFormModel.getProperties().isEmpty());
    }

    @Test
    public void testCorrectTaskFormModelIsGeneratedForTaskWithDifferentInputsAndOutputsInAdHocSubprocess() {
        final String
                TASK_ID = "_D3B8EE8F-5402-408D-815D-FFE1BAD943D9",
                TASK_NAME = "taskWithDifferentInputsAndOutputs";
        final Map<String, String> EXPECTED_TASK_VARIABLES = new HashMap<String, String>() {{
            putAll(EXPECTED_INPUT_VARIABLES);
            putAll(EXPECTED_OUTPUT_VARIABLES);
        }};

        TaskFormModel taskFormModel = generator.generateTaskFormModel(processWithAllVariablesDefinitions,
                                                                      TASK_ID,
                                                                      path);
        assertTaskFormModelIsCorrect(taskFormModel,
                                     PROCESS_WITH_ALL_VARIABLES_ID,
                                     TASK_NAME);
        assertJBPMVariablesAreCorrect(taskFormModel,
                                      EXPECTED_TASK_VARIABLES);
    }

    @Test
    public void testCorrectTaskFormModelIsGeneratedForTaskWithTheInputsAndOutputsBoundToTheSameNamesInSwimlane() {
        final String TASK_ID = "_9903B013-C42D-486B-A41D-2DEBC60911E3",
                TASK_NAME = "taskWithTheSameInputsAndOutputs";
        final Map<String, String> EXPECTED_TASK_VARIABLES = new HashMap<String, String>() {{
            put("_string_",
                String.class.getName());
            put("_integer_",
                Integer.class.getName());
            put("_boolean_",
                Boolean.class.getName());
            put("_float_",
                Float.class.getName());
            put("_object_",
                Object.class.getName());
            put("_dataObject_",
                DATA_OBJECT_TYPE);
            put("_customType_",
                CUSTOM_TYPE);
        }};

        TaskFormModel taskFormModel = generator.generateTaskFormModel(processWithAllVariablesDefinitions,
                                                                      TASK_ID,
                                                                      path);
        assertTaskFormModelIsCorrect(taskFormModel,
                                     PROCESS_WITH_ALL_VARIABLES_ID,
                                     TASK_NAME);
        assertJBPMVariablesAreCorrect(taskFormModel,
                                      EXPECTED_TASK_VARIABLES);
    }

    @Test
    public void testCorrectTaskFormModelIsGeneratedForTaskThatContainsOnlyInputs() {
        final String TASK_ID = "_9F3A7665-E7EF-4DC2-94F1-F9D20A38547E",
                TASK_NAME = "taskOnlyWithInputs";
        TaskFormModel taskFormModel = generator.generateTaskFormModel(processWithAllVariablesDefinitions,
                                                                      TASK_ID,
                                                                      path);
        assertTaskFormModelIsCorrect(taskFormModel,
                                     PROCESS_WITH_ALL_VARIABLES_ID,
                                     TASK_NAME);
        assertJBPMVariablesAreCorrect(taskFormModel,
                                      EXPECTED_INPUT_VARIABLES);
    }

    @Test
    public void testCorrectTaskFormModelIsGeneratedForTaskThatContainsOnlyOutputsInEmbeddedSubprocess() {
        final String
                TASK_ID = "_9E9EAE16-F9F4-49D0-854D-0D2C8CB9382F",
                TASK_NAME = "taskOnlyWithOutputs";

        TaskFormModel taskFormModel = generator.generateTaskFormModel(processWithAllVariablesDefinitions,
                                                                      TASK_ID,
                                                                      path);
        assertTaskFormModelIsCorrect(taskFormModel,
                                     PROCESS_WITH_ALL_VARIABLES_ID,
                                     TASK_NAME);
        assertJBPMVariablesAreCorrect(taskFormModel,
                                      EXPECTED_OUTPUT_VARIABLES);
    }

    @Test
    public void testGenerateTaskFormModelForTaskWithSharedForm() {
        final String[] TASK_IDS = new String[]{"_77BDDEC9-0D5E-4C46-8AD6-1B528836A22B", "_C976E341-8E35-42C7-B878-67049CE63E5F", "_27F23135-87E9-47C5-9B97-DA793699E8CF"};
        final String TASK_NAME = "task";

        TaskFormModel[] generatedModels = new TaskFormModel[TASK_IDS.length];

        for (int i = 0; i < TASK_IDS.length; i++) {
            generatedModels[i] = generator.generateTaskFormModel(processWithSharedForms,
                                                                 TASK_IDS[i],
                                                                 path);
            assertNotNull(generatedModels[i]);
            assertTaskFormModelIsCorrect(generatedModels[i],
                                         PROCESS_WITH_SHARED_FORMS_ID,
                                         TASK_NAME);
            checkExpectedMergedFormVariables(generatedModels[i]);
        }
    }

    @Test
    public void testGenerateAllTaskFormModelForTasksWithSharedForm() {
        final String TASK_NAME = "task";

        final int EXPECTED_MODELS = 2;

        List<TaskFormModel> generatedModels = generator.generateTaskFormModels(processWithSharedForms,
                                                                               path);

        assertNotNull(generatedModels);

        assertEquals(EXPECTED_MODELS,
                     generatedModels.size());

        for (TaskFormModel formModel : generatedModels) {
            assertNotNull(formModel);
            assertEquals(PROCESS_WITH_SHARED_FORMS_ID,
                         formModel.getProcessId());
            assertNotNull(formModel.getProperties());
            assertFalse(formModel.getProperties().isEmpty());
            if (formModel.getFormName().equals(TASK_NAME + BPMNVariableUtils.TASK_FORM_SUFFIX)) {
                checkExpectedMergedFormVariables(formModel);
            }
        }
    }

    @Test
    public void testGenerateTaskFormModelForTaskWithSharedFormWithWrongMappings() {

        final String[] TASK_IDS = new String[]{"_77BDDEC9-0D5E-4C46-8AD6-1B528836A22B", "_C976E341-8E35-42C7-B878-67049CE63E5F", "_27F23135-87E9-47C5-9B97-DA793699E8CF"};

        int count = 0;

        for (int i = 0; i < TASK_IDS.length; i++) {
            try {
                generator.generateTaskFormModel(processWithSharedFormsWrongMappings,
                                                TASK_IDS[i],
                                                path);

                fail("We shouldn't be here, the form generation should break!");
            } catch (Exception ex) {
                count++;
            }
        }

        assertEquals(TASK_IDS.length,
                     count);
    }

    @Test
    public void testGenerateAllTaskFormModelForTasksWithSharedFormWithWrongMappings() {
        final String TASK_NAME = "task";

        final int EXPECTED_MODELS = 1;

        List<TaskFormModel> generatedModels = generator.generateTaskFormModels(processWithSharedFormsWrongMappings,
                                                                               path);

        assertNotNull(generatedModels);

        assertEquals(EXPECTED_MODELS,
                     generatedModels.size());

        for (TaskFormModel formModel : generatedModels) {
            assertNotNull(formModel);
            assertEquals(PROCESS_WITH_SHARED_FORMS_WRONG_MAPPINGS_ID,
                         formModel.getProcessId());
            assertNotNull(formModel.getProperties());
            assertFalse(formModel.getProperties().isEmpty());
            assertNotEquals(TASK_NAME + BPMNVariableUtils.TASK_FORM_SUFFIX,
                            formModel.getFormName());
        }
    }

    @Test
    public void testGenerateAllWithWrongTypes() throws Exception {
        when(projectClassLoader.loadClass(anyString())).thenAnswer(invocationOnMock -> getClass().getClassLoader().loadClass(invocationOnMock.getArguments()[0].toString()));

        final Map<String, String> EXPECTED_PROPERTIES = new HashMap<String, String>() {{
            put("name", String.class.getName());
            put("age", Integer.class.getName());
            put("error", WorkItemHandlerRuntimeException.class.getName());
            put("list", Object.class.getName());
        }};

        BusinessProcessFormModel processFormModel = generator.generateProcessFormModel(processWithWrongTypes, path);

        assertProcessFormModelFieldsAreCorrect(processFormModel, PROCESS_WITH_WRONG_TYPES);

        Assertions.assertThat(processFormModel.getProperties())
                .isNotNull()
                .isNotEmpty()
                .hasSize(EXPECTED_PROPERTIES.size());

        assertEquals(EXPECTED_PROPERTIES.size(), processFormModel.getProperties().size());
        assertJBPMVariablesAreCorrect(processFormModel, EXPECTED_PROPERTIES);

        List<TaskFormModel> taskFormModels = generator.generateTaskFormModels(processWithWrongTypes,
                                                                              path);
        final int EXPECTED_NUMBER_OF_HUMAN_TASKS = 1;

        Assertions.assertThat(taskFormModels)
                .isNotNull()
                .isNotEmpty()
                .hasSize(EXPECTED_NUMBER_OF_HUMAN_TASKS);

        TaskFormModel taskFormModel = taskFormModels.get(0);

        Assertions.assertThat(taskFormModel.getProperties())
                .isNotNull()
                .isNotEmpty()
                .hasSize(EXPECTED_PROPERTIES.size());

        assertJBPMVariablesAreCorrect(taskFormModel, EXPECTED_PROPERTIES);
    }

    protected void checkExpectedMergedFormVariables(TaskFormModel formModel) {
        final Map<String, String> EXPECTED_TYPES = new HashMap<>();
        EXPECTED_TYPES.put("name",
                           String.class.getName());
        EXPECTED_TYPES.put("lastName",
                           String.class.getName());
        EXPECTED_TYPES.put("age",
                           Integer.class.getName());
        EXPECTED_TYPES.put("married",
                           Boolean.class.getName());

        assertEquals(EXPECTED_TYPES.size(),
                     formModel.getProperties().size());
        for (ModelProperty property : formModel.getProperties()) {
            assertNotNull(EXPECTED_TYPES.get(property.getName()));
            assertEquals(EXPECTED_TYPES.get(property.getName()),
                         property.getTypeInfo().getClassName());
        }
    }

    private void assertProcessFormModelFieldsAreCorrect(BusinessProcessFormModel formModel,
                                                        String processName) {
        final String PROCESS_ID = PROJECT_NAME + "." + processName;
        assertEquals(PROCESS_ID,
                     formModel.getProcessId());
        assertEquals(processName,
                     formModel.getProcessName());
    }

    private void assertJBPMVariablesAreCorrect(AbstractJBPMFormModel formModel,
                                               Map<String, String> expectedVariables) {
        Map<String, String> actualVariables = new HashMap<>();
        for (ModelProperty modelProperty : formModel.getProperties()) {
            actualVariables.put(modelProperty.getName(),
                                modelProperty.getTypeInfo().getClassName());
        }
        assertEquals(expectedVariables,
                     actualVariables);
    }

    private void assertTaskFormModelIsCorrect(TaskFormModel taskFormModel,
                                              String processId,
                                              String taskName) {
        assertEquals(processId,
                     taskFormModel.getProcessId());

        final String EXPECTED_FORM_NAME = taskName + BPMNVariableUtils.TASK_FORM_SUFFIX;
        assertEquals(EXPECTED_FORM_NAME,
                     taskFormModel.getFormName());
    }
}
