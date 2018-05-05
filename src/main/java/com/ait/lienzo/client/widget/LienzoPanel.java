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

import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.i18n.MessageConstants;
import com.ait.lienzo.client.core.mediator.IMediator;
import com.ait.lienzo.client.core.mediator.Mediators;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Scene;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.core.util.CursorMap;
import com.ait.lienzo.shared.core.types.AutoScaleType;
import com.ait.lienzo.shared.core.types.DataURLType;
import com.ait.lienzo.shared.core.types.IColor;
import com.ait.tooling.common.api.java.util.function.Predicate;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * LienzoPanel acts as a Container for a {@link Viewport}.
 *
 * <ul>
 * <li>An application will typically be composed of one or more LienzoPanels.</li>
 * <li>A LienzoPanel takes width and height as input parameters.</li>
 * <li>A {@link Viewport} will contain one main {@link Scene}</li>
 * <li>The main {@link Scene} can contain multiple {@link Layer}.</li>
 * </ul>
 */
public class LienzoPanel extends FocusPanel implements RequiresResize, ProvidesResize
{
    private final Viewport       m_view;

    private int                  m_wide;

    private int                  m_high;

    private boolean              m_flex;

    private AutoScaleType        m_auto;

    private LienzoHandlerManager m_events;

    private Cursor               m_widget_cursor;

    private Cursor               m_active_cursor;

    private Cursor               m_normal_cursor;

    private Cursor               m_select_cursor;

    private DragMouseControl     m_drag_mouse_control;

    public LienzoPanel()
    {
        this(new Viewport());
    }

    public LienzoPanel(final Viewport view)
    {
        if (false == view.adopt(this))
        {
            throw new IllegalArgumentException("Viewport is already adopted.");
        }
        m_view = view;

        setWidth("100%");

        setHeight("100%");

        doPostCTOR(Window.getClientWidth(), Window.getClientHeight(), true);
    }

    public LienzoPanel(final int wide, final int high)
    {
        this(new Viewport(wide, high), wide, high);
    }

    public LienzoPanel(final Scene scene, final int wide, final int high)
    {
        this(new Viewport(scene, wide, high), wide, high);
    }

    public LienzoPanel(final Viewport view, final int wide, final int high)
    {
        if (false == view.adopt(this))
        {
            throw new IllegalArgumentException("Viewport is already adopted.");
        }
        m_view = view;

        doPostCTOR(wide, high, false);
    }

    private final void doPostCTOR(final int wide, final int high, final boolean flex)
    {
        m_wide = wide;

        m_high = high;

        m_flex = flex;

        m_drag_mouse_control = DragMouseControl.LEFT_MOUSE_ONLY;

        if (LienzoCore.IS_CANVAS_SUPPORTED)
        {
            getElement().appendChild(m_view.getElement());

            setPixelSize(wide, high);

            m_widget_cursor = CursorMap.get().lookup(getElement().getStyle().getCursor());

            m_events = new LienzoHandlerManager(this);
        }
        else
        {
            add(new Label(MessageConstants.MESSAGES.getCanvasUnsupportedMessage()));

            m_events = null;
        }

        getElement().getStyle().setOutlineStyle(Style.OutlineStyle.NONE);
    }

    @Override
    public void onResize()
    {
        if (m_flex)
        {
            if (getParent() != null)
            {
                int wide = getParent().getOffsetWidth();

                int high = getParent().getOffsetHeight();

                if ((wide != 0) && (high != 0))
                {
                    setPixelSize(wide, high);
                }
            }
        }
    }

    @Override
    public void onAttach()
    {
        super.onAttach();

        onResize();
    }

    public void destroy() {
        removeAll();
        removeFromParent();
        m_auto = null;
        m_events.destroy();
        m_events = null;
        m_widget_cursor = null;
        m_active_cursor = null;
        m_normal_cursor = null;
        m_select_cursor = null;
        m_drag_mouse_control = null;
    }

    public LienzoPanel setAutoScale(final AutoScaleType type)
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

    public LienzoPanel setDragMouseButtons(DragMouseControl controls)
    {
        m_drag_mouse_control = controls;

        return this;
    }

    public DragMouseControl getDragMouseButtons()
    {
        return m_drag_mouse_control;
    }

    public LienzoPanel setTransform(final Transform transform)
    {
        getViewport().setTransform(transform);

        return this;
    }

    public LienzoPanel draw()
    {
        getViewport().draw();

        return this;
    }

    public LienzoPanel batch()
    {
        getViewport().batch();

        return this;
    }

    /**
     * Adds a layer to the {@link LienzoPanel}.
     * It should be noted that this action will cause a {@link Layer} draw operation, painting all children in the Layer.
     *
     * @param layer
     * @return
     */
    public LienzoPanel add(final Layer layer)
    {
        getScene().add(layer);

        return this;
    }

