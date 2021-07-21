package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.shared.core.types.DataURLType;
import com.ait.lienzo.test.annotation.StubClass;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLImageElement;

@StubClass("com.ait.lienzo.client.core.util.ScratchPad")
public final class ScratchPad {

    private int m_wide;

    private int m_high;

    private final Context2D m_context;

    public ScratchPad(final int wide, final int high) {
        m_wide = wide;

        m_high = high;

        m_context = new Context2D(new HTMLCanvasElement());
    }

    public final void clear() {
        com.ait.lienzo.client.core.Context2D context = getContext();

        if (null != context) {
            context.clearRect(0, 0, m_wide, m_high);
        }
    }

    public final void setPixelSize(final int wide, final int high) {
        m_wide = wide;

        m_high = high;
    }

    public final elemental2.dom.HTMLCanvasElement getElement() {

        return null;
    }

    public final int getWidth() {
        return m_wide;
    }

    public final int getHeight() {
        return m_high;
    }

    public final Context2D getContext() {
        return m_context;
    }

    public final String toDataURL() {
        return "data:,";
    }

    public final String toDataURL(DataURLType mimetype, final double quality) {
        return "data:,";
    }

    public static final String toDataURL(final HTMLImageElement element, final double quality) {
        return toDataURL(element, DataURLType.PNG, quality);
    }

    public static final String toDataURL(final HTMLImageElement element, DataURLType mimetype, final double quality) {
        if (null == mimetype) {
            mimetype = DataURLType.PNG;
        }
        ScratchPad canvas = new ScratchPad(element.width, element.height);

        canvas.getContext().drawImage(element, 0, 0, element.width, element.height);

        return canvas.toDataURL(mimetype, quality);
    }

    public static final String toDataURL(final HTMLImageElement element) {
        final ScratchPad canvas = new ScratchPad(element.width, element.height);

        canvas.getContext().drawImage(element, 0, 0);

        return canvas.toDataURL();
    }

    private static final String toDataURL(final elemental2.dom.HTMLCanvasElement element) {
        return element.toDataURL(null);
    }

    private static final String toDataURL(HTMLCanvasElement element, String mimetype, double quality) {
        return element.toDataURL(mimetype, quality);
    }
}
