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

package org.kie.workbench.common.stunner.core.client.canvas.controls.actions;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.keyboard.KeyDownEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasElementSelectedEvent;
import org.kie.workbench.common.stunner.core.client.components.actions.NameEditBox;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseDoubleClickHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextOutEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextOutHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextOverEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextOverHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.uberfire.mvp.Command;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class CanvasNameEditionControlImpl
        extends AbstractCanvasHandlerRegistrationControl
        implements CanvasNameEditionControl<AbstractCanvasHandler, Element> {

    private static final int FLOATING_VIEW_TIMEOUT = 3000;
    private static final double SHAPE_EDIT_ALPH = 0.2d;

    FloatingView<IsWidget> floatingView;
    NameEditBox<AbstractCanvasHandler, Element> nameEditBox;
    Event<CanvasElementSelectedEvent> elementSelectedEvent;

    private String uuid;

    private final Command floatingHideCallback = CanvasNameEditionControlImpl.this::hide;

    @Inject
    public CanvasNameEditionControlImpl(final FloatingView<IsWidget> floatingView,
                                        final NameEditBox<AbstractCanvasHandler, Element> nameEditBox,
                                        final Event<CanvasElementSelectedEvent> elementSelectedEvent) {
        this.floatingView = floatingView;
        this.nameEditBox = nameEditBox;
        this.elementSelectedEvent = elementSelectedEvent;
        this.uuid = null;
    }

    @Override
    public void enable(final AbstractCanvasHandler canvasHandler) {
        super.enable(canvasHandler);
        nameEditBox.initialize(canvasHandler,
                               () -> {
                                   CanvasNameEditionControlImpl.this.hide();
                                   elementSelectedEvent.fire(new CanvasElementSelectedEvent(canvasHandler,
                                                                                            CanvasNameEditionControlImpl.this.uuid));
                               });
        floatingView
                .hide()
                .setHideCallback(floatingHideCallback)
                .setTimeOut(FLOATING_VIEW_TIMEOUT)
                .add(nameEditBox.asWidget());
    }

    @Override
    public void register(final Element element) {
        final Shape<?> shape = getShape(element.getUUID());
        if (null != shape) {
            final ShapeView shapeView = shape.getShapeView();
            if (shapeView instanceof HasEventHandlers) {
                final HasEventHandlers hasEventHandlers = (HasEventHandlers) shapeView;
                if (hasEventHandlers.supports(ViewEventType.MOUSE_DBL_CLICK)) {
                    // Double click event.
                    final MouseDoubleClickHandler doubleClickHandler = new MouseDoubleClickHandler() {
                        @Override
                        public void handle(final MouseDoubleClickEvent event) {
                            CanvasNameEditionControlImpl.this.show(element,
                                                                   event.getClientX(),
                                                                   event.getClientY());
                        }
                    };
                    hasEventHandlers.addHandler(ViewEventType.MOUSE_DBL_CLICK,
                                                doubleClickHandler);
                    registerHandler(shape.getUUID(),
                                    doubleClickHandler);
                    // TODO: Not firing - Text over event.
                    final TextOverHandler overHandler = new TextOverHandler() {
                        @Override
                        public void handle(TextOverEvent event) {
                            canvasHandler.getCanvas().getView().setCursor(AbstractCanvas.Cursors.TEXT);
                        }
                    };
                    if (hasEventHandlers.supports(ViewEventType.TEXT_OVER) &&
                            hasEventHandlers.supports(ViewEventType.TEXT_OUT)) {
                        hasEventHandlers.addHandler(ViewEventType.TEXT_OVER,
                                                    overHandler);
                        registerHandler(shape.getUUID(),
                                        overHandler);
                        // TODO: Not firing - Text out event.
                        final TextOutHandler outHandler = new TextOutHandler() {
                            @Override
                            public void handle(TextOutEvent event) {
                                canvasHandler.getCanvas().getView().setCursor(AbstractCanvas.Cursors.AUTO);
                            }
                        };
                        hasEventHandlers.addHandler(ViewEventType.TEXT_OUT,
                                                    outHandler);
                        registerHandler(shape.getUUID(),
                                        outHandler);
                    }
                }
            }
        }
    }

    @Override
    public CanvasNameEditionControl<AbstractCanvasHandler, Element> show(final Element item,
                                                                         final double x,
                                                                         final double y) {
        this.uuid = item.getUUID();
        enableShapeEdit();
        nameEditBox.show(item);
        double[] size;
        try {
            size = GraphUtils.getSize((View) item.getContent());
        } catch (final ClassCastException e) {
            size = null;
        }
        final double rx = null != size ? size[0] / 2 : 0d;
        floatingView
                .setX(x - rx)
                .setY(y)
                .show();
        return this;
    }

    @Override
    public CanvasNameEditionControl<AbstractCanvasHandler, Element> hide() {
        if (isVisible()) {
            disableShapeEdit();
            this.uuid = null;
            nameEditBox.hide();
            floatingView.hide();
        }
        return this;
    }

    @Override
    protected void doDisable() {
        super.doDisable();
        disableShapeEdit();
        this.uuid = null;
        nameEditBox.hide();
        nameEditBox = null;
        floatingView.destroy();
        floatingView = null;
    }

    private boolean enableShapeEdit() {
        return setShapeEditMode(true);
    }

    private boolean disableShapeEdit() {
        return setShapeEditMode(false);
    }

    private boolean setShapeEditMode(final boolean editMode) {
        final Shape<?> shape = getShape(this.uuid);
        if (null != shape) {
            final HasTitle hasTitle = (HasTitle) shape.getShapeView();
            final double alpha = editMode ? SHAPE_EDIT_ALPH : 1d;
            shape.getShapeView().setFillAlpha(alpha);
            hasTitle.setTitleAlpha(alpha);
            getCanvas().draw();
            return true;
        }
        return false;
    }

    private Shape<?> getShape(final String uuid) {
        return null != uuid ? getCanvas().getShape(uuid) : null;
    }

    private boolean isVisible() {
        return null != this.uuid;
    }

    private Canvas getCanvas() {
        return canvasHandler.getCanvas();
    }

    void onKeyDownEvent(final @Observes KeyDownEvent keyDownEvent) {
        checkNotNull("keyDownEvent",
                     keyDownEvent);
        final KeyboardEvent.Key key = keyDownEvent.getKey();
        if (null != key && KeyboardEvent.Key.ESC.equals(key)) {
            hide();
        }
    }

    void onCanvasFocusedEvent(final @Observes CanvasFocusedEvent canvasFocusedEvent) {
        checkNotNull("canvasFocusedEvent",
                     canvasFocusedEvent);
        hide();
    }
}
