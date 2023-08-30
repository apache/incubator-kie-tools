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

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.ScopedSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class IntermediateSignalEventThrowingTest {

    private BPMNGeneralSet general;
    private BackgroundSet backgroundSet;
    private FontSet fontSet;
    private CircleDimensionSet dimensionSet;
    private DataIOSet dataIOSet;
    private AdvancedData advancedData;
    private ScopedSignalEventExecutionSet scopedSignalEventExecutionSet;

    private IntermediateSignalEventThrowing tested;

    @Before
    public void setUp() {
        general = mock(BPMNGeneralSet.class);
        backgroundSet = mock(BackgroundSet.class);
        fontSet = mock(FontSet.class);
        dimensionSet = mock(CircleDimensionSet.class);
        dataIOSet = mock(DataIOSet.class);
        scopedSignalEventExecutionSet = mock(ScopedSignalEventExecutionSet.class);

        tested = new IntermediateSignalEventThrowing(general,
                                                     backgroundSet,
                                                     fontSet,
                                                     dimensionSet,
                                                     dataIOSet,
                                                     advancedData,
                                                     scopedSignalEventExecutionSet);
    }

    @Test
    public void getExecutionSet() {
        assertEquals(scopedSignalEventExecutionSet, tested.getExecutionSet());
    }

    @Test
    public void setExecutionSet() {
        ScopedSignalEventExecutionSet executionSet = mock(ScopedSignalEventExecutionSet.class);
        tested.setExecutionSet(executionSet);
        assertEquals(executionSet, tested.executionSet);
    }

    @Test
    public void testHashCode() {
        IntermediateSignalEventThrowing compare =
                new IntermediateSignalEventThrowing(general,
                                                    backgroundSet,
                                                    fontSet,
                                                    dimensionSet,
                                                    dataIOSet,
                                                    advancedData,
                                                    scopedSignalEventExecutionSet);
        assertEquals(compare.hashCode(), tested.hashCode());
    }

    @Test
    public void testEquals() {
        IntermediateSignalEventCatching compare1 = new IntermediateSignalEventCatching();
        IntermediateSignalEventThrowing compare2 = new IntermediateSignalEventThrowing(general,
                                                                                       backgroundSet,
                                                                                       fontSet,
                                                                                       dimensionSet,
                                                                                       dataIOSet,
                                                                                       advancedData,
                                                                                       null);
        ScopedSignalEventExecutionSet executionSet = new ScopedSignalEventExecutionSet();
        executionSet.setSignalRef(new SignalRef("SignalRef"));
        IntermediateSignalEventThrowing compare3 =
                new IntermediateSignalEventThrowing(general,
                                                    backgroundSet,
                                                    fontSet,
                                                    dimensionSet,
                                                    dataIOSet,
                                                    advancedData,
                                                    executionSet);
        IntermediateSignalEventThrowing compare4 =
                new IntermediateSignalEventThrowing(general,
                                                    backgroundSet,
                                                    fontSet,
                                                    dimensionSet,
                                                    dataIOSet,
                                                    advancedData,
                                                    scopedSignalEventExecutionSet);

        assertFalse(tested.equals(compare1));
        assertFalse(tested.equals(compare2));
        assertFalse(tested.equals(compare3));
        assertTrue(tested.equals(compare4));
    }
}