/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.canvas.index.bounds;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.util.LienzoLayerUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.index.bounds.CanvasBoundsIndexer;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.BoundsIndexer;

@Dependent
public class CanvasBoundsIndexerImpl implements CanvasBoundsIndexer<AbstractCanvasHandler> {

    private AbstractCanvasHandler canvasHandler;

    public BoundsIndexer<AbstractCanvasHandler, Node<View<?>, Edge>> build(final AbstractCanvasHandler context) {
        this.canvasHandler = context;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Node<View<?>, Edge> getAt(final double x,
                                     final double y) {
        final AbstractCanvas canvas = canvasHandler.getAbstractCanvas();
        final LienzoLayer lienzoLayer = (LienzoLayer) canvas.getLayer();
        final String viewUUID = LienzoLayerUtils.getUUID_At(lienzoLayer,
                                                            x,
                                                            y);
        if (null != viewUUID && viewUUID.trim().length() > 0) {
            final Shape<?> shape = canvas.getShape(viewUUID);
            if (null != shape) {
                return canvasHandler.getGraphIndex().getNode(shape.getUUID());
            }
        }
        return null;
    }

    @Override
    public double[] getTrimmedBounds() {
        // TODO
        return new double[0];
    }

    @Override
    public void destroy() {
        this.canvasHandler = null;
    }
}
