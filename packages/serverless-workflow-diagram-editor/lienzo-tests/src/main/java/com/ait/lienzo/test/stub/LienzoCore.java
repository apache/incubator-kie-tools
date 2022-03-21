package com.ait.lienzo.test.stub;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.IColor;
import com.ait.lienzo.shared.core.types.ImageSelectionMode;
import com.ait.lienzo.shared.core.types.LayerClearMode;
import com.ait.lienzo.test.annotation.StubClass;
import com.google.gwt.dom.client.Style;

@StubClass("com.ait.lienzo.client.core.config.LienzoCore")
public class LienzoCore {

    public static final List<Attribute> STANDARD_TRANSFORMING_ATTRIBUTES = Collections.unmodifiableList(Arrays.asList(Attribute.X, Attribute.Y, Attribute.SCALE, Attribute.SHEAR, Attribute.ROTATION, Attribute.OFFSET));

    public static final List<Attribute> VIEWPORT_TRANSFORMING_ATTRIBUTES = Collections.unmodifiableList(Arrays.asList(Attribute.X, Attribute.Y, Attribute.SCALE, Attribute.SHEAR, Attribute.ROTATION, Attribute.OFFSET));
    public static final double DEFAULT_FONT_SIZE = 48;
    public static final double DEFAULT_CONNECTOR_OFFSET = 10;
    public static final String DEFAULT_FONT_STYLE = "normal";
    public static final String DEFAULT_FONT_FAMILY = "Helvetica";
    public static final boolean IS_CANVAS_SUPPORTED = isCanvasSupported();
    private static final LienzoCore INSTANCE = new LienzoCore();
    Style.Cursor m_normal_cursor, m_select_cursor;
    private double m_deviceScale = 0;
    private double m_strokeWidth = 1;
    private double m_backingStorePixelRatio = 0;
    private double m_defaultConnectorOffset = DEFAULT_CONNECTOR_OFFSET;
    private String m_strokeColor = "black";
    private boolean m_fillShapeForSelection = true;
    private boolean m_globalLineDashSupport = true;
    private boolean m_scaledCanvasForRetina = true;
    private boolean m_nativeLineDashSupport = false;
    private boolean m_enableBlobIfSupported = true;
    private boolean m_nativeLineDashExamine = false;
    private boolean m_hidpiEnabled = false;
    private LayerClearMode m_layerClearMode = LayerClearMode.CLEAR;
    private ImageSelectionMode m_imageSelectionMode = ImageSelectionMode.SELECT_NON_TRANSPARENT;

    private LienzoCore() {
    }

    private static boolean isCanvasSupported() {

        return true;
    }

    public static final LienzoCore get() {
        return INSTANCE;
    }

    public final void log(final String message) {
        System.out.println(message);
    }

    public final void info(final String message) {
        log(message);
    }

    public final void warn(final String message) {
        log(message);
    }

    public final void error(final String message) {
        log(message);
    }

    public final void error(final String message, final Throwable e) {
        log(message);
    }

    public final String getUserAgent() {
        return "gecko";
    }

    public final boolean isSafari() {
        String ua = getUserAgent();

        if ((ua.indexOf("Safari") >= 0) && (ua.indexOf("Chrome") < 0)) {
            return true;
        }
        return false;
    }

