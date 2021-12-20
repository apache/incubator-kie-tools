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

import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.types.DashArray;
import com.ait.lienzo.client.core.types.ImageDataPixelColor;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.PatternGradient;
import com.ait.lienzo.client.core.types.RadialGradient;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.CompositeOperation;
import com.ait.lienzo.shared.core.types.IColor;
import com.ait.lienzo.shared.core.types.LineCap;
import com.ait.lienzo.shared.core.types.LineJoin;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import elemental2.dom.BaseRenderingContext2D.FillStyleUnionType;
import elemental2.dom.BaseRenderingContext2D.StrokeStyleUnionType;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.HTMLVideoElement;
import elemental2.dom.ImageData;
import elemental2.dom.Path2D;
import elemental2.dom.TextMetrics;

/**
 * Wrapper around a JSO that serves as a proxy to access the native capabilities of Canvas 2D.
 *
 * @see {@link NativeContext2D}
 */
public class Context2D {

    private final NativeContext2D m_jso;

    public Context2D(final HTMLCanvasElement element) {
        this(NativeContext2D.make(element, LienzoCore.get().isHidpiEnabled()));
    }

    public Context2D(final NativeContext2D jso) {
        m_jso = jso;
    }

    public NativeContext2D getNativeContext() {
        return m_jso;
    }

    /**
     * Save and push a new container context to the stack
     */
    public void saveContainer(String id) {
        m_jso.saveContainer(id);
    }

    /**
     * Restore and pop the current container context from the stack returning to the previous context
     */
    public void restoreContainer() {
        m_jso.restoreContainer();
    }

    /**
     * Saves the current context state (i.e style, fill, stroke...) pushing to the stack
     */
    public void save() {
        m_jso.save();
    }

    public void save(String id) {
        m_jso.save(id);
    }

    /**
     * Restore the saved context state (i.e style, fill, stroke...) by popping from the stack
     */
    public void restore() {
        m_jso.restore();
    }

    public void beginPath() {
        m_jso.beginPath();
    }

    public void closePath() {
        m_jso.closePath();
    }

    public void rect(final double x, final double y, final double w, final double h) {
        m_jso.rect(x, y, w, h);
    }

    public void fillRect(final double x, final double y, final double w, final double h) {
        m_jso.fillRect(x, y, w, h);
    }

    public void fill() {
        m_jso.fill();
    }

    public void stroke() {
        m_jso.stroke();
    }

    public void setFillColor(final String color) {
        m_jso.fillStyle = FillStyleUnionType.of(color);
    }

    /**
     * Sets the fill color
     *
     * @param color {@link ColorName} or {@link IColor}
     * @return this Context2D
     */
    public void setFillColor(final IColor color) {
        setFillColor((null != color) ? color.getColorString() : null);
    }

    public void arc(final double x, final double y, final double radius, final double startAngle, final double endAngle, final boolean antiClockwise) {
        m_jso.arc(x, y, radius, startAngle, endAngle, antiClockwise);
    }

    public void arc(final double x, final double y, final double radius, final double startAngle, final double endAngle) {
        m_jso.arc(x, y, radius, startAngle, endAngle, false);
    }

    public void arcTo(final double x1, final double y1, final double x2, final double y2, final double radius) {
        m_jso.arcTo(x1, y1, x2, y2, radius);
    }

