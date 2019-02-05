/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext;

import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.ITextWrapper;
import com.ait.lienzo.client.core.shape.ITextWrapperWithBoundaries;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.TextBoundsWrap;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.google.gwt.event.shared.HandlerRegistration;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.ViewEventHandlerManager;
import org.kie.workbench.common.stunner.core.client.shape.TextWrapperStrategy;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextEnterEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextExitEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;

/**
 * A helper class for handling the wires shapes' text primitive
 * that is used to display the shape's name.
 * <p>
 * It handles common logic for ShapeViews that implement <code>HasText</code>
 * type, can be reused for shapes or connectors.
 * <p>
 */
public class WiresTextDecorator {

    // Default text attribute values.
    private static final double TEXT_ALPHA = 1d;
    private static final String TEXT_FONT_FAMILY = "Verdana";
    private static final double TEXT_FONT_SIZE = 10d;
    private static final String TEXT_FILL_COLOR = "#000000";
    private static final String TEXT_STROKE_COLOR = "#000000";
    private static final double TEXT_STROKE_WIDTH = 0.5d;
    private static final TextAlign TEXT_ALIGN = TextAlign.CENTER;
    private static final LayoutContainer.Layout TEXT_LAYOUT_ALIGN = LayoutContainer.Layout.CENTER;

    private final Supplier<ViewEventHandlerManager> eventHandlerManager;
    private final Group textContainer = new Group();
    private ViewHandler<TextEnterEvent> textOverHandlerViewHandler;
    private ViewHandler<TextExitEvent> textOutEventViewHandler;
    private ViewHandler<TextClickEvent> textClickEventViewHandler;
    private ViewHandler<TextDoubleClickEvent> textDblClickEventViewHandler;
    private Text text;
    private ITextWrapper textWrapper;
    private LayoutContainer.Layout currentTextLayout;
    private double width;
    private double height;

    // WiresLayoutContainer lays out content based on the BoundingBox of the content. Therefore, even when the Text
    // has its Location set to non-zero co-ordinates its BoundingBox does not change. This (almost invisible) Line is
    // used to ensure the BoundingBox represents the positioning of the WiresTextDecorator
    private Line textSpacer = new Line().setAlpha(0.1).setListening(false);

    public WiresTextDecorator(final Supplier<ViewEventHandlerManager> eventHandlerManager,
                              final BoundingBox boundingBox) {
        this.eventHandlerManager = eventHandlerManager;
        initialize(boundingBox);
    }

    public void setTextClickHandler(final ViewHandler<TextClickEvent> textClickEventViewHandler) {
        this.textClickEventViewHandler = textClickEventViewHandler;
    }

    public void setTextDblClickHandler(final ViewHandler<TextDoubleClickEvent> textDblClickEventViewHandler) {
        this.textDblClickEventViewHandler = textDblClickEventViewHandler;
    }

    public void setTextEnterHandler(final ViewHandler<TextEnterEvent> textOverHandlerViewHandler) {
        this.textOverHandlerViewHandler = textOverHandlerViewHandler;
    }

    public void setTextExitHandler(final ViewHandler<TextExitEvent> textOutEventViewHandler) {
        this.textOutEventViewHandler = textOutEventViewHandler;
    }

    private void initialize(final BoundingBox boundingBox) {
        this.text = new Text("")
                .setAlpha(TEXT_ALPHA)
                .setFontFamily(TEXT_FONT_FAMILY)
                .setFontSize(TEXT_FONT_SIZE)
                .setFillColor(TEXT_FILL_COLOR)
                .setStrokeColor(TEXT_STROKE_COLOR)
                .setStrokeWidth(TEXT_STROKE_WIDTH)
                .setTextAlign(TEXT_ALIGN)
                .setDraggable(false);
        this.textWrapper = new TextBoundsWrap(text,
                                              new BoundingBox(0,
                                                              0,
                                                              1,
                                                              1));
        this.text.setWrapper(textWrapper);
        this.currentTextLayout = TEXT_LAYOUT_ALIGN;
        textContainer.add(text);
        textContainer.add(textSpacer);
        // Ensure path bounds are available on the selection context.
        text.setFillBoundsForSelection(true);
        initializeHandlers();
        resize(boundingBox.getWidth(), boundingBox.getHeight());
    }

