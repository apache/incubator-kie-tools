/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.subprocesses;

import java.util.List;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.assertj.core.api.Assertions.assertThat;

public class MultipleInstanceSubProcessTest extends SubProcessTest<MultipleInstanceSubprocess> {

    private static final String BPMN_SUB_PROCESS_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/multipleInstanceSubProcesses.bpmn";

    private static final String TOP_LEVEL_EMPTY_SUBPROCESS_ID = "_3F304098-64C6-4E53-A653-FDF893D88B3A";
    private static final String TOP_LEVEL_FILLED_SUBPROCESS_JAVA_ID = "_BB01C3A5-CEC6-4DE6-A1F7-CC4A8633E4CB";
    private static final String TOP_LEVEL_FILLED_SUBPROCESS_JAVASCRIPT_ID = "_F894F69F-A948-446A-A8F2-A072CDF046DD";
    private static final String TOP_LEVEL_FILLED_SUBPROCESS_MVEL_ID = "_AFA1E5FC-9DA4-4BD0-8269-CB99A7D6C3AB";

    private static final String SUBPROCESS_LEVEL_EMPTY_SUBPROCESS_ID = "_D8C4C140-CFE8-4013-92AF-C72A61CB1D64";
    private static final String SUBPROCESS_LEVEL_FILLED_SUBPROCESS_JAVA_ID = "_98379D8A-1DB6-416E-BFA0-DC4A09A695BD";
    private static final String SUBPROCESS_LEVEL_FILLED_SUBPROCESS_JAVASCRIPT_ID = "_CCD1EB1E-8BEB-471C-BAEE-2965F4C808EC";
    private static final String SUBPROCESS_LEVEL_FILLED_SUBPROCESS_MVEL_ID = "_BFDA818F-F2F2-4902-9707-E4838939B583";

    private static final String TOP_LEVEL_SUBPROCESS_WITH_EDGES = "_3815E005-22BB-44F1-A5DC-387FF966A1BD";
    private static final String SUBPROCESS_LEVEL_SUBPROCESS_WITH_EDGES = "_8B644D06-C140-4544-9E66-E5C00822411B";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 23;

    private static final String DEFAULT_MULTIPLE_INSTANCE_COLLECTION = null;
    private static final String MULTIPLE_INSTANCE_COLLECTION_INPUT = "miCollectionInput";
    private static final String MULTIPLE_INSTANCE_COLLECTION_OUTPUT = "miCollectionOutput";
    private static final String MULTIPLE_INSTANCE_DATA_INPUT = "collectionItemVarIn";
    private static final String MULTIPLE_INSTANCE_DATA_OUTPUT = "collectionItemVarOut";
    private static final String MULTIPLE_INSTANCE_COMPLETION_CONDITION = "($ in miCollectionOutput if $ == true).size() == miOutAmountRequired;";

    private static final String SUBPROCESS_SCRIPT_JAVA_LANGUAGE = "java";
    private static final String SUBPROCESS_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action\");\n" +
            "System.out.println(\"`&(^*&^(\\n\\r\");\n" +
            "Object o = kcontext.getVariable(\"hello_world\");";
    private static final String SUBPROCESS_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action\");\n" +
            "System.out.println(\"`&(^*&^(\\n\\r\");\n" +
            "Object o = kcontext.getVariable(\"hello_world\");";

    private static final String SUBPROCESS_SCRIPT_JAVASCRIPT_LANGUAGE = "javascript";
    private static final String SUBPROCESS_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action\");\n" +
            "print(\"~``!@#$%^&*()_+=-{}|\\n\\r][:\\\",.?\");\n" +
            "print(\"somevalue\" + \"~``!@#$%^&*()_+=-{}|\\n\\r][:\\\",.?\");";
    private static final String SUBPROCESS_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action\");\n" +
            "print(\"~``!@#$%^&*()_+=-{}|\\n\\r][:\\\",.?\");\n" +
            "print(\"somevalue\" + \"~``!@#$%^&*()_+=-{}|\\n\\r][:\\\",.?\");";

