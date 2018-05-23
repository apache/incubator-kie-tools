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

package org.kie.workbench.common.stunner.client.lienzo.canvas.export;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import com.ait.lienzo.client.core.INativeContext2D;
import com.ait.lienzo.client.core.Path2D;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.ait.lienzo.client.core.types.PathPartEntryJSO;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.PatternGradient;
import com.ait.lienzo.client.core.types.RadialGradient;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.client.core.types.TextMetrics;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.tooling.nativetools.client.collection.NFastDoubleArrayJSO;
import com.google.gwt.dom.client.Element;
import elemental2.dom.HTMLCanvasElement;
import org.kie.workbench.common.stunner.client.lienzo.util.NativeClassConverter;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.uberfire.ext.editor.commons.client.file.exports.svg.IContext2D;

public class DelegateNativeContext2D implements INativeContext2D {

    protected static final String DEFAULT_NODE_ID = "id";

    interface Converter {

        <I, O> O convert(I input, Class<O> outputType);
    }

    private final IContext2D context;
    private final Converter nativeClassConverter;
    private String svgNodeId;
    private AbstractCanvasHandler canvasHandler;

    public DelegateNativeContext2D(IContext2D context, AbstractCanvasHandler canvasHandler) {
        this(context, canvasHandler, NativeClassConverter::convert);
    }

    DelegateNativeContext2D(IContext2D context, AbstractCanvasHandler canvasHandler, Converter converter) {
        this.context = context;
        this.nativeClassConverter = converter;
        this.canvasHandler = canvasHandler;
        this.svgNodeId = getSvgNodeId(canvasHandler).orElse(DEFAULT_NODE_ID);
    }

    /**
     * Get the SVG node id referred to the current diagram definition set. It uses the adapters to get the id.
     * @param canvasHandler the current {@link CanvasHandler}
     * @return the optional id if set otherwise empty.
     */
    private Optional<String> getSvgNodeId(AbstractCanvasHandler canvasHandler) {
        final String diagramDefinitionSetId = canvasHandler.getDiagram().getMetadata().getDefinitionSetId();
        final Object diagramDefinitionSet = canvasHandler.getDefinitionManager().definitionSets().getDefinitionSetById(diagramDefinitionSetId);
        return canvasHandler.getDefinitionManager().adapters().forDefinitionSet().getSvgNodeId(diagramDefinitionSet);
    }

    public void initDeviceRatio() {
    }

    @Override
    public void saveContainer(String id) {
        context.saveGroup(new HashMap<String, String>() {{
            //setting the node id in case it exists on graph
            Optional.ofNullable(canvasHandler.getGraphIndex().get(id)).ifPresent(node -> {
                put(DEFAULT_NODE_ID, id);
                put(svgNodeId, id);
            });
        }});
    }

    @Override
    public void restoreContainer() {
        context.restoreGroup();
    }

    public void save() {
        context.saveStyle();
    }

    public void save(String id) {
        context.saveStyle();
        context.addAttributes(new HashMap<String, String>() {{
            //setting the node id in case it exists on graph
            Optional.ofNullable(id).ifPresent(node -> put(DEFAULT_NODE_ID, id));
        }});
    }

    public void restore() {
        context.restoreStyle();
    }

    public void beginPath() {
        context.beginPath();
    }

    public void closePath() {
        context.closePath();
    }

    public void moveTo(double x, double y) {
        context.moveTo(x, y);
    }

    public void lineTo(double x, double y) {
        context.lineTo(x, y);
    }

    public void setGlobalCompositeOperation(String operation) {
        context.setGlobalCompositeOperation(operation);
    }

    public void setLineCap(String lineCap) {
        context.setLineCap(lineCap);
    }

    public void setLineJoin(String lineJoin) {
        context.setLineJoin(lineJoin);
    }

    public void quadraticCurveTo(double cpx, double cpy, double x, double y) {
        context.quadraticCurveTo(cpx, cpy, x, y);
    }

    public void arc(double x, double y, double radius, double startAngle, double endAngle) {
        context.arc(x, y, radius, startAngle, endAngle);
    }

    public void arc(double x, double y, double radius, double startAngle, double endAngle, boolean antiClockwise) {
        context.arc(x, y, radius, startAngle, endAngle, antiClockwise);
    }

    public void ellipse(double x, double y, double rx, double ry, double ro, double sa, double ea, boolean ac) {
        context.ellipse(x, y, rx, ry, ro, sa, ea, ac);
    }

    public void ellipse(double x, double y, double rx, double ry, double ro, double sa, double ea) {
        context.ellipse(x, y, rx, ry, ro, sa, ea);
    }

