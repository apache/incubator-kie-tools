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

import org.kie.workbench.common.stunner.client.lienzo.shape.view.LienzoShapeView;
import org.kie.workbench.common.stunner.core.client.animation.AnimationHandle;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.animation.AbstractShapeAnimation;

public class ShapeDecoratorAnimation extends AbstractShapeAnimation<Shape> {

    private final ShapeViewDecoratorAnimation viewAnimation;

    public ShapeDecoratorAnimation(final String color,
                                   final double strokeWidth,
                                   final double strokeAlpha) {
        viewAnimation = new ShapeViewDecoratorAnimation(this::getShapeView,
                                                        color,
                                                        strokeWidth,
                                                        strokeAlpha);
    }

    private LienzoShapeView<?> getShapeView() {
        return (LienzoShapeView<?>) getSource().getShapeView();
    }

    @Override
    public AnimationHandle run() {
        return viewAnimation.run();
    }
}
