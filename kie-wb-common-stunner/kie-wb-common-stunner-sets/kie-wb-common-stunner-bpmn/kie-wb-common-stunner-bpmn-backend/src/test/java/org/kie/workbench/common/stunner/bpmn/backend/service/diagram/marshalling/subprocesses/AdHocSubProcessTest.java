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
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.assertj.core.api.Assertions.assertThat;

public class AdHocSubProcessTest extends SubProcessTest<AdHocSubprocess> {

    private static final String BPMN_SUB_PROCESS_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/adHocSubProcesses.bpmn";

    private static final String TOP_LEVEL_EMPTY_SUBPROCESS_ID = "_C13AA1A1-8AA5-4F37-821D-616953802599";
    private static final String TOP_LEVEL_FILLED_SUBPROCESS_JAVA_ID = "_7FBF0C49-8268-495A-98D9-44CA6D34BE96";
    private static final String TOP_LEVEL_FILLED_SUBPROCESS_JAVASCRIPT_ID = "_6F38D400-A187-4845-BA2D-CACEFA63013A";
    private static final String TOP_LEVEL_FILLED_SUBPROCESS_MVEL_ID = "_752AED87-C2E8-4E62-8DA1-2FC831F4EA7E";

    private static final String SUBPROCESS_LEVEL_EMPTY_SUBPROCESS_ID = "_41EBC350-8CF6-4781-84BE-A54A647C779A";
    private static final String SUBPROCESS_LEVEL_FILLED_SUBPROCESS_JAVA_ID = "_F9AD8287-3BD1-4D8E-9888-6524F1E8079C";
    private static final String SUBPROCESS_LEVEL_FILLED_SUBPROCESS_JAVASCRIPT_ID = "_06F7FACD-7F14-4B88-9DF0-30C3D4F859B0";
    private static final String SUBPROCESS_LEVEL_FILLED_SUBPROCESS_MVEL_ID = "_336E3C64-942E-47F9-894F-E15BAF72D514";

    private static final String TOP_LEVEL_SUBPROCESS_WITH_EDGES = "_22F2BA2D-7AA3-4924-973A-452C379E563A";
    private static final String SUBPROCESS_LEVEL_SUBPROCESS_WITH_EDGES = "_D14DA50A-FA9F-4EE3-A06B-F7F35A923CEE";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 23;

    private static final String ADHOC_COMPLETION_CONDITION_SCRIPT_DEFAULT = "autocomplete";
    private static final String ADHOC_COMPLETION_CONDITION_SCRIPT = "org.kie.api.runtime.process.CaseData(data.get(\"someData\") == true)";
    private static final String ADHOC_COMPLETION_CONDITION_LANGUAGE_MVEL = "mvel";
    private static final String ADHOC_COMPLETION_CONDITION_LANGUAGE_DROOLS = "drools";
    private static final String ADHOC_ORDERING_SEQUENTIAL = "Sequential";
    private static final String ADHOC_ORDERING_PARALLEL = "Parallel";

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

    public AdHocSubProcessTest() throws Exception {
    }

