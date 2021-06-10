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

package com.ait.lienzo.client.core;

import com.ait.lienzo.client.core.style.Style;
import com.ait.lienzo.client.core.types.LinearGradient.LinearGradientJSO;
import com.ait.lienzo.client.core.types.PathPartEntryJSO;
import com.ait.lienzo.client.core.types.PathPartListJSO;
import com.ait.lienzo.client.core.types.PatternGradient.PatternGradientJSO;
import com.ait.lienzo.client.core.types.RadialGradient.RadialGradientJSO;
import com.ait.lienzo.client.core.types.Shadow.ShadowJSO;
import com.ait.lienzo.client.core.types.Transform;
import elemental2.core.JsArray;
import elemental2.dom.CSSProperties;
import elemental2.dom.CanvasGradient;
import elemental2.dom.CanvasPattern;
import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * This class is used to natively access the Canvas 2D context.
 * 
 * @see <a href="http://www.whatwg.org/specs/web-apps/current-work/multipage/the-canvas-element.html#2dcontext">Canvas 2d Context</a> 
 */

@JsType(isNative = true, name="CanvasRenderingContext2D", namespace = JsPackage.GLOBAL)
public class NativeContext2D extends CanvasRenderingContext2D {

    @JsProperty
    public boolean hidpiEnabled;

    @JsProperty
    public double devicePixelRatio;

    @JsProperty
    public double scalingRatio;

    @JsProperty
    public double backingStorePixelRatio;

    @JsOverlay
    private static final NativeContext2D make_0(HTMLCanvasElement element)
    {
		return Js.uncheckedCast(element.getContext("2d"));
    }

    @JsOverlay
    public static final NativeContext2D make(final HTMLCanvasElement element, final boolean enableHidpi)
    {
        return make_0(element).init(enableHidpi);
    }

    protected NativeContext2D()
    {
    }

    @JsOverlay
    public final void initDeviceRatio()
    {
        if(!this.hidpiEnabled)
        {
                return;
        }

        HTMLCanvasElement canvas = this.canvas;

        JsPropertyMap<Object> windowMap = Js.uncheckedCast( DomGlobal.window );
        devicePixelRatio = windowMap.has("devicePixelRatio") ? (double) windowMap.get("devicePixelRatio") : 1;

        double backingStoreRatio = getBackingStorePixelRatio();

        if (devicePixelRatio != backingStoreRatio)
        {
            this.scalingRatio = devicePixelRatio / backingStoreRatio;

            double oldWidth = canvas.width;
            double oldHeight = canvas.height;

            canvas.width = (int) Math.round(oldWidth * this.scalingRatio);
            canvas.height = (int) Math.round(oldHeight * this.scalingRatio);

            canvas.style.width = CSSProperties.WidthUnionType.of(oldWidth + Style.Unit.PX.getType());
            canvas.style.height = CSSProperties.HeightUnionType.of(oldHeight + Style.Unit.PX.getType());

            scale(this.scalingRatio, this.scalingRatio);
        }

    }

