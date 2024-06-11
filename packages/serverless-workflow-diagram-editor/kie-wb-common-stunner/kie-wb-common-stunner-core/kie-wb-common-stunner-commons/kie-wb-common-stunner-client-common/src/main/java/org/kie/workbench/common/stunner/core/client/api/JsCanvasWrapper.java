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

package org.kie.workbench.common.stunner.core.client.api;

import com.ait.lienzo.client.core.NativeContext2D;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.types.JsWiresShape;
import com.ait.lienzo.client.core.types.JsCanvas;
import com.ait.lienzo.client.core.types.JsCanvasAnimations;
import com.ait.lienzo.client.core.types.JsCanvasEvents;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import jsinterop.annotations.JsType;
import org.kie.j2cl.tools.processors.annotations.GWT3Export;

@JsType
@GWT3Export
public class JsCanvasWrapper {

    private JsCanvas wrapper = JsCanvas.getInstance();

    public Layer getLayer() {
        return wrapper.getLayer();
    }

    public HTMLCanvasElement getCanvas() {
        return wrapper.getCanvas();
    }

    public Viewport getViewport() {
        return wrapper.getViewport();
    }

    public void translate(double x, double y) {
        wrapper.translate(x, y);
    }

    public double getTranslateX() {
        return wrapper.getTranslateX();
    }

    public double getTranslateY() {
        return wrapper.getTranslateY();
    }

    public void scale(int factor) {
        wrapper.scale(factor);
    }

    public void scaleWithXY(double x, double y) {
        wrapper.scaleWithXY(x, y);
    }

    public double getScaleX() {
        return wrapper.getScaleX();
    }

    public double getScaleY() {
        return wrapper.getScaleY();
    }

    public NativeContext2D getNativeContext() {
        return wrapper.getNativeContext();
    }

    public JsCanvasEvents events() {
        return wrapper.events();
    }

    public JsCanvasAnimations animations() {
        return wrapper.animations();
    }

    public int getPanelOffsetLeft() {
        return wrapper.getPanelOffsetLeft();
    }

    public int getPanelOffsetTop() {
        return wrapper.getPanelOffsetTop();
    }

    public void add(IPrimitive<?> shape) {
        wrapper.add(shape);
    }

    public void draw() {
        wrapper.draw();
    }

    public IPrimitive<?> getShape(String id) {
        return wrapper.getShape(id);
    }

    public JsWiresShape getWiresShape(String id) {
        return wrapper.getWiresShape(id);
    }

    public String getBackgroundColor(String UUID) {
        return wrapper.getBackgroundColor(UUID);
    }

    public void setBackgroundColor(String UUID, String backgroundColor) {
        wrapper.setBackgroundColor(UUID, backgroundColor);
    }

    public String getBorderColor(String UUID) {
        return wrapper.getBorderColor(UUID);
    }

    public void setBorderColor(String UUID, String borderColor) {
        wrapper.setBorderColor(UUID, borderColor);
    }

    public NFastArrayList<Double> getLocation(String UUID) {
        return wrapper.getLocation(UUID);
    }

    public NFastArrayList<Double> getAbsoluteLocation(String UUID) {
        return wrapper.getAbsoluteLocation(UUID);
    }

    public NFastArrayList<Double> getDimensions(String UUID) {
        return wrapper.getDimensions(UUID);
    }

    public NFastArrayList<String> getShapeIds() {
        return wrapper.getShapeIds();
    }

    @Deprecated // Use getShapeIds() instaed.
    public NFastArrayList<String> getNodeIds() {
        DomGlobal.console.log("DEPRECATED: Use getShapeIds() instead."  + (wrapper == null));
        return wrapper.getNodeIds();
    }

    public void applyState(String UUID, String state) {
        wrapper.applyState(UUID, state);
    }

    public boolean isConnected(String uuid1, String uuid2) {
        return wrapper.isConnected(uuid1, uuid2);
    }

    @SuppressWarnings("all")
    public void center(String uuid) {
        wrapper.center(uuid);
    }

    public boolean isShapeVisible(String uuid) {
        return wrapper.isShapeVisible(uuid);
    }

    public void close() {
        wrapper.close();
    }

}
