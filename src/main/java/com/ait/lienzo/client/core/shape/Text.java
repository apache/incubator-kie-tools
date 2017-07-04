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

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.FillGradient;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.ait.lienzo.client.core.types.PatternGradient;
import com.ait.lienzo.client.core.types.RadialGradient;
import com.ait.lienzo.client.core.types.TextMetrics;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.shared.core.types.TextUnit;
import com.ait.tooling.nativetools.client.collection.NFastDoubleArrayJSO;
import com.ait.tooling.nativetools.client.collection.NFastStringMap;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.json.client.JSONObject;

/**
 * Text implementation for Canvas.
 */
public class Text extends Shape<Text>
{
    private static final boolean                             GRADFILLS = LienzoCore.get().isSafariBroken();

    private static final ScratchPad                          FORBOUNDS = new ScratchPad(1, 1);

    private static final NFastStringMap<NFastDoubleArrayJSO> OFFSCACHE = new NFastStringMap<NFastDoubleArrayJSO>();

    private final DrawString STROKE = new DrawString() {
        @Override
        public void draw(Context2D context, String s,
                         double xOffset, double lineNum) {
            context.beginPath();

            context.strokeText(s, xOffset, getLineHeight(context) * lineNum);

            context.closePath();

        }
    };

    private final DrawString FILL = new DrawString() {
        @Override
        public void draw(Context2D context,
                         String s,
                         double xOffset, double lineNum) {
            context.fillText(s, xOffset, getLineHeight(context) * lineNum);
        }
    };

    private BoundingBox wrapBoundaries;

    /**
     * Constructor. Creates an instance of text.
     * 
     * @param text
     */
    public Text(String text)
    {
        super(ShapeType.TEXT);
        wrapBoundaries = null;

        final LienzoCore globals = LienzoCore.get();

        if (null == text)
        {
            text = "";
        }
        setText(text).setFontFamily(globals.getDefaultFontFamily()).setFontStyle(globals.getDefaultFontStyle()).setFontSize(globals.getDefaultFontSize());
    }

    /**
     * Constructor. Creates an instance of text.
     * 
     * @param text 
     * @param family font family
     * @param points font size
     */
    public Text(String text, String family, double size)
    {
        super(ShapeType.TEXT);
        wrapBoundaries = null;

        final LienzoCore globals = LienzoCore.get();

        if (null == text)
        {
            text = "";
        }
        if ((null == family) || ((family = family.trim()).isEmpty()))
        {
            family = globals.getDefaultFontFamily();
        }
        if (size <= 0)
        {
            size = globals.getDefaultFontSize();
        }
        setText(text).setFontFamily(family).setFontStyle(globals.getDefaultFontStyle()).setFontSize(size);
    }

    /**
     * Constructor. Creates an instance of text.
     * 
     * @param text
     * @param family font family
     * @param style font style (bold, italic, etc)
     * @param points font size
     */
    public Text(String text, String family, String style, double size)
    {
        super(ShapeType.TEXT);
        wrapBoundaries = null;

        final LienzoCore globals = LienzoCore.get();

        if (null == text)
        {
            text = "";
        }
        if ((null == family) || ((family = family.trim()).isEmpty()))
        {
            family = globals.getDefaultFontFamily();
        }
        if ((null == style) || ((style = style.trim()).isEmpty()))
        {
            style = globals.getDefaultFontStyle();
        }
        if (size <= 0)
        {
            size = globals.getDefaultFontSize();
        }
        setText(text).setFontFamily(family).setFontStyle(style).setFontSize(size);
    }

    protected Text(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.TEXT, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        if (null == wrapBoundaries) {
            return getBoundingBoxForString(getText());
        }
        double width = wrapBoundaries.getWidth();
        int numOfLines = 1;
        String[] words = getText().split(" ");
        StringBuilder nextLine = new StringBuilder(words[0]);
        for (int i = 1; i < words.length; i++) {
            if (getBoundingBoxForString(nextLine + " " + words[i]).getWidth() <= wrapBoundaries.getWidth())
            {
                nextLine.append(" ").append(words[i]);
            }
            else {
                nextLine.setLength(words[i].length());
                nextLine.replace(0,words[i].length(),words[i]);
                numOfLines++;
            }
        }
        double height = getBoundingBoxForString(getText()).getHeight();
        height = height * numOfLines;
        return new BoundingBox().addX(0).addX(width).addY(0).addY(height);
    }

