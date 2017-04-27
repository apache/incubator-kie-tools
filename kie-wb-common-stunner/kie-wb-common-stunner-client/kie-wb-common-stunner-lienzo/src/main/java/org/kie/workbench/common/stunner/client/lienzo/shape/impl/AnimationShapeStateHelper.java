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

package org.kie.workbench.common.stunner.client.lienzo.shape.impl;

import org.kie.workbench.common.stunner.client.lienzo.shape.animation.ShapeDecoratorAnimation;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateHelper;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

public class AnimationShapeStateHelper<V extends ShapeView, S extends Shape<V>> extends ShapeStateHelper<V, S> {

    public AnimationShapeStateHelper() {
        super();
    }

    public AnimationShapeStateHelper(final S shape) {
        super(shape);
    }

    /**
     * Use Lienzo animations for decorator updates.
     */
    @Override
    protected void applyActiveState(final String color) {
        runAnimation(color,
                     getActiveStrokeWidth(),
                     ACTIVE_STROKE_ALPHA);
    }

    /**
     * Use Lienzo animations for decorator updates.
     */
    @Override
    protected void applyNoneState(final String color,
                                  final double width,
                                  final double alpha) {
        runAnimation(color,
                     width,
                     alpha);
    }

    private void runAnimation(final String color,
                              final double width,
                              final double alpha) {
        new ShapeDecoratorAnimation(color,
                                    width,
                                    alpha).forShape(getShape()).run();
    }
}
