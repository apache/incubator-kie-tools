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


package org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor;

import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.HTMLElement;
import jakarta.enterprise.event.Observes;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.touch.client.Point;
import org.kie.j2cl.tools.di.core.IsElement;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasInlineTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl.KogitoKeyPress;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.NativeHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextDoubleClickHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextEnterEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextEnterHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextExitEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextExitHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

import static org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key.ESC;

public abstract class AbstractCanvasInlineTextEditorControl
        extends AbstractCanvasHandlerRegistrationControl<AbstractCanvasHandler>
        implements CanvasInlineTextEditorControl<AbstractCanvasHandler, EditorSession, Element> {

    public static final double SHAPE_EDIT_ALPHA = 0.2d;
    public static final double TITLE_EDIT_ALPHA = 0.0d;
    public static final double NOT_EDIT_ALPHA = 1.0d;

    private String uuid;
    private Point shapePosition;
    private Point shapeSize;
    private Point2D canvasPosition;
    private Point scrollBarsPosition;
    private double zoomFactor;
    private NativeHandler mouseWheelHandler;

    // Configurable parameters
    protected boolean isMultiline;
    protected double borderOffsetX;
    protected double borderOffsetY;
    protected double underBoxOffset;
    protected double topBorderOffset;
    protected double fontSizeCorrection;
    protected double maxInnerLeftBoxWidth;
    protected double maxInnerLeftBoxHeight;
    protected double maxInnerTopBoxWidth;
    protected double maxInnerTopBoxHeight;
    protected double scrollBarOffset;
    protected double paletteOffsetX;
    protected double innerBoxOffsetY;
    protected EditorSession session;
    private int EDIT_TEXTBOX_BOTTOM_PADDING = 10;

    private static final String MOUSE_WHEEL = "wheel";

    protected abstract FloatingView<IsElement> getFloatingView();

    protected abstract TextEditorBox<AbstractCanvasHandler, Element> getTextEditorBox();

    @Override
    public void bind(final EditorSession session) {
        session.getKeyboardControl().addKeyShortcutCallback(new KogitoKeyPress(new Key[]{ESC}, "Edit | Hide", this::hide));
        session.getKeyboardControl().addKeyShortcutCallback(this::onKeyDownEvent);
        this.session = session;
    }

    @Override
    protected void doInit() {
        super.doInit();
        getTextEditorBox().initialize(canvasHandler,
                                      () -> scheduleDeferredCommand(AbstractCanvasInlineTextEditorControl.this::hide));

        getFloatingView()
                .hide()
                .add(getTextEditorBox());

        // if the user tries to scroll the editor must be closed
        setMouseWheelHandler();
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
                        registerTextDoubleClick(shape, element, hasEventHandlers);
                    }

                    // Change mouse cursor, if shape supports it.
                    if (hasEventHandlers.supports(ViewEventType.TEXT_ENTER)) {
                        changeMouseCursorOnTextEnter(shape, hasEventHandlers);
                    }
                    if (hasEventHandlers.supports(ViewEventType.TEXT_EXIT)) {
                        changeMouseCursorOnTextExit(shape, hasEventHandlers);
                    }
                }
            }
        }
    }

    private void registerTextDoubleClick(final Shape<?> shape, final Element element, final HasEventHandlers hasEventHandlers) {
        final TextDoubleClickHandler clickHandler = new TextDoubleClickHandler() {
            @Override
            public void handle(final TextDoubleClickEvent event) {
                scheduleDeferredCommand(() -> AbstractCanvasInlineTextEditorControl.this.show(element));
            }
        };
        hasEventHandlers.addHandler(ViewEventType.TEXT_DBL_CLICK,
                                    clickHandler);
        registerHandler(shape.getUUID(),
                        clickHandler);
    }

    void onEnableInlineEdit(@Observes InlineTextEditEvent event) {
        final Element element = CanvasLayoutUtils.getElement(canvasHandler, event.getUuid());
        if (element != null) {
            AbstractCanvasInlineTextEditorControl.this.show(element);
        }
    }

    private void changeMouseCursorOnTextEnter(final Shape<?> shape, final HasEventHandlers hasEventHandlers) {
        final TextEnterHandler enterHandler = new TextEnterHandler() {
            @Override
            public void handle(TextEnterEvent event) {
                getAbstractCanvas().getView().setCursor(AbstractCanvas.Cursors.TEXT);
            }
        };
        hasEventHandlers.addHandler(ViewEventType.TEXT_ENTER,
                                    enterHandler);
        registerHandler(shape.getUUID(),
                        enterHandler);
    }

    private void changeMouseCursorOnTextExit(final Shape<?> shape, final HasEventHandlers hasEventHandlers) {
        final TextExitHandler exitHandler = new TextExitHandler() {
            @Override
            public void handle(TextExitEvent event) {
                getAbstractCanvas().getView().setCursor(AbstractCanvas.Cursors.DEFAULT);
            }
        };
        hasEventHandlers.addHandler(ViewEventType.TEXT_EXIT,
                                    exitHandler);
        registerHandler(shape.getUUID(),
                        exitHandler);
    }

    @Override
    public CanvasInlineTextEditorControl<AbstractCanvasHandler, EditorSession, Element> show(final Element item) {
        final double editorBoxWidth;
        final double editorBoxHeight;
        final double floatingViewPositionX;
        final double floatingViewPositionY;

        this.uuid = item.getUUID();

        shapePosition = getShapePosition();
        shapeSize = getShapeSize();
        canvasPosition = getCanvasAbsolutePosition();
        scrollBarsPosition = getScrollBarsPosition();
        zoomFactor = getZoomFactor();
        final HasTitle hasTitle = getHasTitle();

        if (hasTitle != null) {
            Point shapePosition = getInnerLeftBoxPosition();
            editorBoxWidth = zoomFactor * hasTitle.getTextboxWidth();
            editorBoxHeight = zoomFactor * (hasTitle.getTextboxHeight() - EDIT_TEXTBOX_BOTTOM_PADDING);
            floatingViewPositionX = (zoomFactor * (hasTitle.getTitlePosition().getX() - borderOffsetX)) + shapePosition.getX();
            floatingViewPositionY = (zoomFactor * (hasTitle.getTitlePosition().getY() - borderOffsetY)) + shapePosition.getY();
        } else {
            editorBoxWidth = 0;
            editorBoxHeight = 0;
            floatingViewPositionX = 0;
            floatingViewPositionY = 0;
        }

        // Do not show editBox if position is out of canvas bounds
        if (isPositionXValid(floatingViewPositionX)
                && isPositionYValid(floatingViewPositionY)) {
            enableShapeEdit();

            getTextEditorBox().setFontFamily(hasTitle.getTitleFontFamily());
            getTextEditorBox().setFontSize(ptToPx(hasTitle.getTitleFontSize() * zoomFactor));
            getTextEditorBox().setMultiline(isMultiline);
            getFloatingView().setX(floatingViewPositionX);
            getFloatingView().setY(floatingViewPositionY);
            getTextEditorBox().setTextBoxInternalAlignment("LEFT");
            getFloatingView().clearTimeOut();

            getTextEditorBox().show(item,
                                    fixBoundaryX(editorBoxWidth, floatingViewPositionX),
                                    fixBoundaryY(editorBoxHeight, floatingViewPositionY));
            getFloatingView().show();
        }

        return this;
    }

    private double ptToPx(double value) {
        return value * 4 / 3;
    }

    private boolean isPositionXValid(final double floatingViewPositionX) {
        boolean isValid = (canvasPosition.getX() + paletteOffsetX) <= floatingViewPositionX;
        if (DomGlobal.document != null) {
            final elemental2.dom.Element editorPanel = DomGlobal.document.getElementById("canvasPanel");
            final double canvasWidth = editorPanel.clientWidth;
            isValid = isValid && (canvasPosition.getX() + paletteOffsetX + canvasWidth + scrollBarOffset >= floatingViewPositionX);
        }

        return isValid;
    }

    private boolean isPositionYValid(final double floatingViewPositionY) {
        return !(floatingViewPositionY > getCanvasAbsoluteHeight());
    }

    private double fixBoundaryX(final double editorBoxWidth,
                                final double floatingViewPositionX) {
        if ((editorBoxWidth + floatingViewPositionX) > getCanvasAbsoluteWidth()) {
            return editorBoxWidth - ((editorBoxWidth + floatingViewPositionX) - getCanvasAbsoluteWidth());
        } else if (canvasPosition.getX() + paletteOffsetX > floatingViewPositionX) {
            return editorBoxWidth - paletteOffsetX - (canvasPosition.getX() - floatingViewPositionX);
        } else {
            return editorBoxWidth;
        }
    }

    private double fixBoundaryY(final double editorBoxHeight,
                                final double floatingViewPositionY) {
        if ((editorBoxHeight + floatingViewPositionY) > getCanvasAbsoluteHeight()) {
            return editorBoxHeight - ((editorBoxHeight + floatingViewPositionY) - getCanvasAbsoluteHeight());
        } else {
            return editorBoxHeight;
        }
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
        uuid = null;

        mouseWheelHandler.removeHandler();
        mouseWheelHandler = null;
    }

    private void enableShapeEdit() {
        setShapeEditMode(true);
    }

    private void disableShapeEdit() {
        setShapeEditMode(false);
    }

    private double getInnerLeftBoxHeight() {
        if (shapeSize.getY() > maxInnerLeftBoxHeight) {
            return maxInnerLeftBoxHeight * zoomFactor;
        } else {
            return shapeSize.getY() * zoomFactor;
        }
    }

    private Point getInnerLeftBoxPosition() {
        final double offsetY = ((shapeSize.getY() * zoomFactor) - getInnerLeftBoxHeight()) / 2d;

        final double x = ((shapePosition.getX() + borderOffsetX) * zoomFactor) +
                canvasPosition.getX() +
                scrollBarsPosition.getX();
        final double y = (shapePosition.getY() * zoomFactor) +
                offsetY +
                canvasPosition.getY() +
                scrollBarsPosition.getY();

        return new Point(x, y);
    }

    private double getZoomFactor() {
        return getCanvas().getTransform().getScale().getX();
    }

    private Point getScrollBarsPosition() {
        final double scrollX = getCanvas().getTransform().getTranslate().getX();
        final double scrollY = getCanvas().getTransform().getTranslate().getY();
        return new Point(scrollX, scrollY);
    }

    private Point2D getCanvasAbsolutePosition() {
        return getAbstractCanvas().getView().getAbsoluteLocation();
    }

    private Point getShapeSize() {
        final Shape<?> shape = getShape(uuid);
        double width = 0;
        double height = 0;

        if (null != shape) {
            width = shape.getShapeView().getBoundingBox().getWidth();
            height = shape.getShapeView().getBoundingBox().getHeight();
        }

        return new Point(width, height);
    }

    private Point getShapePosition() {
        final Shape<?> shape = getShape(uuid);
        int x = 0;
        int y = 0;

        if (null != shape) {
            x = (int) shape.getShapeView().getShapeAbsoluteLocation().getX();
            y = (int) shape.getShapeView().getShapeAbsoluteLocation().getY();
        }

        return new Point(x, y);
    }

    double getCanvasAbsoluteWidth() {
        return getAbstractCanvas().getView().getPanel().getElement().offsetWidth +
                canvasPosition.getX() -
                scrollBarOffset;
    }

    double getCanvasAbsoluteHeight() {
        return getAbstractCanvas().getView().getPanel().getElement().offsetHeight +
                canvasPosition.getY() -
                scrollBarOffset;
    }

    private boolean setShapeEditMode(final boolean editMode) {
        final Shape<?> shape = getShape(this.uuid);
        if (null != shape) {
            final HasTitle hasTitle = (HasTitle) shape.getShapeView();
            final double alpha = editMode ? SHAPE_EDIT_ALPHA : NOT_EDIT_ALPHA;
            final double titleAlpha = editMode ? TITLE_EDIT_ALPHA : NOT_EDIT_ALPHA;
            shape.getShapeView().setFillAlpha(alpha);
            hasTitle.setTitleAlpha(titleAlpha);
            hasTitle.batch();
            return true;
        }
        return false;
    }

    private Shape<?> getShape(final String uuid) {
        return null != uuid ? getCanvas().getShape(uuid) : null;
    }

    HasTitle getHasTitle() {
        Shape<?> shape = getShape(this.uuid);
        return null != shape ? (HasTitle) shape.getShapeView() : null;
    }

    private boolean isVisible() {
        return null != this.uuid;
    }

    private Canvas getCanvas() {
        return canvasHandler.getCanvas();
    }

    AbstractCanvas getAbstractCanvas() {
        return canvasHandler.getAbstractCanvas();
    }

    void onKeyDownEvent(final Key... keys) {
        if (KeysMatcher.doKeysMatch(keys,
                                    Key.ESC)) {
            rollback();
        }
    }

    void setMouseWheelHandler() {
        HTMLElement panelElement = getAbstractCanvas()
                                       .getView()
                                       .getPanel()
                                       .getElement();

        mouseWheelHandler = new NativeHandler(MOUSE_WHEEL,
                                              this::onMouseWheel,
                                              panelElement).add();
    }

    void onMouseWheel(final Event event) {
        if (event.type.equals(MOUSE_WHEEL)) {
            rollback();
        }
    }

    @Override
    public CanvasInlineTextEditorControl<AbstractCanvasHandler, EditorSession, Element> rollback() {
        if (isVisible()) {
            getTextEditorBox().rollback();
        }
        return this;
    }

    @Override
    public CanvasInlineTextEditorControl<AbstractCanvasHandler, EditorSession, Element> hide() {
        if (isVisible()) {
            disableShapeEdit();
            uuid = null;
            getTextEditorBox().hide();
            getFloatingView().hide();
        }
        return this;
    }

    public void scheduleDeferredCommand(final Scheduler.ScheduledCommand command) {
        Scheduler.get().scheduleDeferred(command);
    }
}
