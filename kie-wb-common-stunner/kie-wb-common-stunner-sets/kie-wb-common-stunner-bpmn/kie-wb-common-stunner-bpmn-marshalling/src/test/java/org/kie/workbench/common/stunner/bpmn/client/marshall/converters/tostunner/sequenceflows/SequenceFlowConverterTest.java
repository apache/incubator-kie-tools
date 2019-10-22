/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.sequenceflows;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.FlowNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnEdge;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.SequenceFlowPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SequenceFlowConverterTest {

    public static final String SOURCE_ID = "1";
    public static final String TARGET_ID = "2";
    private SequenceFlowConverter tested;

    @Mock
    private TypedFactoryManager factoryManager;

    @Mock
    private PropertyReaderFactory propertyReaderFactory;

    @Mock
    private Edge<View<SequenceFlow>, Node> edge;

    @Mock
    private View<SequenceFlow> content;

    @Mock
    private SequenceFlow def;

    @Mock
    private SequenceFlowPropertyReader reader;

    @Before
    public void setUp() throws Exception {
        when(factoryManager.newEdge(anyString(), eq(SequenceFlow.class))).thenReturn(edge);
        when(edge.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(def);
        when(propertyReaderFactory.of(any(org.eclipse.bpmn2.SequenceFlow.class))).thenReturn(reader);
        when(reader.getSourceId()).thenReturn(SOURCE_ID);
        when(reader.getTargetId()).thenReturn(TARGET_ID);

        tested = new SequenceFlowConverter(factoryManager, propertyReaderFactory);
    }

    @Test
    public void convertEdge() {
        org.eclipse.bpmn2.SequenceFlow sequenceFlow = Bpmn2Factory.eINSTANCE.createSequenceFlow();
        FlowNode source = Bpmn2Factory.eINSTANCE.createUserTask();
        sequenceFlow.setSourceRef(source);
        FlowNode target = Bpmn2Factory.eINSTANCE.createBusinessRuleTask();
        sequenceFlow.setTargetRef(target);

        //testing with empty source/target nodes, the edge should be ignored
        Result<BpmnEdge> result = tested.convertEdge(sequenceFlow, new HashMap<>());
        assertTrue(result.isIgnored());
        assertNull(result.value());

        //testing with the source/target nodes
        Map<String, BpmnNode> nodes = new Maps.Builder<String, BpmnNode>()
                .put(SOURCE_ID, mock(BpmnNode.class))
                .put(TARGET_ID, mock(BpmnNode.class))
                .build();
        result = tested.convertEdge(sequenceFlow, nodes);
        assertTrue(result.isSuccess());
        BpmnEdge.Simple value = (BpmnEdge.Simple) result.value();
        assertEquals(edge, value.getEdge());
    }
}