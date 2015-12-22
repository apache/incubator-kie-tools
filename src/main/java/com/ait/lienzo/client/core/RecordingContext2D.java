/*
   Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core;

import com.ait.lienzo.client.core.types.DashArray;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.core.types.ImageDataPixelColor;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.PatternGradient;
import com.ait.lienzo.client.core.types.RadialGradient;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.client.core.types.TextMetrics;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.CompositeOperation;
import com.ait.lienzo.shared.core.types.IColor;
import com.ait.lienzo.shared.core.types.LineCap;
import com.ait.lienzo.shared.core.types.LineJoin;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.google.gwt.dom.client.Element;

/**
 * Wrapper around a JSO that serves as a proxy to access the native capabilities of Canvas 2D.
 * @see {@link NativeContext2D} 
 */
public class RecordingContext2D extends Context2D
{
    private final Context2D m_context;

    public RecordingContext2D(final Context2D context)
    {
        super(context.getNativeContext());

        m_context = context;
    }

    @Override
    protected NativeContext2D getNativeContext()
    {
        return m_context.getNativeContext();
    }

    @Override
    public void save()
    {
        m_context.save();
    }

    @Override
    public void restore()
    {
        m_context.restore();
    }

    @Override
    public void beginPath()
    {
        m_context.beginPath();
    }

    @Override
    public void closePath()
    {
        m_context.closePath();
    }

    @Override
    public void rect(final double x, final double y, final double w, final double h)
    {
        m_context.rect(x, y, w, h);
    }

    @Override
    public void fillRect(final double x, final double y, final double w, final double h)
    {
        m_context.fillRect(x, y, w, h);
    }

    @Override
    public void fill()
    {
        m_context.fill();
    }

    @Override
    public void stroke()
    {
        m_context.stroke();
    }

    @Override
    public void setFillColor(final String color)
    {
        m_context.setFillColor(color);
    }

    /**
     * Sets the fill color
     * 
     * @param color {@link ColorName} or {@link Color}
     * 
     * @return this Context2D
     */
    @Override
    public void setFillColor(final IColor color)
    {
        m_context.setFillColor((null != color) ? color.getColorString() : null);
    }

    @Override
    public void arc(final double x, final double y, final double radius, final double startAngle, final double endAngle, final boolean anticlockwise)
    {
        m_context.arc(x, y, radius, startAngle, endAngle, anticlockwise);
    }

    @Override
    public void arc(final double x, final double y, final double radius, final double startAngle, final double endAngle)
    {
        m_context.arc(x, y, radius, startAngle, endAngle, false);
    }
    