    /**
     * Adds a layer to the {@link LienzoPanel}.
     * It should be noted that this action will cause a {@link Layer} draw operation, painting all children in the Layer.
     *
     * @param layer
     * @return
     */
    public LienzoPanel add(final Layer layer, final Layer... layers)
    {
        add(layer);

        for (Layer node : layers)
        {
            add(node);
        }
        return this;
    }

    /**
     * Removes a layer from the {@link LienzoPanel}.
     * It should be noted that this action will cause a {@link Layer} draw operation, painting all children in the Layer.
     *
     * @param layer
     * @return
     */
    public LienzoPanel remove(final Layer layer)
    {
        getScene().remove(layer);

        return this;
    }

    /**
     * Removes all layer from the {@link LienzoPanel}.

     * @return
     */
    public LienzoPanel removeAll()
    {
        getScene().removeAll();

        return this;
    }

    /**
     * Sets the size in pixels of the {@link LienzoPanel}
     * Sets the size in pixels of the {@link Viewport} contained and automatically added to the instance of the {@link LienzoPanel}
     */
    @Override
    public void setPixelSize(final int wide, final int high)
    {
        super.setPixelSize(wide, high);

        getViewport().setPixelSize(wide, high);

        getViewport().draw();
    }

    /**
     * Sets the type of cursor to be used when hovering above the element.
     * @param cursor
     */
    public LienzoPanel setCursor(final Cursor cursor)
    {
        if ((cursor != null) && (cursor != m_active_cursor))
        {
            m_active_cursor = cursor;

            // Need to defer this, sometimes, if the browser is busy, etc, changing cursors does not take effect till events are done processing

            Scheduler.get().scheduleDeferred(new ScheduledCommand()
            {
                @Override
                public void execute()
                {
                    getElement().getStyle().setCursor(m_active_cursor);
                }
            });
        }
        return this;
    }

    public LienzoPanel setNormalCursor(final Cursor cursor)
    {
        m_normal_cursor = cursor;

        return this;
    }

    public Cursor getNormalCursor()
    {
        return m_normal_cursor;
    }

    public LienzoPanel setSelectCursor(final Cursor cursor)
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
     * Returns the {@link Viewport} main {@link Scene}
     * @return
     */
    public Scene getScene()
    {
        return getViewport().getScene();
    }

    /**
     * Returns the automatically create {@link Viewport} instance.
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
     * Sets the {@link Viewport} background {@link Layer}
     *
     * @param layer
     */
    public LienzoPanel setBackgroundLayer(final Layer layer)
    {
        getViewport().setBackgroundLayer(layer);

        return this;
    }

    /**
     * Returns the {@link Viewport} Drag {@link Layer}
     *
     * @return
     */
    public Layer getDragLayer()
    {
        return getViewport().getDragLayer();
    }

    /**
     * Gets the width in pixels.
     *
     * @return
     */
    public int getWidth()
    {
        return m_wide;
    }

    /**
     * Returns the height.
     *
     * @return
     */
    public int getHeight()
    {
        return m_high;
    }

    /**
     * Returns a JSON representation of the {@link Viewport} children.
     * @return
     */
    public String toJSONString()
    {
        return getViewport().toJSONString();
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
     * Sets the background color of the LienzoPanel.
     *
     * @param color String
     * @return this LienzoPanel
     */
    public LienzoPanel setBackgroundColor(final String color)
    {
        if (null != color)
        {
            getElement().getStyle().setBackgroundColor(color);
        }
        return this;
    }

    /**
     * Sets the background color of the LienzoPanel.
     *
     * @param color IColor, i.e. ColorName or Color
     * @return this LienzoPanel
     */
    public LienzoPanel setBackgroundColor(final IColor color)
    {
        if (null != color)
        {
            setBackgroundColor(color.getColorString());
        }
        return this;
    }

    /**
     * Returns the background color of this LienzoPanel.
     * Will return null if no color was set, in which case it's probably "white",
     * unless it was changed via CSS rules.
     *
     * @return String
     */
    public String getBackgroundColor()
    {
        return getElement().getStyle().getBackgroundColor();
    }

    /**
     * Returns the {@link Mediators} for this panels {@link Viewport}.
     * Mediators can be used to e.g. to add zoom operations.
     *
     * @return Mediators
     */
    public Mediators getMediators()
    {
        return getViewport().getMediators();
    }

    /**
     * Add a mediator to the stack of {@link Mediators} for this panels {@link Viewport}.
     * The one that is added last, will be called first.
     *
     * Mediators can be used to e.g. to add zoom operations.
     *
     * @param mediator IMediator
     */
    public LienzoPanel pushMediator(final IMediator mediator)
    {
        getViewport().pushMediator(mediator);

        return this;
    }

    public static native void enableWindowMouseWheelScroll(boolean enabled)
    /*-{
		$wnd.mousewheel = function() {
			return enabled;
		}
    }-*/;
}
