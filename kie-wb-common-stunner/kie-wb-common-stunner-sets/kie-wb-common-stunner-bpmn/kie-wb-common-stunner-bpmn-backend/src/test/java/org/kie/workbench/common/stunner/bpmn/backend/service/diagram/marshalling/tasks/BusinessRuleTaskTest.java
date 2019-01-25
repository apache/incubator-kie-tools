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

import org.junit.Ignore;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BusinessRuleTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleLanguage;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BusinessRuleTaskTest extends Task<BusinessRuleTask> {

    private static final String BPMN_TASK_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/businessRuleTasks.bpmn";

    private static final String EMPTY_TOP_LEVEL_TASK_ID = "0A575AC2-2D97-484B-A3C9-CA7AC8D1BFA6";
    private static final String FILLED_TOP_LEVEL_TASK_JAVA_ID = "3FE580D6-75F6-402B-8C24-47C95E67EE11";
    private static final String FILLED_TOP_LEVEL_TASK_JAVASCRIPT_ID = "7917DDBD-CA16-40D3-83CF-376BD32F301E";
    private static final String FILLED_TOP_LEVEL_TASK_MVEL_ID = "34628985-F9C4-43A4-AD22-1173275A26CA";

    private static final String EMPTY_SUBPROCESS_LEVEL_TASK_ID = "25D04D0B-9826-462F-A012-01B85BD371CE";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_JAVA_ID = "4E6D1968-9819-4EB3-8A60-70622060BE88";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "159A5FF3-472D-4D15-863C-D377DA0E76BC";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_MVEL_ID = "70EAE6AB-2FF1-416E-8B5A-CDB9B0BE2FDA";

    private static final String EMPTY_ONE_INCOME_SUBPROCESS_LEVEL_TASK_ID = "7AC9EBD3-258E-4118-AB58-2723D30E879F";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVA_ID = "40478C64-73C7-4B74-A278-7170D18F95C9";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "F7A7DCA6-C9AC-45AB-9E82-790CCBF6EEA1";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_MVEL_ID = "53CB534E-11C1-42BE-8697-104EE888B55B";

    private static final String EMPTY_ONE_INCOME_TOP_LEVEL_TASK_ID = "85660E3E-FC4B-4869-9BBE-8A5F2F483BF6";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVA_ID = "F1B724ED-14AB-41E6-B0BD-7776D0487908";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVASCRIPT_ID = "2D36CBE3-E497-467E-9A17-7A9B5A69EF7D";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_MVEL_ID = "2E098245-15C1-4C77-A719-B4F207B2C516";

    private static final String EMPTY_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_ID = "A6425B2C-DF96-4C25-A1EF-2CEA0B23415D";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVA_ID = "38B4D0B8-7F61-406C-91F1-36F3CD231183";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID = "FE864C5E-A212-41CA-9B0E-5BF2D0D9A492";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_MVEL_ID = "2B18305C-EDC6-4A0C-A6F7-C2FCE6A9AD94";

    private static final String EMPTY_TWO_INCOMES_TOP_LEVEL_TASK_ID = "B2261986-4D03-4757-8DD9-1C003CC70B42";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVA_ID = "E9C51991-DF8C-4DED-9F9D-846ACDE9AA88";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVASCRIPT_ID = "CF34C3FA-B9A5-49C9-A63A-988F120EAB9D";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_MVEL_ID = "1D6B0146-E20A-488F-8E27-584009BDC92F";

    private static final String DMN_RULE_LANGUAGE_TASK_ID = "_10EAF4D5-66E2-49B4-B2A6-EE5DCA14E9C5";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 70;

    private static final String EMPTY_TASK_DATA_INPUT_OUTPUT = "||||";
    private static final String DMN_TASK_DATA_INPUT_OUTPUT = "|namespace:java.lang.String,decision:java.lang.String,model:java.lang.String|||[din]namespace=Namespace,[din]decision=DecisionName,[din]model=DMNModelName";
    private static final String TASK_SCRIPT_JAVA_LANGUAGE = "java";
    private static final String TASK_SCRIPT_JAVASCRIPT_LANGUAGE = "javascript";
    private static final String TASK_SCRIPT_MVEL_LANGUAGE = "mvel";
    private static final boolean IS_ASYNC = true;
    private static final boolean IS_NOT_ASYNC = false;
    private static final boolean AD_HOC_AUTOSTART = true;
    private static final boolean NOT_AD_HOC_AUTOSTART = false;

    private final Marshaller marshallerType;

    public BusinessRuleTaskTest(Marshaller marshallerType) {
        super(marshallerType);
        this.marshallerType = marshallerType;
    }

    @Ignore("Test is ignored, Business Rule Task has properties that are not supported by the old marshallers.")
    public void testMigration() {
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskFilledProperties() throws Exception {
        final String TASK_NAME_JAVA = "Task01 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Task01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Business Rule Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Business Rule Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Task04 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Task04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Business Rule Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Business Rule Task.\");";

        final String TASK_NAME_MVEL = "Task07 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Task07 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Business Rule Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Business Rule Task.\");";

        final String RULE_FLOW_GROUP = "Group01";
        final String TASK_DATA_INPUT_OUTPUT = "|input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        BusinessRuleTask filledTopLevelTaskJava = getTaskNodeById(diagram,
                                                                  FILLED_TOP_LEVEL_TASK_JAVA_ID,
                                                                  ZERO_INCOME_EDGES,
                                                                  HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertBusinessRuleTaskExecutionSet(filledTopLevelTaskJava.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           RULE_FLOW_GROUP,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           TASK_ON_ENTRY_ACTION_JAVA,
                                           TASK_SCRIPT_JAVA_LANGUAGE,
                                           TASK_ON_EXIT_ACTION_JAVA,
                                           TASK_SCRIPT_JAVA_LANGUAGE,
                                           IS_ASYNC,
                                           AD_HOC_AUTOSTART);
        assertDataIOSet(filledTopLevelTaskJava.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        BusinessRuleTask filledTopLevelTaskJavascript = getTaskNodeById(diagram,
                                                                        FILLED_TOP_LEVEL_TASK_JAVASCRIPT_ID,
                                                                        ZERO_INCOME_EDGES,
                                                                        HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertBusinessRuleTaskExecutionSet(filledTopLevelTaskJavascript.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           RULE_FLOW_GROUP,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           TASK_ON_ENTRY_ACTION_JAVASCRIPT,
                                           TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                           TASK_ON_EXIT_ACTION_JAVASCRIPT,
                                           TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                           IS_ASYNC,
                                           AD_HOC_AUTOSTART);
        assertDataIOSet(filledTopLevelTaskJavascript.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        BusinessRuleTask filledTopLevelTaskMvel = getTaskNodeById(diagram,
                                                                  FILLED_TOP_LEVEL_TASK_MVEL_ID,
                                                                  ZERO_INCOME_EDGES,
                                                                  HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertBusinessRuleTaskExecutionSet(filledTopLevelTaskMvel.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           RULE_FLOW_GROUP,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
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
    public void testUnmarshallTopLevelEmptyTaskProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        BusinessRuleTask emptyTopLevelTask = getTaskNodeById(diagram,
                                                             EMPTY_TOP_LEVEL_TASK_ID,
                                                             ZERO_INCOME_EDGES,
                                                             HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(emptyTopLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertBusinessRuleTaskExecutionSet(emptyTopLevelTask.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
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
        final String TASK_NAME_JAVA = "Task10 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Task10 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Business Rule Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Business Rule Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Task13 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Task13 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Business Rule Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Business Rule Task.\");";

        final String TASK_NAME_MVEL = "Task16 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Task16 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Business Rule Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Business Rule Task.\");";

        final String RULE_FLOW_GROUP = "Group01";
        final String TASK_DATA_INPUT_OUTPUT = "|input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        BusinessRuleTask filledSubprocessLevelTaskJava = getTaskNodeById(diagram,
                                                                         FILLED_SUBPROCESS_LEVEL_TASK_JAVA_ID,
                                                                         ZERO_INCOME_EDGES,
                                                                         HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertBusinessRuleTaskExecutionSet(filledSubprocessLevelTaskJava.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           RULE_FLOW_GROUP,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           TASK_ON_ENTRY_ACTION_JAVA,
                                           TASK_SCRIPT_JAVA_LANGUAGE,
                                           TASK_ON_EXIT_ACTION_JAVA,
                                           TASK_SCRIPT_JAVA_LANGUAGE,
                                           IS_ASYNC,
                                           AD_HOC_AUTOSTART);
        assertDataIOSet(filledSubprocessLevelTaskJava.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        BusinessRuleTask filledSubprocessLevelTaskJavascript = getTaskNodeById(diagram,
                                                                               FILLED_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID,
                                                                               ZERO_INCOME_EDGES,
                                                                               HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertBusinessRuleTaskExecutionSet(filledSubprocessLevelTaskJavascript.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           RULE_FLOW_GROUP,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           TASK_ON_ENTRY_ACTION_JAVASCRIPT,
                                           TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                           TASK_ON_EXIT_ACTION_JAVASCRIPT,
                                           TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                           IS_ASYNC,
                                           AD_HOC_AUTOSTART);
        assertDataIOSet(filledSubprocessLevelTaskJavascript.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        BusinessRuleTask filledSubprocessLevelTaskMvel = getTaskNodeById(diagram,
                                                                         FILLED_SUBPROCESS_LEVEL_TASK_MVEL_ID,
                                                                         ZERO_INCOME_EDGES,
                                                                         HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertBusinessRuleTaskExecutionSet(filledSubprocessLevelTaskMvel.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           RULE_FLOW_GROUP,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
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
    public void testUnmarshallSubprocessLevelTaskEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        BusinessRuleTask emptySubprocessLevelTask = getTaskNodeById(diagram,
                                                                    EMPTY_SUBPROCESS_LEVEL_TASK_ID,
                                                                    ZERO_INCOME_EDGES,
                                                                    HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(emptySubprocessLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertBusinessRuleTaskExecutionSet(emptySubprocessLevelTask.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
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
        final String TASK_NAME_JAVA = "Task02 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Task02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Business Rule Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Business Rule Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Task05 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Task05 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Business Rule Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Business Rule Task.\");";

        final String TASK_NAME_MVEL = "Task08 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Task08 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Business Rule Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Business Rule Task.\");";

        final String RULE_FLOW_GROUP = "Group01";
        final String TASK_DATA_INPUT_OUTPUT = "|input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        BusinessRuleTask filledTopLevelTaskJava = getTaskNodeById(diagram,
                                                                  FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVA_ID,
                                                                  ONE_INCOME_EDGE,
                                                                  HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertBusinessRuleTaskExecutionSet(filledTopLevelTaskJava.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           RULE_FLOW_GROUP,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           TASK_ON_ENTRY_ACTION_JAVA,
                                           TASK_SCRIPT_JAVA_LANGUAGE,
                                           TASK_ON_EXIT_ACTION_JAVA,
                                           TASK_SCRIPT_JAVA_LANGUAGE,
                                           IS_ASYNC,
                                           AD_HOC_AUTOSTART);
        assertDataIOSet(filledTopLevelTaskJava.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        BusinessRuleTask filledTopLevelTaskJavascript = getTaskNodeById(diagram,
                                                                        FILLED_ONE_INCOME_TOP_LEVEL_TASK_JAVASCRIPT_ID,
                                                                        ONE_INCOME_EDGE,
                                                                        HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertBusinessRuleTaskExecutionSet(filledTopLevelTaskJavascript.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           RULE_FLOW_GROUP,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           TASK_ON_ENTRY_ACTION_JAVASCRIPT,
                                           TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                           TASK_ON_EXIT_ACTION_JAVASCRIPT,
                                           TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                           IS_ASYNC,
                                           AD_HOC_AUTOSTART);
        assertDataIOSet(filledTopLevelTaskJavascript.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        BusinessRuleTask filledTopLevelTaskMvel = getTaskNodeById(diagram,
                                                                  FILLED_ONE_INCOME_TOP_LEVEL_TASK_MVEL_ID,
                                                                  ONE_INCOME_EDGE,
                                                                  HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertBusinessRuleTaskExecutionSet(filledTopLevelTaskMvel.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           RULE_FLOW_GROUP,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
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
    public void testUnmarshallTopLevelTaskOneIncomeEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        BusinessRuleTask emptyTopLevelTask = getTaskNodeById(diagram,
                                                             EMPTY_ONE_INCOME_TOP_LEVEL_TASK_ID,
                                                             ONE_INCOME_EDGE,
                                                             HAS_OUTCOME_EDGE);
        assertGeneralSet(emptyTopLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertBusinessRuleTaskExecutionSet(emptyTopLevelTask.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
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
    public void testUnmarshallSubprocessLevelTaskOneIncomeEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        BusinessRuleTask emptySubprocessLevelTask = getTaskNodeById(diagram,
                                                                    EMPTY_ONE_INCOME_SUBPROCESS_LEVEL_TASK_ID,
                                                                    ONE_INCOME_EDGE,
                                                                    HAS_OUTCOME_EDGE);
        assertGeneralSet(emptySubprocessLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertBusinessRuleTaskExecutionSet(emptySubprocessLevelTask.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
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
        final String TASK_NAME_JAVA = "Task11 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Task11 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Business Rule Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Business Rule Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Task14 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Task14 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Business Rule Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Business Rule Task.\");";

        final String TASK_NAME_MVEL = "Task17 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Task17 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Business Rule Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Business Rule Task.\");";

        final String RULE_FLOW_GROUP = "Group01";
        final String TASK_DATA_INPUT_OUTPUT = "|input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        BusinessRuleTask filledSubprocessLevelTaskJava = getTaskNodeById(diagram,
                                                                         FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVA_ID,
                                                                         ONE_INCOME_EDGE,
                                                                         HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertBusinessRuleTaskExecutionSet(filledSubprocessLevelTaskJava.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           RULE_FLOW_GROUP,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           TASK_ON_ENTRY_ACTION_JAVA,
                                           TASK_SCRIPT_JAVA_LANGUAGE,
                                           TASK_ON_EXIT_ACTION_JAVA,
                                           TASK_SCRIPT_JAVA_LANGUAGE,
                                           IS_ASYNC,
                                           AD_HOC_AUTOSTART);
        assertDataIOSet(filledSubprocessLevelTaskJava.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        BusinessRuleTask filledSubprocessLevelTaskJavascript = getTaskNodeById(diagram,
                                                                               FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID,
                                                                               ONE_INCOME_EDGE,
                                                                               HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertBusinessRuleTaskExecutionSet(filledSubprocessLevelTaskJavascript.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           RULE_FLOW_GROUP,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           TASK_ON_ENTRY_ACTION_JAVASCRIPT,
                                           TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                           TASK_ON_EXIT_ACTION_JAVASCRIPT,
                                           TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                           IS_ASYNC,
                                           AD_HOC_AUTOSTART);
        assertDataIOSet(filledSubprocessLevelTaskJavascript.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        BusinessRuleTask filledSubprocessLevelTaskMvel = getTaskNodeById(diagram,
                                                                         FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_MVEL_ID,
                                                                         ONE_INCOME_EDGE,
                                                                         HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertBusinessRuleTaskExecutionSet(filledSubprocessLevelTaskMvel.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           RULE_FLOW_GROUP,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
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
        final String TASK_NAME_JAVA = "Task03 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Task03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Business Rule Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Business Rule Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Task06 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Task06 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Business Rule Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Business Rule Task.\");";

        final String TASK_NAME_MVEL = "Task09 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Task09 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Business Rule Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Business Rule Task.\");";

        final String RULE_FLOW_GROUP = "Group01";
        final String TASK_DATA_INPUT_OUTPUT = "|input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        BusinessRuleTask filledTopLevelTaskJava = getTaskNodeById(diagram,
                                                                  FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVA_ID,
                                                                  TWO_INCOME_EDGES,
                                                                  HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertBusinessRuleTaskExecutionSet(filledTopLevelTaskJava.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           RULE_FLOW_GROUP,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           TASK_ON_ENTRY_ACTION_JAVA,
                                           TASK_SCRIPT_JAVA_LANGUAGE,
                                           TASK_ON_EXIT_ACTION_JAVA,
                                           TASK_SCRIPT_JAVA_LANGUAGE,
                                           IS_ASYNC,
                                           AD_HOC_AUTOSTART);
        assertDataIOSet(filledTopLevelTaskJava.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        BusinessRuleTask filledTopLevelTaskJavascript = getTaskNodeById(diagram,
                                                                        FILLED_TWO_INCOMES_TOP_LEVEL_TASK_JAVASCRIPT_ID,
                                                                        TWO_INCOME_EDGES,
                                                                        HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertBusinessRuleTaskExecutionSet(filledTopLevelTaskJavascript.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           RULE_FLOW_GROUP,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           TASK_ON_ENTRY_ACTION_JAVASCRIPT,
                                           TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                           TASK_ON_EXIT_ACTION_JAVASCRIPT,
                                           TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                           IS_ASYNC,
                                           AD_HOC_AUTOSTART);
        assertDataIOSet(filledTopLevelTaskJavascript.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        BusinessRuleTask filledTopLevelTaskMvel = getTaskNodeById(diagram,
                                                                  FILLED_TWO_INCOMES_TOP_LEVEL_TASK_MVEL_ID,
                                                                  TWO_INCOME_EDGES,
                                                                  HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertBusinessRuleTaskExecutionSet(filledTopLevelTaskMvel.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           RULE_FLOW_GROUP,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
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
    public void testUnmarshallTopLevelTaskTwoIncomesEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        BusinessRuleTask emptyTopLevelTask = getTaskNodeById(diagram,
                                                             EMPTY_TWO_INCOMES_TOP_LEVEL_TASK_ID,
                                                             TWO_INCOME_EDGES,
                                                             HAS_OUTCOME_EDGE);
        assertGeneralSet(emptyTopLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertBusinessRuleTaskExecutionSet(emptyTopLevelTask.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
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
    public void testUnmarshallSubprocessLevelTaskTwoIncomesEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        BusinessRuleTask emptySubprocessLevelTask = getTaskNodeById(diagram,
                                                                    EMPTY_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_ID,
                                                                    TWO_INCOME_EDGES,
                                                                    HAS_OUTCOME_EDGE);
        assertGeneralSet(emptySubprocessLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertBusinessRuleTaskExecutionSet(emptySubprocessLevelTask.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
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
        final String TASK_NAME_JAVA = "Task12 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVA = "Task12 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action from Business Rule Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action from Business Rule Task.\");";

        final String TASK_NAME_JAVASCRIPT = "Task15 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_JAVASCRIPT = "Task15 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action from Business Rule Task.\");";
        final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action from Business Rule Task.\");";

        final String TASK_NAME_MVEL = "Task18 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String TASK_DOCUMENTATION_MVEL = "Task18 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action from Business Rule Task.\");";
        final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action from Business Rule Task.\");";

        final String RULE_FLOW_GROUP = "Group01";
        final String TASK_DATA_INPUT_OUTPUT = "|input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        BusinessRuleTask filledSubprocessLevelTaskJava = getTaskNodeById(diagram,
                                                                         FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVA_ID,
                                                                         TWO_INCOME_EDGES,
                                                                         HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJava.getGeneral(), TASK_NAME_JAVA, TASK_DOCUMENTATION_JAVA);
        assertBusinessRuleTaskExecutionSet(filledSubprocessLevelTaskJava.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           RULE_FLOW_GROUP,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           TASK_ON_ENTRY_ACTION_JAVA,
                                           TASK_SCRIPT_JAVA_LANGUAGE,
                                           TASK_ON_EXIT_ACTION_JAVA,
                                           TASK_SCRIPT_JAVA_LANGUAGE,
                                           IS_ASYNC,
                                           AD_HOC_AUTOSTART);
        assertDataIOSet(filledSubprocessLevelTaskJava.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        BusinessRuleTask filledSubprocessLevelTaskJavascript = getTaskNodeById(diagram,
                                                                               FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_JAVASCRIPT_ID,
                                                                               TWO_INCOME_EDGES,
                                                                               HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskJavascript.getGeneral(), TASK_NAME_JAVASCRIPT, TASK_DOCUMENTATION_JAVASCRIPT);
        assertBusinessRuleTaskExecutionSet(filledSubprocessLevelTaskJavascript.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           RULE_FLOW_GROUP,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           TASK_ON_ENTRY_ACTION_JAVASCRIPT,
                                           TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                           TASK_ON_EXIT_ACTION_JAVASCRIPT,
                                           TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                           IS_ASYNC,
                                           AD_HOC_AUTOSTART);
        assertDataIOSet(filledSubprocessLevelTaskJavascript.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);

        BusinessRuleTask filledSubprocessLevelTaskMvel = getTaskNodeById(diagram,
                                                                         FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_MVEL_ID,
                                                                         TWO_INCOME_EDGES,
                                                                         HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTaskMvel.getGeneral(), TASK_NAME_MVEL, TASK_DOCUMENTATION_MVEL);
        assertBusinessRuleTaskExecutionSet(filledSubprocessLevelTaskMvel.getExecutionSet(),
                                           RuleLanguage.DRL,
                                           RULE_FLOW_GROUP,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           EMPTY_VALUE,
                                           TASK_ON_ENTRY_ACTION_MVEL,
                                           TASK_SCRIPT_MVEL_LANGUAGE,
                                           TASK_ON_EXIT_ACTION_MVEL,
                                           TASK_SCRIPT_MVEL_LANGUAGE,
                                           IS_ASYNC,
                                           AD_HOC_AUTOSTART);
        assertDataIOSet(filledSubprocessLevelTaskMvel.getDataIOSet(), TASK_DATA_INPUT_OUTPUT);
    }

    @Test
    public void testUnmarshallDMNRuleLanguageProperties() throws Exception {
        final String DMN_LANGUAGE_TASK_NAME = "DMN Task";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_TASK_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        BusinessRuleTask emptyTopLevelTask = getTaskNodeById(diagram,
                                                             DMN_RULE_LANGUAGE_TASK_ID,
                                                             ZERO_INCOME_EDGES,
                                                             HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(emptyTopLevelTask.getGeneral(), DMN_LANGUAGE_TASK_NAME, EMPTY_VALUE);
        assertBusinessRuleTaskExecutionSet(emptyTopLevelTask.getExecutionSet(),
                                           RuleLanguage.DMN,
                                           EMPTY_VALUE,
                                           "Namespace",
                                           "DecisionName",
                                           "DMNModelName",
                                           EMPTY_VALUE,
                                           TASK_SCRIPT_JAVA_LANGUAGE,
                                           EMPTY_VALUE,
                                           TASK_SCRIPT_JAVA_LANGUAGE,
                                           IS_NOT_ASYNC,
                                           NOT_AD_HOC_AUTOSTART);

        if (marshallerType == Marshaller.NEW) {
            assertDataIOSet(emptyTopLevelTask.getDataIOSet(), EMPTY_TASK_DATA_INPUT_OUTPUT);
        }

        if (marshallerType == Marshaller.OLD) {
            assertDataIOSet(emptyTopLevelTask.getDataIOSet(), DMN_TASK_DATA_INPUT_OUTPUT);
        }
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

    @Test
    public void testMarshallDMNRuleLanguageProperties() throws Exception {
        checkTaskMarshalling(DMN_RULE_LANGUAGE_TASK_ID, ZERO_INCOME_EDGES, HAS_NO_OUTCOME_EDGE);
    }

    @Override
    String getBpmnTaskFilePath() {
        return BPMN_TASK_FILE_PATH;
    }

    @Override
    Class<BusinessRuleTask> getTaskType() {
        return BusinessRuleTask.class;
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

    private void assertBusinessRuleTaskExecutionSet(BusinessRuleTaskExecutionSet executionSet,
                                                    String ruleLanguage,
                                                    String ruleFlowGroup,
                                                    String namespace,
                                                    String decisionName,
                                                    String dmnModelName,
                                                    String onEntryActionScriptValue,
                                                    String onEntryActionScriptLanguage,
                                                    String onExitActionScriptValue,
                                                    String onExitActionScriptLanguage,
                                                    boolean isAsync,
                                                    boolean adHocAutostart) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getRuleLanguage());
        assertNotNull(executionSet.getRuleFlowGroup());
        assertNotNull(executionSet.getNamespace());
        assertNotNull(executionSet.getDmnModelName());
        assertNotNull(executionSet.getDmnModelName());
        assertNotNull(executionSet.getOnEntryAction());
        assertNotNull(executionSet.getOnExitAction());
        assertNotNull(executionSet.getIsAsync());
        assertNotNull(executionSet.getAdHocAutostart());

        assertNotNull(executionSet.getOnEntryAction().getValue());
        assertNotNull(executionSet.getOnExitAction().getValue());

        List<ScriptTypeValue> onEntryScriptTypeValues = executionSet.getOnEntryAction().getValue().getValues();
        List<ScriptTypeValue> onExitScriptTypeValues = executionSet.getOnExitAction().getValue().getValues();

        assertNotNull(onEntryScriptTypeValues);
        assertNotNull(onExitScriptTypeValues);
        assertNotNull(onEntryScriptTypeValues.get(0));
        assertNotNull(onExitScriptTypeValues.get(0));

        if (marshallerType == Marshaller.NEW) {
            assertEquals(ruleLanguage, executionSet.getRuleLanguage().getValue());
            assertEquals(ruleFlowGroup, executionSet.getRuleFlowGroup().getValue());
            assertEquals(namespace, executionSet.getNamespace().getValue());
            assertEquals(decisionName, executionSet.getDecisionName().getValue());
            assertEquals(dmnModelName, executionSet.getDmnModelName().getValue());
        } else if (marshallerType == Marshaller.OLD) {
            assertEquals(ruleFlowGroup, executionSet.getRuleFlowGroup().getValue());
        }

        assertEquals(onEntryActionScriptValue, onEntryScriptTypeValues.get(0).getScript());
        assertEquals(onEntryActionScriptLanguage, onEntryScriptTypeValues.get(0).getLanguage());
        assertEquals(onExitActionScriptValue, onExitScriptTypeValues.get(0).getScript());
        assertEquals(onExitActionScriptLanguage, onExitScriptTypeValues.get(0).getLanguage());
        assertEquals(isAsync, executionSet.getIsAsync().getValue());
        assertEquals(adHocAutostart, executionSet.getAdHocAutostart().getValue());
    }

    private void assertDataIOSet(DataIOSet dataIOSet, String value) {
        assertNotNull(dataIOSet);
        assertNotNull(dataIOSet.getAssignmentsinfo());
        assertEquals(value, dataIOSet.getAssignmentsinfo().getValue());
    }
}