    public final boolean isSafariBroken() {
        String ua = getUserAgent();

        if ((ua.indexOf("Safari") >= 0) && (ua.indexOf("Chrome") < 0)) {
            if (ua.indexOf("OS X") >= 0) {
                int p = ua.indexOf("Version/");

                if (p >= 0) {
                    ua = ua.substring(p + "Version/".length());

                    p = ua.indexOf(" ");

                    if (p >= 0) {
                        ua = ua.substring(0, p).replaceAll("\\.", "");

                        try {
                            if (Integer.parseInt(ua) > 902) {
                                return false;
                            }
                        } catch (Exception e) {
                            error("isSafariBroken(" + ua + ")", e);
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    public final boolean isFirefox() {
        String ua = getUserAgent();

        // IE 11 Says it is Mozilla!!! Check for Trident

        if (((ua.indexOf("Mozilla") >= 0) || (ua.indexOf("Firefox") >= 0)) && (ua.indexOf("Trident") < 0)) {
            return (false == isSafari());
        }
        return false;
    }

    public final boolean isBlobAPIEnabled() {
        return m_enableBlobIfSupported;
    }

    public final LienzoCore setBlobAPIEnabled(final boolean enabled) {
        m_enableBlobIfSupported = enabled;

        return this;
    }

    public final boolean isScaledCanvasForRetina() {
        return m_scaledCanvasForRetina;
    }

    public final LienzoCore setScaledCanvasForRetina(final boolean enabled) {
        m_scaledCanvasForRetina = enabled;

        return this;
    }

    public final ImageSelectionMode getDefaultImageSelectionMode() {
        return m_imageSelectionMode;
    }

    public final LienzoCore setDefaultImageSelectionMode(final ImageSelectionMode mode) {
        if (null == mode) {
            m_imageSelectionMode = ImageSelectionMode.SELECT_NON_TRANSPARENT;
        } else {
            m_imageSelectionMode = mode;
        }
        return this;
    }

    public final boolean getDefaultFillShapeForSelection() {
        return m_fillShapeForSelection;
    }

    public final LienzoCore setDefaultFillShapeForSelection(final boolean fill) {
        m_fillShapeForSelection = fill;

        return this;
    }

    public final double getDefaultStrokeWidth() {
        return m_strokeWidth;
    }

    public final LienzoCore setDefaultStrokeWidth(final double width) {
        if (width > 0) {
            m_strokeWidth = width;
        } else {
            m_strokeWidth = 1;
        }
        return this;
    }

    public final String getDefaultStrokeColor() {
        return m_strokeColor;
    }

    public final LienzoCore setDefaultStrokeColor(final IColor color) {
        if (color != null) {
            m_strokeColor = color.getColorString();
        } else {
            m_strokeColor = "black";
        }
        return this;
    }

    public final double getDefaultConnectorOffset() {
        return m_defaultConnectorOffset;
    }

    public final LienzoCore setDefaultConnectorOffset(final double offset) {
        if (offset >= 0) {
            m_defaultConnectorOffset = offset;
        }
        return this;
    }

    /**
     * Returns true if the Canvas element is supported.
     *
     * @return
     */
    public final boolean isCanvasSupportedx() {
        return IS_CANVAS_SUPPORTED;
    }

    public final boolean isLineDashSupported() {
        return ((isGlobalLineDashSupported()) && (isNativeLineDashSupported()));
    }

    public final boolean isGlobalLineDashSupported() {
        return m_globalLineDashSupport;
    }

    public final LienzoCore setGlobalLineDashSupported(final boolean supported) {
        m_globalLineDashSupport = supported;

        return this;
    }

    public final boolean isNativeLineDashSupported() {
        if (false == m_nativeLineDashExamine) {
            m_nativeLineDashSupport = examineNativeLineDashSupported();

            m_nativeLineDashExamine = true;
        }
        return m_nativeLineDashSupport;
    }

    public final double getDefaultFontSize() {
        return DEFAULT_FONT_SIZE;
    }

    public final String getDefaultFontStyle() {
        return DEFAULT_FONT_STYLE;
    }

    public final String getDefaultFontFamily() {
        return DEFAULT_FONT_FAMILY;
    }

    public final LayerClearMode getLayerClearMode() {
        return m_layerClearMode;
    }

    public final LienzoCore setLayerClearMode(final LayerClearMode mode) {
        if (null != mode) {
            m_layerClearMode = mode;
        } else {
            m_layerClearMode = LayerClearMode.CLEAR;
        }
        return this;
    }

    public final double getDevicePixelRatio() {
        return 1600;
    }

    public final double getBackingStorePixelRatio() {
        if (m_backingStorePixelRatio != 0) {
            return m_backingStorePixelRatio;
        }
        if (IS_CANVAS_SUPPORTED) {
            try {
                m_backingStorePixelRatio = new ScratchPad(1, 1).getContext().getBackingStorePixelRatio();
            } catch (Exception e) {
                m_backingStorePixelRatio = 1;

                error("Backing Store Pixel Ratio failed ", e);
            }
        } else {
            m_backingStorePixelRatio = 1;
        }
        return m_backingStorePixelRatio;
    }

    public final double getDeviceScale() {
        if (m_deviceScale != 0) {
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

    private final boolean examineNativeLineDashSupported() {
        return true;
    }

    public final Style.Cursor getDefaultNormalCursor() {
        return m_normal_cursor;
    }

    public final LienzoCore setDefaultNormalCursor(final Style.Cursor cursor) {
        m_normal_cursor = cursor;
        return this;
    }

    public final Style.Cursor getDefaultSelectCursor() {
        return m_select_cursor;
    }

    public final LienzoCore setDefaultSelectCursor(final Style.Cursor cursor) {
        m_select_cursor = cursor;
        return this;
    }
}

