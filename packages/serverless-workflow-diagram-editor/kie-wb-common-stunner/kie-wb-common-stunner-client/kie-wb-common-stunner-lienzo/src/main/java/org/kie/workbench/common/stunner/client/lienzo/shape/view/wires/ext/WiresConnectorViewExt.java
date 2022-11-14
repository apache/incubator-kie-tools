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

import java.util.Map;
import java.util.Optional;

import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.util.WiresConnectorLabel;
import com.ait.lienzo.client.core.shape.wires.util.WiresConnectorLabelFactory;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.ViewEventHandlerManager;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresConnectorView;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;

// TODO: (Roger) Refactor use of inner text child as done in WiresShapeViewExt.
public class WiresConnectorViewExt<T>
        extends WiresConnectorView<T>
        implements
        HasTitle<T>,
        HasEventHandlers<T, Shape<?>> {

    protected ViewEventHandlerManager eventHandlerManager;
    protected Optional<WiresConnectorLabel> label;
    protected double textRotationDegrees;

    public WiresConnectorViewExt(final ViewEventType[] supportedEventTypes,
                                 final AbstractDirectionalMultiPointShape<?> line,
                                 final MultiPathDecorator headDecorator,
                                 final MultiPathDecorator tailDecorator) {
        super(line,
              headDecorator,
              tailDecorator);
        init(supportedEventTypes);
    }

    public WiresConnectorViewExt(final ViewEventType[] supportedEventTypes,
                                 final WiresMagnet headMagnet,
                                 final WiresMagnet tailMagnet,
                                 final AbstractDirectionalMultiPointShape<?> line,
                                 final MultiPathDecorator headDecorator,
                                 final MultiPathDecorator tailDecorator) {
        super(headMagnet,
              tailMagnet,
              line,
              headDecorator,
              tailDecorator);
        init(supportedEventTypes);
    }

    protected void init(final ViewEventType[] supportedEventTypes) {
        this.label = createLabel("");
        this.textRotationDegrees = 0;
        this.eventHandlerManager = new ViewEventHandlerManager(getLine().asShape(),
                                                               getLine().asShape(),
                                                               supportedEventTypes);
    }

    @Override
    public boolean supports(final ViewEventType type) {
        return eventHandlerManager.supports(type);
    }

    @Override
    public Shape<?> getAttachableShape() {
        return getLine().asShape();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T addHandler(final ViewEventType type,
                        final ViewHandler<? extends ViewEvent> eventHandler) {
        eventHandlerManager.addHandler(type,
                                       eventHandler);
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T removeHandler(final ViewHandler<? extends ViewEvent> eventHandler) {
        eventHandlerManager.removeHandler(eventHandler);
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T disableHandlers() {
        eventHandlerManager.disable();
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T enableHandlers() {
        eventHandlerManager.enable();
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setTitle(final String title) {
        return Optional.ofNullable(title)
                .map(t -> label.map(l -> l.configure(text -> {
                                                         l.rectangle.setHeight(text.getBoundingBox().getHeight());
                                                         l.rectangle.setWidth(text.getBoundingBox().getWidth());
                                                         text.setFillColor("white");
                                                         text.setStrokeColor("white");
                                                         text.setFontFamily("Verdana");
                                                         text.setFontSize(10);
                                                         text.setText(t);
                                                     }
                                    )
                ))
                .map(l -> cast())
                .orElse(cast());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setTitleBackgroundColor(String color) {
        label.ifPresent(l -> l.setRectangleColor(color));
    }

    @Override
    public T setMargins(final Map<Enum, Double> margins) {
        // Do not apply here...
        return cast();
    }

    @Override
    public T setTitlePosition(final VerticalAlignment verticalAlignment, final HorizontalAlignment horizontalAlignment,
                              final ReferencePosition referencePosition, final Orientation orientation) {
        // Do not apply here...
        return cast();
    }

    @Override
    public T setTitleSizeConstraints(final Size sizeConstraints) {
        // Do not apply here...
        return cast();
    }

    @Override
    public T setTitleXOffsetPosition(final Double xOffset) {
        // Do not apply here...
        return cast();
    }

    @Override
    public T setTitleYOffsetPosition(final Double yOffset) {
        // Do not apply here...
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setTitleRotation(double degrees) {
        this.textRotationDegrees = degrees;
        return cast();
    }

    @Override
    public T setTitleStrokeColor(final String color) {
        label.ifPresent(l -> l.configure(text -> text.setStrokeColor(color)));
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setTitleFontFamily(final String fontFamily) {
        label.ifPresent(l -> l.configure(text -> text.setFontFamily(fontFamily)));
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setTitleFontSize(final double fontSize) {
        label.ifPresent(l -> l.configure(text -> text.setFontSize(fontSize)));
        return cast();
    }

    @Override
    public T setTitleFontColor(final String fillColor) {
        label.ifPresent(l -> l.configure(text -> text.setFillColor(fillColor)));
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setTitleStrokeWidth(final double strokeWidth) {
        label.ifPresent(l -> l.configure(text -> text.setStrokeWidth(strokeWidth)));
        return cast();
    }

    @Override
    public String getTitlePosition() {
        // Do not apply here...
        return null;
    }

    @Override
    public String getOrientation() {
        // Do not apply here...
        return null;
    }

    @Override
    public double getMarginX() {
        // Do not apply here...
        return 0;
    }

    @Override
    public String getTitleFontFamily() {
        // Do not apply here...
        return null;
    }

    @Override
    public double getTitleFontSize() {
        // Do not apply here...
        return 0;
    }

    @Override
    public String getFontPosition() {
        // Do not apply here...
        return null;
    }

    @Override
    public String getFontAlignment() {
        // Do not apply here...
        return null;
    }

    @Override
    public void batch() {
        label.ifPresent(l -> l.configure(Shape::batch));
    }

    @Override
    @SuppressWarnings("unchecked")
    public T moveTitleToTop() {
        label.ifPresent(l -> l.configure(Shape::moveToTop));
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setTitleAlpha(final double alpha) {
        label.ifPresent(l -> l.configure(text -> text.setAlpha(alpha)));
        return cast();
    }

    @Override
    public T setTitleStrokeAlpha(double alpha) {
        label.ifPresent(l -> l.configure(text -> text.setStrokeAlpha(alpha)));
        return cast();
    }

    @Override
    public void destroy() {
        super.destroy();
        // Clear registered event handlers.
        if (null != eventHandlerManager) {
            eventHandlerManager.destroy();
            eventHandlerManager = null;
        }
        destroyLabel();
    }

    protected Optional<WiresConnectorLabel> createLabel(final String title) {
        return Optional.of(WiresConnectorLabelFactory.newLabelOnLongestSegment(title, this));
    }

    private void destroyLabel() {
        this.label.ifPresent(WiresConnectorLabel::destroy);
        this.label = Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private T cast() {
        return (T) this;
    }
}
