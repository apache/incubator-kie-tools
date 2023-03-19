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
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.FillGradient;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.ait.lienzo.client.core.types.PatternGradient;
import com.ait.lienzo.client.core.types.RadialGradient;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.shared.core.types.TextUnit;
import elemental2.dom.TextMetrics;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Text implementation for Canvas.
 */
@JsType
public class Text extends Shape<Text> {

    private static final boolean GRADFILLS = LienzoCore.get().isSafariBroken();

    @JsProperty
    private String text;

    @JsProperty
    private double fontSize = LienzoCore.get().getDefaultFontSize();

    @JsProperty
    private String fontFamily = LienzoCore.get().getDefaultFontFamily();

    @JsProperty
    private String fontStyle = LienzoCore.get().getDefaultFontStyle();

    @JsProperty
    private TextBaseLine textBaseLine = TextBaseLine.ALPHABETIC;

    @JsProperty
    private TextAlign textAlign = TextAlign.START;

    @JsProperty
    private TextUnit textUnit = TextUnit.PT;

    protected TextUtils textUtils = new TextUtils();

    private final IDrawString STROKE = (context, s, xOffset, lineNum) -> {
        context.beginPath();

        context.strokeText(s, xOffset, getLineHeight(context) * lineNum);

        context.closePath();
    };

    private final IDrawString FILL = (context, s, xOffset, lineNum) -> context.fillText(s, xOffset, getLineHeight(context) * lineNum);

    private ITextWrapper wrapper = new TextNoWrap(this);

    /**
     * Constructor. Creates an instance of text. Default no-wrap text wrapping.
     *
     * @param text
     */
    @JsIgnore
    public Text(String text) {
       this(text, null, null, -1);
    }

    /**
     * Constructor. Creates an instance of text. Default no-wrap text wrapping.
     *
     * @param text
     * @param family font family
     * @param size   font size
     */
    @JsIgnore
    public Text(String text, String family, double size) {
        this(text, family, null, size);
    }

    /**
     * Constructor. Creates an instance of text. Default no-wrap text wrapping.
     *
     * @param text
     * @param family font family
     * @param style  font style (bold, italic, etc)
     * @param size   font size
     */
    public Text(String text, String family, String style, double size) {
        super(ShapeType.TEXT);

        final LienzoCore globals = LienzoCore.get();

        if (null == text) {
            text = "";
        }
        if ((null == family) || ((family = family.trim()).isEmpty())) {
            family = globals.getDefaultFontFamily();
        }
        if ((null == style) || ((style = style.trim()).isEmpty())) {
            style = globals.getDefaultFontStyle();
        }
        if (size <= 0) {
            size = globals.getDefaultFontSize();
        }
        setText(text).setFontFamily(family).setFontStyle(style).setFontSize(size);
    }

    @Override
    public BoundingBox getBoundingBox() {
        return wrapper.getBoundingBox();
    }

    public BoundingBox getBoundingBoxForString(String string) {
        return textUtils.getBoundingBox(string,
                                        getFontSize(),
                                        getFontStyle(),
                                        getFontFamily(),
                                        getTextUnit(),
                                        getTextBaseLine(),
                                        getTextAlign());
    }

    public String getFontString(final double size, final TextUnit unit, final String style, final String family) {
        return textUtils.getFontString(size, unit, style, family);
    }

    @Override
    protected void drawWithoutTransforms(final Context2D context, double alpha, BoundingBox bounds) {
        alpha = alpha * getAlpha();

        if (alpha <= 0) {
            return;
        }
        final String text = getText();

        final double size = getFontSize();

        if ((null == text) || (text.isEmpty()) || (!(size > 0))) {
            return;
        }
        if (context.isSelection()) {
            if (dofillBoundsForSelection(context, alpha)) {
                return;
            }
        } else {
            setAppliedShadow(false);
        }
        if (textBaseLine != null) {
            context.setTextBaseline(textBaseLine);
        }
        if (textAlign != null) {
            context.setTextAlign(getTextAlign());
        }
        context.setTextFont(getFontString(size, getTextUnit(), getFontStyle(), getFontFamily()));

        final boolean fill = fill(context, alpha);

        stroke(context, alpha, fill);
    }

