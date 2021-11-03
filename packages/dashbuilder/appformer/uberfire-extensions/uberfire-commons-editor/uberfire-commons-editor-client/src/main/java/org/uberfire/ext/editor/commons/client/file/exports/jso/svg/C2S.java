/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.editor.commons.client.file.exports.jso.svg;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import elemental2.core.JsArray;
import elemental2.dom.CanvasGradient;
import elemental2.dom.Element;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.ImageData;
import elemental2.dom.TextMetrics;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * This is a JsInterop class responsible to make the interface to the <b>canvas2svg</b> library,
 * and the overlay operations to export canvas to SVG.
 * @see <a href="https://github.com/gliffy/canvas2svg">https://github.com/gliffy/canvas2svg</a>
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
class C2S {

    @JsOverlay
    protected static final C2S create(double width, double height, Object nativeContext) {
        C2SSettings settings = new C2SSettings();
        settings.setWidth(width);
        settings.setHeight(height);
        settings.setEnableMirroring(true);
        settings.setCtx(nativeContext);
        C2S c2S = new C2S(settings);
        c2S.setImageSmoothingEnabled(false);
        //setting the viewBox on the svg root, this is necessary to scaling th svg on html
        c2S.setViewBox(width, height);
        return c2S;
    }

    @JsOverlay
    protected final void setViewBox(double width, double height) {
        final String viewBox = "0 0 " + width + " " + height;
        Optional.ofNullable(this.__root)
                .ifPresent(root -> root.setAttribute("viewBox", viewBox));
    }

    protected C2S(C2SSettings options) {
    }

    //----------------------------- C2S Methods  -----------------------------

    @JsProperty
    private final native void setOptions(C2SSettings settings);

    public final native String getSerializedSvg();

    public final native Element getSvg();

    //----------------------------- Native Context Methods -----------------------------
    @JsProperty
    public final native void setFillStyle(String fillStyleColor);

    @JsProperty
    public final native void setStrokeStyle(String fillStyleColor);

    @JsProperty
    public final native void setLineWidth(double var1);

    @JsProperty
    public final native void setLineCap(String lineCap);

    @JsProperty
    public final native void setLineJoin(String lineJoin);

    @JsProperty
    public final native void setImageSmoothingEnabled(boolean enabled);

    @JsProperty
    public final native void setFont(String font);

    @JsProperty
    public final native void setTextBaseline(String baseline);

    @JsProperty
    public final native void setTextAlign(String align);

    @JsProperty
    public final native void setGlobalAlpha(double alpha);

    @JsProperty
    public final native void setShadowColor(String color);

    @JsProperty
    public final native void setShadowOffsetX(double color);

    @JsProperty
    public final native void setShadowOffsetY(double color);

    @JsProperty
    public final native void setShadowBlur(int color);

    @JsProperty
    public final native void setMiterLimit(double limit);

    @JsProperty
    public final native void setLineDashOffset(double offset);

    public final native void save();

    public final native void restore();

    public final native void beginPath();

    public final native void closePath();

    public final native void moveTo(double x, double y);

    public final native void lineTo(double x, double y);

    public final native void setGlobalCompositeOperation(String operation);

    public final native void quadraticCurveTo(double cpx, double cpy, double x, double y);

    public final native void arc(double x, double y, double radius, double startAngle, double endAngle);

    public final native void arc(double x, double y, double radius, double startAngle, double endAngle, boolean antiClockwise);

    public final native void ellipse(double x, double y, double rx, double ry, double ro, double sa, double ea, boolean ac);

    public final native void ellipse(double x, double y, double rx, double ry, double ro, double sa, double ea);

    public final native void arcTo(double x1, double y1, double x2, double y2, double radius);

    public final native void bezierCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y);

    public final native void clearRect(double x, double y, double w, double h);

    public final native void clip();

    public final native void fill();

    public final native void stroke();

    public final native void fillRect(double x, double y, double w, double h);

    public final native void fillText(String text, double x, double y);

    public final native CanvasGradient createLinearGradient(double x0, double y0, double x1, double y1);

    public final native CanvasGradient createRadialGradient(double x0, double y0, double r0, double x1, double y1, double r1);

    public final native void rect(double x, double y, double w, double h);

    public final native void rotate(double angle);

    public final native void scale(double sx, double sy);

    public final native void transform(double d0, double d1, double d2, double d3, double d4, double d5);

    public final native void setTransform(double d0, double d1, double d2, double d3, double d4, double d5);

    public final native void strokeText(String text, double x, double y);

    public final native void translate(double x, double y);

    public final native boolean isPointInPath(double x, double y);

    public final native void putImageData(ImageData imageData, double x, double y);

    public final native void putImageData(ImageData imageData, double x, double y, double dx, double dy, double dw, double dh);

    public final native void resetClip();

    public final native void setLineDash(double[] dashes);

    public final native TextMetrics measureText(String text);

    public final native HTMLCanvasElement createImageData(ImageData data);

    public final native ImageData getImageData(double x, double y, double width, double height);

    public final native ImageData createImageData(double width, double height);

    public final native void drawImage(Element image, double x, double y);

    public final native void drawImage(Element image, double x, double y, double w, double h);

    public final native void drawImage(Element image, double sx, double sy, double sw, double sh, double x, double y, double w, double h);

    // -----------------------------JS overlay save/restore group -----------------------------
    @JsOverlay
    public final void saveGroup(Map<String, String> attributes) {
        Element group = this.__createElement("g");
        Element parent = this.__closestGroupOrSvg(null);
        this.__groupStack.push(parent);
        parent.appendChild(group);
        this.__currentElement = group;
        //setting the group attributes
        addAttributes(attributes);
    }

    @JsOverlay
    public final void restoreGroup() {
        this.__currentElement = (Element) this.__groupStack.pop();
        //Clearing canvas will make the poped group invalid, currentElement is set to the root group node.
        if (this.__currentElement == null) {
            this.__currentElement = (Element) this.__root.childNodes.item(1);
        }
    }

    @JsOverlay
    public final void saveStyle() {
        this.__stack.push(this.__getStyleState());
    }

    @JsOverlay
    public final void restoreStyle() {
        this.__currentElementsToStyle = null;
        Object state = this.__stack.pop();
        this.__applyStyleState(state);
    }

    @JsOverlay
    public final void addAttributes(Map<String, String> attributes) {
        Optional.ofNullable(attributes).ifPresent(attr -> attr.entrySet()
                .stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .forEach(entry -> this.__currentElement.setAttribute(entry.getKey(), entry.getValue())));
    }

    public final native Element __createElement(String elementName);

    public final native Element __closestGroupOrSvg(Object node);

    public final native Object __getStyleState();

    public final native void __applyStyleState(Object styleState);

    @JsProperty
    public JsArray __groupStack;

    @JsProperty
    public JsArray __stack;

    @JsProperty
    public Element __currentElement;

    @JsProperty
    public Element __root;

    @JsProperty
    public Object __currentElementsToStyle;
}