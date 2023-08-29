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


package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.junit.Before;
import org.kie.workbench.common.stunner.core.client.shape.ConnectorViewStub;
import org.kie.workbench.common.stunner.core.client.shape.impl.ConnectorShape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnectorImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractCanvasControlPointCommandTest extends AbstractCanvasCommandTest {

    private static final String EDGE_UUID = UUID.uuid();

    @Mock
    protected ConnectorShape shape;

    protected Edge edge;
    protected ViewConnector viewConnector;
    protected ControlPoint[] controlPoints;
    protected ControlPoint controlPoint1;
    protected ControlPoint controlPoint2;
    protected ControlPoint controlPoint3;
    protected ConnectorViewStub connectorView;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();

        edge = new EdgeImpl<>(EDGE_UUID);
        viewConnector = new ViewConnectorImpl<>(mock(Object.class), Bounds.createEmpty());
        edge.setContent(viewConnector);

        connectorView = spy(new ConnectorViewStub());
        when(shape.getShapeView()).thenReturn(connectorView);
        when(canvasHandler.getGraphIndex().get(EDGE_UUID)).thenReturn(edge);
        when(canvasHandler.getCanvas().getShape(EDGE_UUID)).thenReturn(shape);

        controlPoint1 = ControlPoint.build(1, 1);
        controlPoint2 = ControlPoint.build(2, 2);
        controlPoint3 = ControlPoint.build(3, 3);
        controlPoints = new ControlPoint[]{controlPoint1, controlPoint2, controlPoint3};
        viewConnector.setControlPoints(controlPoints);
        when(connectorView.getManageableControlPoints()).thenReturn(controlPoints);
        when(connectorView.areControlsVisible()).thenReturn(true);
    }

    protected void checkControlPointsVisibilitySwitch(boolean areControlPointsVisible) {
        if (areControlPointsVisible) {
            verify(connectorView, times(1)).hideControlPoints();
            verify(connectorView, times(1)).showControlPoints(eq(HasControlPoints.ControlPointType.POINTS));
        } else {
            verify(connectorView, never()).hideControlPoints();
            verify(connectorView, never()).showControlPoints(any(HasControlPoints.ControlPointType.class));
        }
    }
}
