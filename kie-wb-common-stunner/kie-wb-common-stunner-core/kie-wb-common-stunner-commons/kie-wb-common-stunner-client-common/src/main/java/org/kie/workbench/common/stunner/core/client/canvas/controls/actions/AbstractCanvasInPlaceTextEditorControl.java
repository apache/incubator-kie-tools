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

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
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
import org.uberfire.mvp.Command;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public abstract class AbstractCanvasInPlaceTextEditorControl
        extends AbstractCanvasHandlerRegistrationControl<AbstractCanvasHandler>
        implements CanvasInPlaceTextEditorControl<AbstractCanvasHandler, EditorSession, Element> {

    static final int FLOATING_VIEW_TIMEOUT = 3000;
    static final double SHAPE_EDIT_ALPHA = 0.2d;
    static final double SHAPE_NOT_EDIT_ALPHA = 1.0d;

    private String uuid;

    private final Command hideFloatingViewOnTimeoutCommand = this::flush;

    protected abstract FloatingView<IsWidget> getFloatingView();

    protected abstract TextEditorBox<AbstractCanvasHandler, Element> getTextEditorBox();

    protected abstract Event<CanvasSelectionEvent> getCanvasSelectionEvent();

    @Override
    public void bind(final EditorSession session) {
        session.getKeyboardControl().addKeyShortcutCallback(this::onKeyDownEvent);
    }

    @Override
    protected void doInit() {
        super.doInit();
        getTextEditorBox().initialize(canvasHandler,
                                      () -> {
                                          final String idToSelect = AbstractCanvasInPlaceTextEditorControl.this.uuid;
                                          AbstractCanvasInPlaceTextEditorControl.this.hide();
                                          getCanvasSelectionEvent().fire(new CanvasSelectionEvent(canvasHandler,
                                                                                                  idToSelect));
                                      });

        getFloatingView()
                .hide()
                .setHideCallback(hideFloatingViewOnTimeoutCommand)
                .setTimeOut(FLOATING_VIEW_TIMEOUT)
                .add(wrapTextEditorBoxElement(getTextEditorBox().getElement()));
    }

    protected IsWidget wrapTextEditorBoxElement(final HTMLElement element) {
        return ElementWrapperWidget.getWidget(element);
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
                                AbstractCanvasInPlaceTextEditorControl.this.show(element,
                                                                                 event.getClientX(),
                                                                                 event.getClientY());
                            }
                        };
                        hasEventHandlers.addHandler(ViewEventType.TEXT_DBL_CLICK,
                                                    clickHandler);
                        registerHandler(shape.getUUID(),
                                        clickHandler);
                    }

                    // Change mouse cursor, if shape supports it.
                    if (hasEventHandlers.supports(ViewEventType.TEXT_ENTER)) {
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
                    }
                    if (hasEventHandlers.supports(ViewEventType.TEXT_EXIT)) {
                        final TextExitHandler exitHandler = new TextExitHandler() {
                            @Override
                            public void handle(TextExitEvent event) {
                                canvasHandler.getAbstractCanvas().getView().setCursor(AbstractCanvas.Cursors.DEFAULT);
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

    @Override
    public CanvasInPlaceTextEditorControl<AbstractCanvasHandler, EditorSession, Element> show(final Element item,
                                                                                              final double x,
                                                                                              final double y) {
        if (getTextEditorBox().isVisible()) {
            flush();
        }
        this.uuid = item.getUUID();
        enableShapeEdit();
        getTextEditorBox().show(item);
        final double offsetX = getTextEditorBox().getDisplayOffsetX();
        final double offsetY = getTextEditorBox().getDisplayOffsetY();
        getFloatingView()
                .setX(x)
                .setY(y)
                .setOffsetX(-offsetX)
                .setOffsetY(-offsetY)
                .show();
        return this;
    }

    @Override
    public CanvasInPlaceTextEditorControl<AbstractCanvasHandler, EditorSession, Element> hide() {
        if (isVisible()) {
            disableShapeEdit();
            this.uuid = null;
            getTextEditorBox().hide();
            getFloatingView().hide();
        }
        return this;
    }

    @Override
    public void setCommandManagerProvider(final CommandManagerProvider<AbstractCanvasHandler> provider) {
        getTextEditorBox().setCommandManagerProvider(provider);
    }

    @Override
    protected void doDestroy() {
        super.doDestroy();
        getTextEditorBox().hide();
        disableShapeEdit();
        getFloatingView().destroy();
        this.uuid = null;
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
            final double alpha = editMode ? SHAPE_EDIT_ALPHA : SHAPE_NOT_EDIT_ALPHA;
            shape.getShapeView().setFillAlpha(alpha);
            hasTitle.setTitleAlpha(alpha);
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

    void onCanvasClearSelectionEvent(final @Observes CanvasClearSelectionEvent event) {
        checkNotNull("event",
                     event);
        flush();
    }

    void onCanvasShapeRemovedEvent(final @Observes CanvasShapeRemovedEvent event) {
        checkNotNull("event",
                     event);
        flush();
    }

    void onCanvasFocusedEvent(final @Observes CanvasFocusedEvent canvasFocusedEvent) {
        checkNotNull("canvasFocusedEvent",
                     canvasFocusedEvent);
        flush();
    }

    void onCanvasSelectionEvent(final @Observes CanvasSelectionEvent event) {
        checkNotNull("event",
                     event);
        flush();
    }

    void flush() {
        getTextEditorBox().flush();
        hide();
    }
}
