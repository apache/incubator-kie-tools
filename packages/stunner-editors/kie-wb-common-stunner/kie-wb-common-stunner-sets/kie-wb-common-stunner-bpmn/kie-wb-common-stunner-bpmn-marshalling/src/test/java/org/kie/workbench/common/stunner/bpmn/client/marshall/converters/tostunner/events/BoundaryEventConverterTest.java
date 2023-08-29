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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.events;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BoundaryEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnEdge;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnNode;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BoundaryEventConverterTest {

    public static final String PARENT_ID = "1";
    public static final String CHILD_ID = "2";
    private BoundaryEventConverter tested;

    @Mock
    protected BpmnNode node1;

    @Mock
    protected BpmnNode node2;

    @Mock
    private BoundaryEvent event;

    @Mock
    private Activity ref;

    @Before
    public void setUp() throws Exception {

        tested = new BoundaryEventConverter();

        when(event.getAttachedToRef()).thenReturn(ref);
        when(ref.getId()).thenReturn(PARENT_ID);
        when(event.getId()).thenReturn(CHILD_ID);
    }

    @Test
    public void convertEdge() {
        Map<String, BpmnNode> nodes = Stream.of(
                        new AbstractMap.SimpleEntry<>(PARENT_ID, node1),
                        new AbstractMap.SimpleEntry<>(CHILD_ID, node2))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Result<BpmnEdge> result = tested.convertEdge(event, nodes);
        BpmnEdge value = result.value();
        assertTrue(result.isSuccess());
        assertEquals(node1, value.getSource());
        assertEquals(node2, value.getTarget());
        assertTrue(value.isDocked());
    }

    @Test
    public void convertMissingNodes() {
        Map<String, BpmnNode> nodes = new HashMap<>();
        Result<BpmnEdge> result = tested.convertEdge(event, nodes);
        BpmnEdge value = result.value();
        assertTrue(result.isIgnored());
        assertNull(value);
    }
}