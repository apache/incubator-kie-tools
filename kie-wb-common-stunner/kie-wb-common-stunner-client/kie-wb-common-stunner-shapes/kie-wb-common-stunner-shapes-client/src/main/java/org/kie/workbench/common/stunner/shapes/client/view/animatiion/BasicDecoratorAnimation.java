/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.shapes.client.view.animatiion;

import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import org.kie.workbench.common.stunner.core.client.shape.Shape;

import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.STROKE_ALPHA;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.STROKE_COLOR;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.STROKE_WIDTH;

abstract class BasicDecoratorAnimation<S extends Shape> extends AbstractBasicAnimation<S> {

    private final String color;
    private final double strokeWidth;
    private final double strokeAlpha;

    public BasicDecoratorAnimation(final String color,
                                   final double strokeWidth,
                                   final double strokeAlpha) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.strokeAlpha = strokeAlpha;
    }

    abstract com.ait.lienzo.client.core.shape.Shape getDecorator();

    @Override
    public void run() {
        getDecorator().animate(
                AnimationTweener.LINEAR,
                AnimationProperties.toPropertyList(STROKE_ALPHA(strokeAlpha),
                                                   STROKE_COLOR(color),
                                                   STROKE_WIDTH(strokeWidth)),
                getDuration(),
                getAnimationCallback());
    }
}