    public void arcTo(double x1, double y1, double x2, double y2, double radius) {
        context.arcTo(x1, y1, x2, y2, radius);
    }

    public void bezierCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y) {
        context.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
    }

    public void clearRect(double x, double y, double w, double h) {
        context.clearRect(x, y, w, h);
    }

    public void clip() {
        context.clip();
    }

    public void fill() {
        context.fill();
    }

    public void stroke() {
        context.stroke();
    }

    public void fillRect(double x, double y, double w, double h) {
        context.fillRect(x, y, w, h);
    }

    public void fillText(String text, double x, double y) {
        context.fillText(text, x, y);
    }

    public void fillTextWithGradient(String text, double x, double y, double sx, double sy, double ex, double ey, String color) {
        throwException();
    }

    public void fillText(String text, double x, double y, double maxWidth) {
        context.fillText(text, x, y);
    }

    public void setFillColor(String fill) {
        context.setFillStyle(fill);
    }

    public void rect(double x, double y, double w, double h) {
        context.rect(x, y, w, h);
    }

    public void rotate(double angle) {
        context.rotate(angle);
    }

    public void scale(double sx, double sy) {
        context.scale(sx, sy);
    }

    public void setStrokeColor(String color) {
        context.setStrokeStyle(color);
    }

    public void setStrokeWidth(double width) {
        context.setLineWidth(width);
    }

    public void setImageSmoothingEnabled(boolean enabled) {
        context.setImageSmoothingEnabled(enabled);
    }

    public void setFillGradient(LinearGradient.LinearGradientJSO grad) {
        setFillColor(null);
    }

    public void setFillGradient(PatternGradient.PatternGradientJSO grad) {
        setFillColor(null);
    }

    public void setFillGradient(RadialGradient.RadialGradientJSO grad) {
        setFillColor(null);
    }

    public void transform(Transform.TransformJSO jso) {
        this.transform(jso.get(0), jso.get(1), jso.get(2), jso.get(3), jso.get(4), jso.get(5));
    }

    public void transform(double d0, double d1, double d2, double d3, double d4, double d5) {
        context.transform(d0, d1, d2, d3, d4, d5);
    }

    public void setTransform(Transform.TransformJSO jso) {
        this.setTransform(jso.get(0), jso.get(1), jso.get(2), jso.get(3), jso.get(4), jso.get(5));
    }

    public void setTransform(double d0, double d1, double d2, double d3, double d4, double d5) {
        context.setTransform(d0, d1, d2, d3, d4, d5);
    }

    public void setToIdentityTransform() {
        this.setTransform(1, 0, 0, 1, 0, 0);
    }

    public void setTextFont(String font) {
        context.setFont(font);
    }

    public void setTextBaseline(String baseline) {
        context.setTextBaseline(baseline);
    }

    public void setTextAlign(String align) {
        context.setTextAlign(align);
    }

    public void strokeText(String text, double x, double y) {
        context.strokeText(text, x, y);
    }

    public void setGlobalAlpha(double alpha) {
        context.setGlobalAlpha(alpha);
    }

    public void translate(double x, double y) {
        context.translate(x, y);
    }

    public void setShadow(Shadow.ShadowJSO shadow) {
        if (Objects.nonNull(shadow)) {
            context.setShadowColor(shadow.getColor());
            context.setShadowOffsetX(shadow.getOffset().getX());
            context.setShadowOffsetY(shadow.getOffset().getY());
            context.setShadowBlur(shadow.getBlur());
        } else {
            context.setShadowColor("transparent");
            context.setShadowOffsetX(0);
            context.setShadowOffsetY(0);
            context.setShadowBlur(0);
        }
    }

    public boolean isSupported(String feature) {
        throwException();
        return false;
    }

    public boolean isPointInPath(double x, double y) {
        return context.isPointInPath(x, y);
    }

    public ImageData getImageData(double x, double y, double width, double height) {
        elemental2.dom.ImageData nativeImageData = context.getImageData(x, y, width, height);
        return nativeClassConverter.convert(nativeImageData, ImageData.class);
    }

    public ImageData createImageData(double width, double height) {
        return nativeClassConverter.convert(context.createImageData(width, height), ImageData.class);
    }

    public ImageData createImageData(ImageData data) {
        return nativeClassConverter.convert(context.createImageData(nativeClassConverter.convert(data, elemental2.dom.ImageData.class)), ImageData.class);
    }

    public void putImageData(ImageData image, double x, double y) {
        context.putImageData(nativeClassConverter.convert(image, elemental2.dom.ImageData.class), x, y);
    }

    public void putImageData(ImageData image, double x, double y, double dx, double dy, double dw, double dh) {
        context.putImageData(nativeClassConverter.convert(image, elemental2.dom.ImageData.class), x, y, dx, dy, dw, dh);
    }

    public TextMetrics measureText(String text) {
        return nativeClassConverter.convert(context.measureText(text), TextMetrics.class);
    }

    public void drawImage(Element image, double x, double y) {
        context.drawImage(nativeClassConverter.convert(image, HTMLCanvasElement.class), x, y);
    }

    public void drawImage(Element image, double x, double y, double w, double h) {
        context.drawImage(nativeClassConverter.convert(image, HTMLCanvasElement.class), x, y, w, h);
    }

    public void drawImage(Element image, double sx, double sy, double sw, double sh, double x, double y, double w, double h) {
        context.drawImage(nativeClassConverter.convert(image, HTMLCanvasElement.class), sx, sy, sw, sh, x, y, w, h);
    }

    public void resetClip() {
        context.resetClip();
    }

    public void setMiterLimit(double limit) {
        context.setMiterLimit(limit);
    }

    public void setLineDash(NFastDoubleArrayJSO dashes) {
        context.setLineDash(dashes.toArray());
    }

    public void setLineDashOffset(double offset) {
        context.setLineDashOffset(offset);
    }

    public double getBackingStorePixelRatio() {
        return 1;
    }

    public boolean path(PathPartList.PathPartListJSO list) {
        //As-is logic from native code on Lienzo NativeContext2D class
        if (list == null) {
            return false;
        }
        int leng = list.length();
        if (leng < 1) {
            return false;
        }
        int indx = 0;
        boolean fill = true;
        this.beginPath();
        while (indx < leng) {
            PathPartEntryJSO e = list.get(indx);
            indx++;
            NFastDoubleArrayJSO p = e.getPoints();
            switch (e.getCommand()) {
                case 1:
                    lineTo(p.get(0), p.get(1));
                    break;
                case 2:
                    moveTo(p.get(0), p.get(1));
                    break;
                case 3:
                    bezierCurveTo(p.get(0), p.get(1), p.get(2), p.get(3), p.get(4), p.get(5));
                    break;
                case 4:
                    quadraticCurveTo(p.get(0), p.get(1), p.get(2), p.get(3));
                    break;
                case 5:
                    ellipse(p.get(0), p.get(1), p.get(2), p.get(3), p.get(6), p.get(4), p.get(4) + p.get(5),
                            (1 - p.get(7)) > 0);
                    break;
                case 6:
                    fill = true;
                    closePath();
                    break;
                case 7:
                    this.arcTo(p.get(0), p.get(1), p.get(2), p.get(3), p.get(4));
                    break;
            }
        }
        return fill;
    }

    public boolean clip(PathPartList.PathPartListJSO list) {
        //As-is logic from native code on Lienzo NativeContext2D class
        if (list == null) {
            return false;
        }
        int leng = list.length();
        if (leng < 1) {
            return false;
        }
        int indx = 0;
        boolean fill = false;
        while (indx < leng) {
            PathPartEntryJSO e = list.get(indx++);
            NFastDoubleArrayJSO p = e.getPoints();
            switch (e.getCommand()) {
                case 1:
                    this.lineTo(p.get(0), p.get(1));
                    break;
                case 2:
                    this.moveTo(p.get(0), p.get(1));
                    break;
                case 3:
                    this.bezierCurveTo(p.get(0), p.get(1), p.get(2), p.get(3), p.get(4), p.get(5));
                    break;
                case 4:
                    this.quadraticCurveTo(p.get(0), p.get(1), p.get(2), p.get(3));
                    break;
                case 5:
                    this.ellipse(p.get(0), p.get(1), p.get(2), p.get(3), p.get(6), p.get(4), p.get(4) + p.get(5),
                                 (1 - p.get(7)) > 0);
                    break;
                case 6:
                    return true;
                case 7:
                    this.arcTo(p.get(0), p.get(1), p.get(2), p.get(3), p.get(4));
                    break;
            }
        }
        return fill;
    }

    public void fill(Path2D.NativePath2D path) {
        throwException();
    }

    public void stroke(Path2D.NativePath2D path) {
        throwException();
    }

    public void clip(Path2D.NativePath2D path) {
        throwException();
    }

    public Path2D.NativePath2D getCurrentPath() {
        return throwException();
    }

    public void setCurrentPath(Path2D.NativePath2D path) {
        throwException();
    }

    private Path2D.NativePath2D throwException() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public IContext2D getDelegate() {
        return context;
    }
}