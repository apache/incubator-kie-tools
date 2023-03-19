/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.ITextWrapper;
import com.ait.lienzo.client.core.shape.ITextWrapperWithBoundaries;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.TextBoundsWrap;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout.Direction;
import com.ait.lienzo.client.core.shape.wires.layout.label.LabelContainerLayout;
import com.ait.lienzo.client.core.shape.wires.layout.label.LabelLayout;
import com.ait.lienzo.client.core.shape.wires.layout.size.SizeConstraints;
import com.ait.lienzo.client.core.shape.wires.layout.size.SizeConstraints.Type;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.ViewEventHandlerManager;
import org.kie.workbench.common.stunner.core.client.shape.TextWrapperStrategy;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextEnterEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextExitEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.kie.workbench.common.stunner.core.graph.util.Exceptions;

import static org.kie.workbench.common.stunner.client.lienzo.shape.view.ViewEventHandlerManager.getClientX;
import static org.kie.workbench.common.stunner.client.lienzo.shape.view.ViewEventHandlerManager.getClientY;

/**
 * A helper class for handling the wires shapes' text primitive
 * that is used to display the shape's name.
 * <p>
 * It handles common logic for ShapeViews that implement <code>HasText</code>
 * type, can be reused for shapes or connectors.
 * <p>
 */
public class WiresTextDecorator implements HasTitle<WiresTextDecorator> {

    // Default text attribute values.
    private static final double TEXT_ALPHA = 1d;
    private static final String TEXT_FONT_FAMILY = "Verdana";
    private static final double TEXT_FONT_SIZE = 10d;
    private static final String TEXT_FILL_COLOR = "#000000";
    private static final String TEXT_STROKE_COLOR = "#000000";
    private static final double TEXT_STROKE_WIDTH = 0;
    private static final TextAlign TEXT_ALIGN = TextAlign.CENTER;

    private final Supplier<ViewEventHandlerManager> eventHandlerManager;
    private ViewHandler<TextEnterEvent> textOverHandlerViewHandler;
    private ViewHandler<TextExitEvent> textOutEventViewHandler;
    private ViewHandler<TextClickEvent> textClickEventViewHandler;
    private ViewHandler<TextDoubleClickEvent> textDblClickEventViewHandler;
    private Text text;
    private ITextWrapper textWrapper;
    private LabelLayout labelLayout;
    private Optional<Size> sizeConstraints = Optional.empty();
    private Map<Enum, Double> margins = Collections.emptyMap();
    private WiresShapeViewExt<WiresShapeViewExt> shape;

