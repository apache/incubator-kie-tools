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

package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires;

import java.util.function.Predicate;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayoutContainer;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;

public class WiresScalableContainer extends WiresLayoutContainer {

    private final NFastArrayList<IPrimitive<?>> scalableChildren = new NFastArrayList<>();
    private Group transformableContainer;
    private Point2D scaleRatio;
    private boolean scaleRequired;

    public WiresScalableContainer() {
        this(null);
    }

    WiresScalableContainer(final Group transformableContainer) {
        this.transformableContainer = transformableContainer;
    }

    public WiresScalableContainer setScaleRatio(final Point2D scaleRatio) {
        this.scaleRatio = scaleRatio;
        return this;
    }

    private boolean isScalable(final IPrimitive<?> child) {
        return hasWidthHeight().test(child);
    }

    private void scaleChildTo(final IPrimitive<?> child,
                              final double x,
                              final double y,
                              final double width,
                              final double height) {
        if (hasWidthHeight().test(child)) {
            if (width > 0) {
                child.getAttributes().setWidth(width);
            }
            if (height > 0) {
                child.getAttributes().setHeight(height);
            }
        }
        child.getAttributes().setX(x);
        child.getAttributes().setY(y);
    }

    public WiresScalableContainer addScalable(final IPrimitive<?> child) {
        if (isScalable(child)) {
            scalableChildren.add(child);
            add(child);
        } else {
            if (null == transformableContainer) {
                transformableContainer = new Group();
                addChild(transformableContainer);
            }
            transformableContainer.add(child);
            scaleRatio = null;
        }
        scaleRequired = true;
        return this;
    }

    @Override
    public WiresLayoutContainer setSize(final double width,
                                        final double height) {
        if (getWidth() != width || getHeight() != height) {
            scaleRatio = null;
            scaleRequired = true;
        }
        super.setSize(width, height);
        return this;
    }

    @Override
    public LayoutContainer execute() {
        scaleTo(getOffset().getX(),
                getOffset().getY(),
                getWidth(),
                getHeight());
        return super.execute();
    }

    public void scaleTo(final double x,
                        final double y,
                        final double width,
                        final double height) {
        if (scaleRequired) {
            if (null != transformableContainer) {
                transformableContainer
                        .setX(x)
                        .setY(y);
                if (null == scaleRatio) {
                    // GWT.log("CALCULATING SCALE RATIO...");
                    final BoundingBox bb = transformableContainer.getBoundingBox();
                    final double sx = width / bb.getWidth();
                    final double sy = height / bb.getHeight();
                    scaleRatio = new Point2D(sx, sy);
                    // GWT.log("SCALE RATIO VALUES [" + scaleRatio + "]");
                    transformableContainer.setScale(scaleRatio);
                }
            }
            for (int i = 0; i < scalableChildren.size(); i++) {
                final IPrimitive<?> child = scalableChildren.get(i);
                scaleChildTo(child,
                             x,
                             y,
                             width,
                             height);
            }
            scaleRequired = false;
        }
    }

    private double getWidth(final IPrimitive<?> prim) {
        return prim.getAttributes().getDouble(Attribute.WIDTH.getProperty());
    }

    private double getHeight(final IPrimitive<?> prim) {
        return prim.getAttributes().getDouble(Attribute.HEIGHT.getProperty());
    }

    private Predicate<IPrimitive> hasWidthHeight() {
        return primitive -> getWidth(primitive) > 0 && getHeight(primitive) > 0;
    }
}
