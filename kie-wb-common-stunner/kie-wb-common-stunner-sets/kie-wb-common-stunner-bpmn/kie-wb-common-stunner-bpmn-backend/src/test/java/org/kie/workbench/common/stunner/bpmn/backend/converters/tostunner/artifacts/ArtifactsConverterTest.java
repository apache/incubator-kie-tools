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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.artifacts;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.DataObjectReference;
import org.eclipse.bpmn2.TextAnnotation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Result;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.DataObjectPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.TextAnnotationPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactsConverterTest {

    private ArtifactsConverter tested;

    @Mock
    private TypedFactoryManager typedFactoryManager;

    @Mock
    private PropertyReaderFactory propertyReaderFactory;

    @Mock
    private Node<View<org.kie.workbench.common.stunner.bpmn.definition.TextAnnotation>, Edge> nodeTextAnnotation;

    @Mock
    private Node<View<org.kie.workbench.common.stunner.bpmn.definition.DataObject>, Edge> nodeDataObject;

    @Mock
    private View<org.kie.workbench.common.stunner.bpmn.definition.TextAnnotation> contentTextAnnotation;

    @Mock
    private View<org.kie.workbench.common.stunner.bpmn.definition.DataObject> contentDataObject;

    @Mock
    private org.kie.workbench.common.stunner.bpmn.definition.TextAnnotation defTextAnnotation;

    @Mock
    private org.kie.workbench.common.stunner.bpmn.definition.DataObject defDataObject;

    @Mock
    private TextAnnotationPropertyReader readerTextAnnotation;

    @Mock
    private DataObjectPropertyReader readerDataObject;

    @Before
    public void setUp() {
        tested = new ArtifactsConverter(typedFactoryManager, propertyReaderFactory);
    }

    @Test
    public void convertTextAnnotation() {
        TextAnnotation element = Bpmn2Factory.eINSTANCE.createTextAnnotation();


        when(typedFactoryManager.newNode(anyString(),
                                         eq(org.kie.workbench.common.stunner.bpmn.definition.TextAnnotation.class))).thenReturn(nodeTextAnnotation);
        when(nodeTextAnnotation.getContent()).thenReturn(contentTextAnnotation);
        when(contentTextAnnotation.getDefinition()).thenReturn(defTextAnnotation);
        when(propertyReaderFactory.of(element)).thenReturn(readerTextAnnotation);

        final Result<BpmnNode> node = tested.convert(element);
        final Node<? extends View<? extends BPMNViewDefinition>, ?> value = node.value().value();
        assertEquals(contentTextAnnotation, value.getContent());
        assertEquals(defTextAnnotation, value.getContent().getDefinition());
    }

    @Test
    public void convertDataObject() {
        DataObjectReference element = Bpmn2Factory.eINSTANCE.createDataObjectReference();
        when(typedFactoryManager.newNode(anyString(),
                                         eq(org.kie.workbench.common.stunner.bpmn.definition.DataObject.class))).thenReturn(nodeDataObject);
        when(nodeDataObject.getContent()).thenReturn(contentDataObject);
        when(contentDataObject.getDefinition()).thenReturn(defDataObject);
        when(propertyReaderFactory.of(element)).thenReturn(readerDataObject);

        final Result<BpmnNode> node = tested.convert(element);
        final Node<? extends View<? extends BPMNViewDefinition>, ?> value = node.value().value();
        assertEquals(contentDataObject, value.getContent());
        assertEquals(defDataObject, value.getContent().getDefinition());
    }
}