    public WiresTextDecorator(final Supplier<ViewEventHandlerManager> eventHandlerManager,
                              final WiresShapeViewExt shape) {
        this.eventHandlerManager = eventHandlerManager;
        this.shape = shape;
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
                .setAlpha(TEXT_ALPHA)
                .setFontFamily(TEXT_FONT_FAMILY)
                .setFontSize(TEXT_FONT_SIZE)
                .setFillColor(TEXT_FILL_COLOR)
                .setStrokeColor(TEXT_STROKE_COLOR)
                .setStrokeWidth(TEXT_STROKE_WIDTH)
                .setTextAlign(TEXT_ALIGN)
                .setDraggable(false);
        this.textWrapper = new TextBoundsWrap(text, shape.getPath().getBoundingBox());
        this.text.setWrapper(textWrapper);
        // Ensure path bounds are available on the selection context.
        text.setFillBoundsForSelection(true);
        initializeHandlers();
        setTextBoundaries(shape.getPath().getBoundingBox());
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
                                                            getClientX(event),
                                                            getClientY(event));
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
                                                                        getClientX(event),
                                                                        getClientY(event));
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
                                                                        getClientX(event),
                                                                        getClientY(event));
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
                                                                     getClientX(event),
                                                                     getClientY(event));
                textOutEventViewHandler.handle(textOutEvent);
            }
        });
        eventHandlerManager.get().addHandlersRegistration(ViewEventType.TEXT_EXIT,
                                                          registration);
    }

    @SuppressWarnings("unchecked")
    public WiresTextDecorator setTitle(final String title) {
        if (null == title) {
            text.setText(null);
        } else {
            text.setText(title.trim());
        }
        return this;
    }

    public void isListening(boolean isListening) {
        text.setListening(isListening);
    }

    @Override
    public WiresTextDecorator setMargins(final Map<Enum, Double> margins) {
        this.margins = margins;
        return this;
    }

    @SuppressWarnings("unchecked")
    public WiresTextDecorator setTitleRotation(final double degrees) {
        text.setRotationDegrees(degrees);
        return this;
    }

    @SuppressWarnings("unchecked")
    public WiresTextDecorator setTitleStrokeColor(final String color) {
        text.setStrokeColor(color);
        return this;
    }

    @SuppressWarnings("unchecked")
    public WiresTextDecorator setTitleFontFamily(final String fontFamily) {
        text.setFontFamily(fontFamily);
        return this;
    }

    @SuppressWarnings("unchecked")
    public WiresTextDecorator setTitleFontSize(final double fontSize) {
        text.setFontSize(fontSize);
        return this;
    }

    @SuppressWarnings("unchecked")
    public WiresTextDecorator setTitleFontColor(final String fillColor) {
        text.setFillColor(fillColor);
        return this;
    }

    @SuppressWarnings("unchecked")
    public WiresTextDecorator setTitleAlpha(final double alpha) {
        text.setAlpha(alpha);
        return this;
    }

    @SuppressWarnings("unchecked")
    public WiresTextDecorator setTitleStrokeWidth(final double strokeWidth) {
        text.setStrokeWidth(strokeWidth);
        return this;
    }

    @Override
    public String getTitleFontFamily() {
        return text.getFontFamily();
    }

    @Override
    public double getTitleFontSize() {
        return text.getFontSize();
    }

    @Override
    public String getTitlePosition() {
        return getLabelLayout()
                .getDirectionLayout()
                .getReferencePosition()
                .toString();
    }

    @Override
    public String getOrientation() {
        return getLabelLayout()
                .getDirectionLayout()
                .getOrientation().name();
    }

    @Override
    public double getMarginX() {
        return getLabelLayout().getSizeConstraints().getMarginX();
    }

    @Override
    public String getFontPosition() {
        return getLabelLayout()
                .getDirectionLayout()
                .getReferencePosition()
                .name();
    }

    @Override
    public String getFontAlignment() {
        return getLabelLayout()
                .getDirectionLayout()
                .getVerticalAlignment()
                .name();
    }

    @Override
    public void batch() {
        text.batch();
    }

    public WiresTextDecorator setTitleStrokeAlpha(final double strokeAlpha) {
        text.setStrokeAlpha(strokeAlpha);
        return this;
    }

    public WiresTextDecorator setTitleWrapper(final TextWrapperStrategy strategy) {

        final ITextWrapper wrapper = getTextWrapper(strategy);
        this.textWrapper = wrapper;
        text.setWrapper(textWrapper);
        update();
        return this;
    }

    @Override
    public WiresTextDecorator setTitleXOffsetPosition(final Double xOffset) {
        this.text.setX(xOffset);
        return this;
    }

    @Override
    public WiresTextDecorator setTitleYOffsetPosition(final Double yOffset) {
        this.text.setY(yOffset);
        return this;
    }

    @Override
    public void setTitleBoundaries(final double width, final double height) {
        setTextBoundaries(BoundingBox.fromDoubles(0, 0, width, height));
    }

    ITextWrapper getTextWrapper(final TextWrapperStrategy strategy) {
        return TextWrapperProvider.get(strategy, text);
    }

    @SuppressWarnings("unchecked")
    public WiresTextDecorator moveTitleToTop() {
        text.moveToTop();
        moveShapeChildrenToFront();
        return this;
    }

    private void moveShapeChildrenToFront() {
        shape.getChildShapes()
                .toList()
                .stream()
                .map(WiresShape::getGroup)
                .forEach(Group::moveToTop);
    }

    public Text getView() {
        return text;
    }

    /**
     * Returns the label layout based on the model
     *
     * @return
     */
    public LabelLayout getLabelLayout() {
        return Optional.ofNullable(labelLayout)
                .orElseGet(() -> new LabelLayout.Builder().horizontalAlignment(
                        DirectionLayout.HorizontalAlignment.CENTER)
                        .verticalAlignment(DirectionLayout.VerticalAlignment.MIDDLE)
                        .orientation(DirectionLayout.Orientation.HORIZONTAL)
                        .referencePosition(DirectionLayout.ReferencePosition.INSIDE)
                        .sizeConstraints(getDefaultSizeConstraints())
                        .build());
    }

    private com.ait.lienzo.client.core.shape.wires.layout.size.SizeConstraints getDefaultSizeConstraints() {
        return new com.ait.lienzo.client.core.shape.wires.layout.size.SizeConstraints(100, 100,
                                                                                      Type.PERCENTAGE);
    }

    private <T extends Enum<T>> T convertEnum(Enum<?> input, Class<T> outputType) {
        return Exceptions.swallow(() -> Enum.valueOf(outputType, input.name()), null);
    }

    public WiresTextDecorator setTitlePosition(final HasTitle.VerticalAlignment verticalAlignment, final HasTitle.HorizontalAlignment horizontalAlignment,
                                               final HasTitle.ReferencePosition referencePosition, final HasTitle.Orientation orientation) {
        labelLayout = new LabelLayout.Builder()
                .horizontalAlignment(convertEnum(horizontalAlignment, DirectionLayout.HorizontalAlignment.class))
                .verticalAlignment(convertEnum(verticalAlignment, DirectionLayout.VerticalAlignment.class))
                .orientation(convertEnum(orientation, DirectionLayout.Orientation.class))
                .referencePosition(convertEnum(referencePosition, DirectionLayout.ReferencePosition.class))
                .margins(margins.entrySet()
                                 .stream()
                                 .map(e -> new SimpleEntry<>(
                                         Optional.<Direction>ofNullable(convertEnum(e.getKey(), DirectionLayout.VerticalAlignment.class))
                                                 .orElse(convertEnum(e.getKey(),
                                                                     DirectionLayout.HorizontalAlignment.class)),
                                         e.getValue()))
                                 .collect(Collectors.toMap(Entry::getKey, Entry::getValue)))
                .sizeConstraints(sizeConstraints
                                         .map(s -> new SizeConstraints(s.getWidth(), s.getHeight(),
                                                                       convertEnum(s.getType(), Type.class)))
                                         .orElse(getDefaultSizeConstraints()))
                .build();
        return this;
    }

    @Override
    public WiresTextDecorator setTitleSizeConstraints(final Size sizeConstraints) {
        this.sizeConstraints = Optional.ofNullable(sizeConstraints);
        return this;
    }

    public void update() {
        setTextBoundaries(shape.getPath().getBoundingBox());
    }

    public void destroy() {
        if (null != text) {
            text.removeFromParent();
            this.text = null;
        }
        deregisterHandler(textOverHandlerViewHandler);
        deregisterHandler(textOutEventViewHandler);
        deregisterHandler(textClickEventViewHandler);
        deregisterHandler(textDblClickEventViewHandler);
        eventHandlerManager.get().destroy();
        textWrapper = null;
        labelLayout = null;
        sizeConstraints = null;
        margins.clear();
        margins = null;
        shape = null;
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

    void setTextBoundaries(BoundingBox boundaries) {
        //update text wrapper boundaries
        Optional.ofNullable(textWrapper)
                .filter(wrapper -> wrapper instanceof ITextWrapperWithBoundaries)
                .map(wrapper -> (ITextWrapperWithBoundaries) wrapper)
                .ifPresent(wrapper -> wrapper
                        .setWrapBoundaries(shape.getLabelContainerLayout()
                                                   .map(layout -> layout.getMaxSize(text))
                                                   .orElse(boundaries)));

        //update position
        shape.getLabelContainerLayout().ifPresent(LabelContainerLayout::execute);
    }
}