    private BoundingBox getBoundingBoxForString(String string) {
        return getBoundingBox(string, getFontSize(), getFontStyle(), getFontFamily(), getTextUnit(), getTextBaseLine(), getTextAlign());
    }

    private static final native NFastDoubleArrayJSO getTextOffsets(CanvasPixelArray data, int wide, int high, int base)
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

    private static final NFastDoubleArrayJSO getTextOffsets(final String font, final TextBaseLine baseline)
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

        ctxt.fillText("Mg", 0, m * 2);

        return getTextOffsets(ctxt.getImageData(0, 0, w, h).getData(), w, h, m * 2);
    }

    private final static BoundingBox getBoundingBox(final String text, final double size, final String style, final String family, final TextUnit unit, final TextBaseLine baseline, final TextAlign align)
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

    public static final String getFontString(final double size, final TextUnit unit, final String style, final String family)
    {
        return style + " " + size + unit.toString() + " " + family;
    }

    @Override
    protected void drawWithoutTransforms(final Context2D context, double alpha, BoundingBox bounds)
    {
        final Attributes attr = getAttributes();

        alpha = alpha * attr.getAlpha();

        if (alpha <= 0)
        {
            return;
        }
        final String text = attr.getText();

        final double size = attr.getFontSize();

        if ((null == text) || (text.isEmpty()) || (false == (size > 0)))
        {
            return;
        }
        if (context.isSelection())
        {
            if (dofillBoundsForSelection(context, attr, alpha))
            {
                return;
            }
        }
        else
        {
            setAppliedShadow(false);
        }
        if (attr.isDefined(Attribute.TEXT_BASELINE))
        {
            context.setTextBaseline(attr.getTextBaseLine());
        }
        if (attr.isDefined(Attribute.TEXT_ALIGN))
        {
            context.setTextAlign(attr.getTextAlign());
        }
        context.setTextFont(getFontString(size, attr.getTextUnit(), attr.getFontStyle(), attr.getFontFamily()));

        final boolean fill = fill(context, attr, alpha);

        stroke(context, attr, alpha, fill);
    }

    /**
     * Draws this text
     * 
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        return false;
    }

    @Override
    protected boolean fill(final Context2D context, final Attributes attr, double alpha)
    {
        final boolean filled = attr.hasFill();

        if ((filled) || (attr.isFillShapeForSelection()))
        {
            alpha = alpha * attr.getFillAlpha();

            if (alpha <= 0)
            {
                return false;
            }
            if (context.isSelection())
            {
                final String color = getColorKey();

                if (null == color)
                {
                    return false;
                }
                context.save();

                if (GRADFILLS)
                {
                    final TextMetrics size = measureWithIdentityTransform(context);

                    if (null != size)
                    {
                        final double wide = size.getWidth();

                        final double high = size.getHeight();

                        drawString(context,
                                   attr,
                                   new DrawString() {
                                       @Override
                                       public void draw(Context2D context,
                                                        String s,
                                                        double xOffset, double lineNum) {
                                           context.fillTextWithGradient(s, xOffset, getLineHeight(context) * lineNum, 0, 0, wide + (wide / 6), high + (high / 6), color);
                                       }
                                   });

                    }
                    else
                    {
                        final Layer layer = getLayer();

                        drawString(context,
                                   attr,
                                   new DrawString() {
                                       @Override
                                       public void draw(Context2D context,
                                                        String s,
                                                        double xOffset, double lineNum) {
                                           context.fillTextWithGradient(s, xOffset, getLineHeight(context) * lineNum, 0,0, layer.getWidth(), layer.getHeight(), color);
                                       }
                                   });
                    }
                }
                else
                {
                    context.setFillColor(color);

                    drawString(context,attr,FILL);
                }
                context.restore();

                return true;
            }
            if (false == filled)
            {
                return false;
            }
            context.save();

            if (attr.hasShadow())
            {
                doApplyShadow(context, attr);
            }
            context.setGlobalAlpha(alpha);

            final String fill = attr.getFillColor();

            if (null != fill)
            {
                context.setFillColor(fill);

                drawString(context,attr,FILL);
                
                context.restore();

                return true;
            }
            else
            {
                final FillGradient grad = attr.getFillGradient();

                if (null != grad)
                {
                    final String type = grad.getType();

                    if (LinearGradient.TYPE.equals(type))
                    {
                        context.setFillGradient(grad.asLinearGradient());

                        drawString(context,attr,FILL);

                        context.restore();

                        return true;
                    }
                    else if (RadialGradient.TYPE.equals(type))
                    {
                        context.setFillGradient(grad.asRadialGradient());

                        drawString(context,attr,FILL);

                        context.restore();

                        return true;
                    }
                    else if (PatternGradient.TYPE.equals(type))
                    {
                        context.setFillGradient(grad.asPatternGradient());

                        drawString(context,attr,FILL);

                        context.restore();

                        return true;
                    }
                }
            }
            context.restore();
        }
        return false;
    }

    @Override
    protected void stroke(final Context2D context, final Attributes attr, final double alpha, final boolean filled)
    {
        if (setStrokeParams(context, attr, alpha, filled))
        {
            if ((attr.hasShadow()) && (false == context.isSelection()))
            {
                doApplyShadow(context, attr);
            }

            drawString(context, attr, STROKE);
            context.restore();
        }
    }

    private static final native void log(String msg)/*-{
        console.log(msg);
    }-*/;

    private void drawString(final Context2D context, final Attributes attr, DrawString drawCommand)
    {
        if (null != wrapBoundaries) {
            String[] words = attr.getText().split(" ");
            StringBuilder nextLine = new StringBuilder(words[0]);
            ArrayList<String> lines = new ArrayList<>();
            for (int i = 1; i < words.length; i++) {
                if (getBoundingBoxForString(nextLine + " " + words[i]).getWidth() <= wrapBoundaries.getWidth())
                {
                    nextLine.append(" ").append(words[i]);
                }
                else {
                    lines.add(nextLine.toString());
                    nextLine.setLength(words[i].length());
                    nextLine.replace(0,words[i].length(),words[i]);
                }
            }
            lines.add(nextLine.toString());

            double xOffset = 0;

            switch (getTextAlign()) {
                case START:
                case LEFT:
                    xOffset = 0;
                    break;

                case CENTER:
                    xOffset = wrapBoundaries.getWidth()/2;
                    break;

                case END:
                case RIGHT:
                    xOffset = wrapBoundaries.getWidth();
                    break;
            }

            for (int i = 0; i < lines.size(); i++){
                String line = lines.get(i);
                int toPad = (int)Math.round((wrapBoundaries.getWidth() - getBoundingBoxForString(line).getWidth())/getBoundingBoxForString(" ").getWidth());
                line = padString(line, line.length() + toPad, ' ', getTextAlign());
                drawCommand.draw(context,line,xOffset,i + 0.8);
            }
        }
        else {
            drawCommand.draw(context,attr.getText(),0,0);
        }
    }

    private String padString(String string, int targetSize, char padChar, TextAlign where) {
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

    /**
     * Returns TextMetrics, which includes an approximate value for
     * height. As close as we can estimate it at this time.
     * 
     * @param context
     * @return TextMetric or null if the text is empty or null
     */
    public TextMetrics measure(final Context2D context)
    {
        final String text = getText();

        final double size = getFontSize();

        if ((null == text) || (text.isEmpty()) || (false == (size > 0)))
        {
            return TextMetrics.make(0, 0);
        }
        context.save();

        context.setTextAlign(TextAlign.LEFT);

        context.setTextBaseline(TextBaseLine.ALPHABETIC);

        context.setTextFont(getFontString(size, getTextUnit(), getFontStyle(), getFontFamily()));

        double width = getStrokeWidth();

        if (width == 0)
        {
            width = 1;
        }
        context.setStrokeWidth(width);

        context.transform(getAbsoluteTransform());

        TextMetrics meas = context.measureText(text);

        double height = context.measureText("M").getWidth();

        meas.setHeight(height - height / 6);

        context.restore();

        return meas;
    }


    /**
     * Returns TextMetrics, which includes an approximate value for
     * height. As close as we can estimate it at this time.
     *
     * @param context
     * @return TextMetric or null if the text is empty or null
     */
    public double getLineHeight(final Context2D context)
    {
        return getBoundingBoxForString("Mg").getHeight();
    }
    /**
     * Returns TextMetrics, which includes an approximate value for
     * height. As close as we can estimate it at this time.
     * 
     * @param context
     * @return TextMetric or null if the text is empty or null
     */
    public TextMetrics measureWithIdentityTransform(Context2D context)
    {
        context.save();

        context.setToIdentityTransform();

        TextMetrics size = measure(context);

        context.restore();

        return size;
    }

    /**
     * Returns the {@link Text} String
     * 
     * @return String
     */
    public String getText()
    {
        return getAttributes().getText();
    }

    /**
     * Sets the {@link Text} String
     * 
     * @return this Text
     */
    public Text setText(String text)
    {
        getAttributes().setText(text);

        return this;
    }

    /**
     * Returns the Font Family.
     * 
     * @return String
     */
    public String getFontFamily()
    {
        return getAttributes().getFontFamily();
    }

    /**
     * Sets the {@link Text} Font Family
     * 
     * @return this Text
     */
    public Text setFontFamily(String family)
    {
        getAttributes().setFontFamily(family);

        return this;
    }

    /**
     * Returns the Font Style.
     * 
     * @return String
     */
    public String getFontStyle()
    {
        return getAttributes().getFontStyle();
    }

    /**
     * Sets the Font Style.
     * 
     * @param style
     * @return this Text
     */
    public Text setFontStyle(String style)
    {
        getAttributes().setFontStyle(style);

        return this;
    }

    /**
     * Returns the Font Size.
     * 
     * @return double
     */
    public double getFontSize()
    {
        return getAttributes().getFontSize();
    }

    /**
     * Sets the Font Size.
     * 
     * @param size
     * @return this Text
     */
    public Text setFontSize(double size)
    {
        getAttributes().setFontSize(size);

        return this;
    }

    public Text setTextUnit(TextUnit unit)
    {
        getAttributes().setTextUnit(unit);

        return this;
    }

    public TextUnit getTextUnit()
    {
        return getAttributes().getTextUnit();
    }

    /**
     * Returns the {@link TextAlign}
     * 
     * @return {@link TextAlign}
     */
    public TextAlign getTextAlign()
    {
        return getAttributes().getTextAlign();
    }

    /**
     * Sets the {@link TextAlign}
     * 
     * @param align
     * @return this Text
     */
    public Text setTextAlign(TextAlign align)
    {
        getAttributes().setTextAlign(align);

        return this;
    }

    /**
     * Returns the {@link TextBaseLine}
     * 
     * @return {@link TextBaseLine}
     */
    public TextBaseLine getTextBaseLine()
    {
        return getAttributes().getTextBaseLine();
    }

    /**
     * Sets the {@link TextBaseLine}
     * 
     * @param baseline
     * @return this Text
     */
    public Text setTextBaseLine(TextBaseLine baseline)
    {
        getAttributes().setTextBaseLine(baseline);

        return this;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes()
    {
        return asAttributes(Attribute.TEXT, Attribute.FONT_SIZE, Attribute.FONT_STYLE, Attribute.FONT_FAMILY, Attribute.TEXT_UNIT, Attribute.TEXT_ALIGN, Attribute.TEXT_BASELINE);
    }

    public BoundingBox getWrapBoundaries() {
        return wrapBoundaries;
    }

    public Text setWrapBoundaries(BoundingBox boundaries) {
        wrapBoundaries = boundaries;
        return this;
    }

    public static class TextFactory extends ShapeFactory<Text>
    {
        public TextFactory()
        {
            super(ShapeType.TEXT);

            addAttribute(Attribute.TEXT, true);

            addAttribute(Attribute.FONT_SIZE);

            addAttribute(Attribute.FONT_STYLE);

            addAttribute(Attribute.FONT_FAMILY);

            addAttribute(Attribute.TEXT_UNIT);

            addAttribute(Attribute.TEXT_ALIGN);

            addAttribute(Attribute.TEXT_BASELINE);
        }

        @Override
        public Text create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new Text(node, ctx);
        }
    }

    private interface DrawString {
        void draw(Context2D c, String s, double xOffset, double lineNum);
    }

    private enum LinePlacement
    {
        TOP,
        CENTER,
        BOTTOM
    }
}
