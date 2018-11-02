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

package org.kie.workbench.common.stunner.cm.client.canvas;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.client.lienzo.Lienzo;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.widgets.canvas.view.LienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.canvas.wires.WiresCanvasPresenter;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementShapeView;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasDrawnEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;

@Dependent
@CaseManagementEditor
public class CaseManagementCanvasPresenter extends WiresCanvasPresenter {

    protected CaseManagementCanvasPresenter() {
        this(null, null, null, null, null, null, null, null);
    }

    @Inject
    public CaseManagementCanvasPresenter(final Event<CanvasClearEvent> canvasClearEvent,
                                         final Event<CanvasShapeAddedEvent> canvasShapeAddedEvent,
                                         final Event<CanvasShapeRemovedEvent> canvasShapeRemovedEvent,
                                         final Event<CanvasDrawnEvent> canvasDrawnEvent,
                                         final Event<CanvasFocusedEvent> canvasFocusedEvent,
                                         final @Lienzo Layer layer,
                                         final @CaseManagementEditor WiresCanvas.View view,
                                         final LienzoPanel lienzoPanel) {
        super(canvasClearEvent,
              canvasShapeAddedEvent,
              canvasShapeRemovedEvent,
              canvasDrawnEvent,
              canvasFocusedEvent,
              layer,
              view,
              lienzoPanel);
    }

    @SuppressWarnings("unchecked")
    public Canvas addChildShape(final Shape parent, final Shape child, final int index) {

        ((CaseManagementCanvasView) view).addChildShape(parent.getShapeView(), child.getShapeView(), index);

        log(Level.FINE, "Adding child [" + child.getUUID() + "] into parent [" + parent.getUUID() + "]");
        return this;
    }

    @Override
    protected void clearShapes() {
        // the child shapes are not ordered
        // clear the child shapes recursively in the right order
        shapes.values().stream().filter(s -> {
            CaseManagementShapeView view = (CaseManagementShapeView) s.getShapeView();
            return !(view.getParent() instanceof CaseManagementShapeView);
        }).collect(Collectors.toList()).forEach(this::clearShape);

        shapes.clear();
    }

    private void clearShape(Shape shape) {
        CaseManagementShapeView view = (CaseManagementShapeView) shape.getShapeView();
        List<Shape> childShapes = view.getChildShapes().toList().stream()
                .map(v -> shapes.get(((CaseManagementShapeView) v).getUUID())).collect(Collectors.toList());

        deleteShape(shape);
        childShapes.forEach(this::clearShape);
    }
}
