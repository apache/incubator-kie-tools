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
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.shared.core.types.TextUnit;
import com.google.gwt.json.client.JSONObject;

/**
 * Text implementation for Canvas.
 */
public class Text extends Shape<Text>
{
    private static final boolean                             GRADFILLS = LienzoCore.get().isSafariBroken();

    private final IDrawString STROKE = new IDrawString() {
        @Override
        public void draw(Context2D context, String s,
                         double xOffset, double lineNum) {
            context.beginPath();

            context.strokeText(s, xOffset, getLineHeight(context) * lineNum);

            context.closePath();

        }
    };

    private final IDrawString FILL = new IDrawString() {
        @Override
        public void draw(Context2D context,
                         String s,
                         double xOffset, double lineNum) {
            context.fillText(s, xOffset, getLineHeight(context) * lineNum);
        }
    };

    private ITextWrapper wrapper = new TextNoWrap(this);

    /**
     * Constructor. Creates an instance of text. Default no-wrap text wrapping.
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
     * Constructor. Creates an instance of text. Default no-wrap text wrapping.
     * 
     * @param text 
     * @param family font family
     * @param size font size
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
     * Constructor. Creates an instance of text. Default no-wrap text wrapping.
     * 
     * @param text
     * @param family font family
     * @param style font style (bold, italic, etc)
     * @param size font size
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
        return wrapper.getBoundingBox();
    }

    private BoundingBox getBoundingBoxForString(String string) {
        return TextUtils.getBoundingBox(string, getFontSize(), getFontStyle(), getFontFamily(), getTextUnit(), getTextBaseLine(), getTextAlign());
    }

    @Deprecated
    public static final String getFontString(final double size, final TextUnit unit, final String style, final String family)
    {
        //public static method preserved for compatibility
        return TextUtils.getFontString(size, unit, style, family);
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
                                   new IDrawString() {
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
                                   new IDrawString() {
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

    private void drawString(final Context2D context, final Attributes attr, IDrawString drawCommand)
    {
        wrapper.drawString(context,attr,drawCommand);
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
        return asAttributes(Attribute.TEXT, Attribute.FONT_SIZE, Attribute.FONT_STYLE, Attribute.FONT_FAMILY, Attribute.TEXT_UNIT, Attribute.TEXT_ALIGN, Attribute.TEXT_BASELINE, Attribute.WIDTH);
    }

    @Override
    public List<Attribute> getTransformingAttributes()
    {
        return asAttributes(Attribute.TEXT, Attribute.FONT_SIZE, Attribute.FONT_STYLE, Attribute.FONT_FAMILY, Attribute.TEXT_UNIT, Attribute.TEXT_ALIGN, Attribute.TEXT_BASELINE, Attribute.WIDTH);
    }

    public ITextWrapper getWrapper() {
        return wrapper;
    }

    public Text setWrapper(final ITextWrapper wrapper) {
        this.wrapper = wrapper;
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

            addAttribute(Attribute.WIDTH);
        }

        @Override
        public Text create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new Text(node, ctx);
        }
    }
}
