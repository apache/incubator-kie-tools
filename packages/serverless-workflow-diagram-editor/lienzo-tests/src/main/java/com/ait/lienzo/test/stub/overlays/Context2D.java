package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.client.core.NativeContext2D;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.image.JsImageBitmap;
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
import com.ait.lienzo.test.annotation.StubClass;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.HTMLVideoElement;
import elemental2.dom.ImageData;
import elemental2.dom.Path2D;
import elemental2.dom.TextMetrics;

@StubClass("com.ait.lienzo.client.core.Context2D")
public class Context2D {

    private final NativeContext2D jso;

    public Context2D(final HTMLCanvasElement element) {
        this(NativeContext2D.make(element, LienzoCore.get().isHidpiEnabled()));
    }

    public Context2D(final NativeContext2D jso) {
        this.jso = jso;
    }

    public NativeContext2D getNativeContext() {
        return jso;
    }

    /**
     * Save and push a new container context to the stack
     */
    public void saveContainer(String id) {
        jso.saveContainer(id);
    }

    /**
     * Restore and pop the current container context from the stack returning to the previous context
     */
    public void restoreContainer() {
        jso.restoreContainer();
    }

    /**
     * Saves the current context state (i.e style, fill, stroke...) pushing to the stack
     */
    public void save() {
        jso.save();
    }

    public void save(String id) {
        jso.save(id);
    }

    /**
     * Restore the saved context state (i.e style, fill, stroke...) by popping from the stack
     */
    public void restore() {
        jso.restore();
    }

    public void beginPath() {
        jso.beginPath();
    }

    public void closePath() {
        jso.closePath();
    }

    public void rect(final double x, final double y, final double w, final double h) {
        jso.rect(x, y, w, h);
    }

    public void fillRect(final double x, final double y, final double w, final double h) {
        jso.fillRect(x, y, w, h);
    }

    public void fill() {
        jso.fill();
    }

    public void stroke() {
        jso.stroke();
    }

    public void setFillColor(final String color) {
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
        jso.arc(x, y, radius, startAngle, endAngle, antiClockwise);
    }

    public void arc(final double x, final double y, final double radius, final double startAngle, final double endAngle) {
        jso.arc(x, y, radius, startAngle, endAngle, false);
    }

    public void arcTo(final double x1, final double y1, final double x2, final double y2, final double radius) {
        jso.arcTo(x1, y1, x2, y2, radius);
    }

