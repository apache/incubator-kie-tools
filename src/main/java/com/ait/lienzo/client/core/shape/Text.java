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
import com.ait.lienzo.client.core.types.PatternGradient;
import com.ait.lienzo.client.core.types.RadialGradient;
import com.ait.lienzo.client.core.types.TextMetrics;
import com.ait.lienzo.client.core.util.ScratchCanvas;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.google.gwt.json.client.JSONObject;

/**
 * Text implementation for Canvas.
 */
public class Text extends Shape<Text>
{
    private static final boolean       IS_SAFARI = LienzoCore.get().isSafari();

    private static final ScratchCanvas FORBOUNDS = new ScratchCanvas(1, 1);

    /**
     * Constructor. Creates an instance of text.
     * 
     * @param text
     */
    public Text(String text)
    {
        super(ShapeType.TEXT);

        LienzoCore globals = LienzoCore.get();

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
    public Text(String text, String family, double points)
    {
        super(ShapeType.TEXT);

        LienzoCore globals = LienzoCore.get();

        if (null == text)
        {
            text = "";
        }
        if ((null == family) || ((family = family.trim()).isEmpty()))
        {
            family = globals.getDefaultFontFamily();
        }
        if (points <= 0)
        {
            points = globals.getDefaultFontSize();
        }
        setText(text).setFontFamily(family).setFontStyle(globals.getDefaultFontStyle()).setFontSize(points);
    }

    /**
     * Constructor. Creates an instance of text.
     * 
     * @param text
     * @param family font family
     * @param style font style (bold, italic, etc)
     * @param points font size
     */
    public Text(String text, String family, String style, double points)
    {
        super(ShapeType.TEXT);

        LienzoCore globals = LienzoCore.get();

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
        if (points <= 0)
        {
            points = globals.getDefaultFontSize();
        }
        setText(text).setFontFamily(family).setFontStyle(style).setFontSize(points);
    }

    protected Text(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.TEXT, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        return getBoundingBox(getText(), getFontSize(), getFontStyle(), getFontFamily());
    }

    public final static BoundingBox getBoundingBox(String text, double size, String style, String family)
    {
        FORBOUNDS.getContext().setTextFont(style + " " + size + "pt " + family);

        TextMetrics metrics = FORBOUNDS.getContext().measureText(text);

        return new BoundingBox(0, 0, metrics.getWidth(), metrics.getHeight());
    }

    /**
     * Draws this text
     * 
     * @param context
     */
    @Override
    public boolean prepare(Context2D context, Attributes attr, double alpha)
    {
        String text = getText();

        double size = getFontSize();

        if ((null == text) || (text.isEmpty()) || (false == (size > 0)))
        {
            return false;
        }
        if (attr.isDefined(Attribute.TEXT_BASELINE))
        {
            context.setTextBaseline(getTextBaseLine());
        }
        if (attr.isDefined(Attribute.TEXT_ALIGN))
        {
            context.setTextAlign(getTextAlign());
        }
        context.setTextFont(getFontStyle() + " " + size + "pt " + getFontFamily());

        return true;
    }

    protected void fill(Context2D context, Attributes attr, double alpha)
    {
        boolean filled = attr.isDefined(Attribute.FILL);

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

                        context.getJSO().fillTextWithGradient(getText(), 0, 0, 0, 0, wide + (wide / 6), high + (high / 6), getColorKey());
                    }
                    else
                    {
                        Layer layer = getLayer();

                        context.getJSO().fillTextWithGradient(getText(), 0, 0, 0, 0, layer.getWidth(), layer.getHeight(), getColorKey());
                    }
                }
                else
                {
                    context.setFillColor(getColorKey());

                    context.fillText(getText(), 0, 0);
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

            context.setGlobalAlpha(alpha * getFillAlpha());

            String fill = attr.getFillColor();

            if (null != fill)
            {
                context.setFillColor(fill);

                context.fillText(getText(), 0, 0);

                setWasFilledFlag(true);
            }
            else
            {
                FillGradient grad = attr.getFillGradient();

                if (null != grad)
                {
                    if (LinearGradient.TYPE.equals(grad.getType()))
                    {
                        context.setFillGradient(grad.asLinearGradient());

                        context.fillText(getText(), 0, 0);

                        setWasFilledFlag(true);
                    }
                    else if (RadialGradient.TYPE.equals(grad.getType()))
                    {
                        context.setFillGradient(grad.asRadialGradient());

                        context.fillText(getText(), 0, 0);

                        setWasFilledFlag(true);
                    }
                    else if (PatternGradient.TYPE.equals(grad.getType()))
                    {
                        context.setFillGradient(grad.asPatternGradient());

                        context.fillText(getText(), 0, 0);

                        setWasFilledFlag(true);
                    }
                }
            }
            context.restore();
        }
    }

    @Override
    protected void stroke(Context2D context, Attributes attr, double alpha)
    {
        context.save();

        if (setStrokeParams(context, attr, alpha))
        {
            if (context.isSelection())
            {
                context.beginPath();

                context.strokeText(getText(), 0, 0);

                context.closePath();
            }
            else
            {
                doApplyShadow(context, attr);

                context.beginPath();

                context.strokeText(getText(), 0, 0);

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
    public TextMetrics measure(Context2D context)
    {
        TextMetrics size = null;

        String text = getText();

        if ((null == text) || (text.isEmpty()))
        {
            return size;
        }
        context.save();

        context.setTextAlign(TextAlign.LEFT);

        context.setTextBaseline(TextBaseLine.ALPHABETIC);

        context.setTextFont(getFontStyle() + " " + getFontSize() + "pt " + getFontFamily());

        double width = getStrokeWidth();

        if (width == 0)
        {
            width = 1;
        }
        context.setStrokeWidth(width);

        context.transform(getAbsoluteTransform());

        size = context.measureText(text);

        double height = context.measureText("M").getWidth();

        size.setHeight(height - height / 6);

        context.restore();

        return size;
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
