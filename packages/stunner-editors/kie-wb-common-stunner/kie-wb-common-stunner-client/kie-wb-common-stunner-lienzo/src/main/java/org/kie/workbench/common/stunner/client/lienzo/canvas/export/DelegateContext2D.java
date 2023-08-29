/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.client.lienzo.canvas.export;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.DashArray;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.ait.lienzo.client.core.types.PathPartEntryJSO;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.PatternGradient;
import com.ait.lienzo.client.core.types.RadialGradient;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.CompositeOperation;
import com.ait.lienzo.shared.core.types.LineCap;
import com.ait.lienzo.shared.core.types.LineJoin;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.HTMLVideoElement;
import elemental2.dom.ImageData;
import elemental2.dom.Path2D;
import elemental2.dom.TextMetrics;
import org.kie.workbench.common.stunner.client.lienzo.util.NativeClassConverter;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.uberfire.ext.editor.commons.client.file.exports.svg.IContext2D;

public class DelegateContext2D extends Context2D {

    protected static final String DEFAULT_NODE_ID = "id";

    interface Converter {

        <I, O> O convert(I input, Class<O> outputType);
    }

    IContext2D context;
    Converter nativeClassConverter;
    AbstractCanvasHandler canvasHandler;

    private final String svgNodeId;

    public DelegateContext2D(final HTMLCanvasElement element,
                             final IContext2D context,
                             final AbstractCanvasHandler canvasHandler) {
        this(element,
             context,
             canvasHandler,
             NativeClassConverter::convert);
    }

    DelegateContext2D(final HTMLCanvasElement element,
                      final IContext2D context,
                      final AbstractCanvasHandler canvasHandler,
                      final Converter converter) {
        super(element);
        this.context = context;
        this.nativeClassConverter = converter;
        this.canvasHandler = canvasHandler;
        this.svgNodeId = getSvgNodeId(canvasHandler).orElse(DEFAULT_NODE_ID);
    }

    private Optional<String> getSvgNodeId(final AbstractCanvasHandler canvasHandler) {
        final String diagramDefinitionSetId = canvasHandler.getDiagram()
                .getMetadata()
                .getDefinitionSetId();
        final Object diagramDefinitionSet = canvasHandler.getDefinitionManager()
                .definitionSets()
                .getDefinitionSetById(diagramDefinitionSetId);
        return canvasHandler.getDefinitionManager()
                .adapters()
                .forDefinitionSet()
                .getSvgNodeId(diagramDefinitionSet);
    }

    @Override
    public void saveContainer(final String id) {
        HashMap<String, String> map = new HashMap<>();
        //setting the node id in case it exists on graph
        if (canvasHandler.getGraphIndex().get(id) != null) {
            map.put(DEFAULT_NODE_ID, id);
            map.put(svgNodeId, id);
        }
        context.saveGroup(map);
    }

    @Override
    public void restoreContainer() {
        context.restoreGroup();
    }

    @Override
    public void save() {
        context.saveStyle();
    }

    @Override
    public void save(final String id) {
        context.saveStyle();
        HashMap<String, String> map = new HashMap<>();
        //setting the node id in case it exists on graph
        if (id != null) {
            map.put(DEFAULT_NODE_ID, id);
        }
        context.addAttributes(map);
    }

    @Override
    public void restore() {
        context.restoreStyle();
    }

    @Override
    public void beginPath() {
        context.beginPath();
    }

    @Override
    public void closePath() {
        context.closePath();
    }

    @Override
    public void rect(final double x,
                     final double y,
                     final double w,
                     final double h) {
        context.rect(x, y, w, h);
    }

    @Override
    public void fillRect(final double x,
                         final double y,
                         final double w,
                         final double h) {
        context.fillRect(x, y, w, h);
    }

    @Override
    public void fill() {
        context.fill();
    }

    @Override
    public void stroke() {
        context.stroke();
    }

    @Override
    public void setFillColor(final String color) {
        context.setFillStyle(color);
    }

    @Override
    public void arc(final double x,
                    final double y,
                    final double radius,
                    final double startAngle,
                    final double endAngle,
                    final boolean antiClockwise) {
        context.arc(x, y, radius, startAngle, endAngle, antiClockwise);
    }

    @Override
    public void arc(final double x,
                    final double y,
                    final double radius,
                    final double startAngle,
                    final double endAngle) {
        context.arc(x, y, radius, startAngle, endAngle);
    }

    @Override
    public void arcTo(final double x1,
                      final double y1,
                      final double x2,
                      final double y2,
                      final double radius) {
        context.arcTo(x1, y1, x2, y2, radius);
    }

