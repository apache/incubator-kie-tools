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


package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.util.EqualsAndHashCodeTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MultipleInstanceExecutionModeTest {

    @Test
    public void testEqualsAndHashCode() {
        EqualsAndHashCodeTestUtils.TestCaseBuilder.newTestCase()
                .addTrueCase(new MultipleInstanceExecutionMode(), new MultipleInstanceExecutionMode())
                .addTrueCase(new MultipleInstanceExecutionMode(true), new MultipleInstanceExecutionMode(true))
                .addTrueCase(new MultipleInstanceExecutionMode(false), new MultipleInstanceExecutionMode(false))
                .addFalseCase(new MultipleInstanceExecutionMode(true), new MultipleInstanceExecutionMode(false))
                .addFalseCase(new MultipleInstanceExecutionMode(false), new MultipleInstanceExecutionMode(true))
                .test();
    }

    @Test
    public void testCreateFromSequentialTrue() {
        MultipleInstanceExecutionMode executionMode = new MultipleInstanceExecutionMode(true);
        assertTrue(executionMode.isSequential());
        assertEquals(ExecutionOrder.SEQUENTIAL.value(), executionMode.getValue());
    }

    @Test
    public void testCreateFromSequentialFalse() {
        MultipleInstanceExecutionMode executionMode = new MultipleInstanceExecutionMode(false);
        assertFalse(executionMode.isSequential());
        assertEquals(ExecutionOrder.PARALLEL.value(), executionMode.getValue());
    }
}
