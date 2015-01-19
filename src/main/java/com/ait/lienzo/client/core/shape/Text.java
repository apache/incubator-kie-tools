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

package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.FillGradient;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.ait.lienzo.client.core.types.NFastDoubleArrayJSO;
import com.ait.lienzo.client.core.types.NFastStringMap;
import com.ait.lienzo.client.core.types.PatternGradient;
import com.ait.lienzo.client.core.types.RadialGradient;
import com.ait.lienzo.client.core.types.TextMetrics;
import com.ait.lienzo.client.core.util.ScratchCanvas;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.shared.core.types.TextUnit;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.json.client.JSONObject;

/**
 * Text implementation for Canvas.
 */
public class Text extends Shape<Text>
{
    private static final boolean                             IS_SAFARI = LienzoCore.get().isSafari();

    private static final ScratchCanvas                       FORBOUNDS = new ScratchCanvas(1, 1);

    private static final NFastStringMap<NFastDoubleArrayJSO> OFFSCACHE = new NFastStringMap<NFastDoubleArrayJSO>();

    /**
     * Constructor. Creates an instance of text.
     * 
     * @param text
     */
    public Text(String text)
    {
        super(ShapeType.TEXT);

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
        return getBoundingBox(getText(), getFontSize(), getFontStyle(), getFontFamily(), getTextUnit(), getTextBaseLine(), getTextAlign());
    }

    private static final native NFastDoubleArrayJSO getTextOffsets(CanvasPixelArray data, int wide, int high, int base)
    /*-{
        var top = -1;
        var bot = -1;
        for(var y = 0; ((y < high) && (top < 0)); y++) {
            for(var x = 0; ((x < wide) && (top < 0)); x++) {
                if (data[(y * wide + x) * 4] != 0) {
                    top = y;
                }
            }
        }
        if (top < 0) {
            top = 0;
        }
        for(var y = high - 1; ((y > top) && (bot < 0)); y--) {
            for(var x = 0; ((x < wide) && (bot < 0)); x++) {
                if (data[(y * wide + x) * 4] != 0) {
                    bot = y;
                }
            }
        }
        if ((top < 0) || (bot < 0)) {
            return null;
        }
        return [top-base, bot-base];
    }-*/;

    private static final NFastDoubleArrayJSO getTextOffsets(final String font, final TextBaseLine baseline)
    {
        FORBOUNDS.getContext().setTextFont(font);

        FORBOUNDS.getContext().setTextAlign(TextAlign.LEFT);

        FORBOUNDS.getContext().setTextBaseline(TextBaseLine.ALPHABETIC);

        final int m = (int) FORBOUNDS.getContext().measureText("M").getWidth();

        final int w = (int) FORBOUNDS.getContext().measureText("Mg").getWidth();

        final int h = (m * 4);

        final ScratchCanvas temp = new ScratchCanvas(w, h);

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
        final String font = getFontString(size, style, family, unit.getValue());

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

    private final static String getFontString(double size, String style, String family, String unit)
    {
        return style + " " + size + unit + " " + family;
    }

    /**
     * Draws this text
     * 
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        final String text = attr.getText();

        final double size = attr.getFontSize();

        if ((null == text) || (text.isEmpty()) || (false == (size > 0)))
        {
            return false;
        }
        if (attr.isDefined(Attribute.TEXT_BASELINE))
        {
            context.setTextBaseline(attr.getTextBaseLine());
        }
        if (attr.isDefined(Attribute.TEXT_ALIGN))
        {
            context.setTextAlign(attr.getTextAlign());
        }
        context.setTextFont(getFontString(size, attr.getFontStyle(), attr.getFontFamily(), attr.getTextUnit().getValue()));

        return true;
    }

    @Override
    protected void fill(final Context2D context, final Attributes attr, double alpha)
    {
        alpha = alpha * attr.getFillAlpha();

        if (alpha <= 0)
        {
            return;
        }
        final boolean filled = attr.isDefined(Attribute.FILL);

        if ((filled) || (attr.isFillShapeForSelection()))
        {
            if (context.isSelection())
            {
                context.save();

                context.setGlobalAlpha(1);

                if (IS_SAFARI)
                {
                    TextMetrics size = measureWithIdentityTransform(context);

                    if (null != size)
                    {
                        double wide = size.getWidth();

                        double high = size.getHeight();

                        context.getJSO().fillTextWithGradient(attr.getText(), 0, 0, 0, 0, wide + (wide / 6), high + (high / 6), getColorKey());
                    }
                    else
                    {
                        Layer layer = getLayer();

                        context.getJSO().fillTextWithGradient(attr.getText(), 0, 0, 0, 0, layer.getWidth(), layer.getHeight(), getColorKey());
                    }
                }
                else
                {
                    context.setFillColor(getColorKey());

                    context.fillText(attr.getText(), 0, 0);
                }
                context.restore();

                setWasFilledFlag(true);

                return;
            }
            if (false == filled)
            {
                return;
            }
            context.save();

            doApplyShadow(context, attr);

            context.setGlobalAlpha(alpha);

            final String fill = attr.getFillColor();

            if (null != fill)
            {
                context.setFillColor(fill);

                context.fillText(attr.getText(), 0, 0);

                setWasFilledFlag(true);
            }
            else
            {
                final FillGradient grad = attr.getFillGradient();

                if (null != grad)
                {
                    if (LinearGradient.TYPE.equals(grad.getType()))
                    {
                        context.setFillGradient(grad.asLinearGradient());

                        context.fillText(attr.getText(), 0, 0);

                        setWasFilledFlag(true);
                    }
                    else if (RadialGradient.TYPE.equals(grad.getType()))
                    {
                        context.setFillGradient(grad.asRadialGradient());

                        context.fillText(attr.getText(), 0, 0);

                        setWasFilledFlag(true);
                    }
                    else if (PatternGradient.TYPE.equals(grad.getType()))
                    {
                        context.setFillGradient(grad.asPatternGradient());

                        context.fillText(attr.getText(), 0, 0);

                        setWasFilledFlag(true);
                    }
                }
            }
            context.restore();
        }
    }

    @Override
    protected void stroke(final Context2D context, final Attributes attr, final double alpha)
    {
        context.save();

        if (setStrokeParams(context, attr, alpha))
        {
            if (context.isSelection())
            {
                context.beginPath();

                context.strokeText(attr.getText(), 0, 0);

                context.closePath();
            }
            else
            {
                doApplyShadow(context, attr);

                context.beginPath();

                context.strokeText(attr.getText(), 0, 0);

                context.closePath();
            }
        }
        context.restore();
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

        context.setTextFont(getFontString(size, getFontStyle(), getFontFamily(), getTextUnit().getValue()));

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
    public IFactory<Text> getFactory()
    {
        return new TextFactory();
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
}
