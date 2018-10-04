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

import org.junit.Ignore;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

public class LogServiceTaskTest extends org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.tasks.ServiceTask<ServiceTask> {

    private static final String BPMN_TASK_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/logServiceTasks.bpmn";

    private static final String EMPTY_TOP_LEVEL_TASK_ID = "_F371ADA8-07DF-4215-B750-CD9DB3646A44";
    private static final String FILLED_TOP_LEVEL_TASK_JAVA_ID = "_9A87BE2B-BD48-4035-89B9-628DB3007A6E";
    private static final String FILLED_TOP_LEVEL_TASK_JAVASCRIPT_ID = "_361EB120-042F-4A48-BA20-B33CE85A94C4";
    private static final String FILLED_TOP_LEVEL_TASK_MVEL_ID = "_6CD848E0-6DF3-44A6-BEE9-A67072A02199";

    private static final String EMPTY_SUBPROCESS_LEVEL_TASK_ID = "_DC8C1405-795C-424A-A250-DE79DF6207B3";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_JAVA_ID = "_A629F9C9-0163-49D6-A0B9-17A6968F61DA";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "_6F287EA8-9FAF-461E-9E26-1FB287009207";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_MVEL_ID = "_923A2D3E-2DA0-454F-A645-E6C5DD754486";

    private static final String EMPTY_ONE_INCOME_SUBPROCESS_LEVEL_TASK_ID = "_1F349EA1-93E2-4108-B634-1B09F26C7237";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVA_ID = "_47C5492F-8031-4781-85A9-5D5F5799BE18";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "_996A029F-C6D4-41B9-A0CB-861FAD7C55A4";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_MVEL_ID = "_4CC87D2B-BAC5-4758-9206-CE6BE96C1EA8";

    private static final String EMPTY_ONE_INCOME_TOP_LEVEL_TASK_ID = "_A2C81422-DD5D-4BB9-A88F-3E623AC5493A";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVA_ID = "_D5B33025-E4E3-4515-9D5A-B1EC6AB88F78";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVASCRIPT_ID = "_7169D7D7-54EA-4647-969F-A3C4D691FA8B";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_MVEL_ID = "_1123E57A-A8FF-4B9B-B4FF-BB5CA015E2FD";

    private static final String EMPTY_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_ID = "_F238B986-4CA8-46E2-98EA-55962D05DB48";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVA_ID = "_A8EF686B-18E3-4B61-B91F-78985DDA41E7";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "_44108ABD-CDD0-4BE9-9E8F-16DA829E4737";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_MVEL_ID = "_9B841422-9B0D-42A5-B084-ADFA089249E0";

    private static final String EMPTY_TWO_INCOMES_TOP_LEVEL_TASK_ID = "_A7F4082C-E768-49C1-99CB-4751052595E8";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVA_ID = "_0ED3105D-CFA5-409F-B4EC-8D775FC185E3";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVASCRIPT_ID = "_61FF614E-412C-4E38-BEFF-E917DFAC1A58";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_MVEL_ID = "_05A742D4-7C8F-497B-B670-F8CA06B53727";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 69;

    private static final String EMPTY_TASK_DATA_INPUT_OUTPUT = "||||";
    private static final String TASK_SCRIPT_JAVA_LANGUAGE = "java";
    private static final String TASK_SCRIPT_JAVASCRIPT_LANGUAGE = "javascript";
    private static final String TASK_SCRIPT_MVEL_LANGUAGE = "mvel";
    private static final boolean IS_ASYNC = true;
    private static final boolean IS_NOT_ASYNC = false;
    private static final boolean AD_HOC_AUTOSTART = true;
    private static final boolean NOT_AD_HOC_AUTOSTART = false;

