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

package org.kie.workbench.common.stunner.shapes.client;

import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.shapes.client.view.RingView;
import org.kie.workbench.common.stunner.shapes.def.RingShapeDef;

// TODO
public class RingShape<W> extends AbstractBasicShapeWithTitle<W, RingView, RingShapeDef<W>> {

    public RingShape(final RingView view,
                     final RingShapeDef<W> proxy) {
        super(view,
              proxy);
    }

    @Override
    public void applyProperties(final Node<View<W>, Edge> element,
                                final MutationContext mutationContext) {
        super.applyProperties(element,
                              mutationContext);
        // Outer Radius.
        final Double outerRadius = proxy.getOuterRadius(getDefinition(element));
        _applyOuterRadius(element,
                          outerRadius,
                          mutationContext);
        // Inner Radius.
        final Double innerRadius = proxy.getInnerRadius(getDefinition(element));
        _applyInnerRadius(element,
                          innerRadius,
                          mutationContext);
    }

    protected RingShape<W> _applyOuterRadius(final Node<View<W>, Edge> element,
                                             final Double radius,
                                             final MutationContext mutationContext) {
        if (null != radius) {
            getShapeView().setOuterRadius(radius);
        }
        return this;
    }

    protected RingShape<W> _applyInnerRadius(final Node<View<W>, Edge> element,
                                             final Double radius,
                                             final MutationContext mutationContext) {
        if (null != radius) {
            getShapeView().setInnerRadius(radius);
        }
        return this;
    }

    @Override
    public String toString() {
        return "RingShape{}";
    }
}
