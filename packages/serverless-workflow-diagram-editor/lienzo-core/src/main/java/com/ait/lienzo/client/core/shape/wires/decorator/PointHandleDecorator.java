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


package com.ait.lienzo.client.core.shape.wires.decorator;

import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;

/**
 * Changes the style of connector point handles {@link WiresConnector#getPointHandles()} shapes, according to a given {@link ShapeState}.
 */
public class PointHandleDecorator implements IShapeDecorator<Shape<?>> {

    public static final String MAIN_COLOR = "#0088CE";
    public static final String STROKE_COLOR = "#FFFFFF";
    public static final double VALID_FILL_ALPHA = 0.8;
    public static final int VALID_STROKE_WIDTH = 2;
    public static final double VALID_STROKE_ALPHA = 1;
    public static final double INVALID_FILL_ALPHA = 1;
    public static final int INVALID_STROKE_WIDTH = 2;
    public static final double INVALID_STROKE_ALPHA = 1;


    @Override
    public Shape decorate(Shape shape, ShapeState state) {
        return decorateShape(shape, state);
    }

    public static <T extends Shape> T decorateShape(T shape, ShapeState state) {
        switch (state) {
            case NONE:
            case VALID:
                shape.moveToTop()
                        .setFillColor(MAIN_COLOR)
                        .setStrokeWidth(VALID_STROKE_WIDTH)
                        .setStrokeColor(STROKE_COLOR)
                        .setFillAlpha(VALID_FILL_ALPHA)
                        .setStrokeAlpha(VALID_STROKE_ALPHA);
                break;
            case INVALID:
                // Reversed Main and Stroke colors
                shape.moveToTop()
                        .setFillColor(STROKE_COLOR)
                        .setStrokeColor(MAIN_COLOR)
                        .setStrokeWidth(INVALID_STROKE_WIDTH)
                        .setFillAlpha(INVALID_FILL_ALPHA)
                        .setStrokeAlpha(INVALID_STROKE_ALPHA);
                break;
        }
        return shape;
    }
}