    @Override
    public void ellipse(final double x, final double y, final double radiusX, final double radiusY, final double rotation, final double startAngle, final double endAngle, final boolean anticlockwise)
    {
        m_context.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle, anticlockwise);
    }

    @Override
    public void ellipse(final double x, final double y, final double radiusX, final double radiusY, final double rotation, final double startAngle, final double endAngle)
    {
        m_context.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle);
    }

    @Override
    public void arcTo(final double x1, final double y1, final double x2, final double y2, final double radius)
    {
        m_context.arcTo(x1, y1, x2, y2, radius);
    }

    @Override
    public void setStrokeColor(final String color)
    {
        m_context.setStrokeColor(color);
    }

    /**
     * Sets the stroke color
     * 
     * @param color {@link ColorName} or {@link Color}
     * 
     * @return this Context2D
     */
    @Override
    public void setStrokeColor(final IColor color)
    {
        m_context.setStrokeColor((null != color) ? color.getColorString() : null);
    }

    @Override
    public void setStrokeWidth(final double width)
    {
        m_context.setStrokeWidth(width);
    }

    @Override
    public void setLineCap(final LineCap linecap)
    {
        m_context.setLineCap(linecap);
    }

    @Override
    public void setLineJoin(final LineJoin linejoin)
    {
        m_context.setLineJoin(linejoin);
    }

    @Override
    public void transform(final double d0, final double d1, final double d2, final double d3, final double d4, final double d5)
    {
        m_context.transform(d0, d1, d2, d3, d4, d5);
    }

    @Override
    public void setTransform(final double d0, final double d1, final double d2, final double d3, final double d4, final double d5)
    {
        m_context.setTransform(d0, d1, d2, d3, d4, d5);
    };

    @Override
    public void setToIdentityTransform()
    {
        m_context.setToIdentityTransform();
    };

    @Override
    public void moveTo(final double x, final double y)
    {
        m_context.moveTo(x, y);
    }

    @Override
    public void bezierCurveTo(final double cp1x, final double cp1y, final double cp2x, final double cp2y, final double x, final double y)
    {
        m_context.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
    }

    @Override
    public void lineTo(final double x, final double y)
    {
        m_context.lineTo(x, y);
    }

    @Override
    public void setFillGradient(final LinearGradient gradient)
    {
        m_context.setFillGradient(gradient);
    }

    @Override
    public void setFillGradient(final RadialGradient gradient)
    {
        m_context.setFillGradient(gradient);
    }

    @Override
    public void setFillGradient(final PatternGradient gradient)
    {
        m_context.setFillGradient(gradient);
    }

    @Override
    public void quadraticCurveTo(final double cpx, final double cpy, final double x, final double y)
    {
        m_context.quadraticCurveTo(cpx, cpy, x, y);
    }

    @Override
    public void transform(final Transform transform)
    {
        m_context.transform(transform);
    }

    @Override
    public void setTransform(final Transform transform)
    {
        m_context.setTransform(transform);
    }

    @Override
    public void fillTextWithGradient(final String text, final double x, final double y, final double sx, final double sy, final double ex, final double ey, final String color)
    {
        m_context.fillTextWithGradient(text, x, y, sx, sy, ex, ey, color);
    }

    @Override
    public void setTextFont(final String font)
    {
        m_context.setTextFont(font);
    }

    @Override
    public void setTextBaseline(final TextBaseLine baseline)
    {
        m_context.setTextBaseline(baseline);
    }

    @Override
    public void setTextAlign(final TextAlign textAlign)
    {
        m_context.setTextAlign(textAlign);
    }

    @Override
    public void fillText(final String text, final double x, final double y)
    {
        m_context.fillText(text, x, y);
    }

    @Override
    public void strokeText(final String text, final double x, final double y)
    {
        m_context.strokeText(text, x, y);
    }

    @Override
    public void setGlobalAlpha(final double alpha)
    {
        m_context.setGlobalAlpha(alpha);
    }

    @Override
    public void translate(final double x, final double y)
    {
        m_context.translate(x, y);
    }

    @Override
    public void rotate(final double rot)
    {
        m_context.rotate(rot);
    }

    @Override
    public void scale(final double sx, final double sy)
    {
        m_context.scale(sx, sy);
    }

    @Override
    public void clearRect(final double x, final double y, final double wide, final double high)
    {
        m_context.clearRect(x, y, wide, high);
    }

    @Override
    public void setShadow(final Shadow shadow)
    {
        m_context.setShadow(shadow);
    }

    @Override
    public void clip()
    {
        m_context.clip();
    }

    @Override
    public void resetClip()
    {
        m_context.resetClip();
    }

    @Override
    public void setMiterLimit(final double limit)
    {
        m_context.setMiterLimit(limit);
    }

    @Override
    public boolean path(final PathPartList list)
    {
        return m_context.path(list);
    }

    @Override
    public boolean clip(final PathPartList list)
    {
        return m_context.clip(list);
    }

    @Override
    public boolean isSupported(final String feature)
    {
        return m_context.isSupported(feature);
    }

    @Override
    public boolean isPointInPath(final double x, final double y)
    {
        return m_context.isPointInPath(x, y);
    }

    @Override
    public ImageDataPixelColor getImageDataPixelColor(final int x, final int y)
    {
        return new ImageDataPixelColor(getImageData(x, y, 1, 1));
    }

    @Override
    public ImageData getImageData(final int x, final int y, final int width, final int height)
    {
        return m_context.getImageData(x, y, width, height);
    }

    @Override
    public void putImageData(final ImageData imageData, final int x, final int y)
    {
        m_context.putImageData(imageData, x, y);
    }

    @Override
    public void putImageData(final ImageData imageData, final int x, final int y, final int dirtyX, final int dirtyY, final int dirtyWidth, final int dirtyHeight)
    {
        m_context.putImageData(imageData, x, y, dirtyX, dirtyY, dirtyWidth, dirtyHeight);
    }

    @Override
    public ImageData createImageData(final double width, final double height)
    {
        return m_context.createImageData(width, height);
    }

    @Override
    public ImageData createImageData(final ImageData data)
    {
        return m_context.createImageData(data);
    }

    @Override
    public TextMetrics measureText(final String text)
    {
        return m_context.measureText(text);
    }

    @Override
    public void setGlobalCompositeOperation(final CompositeOperation operation)
    {
        m_context.setGlobalCompositeOperation(operation);
    }

    @Override
    public void setImageSmoothingEnabled(final boolean enabled)
    {
        m_context.setImageSmoothingEnabled(enabled);
    }

    @Override
    public void drawImage(final Element image, final double x, final double y)
    {
        m_context.drawImage(image, x, y);
    }

    @Override
    public void drawImage(final Element image, final double x, final double y, final double w, final double h)
    {
        m_context.drawImage(image, x, y, w, h);
    }

    @Override
    public void drawImage(final Element image, final double sx, final double sy, final double sw, final double sh, final double x, final double y, final double w, final double h)
    {
        m_context.drawImage(image, sx, sy, sw, sh, x, y, w, h);
    }

    @Override
    public void setLineDash(final DashArray dashes)
    {
        m_context.setLineDash(dashes);
    }

    @Override
    public void setLineDashOffset(final double offset)
    {
        m_context.setLineDashOffset(offset);
    }

    @Override
    public double getBackingStorePixelRatio()
    {
        return m_context.getBackingStorePixelRatio();
    }
    
    @Override
    public void fill(final Path2D path)
    {
        m_context.fill(path);
    }

    @Override
    public void stroke(final Path2D path)
    {
        m_context.stroke(path);
    }

    @Override
    public void clip(final Path2D path)
    {
        m_context.clip(path);
    }

    @Override
    public Path2D getCurrentPath()
    {
        return m_context.getCurrentPath();
    }

    @Override
    public boolean isSelection()
    {
        return m_context.isSelection();
    }

    @Override
    public boolean isDrag()
    {
        return m_context.isDrag();
    }

    @Override
    public boolean isRecording()
    {
        return true;
    }
}
