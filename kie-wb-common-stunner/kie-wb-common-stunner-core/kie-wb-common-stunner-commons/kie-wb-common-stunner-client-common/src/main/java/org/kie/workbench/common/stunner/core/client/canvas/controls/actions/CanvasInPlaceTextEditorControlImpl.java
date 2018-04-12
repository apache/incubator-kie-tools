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

package org.kie.workbench.common.stunner.core.client.canvas.controls.actions;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.components.actions.TextEditorBox;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextDoubleClickHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextEnterEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextEnterHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextExitEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextExitHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.uberfire.mvp.Command;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class CanvasInPlaceTextEditorControlImpl
        extends AbstractCanvasHandlerRegistrationControl<AbstractCanvasHandler>
        implements CanvasInPlaceTextEditorControl<AbstractCanvasHandler, AbstractClientFullSession, Element> {

    private static final int FLOATING_VIEW_TIMEOUT = 3000;
    private static final double SHAPE_EDIT_ALPHA = 0.2d;

    private final FloatingView<IsWidget> floatingView;
    private final TextEditorBox<AbstractCanvasHandler, Element> textEditorBox;
    private final Event<CanvasSelectionEvent> canvasSelectionEvent;
    private String uuid;

    private final Command floatingHideCallback = CanvasInPlaceTextEditorControlImpl.this::hide;

    @Inject
    public CanvasInPlaceTextEditorControlImpl(final FloatingView<IsWidget> floatingView,
                                              final TextEditorBox<AbstractCanvasHandler, Element> textEditorBox,
                                              final Event<CanvasSelectionEvent> canvasSelectionEvent) {
        this.floatingView = floatingView;
        this.textEditorBox = textEditorBox;
        this.canvasSelectionEvent = canvasSelectionEvent;
        this.uuid = null;
    }

    @Override
    public void bind(final AbstractClientFullSession session) {
        session.getKeyboardControl().addKeyShortcutCallback(this::onKeyDownEvent);
    }

    @Override
    public void unbind() {
        //Nothing to do
    }

    @Override
    public void enable(final AbstractCanvasHandler canvasHandler) {
        super.enable(canvasHandler);
        textEditorBox.initialize(canvasHandler,
                                 () -> {
                                     final String idToSelect = CanvasInPlaceTextEditorControlImpl.this.uuid;
                                     CanvasInPlaceTextEditorControlImpl.this.hide();
                                     canvasSelectionEvent.fire(new CanvasSelectionEvent(canvasHandler,
                                                                                        idToSelect));
                                 });

        // TODO: move floatingView to support IsElement instead of IsWidget
        floatingView
                .hide()
                .setHideCallback(floatingHideCallback)
                .setTimeOut(FLOATING_VIEW_TIMEOUT)
                .add(ElementWrapperWidget.getWidget(textEditorBox.getElement()));
    }

    @Override
    public void register(final Element element) {
        if (checkNotRegistered(element)) {
            final Shape<?> shape = getShape(element.getUUID());
            if (null != shape) {
                final ShapeView shapeView = shape.getShapeView();
                if (shapeView instanceof HasEventHandlers) {
                    final HasEventHandlers hasEventHandlers = (HasEventHandlers) shapeView;
                    if (hasEventHandlers.supports(ViewEventType.TEXT_DBL_CLICK)) {
                        final TextDoubleClickHandler clickHandler = new TextDoubleClickHandler() {
                            @Override
                            public void handle(final TextDoubleClickEvent event) {
                                CanvasInPlaceTextEditorControlImpl.this.show(element,
                                                                             event.getClientX(),
                                                                             event.getClientY());
                            }
                        };
                        hasEventHandlers.addHandler(ViewEventType.TEXT_DBL_CLICK,
                                                    clickHandler);
                        registerHandler(shape.getUUID(),
                                        clickHandler);
                        // Change mouse cursor, if shape supports it.
                        if (hasEventHandlers.supports(ViewEventType.TEXT_ENTER) &&
                                hasEventHandlers.supports(ViewEventType.TEXT_EXIT)) {
                            final TextEnterHandler enterHandler = new TextEnterHandler() {
                                @Override
                                public void handle(TextEnterEvent event) {
                                    canvasHandler.getAbstractCanvas().getView().setCursor(AbstractCanvas.Cursors.TEXT);
                                }
                            };
                            hasEventHandlers.addHandler(ViewEventType.TEXT_ENTER,
                                                        enterHandler);
                            registerHandler(shape.getUUID(),
                                            enterHandler);
                            final TextExitHandler exitHandler = new TextExitHandler() {
                                @Override
                                public void handle(TextExitEvent event) {
                                    canvasHandler.getAbstractCanvas().getView().setCursor(AbstractCanvas.Cursors.AUTO);
                                }
                            };
                            hasEventHandlers.addHandler(ViewEventType.TEXT_EXIT,
                                                        exitHandler);
                            registerHandler(shape.getUUID(),
                                            exitHandler);
                        }
                    }
                }
            }
        }
    }

    @Override
    public CanvasInPlaceTextEditorControl<AbstractCanvasHandler, AbstractClientFullSession, Element> show(final Element item,
                                                                                                          final double x,
                                                                                                          final double y) {
        this.uuid = item.getUUID();
        enableShapeEdit();
        textEditorBox.show(item);
        double[] size;
        try {
            size = GraphUtils.getNodeSize((View) item.getContent());
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
    public CanvasInPlaceTextEditorControl<AbstractCanvasHandler, AbstractClientFullSession, Element> hide() {
        if (isVisible()) {
            disableShapeEdit();
            this.uuid = null;
            textEditorBox.hide();
            floatingView.hide();
        }
        return this;
    }

    @Override
    public void setCommandManagerProvider(final CommandManagerProvider<AbstractCanvasHandler> provider) {
        textEditorBox.setCommandManagerProvider(provider);
    }

    @Override
    protected void doDisable() {
        super.doDisable();
        disableShapeEdit();
        this.uuid = null;
        textEditorBox.hide();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        floatingView.destroy();
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
            final double alpha = editMode ? SHAPE_EDIT_ALPHA : 1d;
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

    void onKeyDownEvent(final KeyboardEvent.Key... keys) {
        if (KeysMatcher.doKeysMatch(keys,
                                    KeyboardEvent.Key.ESC)) {
            hide();
        }
    }

    void onCanvasFocusedEvent(final @Observes CanvasFocusedEvent canvasFocusedEvent) {
        checkNotNull("canvasFocusedEvent",
                     canvasFocusedEvent);
        hide();
    }
}