    @JsOverlay
    private final NativeContext2D init(boolean enableHidpi)
    {
        JsPropertyMap<Object> canvasMap = Js.uncheckedCast( this.canvas );
        canvasMap.set("imageSmoothingEnabled", false);
        canvasMap.set("webkitImageSmoothingEnabled", false);
        canvasMap.set("mozImageSmoothingEnabled", false);
        canvasMap.set("msImageSmoothingEnabled", false);
        canvasMap.set("oImageSmoothingEnabled", false);

        this.scalingRatio = 1;

        this.hidpiEnabled = enableHidpi;
        if(enableHidpi) {
            if ( canvasMap.has("backingStorePixelRatio") )
            {
                this.backingStorePixelRatio =  (double) canvasMap.get("backingStorePixelRatio");
            }
            else if ( canvasMap.has("webkitBackingStorePixelRatio") )
            {
                this.backingStorePixelRatio =  (double) canvasMap.get("webkitBackingStorePixelRatio");
            }
            else if ( canvasMap.has("mozBackingStorePixelRatio") )
            {
                this.backingStorePixelRatio =  (double) canvasMap.get("mozBackingStorePixelRatio");
            }
            else if ( canvasMap.has("msBackingStorePixelRatio") )
            {
                this.backingStorePixelRatio =  (double) canvasMap.get("msBackingStorePixelRatio");
            }
            else if ( canvasMap.has("oBackingStorePixelRatio") )
            {
                this.backingStorePixelRatio =  (double) canvasMap.get("oBackingStorePixelRatio");
            }
            else
            {
                this.backingStorePixelRatio =  1;
            }
        }


        // @FIXME Add back in these pollyfills (or find better alterantive) (mdp)
//		if (this.setLineDash) {
//			this.setLineDashOffset = function(d) {
//				this.lineDashOffset = d;
//			};
//		} else if (this.webkitLineDash) {
//			this.setLineDash = function(d) {
//				this.webkitLineDash = d;
//			};
//			this.setLineDashOffset = function(d) {
//				this.webkitLineDashOffset = d;
//			};
//		} else if (this.mozDash) {
//			this.setLineDash = function(d) {
//				this.mozDash = d;
//			};
//			this.setLineDashOffset = function(d) {
//				this.mozDashOffset = d;
//			};
//		} else {
//			this.setLineDash = function(d) {
//			};
//			this.setLineDashOffset = function(d) {
//			};
//		}
//		if (!this.ellipse) {
//			this.ellipse = function(x, y, rx, ry, ro, sa, ea, ac) {
//				this.save();
//				this.translate(x, y);
//				this.rotate(ro);
//				this.scale(rx*scalingRatio, ry*scalingRatio);
//				this.arc(0, 0, 1, sa, ea, ac);
//				this.restore();
//			};
//		}
		return this;
    }

    @JsOverlay
    public final boolean isHidpiEnabled()
    {
        return this.hidpiEnabled;
    }

    @JsOverlay
    public final void saveContainer(String id) {
        this.save();
    }

    @JsOverlay
    public final void restoreContainer() {
        restore();
    }

    @JsOverlay
    public final void save(String id) {
        save();
    }

//    public final native void save()
//    /*-{
//		this.save();
//    }-*/;

//    public final native void restore()
//    /*-{
//		this.restore();
//    }-*/;

//    public final native void beginPath()
//    /*-{
//		this.beginPath();
//    }-*/;

//    public final native void closePath()
//    /*-{
//		this.closePath();
//    }-*/;

//    public final native void moveTo(double x, double y)
//    /*-{
//		this.moveTo(x, y);
//    }-*/;

//    public final native void lineTo(double x, double y)
//    /*-{
//		this.lineTo(x, y);
//    }-*/;