    private static final String SUBPROCESS_SCRIPT_MVEL_LANGUAGE = "mvel";
    private static final String SUBPROCESS_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action\");\n" +
            "System.out.println(\"`&(^*&^(\\n\\r\");\n" +
            "Object o = kcontext.getVariable(\"hello_world\");";
    private static final String SUBPROCESS_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action\");\n" +
            "System.out.println(\"`&(^*&^(\\n\\r\");\n" +
            "Object o = kcontext.getVariable(\"hello_world\");";

    private static final String SLA_DUE_DATE = "12/25/1983";

    public MultipleInstanceSubProcessTest() throws Exception {
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyPropertiesSubProcess() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        MultipleInstanceSubprocess topLevelSubProcess = getSubProcessNodeById(diagram,
                                                                              TOP_LEVEL_EMPTY_SUBPROCESS_ID,
                                                                              EMPTY_INCOME_EDGES,
                                                                              EMPTY_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcess.getGeneral(), DEFAULT_NAME, DEFAULT_DOCUMENTATION);
        assertMultipleInstanceSubProcessExecutionSet(topLevelSubProcess.getExecutionSet(),
                                                     DEFAULT_MULTIPLE_INSTANCE_COLLECTION,
                                                     DEFAULT_MULTIPLE_INSTANCE_COLLECTION,
                                                     EMPTY_VALUE,
                                                     EMPTY_VALUE,
                                                     EMPTY_VALUE,
                                                     IS_NOT_ASYNC,
                                                     EMPTY_VALUE,
                                                     SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                                     EMPTY_VALUE,
                                                     SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                                     EMPTY_VALUE);
        assertSubProcessProcessData(topLevelSubProcess.getProcessData(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelFilledPropertiesSubProcess() {
        final String SUB_PROCESS_NAME_JAVA = "Multiple Instance sub-process01 name ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION_JAVA = "Multiple Instance sub-process01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_NAME_JAVASCRIPT = "Multiple Instance sub-process02 name ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION_JAVASCRIPT = "Multiple Instance sub-process02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_NAME_MVEL = "Multiple Instance sub-process03 name ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION_MVEL = "Multiple Instance sub-process03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";

        final String SUB_PROCESS_VARIABLES_JAVA = "subVar01:String:false";
        final String SUB_PROCESS_VARIABLES_JAVASCRIPT = "subVar02:String:false";
        final String SUB_PROCESS_VARIABLES_MVEL = "subVarCustom:any.Custom:false,subVarBoolean:Boolean:false,subVarFloat:Float:false,subVarInteger:Integer:false,subVarObject:Object:false,subVarString:String:false";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        MultipleInstanceSubprocess topLevelSubProcessJava = getSubProcessNodeById(diagram,
                                                                                  TOP_LEVEL_FILLED_SUBPROCESS_JAVA_ID,
                                                                                  EMPTY_INCOME_EDGES,
                                                                                  EMPTY_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcessJava.getGeneral(), SUB_PROCESS_NAME_JAVA, SUB_PROCESS_DOCUMENTATION_JAVA);
        assertMultipleInstanceSubProcessExecutionSet(topLevelSubProcessJava.getExecutionSet(),
                                                     MULTIPLE_INSTANCE_COLLECTION_INPUT,
                                                     MULTIPLE_INSTANCE_COLLECTION_OUTPUT,
                                                     MULTIPLE_INSTANCE_DATA_INPUT,
                                                     MULTIPLE_INSTANCE_DATA_OUTPUT,
                                                     MULTIPLE_INSTANCE_COMPLETION_CONDITION,
                                                     IS_ASYNC,
                                                     SUBPROCESS_ON_ENTRY_ACTION_JAVA,
                                                     SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                                     SUBPROCESS_ON_EXIT_ACTION_JAVA,
                                                     SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                                     SLA_DUE_DATE);
        assertSubProcessProcessData(topLevelSubProcessJava.getProcessData(), SUB_PROCESS_VARIABLES_JAVA);

        MultipleInstanceSubprocess topLevelSubProcessJavascript = getSubProcessNodeById(diagram,
                                                                                        TOP_LEVEL_FILLED_SUBPROCESS_JAVASCRIPT_ID,
                                                                                        EMPTY_INCOME_EDGES,
                                                                                        EMPTY_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcessJavascript.getGeneral(), SUB_PROCESS_NAME_JAVASCRIPT, SUB_PROCESS_DOCUMENTATION_JAVASCRIPT);
        assertMultipleInstanceSubProcessExecutionSet(topLevelSubProcessJavascript.getExecutionSet(),
                                                     MULTIPLE_INSTANCE_COLLECTION_INPUT,
                                                     MULTIPLE_INSTANCE_COLLECTION_OUTPUT,
                                                     MULTIPLE_INSTANCE_DATA_INPUT,
                                                     MULTIPLE_INSTANCE_DATA_OUTPUT,
                                                     MULTIPLE_INSTANCE_COMPLETION_CONDITION,
                                                     IS_ASYNC,
                                                     SUBPROCESS_ON_ENTRY_ACTION_JAVASCRIPT,
                                                     SUBPROCESS_SCRIPT_JAVASCRIPT_LANGUAGE,
                                                     SUBPROCESS_ON_EXIT_ACTION_JAVASCRIPT,
                                                     SUBPROCESS_SCRIPT_JAVASCRIPT_LANGUAGE,
                                                     SLA_DUE_DATE);
        assertSubProcessProcessData(topLevelSubProcessJavascript.getProcessData(), SUB_PROCESS_VARIABLES_JAVASCRIPT);

        MultipleInstanceSubprocess topLevelSubProcessMVEL = getSubProcessNodeById(diagram,
                                                                                  TOP_LEVEL_FILLED_SUBPROCESS_MVEL_ID,
                                                                                  EMPTY_INCOME_EDGES,
                                                                                  EMPTY_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcessMVEL.getGeneral(), SUB_PROCESS_NAME_MVEL, SUB_PROCESS_DOCUMENTATION_MVEL);
        assertMultipleInstanceSubProcessExecutionSet(topLevelSubProcessMVEL.getExecutionSet(),
                                                     MULTIPLE_INSTANCE_COLLECTION_INPUT,
                                                     MULTIPLE_INSTANCE_COLLECTION_OUTPUT,
                                                     MULTIPLE_INSTANCE_DATA_INPUT,
                                                     MULTIPLE_INSTANCE_DATA_OUTPUT,
                                                     MULTIPLE_INSTANCE_COMPLETION_CONDITION,
                                                     IS_ASYNC,
                                                     SUBPROCESS_ON_ENTRY_ACTION_MVEL,
                                                     SUBPROCESS_SCRIPT_MVEL_LANGUAGE,
                                                     SUBPROCESS_ON_EXIT_ACTION_MVEL,
                                                     SUBPROCESS_SCRIPT_MVEL_LANGUAGE,
                                                     SLA_DUE_DATE);
        assertSubProcessProcessData(topLevelSubProcessMVEL.getProcessData(), SUB_PROCESS_VARIABLES_MVEL);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelSubProcessWithEdges() {
        final String SUB_PROCESS_NAME = "Multiple Instance Sub-process07";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        MultipleInstanceSubprocess topLevelSubProcess = getSubProcessNodeById(diagram,
                                                                              TOP_LEVEL_SUBPROCESS_WITH_EDGES,
                                                                              ONE_INCOME_EDGE,
                                                                              FOUR_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcess.getGeneral(), SUB_PROCESS_NAME, DEFAULT_DOCUMENTATION);
        assertMultipleInstanceSubProcessExecutionSet(topLevelSubProcess.getExecutionSet(),
                                                     DEFAULT_MULTIPLE_INSTANCE_COLLECTION,
                                                     DEFAULT_MULTIPLE_INSTANCE_COLLECTION,
                                                     EMPTY_VALUE,
                                                     EMPTY_VALUE,
                                                     EMPTY_VALUE,
                                                     IS_NOT_ASYNC,
                                                     EMPTY_VALUE,
                                                     SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                                     EMPTY_VALUE,
                                                     SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                                     EMPTY_VALUE);
        assertSubProcessProcessData(topLevelSubProcess.getProcessData(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubProcessLevelEmptyPropertiesSubProcess() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        MultipleInstanceSubprocess subProcessLevelSubProcess = getSubProcessNodeById(diagram,
                                                                                     SUBPROCESS_LEVEL_EMPTY_SUBPROCESS_ID,
                                                                                     EMPTY_INCOME_EDGES,
                                                                                     EMPTY_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcess.getGeneral(), DEFAULT_NAME, DEFAULT_DOCUMENTATION);
        assertMultipleInstanceSubProcessExecutionSet(subProcessLevelSubProcess.getExecutionSet(),
                                                     DEFAULT_MULTIPLE_INSTANCE_COLLECTION,
                                                     DEFAULT_MULTIPLE_INSTANCE_COLLECTION,
                                                     EMPTY_VALUE,
                                                     EMPTY_VALUE,
                                                     EMPTY_VALUE,
                                                     IS_NOT_ASYNC,
                                                     EMPTY_VALUE,
                                                     SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                                     EMPTY_VALUE,
                                                     SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                                     EMPTY_VALUE);
        assertSubProcessProcessData(subProcessLevelSubProcess.getProcessData(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubProcessLevelFilledPropertiesSubProcess() {
        final String SUB_PROCESS_NAME_JAVA = "Multiple Instance sub-process04 name ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION_JAVA = "Multiple Instance sub-process04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_NAME_JAVASCRIPT = "Multiple Instance sub-process05 name ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION_JAVASCRIPT = "Multiple Instance sub-process05 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_NAME_MVEL = "Multiple Instance sub-process06 name ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION_MVEL = "Multiple Instance sub-process06 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";

        final String SUB_PROCESS_VARIABLES_JAVA = "subVar04:String:false";
        final String SUB_PROCESS_VARIABLES_JAVASCRIPT = "subVar05:String:false";
        final String SUB_PROCESS_VARIABLES_MVEL = "subVarCustom:any.Custom:false,subVarBoolean:Boolean:false,subVarFloat:Float:false,subVarInteger:Integer:false,subVarObject:Object:false,subVarString:String:false";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        MultipleInstanceSubprocess subProcessLevelSubProcessJava = getSubProcessNodeById(diagram,
                                                                                         SUBPROCESS_LEVEL_FILLED_SUBPROCESS_JAVA_ID,
                                                                                         EMPTY_INCOME_EDGES,
                                                                                         EMPTY_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcessJava.getGeneral(), SUB_PROCESS_NAME_JAVA, SUB_PROCESS_DOCUMENTATION_JAVA);
        assertMultipleInstanceSubProcessExecutionSet(subProcessLevelSubProcessJava.getExecutionSet(),
                                                     MULTIPLE_INSTANCE_COLLECTION_INPUT,
                                                     MULTIPLE_INSTANCE_COLLECTION_OUTPUT,
                                                     MULTIPLE_INSTANCE_DATA_INPUT,
                                                     MULTIPLE_INSTANCE_DATA_OUTPUT,
                                                     MULTIPLE_INSTANCE_COMPLETION_CONDITION,
                                                     IS_ASYNC,
                                                     SUBPROCESS_ON_ENTRY_ACTION_JAVA,
                                                     SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                                     SUBPROCESS_ON_EXIT_ACTION_JAVA,
                                                     SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                                     SLA_DUE_DATE);
        assertSubProcessProcessData(subProcessLevelSubProcessJava.getProcessData(), SUB_PROCESS_VARIABLES_JAVA);

        MultipleInstanceSubprocess subProcessLevelSubProcessJavascript = getSubProcessNodeById(diagram,
                                                                                               SUBPROCESS_LEVEL_FILLED_SUBPROCESS_JAVASCRIPT_ID,
                                                                                               EMPTY_INCOME_EDGES,
                                                                                               EMPTY_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcessJavascript.getGeneral(), SUB_PROCESS_NAME_JAVASCRIPT, SUB_PROCESS_DOCUMENTATION_JAVASCRIPT);
        assertMultipleInstanceSubProcessExecutionSet(subProcessLevelSubProcessJavascript.getExecutionSet(),
                                                     MULTIPLE_INSTANCE_COLLECTION_INPUT,
                                                     MULTIPLE_INSTANCE_COLLECTION_OUTPUT,
                                                     MULTIPLE_INSTANCE_DATA_INPUT,
                                                     MULTIPLE_INSTANCE_DATA_OUTPUT,
                                                     MULTIPLE_INSTANCE_COMPLETION_CONDITION,
                                                     IS_ASYNC,
                                                     SUBPROCESS_ON_ENTRY_ACTION_JAVASCRIPT,
                                                     SUBPROCESS_SCRIPT_JAVASCRIPT_LANGUAGE,
                                                     SUBPROCESS_ON_EXIT_ACTION_JAVASCRIPT,
                                                     SUBPROCESS_SCRIPT_JAVASCRIPT_LANGUAGE,
                                                     SLA_DUE_DATE);
        assertSubProcessProcessData(subProcessLevelSubProcessJavascript.getProcessData(), SUB_PROCESS_VARIABLES_JAVASCRIPT);

        MultipleInstanceSubprocess subProcessLevelSubProcessMVEL = getSubProcessNodeById(diagram,
                                                                                         SUBPROCESS_LEVEL_FILLED_SUBPROCESS_MVEL_ID,
                                                                                         EMPTY_INCOME_EDGES,
                                                                                         EMPTY_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcessMVEL.getGeneral(), SUB_PROCESS_NAME_MVEL, SUB_PROCESS_DOCUMENTATION_MVEL);
        assertMultipleInstanceSubProcessExecutionSet(subProcessLevelSubProcessMVEL.getExecutionSet(),
                                                     MULTIPLE_INSTANCE_COLLECTION_INPUT,
                                                     MULTIPLE_INSTANCE_COLLECTION_OUTPUT,
                                                     MULTIPLE_INSTANCE_DATA_INPUT,
                                                     MULTIPLE_INSTANCE_DATA_OUTPUT,
                                                     MULTIPLE_INSTANCE_COMPLETION_CONDITION,
                                                     IS_ASYNC,
                                                     SUBPROCESS_ON_ENTRY_ACTION_MVEL,
                                                     SUBPROCESS_SCRIPT_MVEL_LANGUAGE,
                                                     SUBPROCESS_ON_EXIT_ACTION_MVEL,
                                                     SUBPROCESS_SCRIPT_MVEL_LANGUAGE,
                                                     SLA_DUE_DATE);
        assertSubProcessProcessData(subProcessLevelSubProcessMVEL.getProcessData(), SUB_PROCESS_VARIABLES_MVEL);
    }

    @Test
    @Override
    public void testUnmarshallSubProcessLevelSubProcessWithEdges() {
        final String SUB_PROCESS_NAME = "Multiple Instance Sub-process08";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        MultipleInstanceSubprocess subProcessLevelSubProcess = getSubProcessNodeById(diagram,
                                                                                     SUBPROCESS_LEVEL_SUBPROCESS_WITH_EDGES,
                                                                                     ONE_INCOME_EDGE,
                                                                                     FOUR_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcess.getGeneral(), SUB_PROCESS_NAME, DEFAULT_DOCUMENTATION);
        assertMultipleInstanceSubProcessExecutionSet(subProcessLevelSubProcess.getExecutionSet(),
                                                     DEFAULT_MULTIPLE_INSTANCE_COLLECTION,
                                                     DEFAULT_MULTIPLE_INSTANCE_COLLECTION,
                                                     EMPTY_VALUE,
                                                     EMPTY_VALUE,
                                                     EMPTY_VALUE,
                                                     IS_NOT_ASYNC,
                                                     EMPTY_VALUE,
                                                     SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                                     EMPTY_VALUE,
                                                     SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                                     EMPTY_VALUE);
        assertSubProcessProcessData(subProcessLevelSubProcess.getProcessData(), EMPTY_VALUE);
    }

    @Override
    Class<MultipleInstanceSubprocess> getSubProcessType() {
        return MultipleInstanceSubprocess.class;
    }

    @Override
    String getBpmnSubProcessFilePath() {
        return BPMN_SUB_PROCESS_FILE_PATH;
    }

    @Override
    String getTopLevelEmptyPropertiesSubProcessId() {
        return TOP_LEVEL_EMPTY_SUBPROCESS_ID;
    }

    @Override
    String[] getTopLevelFilledPropertiesSubProcessesIds() {
        return new String[]{TOP_LEVEL_FILLED_SUBPROCESS_JAVA_ID,
                TOP_LEVEL_FILLED_SUBPROCESS_JAVASCRIPT_ID,
                TOP_LEVEL_FILLED_SUBPROCESS_MVEL_ID};
    }

    @Override
    String getTopLevelSubProcessWithEdgesId() {
        return TOP_LEVEL_SUBPROCESS_WITH_EDGES;
    }

    @Override
    String getSubProcessLevelEmptyPropertiesSubProcessId() {
        return SUBPROCESS_LEVEL_EMPTY_SUBPROCESS_ID;
    }

    @Override
    String[] getSubProcessLevelFilledPropertiesSubProcessesIds() {
        return new String[]{SUBPROCESS_LEVEL_FILLED_SUBPROCESS_JAVA_ID,
                SUBPROCESS_LEVEL_FILLED_SUBPROCESS_JAVASCRIPT_ID,
                SUBPROCESS_LEVEL_FILLED_SUBPROCESS_MVEL_ID};
    }

    @Override
    String getSubProcessLevelSubProcessWithEdgesId() {
        return SUBPROCESS_LEVEL_SUBPROCESS_WITH_EDGES;
    }

    private void assertMultipleInstanceSubProcessExecutionSet(MultipleInstanceSubprocessTaskExecutionSet executionSet,
                                                              String multipleInstanceCollectionInput,
                                                              String multipleInstanceCollectionOutput,
                                                              String multipleInstanceDataInput,
                                                              String multipleInstanceDataOutput,
                                                              String multipleInstanceCompletionCondition,
                                                              boolean isAsync,
                                                              String onEntryActionScriptValue,
                                                              String onEntryActionScriptLanguage,
                                                              String onExitActionScriptValue,
                                                              String onExitActionScriptLanguage,
                                                              String slaDueDate) {
        assertThat(executionSet).isNotNull();

        assertThat(executionSet.getMultipleInstanceCollectionInput()).isNotNull();
        assertThat(executionSet.getMultipleInstanceCollectionOutput()).isNotNull();
        assertThat(executionSet.getMultipleInstanceDataInput()).isNotNull();
        assertThat(executionSet.getMultipleInstanceDataOutput()).isNotNull();
        assertThat(executionSet.getMultipleInstanceCompletionCondition()).isNotNull();
        assertThat(executionSet.getIsAsync()).isNotNull();
        assertThat(executionSet.getSlaDueDate()).isNotNull();

        assertThat(executionSet.getOnEntryAction()).isNotNull();
        assertThat(executionSet.getOnExitAction()).isNotNull();
        assertThat(executionSet.getOnEntryAction().getValue()).isNotNull();
        assertThat(executionSet.getOnExitAction().getValue()).isNotNull();

        List<ScriptTypeValue> onEntryScriptTypeValues = executionSet.getOnEntryAction().getValue().getValues();
        List<ScriptTypeValue> onExitScriptTypeValues = executionSet.getOnExitAction().getValue().getValues();

        assertThat(onEntryScriptTypeValues).isNotNull();
        assertThat(onExitScriptTypeValues).isNotNull();
        assertThat(onEntryScriptTypeValues.get(0)).isNotNull();
        assertThat(onExitScriptTypeValues.get(0)).isNotNull();

        assertThat(executionSet.getMultipleInstanceCollectionInput().getValue()).isEqualTo(multipleInstanceCollectionInput);
        assertThat(executionSet.getMultipleInstanceCollectionOutput().getValue()).isEqualTo(multipleInstanceCollectionOutput);
        assertThat(executionSet.getMultipleInstanceDataInput().getValue()).isEqualTo(multipleInstanceDataInput);
        assertThat(executionSet.getMultipleInstanceDataOutput().getValue()).isEqualTo(multipleInstanceDataOutput);
        assertThat(executionSet.getMultipleInstanceCompletionCondition().getValue()).isEqualTo(multipleInstanceCompletionCondition);

        assertThat(executionSet.getIsAsync().getValue()).isEqualTo(isAsync);
        assertThat(executionSet.getSlaDueDate().getValue()).isEqualTo(slaDueDate);

        assertThat(onEntryScriptTypeValues.get(0).getScript()).isEqualTo(onEntryActionScriptValue);
        assertThat(onEntryScriptTypeValues.get(0).getLanguage()).isEqualTo(onEntryActionScriptLanguage);
        assertThat(onExitScriptTypeValues.get(0).getScript()).isEqualTo(onExitActionScriptValue);
        assertThat(onExitScriptTypeValues.get(0).getLanguage()).isEqualTo(onExitActionScriptLanguage);
    }
}
