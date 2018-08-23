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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.events;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller;
import org.kie.workbench.common.stunner.bpmn.definition.StartConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.conditional.InterruptingConditionalEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StartConditionalEventTest extends StartEvent<StartConditionalEvent> {

    private static final String BPMN_CONDITIONAL_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startConditionalEvents.bpmn";

    private static final String FILLED_TOP_LEVEL_EVENT_ID = "_6148BD21-0D19-4B66-9FB7-7F19A078465B";
    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "_14C6EB8F-09FF-454E-A0C5-50D4A38392A0";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "_C5369C96-8531-4D05-88DA-9261189D70B2";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "_AC859BAB-5995-4125-9C6E-A4A3B9476020";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 10;

    private static final String CONDITION_EXPRESSION_SCRIPT_DEFAULT_VALUE = null;
    private static final String CONDITION_EXPRESSION_LANGUAGE = "drools";
    private static final String CONDITION_ERPRESSION_TYPE = "stunner.bpmn.ScriptType";

    public StartConditionalEventTest(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Conditional event01 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Conditional event01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String CONDITION_EXPRESSION_SCRIPT = "com.myspace.testproject.Person(name == \"John\")";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CONDITIONAL_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartConditionalEvent filledTop = getStartNodeById(diagram, FILLED_TOP_LEVEL_EVENT_ID, StartConditionalEvent.class);
        assertGeneralSet(filledTop.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertConditionalEventExecutionSet(filledTop.getExecutionSet(),
                                           CONDITION_EXPRESSION_SCRIPT,
                                           CONDITION_EXPRESSION_LANGUAGE,
                                           CONDITION_ERPRESSION_TYPE,
                                           INTERRUPTING);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CONDITIONAL_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartConditionalEvent emptyTop = getStartNodeById(diagram, EMPTY_TOP_LEVEL_EVENT_ID, StartConditionalEvent.class);
        assertGeneralSet(emptyTop.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertConditionalEventExecutionSet(emptyTop.getExecutionSet(),
                                           CONDITION_EXPRESSION_SCRIPT_DEFAULT_VALUE,
                                           CONDITION_EXPRESSION_LANGUAGE,
                                           CONDITION_ERPRESSION_TYPE,
                                           INTERRUPTING);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Conditional event02 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Conditional event02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String CONDITION_EXPRESSION_SCRIPT = "com.myspace.testproject.Person(name == \"John\")";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CONDITIONAL_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartConditionalEvent filledSubprocess = getStartNodeById(diagram, FILLED_SUBPROCESS_LEVEL_EVENT_ID, StartConditionalEvent.class);
        assertGeneralSet(filledSubprocess.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertConditionalEventExecutionSet(filledSubprocess.getExecutionSet(),
                                           CONDITION_EXPRESSION_SCRIPT,
                                           CONDITION_EXPRESSION_LANGUAGE,
                                           CONDITION_ERPRESSION_TYPE,
                                           INTERRUPTING);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CONDITIONAL_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartConditionalEvent emptySubprocess = getStartNodeById(diagram, EMPTY_SUBPROCESS_LEVEL_EVENT_ID, StartConditionalEvent.class);
        assertGeneralSet(emptySubprocess.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertConditionalEventExecutionSet(emptySubprocess.getExecutionSet(),
                                           CONDITION_EXPRESSION_SCRIPT_DEFAULT_VALUE,
                                           CONDITION_EXPRESSION_LANGUAGE,
                                           CONDITION_ERPRESSION_TYPE,
                                           NON_INTERRUPTING);
    }

    @Override
    String getBpmnStartEventFilePath() {
        return BPMN_CONDITIONAL_EVENT_FILE_PATH;
    }

    @Override
    String getFilledTopLevelEventId() {
        return FILLED_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String getEmptyTopLevelEventId() {
        return EMPTY_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String getFilledSubprocessLevelEventId() {
        return FILLED_SUBPROCESS_LEVEL_EVENT_ID;
    }

    @Override
    String getEmptySubprocessLevelEventId() {
        return EMPTY_SUBPROCESS_LEVEL_EVENT_ID;
    }

    @Override
    Class<StartConditionalEvent> getStartEventType() {
        return StartConditionalEvent.class;
    }

    private void assertConditionalEventExecutionSet(InterruptingConditionalEventExecutionSet executionSet,
                                                    String conditionExpressionScript,
                                                    String conditionExpressionLanguage,
                                                    String conditionExpressionType,
                                                    boolean isInterrupting) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getConditionExpression());
        assertNotNull(executionSet.getConditionExpression().getValue());
        assertNotNull(executionSet.getConditionExpression().getType());
        assertNotNull(executionSet.getIsInterrupting());

        assertEquals(conditionExpressionLanguage, executionSet.getConditionExpression().getValue().getLanguage());
        assertEquals(conditionExpressionScript, executionSet.getConditionExpression().getValue().getScript());
        assertEquals(conditionExpressionType, executionSet.getConditionExpression().getType().getName());
        assertEquals(isInterrupting, executionSet.getIsInterrupting().getValue());
    }
}
