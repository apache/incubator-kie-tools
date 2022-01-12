/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.lienzo;

import com.ait.lienzo.client.core.animation.AnimationCallback;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.user.client.Timer;

public class Decorator extends Group {

    public interface ItemCallback {

        void onShow(double x,
                    double y);

        void onHide();
    }

    private static final int TIMER_DELAY = 200;
    private static final double ANIMATION_DURATION = 200;
    private double padding = 5;
    Rectangle decorator;
    ItemCallback callback;

    public Decorator(final ItemCallback callback) {
        this.callback = callback;
    }

    private Timer timer = createTimer();

    protected Timer createTimer() {
        return new Timer() {
            @Override
            public void run() {
                hide();
            }
        };
    }

    protected void resetTimer(final Timer timer) {
        this.timer = timer;
    }

    public Decorator setPadding(final double padding) {
        this.padding = padding;
        return this;
    }

    public Decorator setItemCallback(final ItemCallback callback) {
        this.callback = callback;
        return this;
    }

    public IPrimitive<?> build(final IPrimitive<?> item,
                               final double width,
                               final double height) {
        decorator = createRectangle(width,
                                    height);
        this.add(decorator);
        this.add(item);
        decorator.setX(item.getX() - (padding / 4));
        decorator.setY(item.getY() - (padding / 4));
        decorator.addNodeMouseEnterHandler(nodeMouseEnterEvent -> show(nodeMouseEnterEvent.getX(),
                                                                       nodeMouseEnterEvent.getY()));
        decorator.addNodeMouseExitHandler(nodeMouseExitEvent -> hide());
        decorator.addNodeMouseMoveHandler(nodeMouseMoveEvent -> timer.cancel());
        item.setDraggable(false);
        decorator.setDraggable(false).moveToTop();
        return this;
    }

    public Rectangle createRectangle(final double width,
                                     final double height) {
        return new Rectangle(width + padding,
                             height + padding)
                .setCornerRadius(5)
                .setFillColor(ColorName.BLACK)
                .setFillAlpha(0.01)
                .setStrokeWidth(1)
                .setStrokeColor(ColorName.BLACK)
                .setStrokeAlpha(0);
    }

    public Decorator show(final double x,
                          final double y) {
        if (!timer.isRunning()) {
            decorator.animate(AnimationTweener.LINEAR,
                              AnimationProperties.toPropertyList(AnimationProperty.Properties.STROKE_ALPHA(1)),
                              ANIMATION_DURATION,
                              createShowAnimationCallback(x,
                                                          y));
            timer.schedule(TIMER_DELAY);
        }
        return this;
    }

    protected AnimationCallback createShowAnimationCallback(final double x,
                                                            final double y) {
        return new AnimationCallback() {
            @Override
            public void onClose(final IAnimation animation,
                                final IAnimationHandle handle) {
                super.onClose(animation,
                              handle);
                fireShow(x,
                         y);
            }
        };
    }

    public Decorator hide() {
        if (!timer.isRunning()) {
            decorator.animate(AnimationTweener.LINEAR,
                              AnimationProperties.toPropertyList(AnimationProperty.Properties.STROKE_ALPHA(0)),
                              ANIMATION_DURATION,
                              createHideAnimationCallback());
        }
        return this;
    }

    protected AnimationCallback createHideAnimationCallback() {
        return new AnimationCallback() {
            @Override
            public void onClose(IAnimation animation,
                                IAnimationHandle handle) {
                super.onClose(animation,
                              handle);
                fireHide();
            }
        };
    }

    protected void fireShow(final double x,
                            final double y) {
        if (null != callback) {
            callback.onShow(x,
                            y);
        }
    }

    protected void fireHide() {
        if (null != callback) {
            callback.onHide();
        }
    }
}
