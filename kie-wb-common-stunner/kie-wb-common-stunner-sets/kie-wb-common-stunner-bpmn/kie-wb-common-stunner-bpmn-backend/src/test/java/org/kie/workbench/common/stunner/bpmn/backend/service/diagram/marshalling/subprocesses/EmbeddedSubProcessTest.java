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

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.di.DiagramElement;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsConverter;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.execution.EmbeddedSubprocessExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.assertj.core.api.Assertions.assertThat;

public class EmbeddedSubProcessTest extends SubProcessTest<EmbeddedSubprocess> {

    private static final String BPMN_SUB_PROCESS_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/embeddedSubProcesses.bpmn";

    private static final String TOP_LEVEL_EMPTY_SUBPROCESS_ID = "_E9D3FBF1-55DE-4C93-8CFE-67FD311BB452";
    private static final String TOP_LEVEL_FILLED_SUBPROCESS_JAVA_ID = "_8050BE36-E9A9-4461-BE33-6813B891D1D9";
    private static final String TOP_LEVEL_FILLED_SUBPROCESS_JAVASCRIPT_ID = "_395EFE1F-4FED-4114-BF8C-C6094A556B65";
    private static final String TOP_LEVEL_FILLED_SUBPROCESS_MVEL_ID = "_0C0C20E4-A384-4F9E-BDCE-D13CA2C54506";

    private static final String SUBPROCESS_LEVEL_EMPTY_SUBPROCESS_ID = "_7C50F29B-02A6-4839-B1B7-E41F94A8ADAE";
    private static final String SUBPROCESS_LEVEL_FILLED_SUBPROCESS_JAVA_ID = "_76188540-34C4-48C3-979D-9CA8D89ECFE1";
    private static final String SUBPROCESS_LEVEL_FILLED_SUBPROCESS_JAVASCRIPT_ID = "_556F8833-ACC8-4CAF-ABA8-2A34A42F3A20";
    private static final String SUBPROCESS_LEVEL_FILLED_SUBPROCESS_MVEL_ID = "_8305C048-5790-4717-A219-66F39209C652";

    private static final String TOP_LEVEL_SUBPROCESS_WITH_EDGES = "_B4FFD902-D756-4C37-960D-74487547A934";
    private static final String SUBPROCESS_LEVEL_SUBPROCESS_WITH_EDGES = "_DE818F69-E02C-471E-8F0B-27101308541B";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 23;

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

    public EmbeddedSubProcessTest() throws Exception {
    }

