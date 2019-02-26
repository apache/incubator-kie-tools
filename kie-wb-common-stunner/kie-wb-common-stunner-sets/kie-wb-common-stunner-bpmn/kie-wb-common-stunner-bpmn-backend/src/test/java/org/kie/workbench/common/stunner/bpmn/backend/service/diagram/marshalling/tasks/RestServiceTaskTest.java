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

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

public class RestServiceTaskTest extends org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.tasks.ServiceTask<ServiceTask> {

    private static final String BPMN_TASK_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/restServiceTasks.bpmn";

    private static final String EMPTY_TOP_LEVEL_TASK_ID = "_8C213283-8ED2-4BDB-B480-5DBBF09C7D72";
    private static final String FILLED_TOP_LEVEL_TASK_JAVA_ID = "_E36FC9D2-2C92-449F-A911-60D6E0A5A066";
    private static final String FILLED_TOP_LEVEL_TASK_JAVASCRIPT_ID = "_22C693C6-015C-4EA8-95DB-BFE877C8F16B";
    private static final String FILLED_TOP_LEVEL_TASK_MVEL_ID = "_829407B2-8980-41B9-8688-22E392C8F3D4";

    private static final String EMPTY_SUBPROCESS_LEVEL_TASK_ID = "_97651B24-CA61-4563-B624-067042A52BAB";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_JAVA_ID = "_9D056204-192D-43B8-8675-D40ADC632D9F";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "_1691F474-6308-4067-9500-D8DC78BD85BE";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_MVEL_ID = "_86ED7127-B4A7-497C-B367-6830EA181EB5";

    private static final String EMPTY_ONE_INCOME_SUBPROCESS_LEVEL_TASK_ID = "_1CF02831-FD6B-4EF3-A421-E3408394C163";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVA_ID = "_806F5522-E200-44D7-B20F-4422E87FDDA7";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "_006987AA-76A5-412E-885D-C7B3025581E5";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_MVEL_ID = "_9F36E90E-2A28-4074-9091-8BC0D1368FD4";

    private static final String EMPTY_ONE_INCOME_TOP_LEVEL_TASK_ID = "_F22F6E7E-F5CA-4D93-ADCD-DB1F14D7FD02";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVA_ID = "_32668A63-DFEA-4995-BC67-2AFC13EB157D";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVASCRIPT_ID = "_C05933AC-EF40-49FA-A9CE-20E133AEC088";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_MVEL_ID = "_3CCFE05A-EF1E-41EE-87E0-63AEA5E859C1";

    private static final String EMPTY_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_ID = "_62D7C457-8263-46D5-BF3D-F7B41075D29D";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVA_ID = "_491735A0-7C85-49C2-8449-DD3BC2BC6A9B";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "_0167A624-220A-407A-BC61-A195C209D328";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_MVEL_ID = "_06AF6DF2-035C-4DB7-9EC3-5AE61FF3E7FF";

    private static final String EMPTY_TWO_INCOMES_TOP_LEVEL_TASK_ID = "_3E6B6E8B-C444-44E7-B32C-916296C8D979";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVA_ID = "_D0221D6B-4088-4E8F-9ED9-3568D8E1491C";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVASCRIPT_ID = "_800088AB-06A0-437C-8556-41F15A4C9680";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_MVEL_ID = "_388BB578-C003-4F31-9986-347783750D84";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 69;

    private static final String EMPTY_TASK_DATA_INPUT_OUTPUT = "||||";
    private static final String TASK_SCRIPT_JAVA_LANGUAGE = "java";
    private static final String TASK_SCRIPT_JAVASCRIPT_LANGUAGE = "javascript";
    private static final String TASK_SCRIPT_MVEL_LANGUAGE = "mvel";
    private static final boolean IS_ASYNC = true;
    private static final boolean IS_NOT_ASYNC = false;
    private static final boolean AD_HOC_AUTOSTART = true;
    private static final boolean NOT_AD_HOC_AUTOSTART = false;

    private static Diagram<Graph, Metadata> oldDiagram;
    private static Diagram<Graph, Metadata> oldRoundTripDiagram;

