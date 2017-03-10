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

import org.kie.workbench.common.stunner.core.client.canvas.Point2D;
import org.kie.workbench.common.stunner.core.client.shape.Lifecycle;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.NodeShape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.definition.shape.MutableShapeDef;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

/**
 * The default Shape implementation for nodes. It acts as the bridge between a node and the shape view.
 * <p/>
 * This implementation relies on ShapeDefinitions. This way provides the bridge between the node and it's
 * bean definition instance, and delegates the interaction logic between the definition instance and the shape's
 * view to a ShapeDefViewHandler type.
 * @param <W> The bean type.
 * @param <V> The view type.
 * @param <D> The mutable shape definition type..
 */
public class NodeShapeImpl<W, D extends MutableShapeDef<W>, V extends ShapeView<?>>
        extends AbstractElementShape<W, View<W>, Node<View<W>, Edge>, D, V>
        implements NodeShape<W, View<W>, Node<View<W>, Edge>, V>,
                   Lifecycle {

    /*
        The following instance members:
            - _strokeWidth
            - _strokeAlpha
            - _strokeColor
        Are used to keep the original stroke attributes from the
        domain model object because the behavior for changing
        this shape state is based on updating the shape's borders.
        Eg: when the shape is in SELECTED state, the borders are
        using different colors/sizes, so to be able to get
        back to NONE state, it just reverts the border attributes
        to these private instance members.
     */
    protected double _strokeWidth = 1;
    protected double _strokeAlpha = 0;
    protected String _strokeColor = null;
    private ShapeState state;

    public NodeShapeImpl(final D shapeDef,
                         final V view) {
        super(shapeDef,
              view);
        this.state = ShapeState.NONE;
    }

    @Override
    public void applyPosition(final Node<View<W>, Edge> element,
                              final MutationContext mutationContext) {
        final Point2D position = GraphUtils.getPosition(element.getContent());
        getShapeView().setShapeX(position.getX());
        getShapeView().setShapeY(position.getY());
    }

    @Override
    public void applyProperties(final Node<View<W>, Edge> element,
                                final MutationContext mutationContext) {
        final W definition = getDefinition(element);
        _strokeColor = getDefViewHandler().getShapeDefinition().getBorderColor(definition);
        _strokeWidth = getDefViewHandler().getShapeDefinition().getBorderSize(definition);
        _strokeAlpha = getDefViewHandler().getShapeDefinition().getBorderAlpha(definition);
        getDefViewHandler().applyProperties(definition,
                                            mutationContext);
    }

    @Override
    public void applyState(final ShapeState shapeState) {
        if (!this.state.equals(shapeState)) {
            this.state = shapeState;
            switchState();
        }
    }

    public ShapeState getState() {
        return state;
    }

    protected void switchState() {
        switch (this.state) {
            case NONE:
                applyNoneState();
                break;
            default:
                applyActiveState(this.state.getColor());
        }
    }

    protected void applyActiveState(final String color) {
        getShapeView().setStrokeColor(color);
        getShapeView().setStrokeWidth(1.5d);
        getShapeView().setStrokeAlpha(1d);
    }

    protected void applyNoneState() {
        getShapeView().setStrokeColor(_strokeColor);
        getShapeView().setStrokeWidth(_strokeWidth);
        getShapeView().setStrokeAlpha(_strokeAlpha);
    }
}
