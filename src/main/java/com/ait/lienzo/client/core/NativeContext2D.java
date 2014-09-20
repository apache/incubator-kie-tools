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

package com.ait.lienzo.client.core;

import com.ait.lienzo.client.core.types.DashArray;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.ait.lienzo.client.core.types.LinearGradient.LinearGradientJSO;
import com.ait.lienzo.client.core.types.NFastDoubleArrayJSO;
import com.ait.lienzo.client.core.types.PatternGradient;
import com.ait.lienzo.client.core.types.PatternGradient.PatternGradientJSO;
import com.ait.lienzo.client.core.types.RadialGradient;
import com.ait.lienzo.client.core.types.RadialGradient.RadialGradientJSO;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.client.core.types.Shadow.ShadowJSO;
import com.ait.lienzo.client.core.types.TextMetrics;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.core.types.Transform.TransformJSO;
import com.ait.lienzo.shared.core.types.CompositeOperation;
import com.ait.lienzo.shared.core.types.LineCap;
import com.ait.lienzo.shared.core.types.LineJoin;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

/**
 * This class is used to natively access the Canvas 2D context.
 * 
 * @see <a href="http://www.whatwg.org/specs/web-apps/current-work/multipage/the-canvas-element.html#2dcontext">Canvas 2d Context</a> 
 */
public final class NativeContext2D extends JavaScriptObject
{
    protected NativeContext2D()
    {
    }

    public final native void save()
    /*-{
    	this.save();
    }-*/;

    public final native void restore()
    /*-{
    	this.restore();
    }-*/;

    public final native void beginPath()
    /*-{
    	this.beginPath();
    }-*/;

    public final native void closePath()
    /*-{
    	this.closePath();
    }-*/;

    public final native void moveTo(double x, double y)
    /*-{
    	this.moveTo(x, y);
    }-*/;

    public final native void lineTo(double x, double y)
    /*-{
    	this.lineTo(x, y);
    }-*/;

    public final void setLineCap(LineCap lineCap)
    {
        setLineCap(lineCap.getValue());
    }

    public final void setLineJoin(LineJoin lineJoin)
    {
        setLineJoin(lineJoin.getValue());
    }

    public final void setGlobalCompositeOperation(CompositeOperation operation)
    {
        setGlobalCompositeOperation(operation.getValue());
    }

    public final native void setGlobalCompositeOperation(String operation)
    /*-{
    	this.globalCompositeOperation = operation
    }-*/;

    public final native void setLineCap(String lineCap)
    /*-{
    	this.lineCap = lineCap
    }-*/;

    public final native void setLineJoin(String lineJoin)
    /*-{
    	this.lineJoin = lineJoin
    }-*/;

    public final native void quadraticCurveTo(double cpx, double cpy, double x, double y)
    /*-{
    	this.quadraticCurveTo(cpx, cpy, x, y);
    }-*/;

    public final native void arc(double x, double y, double radius, double startAngle, double endAngle)
    /*-{
    	this.arc(x, y, radius, startAngle, endAngle, false);
    }-*/;

    public final native void arc(double x, double y, double radius, double startAngle, double endAngle, boolean anticlockwise)
    /*-{
    	this.arc(x, y, radius, startAngle, endAngle, anticlockwise);
    }-*/;

    public final native void arcTo(double x1, double y1, double x2, double y2, double radius)
    /*-{
    	this.arcTo(x1, y1, x2, y2, radius);
    }-*/;