    private static Diagram<Graph, Metadata> newDiagram;
    private static Diagram<Graph, Metadata> newRoundTripDiagram;

    public RestServiceTaskTest(Marshaller marshallerType) throws Exception {
        super(marshallerType);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskFilledProperties() {
        final String TASK_NAME_JAVA = "Rest task01 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Rest task01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Rest Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Rest Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Rest task04 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Rest task04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Rest Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Rest Task.\");";

        final String TASK_NAME_MVEL = "Rest task07 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Rest task07 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Rest Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Rest Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|ConnectTimeout:String,ContentData:String,Method:String,Password:String,ReadTimeout:String,Url:String,Username:String,input:String||Result:java.lang.Object,output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask filledTopLevelTaskJava = getTaskNodeById(getDiagram(),
                                                             FILLED_TOP_LEVEL_TASK_JAVA_ID,
                                                             ZERO_INCOME_EDGES,
                                                             HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertServiceTaskExecutionSet(filledTopLevelTaskJava.getExecutionSet(),
                                      TASK_ON_ENTRY_ACTION_JAVA,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      TASK_ON_EXIT_ACTION_JAVA,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      IS_ASYNC,
                                      AD_HOC_AUTOSTART);
        assertDataIOSet(filledTopLevelTaskJava.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        ServiceTask filledTopLevelTaskJavascript = getTaskNodeById(getDiagram(),
                                                                   FILLED_TOP_LEVEL_TASK_JAVASCRIPT_ID,
                                                                   ZERO_INCOME_EDGES,
                                                                   HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertServiceTaskExecutionSet(filledTopLevelTaskJavascript.getExecutionSet(),
                                      TASK_ON_ENTRY_ACTION_JAVASCRIPT,
                                      TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                      TASK_ON_EXIT_ACTION_JAVASCRIPT,
                                      TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                      IS_ASYNC,
                                      AD_HOC_AUTOSTART);
        assertDataIOSet(filledTopLevelTaskJavascript.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        ServiceTask filledTopLevelTaskMvel = getTaskNodeById(getDiagram(),
                                                             FILLED_TOP_LEVEL_TASK_MVEL_ID,
                                                             ZERO_INCOME_EDGES,
                                                             HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertServiceTaskExecutionSet(filledTopLevelTaskMvel.getExecutionSet(),
                                      TASK_ON_ENTRY_ACTION_MVEL,
                                      TASK_SCRIPT_MVEL_LANGUAGE,
                                      TASK_ON_EXIT_ACTION_MVEL,
                                      TASK_SCRIPT_MVEL_LANGUAGE,
                                      IS_ASYNC,
                                      AD_HOC_AUTOSTART);
        assertDataIOSet(filledTopLevelTaskMvel.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyTaskProperties() {
        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask emptyTopLevelTask = getTaskNodeById(getDiagram(),
                                                        EMPTY_TOP_LEVEL_TASK_ID,
                                                        ZERO_INCOME_EDGES,
                                                        HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(emptyTopLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE); // Rest, null

        assertServiceTaskExecutionSet(emptyTopLevelTask.getExecutionSet(),
                                      EMPTY_VALUE,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      EMPTY_VALUE,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      IS_NOT_ASYNC,
                                      NOT_AD_HOC_AUTOSTART);
        assertDataIOSet(emptyTopLevelTask.getDataIOSet(), EMPTY_TASK_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskFilledProperties() {
        final String TASK_NAME_JAVA = "Rest task10 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Rest task10 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Rest Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Rest Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Rest task13 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Rest task13 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Rest Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Rest Task.\");";

        final String TASK_NAME_MVEL = "Rest task16 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Rest task16 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Rest Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Rest Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|ConnectTimeout:String,ContentData:String,Method:String,Password:String,ReadTimeout:String,Url:String,Username:String,input:String||Result:java.lang.Object,output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask filledSubprocessLevelTaskJava = getTaskNodeById(getDiagram(),
                                                                    FILLED_SUBPROCESS_LEVEL_TASK_JAVA_ID,
                                                                    ZERO_INCOME_EDGES,
                                                                    HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertServiceTaskExecutionSet(filledSubprocessLevelTaskJava.getExecutionSet(),
                                      TASK_ON_ENTRY_ACTION_JAVA,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      TASK_ON_EXIT_ACTION_JAVA,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      IS_ASYNC,
                                      AD_HOC_AUTOSTART);
        assertDataIOSet(filledSubprocessLevelTaskJava.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        ServiceTask filledSubprocessLevelTaskJavascript = getTaskNodeById(getDiagram(),
                                                                          FILLED_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID,
                                                                          ZERO_INCOME_EDGES,
                                                                          HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertServiceTaskExecutionSet(filledSubprocessLevelTaskJavascript.getExecutionSet(),
                                      TASK_ON_ENTRY_ACTION_JAVASCRIPT,
                                      TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                      TASK_ON_EXIT_ACTION_JAVASCRIPT,
                                      TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                      IS_ASYNC,
                                      AD_HOC_AUTOSTART);
        assertDataIOSet(filledSubprocessLevelTaskJavascript.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        ServiceTask filledSubprocessLevelTaskMvel = getTaskNodeById(getDiagram(),
                                                                    FILLED_SUBPROCESS_LEVEL_TASK_MVEL_ID,
                                                                    ZERO_INCOME_EDGES,
                                                                    HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertServiceTaskExecutionSet(filledSubprocessLevelTaskMvel.getExecutionSet(),
                                      TASK_ON_ENTRY_ACTION_MVEL,
                                      TASK_SCRIPT_MVEL_LANGUAGE,
                                      TASK_ON_EXIT_ACTION_MVEL,
                                      TASK_SCRIPT_MVEL_LANGUAGE,
                                      IS_ASYNC,
                                      AD_HOC_AUTOSTART);
        assertDataIOSet(filledSubprocessLevelTaskMvel.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskEmptyProperties() {
        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask emptySubprocessLevelTask = getTaskNodeById(getDiagram(),
                                                               EMPTY_SUBPROCESS_LEVEL_TASK_ID,
                                                               ZERO_INCOME_EDGES,
                                                               HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(emptySubprocessLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertServiceTaskExecutionSet(emptySubprocessLevelTask.getExecutionSet(),
                                      EMPTY_VALUE,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      EMPTY_VALUE,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      IS_NOT_ASYNC,
                                      NOT_AD_HOC_AUTOSTART);
        assertDataIOSet(emptySubprocessLevelTask.getDataIOSet(), EMPTY_TASK_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskOneIncomeFilledProperties() {
        final String TASK_NAME_JAVA = "Rest task02 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Rest task02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Rest Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Rest Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Rest task05 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Rest task05 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Rest Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Rest Task.\");";

        final String TASK_NAME_MVEL = "Rest task08 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Rest task08 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Rest Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Rest Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|ConnectTimeout:String,ContentData:String,Method:String,Password:String,ReadTimeout:String,Url:String,Username:String,input:String||Result:java.lang.Object,output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask filledTopLevelTaskJava = getTaskNodeById(getDiagram(),
                                                             FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVA_ID,
                                                             ONE_INCOME_EDGE,
                                                             HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertServiceTaskExecutionSet(filledTopLevelTaskJava.getExecutionSet(),
                                      TASK_ON_ENTRY_ACTION_JAVA,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      TASK_ON_EXIT_ACTION_JAVA,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      IS_ASYNC,
                                      AD_HOC_AUTOSTART);
        assertDataIOSet(filledTopLevelTaskJava.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        ServiceTask filledTopLevelTaskJavascript = getTaskNodeById(getDiagram(),
                                                                   FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVASCRIPT_ID,
                                                                   ONE_INCOME_EDGE,
                                                                   HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertServiceTaskExecutionSet(filledTopLevelTaskJavascript.getExecutionSet(),
                                      TASK_ON_ENTRY_ACTION_JAVASCRIPT,
                                      TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                      TASK_ON_EXIT_ACTION_JAVASCRIPT,
                                      TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                      IS_ASYNC,
                                      AD_HOC_AUTOSTART);
        assertDataIOSet(filledTopLevelTaskJavascript.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        ServiceTask filledTopLevelTaskMvel = getTaskNodeById(getDiagram(),
                                                             FILLED_ONE_INCOME_TOP_LEVEL_TASK_MVEL_ID,
                                                             ONE_INCOME_EDGE,
                                                             HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertServiceTaskExecutionSet(filledTopLevelTaskMvel.getExecutionSet(),
                                      TASK_ON_ENTRY_ACTION_MVEL,
                                      TASK_SCRIPT_MVEL_LANGUAGE,
                                      TASK_ON_EXIT_ACTION_MVEL,
                                      TASK_SCRIPT_MVEL_LANGUAGE,
                                      IS_ASYNC,
                                      AD_HOC_AUTOSTART);
        assertDataIOSet(filledTopLevelTaskMvel.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskOneIncomeEmptyProperties() {
        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask emptyTopLevelTask = getTaskNodeById(getDiagram(),
                                                        EMPTY_ONE_INCOME_TOP_LEVEL_TASK_ID,
                                                        ONE_INCOME_EDGE,
                                                        HAS_OUTCOME_EDGE);
        assertGeneralSet(emptyTopLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertServiceTaskExecutionSet(emptyTopLevelTask.getExecutionSet(),
                                      EMPTY_VALUE,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      EMPTY_VALUE,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      IS_NOT_ASYNC,
                                      NOT_AD_HOC_AUTOSTART);
        assertDataIOSet(emptyTopLevelTask.getDataIOSet(), EMPTY_TASK_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskOneIncomeEmptyProperties() {
        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask emptySubprocessLevelTask = getTaskNodeById(getDiagram(),
                                                               EMPTY_ONE_INCOME_SUBPROCESS_LEVEL_TASK_ID,
                                                               ONE_INCOME_EDGE,
                                                               HAS_OUTCOME_EDGE);
        assertGeneralSet(emptySubprocessLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertServiceTaskExecutionSet(emptySubprocessLevelTask.getExecutionSet(),
                                      EMPTY_VALUE,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      EMPTY_VALUE,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      IS_NOT_ASYNC,
                                      NOT_AD_HOC_AUTOSTART);
        assertDataIOSet(emptySubprocessLevelTask.getDataIOSet(), EMPTY_TASK_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskOneIncomeFilledProperties() {
        final String TASK_NAME_JAVA = "Rest task11 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Rest task11 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Rest Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Rest Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Rest task14 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Rest task14 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Rest Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Rest Task.\");";

        final String TASK_NAME_MVEL = "Rest task17 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Rest task17 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Rest Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Rest Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|ConnectTimeout:String,ContentData:String,Method:String,Password:String,ReadTimeout:String,Url:String,Username:String,input:String||Result:java.lang.Object,output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask filledSubprocessLevelTaskJava = getTaskNodeById(getDiagram(),
                                                                    FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVA_ID,
                                                                    ONE_INCOME_EDGE,
                                                                    HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertServiceTaskExecutionSet(filledSubprocessLevelTaskJava.getExecutionSet(),
                                      TASK_ON_ENTRY_ACTION_JAVA,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      TASK_ON_EXIT_ACTION_JAVA,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      IS_ASYNC,
                                      AD_HOC_AUTOSTART);
        assertDataIOSet(filledSubprocessLevelTaskJava.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        ServiceTask filledSubprocessLevelTaskJavascript = getTaskNodeById(getDiagram(),
                                                                          FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID,
                                                                          ONE_INCOME_EDGE,
                                                                          HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertServiceTaskExecutionSet(filledSubprocessLevelTaskJavascript.getExecutionSet(),
                                      TASK_ON_ENTRY_ACTION_JAVASCRIPT,
                                      TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                      TASK_ON_EXIT_ACTION_JAVASCRIPT,
                                      TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                      IS_ASYNC,
                                      AD_HOC_AUTOSTART);
        assertDataIOSet(filledSubprocessLevelTaskJavascript.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        ServiceTask filledSubprocessLevelTaskMvel = getTaskNodeById(getDiagram(),
                                                                    FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_MVEL_ID,
                                                                    ONE_INCOME_EDGE,
                                                                    HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertServiceTaskExecutionSet(filledSubprocessLevelTaskMvel.getExecutionSet(),
                                      TASK_ON_ENTRY_ACTION_MVEL,
                                      TASK_SCRIPT_MVEL_LANGUAGE,
                                      TASK_ON_EXIT_ACTION_MVEL,
                                      TASK_SCRIPT_MVEL_LANGUAGE,
                                      IS_ASYNC,
                                      AD_HOC_AUTOSTART);
        assertDataIOSet(filledSubprocessLevelTaskMvel.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskTwoIncomesFilledProperties() {
        final String TASK_NAME_JAVA = "Rest task03 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Rest task03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Rest Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Rest Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Rest task06 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Rest task06 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Rest Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Rest Task.\");";

        final String TASK_NAME_MVEL = "Rest task09 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Rest task09 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Rest Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Rest Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|ConnectTimeout:String,ContentData:String,Method:String,Password:String,ReadTimeout:String,Url:String,Username:String,input:String||Result:java.lang.Object,output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask filledTopLevelTaskJava = getTaskNodeById(getDiagram(),
                                                             FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVA_ID,
                                                             TWO_INCOME_EDGES,
                                                             HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertServiceTaskExecutionSet(filledTopLevelTaskJava.getExecutionSet(),
                                      TASK_ON_ENTRY_ACTION_JAVA,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      TASK_ON_EXIT_ACTION_JAVA,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      IS_ASYNC,
                                      AD_HOC_AUTOSTART);
        assertDataIOSet(filledTopLevelTaskJava.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        ServiceTask filledTopLevelTaskJavascript = getTaskNodeById(getDiagram(),
                                                                   FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVASCRIPT_ID,
                                                                   TWO_INCOME_EDGES,
                                                                   HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertServiceTaskExecutionSet(filledTopLevelTaskJavascript.getExecutionSet(),
                                      TASK_ON_ENTRY_ACTION_JAVASCRIPT,
                                      TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                      TASK_ON_EXIT_ACTION_JAVASCRIPT,
                                      TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                      IS_ASYNC,
                                      AD_HOC_AUTOSTART);
        assertDataIOSet(filledTopLevelTaskJavascript.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        ServiceTask filledTopLevelTaskMvel = getTaskNodeById(getDiagram(),
                                                             FILLED_TWO_INCOMES_TOP_LEVEL_TASK_MVEL_ID,
                                                             TWO_INCOME_EDGES,
                                                             HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertServiceTaskExecutionSet(filledTopLevelTaskMvel.getExecutionSet(),
                                      TASK_ON_ENTRY_ACTION_MVEL,
                                      TASK_SCRIPT_MVEL_LANGUAGE,
                                      TASK_ON_EXIT_ACTION_MVEL,
                                      TASK_SCRIPT_MVEL_LANGUAGE,
                                      IS_ASYNC,
                                      AD_HOC_AUTOSTART);
        assertDataIOSet(filledTopLevelTaskMvel.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskTwoIncomesEmptyProperties() {
        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask emptyTopLevelTask = getTaskNodeById(getDiagram(),
                                                        EMPTY_TWO_INCOMES_TOP_LEVEL_TASK_ID,
                                                        TWO_INCOME_EDGES,
                                                        HAS_OUTCOME_EDGE);
        assertGeneralSet(emptyTopLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertServiceTaskExecutionSet(emptyTopLevelTask.getExecutionSet(),
                                      EMPTY_VALUE,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      EMPTY_VALUE,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      IS_NOT_ASYNC,
                                      NOT_AD_HOC_AUTOSTART);
        assertDataIOSet(emptyTopLevelTask.getDataIOSet(), EMPTY_TASK_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskTwoIncomesEmptyProperties() {
        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask emptySubprocessLevelTask = getTaskNodeById(getDiagram(),
                                                               EMPTY_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_ID,
                                                               TWO_INCOME_EDGES,
                                                               HAS_OUTCOME_EDGE);
        assertGeneralSet(emptySubprocessLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertServiceTaskExecutionSet(emptySubprocessLevelTask.getExecutionSet(),
                                      EMPTY_VALUE,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      EMPTY_VALUE,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      IS_NOT_ASYNC,
                                      NOT_AD_HOC_AUTOSTART);
        assertDataIOSet(emptySubprocessLevelTask.getDataIOSet(), EMPTY_TASK_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskTwoIncomesFilledProperties() {
        final String TASK_NAME_JAVA = "Rest task12 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Rest task12 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Rest Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Rest Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Rest task15 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Rest task15 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Rest Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Rest Task.\");";

        final String TASK_NAME_MVEL = "Rest task18 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Rest task18 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Rest Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Rest Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|ConnectTimeout:String,ContentData:String,Method:String,Password:String,ReadTimeout:String,Url:String,Username:String,input:String||Result:java.lang.Object,output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask filledSubprocessLevelTaskJava = getTaskNodeById(getDiagram(),
                                                                    FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVA_ID,
                                                                    TWO_INCOME_EDGES,
                                                                    HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertServiceTaskExecutionSet(filledSubprocessLevelTaskJava.getExecutionSet(),
                                      TASK_ON_ENTRY_ACTION_JAVA,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      TASK_ON_EXIT_ACTION_JAVA,
                                      TASK_SCRIPT_JAVA_LANGUAGE,
                                      IS_ASYNC,
                                      AD_HOC_AUTOSTART);
        assertDataIOSet(filledSubprocessLevelTaskJava.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        ServiceTask filledSubprocessLevelTaskJavascript = getTaskNodeById(getDiagram(),
                                                                          FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID,
                                                                          TWO_INCOME_EDGES,
                                                                          HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertServiceTaskExecutionSet(filledSubprocessLevelTaskJavascript.getExecutionSet(),
                                      TASK_ON_ENTRY_ACTION_JAVASCRIPT,
                                      TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                      TASK_ON_EXIT_ACTION_JAVASCRIPT,
                                      TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                      IS_ASYNC,
                                      AD_HOC_AUTOSTART);
        assertDataIOSet(filledSubprocessLevelTaskJavascript.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        ServiceTask filledSubprocessLevelTaskMvel = getTaskNodeById(getDiagram(),
                                                                    FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_MVEL_ID,
                                                                    TWO_INCOME_EDGES,
                                                                    HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertServiceTaskExecutionSet(filledSubprocessLevelTaskMvel.getExecutionSet(),
                                      TASK_ON_ENTRY_ACTION_MVEL,
                                      TASK_SCRIPT_MVEL_LANGUAGE,
                                      TASK_ON_EXIT_ACTION_MVEL,
                                      TASK_SCRIPT_MVEL_LANGUAGE,
                                      IS_ASYNC,
                                      AD_HOC_AUTOSTART);
        assertDataIOSet(filledSubprocessLevelTaskMvel.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);
    }

    @Override
    Diagram<Graph, Metadata> getOldDiagram() {
        return oldDiagram;
    }

    @Override
    void setOldDiagram(Diagram<Graph, Metadata> diagram) {
        oldDiagram = diagram;
    }

    @Override
    Diagram<Graph, Metadata> getOldRoundTripDiagram() {
        return oldRoundTripDiagram;
    }

    @Override
    void setOldRoundTripDiagram(Diagram<Graph, Metadata> diagram) {
        oldRoundTripDiagram = diagram;
    }

    @Override
    Diagram<Graph, Metadata> getNewDiagram() {
        return newDiagram;
    }

    @Override
    void setNewDiagram(Diagram<Graph, Metadata> diagram) {
        newDiagram = diagram;
    }

    @Override
    Diagram<Graph, Metadata> getNewRoundTripDiagram() {
        return newRoundTripDiagram;
    }

    @Override
    void setNewRoundTripDiagram(Diagram<Graph, Metadata> diagram) {
        newRoundTripDiagram = diagram;
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
    Class<ServiceTask> getTaskType() {
        return ServiceTask.class;
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
}
