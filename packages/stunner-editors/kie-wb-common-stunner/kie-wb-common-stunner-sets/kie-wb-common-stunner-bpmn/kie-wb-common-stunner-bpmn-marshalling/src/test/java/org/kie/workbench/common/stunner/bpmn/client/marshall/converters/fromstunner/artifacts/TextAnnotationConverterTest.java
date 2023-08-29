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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.artifacts;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.TextAnnotationPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.TextAnnotation;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TextAnnotationConverterTest {

    public static final String NAME = "name";
    public static final String DOC = "doc";
    private TextAnnotationConverter tested;

    @Mock
    private PropertyWriterFactory propertyWriterFactory;

    private Node<View<TextAnnotation>, ?> node;

    @Mock
    private View<TextAnnotation> textAnnotationView;

    private TextAnnotation textAnnotation;

    @Mock
    private TextAnnotationPropertyWriter writer;

    @Before
    public void setUp() {
        textAnnotation = new TextAnnotation();
        textAnnotation.getGeneral().getDocumentation().setValue(DOC);
        textAnnotation.getGeneral().getName().setValue(NAME);
        node = new NodeImpl<>(UUID.uuid());
        node.setContent(textAnnotationView);
        when(textAnnotationView.getDefinition()).thenReturn(textAnnotation);
        when(propertyWriterFactory.of(any(org.eclipse.bpmn2.TextAnnotation.class))).thenReturn(writer);

        tested = new TextAnnotationConverter(propertyWriterFactory);
    }

    @Test
    public void toElement() {
        PropertyWriter propertyWriter = tested.toElement(node);
        verify(writer).setName(NAME);
        verify(writer).setDocumentation(DOC);
        verify(writer).setAbsoluteBounds(node);
        assertEquals(writer, propertyWriter);
    }

    @Test(expected = NullPointerException.class)
    public void toElementWithNullValue() {
        when(textAnnotationView.getDefinition()).thenReturn(null);
        PropertyWriter propertyWriter = tested.toElement(node);
    }
}