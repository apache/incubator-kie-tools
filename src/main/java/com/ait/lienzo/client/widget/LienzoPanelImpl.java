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

package com.ait.lienzo.client.widget;

import java.util.function.Predicate;

import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.i18n.MessageConstants;
import com.ait.lienzo.client.core.mediator.IMediator;
import com.ait.lienzo.client.core.mediator.Mediators;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Scene;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.style.Style;
import com.ait.lienzo.client.core.style.Style.Cursor;
import com.ait.lienzo.client.core.style.Style.OutlineStyle;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.core.util.CursorMap;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.impl.LienzoPanelHandlerManager;
import com.ait.lienzo.shared.core.types.AutoScaleType;
import com.ait.lienzo.shared.core.types.DataURLType;
import com.ait.lienzo.shared.core.types.IColor;
import com.google.gwt.core.client.Scheduler;
import elemental2.core.JsNumber;
import elemental2.dom.CSSProperties.HeightUnionType;
import elemental2.dom.CSSProperties.WidthUnionType;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.ViewCSS;
import jsinterop.base.Js;

/**
 * LienzoPanelImpl acts as a Container for a {@link com.ait.lienzo.client.core.shape.Viewport}.
 *
 * <ul>
 * <li>An application will typically be composed of one or more LienzoPanels.</li>
 * <li>A LienzoPanelImpl takes width and height as input parameters.</li>
 * <li>A {@link com.ait.lienzo.client.core.shape.Viewport} will contain one main {@link com.ait.lienzo.client.core.shape.Scene}</li>
 * <li>The main {@link com.ait.lienzo.client.core.shape.Scene} can contain multiple {@link com.ait.lienzo.client.core.shape.Layer}.</li>
 * </ul>
 */
public class LienzoPanelImpl extends LienzoPanel //extends FocusPanel implements RequiresResize, ProvidesResize
{
    private final Viewport       m_view;

    private HTMLDivElement       m_elm;

    private int                  m_width;

    private int                  m_height;

    private boolean              m_flex;

    private AutoScaleType        m_auto;

    private LienzoPanelHandlerManager m_events;

    private Cursor               m_widget_cursor;

    private Cursor               m_active_cursor;

    private Cursor               m_normal_cursor;

    private Cursor               m_select_cursor;

    private int                  m_widthOffset;
    private int                  m_heightOffset;

    private EventListener        m_resizeListener;

    public LienzoPanelImpl(final HTMLDivElement elm, boolean resize)
    {
        this(elm, resize, 0,0);
    }

    public LienzoPanelImpl(final HTMLDivElement elm, boolean resize, int widthOffset, int heightOffset)
    {
        m_view = new Viewport();
        m_elm = elm;
        m_elm.tabIndex = 0;

        Size size = getSize((HTMLDivElement)elm.parentNode);

        m_widthOffset = widthOffset;
        m_heightOffset = heightOffset;

        doPostCTOR(size.width - widthOffset, size.height - heightOffset);

        if (resize)
        {
            m_resizeListener = e ->
            {
                Size resizeSize = getSize((HTMLDivElement)elm.parentNode);
                setPixelSize(resizeSize.width, resizeSize.height);
            };
            DomGlobal.window.addEventListener("resize", m_resizeListener);
        }
    }

    public LienzoPanelImpl(HTMLDivElement elm, int width, int height)
    {
        m_view = new Viewport();
        m_elm = elm;
        m_elm.tabIndex = 0;

        doPostCTOR(width, height);
    }

    private Size getSize(final HTMLDivElement elm)
    {
        CSSStyleDeclaration cs = Js.<ViewCSS>uncheckedCast(DomGlobal.window).getComputedStyle(elm);

        double paddingX = JsNumber.parseFloat(cs.paddingLeft.asString()) + JsNumber.parseFloat(cs.paddingRight.asString());
        double paddingY = JsNumber.parseFloat(cs.paddingTop.asString()) + JsNumber.parseFloat(cs.paddingBottom.asString());

        double borderX = JsNumber.parseFloat(cs.borderLeftWidth.asString()) + JsNumber.parseFloat(cs.borderRightWidth.asString());
        double borderY = JsNumber.parseFloat(cs.borderTopWidth.asString()) + JsNumber.parseFloat(cs.borderBottomWidth.asString());

        // Element width and height minus padding and border
        int width  = (int)(elm.offsetWidth - paddingX - borderX);
        int height = (int)(elm.offsetHeight - paddingY - borderY);

        return new Size(width, height);
    }

    public static class Size
    {
        public int width;
        public int height;

        public Size(final int width, final int height)
        {
            this.width = width;
            this.height = height;
        }
    }

