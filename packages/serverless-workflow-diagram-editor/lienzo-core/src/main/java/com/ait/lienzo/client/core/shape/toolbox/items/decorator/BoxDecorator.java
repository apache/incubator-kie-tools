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


package com.ait.lienzo.client.core.shape.toolbox.items.decorator;

import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.toolbox.items.AbstractDecoratorItem;
import com.ait.lienzo.client.core.types.BoundingBox;

public class BoxDecorator
        extends AbstractDecoratorItem<BoxDecorator> {

    private static final String DECORATOR_STROKE_COLOR = "#595959";
    private static final double DECORATOR_STROKE_WIDTH = 1;
    private static final double DECORATOR_CORNER_RADIUS = 3;

    private final MultiPath decorator;
    private double padding;
    private Consumer<MultiPath> showExecutor = path -> path.setAlpha(1);
    private Consumer<MultiPath> hideExecutor = path -> path.setAlpha(0);

    BoxDecorator() {
        this(rect(new MultiPath(),
                  1,
                  1,
                  DECORATOR_CORNER_RADIUS)
                     .setStrokeWidth(DECORATOR_STROKE_WIDTH)
                     .setStrokeColor(DECORATOR_STROKE_COLOR)
                     .setFillAlpha(0)
                     .setDraggable(false)
                     .setListening(false)
                     .setFillBoundsForSelection(false));
    }

    private BoxDecorator(final MultiPath decorator) {
        this.decorator = decorator;
        this.padding = 5;
    }

    public BoxDecorator setPadding(final double padding) {
        this.padding = padding;
        return this;
    }

    public BoxDecorator configure(final Consumer<MultiPath> consumer) {
        consumer.accept(decorator);
        return this;
    }

    @Override
    public BoxDecorator setBoundingBox(final BoundingBox boundingBox) {
        final double offset = -(padding / 2);
        rect(decorator,
             boundingBox.getWidth() + padding,
             boundingBox.getHeight() + padding,
             DECORATOR_CORNER_RADIUS)
                .setX(offset)
                .setY(offset);
        return this;
    }

    public BoxDecorator useShowExecutor(final Consumer<MultiPath> showExecutor) {
        this.showExecutor = showExecutor;
        return this;
    }

    public BoxDecorator useHideExecutor(final Consumer<MultiPath> hideExecutor) {
        this.hideExecutor = hideExecutor;
        return this;
    }

    @Override
    protected void doShow() {
        showExecutor.accept(decorator);
    }

    @Override
    protected void doHide() {
        hideExecutor.accept(decorator);
    }

    @Override
    public BoxDecorator copy() {
        return new BoxDecorator(MultiPath.clonePath(decorator))
                .setPadding(padding);
    }

    @Override
    public MultiPath asPrimitive() {
        return decorator;
    }

    Consumer<MultiPath> getShowExecutor() {
        return showExecutor;
    }

    Consumer<MultiPath> getHideExecutor() {
        return hideExecutor;
    }

    private static MultiPath rect(final MultiPath path,
                                  final double w,
                                  final double h,
                                  final double r) {
        if ((w > 0) && (h > 0)) {
            path.clear();
            if ((r > 0) && (r < (w / 2)) && (r < (h / 2))) {
                path.M(r,
                       0);
                path.L(w - r,
                       0);
                path.A(w,
                       0,
                       w,
                       r,
                       r);
                path.L(w,
                       h - r);
                path.A(w,
                       h,
                       w - r,
                       h,
                       r);
                path.L(r,
                       h);
                path.A(0,
                       h,
                       0,
                       h - r,
                       r);
                path.L(0,
                       r);
                path.A(0,
                       0,
                       r,
                       0,
                       r);
            } else {
                path.rect(0,
                          0,
                          w,
                          h);
            }
            path.Z();
        }
        return path;
    }
}