    @JsOverlay
    public final void setGlobalCompositeOperation(String operation)
    {
		this.globalCompositeOperation = operation != null ? operation : "source-over";
    }

//    public final native void setLineCap(String lineCap)
//    /*-{
//		this.lineCap = lineCap || "butt";
//    }-*/;

//    public final native void setLineJoin(String lineJoin)
//    /*-{
//		this.lineJoin = lineJoin || "miter";
//    }-*/;

//    public final native void quadraticCurveTo(double cpx, double cpy, double x, double y)
//    /*-{
//		this.quadraticCurveTo(cpx, cpy, x, y);
//    }-*/;

//    public final native void arc(double x, double y, double radius, double startAngle, double endAngle)
//    /*-{
//		this.arc(x, y, radius, startAngle, endAngle, false);
//    }-*/;

//    public final native void arc(double x, double y, double radius, double startAngle, double endAngle, boolean antiClockwise)
//    /*-{
//		this.arc(x, y, radius, startAngle, endAngle, antiClockwise);
//    }-*/;

//    public final native void ellipse(double x, double y, double rx, double ry, double ro, double sa, double ea, boolean ac)
//    /*-{
//		this.ellipse(x, y, rx, ry, ro, sa, ea, ac);
//    }-*/;

//    public final native void ellipse(double x, double y, double rx, double ry, double ro, double sa, double ea)
//    /*-{
//		this.ellipse(x, y, rx, ry, ro, sa, ea, false);
//    }-*/;

//    public final native void arcTo(double x1, double y1, double x2, double y2, double radius)
//    /*-{
//		this.arcTo(x1, y1, x2, y2, radius);
//    }-*/;

//    public final native void bezierCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y)
//    /*-{
//		this.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
//    }-*/;

//    public final native void clearRect(double x, double y, double w, double h)
//    /*-{
//		if ((w <= 0) || (h <= 0)) {
//			return;
//		}
//		this.clearRect(x, y, w, h);
//    }-*/;

//    public final native void clip()
//    /*-{
//		this.clip();
//    }-*/;

//    public final native void fill()
//    /*-{
//		this.fill();
//    }-*/;

//    public final native void stroke()
//    /*-{
//		this.stroke();
//    }-*/;

//    public final native void fillRect(double x, double y, double w, double h)
//    /*-{
//		if ((w <= 0) || (h <= 0)) {
//			return;
//		}
//		this.fillRect(x, y, w, h);
//    }-*/;

//    public final native void fillText(String text, double x, double y)
//    /*-{
//		this.fillText(text, x, y);
//    }-*/;

    @JsOverlay
    public final void fillTextWithGradient(String text, double x, double y, double sx, double sy, double ex, double ey, String color)
    {
        CanvasGradient grad = this.createLinearGradient(sx, sy, ex, ey);

		grad.addColorStop(0, color);

		grad.addColorStop(1, color);

        // @FIXME is this correct? (mdp)
        this.fillStyle = CanvasRenderingContext2D.FillStyleUnionType.of(grad);

		this.fillText(text, x, y);
    }

//    public final native void fillText(String text, double x, double y, double maxWidth)
//    /*-{
//		this.fillText(text, x, y, maxWidth);
//    }-*/;

//    public final native void setFillColor(String fill)
//    /*-{
//		this.fillStyle = fill;
//    }-*/;

//    public final native void rect(double x, double y, double w, double h)
//    /*-{
//		if ((w <= 0) || (h <= 0)) {
//			return;
//		}
//		this.rect(x, y, w, h);
//    }-*/;

//    public final native void rotate(double angle)
//    /*-{
//		this.rotate(angle);
//    }-*/;

//    public final native void scale(double sx, double sy)
//    /*-{
//		this.scale(sx*scalingRatio, sy*scalingRatio);
//    }-*/;

//    public final void setStrokeColor(String color)
//    {
//		this.strokeStyle = color;
//    };

//    @JsOverlay
//    public final void setLineWidth(double width)
//    {
//		this.lineWidth = width;
//    };

//    public final void setImageSmoothingEnabled(boolean enabled)
//    {
//		this.imageSmoothingEnabled = enabled;
//    };

    @JsOverlay
    public final void setFillGradient(LinearGradientJSO grad)
    {
        //createLinearGradient()
		if (grad!=null) {
            CanvasGradient that = this.createLinearGradient(grad.sx, grad.sy,
                                                            grad.ex, grad.ey);

			JsArray<Object[]> list = grad.colorStops;

			for (int i = 0; i < list.length; i++) {
			    // indexes : 0 is stop, colour is 1
				that.addColorStop((double) list.getAt(i)[0], (String) list.getAt(i)[1]);
			}
			this.fillStyle = FillStyleUnionType.of(that);
		} else {
			this.fillStyle = null;
		}
    }

    @JsOverlay
    public final void setFillGradient(PatternGradientJSO grad)
    {
        if ( grad == null || grad.image== null)
        {
            this.fillStyle = null;
            return;
        }
        CanvasPattern pattern =  this.createPattern(grad.image, grad.repeat);
        this.fillStyle = FillStyleUnionType.of(pattern);
    }

