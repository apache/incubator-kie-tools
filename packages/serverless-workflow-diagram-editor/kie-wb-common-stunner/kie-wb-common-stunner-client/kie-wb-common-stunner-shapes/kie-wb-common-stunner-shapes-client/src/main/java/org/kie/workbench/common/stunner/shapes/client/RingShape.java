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

import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.shapes.client.view.RingView;
import org.kie.workbench.common.stunner.shapes.def.RingShapeDef;

public class RingShape<W> extends BasicContainerShape<W, RingShapeDef<W, RingView>, RingView> {

    public RingShape(final RingShapeDef<W, RingView> shapeDef,
                     final RingView view) {
        super(shapeDef,
              view);
    }

    @Override
    public void applyCustomProperties(final Node<View<W>, Edge> element,
                                      final MutationContext mutationContext) {
        super.applyCustomProperties(element,
                                    mutationContext);
        // Inner Radius.
        final Double innerRadius = getShapeDefinition().getInnerRadius(getDefinition(element));
        _applyInnerRadius(element,
                          innerRadius,
                          mutationContext);
    }

    private void _applyInnerRadius(final Node<View<W>, Edge> element,
                                   final Double radius,
                                   final MutationContext mutationContext) {
        if (null != radius) {
            getShapeView().setInnerRadius(radius);
        }
    }

    @Override
    public String toString() {
        return "RingShape{}";
    }
}