    public void ellipse(final double x, final double y, final double radiusX, final double radiusY, final double rotation, final double startAngle, final double endAngle, final boolean antiClockwise) {
        jso.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle, antiClockwise);
    }

    public void ellipse(final double x, final double y, final double radiusX, final double radiusY, final double rotation, final double startAngle, final double endAngle) {
        jso.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle);
    }

    public void setStrokeColor(final String color) {
    }

    /**
     * Sets the stroke color
     *
     * @param color {@link ColorName} or {@link IColor}
     * @return this Context2D
     */
    public void setStrokeColor(final IColor color) {
        //jso.strokeStyle = StrokeStyleUnionType.of((null != color) ? color.getColorString() : null);
    }

    public void setStrokeWidth(final double width) {
        jso.setLineWidth(width);
    }

    public void setLineCap(final LineCap linecap) {
        jso.setLineCap((null != linecap) ? linecap.getValue() : null);
    }

    public void setLineJoin(final LineJoin linejoin) {
        jso.setLineJoin((null != linejoin) ? linejoin.getValue() : null);
    }

    public void transform(final double d0, final double d1, final double d2, final double d3, final double d4, final double d5) {
        jso.transform(d0, d1, d2, d3, d4, d5);
    }

    public void setTransform(final double d0, final double d1, final double d2, final double d3, final double d4, final double d5) {
        jso.setTransform(d0, d1, d2, d3, d4, d5);
    }

    public void setToIdentityTransform() {
        jso.setToIdentityTransform();
    }

    public void moveTo(final double x, final double y) {
        jso.moveTo(x, y);
    }

    public void bezierCurveTo(final double cp1x, final double cp1y, final double cp2x, final double cp2y, final double x, final double y) {
        jso.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
    }

    public void lineTo(final double x, final double y) {
        jso.lineTo(x, y);
    }

    public void setFillGradient(final LinearGradient gradient) {
        jso.setFillGradient((null != gradient) ? gradient.getJSO() : null);
    }

    public void setFillGradient(final RadialGradient gradient) {
        jso.setFillGradient((null != gradient) ? gradient.getJSO() : null);
    }

    public void setFillGradient(final PatternGradient gradient) {
        jso.setFillGradient((null != gradient) ? gradient.getJSO() : null);
    }

    public void quadraticCurveTo(final double cpx, final double cpy, final double x, final double y) {
        jso.quadraticCurveTo(cpx, cpy, x, y);
    }

    public void transform(final Transform transform) {
        jso.transform((null != transform) ? transform : null);
    }

    public void setTransform(final Transform transform) {
        jso.setTransform((null != transform) ? transform : null);
    }

    public void fillTextWithGradient(final String text, final double x, final double y, final double sx, final double sy, final double ex, final double ey, final String color) {
        jso.fillTextWithGradient(text, x, y, sx, sy, ex, ey, color);
    }

    public void setTextFont(final String font) {
        jso.setTextFont(font);
    }

    public void setTextBaseline(final TextBaseLine baseline) {
        jso.setTextBaseline((null != baseline) ? baseline.getValue() : null);
    }

    public void setTextAlign(final TextAlign textAlign) {
        jso.setTextAlign((null != textAlign) ? textAlign.getValue() : null);
    }

    public void fillText(final String text, final double x, final double y) {
        jso.fillText(text, x, y);
    }

    public void strokeText(final String text, final double x, final double y) {
        jso.strokeText(text, x, y);
    }

    public void setGlobalAlpha(final double alpha) {
        jso.setGlobalAlpha(alpha);
    }

    public void translate(final double x, final double y) {
        jso.translate(x, y);
    }

    public void rotate(final double rot) {
        jso.rotate(rot);
    }

    public void scale(final double sx, final double sy) {
        jso.scale(sx * jso.scalingRatio, sy * jso.scalingRatio);
    }

    public void clearRect(final double x, final double y, final double wide, final double high) {
        jso.clearRect(x, y, wide, high);
    }

    public void setShadow(final Shadow shadow) {
        jso.setShadow((null != shadow) ? shadow.getJSO() : null);
    }

    public void clip() {
        jso.clip();
    }

    public void resetClip() {
        jso.resetClip();
    }

    public void setMiterLimit(final double limit) {
        jso.setMiterLimit(limit);
    }

    public boolean path(final PathPartList list) {
        if (null != list) {
            return jso.path(list.getJSO());
        }
        return false;
    }

    public boolean clip(final PathPartList list) {
        if (null != list) {
            return jso.clip(list.getJSO());
        }
        return false;
    }

    public boolean isSupported(final String feature) {
        return jso.isSupported(feature);
    }

    public boolean isPointInPath(final double x, final double y) {
        return jso.isPointInPath(x, y);
    }

    public ImageDataPixelColor getImageDataPixelColor(final int x, final int y) {
        return new ImageDataPixelColor(getImageData(x, y, 1, 1));
    }

    public ImageData getImageData(final int x, final int y, final int width, final int height) {
        return jso.getImageData(x, y, width, height);
    }

    public void putImageData(final ImageData imageData, final int x, final int y) {
        jso.putImageData(imageData, x, y);
    }

    public void putImageData(final ImageData imageData, final int x, final int y, final int dirtyX, final int dirtyY, final int dirtyWidth, final int dirtyHeight) {
        jso.putImageData(imageData, x, y, dirtyX, dirtyY, dirtyWidth, dirtyHeight);
    }

    public ImageData createImageData(final int width, final int height) {
        return jso.createImageData(width, height);
    }

    public ImageData createImageData(final ImageData data) {
        return jso.createImageData(data.width, data.height);
    }

    public TextMetrics measureText(final String text) {
        TextMetrics textMetrics = new TextMetrics();
        textMetrics.width = text.length();
        return textMetrics;
    }

    public void setGlobalCompositeOperation(final CompositeOperation operation) {
        jso.setGlobalCompositeOperation((null != operation) ? operation.getValue() : null);
    }

    public void drawImage(final HTMLImageElement image, final double x, final double y) {
        jso.drawImage(image, x, y);
    }

    public void drawImage(final JsImageBitmap image, final double x, final double y) {
        jso.drawImage(image, x, y);
    }

    public void drawImage(final HTMLImageElement image, final double x, final double y, final double w, final double h) {
        jso.drawImage(image, x, y, w, h);
    }

    public void drawImage(final JsImageBitmap image, final double x, final double y, final double w, final double h) {
        jso.drawImage(image, x, y, w, h);
    }

    public void drawImage(final HTMLImageElement image, final double sx, final double sy, final double sw, final double sh, final double x, final double y, final double w, final double h) {
        jso.drawImage(image, sx, sy, sw, sh, x, y, w, h);
    }

    public void drawImage(final JsImageBitmap image, final double sx, final double sy, final double sw, final double sh, final double x, final double y, final double w, final double h) {
        jso.drawImage(image, sx, sy, sw, sh, x, y, w, h);
    }

    public void drawImage(final HTMLCanvasElement image, final double x, final double y) {
        jso.drawImage(image, x, y);
    }

    public void drawImage(final HTMLCanvasElement image, final double x, final double y, final double w, final double h) {
        jso.drawImage(image, x, y, w, h);
    }

    public void drawImage(final HTMLCanvasElement image, final double sx, final double sy, final double sw, final double sh, final double x, final double y, final double w, final double h) {
        jso.drawImage(image, sx, sy, sw, sh, x, y, w, h);
    }

    public void drawImage(final HTMLVideoElement image, final double x, final double y) {
        jso.drawImage(image, x, y);
    }

    public void drawImage(final HTMLVideoElement image, final double x, final double y, final double w, final double h) {
        jso.drawImage(image, x, y, w, h);
    }

    public void drawImage(final HTMLVideoElement image, final double sx, final double sy, final double sw, final double sh, final double x, final double y, final double w, final double h) {
        jso.drawImage(image, sx, sy, sw, sh, x, y, w, h);
    }

    public void setLineDash(final DashArray dashes) {
        jso.setLineDash((null != dashes) ? dashes.getJSO() : null);
    }

    public void setLineDashOffset(final double offset) {
        jso.lineDashOffset = offset;
    }

    public double getBackingStorePixelRatio() {
        return jso.getBackingStorePixelRatio();
    }

    public void fill(final Path2D path) {
        jso.fill(path);
    }

    public void stroke(final Path2D path) {
        jso.stroke(path);
    }

    public void clip(final Path2D path) {
        jso.clip(path);
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
