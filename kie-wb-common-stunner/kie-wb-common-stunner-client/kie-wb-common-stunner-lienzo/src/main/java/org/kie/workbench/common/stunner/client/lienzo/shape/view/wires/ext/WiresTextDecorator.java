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

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.google.gwt.event.shared.HandlerRegistration;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.ViewEventHandlerManager;
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

    private final ViewEventHandlerManager eventHandlerManager;
    private final Group textContainer = new Group();
    private ViewHandler<TextEnterEvent> textOverHandlerViewHandler;
    private ViewHandler<TextExitEvent> textOutEventViewHandler;
    private ViewHandler<TextClickEvent> textClickEventViewHandler;
    private ViewHandler<TextDoubleClickEvent> textDblClickEventViewHandler;
    private Text text;
    private LayoutContainer.Layout currentTextLayout;

    public WiresTextDecorator(final ViewEventHandlerManager eventHandlerManager) {
        this.eventHandlerManager = eventHandlerManager;
        initialize();
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

    private void initialize() {
        this.text = new Text("")
                .setFontSize(14)
                .setFillColor(ColorName.BLACK)
                .setStrokeWidth(1)
                .setDraggable(false)
                .setAlpha(0)
                .setTextAlign(TextAlign.CENTER);
        this.currentTextLayout = LayoutContainer.Layout.CENTER;
        textContainer.add(text);
        // Ensure path bounds are available on the selection context.
        text.setFillBoundsForSelection(true);
        initializeHandlers();
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
                eventHandlerManager.skipClickHandler();
                final TextClickEvent e = new TextClickEvent(event.getX(),
                                                            event.getY(),
                                                            event.getMouseEvent().getClientX(),
                                                            event.getMouseEvent().getClientY());
                textClickEventViewHandler.handle(e);
                eventHandlerManager.restoreClickHandler();
            }
        });
        eventHandlerManager.addHandlersRegistration(ViewEventType.TEXT_CLICK,
                                                    registration);
    }

    private void registerDoubleClickHandler() {
        HandlerRegistration registration = text.addNodeMouseDoubleClickHandler(event -> {
            if (null != textDblClickEventViewHandler) {
                eventHandlerManager.skipClickHandler();
                final TextDoubleClickEvent e = new TextDoubleClickEvent(event.getX(),
                                                                        event.getY(),
                                                                        event.getMouseEvent().getClientX(),
                                                                        event.getMouseEvent().getClientY());
                textDblClickEventViewHandler.handle(e);
                eventHandlerManager.restoreClickHandler();
            }
        });
        eventHandlerManager.addHandlersRegistration(ViewEventType.TEXT_DBL_CLICK,
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
        eventHandlerManager.addHandlersRegistration(ViewEventType.TEXT_ENTER,
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
        eventHandlerManager.addHandlersRegistration(ViewEventType.TEXT_EXIT,
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
        setTextBoundaries(text.getWrapBoundaries());
        return changed;
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

    public void destroy() {
        if (null != text) {
            text.removeFromParent();
            this.text = null;
        }
        textContainer.removeFromParent();
        deregisterHandler(textOverHandlerViewHandler);
        deregisterHandler(textOutEventViewHandler);
        deregisterHandler(textClickEventViewHandler);
        deregisterHandler(textDblClickEventViewHandler);
    }

    private void deregisterHandler(final ViewHandler<?> handler) {
        if (null != handler) {
            eventHandlerManager.removeHandler(handler);
        }
    }

    private boolean hasText() {
        final String text = this.text.getText();
        return null != text && text.trim().length() > 0;
    }

    public void setTextBoundaries(BoundingBox boundaries) {
        switch (getLayout()) {
            case LEFT:
                if (null != boundaries) {
                    text.setWrapBoundaries(new BoundingBox(boundaries.getMinY(),
                                                           boundaries.getMaxX(),
                                                           boundaries.getMaxY(),
                                                           boundaries.getMaxX()));
                }
                break;

            case RIGHT:
                if (null != boundaries) {
                    text.setWrapBoundaries(new BoundingBox(boundaries.getMinY(),
                                                           boundaries.getMaxX(),
                                                           boundaries.getMaxY(),
                                                           boundaries.getMaxX()));
                }
                break;

            case TOP:
            case CENTER:
            case BOTTOM:
            default:
                text.setWrapBoundaries(boundaries);
                break;
        }
    }
}
