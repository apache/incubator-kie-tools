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

package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.shared.core.types.TextUnit;
import com.ait.tooling.nativetools.client.collection.NFastDoubleArrayJSO;
import com.ait.tooling.nativetools.client.collection.NFastStringMap;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;

/**
 * Text utilities.
 */
public class TextUtils
{

    static ScratchPad                          FORBOUNDS = new ScratchPad(1, 1);

    static NFastStringMap<NFastDoubleArrayJSO> OFFSCACHE = new NFastStringMap<NFastDoubleArrayJSO>();

    static native NFastDoubleArrayJSO getTextOffsets(CanvasPixelArray data, int wide, int high, int base)
    /*-{
		var top = -1;
		var bot = -1;
		for (var y = 0; ((y < high) && (top < 0)); y++) {
			for (var x = 0; ((x < wide) && (top < 0)); x++) {
				if (data[(y * wide + x) * 4] != 0) {
					top = y;
				}
			}
		}
		if (top < 0) {
			top = 0;
		}
		for (var y = high - 1; ((y > top) && (bot < 0)); y--) {
			for (var x = 0; ((x < wide) && (bot < 0)); x++) {
				if (data[(y * wide + x) * 4] != 0) {
					bot = y;
				}
			}
		}
		if ((top < 0) || (bot < 0)) {
			return null;
		}
		return [ top - base, bot - base ];
    }-*/;

    public static final NFastDoubleArrayJSO getTextOffsets(final String font, final TextBaseLine baseline)
    {
        FORBOUNDS.getContext().setTextFont(font);

        FORBOUNDS.getContext().setTextAlign(TextAlign.LEFT);

        FORBOUNDS.getContext().setTextBaseline(TextBaseLine.ALPHABETIC);

        final int m = (int) FORBOUNDS.getContext().measureText("M").getWidth();

        final int w = (int) FORBOUNDS.getContext().measureText("Mg").getWidth();

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

        return getTextOffsets(ctxt.getImageData(0, 0, w, h).getData(), w, h, m * 2);
    }

    public static BoundingBox getBoundingBox(final String text, final double size, final String style, final String family, final TextUnit unit, final TextBaseLine baseline, final TextAlign align)
    {
        if ((null == text) || (text.isEmpty()) || (false == (size > 0)))
        {
            return new BoundingBox(0, 0, 0, 0);
        }
        final String font = getFontString(size, unit, style, family);

        final String base = font + " " + baseline.getValue();

        NFastDoubleArrayJSO offs = OFFSCACHE.get(base);

        if (null == offs)
        {
            OFFSCACHE.put(base, offs = getTextOffsets(font, baseline));
        }
        if (null == offs)
        {
            return new BoundingBox(0, 0, 0, 0);
        }
        FORBOUNDS.getContext().setTextFont(font);

        FORBOUNDS.getContext().setTextAlign(TextAlign.LEFT);

        FORBOUNDS.getContext().setTextBaseline(TextBaseLine.ALPHABETIC);

        final double wide = FORBOUNDS.getContext().measureText(text).getWidth();

        final BoundingBox bbox = new BoundingBox().addY(offs.get(0)).addY(offs.get(1));

        switch (align)
        {
            case LEFT:
            case START:
                bbox.addX(0).addX(wide);
                break;
            case END:
            case RIGHT:
                bbox.addX(0).addX(0 - wide);
                break;
            case CENTER:
                bbox.addX(wide / 2).addX(0 - (wide / 2));
                break;
        }
        return bbox;
    }

    public static String getFontString(final double size, final TextUnit unit, final String style, final String family)
    {
        return style + " " + size + unit.toString() + " " + family;
    }

    public static String padString(String string, int targetSize, char padChar, TextAlign where) {
        if (string.length() >= targetSize) {
            return string;
        }

        int toPad = targetSize - string.length();
        StringBuilder buffer = new StringBuilder(targetSize);
        switch (where) {
            case END:
            case RIGHT:
                for (int i = 0; i < toPad; i++) {
                    buffer.append(padChar);
                }
                buffer.append(string);
                return buffer.toString();

            case START:
            case LEFT:
                buffer.append(string);
                for (int i = 0; i < toPad; i++) {
                    buffer.append(padChar);
                }
                return buffer.toString();

            case CENTER:
                int leftPad = toPad / 2;
                int rightPad = toPad - leftPad;
                for (int i = 0; i < leftPad; i++) {
                    buffer.append(padChar);
                }
                buffer.append(string);
                for (int i = 0; i < rightPad; i++) {
                    buffer.append(padChar);
                }
                return buffer.toString();

            default:
                return string;
        }
    }


}
