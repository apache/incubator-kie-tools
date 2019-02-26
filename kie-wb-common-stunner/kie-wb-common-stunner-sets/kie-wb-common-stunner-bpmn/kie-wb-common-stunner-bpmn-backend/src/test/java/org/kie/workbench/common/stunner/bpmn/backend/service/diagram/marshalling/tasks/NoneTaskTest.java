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
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.EmptyTaskExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertNotNull;

public class NoneTaskTest extends Task<NoneTask> {

    private static final String BPMN_TASK_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/noneTasks.bpmn";

    private static final String EMPTY_TOP_LEVEL_TASK_ID = "D9666725-31FB-4D20-8E81-579D8BA893CE";
    private static final String FILLED_TOP_LEVEL_TASK_ID = "4EE0C527-0D2E-411C-B277-1E6A60C9053B";
    private static final String EMPTY_SUBPROCESS_LEVEL_TASK_ID = "9DD93380-3FFB-4DD3-88B9-5423E29FB3CF";
    private static final String FILLED_SUBPROCESS_LEVEL_TASK_ID = "740A426C-9E13-4502-8031-FC002E6FCFA3";

    private static final String EMPTY_ONE_INCOME_SUBPROCESS_LEVEL_TASK_ID = "51C523B0-594E-4BBF-B50F-D190A3F6A567";
    private static final String FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_ID = "B6137593-169A-41A9-A1BD-240B54EF3F1E";
    private static final String EMPTY_ONE_INCOME_TOP_LEVEL_TASK_ID = "D7A75B7F-E37E-4960-BF0F-EC8867EC143A";
    private static final String FILLED_ONE_INCOME_TOP_LEVEL_TASK_ID = "C5F45AA4-2B5B-4BFE-8BEC-1FCCFA3A0E2A";

    private static final String EMPTY_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_ID = "981DEA00-9B6A-4FA1-86AB-7835D802A151";
    private static final String FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_ID = "344AC834-66D4-4FF9-89CF-EB90A3ECABCE";
    private static final String EMPTY_TWO_INCOMES_TOP_LEVEL_TASK_ID = "F4D41CC4-B8A4-40C5-8973-0FF278F4324A";
    private static final String FILLED_TWO_INCOMES_TOP_LEVEL_TASK_ID = "715FE636-0609-4031-96D4-7394B2B0D88D";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 36;

    private static Diagram<Graph, Metadata> oldDiagram;
    private static Diagram<Graph, Metadata> oldRoundTripDiagram;

    private static Diagram<Graph, Metadata> newDiagram;
    private static Diagram<Graph, Metadata> newRoundTripDiagram;

    public NoneTaskTest(Marshaller marshallerType) throws Exception {
        super(marshallerType, marshallers());
    }

