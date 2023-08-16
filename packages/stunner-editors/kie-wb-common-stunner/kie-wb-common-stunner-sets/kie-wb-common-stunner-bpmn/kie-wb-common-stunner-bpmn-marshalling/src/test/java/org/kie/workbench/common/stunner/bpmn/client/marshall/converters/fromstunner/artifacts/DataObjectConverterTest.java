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
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.DataObjectPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
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
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataObjectConverterTest {

    public static final String NAME = "name";
    public static final String TYPE = "type";
    private DataObjectConverter tested;

    @Mock
    private PropertyWriterFactory propertyWriterFactory;

    private Node<View<DataObject>, ?> node;

    @Mock
    private View<DataObject> dataObjectView;

    @Mock
    private View<DataObject> dataObjectView2;

    private DataObject dataObject;

    @Mock
    private DataObjectPropertyWriter writer;

    @Before
    public void setUp() {
        dataObject = new DataObject();
        dataObject.setName(new Name(NAME));
        dataObject.setType(new DataObjectType(new DataObjectTypeValue(TYPE)));

        node = new NodeImpl<>(UUID.uuid());
        node.setContent(dataObjectView);

        when(dataObjectView.getDefinition()).thenReturn(dataObject);
        when(propertyWriterFactory.of(any(org.eclipse.bpmn2.DataObjectReference.class))).thenReturn(writer);

        tested = new DataObjectConverter(propertyWriterFactory);
    }

    @Test(expected = NullPointerException.class)
    public void toElement() {
        PropertyWriter propertyWriter = tested.toElement(node);
        verify(writer).setName(NAME);
        verify(writer).setType(TYPE);
        verify(writer).setAbsoluteBounds(node);
        assertEquals(writer, propertyWriter);

        Node<View<DataObject>, ?> node2 = new NodeImpl<>(UUID.uuid());
        node2.setContent(dataObjectView2);
        TextAnnotation textAnnotation = new TextAnnotation();
        when(dataObjectView2.getDefinition()).thenReturn(null);
        PropertyWriter propertyWriter2 = tested.toElement(node2);
    }
}
