/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.ait.lienzo.client.widget.panel.impl;

import java.util.function.Predicate;

import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.i18n.MessageConstants;
import com.ait.lienzo.client.core.mediator.Mediators;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Scene;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.style.Style.Cursor;
import com.ait.lienzo.client.core.style.Style.OutlineStyle;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.DragMouseControl;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.shared.core.types.DataURLType;
import com.ait.lienzo.shared.core.types.IColor;
import elemental2.dom.HTMLDivElement;

public class LienzoFixedPanel extends LienzoPanel<LienzoFixedPanel> {

    private final Viewport m_view;

    private int widePx;
    private int highPx;

    private LienzoPanelHandlerManager m_events;

    public static LienzoFixedPanel newPanel() {
        return new LienzoFixedPanel(new Viewport());
    }

    public static LienzoFixedPanel newPanel(int wide,
                                            int high) {
        final LienzoFixedPanel panel = newPanel();
        panel.setPixelSize(wide, high);
        return panel;
    }

    LienzoFixedPanel(final Viewport view) {
        this.m_view = view;
        doPostCTOR();
    }

    private final void doPostCTOR() {
        m_view.setDragMouseButtons(DragMouseControl.LEFT_MOUSE_ONLY);

        if (LienzoCore.IS_CANVAS_SUPPORTED) {

            m_events = new LienzoPanelHandlerManager(this);
        } else {
            getElement().innerHTML = MessageConstants.MESSAGES.getCanvasUnsupportedMessage();

            m_events = null;
        }

        getElement().style.outlineStyle = OutlineStyle.NONE.getCssName();
    }

    public int getWidePx() {
        return widePx;
    }

    public int getHighPx() {
        return highPx;
    }

    @Override
    public void destroy() {
        if (null != getElement().parentElement) {
            getElement().parentElement.removeChild(getElement());
        }
        removeAll();
        if (m_events != null) {
            m_events.destroy();
            m_events = null;
        }
    }

    @Override
    public HTMLDivElement getElement() {
        return m_view.getElement();
    }

    public LienzoFixedPanel setTransform(final Transform transform) {
        getViewport().setTransform(transform);

        return this;
    }

    public LienzoFixedPanel draw() {
        getViewport().draw();

        return this;
    }

    public LienzoFixedPanel batch() {
        getViewport().batch();

        return this;
    }

    /**
     * Adds a layer to the {@link LienzoFixedPanel}. It should be noted that this
     * action will cause a {@link com.ait.lienzo.client.core.shape.Layer} draw operation, painting all children in the
     * Layer.
     * @param layer
     * @return
     */
    @Override
    public LienzoFixedPanel add(final Layer layer) {
        getScene().add(layer);

        return this;
    }

    /**
     * Adds a layer to the {@link LienzoFixedPanel}. It should be noted that this
     * action will cause a {@link com.ait.lienzo.client.core.shape.Layer} draw operation, painting all children in the
     * Layer.
     * @param layer
     * @return
     */
    public LienzoFixedPanel add(final Layer layer, final Layer... layers) {
        add(layer);

        for (Layer node : layers) {
            add(node);
        }
        return this;
    }

    /**
     * Removes a layer from the {@link LienzoFixedPanel}. It should be noted that
     * this action will cause a {@link com.ait.lienzo.client.core.shape.Layer} draw operation, painting all children in
     * the Layer.
     * @param layer
     * @return
     */
    public LienzoFixedPanel remove(final Layer layer) {
        getScene().remove(layer);

        return this;
    }

    /**
     * Removes all layer from the {@link LienzoFixedPanel}.
     * @return
     */
    public LienzoFixedPanel removeAll() {
        getScene().removeAll();

        return this;
    }

    public void setPixelSize(final int wide, final int high) {
        if (widePx != wide || highPx != high) {
            widePx = wide;
            highPx = high;
            getViewport().setPixelSize(wide, high);
            getViewport().draw();
        }
    }

    /**
     * Sets the type of cursor to be used when hovering above the element.
     * @param cursor
     */
    @Override
    public LienzoFixedPanel setCursor(final Cursor cursor) {
        getElement().style.cursor = cursor.getCssName();
        return this;
    }

    /**
     * Returns the {@link com.ait.lienzo.client.core.shape.Viewport} main {@link com.ait.lienzo.client.core.shape.Scene}
     * @return
     */
    public Scene getScene() {
        return getViewport().getScene();
    }

    /**
     * Returns the automatically create {@link com.ait.lienzo.client.core.shape.Viewport} instance.
     * @return
     */
    @Override
    public final Viewport getViewport() {
        return m_view;
    }

    public Iterable<Node<?>> findByID(final String id) {
        return getViewport().findByID(id);
    }

    public Iterable<Node<?>> find(final Predicate<Node<?>> predicate) {
        return getViewport().find(predicate);
    }

    /**
     * Sets the {@link com.ait.lienzo.client.core.shape.Viewport} background {@link
     * com.ait.lienzo.client.core.shape.Layer}
     * @param layer
     */
    @Override
    public LienzoFixedPanel setBackgroundLayer(final Layer layer) {
        getViewport().setBackgroundLayer(layer);

        return this;
    }

    /**
     * Returns the {@link com.ait.lienzo.client.core.shape.Viewport} Drag {@link com.ait.lienzo.client.core.shape.Layer}
     * @return
     */
    public Layer getDragLayer() {
        return getViewport().getDragLayer();
    }

    public String toDataURL() {
        return getViewport().toDataURL();
    }

    public String toDataURL(final boolean includeBackgroundLayer) {
        return getViewport().toDataURL(includeBackgroundLayer);
    }

    public String toDataURL(final DataURLType mimetype) {
        return getViewport().toDataURL(mimetype);
    }

    public String toDataURL(final DataURLType mimetype, final boolean includeBackgroundLayer) {
        return getViewport().toDataURL(mimetype, includeBackgroundLayer);
    }

    /**
     * Sets the background color of the LienzoPanelImpl.
     * @param color String
     * @return this LienzoPanelImpl
     */
    public LienzoFixedPanel setBackgroundColor(final String color) {
        getElement().style.backgroundColor = color;
        return this;
    }

    /**
     * Sets the background color of the LienzoPanelImpl.
     * @param color IColor, i.e. ColorName or Color
     * @return this LienzoPanelImpl
     */
    public LienzoFixedPanel setBackgroundColor(final IColor color) {
        if (null != color) {
            setBackgroundColor(color.getColorString());
        }
        return this;
    }

    /**
     * Returns the {@link com.ait.lienzo.client.core.mediator.Mediators} for this panels {@link
     * com.ait.lienzo.client.core.shape.Viewport}. Mediators can be used to e.g. to add zoom operations.
     * @return Mediators
     */
    public Mediators getMediators() {
        return getViewport().getMediators();
    }
}
