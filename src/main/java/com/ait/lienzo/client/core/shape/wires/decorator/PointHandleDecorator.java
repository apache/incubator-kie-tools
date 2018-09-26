/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package com.ait.lienzo.client.core.shape.wires.decorator;

import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.shared.core.types.ColorName;

/**
 * Changes the style of connector point handles {@link WiresConnector#getPointHandles()} shapes, according to a given {@link ShapeState}.
 */
public class PointHandleDecorator implements IShapeDecorator<Shape<?>> {

    public static final String MAIN_COLOR = ColorName.DARKRED.getHexColor();

    @Override
    public Shape decorate(Shape shape, ShapeState state) {
        switch (state) {
            case NONE:
            case VALID:
                shape.moveToTop()
                        .setFillColor(MAIN_COLOR)
                        .setFillAlpha(0.8)
                        .setStrokeAlpha(0);
                break;
            case INVALID:
                shape.moveToTop()
                        .setFillColor(ColorName.GREEN);
                break;
        }
        return shape;
    }
}