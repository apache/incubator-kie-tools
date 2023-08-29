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
import static org.kie.lienzo.client.selenium.JsCanvasExecutor.RETURN;

public class JsCanvasWiresShapeExecutor extends JsCanvasShapeExecutor {

    static final String GET_WIRES_SHAPE = JS_CANVAS + ".getWiresShape(arguments[0])";

    public JsCanvasWiresShapeExecutor(JsCanvasExecutor executor, String id) {
        super(executor, id);
    }

    public double getComputedX() {
        return getComputedX(id);
    }

    public double getComputedY() {
        return getComputedY(id);
    }

    public double getMagnetX(int magnet) {
        return getMagnetX(id, magnet);
    }

    public double getMagnetY(int magnet) {
        return getMagnetY(id, magnet);
    }

    public double getConnectionX(int magnet, int connection) {
        return getConnectionX(id, magnet, connection);
    }

    public double getConnectionY(int magnet, int connection) {
        return getConnectionY(id, magnet, connection);
    }

    public JsCanvasWiresShapeExecutor getParent() {
        String parentId = (String) executor.executeScript(RETURN + GET_WIRES_SHAPE + ".getParentID()", id);
        if (null != parentId) {
            return new JsCanvasWiresShapeExecutor(executor, parentId);
        }
        return null;
    }

    private double getComputedX(String id) {
        return getWiresShapeDoubleProperty(id, "getComputedLocation().x");
    }

    private double getComputedY(String id) {
        return getWiresShapeDoubleProperty(id, "getComputedLocation().y");
    }

    private double getMagnetX(String id, int magnet) {
        return getWiresShapeDoubleProperty(id, "getMagnet(" + magnet + ").getControl().x");
    }

    private double getMagnetY(String id, int magnet) {
        return getWiresShapeDoubleProperty(id, "getMagnet(" + magnet + ").getControl().y");
    }

    private double getConnectionX(String id, int magnet, int connection) {
        return getWiresShapeDoubleProperty(id, "getMagnet(" + magnet + ").getConnection(" + connection + ").getControl().x");
    }

    private double getConnectionY(String id, int magnet, int connection) {
        return getWiresShapeDoubleProperty(id, "getMagnet(" + magnet + ").getConnection(" + connection + ").getControl().y");
    }

    <T> T getWiresShapeProperty(String id, String property) {
        return getProperty(GET_WIRES_SHAPE, id, property);
    }

    double getWiresShapeDoubleProperty(String id, String property) {
        return getDoubleProperty(GET_WIRES_SHAPE, id, property);
    }
}
