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


package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext;

import java.util.Optional;

import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.util.WiresConnectorLabel;
import com.ait.lienzo.client.core.shape.wires.util.WiresConnectorLabelFactory;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.ViewEventHandlerManager;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresConnectorView;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.kie.workbench.common.stunner.core.client.theme.StunnerTheme;

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

    protected void init(final ViewEventType[] supportedEventTypes) {
        this.label = Optional.of(WiresConnectorLabelFactory.newLabelOnLongestSegment("", this));
        this.textRotationDegrees = 0;
        this.eventHandlerManager = new ViewEventHandlerManager(getLine().asShape(),
                                                               getLine().asShape(),
                                                               supportedEventTypes);
    }

    public WiresConnectorLabel getLabel() {
        return label.orElse(null);
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
    public T addHandler(final ViewEventType type,
                        final ViewHandler<? extends ViewEvent> eventHandler) {
        eventHandlerManager.addHandler(type,
                                       eventHandler);
        return cast();
    }

    @Override
    public T removeHandler(final ViewHandler<? extends ViewEvent> eventHandler) {
        eventHandlerManager.removeHandler(eventHandler);
        return cast();
    }

    @Override
    public T disableHandlers() {
        eventHandlerManager.disable();
        return cast();
    }

    @Override
    public T enableHandlers() {
        eventHandlerManager.enable();
        return cast();
    }

    @Override
    public T setTitle(final String title) {
        return Optional.ofNullable(title)
                .map(t -> label.map(l -> l.configure(text -> {
                                                         text.setFillColor(StunnerTheme.getTheme().getEdgeTextFillColor());
                                                         text.setStrokeColor(StunnerTheme.getTheme().getEdgeTextStrokeColor());
                                                         text.setStrokeWidth(0.70);
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
    public void setTitleBackgroundColor(String color) {
        label.ifPresent(l -> l.setRectangleColor(color));
    }

    @Override
    public T setTitleStrokeColor(final String color) {
        label.ifPresent(l -> l.configure(text -> text.setStrokeColor(color)));
        return cast();
    }

    @Override
    public T setTitleFontFamily(final String fontFamily) {
        label.ifPresent(l -> l.configure(text -> text.setFontFamily(fontFamily)));
        return cast();
    }

    @Override
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
    public T setTitleStrokeWidth(final double strokeWidth) {
        label.ifPresent(l -> l.configure(text -> text.setStrokeWidth(strokeWidth)));
        return cast();
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
    public void batch() {
        label.ifPresent(l -> l.configure(Shape::batch));
    }

    @Override
    public T moveTitleToTop() {
        label.ifPresent(l -> l.configure(Shape::moveToTop));
        return cast();
    }

    @Override
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

    private void destroyLabel() {
        this.label.ifPresent(WiresConnectorLabel::destroy);
        this.label = Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private T cast() {
        return (T) this;
    }
}
