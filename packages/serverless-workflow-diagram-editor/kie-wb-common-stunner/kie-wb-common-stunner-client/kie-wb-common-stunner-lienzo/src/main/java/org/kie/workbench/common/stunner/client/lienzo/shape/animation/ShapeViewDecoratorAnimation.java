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


package org.kie.workbench.common.stunner.client.lienzo.shape.animation;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.LienzoShapeView;
import org.kie.workbench.common.stunner.core.client.animation.AbstractAnimation;
import org.kie.workbench.common.stunner.core.client.animation.AnimationHandle;

import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.FILL_ALPHA;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.FILL_COLOR;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.STROKE_ALPHA;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.STROKE_COLOR;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.STROKE_WIDTH;

public class ShapeViewDecoratorAnimation extends AbstractAnimation<LienzoShapeView<?>> {

    private final boolean isStrokeNotFill;
    private final Supplier<LienzoShapeView<?>> shapeView;
    private final String color;
    private final double alpha;
    private final double strokeWidth;

    public static ShapeViewDecoratorAnimation newStrokeDecoratorAnimation(final Supplier<LienzoShapeView<?>> shapeView,
                                                                          final String strokeColor,
                                                                          final double strokeWidth,
                                                                          final double strokeAlpha) {
        return new ShapeViewDecoratorAnimation(shapeView,
                                               strokeColor,
                                               strokeWidth,
                                               strokeAlpha);
    }

    public static ShapeViewDecoratorAnimation newFillDecoratorAnimation(final Supplier<LienzoShapeView<?>> shapeView,
                                                                        final String fillColor,
                                                                        final double fillAlpha) {
        return new ShapeViewDecoratorAnimation(shapeView,
                                               fillColor,
                                               fillAlpha);
    }

    ShapeViewDecoratorAnimation(final Supplier<LienzoShapeView<?>> shapeView,
                                final String fillColor,
                                final double fillAlpha) {
        this.isStrokeNotFill = false;
        this.shapeView = shapeView;
        this.color = fillColor;
        this.alpha = fillAlpha;
        this.strokeWidth = 0;
    }

    ShapeViewDecoratorAnimation(final Supplier<LienzoShapeView<?>> shapeView,
                                final String strokeColor,
                                final double strokeWidth,
                                final double strokeAlpha) {
        this.isStrokeNotFill = true;
        this.shapeView = shapeView;
        this.color = strokeColor;
        this.alpha = strokeAlpha;
        this.strokeWidth = strokeWidth;
    }

    @Override
    public LienzoShapeView<?> getSource() {
        return shapeView.get();
    }

    @Override
    public AnimationHandle run() {
        final List<IAnimationHandle> handles = new LinkedList<>();
        getDecorators().forEach(dec -> handles.add(animate(dec)));
        return new AnimationHandle() {

            @Override
            public AnimationHandle run() {
                handles.forEach(IAnimationHandle::run);
                return this;
            }

            @Override
            public AnimationHandle stop() {
                handles.forEach(IAnimationHandle::stop);
                return this;
            }

            @Override
            public boolean isRunning() {
                for (IAnimationHandle h : handles) {
                    if (h.isRunning()) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public Supplier<LienzoShapeView<?>> getShapeView() {
        return shapeView;
    }

    public String getColor() {
        return color;
    }

    public double getStrokeWidth() {
        return strokeWidth;
    }

    public double getAlpha() {
        return alpha;
    }

    private IAnimationHandle animate(final com.ait.lienzo.client.core.shape.Shape<?> shape) {
        final AnimationProperties properties = isStrokeNotFill ?
                AnimationProperties.toPropertyList(STROKE_COLOR(color),
                                                   STROKE_ALPHA(alpha),
                                                   STROKE_WIDTH(strokeWidth)) :
                AnimationProperties.toPropertyList(FILL_COLOR(color),
                                                   FILL_ALPHA(alpha));
        return shape.animate(
                AnimationTweener.LINEAR,
                properties,
                getDuration(),
                getAnimationCallback());
    }

    private List<com.ait.lienzo.client.core.shape.Shape<?>> getDecorators() {
        return getSource().getDecorators();
    }

    com.ait.lienzo.client.core.animation.AnimationCallback getAnimationCallback() {
        return animationCallback;
    }

    private final com.ait.lienzo.client.core.animation.AnimationCallback animationCallback =
            new com.ait.lienzo.client.core.animation.AnimationCallback() {

                @Override
                public void onStart(final IAnimation animation,
                                    final IAnimationHandle handle) {
                    super.onStart(animation,
                                  handle);
                    if (null != getCallback()) {
                        getCallback().onStart();
                    }
                }

                @Override
                public void onFrame(final IAnimation animation,
                                    final IAnimationHandle handle) {
                    super.onFrame(animation,
                                  handle);
                    if (null != getCallback()) {
                        getCallback().onFrame();
                    }
                }

                @Override
                public void onClose(final IAnimation animation,
                                    final IAnimationHandle handle) {
                    super.onClose(animation,
                                  handle);
                    if (null != getCallback()) {
                        getCallback().onComplete();
                    }
                }
            };
}