    private void initializeHandlers() {
        registerTextEnterHandler();
        registerTextExitHandler();
        registerClickHandler();
        registerDoubleClickHandler();
    }

    private void registerClickHandler() {
        HandlerRegistration registration = text.addNodeMouseClickHandler(event -> {
            if (null != textClickEventViewHandler) {
                eventHandlerManager.get().skipClickHandler();
                final TextClickEvent e = new TextClickEvent(event.getX(),
                                                            event.getY(),
                                                            event.getMouseEvent().getClientX(),
                                                            event.getMouseEvent().getClientY());
                textClickEventViewHandler.handle(e);
                eventHandlerManager.get().restoreClickHandler();
            }
        });
        eventHandlerManager.get().addHandlersRegistration(ViewEventType.TEXT_CLICK,
                                                          registration);
    }

    private void registerDoubleClickHandler() {
        HandlerRegistration registration = text.addNodeMouseDoubleClickHandler(event -> {
            if (null != textDblClickEventViewHandler) {
                eventHandlerManager.get().skipClickHandler();
                final TextDoubleClickEvent e = new TextDoubleClickEvent(event.getX(),
                                                                        event.getY(),
                                                                        event.getMouseEvent().getClientX(),
                                                                        event.getMouseEvent().getClientY());
                textDblClickEventViewHandler.handle(e);
                eventHandlerManager.get().restoreClickHandler();
            }
        });
        eventHandlerManager.get().addHandlersRegistration(ViewEventType.TEXT_DBL_CLICK,
                                                          registration);
    }

    private void registerTextEnterHandler() {
        HandlerRegistration registration = text.addNodeMouseEnterHandler(event -> {
            if (null != textOverHandlerViewHandler && hasText()) {
                final TextEnterEvent textOverEvent = new TextEnterEvent(event.getX(),
                                                                        event.getY(),
                                                                        event.getMouseEvent().getClientX(),
                                                                        event.getMouseEvent().getClientY());
                textOverHandlerViewHandler.handle(textOverEvent);
            }
        });
        eventHandlerManager.get().addHandlersRegistration(ViewEventType.TEXT_ENTER,
                                                          registration);
    }

    private void registerTextExitHandler() {
        HandlerRegistration registration = text.addNodeMouseExitHandler(event -> {
            if (null != textOutEventViewHandler && hasText()) {
                final TextExitEvent textOutEvent = new TextExitEvent(event.getX(),
                                                                     event.getY(),
                                                                     event.getMouseEvent().getClientX(),
                                                                     event.getMouseEvent().getClientY());
                textOutEventViewHandler.handle(textOutEvent);
            }
        });
        eventHandlerManager.get().addHandlersRegistration(ViewEventType.TEXT_EXIT,
                                                          registration);
    }

    @SuppressWarnings("unchecked")
    public void setTitle(final String title) {
        if (null == title) {
            text.setText(null);
        } else {
            text.setText(title.trim());
        }
    }

    @SuppressWarnings("unchecked")
    public boolean setTitlePosition(final HasTitle.Position position) {
        LayoutContainer.Layout layout = LayoutContainer.Layout.CENTER;
        switch (position) {
            case BOTTOM:
                layout = LayoutContainer.Layout.BOTTOM;
                break;
            case TOP:
                layout = LayoutContainer.Layout.TOP;
                break;
            case LEFT:
                layout = LayoutContainer.Layout.LEFT;
                break;
            case RIGHT:
                layout = LayoutContainer.Layout.RIGHT;
                break;
        }
        final boolean changed = !currentTextLayout.equals(layout);
        this.currentTextLayout = layout;
        return changed;
    }

    public void setTitleXOffsetPosition(final double xOffset) {
        this.text.setX(xOffset);
    }

    public void setTitleYOffsetPosition(final double yOffset) {
        this.text.setY(yOffset);
    }

