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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.arifacts;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.DataObjectPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.TextAnnotationPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.bpmn.definition.TextAnnotation;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectType;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactsConverterTest {

    public static final String NAME = "name";
    public static final String DOC = "doc";
    private ArtifactsConverter artifactsConverter;

    @Mock
    private PropertyWriterFactory propertyWriterFactory;

    private Node<View<TextAnnotation>, ?> textAnnotationNode;

    private Node<View<DataObject>, ?> dataObjectNode;

    @Mock
    private View<TextAnnotation> textAnnotationView;

    @Mock
    private View<DataObject> dataObjectView;

    private TextAnnotation textAnnotation;

    private DataObject dataObject;

    @Mock
    private TextAnnotationPropertyWriter textAnnotationWriter;

    @Mock
    private DataObjectPropertyWriter dataObjectWriter;

    @Test
    public void toTextAnnotationElement() {
        textAnnotation = new TextAnnotation();
        textAnnotation.getGeneral().getDocumentation().setValue(DOC);
        textAnnotation.getGeneral().getName().setValue(NAME);

        textAnnotationNode = new NodeImpl<>(UUID.uuid());
        textAnnotationNode.setContent(textAnnotationView);

        when(textAnnotationView.getDefinition()).thenReturn(textAnnotation);
        when(propertyWriterFactory.of(any(org.eclipse.bpmn2.TextAnnotation.class))).thenReturn(textAnnotationWriter);


        artifactsConverter = new ArtifactsConverter(propertyWriterFactory);

        PropertyWriter propertyWriter = artifactsConverter.toElement(((NodeImpl) textAnnotationNode));
        verify(textAnnotationWriter).setName(NAME);
        verify(textAnnotationWriter).setDocumentation(DOC);
        verify(textAnnotationWriter).setAbsoluteBounds(textAnnotationNode);
        assertEquals(textAnnotationWriter, propertyWriter);
    }

    @Test
    public void toDataObjectElement() {
        dataObject = new DataObject();
        dataObject.getGeneral().getDocumentation().setValue(DOC);
        dataObject.setName(new Name(NAME));
        dataObject.setType(new DataObjectType(new DataObjectTypeValue(NAME)));

        dataObjectNode = new NodeImpl<>(UUID.uuid());
        dataObjectNode.setContent(dataObjectView);

        when(dataObjectView.getDefinition()).thenReturn(dataObject);
        when(propertyWriterFactory.of(any(org.eclipse.bpmn2.DataObjectReference.class))).thenReturn(dataObjectWriter);

        artifactsConverter = new ArtifactsConverter(propertyWriterFactory);

        PropertyWriter propertyWriter = artifactsConverter.toElement(((NodeImpl) dataObjectNode));
        verify(dataObjectWriter).setName(NAME);
        verify(dataObjectWriter).setType(NAME);
        verify(dataObjectWriter).setAbsoluteBounds(dataObjectNode);
        assertEquals(dataObjectWriter, propertyWriter);
    }
}

