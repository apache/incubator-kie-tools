/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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
import com.ait.lienzo.client.core.shape.Scene;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.util.CursorMap;
import com.ait.lienzo.shared.core.types.DataURLType;
import com.ait.lienzo.shared.core.types.IColor;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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
    private LienzoHandlerManager m_events;

    private int                  m_wide;

    private int                  m_high;

    private boolean              m_flex;

    private Viewport             m_view;

    private Cursor               m_widget_cursor;

    private Cursor               m_active_cursor;

    private Cursor               m_normal_cursor;

    private Cursor               m_select_cursor;

    public LienzoPanel()
    {
        this(new Viewport());
    }

    public LienzoPanel(Viewport view)
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

    public LienzoPanel(int wide, int high)
    {
        this(new Viewport(), wide, high);
    }

    public LienzoPanel(Scene scene, int wide, int high)
    {
        this(new Viewport(scene, wide, high), wide, high);
    }

    public LienzoPanel(Viewport view, int wide, int high)
    {
        if (false == view.adopt(this))
        {
            throw new IllegalArgumentException("Viewport is already adopted.");
        }
        m_view = view;

        doPostCTOR(wide, high, false);
    }

    void doPostCTOR(int wide, int high, boolean flex)
    {
        m_wide = wide;

        m_high = high;

        m_flex = flex;

        if (LienzoCore.get().isCanvasSupported())
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
    }

    @Override
    public void onResize()
    {
        if (m_flex)
        {
            int wide = getParent().getOffsetWidth();

            int high = getParent().getOffsetHeight();

            if ((wide != 0) && (high != 0))
            {
                setPixelSize(wide, high);
            }
        }
    }

    @Override
    public void onAttach()
    {
        super.onAttach();

        onResize();
    }

    /**
     * Adds a layer to the {@link LienzoPanel}.
     * It should be noted that this action will cause a {@link Layer} draw operation, painting all children in the Layer.
     * 
     * @param layer
     * @return
     */
    public void add(Layer layer)
    {
        getScene().add(layer);
    }

    /**
     * Removes a layer from the {@link LienzoPanel}.
     * It should be noted that this action will cause a {@link Layer} draw operation, painting all children in the Layer.
     * 
     * @param layer
     * @return
     */
    public void remove(Layer layer)
    {
        getScene().remove(layer);
    }

    /**
     * Removes all layer from the {@link LienzoPanel}.
     * It should be noted that this action will cause a {@link Layer} draw operation, painting all children in the Layer.
     * 
     * @param layer
     * @return
     */
    public void removeAll()
    {
        getScene().removeAll();
    }

    /**
     * Sets the size in pixels of the {@link LienzoPanel}
     * Sets the size in pixels of the {@link Viewport} contained and automatically added to the instance of the {@link LienzoPanel}
     */
    @Override
    public void setPixelSize(int wide, int high)
    {
        super.setPixelSize(wide, high);

        m_view.setPixelSize(wide, high);

        m_view.draw();
    }

    /**
     * Sets the type of cursor to be used when hovering above the element.
     * @param cursor
     */
    public void setCursor(Cursor cursor)
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
    }

    public void setNormalCursor(Cursor cursor)
    {
        m_normal_cursor = cursor;
    }

    public Cursor getNormalCursor()
    {
        return m_normal_cursor;
    }

    public void setSelectCursor(Cursor cursor)
    {
        m_select_cursor = cursor;
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
        return m_view.getScene();
    }

    /**
     * Returns the automatically create {@link Viewport} instance.
     * @return
     */
    public Viewport getViewport()
    {
        return m_view;
    }

    /**
     * Sets the {@link Viewport} background {@link Layer}
     * 
     * @param layer
     */
    public void setBackgroundLayer(Layer layer)
    {
        m_view.setBackgroundLayer(layer);
    }

    /**
     * Returns the {@link Viewport} Drag {@link Layer}
     * 
     * @return
     */
    public Layer getDragLayer()
    {
        return m_view.getDraglayer();
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
        return m_view.toJSONString();
    }

    public final String toDataURL()
    {
        return m_view.toDataURL();
    }

    public final String toDataURL(boolean includeBackgroundLayer)
    {
        return m_view.toDataURL(includeBackgroundLayer);
    }

    public final String toDataURL(DataURLType mimetype)
    {
        return m_view.toDataURL(mimetype);
    }

    public final String toDataURL(DataURLType mimetype, boolean includeBackgroundLayer)
    {
        return m_view.toDataURL(mimetype, includeBackgroundLayer);
    }

    /**
     * Sets the background color of the LienzoPanel.
     * 
     * @param color String
     * @return this LienzoPanel
     */
    public void setBackgroundColor(String color)
    {
        getElement().getStyle().setBackgroundColor(color);
    }

    /**
     * Sets the background color of the LienzoPanel.
     * 
     * @param color IColor, i.e. ColorName or Color
     * @return this LienzoPanel
     */
    public void setBackgroundColor(IColor color)
    {
        setBackgroundColor(color.getColorString());
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
        return m_view.getMediators();
    }

    /**
     * Add a mediator to the stack of {@link Mediators} for this panels {@link Viewport}.
     * The one that is added last, will be called first.
     * 
     * Mediators can be used to e.g. to add zoom operations.
     * 
     * @param mediator IMediator
     */
    public void pushMediator(IMediator mediator)
    {
        m_view.pushMediator(mediator);
    }

    public static native void enableWindowMouseWheelScroll(boolean enabled)
    /*-{
		$wnd.mousewheel = function() {
			return enabled;
		}
    }-*/;
}