    @Test
    public void testUnmarshallTopLevelEmptyPropertiesSubProcess() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        AdHocSubprocess topLevelSubProcess = getSubProcessNodeById(diagram,
                                                                   TOP_LEVEL_EMPTY_SUBPROCESS_ID,
                                                                   EMPTY_INCOME_EDGES,
                                                                   EMPTY_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcess.getGeneral(), DEFAULT_NAME, DEFAULT_DOCUMENTATION);
        assertAdHocSubProcessExecutionSet(topLevelSubProcess.getExecutionSet(),
                                          EMPTY_VALUE,
                                          ADHOC_COMPLETION_CONDITION_LANGUAGE_MVEL,
                                          ADHOC_ORDERING_SEQUENTIAL,
                                          EMPTY_VALUE,
                                          SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                          EMPTY_VALUE,
                                          SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                          IS_NOT_ASYNC,
                                          EMPTY_VALUE);
        assertSubProcessProcessData(topLevelSubProcess.getProcessData(), EMPTY_VALUE);
    }

    @Test
    public void testUnmarshallTopLevelFilledPropertiesSubProcess() {
        final String SUB_PROCESS_NAME_JAVA = "Ad-hoc sub-process01 name ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION_JAVA = "Ad-hoc sub-process01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_NAME_JAVASCRIPT = "Ad-hoc sub-process02 name ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION_JAVASCRIPT = "Ad-hoc sub-process02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_NAME_MVEL = "Ad-hoc sub-process03 name ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION_MVEL = "Ad-hoc sub-process03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";

        final String SUB_PROCESS_VARIABLES_JAVA = "subVar01:String:false";
        final String SUB_PROCESS_VARIABLES_JAVASCRIPT = "subVar02:String:false";
        final String SUB_PROCESS_VARIABLES_MVEL = "subVarString:String:false,subVarCustom:CustomType:false,subVarBoolean:Boolean:false,subVarFloat:Float:false,subVarInteger:Integer:false,subVarObject:Object:false";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        AdHocSubprocess topLevelSubProcessJava = getSubProcessNodeById(diagram,
                                                                       TOP_LEVEL_FILLED_SUBPROCESS_JAVA_ID,
                                                                       EMPTY_INCOME_EDGES,
                                                                       EMPTY_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcessJava.getGeneral(), SUB_PROCESS_NAME_JAVA, SUB_PROCESS_DOCUMENTATION_JAVA);
        assertAdHocSubProcessExecutionSet(topLevelSubProcessJava.getExecutionSet(),
                                          ADHOC_COMPLETION_CONDITION_SCRIPT_DEFAULT,
                                          ADHOC_COMPLETION_CONDITION_LANGUAGE_MVEL,
                                          ADHOC_ORDERING_SEQUENTIAL,
                                          SUBPROCESS_ON_ENTRY_ACTION_JAVA,
                                          SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                          SUBPROCESS_ON_EXIT_ACTION_JAVA,
                                          SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                          IS_ASYNC,
                                          SLA_DUE_DATE);
        assertSubProcessProcessData(topLevelSubProcessJava.getProcessData(), SUB_PROCESS_VARIABLES_JAVA);

        AdHocSubprocess topLevelSubProcessJavascript = getSubProcessNodeById(diagram,
                                                                             TOP_LEVEL_FILLED_SUBPROCESS_JAVASCRIPT_ID,
                                                                             EMPTY_INCOME_EDGES,
                                                                             EMPTY_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcessJavascript.getGeneral(), SUB_PROCESS_NAME_JAVASCRIPT, SUB_PROCESS_DOCUMENTATION_JAVASCRIPT);
        assertAdHocSubProcessExecutionSet(topLevelSubProcessJavascript.getExecutionSet(),
                                          ADHOC_COMPLETION_CONDITION_SCRIPT,
                                          ADHOC_COMPLETION_CONDITION_LANGUAGE_DROOLS,
                                          ADHOC_ORDERING_PARALLEL,
                                          SUBPROCESS_ON_ENTRY_ACTION_JAVASCRIPT,
                                          SUBPROCESS_SCRIPT_JAVASCRIPT_LANGUAGE,
                                          SUBPROCESS_ON_EXIT_ACTION_JAVASCRIPT,
                                          SUBPROCESS_SCRIPT_JAVASCRIPT_LANGUAGE,
                                          IS_ASYNC,
                                          SLA_DUE_DATE);
        assertSubProcessProcessData(topLevelSubProcessJavascript.getProcessData(), SUB_PROCESS_VARIABLES_JAVASCRIPT);

        AdHocSubprocess topLevelSubProcessMVEL = getSubProcessNodeById(diagram,
                                                                       TOP_LEVEL_FILLED_SUBPROCESS_MVEL_ID,
                                                                       EMPTY_INCOME_EDGES,
                                                                       EMPTY_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcessMVEL.getGeneral(), SUB_PROCESS_NAME_MVEL, SUB_PROCESS_DOCUMENTATION_MVEL);
        assertAdHocSubProcessExecutionSet(topLevelSubProcessMVEL.getExecutionSet(),
                                          ADHOC_COMPLETION_CONDITION_SCRIPT_DEFAULT,
                                          ADHOC_COMPLETION_CONDITION_LANGUAGE_MVEL,
                                          ADHOC_ORDERING_SEQUENTIAL,
                                          SUBPROCESS_ON_ENTRY_ACTION_MVEL,
                                          SUBPROCESS_SCRIPT_MVEL_LANGUAGE,
                                          SUBPROCESS_ON_EXIT_ACTION_MVEL,
                                          SUBPROCESS_SCRIPT_MVEL_LANGUAGE,
                                          IS_ASYNC,
                                          SLA_DUE_DATE);
        assertSubProcessProcessData(topLevelSubProcessMVEL.getProcessData(), SUB_PROCESS_VARIABLES_MVEL);
    }

    @Test
    public void testUnmarshallTopLevelSubProcessWithEdges() {
        final String SUB_PROCESS_NAME = "Sub-process07";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        AdHocSubprocess topLevelSubProcess = getSubProcessNodeById(diagram,
                                                                   TOP_LEVEL_SUBPROCESS_WITH_EDGES,
                                                                   ONE_INCOME_EDGE,
                                                                   FOUR_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcess.getGeneral(), SUB_PROCESS_NAME, DEFAULT_DOCUMENTATION);
        assertAdHocSubProcessExecutionSet(topLevelSubProcess.getExecutionSet(),
                                          ADHOC_COMPLETION_CONDITION_SCRIPT_DEFAULT,
                                          ADHOC_COMPLETION_CONDITION_LANGUAGE_MVEL,
                                          ADHOC_ORDERING_SEQUENTIAL,
                                          EMPTY_VALUE,
                                          SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                          EMPTY_VALUE,
                                          SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                          IS_NOT_ASYNC,
                                          EMPTY_VALUE);
        assertSubProcessProcessData(topLevelSubProcess.getProcessData(), EMPTY_VALUE);
    }

    @Test
    public void testUnmarshallSubProcessLevelEmptyPropertiesSubProcess() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        AdHocSubprocess subProcessLevelSubProcess = getSubProcessNodeById(diagram,
                                                                          SUBPROCESS_LEVEL_EMPTY_SUBPROCESS_ID,
                                                                          EMPTY_INCOME_EDGES,
                                                                          EMPTY_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcess.getGeneral(), DEFAULT_NAME, DEFAULT_DOCUMENTATION);
        assertAdHocSubProcessExecutionSet(subProcessLevelSubProcess.getExecutionSet(),
                                          EMPTY_VALUE,
                                          ADHOC_COMPLETION_CONDITION_LANGUAGE_MVEL,
                                          ADHOC_ORDERING_SEQUENTIAL,
                                          EMPTY_VALUE,
                                          SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                          EMPTY_VALUE,
                                          SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                          IS_NOT_ASYNC,
                                          EMPTY_VALUE);
        assertSubProcessProcessData(subProcessLevelSubProcess.getProcessData(), EMPTY_VALUE);
    }

    @Test
    public void testUnmarshallSubProcessLevelFilledPropertiesSubProcess() {
        final String SUB_PROCESS_NAME_JAVA = "Ad-hoc sub-process04 name ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION_JAVA = "Ad-hoc sub-process04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_NAME_JAVASCRIPT = "Ad-hoc sub-process05 name ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION_JAVASCRIPT = "Ad-hoc sub-process05 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_NAME_MVEL = "Ad-hoc sub-process06 name ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION_MVEL = "Ad-hoc sub-process06 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";

        final String SUB_PROCESS_VARIABLES_JAVA = "subVar04:String:false";
        final String SUB_PROCESS_VARIABLES_JAVASCRIPT = "subVar05:String:false";
        final String SUB_PROCESS_VARIABLES_MVEL = "subVarString:String:false,subVarCustom:CustomType:false,subVarBoolean:Boolean:false,subVarFloat:Float:false,subVarInteger:Integer:false,subVarObject:Object:false";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        AdHocSubprocess subProcessLevelSubProcessJava = getSubProcessNodeById(diagram,
                                                                              SUBPROCESS_LEVEL_FILLED_SUBPROCESS_JAVA_ID,
                                                                              EMPTY_INCOME_EDGES,
                                                                              EMPTY_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcessJava.getGeneral(), SUB_PROCESS_NAME_JAVA, SUB_PROCESS_DOCUMENTATION_JAVA);
        assertAdHocSubProcessExecutionSet(subProcessLevelSubProcessJava.getExecutionSet(),
                                          ADHOC_COMPLETION_CONDITION_SCRIPT_DEFAULT,
                                          ADHOC_COMPLETION_CONDITION_LANGUAGE_MVEL,
                                          ADHOC_ORDERING_SEQUENTIAL,
                                          SUBPROCESS_ON_ENTRY_ACTION_JAVA,
                                          SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                          SUBPROCESS_ON_EXIT_ACTION_JAVA,
                                          SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                          IS_ASYNC,
                                          SLA_DUE_DATE);
        assertSubProcessProcessData(subProcessLevelSubProcessJava.getProcessData(), SUB_PROCESS_VARIABLES_JAVA);

        AdHocSubprocess subProcessLevelSubProcessJavascript = getSubProcessNodeById(diagram,
                                                                                    SUBPROCESS_LEVEL_FILLED_SUBPROCESS_JAVASCRIPT_ID,
                                                                                    EMPTY_INCOME_EDGES,
                                                                                    EMPTY_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcessJavascript.getGeneral(), SUB_PROCESS_NAME_JAVASCRIPT, SUB_PROCESS_DOCUMENTATION_JAVASCRIPT);
        assertAdHocSubProcessExecutionSet(subProcessLevelSubProcessJavascript.getExecutionSet(),
                                          ADHOC_COMPLETION_CONDITION_SCRIPT,
                                          ADHOC_COMPLETION_CONDITION_LANGUAGE_DROOLS,
                                          ADHOC_ORDERING_PARALLEL,
                                          SUBPROCESS_ON_ENTRY_ACTION_JAVASCRIPT,
                                          SUBPROCESS_SCRIPT_JAVASCRIPT_LANGUAGE,
                                          SUBPROCESS_ON_EXIT_ACTION_JAVASCRIPT,
                                          SUBPROCESS_SCRIPT_JAVASCRIPT_LANGUAGE,
                                          IS_ASYNC,
                                          SLA_DUE_DATE);
        assertSubProcessProcessData(subProcessLevelSubProcessJavascript.getProcessData(), SUB_PROCESS_VARIABLES_JAVASCRIPT);

        AdHocSubprocess subProcessLevelSubProcessMVEL = getSubProcessNodeById(diagram,
                                                                              SUBPROCESS_LEVEL_FILLED_SUBPROCESS_MVEL_ID,
                                                                              EMPTY_INCOME_EDGES,
                                                                              EMPTY_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcessMVEL.getGeneral(), SUB_PROCESS_NAME_MVEL, SUB_PROCESS_DOCUMENTATION_MVEL);
        assertAdHocSubProcessExecutionSet(subProcessLevelSubProcessMVEL.getExecutionSet(),
                                          ADHOC_COMPLETION_CONDITION_SCRIPT_DEFAULT,
                                          ADHOC_COMPLETION_CONDITION_LANGUAGE_MVEL,
                                          ADHOC_ORDERING_SEQUENTIAL,
                                          SUBPROCESS_ON_ENTRY_ACTION_MVEL,
                                          SUBPROCESS_SCRIPT_MVEL_LANGUAGE,
                                          SUBPROCESS_ON_EXIT_ACTION_MVEL,
                                          SUBPROCESS_SCRIPT_MVEL_LANGUAGE,
                                          IS_ASYNC,
                                          SLA_DUE_DATE);
        assertSubProcessProcessData(subProcessLevelSubProcessMVEL.getProcessData(), SUB_PROCESS_VARIABLES_MVEL);
    }

    @Test
    public void testUnmarshallSubProcessLevelSubProcessWithEdges() {
        final String SUB_PROCESS_NAME = "Sub-process08";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        AdHocSubprocess subProcessLevelSubProcess = getSubProcessNodeById(diagram,
                                                                          SUBPROCESS_LEVEL_SUBPROCESS_WITH_EDGES,
                                                                          ONE_INCOME_EDGE,
                                                                          FOUR_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcess.getGeneral(), SUB_PROCESS_NAME, DEFAULT_DOCUMENTATION);
        assertAdHocSubProcessExecutionSet(subProcessLevelSubProcess.getExecutionSet(),
                                          ADHOC_COMPLETION_CONDITION_SCRIPT_DEFAULT,
                                          ADHOC_COMPLETION_CONDITION_LANGUAGE_MVEL,
                                          ADHOC_ORDERING_SEQUENTIAL,
                                          EMPTY_VALUE,
                                          SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                          EMPTY_VALUE,
                                          SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                          IS_NOT_ASYNC,
                                          EMPTY_VALUE);
        assertSubProcessProcessData(subProcessLevelSubProcess.getProcessData(), EMPTY_VALUE);
    }

    @Override
    Class<AdHocSubprocess> getSubProcessType() {
        return AdHocSubprocess.class;
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

    private void assertAdHocSubProcessExecutionSet(AdHocSubprocessTaskExecutionSet executionSet,
                                                   String adHocCompletionConditionScript,
                                                   String adHocCompletionConditionLanguage,
                                                   String adHocOrdering,
                                                   String onEntryActionScriptValue,
                                                   String onEntryActionScriptLanguage,
                                                   String onExitActionScriptValue,
                                                   String onExitActionScriptLanguage,
                                                   Boolean isAsync,
                                                   String slaDueDate) {
        assertThat(executionSet).isNotNull();

        assertThat(executionSet.getAdHocCompletionCondition()).isNotNull();
        assertThat(executionSet.getAdHocCompletionCondition().getValue()).isNotNull();
        assertThat(executionSet.getAdHocOrdering()).isNotNull();

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

        assertThat(executionSet.getAdHocCompletionCondition().getValue().getScript()).isEqualTo(adHocCompletionConditionScript);
        assertThat(executionSet.getAdHocCompletionCondition().getValue().getLanguage()).isEqualTo(adHocCompletionConditionLanguage);
        assertThat(executionSet.getAdHocOrdering().getValue()).isEqualTo(adHocOrdering);

        assertThat(onEntryScriptTypeValues.get(0).getScript()).isEqualTo(onEntryActionScriptValue);
        assertThat(onEntryScriptTypeValues.get(0).getLanguage()).isEqualTo(onEntryActionScriptLanguage);
        assertThat(onExitScriptTypeValues.get(0).getScript()).isEqualTo(onExitActionScriptValue);
        assertThat(onExitScriptTypeValues.get(0).getLanguage()).isEqualTo(onExitActionScriptLanguage);

        assertThat(executionSet.getSlaDueDate()).isNotNull();
        assertThat(executionSet.getSlaDueDate().getValue()).isEqualTo(slaDueDate);

        assertThat(executionSet.getIsAsync()).isNotNull();
        assertThat(executionSet.getIsAsync().getValue()).isEqualTo(isAsync);
    }
}