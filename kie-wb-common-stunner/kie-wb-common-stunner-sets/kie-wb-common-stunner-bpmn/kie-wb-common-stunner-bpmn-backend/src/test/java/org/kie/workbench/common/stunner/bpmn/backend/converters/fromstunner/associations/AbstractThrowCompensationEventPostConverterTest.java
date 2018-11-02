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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.associations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.ThrowEvent;
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
        List<EventDefinition> eventDefinitions = Collections.singletonList(compensateEvent);
        when(throwEvent.getEventDefinitions()).thenReturn(eventDefinitions);
        return throwEvent;
    }

    public abstract CompensationEventExecutionSet getExecutionSet(T event);

    @Test
    @SuppressWarnings("unchecked")
    public void testProcessWhenActivityFound() {
        List<FlowElement> flowElements = Collections.singletonList(activity);
        when(process.getFlowElements()).thenReturn(flowElements);
        converter.process(processWriter, nodeWriter, (Node) node);
        verify(compensateEvent).setActivityRef(activity);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProcessWhenActivityNotFound() {
        List<FlowElement> flowElements = new ArrayList<>();
        when(process.getFlowElements()).thenReturn(flowElements);
        converter.process(processWriter, nodeWriter, (Node) node);
        verify(compensateEvent, never()).setActivityRef(activity);
    }
}
