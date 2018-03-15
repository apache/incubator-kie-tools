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

package org.kie.workbench.common.stunner.cm.client.shape;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.cm.client.shape.def.NullShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.view.NullView;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.AbstractElementShape;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeImpl;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributeHandler;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class NullShape
        extends AbstractElementShape<BPMNViewDefinition, View<BPMNViewDefinition>, Node<View<BPMNViewDefinition>, Edge>, NullShapeDef, NullView> {

    private final ShapeImpl<NullView> shape;

    @SuppressWarnings("unchecked")
    public NullShape(final NullShapeDef shapeDef,
                     final NullView view) {
        super(shapeDef);
        this.shape = new ShapeImpl<>(view,
                                     new ShapeStateAttributeHandler<NullView>()
                                             .setView(() -> view));
    }

    @Override
    protected ShapeImpl<NullView> getShape() {
        return shape;
    }

    @Override
    public void applyPosition(final Node<View<BPMNViewDefinition>, Edge> element,
                              final MutationContext mutationContext) {
        //A NullShape should not be rendered; and represents a BPMN2 node that is not rendered for Case Management
    }

    @Override
    public void applyProperties(final Node<View<BPMNViewDefinition>, Edge> element,
                                final MutationContext mutationContext) {
        //A NullShape should not be rendered; and represents a BPMN2 node that is not rendered for Case Management
    }

    @Override
    public void applyState(final ShapeState shapeState) {
        //A NullShape should not be rendered; and represents a BPMN2 node that is not rendered for Case Management
    }
}
