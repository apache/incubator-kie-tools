/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext;

import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayoutContainer;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.ViewEventHandlerManager;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresConnectorView;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.uberfire.mvp.Command;

// TODO: (Roger) Refactor use of inner text child as done in WiresShapeViewExt.
public class WiresConnectorViewExt<T> extends WiresConnectorView<T>
        implements
        HasTitle<T>,
        HasEventHandlers<T, Shape<?>> {

    protected ViewEventHandlerManager eventHandlerManager;
    protected Text text;
    protected WiresLayoutContainer.Layout textPosition;
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
        this.textPosition = WiresLayoutContainer.Layout.CENTER;
        this.textRotationDegrees = 0;
        this.eventHandlerManager = new ViewEventHandlerManager(getLine(),
                                                               getLine(),
                                                               supportedEventTypes);
    }

    @Override
    public boolean supports(final ViewEventType type) {
        return eventHandlerManager.supports(type);
    }

    @Override
    public Shape<?> getAttachableShape() {
        return getLine();
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
        if (null != text) {
            text.removeFromParent();
        }
        if (null != title) {
            // TODO
        }
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setTitlePosition(final Position position) {
        if (Position.BOTTOM.equals(position)) {
            this.textPosition = LayoutContainer.Layout.BOTTOM;
        } else if (Position.TOP.equals(position)) {
            this.textPosition = LayoutContainer.Layout.TOP;
        } else if (Position.LEFT.equals(position)) {
            this.textPosition = LayoutContainer.Layout.LEFT;
        } else if (Position.RIGHT.equals(position)) {
            this.textPosition = LayoutContainer.Layout.RIGHT;
        } else if (Position.CENTER.equals(position)) {
            this.textPosition = LayoutContainer.Layout.CENTER;
        }
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
        return updateTextIfAny(() -> text.setStrokeColor(color));
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setTitleFontFamily(final String fontFamily) {
        return updateTextIfAny(() -> text.setFontFamily(fontFamily));
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setTitleFontSize(final double fontSize) {
        return updateTextIfAny(() -> text.setFontSize(fontSize));
    }

    @Override
    public T setTitleFontColor(final String fillColor) {
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setTitleStrokeWidth(final double strokeWidth) {
        return updateTextIfAny(() -> text.setStrokeWidth(strokeWidth));
    }

    @Override
    @SuppressWarnings("unchecked")
    public T moveTitleToTop() {
        return updateTextIfAny(() -> text.moveToTop());
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setTitleAlpha(final double alpha) {
        return updateTextIfAny(() -> text.setAlpha(alpha));
    }

    @Override
    public void destroy() {
        super.destroy();
        // Clear registered event handlers.
        if (null != eventHandlerManager) {
            eventHandlerManager.destroy();
            eventHandlerManager = null;
        }
        // Nullify.
        this.text = null;
        this.textPosition = null;
    }

    @SuppressWarnings("unchecked")
    private T updateTextIfAny(final Command callback) {
        if (null != text) {
            callback.execute();
        }
        return cast();
    }

    @SuppressWarnings("unchecked")
    private T cast() {
        return (T) this;
    }
}
