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

package com.ait.lienzo.client.core.types;

import java.util.HashSet;
import java.util.Set;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.NativeContext2D;
import com.ait.lienzo.client.core.shape.ContainerNode;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.types.JsWiresShape;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import elemental2.dom.HTMLCanvasElement;
import jsinterop.annotations.JsType;

@JsType
public class JsCanvas implements JsCanvasNodeLister {

    LienzoPanel panel;
    Layer layer;
    protected JSShapeStateApplier stateApplier;
    public static JsCanvasEvents events;
    public static JsCanvasAnimations animations;
    public static JsCanvasLogger logger;

    public JsCanvas(LienzoPanel panel, Layer layer, JSShapeStateApplier stateApplier) {
        this.panel = panel;
        this.layer = layer;
        this.stateApplier = stateApplier;
        this.events = null;
    }

    public Layer getLayer() {
        return layer;
    }

    public HTMLCanvasElement getCanvas() {
        HTMLCanvasElement canvasElement = getLayer().getCanvasElement();
        return canvasElement;
    }

    public Viewport getViewport() {
        return getLayer().getViewport();
    }

    public NativeContext2D getNativeContent() {
        Context2D context = getLayer().getContext();
        NativeContext2D nativeContext = context.getNativeContext();
        return nativeContext;
    }

    public JsCanvasEvents events() {
        if (null == events) {
            events = new JsCanvasEvents(this);
        }
        return events;
    }

    public JsCanvasAnimations animations() {
        if (null == animations) {
            animations = new JsCanvasAnimations();
        }
        return animations;
    }

    public JsCanvasLogger log() {
        if (null == logger) {
            logger = new JsCanvasLogger(this);
        }
        return logger;
    }

    public int getPanelOffsetLeft() {
        int result = panel.getElement().offsetLeft;
        return result;
    }

    public int getPanelOffsetTop() {
        int result = panel.getElement().offsetTop;
        return result;
    }

    public void add(IPrimitive<?> shape) {
        getLayer().add(shape);
    }

    public void draw() {
        getLayer().draw();
    }

    public IPrimitive<?> getShape(String id) {
        return getShapeInContainer(id, getLayer());
    }

    @SuppressWarnings("all")
    private static IPrimitive<?> getShapeInContainer(String id, ContainerNode parent) {
        NFastArrayList<IPrimitive<?>> shapes = parent.getChildNodes();
        if (null != shapes) {
            for (IPrimitive<?> shape : shapes.asList()) {
                String shapeID = shape.getID();
                if (id.equals(shapeID)) {
                    return shape;
                }
                if (shape instanceof ContainerNode) {
                    IPrimitive<?> shape1 = getShapeInContainer(id, (ContainerNode) shape);
                    if (null != shape1) {
                        return shape1;
                    }
                }
            }
        }
        return null;
    }

    public WiresManager getWiresManager() {
        return WiresManager.get(getLayer());
    }

    public JsWiresShape getWiresShape(String id) {
        WiresShape[] shapes = getWiresManager().getShapes();
        for (WiresShape shape : shapes) {
            if (id.equals(shape.getID())) {
                final JsWiresShape jsWiresShape = new JsWiresShape(shape);
                jsWiresShape.linkLister(this);
                return jsWiresShape;
            }
        }
        return null;
    }

    public String getBackgroundColor(String UUID) {

        if (UUID == null || "".equals(UUID)) {
            return null;
        }
        JsWiresShape shape = getWiresShape(UUID);
        if (shape == null) {
            return null;
        }
        return shape.getBackgroundColor();
    }

    public void setBackgroundColor(String UUID, String backgroundColor) {

        if (UUID == null || "".equals(UUID) || backgroundColor == null || "".equals(backgroundColor)) {
            return;
        }

        JsWiresShape shape = getWiresShape(UUID);
        if (shape == null) {
            return;
        }
        shape.setBackgroundColor(backgroundColor);
    }

