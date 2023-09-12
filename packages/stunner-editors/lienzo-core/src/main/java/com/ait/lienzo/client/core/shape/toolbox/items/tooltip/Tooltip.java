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


package com.ait.lienzo.client.core.shape.toolbox.items.tooltip;

import java.util.function.Consumer;

import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;

public class Tooltip {

    private static final String PATH_FILL_COLOR = "#404040";
    private static final String PATH_STROKE_COLOR = "#000000";
    private static final String TEXT_COLOR = "#FFFFFF";
    private static final double TEXT_STROKE_WIDTH = 0;
    private static final double DURATION = 150;
    private final MultiPath path;
    private final Text text;
    private final Group group;
    private IAnimationHandle animationHandle;
    private Direction direction;
    private double padding;

    public Tooltip() {
        this.path =
                new MultiPath()
                        .setFillColor(PATH_FILL_COLOR)
                        .setStrokeColor(PATH_STROKE_COLOR);
        this.text =
                new Text("")
                        .setFillColor(TEXT_COLOR).
                        setStrokeWidth(TEXT_STROKE_WIDTH);
        this.group =
                new Group()
                        .setAlpha(0)
                        .add(path)
                        .add(text)
                        .setListening(false);
        this.padding = 5;
        this.direction = Direction.EAST;
        this.animationHandle = null;
    }

    public Tooltip setLocation(final Point2D location) {
        this.group.setLocation(location);
        return checkRefresh();
    }

    public Tooltip setDirection(final Direction direction) {
        if (!isSupported(direction)) {
            throw new UnsupportedOperationException("Only NSEW directions are supported.");
        }
        this.direction = direction;
        return checkRefresh();
    }

    public Tooltip setPadding(final double value) {
        this.padding = value;
        return checkRefresh();
    }

    public Tooltip withText(final Consumer<Text> text) {
        text.accept(this.text);
        return checkRefresh();
    }

    public Tooltip show() {
        refresh();
        stopIfRunning();
        animationHandle = group
                .setScale(0.1,
                          1)
                .setAlpha(1)
                .animate(AnimationTweener.LINEAR,
                         AnimationProperties.toPropertyList(AnimationProperty.Properties.SCALE(1,
                                                                                               1)),
                         DURATION);
        return this;
    }

    private void stopIfRunning() {
        if (null != animationHandle
                && animationHandle.isRunning()) {
            animationHandle.stop();
            animationHandle = null;
        }
    }

    public Tooltip hide() {
        stopIfRunning();
        group.setAlpha(0);
        return this;
    }

    public boolean isVisible() {
        return group.getAlpha() > 0;
    }

    public void destroy() {
        stopIfRunning();
        path.removeFromParent();
        text.removeFromParent();
        group.destroy();
        direction = null;
    }

    public IPrimitive<?> asPrimitive() {
        return group;
    }

    private Tooltip checkRefresh() {
        if (isVisible()) {
            refresh();
        }
        return this;
    }

    private void refresh() {

        final boolean isH = isHorizontal(direction);

        // Dimensions for text.
        final BoundingBox textBB = text.getBoundingBox();
        final double tw = textBB.getWidth();
        final double th = textBB.getHeight();

        // Head dimensions.
        final double fontSize = text.getFontSize();
        final double hw = isH ? fontSize / 2 : fontSize;
        final double hl = !isH ? fontSize / 2 : fontSize;

        // Body dimensions.
        final double cbw = (isH ? hl : 0)
                + tw + (padding * 2);
        final double cbh = (!isH ? hl : 0) +
                th + (padding * 2);
        final double bw = isH ? cbw : cbh;
        final double bh = isH ? cbh : cbw;
        final double br = 5;

        // Some pre-calculations.
        final double hd = (bh - hw) / 2;
        final double y = -(bh / 2);

        path
                .clear()
                .M(hl + br,
                   y + 0)
                .L(bw - br,
                   y + 0)
                .A(bw,
                   y + 0,
                   bw,
                   y + br,
                   br)
                .L(bw,
                   y + (bh - br))
                .A(bw,
                   y + bh,
                   bw - br,
                   y + bh,
                   br)
                .L(hl + br,
                   y + bh)
                .A(hl,
                   y + bh,
                   hl,
                   y + (bh - br),
                   br)
                .L(hl,
                   y + (bh - hd))
                .L(0,
                   y + (bh / 2))
                .L(hl,
                   y + hd)
                .L(hl,
                   y + br)
                .A(hl,
                   y + 0,
                   hl + br,
                   y + 0,
                   br)
                .Z();

        // Direction.
        final double tpw = (bw - tw - (padding * 2)) > 0 ? padding : 0;
        final double tph = (bh - th - (padding * 2)) > 0 ? padding : 0;
        final Point2D textLoc = new Point2D(0, 0);
        switch (direction) {
            case WEST:
                path.setRotationDegrees(180);
                textLoc.setX(-hl - tpw - tw)
                        .setY((th / 2) - tph);
                break;
            case NORTH:
                path.setRotationDegrees(270);
                textLoc.setX(-tpw - (tw / 2))
                        .setY(-tph - hl);
                break;
            case SOUTH:
                path.setRotationDegrees(90);
                textLoc.setX(-tpw - (tw / 2))
                        .setY(hl + th + tph);
                break;
            default:
                path.setRotationDegrees(0);
                textLoc.setX(hl + tpw)
                        .setY((th / 2) - tph);
        }

        // Location.
        text.setLocation(textLoc);
        group.moveToTop();
    }

    private static boolean isHorizontal(final Direction direction) {
        return Direction.EAST.equals(direction) ||
                Direction.WEST.equals(direction);
    }

    private static boolean isVertical(final Direction direction) {
        return Direction.NORTH.equals(direction) ||
                Direction.SOUTH.equals(direction);
    }

    private static boolean isSupported(final Direction direction) {
        return isHorizontal(direction) ||
                isVertical(direction);
    }
}
