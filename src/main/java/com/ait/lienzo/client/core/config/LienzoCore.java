/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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
import java.util.Collection;
import java.util.Collections;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.event.IAttributesChangedBatcher;
import com.ait.lienzo.client.core.types.DashArray;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.core.util.ScratchCanvas;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.IColor;
import com.ait.lienzo.shared.core.types.ImageSelectionMode;
import com.ait.lienzo.shared.core.types.LayerClearMode;
import com.ait.lienzo.shared.core.types.LineCap;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.user.client.Window;

/**
 * A Global Configuration Manager.
 */
public final class LienzoCore
{
    private static final LienzoCore   INSTANCE                 = new LienzoCore();

    public static final double        DEFAULT_FONT_SIZE        = 48;

    public static final double        DEFAULT_CONNECTOR_OFFSET = 10;

    public static final String        DEFAULT_FONT_STYLE       = "normal";

    public static final String        DEFAULT_FONT_FAMILY      = "Helvetica";

    private double                    m_strokeWidth            = 1;

    private double                    m_backingStorePixelRatio = 0;

    private double                    m_defaultConnectorOffset = DEFAULT_CONNECTOR_OFFSET;

    private String                    m_strokeColor            = "black";

    private boolean                   m_fillShapeForSelection  = true;

    private boolean                   m_globalLineDashSupport  = true;

    private boolean                   m_scaledCanvasForRetina  = true;

    private boolean                   m_nativeLineDashSupport  = false;

    private boolean                   m_enableBlobIfSupported  = true;

    private boolean                   m_nativeLineDashExamine  = false;

    private Cursor                    m_normal_cursor          = Cursor.DEFAULT;

    private Cursor                    m_select_cursor          = Cursor.CROSSHAIR;

    private final boolean             m_canvasSupported        = Canvas.isSupported();

    private LayerClearMode            m_layerClearMode         = LayerClearMode.CLEAR;

    private ImageSelectionMode        m_imageSelectionMode     = ImageSelectionMode.SELECT_NON_TRANSPARENT;

    private IAttributesChangedBatcher m_batcher;

    private ArrayList<ILienzoPlugin>  m_plugins                = new ArrayList<ILienzoPlugin>();

    private LienzoCore()
    {
    }

    public static final LienzoCore get()
    {
        return INSTANCE;
    }

    public final void addPlugin(ILienzoPlugin plugin)
    {
        if (GWT.isScript())
        {
            log("Lienzo adding plugin: " + plugin.getNameSpace());
        }
        else
        {
            GWT.log("Lienzo adding plugin: " + plugin.getNameSpace());
        }
        m_plugins.add(plugin);
    }

    public final Collection<ILienzoPlugin> getPlugins()
    {
        return Collections.unmodifiableCollection(m_plugins);
    }

    public final native void log(String message)
    /*-{
        if ($wnd.console) {
            $wnd.console.log(message);
        }
    }-*/;

    public final native void error(String message)
    /*-{
        if ($wnd.console) {
            $wnd.console.error(message);
        }
    }-*/;

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

    public final LienzoCore setBlobAPIEnabled(boolean enabled)
    {
        m_enableBlobIfSupported = enabled;

        return this;
    }

    public final LienzoCore setAttributesChangedBatcher(final IAttributesChangedBatcher batcher)
    {
        if (null != batcher)
        {
            m_batcher = batcher;
        }
        else
        {
            m_batcher = null;
        }
        return this;
    }

    public final IAttributesChangedBatcher getAttributesChangedBatcher()
    {
        if (null != m_batcher)
        {
            return m_batcher.copy();
        }
        else
        {
            return null;
        }
    }

    public final LienzoCore setScaledCanvasForRetina(boolean enabled)
    {
        m_scaledCanvasForRetina = enabled;

        return this;
    }

    public final boolean isScaledCanvasForRetina()
    {
        return m_scaledCanvasForRetina;
    }

    public final LienzoCore setDefaultFillShapeForSelection(boolean fill)
    {
        m_fillShapeForSelection = fill;

        return this;
    }

    public final ImageSelectionMode getDefaultImageSelectionMode()
    {
        return m_imageSelectionMode;
    }

    public final LienzoCore setDefaultImageSelectionMode(ImageSelectionMode mode)
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

    public final LienzoCore setDefaultStrokeWidth(double width)
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

    public final LienzoCore setDefaultStrokeColor(IColor color)
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

    public final LienzoCore setDefaultConnectorOffset(double offset)
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
    public final boolean isCanvasSupported()
    {
        return m_canvasSupported;
    }

    public final boolean isLineDashSupported()
    {
        return ((isGlobalLineDashSupported()) && (isNativeLineDashSupported()));
    }

    public final boolean isGlobalLineDashSupported()
    {
        return m_globalLineDashSupport;
    }

    public final LienzoCore setGlobalLineDashSupported(boolean supported)
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

    public final LienzoCore setLayerClearMode(LayerClearMode mode)
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
        if (isCanvasSupported())
        {
            try
            {
                ScratchCanvas scratch = new ScratchCanvas(1, 1);

                m_backingStorePixelRatio = scratch.getContext().getBackingStorePixelRatio();
            }
            catch (Exception e)
            {
                m_backingStorePixelRatio = 1;

                GWT.log("Backing Store Pixel Ratio failed ", e);
            }
        }
        else
        {
            m_backingStorePixelRatio = 1;
        }
        return m_backingStorePixelRatio;
    }

    private final boolean examineNativeLineDashSupported()
    {
        if (isCanvasSupported())
        {
            try
            {
                ScratchCanvas scratch = new ScratchCanvas(20, 10);

                Context2D context = scratch.getContext();

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

                ImageData backing = context.getImageData(0, 0, 20, 10);

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
                GWT.log("Line Dash test failed ", e); // FF 22 dev mode does not like line dashes
            }
        }
        return false;
    }

    public final LienzoCore setDefaultNormalCursor(Cursor cursor)
    {
        if (null != cursor)
        {
            m_normal_cursor = cursor;
        }
        else
        {
            m_normal_cursor = Cursor.DEFAULT;
        }
        return this;
    }

    public final Cursor getDefaultNormalCursor()
    {
        return m_normal_cursor;
    }

    public final LienzoCore setSefaultSelectCursor(Cursor cursor)
    {
        if (null != cursor)
        {
            m_select_cursor = cursor;
        }
        else
        {
            m_select_cursor = Cursor.CROSSHAIR;
        }
        return this;
    }

    public final Cursor getDefaultSelectCursor()
    {
        return m_select_cursor;
    }
}
