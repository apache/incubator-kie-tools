/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.definition;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.ReflectionAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

public class BaseCatchingIntermediateEventTest {

    private FakeBaseCatchingIntermediateEvent tested;

    @Before
    public void setUp() {
        tested = spy(new FakeBaseCatchingIntermediateEvent());
    }

    @Test
    public void initLabels() {
        tested.initLabels();
        assertTrue(tested.labels.contains("all"));
        assertTrue(tested.labels.contains("lane_child"));
        assertTrue(tested.labels.contains("sequence_start"));
        assertTrue(tested.labels.contains("sequence_end"));
        assertTrue(tested.labels.contains("to_task_event"));
        assertTrue(tested.labels.contains("from_task_event"));
        assertTrue(tested.labels.contains("fromtoall"));
        assertTrue(tested.labels.contains("IntermediateEventOnSubprocessBoundary"));
        assertTrue(tested.labels.contains("IntermediateEventOnActivityBoundary"));
        assertTrue(tested.labels.contains("EventOnChoreographyActivityBoundary"));
        assertTrue(tested.labels.contains("IntermediateEventsMorph"));
        assertTrue(tested.labels.contains("cm_nop"));
        assertTrue(tested.labels.contains("IntermediateEventCatching"));
    }

    @Test
    public void hasOutputVars() {
        assertTrue(tested.hasOutputVars());
    }

    @Test
    public void isSingleOutputVar() {
        assertTrue(tested.isSingleOutputVar());
    }

    @Test
    public void getCategory() {
        assertEquals(BaseCatchingIntermediateEvent.category, tested.getCategory());
    }

    @Test
    public void testBaseCatchingIntermediateEventCanBeContainedByALane() throws Exception {

        final FakeBaseCatchingIntermediateEvent baseCatchingIntermediateEvent = new FakeBaseCatchingIntermediateEvent();
        final Set<String> labels = ReflectionAdapterUtils.getAnnotatedFieldValue(baseCatchingIntermediateEvent, Labels.class);

        assertNotNull(labels);
        assertTrue(labels.contains("lane_child"));
    }

    private class FakeBaseCatchingIntermediateEvent extends BaseCatchingIntermediateEvent {

    }
}