    @Test
    public void testUnmarshallTopLevelEmptyPropertiesSubProcess() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EmbeddedSubprocess topLevelSubProcess = getSubProcessNodeById(diagram,
                                                                      TOP_LEVEL_EMPTY_SUBPROCESS_ID,
                                                                      EMPTY_INCOME_EDGES,
                                                                      EMPTY_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcess.getGeneral(), DEFAULT_NAME, DEFAULT_DOCUMENTATION);
        assertEmbeddedSubprocessExecutionSet(topLevelSubProcess.getExecutionSet(),
                                             IS_NOT_ASYNC,
                                             EMPTY_VALUE,
                                             SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                             EMPTY_VALUE,
                                             SUBPROCESS_SCRIPT_JAVA_LANGUAGE);
        assertSubProcessProcessData(topLevelSubProcess.getProcessData(), EMPTY_VALUE);
    }

    @Test
    public void testUnmarshallTopLevelFilledPropertiesSubProcess() {
        final String SUB_PROCESS_NAME_JAVA = "Embedded process01 name ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION_JAVA = "Embedded process01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_NAME_JAVASCRIPT = "Embedded process02 name ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION_JAVASCRIPT = "Embedded process02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_NAME_MVEL = "Embedded process03 name ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION_MVEL = "Embedded process03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";

        final String SUB_PROCESS_VARIABLES_JAVA = "subVar01:String";
        final String SUB_PROCESS_VARIABLES_JAVASCRIPT = "subVar02:String";
        final String SUB_PROCESS_VARIABLES_MVEL = "subVar03:String";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EmbeddedSubprocess topLevelSubProcessJava = getSubProcessNodeById(diagram,
                                                                          TOP_LEVEL_FILLED_SUBPROCESS_JAVA_ID,
                                                                          EMPTY_INCOME_EDGES,
                                                                          EMPTY_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcessJava.getGeneral(), SUB_PROCESS_NAME_JAVA, SUB_PROCESS_DOCUMENTATION_JAVA);
        assertEmbeddedSubprocessExecutionSet(topLevelSubProcessJava.getExecutionSet(),
                                             IS_ASYNC,
                                             SUBPROCESS_ON_ENTRY_ACTION_JAVA,
                                             SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                             SUBPROCESS_ON_EXIT_ACTION_JAVA,
                                             SUBPROCESS_SCRIPT_JAVA_LANGUAGE);
        assertSubProcessProcessData(topLevelSubProcessJava.getProcessData(), SUB_PROCESS_VARIABLES_JAVA);

        EmbeddedSubprocess topLevelSubProcessJavascript = getSubProcessNodeById(diagram,
                                                                                TOP_LEVEL_FILLED_SUBPROCESS_JAVASCRIPT_ID,
                                                                                EMPTY_INCOME_EDGES,
                                                                                EMPTY_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcessJavascript.getGeneral(), SUB_PROCESS_NAME_JAVASCRIPT, SUB_PROCESS_DOCUMENTATION_JAVASCRIPT);
        assertEmbeddedSubprocessExecutionSet(topLevelSubProcessJavascript.getExecutionSet(),
                                             IS_ASYNC,
                                             SUBPROCESS_ON_ENTRY_ACTION_JAVASCRIPT,
                                             SUBPROCESS_SCRIPT_JAVASCRIPT_LANGUAGE,
                                             SUBPROCESS_ON_EXIT_ACTION_JAVASCRIPT,
                                             SUBPROCESS_SCRIPT_JAVASCRIPT_LANGUAGE);
        assertSubProcessProcessData(topLevelSubProcessJavascript.getProcessData(), SUB_PROCESS_VARIABLES_JAVASCRIPT);

        EmbeddedSubprocess topLevelSubProcessMVEL = getSubProcessNodeById(diagram,
                                                                          TOP_LEVEL_FILLED_SUBPROCESS_MVEL_ID,
                                                                          EMPTY_INCOME_EDGES,
                                                                          EMPTY_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcessMVEL.getGeneral(), SUB_PROCESS_NAME_MVEL, SUB_PROCESS_DOCUMENTATION_MVEL);
        assertEmbeddedSubprocessExecutionSet(topLevelSubProcessMVEL.getExecutionSet(),
                                             IS_ASYNC,
                                             SUBPROCESS_ON_ENTRY_ACTION_MVEL,
                                             SUBPROCESS_SCRIPT_MVEL_LANGUAGE,
                                             SUBPROCESS_ON_EXIT_ACTION_MVEL,
                                             SUBPROCESS_SCRIPT_MVEL_LANGUAGE);
        assertSubProcessProcessData(topLevelSubProcessMVEL.getProcessData(), SUB_PROCESS_VARIABLES_MVEL);
    }

    @Test
    public void testUnmarshallTopLevelSubProcessWithEdges() {
        final String SUB_PROCESS_NAME = "Sub-process07";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EmbeddedSubprocess topLevelSubProcess = getSubProcessNodeById(diagram,
                                                                      TOP_LEVEL_SUBPROCESS_WITH_EDGES,
                                                                      ONE_INCOME_EDGE,
                                                                      FOUR_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcess.getGeneral(), SUB_PROCESS_NAME, DEFAULT_DOCUMENTATION);
        assertEmbeddedSubprocessExecutionSet(topLevelSubProcess.getExecutionSet(),
                                             IS_NOT_ASYNC,
                                             EMPTY_VALUE,
                                             SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                             EMPTY_VALUE,
                                             SUBPROCESS_SCRIPT_JAVA_LANGUAGE);
        assertSubProcessProcessData(topLevelSubProcess.getProcessData(), EMPTY_VALUE);
    }

    @Test
    public void testUnmarshallSubProcessLevelEmptyPropertiesSubProcess() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EmbeddedSubprocess subProcessLevelSubProcess = getSubProcessNodeById(diagram,
                                                                             SUBPROCESS_LEVEL_EMPTY_SUBPROCESS_ID,
                                                                             EMPTY_INCOME_EDGES,
                                                                             EMPTY_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcess.getGeneral(), DEFAULT_NAME, DEFAULT_DOCUMENTATION);
        assertEmbeddedSubprocessExecutionSet(subProcessLevelSubProcess.getExecutionSet(),
                                             IS_NOT_ASYNC,
                                             EMPTY_VALUE,
                                             SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                             EMPTY_VALUE,
                                             SUBPROCESS_SCRIPT_JAVA_LANGUAGE);
        assertSubProcessProcessData(subProcessLevelSubProcess.getProcessData(), EMPTY_VALUE);
    }

    @Test
    public void testUnmarshallSubProcessLevelFilledPropertiesSubProcess() {
        final String SUB_PROCESS_NAME_JAVA = "Embedded process04 name ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION_JAVA = "Embedded process04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_NAME_JAVASCRIPT = "Embedded process05 name ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION_JAVASCRIPT = "Embedded process05 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_NAME_MVEL = "Embedded process06 name ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION_MVEL = "Embedded process06 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";

        final String SUB_PROCESS_VARIABLES_JAVA = "subVar04:String";
        final String SUB_PROCESS_VARIABLES_JAVASCRIPT = "subVar05:String";
        final String SUB_PROCESS_VARIABLES_MVEL = "subVar06:String";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EmbeddedSubprocess subProcessLevelSubProcessJava = getSubProcessNodeById(diagram,
                                                                                 SUBPROCESS_LEVEL_FILLED_SUBPROCESS_JAVA_ID,
                                                                                 EMPTY_INCOME_EDGES,
                                                                                 EMPTY_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcessJava.getGeneral(), SUB_PROCESS_NAME_JAVA, SUB_PROCESS_DOCUMENTATION_JAVA);
        assertEmbeddedSubprocessExecutionSet(subProcessLevelSubProcessJava.getExecutionSet(),
                                             IS_ASYNC,
                                             SUBPROCESS_ON_ENTRY_ACTION_JAVA,
                                             SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                             SUBPROCESS_ON_EXIT_ACTION_JAVA,
                                             SUBPROCESS_SCRIPT_JAVA_LANGUAGE);
        assertSubProcessProcessData(subProcessLevelSubProcessJava.getProcessData(), SUB_PROCESS_VARIABLES_JAVA);

        EmbeddedSubprocess subProcessLevelSubProcessJavascript = getSubProcessNodeById(diagram,
                                                                                       SUBPROCESS_LEVEL_FILLED_SUBPROCESS_JAVASCRIPT_ID,
                                                                                       EMPTY_INCOME_EDGES,
                                                                                       EMPTY_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcessJavascript.getGeneral(), SUB_PROCESS_NAME_JAVASCRIPT, SUB_PROCESS_DOCUMENTATION_JAVASCRIPT);
        assertEmbeddedSubprocessExecutionSet(subProcessLevelSubProcessJavascript.getExecutionSet(),
                                             IS_ASYNC,
                                             SUBPROCESS_ON_ENTRY_ACTION_JAVASCRIPT,
                                             SUBPROCESS_SCRIPT_JAVASCRIPT_LANGUAGE,
                                             SUBPROCESS_ON_EXIT_ACTION_JAVASCRIPT,
                                             SUBPROCESS_SCRIPT_JAVASCRIPT_LANGUAGE);
        assertSubProcessProcessData(subProcessLevelSubProcessJavascript.getProcessData(), SUB_PROCESS_VARIABLES_JAVASCRIPT);

        EmbeddedSubprocess subProcessLevelSubProcessMVEL = getSubProcessNodeById(diagram,
                                                                                 SUBPROCESS_LEVEL_FILLED_SUBPROCESS_MVEL_ID,
                                                                                 EMPTY_INCOME_EDGES,
                                                                                 EMPTY_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcessMVEL.getGeneral(), SUB_PROCESS_NAME_MVEL, SUB_PROCESS_DOCUMENTATION_MVEL);
        assertEmbeddedSubprocessExecutionSet(subProcessLevelSubProcessMVEL.getExecutionSet(),
                                             IS_ASYNC,
                                             SUBPROCESS_ON_ENTRY_ACTION_MVEL,
                                             SUBPROCESS_SCRIPT_MVEL_LANGUAGE,
                                             SUBPROCESS_ON_EXIT_ACTION_MVEL,
                                             SUBPROCESS_SCRIPT_MVEL_LANGUAGE);
        assertSubProcessProcessData(subProcessLevelSubProcessMVEL.getProcessData(), SUB_PROCESS_VARIABLES_MVEL);
    }

    @Test
    public void testUnmarshallSubProcessLevelSubProcessWithEdges() {
        final String SUB_PROCESS_NAME = "Sub-process08";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EmbeddedSubprocess subProcessLevelSubProcess = getSubProcessNodeById(diagram,
                                                                             SUBPROCESS_LEVEL_SUBPROCESS_WITH_EDGES,
                                                                             ONE_INCOME_EDGE,
                                                                             FOUR_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcess.getGeneral(), SUB_PROCESS_NAME, DEFAULT_DOCUMENTATION);
        assertEmbeddedSubprocessExecutionSet(subProcessLevelSubProcess.getExecutionSet(),
                                             IS_NOT_ASYNC,
                                             EMPTY_VALUE,
                                             SUBPROCESS_SCRIPT_JAVA_LANGUAGE,
                                             EMPTY_VALUE,
                                             SUBPROCESS_SCRIPT_JAVA_LANGUAGE);
        assertSubProcessProcessData(subProcessLevelSubProcess.getProcessData(), EMPTY_VALUE);
    }

    @Override
    Class<EmbeddedSubprocess> getSubProcessType() {
        return EmbeddedSubprocess.class;
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

    private void assertEmbeddedSubprocessExecutionSet(EmbeddedSubprocessExecutionSet executionSet,
                                                      boolean isAsync,
                                                      String onEntryActionScriptValue,
                                                      String onEntryActionScriptLanguage,
                                                      String onExitActionScriptValue,
                                                      String onExitActionScriptLanguage) {
        assertThat(executionSet).isNotNull();
        assertThat(executionSet.getIsAsync()).isNotNull();

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

        assertThat(executionSet.getIsAsync().getValue()).isEqualTo(isAsync);

        assertThat(onEntryScriptTypeValues.get(0).getScript()).isEqualTo(onEntryActionScriptValue);
        assertThat(onEntryScriptTypeValues.get(0).getLanguage()).isEqualTo(onEntryActionScriptLanguage);
        assertThat(onExitScriptTypeValues.get(0).getScript()).isEqualTo(onExitActionScriptValue);
        assertThat(onExitScriptTypeValues.get(0).getLanguage()).isEqualTo(onExitActionScriptLanguage);
    }

    @Test
    public void testMarshallEmbeddedCoords() throws Exception {
        final String BPMN_EMBEDDED_SUBPROCESS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/embeddedSubprocess.bpmn";
        String END_EVENT = "shape__FF050977-4D13-47F1-8B9B-D68FDE208666";

        Diagram<Graph, Metadata> diagram = getSpecificDiagram(BPMN_EMBEDDED_SUBPROCESS);

        // we start converting from the root, then pull out the result
        DefinitionsConverter definitionsConverter =
                new DefinitionsConverter(diagram.getGraph());

        Definitions definitions =
                definitionsConverter.toDefinitions();

        List<DiagramElement> planeElement = definitions.getDiagrams().get(0).getPlane().getPlaneElement();
        BPMNShape diagramElement = planeElement.stream()
                .filter(BPMNShape.class::isInstance)
                .map(s -> (BPMNShape) s)
                .filter(el -> el.getId().equals(END_EVENT)).findFirst().get();
        assertThat(diagramElement.getBounds().getX()).isEqualTo(885f);
        assertThat(diagramElement.getBounds().getY()).isEqualTo(143f);
    }
}
