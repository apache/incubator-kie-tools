/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.svg.client.shape.view.factory;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGContainer;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;

public abstract class AbstractSVGViewFactory {

    public static final SVGShapeViewBuilder DEFAULT_VIEW_BUILDER = SVGShapeViewImpl::new;

    private final SVGShapeViewBuilder viewBuilder;

    protected AbstractSVGViewFactory() {
        this(DEFAULT_VIEW_BUILDER);
    }

    protected AbstractSVGViewFactory(final SVGShapeViewBuilder viewBuilder) {
        this.viewBuilder = viewBuilder;
    }

    public SVGShapeViewBuilder getViewBuilder() {
        return viewBuilder;
    }

    protected static SVGContainer newSVGContainer(final String id,
                                                  final Group group,
                                                  final boolean scalable,
                                                  final LayoutContainer.Layout layout) {
        return new SVGContainer(id,
                                group,
                                scalable,
                                layout);
    }

    protected static SVGPrimitiveShape newSVGPrimitiveShape(final Shape<?> primitive,
                                                            final boolean scalable,
                                                            final LayoutContainer.Layout layout) {
        return new SVGPrimitiveShape(primitive,
                                     scalable,
                                     layout);
    }
}
