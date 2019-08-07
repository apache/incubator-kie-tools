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
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;

public class EmailServiceTaskTest extends BaseServiceTaskTest<ServiceTask> {

    private static final String BPMN_TASK_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/emailServiceTasks.bpmn";

    private static final String EMPTY_TOP_LEVEL_TASK_ID = "_E9FCC849-3CA2-45EE-9D82-1DF4943ED5D5";
    private static final String FILLED_TOP_LEVEL_TASK_JAVA_ID = "_0DB9E25C-DB6D-4523-B93F-5FB8B06E641B";
    private static final String FILLED_TOP_LEVEL_TASK_JAVASCRIPT_ID = "_6D492D46-452E-4325-A173-95E45F162D2F";
    private static final String FILLED_TOP_LEVEL_TASK_MVEL_ID = "_3FD1AC6E-DF1F-4216-B264-3B0F39B5C456";

    private static final String EMPTY_SUBPROCESS_LEVEL_TASK_ID = "_1503949A-282E-47EA-9FBF-D97169BC7032";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_JAVA_ID = "_720E61CF-E5BC-4502-892F-94F733DFA472";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "_BD87C14A-25FC-4A89-9166-C75A401630C1";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_MVEL_ID = "_464DB47C-DB7E-46DB-A01E-73316E224E5E";

    private static final String EMPTY_ONE_INCOME_SUBPROCESS_LEVEL_TASK_ID = "_B559A21B-BA5F-478C-88DC-E55266B0F1B8";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVA_ID = "_80E21A45-5F93-4C1A-9500-A0B874844E69";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "_99CAF167-96D5-43B7-B971-563BACDEB8BD";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_MVEL_ID = "_5B167803-E9CF-417C-89FD-076614F64662";

    private static final String EMPTY_ONE_INCOME_TOP_LEVEL_TASK_ID = "_8702AF93-BA74-433C-BDF5-355447EC01D1";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVA_ID = "_B6F7C712-EADC-4DEA-ACC9-D291A13B86E3";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVASCRIPT_ID = "_A775A42C-7E90-441F-8757-B877CC2548D8";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_MVEL_ID = "_792EAC3D-AFEE-4876-A47F-74C84519527F";

    private static final String EMPTY_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_ID = "_46FCD1B0-0D9D-4FE6-97E7-897BCC0E8123";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVA_ID = "_4D90EE12-4931-47B2-8A96-DD48671F736E";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "_41D5F885-3642-4705-8858-2EBE700115BE";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_MVEL_ID = "_7A5403EB-596A-45F8-91B4-86A194A2560D";

    private static final String EMPTY_TWO_INCOMES_TOP_LEVEL_TASK_ID = "_47E10EF2-1922-48FD-8202-C565D48D34B1";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVA_ID = "_0BDF4C17-FE34-4240-A415-6E21CDB1E86A";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVASCRIPT_ID = "_CC5D0205-42B1-4232-BAEE-64227F611C40";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_MVEL_ID = "_C95FA20B-538C-4803-B73C-A6C081E56840";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 69;

    private static final String EMPTY_TASK_DATA_INPUT_OUTPUT = "||||";
    private static final String TASK_SCRIPT_JAVA_LANGUAGE = "java";
    private static final String TASK_SCRIPT_JAVASCRIPT_LANGUAGE = "javascript";
    private static final String TASK_SCRIPT_MVEL_LANGUAGE = "mvel";
    private static final boolean IS_ASYNC = true;
    private static final boolean IS_NOT_ASYNC = false;
    private static final boolean AD_HOC_AUTOSTART = true;
    private static final boolean NOT_AD_HOC_AUTOSTART = false;

