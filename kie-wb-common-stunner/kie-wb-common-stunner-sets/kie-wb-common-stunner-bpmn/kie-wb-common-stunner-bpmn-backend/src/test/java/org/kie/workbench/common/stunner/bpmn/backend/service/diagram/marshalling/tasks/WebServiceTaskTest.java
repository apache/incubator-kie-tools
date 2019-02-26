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

public class WebServiceTaskTest extends org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.tasks.ServiceTask<ServiceTask> {

    private static final String BPMN_TASK_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/webServiceTasks.bpmn";

    private static final String EMPTY_TOP_LEVEL_TASK_ID = "_5A6F1F2B-0DA7-4CC9-A0DC-3E084C1CC04B";
    private static final String FILLED_TOP_LEVEL_TASK_JAVA_ID = "_A1F87E6F-4661-40A0-82FD-40EFA8B4EB0D";
    private static final String FILLED_TOP_LEVEL_TASK_JAVASCRIPT_ID = "_CECED7C4-5723-4A2A-87ED-C335149B2B09";
    private static final String FILLED_TOP_LEVEL_TASK_MVEL_ID = "_88C979F8-BA89-45C5-B537-E0A69354C77D";

    private static final String EMPTY_SUBPROCESS_LEVEL_TASK_ID = "_5D642034-18F7-4923-B5A5-522E7DA239A6";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_JAVA_ID = "_35A8D726-E7BD-4C1C-BE59-F4798BFE7D41";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "_597F6FAF-BE24-4D85-AF01-B97B98404A03";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_MVEL_ID = "_68B36482-F5F7-401D-A3A1-A9F396F5BB65";

    private static final String EMPTY_ONE_INCOME_SUBPROCESS_LEVEL_TASK_ID = "_B31AD4AA-05F4-4FFC-931D-3D2B08FC5BD2";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVA_ID = "_52DDB629-4972-4322-857A-FC3784F063C9";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "_C79B91B0-0EC1-47B4-B4C6-0EBECFE86BFE";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_MVEL_ID = "_C6C31E27-BC23-4F39-B842-1BB7E46A732B";

    private static final String EMPTY_ONE_INCOME_TOP_LEVEL_TASK_ID = "_3DAE301C-D97B-46FA-819B-D977C9C4E40D";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVA_ID = "_93B7896F-D263-46D8-BC54-9E117FB426C3";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVASCRIPT_ID = "_B413F625-D2B0-4243-88F1-15C275695752";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_MVEL_ID = "_EBAEE436-8140-4E58-8C74-D31EE2225D9E";

    private static final String EMPTY_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_ID = "_CED3E374-DC2F-49C3-9793-66E6DD305CFF";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVA_ID = "_C9E2A420-7C21-4E30-BE2A-36583F0A0C07";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "_098C8C0E-A580-4CA6-B4C0-604E934BD2A8";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_MVEL_ID = "_914BF590-F197-4294-86BF-9978E2FF1AED";

    private static final String EMPTY_TWO_INCOMES_TOP_LEVEL_TASK_ID = "_34A5EEA0-7164-4DF3-B321-5651606EFF70";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVA_ID = "_48F6449C-CF57-4BE7-9778-32BABF22228B";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVASCRIPT_ID = "_32F3054C-C641-409A-8EA4-2857801DAE7A";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_MVEL_ID = "_43B109C6-A65C-40DF-97DF-33723FC77B3B";

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

