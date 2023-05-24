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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class AbstractControlPointCommandTest extends AbstractGraphCommandTest {

    static final String EDGE_UUID = "cpEdge";

    @Mock
    protected Edge edge;

    @Mock
    protected ViewConnector viewConnector;

    protected ControlPoint controlPoint1;

    protected ControlPoint controlPoint2;

    protected ControlPoint controlPoint3;

    public void setUp() {
        super.init();
        when(edge.getUUID()).thenReturn(EDGE_UUID);
        when(graphIndex.get(eq(EDGE_UUID))).thenReturn(edge);
        when(graphIndex.getEdge(eq(EDGE_UUID))).thenReturn(edge);
        controlPoint1 = ControlPoint.create(new Point2D(1, 1));
        controlPoint2 = ControlPoint.create(new Point2D(2, 2));
        controlPoint3 = ControlPoint.create(new Point2D(3, 3));
        when(edge.getContent()).thenReturn(viewConnector);
        when(viewConnector.getControlPoints())
                .thenReturn(new ControlPoint[]{controlPoint1, controlPoint2, controlPoint3});
    }
}