    /**
     * Draws this text
     *
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final double alpha) {
        return false;
    }

    @Override
    protected boolean fill(final Context2D context, double alpha) {
        final boolean filled = hasFill();

        if ((filled) || (isFillShapeForSelection())) {
            alpha = alpha * getFillAlpha();

            if (alpha <= 0) {
                return false;
            }
            if (context.isSelection()) {
                final String color = getColorKey();

                if (null == color) {
                    return false;
                }
                context.save();

                if (GRADFILLS) {
                    final TextMetrics size = measureWithIdentityTransform(context);

                    if (null != size) {
                        final double wide = size.width;

                        // This is a technique to determine the height of the font, using the highest letter 'M'
                        double mWidth = context.measureText("M").width;
                        double high = mWidth - mWidth / 6;

                        drawString(context,
                                   (context12, s, xOffset, lineNum) -> context12.fillTextWithGradient(s, xOffset, getLineHeight(context12) * lineNum, 0, 0, wide + (wide / 6), high + (high / 6), color));
                    } else {
                        final Layer layer = getLayer();

                        drawString(context,
                                   (context1, s, xOffset, lineNum) -> context1.fillTextWithGradient(s, xOffset, getLineHeight(context1) * lineNum, 0, 0, layer.getWidth(), layer.getHeight(), color));
                    }
                } else {
                    context.setFillColor(color);

                    drawString(context, FILL);
                }
                context.restore();

                return true;
            }
            if (!filled) {
                return false;
            }
            context.save();

            if (getShadow() != null) {
                doApplyShadow(context);
            }
            context.setGlobalAlpha(alpha);

            final String fill = getFillColor();

            if (null != fill) {
                context.setFillColor(fill);

                drawString(context, FILL);

                context.restore();

                return true;
            } else {
                final FillGradient grad = getFillGradient();

                if (null != grad) {
                    final String type = grad.getType();

                    if (LinearGradient.TYPE.equals(type)) {
                        context.setFillGradient(grad.asLinearGradient());

                        drawString(context, FILL);

                        context.restore();

                        return true;
                    } else if (RadialGradient.TYPE.equals(type)) {
                        context.setFillGradient(grad.asRadialGradient());

                        drawString(context, FILL);

                        context.restore();

                        return true;
                    } else if (PatternGradient.TYPE.equals(type)) {
                        context.setFillGradient(grad.asPatternGradient());

                        drawString(context, FILL);

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
    protected void stroke(final Context2D context, final double alpha, final boolean filled) {
        if (setStrokeParams(context, alpha, filled)) {
            if (getShadow() != null && !context.isSelection()) {
                doApplyShadow(context);
            }

            drawString(context, STROKE);
            context.restore();
        }
    }

    private void drawString(final Context2D context, IDrawString drawCommand) {
        wrapper.drawString(context, drawCommand);
    }

    /**
     * Returns TextMetrics, which includes an approximate value for
     * height. As close as we can estimate it at this time.
     *
     * @param context
     * @return TextMetric or null if the text is empty or null
     */
    public TextMetrics measure(final Context2D context) {
        final String text = getText();

        final double size = getFontSize();

        if ((null == text) || (text.isEmpty()) || (!(size > 0))) {
            return new TextMetrics();
        }
        context.save();

        context.setTextAlign(TextAlign.LEFT);

        context.setTextBaseline(TextBaseLine.ALPHABETIC);

        context.setTextFont(getFontString(size, getTextUnit(), getFontStyle(), getFontFamily()));

        double width = getStrokeWidth();

        if (width == 0) {
            width = 1;
        }
        context.setStrokeWidth(width);

        context.transform(getAbsoluteTransform());

        TextMetrics meas = context.measureText(text);

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
    public double getLineHeight(final Context2D context) {
        return getBoundingBoxForString("Mg").getHeight();
    }

    /**
     * Returns TextMetrics, which includes an approximate value for
     * height. As close as we can estimate it at this time.
     *
     * @param context
     * @return TextMetric or null if the text is empty or null
     */
    public TextMetrics measureWithIdentityTransform(Context2D context) {
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
    public String getText() {
        return this.text;
    }

    /**
     * Sets the {@link Text} String
     *
     * @return this Text
     */
    public Text setText(String text) {
        this.text = text;

        return this;
    }

    /**
     * Returns the Font Family.
     *
     * @return String
     */
    public String getFontFamily() {
        return this.fontFamily;
    }

    /**
     * Sets the {@link Text} Font Family
     *
     * @return this Text
     */
    public Text setFontFamily(String family) {
        if ((null == family) || (family = family.trim()).isEmpty()) {
            family = LienzoCore.get().getDefaultFontFamily();
        }
        this.fontFamily = family;

        return this;
    }

    /**
     * Returns the Font Style.
     *
     * @return String
     */
    public String getFontStyle() {
        return this.fontStyle;
    }

    /**
     * Sets the Font Style.
     *
     * @param style
     * @return this Text
     */
    public Text setFontStyle(String style) {
        if ((null == style) || (style = style.trim()).isEmpty()) {
            style = LienzoCore.get().getDefaultFontStyle();
        }
        this.fontStyle = style;

        return this;
    }

    /**
     * Returns the Font Size.
     *
     * @return double
     */
    public double getFontSize() {
        return this.fontSize;
    }

    /**
     * Sets the Font Size.
     *
     * @param size
     * @return this Text
     */
    public Text setFontSize(double size) {
        if (size <= 0.0) {
            size = LienzoCore.get().getDefaultFontSize();
        }
        this.fontSize = size;

        return this;
    }

    public Text setTextUnit(TextUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("TextUnit cannot be null");
        }
        this.textUnit = unit;

        return this;
    }

    public TextUnit getTextUnit() {
        return this.textUnit;
    }

    /**
     * Returns the {@link TextAlign}
     *
     * @return {@link TextAlign}
     */
    public TextAlign getTextAlign() {
        return this.textAlign;
    }

    /**
     * Sets the {@link TextAlign}
     *
     * @param align
     * @return this Text
     */
    public Text setTextAlign(TextAlign align) {
        this.textAlign = align;

        return this;
    }

    /**
     * Returns the {@link TextBaseLine}
     *
     * @return {@link TextBaseLine}
     */
    public TextBaseLine getTextBaseLine() {
        return this.textBaseLine;
    }

    /**
     * Sets the {@link TextBaseLine}
     *
     * @param baseLine
     * @return this Text
     */
    public Text setTextBaseLine(TextBaseLine baseLine) {
        this.textBaseLine = baseLine;

        return this;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return asAttributes(Attribute.TEXT, Attribute.FONT_SIZE, Attribute.FONT_STYLE, Attribute.FONT_FAMILY, Attribute.TEXT_UNIT, Attribute.TEXT_ALIGN, Attribute.TEXT_BASELINE, Attribute.WIDTH);
    }

    @Override
    public List<Attribute> getTransformingAttributes() {
        return asAttributes(Attribute.TEXT, Attribute.FONT_SIZE, Attribute.FONT_STYLE, Attribute.FONT_FAMILY, Attribute.TEXT_UNIT, Attribute.TEXT_ALIGN, Attribute.TEXT_BASELINE, Attribute.WIDTH);
    }

    public ITextWrapper getWrapper() {
        return wrapper;
    }

    public Text setWrapper(final ITextWrapper wrapper) {
        this.wrapper = wrapper;
        return this;
    }
}
