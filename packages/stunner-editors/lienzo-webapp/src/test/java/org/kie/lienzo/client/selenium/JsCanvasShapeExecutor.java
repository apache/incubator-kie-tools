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


package org.kie.lienzo.client.selenium;

import static org.kie.lienzo.client.selenium.JsCanvasExecutor.JS_CANVAS;
import static org.kie.lienzo.client.selenium.JsCanvasExecutor.JS_CANVAS_EVENTS;
import static org.kie.lienzo.client.selenium.JsCanvasExecutor.RETURN;

public class JsCanvasShapeExecutor {

    static final String GET_SHAPE = JS_CANVAS + ".getShape(arguments[0])";

    protected final JsCanvasExecutor executor;
    protected final String id;

    public JsCanvasShapeExecutor(JsCanvasExecutor executor, String id) {
        this.executor = executor;
        this.id = id;
    }

    // ************* PROPERTIES ****************************

    public String getID() {
        return id;
    }

    public double getX() {
        return getX(id);
    }

    public double getY() {
        return getY(id);
    }

    public double getWidth() {
        return getWidth(id);
    }

    public double getHeight() {
        return getHeight(id);
    }

    public String getFillColor() {
        return getFillColor(id);
    }

    public String getStrokeColor() {
        return getStrokeColor(id);
    }

    // ************* EVENTS ****************************

    public void doubleClick() {
        doubleClick(id);
    }

    public void click() {
        click(id);
    }

    public void over() {
        over(id);
    }

    public void out() {
        out(id);
    }

    public void drag(double tx, double ty) {
        drag(id, tx, ty);
    }

    public void move(double tx, double ty) {
        move(id, tx, ty);
    }

    // ************* GENERIC ****************************

    private double getX(String id) {
        return getShapeDoubleProperty(id, "x");
    }

    private double getY(String id) {
        return getShapeDoubleProperty(id, "y");
    }

    private double getWidth(String id) {
        return getShapeDoubleProperty(id, "width");
    }

    private double getHeight(String id) {
        return getShapeDoubleProperty(id, "height");
    }

    private String getFillColor(String id) {
        return getShapeProperty(id, "fillColor");
    }

    private String getStrokeColor(String id) {
        return getShapeProperty(id, "strokeColor");
    }

    private void click(String id) {
        executor.executeScript(JS_CANVAS_EVENTS + ".click(" + GET_SHAPE + ")", id);
    }

    private void doubleClick(String id) {
        executor.executeScript(JS_CANVAS_EVENTS + ".doubleClick(" + GET_SHAPE + ")", id);
    }

    private void over(String id) {
        executor.executeScript(JS_CANVAS_EVENTS + ".over(" + GET_SHAPE + ")", id);
    }

    private void out(String id) {
        executor.executeScript(JS_CANVAS_EVENTS + ".out(" + GET_SHAPE + ")", id);
    }

    private void drag(String id, double tx, double ty) {
        executor.executeAsyncScript(JS_CANVAS_EVENTS + ".drag(" + GET_SHAPE + ", arguments[1], arguments[2], arguments[3])",
                                    id,
                                    tx, ty);
    }

    private void move(String id, double tx, double ty) {
        executor.executeScript(JS_CANVAS_EVENTS + ".move(" + GET_SHAPE + ", arguments[1], arguments[2])",
                               id,
                               tx, ty);
    }

    <T> T getShapeProperty(String id, String property) {
        return getProperty(GET_SHAPE, id, property);
    }

    double getShapeDoubleProperty(String id, String property) {
        return getDoubleProperty(GET_SHAPE, id, property);
    }

    @SuppressWarnings("all")
    <T> T getProperty(String method, String id, String property) {
        T value = (T) executor.executeScript(RETURN + method + "." + property, id);
        return value;
    }

    @SuppressWarnings("all")
    double getDoubleProperty(String method, String id, String property) {
        Object value = executor.executeScript(RETURN + method + "." + property, id);
        if (value instanceof Long) {
            return ((Long) value).doubleValue();
        }
        return (double) value;
    }
}