    public WebServiceTaskTest(Marshaller marshallerType) throws Exception {
        super(marshallerType);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskFilledProperties() {
        final String TASK_NAME_JAVA = "WebService task01 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "WebService task01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from WebService Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from WebService Task.\");";

        final String TASK_NAME_JAVASCRIPT = "WebService task04 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "WebService task04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from WebService Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from WebService Task.\");";

        final String TASK_NAME_MVEL = "WebService task07 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "WebService task07 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from WebService Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from WebService Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|Endpoint:String,Interface:String,Mode:String,Namespace:String,Operation:String,Parameter:String,Url:String,input:String||Result:java.lang.Object,output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

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

    @Ignore("The test is ignored because there is a bug in new Marshaller.\n" +
            "It should be enabled after these issues will be resolved:\n" +
            "https://issues.jboss.org/browse/JBPM-7072\n" +
            "https://issues.jboss.org/browse/JBPM-7726")
    @Test
    @Override
    public void testUnmarshallTopLevelEmptyTaskProperties() {
        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ServiceTask emptyTopLevelTask = getTaskNodeById(getDiagram(),
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
    public void testUnmarshallSubprocessLevelTaskFilledProperties() {
        final String TASK_NAME_JAVA = "WebService task10 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "WebService task10 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from WebService Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from WebService Task.\");";

        final String TASK_NAME_JAVASCRIPT = "WebService task13 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "WebService task13 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from WebService Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from WebService Task.\");";

        final String TASK_NAME_MVEL = "WebService task16 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "WebService task16 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from WebService Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from WebService Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|Endpoint:String,Interface:String,Mode:String,Namespace:String,Operation:String,Parameter:String,Url:String,input:String||Result:java.lang.Object,output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

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

    @Ignore("The test is ignored because there is a bug in new Marshaller.\n" +
            "It should be enabled after these issues will be resolved:\n" +
            "https://issues.jboss.org/browse/JBPM-7072\n" +
            "https://issues.jboss.org/browse/JBPM-7726")
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
        final String TASK_NAME_JAVA = "WebService task02 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "WebService task02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from WebService Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from WebService Task.\");";

        final String TASK_NAME_JAVASCRIPT = "WebService task05 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "WebService task05 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from WebService Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from WebService Task.\");";

        final String TASK_NAME_MVEL = "WebService task08 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "WebService task08 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from WebService Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from WebService Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|Endpoint:String,Interface:String,Mode:String,Namespace:String,Operation:String,Parameter:String,Url:String,input:String||Result:java.lang.Object,output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

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

    @Ignore("The test is ignored because there is a bug in new Marshaller.\n" +
            "It should be enabled after these issues will be resolved:\n" +
            "https://issues.jboss.org/browse/JBPM-7072\n" +
            "https://issues.jboss.org/browse/JBPM-7726")
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

    @Ignore("The test is ignored because there is a bug in new Marshaller.\n" +
            "It should be enabled after these issues will be resolved:\n" +
            "https://issues.jboss.org/browse/JBPM-7072\n" +
            "https://issues.jboss.org/browse/JBPM-7726")
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
        final String TASK_NAME_JAVA = "WebService task11 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "WebService task11 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from WebService Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from WebService Task.\");";

        final String TASK_NAME_JAVASCRIPT = "WebService task14 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "WebService task14 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from WebService Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from WebService Task.\");";

        final String TASK_NAME_MVEL = "WebService task17 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "WebService task17 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from WebService Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from WebService Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|Endpoint:String,Interface:String,Mode:String,Namespace:String,Operation:String,Parameter:String,Url:String,input:String||Result:java.lang.Object,output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

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
        final String TASK_NAME_JAVA = "WebService task03 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "WebService task03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from WebService Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from WebService Task.\");";

        final String TASK_NAME_JAVASCRIPT = "WebService task06 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "WebService task06 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from WebService Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from WebService Task.\");";

        final String TASK_NAME_MVEL = "WebService task09 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "WebService task09 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from WebService Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from WebService Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|Endpoint:String,Interface:String,Mode:String,Namespace:String,Operation:String,Parameter:String,Url:String,input:String||Result:java.lang.Object,output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

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

    @Ignore("The test is ignored because there is a bug in new Marshaller.\n" +
            "It should be enabled after these issues will be resolved:\n" +
            "https://issues.jboss.org/browse/JBPM-7072\n" +
            "https://issues.jboss.org/browse/JBPM-7726")
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

    @Ignore("The test is ignored because there is a bug in new Marshaller.\n" +
            "It should be enabled after these issues will be resolved:\n" +
            "https://issues.jboss.org/browse/JBPM-7072\n" +
            "https://issues.jboss.org/browse/JBPM-7726")
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
        final String TASK_NAME_JAVA = "WebService task12 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "WebService task12 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from WebService Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from WebService Task.\");";

        final String TASK_NAME_JAVASCRIPT = "WebService task15 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "WebService task15 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from WebService Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from WebService Task.\");";

        final String TASK_NAME_MVEL = "WebService task18 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "WebService task18 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from WebService Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from WebService Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|Endpoint:String,Interface:String,Mode:String,Namespace:String,Operation:String,Parameter:String,Url:String,input:String||Result:java.lang.Object,output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

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