    @JsOverlay
    public final void setFillGradient(RadialGradientJSO grad)
    {
		if (grad != null) {
            CanvasGradient that = this.createRadialGradient(grad.sx, grad.sy, grad.sr,
                                                            grad.ex, grad.ey, grad.er);

            JsArray<Object[]> list = grad.colorStops;

            for (int i = 0; i < list.length; i++) {
                // indexes : 0 is stop, colour is 1
                that.addColorStop((double) list.getAt(i)[0], (String) list.getAt(i)[1]);
            }
            this.fillStyle = FillStyleUnionType.of(that);
		} else {
			this.fillStyle = null;
		}
    }

    @JsOverlay
    public final void transform(Transform jso)
    {
		if (jso != null) {
			this.transform(jso.v[0], jso.v[1], jso.v[2], jso.v[3], jso.v[4], jso.v[5]);
		}
    }

//    public final native void transform(double d0, double d1, double d2, double d3, double d4, double d5)
//    /*-{
//		this.transform(d0, d1, d2, d3, d4, d5);
//    }-*/;

    @JsOverlay
    public final void setTransform(Transform jso)
    {
		if (jso != null ) {
			this.setTransform(jso.v[0], jso.v[1], jso.v[2], jso.v[3], jso.v[4], jso.v[5]);
		}
    }

//    public final native void setTransform(double d0, double d1, double d2, double d3, double d4, double d5)
//    /*-{
//		this.setTransform(d0, d1, d2, d3, d4, d5);
//    }-*/;

    @JsOverlay
    public final void setToIdentityTransform()
    {
		setTransform(1, 0, 0, 1, 0, 0);
    };

    @JsOverlay
    public final void setTextFont(String font)
    {
		setFont(font);
    };

//    public final native void setTextBaseline(String baseline)
//    /*-{
//		this.textBaseline = baseline || "alphabetic";
//    }-*/;

//    public final native void setTextAlign(String align)
//    /*-{
//		this.textAlign = align || "start";
//    }-*/;

//    public final native void strokeText(String text, double x, double y)
//    /*-{
//		this.strokeText(text, x, y);
//    }-*/;

    @JsOverlay
    public final void setGlobalAlpha(double alpha)
    {
		this.globalAlpha = alpha;
    };

//    public final native void translate(double x, double y)
//    /*-{
//		this.translate(x, y);
//    }-*/;

    @JsOverlay
    public final void setShadow(ShadowJSO shadow)
    {
		if (shadow!=null) {
			this.shadowColor = shadow.getColor();
			this.shadowOffsetX = shadow.getOffset().getX();
			this.shadowOffsetY = shadow.getOffset().getY();
			this.shadowBlur = shadow.getBlur();
		} else {
			this.shadowColor = "transparent";
			this.shadowOffsetX = 0;
			this.shadowOffsetY = 0;
			this.shadowBlur = 0;
		}
    }

