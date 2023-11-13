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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import java.util.HashSet;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// TODO: Kogito
@Ignore
public class BoundaryEventPropertyWriterTest extends AbstractBasePropertyWriterTest<BoundaryEventPropertyWriter, BoundaryEvent> {

    @Mock
    private FeatureMap featureMap;

    @Captor
    private ArgumentCaptor<FeatureMap.Entry> entryCaptor;

    @Override
    protected BoundaryEventPropertyWriter newPropertyWriter(BoundaryEvent baseElement, VariableScope variableScope) {
        return new BoundaryEventPropertyWriter(baseElement, variableScope, new HashSet<>());
    }

    @Override
    protected BoundaryEvent mockElement() {
        BoundaryEvent eventMock = mock(BoundaryEvent.class);
        when(eventMock.getAnyAttribute()).thenReturn(featureMap);
        return eventMock;
    }

    @Test
    public void testSetCancelActivityTrue() {
        testSetCancelActivity(true);
    }

    @Test
    public void testSetCancelActivityFalse() {
        testSetCancelActivity(false);
    }

    private void testSetCancelActivity(boolean value) {
        propertyWriter.setCancelActivity(value);
        verify(featureMap).add(entryCaptor.capture());
        assertEquals(String.format("drools:boundaryca=%s", value), entryCaptor.getValue().toString());
        verify(element).setCancelActivity(value);
    }

    @Test
    public void testSetParentActivity() {
        ActivityPropertyWriter parentActivityWriter = mock(ActivityPropertyWriter.class);
        Activity activity = mock(Activity.class);
        when(parentActivityWriter.getFlowElement()).thenReturn(activity);
        propertyWriter.setParentActivity(parentActivityWriter);
        verify(element).setAttachedToRef(activity);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddEventDefinition() {
        EList<EventDefinition> eventDefinitions = spy(ECollections.newBasicEList());
        when(element.getEventDefinitions()).thenReturn(eventDefinitions);
        EventDefinition eventDefinition = mock(EventDefinition.class);
        propertyWriter.addEventDefinition(eventDefinition);
        verify(eventDefinitions).add(eventDefinition);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetAbsoluteBounds() {
        Node<View, Edge> node = (Node<View, Edge>) super.createNode();
        Node<View, ?> dockSourceParentNode = mockNode(new Object(), org.kie.workbench.common.stunner.core.graph.content.Bounds.create(PARENT_ABSOLUTE_X1, PARENT_ABSOLUTE_Y1, PARENT_ABSOLUTE_X2, PARENT_ABSOLUTE_Y2));
        double dockSourceRelativeX1 = 15d;
        double dockSourceRelativeY1 = 20d;
        double dockSourceRelativeX2 = 50d;
        double dockSourceAbsoluteY2 = 45d;
        Node<View, ?> dockSourceNode = mockNode(new Object(), org.kie.workbench.common.stunner.core.graph.content.Bounds.create(dockSourceRelativeX1, dockSourceRelativeY1, dockSourceRelativeX2, dockSourceAbsoluteY2), dockSourceParentNode);

        Edge dockEdge = mock(Edge.class);
        when(dockEdge.getSourceNode()).thenReturn(dockSourceNode);
        Dock dock = mock(Dock.class);
        when(dockEdge.getContent()).thenReturn(dock);
        node.getInEdges().clear();
        node.getInEdges().add(dockEdge);

        propertyWriter.setAbsoluteBounds(node);
        Bounds shapeBounds = propertyWriter.getShape().getBounds();
        org.kie.workbench.common.stunner.core.graph.content.Bounds relativeBounds = node.getContent().getBounds();
        double dockSourceAbsoluteX = PARENT_ABSOLUTE_X1 + dockSourceRelativeX1;
        double dockSourceAbsoluteY = PARENT_ABSOLUTE_Y1 + dockSourceRelativeY1;
        assertEquals(dockSourceAbsoluteX + relativeBounds.getX(), shapeBounds.getX(), 0);
        assertEquals(dockSourceAbsoluteY + relativeBounds.getY(), shapeBounds.getY(), 0);
        assertEquals(relativeBounds.getWidth(), shapeBounds.getWidth(), 0);
        assertEquals(relativeBounds.getHeight(), shapeBounds.getHeight(), 0);

        verifyDockerInfoWasSet(relativeBounds);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetAbsoluteBoundsWhenDockedSourceNodeIsNotPresent() {
        //no dock source is set
        Node<View, Edge> node = (Node<View, Edge>) super.createNode();
        org.kie.workbench.common.stunner.core.graph.content.Bounds relativeBounds = node.getContent().getBounds();
        double absoluteX = PARENT_ABSOLUTE_X1 + relativeBounds.getUpperLeft().getX();
        double absoluteY = PARENT_ABSOLUTE_Y1 + relativeBounds.getUpperLeft().getY();

        propertyWriter.setAbsoluteBounds(node);

        Bounds shapeBounds = propertyWriter.getShape().getBounds();
        assertEquals(absoluteX, shapeBounds.getX(), 0);
        assertEquals(absoluteY, shapeBounds.getY(), 0);
        assertEquals(relativeBounds.getWidth(), shapeBounds.getWidth(), 0);
        assertEquals(relativeBounds.getHeight(), shapeBounds.getHeight(), 0);

        verifyDockerInfoWasSet(relativeBounds);
    }

    private void verifyDockerInfoWasSet(org.kie.workbench.common.stunner.core.graph.content.Bounds relativeBounds) {
        verify(featureMap).add(entryCaptor.capture());
        assertEquals(String.format("drools:dockerinfo=%s^%s|", relativeBounds.getUpperLeft().getX(), relativeBounds.getUpperLeft().getY()),
                     entryCaptor.getValue().toString());
    }
}
