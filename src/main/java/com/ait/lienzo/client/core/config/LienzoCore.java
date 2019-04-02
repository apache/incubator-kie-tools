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

package com.ait.lienzo.client.core.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.types.DashArray;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.IColor;
import com.ait.lienzo.shared.core.types.ImageSelectionMode;
import com.ait.lienzo.shared.core.types.LayerClearMode;
import com.ait.lienzo.shared.core.types.LineCap;
import com.ait.tooling.common.api.java.util.StringOps;
import com.ait.tooling.common.api.types.IStringValued;
import com.ait.tooling.nativetools.client.util.Client;
import com.ait.tooling.nativetools.client.util.Console;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * A Global Configuration Manager.
 */
public final class LienzoCore
{
    public static final List<Attribute>    STANDARD_TRANSFORMING_ATTRIBUTES = Collections.unmodifiableList(Arrays.asList(Attribute.X, Attribute.Y, Attribute.SCALE, Attribute.SHEAR, Attribute.ROTATION, Attribute.OFFSET));

    public static final List<Attribute>    VIEWPORT_TRANSFORMING_ATTRIBUTES = Collections.unmodifiableList(Arrays.asList(Attribute.X, Attribute.Y, Attribute.SCALE, Attribute.SHEAR, Attribute.ROTATION, Attribute.OFFSET));

    private static final LienzoCore        INSTANCE                         = new LienzoCore();

    public static final double             DEFAULT_FONT_SIZE                = 48;

    public static final double             DEFAULT_CONNECTOR_OFFSET         = 10;

    public static final String             DEFAULT_FONT_STYLE               = "normal";

    public static final String             DEFAULT_FONT_FAMILY              = "Helvetica";

    public static final boolean            IS_CANVAS_SUPPORTED              = Canvas.isSupported();

    private double                         m_deviceScale                    = 0;

    private double                         m_strokeWidth                    = 1;

    private double                         m_backingStorePixelRatio         = 0;

    private double                         m_defaultConnectorOffset         = DEFAULT_CONNECTOR_OFFSET;

    private String                         m_strokeColor                    = "black";

    private boolean                        m_fillShapeForSelection          = true;

    private boolean                        m_globalLineDashSupport          = true;

    private boolean                        m_scaledCanvasForRetina          = true;

    private boolean                        m_nativeLineDashSupport          = false;

    private boolean                        m_enableBlobIfSupported          = true;

    private boolean                        m_nativeLineDashExamine          = false;

    private boolean                        m_hidpiEnabled                   = false;

    private Cursor                         m_normal_cursor                  = Cursor.DEFAULT;

    private Cursor                         m_select_cursor                  = Cursor.CROSSHAIR;

    private LayerClearMode                 m_layerClearMode                 = LayerClearMode.CLEAR;

    private ImageSelectionMode             m_imageSelectionMode             = ImageSelectionMode.SELECT_NON_TRANSPARENT;

    private final ArrayList<ILienzoPlugin> m_plugins                        = new ArrayList<ILienzoPlugin>();

    private LienzoCore()
    {
        RootPanel.get().getElement().getStyle().setProperty("webkitTapHighlightColor", "rgba(0,0,0,0)");
    }

    public static final LienzoCore get()
    {
        return INSTANCE;
    }

    public final boolean addPlugin(final ILienzoPlugin plugin)
    {
        if (null == plugin)
        {
            return false;
        }
        log("Lienzo adding plugin: " + plugin.getNameSpace());

        if (m_plugins.contains(plugin))
        {
            error("Lienzo plugin " + plugin.getNameSpace() + " already added.");

            return false;
        }
        for (ILienzoPlugin p : m_plugins)
        {
            if (plugin.getNameSpace().equals(p.getNameSpace()))
            {
                error("Lienzo plugin " + plugin.getNameSpace() + " with name name space already added.");

                return false;
            }
        }
        m_plugins.add(plugin);

        return true;
    }

    public final ILienzoPlugin getPlugin(String name)
    {
        if (null != (name = StringOps.toTrimOrNull(name)))
        {
            for (ILienzoPlugin p : m_plugins)
            {
                if (p.getNameSpace().equals(name))
                {
                    return p;
                }
            }
        }
        return null;
    }

    public final List<ILienzoPlugin> getPlugins()
    {
        return Collections.unmodifiableList(m_plugins);
    }

    public final IFactory<?> getFactory(final IStringValued type)
    {
        return getFactory((null != type) ? type.getValue() : null);
    }

    public final IFactory<?> getFactory(String name)
    {
        if (null != (name = StringOps.toTrimOrNull(name)))
        {
            for (ILienzoPlugin p : m_plugins)
            {
                final IFactory<?> factory = p.getFactory(name);

                if (null != factory)
                {
                    return factory;
                }
            }
        }
        return null;
    }

