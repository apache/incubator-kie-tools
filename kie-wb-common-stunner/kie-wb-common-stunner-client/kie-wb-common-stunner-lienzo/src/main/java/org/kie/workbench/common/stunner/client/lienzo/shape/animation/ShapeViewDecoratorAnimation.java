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

import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.STROKE_ALPHA;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.STROKE_COLOR;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.STROKE_WIDTH;

public class ShapeViewDecoratorAnimation extends AbstractAnimation<LienzoShapeView<?>> {

    private final Supplier<LienzoShapeView<?>> shapeView;
    private final String color;
    private final double strokeWidth;
    private final double strokeAlpha;

    public ShapeViewDecoratorAnimation(final Supplier<LienzoShapeView<?>> shapeView,
                                       final String color,
                                       final double strokeWidth,
                                       final double strokeAlpha) {
        this.shapeView = shapeView;
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.strokeAlpha = strokeAlpha;
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

    public double getStrokeAlpha() {
        return strokeAlpha;
    }

    private IAnimationHandle animate(final com.ait.lienzo.client.core.shape.Shape<?> shape) {
        return shape.animate(
                AnimationTweener.LINEAR,
                AnimationProperties.toPropertyList(STROKE_COLOR(color),
                                                   STROKE_ALPHA(strokeAlpha),
                                                   STROKE_WIDTH(strokeWidth)),
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
