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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.associations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.bpmn2.Association;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnEdge;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.AssociationPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.DirectionalAssociation;
import org.kie.workbench.common.stunner.bpmn.definition.NonDirectionalAssociation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssociationConverterTest {

    private static final String ASSOCIATION_ID = "ASSOCIATION_ID";

    private static final String ASSOCIATION_DOCUMENTATION = "ASSOCIATION_DOCUMENTATION";

    private static final String SOURCE_ID = "SOURCE_ID";

    private static final String TARGET_ID = "TARGET_ID";

    @Mock
    private PropertyReaderFactory propertyReaderFactory;

    @Mock
    private TypedFactoryManager factoryManager;

    @Mock
    private Edge<View<org.kie.workbench.common.stunner.bpmn.definition.Association>, Node> edge;

    @Mock
    private View<org.kie.workbench.common.stunner.bpmn.definition.Association> content;

    @Mock
    private org.kie.workbench.common.stunner.bpmn.definition.Association definition;

    @Captor
    private ArgumentCaptor<BPMNGeneralSet> generalSetCaptor;

    @Mock
    private Association association;

    @Mock
    private AssociationPropertyReader associationReader;

    private AssociationConverter associationConverter;

    private Map<String, BpmnNode> nodes;

    @Mock
    private BpmnNode sourceNode;

    @Mock
    private BpmnNode targetNode;

    @Mock
    private Connection sourceConnection;

    @Mock
    private Connection targetConnection;

    @Mock
    private List<Point2D> controlPoints;

    @Mock
    private Edge<View<NonDirectionalAssociation>, Node> edgeNonDirectional;

    @Mock
    private View<NonDirectionalAssociation> contentNonDirectional;

    @Mock
    private NonDirectionalAssociation definitionNonDirectional;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(association.getId()).thenReturn(ASSOCIATION_ID);
        when(edge.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(definition);
        when(factoryManager.newEdge(ASSOCIATION_ID, DirectionalAssociation.class)).thenReturn((Edge) edge);
        when(propertyReaderFactory.of(association)).thenReturn(associationReader);
        when(associationReader.getAssociationByDirection()).thenAnswer(a -> DirectionalAssociation.class);
        when(associationReader.getDocumentation()).thenReturn(ASSOCIATION_DOCUMENTATION);
        when(associationReader.getSourceId()).thenReturn(SOURCE_ID);
        when(associationReader.getTargetId()).thenReturn(TARGET_ID);
        when(associationReader.getSourceConnection()).thenReturn(sourceConnection);
        when(associationReader.getTargetConnection()).thenReturn(targetConnection);
        when(associationReader.getControlPoints()).thenReturn(controlPoints);

        associationConverter = new AssociationConverter(factoryManager, propertyReaderFactory);
        nodes = new HashMap<>();
        nodes.put(SOURCE_ID, sourceNode);
        nodes.put(TARGET_ID, targetNode);
    }

    @Test
    public void testConvertEdge() {
        associationConverter.convertEdge(association, nodes);
        verify(definition).setGeneral(generalSetCaptor.capture());
        assertEquals(ASSOCIATION_DOCUMENTATION, generalSetCaptor.getValue().getDocumentation().getValue());
        assertEdgeWithConnections();
    }

    private void assertEdgeWithConnections() {
        BpmnEdge.Simple result = (BpmnEdge.Simple) associationConverter.convertEdge(association, nodes).value();
        assertEquals(sourceNode, result.getSource());
        assertEquals(targetNode, result.getTarget());
        assertEquals(sourceConnection, result.getSourceConnection());
        assertEquals(targetConnection, result.getTargetConnection());
        assertEquals(controlPoints, result.getControlPoints());
        assertEquals(edge, result.getEdge());
    }

    @Test
    public void testConvertEdgeNonDirectional() {
        when(factoryManager.newEdge(ASSOCIATION_ID, NonDirectionalAssociation.class)).thenReturn((Edge) edgeNonDirectional);
        when(associationReader.getAssociationByDirection()).thenAnswer(a -> NonDirectionalAssociation.class);
        when(edgeNonDirectional.getContent()).thenReturn(contentNonDirectional);
        when(contentNonDirectional.getDefinition()).thenReturn(definitionNonDirectional);

        BpmnEdge.Simple result = (BpmnEdge.Simple) associationConverter.convertEdge(association, nodes).value();
        assertEquals(edgeNonDirectional, result.getEdge());
    }

    @Test
    public void testConvertIgnoredEdge() {
        //assert connections
        assertEdgeWithConnections();

        //now remove the source node
        nodes.remove(SOURCE_ID);

        BpmnEdge.Simple result = (BpmnEdge.Simple) associationConverter.convertEdge(association, nodes).value();
        assertNull(result);

        //add source node and remove target
        nodes.put(SOURCE_ID, sourceNode);
        nodes.remove(TARGET_ID);

        result = (BpmnEdge.Simple) associationConverter.convertEdge(association, nodes).value();
        assertNull(result);

        //adding both again
        nodes.put(SOURCE_ID, sourceNode);
        nodes.put(TARGET_ID, targetNode);
        assertEdgeWithConnections();
    }
}
