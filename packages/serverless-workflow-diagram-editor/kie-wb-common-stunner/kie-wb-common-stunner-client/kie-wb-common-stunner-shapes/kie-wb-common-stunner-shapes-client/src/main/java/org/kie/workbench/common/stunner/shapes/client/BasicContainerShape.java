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

package org.kie.workbench.common.stunner.shapes.client;

import org.kie.workbench.common.stunner.client.lienzo.shape.impl.LienzoShape;
import org.kie.workbench.common.stunner.client.lienzo.shape.impl.ShapeStateAttributeAnimationHandler;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.LienzoShapeView;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.impl.ContainerShape;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributesFactory;

public class BasicContainerShape<W, V extends LienzoShapeView<?>>
        extends ContainerShape<W, V, Shape<?>> {

    @SuppressWarnings("unchecked")
    public BasicContainerShape(final V view) {
        super(new LienzoShape<V>(view,
                                 new ShapeStateAttributeAnimationHandler<>()
                                         .getAttributesHandler()
                                         .useAttributes(ShapeStateAttributesFactory::buildStateAttributes)
                                         .setView(() -> view)));
    }
}
