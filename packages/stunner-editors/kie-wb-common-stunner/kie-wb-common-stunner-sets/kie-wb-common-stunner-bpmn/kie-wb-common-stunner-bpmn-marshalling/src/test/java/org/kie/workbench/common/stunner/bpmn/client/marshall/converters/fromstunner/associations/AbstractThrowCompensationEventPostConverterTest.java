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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.associations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.emf.common.util.ECollections;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.compensation.ActivityRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.compensation.CompensationEventExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractThrowCompensationEventPostConverterTest<T extends BPMNViewDefinition>
        extends AbstractCompensationEventPostConverterTest<T, ThrowEvent> {

    protected static final String ACTIVITY_REF = "ACTIVITY_REF";

    @Mock
    protected ActivityRef activityRef;

    @Mock
    protected Activity activity;

    @Before
    public void setUp() {
        super.setUp();
        getExecutionSet(event).getActivityRef().setValue(ACTIVITY_REF);
        when(activity.getId()).thenReturn(ACTIVITY_REF);
    }

    @Override
    public ThrowEvent createBpmn2Event(CompensateEventDefinition compensateEvent) {
        ThrowEvent throwEvent = mock(ThrowEvent.class);
        when(throwEvent.getEventDefinitions()).thenReturn(ECollections.singletonEList(compensateEvent));
        return throwEvent;
    }

    public abstract CompensationEventExecutionSet getExecutionSet(T event);

    @Test
    @SuppressWarnings("unchecked")
    public void testProcessWhenActivityFound() {
        when(process.getFlowElements()).thenReturn(ECollections.singletonEList(activity));
        converter.process(processWriter, nodeWriter, (Node) node);
        verify(compensateEvent).setActivityRef(activity);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProcessWhenActivityNotFound() {
        List<FlowElement> flowElements = new ArrayList<>();
        when(process.getFlowElements()).thenReturn(ECollections.emptyEList());
        converter.process(processWriter, nodeWriter, (Node) node);
        verify(compensateEvent, never()).setActivityRef(activity);
    }
}