    @Override
    public void ellipse(final double x,
                        final double y,
                        final double radiusX,
                        final double radiusY,
                        final double rotation,
                        final double startAngle,
                        final double endAngle,
                        final boolean antiClockwise) {
        context.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle, antiClockwise);
    }

    @Override
    public void ellipse(final double x,
                        final double y,
                        final double radiusX,
                        final double radiusY,
                        final double rotation,
                        final double startAngle,
                        final double endAngle) {
        context.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle);
    }

    @Override
    public void setStrokeColor(final String color) {
        context.setStrokeStyle(color);
    }

    @Override
    public void setStrokeWidth(final double width) {
        context.setLineWidth(width);
    }

    @Override
    public void setLineCap(final LineCap linecap) {
        context.setLineCap(linecap.getValue());
    }

    @Override
    public void setLineJoin(final LineJoin linejoin) {
        context.setLineJoin(linejoin.getValue());
    }

    @Override
    public void transform(final double d0,
                          final double d1,
                          final double d2,
                          final double d3,
                          final double d4,
                          final double d5) {
        context.transform(d0, d1, d2, d3, d4, d5);
    }

    @Override
    public void setTransform(final double d0,
                             final double d1,
                             final double d2,
                             final double d3,
                             final double d4,
                             final double d5) {
        context.setTransform(d0, d1, d2, d3, d4, d5);
        ;
    }

    @Override
    public void setToIdentityTransform() {
        this.setTransform(1, 0, 0, 1, 0, 0);
    }

    @Override
    public void moveTo(final double x, final double y) {
        context.moveTo(x, y);
    }

    @Override
    public void bezierCurveTo(final double cp1x,
                              final double cp1y,
                              final double cp2x,
                              final double cp2y,
                              final double x,
                              final double y) {
        context.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
    }

    @Override
    public void lineTo(final double x, final double y) {
        context.lineTo(x, y);
    }

    @Override
    public void setFillGradient(final LinearGradient gradient) {
        setFillColor((String) null);
    }

    @Override
    public void setFillGradient(final RadialGradient gradient) {
        setFillColor((String) null);
    }

    @Override
    public void setFillGradient(final PatternGradient gradient) {
        setFillColor((String) null);
    }

    @Override
    public void quadraticCurveTo(final double cpx,
                                 final double cpy,
                                 final double x,
                                 final double y) {
        context.quadraticCurveTo(cpx, cpy, x, y);
    }

    @Override
    public void transform(final Transform transform) {
        this.transform(transform.get(0),
                       transform.get(1),
                       transform.get(2),
                       transform.get(3),
                       transform.get(4),
                       transform.get(5));
    }

    @Override
    public void setTransform(final Transform transform) {
        this.setTransform(transform.get(0),
                          transform.get(1),
                          transform.get(2),
                          transform.get(3),
                          transform.get(4),
                          transform.get(5));
    }

    @Override
    public void fillTextWithGradient(final String text,
                                     final double x,
                                     final double y,
                                     final double sx,
                                     final double sy,
                                     final double ex,
                                     final double ey,
                                     final String color) {
        throwException();
    }

    @Override
    public void setTextFont(final String font) {
        context.setFont(font);
    }

    @Override
    public void setTextBaseline(final TextBaseLine baseline) {
        context.setTextBaseline(baseline.getValue());
    }

    @Override
    public void setTextAlign(final TextAlign textAlign) {
        context.setTextAlign(textAlign.getValue());
    }

    @Override
    public void fillText(final String text, final double x, final double y) {
        context.fillText(text, x, y);
    }

    @Override
    public void strokeText(final String text, final double x, final double y) {
        context.strokeText(text, x, y);
    }

    @Override
    public void setGlobalAlpha(final double alpha) {
        context.setGlobalAlpha(alpha);
    }

    @Override
    public void translate(final double x, final double y) {
        context.translate(x, y);
    }

    @Override
    public void rotate(final double rot) {
        context.rotate(rot);
    }

    @Override
    public void scale(final double sx, final double sy) {
        context.scale(sx, sy);
    }

    @Override
    public void clearRect(final double x,
                          final double y,
                          final double wide,
                          final double high) {
        context.clearRect(x, y, wide, high);
    }

    @Override
    public void setShadow(final Shadow shadow) {
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

    @Override
    public void clip() {
        context.clip();
    }

    @Override
    public void resetClip() {
        context.resetClip();
    }

    @Override
    public void setMiterLimit(final double limit) {
        context.setMiterLimit(limit);
    }

    @Override
    public boolean path(final PathPartList list) {
        return path(list, true);
    }

    @Override
    public boolean clip(final PathPartList list) {
        return path(list, false);
    }

    private boolean path(PathPartList list, boolean beginPath) {
        //Logic from native code on Lienzo NativeContext2D class
        boolean fill = false;
        if (null != list && list.size() > 0) {
            if (beginPath) {
                this.beginPath();
            }
            for (int i = 0; i < list.size(); i++) {
                PathPartEntryJSO e = list.get(i);
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
        }
        return fill;
    }

    @Override
    public boolean isSupported(final String feature) {
        throwException();
        return false;
    }

    @Override
    public boolean isPointInPath(final double x, final double y) {
        return context.isPointInPath(x, y);
    }

    @Override
    public ImageData getImageData(final int x,
                                  final int y,
                                  final int width,
                                  final int height) {
        elemental2.dom.ImageData nativeImageData = context.getImageData(x, y, width, height);
        return nativeClassConverter.convert(nativeImageData, ImageData.class);
    }

    @Override
    public void putImageData(final ImageData imageData, final int x, final int y) {
        context.putImageData(nativeClassConverter.convert(imageData, elemental2.dom.ImageData.class), x, y);
    }

    @Override
    public void putImageData(final ImageData imageData,
                             final int x,
                             final int y,
                             final int dirtyX,
                             final int dirtyY,
                             final int dirtyWidth,
                             final int dirtyHeight) {
        context.putImageData(nativeClassConverter.convert(imageData, elemental2.dom.ImageData.class),
                             x,
                             y,
                             dirtyX,
                             dirtyY,
                             dirtyWidth,
                             dirtyHeight);
    }

    @Override
    public ImageData createImageData(final int width, final int height) {
        return nativeClassConverter.convert(context.createImageData(width, height), ImageData.class);
    }

    @Override
    public ImageData createImageData(final ImageData data) {
        return nativeClassConverter.convert(
                context.createImageData(nativeClassConverter.convert(data, elemental2.dom.ImageData.class)),
                ImageData.class);
    }

    @Override
    public TextMetrics measureText(final String text) {
        return nativeClassConverter.convert(context.measureText(text), TextMetrics.class);
    }

    @Override
    public void setGlobalCompositeOperation(final CompositeOperation operation) {
        context.setGlobalCompositeOperation(operation.getValue());
    }

    @Override
    public void drawImage(final HTMLImageElement image, final double x, final double y) {
        context.drawImage(nativeClassConverter.convert(image, HTMLCanvasElement.class), x, y);
    }

    @Override
    public void drawImage(final HTMLImageElement image,
                          final double x,
                          final double y,
                          final double w,
                          final double h) {
        context.drawImage(nativeClassConverter.convert(image, HTMLCanvasElement.class), x, y, w, h);
    }

    @Override
    public void drawImage(final HTMLImageElement image,
                          final double sx,
                          final double sy,
                          final double sw,
                          final double sh,
                          final double x,
                          final double y,
                          final double w,
                          final double h) {
        context.drawImage(nativeClassConverter.convert(image, HTMLCanvasElement.class),
                          sx,
                          sy,
                          sw,
                          sh,
                          x,
                          y,
                          w,
                          h);
    }

    @Override
    public void drawImage(final HTMLCanvasElement image, final double x, final double y) {
        context.drawImage(nativeClassConverter.convert(image, HTMLCanvasElement.class), x, y);
    }

    @Override
    public void drawImage(final HTMLCanvasElement image,
                          final double x, final double y,
                          final double w, final double h) {
        context.drawImage(nativeClassConverter.convert(image, HTMLCanvasElement.class), x, y, w, h);
    }

    @Override
    public void drawImage(final HTMLCanvasElement image,
                          final double sx,
                          final double sy,
                          final double sw,
                          final double sh,
                          final double x,
                          final double y,
                          final double w,
                          final double h) {
        context.drawImage(nativeClassConverter.convert(image, HTMLCanvasElement.class),
                          sx,
                          sy,
                          sw,
                          sh,
                          x,
                          y,
                          w,
                          h);
    }

    @Override
    public void drawImage(final HTMLVideoElement image, final double x, final double y) {
        context.drawImage(nativeClassConverter.convert(image, HTMLCanvasElement.class), x, y);
    }

    @Override
    public void drawImage(final HTMLVideoElement image,
                          final double x,
                          final double y,
                          final double w,
                          final double h) {
        context.drawImage(nativeClassConverter.convert(image, HTMLCanvasElement.class),
                          x,
                          y,
                          w,
                          h);
    }

    @Override
    public void drawImage(final HTMLVideoElement image,
                          final double sx,
                          final double sy,
                          final double sw,
                          final double sh,
                          final double x,
                          final double y,
                          final double w,
                          final double h) {
        context.drawImage(nativeClassConverter.convert(image, HTMLCanvasElement.class),
                          sx,
                          sy,
                          sw,
                          sh,
                          x,
                          y,
                          w,
                          h);
    }

    @Override
    public void setLineDash(final DashArray dashes) {
        context.setLineDash(dashes.getJSO());
    }

    @Override
    public void setLineDashOffset(final double offset) {
        context.setLineDashOffset(offset);
    }

    @Override
    public double getBackingStorePixelRatio() {
        return 1;
    }

    public void fill(final Path2D path) {
        throwException();
    }

    public void stroke(final Path2D path) {
        throwException();
    }

    public void clip(final Path2D path) {
        throwException();
    }

    private Path2D throwException() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
