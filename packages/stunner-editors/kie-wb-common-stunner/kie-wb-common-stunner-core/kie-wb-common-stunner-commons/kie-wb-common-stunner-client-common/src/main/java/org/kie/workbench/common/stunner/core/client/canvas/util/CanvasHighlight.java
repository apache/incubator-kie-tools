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


package org.kie.workbench.common.stunner.core.client.canvas.util;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;

@Dependent
public class CanvasHighlight {

    private final Set<String> uuids;
    private AbstractCanvasHandler canvasHandler;

    public CanvasHighlight() {
        this.uuids = new HashSet<>();
    }

    public CanvasHighlight setCanvasHandler(final AbstractCanvasHandler canvasHandler) {
        this.canvasHandler = canvasHandler;
        return this;
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
            HashSet<String> copy = new HashSet<>(uuids);
            uuids.clear();
            copy.forEach(uuid -> {
                final Shape shape = getShape(uuid);
                if (null != shape) {
                    shape.applyState(ShapeState.NONE);
                }
            });
        }
        setCursor(AbstractCanvas.Cursors.MOVE);
        return this;
    }

    public void destroy() {
        setValidCursor();
        this.uuids.clear();
        this.canvasHandler = null;
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

    void onCanvasUnhighlightEvent(final @Observes CanvasUnhighlightEvent event) {
        unhighLight();
    }

    private void setInvalidCursor() {
        setCursor(AbstractCanvas.Cursors.NOT_ALLOWED);
    }

    private void setValidCursor() {
        setCursor(AbstractCanvas.Cursors.DEFAULT);
    }

    private void setCursor(final AbstractCanvas.Cursors cursor) {
        if (null != getCanvas()) {
            getCanvas().getView().setCursor(cursor);
        }
    }

    private Shape getShape(final String uuid) {
        return null != getCanvas() ? getCanvas().getShape(uuid) : null;
    }

    private AbstractCanvas getCanvas() {
        return null != canvasHandler ? canvasHandler.getAbstractCanvas() : null;
    }
}
