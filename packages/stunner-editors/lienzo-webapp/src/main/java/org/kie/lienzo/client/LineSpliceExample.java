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


package org.kie.lienzo.client;

import java.util.List;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IControlPointsAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.ILineSpliceAcceptor;
import com.ait.lienzo.client.core.shape.wires.ILocationAcceptor;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeHighlightImpl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import elemental2.dom.HTMLDivElement;

import static org.kie.lienzo.client.util.WiresUtils.connect;

public class LineSpliceExample extends BaseExample implements Example {

    private static final String LABEL_PARENT = "parent";
    private static final String LABEL_CHILD = "child";

    private WiresManager wiresManager;
    private WiresShape rectangleRedShape;
    private WiresShape circleRedShape;
    private WiresShape rectangleGreenShape;
    private WiresShape circleGreenShape;
    private WiresShape rectangleYellowShape;
    private WiresShape rectangleBlackShape;
    private WiresShapeHighlightImpl highlight;

    public LineSpliceExample(final String title) {
        super(title);
        highlight = new WiresShapeHighlightImpl(10);
    }

    @Override
    public void init(LienzoPanel panel,
                     HTMLDivElement topDiv) {
        super.init(panel, topDiv);
    }

    @Override
    public void run() {
        // Wires setup
        wiresManager = WiresManager.get(layer);
        wiresManager.setSpliceEnabled(true);
        wiresManager.enableSelectionManager();
        wiresManager.setContainmentAcceptor(CONTAINMENT_ACCEPTOR);
        wiresManager.setDockingAcceptor(DOCKING_ACCEPTOR);
        wiresManager.setConnectionAcceptor(IConnectionAcceptor.ALL);
        wiresManager.setLocationAcceptor(ILocationAcceptor.ALL);
        wiresManager.setControlPointsAcceptor(IControlPointsAcceptor.ALL);
        wiresManager.setLineSpliceAcceptor(new ILineSpliceAcceptor() {
            @Override
            public boolean allowSplice(WiresShape shape,
                                       double[] candidateLocation,
                                       WiresConnector connector,
                                       WiresContainer parent) {
                highlight.highlight(shape, PickerPart.ShapePart.BODY);
                return true;
            }

            @Override
            public boolean acceptSplice(WiresShape shape,
                                        double[] candidateLocation,
                                        WiresConnector connector,
                                        List<double[]> firstHalfPoints,
                                        List<double[]> secondHalfPoints,
                                        WiresContainer parent) {
                highlight.restore();
                return true;
            }

            @Override
            public void ensureUnHighLight() {
                highlight.restore();
            }
        });

        // Rectangle - Black
        rectangleBlackShape = new WiresShape(new MultiPath().rect(0, 0, 650, 450)
                                                     .setStrokeColor("#000000")
                                                     .setFillColor("#E3E0DA"))
                .setDraggable(true)
                .setLocation(new Point2D(30, 50));
        rectangleBlackShape.getGroup().setUserData(LABEL_PARENT);
        wiresManager.register(rectangleBlackShape);
        wiresManager.getMagnetManager().createMagnets(rectangleBlackShape);

        // Rectangle - Red
        rectangleRedShape = new WiresShape(new MultiPath().rect(0, 0, 100, 100)
                                                   .setStrokeColor("#FF0000")
                                                   .setFillColor("#FF0000"))
                .setDraggable(true)
                .setLocation(new Point2D(700, 50));
        rectangleRedShape.getGroup().setUserData(LABEL_CHILD);
        wiresManager.register(rectangleRedShape);
        wiresManager.getMagnetManager().createMagnets(rectangleRedShape);

        // Rectangle - Green
        rectangleGreenShape = new WiresShape(new MultiPath().rect(0, 0, 100, 100)
                                                     .setStrokeColor("#AFE1AF")
                                                     .setFillColor("#AFE1AF"))
                .setDraggable(true)
                .setLocation(new Point2D(700, 300));
        rectangleGreenShape.getGroup().setUserData(LABEL_CHILD);
        wiresManager.register(rectangleGreenShape);
        wiresManager.getMagnetManager().createMagnets(rectangleGreenShape);

        // Circle - Red
        circleRedShape = new WiresShape(new MultiPath().circle(50)
                                                .setStrokeColor("#FF0000")
                                                .setFillColor("#FF0000"))
                .setDraggable(true)
                .setLocation(new Point2D(1200, 50));
        circleRedShape.getGroup().setUserData(LABEL_CHILD);
        wiresManager.register(circleRedShape);
        wiresManager.getMagnetManager().createMagnets(circleRedShape);

        // Circle - Green
        circleGreenShape = new WiresShape(new MultiPath().circle(50)
                                                  .setStrokeColor("#AFE1AF")
                                                  .setFillColor("#AFE1AF"))
                .setDraggable(true)
                .setLocation(new Point2D(1200, 300));
        circleGreenShape.getGroup().setUserData(LABEL_CHILD);
        wiresManager.register(circleGreenShape);
        wiresManager.getMagnetManager().createMagnets(circleGreenShape);

        // Rectangle - Blue
        rectangleYellowShape = new WiresShape(new MultiPath().rect(0, 0, 100, 100)
                                                      .setStrokeColor("#FFFF00")
                                                      .setFillColor("#FFFF00"))
                .setDraggable(true)
                .setLocation(new Point2D(650, 600));
        rectangleYellowShape.getGroup().setUserData(LABEL_CHILD);
        wiresManager.register(rectangleYellowShape);
        wiresManager.getMagnetManager().createMagnets(rectangleYellowShape);

        // Connection
        connect(rectangleRedShape.getMagnets(),
                3,
                circleRedShape.getMagnets(),
                7,
                wiresManager,
                false);

        connect(rectangleGreenShape.getMagnets(),
                3,
                circleGreenShape.getMagnets(),
                7,
                wiresManager,
                false);
    }

    private static final IContainmentAcceptor CONTAINMENT_ACCEPTOR = new IContainmentAcceptor() {
        @Override
        public boolean containmentAllowed(WiresContainer parent, WiresShape[] children) {
            return test(parent, children);
        }

        @Override
        public boolean acceptContainment(WiresContainer parent, WiresShape[] children) {
            return test(parent, children);
        }
    };

    private static final IDockingAcceptor DOCKING_ACCEPTOR = new IDockingAcceptor() {
        @Override
        public boolean dockingAllowed(WiresContainer parent, WiresShape child) {
            return test(parent, child);
        }

        @Override
        public boolean acceptDocking(WiresContainer parent, WiresShape child) {
            return test(parent, child);
        }

        @Override
        public int getHotspotSize() {
            return 25;
        }
    };

    private static boolean test(WiresContainer parent, WiresShape... children) {
        if (null == parent || null == parent.getGroup()) {
            return true;
        }
        if (LABEL_PARENT.equals(parent.getGroup().getUserData())) {
            for (WiresShape child : children) {
                Object data = child.getGroup().getUserData();
                if (!LABEL_CHILD.equals(data)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}