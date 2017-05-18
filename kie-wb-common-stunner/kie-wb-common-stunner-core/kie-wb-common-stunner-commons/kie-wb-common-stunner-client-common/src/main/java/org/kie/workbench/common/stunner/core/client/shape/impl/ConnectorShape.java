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

package org.kie.workbench.common.stunner.core.client.shape.impl;

import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.kie.workbench.common.stunner.core.client.shape.Lifecycle;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.IsConnector;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.definition.shape.MutableShapeDef;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Magnet;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

/**
 * The default Shape implementation for edges with ViewContent type, so basically connectors.
 * <p>
 * It acts as the bridge between the edge and the shape view.
 * <p>
 * This implementation relies on ShapeDefinitions. This way provides the bridge between the edge and it's
 * bean definition instance, and delegates the interaction logic between the definition instance and the shape's
 * view to a ShapeDefViewHandler type.
 * @param <W> The bean type.
 * @param <V> The view type.
 * @param <D> The mutable shape definition type..
 */
public class ConnectorShape<W, D extends MutableShapeDef<W>, V extends ShapeView<?>>
        extends AbstractElementShape<W, ViewConnector<W>, Edge<ViewConnector<W>, Node>, MutableShapeDef<W>, V>
        implements EdgeShape<W, ViewConnector<W>, Edge<ViewConnector<W>, Node>, V>,
                   Lifecycle {

    public ConnectorShape(final D shapeDef,
                          final V view) {
        super(shapeDef,
              view);
    }

    public ConnectorShape(final D shapeDef,
                          final V view,
                          final ShapeStateHelper<V, Shape<V>> shapeStateHelper) {
        super(shapeDef,
              view,
              shapeStateHelper);
    }

    @Override
    public void applyConnections(final Edge<ViewConnector<W>, Node> element,
                                 final ShapeView<?> source,
                                 final ShapeView<?> target,
                                 final MutationContext mutationContext) {
        final ViewConnector connectionContent = (ViewConnector) element.getContent();
        final Magnet sourceMagnet = (Magnet) connectionContent.getSourceMagnet().orElse(null);
        final Magnet targetMagnet = (Magnet) connectionContent.getTargetMagnet().orElse(null);
        if (null != source && null != target) {
            ((IsConnector) getShapeView()).connect(source,
                                                   sourceMagnet,
                                                   target,
                                                   targetMagnet,
                                                   true,
                                                   false);
        }
    }

    @Override
    public void applyPosition(final Edge<ViewConnector<W>, Node> element,
                              final MutationContext mutationContext) {
        // TODO
    }

    @Override
    public void applyState(final ShapeState shapeState) {
        getShape().applyState(shapeState);
    }
}
