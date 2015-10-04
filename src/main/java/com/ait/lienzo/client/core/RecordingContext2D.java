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
import com.ait.lienzo.shared.core.types.CompositeOperation;
import com.ait.lienzo.shared.core.types.IColor;
import com.ait.lienzo.shared.core.types.LineCap;
import com.ait.lienzo.shared.core.types.LineJoin;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.google.gwt.dom.client.Element;

public class RecordingContext2D extends Context2D
{
    public RecordingContext2D(final Context2D parent)
    {
        super(parent.getNativeContext());
    }

    @Override
    public void save()
    {
        super.save();
    }

    @Override
    public void restore()
    {
        super.restore();
    }

    @Override
    public void beginPath()
    {
        super.beginPath();
    }

    @Override
    public void closePath()
    {
        super.closePath();
    }

    @Override
    public void rect(final double x, final double y, final double w, final double h)
    {
        super.rect(x, y, w, h);
    }

    @Override
    public void fillRect(final double x, final double y, final double w, final double h)
    {
        super.fillRect(x, y, w, h);
    }

    @Override
    public void fill()
    {
        super.fill();
    }

    @Override
    public void stroke()
    {
        super.stroke();
    }

    @Override
    public void setFillColor(final String color)
    {
        super.setFillColor(color);
    }

    @Override
    public void setFillColor(final IColor color)
    {
        super.setFillColor(color);
    }

    @Override
    public void arc(final double x, final double y, final double radius, final double startAngle, final double endAngle, final boolean anticlockwise)
    {
        super.arc(x, y, radius, startAngle, endAngle, anticlockwise);
    }

    @Override
    public void arc(final double x, final double y, final double radius, final double startAngle, final double endAngle)
    {
        super.arc(x, y, radius, startAngle, endAngle, false);
    }

    @Override
    public void arcTo(final double x1, final double y1, final double x2, final double y2, final double radius)
    {
        super.arcTo(x1, y1, x2, y2, radius);
    }

    @Override
    public void setStrokeColor(final String color)
    {
        super.setStrokeColor(color);
    }

    @Override
    public void setStrokeColor(final IColor color)
    {
        super.setStrokeColor(color);
    }

    @Override
    public void setStrokeWidth(final double width)
    {
        super.setStrokeWidth(width);
    }

    @Override
    public void setLineCap(final LineCap linecap)
    {
        super.setLineCap(linecap);
    }

    @Override
    public void setLineJoin(final LineJoin linejoin)
    {
        super.setLineJoin(linejoin);
    }

    @Override
    public void transform(final double d0, final double d1, final double d2, final double d3, final double d4, final double d5)
    {
        super.transform(d0, d1, d2, d3, d4, d5);
    }

    @Override
    public void setTransform(final double d0, final double d1, final double d2, final double d3, final double d4, final double d5)
    {
        super.setTransform(d0, d1, d2, d3, d4, d5);
    };

    @Override
    public void setToIdentityTransform()
    {
        super.setToIdentityTransform();
    };

    @Override
    public void moveTo(final double x, final double y)
    {
        super.moveTo(x, y);
    }

    @Override
    public void bezierCurveTo(final double cp1x, final double cp1y, final double cp2x, final double cp2y, final double x, final double y)
    {
        super.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
    }

    @Override
    public void lineTo(final double x, final double y)
    {
        super.lineTo(x, y);
    }

    @Override
    public void setFillGradient(final LinearGradient gradient)
    {
        super.setFillGradient(gradient);
    }

    @Override
    public void setFillGradient(final RadialGradient gradient)
    {
        super.setFillGradient(gradient);
    }

    @Override
    public void quadraticCurveTo(final double cpx, final double cpy, final double x, final double y)
    {
        super.quadraticCurveTo(cpx, cpy, x, y);
    }

    @Override
    public void transform(final Transform transform)
    {
        super.transform(transform);
    }

    @Override
    public void setTransform(final Transform transform)
    {
        super.setTransform(transform);
    }

    @Override
    public void fillTextWithGradient(final String text, final double x, final double y, final double sx, final double sy, final double ex, final double ey, final String color)
    {
        super.fillTextWithGradient(text, x, y, sx, sy, ex, ey, color);
    }

