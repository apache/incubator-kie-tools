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

@JsType(isNative = true, name = "CanvasRenderingContext2D", namespace = JsPackage.GLOBAL)
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
    private static final NativeContext2D make_0(HTMLCanvasElement element) {
        return Js.uncheckedCast(element.getContext("2d"));
    }

    @JsOverlay
    public static final NativeContext2D make(final HTMLCanvasElement element, final boolean enableHidpi) {
        return make_0(element).init(enableHidpi);
    }

    protected NativeContext2D() {
    }

    @JsOverlay
    public final void initDeviceRatio() {
        if (!this.hidpiEnabled) {
            return;
        }

        HTMLCanvasElement canvas = this.canvas;

        JsPropertyMap<Object> windowMap = Js.uncheckedCast(DomGlobal.window);
        devicePixelRatio = windowMap.has("devicePixelRatio") ? (double) windowMap.get("devicePixelRatio") : 1;

        double backingStoreRatio = getBackingStorePixelRatio();

        if (devicePixelRatio != backingStoreRatio) {
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
    private final NativeContext2D init(boolean enableHidpi) {
        JsPropertyMap<Object> canvasMap = Js.uncheckedCast(this.canvas);
        canvasMap.set("imageSmoothingEnabled", false);
        canvasMap.set("webkitImageSmoothingEnabled", false);
        canvasMap.set("mozImageSmoothingEnabled", false);
        canvasMap.set("msImageSmoothingEnabled", false);
        canvasMap.set("oImageSmoothingEnabled", false);

        this.scalingRatio = 1;

        this.hidpiEnabled = enableHidpi;
        if (enableHidpi) {
            if (canvasMap.has("backingStorePixelRatio")) {
                this.backingStorePixelRatio = (double) canvasMap.get("backingStorePixelRatio");
            } else if (canvasMap.has("webkitBackingStorePixelRatio")) {
                this.backingStorePixelRatio = (double) canvasMap.get("webkitBackingStorePixelRatio");
            } else if (canvasMap.has("mozBackingStorePixelRatio")) {
                this.backingStorePixelRatio = (double) canvasMap.get("mozBackingStorePixelRatio");
            } else if (canvasMap.has("msBackingStorePixelRatio")) {
                this.backingStorePixelRatio = (double) canvasMap.get("msBackingStorePixelRatio");
            } else if (canvasMap.has("oBackingStorePixelRatio")) {
                this.backingStorePixelRatio = (double) canvasMap.get("oBackingStorePixelRatio");
            } else {
                this.backingStorePixelRatio = 1;
            }
        }

        return this;
    }

    @JsOverlay
    public final boolean isHidpiEnabled() {
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

    @JsOverlay
    public final void setGlobalCompositeOperation(String operation) {
        this.globalCompositeOperation = operation != null ? operation : "source-over";
    }

    @JsOverlay
    public final void fillTextWithGradient(String text, double x, double y, double sx, double sy, double ex, double ey, String color) {
        CanvasGradient grad = this.createLinearGradient(sx, sy, ex, ey);

        grad.addColorStop(0, color);

        grad.addColorStop(1, color);

        this.fillStyle = CanvasRenderingContext2D.FillStyleUnionType.of(grad);

        this.fillText(text, x, y);
    }

    @JsOverlay
    public final void setFillGradient(LinearGradientJSO grad) {
        if (grad != null) {
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
    public final void setFillGradient(PatternGradientJSO grad) {
        if (grad == null || grad.image == null) {
            this.fillStyle = null;
            return;
        }
        CanvasPattern pattern = this.createPattern(grad.image, grad.repeat);
        this.fillStyle = FillStyleUnionType.of(pattern);
    }

    @JsOverlay
    public final void setFillGradient(RadialGradientJSO grad) {
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
    public final void transform(Transform jso) {
        if (jso != null) {
            this.transform(jso.v[0], jso.v[1], jso.v[2], jso.v[3], jso.v[4], jso.v[5]);
        }
    }

    @JsOverlay
    public final void setTransform(Transform jso) {
        if (jso != null) {
            this.setTransform(jso.v[0], jso.v[1], jso.v[2], jso.v[3], jso.v[4], jso.v[5]);
        }
    }

    @JsOverlay
    public final void setToIdentityTransform() {
        setTransform(1, 0, 0, 1, 0, 0);
    }

    @JsOverlay
    public final void setTextFont(String font) {
        setFont(font);
    }

    @JsOverlay
    public final void setGlobalAlpha(double alpha) {
        this.globalAlpha = alpha;
    }

    @JsOverlay
    public final void setShadow(ShadowJSO shadow) {
        if (shadow != null) {
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
    public final boolean isSupported(String feature) {
        JsPropertyMap<Object> map = Js.uncheckedCast(this);
        return map.has(feature);
    }

    public final native void resetClip();

    @JsOverlay
    public final double getBackingStorePixelRatio() {
        return this.backingStorePixelRatio;
    }

    @JsOverlay
    public final boolean path(PathPartListJSO list) {
        return path(list, true);
    }

    @JsOverlay
    public final boolean path(PathPartListJSO list, boolean beginPath) {
        if (list == null) {
            return false;
        }
        int leng = list.length();
        if (leng < 1) {
            return false;
        }
        int indx = 0;
        boolean fill = false;
        if (beginPath) {
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
                    if (beginPath) {
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
    public final boolean clip(PathPartListJSO list) {
        return path(list, false);
    }
}