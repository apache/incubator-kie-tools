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

import java.util.List;

import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.dd.dc.Point;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.DocumentationTextHandler;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnectorImpl;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.failNotEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.dc;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts.asCData;

public class SequenceFlowPropertyWriterTest {

    private final String SEQ_ID = "SEQ_ID", SOURCE_ID = "SOURCE_ID", TARGET_ID = "TARGET_ID";

    @Test
    public void setConnectionMagnetsNullLocation() {

        TestSequenceFlowWriter w = new TestSequenceFlowWriter();
        SequenceFlowPropertyWriter p = w.sequenceFlowOf(SEQ_ID);

        float sx = 10, sy = 10, sWidth = 50, sHeight = 70;
        PropertyWriter source = w.nodeOf(SOURCE_ID, sx, sy, sWidth, sHeight);

        float tx = 100, ty = 100, tWidth = 20, tHeight = 30;
        PropertyWriter target = w.nodeOf(TARGET_ID, tx, ty, tWidth, tHeight);

        p.setSource(source);
        p.setTarget(target);

        // magnets have null location
        ViewConnectorImpl<SequenceFlow> connector = makeConnector();
        connector.setSourceConnection(new MagnetConnection.Builder().build());
        connector.setTargetConnection(new MagnetConnection.Builder().build());
        p.setConnection(connector);

        BPMNEdge edge = p.getEdge();
        List<Point> expected = asList(
                pointOf(sWidth + sx,
                        sHeight / 2 + sy),
                pointOf(tx,
                        tHeight / 2 + ty));
        List<Point> waypoints = edge.getWaypoint();

        assertPointsEqual(expected, waypoints,
                          "when magnet location is null: " +
                                  "source magnet should be right/middle, " +
                                  "target magnet should left/middle");
    }

    @Test
    public void setConnectionMagnets() {
        TestSequenceFlowWriter w = new TestSequenceFlowWriter();
        SequenceFlowPropertyWriter p = w.sequenceFlowOf(SEQ_ID);

        float sx = 10, sy = 10, sWidth = 50, sHeight = 70;
        PropertyWriter source = w.nodeOf(SOURCE_ID, sx, sy, sWidth, sHeight);

        float tx = 100, ty = 100, tWidth = 20, tHeight = 30;
        PropertyWriter target = w.nodeOf(TARGET_ID, tx, ty, tWidth, tHeight);

        p.setSource(source);
        p.setTarget(target);

        // magnets have null location
        ViewConnectorImpl<SequenceFlow> connector = makeConnector();
        connector.setSourceConnection(MagnetConnection.Builder.at(1, 2));
        connector.setTargetConnection(MagnetConnection.Builder.at(2, 3));
        p.setConnection(connector);

        BPMNEdge edge = p.getEdge();
        List<Point> expected = asList(
                pointOf(sx + 1,
                        sy + 2),
                pointOf(tx + 2,
                        ty + 3));
        List<Point> waypoints = edge.getWaypoint();

        assertPointsEqual(expected, waypoints, "when magnet location is defined, waypoints should be translated into an absolute position");
    }

    @Test
    public void setControlPoints() {
        TestSequenceFlowWriter w = new TestSequenceFlowWriter();
        SequenceFlowPropertyWriter p = w.sequenceFlowOf(SEQ_ID);

        float sx = 10, sy = 10, sWidth = 50, sHeight = 70;
        PropertyWriter source = w.nodeOf(SOURCE_ID, sx, sy, sWidth, sHeight);

        float tx = 100, ty = 100, tWidth = 20, tHeight = 30;
        PropertyWriter target = w.nodeOf(TARGET_ID, tx, ty, tWidth, tHeight);

        p.setSource(source);
        p.setTarget(target);

        // magnets have null location
        ViewConnectorImpl<SequenceFlow> connector = makeConnector();
        connector.setSourceConnection(MagnetConnection.Builder.at(1, 2));
        connector.setTargetConnection(MagnetConnection.Builder.at(2, 3));
        connector.setControlPoints(new ControlPoint[]{
                ControlPoint.build(Point2D.create(
                        sx + 100,
                        sy + 100)),
                ControlPoint.build(Point2D.create(
                        sx + 150,
                        sy + 150))
        });
        p.setConnection(connector);

        BPMNEdge edge = p.getEdge();
        List<Point> expected = asList(
                pointOf(sx + 1,
                        sy + 2),
                pointOf(sx + 100,
                        sy + 100),
                pointOf(sx + 150,
                        sy + 150),
                pointOf(tx + 2,
                        ty + 3));
        List<Point> waypoints = edge.getWaypoint();

        assertPointsEqual(expected, waypoints, "when magnet location is defined, waypoints should be translated into an absolute position");
    }

    @Test
    public void JBPM_7522_shouldPersistProperties() {
        TestSequenceFlowWriter w = new TestSequenceFlowWriter();
        SequenceFlowPropertyWriter p = w.sequenceFlowOf(SEQ_ID);

        String name = "Name";
        String doc = "Doc";
        String priority = "100";
        ScriptTypeValue scriptTypeValue =
                new ScriptTypeValue("java", "System.out.println(1);");

        p.setName(name);
        p.setDocumentation(doc);
        p.setPriority(priority);
        p.setConditionExpression(scriptTypeValue);

        org.eclipse.bpmn2.SequenceFlow seq =
                (org.eclipse.bpmn2.SequenceFlow) p.getFlowElement();

        assertThat(seq.getName()).isEqualTo(name);
        assertThat(DocumentationTextHandler.of(seq.getDocumentation().get(0)).getText()).isEqualTo(asCData(doc));
        assertThat(CustomAttribute.priority.of(seq).get()).isEqualTo(priority);
        assertThat(seq.getConditionExpression()).isNotNull();
    }

    private static ViewConnectorImpl<SequenceFlow> makeConnector() {
        return new ViewConnectorImpl<>(new SequenceFlow(), Bounds.create(0, 0, 1000, 1000));
    }

    private static void assertPointsEqual(List<Point> expected, List<Point> given, String message) {
        assertEquals(expected.size(), given.size());
        for (int i = 0; i < expected.size(); i++) {
            Point pe = expected.get(i);
            Point pg = given.get(i);
            if (pe.getX() != pg.getX() || pe.getY() != pg.getY()) {
                failNotEquals(message, expected, given);
            }
        }
    }

    private static Point pointOf(double x, double y) {
        Point point = dc.createPoint();
        point.setX((float) x);
        point.setY((float) y);
        return point;
    }
}