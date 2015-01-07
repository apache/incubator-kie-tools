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

/**
 * Wrapper around a JSO that serves as a proxy to access the native capabilities of Canvas 2D.
 * @see {@link NativeContext2D} 
 */
public class Context2D
{
    private final NativeContext2D m_jso;

    public Context2D(NativeContext2D jso)
    {
        m_jso = jso;
    }

    public void save()
    {
        m_jso.save();
    }

    public void restore()
    {
        m_jso.restore();
    }

    public void beginPath()
    {
        m_jso.beginPath();
    }

    public void closePath()
    {
        m_jso.closePath();
    }

    public void rect(double x, double y, double w, double h)
    {
        m_jso.rect(x, y, w, h);
    }

    public void fillRect(double x, double y, double w, double h)
    {
        m_jso.fillRect(x, y, w, h);
    }

    public void fill()
    {
        m_jso.fill();
    }

    public void stroke()
    {
        m_jso.stroke();
    }

    public void setFillColor(String color)
    {
        m_jso.setFillColor(color);
    }

    public String getFillColor()
    {
        return m_jso.getFillColor();
    }

    /**
     * Sets the fill color
     * 
     * @param color {@link ColorName} or {@link Color}
     * 
     * @return this Context2D
     */
    public void setFillColor(IColor color)
    {
        m_jso.setFillColor(null == color ? null : color.getColorString());
    }

    public void arc(double x, double y, double radius, double startAngle, double endAngle, boolean anticlockwise)
    {
        m_jso.arc(x, y, radius, startAngle, endAngle, anticlockwise);
    }

    public void arc(double x, double y, double radius, double startAngle, double endAngle)
    {
        m_jso.arc(x, y, radius, startAngle, endAngle, false);
    }

    public void arcTo(double x1, double y1, double x2, double y2, double radius)
    {
        m_jso.arcTo(x1, y1, x2, y2, radius);
    }

    public void setStrokeColor(String color)
    {
        m_jso.setStrokeColor(color);
    }

    /**
     * Sets the stroke color
     * 
     * @param color {@link ColorName} or {@link Color}
     * 
     * @return this Context2D
     */
    public void setStrokeColor(IColor color)
    {
        m_jso.setStrokeColor(null == color ? null : color.getColorString());
    }

    public void setStrokeWidth(double width)
    {
        m_jso.setStrokeWidth(width);
    }

    public void setLineCap(LineCap linecap)
    {
        m_jso.setLineCap(linecap);
    }

    public void setLineJoin(LineJoin linejoin)
    {
        m_jso.setLineJoin(linejoin);
    }

    public void transform(double d0, double d1, double d2, double d3, double d4, double d5)
    {
        m_jso.transform(d0, d1, d2, d3, d4, d5);
    }

    public void setTransform(double d0, double d1, double d2, double d3, double d4, double d5)
    {
        m_jso.setTransform(d0, d1, d2, d3, d4, d5);
    };

    public void setToIdentityTransform()
    {
        m_jso.setToIdentityTransform();
    };

    public void moveTo(double x, double y)
    {
        m_jso.moveTo(x, y);
    }

