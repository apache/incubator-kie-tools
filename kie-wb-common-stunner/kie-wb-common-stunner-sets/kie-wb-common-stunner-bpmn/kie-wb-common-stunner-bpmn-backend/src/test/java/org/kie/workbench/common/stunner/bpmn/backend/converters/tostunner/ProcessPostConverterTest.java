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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.dc.Point;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.BasePropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.SequenceFlowPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.dc;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.GraphBuilderTest.mockBpmnNode;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.GraphBuilderTest.mockNode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProcessPostConverterTest {

    @Test
    public void testPostConvert() {

        DefinitionResolver definitionResolver = mock(DefinitionResolver.class);

        double laneX = 80;
        double laneY = 100;
        double laneWidth = 500;
        double laneHeight = 200;
        RectangleDimensionsSet laneRectangleDimensionsSet = new RectangleDimensionsSet(laneWidth, laneHeight);
        Lane laneDefinition = mock(Lane.class);
        when(laneDefinition.getDimensionsSet()).thenReturn(laneRectangleDimensionsSet);
        Node<? extends View<? extends BPMNViewDefinition>, ?> lane = mockNode(laneDefinition, laneX, laneY, laneWidth, laneHeight);
        BpmnNode laneNode = mockBpmnNode(lane);

        double startEventX = 180;
        double startEventY = 130;
        double eventWidth = 56;
        double eventHeight = 56;
        Node<? extends View<? extends BPMNViewDefinition>, ?> startEvent = mockNode(mock(StartNoneEvent.class), startEventX + laneX, startEventY + laneY, eventWidth, eventHeight);
        BpmnNode startEventNode = mockBpmnNode(startEvent);

        //subprocess is collapsed and has a boundaryEvent, and contains task1 and task2 (task2 has a boundaryEvent)
        double subprocessX = 270;
        double subprocessY = 180;
        double subprocessWidth = 100;
        double subprocessHeight = 60;
        RectangleDimensionsSet subprocessRectangleDimensionsSet = new RectangleDimensionsSet(subprocessWidth, subprocessHeight);
        EmbeddedSubprocess subprocessDefinition = mock(EmbeddedSubprocess.class);
        when(subprocessDefinition.getDimensionsSet()).thenReturn(subprocessRectangleDimensionsSet);
        Node<? extends View<? extends BPMNViewDefinition>, ?> subprocess = mockNode(subprocessDefinition, subprocessX + laneX, subprocessY + laneY, subprocessWidth, subprocessHeight);
        BpmnNode subprocessNode = mockBpmnNode(subprocess);
        BasePropertyReader subprocessPropertyReader = subprocessNode.getPropertyReader();
        when(subprocessPropertyReader.isExpanded()).thenReturn(false);

        double subprocessBoundaryEventX = subprocessWidth - 28;
        double subprocessBoundaryEventY = (subprocessHeight / 2) - 28;
        Node<? extends View<? extends BPMNViewDefinition>, ?> subprocessBoundaryEvent = mockNode(mock(IntermediateTimerEvent.class), subprocessBoundaryEventX, subprocessBoundaryEventY, eventWidth, eventHeight);
        BpmnNode subprocessBoundaryEventNode = mockBpmnNode(subprocessBoundaryEvent).docked();

        double task1X = 10;
        double task1Y = 10;
        double taskWidth = 200;
        double taskHeight = 100;
        Node<? extends View<? extends BPMNViewDefinition>, ?> task1 = mockNode(mock(UserTask.class), task1X, task1Y, taskWidth, taskHeight);
        BpmnNode task1Node = mockBpmnNode(task1);

        double task2X = 300;
        double task2Y = 200;
        Node<? extends View<? extends BPMNViewDefinition>, ?> task2 = mockNode(mock(UserTask.class), task2X, task2Y, taskWidth, taskHeight);
        BpmnNode task2Node = mockBpmnNode(task2);

        double task2BoundaryEventX = taskWidth - 28;
        double task2BoundaryEventY = taskHeight - 28;
        Node<? extends View<? extends BPMNViewDefinition>, ?> task2BoundaryEvent = mockNode(mock(IntermediateTimerEvent.class), task2BoundaryEventX, task2BoundaryEventY, eventWidth, eventHeight);
        BpmnNode task2BoundaryEventNode = mockBpmnNode(task2BoundaryEvent).docked();

        double endEventX = 450;
        double endEventY = 230;
        Node<? extends View<? extends BPMNViewDefinition>, ?> endEvent = mockNode(mock(EndNoneEvent.class), endEventX + laneX, endEventY + laneY, eventWidth, eventHeight);
        BpmnNode endEventNode = mockBpmnNode(endEvent);

        double task3X = 500;
        double task3Y = 600;
        Node<? extends View<? extends BPMNViewDefinition>, ?> task3 = mockNode(mock(UserTask.class), task3X + laneX, task3Y + laneY, taskWidth, taskHeight);
        BpmnNode task3Node = mockBpmnNode(task3);

        double task3BoundaryEventX = taskWidth - 28;
        double task3BoundaryEventY = taskHeight - 28;
        Node<? extends View<? extends BPMNViewDefinition>, ?> task3BoundaryEvent = mockNode(mock(IntermediateTimerEvent.class), task3BoundaryEventX, task3BoundaryEventY, eventWidth, eventHeight);
        BpmnNode task3BoundaryEventNode = mockBpmnNode(task3BoundaryEvent).docked();

        double task4X = 900;
        double task4Y = 1000;
        Node<? extends View<? extends BPMNViewDefinition>, ?> task4 = mockNode(mock(UserTask.class), task4X + laneX, task4Y + laneY, taskWidth, taskHeight);
        BpmnNode task4Node = mockBpmnNode(task4);

        List<Point2D> controlPoints = new ArrayList<>();
        controlPoints.add(Point2D.create(900 + 100 + laneX, 700 + laneY));
        Connection sourceConnection = MagnetConnection.Builder.at(56, 28).setAuto(false);
        Connection targetConnection = MagnetConnection.Builder.at(100, 0).setAuto(false);

        BPMNEdge bpmnEdge = mock(BPMNEdge.class);
        BaseElement baseElement = mock(BaseElement.class);
        SequenceFlowPropertyReader edgePropertyReader = mock(SequenceFlowPropertyReader.class);
        when(edgePropertyReader.getDefinitionResolver()).thenReturn(definitionResolver);
        when(edgePropertyReader.getElement()).thenReturn(baseElement);
        when(baseElement.getId()).thenReturn("elementId");
        when(definitionResolver.getEdge("elementId")).thenReturn(bpmnEdge);

        List<Point> wayPoints = new ArrayList<>();
        wayPoints.add(mockPoint((float) (700 + 28 + laneX), (float) (700 + laneY)));
        wayPoints.add(mockPoint((float) (1000 + laneX), (float) (700 + laneY)));
        wayPoints.add(mockPoint((float) (900 + 100 + laneX), (float) (1000 + laneY)));
        when(bpmnEdge.getWaypoint()).thenReturn(wayPoints);

        org.eclipse.dd.dc.Bounds sourceShapeBounds = mockBounds((float) (task3X + taskWidth - 28 + laneX), (float) (task3Y + taskHeight - 28 + laneY), (float) eventWidth, (float) eventHeight);
        BPMNShape sourceShape = mockShape(sourceShapeBounds);
        when(task3BoundaryEventNode.getPropertyReader().getShape()).thenReturn(sourceShape);

        org.eclipse.dd.dc.Bounds targetShapeBounds = mockBounds(task4.getContent().getBounds());
        BPMNShape targetShape = mockShape(targetShapeBounds);
        when(task4Node.getPropertyReader().getShape()).thenReturn(targetShape);

        BpmnEdge.Simple edgeTask3BoundaryEventToTask4 = BpmnEdge.of(null, task3BoundaryEventNode, sourceConnection, controlPoints, task4Node, targetConnection, edgePropertyReader);

        Node<? extends View<? extends BPMNViewDefinition>, ?> diagram = mockNode(mock(BPMNDiagramImpl.class), 0, 0, 10000, 10000);
        BpmnNode rootNode = mockBpmnNode(diagram);

        rootNode.addChild(laneNode);
        laneNode.addChild(startEventNode);
        laneNode.addChild(subprocessNode);
        laneNode.addChild(subprocessBoundaryEventNode);
        rootNode.addEdge(BpmnEdge.docked(subprocessNode, subprocessBoundaryEventNode));
        subprocessNode.addChild(task1Node);
        subprocessNode.addChild(task2Node);
        subprocessNode.addChild(task2BoundaryEventNode);
        subprocessNode.addEdge(BpmnEdge.docked(task2Node, task2BoundaryEventNode));
        laneNode.addChild(task3Node);
        laneNode.addChild(task3BoundaryEventNode);
        laneNode.addChild(task4Node);
        rootNode.addEdge(BpmnEdge.docked(task3Node, task3BoundaryEventNode));
        rootNode.addEdge(edgeTask3BoundaryEventToTask4);
        laneNode.addChild(endEventNode);

        ProcessPostConverter postConverter = new ProcessPostConverter();
        when(definitionResolver.getResolutionFactor()).thenReturn(2d);
        postConverter.postConvert(rootNode, definitionResolver);

        Bounds startEventBounds = startEventNode.value().getContent().getBounds();
        //preserves original position
        assertEquals(laneX + startEventX, startEventBounds.getUpperLeft().getX(), 0);
        assertEquals(laneY + startEventY, startEventBounds.getUpperLeft().getY(), 0);
        assertEquals(eventWidth, startEventBounds.getWidth(), 0);
        assertEquals(eventHeight, startEventBounds.getHeight(), 0);

        Bounds subProcessBounds = subprocessNode.value().getContent().getBounds();
        //was properly resized and preserves original position
        assertEquals(laneX + subprocessX, subProcessBounds.getUpperLeft().getX(), 0);
        assertEquals(laneY + subprocessY, subProcessBounds.getUpperLeft().getY(), 0);
        assertEquals(300 + 200 + 10, subProcessBounds.getWidth(), 0);
        assertEquals(200 + 100 + 10, subProcessBounds.getHeight(), 0);

        Bounds subProcessBoundaryEventBounds = subprocessBoundaryEventNode.value().getContent().getBounds();
        //boundary event was properly located
        assertEquals(subProcessBounds.getWidth() - 28, subProcessBoundaryEventBounds.getUpperLeft().getX(), 0);
        assertEquals(subProcessBounds.getHeight() / subprocessHeight * subprocessBoundaryEventY, subProcessBoundaryEventBounds.getUpperLeft().getY(), 0);

        Bounds task1Bounds = task1Node.value().getContent().getBounds();
        //was properly positioned and preserves size
        assertEquals(laneX + subprocessX + task1X, task1Bounds.getUpperLeft().getX(), 0);
        assertEquals(laneY + subprocessY + task1Y, task1Bounds.getUpperLeft().getY(), 0);
        assertEquals(taskWidth, task1Bounds.getWidth(), 0);
        assertEquals(taskHeight, task1Bounds.getHeight(), 0);

        Bounds task2Bounds = task2Node.value().getContent().getBounds();
        //was properly positioned and preserves size
        assertEquals(laneX + subprocessX + task2X, task2Bounds.getUpperLeft().getX(), 0);
        assertEquals(laneY + subprocessY + task2Y, task2Bounds.getUpperLeft().getY(), 0);
        assertEquals(taskWidth, task1Bounds.getWidth(), 0);
        assertEquals(taskHeight, task1Bounds.getHeight(), 0);

        Bounds task2BoundaryEventBounds = task2BoundaryEventNode.value().getContent().getBounds();
        //is relative to the task2 in the converted model and preserves size
        assertEquals(task2BoundaryEventX, task2BoundaryEventBounds.getUpperLeft().getX(), 0);
        assertEquals(task2BoundaryEventY, task2BoundaryEventBounds.getUpperLeft().getY(), 0);
        assertEquals(eventWidth, task2BoundaryEventBounds.getWidth(), 0);
        assertEquals(eventHeight, task2BoundaryEventBounds.getHeight(), 0);

        Bounds endEventBounds = endEventNode.value().getContent().getBounds();
        double subprocessDeltaX = subProcessBounds.getWidth() - subprocessWidth;
        double subprocessDeltaY = subProcessBounds.getHeight() - subprocessHeight;
        //was properly moved after the subprocess resize
        assertEquals(laneX + endEventX + subprocessDeltaX, endEventBounds.getUpperLeft().getX(), 0);
        assertEquals(laneY + endEventY + subprocessDeltaY, endEventBounds.getUpperLeft().getY(), 0);

        Bounds task3Bounds = task3Node.value().getContent().getBounds();
        //was properly moved after the subprocess resize
        assertEquals(laneX + task3X + subprocessDeltaX, task3Bounds.getUpperLeft().getX(), 0);
        assertEquals(laneY + task3Y + subprocessDeltaY, task3Bounds.getUpperLeft().getY(), 0);

        Bounds task3BoundaryEventBounds = task3BoundaryEventNode.value().getContent().getBounds();
        //is relative to the task3 in the converted model and preserves size
        assertEquals(task3BoundaryEventX, task3BoundaryEventBounds.getUpperLeft().getX(), 0);
        assertEquals(task3BoundaryEventY, task3BoundaryEventBounds.getUpperLeft().getY(), 0);
        assertEquals(eventWidth, task3BoundaryEventBounds.getWidth(), 0);
        assertEquals(eventHeight, task3BoundaryEventBounds.getHeight(), 0);

        Bounds task4Bounds = task4Node.value().getContent().getBounds();
        //was properly moved after the subprocess resize
        assertEquals(laneX + task4X + subprocessDeltaX, task4Bounds.getUpperLeft().getX(), 0);
        assertEquals(laneY + task4Y + subprocessDeltaY, task4Bounds.getUpperLeft().getY(), 0);

        //edgeTask3BoundaryEventToTask4 was properly translated and magnets are ok
        assertEquals(56, edgeTask3BoundaryEventToTask4.getSourceConnection().getLocation().getX(), 0);
        assertEquals(28, edgeTask3BoundaryEventToTask4.getSourceConnection().getLocation().getY(), 0);
        assertEquals(task4Node.value().getContent().getBounds().getUpperLeft().getX() + taskWidth / 2, controlPoints.get(0).getX(), 0);
        assertEquals(task3Node.value().getContent().getBounds().getLowerRight().getY(), controlPoints.get(0).getY(), 0);
    }

    private static Point mockPoint(float x, float y) {
        Point point = mock(Point.class);
        when(point.getX()).thenReturn(x);
        when(point.getY()).thenReturn(y);
        return point;
    }

    private static org.eclipse.dd.dc.Bounds mockBounds(float x, float y, float width, float height) {
        org.eclipse.dd.dc.Bounds bounds = dc.createBounds();
        bounds.setX(x);
        bounds.setY(y);
        bounds.setWidth(width);
        bounds.setHeight(height);
        return bounds;
    }

    private static org.eclipse.dd.dc.Bounds mockBounds(Bounds bounds) {
        return mockBounds((float) bounds.getX(), (float) bounds.getY(), (float) bounds.getWidth(), (float) bounds.getHeight());
    }

    private static BPMNShape mockShape(org.eclipse.dd.dc.Bounds bounds) {
        BPMNShape shape = mock(BPMNShape.class);
        when(shape.getBounds()).thenReturn(bounds);
        return shape;
    }
}
