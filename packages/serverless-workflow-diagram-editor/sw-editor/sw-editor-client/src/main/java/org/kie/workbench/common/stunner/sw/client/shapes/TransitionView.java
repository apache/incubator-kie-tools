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


package org.kie.workbench.common.stunner.sw.client.shapes;

import com.ait.lienzo.client.core.event.OrthogonalPolylinePointsChangedEvent;
import com.ait.lienzo.client.core.event.OrthogonalPolylinePointsChangedHandler;
import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
import com.ait.lienzo.client.core.shape.wires.util.WiresConnectorLabel;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.tools.client.event.INodeEvent;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext.WiresConnectorViewExt;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ShapeViewSupportedEvents;

public class TransitionView extends WiresConnectorViewExt<TransitionView> {

    private static final double SELECTION_OFFSET = 30;
    private static final double DECORATOR_WIDTH = 8;
    private static final double DECORATOR_HEIGHT = 12;
    private static final double OVERLAP_CORRECTION = 3;
    private static final double STROKE_WIDTH = 2;

    private static final double[] DEFAULT_POLYLINE_POINTS = {0, 0, 100, 100};

    public TransitionView(String color) {
        this(color, DEFAULT_POLYLINE_POINTS);
    }

    public TransitionView(String color, final double... points) {
        this(createLine(new OrthogonalPolyLine(Point2DArray.fromArrayOfDouble(points)), color));
    }

    private TransitionView(final Object[] line) {
        super(ShapeViewSupportedEvents.DESKTOP_CONNECTOR_EVENT_TYPES,
              (OrthogonalPolyLine) line[0],
              (MultiPathDecorator) line[1],
              (MultiPathDecorator) line[2]);

        initHandlers((OrthogonalPolyLine) line[0]);
    }

    static Object[] createLine(AbstractDirectionalMultiPointShape<?> line, String color) {

        // The head decorator must be not visible, as connectors are unidirectional.
        final MultiPath head = new MultiPath();
        final MultiPath tail = getArrowMultiPath();
        head.setStrokeColor(color);
        tail.setStrokeColor(color);

        line.setDraggable(true);
        line.setSelectionStrokeOffset(SELECTION_OFFSET);
        line.setHeadOffset(head.getBoundingBox().getHeight());
        line.setTailOffset(tail.getBoundingBox().getHeight());
        line.asShape().setFillColor(color).setStrokeColor(color).setStrokeWidth(STROKE_WIDTH);

        final MultiPathDecorator headDecorator = new MultiPathDecorator(head);
        final MultiPathDecorator tailDecorator = new MultiPathDecorator(tail);

        return new Object[]{line, headDecorator, tailDecorator};
    }

    private void initHandlers(final OrthogonalPolyLine line) {
        OrthogonalPolylinePointsChangedHandler handler = new OrthogonalPolylinePointsChangedHandler() {
            @Override
            public INodeEvent.Type<OrthogonalPolylinePointsChangedHandler> getType() {
                return new INodeEvent.Type<>();
            }

            @Override
            public void onOrthogonalPointsChanged(OrthogonalPolylinePointsChangedEvent event) {
                final WiresConnectorLabel label = getLabel();
                if (null != label) {
                    label.setOrthogonalPoints(event.getOrthogonalPoints().asList());
                }
            }
        };

        line.addOrthogonalPolylinePointsChangedHandler(handler);
    }

    private static MultiPath getArrowMultiPath() {
        return new MultiPath()
                .M(DECORATOR_WIDTH,
                   DECORATOR_HEIGHT + OVERLAP_CORRECTION)
                .L(0,
                   DECORATOR_HEIGHT + OVERLAP_CORRECTION)
                .L(DECORATOR_WIDTH / 2,
                   OVERLAP_CORRECTION)
                .Z();
    }
}