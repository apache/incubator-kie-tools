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
import java.util.Optional;

import elemental2.dom.CanvasGradient;
import elemental2.dom.Element;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.ImageData;
import elemental2.dom.TextMetrics;
import jsinterop.annotations.JsOverlay;
import org.uberfire.ext.editor.commons.client.file.exports.svg.IContext2D;

/**
 * Delegation class to allow the abstraction of {@link IContext2D}, since the JsInterop classes does not allow
 * methods override with {@link JsOverlay} annotation.
 */
public class C2SContext2D implements IContext2D {

    private final C2S delegate;

    public C2SContext2D(double width, double height, Object nativeContext) {
        delegate = C2S.create(width, height, nativeContext);
    }

    protected C2SContext2D(C2S delegate) {
        this.delegate = delegate;
    }

    public String getSerializedSvg() {
        return delegate.getSerializedSvg();
    }

    public void setFillStyle(String fillStyleColor) {
        delegate.setFillStyle(fillStyleColor);
    }

    public void setStrokeStyle(String fillStyleColor) {
        delegate.setStrokeStyle(fillStyleColor);
    }

    public void setLineWidth(double var1) {
        delegate.setLineWidth(var1);
    }

    public void setLineCap(String lineCap) {
        delegate.setLineCap(lineCap);
    }

    public void setLineJoin(String lineJoin) {
        delegate.setLineJoin(lineJoin);
    }

    public void setImageSmoothingEnabled(boolean enabled) {
        delegate.setImageSmoothingEnabled(enabled);
    }

    public void setFont(String font) {
        delegate.setFont(font);
    }

    public void setTextBaseline(String baseline) {
        delegate.setTextBaseline(baseline);
    }

    public void setTextAlign(String align) {
        delegate.setTextAlign(align);
    }

    public void setGlobalAlpha(double alpha) {
        delegate.setGlobalAlpha(alpha);
    }

    public void setShadowColor(String color) {
        delegate.setShadowColor(color);
    }

    public void setShadowOffsetX(double color) {
        delegate.setShadowOffsetX(color);
    }

    public void setShadowOffsetY(double color) {
        delegate.setShadowOffsetY(color);
    }

    public void setShadowBlur(int color) {
        delegate.setShadowBlur(color);
    }

    public void setMiterLimit(double limit) {
        delegate.setMiterLimit(limit);
    }

    public void setLineDashOffset(double offset) {
        delegate.setLineDashOffset(offset);
    }

    public void addAttributes(Map<String, String> attributes) {
        delegate.addAttributes(attributes);
    }

    public void saveGroup(Map<String, String> attributes) {
        delegate.saveGroup(attributes);
    }

    public void restoreGroup() {
        delegate.restoreGroup();
    }

    public void saveStyle() {
        delegate.saveStyle();
    }

    public void restoreStyle() {
        delegate.restoreStyle();
    }

    public void save() {
        delegate.save();
    }

    public void restore() {
        delegate.restore();
    }

    public void beginPath() {
        delegate.beginPath();
    }

    public void closePath() {
        delegate.closePath();
    }

    public void moveTo(double x, double y) {
        delegate.moveTo(x, y);
    }

    public void lineTo(double x, double y) {
        delegate.lineTo(x, y);
    }

    public void setGlobalCompositeOperation(String operation) {
        delegate.setGlobalCompositeOperation(operation);
    }

    public void quadraticCurveTo(double cpx, double cpy, double x, double y) {
        delegate.quadraticCurveTo(cpx, cpy, x, y);
    }

    public void arc(double x, double y, double radius, double startAngle, double endAngle) {
        delegate.arc(x, y, radius, startAngle, endAngle);
    }

    public void arc(double x, double y, double radius, double startAngle, double endAngle, boolean antiClockwise) {
        delegate.arc(x, y, radius, startAngle, endAngle, antiClockwise);
    }

    public void ellipse(double x, double y, double rx, double ry, double ro, double sa, double ea, boolean ac) {
        delegate.ellipse(x, y, rx, ry, ro, sa, ea, ac);
    }

    public void ellipse(double x, double y, double rx, double ry, double ro, double sa, double ea) {
        delegate.ellipse(x, y, rx, ry, ro, sa, ea);
    }

    public void arcTo(double x1, double y1, double x2, double y2, double radius) {
        delegate.arcTo(x1, y1, x2, y2, radius);
    }

    public void bezierCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y) {
        delegate.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
    }

    public void clearRect(double x, double y, double w, double h) {
        delegate.clearRect(x, y, w, h);
    }

    public void clip() {
        delegate.clip();
    }

    public void fill() {
        delegate.fill();
    }

    public void stroke() {
        delegate.stroke();
    }

    public void fillRect(double x, double y, double w, double h) {
        delegate.fillRect(x, y, w, h);
    }

    public void fillText(String text, double x, double y) {
        delegate.fillText(text, x, y);
    }

    public CanvasGradient createLinearGradient(double x0, double y0, double x1, double y1) {
        return delegate.createLinearGradient(x0, y0, x1, y1);
    }

    public CanvasGradient createRadialGradient(double x0, double y0, double r0, double x1, double y1, double r1) {
        return delegate.createRadialGradient(x0, y0, r0, x1, y1, r1);
    }

    public void rect(double x, double y, double w, double h) {
        delegate.rect(x, y, w, h);
    }

    public void rotate(double angle) {
        delegate.rotate(angle);
    }

    public void scale(double sx, double sy) {
        delegate.scale(sx, sy);
    }

    public void transform(double d0, double d1, double d2, double d3, double d4, double d5) {
        delegate.transform(d0, d1, d2, d3, d4, d5);
    }

    public void setTransform(double d0, double d1, double d2, double d3, double d4, double d5) {
        delegate.setTransform(d0, d1, d2, d3, d4, d5);
    }

    public void strokeText(String text, double x, double y) {
        delegate.strokeText(text, x, y);
    }

    public void translate(double x, double y) {
        delegate.translate(x, y);
    }

    public boolean isPointInPath(double x, double y) {
        return delegate.isPointInPath(x, y);
    }

    public void putImageData(ImageData imageData, double x, double y) {
        delegate.putImageData(imageData, x, y);
    }

    public void putImageData(ImageData imageData, double x, double y, double dx, double dy, double dw, double dh) {
        delegate.putImageData(imageData, x, y, dx, dy, dw, dh);
    }

    public void resetClip() {
        delegate.resetClip();
    }

    public void setLineDash(double[] dashes) {
        delegate.setLineDash(dashes);
    }

    public TextMetrics measureText(String text) {
        return delegate.measureText(text);
    }

    public HTMLCanvasElement createImageData(ImageData data) {
        return delegate.createImageData(data);
    }

    public ImageData getImageData(double x, double y, double width, double height) {
        return delegate.getImageData(x, y, width, height);
    }

    public ImageData createImageData(double width, double height) {
        return delegate.createImageData(width, height);
    }

    public void drawImage(Element image, double x, double y) {
        delegate.drawImage(image, x, y);
    }

    public void drawImage(Element image, double x, double y, double w, double h) {
        delegate.drawImage(image, x, y, w, h);
    }

    public void drawImage(Element image, double sx, double sy, double sw, double sh, double x, double y, double w, double h) {
        delegate.drawImage(image, sx, sy, sw, sh, x, y, w, h);
    }

    protected C2S getDelegate() {
        return delegate;
    }
}
