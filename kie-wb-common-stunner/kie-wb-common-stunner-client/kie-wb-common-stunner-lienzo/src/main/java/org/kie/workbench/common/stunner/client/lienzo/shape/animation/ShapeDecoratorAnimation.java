/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.shape.animation;

import java.util.List;

import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.animation.AbstractShapeAnimation;
import org.kie.workbench.common.stunner.core.client.shape.view.HasDecorators;

import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.STROKE_ALPHA;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.STROKE_COLOR;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.STROKE_WIDTH;

public class ShapeDecoratorAnimation extends AbstractShapeAnimation<Shape> {

    private final String color;
    private final double strokeWidth;
    private final double strokeAlpha;

    public ShapeDecoratorAnimation(final String color,
                                   final double strokeWidth,
                                   final double strokeAlpha) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.strokeAlpha = strokeAlpha;
    }

    @Override
    public void run() {
        getDecorators().forEach(this::animate);
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

    @SuppressWarnings("unchecked")
    private List<com.ait.lienzo.client.core.shape.Shape<?>> getDecorators() {
        final HasDecorators<com.ait.lienzo.client.core.shape.Shape<?>> shapeView =
                (HasDecorators<com.ait.lienzo.client.core.shape.Shape<?>>) getSource().getShapeView();
        return shapeView.getDecorators();
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