    public final void log(final String message)
    {
        Client.get().info(message);
    }

    public final void info(final String message)
    {
        Client.get().info(message);
    }

    public final void fine(final String message)
    {
        Client.get().fine(message);
    }

    public final void warn(final String message)
    {
        Client.get().warn(message);
    }

    public final void error(final String message)
    {
        Client.get().error(message);
    }

    public final void error(final String message, final Throwable e)
    {
        Client.get().error(message, e);
    }

    public final void severe(final String message)
    {
        Client.get().severe(message);
    }

    public final void severe(final String message, final Throwable e)
    {
        Client.get().severe(message, e);
    }

    public final void stack(final String message, final Throwable e)
    {
        if (e instanceof UmbrellaException)
        {
            final UmbrellaException u = ((UmbrellaException) e);

            for (Throwable t : u.getCauses())
            {
                stack(message, t);
            }
            return;
        }
        Console.get().error(message, e);

        for (StackTraceElement s : e.getStackTrace())
        {
            Console.get().error(s.toString());
        }
    }

    public final String getUserAgent()
    {
        return Window.Navigator.getUserAgent();
    }

    public final boolean isSafari()
    {
        String ua = getUserAgent();

        if ((ua.indexOf("Safari") >= 0) && (ua.indexOf("Chrome") < 0))
        {
            return true;
        }
        return false;
    }