    private final void doPostCTOR(final int width, final int height)
    {
        m_view.setDragMouseButtons(DragMouseControl.LEFT_MOUSE_ONLY);

        if (LienzoCore.IS_CANVAS_SUPPORTED)
        {

            HTMLDivElement divElement = Js.uncheckedCast(m_view.getElement());

            m_elm.appendChild(divElement);

            setPixelSize(width, height);

            m_widget_cursor = CursorMap.get().lookup(m_elm.style.cursor);

            m_events = new LienzoPanelHandlerManager(this);
        }
        else
        {

            //add(new Label(MessageConstants.MESSAGES.getCanvasUnsupportedMessage()));
            m_elm.innerHTML = MessageConstants.MESSAGES.getCanvasUnsupportedMessage();

            m_events = null;
        }

        m_elm.style.outlineStyle = OutlineStyle.NONE.getCssName();
    }

    private void setWidth(int width)
    {
        m_elm.style.width = WidthUnionType.of(width);
    }

    private void setHeight(int height)
    {
        m_elm.style.height = HeightUnionType.of(height);
    }

    private void setWidth(String width)
    {
        m_elm.style.width = WidthUnionType.of(width);
    }

    private void setHeight(String height)
    {
        m_elm.style.height = HeightUnionType.of(height);
    }

    public void destroy() {
        removeAll();
        //removeFromParent();
        // @FIXME should remove all listeners (mdp) and check if anything else needs to be done as part of destroy()
        m_events.destroy();
        if (m_resizeListener != null )
        {
            DomGlobal.window.removeEventListener("resize", m_resizeListener);
        }
        m_elm.remove();
        m_auto = null;
        m_events = null;
        m_widget_cursor = null;
        m_active_cursor = null;
        m_normal_cursor = null;
        m_select_cursor = null;
    }

    public LienzoPanelImpl setAutoScale(final AutoScaleType type)
    {
        m_auto = type;

        return this;
    }

    public AutoScaleType getAutoScale()
    {
        if (null == m_auto)
        {
            return AutoScaleType.NONE;
        }
        return m_auto;
    }

    public LienzoPanelImpl setTransform(final Transform transform)
    {
        getViewport().setTransform(transform);

        return this;
    }

    public LienzoPanelImpl draw()
    {
        getViewport().draw();

        return this;
    }

    public LienzoPanelImpl batch()
    {
        getViewport().batch();

        return this;
    }

    /**
     * Adds a layer to the {@link LienzoPanelImpl}.
     * It should be noted that this action will cause a {@link com.ait.lienzo.client.core.shape.Layer} draw operation, painting all children in the Layer.
     *
     * @param layer
     * @return
     */
    public LienzoPanelImpl add(final Layer layer)
    {
        getScene().add(layer);

        return this;
    }

    /**
     * Adds a layer to the {@link LienzoPanelImpl}.
     * It should be noted that this action will cause a {@link com.ait.lienzo.client.core.shape.Layer} draw operation, painting all children in the Layer.
     *
     * @param layer
     * @return
     */
    public LienzoPanelImpl add(final Layer layer, final Layer... layers)
    {
        add(layer);

        for (Layer node : layers)
        {
            add(node);
        }
        return this;
    }

    /**
     * Removes a layer from the {@link LienzoPanelImpl}.
     * It should be noted that this action will cause a {@link com.ait.lienzo.client.core.shape.Layer} draw operation, painting all children in the Layer.
     *
     * @param layer
     * @return
     */
    public LienzoPanelImpl remove(final Layer layer)
    {
        getScene().remove(layer);

        return this;
    }

    /**
     * Removes all layer from the {@link LienzoPanelImpl}.

     * @return
     */
    public LienzoPanelImpl removeAll()
    {
        getScene().removeAll();

        return this;
    }

    /**
     * Sets the size in pixels of the {@link LienzoPanelImpl}
     * Sets the size in pixels of the {@link com.ait.lienzo.client.core.shape.Viewport} contained and automatically added to the instance of the {@link LienzoPanelImpl}
     */
    public void setPixelSize(final int width, final int height)
    {
        if (width >= 0) {
            m_width = width;
            setWidth(width + Style.Unit.PX.getType());
        }
        if (height >= 0) {
            m_height = height;
            setHeight(height + Style.Unit.PX.getType());
        }

        getViewport().setPixelSize(width, height);

        getViewport().draw();
    }

    /**
     * Sets the type of cursor to be used when hovering above the element.
     * @param cursor
     */
    public LienzoPanelImpl setCursor(final Cursor cursor)
    {
        if ((cursor != null) && (cursor != m_active_cursor))
        {
            m_active_cursor = cursor;

            // Need to defer this, sometimes, if the browser is busy, etc, changing cursors does not take effect till events are done processing

            Scheduler.get().scheduleDeferred(() -> m_elm.style.cursor = m_active_cursor.getCssName());
        }
        return this;
    }

    /**
     * Gets the width in pixels.
     *
     * @return
     */
    @Override
    public int getWidePx() {
        return m_width;
    }

    /**
     * Returns the height.
     *
     * @return
     */
    @Override
    public int getHighPx() {
        return m_height;
    }

    public LienzoPanelImpl setNormalCursor(final Cursor cursor)
    {
        m_normal_cursor = cursor;

        return this;
    }

    public Cursor getNormalCursor()
    {
        return m_normal_cursor;
    }

