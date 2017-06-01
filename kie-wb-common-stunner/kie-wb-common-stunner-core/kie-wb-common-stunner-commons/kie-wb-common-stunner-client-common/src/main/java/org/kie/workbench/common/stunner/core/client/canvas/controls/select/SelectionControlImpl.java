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

package org.kie.workbench.common.stunner.core.client.canvas.controls.select;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasElementSelectedEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.graph.Element;

@Dependent
public final class SelectionControlImpl<H extends AbstractCanvasHandler> extends AbstractSelectionControl<H> {

    @Inject
    public SelectionControlImpl(final Event<CanvasElementSelectedEvent> elementSelectedEvent,
                                final Event<CanvasClearSelectionEvent> clearSelectionEvent) {
        super(elementSelectedEvent,
              clearSelectionEvent);
    }

    /*
        **************************************************************
        *               CANVAS CONTROL METHODS
        ***************************************************************
     */

    @Override
    protected void register(final Element element,
                            final Shape<?> shape) {
        final ShapeView shapeView = shape.getShapeView();
        if (shapeView instanceof HasEventHandlers) {
            final HasEventHandlers hasEventHandlers = (HasEventHandlers) shapeView;
            if (hasEventHandlers.supports(ViewEventType.MOUSE_CLICK)) {
                // Click event.
                final MouseClickHandler clickHandler = new MouseClickHandler() {
                    @Override
                    public void handle(final MouseClickEvent event) {
                        if (event.isButtonLeft()) {
                            SelectionControlImpl.super.select(element,
                                                              !event.isShiftKeyDown());
                        }
                    }
                };
                hasEventHandlers.addHandler(ViewEventType.MOUSE_CLICK,
                                            clickHandler);
                registerHandler(shape.getUUID(),
                                clickHandler);
            }
        }
    }
}
