/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.client.lienzo.canvas.index.bounds;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.util.LienzoLayerUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.index.bounds.CanvasBoundsIndexer;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.BoundsIndexer;

@Dependent
public class CanvasBoundsIndexerImpl implements CanvasBoundsIndexer<AbstractCanvasHandler> {

    AbstractCanvasHandler canvasHandler;

    public BoundsIndexer<AbstractCanvasHandler, Node<View<?>, Edge>> build(final AbstractCanvasHandler context) {
        this.canvasHandler = context;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Node<View<?>, Edge> getAt(final double x,
                                     final double y) {
        final WiresCanvas canvas = (WiresCanvas) canvasHandler.getAbstractCanvas();
        final LienzoLayer lienzoLayer = canvas.getView().getLayer();
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
    @SuppressWarnings("unchecked")
    public Node<View<?>, Edge> getAt(final double x,
                                     final double y,
                                     final double width,
                                     final double height,
                                     final Element parentNode) {
        final WiresCanvas canvas = (WiresCanvas) canvasHandler.getAbstractCanvas();
        final LienzoLayer lienzoLayer = canvas.getView().getLayer();
        Node node;

        final String viewUUID_UL = LienzoLayerUtils.getUUID_At(lienzoLayer,
                                                               x,
                                                               y);

        final String viewUUID_UR = LienzoLayerUtils.getUUID_At(lienzoLayer,
                                                               x + width,
                                                               y);

        final String viewUUID_CC = LienzoLayerUtils.getUUID_At(lienzoLayer,
                                                               x + (width / 2),
                                                               y + (height / 2));
        final String viewUUID_LL = LienzoLayerUtils.getUUID_At(lienzoLayer,
                                                               x,
                                                               y + height);
        final String viewUUID_LR = LienzoLayerUtils.getUUID_At(lienzoLayer,
                                                               x + width,
                                                               y + height);

        if (null != viewUUID_UL && viewUUID_UL.trim().length() > 0) {
            final Shape<?> shape = canvas.getShape(viewUUID_UL);
            if (null != shape) {

                node = canvasHandler.getGraphIndex().getNode(shape.getUUID());
                if (node != parentNode) {
                    return node;
                }
            }
        } else if (null != viewUUID_UR && viewUUID_UR.trim().length() > 0) {
            final Shape<?> shape = canvas.getShape(viewUUID_UR);
            if (null != shape) {
                node = canvasHandler.getGraphIndex().getNode(shape.getUUID());
                if (node != parentNode) {
                    return node;
                }
            }
        } else if (null != viewUUID_CC && viewUUID_CC.trim().length() > 0) {
            final Shape<?> shape = canvas.getShape(viewUUID_CC);
            if (null != shape) {
                node = canvasHandler.getGraphIndex().getNode(shape.getUUID());
                if (node != parentNode) {
                    return node;
                }
            }
        } else if (null != viewUUID_LL && viewUUID_LL.trim().length() > 0) {
            final Shape<?> shape = canvas.getShape(viewUUID_LL);
            if (null != shape) {
                node = canvasHandler.getGraphIndex().getNode(shape.getUUID());
                if (node != parentNode) {
                    return node;
                }
            }
        } else if (null != viewUUID_LR && viewUUID_LR.trim().length() > 0) {
            final Shape<?> shape = canvas.getShape(viewUUID_LR);
            if (null != shape) {
                node = canvasHandler.getGraphIndex().getNode(shape.getUUID());
                if (node != parentNode) {
                    return node;
                }
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
