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

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.tools.client.event.EventType;
import com.ait.lienzo.tools.client.event.MouseEventUtil;
import elemental2.dom.DomGlobal;
import elemental2.dom.MouseEvent;
import elemental2.dom.MouseEventInit;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsType;

@JsType
public class JsCanvasEvents {

    private JsCanvas canvas;

    public JsCanvasEvents(JsCanvas canvas) {
        this.canvas = canvas;
    }

    public void doubleClick(IPrimitive<?> shape) {
        Point2D location = shape.getComputedLocation();
        double x = location.getX();
        double y = location.getY();
        doubleClickAt(x, y);
    }

    public void click(IPrimitive<?> shape) {
        Point2D location = shape.getComputedLocation();
        double x = location.getX();
        double y = location.getY();
        clickAt(x, y);
    }

    public void over(IPrimitive<?> shape) {
        Point2D location = shape.getComputedLocation();
        double x = location.getX();
        double y = location.getY();
        mouseOver(x, y);
    }

    public void out(IPrimitive<?> shape) {
        Point2D location = shape.getComputedLocation();
        double x = location.getX();
        double y = location.getY();
        mouseOut(x, y);
    }

    public void clickAt(double x, double y) {
        dispatchEvent(EventType.CLICKED.getType(), x, y);
    }

    public void doubleClickAt(double x, double y) {
        dispatchEvent(EventType.DOUBLE_CLICKED.getType(), x, y);
    }

    public void mouseOver(double x, double y) {
        dispatchEvent(EventType.MOUSE_OVER.getType(), x, y);
    }

    public void mouseOut(double x, double y) {
        dispatchEvent(EventType.MOUSE_OUT.getType(), x, y);
    }

    public void mouseDown(double x, double y) {
        dispatchEvent(EventType.MOUSE_DOWN.getType(), x, y);
    }

    public void mouseMove(double x, double y) {
        dispatchEvent(EventType.MOUSE_MOVE.getType(), x, y);
    }

    public void mouseUp(double x, double y) {
        dispatchEvent(EventType.MOUSE_UP.getType(), x, y);
    }

    public void move(IPrimitive<?> shape, double tx, double ty) {
        Point2D location = shape.getComputedLocation();
        double x = location.getX();
        double y = location.getY();
        mouseDown(x, y);
        mouseMove(x, y);
        mouseMove(tx, ty);
        mouseUp(tx, ty);
    }

    public void drag(IPrimitive<?> shape, double tx, double ty, DragCallbackFn callback) {
        Point2D location = shape.getComputedLocation();
        double x = location.getX();
        double y = location.getY();
        startDrag(x, y, tx, ty, 5, callback);
    }

    // when dragging connections, something tricky happens on WiresConnectorControlPointBuilder, that prevents dragging CPs automatically to work properly
    public void dragControlPoint(double sx, double sy, double tx, double ty, float steps, int timeout, DragCallbackFn callback) {
        mouseMove(sx, sy);
        mouseDown(sx, sy);
        double step = 1 / steps;
        doDrag(sx, sy, tx, ty, step, step, timeout, callback);
    }

    public void startDrag(double sx, double sy, double tx, double ty, int timeout, DragCallbackFn callback) {
        mouseDown(sx, sy);
        mouseMove(sx, sy);
        float steps = 100;
        double step = 1 / steps;
        doDrag(sx, sy, tx, ty, step, step, timeout, callback);
    }

    private void doDrag(final double sx,
                        final double sy,
                        final double tx,
                        final double ty,
                        final double actual,
                        final double step,
                        final int timeout,
                        final DragCallbackFn callback) {
        DomGlobal.setTimeout(new DomGlobal.SetTimeoutCallbackFn() {
            @Override
            public void onInvoke(Object... p0) {
                double dx = (tx - sx) * actual;
                double dy = (ty - sy) * actual;
                mouseMove(sx + dx, sy + dy);
                // DomGlobal.console.log("DRAG STEP TO [" + (sx + dx) + ", " + (sy + dy) + "]");
                if (actual < 1) {
                    doDrag(sx, sy, tx, ty, actual + step, step, timeout, callback);
                } else {
                    // DomGlobal.console.log("DRAG END TO [" + tx + ", " + ty + "]");
                    mouseUp(tx, ty);
                    callback.onInvoke();
                }
            }
        }, timeout);
    }

    public MouseEvent dispatchEvent(String type,
                                    double clientX,
                                    double clientY) {
        MouseEventInit mouseEventInit = MouseEventInit.create();
        mouseEventInit.setView(DomGlobal.window);
        int panelOffsetLeft = canvas.getPanelOffsetLeft();
        int panelOffsetTop = canvas.getPanelOffsetTop();
        long ix = Math.round(Math.ceil(clientX));
        long iy = Math.round(Math.ceil(clientY));
        long x = ix + panelOffsetLeft;
        long y = iy + panelOffsetTop;
        mouseEventInit.setClientX(x);
        mouseEventInit.setClientY(y);
        mouseEventInit.setScreenX(x);
        mouseEventInit.setScreenY(y);
        mouseEventInit.setButton(MouseEventUtil.BUTTON_LEFT);
        MouseEvent event = new MouseEvent(type, mouseEventInit);
        boolean cancelled = !canvas.panel.getElement().dispatchEvent(event);

        return event;
    }

    @JsFunction
    public interface DragCallbackFn {

        void onInvoke();
    }
}
