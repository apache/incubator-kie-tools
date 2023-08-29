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
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.emf.common.util.ECollections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.PostConverterProcessor;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.events.IntermediateCompensationEventPostConverter;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEvent;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class IntermediateCompensationEventPostConverterTest
        extends AbstractCompensationEventPostConverterTest<IntermediateCompensationEvent, CatchEvent> {

    @Override
    public IntermediateCompensationEvent createEvent() {
        return new IntermediateCompensationEvent();
    }

    @Override
    public CatchEvent createBpmn2Event(CompensateEventDefinition compensateEvent) {
        CatchEvent catchEvent = mock(CatchEvent.class);
        when(catchEvent.getEventDefinitions()).thenReturn(ECollections.singletonEList(compensateEvent));
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
        when(process.getFlowElements()).thenReturn(ECollections.singletonEList(activity));

        converter.process(processWriter, nodeWriter, (Node) node);
        verify(activity).setIsForCompensation(true);
    }
}
