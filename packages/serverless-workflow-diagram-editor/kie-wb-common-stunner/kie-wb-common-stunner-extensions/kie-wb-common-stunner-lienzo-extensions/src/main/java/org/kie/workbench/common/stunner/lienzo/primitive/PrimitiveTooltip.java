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


package org.kie.workbench.common.stunner.lienzo.primitive;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.Triangle;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import org.gwtproject.timer.client.Timer;

// TODO: Refactor/replace by the new tooltip implementation (org.kie.workbench.common.stunner.lienzo.toolbox.items.tooltip.Tooltip).
public class PrimitiveTooltip extends PrimitivePopup {

    private static final double PADDING = 10d;
    private static final double TRIANGLE_SIZE = 10d;
    private static final double ALPHA = 1d;
    private static final String BG_COLOR = "#8c8c8c";
    private static final String TEXT_FAMILY = "Verdana";
    private static final String TEXT_COLOR = "#FFFFFF";
    private static final double TEXT_SIZE = 12d;
    private static final double TEXT_WIDTH = 1d;
    private static final int HIDE_TIMEOUT = 3500;

    /**
     * This internal timer ensures that if any error on its usage occurs,
     * it will get removed from the canvas at some point.
     * Use the method <code>setHideTimeout</code> to change the default timeout value.
     */
    private int hideTimeout = HIDE_TIMEOUT;
    private final Timer hideTimer = new Timer() {
        @Override
        public void run() {
            PrimitiveTooltip.this.hide();
        }
    };

    public enum Direction {
        NORTH,
        WEST;
    }

    public PrimitiveTooltip() {
        setzIndex(100);
    }

    public void setHideTimeout(final int hideTimeout) {
        this.hideTimeout = hideTimeout;
    }

    public PrimitiveTooltip show(final String text,
                                 final Point2D location,
                                 final Direction direction) {
        return this.show(text,
                         location,
                         0,
                         0,
                         direction);
    }

    public PrimitiveTooltip show(final String text,
                                 final Point2D location,
                                 final double width,
                                 final double height,
                                 final Direction direction) {
        clearTimers();
        final Text descText = new Text(text)
                .setFontSize(TEXT_SIZE)
                .setFontStyle("")
                .setFontFamily(TEXT_FAMILY)
                .setStrokeWidth(TEXT_WIDTH)
                .setStrokeColor(TEXT_COLOR)
                .setStrokeAlpha(1);
        final BoundingBox descTextBB = descText.getBoundingBox();
        final double descTextBbW = descTextBB.getWidth();
        final double descTextBbH = descTextBB.getHeight();
        final double dw = (descTextBbW > width ? (descTextBbW + PADDING) : (width + PADDING));
        final double dh = height + descTextBbH + PADDING;
        final IPrimitive<?> decorator = buildDecorator(dw,
                                                       dh,
                                                       direction);
        final double w = dw + (isWest(direction) ? TRIANGLE_SIZE * 2 : 0);
        final double h = dh + (isNorth(direction) ? TRIANGLE_SIZE * 2 : 0);
        final Group g = new Group();
        g.add(decorator);
        g.add(descText);
        super.show(g,
                   w,
                   h,
                   location.getX(),
                   location.getY());
        double _x = (w / 2) + (isWest(direction) ? PADDING / 2 : 0);
        double _y = PADDING / 2 + (isNorth(direction) ? TRIANGLE_SIZE : 0);
        descText.setX(_x - (descTextBbW / 2));
        descText.setY(_y + descTextBbH);
        // Ensure text is on top.
        descText.moveToTop();
        canvasLayer.draw();
        startTimers();
        return this;
    }

    @Override
    public PrimitivePopup hide() {
        clearTimers();
        return super.hide();
    }

    @Override
    public PrimitivePopup remove() {
        clearTimers();
        return super.remove();
    }

    private IPrimitive<?> buildDecorator(final double width,
                                         final double height,
                                         final Direction direction) {
        final boolean isWest = isWest(direction);
        final boolean isNorth = isNorth(direction);
        final double h2 = height / 2;
        final double w2 = width / 2;
        final double s2 = TRIANGLE_SIZE / 2;
        final Point2D a = isWest ? new Point2D(0,
                                               h2) : new Point2D(10,
                                                                 0);
        final Point2D b = isWest ? new Point2D(TRIANGLE_SIZE,
                                               h2 + s2) : new Point2D(10 + s2,
                                                                      TRIANGLE_SIZE);
        final Point2D c = isWest ? new Point2D(TRIANGLE_SIZE,
                                               h2 - s2) : new Point2D(10 - s2,
                                                                      TRIANGLE_SIZE);
        final Triangle triangle = new Triangle(a,
                                               b,
                                               c)
                .setFillColor(BG_COLOR)
                .setFillAlpha(ALPHA)
                .setStrokeWidth(0);
        final Rectangle rectangle =
                new Rectangle(
                        width + (isWest ? TRIANGLE_SIZE : 0),
                        height + (isNorth ? TRIANGLE_SIZE : 0))
                        .setX(isWest ? TRIANGLE_SIZE : 0)
                        .setY(isWest ? 0 : TRIANGLE_SIZE)
                        .setCornerRadius(10)
                        .setFillColor(BG_COLOR)
                        .setFillAlpha(ALPHA)
                        .setStrokeAlpha(0)
                        .setCornerRadius(5);
        final Group decorator = new Group();
        decorator.add(rectangle);
        decorator.add(triangle);
        return decorator;
    }

    private boolean isWest(final Direction direction) {
        return Direction.WEST.equals(direction);
    }

    private boolean isNorth(final Direction direction) {
        return Direction.NORTH.equals(direction);
    }

    private void startTimers() {
        this.hideTimer.schedule(hideTimeout);
    }

    private void clearTimers() {
        if (null != this.hideTimer) {
            if (this.hideTimer.isRunning()) {
                this.hideTimer.cancel();
            }
        }
    }
}
