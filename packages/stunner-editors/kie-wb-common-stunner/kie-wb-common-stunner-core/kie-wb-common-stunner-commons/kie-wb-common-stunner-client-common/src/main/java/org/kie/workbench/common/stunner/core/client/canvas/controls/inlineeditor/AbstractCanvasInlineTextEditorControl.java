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

import javax.enterprise.event.Observes;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.DomGlobal;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly;
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
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextDoubleClickHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextEnterEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextEnterHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextExitEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextExitHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key.ESC;

public abstract class AbstractCanvasInlineTextEditorControl
        extends AbstractCanvasHandlerRegistrationControl<AbstractCanvasHandler>
        implements CanvasInlineTextEditorControl<AbstractCanvasHandler, EditorSession, Element> {

    public static final double SHAPE_EDIT_ALPHA = 0.2d;
    public static final double TITLE_EDIT_ALPHA = 0.0d;
    public static final double NOT_EDIT_ALPHA = 1.0d;
    public static final String ALIGN_MIDDLE = "MIDDLE";
    public static final String ALIGN_LEFT = "LEFT";
    public static final String ALIGN_TOP = "TOP";
    public static final String POSITION_INSIDE = "INSIDE";
    public static final String POSITION_OUTSIDE = "OUTSIDE";
    public static final String ORIENTATION_VERTICAL = "VERTICAL";
    public static final String ORIENTATION_HORIZONTAL = "HORIZONTAL";
    public static final double DEFAULT_MARGIN_X = 0d;
    public static final double DEFAULT_FONT_SIZE = 14d;
    public static final String DEFAULT_FONT_FAMILY = "Open Sans";

    private String uuid;
    private Point shapePosition;
    private Point shapeSize;
    private Point2D canvasPosition;
    private Point scrollBarsPosition;
    private double zoomFactor;
    private BoxType boxType;
    private HandlerRegistration mouseWheelHandler;
    private String fontFamily;
    private double fontSize;
    private double marginX;

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

    protected abstract FloatingView<IsWidget> getFloatingView();

    protected abstract TextEditorBox<AbstractCanvasHandler, Element> getTextEditorBox();

    private enum BoxType {
        OUTSIDE,
        INSIDE_MIDDLE,
        INSIDE_LEFT,
        INSIDE_TOP
    }

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
                .add(wrapTextEditorBoxElement(getTextEditorBox().getElement()));

        // if the user tries to scroll the editor must be closed
        setMouseWheelHandler();
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
                if (isEditableForDoubleClick(element)) {
                    scheduleDeferredCommand(() -> AbstractCanvasInlineTextEditorControl.this.show(element));
                }
            }
        };
        hasEventHandlers.addHandler(ViewEventType.TEXT_DBL_CLICK,
                                    clickHandler);
        registerHandler(shape.getUUID(),
                        clickHandler);
    }

    void onEnableInlineEdit(@Observes InlineTextEditEvent event) {
        final Element element = CanvasLayoutUtils.getElement(canvasHandler, event.getUuid());
        if (element != null && isEditable(element)) {
            AbstractCanvasInlineTextEditorControl.this.show(element);
        }
    }

    protected boolean isEditableForDoubleClick(Element element) {
        return true;
    }

    protected boolean isEditable(Element element) {
        Node<View<?>, Edge> sourceNode = (Node<View<?>, Edge>) element;
        final Object sourceDefinition = null != sourceNode ? sourceNode.getContent().getDefinition() : null;
        final boolean isEditable = isFiltered(sourceDefinition);
        return isEditable;
    }

    public boolean isFiltered(final Object bean) {
        return true;
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

    boolean allowOnlyVisualChanges(final Element element) {

        if (element.getContent() instanceof Definition) {
            final Definition definition = (Definition) element.getContent();
            if (definition.getDefinition() instanceof DynamicReadOnly) {
                return ((DynamicReadOnly) definition.getDefinition()).isAllowOnlyVisualChange();
            }
        }
        return false;
    }

    @Override
    public CanvasInlineTextEditorControl<AbstractCanvasHandler, EditorSession, Element> show(final Element item) {
        final double editorBoxWidth;
        final double editorBoxHeight;
        final String editorBoxAlign;
        final double floatingViewPositionX;
        final double floatingViewPositionY;

        setInlineBoxContext(item.getUUID());

        if (boxType == BoxType.INSIDE_LEFT) {
            editorBoxWidth = getInnerLeftBoxWidth();
            editorBoxHeight = getInnerLeftBoxHeight();
            editorBoxAlign = ALIGN_LEFT;
            floatingViewPositionX = getInnerLeftBoxPosition().getX();
            floatingViewPositionY = getInnerLeftBoxPosition().getY();
        } else if (boxType == BoxType.INSIDE_TOP) {
            editorBoxWidth = getInnerTopBoxWidth();
            editorBoxHeight = getInnerTopBoxHeight();
            editorBoxAlign = ALIGN_TOP;
            floatingViewPositionX = getInnerTopBoxPosition().getX();
            floatingViewPositionY = getInnerTopBoxPosition().getY();
        } else if (boxType == BoxType.OUTSIDE) {
            editorBoxWidth = getUnderBoxWidth();
            editorBoxHeight = getUnderBoxHeight();
            editorBoxAlign = ALIGN_TOP;
            floatingViewPositionX = getUnderBoxPosition().getX();
            floatingViewPositionY = getUnderBoxPosition().getY();
        } else {
            editorBoxWidth = getInnerBoxWidth();
            editorBoxHeight = getInnerBoxHeight();
            editorBoxAlign = ALIGN_MIDDLE;
            floatingViewPositionX = getInnerBoxPosition().getX();
            floatingViewPositionY = getInnerBoxPosition().getY();
        }

        // Do not show editBox if position is out of canvas bounds
        if (isPositionXValid(floatingViewPositionX)
                && isPositionYValid(floatingViewPositionY)) {
            enableShapeEdit();

            getTextEditorBox().setFontFamily(fontFamily);
            getTextEditorBox().setFontSize((fontSize + fontSizeCorrection) * zoomFactor);
            getTextEditorBox().setTextBoxInternalAlignment(editorBoxAlign);
            getTextEditorBox().setMultiline(isMultiline);
            getFloatingView().setX(floatingViewPositionX);
            getFloatingView().setY(floatingViewPositionY);
            getFloatingView().clearTimeOut();

            getTextEditorBox().show(item,
                                    fixBoundaryX(editorBoxWidth, floatingViewPositionX),
                                    fixBoundaryY(editorBoxHeight, floatingViewPositionY));
            getFloatingView().show();
        }

        return this;
    }

    private boolean isPositionXValid(final double floatingViewPositionX) {
        final boolean atLeft = (canvasPosition.getX() + paletteOffsetX <= floatingViewPositionX);
        boolean isValid = atLeft;
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
    }

    private void enableShapeEdit() {
        setShapeEditMode(true);
    }

    private void disableShapeEdit() {
        setShapeEditMode(false);
    }

    private void setInlineBoxContext(final String uuid) {
        this.uuid = uuid;
        final String titlePosition;
        final String orientation;
        final String fontAlignment;
        final HasTitle hasTitle = getHasTitle();

        if (null != hasTitle) {
            titlePosition = hasTitle.getTitlePosition();
            orientation = hasTitle.getOrientation();
            fontAlignment = hasTitle.getFontAlignment();
            marginX = hasTitle.getMarginX();
            fontSize = hasTitle.getTitleFontSize();
            fontFamily = hasTitle.getTitleFontFamily();
        } else {
            titlePosition = POSITION_INSIDE;
            orientation = ORIENTATION_HORIZONTAL;
            fontAlignment = ALIGN_MIDDLE;
            marginX = DEFAULT_MARGIN_X;
            fontSize = DEFAULT_FONT_SIZE;
            fontFamily = DEFAULT_FONT_FAMILY;
        }

        shapePosition = getShapePosition();
        shapeSize = getShapeSize();
        canvasPosition = getCanvasAbsolutePosition();
        scrollBarsPosition = getScrollBarsPosition();
        zoomFactor = getZoomFactor();

        // Find out BoxType
        switch (titlePosition) {
            case POSITION_INSIDE:
                if (orientation.equals(ORIENTATION_HORIZONTAL) && fontAlignment.equals(ALIGN_MIDDLE)) {
                    boxType = BoxType.INSIDE_MIDDLE;
                } else if (orientation.equals(ORIENTATION_HORIZONTAL) && fontAlignment.equals(ALIGN_TOP)) {
                    boxType = BoxType.INSIDE_TOP;
                } else if (orientation.equals(ORIENTATION_VERTICAL)) {
                    boxType = BoxType.INSIDE_LEFT;
                }
                break;
            case POSITION_OUTSIDE:
                boxType = BoxType.OUTSIDE;
                break;
            default:
                boxType = BoxType.INSIDE_MIDDLE;
                break;
        }
    }

    private double getUnderBoxWidth() {
        return shapeSize.getX() * 2d * zoomFactor;
    }

    private double getUnderBoxHeight() {
        return shapeSize.getY() * zoomFactor;
    }

    private Point getUnderBoxPosition() {
        final double x = ((shapePosition.getX() - (shapeSize.getX() / 2d)) * zoomFactor) +
                canvasPosition.getX() +
                scrollBarsPosition.getX();
        final double y = ((shapePosition.getY() + shapeSize.getY() + underBoxOffset) * zoomFactor) +
                canvasPosition.getY() +
                scrollBarsPosition.getY();

        return new Point(x, y);
    }

    private double getInnerBoxWidth() {
        return (shapeSize.getX() - marginX - borderOffsetX * 2d) * zoomFactor;
    }

    private double getInnerBoxHeight() {
        return (shapeSize.getY() - borderOffsetY * 2d) * zoomFactor;
    }

    private Point getInnerBoxPosition() {
        final double x = ((shapePosition.getX() + borderOffsetX + marginX) * zoomFactor) +
                canvasPosition.getX() +
                scrollBarsPosition.getX();
        final double y = ((shapePosition.getY() + borderOffsetY + innerBoxOffsetY) * zoomFactor) +
                canvasPosition.getY() +
                scrollBarsPosition.getY();

        return new Point(x, y);
    }

    private double getInnerLeftBoxWidth() {
        if (shapeSize.getX() > maxInnerLeftBoxWidth) {
            return maxInnerLeftBoxWidth * zoomFactor;
        } else {
            return shapeSize.getX() * zoomFactor;
        }
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

    private double getInnerTopBoxWidth() {
        if (shapeSize.getX() > maxInnerTopBoxWidth) {
            return maxInnerTopBoxWidth * zoomFactor;
        } else {
            return (shapeSize.getX() - borderOffsetX * 2d) * zoomFactor;
        }
    }

    private double getInnerTopBoxHeight() {
        if (shapeSize.getY() > maxInnerTopBoxHeight) {
            return maxInnerTopBoxHeight * zoomFactor;
        } else {
            return (shapeSize.getY() - topBorderOffset) * zoomFactor;
        }
    }

    private Point getInnerTopBoxPosition() {
        final double x;

        if (shapeSize.getX() > maxInnerTopBoxWidth) {
            x = ((shapePosition.getX() + marginX + ((shapeSize.getX() - maxInnerTopBoxWidth) / 2d)) * zoomFactor) +
                    canvasPosition.getX() +
                    scrollBarsPosition.getX();
        } else {
            x = ((shapePosition.getX() + borderOffsetX + marginX) * zoomFactor) +
                    canvasPosition.getX() +
                    scrollBarsPosition.getX();
        }

        final double y = ((shapePosition.getY() + topBorderOffset) * zoomFactor) +
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
        return getAbstractCanvas().getView().getPanel().asWidget().getOffsetWidth() +
                canvasPosition.getX() -
                scrollBarOffset;
    }

    double getCanvasAbsoluteHeight() {
        return getAbstractCanvas().getView().getPanel().asWidget().getOffsetHeight() +
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
        mouseWheelHandler = getAbstractCanvas()
                .getView()
                .getPanel()
                .asWidget()
                .addDomHandler(this::onMouseWheel,
                               MouseWheelEvent.getType());
    }

    void onMouseWheel(final MouseWheelEvent event) {
        rollback();
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