    @SuppressWarnings("unchecked")
    public void setTitleRotation(final double degrees) {
        text.setRotationDegrees(degrees);
    }

    @SuppressWarnings("unchecked")
    public void setTitleStrokeColor(final String color) {
        text.setStrokeColor(color);
    }

    @SuppressWarnings("unchecked")
    public void setTitleFontFamily(final String fontFamily) {
        text.setFontFamily(fontFamily);
    }

    @SuppressWarnings("unchecked")
    public void setTitleFontSize(final double fontSize) {
        text.setFontSize(fontSize);
    }

    @SuppressWarnings("unchecked")
    public void setTitleFontColor(final String fillColor) {
        text.setFillColor(fillColor);
    }

    @SuppressWarnings("unchecked")
    public void setTitleAlpha(final double alpha) {
        text.setAlpha(alpha);
    }

    @SuppressWarnings("unchecked")
    public void setTitleStrokeWidth(final double strokeWidth) {
        text.setStrokeWidth(strokeWidth);
    }

    public void setTitleStrokeAlpha(final double strokeAlpha) {
        text.setStrokeAlpha(strokeAlpha);
    }

    public void setTextWrapper(final TextWrapperStrategy strategy) {

        final ITextWrapper wrapper = getTextWrapper(strategy);
        this.textWrapper = wrapper;
        text.setWrapper(textWrapper);
        updateTextBoundaries();
    }

    ITextWrapper getTextWrapper(final TextWrapperStrategy strategy) {
        return TextWrapperProvider.get(strategy, text);
    }

    @SuppressWarnings("unchecked")
    public void moveTitleToTop() {
        textContainer.moveToTop();
    }

    public IPrimitive<?> getView() {
        return textContainer;
    }

    public LayoutContainer.Layout getLayout() {
        return currentTextLayout;
    }

    public void update() {
        updateTextBoundaries();
    }

    public void resize(final double width,
                       final double height) {
        this.width = width;
        this.height = height;
        update();
    }

    public void destroy() {
        if (null != text) {
            text.removeFromParent();
            this.text = null;
        }
        textContainer.destroy();
        deregisterHandler(textOverHandlerViewHandler);
        deregisterHandler(textOutEventViewHandler);
        deregisterHandler(textClickEventViewHandler);
        deregisterHandler(textDblClickEventViewHandler);
        eventHandlerManager.get().destroy();
        textWrapper = null;
        currentTextLayout = null;
    }

    private void deregisterHandler(final ViewHandler<?> handler) {
        if (null != handler) {
            eventHandlerManager.get().removeHandler(handler);
        }
    }

    private boolean hasText() {
        final String text = this.text.getText();
        return null != text && text.trim().length() > 0;
    }

    void updateTextBoundaries() {
        setTextBoundaries(new BoundingBox(0,
                                          0,
                                          width,
                                          height));
    }

    void setTextBoundaries(BoundingBox boundaries) {
        if (!(textWrapper instanceof ITextWrapperWithBoundaries)) {
            return;
        }

        final ITextWrapperWithBoundaries textWrapperWithBoundaries = (ITextWrapperWithBoundaries) textWrapper;

        switch (getLayout()) {
            case LEFT:
                if (null != boundaries) {
                    textWrapperWithBoundaries.setWrapBoundaries(new BoundingBox(boundaries.getMinY(),
                                                                                boundaries.getMaxX(),
                                                                                boundaries.getMaxY(),
                                                                                boundaries.getMaxX()));
                }
                break;

            case RIGHT:
                if (null != boundaries) {
                    textWrapperWithBoundaries.setWrapBoundaries(new BoundingBox(boundaries.getMinY(),
                                                                                boundaries.getMaxX(),
                                                                                boundaries.getMaxY(),
                                                                                boundaries.getMaxX()));
                }
                break;

            case TOP:
            case CENTER:
            case BOTTOM:
            default:
                textWrapperWithBoundaries.setWrapBoundaries(boundaries);
                break;
        }
    }
}