    public void ellipse(final double x, final double y, final double radiusX, final double radiusY, final double rotation, final double startAngle, final double endAngle, final boolean antiClockwise) {
        m_jso.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle, antiClockwise);
    }

    public void ellipse(final double x, final double y, final double radiusX, final double radiusY, final double rotation, final double startAngle, final double endAngle) {
        m_jso.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle);
    }

    public void setStrokeColor(final String color) {
        //m_jso.setStrokeColor(color);
        m_jso.strokeStyle = StrokeStyleUnionType.of(color);
    }

    /**
     * Sets the stroke color
     *
     * @param color {@link ColorName} or {@link IColor}
     * @return this Context2D
     */
    public void setStrokeColor(final IColor color) {
        m_jso.strokeStyle = StrokeStyleUnionType.of((null != color) ? color.getColorString() : null);
    }

    public void setStrokeWidth(final double width) {
        m_jso.setLineWidth(width);
    }

    public void setLineCap(final LineCap linecap) {
        m_jso.setLineCap((null != linecap) ? linecap.getValue() : null);
    }

    public void setLineJoin(final LineJoin linejoin) {
        m_jso.setLineJoin((null != linejoin) ? linejoin.getValue() : null);
    }

    public void transform(final double d0, final double d1, final double d2, final double d3, final double d4, final double d5) {
        m_jso.transform(d0, d1, d2, d3, d4, d5);
    }

    public void setTransform(final double d0, final double d1, final double d2, final double d3, final double d4, final double d5) {
        m_jso.setTransform(d0, d1, d2, d3, d4, d5);
    }

    public void setToIdentityTransform() {
        m_jso.setToIdentityTransform();
    }

    public void moveTo(final double x, final double y) {
        m_jso.moveTo(x, y);
    }

    public void bezierCurveTo(final double cp1x, final double cp1y, final double cp2x, final double cp2y, final double x, final double y) {
        m_jso.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
    }

    public void lineTo(final double x, final double y) {
        m_jso.lineTo(x, y);
    }

    public void setFillGradient(final LinearGradient gradient) {
        m_jso.setFillGradient((null != gradient) ? gradient.getJSO() : null);
    }

    public void setFillGradient(final RadialGradient gradient) {
        m_jso.setFillGradient((null != gradient) ? gradient.getJSO() : null);
    }

    public void setFillGradient(final PatternGradient gradient) {
        m_jso.setFillGradient((null != gradient) ? gradient.getJSO() : null);
    }

    public void quadraticCurveTo(final double cpx, final double cpy, final double x, final double y) {
        m_jso.quadraticCurveTo(cpx, cpy, x, y);
    }

    public void transform(final Transform transform) {
        m_jso.transform((null != transform) ? transform : null);
    }

    public void setTransform(final Transform transform) {
        m_jso.setTransform((null != transform) ? transform : null);
    }

    public void fillTextWithGradient(final String text, final double x, final double y, final double sx, final double sy, final double ex, final double ey, final String color) {
        m_jso.fillTextWithGradient(text, x, y, sx, sy, ex, ey, color);
    }

    public void setTextFont(final String font) {
        m_jso.setTextFont(font);
    }

    public void setTextBaseline(final TextBaseLine baseline) {
        m_jso.setTextBaseline((null != baseline) ? baseline.getValue() : null);
    }

    public void setTextAlign(final TextAlign textAlign) {
        m_jso.setTextAlign((null != textAlign) ? textAlign.getValue() : null);
    }

    public void fillText(final String text, final double x, final double y) {
        m_jso.fillText(text, x, y);
    }

    public void strokeText(final String text, final double x, final double y) {
        m_jso.strokeText(text, x, y);
    }

    public void setGlobalAlpha(final double alpha) {
        m_jso.setGlobalAlpha(alpha);
    }

    public void translate(final double x, final double y) {
        m_jso.translate(x, y);
    }

    public void rotate(final double rot) {
        m_jso.rotate(rot);
    }

    public void scale(final double sx, final double sy) {
        m_jso.scale(sx * m_jso.scalingRatio, sy * m_jso.scalingRatio);
    }

    public void clearRect(final double x, final double y, final double wide, final double high) {
        m_jso.clearRect(x, y, wide, high);
    }

    public void setShadow(final Shadow shadow) {
        m_jso.setShadow((null != shadow) ? shadow.getJSO() : null);
    }

    public void clip() {
        m_jso.clip();
    }

    public void resetClip() {
        m_jso.resetClip();
    }

    public void setMiterLimit(final double limit) {
        m_jso.setMiterLimit(limit);
    }

    public boolean path(final PathPartList list) {
        if (null != list) {
            return m_jso.path(list.getJSO());
        }
        return false;
    }

    public boolean clip(final PathPartList list) {
        if (null != list) {
            return m_jso.clip(list.getJSO());
        }
        return false;
    }

    public boolean isSupported(final String feature) {
        return m_jso.isSupported(feature);
    }

    public boolean isPointInPath(final double x, final double y) {
        return m_jso.isPointInPath(x, y);
    }

    public ImageDataPixelColor getImageDataPixelColor(final int x, final int y) {
        return new ImageDataPixelColor(getImageData(x, y, 1, 1));
    }

    public ImageData getImageData(final int x, final int y, final int width, final int height) {
        return m_jso.getImageData(x, y, width, height);
    }

    public void putImageData(final ImageData imageData, final int x, final int y) {
        m_jso.putImageData(imageData, x, y);
    }

    public void putImageData(final ImageData imageData, final int x, final int y, final int dirtyX, final int dirtyY, final int dirtyWidth, final int dirtyHeight) {
        m_jso.putImageData(imageData, x, y, dirtyX, dirtyY, dirtyWidth, dirtyHeight);
    }

    public ImageData createImageData(final int width, final int height) {
        return m_jso.createImageData(width, height);
    }

    public ImageData createImageData(final ImageData data) {
        return m_jso.createImageData(data.width, data.height);
    }

    public TextMetrics measureText(final String text) {
        return m_jso.measureText(text);
    }

    public void setGlobalCompositeOperation(final CompositeOperation operation) {
        m_jso.setGlobalCompositeOperation((null != operation) ? operation.getValue() : null);
    }

    public void drawImage(final HTMLImageElement image, final double x, final double y) {
        m_jso.drawImage(image, x, y);
    }

    public void drawImage(final HTMLImageElement image, final double x, final double y, final double w, final double h) {
        m_jso.drawImage(image, x, y, w, h);
    }

    public void drawImage(final HTMLImageElement image, final double sx, final double sy, final double sw, final double sh, final double x, final double y, final double w, final double h) {
        m_jso.drawImage(image, sx, sy, sw, sh, x, y, w, h);
    }

    public void drawImage(final HTMLCanvasElement image, final double x, final double y) {
        m_jso.drawImage(image, x, y);
    }

    public void drawImage(final HTMLCanvasElement image, final double x, final double y, final double w, final double h) {
        m_jso.drawImage(image, x, y, w, h);
    }

    public void drawImage(final HTMLCanvasElement image, final double sx, final double sy, final double sw, final double sh, final double x, final double y, final double w, final double h) {
        m_jso.drawImage(image, sx, sy, sw, sh, x, y, w, h);
    }

    public void drawImage(final HTMLVideoElement image, final double x, final double y) {
        m_jso.drawImage(image, x, y);
    }

    public void drawImage(final HTMLVideoElement image, final double x, final double y, final double w, final double h) {
        m_jso.drawImage(image, x, y, w, h);
    }

    public void drawImage(final HTMLVideoElement image, final double sx, final double sy, final double sw, final double sh, final double x, final double y, final double w, final double h) {
        m_jso.drawImage(image, sx, sy, sw, sh, x, y, w, h);
    }

    public void setLineDash(final DashArray dashes) {
        m_jso.setLineDash((null != dashes) ? dashes.getJSO() : null);
    }

    public void setLineDashOffset(final double offset) {
        m_jso.lineDashOffset = offset;
    }

    public double getBackingStorePixelRatio() {
        return m_jso.getBackingStorePixelRatio();
    }

    public void fill(final Path2D path) {
        m_jso.fill(path);
    }

    public void stroke(final Path2D path) {
        m_jso.stroke(path);
    }

    public void clip(final Path2D path) {
        m_jso.clip(path);
    }

    public boolean isSelection() {
        return false;
    }

    public boolean isDrag() {
        return false;
    }

    public boolean isRecording() {
        return false;
    }
}
