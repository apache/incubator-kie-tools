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

package org.kie.workbench.common.stunner.core.client.canvas.util;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.graph.Node;

public class CanvasHighlight {

    private final AbstractCanvasHandler canvasHandler;
    private Shape shape;
    private long duration = 50;

    public CanvasHighlight(final AbstractCanvasHandler canvasHandler) {
        this.canvasHandler = canvasHandler;
    }

    public CanvasHighlight setDuration(final long duration) {
        this.duration = duration;
        return this;
    }

    public void highLight(final Node node) {
        applyState(node,
                   ShapeState.HIGHLIGHT);
    }

    public void invalid(final Node node) {
        applyState(node,
                   ShapeState.INVALID);
    }

    public void none(final Node node) {
        applyState(node,
                   ShapeState.NONE);
    }

    private void applyState(final Node node,
                            final ShapeState state) {
        // Only one shape is being highlight at same time, so take this into account in the next conditional sentence.
        if (null != this.shape && !node.getUUID().equals(shape.getUUID())) {
            unhighLight();
        }
        if (null == this.shape) {
            final String uuid = node.getUUID();
            final Shape shape = getShape(uuid);
            if (null != shape) {
                this.shape = shape;
                shape.applyState(state);
                getCanvas().draw();
            }
        }
    }

    public void unhighLight() {
        if (null != this.shape) {
            this.shape.applyState(ShapeState.NONE);
            getCanvas().draw();
            this.shape = null;
        }
    }

    public void destroy() {
        this.shape = null;
    }

    private Shape getShape(final String uuid) {
        return getCanvas().getShape(uuid);
    }

    private AbstractCanvas getCanvas() {
        return canvasHandler.getCanvas();
    }
}
