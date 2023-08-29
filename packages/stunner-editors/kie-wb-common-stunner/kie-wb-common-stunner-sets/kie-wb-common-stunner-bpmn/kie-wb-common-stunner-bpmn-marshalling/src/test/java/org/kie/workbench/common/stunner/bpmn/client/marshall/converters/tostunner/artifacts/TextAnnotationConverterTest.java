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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.artifacts;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.TextAnnotation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.TextAnnotationPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TextAnnotationConverterTest {

    private TextAnnotationConverter tested;

    @Mock
    private TypedFactoryManager typedFactoryManager;

    @Mock
    private PropertyReaderFactory propertyReaderFactory;

    private TextAnnotation element;

    @Mock
    private Node<View<org.kie.workbench.common.stunner.bpmn.definition.TextAnnotation>, Edge> node;

    @Mock
    private View<org.kie.workbench.common.stunner.bpmn.definition.TextAnnotation> content;

    @Mock
    private org.kie.workbench.common.stunner.bpmn.definition.TextAnnotation def;

    @Mock
    private TextAnnotationPropertyReader reader;

    @Before
    public void setUp() {
        element = Bpmn2Factory.eINSTANCE.createTextAnnotation();
        tested = new TextAnnotationConverter(typedFactoryManager, propertyReaderFactory);

        when(typedFactoryManager.newNode(any(),
                                         eq(org.kie.workbench.common.stunner.bpmn.definition.TextAnnotation.class))).thenReturn(node);
        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(def);
        when(propertyReaderFactory.of(element)).thenReturn(reader);
    }

    @Test
    public void convert() {
        final Result<BpmnNode> node = tested.convert(element);
        final Node<? extends View<? extends BPMNViewDefinition>, ?> value = node.value().value();
        assertEquals(content, value.getContent());
        assertEquals(def, value.getContent().getDefinition());
    }
}