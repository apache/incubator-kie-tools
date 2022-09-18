/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.marshall;

import org.junit.Test;
import org.kie.workbench.common.stunner.sw.definition.StartDefinition;
import org.kie.workbench.common.stunner.sw.definition.StateEnd;
import org.kie.workbench.common.stunner.sw.definition.StateTransition;
import org.kie.workbench.common.stunner.sw.definition.Workflow;
import org.kie.workbench.common.stunner.sw.definition.Workflow_JsonMapperImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StartTransitionEndObjectsTest {

    private final Workflow_JsonMapperImpl mapper = Workflow_JsonMapperImpl.INSTANCE;

    @Test
    public void test(){
        String json = "{\n" +
                " \"id\": \"startTransitionEndObjects\",\n" +
                " \"name\": \"Start, Transition and End as Objects\",\n" +
                " \"start\": {\n" +
                "  \"stateName\": \"Init State\",\n" +
                "  \"schedule\": \"2020-03-20T09:00:00Z/2020-03-20T15:00:00Z\"\n" +
                " },\n" +
                " \"states\": [\n" +
                "  {\n" +
                "   \"name\": \"Init State\",\n" +
                "   \"type\": \"inject\",\n" +
                "   \"end\": false,\n" +
                "   \"transition\": {\n" +
                "    \"nextState\": \"Next State\"\n" +
                "   },\n" +
                "   \"onErrors\": [\n" +
                "    {\n" +
                "     \"errorRef\": \"Some Error\",\n" +
                "     \"end\": false,\n" +
                "     \"transition\": {\n" +
                "      \"nextState\": \"Error State\"\n" +
                "     }\n" +
                "    }\n" +
                "   ]\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"Error State\",\n" +
                "   \"type\": \"inject\",\n" +
                "   \"end\": true\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"Next State\",\n" +
                "   \"type\": \"inject\",\n" +
                "   \"end\": {\n" +
                "    \"terminate\": true\n" +
                "   }\n" +
                "  }\n" +
                " ]\n" +
                "}";

        Workflow workflow = mapper.fromJSON(json);
        assertNotNull(workflow);

        assertEquals("startTransitionEndObjects", workflow.getId());
        assertEquals("Start, Transition and End as Objects", workflow.getName());
        assertEquals(StartDefinition.class, workflow.getStart().getClass());
        assertEquals("Init State", ((StartDefinition)workflow.getStart()).getStateName());
        assertEquals("2020-03-20T09:00:00Z/2020-03-20T15:00:00Z", ((StartDefinition)workflow.getStart()).getSchedule());
        assertEquals(3, workflow.getStates().length);
        assertEquals("Init State", workflow.getStates()[0].getName());
        assertEquals("Error State", workflow.getStates()[1].getName());
        assertEquals("Next State", workflow.getStates()[2].getName());

        assertEquals("inject", workflow.getStates()[0].getType());
        assertEquals("inject", workflow.getStates()[1].getType());
        assertEquals("inject", workflow.getStates()[2].getType());

        assertFalse((Boolean)workflow.getStates()[0].getEnd());
        assertTrue((Boolean)workflow.getStates()[1].getEnd());
        assertTrue(((StateEnd)workflow.getStates()[2].getEnd()).getTerminate());

        assertEquals("Next State", ((StateTransition)workflow.getStates()[0].getTransition()).getNextState());
        assertEquals(1, workflow.getStates()[0].getOnErrors().length);
        assertFalse((Boolean) workflow.getStates()[0].getOnErrors()[0].getEnd());
        assertEquals("Some Error", workflow.getStates()[0].getOnErrors()[0].getErrorRef());
        assertEquals("Error State", ((StateTransition)workflow.getStates()[0].getOnErrors()[0].getTransition()).getNextState());
    }
}