    @Override
    public void setTextFont(final String font)
    {
        super.setTextFont(font);
    }

    @Override
    public void setTextBaseline(final TextBaseLine baseline)
    {
        super.setTextBaseline(baseline);
    }

    @Override
    public void setTextAlign(final TextAlign textAlign)
    {
        super.setTextAlign(textAlign);
    }

    @Override
    public void fillText(final String text, final double x, final double y)
    {
        super.fillText(text, x, y);
    }

    @Override
    public void strokeText(final String text, final double x, final double y)
    {
        super.strokeText(text, x, y);
    }

    @Override
    public void setGlobalAlpha(final double alpha)
    {
        super.setGlobalAlpha(alpha);
    }

    @Override
    public void translate(final double x, final double y)
    {
        super.translate(x, y);
    }

    @Override
    public void rotate(final double rot)
    {
        super.rotate(rot);
    }

    @Override
    public void scale(final double sx, final double sy)
    {
        super.scale(sx, sy);
    }

    @Override
    public void clearRect(final double x, final double y, final double wide, final double high)
    {
        super.clearRect(x, y, wide, high);
    }

    @Override
    public void setFillGradient(final PatternGradient gradient)
    {
        super.setFillGradient(gradient);
    }

    @Override
    public void setShadow(final Shadow shadow)
    {
        super.setShadow(shadow);
    }

    @Override
    public void clip()
    {
        super.clip();
    }

    @Override
    public void resetClip()
    {
        super.resetClip();
    }

    @Override
    public void setMiterLimit(final double limit)
    {
        super.setMiterLimit(limit);
    }

    @Override
    public boolean path(final PathPartList list)
    {
        return super.path(list);
    }

    @Override
    public boolean clip(final PathPartList list)
    {
        return super.clip(list);
    }

    @Override
    public boolean isSupported(final String feature)
    {
        return super.isSupported(feature);
    }

    @Override
    public boolean isPointInPath(final double x, final double y)
    {
        return super.isPointInPath(x, y);
    }

    @Override
    public ImageDataPixelColor getImageDataPixelColor(final int x, final int y)
    {
        return new ImageDataPixelColor(getImageData(x, y, 1, 1));
    }

    @Override
    public ImageData getImageData(final int x, final int y, final int width, final int height)
    {
        return super.getImageData(x, y, width, height);
    }

    @Override
    public void putImageData(final ImageData imageData, final int x, final int y)
    {
        super.putImageData(imageData, x, y);
    }

    @Override
    public void putImageData(final ImageData imageData, final int x, final int y, final int dirtyX, final int dirtyY, final int dirtyWidth, final int dirtyHeight)
    {
        super.putImageData(imageData, x, y, dirtyX, dirtyY, dirtyWidth, dirtyHeight);
    }

    @Override
    public ImageData createImageData(final double width, final double height)
    {
        return super.createImageData(width, height);
    }

    @Override
    public ImageData createImageData(final ImageData data)
    {
        return super.createImageData(data);
    }

    @Override
    public TextMetrics measureText(final String text)
    {
        return super.measureText(text);
    }

    @Override
    public void setGlobalCompositeOperation(final CompositeOperation operation)
    {
        super.setGlobalCompositeOperation(operation);
    }

    @Override
    public boolean isSelection()
    {
        return super.isSelection();
    }

    @Override
    public boolean isDrag()
    {
        return super.isDrag();
    }

    @Override
    public void drawImage(final Element image, final double x, final double y)
    {
        super.drawImage(image, x, y);
    }

    @Override
    public void drawImage(final Element image, final double x, final double y, final double w, final double h)
    {
        super.drawImage(image, x, y, w, h);
    }

    @Override
    public void drawImage(final Element image, final double sx, final double sy, final double sw, final double sh, final double x, final double y, final double w, final double h)
    {
        super.drawImage(image, sx, sy, sw, sh, x, y, w, h);
    }

    @Override
    public void setLineDash(final DashArray dashes)
    {
        super.setLineDash(dashes);
    }

    @Override
    public void setLineDashOffset(final double offset)
    {
        super.setLineDashOffset(offset);
    }

    @Override
    public double getBackingStorePixelRatio()
    {
        return super.getBackingStorePixelRatio();
    }
}