    public LienzoPanelImpl setSelectCursor(final Cursor cursor)
    {
        m_select_cursor = cursor;

        return this;
    }

    public Cursor getSelectCursor()
    {
        return m_select_cursor;
    }

    public Cursor getActiveCursor()
    {
        return m_active_cursor;
    }

    final Cursor getWidgetCursor()
    {
        return m_widget_cursor;
    }

    /**
     * Returns the {@link com.ait.lienzo.client.core.shape.Viewport} main {@link com.ait.lienzo.client.core.shape.Scene}
     * @return
     */
    public Scene getScene()
    {
        return getViewport().getScene();
    }

    /**
     * Returns the automatically create {@link com.ait.lienzo.client.core.shape.Viewport} instance.
     * @return
     */
    public final Viewport getViewport()
    {
        return m_view;
    }

    public Iterable<Node<?>> findByID(final String id)
    {
        return getViewport().findByID(id);
    }

    public Iterable<Node<?>> find(final Predicate<Node<?>> predicate)
    {
        return getViewport().find(predicate);
    }

    /**
     * Sets the {@link com.ait.lienzo.client.core.shape.Viewport} background {@link com.ait.lienzo.client.core.shape.Layer}
     *
     * @param layer
     */
    public LienzoPanelImpl setBackgroundLayer(final Layer layer)
    {
        getViewport().setBackgroundLayer(layer);

        return this;
    }

    /**
     * Returns the {@link com.ait.lienzo.client.core.shape.Viewport} Drag {@link com.ait.lienzo.client.core.shape.Layer}
     *
     * @return
     */
    public Layer getDragLayer()
    {
        return getViewport().getDragLayer();
    }

    public String toDataURL()
    {
        return getViewport().toDataURL();
    }

    public String toDataURL(final boolean includeBackgroundLayer)
    {
        return getViewport().toDataURL(includeBackgroundLayer);
    }

    public String toDataURL(final DataURLType mimetype)
    {
        return getViewport().toDataURL(mimetype);
    }

    public String toDataURL(final DataURLType mimetype, final boolean includeBackgroundLayer)
    {
        return getViewport().toDataURL(mimetype, includeBackgroundLayer);
    }

    /**
     * Sets the background color of the LienzoPanelImpl.
     *
     * @param color String
     * @return this LienzoPanelImpl
     */
    public LienzoPanelImpl setBackgroundColor(final String color)
    {
        if (null != color)
        {
            m_elm.style.backgroundColor = color;
        }
        return this;
    }

    /**
     * Sets the background color of the LienzoPanelImpl.
     *
     * @param color IColor, i.e. ColorName or Color
     * @return this LienzoPanelImpl
     */
    public LienzoPanelImpl setBackgroundColor(final IColor color)
    {
        if (null != color)
        {
            setBackgroundColor(color.getColorString());
        }
        return this;
    }

    /**
     * Returns the background color of this LienzoPanelImpl.
     * Will return null if no color was set, in which case it's probably "white",
     * unless it was changed via CSS rules.
     *
     * @return String
     */
    public String getBackgroundColor()
    {
        return m_elm.style.backgroundColor;
    }

    /**
     * Returns the {@link com.ait.lienzo.client.core.mediator.Mediators} for this panels {@link com.ait.lienzo.client.core.shape.Viewport}.
     * Mediators can be used to e.g. to addBoundingBox zoom operations.
     *
     * @return Mediators
     */
    public Mediators getMediators()
    {
        return getViewport().getMediators();
    }

    /**
     * Add a mediator to the stack of {@link com.ait.lienzo.client.core.mediator.Mediators} for this panels {@link com.ait.lienzo.client.core.shape.Viewport}.
     * The one that is added last, will be called first.
     *
     * Mediators can be used to e.g. to addBoundingBox zoom operations.
     *
     * @param mediator IMediator
     */
    public LienzoPanelImpl pushMediator(final IMediator mediator)
    {
        getViewport().pushMediator(mediator);

        return this;
    }

    // @FIXME I don't think this does anything, so ignoring. Delete later. (mdp)
    public static void enableWindowMouseWheelScroll(boolean enabled)
    {
//		$wnd.mousewheel = function() {
//			return enabled;
//		}
    }

    public void setFocus(boolean focused) {
        if (focused) {
            m_elm.focus();
        } else {
            m_elm.blur();
        }
    }

    public void setTabIndex(int index) {
        m_elm.tabIndex = index;
    }

    public HTMLDivElement getElement()
    {
        return m_elm;
    }

    //TODO lienzo-to-native Check if works
    public void setVisible(boolean visible)
    {
        if (visible) {
            m_elm.style.display = Style.Display.BLOCK.getCssName(); //inline
        } else {
            m_elm.style.display = Style.Display.NONE.getCssName();
        }
    }

    //TODO lienzo-to-native Check if works
    public void removeFromParent() {
        Element parent = (Element) m_elm.parentNode;
        if (null != parent) {
            parent.removeChild(m_elm);
        }
    }
}
