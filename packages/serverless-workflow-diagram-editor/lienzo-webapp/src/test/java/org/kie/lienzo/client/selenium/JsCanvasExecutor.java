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

import org.openqa.selenium.JavascriptExecutor;

public class JsCanvasExecutor {

    final JavascriptExecutor executor;
    static final String RETURN = "return ";
    static final String JS_CANVAS = "window.jsCanvas";
    static final String JS_CANVAS_EVENTS = JS_CANVAS + ".events()";

    public JsCanvasExecutor(JavascriptExecutor executor) {
        this.executor = executor;
    }

    public JsCanvasExecutor doubleClickAt(double x, double y) {
        executor.executeScript(JS_CANVAS_EVENTS + ".doubleClickAt(arguments[0], arguments[1])",
                               x, y);
        return this;
    }

    public JsCanvasExecutor clickAt(double x, double y) {
        executor.executeScript(JS_CANVAS_EVENTS + ".clickAt(arguments[0], arguments[1])",
                               x, y);
        return this;
    }

    public JsCanvasExecutor startDrag(double sx, double sy, double tx, double ty, int timeout) {
        executor.executeAsyncScript(JS_CANVAS_EVENTS + ".startDrag(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5])",
                                    sx, sy,
                                    ty, ty);
        return this;
    }

    public JsCanvasExecutor sleep() {
        sleep(200);
        return this;
    }

    public JsCanvasWiresShapeExecutor forWiresShape(String id) {
        return new JsCanvasWiresShapeExecutor(this, id);
    }

    public JsCanvasShapeExecutor forShape(String id) {
        return new JsCanvasShapeExecutor(this, id);
    }

    Object executeScript(String script, Object... args) {
        return executor.executeScript(script, args);
    }

    Object executeAsyncScript(String script, Object... args) {
        return executor.executeAsyncScript(script, args);
    }

    // TODO: Refactor this by using selenium until conditions
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log(e.getMessage());
        }
    }

    public static void log(String s) {
        System.err.println(s);
    }
}
