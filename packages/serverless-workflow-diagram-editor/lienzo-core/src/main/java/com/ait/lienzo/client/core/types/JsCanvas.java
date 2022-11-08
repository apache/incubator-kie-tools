/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package com.ait.lienzo.client.core.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.NativeContext2D;
import com.ait.lienzo.client.core.shape.ContainerNode;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.types.JsWiresConnection;
import com.ait.lienzo.client.core.shape.wires.types.JsWiresMagnet;
import com.ait.lienzo.client.core.shape.wires.types.JsWiresShape;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import elemental2.dom.HTMLCanvasElement;
import jsinterop.annotations.JsType;

@JsType
public class JsCanvas implements JsCanvasNodeLister {

    public static JsCanvasEvents events;
    public static JsCanvasAnimations animations;
    public static JsCanvasLogger logger;
    protected JSShapeStateApplier stateApplier;
    LienzoPanel panel;
    Layer layer;

    public JsCanvas(LienzoPanel panel, Layer layer, JSShapeStateApplier stateApplier) {
        this.panel = panel;
        this.layer = layer;
        this.stateApplier = stateApplier;
        this.events = null;
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

    private Map<String, String> getConnections(String uuid) {
        JsWiresShape ws1 = getWiresShape(uuid);

        Map<String, String> headAndTailsMap = new HashMap<>();
        for (int i = 0; i < ws1.getMagnetsSize(); i++) {
            JsWiresMagnet magnet = ws1.getMagnet(i);
            for (int ii = 0; ii < magnet.getConnectionSize(); ii++) {
                JsWiresConnection connection = magnet.getConnection(ii);
                headAndTailsMap.put(connection.getConnector().getTailConnection().getControl().uuid(), connection.getConnector().getHeadConnection().getControl().uuid());
            }
        }

        return headAndTailsMap;
    }

    public boolean isConnected(String uuid1, String uuid2) {
        final Map<String, String> connections1 = getConnections(uuid1);
        final Map<String, String> connections2 = getConnections(uuid2);

        for (Map.Entry<String, String> entry : connections1.entrySet()) {
            String tail = connections2.get(entry.getKey());
            if (tail != null && tail.equals(entry.getValue())) {
                return true;
            }
        }

        return false;
    }

    public void centerNode(String uuid) {
        if (!isShapeVisible(uuid)) {
            final double[] center = calculateCenter(uuid);
            layer.getViewport().getTransform().translate(center[0], center[1]);
            ((ScrollablePanel) panel).refresh();
        }
    }

    @SuppressWarnings("all")
    public double[] calculateCenter(String UUID) {
        NFastArrayList<Double> absoluteLocation = getAbsoluteLocation(UUID);
        final Bounds visibleBounds = ((ScrollablePanel) panel).getVisibleBounds();
        final double visibleAreaX = visibleBounds.getX();
        final double visibleAreaY = visibleBounds.getY();
        final double areaMaxX = visibleAreaX + visibleBounds.getWidth();
        final double areaMaxY = visibleAreaY + visibleBounds.getHeight();

        if (absoluteLocation != null) {
            NFastArrayList<Double> dimensions = getDimensions(UUID);
            double nodeWidth = 0;
            double nodeHeight = 0;

            if (dimensions != null) {
                nodeWidth = dimensions.get(0);
                nodeHeight = dimensions.get(1);
            }

            double adjustX = visibleAreaX / 2;
            double adjustY = visibleAreaY / 2;

            // Calculate absolute center
            double translatedX = (-absoluteLocation.get(0) + (areaMaxX / 2) - (nodeWidth / 2)) + adjustX;
            double translatedY = (-absoluteLocation.get(1) + (areaMaxY / 2) - (nodeHeight / 2)) + adjustY;

            // Do not exceed min bounds
            translatedX = Math.min(translatedX, visibleAreaX);
            translatedY = Math.min(translatedY, visibleAreaY);

            return new double[]{translatedX, translatedY};
        }
        return null;
    }

    public boolean isShapeVisible(String uuid) {
        final Bounds visibleBounds = ((ScrollablePanel) panel).getVisibleBounds();
        final JsWiresShape wiresShape = getWiresShape(uuid);
        final BoundingBox shapeBounds = wiresShape.getBoundingBox();
        final double shapeX = wiresShape.getLocation().getX();
        final double shapeY = wiresShape.getLocation().getY();
        final double shapeMaxX = shapeX + shapeBounds.getWidth();
        final double shapeMaxY = shapeY + shapeBounds.getHeight();
        final double visibleAreaX = visibleBounds.getX();
        final double visibleAreaY = visibleBounds.getY();
        final double areaMaxX = visibleAreaX + visibleBounds.getWidth();
        final double areaMaxY = visibleAreaY + visibleBounds.getHeight();

        if ((shapeX >= visibleAreaX && shapeMaxX <= areaMaxX) &&
                (shapeY >= visibleAreaY && shapeMaxY <= areaMaxY)) {
            return true;
        }
        return false;
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

    public void close() {
        if (null != layer) {
            layer.clear();
            layer = null;
        }
        panel = null;
        stateApplier = null;
    }
}
