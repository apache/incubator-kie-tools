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

package org.kie.workbench.common.stunner.basicset.client.shape.def;

import org.kie.workbench.common.stunner.basicset.definition.Circle;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.shapes.client.view.CircleView;
import org.kie.workbench.common.stunner.shapes.def.CircleShapeDef;

public final class CircleShapeDefImpl
        implements BaseShapeViewDef<Circle, CircleView>,
                   CircleShapeDef<Circle, CircleView> {

    @Override
    public SizeHandler<Circle, CircleView> newSizeHandler() {
        return newSizeHandlerBuilder()
                .radius(this::getRadius)
                .build();
    }

    @Override
    public Double getRadius(final Circle element) {
        return element.getRadius().getValue();
    }
}