    public LogServiceTaskTest(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskFilledProperties() throws Exception {
        final String TASK_NAME_JAVA = "Log task01 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Log task01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Log Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Log Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Log task04 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Log task04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Log Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Log Task.\");";

        final String TASK_NAME_MVEL = "Log task07 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Log task07 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Log Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Log Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|Message:String,input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask filledTopLevelTaskJava = getTaskNodeById(diagram,
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

        ServiceTask filledTopLevelTaskJavascript = getTaskNodeById(diagram,
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

        ServiceTask filledTopLevelTaskMvel = getTaskNodeById(diagram,
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

    @Ignore("The test is ignored because there is a bug in new Marshaller.\n" +
            "It should be enabled after these issues will be resolved:\n" +
            "https://issues.jboss.org/browse/JBPM-7072\n" +
            "https://issues.jboss.org/browse/JBPM-7726")
    @Test
    @Override
    public void testUnmarshallTopLevelEmptyTaskProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask emptyTopLevelTask = getTaskNodeById(diagram,
                                                        EMPTY_TOP_LEVEL_TASK_ID,
                                                        ZERO_INCOME_EDGES,
                                                        HAS_NO_OUTCOME_EDGE);
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
    public void testUnmarshallSubprocessLevelTaskFilledProperties() throws Exception {
        final String TASK_NAME_JAVA = "Log task10 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Log task10 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Log Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Log Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Log task13 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Log task13 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Log Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Log Task.\");";

        final String TASK_NAME_MVEL = "Log task16 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Log task16 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Log Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Log Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|Message:String,input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask filledSubprocessLevelTaskJava = getTaskNodeById(diagram,
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

        ServiceTask filledSubprocessLevelTaskJavascript = getTaskNodeById(diagram,
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

        ServiceTask filledSubprocessLevelTaskMvel = getTaskNodeById(diagram,
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

    @Ignore("The test is ignored because there is a bug in new Marshaller.\n" +
            "It should be enabled after these issues will be resolved:\n" +
            "https://issues.jboss.org/browse/JBPM-7072\n" +
            "https://issues.jboss.org/browse/JBPM-7726")
    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask emptySubprocessLevelTask = getTaskNodeById(diagram,
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
    public void testUnmarshallTopLevelTaskOneIncomeFilledProperties() throws Exception {
        final String TASK_NAME_JAVA = "Log task02 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Log task02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Log Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Log Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Log task05 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Log task05 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Log Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Log Task.\");";

        final String TASK_NAME_MVEL = "Log task08 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Log task08 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Log Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Log Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|Message:String,input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask filledTopLevelTaskJava = getTaskNodeById(diagram,
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

        ServiceTask filledTopLevelTaskJavascript = getTaskNodeById(diagram,
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

        ServiceTask filledTopLevelTaskMvel = getTaskNodeById(diagram,
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

    @Ignore("The test is ignored because there is a bug in new Marshaller.\n" +
            "It should be enabled after these issues will be resolved:\n" +
            "https://issues.jboss.org/browse/JBPM-7072\n" +
            "https://issues.jboss.org/browse/JBPM-7726")
    @Test
    @Override
    public void testUnmarshallTopLevelTaskOneIncomeEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask emptyTopLevelTask = getTaskNodeById(diagram,
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

    @Ignore("The test is ignored because there is a bug in new Marshaller.\n" +
            "It should be enabled after these issues will be resolved:\n" +
            "https://issues.jboss.org/browse/JBPM-7072\n" +
            "https://issues.jboss.org/browse/JBPM-7726")
    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskOneIncomeEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask emptySubprocessLevelTask = getTaskNodeById(diagram,
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
    public void testUnmarshallSubprocessLevelTaskOneIncomeFilledProperties() throws Exception {
        final String TASK_NAME_JAVA = "Log task11 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Log task11 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Log Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Log Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Log task14 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Log task14 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Log Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Log Task.\");";

        final String TASK_NAME_MVEL = "Log task17 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Log task17 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Log Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Log Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|Message:String,input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask filledSubprocessLevelTaskJava = getTaskNodeById(diagram,
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

        ServiceTask filledSubprocessLevelTaskJavascript = getTaskNodeById(diagram,
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

        ServiceTask filledSubprocessLevelTaskMvel = getTaskNodeById(diagram,
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
    public void testUnmarshallTopLevelTaskTwoIncomesFilledProperties() throws Exception {
        final String TASK_NAME_JAVA = "Log task03 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Log task03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Log Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Log Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Log task06 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Log task06 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Log Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Log Task.\");";

        final String TASK_NAME_MVEL = "Log task09 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Log task09 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Log Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Log Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|Message:String,input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask filledTopLevelTaskJava = getTaskNodeById(diagram,
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

        ServiceTask filledTopLevelTaskJavascript = getTaskNodeById(diagram,
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

        ServiceTask filledTopLevelTaskMvel = getTaskNodeById(diagram,
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

    @Ignore("The test is ignored because there is a bug in new Marshaller.\n" +
            "It should be enabled after these issues will be resolved:\n" +
            "https://issues.jboss.org/browse/JBPM-7072\n" +
            "https://issues.jboss.org/browse/JBPM-7726")
    @Test
    @Override
    public void testUnmarshallTopLevelTaskTwoIncomesEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask emptyTopLevelTask = getTaskNodeById(diagram,
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

    @Ignore("The test is ignored because there is a bug in new Marshaller.\n" +
            "It should be enabled after these issues will be resolved:\n" +
            "https://issues.jboss.org/browse/JBPM-7072\n" +
            "https://issues.jboss.org/browse/JBPM-7726")
    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskTwoIncomesEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask emptySubprocessLevelTask = getTaskNodeById(diagram,
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
    public void testUnmarshallSubprocessLevelTaskTwoIncomesFilledProperties() throws Exception {
        final String TASK_NAME_JAVA = "Log task12 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Log task12 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Log Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Log Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Log task15 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Log task15 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Log Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Log Task.\");";

        final String TASK_NAME_MVEL = "Log task18 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Log task18 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Log Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Log Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|Message:String,input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask filledSubprocessLevelTaskJava = getTaskNodeById(diagram,
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

        ServiceTask filledSubprocessLevelTaskJavascript = getTaskNodeById(diagram,
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

        ServiceTask filledSubprocessLevelTaskMvel = getTaskNodeById(diagram,
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

    @Test
    @Override
    public void testMarshallTopLevelTaskFilledProperties() throws Exception {
        checkTaskMarshalling(FILLED_TOP_LEVEL_TASK_JAVA_ID, ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_TOP_LEVEL_TASK_JAVASCRIPT_ID, ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_TOP_LEVEL_TASK_MVEL_ID, ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelTaskFilledProperties() throws Exception {
        checkTaskMarshalling(FILLED_SUBPROCESS_LEVEL_TASK_JAVA_ID, ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID, ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_SUBPROCESS_LEVEL_TASK_MVEL_ID, ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
    }

    @Test
    @Override
    public void testMarshallTopLevelTaskOneIncomeFilledProperties() throws Exception {
        checkTaskMarshalling(FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVA_ID, ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVASCRIPT_ID, ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_ONE_INCOME_TOP_LEVEL_TASK_MVEL_ID, ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelTaskOneIncomeFilledProperties() throws Exception {
        checkTaskMarshalling(FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVA_ID, ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID, ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_MVEL_ID, ONE_INCOME_EDGE, HAS_OUTCOME_EDGE);
    }

    @Test
    @Override
    public void testMarshallTopLevelTaskTwoIncomesFilledProperties() throws Exception {
        checkTaskMarshalling(FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVA_ID, TWO_INCOME_EDGES, HAS_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVASCRIPT_ID, TWO_INCOME_EDGES, HAS_OUTCOME_EDGE);
        checkTaskMarshalling(FILLED_TWO_INCOMES_TOP_LEVEL_TASK_MVEL_ID, TWO_INCOME_EDGES, HAS_OUTCOME_EDGE);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelTaskTwoIncomesFilledProperties() throws Exception {
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