    @Override
    synchronized Diagram<Graph, Metadata> getOldDiagram() {
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
    synchronized Diagram<Graph, Metadata> getNewDiagram() {
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
    public void testUnmarshallTopLevelTaskFilledProperties() {
        final String TASK_NAME = "Task01 name ~!@#$%^&*()_+`-={}[]:\"|;'\\<>?,./";
        final String TASK_DOCUMENTATION = "Task01 doc\n ~!@#$%^&*()_+`1234567890-={}[]:\"|;'\\<>?,./";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        NoneTask filledTopLevelTask = getTaskNodeById(getDiagram(),
                                                      FILLED_TOP_LEVEL_TASK_ID,
                                                      ZERO_INCOME_EDGES,
                                                      HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTask.getGeneral(), TASK_NAME, TASK_DOCUMENTATION);
        assertNoneTaskExecutionSet(filledTopLevelTask.getExecutionSet());
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyTaskProperties() {
        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        NoneTask emptyTopLevelTask = getTaskNodeById(getDiagram(),
                                                     EMPTY_TOP_LEVEL_TASK_ID,
                                                     ZERO_INCOME_EDGES,
                                                     HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(emptyTopLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertNoneTaskExecutionSet(emptyTopLevelTask.getExecutionSet());
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskFilledProperties() {
        final String TASK_NAME = "Task03 name ~!@#$%^&*()_+`-={}[]:\"|;'\\<>?,./";
        final String TASK_DOCUMENTATION = "Task03 doc\n ~!@#$%^&*()_+`1234567890-={}[]:\"|;'\\<>?,./";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        NoneTask filledSubprocessLevelTask = getTaskNodeById(getDiagram(),
                                                             FILLED_SUBPROCESS_LEVEL_TASK_ID,
                                                             ZERO_INCOME_EDGES,
                                                             HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTask.getGeneral(), TASK_NAME, TASK_DOCUMENTATION);
        assertNoneTaskExecutionSet(filledSubprocessLevelTask.getExecutionSet());
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskEmptyProperties() {
        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        NoneTask emptySubprocessLevelTask = getTaskNodeById(getDiagram(),
                                                            EMPTY_SUBPROCESS_LEVEL_TASK_ID,
                                                            ZERO_INCOME_EDGES,
                                                            HAS_NO_OUTCOME_EDGE);
        assertGeneralSet(emptySubprocessLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertNoneTaskExecutionSet(emptySubprocessLevelTask.getExecutionSet());
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskOneIncomeFilledProperties() {
        final String TASK_NAME = "Task02 name ~!@#$%^&*()_+`-={}[]:\"|;'\\<>?,./";
        final String TASK_DOCUMENTATION = "Task02 doc\n ~!@#$%^&*()_+`1234567890-={}[]:\"|;'\\<>?,./";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        NoneTask filledTopLevelTask = getTaskNodeById(getDiagram(),
                                                      FILLED_ONE_INCOME_TOP_LEVEL_TASK_ID,
                                                      ONE_INCOME_EDGE,
                                                      HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTask.getGeneral(), TASK_NAME, TASK_DOCUMENTATION);
        assertNoneTaskExecutionSet(filledTopLevelTask.getExecutionSet());
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskOneIncomeEmptyProperties() {
        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        NoneTask emptyTopLevelTask = getTaskNodeById(getDiagram(),
                                                     EMPTY_ONE_INCOME_TOP_LEVEL_TASK_ID,
                                                     ONE_INCOME_EDGE,
                                                     HAS_OUTCOME_EDGE);
        assertGeneralSet(emptyTopLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertNoneTaskExecutionSet(emptyTopLevelTask.getExecutionSet());
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskOneIncomeEmptyProperties() {
        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        NoneTask emptySubprocessLevelTask = getTaskNodeById(getDiagram(),
                                                            EMPTY_ONE_INCOME_SUBPROCESS_LEVEL_TASK_ID,
                                                            ONE_INCOME_EDGE,
                                                            HAS_OUTCOME_EDGE);
        assertGeneralSet(emptySubprocessLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertNoneTaskExecutionSet(emptySubprocessLevelTask.getExecutionSet());
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskOneIncomeFilledProperties() {
        final String TASK_NAME = "Task04 name ~!@#$%^&*()_+`-={}[]:\"|;'\\<>?,./";
        final String TASK_DOCUMENTATION = "Task04 doc\n ~!@#$%^&*()_+`1234567890-={}[]:\"|;'\\<>?,./";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        NoneTask filledSubprocessLevelTask = getTaskNodeById(getDiagram(),
                                                             FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_ID,
                                                             ONE_INCOME_EDGE,
                                                             HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTask.getGeneral(), TASK_NAME, TASK_DOCUMENTATION);
        assertNoneTaskExecutionSet(filledSubprocessLevelTask.getExecutionSet());
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskTwoIncomesFilledProperties() {
        final String TASK_NAME = "Task05 name ~!@#$%^&*()_+`-={}[]:\"|;'\\<>?,./";
        final String TASK_DOCUMENTATION = "Task05 doc\n ~!@#$%^&*()_+`1234567890-={}[]:\"|;'\\<>?,./";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        NoneTask filledTopLevelTask = getTaskNodeById(getDiagram(),
                                                      FILLED_TWO_INCOMES_TOP_LEVEL_TASK_ID,
                                                      TWO_INCOME_EDGES,
                                                      HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTask.getGeneral(), TASK_NAME, TASK_DOCUMENTATION);
        assertNoneTaskExecutionSet(filledTopLevelTask.getExecutionSet());
    }

    @Test
    @Override
    public void testUnmarshallTopLevelTaskTwoIncomesEmptyProperties() {
        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        NoneTask filledTopLevelTask = getTaskNodeById(getDiagram(),
                                                      EMPTY_TWO_INCOMES_TOP_LEVEL_TASK_ID,
                                                      TWO_INCOME_EDGES,
                                                      HAS_OUTCOME_EDGE);
        assertGeneralSet(filledTopLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertNoneTaskExecutionSet(filledTopLevelTask.getExecutionSet());
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskTwoIncomesEmptyProperties() {
        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        NoneTask emptySubprocessLevelTask = getTaskNodeById(getDiagram(),
                                                            EMPTY_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_ID,
                                                            TWO_INCOME_EDGES,
                                                            HAS_OUTCOME_EDGE);
        assertGeneralSet(emptySubprocessLevelTask.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertNoneTaskExecutionSet(emptySubprocessLevelTask.getExecutionSet());
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelTaskTwoIncomesFilledProperties() {
        final String TASK_NAME = "Task06 name ~!@#$%^&*()_+`-={}[]:\"|;'\\<>?,./";
        final String TASK_DOCUMENTATION = "Task06 doc\n ~!@#$%^&*()_+`1234567890-={}[]:\"|;'\\<>?,./";

        assertDiagram(getDiagram(), AMOUNT_OF_NODES_IN_DIAGRAM);

        NoneTask filledSubprocessLevelTask = getTaskNodeById(getDiagram(),
                                                             FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_ID,
                                                             TWO_INCOME_EDGES,
                                                             HAS_OUTCOME_EDGE);
        assertGeneralSet(filledSubprocessLevelTask.getGeneral(), TASK_NAME, TASK_DOCUMENTATION);
        assertNoneTaskExecutionSet(filledSubprocessLevelTask.getExecutionSet());
    }

    @Override
    String getBpmnTaskFilePath() {
        return BPMN_TASK_FILE_PATH;
    }

    @Override
    Class<NoneTask> getTaskType() {
        return NoneTask.class;
    }

    @Override
    String getFilledTopLevelTaskId() {
        return FILLED_TOP_LEVEL_TASK_ID;
    }

    @Override
    String getEmptyTopLevelTaskId() {
        return EMPTY_TOP_LEVEL_TASK_ID;
    }

    @Override
    String getFilledSubprocessLevelTaskId() {
        return FILLED_SUBPROCESS_LEVEL_TASK_ID;
    }

    @Override
    String getEmptySubprocessLevelTaskId() {
        return EMPTY_SUBPROCESS_LEVEL_TASK_ID;
    }

    @Override
    String getFilledTopLevelTaskOneIncomeId() {
        return FILLED_ONE_INCOME_TOP_LEVEL_TASK_ID;
    }

    @Override
    String getEmptyTopLevelTaskOneIncomeId() {
        return EMPTY_ONE_INCOME_TOP_LEVEL_TASK_ID;
    }

    @Override
    String getFilledSubprocessLevelTaskOneIncomeId() {
        return FILLED_ONE_INCOME_SUBPROCESS_LEVEL_TASK_ID;
    }

    @Override
    String getEmptySubprocessLevelTaskOneIncomeId() {
        return EMPTY_ONE_INCOME_SUBPROCESS_LEVEL_TASK_ID;
    }

    @Override
    String getFilledTopLevelTaskTwoIncomesId() {
        return FILLED_TWO_INCOMES_TOP_LEVEL_TASK_ID;
    }

    @Override
    String getEmptyTopLevelTaskTwoIncomesId() {
        return EMPTY_TWO_INCOMES_TOP_LEVEL_TASK_ID;
    }

    @Override
    String getFilledSubprocessLevelTaskTwoIncomesId() {
        return FILLED_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_ID;
    }

    @Override
    String getEmptySubprocessLevelTaskTwoIncomesId() {
        return EMPTY_TWO_INCOMES_SUBPROCESS_LEVEL_TASK_ID;
    }

    private void assertNoneTaskExecutionSet(EmptyTaskExecutionSet executionSet) {
        assertNotNull(executionSet);
    }
}