    public final boolean isSafariBroken()
    {
        String ua = getUserAgent();

        if ((ua.indexOf("Safari") >= 0) && (ua.indexOf("Chrome") < 0))
        {
            if (ua.indexOf("OS X") >= 0)
            {
                int p = ua.indexOf("Version/");

                if (p >= 0)
                {
                    ua = ua.substring(p + "Version/".length());

                    p = ua.indexOf(" ");

                    if (p >= 0)
                    {
                        ua = ua.substring(0, p).replaceAll("\\.", "");

                        try
                        {
                            if (Integer.parseInt(ua) > 902)
                            {
                                return false;
                            }
                        }
                        catch (Exception e)
                        {
                            error("isSafariBroken(" + ua + ")", e);
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    public final boolean isFirefox()
    {
        String ua = getUserAgent();

        // IE 11 Says it is Mozilla!!! Check for Trident

        if (((ua.indexOf("Mozilla") >= 0) || (ua.indexOf("Firefox") >= 0)) && (ua.indexOf("Trident") < 0))
        {
            return (false == isSafari());
        }
        return false;
    }

    public final boolean isBlobAPIEnabled()
    {
        return m_enableBlobIfSupported;
    }

    public final LienzoCore setBlobAPIEnabled(final boolean enabled)
    {
        m_enableBlobIfSupported = enabled;

        return this;
    }

    public final LienzoCore setScaledCanvasForRetina(final boolean enabled)
    {
        m_scaledCanvasForRetina = enabled;

        return this;
    }

    public final boolean isScaledCanvasForRetina()
    {
        return m_scaledCanvasForRetina;
    }

    public final LienzoCore setDefaultFillShapeForSelection(final boolean fill)
    {
        m_fillShapeForSelection = fill;

        return this;
    }

    public final ImageSelectionMode getDefaultImageSelectionMode()
    {
        return m_imageSelectionMode;
    }

    public final LienzoCore setDefaultImageSelectionMode(final ImageSelectionMode mode)
    {
        if (null == mode)
        {
            m_imageSelectionMode = ImageSelectionMode.SELECT_NON_TRANSPARENT;
        }
        else
        {
            m_imageSelectionMode = mode;
        }
        return this;
    }

    public final boolean getDefaultFillShapeForSelection()
    {
        return m_fillShapeForSelection;
    }

    public final LienzoCore setDefaultStrokeWidth(final double width)
    {
        if (width > 0)
        {
            m_strokeWidth = width;
        }
        else
        {
            m_strokeWidth = 1;
        }
        return this;
    }

    public final double getDefaultStrokeWidth()
    {
        return m_strokeWidth;
    }

    public final LienzoCore setDefaultStrokeColor(final IColor color)
    {
        if (color != null)
        {
            m_strokeColor = color.getColorString();
        }
        else
        {
            m_strokeColor = "black";
        }
        return this;
    }

    public final String getDefaultStrokeColor()
    {
        return m_strokeColor;
    }

    public final double getDefaultConnectorOffset()
    {
        return m_defaultConnectorOffset;
    }

    public final LienzoCore setDefaultConnectorOffset(final double offset)
    {
        if (offset >= 0)
        {
            m_defaultConnectorOffset = offset;
        }
        return this;
    }

    /**
     * Returns true if the Canvas element is supported.
     * @return
     */
    public final boolean isCanvasSupportedx()
    {
        return IS_CANVAS_SUPPORTED;
    }

    public final boolean isLineDashSupported()
    {
        return ((isGlobalLineDashSupported()) && (isNativeLineDashSupported()));
    }

    public final boolean isGlobalLineDashSupported()
    {
        return m_globalLineDashSupport;
    }

    public final LienzoCore setGlobalLineDashSupported(final boolean supported)
    {
        m_globalLineDashSupport = supported;

        return this;
    }

    public final boolean isNativeLineDashSupported()
    {
        if (false == m_nativeLineDashExamine)
        {
            m_nativeLineDashSupport = examineNativeLineDashSupported();

            m_nativeLineDashExamine = true;
        }
        return m_nativeLineDashSupport;
    }

    public final double getDefaultFontSize()
    {
        return DEFAULT_FONT_SIZE;
    }

    public final String getDefaultFontStyle()
    {
        return DEFAULT_FONT_STYLE;
    }

    public final String getDefaultFontFamily()
    {
        return DEFAULT_FONT_FAMILY;
    }

    public final LayerClearMode getLayerClearMode()
    {
        return m_layerClearMode;
    }

    public final LienzoCore setLayerClearMode(final LayerClearMode mode)
    {
        if (null != mode)
        {
            m_layerClearMode = mode;
        }
        else
        {
            m_layerClearMode = LayerClearMode.CLEAR;
        }
        return this;
    }

    public final native double getDevicePixelRatio()
    /*-{
		return $wnd.devicePixelRatio || 1;
    }-*/;

    public final double getBackingStorePixelRatio()
    {
        if (m_backingStorePixelRatio != 0)
        {
            return m_backingStorePixelRatio;
        }
        if (IS_CANVAS_SUPPORTED)
        {
            try
            {
                m_backingStorePixelRatio = new ScratchPad(1, 1).getContext().getBackingStorePixelRatio();
            }
            catch (Exception e)
            {
                m_backingStorePixelRatio = 1;

                error("Backing Store Pixel Ratio failed ", e);
            }
        }
        else
        {
            m_backingStorePixelRatio = 1;
        }
        return m_backingStorePixelRatio;
    }

    public final double getDeviceScale()
    {
        if (m_deviceScale != 0)
        {
            return m_deviceScale;
        }
        return (m_deviceScale = getDevicePixelRatio() / getBackingStorePixelRatio());
    }

    public boolean isHidpiEnabled() {
        return m_hidpiEnabled;
    }

    public boolean setHidpiEnabled(final boolean hidpiEnabled) {
        return m_hidpiEnabled = hidpiEnabled;
    }

    private final boolean examineNativeLineDashSupported()
    {
        if (IS_CANVAS_SUPPORTED)
        {
            try
            {
                final ScratchPad scratch = new ScratchPad(20, 10);

                final Context2D context = scratch.getContext();

                context.setStrokeWidth(10);

                context.setLineCap(LineCap.BUTT);

                context.setStrokeColor(ColorName.BLUE);

                context.beginPath();

                context.moveTo(0, 5);

                context.lineTo(20, 5);

                context.stroke();

                context.setStrokeColor(ColorName.RED);

                context.setLineDash(new DashArray(5, 5));

                context.beginPath();

                context.moveTo(0, 5);

                context.lineTo(20, 5);

                context.stroke();

                final ImageData backing = context.getImageData(0, 0, 20, 10);

                if (null != backing)
                {
                    if ((backing.getRedAt(3, 5) == 255) && (backing.getBlueAt(3, 5) == 0) && (backing.getGreenAt(3, 5) == 0))
                    {
                        if ((backing.getRedAt(8, 5) == 0) && (backing.getBlueAt(8, 5) == 255) && (backing.getGreenAt(8, 5) == 0))
                        {
                            return true;
                        }
                    }
                }
            }
            catch (Exception e)
            {
                error("Line Dash test failed ", e);// FF 22 dev mode does not like line dashes
            }
        }
        return false;
    }

    public final LienzoCore setDefaultNormalCursor(final Cursor cursor)
    {
        m_normal_cursor = cursor;
        return this;
    }

    public final Cursor getDefaultNormalCursor()
    {
        return m_normal_cursor;
    }

    public final LienzoCore setDefaultSelectCursor(final Cursor cursor)
    {
        m_select_cursor = cursor;
        return this;
    }

    public final Cursor getDefaultSelectCursor()
    {
        return m_select_cursor;
    }
}
