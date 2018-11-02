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

import org.eclipse.bpmn2.Association;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Result;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.ElementContainer;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.AssociationPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssociationConverterTest {

    private static final String EDGE_ID = "EDGE_ID";

    private static final String SOURCE_NODE_ID = "SOURCE_NODE_ID";

    private static final String TARGET_NODE_ID = "TARGET_NODE_ID";

    private static final String ERROR_PATTERN = "BasePropertyWriter was not found for source node or target node at edge: %s, pSrc = %s, pTgt = %s";

    @Mock
    private PropertyWriterFactory propertyWriterFactory;

    @Mock
    private AssociationPropertyWriter associationPropertyWriter;

    @Mock
    private Edge<ViewConnector<org.kie.workbench.common.stunner.bpmn.definition.Association> , Node> edge;

    @Mock
    private ViewConnector<org.kie.workbench.common.stunner.bpmn.definition.Association> connector;

    @Mock
    private Node sourceNode;

    @Mock
    private Node targetNode;

    @Mock
    private BasePropertyWriter pSrc;

    @Mock
    private BasePropertyWriter pTgt;

    @Mock
    private ElementContainer process;

    private AssociationConverter converter;

    @Captor
    private ArgumentCaptor<Association> argumentCaptor;

    @Before
    public void setUp() {
        when(propertyWriterFactory.of(any(Association.class))).thenReturn(associationPropertyWriter);
        when(edge.getUUID()).thenReturn(EDGE_ID);
        when(edge.getContent()).thenReturn(connector);
        when(edge.getSourceNode()).thenReturn(sourceNode);
        when(edge.getTargetNode()).thenReturn(targetNode);
        when(sourceNode.getUUID()).thenReturn(SOURCE_NODE_ID);
        when(targetNode.getUUID()).thenReturn(TARGET_NODE_ID);
        when(process.getChildElement(SOURCE_NODE_ID)).thenReturn(pSrc);
        when(process.getChildElement(TARGET_NODE_ID)).thenReturn(pTgt);
        converter = new AssociationConverter(propertyWriterFactory);
    }

    @Test
    public void testToFlowElementSuccess() {
        org.kie.workbench.common.stunner.bpmn.definition.Association association = new org.kie.workbench.common.stunner.bpmn.definition.Association();
        association.setGeneral(new BPMNGeneralSet("nameValue", "documentationValue"));
        when(connector.getDefinition()).thenReturn(association);

        Result<BasePropertyWriter> result = converter.toFlowElement(edge, process);

        assertTrue(result.isSuccess());
        verify(propertyWriterFactory).of(argumentCaptor.capture());
        assertEquals(EDGE_ID, argumentCaptor.getValue().getId());

        verify(associationPropertyWriter).setSource(pSrc);
        verify(associationPropertyWriter).setTarget(pTgt);
        verify(associationPropertyWriter).setConnection(connector);
        verify(associationPropertyWriter).setDocumentation("documentationValue");
        verify(associationPropertyWriter).setOneDirectionAssociation();
    }

    @Test
    public void testToFlowElementWithSourceMissingFailure() {
        when(process.getChildElement(SOURCE_NODE_ID)).thenReturn(null);
        Result<BasePropertyWriter> result = converter.toFlowElement(edge, process);
        verifyFailure(String.format(ERROR_PATTERN, EDGE_ID, null, pTgt), result);
    }

    @Test
    public void testToFlowElementWithTargetMissingFailure() {
        when(process.getChildElement(TARGET_NODE_ID)).thenReturn(null);

        Result<BasePropertyWriter> result = converter.toFlowElement(edge, process);
        verifyFailure(String.format(ERROR_PATTERN, EDGE_ID, pSrc, null), result);
    }

    @Test
    public void testToFlowElementWithSourceAndTargetMissingFailure() {
        when(process.getChildElement(SOURCE_NODE_ID)).thenReturn(null);
        when(process.getChildElement(TARGET_NODE_ID)).thenReturn(null);
        Result<BasePropertyWriter> result = converter.toFlowElement(edge, process);
        verifyFailure(String.format(ERROR_PATTERN, EDGE_ID, null, null), result);
    }

    private void verifyFailure(String expectedError, Result<BasePropertyWriter> result) {
        assertTrue(result.isFailure());
        assertEquals(expectedError, result.asFailure().reason());
    }
}
