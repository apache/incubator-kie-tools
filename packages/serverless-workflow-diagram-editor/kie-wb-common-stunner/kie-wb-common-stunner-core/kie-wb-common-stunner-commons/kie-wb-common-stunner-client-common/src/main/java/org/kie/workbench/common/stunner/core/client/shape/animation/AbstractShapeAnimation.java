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

package org.kie.workbench.common.stunner.core.client.shape.animation;

import org.kie.workbench.common.stunner.core.client.animation.AbstractAnimation;
import org.kie.workbench.common.stunner.core.client.shape.Shape;

public abstract class AbstractShapeAnimation<S extends Shape>
        extends AbstractAnimation<S>
        implements ShapeAnimation<S> {

    private S shape;

    @Override
    public ShapeAnimation<S> forShape(final S shape) {
        this.shape = shape;
        return this;
    }

    @Override
    public S getSource() {
        return shape;
    }
}
