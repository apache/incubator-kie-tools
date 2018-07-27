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

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

public class AbstractControlPointCommandTest extends AbstractGraphCommandTest {

    @Mock
    protected Edge edge;

    protected ControlPoint controlPoint1;

    protected ControlPoint controlPoint2;

    protected ControlPoint controlPoint3;

    protected Point2D newLocation;

    protected List<ControlPoint> controlPointList;

    @Mock
    protected ViewConnector viewConnector;

    public void setUp() {
        super.init(0, 0);

        newLocation = new Point2D(0, 0);
        controlPoint1 = ControlPoint.build(new Point2D(1, 1), 1);
        controlPoint2 = ControlPoint.build(new Point2D(2, 2), 2);
        controlPoint3 = ControlPoint.build(new Point2D(3, 3), 3);
        controlPointList = new ArrayList<ControlPoint>() {{
            add(controlPoint1);
            add(controlPoint2);
            add(controlPoint3);
        }};

        when(edge.getContent()).thenReturn(viewConnector);
        when(viewConnector.getControlPoints()).thenReturn(controlPointList);
    }
}
