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
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTaskExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ScriptTaskTest extends Task<ScriptTask> {

    private static final String BPMN_TASK_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/scriptTasks.bpmn";

    private static final String EMPTY_TOP_LEVEL_TASK_ID = "333A4DB9-7C2E-4977-A097-6EB5C0063758";
    private static final String FILLED_TOP_LEVEL_TASK_JAVA_ID = "31BA2691-4801-47C2-A3AA-8B7251EA3211";
    private static final String FILLED_TOP_LEVEL_TASK_JAVASCRIPT_ID = "C0CEEC7A-2CF2-46BE-871D-48326081333D";
    private static final String FILLED_TOP_LEVEL_TASK_MVEL_ID = "A624A934-9FF9-466B-87BD-AFCB9B49DBE8";

    private static final String EMPTY_SUBPROCESS_LEVEL_TASK_ID = "B5FFAB2E-541C-4076-B8BE-C7DF021BFBB7";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_JAVA_ID = "49B88C7E-D489-45FD-823F-BC311C9C77D9";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "3E62ED06-C624-4CE0-8E14-0924870E58DA";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_MVEL_ID = "1AFC648A-5349-4DB5-990B-100EB53F27F7";

    private static final String EMPTY_ONE_INCOME_SUBPROCESS_LEVEL_TASK_ID = "E2D022E8-F2FA-42E8-9681-A904B7AB1CFF";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVA_ID = "9C038A3F-69E5-4B53-9069-E7F6A592610F";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "937646E6-395A-44C9-8B40-510D0D8740A9";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_MVEL_ID = "9954B580-F35D-43C4-A8AD-5032AC809AD2";

    private static final String EMPTY_ONE_INCOME_TOP_LEVEL_TASK_ID = "68D3692A-B349-4028-82EE-B2A8C21F5AA0";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVA_ID = "B088F330-3FF9-49A1-BF54-A2C2F3D9A5DA";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVASCRIPT_ID = "2C78233A-5E43-4B84-AA78-D80856DCC5E7";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_MVEL_ID = "EFC669E1-196D-49AD-95CC-18C521C7F4EF";

    private static final String EMPTY_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_ID = "8F3F3DBE-69B6-4E70-A6FC-63CC781E785A";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVA_ID = "C9C71AC1-DBDB-42C8-B97E-77BA7540FD43";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "28BBADA1-77B6-45D5-8CAA-DDDE56B5ADC4";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_MVEL_ID = "71B8BC86-1F13-42F0-9F0E-0C7A517BDC2D";

    private static final String EMPTY_TWO_INCOMES_TOP_LEVEL_TASK_ID = "BF703F87-D918-404F-AC1F-BB3AA4B7F193";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVA_ID = "A70EDCB6-7F3B-4524-BFEB-AD467B658BB4";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVASCRIPT_ID = "E4FE0906-EDFF-4A5C-9FA1-E470AFD00733";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_MVEL_ID = "8A7FB383-DC6A-4271-A629-4E62A309D646";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 70;

    private static final String TASK_SCRIPT_DEFAULT_VALUE = null;
    private static final String TASK_SCRIPT_JAVA_LANGUAGE = "java";
    private static final String TASK_SCRIPT_JAVASCRIPT_LANGUAGE = "javascript";
    private static final String TASK_SCRIPT_MVEL_LANGUAGE = "mvel";
    private static final boolean IS_ASYNC = true;
    private static final boolean IS_NOT_ASYNC = false;
    private static final boolean IS_ADHOC_AUTOSTART = true;
    private static final boolean IS_NOT_ADHOC_AUTOSTART = false;

    private static Diagram<Graph, Metadata> oldDiagram;
    private static Diagram<Graph, Metadata> oldRoundTripDiagram;

    private static Diagram<Graph, Metadata> newDiagram;
    private static Diagram<Graph, Metadata> newRoundTripDiagram;

    public ScriptTaskTest(Marshaller marshallerType) throws Exception {
        super(marshallerType, marshallers());
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskFilledProperties() {
        final String TASK_NAME_JAVA = "Task01 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Task01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_SCRIPT_JAVA = "System.out.println(\"Called from Script Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Task07 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Task07 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_SCRIPT_JAVASCRIPT = "console.log(\"Called from Script Task.\");";

        final String TASK_NAME_MVEL = "Task10 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Task10 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_SCRIPT_MVEL = "System.out.println(\"Called from Script Task.\");";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ScriptTask filledTopLevelTaskJava = getTaskNodeById(getDiagram(),
                                                            FILLED_TOP_LEVEL_TASK_JAVA_ID,
                                                            ZERO_INCOME_EDGES,
                                                            HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertScriptTaskExecutionSet(filledTopLevelTaskJava.getExecutionSet(),
                                     TASK_SCRIPT_JAVA,
                                     TASK_SCRIPT_JAVA_LANGUAGE,
                                     IS_ASYNC,
                                     IS_ADHOC_AUTOSTART);

        ScriptTask filledTopLevelTaskJavascript = getTaskNodeById(getDiagram(),
                                                                  FILLED_TOP_LEVEL_TASK_JAVASCRIPT_ID,
                                                                  ZERO_INCOME_EDGES,
                                                                  HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertScriptTaskExecutionSet(filledTopLevelTaskJavascript.getExecutionSet(),
                                     TASK_SCRIPT_JAVASCRIPT,
                                     TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                     IS_ASYNC,
                                     IS_ADHOC_AUTOSTART);

        ScriptTask filledTopLevelTaskMvel = getTaskNodeById(getDiagram(),
                                                            FILLED_TOP_LEVEL_TASK_MVEL_ID,
                                                            ZERO_INCOME_EDGES,
                                                            HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertScriptTaskExecutionSet(filledTopLevelTaskMvel.getExecutionSet(),
                                     TASK_SCRIPT_MVEL,
                                     TASK_SCRIPT_MVEL_LANGUAGE,
                                     IS_ASYNC,
                                     IS_ADHOC_AUTOSTART);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyTaskProperties() {
        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ScriptTask filledTopLevelTask = getTaskNodeById(getDiagram(),
                                                        EMPTY_TOP_LEVEL_TASK_ID,
                                                        ZERO_INCOME_EDGES,
                                                        HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertScriptTaskExecutionSet(filledTopLevelTask.getExecutionSet(),
                                     TASK_SCRIPT_DEFAULT_VALUE,
                                     TASK_SCRIPT_JAVA_LANGUAGE,
                                     IS_NOT_ASYNC,
                                     IS_NOT_ADHOC_AUTOSTART);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskFilledProperties() {
        final String TASK_NAME_JAVA = "Task03 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Task03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_SCRIPT_JAVA = "System.out.println(\"Called from Script Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Task13 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Task13 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_SCRIPT_JAVASCRIPT = "console.log(\"Called from Script Task.\");";

        final String TASK_NAME_MVEL = "Task16 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Task16 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_SCRIPT_MVEL = "System.out.println(\"Called from Script Task.\");";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ScriptTask filledSubprocessLevelTaskJava = getTaskNodeById(getDiagram(),
                                                                   FILLED_SUBPROCESS_LEVEL_TASK_JAVA_ID,
                                                                   ZERO_INCOME_EDGES,
                                                                   HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertScriptTaskExecutionSet(filledSubprocessLevelTaskJava.getExecutionSet(),
                                     TASK_SCRIPT_JAVA,
                                     TASK_SCRIPT_JAVA_LANGUAGE,
                                     IS_ASYNC,
                                     IS_ADHOC_AUTOSTART);

        ScriptTask filledSubprocessLevelTaskJavascript = getTaskNodeById(getDiagram(),
                                                                         FILLED_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID,
                                                                         ZERO_INCOME_EDGES,
                                                                         HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertScriptTaskExecutionSet(filledSubprocessLevelTaskJavascript.getExecutionSet(),
                                     TASK_SCRIPT_JAVASCRIPT,
                                     TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                     IS_ASYNC,
                                     IS_ADHOC_AUTOSTART);

        ScriptTask filledSubprocessLevelTaskMvel = getTaskNodeById(getDiagram(),
                                                                   FILLED_SUBPROCESS_LEVEL_TASK_MVEL_ID,
                                                                   ZERO_INCOME_EDGES,
                                                                   HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertScriptTaskExecutionSet(filledSubprocessLevelTaskMvel.getExecutionSet(),
                                     TASK_SCRIPT_MVEL,
                                     TASK_SCRIPT_MVEL_LANGUAGE,
                                     IS_ASYNC,
                                     IS_ADHOC_AUTOSTART);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskEmptyProperties() {
        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ScriptTask filledSubprocessLevelTaskJava = getTaskNodeById(getDiagram(),
                                                                   EMPTY_SUBPROCESS_LEVEL_TASK_ID,
                                                                   ZERO_INCOME_EDGES,
                                                                   HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJava.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertScriptTaskExecutionSet(filledSubprocessLevelTaskJava.getExecutionSet(),
                                     TASK_SCRIPT_DEFAULT_VALUE,
                                     TASK_SCRIPT_JAVA_LANGUAGE,
                                     IS_NOT_ASYNC,
                                     IS_NOT_ADHOC_AUTOSTART);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskOneIncomeFilledProperties() {
        final String TASK_NAME_JAVA = "Task02 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Task02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_SCRIPT_JAVA = "System.out.println(\"Called from Script Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Task08 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Task08 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_SCRIPT_JAVASCRIPT = "console.log(\"Called from Script Task.\");";

        final String TASK_NAME_MVEL = "Task11 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Task11 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_SCRIPT_MVEL = "System.out.println(\"Called from Script Task.\");";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ScriptTask filledTopLevelTaskJava = getTaskNodeById(getDiagram(),
                                                            FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVA_ID,
                                                            ONE_INCOME_EDGE,
                                                            HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertScriptTaskExecutionSet(filledTopLevelTaskJava.getExecutionSet(),
                                     TASK_SCRIPT_JAVA,
                                     TASK_SCRIPT_JAVA_LANGUAGE,
                                     IS_ASYNC,
                                     IS_ADHOC_AUTOSTART);

        ScriptTask filledTopLevelTaskJavascript = getTaskNodeById(getDiagram(),
                                                                  FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVASCRIPT_ID,
                                                                  ONE_INCOME_EDGE,
                                                                  HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertScriptTaskExecutionSet(filledTopLevelTaskJavascript.getExecutionSet(),
                                     TASK_SCRIPT_JAVASCRIPT,
                                     TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                     IS_ASYNC,
                                     IS_ADHOC_AUTOSTART);

        ScriptTask filledTopLevelTaskMvel = getTaskNodeById(getDiagram(),
                                                            FILLED_ONE_INCOME_TOP_LEVEL_TASK_MVEL_ID,
                                                            ONE_INCOME_EDGE,
                                                            HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertScriptTaskExecutionSet(filledTopLevelTaskMvel.getExecutionSet(),
                                     TASK_SCRIPT_MVEL,
                                     TASK_SCRIPT_MVEL_LANGUAGE,
                                     IS_ASYNC,
                                     IS_ADHOC_AUTOSTART);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskOneIncomeEmptyProperties() {
        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ScriptTask filledTopLevelTask = getTaskNodeById(getDiagram(),
                                                        EMPTY_ONE_INCOME_TOP_LEVEL_TASK_ID,
                                                        ONE_INCOME_EDGE,
                                                        HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertScriptTaskExecutionSet(filledTopLevelTask.getExecutionSet(),
                                     TASK_SCRIPT_DEFAULT_VALUE,
                                     TASK_SCRIPT_JAVA_LANGUAGE,
                                     IS_NOT_ASYNC,
                                     IS_NOT_ADHOC_AUTOSTART);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskOneIncomeEmptyProperties() {
        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ScriptTask filledSubprocessLevelTaskJava = getTaskNodeById(getDiagram(),
                                                                   EMPTY_ONE_INCOME_SUBPROCESS_LEVEL_TASK_ID,
                                                                   ONE_INCOME_EDGE,
                                                                   HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJava.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertScriptTaskExecutionSet(filledSubprocessLevelTaskJava.getExecutionSet(),
                                     TASK_SCRIPT_DEFAULT_VALUE,
                                     TASK_SCRIPT_JAVA_LANGUAGE,
                                     IS_NOT_ASYNC,
                                     IS_NOT_ADHOC_AUTOSTART);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskOneIncomeFilledProperties() {
        final String TASK_NAME_JAVA = "Task04 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Task04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_SCRIPT_JAVA = "System.out.println(\"Called from Script Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Task14 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Task14 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_SCRIPT_JAVASCRIPT = "console.log(\"Called from Script Task.\");";

        final String TASK_NAME_MVEL = "Task17 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Task17 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_SCRIPT_MVEL = "System.out.println(\"Called from Script Task.\");";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ScriptTask filledSubprocessLevelTaskJava = getTaskNodeById(getDiagram(),
                                                                   FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVA_ID,
                                                                   ONE_INCOME_EDGE,
                                                                   HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertScriptTaskExecutionSet(filledSubprocessLevelTaskJava.getExecutionSet(),
                                     TASK_SCRIPT_JAVA,
                                     TASK_SCRIPT_JAVA_LANGUAGE,
                                     IS_ASYNC,
                                     IS_ADHOC_AUTOSTART);

        ScriptTask filledSubprocessLevelTaskJavascript = getTaskNodeById(getDiagram(),
                                                                         FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID,
                                                                         ONE_INCOME_EDGE,
                                                                         HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertScriptTaskExecutionSet(filledSubprocessLevelTaskJavascript.getExecutionSet(),
                                     TASK_SCRIPT_JAVASCRIPT,
                                     TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                     IS_ASYNC,
                                     IS_ADHOC_AUTOSTART);

        ScriptTask filledSubprocessLevelTaskMvel = getTaskNodeById(getDiagram(),
                                                                   FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_MVEL_ID,
                                                                   ONE_INCOME_EDGE,
                                                                   HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertScriptTaskExecutionSet(filledSubprocessLevelTaskMvel.getExecutionSet(),
                                     TASK_SCRIPT_MVEL,
                                     TASK_SCRIPT_MVEL_LANGUAGE,
                                     IS_ASYNC,
                                     IS_ADHOC_AUTOSTART);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskTwoIncomesFilledProperties() {
        final String TASK_NAME_JAVA = "Task05 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Task05 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_SCRIPT_JAVA = "System.out.println(\"Called from Script Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Task09 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Task09 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_SCRIPT_JAVASCRIPT = "console.log(\"Called from Script Task.\");";

        final String TASK_NAME_MVEL = "Task12 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Task12 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_SCRIPT_MVEL = "System.out.println(\"Called from Script Task.\");";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ScriptTask filledTopLevelTaskJava = getTaskNodeById(getDiagram(),
                                                            FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVA_ID,
                                                            TWO_INCOME_EDGES,
                                                            HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertScriptTaskExecutionSet(filledTopLevelTaskJava.getExecutionSet(),
                                     TASK_SCRIPT_JAVA,
                                     TASK_SCRIPT_JAVA_LANGUAGE,
                                     IS_ASYNC,
                                     IS_ADHOC_AUTOSTART);

        ScriptTask filledTopLevelTaskJavascript = getTaskNodeById(getDiagram(),
                                                                  FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVASCRIPT_ID,
                                                                  TWO_INCOME_EDGES,
                                                                  HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertScriptTaskExecutionSet(filledTopLevelTaskJavascript.getExecutionSet(),
                                     TASK_SCRIPT_JAVASCRIPT,
                                     TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                     IS_ASYNC,
                                     IS_ADHOC_AUTOSTART);

        ScriptTask filledTopLevelTaskMvel = getTaskNodeById(getDiagram(),
                                                            FILLED_TWO_INCOMES_TOP_LEVEL_TASK_MVEL_ID,
                                                            TWO_INCOME_EDGES,
                                                            HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertScriptTaskExecutionSet(filledTopLevelTaskMvel.getExecutionSet(),
                                     TASK_SCRIPT_MVEL,
                                     TASK_SCRIPT_MVEL_LANGUAGE,
                                     IS_ASYNC,
                                     IS_ADHOC_AUTOSTART);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskTwoIncomesEmptyProperties() {
        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ScriptTask filledTopLevelTask = getTaskNodeById(getDiagram(),
                                                        EMPTY_TWO_INCOMES_TOP_LEVEL_TASK_ID,
                                                        TWO_INCOME_EDGES,
                                                        HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertScriptTaskExecutionSet(filledTopLevelTask.getExecutionSet(),
                                     TASK_SCRIPT_DEFAULT_VALUE,
                                     TASK_SCRIPT_JAVA_LANGUAGE,
                                     IS_NOT_ASYNC,
                                     IS_NOT_ADHOC_AUTOSTART);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskTwoIncomesEmptyProperties() {
        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ScriptTask filledSubprocessLevelTaskJava = getTaskNodeById(getDiagram(),
                                                                   EMPTY_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_ID,
                                                                   TWO_INCOME_EDGES,
                                                                   HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJava.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertScriptTaskExecutionSet(filledSubprocessLevelTaskJava.getExecutionSet(),
                                     TASK_SCRIPT_DEFAULT_VALUE,
                                     TASK_SCRIPT_JAVA_LANGUAGE,
                                     IS_NOT_ASYNC,
                                     IS_NOT_ADHOC_AUTOSTART);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskTwoIncomesFilledProperties() {
        final String TASK_NAME_JAVA = "Task06 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Task06 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_SCRIPT_JAVA = "System.out.println(\"Called from Script Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Task15 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Task15 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_SCRIPT_JAVASCRIPT = "console.log(\"Called from Script Task.\");";

        final String TASK_NAME_MVEL = "Task18 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Task18 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_SCRIPT_MVEL = "System.out.println(\"Called from Script Task.\");";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        ScriptTask filledSubprocessLevelTaskJava = getTaskNodeById(getDiagram(),
                                                                   FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVA_ID,
                                                                   TWO_INCOME_EDGES,
                                                                   HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertScriptTaskExecutionSet(filledSubprocessLevelTaskJava.getExecutionSet(),
                                     TASK_SCRIPT_JAVA,
                                     TASK_SCRIPT_JAVA_LANGUAGE,
                                     IS_ASYNC,
                                     IS_ADHOC_AUTOSTART);

        ScriptTask filledSubprocessLevelTaskJavascript = getTaskNodeById(getDiagram(),
                                                                         FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID,
                                                                         TWO_INCOME_EDGES,
                                                                         HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertScriptTaskExecutionSet(filledSubprocessLevelTaskJavascript.getExecutionSet(),
                                     TASK_SCRIPT_JAVASCRIPT,
                                     TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                     IS_ASYNC,
                                     IS_ADHOC_AUTOSTART);

        ScriptTask filledSubprocessLevelTaskMvel = getTaskNodeById(getDiagram(),
                                                                   FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_MVEL_ID,
                                                                   TWO_INCOME_EDGES,
                                                                   HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertScriptTaskExecutionSet(filledSubprocessLevelTaskMvel.getExecutionSet(),
                                     TASK_SCRIPT_MVEL,
                                     TASK_SCRIPT_MVEL_LANGUAGE,
                                     IS_ASYNC,
                                     IS_ADHOC_AUTOSTART);
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
    Class<ScriptTask> getTaskType() {
        return ScriptTask.class;
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

    private void assertScriptTaskExecutionSet(ScriptTaskExecutionSet executionSet,
                                              String script,
                                              String scriptLanguage,
                                              boolean isAsync,
                                              boolean isAdHocAutostart) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getScript());
        assertNotNull(executionSet.getScript().getValue());
        assertNotNull(executionSet.getIsAsync());
        assertNotNull(executionSet.getAdHocAutostart());

        assertEquals(script, executionSet.getScript().getValue().getScript());
        assertEquals(scriptLanguage, executionSet.getScript().getValue().getLanguage());
        assertEquals(isAsync, executionSet.getIsAsync().getValue());
        assertEquals(isAdHocAutostart, executionSet.getIsAsync().getValue());
    }
}
