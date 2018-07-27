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

package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.kie.workbench.common.stunner.core.client.shape.impl.ConnectorShape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

public abstract class AbstractCanvasControlPointCommandTest extends AbstractCanvasCommandTest {

    protected ControlPoint controlPoint1;

    protected ControlPoint controlPoint2;

    protected Point2D location1;

    private static final String EDGE_UUID = UUID.uuid();

    @Mock
    protected Edge edge;

    @Mock
    protected ConnectorShape shape;

    @Mock
    protected ShapeView shapeView;

    @Mock
    private ViewConnector viewConnector;

    protected List<ControlPoint> controlPointList;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        location1 = new Point2D(0, 0);
        controlPoint1 = ControlPoint.build(location1);
        controlPoint2 = ControlPoint.build(location1, 1);
        controlPointList = Arrays.asList(controlPoint1);

        when(shape.getShapeView()).thenReturn(shapeView);
        when(shape.addControlPoints(controlPoint1)).thenReturn(Arrays.asList(controlPoint2));
        when(edge.getUUID()).thenReturn(EDGE_UUID);
        when(edge.getContent()).thenReturn(viewConnector);
        when(canvasHandler.getCanvas().getShape(EDGE_UUID)).thenReturn(shape);
    }
}