    public void bezierCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y)
    {
        m_jso.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
    }

    public void lineTo(double x, double y)
    {
        m_jso.lineTo(x, y);
    }

    public void setFillGradient(LinearGradient gradient)
    {
        m_jso.setFillGradient(gradient);
    }

    public void setFillGradient(RadialGradient gradient)
    {
        m_jso.setFillGradient(gradient);
    }

    public void quadraticCurveTo(double cpx, double cpy, double x, double y)
    {
        m_jso.quadraticCurveTo(cpx, cpy, x, y);
    }

    public void transform(Transform transform)
    {
        m_jso.transform(transform);
    }

    public void setTransform(Transform transform)
    {
        m_jso.setTransform(transform);
    }

    public void setTextFont(String font)
    {
        m_jso.setTextFont(font);
    }

    public void setTextBaseline(TextBaseLine baseline)
    {
        if (null != baseline)
        {
            m_jso.setTextBaseline(baseline.getValue());
        }
    }

    public void setTextAlign(TextAlign textAlign)
    {
        if (null != textAlign)
        {
            m_jso.setTextAlign(textAlign.getValue());
        }
    }

    public void fillText(String text, double x, double y)
    {
        m_jso.fillText(text, x, y);
    }

    public void strokeText(String text, double x, double y)
    {
        m_jso.strokeText(text, x, y);
    }

    public void setGlobalAlpha(double alpha)
    {
        m_jso.setGlobalAlpha(alpha);
    }

    public void translate(double x, double y)
    {
        m_jso.translate(x, y);
    }

    public void rotate(double rot)
    {
        m_jso.rotate(rot);
    }

    public void scale(double sx, double sy)
    {
        m_jso.scale(sx, sy);
    }

    public void clearRect(double x, double y, double wide, double high)
    {
        m_jso.clearRect(x, y, wide, high);
    }

    public void setFillGradient(PatternGradient gradient)
    {
        m_jso.setFillGradient(gradient);
    }

    public void setShadow(Shadow shadow)
    {
        m_jso.setShadow(shadow);
    }

    public void clip()
    {
        m_jso.clip();
    }

    public void resetClip()
    {
        m_jso.resetClip();
    }

    public void setMiterLimit(double limit)
    {
        m_jso.setMiterLimit(limit);
    }

    public boolean path(PathPartList list)
    {
        return m_jso.path(list.getJSO());
    }

    public boolean isSupported(String feature)
    {
        return m_jso.isSupported(feature);
    }

    public boolean isPointInPath(double x, double y)
    {
        return m_jso.isPointInPath(x, y);
    }

    public ImageDataPixelColor getImageDataPixelColor(int x, int y)
    {
        return new ImageDataPixelColor(getImageData(x, y, 1, 1));
    }

    public ImageData getImageData(int x, int y, int width, int height)
    {
        return m_jso.getImageData(x, y, width, height);
    }

    public void putImageData(ImageData imageData, int x, int y)
    {
        m_jso.putImageData(imageData, x, y);
    }

    public void putImageData(ImageData imageData, int x, int y, int dirtyX, int dirtyY, int dirtyWidth, int dirtyHeight)
    {
        m_jso.putImageData(imageData, x, y, dirtyX, dirtyY, dirtyWidth, dirtyHeight);
    }

    public ImageData createImageData(double width, double height)
    {
        return m_jso.createImageData(width, height);
    }

    public ImageData createImageData(ImageData data)
    {
        return m_jso.createImageData(data);
    }

    public TextMetrics measureText(String text)
    {
        return m_jso.measureText(text);
    }

    public void setGlobalCompositeOperation(CompositeOperation operation)
    {
        m_jso.setGlobalCompositeOperation(operation);
    }

    public CompositeOperation getGlobalCompositeOperation()
    {
        return CompositeOperation.lookup(m_jso.getGlobalCompositeOperation());
    }

    public boolean isSelection()
    {
        return false;
    }

    public boolean isDrag()
    {
        return false;
    }

    public NativeContext2D getJSO()
    {
        return m_jso;
    }

    public void drawImage(Element image, double x, double y)
    {
        m_jso.drawImage(image, x, y);
    }

    public void drawImage(Element image, double x, double y, double w, double h)
    {
        m_jso.drawImage(image, x, y, w, h);
    }

    public void drawImage(Element image, double sx, double sy, double sw, double sh, double x, double y, double w, double h)
    {
        m_jso.drawImage(image, sx, sy, sw, sh, x, y, w, h);
    }

    public void setLineDash(DashArray dashes)
    {
        if (null != dashes)
        {
            m_jso.setLineDash(dashes.getJSO());
        }
    }

    public void setLineDashOffset(double offset)
    {
        m_jso.setLineDashOffset(offset);
    }

    public final double getBackingStorePixelRatio()
    {
        return m_jso.getBackingStorePixelRatio();
    }
}