    public String getBorderColor(String UUID) {

        if (UUID == null || "".equals(UUID)) {
            return null;
        }

        JsWiresShape shape = getWiresShape(UUID);
        if (shape == null) {
            return null;
        }
        return shape.getBorderColor();
    }

    public void setBorderColor(String UUID, String borderColor) {

        if (UUID == null || "".equals(UUID) || borderColor == null || "".equals(borderColor)) {
            return;
        }

        JsWiresShape shape = getWiresShape(UUID);
        if (shape == null) {
            return;
        }
        shape.setBorderColor(borderColor);
    }

    public NFastArrayList<Double> getLocation(String UUID) {

        if (UUID == null || "".equals(UUID)) {
            return null;
        }

        JsWiresShape shape = getWiresShape(UUID);
        if (shape == null) {
            return null;
        }

        final Point2D location = shape.getLocationXY();
        NFastArrayList<Double> locationArray = new NFastArrayList<>();
        locationArray.add(location.getX());
        locationArray.add(location.getY());
        return locationArray;
    }

    public NFastArrayList<Double> getAbsoluteLocation(String UUID) {

        if (UUID == null || "".equals(UUID)) {
            return null;
        }

        JsWiresShape shape = getWiresShape(UUID);
        if (shape == null) {
            return null;
        }
        final Point2D location = shape.getAbsoluteLocation();
        NFastArrayList<Double> locationArray = new NFastArrayList<>();
        locationArray.add(location.getX());
        locationArray.add(location.getY());

        return locationArray;
    }

    public NFastArrayList<Double> getDimensions(String UUID) {

        if (UUID == null || "".equals(UUID)) {
            return null;
        }

        JsWiresShape shape = getWiresShape(UUID);
        if (shape == null) {
            return null;
        }
        final Point2D dimensions = shape.getBounds();
        NFastArrayList<Double> dimensionsArray = new NFastArrayList<>();
        dimensionsArray.add(dimensions.getX());
        dimensionsArray.add(dimensions.getY());
        return dimensionsArray;
    }

    public NFastArrayList<String> getNodeIds() {
        WiresShape[] shapes = getWiresManager().getShapes();
        NFastArrayList<String> ids = new NFastArrayList<>();
        for (int i = 0; i < shapes.length; i++) {
            WiresShape shape = shapes[i];
            ids.add(shape.getID());
        }
        return ids;
    }

    public void applyState(String UUID, String state) {
        if (UUID != null && state != null) {
            stateApplier.applyState(UUID, state);
        }
    }

    public void center(String UUID) {
        if (UUID != null) {
            centerNode(UUID);
        }
    }

    public void centerNode(String UUID) {
        NFastArrayList<Double> absoluteLocation = getAbsoluteLocation(UUID);

        if (absoluteLocation != null) {

            double width = layer.getViewport().getWidth();
            double height = layer.getViewport().getHeight();

            NFastArrayList<Double> dimensions = getDimensions(UUID);
            double nodeWidth = 0;
            double nodeHeight = 0;

            if (dimensions != null) {
                nodeWidth = dimensions.get(0);
                nodeHeight = dimensions.get(1);
            }

            double translatedX = -absoluteLocation.get(0) + (width / 2) - (nodeWidth / 2);
            double translatedY = -absoluteLocation.get(1) + (height / 2) - (nodeHeight / 2);

            if (translatedX <= 0 && translatedY <= 0) { // prevent moving (0,0) Dotten Line right/below
                layer.getViewport().getTransform().translate(-layer.getViewport().getTransform().getTranslateX(), -layer.getViewport().getTransform().getTranslateY());
                layer.getViewport().getTransform().translate(translatedX, translatedY);
                ((ScrollablePanel) panel).refresh();
            }
        }
    }

    @Override
    public Set<String> getNodeIdSet() {
        WiresShape[] shapes = getWiresManager().getShapes();
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < shapes.length; i++) {
            ids.add(shapes[i].getID());
        }
        return ids;
    }
}
