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
import com.ait.lienzo.client.core.shape.wires.MagnetManager;

/**
 * Changes the style of a {@link MagnetManager.Magnets} shape, according to a given {@link ShapeState}.
 */
public class MagnetDecorator implements IShapeDecorator<Shape<?>> {

    @Override
    public Shape decorate(Shape shape, ShapeState state) {
        switch (state) {
            case VALID:
            case INVALID:
            case NONE:
                shape.setFillColor(PointHandleDecorator.MAIN_COLOR)
                        .setFillAlpha(0.8)
                        .setStrokeAlpha(0)
                        .moveToTop();
        }
        return shape;
    }
}