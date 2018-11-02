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
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.FlowElement;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.PostConverterProcessor;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.events.IntermediateCompensationEventPostConverter;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEvent;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IntermediateCompensationEventPostConverterTest
        extends AbstractCompensationEventPostConverterTest<IntermediateCompensationEvent, CatchEvent> {

    @Override
    public IntermediateCompensationEvent createEvent() {
        return new IntermediateCompensationEvent();
    }

    @Override
    public CatchEvent createBpmn2Event(CompensateEventDefinition compensateEvent) {
        CatchEvent catchEvent = mock(CatchEvent.class);
        List<EventDefinition> eventDefinitions = Collections.singletonList(compensateEvent);
        when(catchEvent.getEventDefinitions()).thenReturn(eventDefinitions);
        return catchEvent;
    }

    @Override
    public PostConverterProcessor createConverter() {
        return new IntermediateCompensationEventPostConverter();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProcessWhenOutEdgeExists() {
        String uuid = "UUID";
        Edge edge = mock(Edge.class);
        Node targetNode = mock(Node.class);
        when(targetNode.getUUID()).thenReturn(uuid);
        List outEdges = new ArrayList<>();
        outEdges.add(edge);
        when(node.getOutEdges()).thenReturn(outEdges);

        Activity activity = mock(Activity.class);
        when(activity.getId()).thenReturn(uuid);
        when(edge.getTargetNode()).thenReturn(targetNode);
        List<FlowElement> flowElements = Collections.singletonList(activity);
        when(process.getFlowElements()).thenReturn(flowElements);

        converter.process(processWriter, nodeWriter, (Node) node);
        verify(activity).setIsForCompensation(true);
    }
}
