/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.svg.gen.model.impl;

import com.ait.lienzo.client.core.shape.Rectangle;
import org.kie.workbench.common.stunner.svg.gen.model.HasSize;

public class RectDefinition extends AbstractShapeDefinition<Rectangle> implements HasSize {

    private final double width;
    private final double height;
    private final double cornerRadius;

    public RectDefinition(final String id,
                          final double width,
                          final double height,
                          final double cornerRadius) {
        super(id);
        this.width = width;
        this.height = height;
        this.cornerRadius = cornerRadius;
    }

    @Override
    public Class<Rectangle> getViewType() {
        return Rectangle.class;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    public double getCornerRadius() {
        return cornerRadius;
    }

    @Override
    public String toString() {
        return this.getClass().getName()
                + " [x=" + getX() + "]"
                + " [y =" + getY() + "]"
                + " [width=" + width + "]"
                + " [height=" + height + "]"
                + " [cornerRadius=" + cornerRadius + "]";
    }
}
