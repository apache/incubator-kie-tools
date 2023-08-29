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


package org.kie.workbench.common.stunner.client.lienzo.canvas;

import java.util.function.BiFunction;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Rectangle;

public class LienzoCanvasDecoratorFactory {

    static final double DECORATOR_ALPHA = 0.8;
    static final double DECORATOR_AUTHORING_WIDTH = 1;
    static final double DECORATOR_PREVIEW_WIDTH = 2;
    static final String DECORATOR_AUTHORING_COLOR = "#d3d3d3";
    static final String DECORATOR_PREVIEW_COLOR = "#404040";

    public static BiFunction<Integer, Integer, IPrimitive<?>> AUTHORING = (width, height) -> axis(width,
                                                                                                  height,
                                                                                                  DECORATOR_ALPHA,
                                                                                                  DECORATOR_AUTHORING_WIDTH,
                                                                                                  DECORATOR_AUTHORING_COLOR);
    public static BiFunction<Integer, Integer, IPrimitive<?>> PREVIEW = (width, height) -> axis(width,
                                                                                                height,
                                                                                                DECORATOR_ALPHA,
                                                                                                DECORATOR_PREVIEW_WIDTH,
                                                                                                DECORATOR_PREVIEW_COLOR);

    public static IPrimitive<?> axis(final int width,
                                     final int height,
                                     final double dAlpha,
                                     final double dWidth,
                                     final String dColor) {
        final Line h = line(width, 0, dAlpha, dWidth, dColor);
        final Line v = line(0, height, dAlpha, dWidth, dColor);
        return group()
                .add(h)
                .add(v);
    }

    public static Line line(final int width,
                            final int height,
                            final double dAlpha,
                            final double dWidth,
                            final String dColor) {
        return new Line(0, 0, width, height)
                .setDraggable(false)
                .setListening(false)
                .setFillAlpha(0)
                .setDashArray(5)
                .setStrokeAlpha(dAlpha)
                .setStrokeWidth(dWidth)
                .setStrokeColor(dColor);
    }

    public static IPrimitive<?> rectangle(final int width,
                                          final int height,
                                          final double dAlpha,
                                          final double dWidth,
                                          final String dColor) {
        return new Rectangle(width, height)
                .setListening(false)
                .setDraggable(false)
                .setX(0)
                .setY(0)
                .setFillAlpha(0)
                .setStrokeAlpha(dAlpha)
                .setStrokeWidth(dWidth)
                .setStrokeColor(dColor);
    }

    private static Group group() {
        return new Group()
                .setDraggable(false)
                .setListening(false);
    }
}