    @JsOverlay
    public final boolean isSupported(String feature)
    {
        JsPropertyMap<Object> map = Js.uncheckedCast(this);
        return map.has(feature);
    }

//    public final native boolean isPointInPath(double x, double y)
//    /*-{
//		return this.isPointInPath(x, y);
//    }-*/;

//    public final native ImageData getImageData(double x, double y, double width, double height)
//    /*-{
//		return this.getImageData(x, y, width, height);
//    }-*/;

//    public final native ImageData createImageData(double width, double height)
//    /*-{
//		return this.createImageData(width, height);
//    }-*/;

//    public final native ImageData createImageData(ImageData data)
//    /*-{
//		return this.createImageData(data);
//    }-*/;

//    public final native void putImageData(ImageData imageData, double x, double y)
//    /*-{
//		this.putImageData(imageData, x, y);
//    }-*/;

//    public final native void putImageData(ImageData imageData, double x, double y, double dx, double dy, double dw, double dh)
//    /*-{
//		if ((dw <= 0) || (dh <= 0)) {
//			return;
//		}
//		this.putImageData(imageData, x, y, dx, dy, dw, dh);
//    }-*/;

//    /**
//     * Returns the metrics for the given text.
//     *
//     * @param text the text to measure, as a String
//     * @return a {@link TextMetrics} object
//     */
//    public final native TextMetrics measureText(String text)
//    /*-{
//		return this.measureText(text);
//    }-*/;

//    public final native void drawImage(Element image, double x, double y)
//    /*-{
//		this.drawImage(image, x, y);
//    }-*/;

//    public final native void drawImage(Element image, double x, double y, double w, double h)
//    /*-{
//		if ((w <= 0) || (h <= 0)) {
//			return;
//		}
//		this.drawImage(image, x, y, w, h);
//    }-*/;

//    public final native void drawImage(Element image, double sx, double sy, double sw, double sh, double x, double y, double w, double h)
//    /*-{
//		if ((w <= 0) || (h <= 0)) {
//			return;
//		}
//		if ((sw <= 0) || (sh <= 0)) {
//			return;
//		}
//		this.drawImage(image, sx, sy, sw, sh, x, y, w, h);
//    }-*/;

      public final native void resetClip();
//    {
//		this.resetClip();
//    }
//
//    public final native void setMiterLimit(double limit)
//    /*-{
//		this.miterLimit = limit;
//    }-*/;

//    public final native void setLineDash(NFastDoubleArrayJSO dashes)
//    /*-{
//		this.setLineDash(dashes || []);
//    }-*/;
//
//    public final native void setLineDashOffset(double offset)
//    {
//		this.setLineDashOffset(offset);
//    };


    @JsOverlay
    public final double getBackingStorePixelRatio()
    {
		return this.backingStorePixelRatio;
    }

    @JsOverlay
    public final boolean path(PathPartListJSO list)
    {
        return path(list, true);
    }

    @JsOverlay
    public final boolean path(PathPartListJSO list, boolean beginPath)
    {
		if (list == null) {
			return false;
		}
		int leng = list.length();
		if (leng < 1) {
			return false;
		}
		int indx = 0;
		boolean fill = false;
		if(beginPath)
        {
            this.beginPath();
        }
		while (indx < leng) {
			PathPartEntryJSO e = list.get(indx++);
			double[] p = e.getPoints();

			switch (e.getCommand()) {
			case 1:
				this.lineTo(p[0], p[1]);
				break;
			case 2:
				this.moveTo(p[0], p[1]);
				break;
			case 3:
				this.bezierCurveTo(p[0], p[1], p[2], p[3], p[4], p[5]);
				break;
			case 4:
				this.quadraticCurveTo(p[0], p[1], p[2], p[3]);
				break;
			case 5:
				this.ellipse(p[0], p[1], p[2], p[3], p[6], p[4], p[4] + p[5],
						(1 - p[7]) > 0);
				break;
			case 6:
			    if (beginPath)
                {
                    this.closePath();
                }
				fill = true;
				break;
			case 7:
				this.arcTo(p[0], p[1], p[2], p[3], p[4]);
				break;
			}
		}
		return fill;
    }

    @JsOverlay
    public final boolean clip(PathPartListJSO list)
    {
        return path(list, false);
    }

//    public final native void fill(NativePath2D path)
//    /*-{
//		if (path) {
//			this.fill(path);
//		}
//    }-*/;

//    public final native void stroke(NativePath2D path)
//    /*-{
//		if (path) {
//			this.stroke(path);
//		}
//    }-*/;

//    public final native void clip(NativePath2D path)
//    /*-{
//		if (path) {
//			this.clip(path);
//		}
//    }-*/;

//    public final native NativePath2D getCurrentPath()
//    /*-{
//		return this.currentPath || null;
//    }-*/;

//    public final native void setCurrentPath(NativePath2D path)
//    /*-{
//		if (path) {
//			this.currentPath = path;
//		}
//    }-*/;
}