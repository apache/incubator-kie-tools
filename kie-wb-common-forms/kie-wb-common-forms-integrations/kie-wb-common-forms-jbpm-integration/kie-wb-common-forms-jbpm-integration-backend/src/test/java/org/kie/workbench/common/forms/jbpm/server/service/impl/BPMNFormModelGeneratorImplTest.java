/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.bpmn2.Definitions;
import org.jbpm.simulation.util.BPMN2Utils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.workbench.common.forms.jbpm.model.authoring.AbstractJBPMFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMVariable;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.service.bpmn.util.BPMNVariableUtils;

import static org.junit.Assert.*;

public class BPMNFormModelGeneratorImplTest {

    private static final String
            PROJECT_NAME = "myProject",
            RESOURCES_PATH = "/definitions/",
            BPMN2_SUFFIX = ".bpmn2",
            PROCESS_WITHOUT_VARIABLES_NAME = "process-without-variables",
            PROCESS_WITH_ALL_VARIABLES_NAME = "process-with-all-possible-variables",
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
            String.class.getName());
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
            String.class.getName());
        put("dataObject_",
            DATA_OBJECT_TYPE);
        put("customType_",
            CUSTOM_TYPE);
    }};

    private static BPMNFormModelGeneratorImpl generator;

    private static Definitions
            processWithoutVariablesDefinitions,
            processWithAllVariablesDefinitions;

    @BeforeClass
    public static void setUp() throws Exception {
        generator = new BPMNFormModelGeneratorImpl();

        processWithoutVariablesDefinitions = BPMN2Utils.getDefinitions(BPMNFormModelGeneratorImplTest.class.getResourceAsStream(RESOURCES_PATH + PROCESS_WITHOUT_VARIABLES_NAME + BPMN2_SUFFIX));
        processWithAllVariablesDefinitions = BPMN2Utils.getDefinitions(BPMNFormModelGeneratorImplTest.class.getResourceAsStream(RESOURCES_PATH + PROCESS_WITH_ALL_VARIABLES_NAME + BPMN2_SUFFIX));
    }

    @Test
    public void testGenerateAllForProcessWithoutProcessVariables() {
        //generate all = generateProcessFormModel + generateTaskFormModels
        BusinessProcessFormModel processFormModel = generator.generateProcessFormModel(processWithoutVariablesDefinitions);
        assertProcessFormModelFieldsAreCorrect(processFormModel,
                                               PROCESS_WITHOUT_VARIABLES_NAME);
        assertTrue(processFormModel.getVariables().isEmpty());

        List<TaskFormModel> taskFormModels = generator.generateTaskFormModels(processWithoutVariablesDefinitions);
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
                String.class.getName());
            put("dataObject",
                DATA_OBJECT_TYPE);
            put("customType",
                CUSTOM_TYPE);
        }};

        //generate all = generateProcessFormModel + generateTaskFormModels
        BusinessProcessFormModel processFormModel = generator.generateProcessFormModel(processWithAllVariablesDefinitions);
        assertProcessFormModelFieldsAreCorrect(processFormModel,
                                               PROCESS_WITH_ALL_VARIABLES_NAME);
        assertJBPMVariablesAreCorrect(processFormModel,
                                      EXPECTED_PROCESS_VARIABLES);

        List<TaskFormModel> taskFormModels = generator.generateTaskFormModels(processWithAllVariablesDefinitions);
        final int EXPECTED_NUMBER_OF_HUMAN_TASKS = 7;
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
                                                                      TASK_ID);
        assertTaskFormModelIsCorrect(taskFormModel,
                                     PROCESS_WITH_ALL_VARIABLES_ID,
                                     TASK_ID,
                                     TASK_NAME);
        assertTrue(taskFormModel.getVariables().isEmpty());
    }

    @Ignore("Currently failing because of JBPM-5977")
    @Test
    public void testCorrectTaskFormModelIsGeneratedForTaskWithDifferentInputsAndOutputsInAdHocSubprocess() {
        final String
                AD_HOC_SUBPROCESS_ID = "_D3B8EE8F-5402-408D-815D-FFE1BAD943D9",
                TASK_ID = "_AFD1A863-57C6-46EB-A85D-0ADB1E21FA13",
                TASK_NAME = "taskWithDifferentInputsAndOutputs";
        final Map<String, String> EXPECTED_TASK_VARIABLES = new HashMap<String, String>() {{
            putAll(EXPECTED_INPUT_VARIABLES);
            putAll(EXPECTED_OUTPUT_VARIABLES);
        }};

        TaskFormModel taskFormModel = generator.generateTaskFormModel(processWithAllVariablesDefinitions,
                                                                      TASK_ID);
        assertTaskFormModelIsCorrect(taskFormModel,
                                     AD_HOC_SUBPROCESS_ID,
                                     TASK_ID,
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
                String.class.getName());
            put("_dataObject_",
                DATA_OBJECT_TYPE);
            put("_customType_",
                CUSTOM_TYPE);
        }};

        TaskFormModel taskFormModel = generator.generateTaskFormModel(processWithAllVariablesDefinitions,
                                                                      TASK_ID);
        assertTaskFormModelIsCorrect(taskFormModel,
                                     PROCESS_WITH_ALL_VARIABLES_ID,
                                     TASK_ID,
                                     TASK_NAME);
        assertJBPMVariablesAreCorrect(taskFormModel,
                                      EXPECTED_TASK_VARIABLES);
    }

    @Test
    public void testCorrectTaskFormModelIsGeneratedForTaskThatContainsOnlyInputs() {
        final String TASK_ID = "_9F3A7665-E7EF-4DC2-94F1-F9D20A38547E",
                TASK_NAME = "taskOnlyWithInputs";
        TaskFormModel taskFormModel = generator.generateTaskFormModel(processWithAllVariablesDefinitions,
                                                                      TASK_ID);
        assertTaskFormModelIsCorrect(taskFormModel,
                                     PROCESS_WITH_ALL_VARIABLES_ID,
                                     TASK_ID,
                                     TASK_NAME);
        assertJBPMVariablesAreCorrect(taskFormModel,
                                      EXPECTED_INPUT_VARIABLES);
    }

    @Ignore("Currently failing because of JBPM-5977")
    @Test
    public void testCorrectTaskFormModelIsGeneratedForTaskThatContainsOnlyOutputsInEmbeddedSubprocess() {
        final String
                EMBEDDED_SUBPROCESS_ID = "_6E36848C-E302-40DB-B05B-53A3136D114A",
                TASK_ID = "_9E9EAE16-F9F4-49D0-854D-0D2C8CB9382F",
                TASK_NAME = "taskOnlyWithOutputs";

        TaskFormModel taskFormModel = generator.generateTaskFormModel(processWithAllVariablesDefinitions,
                                                                      TASK_ID);
        assertTaskFormModelIsCorrect(taskFormModel,
                                     EMBEDDED_SUBPROCESS_ID,
                                     TASK_ID,
                                     TASK_NAME);
        assertJBPMVariablesAreCorrect(taskFormModel,
                                      EXPECTED_OUTPUT_VARIABLES);
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
        for (JBPMVariable variable : formModel.getVariables()) {
            actualVariables.put(variable.getName(),
                                variable.getType());
        }
        assertEquals(expectedVariables,
                     actualVariables);
    }

    private void assertTaskFormModelIsCorrect(TaskFormModel taskFormModel,
                                              String processId,
                                              String taskId,
                                              String taskName) {
        assertEquals(processId,
                     taskFormModel.getProcessId());
        assertEquals(taskId,
                     taskFormModel.getTaskId());
        assertEquals(taskName,
                     taskFormModel.getTaskName());
        final String EXPECTED_FORM_NAME = taskName + BPMNVariableUtils.TASK_FORM_SUFFIX;
        assertEquals(EXPECTED_FORM_NAME,
                     taskFormModel.getFormName());
    }
}