    public final native void bezierCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y)
    /*-{
    	this.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
    }-*/;

    public final native void clearRect(double x, double y, double w, double h)
    /*-{
    	this.clearRect(x, y, w, h);
    }-*/;

    public final native void clip()
    /*-{
    	this.clip();
    }-*/;

    public final native void fill()
    /*-{
    	this.fill();
    }-*/;

    public final native void stroke()
    /*-{
    	this.stroke();
    }-*/;

    public final native void fillRect(double x, double y, double w, double h)
    /*-{
    	this.fillRect(x, y, w, h);
    }-*/;

    public final native void fillText(String text, double x, double y)
    /*-{
    	this.fillText(text, x, y);
    }-*/;

    public final native void fillTextWithGradient(String text, double x, double y, double sx, double sy, double ex, double ey, String color)
    /*-{
    	var grad = this.createLinearGradient(sx, sy, ex, ey);

    	grad.addColorStop(0, color);

    	grad.addColorStop(1, color);

    	this.fillStyle = grad;

    	this.fillText(text, x, y);
    }-*/;

    public final native void fillText(String text, double x, double y, double maxWidth)
    /*-{
    	this.fillText(text, x, y, maxWidth);
    }-*/;

    public final native void setFillColor(String fill)
    /*-{
    	this.fillStyle = fill;
    }-*/;

    public final native String getFillColor()
    /*-{
    	return this.fillStyle;
    }-*/;

    public final native void rect(double x, double y, double w, double h)
    /*-{
    	this.rect(x, y, w, h);
    }-*/;

    public final native void rotate(double angle)
    /*-{
    	this.rotate(angle);
    }-*/;

    public final native void scale(double sx, double sy)
    /*-{
    	this.scale(sx, sy);
    }-*/;

    public final native void setStrokeColor(String color)
    /*-{
    	this.strokeStyle = color;
    }-*/;

    public final native void setStrokeWidth(double width)
    /*-{
    	this.lineWidth = width;
    }-*/;

    public final void setFillGradient(LinearGradient gradient)
    {
        this.setFillGradient(gradient.getJSO());
    }

    private final native void setFillGradient(LinearGradientJSO grad)
    /*-{
    	var that = this.createLinearGradient(grad.start.x, grad.start.y,
    			grad.end.x, grad.end.y);

    	var list = grad.colorStops;

    	for (i = 0; i < list.length; i++) {
    		that.addColorStop(list[i].stop, list[i].color);
    	}
    	this.fillStyle = that;
    }-*/;

    public final void setFillGradient(RadialGradient gradient)
    {
        this.setFillGradient(gradient.getJSO());
    }

    public final void setFillGradient(PatternGradient gradient)
    {
        this.setFillGradient(gradient.getJSO());
    }

    private final native void setFillGradient(PatternGradientJSO grad)
    /*-{
    	this.imageSmoothingEnabled = true;

    	this.fillStyle = this.createPattern(grad.image(), grad.repeat);
    }-*/;

    private final native void setFillGradient(RadialGradientJSO grad)
    /*-{
    	var that = this.createRadialGradient(grad.start.x, grad.start.y, grad.start.radius, grad.end.x, grad.end.y, grad.end.radius);

    	var list = grad.colorStops;

    	for (i = 0; i < list.length; i++) {
    		that.addColorStop(list[i].stop, list[i].color);
    	}
    	this.fillStyle = that;
    }-*/;

    public final void transform(Transform transform)
    {
        this.transform(transform.getJSO());
    }

    private final native void transform(TransformJSO jso)
    /*-{
    	this.transform(jso[0], jso[1], jso[2], jso[3], jso[4], jso[5]);
    }-*/;

    public final native void transform(double d0, double d1, double d2, double d3, double d4, double d5)
    /*-{
    	this.transform(d0, d1, d2, d3, d4, d5);
    }-*/;

    public final void setTransform(Transform transform)
    {
        this.setTransform(transform.getJSO());
    }

    private final native void setTransform(TransformJSO jso)
    /*-{
    	this.setTransform(jso[0], jso[1], jso[2], jso[3], jso[4], jso[5]);
    }-*/;

    public final native void setTransform(double d0, double d1, double d2, double d3, double d4, double d5)
    /*-{
    	this.setTransform(d0, d1, d2, d3, d4, d5);
    }-*/;

    public final native void setToIdentityTransform()
    /*-{
    	this.setTransform(1, 0, 0, 1, 0, 0);
    }-*/;

    public final native void setTextFont(String font)
    /*-{
    	this.font = font;
    }-*/;

    public final native void setTextBaseline(String baseline)
    /*-{
    	this.textBaseline = baseline;
    }-*/;

    public final native void setTextAlign(String align)
    /*-{
    	this.textAlign = align
    }-*/;

    public final native void strokeText(String text, double x, double y)
    /*-{
    	this.strokeText(text, x, y);
    }-*/;

    public final native void setGlobalAlpha(double alpha)
    /*-{
    	this.globalAlpha = alpha;
    }-*/;

    public final native void setGloalCompositeOperation(String operation)
    /*-{
    	this.globalCompositeOperation = operation;
    }-*/;

    public final native void translate(double x, double y)
    /*-{
    	this.translate(x, y);
    }-*/;

    public final void setShadow(Shadow shadow)
    {
        if (null == shadow)
        {
            this.noShadow();
        }
        else
        {
            this.setShadow(shadow.getJSO());
        }
    }

    private final native void setShadow(ShadowJSO shadow)
    /*-{
    	this.shadowColor = shadow.color;

    	this.shadowOffsetX = shadow.offset.x;

    	this.shadowOffsetY = shadow.offset.y;

    	this.shadowBlur = shadow.blur;
    }-*/;

    private final native void noShadow()
    /*-{
    	this.shadowColor = "transparent";

    	this.shadowOffsetX = 0;

    	this.shadowOffsetY = 0;

    	this.shadowBlur = 0;
    }-*/;

    public final native boolean isSupported(String feature)
    /*-{
    	var that = this[feature];

    	if (that !== undefined) {
    		return true;
    	}
    	return false;
    }-*/;

    public final native boolean isPointInPath(double x, double y)
    /*-{
    	return this.isPointInPath(x, y);
    }-*/;

    public final native ImageData getImageData(double x, double y, double width, double height)
    /*-{
    	return this.getImageData(x, y, width, height);
    }-*/;

    public final native ImageData createImageData(double width, double height)
    /*-{
        return this.createImageData(width, height);
    }-*/;

    public final native ImageData createImageData(ImageData data)
    /*-{
        return this.createImageData(data);
    }-*/;

    public final native void putImageData(ImageData imageData, double x, double y)
    /*-{
    	this.putImageData(imageData, x, y);
    }-*/;

    public final native void putImageData(ImageData imageData, double x, double y, double dirtyX, double dirtyY, double dirtyWidth, double dirtyHeight)
    /*-{
    	this.putImageData(imageData, x, y, dirtyX, dirtyY, dirtyWidth,
    			dirtyHeight);
    }-*/;

    /**
     * Returns the metrics for the given text.
     *
     * @param text the text to measure, as a String
     * @return a {@link TextMetrics} object
     */
    public final native TextMetrics measureText(String text)
    /*-{
    	return this.measureText(text);
    }-*/;

    public final native void drawImage(Element image, double x, double y)
    /*-{
    	this.drawImage(image, x, y);
    }-*/;

    public final native void drawImage(Element image, double x, double y, double w, double h)
    /*-{
    	this.drawImage(image, x, y, w, h);
    }-*/;

    public final native void drawImage(Element image, double sx, double sy, double sw, double sh, double x, double y, double w, double h)
    /*-{
    	this.imageSmoothingEnabled = true;

    	this.drawImage(image, sx, sy, sw, sh, x, y, w, h);
    }-*/;

    public final native void resetClip()
    /*-{
    	this.resetClip();
    }-*/;

    public final native void setMiterLimit(double limit)
    /*-{
    	this.miterLimit = limit;
    }-*/;

    public final void setLineDash(double[] dashes)
    {
        if ((null != dashes) && (dashes.length > 0))
        {
            DashArray d = new DashArray();

            for (int i = 0; i < dashes.length; i++)
            {
                d.push(dashes[i]);
            }
            setLineDash(d.getJSO());
        }
    }

    public final native void setLineDash(NFastDoubleArrayJSO dashes)
    /*-{
    	if (!this.LienzoSetLineDash) {
    		if (this.setLineDash) {
    			this.LienzoSetLineDash = this.setLineDash;

    			this.LienzoSetLineDashOffset = function(d) {
    				this.lineDashOffset = d | 0;
    			};
    		} else if (this.webkitLineDash) {
    			this.LienzoSetLineDash = function(d) {
    				this.webkitLineDash = d;
    			};
    			this.LienzoSetLineDashOffset = function(d) {
    				this.webkitLineDashOffset = d | 0;
    			};
    		} else {
    			this.LienzoSetLineDash = function(d) {
    				this.mozDash = d;
    			};
    			this.LienzoSetLineDashOffset = function(d) {
    				this.mozDashOffset = d | 0;
    			};
    		}
    	}
    	this.LienzoSetLineDash(dashes);
    }-*/;

    public final native void setLineDashOffset(double offset)
    /*-{
    	if (!this.LienzoSetLineDash) {
    		if (this.setLineDash) {
    			this.LienzoSetLineDash = this.setLineDash;

    			this.LienzoSetLineDashOffset = function(d) {
    				this.lineDashOffset = d | 0;
    			};
    		} else if (this.webkitLineDash) {
    			this.LienzoSetLineDash = function(d) {
    				this.webkitLineDash = d;
    			};
    			this.LienzoSetLineDashOffset = function(d) {
    				this.webkitLineDashOffset = d | 0;
    			};
    		} else {
    			this.LienzoSetLineDash = function(d) {
    				this.mozDash = d;
    			};
    			this.LienzoSetLineDashOffset = function(d) {
    				this.mozDashOffset = d | 0;
    			};
    		}
    	}
    	this.LienzoSetLineDashOffset(offset);
    }-*/;

    public final native double getBackingStorePixelRatio()
    /*-{
    	return this.webkitBackingStorePixelRatio
    			|| this.mozBackingStorePixelRatio
    			|| this.msBackingStorePixelRatio
    			|| this.oBackingStorePixelRatio || this.backingStorePixelRatio
    			|| 1;
    }-*/;

    public final native String getGlobalCompositeOperation()
    /*-{
    	return this.globalCompositeOperation;
    }-*/;
}