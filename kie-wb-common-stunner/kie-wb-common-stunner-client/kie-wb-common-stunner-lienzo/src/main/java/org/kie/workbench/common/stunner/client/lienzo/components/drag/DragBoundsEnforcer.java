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

package org.kie.workbench.common.stunner.client.lienzo.components.drag;

import java.util.Optional;

import com.ait.lienzo.client.core.shape.wires.SelectionManager;
import com.ait.lienzo.client.core.types.BoundingBox;
import org.kie.workbench.common.stunner.core.client.shape.view.HasDragBounds;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;

public class DragBoundsEnforcer {

    private final Optional<ShapeView> shape;
    private final Optional<SelectionManager> selectionManager;
    private double[] bounds;
    private int margin = 0;

    private DragBoundsEnforcer(ShapeView shape, SelectionManager selectionManager) {
        this.selectionManager = Optional.ofNullable(selectionManager);
        this.shape = Optional.ofNullable(shape);
    }

    public static DragBoundsEnforcer forShape(final ShapeView shape) {
        return new DragBoundsEnforcer(shape, null);
    }

    public static DragBoundsEnforcer forSelectionManager(final SelectionManager selectionManager) {
        return new DragBoundsEnforcer(null, selectionManager);
    }

    public DragBoundsEnforcer withMargin(final int margin) {
        this.margin = margin;
        return this;
    }

    public void enforce(Bounds bounds) {
        this.bounds = parseBounds(bounds);
        enforce();
    }

    public void enforce(Graph<DefinitionSet, ? extends Node> graph) {
        this.bounds = parseBounds(graph.getContent().getBounds());
        enforce();
    }

    private void enforce() {
        selectionManager
                .map(SelectionManager::getControl)
                .ifPresent(control -> control.setBoundsConstraint(new BoundingBox(bounds[0],
                                                                                  bounds[1],
                                                                                  bounds[2],
                                                                                  bounds[3])));
        shape.filter(s -> s instanceof HasDragBounds)
                .map(s -> (HasDragBounds) s)
                .ifPresent(s -> s.setDragBounds(bounds[0], bounds[1], bounds[2], bounds[3]));
    }

    private double[] parseBounds(Bounds bounds) {
        return new double[]{
                bounds.getUpperLeft().getX() + margin,
                bounds.getUpperLeft().getY() + margin,
                bounds.getLowerRight().getX() + margin,
                bounds.getLowerRight().getY() + margin};
    }
}
