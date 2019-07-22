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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.tasks;

import java.util.List;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.DeclarationList;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UserTaskTest extends TaskTest<UserTask> {

    private static final String BPMN_TASK_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/userTasks.bpmn";

    private static final String EMPTY_TOP_LEVEL_TASK_ID = "B740EDEB-E4EE-472C-BEF2-E3C01A7B1949";
    private static final String FILLED_TOP_LEVEL_TASK_JAVA_ID = "7799D66F-5754-4850-AF35-D60E78105D88";
    private static final String FILLED_TOP_LEVEL_TASK_JAVASCRIPT_ID = "38C9D3E0-CD8C-48A5-9359-F5A5B0421C59";
    private static final String FILLED_TOP_LEVEL_TASK_MVEL_ID = "5EEC84C3-2F1A-468A-942F-7BFFF2D20A94";

    private static final String EMPTY_SUBPROCESS_LEVEL_TASK_ID = "E8FD1C7D-EA48-4457-9DB3-3197990DC625";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_JAVA_ID = "47301752-E893-43CB-9BD7-6E85BD8CDCCA";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "0179F9B7-B3F8-4804-B7A2-AFB48E21792B";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_MVEL_ID = "6EA43C79-97AD-474B-935A-883825BA6B5D";

    private static final String EMPTY_ONE_INCOME_SUBPROCESS_LEVEL_TASK_ID = "AA4C5ECB-DBF0-4679-AEA4-23FCDF8EC601";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVA_ID = "FDAC90A9-F7C1-496B-8265-EB915EB0C060";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "92901674-BE25-46E0-8FB5-1126F55FCE9A";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_MVEL_ID = "025AD6AC-2355-4210-82C4-B897C3C5B95A";

    private static final String EMPTY_ONE_INCOME_TOP_LEVEL_TASK_ID = "90934E6B-51C9-445A-9D9B-BB389A457299";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVA_ID = "8EF57263-2946-4681-B638-F7985FCFCE56";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVASCRIPT_ID = "95BE309A-38F7-4110-A460-ADFF3C14671A";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_MVEL_ID = "42CAB178-389C-4141-B895-8ADFEBCFFAEC";

    private static final String EMPTY_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_ID = "BD16C552-83E5-41DE-BB25-C78889712BDD";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVA_ID = "F9D87280-755F-4B68-A8D0-C9A788A8641A";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "3B82D4EC-2AF1-452E-81B9-CA65EEC2A114";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_MVEL_ID = "F1AAE67C-22E6-4696-B78C-28144F43025D";

    private static final String EMPTY_TWO_INCOMES_TOP_LEVEL_TASK_ID = "4B7F7377-E03A-40BA-B919-6649FF6512C5";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVA_ID = "A7D2CD45-16F0-47A1-B618-5D823DBC01DF";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVASCRIPT_ID = "B60669F3-00B4-48BF-8B0A-EFA1F68E8963";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_MVEL_ID = "BF8D305B-B143-4578-84D1-7BC540D0A51E";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 69;

    private static final String EMPTY_TASK_DATA_INPUT_OUTPUT = "|Skippable:Object|||";
    private static final String TASK_SCRIPT_JAVA_LANGUAGE = "java";
    private static final String TASK_SCRIPT_JAVASCRIPT_LANGUAGE = "javascript";
    private static final String TASK_SCRIPT_MVEL_LANGUAGE = "mvel";
    private static final boolean IS_ASYNC = true;
    private static final boolean IS_NOT_ASYNC = false;
    private static final boolean IS_SKIPPABLE = true;
    private static final boolean IS_NOT_SKIPPABLE = false;
    private static final boolean AD_HOC_AUTOSTART = true;
    private static final boolean NOT_AD_HOC_AUTOSTART = false;

    public UserTaskTest() throws Exception {
    }

    @Test
    @SuppressWarnings("unchecked")
    public void RHBA607() throws Exception {
        super.init();
        final String BPMN_USER_TASK_PROPERTIES_FILE_PATH =
                "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/userTaskProperties.bpmn";
        final String DIAGRAM_ID = "_pfJ-8O50EeiVSc03Fghuww";

        Diagram<Graph, Metadata> d = unmarshall(marshaller, BPMN_USER_TASK_PROPERTIES_FILE_PATH);
        Node<View<BPMNDiagramImpl>, ?> node = d.getGraph().getNode(DIAGRAM_ID);
        ProcessData processData = node.getContent().getDefinition().getProcessData();
        ProcessVariables processVariables = processData.getProcessVariables();
        DeclarationList declarationList = DeclarationList.fromString(processVariables.getValue());
        assertTrue(declarationList.getDeclarations().isEmpty());
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyTaskProperties() {
        // cannot be empty
        final String TASK_NAME = "Task_1";
        // cannot be empty
        final String TASK_TASK_NAME = "Task";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        UserTask emptyTopLevelTask = getTaskNodeById(getDiagram(),
                                                     EMPTY_TOP_LEVEL_TASK_ID,
                                                     ZERO_INCOME_EDGES,
                                                     HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(emptyTopLevelTask.getGeneral(), TASK_NAME, EMPTY_VALUE);
        assertUserTaskExecutionSet(emptyTopLevelTask.getExecutionSet(),
                                   TASK_TASK_NAME,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   IS_NOT_ASYNC,
                                   IS_NOT_SKIPPABLE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   NOT_AD_HOC_AUTOSTART,
                                   EMPTY_TASK_DATA_INPUT_OUTPUT,
                                   EMPTY_VALUE,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   EMPTY_VALUE,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE
        );
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskOneIncomeEmptyProperties() {
        // cannot be empty
        final String TASK_NAME = "Task_2";
        // cannot be empty
        final String TASK_TASK_NAME = "Task";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        UserTask emptyTopLevelTask = getTaskNodeById(getDiagram(),
                                                     EMPTY_ONE_INCOME_TOP_LEVEL_TASK_ID,
                                                     ONE_INCOME_EDGE,
                                                     HAS_OUTCOME_EDGE);
        assertGeneralSet(emptyTopLevelTask.getGeneral(), TASK_NAME, EMPTY_VALUE);
        assertUserTaskExecutionSet(emptyTopLevelTask.getExecutionSet(),
                                   TASK_TASK_NAME,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   IS_NOT_ASYNC,
                                   IS_NOT_SKIPPABLE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   NOT_AD_HOC_AUTOSTART,
                                   EMPTY_TASK_DATA_INPUT_OUTPUT,
                                   EMPTY_VALUE,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   EMPTY_VALUE,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE
        );
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskTwoIncomesEmptyProperties() {
        // cannot be empty
        final String TASK_NAME = "Task_3";
        // cannot be empty
        final String TASK_TASK_NAME = "Task";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        UserTask emptyTopLevelTask = getTaskNodeById(getDiagram(),
                                                     EMPTY_TWO_INCOMES_TOP_LEVEL_TASK_ID,
                                                     TWO_INCOME_EDGES,
                                                     HAS_OUTCOME_EDGE);
        assertGeneralSet(emptyTopLevelTask.getGeneral(), TASK_NAME, EMPTY_VALUE);
        assertUserTaskExecutionSet(emptyTopLevelTask.getExecutionSet(),
                                   TASK_TASK_NAME,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   IS_NOT_ASYNC,
                                   IS_NOT_SKIPPABLE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   NOT_AD_HOC_AUTOSTART,
                                   EMPTY_TASK_DATA_INPUT_OUTPUT,
                                   EMPTY_VALUE,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   EMPTY_VALUE,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE
        );
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskFilledProperties() {
        final String TASK_NAME_JAVA = "Task01 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Task01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_TASK_NAME_JAVA = "Task01ImplExecName";
        final String TASK_SUBJECT_JAVA = "subject01 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_ACTORS_JAVA = "customUser";
        final String TASK_GROUPS_JAVA = "customGroup";
        final String TASK_PRIORITY_JAVA = "10";
        final String TASK_DESCRIPTION_JAVA = "Task01 description\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_CREATED_BY_JAVA = "customUser";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action\");";

        final String TASK_NAME_JAVASCRIPT = "Task04 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Task04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_TASK_NAME_JAVASCRIPT = "Task04ImplExecName";
        final String TASK_SUBJECT_JAVASCRIPT = "subject04 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_ACTORS_JAVASCRIPT = "customUser";
        final String TASK_GROUPS_JAVASCRIPT = "customGroup";
        final String TASK_PRIORITY_JAVASCRIPT = "10";
        final String TASK_DESCRIPTION_JAVASCRIPT = "Task04 description\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_CREATED_BY_JAVASCRIPT = "customUser";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action\");";

        final String TASK_NAME_MVEL = "Task07 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Task07 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_TASK_NAME_MVEL = "Task07ImplExecName";
        final String TASK_SUBJECT_MVEL = "subject07 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_ACTORS_MVEL = "customUser";
        final String TASK_GROUPS_MVEL = "customGroup";
        final String TASK_PRIORITY_MVEL = "10";
        final String TASK_DESCRIPTION_MVEL = "Task07 description\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_CREATED_BY_MVEL = "customUser";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action\");";

        final String TASK_DATA_INPUT_OUTPUT_JAVA = "|input:String,Skippable:Object,GroupId:Object,Comment:Object,Description:Object,Priority:Object,CreatedBy:Object,Content:Object||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";
        final String TASK_DATA_INPUT_OUTPUT_JAVASCRIPT = "|input:String,Skippable:Object,GroupId:Object,Comment:Object,Description:Object,Priority:Object,CreatedBy:Object,Content:Object||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";
        final String TASK_DATA_INPUT_OUTPUT_MVEL = "|input:String,Skippable:Object,GroupId:Object,Comment:Object,Description:Object,Priority:Object,CreatedBy:Object,Content:Object||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        final String TASK_CONTENT_JAVA = "Content example. Top level. Java.";
        final String TASK_CONTENT_JAVASCRIPT = "Content example. Top level. Javascript.";
        final String TASK_CONTENT_MVEL = "Content example. Top level. MVEL.";
        final String SLA_DUE_DATE = "25/12/1983";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        UserTask filledTopLevelTaskJava = getTaskNodeById(getDiagram(),
                                                          FILLED_TOP_LEVEL_TASK_JAVA_ID,
                                                          ZERO_INCOME_EDGES,
                                                          HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertUserTaskExecutionSet(filledTopLevelTaskJava.getExecutionSet(),
                                   TASK_TASK_NAME_JAVA,
                                   TASK_SUBJECT_JAVA,
                                   TASK_ACTORS_JAVA,
                                   TASK_GROUPS_JAVA,
                                   IS_ASYNC,
                                   IS_SKIPPABLE,
                                   TASK_PRIORITY_JAVA,
                                   TASK_DESCRIPTION_JAVA,
                                   TASK_CREATED_BY_JAVA,
                                   AD_HOC_AUTOSTART,
                                   TASK_DATA_INPUT_OUTPUT_JAVA,
                                   TASK_ON_ENTRY_ACTION_JAVA,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   TASK_ON_EXIT_ACTION_JAVA,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   TASK_CONTENT_JAVA,
                                   SLA_DUE_DATE);

        UserTask filledTopLevelTaskJavascript = getTaskNodeById(getDiagram(),
                                                                FILLED_TOP_LEVEL_TASK_JAVASCRIPT_ID,
                                                                ZERO_INCOME_EDGES,
                                                                HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertUserTaskExecutionSet(filledTopLevelTaskJavascript.getExecutionSet(),
                                   TASK_TASK_NAME_JAVASCRIPT,
                                   TASK_SUBJECT_JAVASCRIPT,
                                   TASK_ACTORS_JAVASCRIPT,
                                   TASK_GROUPS_JAVASCRIPT,
                                   IS_ASYNC,
                                   IS_SKIPPABLE,
                                   TASK_PRIORITY_JAVASCRIPT,
                                   TASK_DESCRIPTION_JAVASCRIPT,
                                   TASK_CREATED_BY_JAVASCRIPT,
                                   AD_HOC_AUTOSTART,
                                   TASK_DATA_INPUT_OUTPUT_JAVASCRIPT,
                                   TASK_ON_ENTRY_ACTION_JAVASCRIPT,
                                   TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                   TASK_ON_EXIT_ACTION_JAVASCRIPT,
                                   TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                   TASK_CONTENT_JAVASCRIPT,
                                   SLA_DUE_DATE);

        UserTask filledTopLevelTaskMvel = getTaskNodeById(getDiagram(),
                                                          FILLED_TOP_LEVEL_TASK_MVEL_ID,
                                                          ZERO_INCOME_EDGES,
                                                          HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertUserTaskExecutionSet(filledTopLevelTaskMvel.getExecutionSet(),
                                   TASK_TASK_NAME_MVEL,
                                   TASK_SUBJECT_MVEL,
                                   TASK_ACTORS_MVEL,
                                   TASK_GROUPS_MVEL,
                                   IS_ASYNC,
                                   IS_SKIPPABLE,
                                   TASK_PRIORITY_MVEL,
                                   TASK_DESCRIPTION_MVEL,
                                   TASK_CREATED_BY_MVEL,
                                   AD_HOC_AUTOSTART,
                                   TASK_DATA_INPUT_OUTPUT_MVEL,
                                   TASK_ON_ENTRY_ACTION_MVEL,
                                   TASK_SCRIPT_MVEL_LANGUAGE,
                                   TASK_ON_EXIT_ACTION_MVEL,
                                   TASK_SCRIPT_MVEL_LANGUAGE,
                                   TASK_CONTENT_MVEL,
                                   SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskOneIncomeFilledProperties() {
        final String TASK_NAME_JAVA = "Task02 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Task02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_TASK_NAME_JAVA = "Task02ImplExecName";
        final String TASK_SUBJECT_JAVA = "subject02 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_ACTORS_JAVA = "customUser";
        final String TASK_GROUPS_JAVA = "customGroup";
        final String TASK_PRIORITY_JAVA = "10";
        final String TASK_DESCRIPTION_JAVA = "Task02 description\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_CREATED_BY_JAVA = "customUser";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action\");";

        final String TASK_NAME_JAVASCRIPT = "Task05 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Task05 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_TASK_NAME_JAVASCRIPT = "Task05ImplExecName";
        final String TASK_SUBJECT_JAVASCRIPT = "subject05 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_ACTORS_JAVASCRIPT = "customUser";
        final String TASK_GROUPS_JAVASCRIPT = "customGroup";
        final String TASK_PRIORITY_JAVASCRIPT = "10";
        final String TASK_DESCRIPTION_JAVASCRIPT = "Task05 description\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_CREATED_BY_JAVASCRIPT = "customUser";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action\");";

        final String TASK_NAME_MVEL = "Task08 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Task08 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_TASK_NAME_MVEL = "Task08ImplExecName";
        final String TASK_SUBJECT_MVEL = "subject08 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_ACTORS_MVEL = "customUser";
        final String TASK_GROUPS_MVEL = "customGroup";
        final String TASK_PRIORITY_MVEL = "10";
        final String TASK_DESCRIPTION_MVEL = "Task08 description\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_CREATED_BY_MVEL = "customUser";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action\");";

        final String TASK_DATA_INPUT_OUTPUT_JAVA = "|input:String,Skippable:Object,GroupId:Object,Comment:Object,Description:Object,Priority:Object,CreatedBy:Object,Content:Object||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";
        final String TASK_DATA_INPUT_OUTPUT_JAVASCRIPT = "|input:String,Skippable:Object,GroupId:Object,Comment:Object,Description:Object,Priority:Object,CreatedBy:Object,Content:Object||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";
        final String TASK_DATA_INPUT_OUTPUT_MVEL = "|input:String,Skippable:Object,GroupId:Object,Comment:Object,Description:Object,Priority:Object,CreatedBy:Object,Content:Object||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        final String TASK_CONTENT_JAVA = "Content example. Top level. One income. Java.";
        final String TASK_CONTENT_JAVASCRIPT = "Content example. Top level. One income. Javascript.";
        final String TASK_CONTENT_MVEL = "Content example. Top level. One income. MVEL.";
        final String SLA_DUE_DATE = "25/12/1983";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        UserTask filledTopLevelTaskJava = getTaskNodeById(getDiagram(),
                                                          FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVA_ID,
                                                          ONE_INCOME_EDGE,
                                                          HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertUserTaskExecutionSet(filledTopLevelTaskJava.getExecutionSet(),
                                   TASK_TASK_NAME_JAVA,
                                   TASK_SUBJECT_JAVA,
                                   TASK_ACTORS_JAVA,
                                   TASK_GROUPS_JAVA,
                                   IS_ASYNC,
                                   IS_SKIPPABLE,
                                   TASK_PRIORITY_JAVA,
                                   TASK_DESCRIPTION_JAVA,
                                   TASK_CREATED_BY_JAVA,
                                   AD_HOC_AUTOSTART,
                                   TASK_DATA_INPUT_OUTPUT_JAVA,
                                   TASK_ON_ENTRY_ACTION_JAVA,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   TASK_ON_EXIT_ACTION_JAVA,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   TASK_CONTENT_JAVA,
                                   SLA_DUE_DATE);

        UserTask filledTopLevelTaskJavascript = getTaskNodeById(getDiagram(),
                                                                FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVASCRIPT_ID,
                                                                ONE_INCOME_EDGE,
                                                                HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertUserTaskExecutionSet(filledTopLevelTaskJavascript.getExecutionSet(),
                                   TASK_TASK_NAME_JAVASCRIPT,
                                   TASK_SUBJECT_JAVASCRIPT,
                                   TASK_ACTORS_JAVASCRIPT,
                                   TASK_GROUPS_JAVASCRIPT,
                                   IS_ASYNC,
                                   IS_SKIPPABLE,
                                   TASK_PRIORITY_JAVASCRIPT,
                                   TASK_DESCRIPTION_JAVASCRIPT,
                                   TASK_CREATED_BY_JAVASCRIPT,
                                   AD_HOC_AUTOSTART,
                                   TASK_DATA_INPUT_OUTPUT_JAVASCRIPT,
                                   TASK_ON_ENTRY_ACTION_JAVASCRIPT,
                                   TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                   TASK_ON_EXIT_ACTION_JAVASCRIPT,
                                   TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                   TASK_CONTENT_JAVASCRIPT,
                                   SLA_DUE_DATE);

        UserTask filledTopLevelTaskMvel = getTaskNodeById(getDiagram(),
                                                          FILLED_ONE_INCOME_TOP_LEVEL_TASK_MVEL_ID,
                                                          ONE_INCOME_EDGE,
                                                          HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertUserTaskExecutionSet(filledTopLevelTaskMvel.getExecutionSet(),
                                   TASK_TASK_NAME_MVEL,
                                   TASK_SUBJECT_MVEL,
                                   TASK_ACTORS_MVEL,
                                   TASK_GROUPS_MVEL,
                                   IS_ASYNC,
                                   IS_SKIPPABLE,
                                   TASK_PRIORITY_MVEL,
                                   TASK_DESCRIPTION_MVEL,
                                   TASK_CREATED_BY_MVEL,
                                   AD_HOC_AUTOSTART,
                                   TASK_DATA_INPUT_OUTPUT_MVEL,
                                   TASK_ON_ENTRY_ACTION_MVEL,
                                   TASK_SCRIPT_MVEL_LANGUAGE,
                                   TASK_ON_EXIT_ACTION_MVEL,
                                   TASK_SCRIPT_MVEL_LANGUAGE,
                                   TASK_CONTENT_MVEL,
                                   SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskTwoIncomesFilledProperties() {
        final String TASK_NAME_JAVA = "Task03 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Task03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_TASK_NAME_JAVA = "Task03ImplExecName";
        final String TASK_SUBJECT_JAVA = "subject03 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_ACTORS_JAVA = "customUser";
        final String TASK_GROUPS_JAVA = "customGroup";
        final String TASK_PRIORITY_JAVA = "10";
        final String TASK_DESCRIPTION_JAVA = "Task03 description\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_CREATED_BY_JAVA = "customUser";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action\");";

        final String TASK_NAME_JAVASCRIPT = "Task06 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Task06 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_TASK_NAME_JAVASCRIPT = "Task06ImplExecName";
        final String TASK_SUBJECT_JAVASCRIPT = "subject06 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_ACTORS_JAVASCRIPT = "customUser";
        final String TASK_GROUPS_JAVASCRIPT = "customGroup";
        final String TASK_PRIORITY_JAVASCRIPT = "10";
        final String TASK_DESCRIPTION_JAVASCRIPT = "Task06 description\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_CREATED_BY_JAVASCRIPT = "customUser";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action\");";

        final String TASK_NAME_MVEL = "Task09 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Task09 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_TASK_NAME_MVEL = "Task09ImplExecName";
        final String TASK_SUBJECT_MVEL = "subject09 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_ACTORS_MVEL = "customUser";
        final String TASK_GROUPS_MVEL = "customGroup";
        final String TASK_PRIORITY_MVEL = "10";
        final String TASK_DESCRIPTION_MVEL = "Task09 description\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_CREATED_BY_MVEL = "customUser";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action\");";

        final String TASK_DATA_INPUT_OUTPUT_JAVA = "|input:String,Skippable:Object,GroupId:Object,Comment:Object,Description:Object,Priority:Object,CreatedBy:Object,Content:Object||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";
        final String TASK_DATA_INPUT_OUTPUT_JAVASCRIPT = "|input:String,Skippable:Object,GroupId:Object,Comment:Object,Description:Object,Priority:Object,CreatedBy:Object,Content:Object||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";
        final String TASK_DATA_INPUT_OUTPUT_MVEL = "|input:String,Skippable:Object,GroupId:Object,Comment:Object,Description:Object,Priority:Object,CreatedBy:Object,Content:Object||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        final String TASK_CONTENT_JAVA = "Content example. Top level. Two incomes. Java.";
        final String TASK_CONTENT_JAVASCRIPT = "Content example. Top level. Two incomes. Javascript.";
        final String TASK_CONTENT_MVEL = "Content example. Top level. Two incomes. MVEL.";
        final String SLA_DUE_DATE = "25/12/1983";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        UserTask filledTopLevelTaskJava = getTaskNodeById(getDiagram(),
                                                          FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVA_ID,
                                                          TWO_INCOME_EDGES,
                                                          HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertUserTaskExecutionSet(filledTopLevelTaskJava.getExecutionSet(),
                                   TASK_TASK_NAME_JAVA,
                                   TASK_SUBJECT_JAVA,
                                   TASK_ACTORS_JAVA,
                                   TASK_GROUPS_JAVA,
                                   IS_ASYNC,
                                   IS_SKIPPABLE,
                                   TASK_PRIORITY_JAVA,
                                   TASK_DESCRIPTION_JAVA,
                                   TASK_CREATED_BY_JAVA,
                                   AD_HOC_AUTOSTART,
                                   TASK_DATA_INPUT_OUTPUT_JAVA,
                                   TASK_ON_ENTRY_ACTION_JAVA,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   TASK_ON_EXIT_ACTION_JAVA,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   TASK_CONTENT_JAVA,
                                   SLA_DUE_DATE);

        UserTask filledTopLevelTaskJavascript = getTaskNodeById(getDiagram(),
                                                                FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVASCRIPT_ID,
                                                                TWO_INCOME_EDGES,
                                                                HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertUserTaskExecutionSet(filledTopLevelTaskJavascript.getExecutionSet(),
                                   TASK_TASK_NAME_JAVASCRIPT,
                                   TASK_SUBJECT_JAVASCRIPT,
                                   TASK_ACTORS_JAVASCRIPT,
                                   TASK_GROUPS_JAVASCRIPT,
                                   IS_ASYNC,
                                   IS_SKIPPABLE,
                                   TASK_PRIORITY_JAVASCRIPT,
                                   TASK_DESCRIPTION_JAVASCRIPT,
                                   TASK_CREATED_BY_JAVASCRIPT,
                                   AD_HOC_AUTOSTART,
                                   TASK_DATA_INPUT_OUTPUT_JAVASCRIPT,
                                   TASK_ON_ENTRY_ACTION_JAVASCRIPT,
                                   TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                   TASK_ON_EXIT_ACTION_JAVASCRIPT,
                                   TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                   TASK_CONTENT_JAVASCRIPT,
                                   SLA_DUE_DATE);

        UserTask filledTopLevelTaskMvel = getTaskNodeById(getDiagram(),
                                                          FILLED_TWO_INCOMES_TOP_LEVEL_TASK_MVEL_ID,
                                                          TWO_INCOME_EDGES,
                                                          HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertUserTaskExecutionSet(filledTopLevelTaskMvel.getExecutionSet(),
                                   TASK_TASK_NAME_MVEL,
                                   TASK_SUBJECT_MVEL,
                                   TASK_ACTORS_MVEL,
                                   TASK_GROUPS_MVEL,
                                   IS_ASYNC,
                                   IS_SKIPPABLE,
                                   TASK_PRIORITY_MVEL,
                                   TASK_DESCRIPTION_MVEL,
                                   TASK_CREATED_BY_MVEL,
                                   AD_HOC_AUTOSTART,
                                   TASK_DATA_INPUT_OUTPUT_MVEL,
                                   TASK_ON_ENTRY_ACTION_MVEL,
                                   TASK_SCRIPT_MVEL_LANGUAGE,
                                   TASK_ON_EXIT_ACTION_MVEL,
                                   TASK_SCRIPT_MVEL_LANGUAGE,
                                   TASK_CONTENT_MVEL,
                                   SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskEmptyProperties() {
        // cannot be empty
        final String TASK_NAME = "Task_4";
        // cannot be empty
        final String TASK_TASK_NAME = "Task";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        UserTask emptySubprocessLevelTask = getTaskNodeById(getDiagram(),
                                                            EMPTY_SUBPROCESS_LEVEL_TASK_ID,
                                                            ZERO_INCOME_EDGES,
                                                            HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(emptySubprocessLevelTask.getGeneral(), TASK_NAME, EMPTY_VALUE);
        assertUserTaskExecutionSet(emptySubprocessLevelTask.getExecutionSet(),
                                   TASK_TASK_NAME,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   IS_NOT_ASYNC,
                                   IS_NOT_SKIPPABLE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   NOT_AD_HOC_AUTOSTART,
                                   EMPTY_TASK_DATA_INPUT_OUTPUT,
                                   EMPTY_VALUE,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   EMPTY_VALUE,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE
        );
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskOneIncomeEmptyProperties() {
        // cannot be empty
        final String TASK_NAME = "Task_5";
        // cannot be empty
        final String TASK_TASK_NAME = "Task";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        UserTask emptySubprocessLevelTask = getTaskNodeById(getDiagram(),
                                                            EMPTY_ONE_INCOME_SUBPROCESS_LEVEL_TASK_ID,
                                                            ONE_INCOME_EDGE,
                                                            HAS_OUTCOME_EDGE);
        assertGeneralSet(emptySubprocessLevelTask.getGeneral(), TASK_NAME, EMPTY_VALUE);
        assertUserTaskExecutionSet(emptySubprocessLevelTask.getExecutionSet(),
                                   TASK_TASK_NAME,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   IS_NOT_ASYNC,
                                   IS_NOT_SKIPPABLE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   NOT_AD_HOC_AUTOSTART,
                                   EMPTY_TASK_DATA_INPUT_OUTPUT,
                                   EMPTY_VALUE,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   EMPTY_VALUE,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE
        );
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskTwoIncomesEmptyProperties() {
        // cannot be empty
        final String TASK_NAME = "Task_6";
        // cannot be empty
        final String TASK_TASK_NAME = "Task";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        UserTask emptySubprocessLevelTask = getTaskNodeById(getDiagram(),
                                                            EMPTY_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_ID,
                                                            TWO_INCOME_EDGES,
                                                            HAS_OUTCOME_EDGE);
        assertGeneralSet(emptySubprocessLevelTask.getGeneral(), TASK_NAME, EMPTY_VALUE);
        assertUserTaskExecutionSet(emptySubprocessLevelTask.getExecutionSet(),
                                   TASK_TASK_NAME,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   IS_NOT_ASYNC,
                                   IS_NOT_SKIPPABLE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE,
                                   NOT_AD_HOC_AUTOSTART,
                                   EMPTY_TASK_DATA_INPUT_OUTPUT,
                                   EMPTY_VALUE,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   EMPTY_VALUE,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   EMPTY_VALUE,
                                   EMPTY_VALUE
        );
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskFilledProperties() {
        final String TASK_NAME_JAVA = "Task10 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Task10 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_TASK_NAME_JAVA = "Task10ImplExecName";
        final String TASK_SUBJECT_JAVA = "subject10 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_ACTORS_JAVA = "customUser";
        final String TASK_GROUPS_JAVA = "customGroup";
        final String TASK_PRIORITY_JAVA = "10";
        final String TASK_DESCRIPTION_JAVA = "Task10 description\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_CREATED_BY_JAVA = "customUser";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action\");";

        final String TASK_NAME_JAVASCRIPT = "Task13 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Task13 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_TASK_NAME_JAVASCRIPT = "Task13ImplExecName";
        final String TASK_SUBJECT_JAVASCRIPT = "subject13 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_ACTORS_JAVASCRIPT = "customUser";
        final String TASK_GROUPS_JAVASCRIPT = "customGroup";
        final String TASK_PRIORITY_JAVASCRIPT = "10";
        final String TASK_DESCRIPTION_JAVASCRIPT = "Task13 description\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_CREATED_BY_JAVASCRIPT = "customUser";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action\");";

        final String TASK_NAME_MVEL = "Task16 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Task16 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_TASK_NAME_MVEL = "Task16ImplExecName";
        final String TASK_SUBJECT_MVEL = "subject16 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_ACTORS_MVEL = "customUser";
        final String TASK_GROUPS_MVEL = "customGroup";
        final String TASK_PRIORITY_MVEL = "10";
        final String TASK_DESCRIPTION_MVEL = "Task16 description\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_CREATED_BY_MVEL = "customUser";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action\");";

        final String TASK_DATA_INPUT_OUTPUT_JAVA = "|input:String,Skippable:Object,GroupId:Object,Comment:Object,Description:Object,Priority:Object,CreatedBy:Object,Content:Object||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";
        final String TASK_DATA_INPUT_OUTPUT_JAVASCRIPT = "|input:String,Skippable:Object,GroupId:Object,Comment:Object,Description:Object,Priority:Object,CreatedBy:Object,Content:Object||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";
        final String TASK_DATA_INPUT_OUTPUT_MVEL = "|input:String,Skippable:Object,GroupId:Object,Comment:Object,Description:Object,Priority:Object,CreatedBy:Object,Content:Object||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        final String TASK_CONTENT_JAVA = "Content example. Sub-Process level. Java.";
        final String TASK_CONTENT_JAVASCRIPT = "Content example. Sub-Process level. Javascript.";
        final String TASK_CONTENT_MVEL = "Content example. Sub-Process level. MVEL.";
        final String SLA_DUE_DATE = "25/12/1983";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        UserTask filledSubprocessLevelTaskJava = getTaskNodeById(getDiagram(),
                                                                 FILLED_SUBPROCESS_LEVEL_TASK_JAVA_ID,
                                                                 ZERO_INCOME_EDGES,
                                                                 HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertUserTaskExecutionSet(filledSubprocessLevelTaskJava.getExecutionSet(),
                                   TASK_TASK_NAME_JAVA,
                                   TASK_SUBJECT_JAVA,
                                   TASK_ACTORS_JAVA,
                                   TASK_GROUPS_JAVA,
                                   IS_ASYNC,
                                   IS_SKIPPABLE,
                                   TASK_PRIORITY_JAVA,
                                   TASK_DESCRIPTION_JAVA,
                                   TASK_CREATED_BY_JAVA,
                                   AD_HOC_AUTOSTART,
                                   TASK_DATA_INPUT_OUTPUT_JAVA,
                                   TASK_ON_ENTRY_ACTION_JAVA,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   TASK_ON_EXIT_ACTION_JAVA,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   TASK_CONTENT_JAVA,
                                   SLA_DUE_DATE);

        UserTask filledSubprocessLevelTaskJavascript = getTaskNodeById(getDiagram(),
                                                                       FILLED_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID,
                                                                       ZERO_INCOME_EDGES,
                                                                       HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertUserTaskExecutionSet(filledSubprocessLevelTaskJavascript.getExecutionSet(),
                                   TASK_TASK_NAME_JAVASCRIPT,
                                   TASK_SUBJECT_JAVASCRIPT,
                                   TASK_ACTORS_JAVASCRIPT,
                                   TASK_GROUPS_JAVASCRIPT,
                                   IS_ASYNC,
                                   IS_SKIPPABLE,
                                   TASK_PRIORITY_JAVASCRIPT,
                                   TASK_DESCRIPTION_JAVASCRIPT,
                                   TASK_CREATED_BY_JAVASCRIPT,
                                   AD_HOC_AUTOSTART,
                                   TASK_DATA_INPUT_OUTPUT_JAVASCRIPT,
                                   TASK_ON_ENTRY_ACTION_JAVASCRIPT,
                                   TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                   TASK_ON_EXIT_ACTION_JAVASCRIPT,
                                   TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                   TASK_CONTENT_JAVASCRIPT,
                                   SLA_DUE_DATE);

        UserTask filledSubprocessLevelTaskMvel = getTaskNodeById(getDiagram(),
                                                                 FILLED_SUBPROCESS_LEVEL_TASK_MVEL_ID,
                                                                 ZERO_INCOME_EDGES,
                                                                 HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertUserTaskExecutionSet(filledSubprocessLevelTaskMvel.getExecutionSet(),
                                   TASK_TASK_NAME_MVEL,
                                   TASK_SUBJECT_MVEL,
                                   TASK_ACTORS_MVEL,
                                   TASK_GROUPS_MVEL,
                                   IS_ASYNC,
                                   IS_SKIPPABLE,
                                   TASK_PRIORITY_MVEL,
                                   TASK_DESCRIPTION_MVEL,
                                   TASK_CREATED_BY_MVEL,
                                   AD_HOC_AUTOSTART,
                                   TASK_DATA_INPUT_OUTPUT_MVEL,
                                   TASK_ON_ENTRY_ACTION_MVEL,
                                   TASK_SCRIPT_MVEL_LANGUAGE,
                                   TASK_ON_EXIT_ACTION_MVEL,
                                   TASK_SCRIPT_MVEL_LANGUAGE,
                                   TASK_CONTENT_MVEL,
                                   SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskOneIncomeFilledProperties() {
        final String TASK_NAME_JAVA = "Task11 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Task11 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_TASK_NAME_JAVA = "Task11ImplExecName";
        final String TASK_SUBJECT_JAVA = "subject11 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_ACTORS_JAVA = "customUser";
        final String TASK_GROUPS_JAVA = "customGroup";
        final String TASK_PRIORITY_JAVA = "10";
        final String TASK_DESCRIPTION_JAVA = "Task11 description\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_CREATED_BY_JAVA = "customUser";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action\");";

        final String TASK_NAME_JAVASCRIPT = "Task14 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Task14 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_TASK_NAME_JAVASCRIPT = "Task14ImplExecName";
        final String TASK_SUBJECT_JAVASCRIPT = "subject14 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_ACTORS_JAVASCRIPT = "customUser";
        final String TASK_GROUPS_JAVASCRIPT = "customGroup";
        final String TASK_PRIORITY_JAVASCRIPT = "10";
        final String TASK_DESCRIPTION_JAVASCRIPT = "Task14 description\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_CREATED_BY_JAVASCRIPT = "customUser";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action\");";

        final String TASK_NAME_MVEL = "Task17 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Task17 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_TASK_NAME_MVEL = "Task17ImplExecName";
        final String TASK_SUBJECT_MVEL = "subject17 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_ACTORS_MVEL = "customUser";
        final String TASK_GROUPS_MVEL = "customGroup";
        final String TASK_PRIORITY_MVEL = "10";
        final String TASK_DESCRIPTION_MVEL = "Task17 description\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_CREATED_BY_MVEL = "customUser";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action\");";

        final String TASK_DATA_INPUT_OUTPUT_JAVA = "|input:String,Skippable:Object,GroupId:Object,Comment:Object,Description:Object,Priority:Object,CreatedBy:Object,Content:Object||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";
        final String TASK_DATA_INPUT_OUTPUT_JAVASCRIPT = "|input:String,Skippable:Object,GroupId:Object,Comment:Object,Description:Object,Priority:Object,CreatedBy:Object,Content:Object||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";
        final String TASK_DATA_INPUT_OUTPUT_MVEL = "|input:String,Skippable:Object,GroupId:Object,Comment:Object,Description:Object,Priority:Object,CreatedBy:Object,Content:Object||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        final String TASK_CONTENT_JAVA = "Content example. Sub-Process level. One income. Java.";
        final String TASK_CONTENT_JAVASCRIPT = "Content example. Sub-Process level. One income. Javascript.";
        final String TASK_CONTENT_MVEL = "Content example. Sub-Process level. One income. MVEL.";
        final String SLA_DUE_DATE = "25/12/1983";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        UserTask filledSubprocessLevelTaskJava = getTaskNodeById(getDiagram(),
                                                                 FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVA_ID,
                                                                 ONE_INCOME_EDGE,
                                                                 HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertUserTaskExecutionSet(filledSubprocessLevelTaskJava.getExecutionSet(),
                                   TASK_TASK_NAME_JAVA,
                                   TASK_SUBJECT_JAVA,
                                   TASK_ACTORS_JAVA,
                                   TASK_GROUPS_JAVA,
                                   IS_ASYNC,
                                   IS_SKIPPABLE,
                                   TASK_PRIORITY_JAVA,
                                   TASK_DESCRIPTION_JAVA,
                                   TASK_CREATED_BY_JAVA,
                                   AD_HOC_AUTOSTART,
                                   TASK_DATA_INPUT_OUTPUT_JAVA,
                                   TASK_ON_ENTRY_ACTION_JAVA,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   TASK_ON_EXIT_ACTION_JAVA,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   TASK_CONTENT_JAVA,
                                   SLA_DUE_DATE);

        UserTask filledSubprocessLevelTaskJavascript = getTaskNodeById(getDiagram(),
                                                                       FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID,
                                                                       ONE_INCOME_EDGE,
                                                                       HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertUserTaskExecutionSet(filledSubprocessLevelTaskJavascript.getExecutionSet(),
                                   TASK_TASK_NAME_JAVASCRIPT,
                                   TASK_SUBJECT_JAVASCRIPT,
                                   TASK_ACTORS_JAVASCRIPT,
                                   TASK_GROUPS_JAVASCRIPT,
                                   IS_ASYNC,
                                   IS_SKIPPABLE,
                                   TASK_PRIORITY_JAVASCRIPT,
                                   TASK_DESCRIPTION_JAVASCRIPT,
                                   TASK_CREATED_BY_JAVASCRIPT,
                                   AD_HOC_AUTOSTART,
                                   TASK_DATA_INPUT_OUTPUT_JAVASCRIPT,
                                   TASK_ON_ENTRY_ACTION_JAVASCRIPT,
                                   TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                   TASK_ON_EXIT_ACTION_JAVASCRIPT,
                                   TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                   TASK_CONTENT_JAVASCRIPT,
                                   SLA_DUE_DATE);

        UserTask filledSubprocessLevelTaskMvel = getTaskNodeById(getDiagram(),
                                                                 FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_MVEL_ID,
                                                                 ONE_INCOME_EDGE,
                                                                 HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertUserTaskExecutionSet(filledSubprocessLevelTaskMvel.getExecutionSet(),
                                   TASK_TASK_NAME_MVEL,
                                   TASK_SUBJECT_MVEL,
                                   TASK_ACTORS_MVEL,
                                   TASK_GROUPS_MVEL,
                                   IS_ASYNC,
                                   IS_SKIPPABLE,
                                   TASK_PRIORITY_MVEL,
                                   TASK_DESCRIPTION_MVEL,
                                   TASK_CREATED_BY_MVEL,
                                   AD_HOC_AUTOSTART,
                                   TASK_DATA_INPUT_OUTPUT_MVEL,
                                   TASK_ON_ENTRY_ACTION_MVEL,
                                   TASK_SCRIPT_MVEL_LANGUAGE,
                                   TASK_ON_EXIT_ACTION_MVEL,
                                   TASK_SCRIPT_MVEL_LANGUAGE,
                                   TASK_CONTENT_MVEL,
                                   SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskTwoIncomesFilledProperties() {
        final String TASK_NAME_JAVA = "Task12 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Task12 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_TASK_NAME_JAVA = "Task12ImplExecName";
        final String TASK_SUBJECT_JAVA = "subject12 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_ACTORS_JAVA = "customUser";
        final String TASK_GROUPS_JAVA = "customGroup";
        final String TASK_PRIORITY_JAVA = "10";
        final String TASK_DESCRIPTION_JAVA = "Task12 description\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_CREATED_BY_JAVA = "customUser";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action\");";

        final String TASK_NAME_JAVASCRIPT = "Task15 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Task15 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_TASK_NAME_JAVASCRIPT = "Task15ImplExecName";
        final String TASK_SUBJECT_JAVASCRIPT = "subject15 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_ACTORS_JAVASCRIPT = "customUser";
        final String TASK_GROUPS_JAVASCRIPT = "customGroup";
        final String TASK_PRIORITY_JAVASCRIPT = "10";
        final String TASK_DESCRIPTION_JAVASCRIPT = "Task15 description\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_CREATED_BY_JAVASCRIPT = "customUser";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action\");";

        final String TASK_NAME_MVEL = "Task18 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Task18 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_TASK_NAME_MVEL = "Task18ImplExecName";
        final String TASK_SUBJECT_MVEL = "subject18 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_ACTORS_MVEL = "customUser";
        final String TASK_GROUPS_MVEL = "customGroup";
        final String TASK_PRIORITY_MVEL = "10";
        final String TASK_DESCRIPTION_MVEL = "Task18 description\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_CREATED_BY_MVEL = "customUser";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action\");";

        final String TASK_DATA_INPUT_OUTPUT_JAVA = "|input:String,Skippable:Object,GroupId:Object,Comment:Object,Description:Object,Priority:Object,CreatedBy:Object,Content:Object||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";
        final String TASK_DATA_INPUT_OUTPUT_JAVASCRIPT = "|input:String,Skippable:Object,GroupId:Object,Comment:Object,Description:Object,Priority:Object,CreatedBy:Object,Content:Object||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";
        final String TASK_DATA_INPUT_OUTPUT_MVEL = "|input:String,Skippable:Object,GroupId:Object,Comment:Object,Description:Object,Priority:Object,CreatedBy:Object,Content:Object||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        final String TASK_CONTENT_JAVA = "Content example. Sub-Process level. Two incomes. Java.";
        final String TASK_CONTENT_JAVASCRIPT = "Content example. Sub-Process level. Two incomes. Javascript.";
        final String TASK_CONTENT_MVEL = "Content example. Sub-Process level. Two incomes. MVEL.";
        final String SLA_DUE_DATE = "25/12/1983";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        UserTask filledSubprocessLevelTaskJava = getTaskNodeById(getDiagram(),
                                                                 FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVA_ID,
                                                                 TWO_INCOME_EDGES,
                                                                 HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertUserTaskExecutionSet(filledSubprocessLevelTaskJava.getExecutionSet(),
                                   TASK_TASK_NAME_JAVA,
                                   TASK_SUBJECT_JAVA,
                                   TASK_ACTORS_JAVA,
                                   TASK_GROUPS_JAVA,
                                   IS_ASYNC,
                                   IS_SKIPPABLE,
                                   TASK_PRIORITY_JAVA,
                                   TASK_DESCRIPTION_JAVA,
                                   TASK_CREATED_BY_JAVA,
                                   AD_HOC_AUTOSTART,
                                   TASK_DATA_INPUT_OUTPUT_JAVA,
                                   TASK_ON_ENTRY_ACTION_JAVA,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   TASK_ON_EXIT_ACTION_JAVA,
                                   TASK_SCRIPT_JAVA_LANGUAGE,
                                   TASK_CONTENT_JAVA,
                                   SLA_DUE_DATE);

        UserTask filledSubprocessLevelTaskJavascript = getTaskNodeById(getDiagram(),
                                                                       FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID,
                                                                       TWO_INCOME_EDGES,
                                                                       HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertUserTaskExecutionSet(filledSubprocessLevelTaskJavascript.getExecutionSet(),
                                   TASK_TASK_NAME_JAVASCRIPT,
                                   TASK_SUBJECT_JAVASCRIPT,
                                   TASK_ACTORS_JAVASCRIPT,
                                   TASK_GROUPS_JAVASCRIPT,
                                   IS_ASYNC,
                                   IS_SKIPPABLE,
                                   TASK_PRIORITY_JAVASCRIPT,
                                   TASK_DESCRIPTION_JAVASCRIPT,
                                   TASK_CREATED_BY_JAVASCRIPT,
                                   AD_HOC_AUTOSTART,
                                   TASK_DATA_INPUT_OUTPUT_JAVASCRIPT,
                                   TASK_ON_ENTRY_ACTION_JAVASCRIPT,
                                   TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                   TASK_ON_EXIT_ACTION_JAVASCRIPT,
                                   TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                   TASK_CONTENT_JAVASCRIPT,
                                   SLA_DUE_DATE);

        UserTask filledSubprocessLevelTaskMvel = getTaskNodeById(getDiagram(),
                                                                 FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_MVEL_ID,
                                                                 TWO_INCOME_EDGES,
                                                                 HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertUserTaskExecutionSet(filledSubprocessLevelTaskMvel.getExecutionSet(),
                                   TASK_TASK_NAME_MVEL,
                                   TASK_SUBJECT_MVEL,
                                   TASK_ACTORS_MVEL,
                                   TASK_GROUPS_MVEL,
                                   IS_ASYNC,
                                   IS_SKIPPABLE,
                                   TASK_PRIORITY_MVEL,
                                   TASK_DESCRIPTION_MVEL,
                                   TASK_CREATED_BY_MVEL,
                                   AD_HOC_AUTOSTART,
                                   TASK_DATA_INPUT_OUTPUT_MVEL,
                                   TASK_ON_ENTRY_ACTION_MVEL,
                                   TASK_SCRIPT_MVEL_LANGUAGE,
                                   TASK_ON_EXIT_ACTION_MVEL,
                                   TASK_SCRIPT_MVEL_LANGUAGE,
                                   TASK_CONTENT_MVEL,
                                   SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testMarshallTopLevelTaskFilledProperties() {
        checkTaskMarshalling(FILLED_TOP_LEVEL_TASK_JAVA_ID, ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_TOP_LEVEL_TASK_JAVASCRIPT_ID, ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_TOP_LEVEL_TASK_MVEL_ID, ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelTaskFilledProperties() {
        checkTaskMarshalling(FILLED_SUBPROCESS_LEVEL_TASK_JAVA_ID, ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID, ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_SUBPROCESS_LEVEL_TASK_MVEL_ID, ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
    }

    @Test
    @Override
    public void testMarshallTopLevelTaskOneIncomeFilledProperties() {
        checkTaskMarshalling(FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVA_ID, ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVASCRIPT_ID, ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_ONE_INCOME_TOP_LEVEL_TASK_MVEL_ID, ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelTaskOneIncomeFilledProperties() {
        checkTaskMarshalling(FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVA_ID, ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID, ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_MVEL_ID, ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
    }

    @Test
    @Override
    public void testMarshallTopLevelTaskTwoIncomesFilledProperties() {
        checkTaskMarshalling(FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVA_ID, TWO_INCOME_EDGES, HAS_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVASCRIPT_ID, TWO_INCOME_EDGES, HAS_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_TWO_INCOMES_TOP_LEVEL_TASK_MVEL_ID, TWO_INCOME_EDGES, HAS_OUTCOME_EDGE);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelTaskTwoIncomesFilledProperties() {
        checkTaskMarshalling(FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVA_ID, TWO_INCOME_EDGES, HAS_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID, TWO_INCOME_EDGES, HAS_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_MVEL_ID, TWO_INCOME_EDGES, HAS_OUTCOME_EDGE);
    }

    @Override
    String getBpmnTaskFilePath() {
        return BPMN_TASK_FILE_PATH;
    }

    @Override
    Class<UserTask> getTaskType() {
        return UserTask.class;
    }

    @Override
    String getFilledTopLevelTaskId() {
        // There are several Filled tasks, test method is overwritten
        return null;
    }

    @Override
    String getEmptyTopLevelTaskId() {
        return EMPTY_TOP_LEVEL_TASK_ID;
    }

    @Override
    String getFilledSubprocessLevelTaskId() {
        // There are several Filled tasks, test method is overwritten
        return null;
    }

    @Override
    String getEmptySubprocessLevelTaskId() {
        return EMPTY_SUBPROCESS_LEVEL_TASK_ID;
    }

    @Override
    String getFilledTopLevelTaskOneIncomeId() {
        // There are several Filled tasks, test method is overwritten
        return null;
    }

    @Override
    String getEmptyTopLevelTaskOneIncomeId() {
        return EMPTY_ONE_INCOME_TOP_LEVEL_TASK_ID;
    }

    @Override
    String getFilledSubprocessLevelTaskOneIncomeId() {
        // There are several Filled tasks, test method is overwritten
        return null;
    }

    @Override
    String getEmptySubprocessLevelTaskOneIncomeId() {
        return EMPTY_ONE_INCOME_SUBPROCESS_LEVEL_TASK_ID;
    }

    @Override
    String getFilledTopLevelTaskTwoIncomesId() {
        // There are several Filled tasks, test method is overwritten
        return null;
    }

    @Override
    String getEmptyTopLevelTaskTwoIncomesId() {
        return EMPTY_TWO_INCOMES_TOP_LEVEL_TASK_ID;
    }

    @Override
    String getFilledSubprocessLevelTaskTwoIncomesId() {
        // There are several Filled tasks, test method is overwritten
        return null;
    }

    @Override
    String getEmptySubprocessLevelTaskTwoIncomesId() {
        return EMPTY_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_ID;
    }

    private void assertUserTaskExecutionSet(UserTaskExecutionSet executionSet,
                                            String taskName,
                                            String subject,
                                            String actors,
                                            String groupId,
                                            boolean isAsync,
                                            boolean skippable,
                                            String priority,
                                            String description,
                                            String createdBy,
                                            boolean adHocAutostart,
                                            String dataInputOutput,
                                            String onEntryActionScriptValue,
                                            String onEntryActionScriptLanguage,
                                            String onExitActionScriptValue,
                                            String onExitActionScriptLanguage,
                                            String content,
                                            String slaDueDate) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getTaskName());
        assertNotNull(executionSet.getSubject());
        assertNotNull(executionSet.getActors());
        assertNotNull(executionSet.getGroupid());
        assertNotNull(executionSet.getIsAsync());
        assertNotNull(executionSet.getSkippable());
        assertNotNull(executionSet.getPriority());
        assertNotNull(executionSet.getDescription());
        assertNotNull(executionSet.getCreatedBy());
        assertNotNull(executionSet.getAdHocAutostart());
        assertNotNull(executionSet.getAssignmentsinfo());

        assertNotNull(executionSet.getOnEntryAction());
        assertNotNull(executionSet.getOnExitAction());
        assertNotNull(executionSet.getOnEntryAction().getValue());
        assertNotNull(executionSet.getOnExitAction().getValue());

        List<ScriptTypeValue> onEntryScriptTypeValues = executionSet.getOnEntryAction().getValue().getValues();
        List<ScriptTypeValue> onExitScriptTypeValues = executionSet.getOnExitAction().getValue().getValues();

        assertNotNull(onEntryScriptTypeValues);
        assertNotNull(onExitScriptTypeValues);
        assertNotNull(onEntryScriptTypeValues.get(0));
        assertNotNull(onExitScriptTypeValues.get(0));

        assertNotNull(executionSet.getContent().getValue());
        assertNotNull(executionSet.getSlaDueDate().getValue());

        assertEquals(taskName, executionSet.getTaskName().getValue());
        assertEquals(subject, executionSet.getSubject().getValue());
        assertEquals(actors, executionSet.getActors().getValue());
        assertEquals(groupId, executionSet.getGroupid().getValue());
        assertEquals(isAsync, executionSet.getIsAsync().getValue());
        assertEquals(skippable, executionSet.getSkippable().getValue());
        assertEquals(priority, executionSet.getPriority().getValue());
        assertEquals(description, executionSet.getDescription().getValue());
        assertEquals(createdBy, executionSet.getCreatedBy().getValue());
        assertEquals(adHocAutostart, executionSet.getAdHocAutostart().getValue());
        assertEquals(dataInputOutput, executionSet.getAssignmentsinfo().getValue());

        assertEquals(onEntryActionScriptValue, onEntryScriptTypeValues.get(0).getScript());
        assertEquals(onEntryActionScriptLanguage, onEntryScriptTypeValues.get(0).getLanguage());
        assertEquals(onExitActionScriptValue, onExitScriptTypeValues.get(0).getScript());
        assertEquals(onExitActionScriptLanguage, onExitScriptTypeValues.get(0).getLanguage());

        assertEquals(content, executionSet.getContent().getValue());
        assertEquals(slaDueDate, executionSet.getSlaDueDate().getValue());
    }
}
