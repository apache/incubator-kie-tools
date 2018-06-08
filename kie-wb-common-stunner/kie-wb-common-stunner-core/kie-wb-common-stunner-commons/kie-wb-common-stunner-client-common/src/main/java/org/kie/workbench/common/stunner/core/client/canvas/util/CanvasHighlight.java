/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas.util;

import java.util.LinkedList;
import java.util.List;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;

public class CanvasHighlight {

    private final AbstractCanvasHandler canvasHandler;
    private final List<String> uuids = new LinkedList<>();

    public CanvasHighlight(final AbstractCanvasHandler canvasHandler) {
        this.canvasHandler = canvasHandler;
    }

    public CanvasHighlight highLight(final Element<?> node) {
        applyState(node,
                   ShapeState.HIGHLIGHT);
        return this;
    }

    public CanvasHighlight invalid(final Element<?> node) {
        applyState(node,
                   ShapeState.INVALID);
        return this;
    }

    public CanvasHighlight invalid(final RuleViolations violations) {
        invalid(violations.violations());
        return this;
    }

    public CanvasHighlight invalid(final Iterable<? extends RuleViolation> violations) {
        violations.forEach(v -> {
            final String uuid = v.getUUID();
            applyStateToShape(uuid,
                              ShapeState.INVALID);
        });
        return this;
    }

    public CanvasHighlight none(final Element<?> node) {
        applyState(node,
                   ShapeState.NONE);
        return this;
    }

    public CanvasHighlight unhighLight() {
        if (!uuids.isEmpty()) {
            uuids.forEach(uuid -> {
                final Shape shape = getShape(uuid);
                if (null != shape) {
                    shape.applyState(ShapeState.NONE);
                }
            });
            uuids.clear();
        }
        setValidCursor();
        return this;
    }

    public void destroy() {
        this.uuids.clear();
        setValidCursor();
    }

    private void applyState(final Element<?> node,
                            final ShapeState state) {
        applyStateToShape(node.getUUID(),
                          state);
    }

    private void applyStateToShape(final String uuid,
                                   final ShapeState state) {
        final Shape shape = getShape(uuid);
        if (null != shape) {
            uuids.add(uuid);
            shape.applyState(state);
        }
        if (ShapeState.INVALID.equals(state)) {
            setInvalidCursor();
        } else {
            setValidCursor();
        }
    }

    private void setInvalidCursor() {
        setCursor(AbstractCanvas.Cursors.NOT_ALLOWED);
    }

    private void setValidCursor() {
        setCursor(AbstractCanvas.Cursors.AUTO);
    }

    private void setCursor(final AbstractCanvas.Cursors cursor) {
        getCanvas().getView().setCursor(cursor);
    }

    private Shape getShape(final String uuid) {
        return getCanvas().getShape(uuid);
    }

    private AbstractCanvas getCanvas() {
        return canvasHandler.getAbstractCanvas();
    }
}
