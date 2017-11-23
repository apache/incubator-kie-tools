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

package org.kie.workbench.common.stunner.svg.client.shape.impl;

import java.util.Collection;

import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.impl.NodeShapeImpl;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.svg.client.shape.SVGMutableShape;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGShapeViewDef;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGBasicShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitive;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;

public class SVGMutableShapeImpl<W, D extends SVGShapeViewDef<W, ?>>
        extends NodeShapeImpl<W, D, SVGShapeView>
        implements SVGMutableShape<W, SVGShapeView> {

    @SuppressWarnings("unchecked")
    public SVGMutableShapeImpl(final D shapeDef,
                               final SVGShapeViewImpl view) {
        super(shapeDef,
              view,
              view.getShapeStateHandler());
        view.getShapeStateHandler().forShape(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void applyCustomProperties(final Node<View<W>, Edge> element,
                                      final MutationContext mutationContext) {
        super.applyCustomProperties(element,
                                    mutationContext);
        // Handle the different svg primitive views, if any.
        final Collection<SVGPrimitive<?>> svgPrimitives = getShapeView().getChildren();
        svgPrimitives.forEach(this::updateSVGPrimitiveView);
        // Handle the different svg children views, if any.
        final Collection<SVGBasicShapeView> svgViews = getShapeView().getSVGChildren();
        svgViews.forEach(view -> updateSVGPrimitiveView(view.getPrimitive()));
    }

    private void updateSVGPrimitiveView(final SVGPrimitive<?> primitive) {
        if (primitive instanceof SVGPrimitiveShape) {
            ((SVGPrimitiveShape) primitive).getPolicy().accept(getShapeView(),
                                                               ((SVGPrimitiveShape) primitive).get());
        }
    }
}
