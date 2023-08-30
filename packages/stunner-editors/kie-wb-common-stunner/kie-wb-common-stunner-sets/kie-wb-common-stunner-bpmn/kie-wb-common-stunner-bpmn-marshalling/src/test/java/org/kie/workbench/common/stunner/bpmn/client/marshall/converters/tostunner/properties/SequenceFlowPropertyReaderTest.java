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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.Point;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TestDefinitionsWriter;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.dc;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SequenceFlowPropertyReaderTest {

    private static final String SCRIPT = "SCRIPT";
    private final String SEQ_ID = "SEQ_ID", SOURCE_ID = "SOURCE_ID", TARGET_ID = "TARGET_ID";

    @Test
    public void getConnectionsNoWaypoints() {
        TestDefinitionsWriter d = new TestDefinitionsWriter();
        PropertyReaderFactory factory = new PropertyReaderFactory(d.getDefinitionResolver());

        Bounds sourceBounds = boundsOf(10, 10, 50, 50);
        FlowNode source = d.mockNode(SOURCE_ID, sourceBounds);
        Bounds targetBounds = boundsOf(100, 100, 60, 60);
        FlowNode target = d.mockNode(TARGET_ID, targetBounds);
        List<Point> noWaypoints = Collections.emptyList();

        SequenceFlow el = d.sequenceFlowOf(SEQ_ID, source, target, noWaypoints);

        SequenceFlowPropertyReader p = factory.of(el);

        // this is inferred from behavior of the old marshallers
        Connection sourceConnection = p.getSourceConnection();
        assertEquals(sourceBounds.getWidth(), (float) sourceConnection.getLocation().getX(), 0);
        assertEquals(sourceBounds.getHeight() / 2f, (float) sourceConnection.getLocation().getY(), 0);

        Connection targetConnection = p.getTargetConnection();
        assertEquals(0.0f, (float) targetConnection.getLocation().getX(), 0);
        assertEquals(targetBounds.getHeight() / 2.0f, (float) targetConnection.getLocation().getY(), 0);
    }

    @Test
    public void getConnectionsWithWaypoints() {
        TestDefinitionsWriter d = new TestDefinitionsWriter();
        PropertyReaderFactory factory = new PropertyReaderFactory(d.getDefinitionResolver());

        Bounds sourceBounds = boundsOf(10, 10, 50, 50);
        FlowNode source = d.mockNode(SOURCE_ID, sourceBounds);
        Bounds targetBounds = boundsOf(100, 100, 60, 60);
        FlowNode target = d.mockNode(TARGET_ID, targetBounds);
        Point sourcePoint = pointOf(10, 20);
        Point targetPoint = pointOf(100, 120);
        List<Point> waypoints = asList(sourcePoint, targetPoint);

        SequenceFlow el = d.sequenceFlowOf(SEQ_ID, source, target, waypoints);

        SequenceFlowPropertyReader p = factory.of(el);

        Connection sourceConnection = p.getSourceConnection();
        assertEquals(sourcePoint.getX() - sourceBounds.getX(), (float) sourceConnection.getLocation().getX(), 0);
        assertEquals(sourcePoint.getY() - sourceBounds.getY(), (float) sourceConnection.getLocation().getY(), 0);

        Connection targetConnection = p.getTargetConnection();
        assertEquals(targetPoint.getX() - targetBounds.getX(), (float) targetConnection.getLocation().getX(), 0);
        assertEquals(targetPoint.getY() - targetBounds.getY(), (float) targetConnection.getLocation().getY(), 0);
    }

    @Test
    public void get1Waypoint() {
        TestDefinitionsWriter d = new TestDefinitionsWriter();
        PropertyReaderFactory factory = new PropertyReaderFactory(d.getDefinitionResolver());

        Bounds sourceBounds = boundsOf(10, 10, 50, 50);
        FlowNode source = d.mockNode(SOURCE_ID, sourceBounds);
        Bounds targetBounds = boundsOf(100, 100, 60, 60);
        FlowNode target = d.mockNode(TARGET_ID, targetBounds);
        Point sourcePoint = pointOf(10, 20);
        Point mid1 = pointOf(15, 25);
        Point targetPoint = pointOf(100, 120);
        List<Point> waypoints = asList(sourcePoint,
                                       mid1,
                                       targetPoint);

        SequenceFlow el = d.sequenceFlowOf(SEQ_ID, source, target, waypoints);

        SequenceFlowPropertyReader p = factory.of(el);
        List<Point2D> controlPoints = p.getControlPoints();
        List<Point2D> expected =
                Collections.singletonList(
                        Point2D.create(mid1.getX(), mid1.getY()));

        assertEquals(expected, controlPoints);
    }

    @Test
    public void get2Waypoints() {
        TestDefinitionsWriter d = new TestDefinitionsWriter();
        PropertyReaderFactory factory = new PropertyReaderFactory(d.getDefinitionResolver());

        Bounds sourceBounds = boundsOf(10, 10, 50, 50);
        FlowNode source = d.mockNode(SOURCE_ID, sourceBounds);
        Bounds targetBounds = boundsOf(100, 100, 60, 60);
        FlowNode target = d.mockNode(TARGET_ID, targetBounds);
        Point sourcePoint = pointOf(10, 20);
        Point mid1 = pointOf(15, 25);
        Point mid2 = pointOf(20, 30);
        Point targetPoint = pointOf(100, 120);
        List<Point> waypoints = asList(sourcePoint,
                                       mid1,
                                       mid2,
                                       targetPoint);

        SequenceFlow el = d.sequenceFlowOf(SEQ_ID, source, target, waypoints);

        SequenceFlowPropertyReader p = factory.of(el);
        List<Point2D> controlPoints = p.getControlPoints();
        List<Point2D> expected = asList(
                Point2D.create(mid1.getX(), mid1.getY()),
                Point2D.create(mid2.getX(), mid2.getY()));

        assertEquals(expected, controlPoints);
    }

    private Point pointOf(float x, float y) {
        Point point = dc.createPoint();
        point.setX(x);
        point.setY(y);
        return point;
    }

    private Bounds boundsOf(float x, float y, float width, float height) {
        Bounds bounds = dc.createBounds();
        bounds.setX(x);
        bounds.setY(y);
        bounds.setWidth(width);
        bounds.setHeight(height);
        return bounds;
    }

    // TODO: Kogito - @Test
    public void testGetConditionExpressionWithFormalExpression() {
        for (Scripts.LANGUAGE language : Scripts.LANGUAGE.values()) {
            FormalExpression formalExpression = mock(FormalExpression.class);
            when(formalExpression.getLanguage()).thenReturn(language.format());
            when(formalExpression.getBody()).thenReturn(SCRIPT);
            testGetConditionExpression(new ScriptTypeValue(language.language(), SCRIPT), formalExpression);
        }
    }

    @Test
    public void testGetConditionExpressionWithoutFormalExpression() {
        testGetConditionExpression(new ScriptTypeValue(Scripts.LANGUAGE.JAVA.language(), ""), null);
    }

    private void testGetConditionExpression(ScriptTypeValue expectedValue, FormalExpression formalExpression) {
        TestDefinitionsWriter d = new TestDefinitionsWriter();
        PropertyReaderFactory factory = new PropertyReaderFactory(d.getDefinitionResolver());
        SequenceFlow sequenceFlow = mock(SequenceFlow.class);
        SequenceFlowPropertyReader propertyReader = factory.of(sequenceFlow);
        when(sequenceFlow.getConditionExpression()).thenReturn(formalExpression);
        assertEquals(expectedValue, propertyReader.getConditionExpression());
    }
}
