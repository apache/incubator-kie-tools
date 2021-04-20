/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.core.grids.client.model.impl;

import org.uberfire.ext.wires.core.grids.client.model.Bounds;

/**
 * Base implementation of {@link Bounds}
 */
public class BaseBounds implements Bounds {

    private double x;
    private double y;
    private double width;
    private double height;

    public BaseBounds(final double x,
                      final double y,
                      final double width,
                      final double height) {
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public void setWidth(double width) {
        if (!(width >= 0)) {
            throw new IllegalStateException("Width must be positive");
        }
        this.width = width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public void setHeight(double height) {
        if (!(height >= 0)) {
            throw new IllegalStateException("Height must be positive");
        }
        this.height = height;
    }
}
