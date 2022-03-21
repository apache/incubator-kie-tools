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

package org.kie.workbench.common.stunner.sw.client.shapes;

import org.kie.workbench.common.stunner.client.lienzo.shape.impl.ShapeStateDefaultHandler;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.common.DashArray;
import org.kie.workbench.common.stunner.core.client.shape.impl.ConnectorShape;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

public class TransitionShape<W>
        extends ConnectorShape<W, TransitionShapeDef<W>, TransitionView> {

    @SuppressWarnings("unchecked")
    public TransitionShape(TransitionShapeDef<W> shapeDef,
                           TransitionView view) {
        super(shapeDef,
              view,
              new ShapeStateDefaultHandler()
                      .setRenderType(ShapeStateDefaultHandler.RenderType.STROKE)
                      .setBorderShape(() -> view)
                      .setBackgroundShape(() -> view));
    }

    @Override
    protected void applyCustomProperties(Edge<ViewConnector<W>, Node> element, MutationContext mutationContext) {
        super.applyCustomProperties(element, mutationContext);
        DashArray dashArray = getShapeDefinition().getDashArray(getDefinition(element));
        if (null != dashArray) {
            getShapeView().setDashArray(dashArray);
        }
    }

    @Override
    public void applyState(final ShapeState shapeState) {
        super.applyState(shapeState);
        if (!isSelected()) {
            getShapeView().hideControlPoints();
        }
    }

    private boolean isSelected() {
        return ShapeState.SELECTED.equals(getShape().getShapeStateHandler().getShapeState());
    }
}