    public EmailServiceTaskTest() throws Exception {
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskFilledProperties() {
        final String TASK_NAME_JAVA = "Email task01 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Email task01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Email Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Email Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Email task04 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Email task04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Email Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Email Task.\");";

        final String TASK_NAME_MVEL = "Email task07 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Email task07 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Email Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Email Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|Body:String,From:String,Subject:String,To:String,input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

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
                                      AD_HOC_AUTOSTART,
                                      SLA_DUE_DATE);
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
                                      AD_HOC_AUTOSTART,
                                      SLA_DUE_DATE);
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
                                      AD_HOC_AUTOSTART,
                                      SLA_DUE_DATE);
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
                                      NOT_AD_HOC_AUTOSTART,
                                      EMPTY_VALUE);
        assertDataIOSet(emptyTopLevelTask.getDataIOSet(), EMPTY_TASK_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskFilledProperties() {
        final String TASK_NAME_JAVA = "Email task10 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Email task10 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Email Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Email Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Email task13 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Email task13 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Email Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Email Task.\");";

        final String TASK_NAME_MVEL = "Email task16 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Email task16 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Email Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Email Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|Body:String,From:String,Subject:String,To:String,input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

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
                                      AD_HOC_AUTOSTART,
                                      SLA_DUE_DATE);
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
                                      AD_HOC_AUTOSTART,
                                      SLA_DUE_DATE);
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
                                      AD_HOC_AUTOSTART,
                                      SLA_DUE_DATE);
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
                                      NOT_AD_HOC_AUTOSTART,
                                      EMPTY_VALUE);
        assertDataIOSet(emptySubprocessLevelTask.getDataIOSet(), EMPTY_TASK_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskOneIncomeFilledProperties() {
        final String TASK_NAME_JAVA = "Email task02 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Email task02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Email Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Email Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Email task05 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Email task05 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Email Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Email Task.\");";

        final String TASK_NAME_MVEL = "Email task08 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Email task08 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Email Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Email Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|Body:String,From:String,Subject:String,To:String,input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

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
                                      AD_HOC_AUTOSTART,
                                      SLA_DUE_DATE);
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
                                      AD_HOC_AUTOSTART,
                                      SLA_DUE_DATE);
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
                                      AD_HOC_AUTOSTART,
                                      SLA_DUE_DATE);
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
                                      NOT_AD_HOC_AUTOSTART,
                                      EMPTY_VALUE);
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
                                      NOT_AD_HOC_AUTOSTART,
                                      EMPTY_VALUE);
        assertDataIOSet(emptySubprocessLevelTask.getDataIOSet(), EMPTY_TASK_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskOneIncomeFilledProperties() {
        final String TASK_NAME_JAVA = "Email task11 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Email task11 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Email Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Email Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Email task14 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Email task14 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Email Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Email Task.\");";

        final String TASK_NAME_MVEL = "Email task17 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Email task17 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Email Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Email Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|Body:String,From:String,Subject:String,To:String,input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

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
                                      AD_HOC_AUTOSTART,
                                      SLA_DUE_DATE);
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
                                      AD_HOC_AUTOSTART,
                                      SLA_DUE_DATE);
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
                                      AD_HOC_AUTOSTART,
                                      SLA_DUE_DATE);
        assertDataIOSet(filledSubprocessLevelTaskMvel.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskTwoIncomesFilledProperties() {
        final String TASK_NAME_JAVA = "Email task03 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Email task03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Email Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Email Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Email task06 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Email task06 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Email Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Email Task.\");";

        final String TASK_NAME_MVEL = "Email task09 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Email task09 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Email Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Email Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|Body:String,From:String,Subject:String,To:String,input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

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
                                      AD_HOC_AUTOSTART,
                                      SLA_DUE_DATE);
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
                                      AD_HOC_AUTOSTART,
                                      SLA_DUE_DATE);
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
                                      AD_HOC_AUTOSTART,
                                      SLA_DUE_DATE);
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
                                      NOT_AD_HOC_AUTOSTART,
                                      EMPTY_VALUE);
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
                                      NOT_AD_HOC_AUTOSTART,
                                      EMPTY_VALUE);
        assertDataIOSet(emptySubprocessLevelTask.getDataIOSet(), EMPTY_TASK_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskTwoIncomesFilledProperties() {
        final String TASK_NAME_JAVA = "Email task12 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Email task12 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Email Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Email Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Email task15 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Email task15 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Email Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Email Task.\");";

        final String TASK_NAME_MVEL = "Email task18 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Email task18 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Email Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Email Task.\");";

        final String TASK_DATA_INPUT_OUTPUT = "|Body:String,From:String,Subject:String,To:String,input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

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
                                      AD_HOC_AUTOSTART,
                                      SLA_DUE_DATE);
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
                                      AD_HOC_AUTOSTART,
                                      SLA_DUE_DATE);
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
                                      AD_HOC_AUTOSTART,
                                      SLA_DUE_DATE);
        assertDataIOSet(filledSubprocessLevelTaskMvel.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);
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
