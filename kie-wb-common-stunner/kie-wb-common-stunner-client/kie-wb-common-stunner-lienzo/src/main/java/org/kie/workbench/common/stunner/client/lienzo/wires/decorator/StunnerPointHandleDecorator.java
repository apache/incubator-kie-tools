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

package org.kie.workbench.common.stunner.client.lienzo.wires.decorator;

import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.decorator.PointHandleDecorator;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.shared.core.types.ColorName;

public class StunnerPointHandleDecorator extends PointHandleDecorator {

    public static final String MAIN_COLOR = "#0088CE";
    public static final String STROKE_COLOR = "#FFFFFF";
    protected static final Shadow SHADOW_SELECTED =
            new Shadow(MAIN_COLOR, 10, 0, 0);

    @Override
    public Shape decorate(Shape shape, ShapeState state) {
        switch (state) {
            case NONE:
            case VALID:
                shape.setFillColor(MAIN_COLOR)
                        .setShadow(SHADOW_SELECTED)
                        .setStrokeWidth(2)
                        .setStrokeColor(STROKE_COLOR)
                        .setFillAlpha(0.8)
                        .setStrokeAlpha(1);
                break;
            case INVALID:
                shape.setFillColor(ColorName.WHITE)
                        .setShadow(SHADOW_SELECTED)
                        .setStrokeWidth(2)
                        .setStrokeColor(MAIN_COLOR)
                        .setFillAlpha(1)
                        .setStrokeAlpha(1);
                break;
        }
        return shape;
    }
}
