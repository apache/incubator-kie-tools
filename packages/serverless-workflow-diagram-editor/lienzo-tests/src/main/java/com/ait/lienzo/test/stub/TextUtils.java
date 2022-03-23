package com.ait.lienzo.test.stub;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.shared.core.types.TextUnit;
import com.ait.lienzo.test.annotation.StubClass;
import com.ait.lienzo.tools.client.collection.NFastDoubleArray;
import com.ait.lienzo.tools.client.collection.NFastStringMap;
import elemental2.core.Uint8ClampedArray;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/5/19
 */
@StubClass("com.ait.lienzo.client.core.shape.TextUtils")
public class TextUtils {

    private ScratchPad FORBOUNDS = new ScratchPad(1, 1);

    private NFastStringMap<NFastDoubleArray> OFFSCACHE = new NFastStringMap<>();

    private NFastDoubleArray getTextOffsets(Uint8ClampedArray data, int wide, int high, int base) {

        int top = -1;
        int bot = -1;
        for (int y = 0; ((y < high) && (top < 0)); y++) {
            for (int x = 0; ((x < wide) && (top < 0)); x++) {
                Double value = data.getAt((y * wide + x) * 4); //FIX IT
                if (value != 0) {
                    top = y;
                }
            }
        }

        if (top < 0) {
            top = 0;
        }

        for (int y = high - 1; ((y > top) && (bot < 0)); y--) {
            for (int x = 0; ((x < wide) && (bot < 0)); x++) {
                Double value = data.getAt((y * wide + x) * 4);
                if (value != 0) {
                    bot = y;
                }
            }
        }

        if ((top < 0) || (bot < 0)) {
            return null;
        }
        return NFastDoubleArray.make2P(top - base, bot - base);
    }

    private final NFastDoubleArray getTextOffsets(final String font, final TextBaseLine baseline) {
        if (FORBOUNDS.getContext() == null) {
            throw new Error();
        }

        FORBOUNDS.getContext().setTextFont(font);

        FORBOUNDS.getContext().setTextAlign(TextAlign.LEFT);

        FORBOUNDS.getContext().setTextBaseline(TextBaseLine.ALPHABETIC);

        final int m = (int) FORBOUNDS.getContext().measureText("M").width;

        final int w = (int) FORBOUNDS.getContext().measureText("Mg").width;

        final int h = (m * 4);

        final ScratchPad temp = new ScratchPad(w, h);

        final Context2D ctxt = temp.getContext();

        ctxt.setFillColor(ColorName.BLACK);

        ctxt.fillRect(0, 0, w, h);

        ctxt.setTextFont(font);

        ctxt.setTextAlign(TextAlign.LEFT);

        ctxt.setTextBaseline(baseline);

        ctxt.setFillColor(ColorName.WHITE);

        ctxt.fillText("Mg", 0, m * 2.0);

        return getTextOffsets(ctxt.getImageData(0, 0, w, h).data, w, h, m * 2);
    }

    public BoundingBox getBoundingBox(final String text, final double size, final String style, final String family, final TextUnit unit, final TextBaseLine baseline, final TextAlign align) {

        //For WiresConnectorLabelFactoryTest.testSegmentLabelExecutor
        if (text.contains("Doing some")) {
            return BoundingBox.fromDoubles(0, 0, 0, 0);
        }

        if ((null == text) || (text.isEmpty()) || (false == (size > 0))) {
            return BoundingBox.fromDoubles(0, 0, 0, 0);
        }
        final String font = getFontString(size, unit, style, family);

        final String base = font + " " + baseline.getValue();
        NFastDoubleArray offs = OFFSCACHE.get(base);

        if (null == offs) {
            offs = getTextOffsets(font, baseline);
            OFFSCACHE.put(base, offs);
        }

        if (null == offs) {
            return BoundingBox.fromDoubles(0, 0, 0, 0);
        }

        FORBOUNDS.getContext().setTextFont(font);

        FORBOUNDS.getContext().setTextAlign(TextAlign.LEFT);

        FORBOUNDS.getContext().setTextBaseline(TextBaseLine.ALPHABETIC);

        final double wide = FORBOUNDS.getContext().measureText(text).width;

        final BoundingBox bbox = new BoundingBox().addY(0).addY(1);
        bbox.addX(0).addX(wide);
        return bbox;
    }

    public String getFontString(final double size, final TextUnit unit, final String style, final String family) {
        return style + " " + size + unit.toString() + " " + family;
    }

    public String padString(String string, int targetSize, char padChar, TextAlign where) {
        if (string.length() >= targetSize) {
            return string;
        }

        int toPad = targetSize - string.length();
        StringBuilder buffer = new StringBuilder(targetSize);
        buffer.append(string);
        for (int i = 0; i < toPad; i++) {
            buffer.append(padChar);
        }
        return buffer.toString();
    }
}
